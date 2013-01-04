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
|   $Revision: 18486 $
|       $Date: 2008-12-04 17:09:34 -0200 (jue, 04 dic 2008) $
|     $Author: puno $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.DBErrorCodes;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides the definition of a workflow.  The actual in-process workflow
 * is described by the workflow Envelope.
 */
public class Workflow
    implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    /**
     * Holds object types which may be added to this workflow
     */
    private class ObjectType implements Serializable {
        String objectType = null;
        String subTypeID = null;
    }

    private String workflowID = null;
    private String name = null;
    private String description = null;
    private String notes = null;
    private String ownerID = null;
    private boolean isPublished = false;
    private String strictnessID = null;
    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    private boolean isGeneric = true;
    private java.util.Date crc = null;
    private String recordStatus = null;
    private String spaceID = null;

    private String strictnessName = null;
    private String ownerFullName = null;
    private String createdByFullName = null;
    private String modifiedByFullName = null;
    private int activeEnvelopeCount = 0;

    /** The steps for this workflow */
    private StepList steps = null;

    /** The objects permitted at this workflow */
    private ArrayList objectTypes = null;

    private boolean isLoaded = false;
    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = new DBBean();

    /** Indicates whether the workflow may be removed.  This is set to true only
     * when the appropriate checks have been performed */
    private boolean isRemovePermitted = false;
    /** Indicates whether the workflow removal was successful or not. */
    private boolean isRemoveSuccessful = false;

    /**
     * Create a new workflow.
     */
    public Workflow() {
    }


    /**
     * Return the workflow's id
     * @return the id of the workflow
     */
    public String getID() {
        return this.workflowID;
    }

    /**
     * Set the name of the workflow.
     * @param name the name of the workflow
     */
    public void setName(String name) {
        this.name = (name == null)? null : name.trim();
    }

    /**
     * Return the name of the workflow
     * @return the name of the workflow
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the workflow's description
     * @param description the description of the workflow
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the workflow's description
     * @return the description of the workflow
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the workflow's notes
     * @param notes the notes of the workflow
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Return the workflow's notes
     * @return the notes of the workflow
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Getter for property ownerID.
     * @return Value of property ownerID.
     */
    public String getOwnerID() {
        return this.ownerID;
    }

    /**
     * Setter for property ownerID.
     * @param ownerID New value of property ownerID.
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Getter for property isPublished.
     * @return Value of property isPublished.
     */
    public boolean isPublished() {
        return this.isPublished;
    }

    /**
     * Setter for property isPublished.  Note this does NOT do any publishing
     * checks.  As a result, it has default access only.<br>
     * the publish() method should be used to do the checks
     * @param isPublished New value of property isPublished.
     * @see #publish
     */
    void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * Getter for property strictnessID.
     * @return Value of property strictnessID.
     */
    public String getStrictnessID() {
        return this.strictnessID;
    }

    /**
     * Setter for property strictnessID.
     * @param strictnessID New value of property strictnessID.
     */
    public void setStrictnessID(String strictnessID) {
        this.strictnessID = strictnessID;
    }

    public boolean isGeneric() {
        return this.isGeneric;
    }

    public void setGeneric(boolean isGeneric) {
        this.isGeneric = isGeneric;
    }


    public String getCreatedBy() {
        return this.createdBy;
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public java.util.Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    void setCreatedDatetime(java.util.Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    void setModifiedBy(String ModifiedBy) {
        this.modifiedBy = ModifiedBy;
    }

    public java.util.Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    void setModifiedDatetime(java.util.Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    java.util.Date getCrc() {
        return this.crc;
    }

    void setCrc(java.util.Date crc) {
        this.crc = crc;
    }

    String getRecordStatus() {
        return this.recordStatus;
    }

    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getCreatedByFullName() {
        return this.createdByFullName;
    }

    void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getModifiedByFullName() {
        return this.modifiedByFullName;
    }

    void setModifiedByFullName(String modifiedByFullName) {
        this.modifiedByFullName = modifiedByFullName;
    }

    public String getOwnerFullName() {
        return this.ownerFullName;
    }

    void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public String getStrictnessName() {
        return this.strictnessName;
    }

    void setStrictnessName(String strictnessName) {
        this.strictnessName = strictnessName;
    }

    public int getActiveEnvelopeCount() {
        return this.activeEnvelopeCount;
    }

    void setActiveEnvelopeCount(int activeEnvelopeCount) {
        this.activeEnvelopeCount = activeEnvelopeCount;
    }


    /**
     * Returns spaceID to which workflow belongs
     * @return the spaceID
     */
    public String getSpaceID() {
        return this.spaceID;
    }

    /**
     * Set the space to which the workflow belongs.
     * @param spaceID the spaceID
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Set the current user.  This is required before manipulating workflow.
     * It is used to stamp the creation/modification userid in the database.
     * @param user the user manipulating the workflow.
     */
    public void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return this.user;
    }

    /**
     * Return the steps belonging to this workflow
     * @return the steps
     * @throws PersistenceException if there is a problem loading the steps
     */
    public StepList getSteps() throws PersistenceException {
        if (this.steps == null) {
            loadSteps();
        }
        return this.steps;
    }

    void setSteps(StepList steps) {
        this.steps = steps;
    }

    /**
     * Return the transition belonging to this workflow
     * @return an array of workflow transitions
     */
    public ArrayList getTransitions() {
        return null;
    }

    /**
     * Return the object types for this workflow
     * @return a list of object types
     */
    private ArrayList getObjectTypes() throws PersistenceException {
        if (this.objectTypes == null) {
            loadObjectTypes();
        }
        return this.objectTypes;
    }

    /**
     * Set the object types for this workflow
     * @param objectTypes the object type list
     */
    private void setObjectTypes(ArrayList objectTypes) {
        this.objectTypes = objectTypes;
    }

    /**
     * Return the only object type name for this workflow.
     * Note: This method simply returns the name for the first object type
     * This will be changed when support for multiple object types is added
     */
    public String getObjectTypeName() {
        ArrayList objectTypes = null;
        try {
            objectTypes = getObjectTypes();
        } catch (PersistenceException pe) {
            return "";
        }
        if (objectTypes.size() > 0) {
            return ((ObjectType)objectTypes.get(0)).objectType;
        } else {
            return "";
        }
    }

    /**
     * Return the sub type id for the object type for this workflow.
     *
     * @return sub type id
     */
    public String getSubTypeID() {
        ArrayList objectTypes = null;
        try {
            objectTypes = getObjectTypes();
        } catch (PersistenceException pe) {
            return "";
        }
        if (objectTypes.size() > 0) {
            return ((ObjectType)objectTypes.get(0)).subTypeID;
        } else {
            return "";
        }
    }

    /**
     * Add an object type to the workflow.
     *
     * <em>This method currently permits only one object type to be added</em>
     * The last object type added will be the one persisted.
     *
     * @param objectTypeName the name of the object type (e.g. "document")
     * @param subTypeID the id of the sub type (this is object type-sepcific)
     */
    public void addObjectType(String objectTypeName, String subTypeID) {
        ObjectType objectType = new ObjectType();
        objectType.objectType = objectTypeName;
        objectType.subTypeID = subTypeID;
        this.objectTypes = new ArrayList();
        this.objectTypes.add(objectType);

        /* If no specific object type AND sub type, set this workflow to "generic" */
        if ((objectTypeName == null || objectTypeName.equals("")) &&
            (subTypeID == null || subTypeID.equals(""))) {
            setGeneric(true);
        } else {
            setGeneric(false);
        }
    }

    /**
     * Publish this workflow.  Invokes checks.  Creates a validation error
     * if there is a problem.  This does NOT store the workflow.
     */
    public void publish() {
        clearErrors();

        /* Check to make sure we have an initial step */
        validateSteps();

        /* Set the published flag based on presence of errors */
        if (hasErrors()) {
            setPublished(false);
        } else {
            setPublished(true);
        }
    }

    /**
     * Unpublish this workflow.
     */
    public void unpublish() {
        clearErrors();

        setPublished(false);
    }

    /**
     * Clear all Workflow properties
     */
    public void clear() {
        setID(null);
        setName(null);
        setDescription(null);
        setNotes(null);
        setOwnerID(null);
        setPublished(false);
        setStrictnessID(null);
        setGeneric(true);
        setCreatedBy(null);
        setCreatedDatetime(null);
        setModifiedBy(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setSpaceID(null);

        setStrictnessName(null);
        setOwnerFullName(null);
        setCreatedByFullName(null);
        setModifiedByFullName(null);
        setActiveEnvelopeCount(0);

        setSteps(null);
        setObjectTypes(null);

        setLoaded(false);
    }

    /**
     * @return True if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /*
        Implementing IJDBCPersistence
    */

    /**
     * Set the workflow's id property
     * @param workflowID the id property
     */
    public void setID(java.lang.String workflowID) {
        this.workflowID = workflowID;
    }

    /**
     * Load the Workflow from the underlying JDBC object.
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("SELECT workflow_id, workflow_name, strictness_id, workflow_description, created_by_id, ");
        queryBuff.append("notes, created_datetime, modified_by_id, owner_id, modified_datetime, is_published, ");
        queryBuff.append("is_generic, crc, record_status, ");
        queryBuff.append("strictness_name, owner_full_name, created_by_full_name, modified_by_full_name, ");
        queryBuff.append("active_envelope_count, space_id, crc, record_status  ");
        queryBuff.append("FROM pn_workflow_view ");
        queryBuff.append("WHERE workflow_id = " + getID() + " ");

        try {
        	setGeneric(false);
        	loadObjectTypes();
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                setID(db.result.getString("workflow_id"));
                setName(db.result.getString("workflow_name"));
                setStrictnessID(db.result.getString("strictness_id"));
                setDescription(db.result.getString("workflow_description"));
                setCreatedBy(db.result.getString("created_by_id"));
                setNotes(db.result.getString("notes"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setModifiedBy(db.result.getString("modified_by_id"));
                setOwnerID(db.result.getString("owner_id"));
                setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                setPublished(Conversion.toBoolean(db.result.getString("is_published")));

                setStrictnessName(PropertyProvider.get(db.result.getString("strictness_name")));
                setOwnerFullName(db.result.getString("owner_full_name"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setModifiedByFullName(db.result.getString("modified_by_full_name"));
                setActiveEnvelopeCount(db.result.getInt("active_envelope_count"));
                setSpaceID(db.result.getString("space_id"));
                setGeneric(Conversion.toBoolean(db.result.getString("is_generic")));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));

                setLoaded(true);
            }

        } catch (SQLException sqle) {
            setLoaded(false);
            Logger.getLogger(Workflow.class).error("Workflow.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow load operation failed.", sqle);

        } finally {
            db.release();
        }

    } // load()

    /**
     * Load the steps for this workflow
     * @throws PersistenceException if there is a problem loading the steps
     */
    private void loadSteps() throws PersistenceException {
        WorkflowManager manager = new WorkflowManager();
        setSteps(manager.getSteps(getID()));
    }

    /**
     * Load the object types for this workflow
     * @throws PersistenceException if there is a problem loading the list
     */
    private void loadObjectTypes() throws PersistenceException {
        ObjectType objectType = null;
        ArrayList objectTypes = new ArrayList();
        StringBuffer queryBuff = new StringBuffer();

        /* Only load object types if not generic */
        if (!isGeneric()) {
            queryBuff.append("select workflow_id, object_type, sub_type_id ");
            queryBuff.append("from pn_workflow_has_object_type ");
            queryBuff.append("where workflow_id = " + getID() + " ");
            try {
                db.executeQuery(queryBuff.toString());
                while (db.result.next()) {
                    objectType = new ObjectType();
                    objectType.objectType = db.result.getString("object_type");
                    if (db.result.getString("sub_type_id") == null || "null".equals(db.result.getString("sub_type_id"))){
                    	objectType.subTypeID = "";
                    }else{
                    	objectType.subTypeID = db.result.getString("sub_type_id");
                    }
                    objectTypes.add(objectType);
                }

            } catch (SQLException sqle) {
            	sqle.printStackTrace();
            	Logger.getLogger(Workflow.class).error("Workflow.loadObjectTypes() threw an SQL exception: " + sqle);
                throw new PersistenceException("Workflow load object types operation failed.", sqle);
            } finally {
                db.release();

            }
        }
        setObjectTypes(objectTypes);
    }

    /**
     * Save the Workflow to the underlying JDBC object.  This utilizes stored
     * procedures to do the work.
     * Note: If the store is successful this objects properties will be
     * cleared, except for the workflowID.  Thus a load() may be performed
     * afterwards to load the remaining properties.
     * @throws PersistenceException Thrown to indicate a failure storing
     * to the database, a system-level error.
     */
    public void store() throws net.project.persistence.PersistenceException {
        /* errorCode will be returned from stored procedures */
        int errorCode = -1;
        /* WorkflowID which was created or updated */
        String storedWorkflowID;

        if (isLoaded()) {
            // Modify existing workflow
            /*
            procedure modify_workflow (
                i_workflow_id    in varchar2,
                i_workflow_name  in varchar2,
                i_workflow_description in varchar2,
                i_notes          in varchar2,
                i_owner_id       in varchar2,
                i_is_published   in number,
                i_strictness_id  in varchar2,
                i_is_generic     in number,
                i_modified_by_id in varchar2,
                i_crc            in date,
                o_return_value   out number)
            */
            try {
                db.openConnection();
                db.connection.setAutoCommit(false);
                db.prepareCall("BEGIN workflow.modify_workflow(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                db.cstmt.setString(1, getID());
                db.cstmt.setString(2, getName());
                db.cstmt.setString(3, getDescription());
                db.cstmt.setString(4, getNotes());
                db.cstmt.setString(5, getOwnerID());
                db.cstmt.setInt(6, (isPublished() ? 1 : 0));
                db.cstmt.setString(7, getStrictnessID());
                db.cstmt.setInt(8, (isGeneric() ? 1 : 0));
                db.cstmt.setString(9, getUser().getID());
                db.cstmt.setTimestamp(10, new Timestamp(getCrc().getTime()));
                db.cstmt.registerOutParameter(11, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();
                // Get error code for later handling
                errorCode = db.cstmt.getInt(11);
                // Remember workflowID we just modified
                storedWorkflowID = getID();

                db.closeCStatement();
                if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                    /* Rollback if anything went wrong
                       An exception is thrown later based on this errorCode */
                    db.connection.rollback();

                } else {
                    /*
                        Successfully created the workflow
                        Now add object types
                     */
                    storeObjectTypes(storedWorkflowID);

                    /* COMMIT all previous updates */
                    db.connection.commit();
                    errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
                }

            } catch (SQLException sqle) {
            	Logger.getLogger(Workflow.class).error(
                    "Workflow.store(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Workflow store operation failed.", sqle);

            } finally {
                if (db.connection != null) {
                    try {
                        db.connection.rollback();
                    } catch (SQLException sqle) {
                        /* Nothing we can do here except release everything */
                    }
                }
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Workflow.store()", errorCode);

            } catch (net.project.base.UpdateConflictException e) {
                RecordModifiedException rme =
                    new RecordModifiedException("The workflow has been modified by another user.  Please try again.");
                rme.setErrorCode(WORKFLOW_RECORD_MODIFIED);
                throw rme;
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } else {
            // Create new workflow
            /*
            PROCEDURE create_workflow
              ( i_space_id       IN varchar2,
                i_workflow_name  IN varchar2,
                i_workflow_description IN varchar2,
                i_notes          IN varchar2,
                i_owner_id       IN varchar2,
                i_is_published   IN number,
                i_strictness_id  IN varchar2,
                i_is_generic     in number,
                i_created_by_id  IN varchar2,
                o_workflow_id    OUT varchar2,
                o_return_value   OUT number);
            */
            try {
                db.openConnection();
                db.connection.setAutoCommit(false);
                db.prepareCall("BEGIN workflow.create_workflow(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, getSpaceID());
                db.cstmt.setString(2, getName());
                db.cstmt.setString(3, getDescription());
                db.cstmt.setString(4, getNotes());
                db.cstmt.setString(5, getOwnerID());
                db.cstmt.setInt(6, (isPublished() ? 1 : 0));
                db.cstmt.setString(7, getStrictnessID());
                db.cstmt.setInt(8, (isGeneric() ? 1 : 0));
                db.cstmt.setString(9, getUser().getID());
                db.cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);
                db.cstmt.registerOutParameter(11, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get the newly created workflowID
                storedWorkflowID = db.cstmt.getString(10);
                // Get error code for later handling
                errorCode = db.cstmt.getInt(11);

                db.closeCStatement();
                if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                    /* Rollback if anything went wrong
                       An exception is thrown later based on this errorCode */
                    db.connection.rollback();

                } else {
                    /*
                        Successfully created the workflow
                        Now add object types
                     */
                    storeObjectTypes(storedWorkflowID);
                    /* COMMIT all previous updates */
                    db.connection.commit();
                    errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
                }

            } catch (SQLException sqle) {
            	Logger.getLogger(Workflow.class).error(
                    "Workflow.store(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Workflow store operation failed.: " + sqle, sqle);

            } finally {
                if (db.connection != null) {
                    try {
                        db.connection.rollback();
                    } catch (SQLException sqle) {
                        /* Nothing we can do here except release everything */
                    }
                }
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Workflow.store()", errorCode);
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } // if (isLoaded)

        /*
            Since the current properties are out of date (the stored procedure will
            have inserted values for modified date time etc.) I will clear the
            object and simply set the workflowID;
        */
        clear();
        setID(storedWorkflowID);
    }

    /**
     * Store the object types. This method DOES NOT COMMIT.
     * @param workflowID the workflow id to store against
     * @throws SQLException if there is a problem storing the object types
     * @throws PersistenceException if there is a problem storing the object types
     * probably due to a problem getting the object types
     */
    private void storeObjectTypes(String workflowID) throws SQLException, PersistenceException {
        Iterator it = null;
        ObjectType objectType = null;
        String objectTypeName = null;
        String subTypeID = null;
        ArrayList objectTypes = getObjectTypes();           // Read the object types before deleting them !!!

        StringBuffer deleteSQL = new StringBuffer();
        deleteSQL.append("delete from pn_workflow_has_object_type where workflow_id = " + workflowID);

        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("insert into pn_workflow_has_object_type ");
        insertSQL.append("(workflow_id, object_type, sub_type_id) ");
        insertSQL.append(" values ");
        insertSQL.append("(?, ?, ?) ");

        /* delete record all the object type records */
        db.executeQuery(deleteSQL.toString());
        db.closeStatement();

        if (!isGeneric()) {
            /* create all the object type records */
            db.prepareStatement(insertSQL.toString());
            it = objectTypes.iterator();
            while (it.hasNext()) {
                objectType = (ObjectType)it.next();
                objectTypeName = objectType.objectType;
                subTypeID = objectType.subTypeID;
                db.pstmt.setString(1, workflowID);
                db.pstmt.setString(2, objectTypeName);
                db.pstmt.setString(3, subTypeID);
                db.pstmt.addBatch();
            }

            db.pstmt.executeBatch();
            db.closePStatement();

        }

    }

    /**
     * Prepare the workflow to be removed:  Check it is not published and that there are
     * no active envelopes.
     * This routine populates the errorTable.
     */
    public void prepareRemove() {
        boolean isMetCriteria = true;
        setRemovePermitted(false);

        if (isPublished()) {
            errors.put("remove_is_published", null, PropertyProvider.get("prm.workflow.ispublished.remove.validation.message"));
            isMetCriteria &= false;
        }
        if (getActiveEnvelopeCount() > 0) {
            errors.put("remove_active_envelope_count", null, PropertyProvider.get("prm.workflow.hasactiveenvelope.remove.validation.message"));
            isMetCriteria &= false;
        }

        setRemovePermitted(isMetCriteria);
    }

    String getPrepareRemoveErrorsPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    String getRemoveResultPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    public boolean isRemovePermitted() {
        return this.isRemovePermitted;
    }

    void setRemovePermitted(boolean isRemovePermitted) {
        this.isRemovePermitted = isRemovePermitted;
    }

    public boolean isRemoveSuccessful() {
        return this.isRemoveSuccessful;
    }

    void setRemoveSuccessful(boolean isRemoveSuccessful) {
        this.isRemoveSuccessful = isRemoveSuccessful;
    }

    /**
     * Delete the Workflow from the database.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws net.project.persistence.PersistenceException {
        /* Default to a failed removal */
        setRemoveSuccessful(false);

        /* Repeat the checks that should have been done prior to this method call */
        prepareRemove();

        if (!isRemovePermitted()) {
            /*
                Remove is not permitted
                Message will have been created and will be displayed to user
             */

        } else {
            /*
                Remove is permitted.
                Do it.
             */
            removeWorkflow();
            setRemoveSuccessful(true);
        }
    }

    /**
     * Performs the actual remove
     * @throws PersistenceException if there is a problem performing the remove
     */
    private void removeWorkflow() throws PersistenceException {
        /* errorCode will be returned from stored procedures */
        int errorCode = -1;

        try {
            db.prepareCall("{call workflow.remove_workflow(?, ?, ?, ?)}");
            db.connection.setAutoCommit(false);
            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, getUser().getID());
            db.cstmt.setTimestamp(3, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
            db.closeCStatement();
            if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                /* Rollback if anything went wrong
                   An exception is thrown later based on this errorCode */
                db.connection.rollback();

            } else {
                /*
                   Successfully removed the workflow
                   Commit work
                 */
                db.connection.commit();
                errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Workflow.class).error(
                "Workflow.remove(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                sqle);
            throw new PersistenceException("Workflow remove operation failed.", sqle);
        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle) {
                    /* Nothing we can do here except release everything */
                }
            }
            db.release();
        }

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("Workflow.remove()", errorCode);

        } catch (net.project.base.UpdateConflictException e) {
            RecordModifiedException rme =
                new RecordModifiedException("The workflow has been modified by another user.  Please try again.");
            rme.setErrorCode(WORKFLOW_RECORD_MODIFIED);
            throw rme;
        } catch (net.project.persistence.RecordLockedException e) {
            WorkflowPersistenceException wpe =
                new WorkflowPersistenceException("The workflow is currently locked by another user.  Please try again.");
            wpe.setErrorCode(WORKFLOW_RECORD_LOCKED);
            throw wpe;

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage());
        }

    }
    /*
        Implement IXMLPersistence
     */

    /**
     * Converts the workflow into an XML string including the XML header
     * tag.
     * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<workflow>\n");
        xml.append(getXMLElements());
        xml.append("</workflow>\n");

        return xml.toString();
    }

    /**
     * Returns the basic workflow XML elements
     * @return XML elements as string
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();

        xml.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        xml.append("<workflow_id>" + XMLUtils.escape(getID()) + "</workflow_id>\n");
        xml.append("<space_id>" + XMLUtils.escape(getSpaceID()) + "</space_id>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>\n");
        xml.append("<notes>" + XMLUtils.escape(getNotes()) + "</notes>\n");
        xml.append("<owner_id>" + XMLUtils.escape(getOwnerID()) + "</owner_id>\n");
        xml.append("<owner_full_name>" + XMLUtils.escape(getOwnerFullName()) + "</owner_full_name>");
        xml.append("<is_published>" + XMLUtils.escape((isPublished() ? "1" : "0")) + "</is_published>");
        xml.append("<strictness_id>" + XMLUtils.escape(getStrictnessID()) + "</strictness_id>");
        xml.append("<strictness_name>" + XMLUtils.escape(getStrictnessName()) + "</strictness_name>");
        xml.append("<created_by_id>" + XMLUtils.escape(getCreatedBy()) + "</created_by_id>");
        xml.append("<created_datetime>" + XMLUtils.escape(Conversion.dateToString(getCreatedDatetime())) + "</created_datetime>");
        xml.append("<created_by_full_name>" + XMLUtils.escape(getCreatedByFullName()) + "</created_by_full_name>");
        xml.append("<modified_by_id>" + XMLUtils.escape(getModifiedBy()) + "</modified_by_id>");
        xml.append("<modified_datetime>" + XMLUtils.escape(Conversion.dateToString(getModifiedDatetime())) + "</modified_datetime>");
        xml.append("<modified_by_full_name>" + XMLUtils.escape(getModifiedByFullName()) + "</modified_by_full_name>");
        xml.append("<is_generic>" + XMLUtils.escape((isGeneric() ? "1" : "0")) + "</is_generic>");
        xml.append("<active_envelope_count>" + XMLUtils.escape("" + getActiveEnvelopeCount()) + "</active_envelope_count>");
        xml.append("<crc>" + XMLUtils.escape(Conversion.dateToString(getCrc())) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(getRecordStatus()) + "</record_status>");

        return xml.toString();
    }

    /*=================================================================================================
      Error stuff
      ================================================================================================*/

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
    }

    /**
     * Validate the contents of the Envelope
     */
    public void validateAll() {
    }

    /**
     * Indicate whether there are any errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public String getErrorsTable() {
        return errors.getAllErrorsDefaultPresentation();
    }

    public String getFlagError(String fieldID, String label) {
        return errors.flagError(fieldID, label);
    }

    /*
        Validation Routines
     */
