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

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.space.SpaceTypes;
import net.project.util.Conversion;
import net.project.util.NumberFormat;
import net.project.xml.IXMLTags;
import net.project.xml.XMLUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * Methodology Space defines what are also known as "templates".  This functionality
 * allows you to define documents, forms, and other tools that will be copied to a space
 * that chooses to "use that methodology" or "apply that template".
 *
 * Methodology Space is designed to be easily applicable to new spaces.
 *
 * @author Phil Dixon
 * @since 08/2000
 */
public class MethodologySpace extends Space implements IJDBCPersistence, IXMLPersistence, IPortfolioEntry, Serializable {

    private String useScenario = null;
    private String statusID = null;
    private String status = null;
    private String parentSpaceID = null;
    private Space parentSpace = null;
    private String basedOnSpaceID = null;
    private Space basedOnSpace = null;
    private String createdByID = null;
    private String createdBy = null;
    private Date createdDate = null;
    private String modifiedByID = null;
    private String modifiedBy = null;
    private Date modifiedDate = null;
    private String recordStatus = null;
    private Date crc = null;
    private String industryID = null;
    private String categoryID = null;
    private String keywords = null;

    private boolean isGlobal = false;

    private int[] selectedModules = null;
    private List<PnModule> modules;

    private User user = null;

    /**
     * Standard constructor to create a new <code>Methodology</code> object.
     */
    public MethodologySpace() {
        super();
        super.setType(ISpaceTypes.METHODOLOGY_SPACE);
        this.spaceType = SpaceTypes.METHODOLOGY;
    }

    /**
     * Standard constructor to create a new <code>Methodology</code> object with
     * the spaceID property set.
     */
    public MethodologySpace(String spaceID) {
        super(spaceID);
        super.setType(ISpaceTypes.METHODOLOGY_SPACE);
        this.spaceType = SpaceTypes.METHODOLOGY;
    }

    public void setUseScenario(String scenario) {
        this.useScenario = scenario;
    }

