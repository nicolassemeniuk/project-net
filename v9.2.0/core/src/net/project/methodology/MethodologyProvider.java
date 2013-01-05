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

 package net.project.methodology;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.ExceptionList;
import net.project.base.Module;
import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.service.ServiceFactory;
import net.project.methodology.model.LinkContainer;
import net.project.methodology.model.ObjectLink;
import net.project.persistence.PersistenceException;
import net.project.process.ProcessCopier;
import net.project.schedule.ScheduleCopier;
import net.project.security.User;
import oracle.jdbc.OracleTypes;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


/**
 * Provides methods for creating Methodologies (aka Templates).
 *
 * @author Phil Dixon
 * @since 09/2000
 */
public class MethodologyProvider implements java.io.Serializable, net.project.gui.error.IErrorProvider {
	/**
	 * This instance method is intented to use in the template copy from one
	 * space to another. In case the user select the modules "Process" and
	 * "Schedule", then this attribute is esential to know the mapping between
	 * old phase IDs and the new ones.
	 * 
	 * @since 8.2.0
	 */
	private HashMap phaseIDMap = null;
	private MethodologySpace currentSpace = null;
    private User user = null;
    private DBBean db = null;

    public MethodologyProvider() {
        db = new DBBean();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setMethodology(MethodologySpace methodology) {
        this.currentSpace = methodology;
    }

    public MethodologyPortfolio getMethodologyPortfolioForUser(User user) {
        return null;
    }

    public String getMethodologyOptionListForUser(User user) {
        return (getMethodologyOptionListForUser(user.getID()));
    }

    public String getMethodologyOptionList() {
        StringBuffer options = new StringBuffer();
        String qstrGetMethodologyOptionList = "select m.methodology_id, m.methodology_name, b.business_name " +
                        "from pn_methodology_by_user_view m, pn_space_has_space shs, pn_business b " +
                        " where shs.child_space_id = m.methodology_id and b.business_id = shs.parent_space_id and b.record_status = 'A' " +
                        " and m.record_status='A' order by b.business_name, m.methodology_name asc";


        try {

            db.executeQuery(qstrGetMethodologyOptionList);

            String optionStr = null;
            while (db.result.next()) {
             optionStr = db.result.getString("methodology_name") + " (" + db.result.getString("business_name") + ")";
                options.append("<option value=\"" + db.result.getString("methodology_id") + "\">"
                                   + optionStr + "</option>");
            }
            } catch (java.sql.SQLException sqle) {
            	Logger.getLogger(MethodologyProvider.class).debug("MethodologyProvider.getMethodologyOptionList() threw sql exception: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();

    }

    private String getMethodologyOptionListForUser(String userID) {
        DBBean db = new DBBean();
        StringBuffer options = new StringBuffer();
        try {
            // No auto-commit so that we can rollback the temporary data later
            db.setAutoCommit(false);
            // This call returns a reference cursor to all non-hidden
            // Containers in the specified document space id
            db.prepareCall("{ ? = call METHODOLOGY.get_methodology_for_user (?) }");
            db.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            db.cstmt.setString(2, userID);

            db.executeCallable();

            db.result = (ResultSet) db.cstmt.getObject(1);
            
            // Now rollback temporary data
            db.rollback();
       
            String optionStr = null;

            List uniqueValuesList = new ArrayList();
            boolean foundDuplicate = false;
            while (db.result.next()) {

            	optionStr = db.result.getString("methodology_name") + " (" + db.result.getString("parent_space_name") + ")";
            	for ( Iterator it = uniqueValuesList.iterator(); it.hasNext(); ) {
            		String iterOptionStr = (String) it.next();
            		if ( iterOptionStr.equals(optionStr) ) {
            			foundDuplicate = true;
            		}
            	}
            	if ( !foundDuplicate ) {
                    options.append("<option value=\"" + db.result.getString("methodology_id") + "\">" + optionStr + "</option>");
	                uniqueValuesList.add(optionStr);
            	}
            	foundDuplicate = false;
            }
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(MethodologyProvider.class).debug("MethodologyProvider.getMethodologyOptionList() threw sql exception: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();

    }


    public static String getMethodologyName(String methodologyID) {
        String name = null;
        String qstrGetMethodologyName = "select m.methodology_name from pn_methodology_space m" +
                " where m.methodology_id = " + methodologyID;
        DBBean ldb = new DBBean();

        if (methodologyID == null)
            return null;

        try {
            ldb.executeQuery(qstrGetMethodologyName);

            if (ldb.result.next())
                name = ldb.result.getString("methodology_name");

        } // end try
        catch (java.sql.SQLException sqle) {
        	Logger.getLogger(MethodologyProvider.class).debug("MethodologyProvider.getMethodologyName threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return name;
    }


    /**
     * Creates a Template based on the specified space and the modules selected
     * in the template.
     * @param fromSpaceID the space from which to create the template
     * @param template the template being created, containing the modules
     * to create in the template
     * @throws PnetException if there is a problem creating the template
     */
    public void createTemplate(String fromSpaceID, MethodologySpace template) throws PnetException {
        // Get the array of module constants.  Only the objects for each
        // kind of module will be copied.
        int[] modules = template.getSelectedModules();
        
        // ensure copying of process before copy of tasks
        ensureCopySequence(modules);
        
        // Copy all the objects for the selected modules to the template
        if (modules != null){
            copyObjectsForModules(fromSpaceID, template.getID(), modules);
        }
    }

    /**
     * Copy process is module sequence sensitive.
     * Ensures the sequence of modules in an array as expected.
     * (Ensures process before schedule)
     * This is required, otherwise tasks will be copied and phases wont be linked
     * @param modules (arrary of module id's)
     */
    private void ensureCopySequence(int[] modules){
    	if( ArrayUtils.contains(modules, Module.PROCESS) && ArrayUtils.contains(modules, Module.SCHEDULE) 
    		&& ( ArrayUtils.indexOf(modules, Module.PROCESS) > ArrayUtils.indexOf(modules, Module.SCHEDULE) ) ) {
    		modules[ArrayUtils.indexOf(modules, Module.PROCESS)] = Module.SCHEDULE; 
    		modules[ArrayUtils.indexOf(modules, Module.SCHEDULE)] = Module.PROCESS; 
    	}
    	return;
    }
    
    /**
     * Applies a template to a space.
     * @param templateID the id of the template to apply
     * @param spaceID the id of the space to which to apply the template
     * @throws PersistenceException if there is a problem applying the template
     */
    public void applyMethodology(String templateID, String spaceID) throws PersistenceException {

        int[] modules = new int[]{Module.SECURITY, Module.DOCUMENT, Module.PROCESS, Module.WORKFLOW, Module.DISCUSSION, Module.FORM, Module.SCHEDULE};

        // Flag space as having template
        // Allows multiple templates to be applied to a space
        storeTemplateForSpace(templateID, spaceID);

        // Copy all the objects for the selected modules to the new space
        copyObjectsForModules(templateID, spaceID, modules);

        // XXX:
        // We really need to write a factory method that returns a "Copier"
        // object for each module (but not even so rigid as to limit it to
        // one per module)
        //
        // Design:
        //
        // User is presented with a list of "copyable items"
        // User selects one or more "copyable items"
        // Factory method returns a copier object for each "copyable item" (Interface defined for this)
        // Invoke copy method on copier object
        // Thus, individual copier objects can execute whatever code they feel
        // like in order to get the job done
        // We can add arbitrary copyable items
        // Perhaps copyable items can even draw their own presentation, thus
        // allowing arbitrary complexity in customizing the copy

        // XXX:
        // Special handling for forms until above design is implemented
        // Activate the copied forms
        try {
        	// activate the active forms, and skip pending forms to be activated
            net.project.form.FormMenu.activateAllActiveForSpace(spaceID, this.getUser());
        } catch (ExceptionList el) {
            Iterator it = el.getExceptions().iterator();
            int j = 1;

            while (it.hasNext()) {
                this.validationErrors.put("error_formactivate" + "_" + j,
                        (String)it.next());
            }
        }
    }


    /**
     * Copies objects in certain modules from one space to another.
     *
     * @param fromSpaceID the space from which to copy objects
     * @param toSpaceID the space to which to copy
     * @param modules the array of module constants; objects belonging to each
     * module in this array will be copied
     * @see net.project.base.Module for the constants that may be in the array
     */
    private void copyObjectsForModules(String fromSpaceID, String toSpaceID, int[] modules) throws PersistenceException {

        net.project.base.ModuleCollection modulesCollection = new net.project.base.ModuleCollection();
        modulesCollection.load();

        long moduleStartTime = 0L;

        for (int i = 0; i < modules.length; i++) {

            moduleStartTime = System.currentTimeMillis();

            try {
                // Copy all objects for the next module
                copyModule(modules[i], fromSpaceID, toSpaceID);

            } catch (ExceptionList el) {
                Iterator it = el.getExceptions().iterator();
                int j = 1;

                while (it.hasNext()) {
                    this.validationErrors.put("error_" + modules[i] + "_" + j, (String)it.next());
                }
            } catch (PersistenceException pe) {
                // PersistenceExceptions should be propogated out
                throw pe;

            } catch (net.project.base.PnetException pnetE) {
                // Other exceptions are stored as errors, then we continue
                // to process the other modules
                this.validationErrors.put("error_" + modules[i], "A problem occurred while copying " + modulesCollection.getModule("" + modules[i]).getName() + ":\n" + pnetE.getMessage());

            }

            Logger.getLogger(MethodologyProvider.class).debug("Methodology.copyObjectsForModules: Elapsed time for module " + modules[i] + ": " + (System.currentTimeMillis() - moduleStartTime) + " ms");

        } //end for
        List<ObjectLink> documentLinks = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
        List<ObjectLink> objectLLinks = new ArrayList<ObjectLink>();
        if(documentLinks != null){
        	// delete possible duplicates
        	for(int i = 0; i < documentLinks.size() - 1; i++){
        		for(int j = i; j < documentLinks.size(); j++){
        			ObjectLink starting = documentLinks.get(i);
        			ObjectLink ending = documentLinks.get(j);
        			if(starting.getFromObjectIdOld().equals(ending.getFromObjectIdOld()) && starting.getToObjectIdOld().equals(ending.getToObjectIdOld())){
        				// merge objects
        				ObjectLink newObjLink = new ObjectLink();
        				try{
        					PropertyUtils.copyProperties(newObjLink, ending);
        				}catch (Exception e) {
							e.printStackTrace();
						}
        				if(newObjLink.getFromObjectIdNew() == null){
        					newObjLink.setFromObjectIdNew(starting.getFromObjectIdNew());
        				}
        				if(newObjLink.getToObjectIdNew() == null){
        					newObjLink.setToObjectIdNew(starting.getToObjectIdNew());
        				}
        				if(!objectLLinks.contains(newObjLink)){
        					objectLLinks.add(newObjLink);
        				}
        			}
        		}
        	}
        	
			for(ObjectLink o: objectLLinks){
				PnObjectLink objLink = new PnObjectLink(o.getFromObjectIdNew(), o.getToObjectIdNew(), Module.DOCUMENT);
				if(objLink.getComp_id().getToObjectId() != null && objLink.getComp_id().getFromObjectId() != null && objLink.getComp_id().getContext() != null){
					ServiceFactory.getInstance().getPnObjectLinkService().saveOrUpdateObjectLink(objLink);
					o.setSaved(true);
				}
			}
        }
		// when a project is created, delete links from memory
		LinkContainer.getInstance().setLinks(LinkContainer.DOCUMENT, new ArrayList<ObjectLink>());

    }



    /* -------------------------------  Implementing utility methods  ------------------------------- */

    /**
     * Copies all objects for a certain module from one space to another.
     *
     * @param module for which objects should be copied
     * @param fromTemplateID the id of the space from which to copy
     * @param toSpaceID the id of the space to which to copy
     * @throws PnetException if there is a problem copying
     */
    private void copyModule(int module, String fromTemplateID, String toSpaceID) throws net.project.base.PnetException, net.project.base.ExceptionList {
        switch (module) {
            case Module.SECURITY:
                net.project.security.SecurityProvider sp = new net.project.security.SecurityProvider();
                sp.copyAllSettings(fromTemplateID, toSpaceID);
                break;
            case Module.DOCUMENT:
                net.project.document.DocumentManager dm = new net.project.document.DocumentManager();
                dm.copyDocSpace(fromTemplateID, toSpaceID);
                break;
            case Module.PROCESS:
            	ProcessCopier processCopier = new ProcessCopier(fromTemplateID, toSpaceID);            	
            	this.phaseIDMap = processCopier.copyAll();
                break;
            case Module.WORKFLOW:
                net.project.workflow.WorkflowManager wm = new net.project.workflow.WorkflowManager();
                wm.copyAll(fromTemplateID, toSpaceID);
                break;
            case Module.DISCUSSION:
                net.project.discussion.DiscussionManager disc = new net.project.discussion.DiscussionManager();
                disc.copyGroups(fromTemplateID, toSpaceID);
                break;
            case Module.FORM:
                net.project.form.FormManager fm = new net.project.form.FormManager();
                fm.copyAll(fromTemplateID, toSpaceID);
                break;
            case Module.SCHEDULE:
                ScheduleCopier scheduleCopier = new ScheduleCopier(fromTemplateID, toSpaceID);
                scheduleCopier.copyAll(this.phaseIDMap);
                break;
            default:
                break;
        }
    }

    /**
     * Records the fact that a template has been applied to a space.
     * @param templateID the id of the template being applied
     * @param spaceID the id of the space to which the template is being
     * applied
     * @throws PersistenceException if there is a problem storeing
     */
    private void storeTemplateForSpace(String templateID, String spaceID) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("insert into pn_space_has_methodology ");
        query.append("(space_id, methodology_id, person_id, date_applied) ");
        query.append("values (?, ?, ?, ?) ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, spaceID);
            db.pstmt.setString(++index, templateID);
            db.pstmt.setString(++index, getUser().getID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));

            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Error applying template", sqle);

        } finally {
            db.release();

        }

    }

    private net.project.gui.error.ValidationErrors validationErrors = new net.project.gui.error.ValidationErrors();

    /**
     * Clears all errors.
     */
    public void clearErrors() {
        validationErrors.clearErrors();
    }

    /**
     * Indicates whether there are any errors
     * @return true if there are errors; false otherwise
     */
    public boolean hasErrors() {
        return validationErrors.hasErrors();
    }

    /**
     * Gets the Error Flag for the Field.  This method is used for
     * flagging a field label as having an error.  If an error is present
     * for the field with the specified id, the specified label is returned
     * but formatted to indicate there is an error.  Currently this uses
     * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
     * for the field with the specified id, the label is returned untouched.
     *
     * @param fieldID the id of the field which may have the error
     * @param label the label to modify to indicate there is an error
     * @return the HTML formatted label
     */
    public String getFlagError(String fieldID, String label) {
        return validationErrors.getFlagErrorHTML(fieldID, label);
    }

    /**
     * Gets the Error Message for the Field.
     *
     * @param fieldID  the id of the field for which to get the error message
     * @return the HTML formatted error message
     */
    public String getErrorMessage(String fieldID) {
        return validationErrors.getErrorMessageHTML(fieldID);
    }

    /**
     * Gets the Error Message for all fields.
     *
     * @return HTML formatted error messages
     */
    public String getAllErrorMessages() {
        return validationErrors.getAllErrorMessagesHTML();
    }
}
