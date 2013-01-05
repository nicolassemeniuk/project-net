/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20854 $
|       $Date: 2010-05-14 10:47:54 -0300 (vie, 14 may 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.GenericSpace;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * Manages forms.
 */
public class FormManager {
    /**
     * Construct a new FormManager
     */
    public FormManager() {
    }

    /**
     * Copies all forms from one space to another.
     * All copied forms which can be activated will be actived.
     *
     * @param fromSpaceID the id of the space from which to copy forms
     * @param toSpaceID the id of the space to which to copy the forms
     * @throws net.project.base.PnetException if there is a problem copying
     * @see FormDesigner#isActivateAllowed
     */
    public void copyAll(String fromSpaceID, String toSpaceID) throws PnetException {

        int errorCode = 0;
        net.project.database.DBBean db = new net.project.database.DBBean();

        try {
            db.prepareCall("begin FORMS.COPY_ALL  (?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, net.project.security.SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);

            net.project.database.DBExceptionFactory.getException("FormsManager.copyAll()", errorCode);

            // Activate all active forms in the "to" Space and ignore any pending forms to be activated (bug104)
            FormMenu.activateAllActiveForSpace(toSpaceID, net.project.security.SessionManager.getUser());

        } // end try
        catch (java.sql.SQLException sqle) {

        	Logger.getLogger(FormManager.class).error("FormManager.copyAll():  unable to execute stored procedure: " + sqle);
            throw new net.project.persistence.PersistenceException("FormManager.copyAll operation failed! ", sqle);
        } finally {
            db.release();
        }


    }

    /**
     * Copies a form with the specified id within this space.
     * @param formID the id of the form to copy
     * @param spaceID space to which the form will be copied
     * @param user the current user performing the copy
     * @return the new form's id
     * @throws FormException if the form is not found
     * @throws PersistenceException if there is an error copying
     */
    public String copyForm(String formID, String spaceID, User user) throws FormException, PersistenceException {
        DBBean db = new DBBean();
        String newFormID = null;

        try {
            db.openConnection();

            db.setAutoCommit(false);

            newFormID = new FormCopier().copy(db, formID, spaceID, user);

            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(FormManager.class).error("FormManager.copyForm threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying form: " + sqle.getMessage(), sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
// Do nothing but release
            }

            db.release();
        }

        return newFormID;
    }


    /**
     * Copies a form with the specified id within this space.
     * @param formID the id of the form to copy
     * @param fromSpaceID space from which the form will be copied
     * @param toSpaceID space to which the form will be copied
     * @param user the current user performing the copy
     * @return the new form's id
     * @throws FormException if the form is not found
     * @throws PersistenceException if there is an error copying
     */
    public String copyForm(String formID, String fromSpaceID, String toSpaceID, User user) throws FormException, PersistenceException {
        DBBean db = new DBBean();
        String newFormID = null;

        try {
            db.openConnection();

            db.setAutoCommit(false);

            newFormID = new FormCopier().copy(db, formID, fromSpaceID, toSpaceID, user);

            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(FormManager.class).error("FormManager.copyForm threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying form: " + sqle.getMessage(), sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
// Do nothing but release
            }

            db.release();
        }

        return newFormID;
    }

    /**
     * Returns a FormMenu where each entry may or may not be accessible.
     * The FormMenu contains a menu entry for each form available to the space with ID
     * <code>providingSpaceID</code>.  Each menu entry may or may not be
     * accessible by the space with ID <code>accessingSpaceID</code>.
     * Those menu entries which are accessible are of type {@link VisibleFormMenuEntry}.
     * All other menu entries are of type {@link FormMenuEntry}.
     * @param providingSpaceID the space ID of the space which has access to
     * all the entries in the FormMenu
     * @param accessingSpaceID the space ID of the space that wishes to know
     * which of the entries are accessible to it.
     * @return the loaded FormMenu
     */
    public FormMenu getFormMenuOfAccessibleForms(String providingSpaceID, String accessingSpaceID)
        throws PersistenceException {

        FormMenu formMenu = new FormMenu();
        formMenu.setSpaceID(providingSpaceID);
        formMenu.loadAccessibleToSpaceID(accessingSpaceID);

        return formMenu;
    }

    /**
     *
     * @param formID The ID of the Form to be copied
     * @param spaces The String Array of ID of Spaces to which the form is to be copied to
     * @param user   The user
     * @return Returns a HashMap , with ID of the space to which the Form has been copied to , as
     *         the key and ID of the newly created Form as the value
     * @exception PersistenceException
     *                   If anything goes wrong while
     */
    public HashMap copyFormToMultipleSpaces(String formID, String[] spaces, User user)
        throws PersistenceException, FormException {

        DBBean db = new DBBean();
        String query = " select owner_space_id from pn_class where class_id = ?";
        String ownerSpaceID = null;
        HashMap map = new HashMap();

        if (formID == null) {
            throw new NullPointerException(" Form ID is empty");
        } else if (spaces == null) {
            throw new NullPointerException(" List of Spaces cannot be empty");
        }

        try {
            int index = 0;
            db.prepareStatement(query);

            db.pstmt.setString(++index, formID);

            db.executePrepared();

            if (db.result.next()) {
                ownerSpaceID = db.result.getString("owner_space_id");
            }

            for (int i = 0; i < spaces.length; i++) {
                String newFormID = copyForm(formID, ownerSpaceID, spaces[i], user);
                map.put(spaces[i], newFormID);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Form could not be copied" + sqle, sqle);

        } finally {
            db.release();
        }
        return map;
    }

    
    public static List<String> getChildSpaceIds(String parentId, HashMap<String, List<String>> connections){
    	List<String> childIds = new ArrayList<String>();
    	if(connections.get(parentId).size() > 0){
    		childIds.addAll(connections.get(parentId));
			for (String childId : connections.get(parentId)) {
				childIds.addAll(getChildSpaceIds(childId, connections));
			}
		}
    	return childIds;
    }    
    
    /**
     * Add shared form to newly created space
     * Find all shared forms that should be added to child space
     * and update form records visibility for all parents in tree
     * important: only project with the same business or whose business are
     * parent to child space business can see records from child 
     * 
     * @param db             - db bean
     * @param parentSpace    - parent space
     * @param childSpace     - child space - the one in witch forms are added
     * @param childIsProject - flag : true if child space is project false otherwise
     * @throws SQLException  - throw exception
     */
    public static void addSharedForms(DBBean db, Space parentSpace, Space childSpace, boolean childIsProject)  throws SQLException{
    	
    	//find all  forms parent can see 
    	String sql = "select c.class_id, c.class_name, c.shared, shc.is_owner " +
    				 "from pn_class c, pn_space_has_class shc " +
    				 "where c.class_id = shc.class_id and c.record_status = 'A' and shc.space_id = " + parentSpace.getID();
    	db.prepareStatement(sql);
    	db.executePrepared();
    	ResultSet rs = db.result;
    	if (rs.getFetchSize() >0){
	    	List<String> sharedForms = new ArrayList<String>();
	    	while(rs.next()){
	    		boolean shared = rs.getString("shared").equals("1");
	    		if (shared){
	    			sharedForms.add(rs.getString("class_id")); //all shared add to collection
	    		}    		
	    	}
	    	
	    	//connect child space with all shared forms : is_owner is set 0 as child is not the owner of the form
	    	// and visible is initialy set to 0 as shared form must be activated first
	    	db.prepareStatement("insert into pn_space_has_class (space_id, class_id, is_owner, visible) values (?, ?, 0, 0)");
	    	for (String classId : sharedForms){
                db.pstmt.setString(1, childSpace.getID());
                db.pstmt.setString(2,classId);
                db.pstmt.addBatch();	    		
	    	}
	    	db.pstmt.executeBatch();
	    	
	    	//update childIds field for all business ancestors : this will not affect project ancestors
	    	sql = "update pn_shared_forms_visiblity set child_ids = concat(child_ids , to_clob('," + childSpace.getID() + "')) " +
	    		  " where instr(child_ids, '"+ parentSpace.getID() +"') > 0  and instr(child_ids, '"+ childSpace.getID() +"') = 0 ";
	    	db.prepareStatement(sql);
	    	db.executePrepared();
	    	
	    	//in case child space is project all project ancestor must be updated to see records
	    	if (childIsProject){
	    		//select all project ancestor and find their business parents ids
	    		sql = " select distinct shs.child_space_id, shs.parent_space_id as project_parent_id, psbp.PARENT_SPACE_ID as business_parent_id " +
	    	  		" from pn_space_has_space shs, pn_project_space ps, pn_space_has_space psbp " +
	    	  		" where shs.PARENT_SPACE_TYPE = 'project' and shs.PARENT_SPACE_ID = ps.PROJECT_ID and " +
	    	  		" ps.PROJECT_ID = psbp.CHILD_SPACE_ID(+) and psbp.PARENT_SPACE_TYPE = 'business' " +
	    	  		" start with shs.child_space_id = " + childSpace.getID() + " connect by shs.child_space_id = prior shs.parent_space_id ";
	      		db.prepareStatement(sql);
	      		db.executePrepared();
	      		rs = db.result;
	      		HashMap<String, String> businessParents = new HashMap<String,String>();
	      		if (rs.getFetchSize() >0){
	      			while(rs.next()){	      				
	      				businessParents.put(rs.getString("business_parent_id"), rs.getString("project_parent_id"));
	      			}
	      			//find all ancestor of child business parent
		    		sql = " select distinct shs.child_space_id, shs.parent_space_id " +
	    	  		" from pn_space_has_space shs " +
	    	  		" where shs.PARENT_SPACE_TYPE = 'business' " +
	    	  		" start with shs.child_space_id = " + parentSpace.getID() + " connect by shs.child_space_id = prior shs.parent_space_id ";	      			
		      		db.prepareStatement(sql);
		      		db.executePrepared();
		      		rs = db.result;
	      			List<String> businessIds = new ArrayList<String>();
	      			businessIds.add(parentSpace.getID());	      			
	      			if (rs.getFetchSize() >0){
	      				while(rs.next()){
	      					businessIds.add(rs.getString("parent_space_id"));
	      				}
	      			}

	      			//for all project ancestor whose business are in list of businessIds update childIds field visibility 
	      			// this will exclude all project without business or whose business are not in ancestor tree line
	      			for (Map.Entry<String, String> mapEntry : businessParents.entrySet()){
	      				if (businessIds.contains(mapEntry.getKey())){
	      					sql = "update pn_shared_forms_visiblity set child_ids = concat(child_ids , to_clob('," + childSpace.getID() + "')) " +
	      						" where instr(child_ids, '" + mapEntry.getValue()+  "') > 0  and instr(child_ids, '"+ childSpace.getID() +"') = 0 ";
	      					db.prepareStatement(sql);
	      					db.executePrepared();
	      				}
	      			}
	      		}
	    	}
	    	//update childIds for child space itself : for now it only see it's records as it is a leaf in space tree
	    	db.prepareStatement("insert into pn_shared_forms_visiblity (space_id, class_id, child_ids) values (?, ?, to_clob(?))");
	    	for (String classId : sharedForms){
	                db.pstmt.setString(1, childSpace.getID());
	                db.pstmt.setString(2, classId);
	                db.pstmt.setString(3, childSpace.getID());
	                db.pstmt.addBatch();
	    	}
	    	db.pstmt.executeBatch();	    		    
    	}    
    }
    
    private static void createFormHierarchicalSharingPolicy(DBBean db, String classId, String spaceId) throws SQLException{
        //First, delete any existing form id's that are being shared.
    	
        db.prepareStatement("select count(*) as hasManuItem from pn_space_has_class shc, pn_space_has_featured_menuitem shfm " +
        					" where shc.class_id = ? and shc.is_owner = 1 and shc.space_id = shfm.space_id ");
        db.pstmt.setString(1, classId);
        db.executePrepared();
        boolean hasMenuItem = false;
        if (db.result.next()){
        	hasMenuItem = db.result.getInt("hasManuItem") > 0;
        }
        
        db.prepareStatement("select space_id from pn_space_has_class where class_id = ? and space_id <> ? and visible = 1");
        db.pstmt.setString(1, classId);
        db.pstmt.setString(2, spaceId);
        db.executePrepared();
        
        List<String> activatedSpaces = new ArrayList<String>();
        while (db.result.next()){
        	if(!activatedSpaces.contains(db.result.getString("space_id")))
        		activatedSpaces.add(db.result.getString("space_id"));        	
        }
    	
        db.prepareStatement("delete from pn_space_has_class where class_id = ? and space_id <> ?");
        db.pstmt.setString(1, classId);
        db.pstmt.setString(2, spaceId);
        db.executePrepared();
        
        db.prepareStatement("delete from pn_space_has_featured_menuitem where object_id = ? and space_id <> ?");
        db.pstmt.setString(1, classId);
        db.pstmt.setString(2, spaceId);
        db.executePrepared();        
        

            StringBuffer query = new StringBuffer();
            query.append("select distinct shs.child_space_id, shs.child_space_type, shs.parent_space_id, shs.parent_space_type, ");
            query.append("shs.relationship_child_to_parent, shs.relationship_parent_to_child ");
            query.append("from pn_space_has_space shs ");
            query.append("start with shs.parent_space_id = ? connect by shs.parent_space_id = prior shs.child_space_id ");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, spaceId);
            db.executePrepared();
            ResultSet rs = db.result;
            if (rs.getFetchSize() >0){
            	List<String> childSpaceIds = new ArrayList<String>();
            	HashMap<String, List<String>> connections = new HashMap<String,List<String>>();
            	HashMap<String, String> projectBusiness = new HashMap<String,String>();
            	while (rs.next()){
            		if(!childSpaceIds.contains(rs.getString("child_space_id")))
            			childSpaceIds.add(rs.getString("child_space_id"));
            		
            		String childId = rs.getString("child_space_id");
            		String parentId = rs.getString("parent_space_id");            		
            		if (connections.get(parentId) == null){
            			List<String> childs = new ArrayList<String>();
            			childs.add(childId);
            			connections.put(parentId, childs);
            		}else{
            			connections.get(parentId).add(childId);
            		}            		
            		if (connections.get(childId) == null){
            			List<String> childs = new ArrayList<String>();
            			connections.put(childId, childs);
            		}
            		
            		//create a map with connection between project and business
            		//if project does not have business owner it will have 0 as value in map - 
            		//those are projects that should not get selected form as shared 
            		if (rs.getString("child_space_type") != null && rs.getString("child_space_type").equals("project")){
            			String parentBusinessId = rs.getString("parent_space_type").equals("project") ? "0" : rs.getString("parent_space_id"); 
            			if (projectBusiness.get(rs.getString("child_space_id")) == null || 
            					projectBusiness.get(rs.getString("child_space_id")).equals("0")){
            				projectBusiness.put(rs.getString("child_space_id"), parentBusinessId);
            			}
            		}            		            		
            	}
            	  db.prepareStatement("delete from PN_SHARED_FORMS_VISIBLITY where class_id = ? ");
                  db.pstmt.setString(1, classId);
                  db.executePrepared();            	
            	                  
            	db.prepareStatement("insert into pn_shared_forms_visiblity (space_id, class_id, child_ids) values (?, ?, to_clob(?))");
            	for(Map.Entry<String, List<String>> entry : connections.entrySet()){
            		List<String> childIds = new ArrayList<String>();
            		for(String childId : entry.getValue()){
            			childIds.addAll(getChildSpaceIds(childId, connections));
            		}            		
            		childIds.addAll(entry.getValue());
            		childIds.add(entry.getKey());

            		StringBuffer childIdsStr = new StringBuffer();
            		for (String childId : childIds){
            			childIdsStr.append(childId).append(", ");	
            		}
            		if (projectBusiness.get(entry.getKey()) == null || !projectBusiness.get(entry.getKey()).equals("0")){
            			db.pstmt.setString(1, entry.getKey());
            			db.pstmt.setString(2, classId);            		
            			db.pstmt.setString(3, childIdsStr.toString().substring(0, childIdsStr.length() > 0 ? childIdsStr.toString().lastIndexOf(",") : 0));
            			db.pstmt.addBatch();
            		}
            	}
            	db.pstmt.executeBatch();
            	
	            db.prepareStatement("insert into pn_space_has_class (space_id, class_id, is_owner, visible) values (?, ?, 0, ?)");
	            for (String childSpaceId : childSpaceIds){
	            	if (projectBusiness.get(childSpaceId) == null || !projectBusiness.get(childSpaceId).equals("0")){
	            		db.pstmt.setString(1, childSpaceId);
	            		db.pstmt.setString(2, classId);
	            		String visible = activatedSpaces.contains(childSpaceId) ? "1" : "0";
	            		db.pstmt.setString(3, visible);
	            		db.pstmt.addBatch();	       
	            	}
	            }
	            db.pstmt.executeBatch();
	            	            
	            if(hasMenuItem){
	            	db.prepareStatement("insert into pn_space_has_featured_menuitem (space_id, object_id) values (?, ?)");
	            	for (String childSpaceId : childSpaceIds){
	            	    if( activatedSpaces.contains(childSpaceId)){
	            	    	db.pstmt.setString(1, childSpaceId);
	            	    	db.pstmt.setString(2, classId);
	            	    	db.pstmt.addBatch();
	            	    }
	            	}
	            	db.pstmt.executeBatch();
	            }
	            
            }
               	
    }
    
    public static void updateSharedForms(DBBean db, Space parentSpace, Space childSpace) throws SQLException{
    	HashMap<String, String> sharedFormsMap = new HashMap<String,String>();

    	String sql = "select c.class_id,  c.owner_space_id from pn_class c, pn_space_has_class shc " +
			  		 "where c.class_id = shc.class_id and c.record_status = 'A' " +
			  		 "and shc.is_owner = 0 and shc.space_id = " + childSpace.getID();
    	db.prepareStatement(sql);
    	db.executePrepared();    	
    	ResultSet rs = db.result;    	
	    	while (rs.next()){
	    		sharedFormsMap.put(rs.getString("class_id"), rs.getString("owner_space_id"));	
	    	}    	    	        	
    	
    	sql = "delete from pn_space_has_class shc where shc.is_owner = 0 and shc.space_id = " + childSpace.getID();
    	db.prepareStatement(sql);
    	db.executePrepared();    	
    	rs = db.result;    	
    	
    	if(parentSpace != null){
	    	sql = "select c.class_id,  c.owner_space_id " +
				"from pn_class c, pn_space_has_class shc " +
				"where c.class_id = shc.class_id and c.record_status = 'A' " +
				"and (shc.is_owner = 0 or (shc.is_owner = 1 and c.shared = 1)) and shc.space_id = " + parentSpace.getID();   	
	    	db.prepareStatement(sql);
	    	db.executePrepared();    	
	    	rs = db.result;    	
	    	while (rs.next()){
	    		sharedFormsMap.put(rs.getString("class_id"), rs.getString("owner_space_id"));	
	    	}    	    	
    	}    	
    	for(Map.Entry<String, String> entry : sharedFormsMap.entrySet()){
    		createFormHierarchicalSharingPolicy(db, entry.getKey(), entry.getValue());
    	}
    }
    
    public static void removeAllSharedForms(DBBean db, String spaceId) throws SQLException{
    	
    	db.prepareStatement("select class_id from pn_space_has_class where space_id = ? and is_owner = 1");
    	db.pstmt.setString(1, spaceId);
    	db.executePrepared();
        ResultSet rs = db.result;
        List<String> formIds = new ArrayList<String>();
        if (rs.getFetchSize() >0){
        	while (rs.next()){
        		formIds.add(rs.getString("class_id"));
        	}
        }
        
        if (formIds.size() > 0){
        	for(String formId : formIds){
        		db.prepareStatement("delete from pn_space_has_class where class_id = ? and space_id <> ?");
        		db.pstmt.setString(1, formId);
        		db.pstmt.setString(2, spaceId); 
        		db.pstmt.addBatch();
        	}
        	db.pstmt.executeBatch();

        	for(String formId : formIds){
        		db.prepareStatement("delete from pn_space_has_featured_menuitem where object_id = ? and space_id <> ?");
        		db.pstmt.setString(1, formId);
        		db.pstmt.setString(2, spaceId); 
        		db.pstmt.addBatch();
        	}
        	db.pstmt.executeBatch();
        }
    }
}