    public String getUseScenario() {
        return this.useScenario;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStatusID() {
        return this.statusID;
    }

    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the parent space id for this methodology space.
     * @param parentSpaceID the parent space id
     */
    public void setParentSpaceID(String parentSpaceID) {
        if (this.parentSpace != null && !this.parentSpace.getID().equals(parentSpaceID)) {

            // Reset parent space if current parent space object differs
            // from passed in id
            setParentSpace(null);
        }
        this.parentSpaceID = parentSpaceID;
    }    

    public String getParentSpaceID() {
        return this.parentSpaceID;
    }

    void setParentSpace(Space parentSpace) {
        this.parentSpace = parentSpace;
    }

    public Space getParentSpace() throws PersistenceException {
        if (this.parentSpace == null) {
            loadParentSpace();
        }
        return this.parentSpace;
    }

    public String getParentSpaceName() {
        try {
            return getParentSpace().getName();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public String getParentSpaceType() {
        try {
            return getParentSpace().getType();
        } catch (PersistenceException pe) {
            return null;
        }
    }
    
    public String getOwnerSpaceName() {
        try {
            return getParentSpace().getSpaceType().getName();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByID() {
        return this.createdByID;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }
    
    void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }    

    public void setModifiedByID(String modifiedByID) {
        this.modifiedByID = modifiedByID;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedByID() {
        return this.modifiedByID;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    void setCrc(Date crc) {
        this.crc = crc;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setIndustryID(String industryID) {
        this.industryID = industryID;
    }

    public String getIndustryID() {
        return this.industryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryID() {
        return this.categoryID;
    }

    public void setKeywords(String keywords) {
        // this should go to a keywords object soon...
        this.keywords = keywords;
    }

    public String getKeywords() {
        return this.keywords;
    }


    public boolean isGlobal() {
        return this.isGlobal;
    }

    public void setGlobal(String isGlobal) {
        this.isGlobal = Conversion.toBoolean(isGlobal);
    }
    
    public String getBasedOnSpaceID() {
    	return this.basedOnSpaceID;
    }

    public void setBasedOnSpaceID(String basedOnSpaceID) {
        this.basedOnSpaceID = basedOnSpaceID;
    }
    
    public Space getBasedOnSpace() throws PersistenceException {
        if (this.basedOnSpace == null) {
            loadBasedOnSpace();
        }
        return this.basedOnSpace;
    }
    
    void setBasedOnSpace(Space basedOnSpace) {
        this.basedOnSpace = basedOnSpace;
    }

    public void setSelectedModulesStringArray(String[] modules) throws ParseException {
		if (modules != null) {
		    this.selectedModules = new int[modules.length];
		    for (int i = 0; i < modules.length; i++) {
				this.selectedModules[i] = NumberFormat.getInstance()
					.parseNumber(modules[i]).intValue();
		    }
		}
    }

    public int[] getSelectedModules() {
        return this.selectedModules;
    }

    /**
	 * @return the modules
	 */
	public List<PnModule> getModules() {
		return modules;
	}

	/**
	 * Method used for returning sorted modules list for methodology.
	 */
	public TreeSet<PnModule> getSortedModules() {
	    TreeSet<PnModule> sortedModules = new TreeSet<PnModule>(new Comparator<PnModule>() {
		    // Comparator interface requires defining compare method.
		    public int compare(PnModule moda, PnModule modb) {
		        //... Sort in alphabetical ignoring case.
		        if (moda == null || modb == null || moda.getDescription() == null || modb.getDescription() == null) {
		            return -1;
		        } else if (moda.equals(modb)) {
		            return 1;
		        } else {
		        	return PropertyProvider.get(moda.getDescription().substring(1))
		            	.compareToIgnoreCase(PropertyProvider.get(modb.getDescription().substring(1)));
		        }
		    }
	    	
	    });
	    sortedModules.addAll(this.modules);
	    
		return sortedModules;
	}

	/**
	 * @param modules the modules to set
	 */
	public void setModules(List<PnModule> modules) {
		this.modules = modules;
		
		if(modules.size() > 0) {
			this.selectedModules = new int[modules.size()];
			int i = 0;
			for(PnModule module : modules) {
				this.selectedModules[i++] = module.getModuleId();
			}
		}
	}

	/**
     * Load the Methodology space from current id.
     *
     * @throws PersistenceException if an error occurs trying to load the space
     * from the database.
     */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        query.append("select methodology_id, parent_space_id, methodology_name, ");
        query.append("methodology_desc, use_scenario_clob, created_by_id, created_by, modified_by_id, modified_by, ");
        query.append("created_date, modified_date, record_status, is_global, crc, based_on_space_id ");
        query.append("from pn_methodology_view ");
        query.append("where methodology_id = ? ");
        query.append("and record_status = 'A' ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            if (db.result.next()) {
                //Set private member variables for the values returned using the query.
                setParentSpaceID(db.result.getString("parent_space_id"));
                setName(db.result.getString("methodology_name"));
                setDescription(db.result.getString("methodology_desc"));
                setUseScenario(ClobHelper.read(db.result.getClob("use_scenario_clob")));
                setCreatedBy(db.result.getString("created_by"));
                setCreatedByID(db.result.getString("created_by_id"));
                setCreatedDate(db.result.getTimestamp("created_date"));
                setModifiedBy(db.result.getString("modified_by"));
                setModifiedByID(db.result.getString("modified_by_id"));
                setModifiedDate(db.result.getTimestamp("modified_date"));
                setGlobal(db.result.getString("is_global"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setBasedOnSpaceID(db.result.getString("based_on_space_id"));
                isLoaded = true;

            } else {
            	Logger.getLogger(MethodologySpace.class).error("MethodologySpace.load() failed to load row for id " + getID());
                throw new PersistenceException("Methodology space load operation failed.");

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(MethodologySpace.class).error("MethodologySpace.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Methodology space load operation failed.", sqle);

        } finally {
            db.release();

        } //end try

		// load methodology modules
		setModules( ServiceFactory.getInstance().getPnMethodologyModulesService()
					.findMethodologyModules(Integer.valueOf(getID())) );
    }

    private void loadParentSpace() throws PersistenceException {
        Space space = SpaceFactory.constructSpaceFromID(this.parentSpaceID);
        space.load();
        setParentSpace(space);
    }
    
    private void loadBasedOnSpace() throws PersistenceException {
        if(this.basedOnSpaceID != null) {
	    	Space space = SpaceFactory.constructSpaceFromID(this.basedOnSpaceID);
	        space.load();
	        setBasedOnSpace(space);
        }
    }

    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);

            if (isLoaded()) {
                /* A methodology has previously been defined with this id, and
                   it has already been already been loaded - so just do a modify*/
                modifyMethodology(db);

            } else {
                /* This methodology has never been persisted to the database,
                   create a new methodology. */
                createMethodology(db);
            }

            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("Methodology space store operation failed: " + sqle, sqle);

        } catch (Exception e) {
            throw new PersistenceException("Error occured during Methodology space store operation: " + e, e);

        } finally {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Simply release
            }
            db.release();

        }

//	        ServiceFactory.getInstance().getPnMethodologyModulesService().saveOrUpdateMethodologyModules(Integer.valueOf(getID()), getSelectedModules());
        if(!isLoaded())
        	ServiceFactory.getInstance().getPnMethodologyModulesService().saveOrUpdateMethodologyModules(Integer.valueOf(getID()), getSelectedModules());
    }


    /**
     * Creates a methodology object.
     * @param db the DBBean to user for the update
     * @throws SQLException if there is a problem creating the methodology
     * @throws PersistenceException if there is a problem creating the methodology
     */
    private void createMethodology(DBBean db) throws SQLException, PersistenceException {
        int index = 0;
        int useScenarioClobIndex = 0;
        int methodologyIDIndex = 0;

        db.prepareCall("{call METHODOLOGY.CREATE_METHODOLOGY(?,?,?,?,?,?,?,?,?,?,?)}");
        db.cstmt.setString(++index, getName());
        db.cstmt.setString(++index, getDescription());
        DatabaseUtils.setInteger(db.cstmt, ++index, this.parentSpaceID);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.user.getID());
        DatabaseUtils.setInteger(db.cstmt, ++index, this.industryID);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.categoryID);
        db.cstmt.setInt(++index, Conversion.booleanToInt(this.isGlobal));
        db.cstmt.setInt(++index, (this.useScenario == null ? 1 : 0));
        db.cstmt.registerOutParameter((useScenarioClobIndex = ++index), java.sql.Types.CLOB);
        db.cstmt.registerOutParameter((methodologyIDIndex = ++index), java.sql.Types.INTEGER);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.basedOnSpaceID);

        db.executeCallable();

        if (this.useScenario != null) {
            ClobHelper.write(db.cstmt.getClob(useScenarioClobIndex), this.useScenario);
        }

        setID(db.cstmt.getString(methodologyIDIndex));

    }

    /**
     * Update an existing methodology.
     */
    private void modifyMethodology(DBBean db) throws SQLException, PersistenceException {

        int index = 0;
        int useScenarioClobIndex = 0;

        db.prepareCall("{call METHODOLOGY.MODIFY_METHODOLOGY(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        db.cstmt.setString(++index, getID());
        db.cstmt.setString(++index, getName());
        db.cstmt.setString(++index, getDescription());
        DatabaseUtils.setInteger(db.cstmt, ++index, this.parentSpaceID);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.user.getID());
        DatabaseUtils.setInteger(db.cstmt, ++index, this.industryID);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.categoryID);
        db.cstmt.setInt(++index, Conversion.booleanToInt(this.isGlobal));
        DatabaseUtils.setTimestamp(db.cstmt, ++index, this.crc);
        db.cstmt.setInt(++index, (this.useScenario == null ? 1 : 0));
        db.cstmt.registerOutParameter((useScenarioClobIndex = ++index), java.sql.Types.CLOB);

        db.executeCallable();

        if (this.useScenario != null) {
            ClobHelper.write(db.cstmt.getClob(useScenarioClobIndex), this.useScenario);
        }
    }


    /**
     * <code>remove</code> soft-deletes a methodology from the database so that
     * it does not show up in the list of methodologies.
     *
     * This method requires that you first call setID() to indicate which id
     * you'd like to delete.  (load() is not required).
     *
     * @since Gecko (10/20/2001)
     * @throws PersistenceException if a database error occurs or if setID() is
     * not called prior to calling remove().
     */
    public void remove() throws net.project.persistence.PersistenceException {
        // First, check to make sure that an id has already been set
        if ((getID() == null) || (getID().equals(""))) {
        	Logger.getLogger(MethodologySpace.class).debug("MethodologySpace.remove() requires an id parameter, which was not provided");
            throw new PersistenceException("Methodology space remove operation failed.");
        }

        DBBean db = new DBBean();
        try {
            //Change the status code for methodology in the database.  (This is only a soft-delete)
            db.executeQuery("update pn_methodology_space set record_status = 'D' where methodology_id = " + getID());
        } catch (SQLException sqle) {
        	Logger.getLogger(MethodologySpace.class).error("MethodologySpace.store() threw a SQL Exception: " + sqle);
            throw new PersistenceException("Methodology space remove operation failed.", sqle);
        } finally {
            //Free the connection
            db.release();
        }
    }

    /**
     * Get the XML representation of the object without XML header tag.
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();
        xml.append(IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());
        return xml.toString();
    }


    /** Get the XML representation of the object. */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<methodology>");
        xml.append("<object_id>" + XMLUtils.escape(getID()) + "</object_id>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        xml.append("<use_scenario>" + XMLUtils.escape(this.useScenario) + "</use_scenario>");
        xml.append("<status_id>" + XMLUtils.escape(this.statusID) + "</status_id>");
        xml.append("<status>" + XMLUtils.escape(this.status) + "</status>");
        xml.append("<parent_space_id>" + XMLUtils.escape(this.parentSpaceID) + "</parent_space_id>");
        xml.append("<parent_space_name>" + XMLUtils.escape(getOwnerSpaceName()) + "</parent_space_name>");
        xml.append("<parent_space_type>" + XMLUtils.escape(getParentSpaceType()) + "</parent_space_type>");
        xml.append("<created_by_id>" + XMLUtils.escape(this.createdByID) + "</created_by_id>");
        xml.append("<created_by>" + XMLUtils.escape(this.createdBy) + "</created_by>");
        xml.append("<modified_by_id>" + XMLUtils.escape(this.modifiedByID) + "</modified_by_id>");
        xml.append("<modified_by>" + XMLUtils.escape(this.modifiedBy) + "</modified_by>");
        xml.append("<is_global>" + XMLUtils.escape(Conversion.booleanToString(isGlobal())) + "</is_global>");
        xml.append("<crc>" + XMLUtils.escape("" + this.crc) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(this.recordStatus) + "</record_status>");
        xml.append("</methodology>");

        return xml.toString();
    }
    
    /** Get the JSON representation of the object. */
    public String getAsJSON() {
        StringBuffer json = new StringBuffer("{ \n");

        json.append("objectId			:	'" + getID() + "', \n");
        json.append("name				:	'" + ( getName() == null ? "" : StringEscapeUtils.escapeJavaScript(getName()) ) + "', \n");
        json.append("description		:	'" + ( getDescription() == null ? "" : StringEscapeUtils.escapeJavaScript(getDescription()) ) + "', \n");
        json.append("useScenario		:	'" + ( this.useScenario == null ? "" : StringEscapeUtils.escapeJavaScript(this.useScenario) ) + "', \n");
        json.append("owningBusiness		:	'" + ( getParentSpaceName() == null ? "" : StringEscapeUtils.escapeJavaScript(getParentSpaceName()) ) + "', \n");
        json.append("statusId			:	'" + this.statusID + "', \n");
        json.append("status				:	'" + StringEscapeUtils.escapeJavaScript(this.status) + "', \n");
        json.append("parentSpaceId		:	'" + this.parentSpaceID + "', \n");
        json.append("parentSpaceName	:	'" + ( getOwnerSpaceName() == null ? "" : StringEscapeUtils.escapeJavaScript(getOwnerSpaceName()) ) + "', \n");
        json.append("parentSpaceType	:	'" + ( getParentSpaceType() == null ? "" : StringEscapeUtils.escapeJavaScript(getParentSpaceType()) ) + "', \n");
        json.append("createdById		:	'" + this.createdByID + "', \n");
        json.append("createdBy			:	'" + ( this.createdBy == null ? "" : StringEscapeUtils.escapeJavaScript(this.createdBy) ) + "', \n");
        json.append("createdByHref		:	'" + SessionManager.getJSPRootURL() + "/blog/view/" + this.createdByID + "/" + this.createdByID + "/person/" + Module.PERSONAL_SPACE + "?module=" + Module.PERSONAL_SPACE + "', \n");
        json.append("createdDate		:	'" + SessionManager.getUser().formatDateForLocale(this.createdDate) + "', \n");
        json.append("modifiedById		:	'" + this.modifiedByID + "', \n");
        json.append("modifiedDate		:	'" + this.modifiedDate + "', \n");
        json.append("modifiedBy			:	'" + ( this.modifiedBy == null ? "" : StringEscapeUtils.escapeJavaScript(this.modifiedBy) ) + "', \n");
        json.append("isGlobal			:	'" + Conversion.booleanToString(isGlobal()) + "', \n");
        json.append("modules			:	 "  + getModulesAsJSONArray() + ", \n");
        json.append("basedOnSpaceId		:	'" + this.basedOnSpaceID + "', \n");
        // argument used for categorization 
        json.append("basedOnSpaceType	:	'" + DatabaseUtils.getTypeForObjectID(this.basedOnSpaceID) + "', \n");
        json.append("crc				:	'" + this.crc + "', \n");
        json.append("recordStatus		:	'" + this.recordStatus + "' \n");
        json.append("} \n");

        return json.toString();
    }
    
    public String getModulesAsJSONArray() {
        StringBuffer json = new StringBuffer("[ ");
        
    	if( getModules() != null && getModules().size() > 0 ) {
    		int index = 1;
    		for(PnModule module : getSortedModules()) {
    			json.append("{ ")
    					.append("moduleId	:	'").append(module.getModuleId()).append("', ")
    					.append("moduleDescription	:	'").append(PropertyProvider.get(module.getDescription().substring(1))).append("', ")
    					.append("moduleName	:	'").append(module.getName()).append("' ")
    				.append(" }");
    			if( index++ < getModules().size() ) {
    				json.append(", ");
    			}
    		}
    	}
    	
    	json.append(" ]");
        return json.toString();
    }

    /**
     * Restores the MethodologySpace to the same state that should exist
     * immediately after creation.  This allows you to reuse the methodology
     * bean as if it were just created.
     */
    public void clear() {
        super.clear();
        super.setType(Space.METHODOLOGY_SPACE);
        useScenario = null;
        statusID = null;
        status = null;
        parentSpaceID = null;
        parentSpace = null;
        createdByID = null;
        createdBy = null;
        createdDate = null;
        modifiedByID = null;
        modifiedBy = null;
        modifiedDate = null;
        recordStatus = null;
        crc = null;
        industryID = null;
        categoryID = null;
        isGlobal=false;
        keywords = null;
        selectedModules = null;
        isLoaded = false;
        basedOnSpaceID = null;
        basedOnSpace = null;
        modules = null;
    }
    
    /**
     * Checking template with given name is already exist or not.
     * @return
     */
    public boolean isNameExist(){
    	  String qstrGetMethodologies = "select methodology_name" +
            " from pn_methodology_view" +
            " where parent_space_id = " + this.getParentSpaceID() + 
            " and methodology_name = '" + this.getName() + "' and record_status = 'A' ";
        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrGetMethodologies);
            return db.result.next();
        }catch(Exception e){
        	Logger.getLogger(MethodologySpace.class).debug("MethodologyProvider.isNameExist() threw sql exception: " + e);
        }finally {
            db.release();
        }
        return false;
    }

}