//     public void validateWorkflowID() {
//         if (getWorkflowID() == null || getWorkflowID().equals("")) {
//             errorTable.put("workflowID", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
//         }
//     }

    /**
     * Name must be non-null
     */
    public void validateName() {
        if (getName() == null || getName().equals("")) {
            errors.put("name", PropertyProvider.get("prm.workflow.create.name.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateDescription() {
        if (getDescription() != null && getDescription().length() > 500) {
            errors.put("description", PropertyProvider.get("prm.workflow.create.description.label"), PropertyProvider.get("prm.workflow.description.toomanychars.validation.message"));
        }
    }

    public void validateNotes() {
        if (getNotes() != null && getNotes().length() > 4000) {
            errors.put("notes", "Workflow Notes", PropertyProvider.get("prm.workflow.notes.toomanychars.validation.message"));
        }
    }

    public void validateOwnerID() {
        if (getOwnerID() == null || getOwnerID().equals("")) {
            errors.put("owner_id", PropertyProvider.get("prm.workflow.create.owner.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }

    }

    public void validateStrictnessID() {
        if (getStrictnessID() == null || getStrictnessID().equals("")) {
            errors.put("strictness_id", PropertyProvider.get("prm.workflow.create.rule.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateObjectTypes() {
        /* No validation */
    }

    /**
     * Generates an error if the workflow is published
     */
    public void validateUnpublished() {
        if (isPublished()) {
            errors.put("is_published", null, PropertyProvider.get("prm.workflow.ispublished.change.validation.message"));
        }
    }

    /**
     * Validates the steps for this workflow.  A workflow requires at least
     * one step.  One step must be flagged as "Initial"<br>
     */
    public void validateSteps() {
        Iterator it = null;
        boolean hasInitialStep = false;
        StepList steps = null;

        /* Get the steps for this workflow */
        try {
            steps = getSteps();
        } catch (PersistenceException pe) {
            errors.put("steps", null, PropertyProvider.get("prm.workflow.steps.validation.message"));
            return;
        }

        /* Make sure we have at least one step */
        if (steps == null || steps.size() == 0) {
            errors.put("steps", null, PropertyProvider.get("prm.workflow.valildatesteps.hasstep.error.message"));
            return;
        }

        /* Make sure one step is an Initial Step */
        it = steps.iterator();
        while (it.hasNext()) {
            if (((Step)it.next()).isInitialStep()) {
                hasInitialStep = true;
                break;
            }
        }
        if (!hasInitialStep) {
            errors.put("steps", null, PropertyProvider.get("prm.workflow.valildatesteps.hasinitialstep.error.message"));
            return;
        }

    }

    /*
        End of validation routines
     */

    /*=================================================================================================
      End of Error stuff
      ================================================================================================*/

}
