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
|    Envelope.java
|    $RCSfile$
|   $Revision: 19738 $
|       $Date: 2009-08-13 10:39:19 -0300 (jue, 13 ago 2009) $
|     $Author: dpatil $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.Clob;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.util.StringUtils;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * An instantiation of a specific workflow.
 *
 * @author Tim
 * @since 08/2000
 */
public class Envelope implements IJDBCPersistence, IXMLPersistence, Serializable {
    public static final int XML_NORMAL = 0;
    public static final int XML_VERBOSE = 1;

    /** Name of the table which stores the envelope object properties */
    static final String OBJECT_PROPERTIES_CLOB_TABLE_NAME = "pn_envelope_object_clob";

    /** The maximum length of the envelope name. */
    private static final int ENVELOPE_NAME_MAX_LENGTH = 80;
    /** The maximum length of the envelope description. */
    private static final int ENVELOPE_DESCRIPTION_MAX_LENGTH = 500;

    private String envelopeID = null;
    private String workflowID = null;
    private Workflow workflow = null;
    private String currentVersionID = null;
    private String name = null;
    private String description = null;
    private String strictnessID = null;
    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    private java.util.Date crc = null;
    private String recordStatus = null;

    private String workflowName = null;
    private String workflowDescription = null;
    private String strictnessName = null;
    private String strictnessDescription = null;
    private String createdByFullName = null;
    private String modifiedByFullName = null;
    private boolean isActive = false;

    /*
        Information about the current envelope version / properties
        required to create new envelope
     */
    private String statusID = null;
    private String stepID = null;
    private String transitionID = null;
    private String priorityID = null;
    private String comments = null;

    /** Current envelope version */
    private EnvelopeVersion currentVersion = null;

    /** All envelope versions */
    private EnvelopeVersionList envelopeVersions = null;

    /** List of objects begin added to or belonging to this envelope */
    private EnvelopeObjectList objectList = null;

    private boolean isLoaded = false;
    private User user = null;
    private DBBean db = null;
    private String spaceID = null;

    /**
     * Creates new Envelope
     */
    public Envelope() {
        this.db = new DBBean();
    }

    /**
     * Getter for property workflowID.
     * @return Value of property workflowID.
     */
    public String getWorkflowID() {
        return this.workflowID;
    }

    /**
     * Setter for property workflowID.
     * Also sets the envelopes' strictness from the workflow's default strictness if the
     * envelope is new (i.e. is not loaded)
     * @param workflowID New value of property workflowID.
     * @see #setStrictnessID
     */
    public void setWorkflowID(String workflowID) {
        String oldWorkflowID = this.workflowID;
        this.workflowID = workflowID;

        /* Get strictness if the workflowID is not null and
           it is different from the old one */
        if (workflowID != null &&
            (oldWorkflowID == null || !oldWorkflowID.equals(workflowID))) {
            // Since we're changing the workflow, set the Strictness to the default
            this.workflow = new Workflow();
            workflow.setID(workflowID);
            try {
                workflow.load();
                setStrictnessID(workflow.getStrictnessID());
            } catch (PersistenceException pe) {
                /* Hmm.. problem loading workflow */
                setStrictnessID(null);
            }
        }
    }

    /**
     * Sets the workflowID without loading the workflow
     * This method is designed to be called by a helper / manager
     * record when populating a workflow list
     * @param workflowID the workflowID
     */
    void setWorkflowIDNoLoad(String workflowID) {
        this.workflowID = workflowID;
    }

    /**
     * Getter for property currentVersionID.
     * @return Value of property currentVersionID.
     */
    public String getCurrentVersionID() {
        return this.currentVersionID;
    }

    /**
     * Setter for property currentVersionID.
     * @param currentVersionID New value of property currentVersionID.
     */
    void setCurrentVersionID(String currentVersionID) {
        this.currentVersionID = currentVersionID;
    }

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the envelope name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        if (name != null && name.length() > ENVELOPE_NAME_MAX_LENGTH) {
            // Truncate the name to the maximum length
            // This is necessary as often envelopes are created as the result
            // of automated processes; we must ensure no database error will occur
            name = name.substring(0, ENVELOPE_NAME_MAX_LENGTH);
        }
        this.name = name;
    }

    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        if (description != null && description.length() > ENVELOPE_DESCRIPTION_MAX_LENGTH) {
            // Truncate the description to the maximum length
            // This is necessary as often envelopes are created as the result
            // of automated processes; we must ensure no database error will occur
            description = description.substring(0, ENVELOPE_DESCRIPTION_MAX_LENGTH);
        }
        this.description = description;
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

    void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public String getWorkflowName() {
        return this.workflowName;
    }

    void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowDescription() {
        return this.workflowDescription;
    }

    void setWorkflowDescription(String workflowDescription) {
        this.workflowDescription = workflowDescription;
    }

    public String getStrictnessName() {
        return this.strictnessName;
    }

    void setStrictnessName(String strictnessName) {
        this.strictnessName = strictnessName;
    }

    public String getStrictnessDescription() {
        return this.strictnessDescription;
    }

    void setStrictnessDescription(String strictnessDescription) {
        this.strictnessDescription = strictnessDescription;
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

    public boolean isActive() {
        return this.isActive;
    }

    void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Return the current version EnvelopeVersion object, loading if necessary
     * @return the current envelope version object
     * @throws LoadException if the current version could not be loaded
     */
    public EnvelopeVersion getCurrentVersion() throws LoadException {
        if (this.currentVersion == null) {
            try {
                loadCurrentVersion();
            } catch (PersistenceException pe) {
                throw new LoadException("Unable to load current versions for envelope id " + getID());
            }
        }
        return this.currentVersion;
    }

    /**
     * Set the current version record for this envelope
     * @param envelopeVersion the envelopeVersion object
     */
    void setCurrentVersion(EnvelopeVersion envelopeVersion) {
        this.currentVersion = envelopeVersion;
    }


    /**
     * Add an object to this envelope.  This also instantiates the real object
     * @param objectID the object to add
     * @throws NotWorkflowableException if the object does not implement IWorkflowable
     */
    public void addObject(String objectID) throws NotWorkflowableException {
        if (this.objectList == null) {
            this.objectList = new EnvelopeObjectList();
        }
        this.objectList.add(objectID);
    }

    /**
     * Return this list of objects belonging to this envelope
     * @return list of Envelope objects
     */
    EnvelopeObjectList getObjectList() throws PersistenceException {
        if (this.objectList == null) {
            loadObjectList();
        }
        return this.objectList;
    }

    private void setObjectList(EnvelopeObjectList objectList) {
        this.objectList = objectList;
    }

    /**
     * Return the envelope versions for this envelope, loading if necessary
     * @return list of envelope versions
     */
    EnvelopeVersionList getEnvelopeVersions() throws PersistenceException {
        if (this.envelopeVersions == null) {
            EnvelopeVersionList envelopeVersions = new EnvelopeVersionList();
            envelopeVersions.setID(getID());
            envelopeVersions.load();
            this.envelopeVersions = envelopeVersions;
        }
        return this.envelopeVersions;
    }

    private void setEnvelopeVersions(EnvelopeVersionList envelopeVersions) {
        this.envelopeVersions = envelopeVersions;
    }

    /*
       Information required to create version
     */
    public String getStatusID() {
        return this.statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStepID() {
        return this.stepID;
    }

    public void setStepID(String stepID) {
        this.stepID = stepID;
    }

    public String getTransitionID() {
        return this.transitionID;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }

    public String getPriorityID() {
        return this.priorityID;
    }

    public void setPriorityID(String priorityID) {
        this.priorityID = priorityID;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSpaceID() {
        return this.spaceID;
    }

    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return true if the envelope has been loaded from persistent store
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Return the Envelope's id
     * @return the id of the envelope
     */
    public String getID() {
        return this.envelopeID;
    }

    /**
     * Clear all envelope properties.
     */
    public void clear() {
        setID(null);
        setWorkflowID(null);
        //setWorkflow(null);
        setCurrentVersion(null);
        setName(null);
        setDescription(null);
        setStrictnessID(null);
        setCreatedBy(null);
        setCreatedDatetime(null);
        setModifiedBy(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setStatusID(null);
        setStepID(null);
        setTransitionID(null);
        setPriorityID(null);
        setComments(null);
        setWorkflowName(null);
        setWorkflowDescription(null);
        setStrictnessName(null);
        setStrictnessDescription(null);
        setCreatedByFullName(null);
        setModifiedByFullName(null);
        setActive(false);
        setCurrentVersion(null);
        setEnvelopeVersions(null);
        setObjectList(null);
        setLoaded(false);
    }

    /*
        IJDBC Persistence
     */
    public void setID(java.lang.String envelopeID) {
        this.envelopeID = envelopeID;
    }

    /*
     * Load envelope
     * Does not load the current version object - this is loaded when required
     */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select envelope_id, workflow_id, workflow_name, workflow_description, space_id, strictness_id, ");
        queryBuff.append("strictness_name, strictness_description, current_version_id, ");
        queryBuff.append("envelope_name, envelope_description, created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status, is_active, current_step_id, current_status_id, ");
        queryBuff.append("last_transition_id, current_priority_id, current_comments_clob ");
        queryBuff.append("from pn_workflow_envelope_view ");
        queryBuff.append("WHERE envelope_id = " + getID() + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());
            if (db.result.next()) {
                setID(db.result.getString("envelope_id"));
                setWorkflowID(db.result.getString("workflow_id"));
                setWorkflowName(db.result.getString("workflow_name"));
                setWorkflowDescription(db.result.getString("workflow_description"));
                setSpaceID(db.result.getString("space_id"));
                setStrictnessID(db.result.getString("strictness_id"));
                setStrictnessName(PropertyProvider.get(db.result.getString("strictness_name")));
                setStrictnessDescription(db.result.getString("strictness_description"));
                setCurrentVersionID(db.result.getString("current_version_id"));
                setName(db.result.getString("envelope_name"));
                setDescription(db.result.getString("envelope_description"));
                setCreatedBy(db.result.getString("created_by_id"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setModifiedBy(db.result.getString("modified_by_id"));
                setModifiedByFullName(db.result.getString("modified_by_full_name"));
                setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setActive(Conversion.toBoolean(db.result.getString("is_active")));
                setStepID(db.result.getString("current_step_id"));
                setStatusID(db.result.getString("current_status_id"));
                setTransitionID(db.result.getString("last_transition_id"));
                setPriorityID(db.result.getString("current_priority_id"));
                setComments(ClobHelper.read(db.result.getClob("current_comments_clob")));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setLoaded(true);
            }

        } catch (SQLException sqle) {
            this.isLoaded = false;
            Logger.getLogger(Envelope.class).error("Envelope.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope load operation failed.", sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Reloads this envelope; all properties are cleared prior to the load.
     * @throws PersistenceException if there is a problem reloading
     * @see #load()
     */
    public void reload() throws PersistenceException {
        String currentEnvelopeID = getID();
        clear();
        setID(currentEnvelopeID);
        load();
    }

    /**
     * Load the current version record for this envelope
     */
    private void loadCurrentVersion() throws PersistenceException {
        EnvelopeVersion ver = null;
        /* Now load the version information */
        ver = new EnvelopeVersion();
        ver.setID(getCurrentVersionID());
        ver.load();
        setCurrentVersion(ver);
    }

    /**
     * Load the object list for this envelope
     * Note: This does not resolve the real object for each objectID to improve
     * performance.<br />
     * Additionally, it uses its own DBBean such that it may be called during
     * another transaction in the store methods.
     * @throws PersistenceException if there is a problem loading the list
     */
    private void loadObjectList() throws PersistenceException {
        DBBean myDb = new DBBean();
        EnvelopeObject obj = null;
        EnvelopeObjectList objectList = new EnvelopeObjectList();
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select object_id, envelope_id ");
        queryBuff.append("from pn_envelope_has_object ");
        queryBuff.append("where envelope_id = " + getID() + " ");

        try {
            myDb.executeQuery(queryBuff.toString());
            while (myDb.result.next()) {
                obj = new EnvelopeObject();
                obj.setID(myDb.result.getString("object_id"));
                obj.setEnvelopeID(myDb.result.getString("envelope_id"));
                objectList.add(obj);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Envelope.class).error("Envelope.loadObjectList() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope load objects operation failed.", sqle);
        } finally {
            myDb.release();

        }
        setObjectList(objectList);

    }

    /**
     * Store the envelope
     * @exception net.project.persistence.PersistenceException
     *                   if there is a problem storing
     */
    public void store() throws net.project.persistence.PersistenceException {
        if (this.isLoaded) {
            // Modify existing one
            modifyEnvelope();
        } else {
            // Create new one
            createEnvelope();
        }
    }

    /**
     * Creates a new envelope, including initial version and adds all objecs
     * to the envelope and the version.
     * Called from store()
     * @exception net.project.persistence.PersistenceException
     *                   if there is a problem storing
     * @see #store()
     */
    private void createEnvelope() throws net.project.persistence.PersistenceException {

        String storedEnvelopeID = null;

        try {

            int index = 0;
            int commentsClobIndex = 0;
            int envelopeIDIndex = 0;

            db.setAutoCommit(false);

            //Create the envelope and version record.
            db.prepareCall("{call workflow.create_envelope(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            db.cstmt.setString(++index, getWorkflowID());
            db.cstmt.setString(++index, getStrictnessID());
            db.cstmt.setString(++index, getName());
            db.cstmt.setString(++index, getDescription());
            db.cstmt.setString(++index, this.user.getID());
            db.cstmt.setString(++index, getStatusID());
            db.cstmt.setString(++index, getPriorityID());
            db.cstmt.setInt(++index, (getComments() == null ? 1 : 0));
            db.cstmt.registerOutParameter((commentsClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((envelopeIDIndex = ++index), java.sql.Types.VARCHAR);

            db.executeCallable();

            if (getComments() != null) {
                // Stream comments to the clob locater
                ClobHelper.write(db.cstmt.getClob(commentsClobIndex), getComments());
            }

            storedEnvelopeID = db.cstmt.getString(envelopeIDIndex);

            // Successfully created the envelop and its version
            // Now add objects in object list
            storeObjectList(storedEnvelopeID);

            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("Envelope store operation failed: " + sqle, sqle);

        } catch (NotWorkflowableException e) {
            throw new PersistenceException("Envelope store operation failed: " + e, e);

        } finally {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Throw original error and release
            }
            db.release();

        }

        /* storedEnvelopeID contains id of new record */
        clear();
        setID(storedEnvelopeID);

    }

    /**
     * Modifies envelope, creates new version and updates dates etc.
     * Called from store()
     */
    private void modifyEnvelope() throws PersistenceException {

        String storedEnvelopeID = null;

        try {

            int index = 0;
            int commentsClobIndex = 0;

            db.setAutoCommit(false);

            db.prepareCall("{call workflow.modify_envelope(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            db.cstmt.setString(++index, getID());
            db.cstmt.setString(++index, getStrictnessID());
            db.cstmt.setString(++index, getName());
            db.cstmt.setString(++index, getDescription());
            db.cstmt.setString(++index, this.user.getID());
            db.cstmt.setString(++index, getStepID());
            db.cstmt.setString(++index, getTransitionID());
            db.cstmt.setString(++index, getStatusID());
            db.cstmt.setString(++index, getPriorityID());
            db.cstmt.setTimestamp(++index, new java.sql.Timestamp(getCrc().getTime()));
            db.cstmt.setInt(++index, (getComments() == null ? 1 : 0));
            db.cstmt.registerOutParameter((commentsClobIndex = ++index), java.sql.Types.CLOB);

            db.executeCallable();

            if (getComments() != null) {
                // Stream comments to the clob locater
                ClobHelper.write(db.cstmt.getClob(commentsClobIndex), getComments());
            }

            storedEnvelopeID = getID();

            // Successfully modified the envelop and its version
            // Now add objects in object list
            storeObjectList(storedEnvelopeID);

            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("Envelope store operation failed: " + sqle, sqle);

        } catch (NotWorkflowableException e) {
            throw new PersistenceException("Envelope store operation failed: " + e, e);

        } finally {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Simply release and allow any other exception to propogate
            }
            db.release();

        }

        /* storedEnvelopeID contains id of new record */
        clear();
        setID(storedEnvelopeID);
    }


    /**
     * Store this envelope's object list.  If the object list has not been
     * loaded, it will be at this point.  Additionally, each object will be
     * resolved to its real object if not already.<br />
     * As such, there may be (NumberOfObjects + 2) SQL statements issued here.
     * @param envelopeID the envelopeID to store against
     * @throws SQLException if there is a problem storing
     * @throws NotWorkflowableException if an object in the list is not workflowable
     * @throws PersistenceException if there is a problem loading the object list
     */
    private void storeObjectList(String envelopeID)
        throws java.sql.SQLException, NotWorkflowableException, PersistenceException {
        /*
        procedure add_envelope_object
          ( i_envelope_id    in varchar2,
            i_object_id       in varchar2,
            i_object_type     in varchar2,
            i_object_version_id in varchar2,
            i_object_properties_id in varchar2,
            i_created_by_id  in varchar2)
        */
        db.prepareStatement("{call workflow.add_envelope_object(?, ?, ?, ?, ?, ?)}");
        IWorkflowable workflowObject = null;
        Clob clob = null;
        db.setClobTableName(OBJECT_PROPERTIES_CLOB_TABLE_NAME);

        Iterator it = getObjectList().iterator();
        while (it.hasNext()) {
            workflowObject = ((EnvelopeObject)it.next()).getRealObject();

            /* First, create the Clob to store the properties
               We must store it now, since this is the only time we are iterating over
               all the objects
             */
            clob = db.createClob(workflowObject.getXMLBody());
            clob.store();

            db.pstmt.setString(1, envelopeID);
            db.pstmt.setString(2, workflowObject.getID());
            db.pstmt.setString(3, workflowObject.getObjectType());
            db.pstmt.setString(4, workflowObject.getVersionID());
            db.pstmt.setString(5, clob.getID());
            db.pstmt.setString(6, this.user.getID());

            db.pstmt.addBatch();
        }

        /* Execute the batch of statements */
        db.pstmt.executeBatch();
        db.closePStatement();
    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("Envelope.remove() not implemented.");
    }

    /*
        Implement IXMLPersistence
     */

    /**
     * Return the EnvelopeList XML including the XML version tag
     * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return the Envelope XML without the XML version tag
     * This XML includes the &lt;current_version /> element
     * @return XML string
     */
    public java.lang.String getXMLBody() {
        return getXMLBody(XML_NORMAL);
    }

    public String getXMLBody(int verbosity) {
        StringBuffer xml = new StringBuffer();
        xml.append("<envelope>\n");
        xml.append(getXMLElements());

        // Add in current version as its own set of properties
        xml.append("<current_version>");
        try {
            xml.append(getCurrentVersion().getXMLElements());

        } catch (LoadException le) {
            // No current version xml

        }
        xml.append("</current_version>");

        // Verbose xml includes the entire version list too
        if (verbosity == XML_VERBOSE) {

            try {
                xml.append(getEnvelopeVersions().getXMLBody());

            } catch (PersistenceException pe) {
                // No version list

            }

        }

        xml.append("</envelope>\n");
        return xml.toString();
    }

    /**
     * Return just the elements for an envelope
     * @return elements XML string
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();

        xml.append("<envelope_id>" + quote(getID()) + "</envelope_id>\n");
        xml.append("<workflow_id>" + quote(getWorkflowID()) + "</workflow_id>\n");
        xml.append("<workflow_name>" + quote(getWorkflowName()) + "</workflow_name>\n");
        xml.append("<workflow_description>" + quote(getWorkflowDescription()) + "</workflow_description>\n");
        xml.append("<strictness_id>" + quote(getStrictnessID()) + "</strictness_id>\n");
        xml.append("<strictness_name>" + quote(getStrictnessName()) + "</strictness_name>\n");
        xml.append("<strictness_description>" + quote(getStrictnessDescription()) + "</strictness_description>\n");
        xml.append("<current_version_id>" + quote(getCurrentVersionID()) + "</current_version_id>\n");
        xml.append("<name>" + quote(getName()) + "</name>\n");
        xml.append("<description>" + quote(getDescription()) + "</description>\n");
        xml.append("<created_by_id>" + quote(getCreatedBy()) + "</created_by_id>\n");
        xml.append("<created_by_full_name>" + quote(getCreatedByFullName()) + "</created_by_full_name>\n");
        xml.append("<created_datetime>" + quote(XMLUtils.formatISODateTime(getCreatedDatetime())) + "</created_datetime>\n");
        xml.append("<modified_by_id>" + quote(getModifiedBy()) + "</modified_by_id>\n");
        xml.append("<modified_by_full_name>" + quote(getModifiedByFullName()) + "</modified_by_full_name>\n");
        xml.append("<modified_datetime>" + quote(XMLUtils.formatISODateTime(getModifiedDatetime())) + "</modified_datetime>\n");
        xml.append("<is_active>" + quote((isActive() ? "1" : "0")) + "</is_active>\n");
        xml.append("<step_id>" + quote(getStepID()) + "</step_id>\n");
        xml.append("<status_id>" + quote(getStatusID()) + "</status_id>\n");
        xml.append("<transition_id>" + quote(getTransitionID()) + "</transition_id>\n");
        xml.append("<priority_id>" + quote(getPriorityID()) + "</priority_id>\n");
        xml.append("<comments>" + quote(getComments()) + "</comments>\n");
        xml.append("<space_name>" + quote(getSpaceName()) + "</space_name>\n");
        return xml.toString();
    }


    /**
     * quotes string to HTML, turns null strings into empty strings
     * @param s the string
     * @return the quotes string
     */
    private String quote(String s) {
        return XMLUtils.escape(s);
    }

    /*=================================================================================================
      Error stuff
      ================================================================================================*/

    private HashMap errorTable = new HashMap();

    /**
     * Erase the list of errors stored internally in this object.
     */
    public void clearErrors() {
        errorTable = new HashMap();
    }

    /**
     * Validate the contents of the Envelope
     */
    public void validateAll() {
        validateWorkflowID();
        validateName();
        validateDescription();
        validateStrictnessID();
    }

    /**
     * Indicate whether there are any errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return (errorTable.size() > 0 ? true : false);
    }

    /**
     * Return the Presentation for errors in the form of an HTML table ROW
     * for the specified field.  If the field has no error this method returns
     * the empty string ("") such that it may always be called and will not
     * affect the document into which it is inserted if there are no errors.
     * e.g.<br><CODE>
     * &lt;tr>&lt;td><br>
     * &lt;span class="fieldWithError" style="font-size: 10px">The error text&lt;/span>
     * &lt;/td>&lt;/tr><BR></CODE>
     * @param fieldID the field to get the errors for (e.g. "workflowID")
     * @return the HTML error string
     */
    public String getErrorsRow(String fieldID) {
        String prefix = "<tr><td>";
        String suffix = "</td></tr>";
        return getErrorsPresentation(fieldID, prefix, suffix);
    }

    /**
     * Return the presentation for errors in the form of an HTML SPAN tag
     * for the specified field.  If the field has no error this method returns
     * the empty string ("") such that it may always be called and will not
     * affect the document into which it is inserted if there are no errors.
     * e.g.<BR><code>
     * &lt;span class="fieldWithError" style="font-size: 10px">The error text&lt;/span><BR>
     * </CODE>
     * @param fieldID the field to get the errors for (e.g. "workflowID")
     * @return the HTML error string
     */
    public String getErrors(String fieldID) {
        return getErrorsPresentation(fieldID, "", "");
    }

    /**
     * Get the error text (with HTML tags included) for the specified field
     * If the field has no error it returns an empty string ("")
     * @param fieldID the field to get the error for (e.g. "workflowID")
     * @param prefix the additional HTML to prefix
     * @param suffix the additional HTML to suffix
     * @return the HTML error string
     */
    private String getErrorsPresentation(String fieldID, String prefix, String suffix) {
        String errorText = (String)errorTable.get(fieldID);
        if (errorText != null) {
            prefix = prefix + "<span class=\"fieldWithError\" style=\"font-size: 10px\">";
            suffix = "</span>" + suffix;
            errorText = prefix + errorText + suffix;
        } else {
            errorText = "";
        }
        return errorText;
    }

    /*
        Validation Routines
     */
    public void validateWorkflowID() {
        if (getWorkflowID() == null || getWorkflowID().equals("")) {
            errorTable.put("workflowID", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateName() {
        if (getName() == null || getName().equals("")) {
            errorTable.put("name", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateDescription() {
        /* No validation required */
    }

    public void validateStrictnessID() {
        if (getStrictnessID() == null || getStrictnessID().equals("")) {
            errorTable.put("strictnessID", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validatePriorityID() {
        if (getPriorityID() == null || getPriorityID().equals("")) {
            errorTable.put("priorityID", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateStatusID() {
        if (getStatusID() == null || getStatusID().equals("")) {
            errorTable.put("statusID", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }
    
    /**
     * @return Space name
     */
    public String getSpaceName() {
		if (StringUtils.isNotEmpty(spaceID)) {
			return ServiceFactory.getInstance().getPnObjectNameService().getNameFofObject(Integer.parseInt(spaceID));
		}	
		return "";
    }

    public void validateComments() {
        /* No validation required */
    }
    /*
        End of validation routines
     */

    /*=================================================================================================
      End of Error stuff
      ================================================================================================*/

}
