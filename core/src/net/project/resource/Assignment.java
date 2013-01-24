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

 package net.project.resource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.TimeQuantity;
import net.project.xml.XMLUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Represents an assignment for a resource.
 * 
 * @author Carlos Montemuiño 2008-May
 * @author AdamKlatzkin 03/00
 * @author Tim Morrow
 * @since Version 1
 */
public abstract class Assignment implements IXMLPersistence, Cloneable, Serializable {
    
    /** Logging category. */
    private static final Logger LOGGER = Logger.getLogger(Assignment.class);
    
    // These values correspond to status constants in the pn_global_domain
    // table for pn_assignment.
    public static final String ASSIGNED = "10";
    public static final String ACCEPTED = "20";
    public static final String INPROCESS = "30";
    public static final String COMPLETED_PENDING = "40";
    public static final String COMPLETED_CONFIRMED = "50";
    public static final String DELEGATED = "60";
    public static final String REJECTED = "70";
    public static final String RETURNED = "80";

    /**
     * All valid assignment statuses.
     *
     * @deprecated as of Version 7.6.3.  Please use
     * {@link AssignmentStatus#getValidStatuses()} instead.
     */
    public static final String[] VALID_STATUSES = {ASSIGNED, ACCEPTED, INPROCESS, COMPLETED_PENDING, COMPLETED_CONFIRMED,DELEGATED, REJECTED, RETURNED};

    /**
     * Assignment statuses implying that the assignment is still of interest to a person.
     * This includes Assigned, Accepted and In-process assignments.
     *
     * @deprecated as of Version 7.6.3.  Please use
     * {@link AssignmentStatus#getPersonalAssignmentStatuses} instead.
     */
    public static final String[] PERSONAL_ASSIGNMENTS_STATUSES = {ASSIGNED, ACCEPTED, INPROCESS};

    //
    // Instance methods
    //

    /**
     * The ID of the person who is assigned.
     */
    private String personId = null;
    
    /**
     * The ID of the person who assigned it.
     */
    private String assignorId = null;

    /**
     * The space to which this assignment belongs for the purposes of grouping
     * assignments by space for display purposes.
     * This is typically the ID of the space to which the assignment object
     * belongs (in the case of an object assignment) or a user's personal space
     * ID (in the case of a space invitation assignment).
     */
    private String spaceID = null;

    /**
     * Space that this assignment appears in.
     */
    private String spaceName = null;

    /**
     * The display name of the person who is assigned.
     */
    private String personName = null;
    
    /**
     * The display name of the person who assigned is.
     */
    private String assignorName = null;

    /**
     * The ID of the object to which a person is assigned.
     * This is typically the ID of an object in the case of an object assignment
     * or the ID of the space to which a user is invited in the case of a space
     * invitation assignment.
     */
    private String objectID = null;

    /**
     * The display name of the object to which a person is assigned.
     * @see #setObjectName
     */
    private String objectName = null;

    /**
     * The object type of the object to which a person is assigned.
     */
    private String objectType = null;

    /**
     * The percentage of a person's day that is intended to be spent on this
     * assignment.
     */
    private BigDecimal percentAssignedDecimal;

    /**
     * The display string or token of a role.  The role is used only for
     * display purposes.
     */
    private String role = null;

    /**
     * The assignment status ID.
     */
    private AssignmentStatus status = AssignmentStatus.ACCEPTED;

    /**
     * Indicates whether the person is the primary owner of the object to
     * which they are assigned.
     */
    private String primaryOwner = "0";

    /**
     * Person who last modified this assignment
     */
    private String modifiedBy;
    
    /**
     * Date when this was last modified
     */
    private Date modifiedDate;

    /**
     * The date this assignment was added.
     */
    /* TODO: Although this attribute is settled in the "populate" method, the attribute is not used anymore.
     * As the attribute is private, it make no sense to have it, because anyone class could read it.
     */ 
    private Date dateCreated;

    /**
     * Indicates whether this assignment is persisted in the database.
     */
    private boolean isPersisted = false;

    /**
     * Creates an empty Assignment
     */
    public Assignment() {
        // Nothing to initialize
    }


    /**
     * Sets the id of the person the assignment is owned by.
     *
     * @param id the person id
     */
    public void setPersonID(String id) {
        personId = id;
    }


    /**
     * Gets the id of the person the assignment is owned by.
     *
     * @return  the person id
     */
    public String getPersonID() {
        return personId;
    }
    

    /**
     * Sets the id of the person the assignment is owned by.
     *
     * @param id the person id
     */
    public void setAssignorID(String id) {
        assignorId = id;
    }


    /**
     * Gets the id of the person the assignment is owned by.
     *
     * @return  the person id
     */
    public String getAssignorID() {
        return assignorId;
    }


    /**
     * Set the id of the space of the assignment.
     *
     * @param id the space id
     */
    public void setSpaceID(String id) {
        spaceID = id;
    }

    public String getSpaceID() {
        return spaceID;
    }

    /**
     * Get the name of the space that this assignment appears in.
     *
     * @return a <code>String</code> containing the name of the space that this
     * assignment appears in.
     */
    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    /**
     * Sets the name of the person the assignment is owned by.
     * Note: This method has no effect on object persistence.
     *       setting the person name overrides the person name of the instantiated
     *       assignment object.  Stores and loads will rely on the value set by
     *       setPersonID.
     * @param name the person name
     */
    public void setPersonName(String name) {
        personName = name;
    }


    /**
     * Gets the name of the person the assignment is owned by.
     * @return the person name
     */
    public String getPersonName() {
        return personName;
    }


    /**
     * @return the assignorName
     */
    public String getAssignorName() {
        return assignorName;
    }


    /**
     * @param assignorName the assignorName to set
     */
    public void setAssignorName(String assignorName) {
        this.assignorName = assignorName;
    }


    /**
     * Set the name of the object as pulled from the db.
     * @param name the object name
     */
    public void setObjectName(String name) {
        objectName = name;
    }

    /**
     * Get the name of the object this assignment belongs to.
     * @return a <code>String</code> containing the name of the object this
     * assignment belongs to.
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Set the type of the object as pulled from the db.
     *
     * @param objectType a <code>String</code> which corresponds to one of the
     * types listed in {@link net.project.base.ObjectType}.
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * This is the type of object that the person is assignment to.  For a list
     * of object types, see {@link net.project.base.ObjectType}.
     *
     * @return a <code>String</code> which corresponds to one of the types
     * listed in {@link net.project.base.ObjectType}.
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Set the id of the target object of the assignment.
     *
     * @param id     the object id
     */
    public void setObjectID(String id) {
        objectID = id;
    }

    /**
     * get the id of the target object of the assignment
     *
     * @return  String     the object id
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * set the percentage of time the assigneed person is allocated to this
     * assignment.
     * @param percent percent of a person's day that is allocated to this task;
     * this value is a whole number, for example 100 means 100%.
     */
    public void setPercentAssigned(int percent) {
        if (percent >= 0) {
            percentAssignedDecimal = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100), 5, BigDecimal.ROUND_HALF_UP);
        } else {
            percentAssignedDecimal = BigDecimal.valueOf(0);
        }
    }

    /**
     * set the percentage of time the assigneed person is allocated to this
     * assignment
     *
     * @param sPercent a <code>String</code> value containing the percent of a
     * person's day that is allocated to a given task.
     */
    public void setPercentAssigned(String sPercent) {
        if (sPercent == null)
            return;
        setPercentAssignedDecimal(BigDecimal.valueOf(Double.parseDouble(sPercent)));
    }

    /**
     * Specifies the percentage assigned as a decimal value.
     * <p>
     * The value is multiplied by 100 then rounded up to the nearest whole number.
     * </p>
     * @param percentAssignedDecimal the percent assigned where <code>1</code> means 100%.
     */
    public void setPercentAssignedDecimal(BigDecimal percentAssignedDecimal) {
        this.percentAssignedDecimal = percentAssignedDecimal;
    }

    /**
     * Returns the percentage of a day this assignment's resource
     * spends working.
     * @return assignment percentage where <code>100</code> = 100%, <code>50</code> = 50%;
     * the value has a scale of <code>0</code>
     */
    public BigDecimal getPercentAssigned() {
        return getPercentAssignedDecimal();
    }

    /**
     * Returns the percentage of a day this assignment's resource
     * spends working.
     * @return assignment percentage where <code>100</code> = 100%, <code>50</code> = 50%
     */
    public int getPercentAssignedInt() {
        return percentAssignedDecimal != null ? (percentAssignedDecimal.multiply(BigDecimal.valueOf(100))).intValue() : -1;
    }

    /**
     * Returns the percentage of a day this assignment's resource
     * spends working as a decimal value.
     * @return assignment percentage where <code>1.00</code> = 100% assigned, <code>0.50</code> = 50%;
     * the value has a scale of <code>2</code>
     */
    public BigDecimal getPercentAssignedDecimal() {
    	return percentAssignedDecimal;
    }

    /**
     * set the role of the assigned person.
     * This is an arbitrary string value used for display purposes only.
     * @param role the role
     */
    public void setPersonRole(String role) {
        this.role = role;
    }

    /**
     * get the role of the assigned person
     *
     * @return String    the role
     */
    public String getPersonRole() {
        return PropertyProvider.get(role);
    }

    /**
     * Set the id of the assignment's status.
     *
     * @param id a <code>String</code> which corresponds to one of the ID's for
     * the Assignment statuses listed in the AssignmentStatus object.
     */
    public void setStatusID(String id) {
        status = AssignmentStatus.getForID(id);
    }

    /**
     * get the id of the assignment's status
     *
     * @return String     the status id from pm_global_domain
     */
    public String getStatusID() {
        return status.getID();
    }

    /**
     * Get the status of this assignment.
     *
     * @return a <code>AssignmentStatus</code> object which indicates the status
     * of the assignment.
     */
    public AssignmentStatus getStatus() {
        return status;
    }

    /**
     * Set the status of this assignment.
     *
     * @param status a <code>AssignmentStatus</code> object which indicates the
     * status of the assignment.
     */
    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    /**
     * Set the primary owner flag for this assignment.
     * An object can only have one assignment flaged as primary owner.  If there is already
     * an existing primary owner, setting a new one will cause the flag to be set to false
     * on the existing primary owner in the database.  (This only happens when store() is
     * invoked)
     *
     * @param bool   should be "1" -- primary owner
     *                      or "0" -- not primary owner (default)
     */
    public void setPrimaryOwnerString(String bool) {
        primaryOwner = bool;
    }

    /**
     * Set the primary owner flag for this assignment.
     * An object can only have one assignment flaged as primary owner.  If there is already
     * an existing primary owner, setting a new one will cause the flag to be set to false
     * on the existing primary owner in the database.  (This only happens when store() is
     * invoked)
     *
     * @param primaryOwner a <code>boolean</code> indicating whether this
     * assignment is the primary owner.
     */
    public void setPrimaryOwner(boolean primaryOwner) {
        this.primaryOwner = (primaryOwner ? "1" : "0");
    }

    /**
     * get the primary owner flag for this assignment.
     *
     * @return boolean    true - primary owner
     *                   false - not primary owner
     */
    public boolean isPrimaryOwner() {
        if ((primaryOwner != null) && (primaryOwner.equals("1"))){
            return true;
        }
        return false;
    }

    
    
    /**
	 * @return Returns the modifiedBy.
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}


	/**
	 * @param modifiedBy The modifiedBy to set.
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}


	/**
	 * @return Returns the modifiedDate.
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}


	/**
	 * @param modifiedDate The modifiedDate to set.
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	/**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<assignment>\n");
        xml.append(getXMLElements());
        xml.append("</assignment>\n");
        return xml.toString();
    }

    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Returns the elements contained within an <code>&lt;Assignment&gt;</code>.
     * @return the XML elements
     */
    protected String getXMLElements() {

        StringBuffer xml = new StringBuffer();

        xml.append("<object_name>" + XMLUtils.escape(objectName) + "</object_name>\n");
        xml.append("<object_type_pretty>" + XMLUtils.escape(getPrettyAssignmentType()) + "</object_type_pretty>\n");
        xml.append("<object_type>" + XMLUtils.escape(objectType) + "</object_type>\n");
        xml.append("<can_capture_work>" + XMLUtils.format(AssignmentType.forObjectType(objectType).canCaptureWork() && !getStatus().equals(AssignmentStatus.REJECTED)) + "</can_capture_work>\n");
        xml.append("<person_id>" + XMLUtils.escape(personId) + "</person_id>\n");
        xml.append("<space_id>" + XMLUtils.escape(spaceID) + "</space_id>\n");
        xml.append("<space_name>" + XMLUtils.escape(spaceName) + "</space_name>\n");
        xml.append("<person_name>" + XMLUtils.escape(personName) + "</person_name>\n");
        xml.append("<assignor_id>" + XMLUtils.escape(assignorId) + "</assignor_id>\n");
        xml.append("<assignor_name>" + XMLUtils.escape(assignorName) + "</assignor_name>\n");
        xml.append("<object_id>" + XMLUtils.escape(objectID) + "</object_id>\n");
        xml.append("<percent_assigned>" + XMLUtils.escape(String.valueOf(percentAssignedDecimal.multiply(BigDecimal.valueOf(100)))) + "</percent_assigned>\n");
        xml.append("<role>" + XMLUtils.escape(getPersonRole()) + "</role>\n");
        xml.append("<status_id>" + XMLUtils.escape(status.getID()) + "</status_id>\n");
        xml.append("<status>" + XMLUtils.escape(status.getDisplayName()) + "</status>\n");
        xml.append("<primary_owner>" + XMLUtils.escape(primaryOwner) + "</primary_owner>\n");
        xml.append("<modified_by>" + XMLUtils.escape(modifiedBy) + "</modified_by>\n");
        xml.append("<modified_date>" + DateFormat.getInstance().formatDateMedium(modifiedDate) + "</modified_date>\n");

        return xml.toString();
    }

    /***************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************/

    /**
     * Stores the assignment with a null start date, end date and work.
     * Creates an assignment, or updates an assignment if one already exists
     * for the current id for the same space id, person id and object id.
     * @throws PersistenceException if there is a problem storing the assignment
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            store(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(Assignment.class).error("Assignment.java: Error storing assignment: " + sqle);
            throw new PersistenceException("Error storing assignment", sqle);
        } finally {
            db.release();
        }
    }

    public void store(DBBean db) throws SQLException {
        storeAssignment(db, null, null, null, null, null, null, null, null);
    }

    protected void storeAssignment(DBBean db, Date startDate, Date endDate, Date actualStart, Date actualFinish, Date estimatedFinish, TimeQuantity work, TimeQuantity workComplete, BigDecimal percentComplete) throws SQLException {

        try {
            int index = 0;

            db.prepareCall("{call SCHEDULE.STORE_ASSIGNMENT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            DatabaseUtils.setInteger(db.cstmt, ++index, spaceID);
            DatabaseUtils.setInteger(db.cstmt, ++index, personId);
            DatabaseUtils.setInteger(db.cstmt, ++index, objectID);
            if (percentAssignedDecimal == null) {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setBigDecimal(++index, percentAssignedDecimal.multiply(BigDecimal.valueOf(100)));
            }
            db.cstmt.setString(++index, role);
            DatabaseUtils.setInteger(db.cstmt, ++index, primaryOwner);
            if (startDate == null) {
                db.cstmt.setNull(++index, java.sql.Types.TIMESTAMP);
            } else {
                db.cstmt.setTimestamp(++index, new java.sql.Timestamp(startDate.getTime()));
            }
            if (endDate == null) {
                db.cstmt.setNull(++index, java.sql.Types.TIMESTAMP);
            } else {
                db.cstmt.setTimestamp(++index, new java.sql.Timestamp(endDate.getTime()));
            }

            DatabaseUtils.setTimestamp(db.cstmt,  ++index, actualStart);
            DatabaseUtils.setTimestamp(db.cstmt,  ++index, actualFinish);
            DatabaseUtils.setTimestamp(db.cstmt,  ++index, estimatedFinish);

            
            if (work == null) {
            	db.cstmt.setNull(++index, java.sql.Types.DOUBLE);
            	db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setDouble(++index, work.getAmount().doubleValue());  
                db.cstmt.setInt(++index, work.getUnits().getUniqueID());
            }
            if (workComplete == null){
            	db.cstmt.setNull(++index, java.sql.Types.DOUBLE);
            	db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setDouble(++index, workComplete.getAmount().doubleValue());
                db.cstmt.setInt(++index, workComplete.getUnits().getUniqueID());            	
            }

            //DatabaseUtils.setTimeQuantity(db.cstmt, work, ++index, ++index);
            //DatabaseUtils.setTimeQuantity(db.cstmt, workComplete, ++index, ++index);

            if (work != null && workComplete != null) {
                db.cstmt.setBoolean(++index, work.equals(workComplete));
            } else {
                db.cstmt.setBoolean(++index, false);
            }

            if (percentComplete != null) {
                DatabaseUtils.setBigDecimal(db.cstmt, ++index, percentComplete);
            } else {
                db.cstmt.setInt(++index, 0);
            }

            DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager.getUser().getID());          
            db.cstmt.setString(++index, "A");
            DatabaseUtils.setInteger(db.cstmt, ++index, assignorId);

            db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);

            db.executeCallable();

        } catch (NumberFormatException nfe) {
            SQLException sqlException = new SQLException("ParseInt Failed in Assignment.store()");
            sqlException.initCause(nfe);
            throw sqlException;
        }
    }


    /**
     * Soft delete the assignment from the database (change the record status).
     * Requires person ID and object ID to be set.
     * @throws IllegalStateException if person ID or object ID is null
     */
    public void remove() throws PersistenceException {
        if (personId == null || objectID == null) {
            throw new IllegalStateException("Person ID and object ID is required");
        }

        List oneItemArray = new ArrayList();
        oneItemArray.add(this);
        AssignmentManager.deleteAssignments(oneItemArray);    
    }

    /**
     * Delete the assignment from the database permanently.
     * Requires person ID and object ID to be set.
     * @throws IllegalStateException if person ID or object ID is null
     */
    public void delete() throws PersistenceException {

        if (personId == null || objectID == null) {
            throw new IllegalStateException("Person ID and object ID is required");
        }

        String query = "delete from pn_assignment where person_id=" + personId + " and object_id=" + objectID;
        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

        } catch (SQLException sqle) {
        	Logger.getLogger(Assignment.class).error("Assignment.delete failed " + sqle);

        } finally {
            db.release();

        }
    }

    /**
     * Load properties from persistent storage based on the current ID.
     * 
     * @throws PersistenceException in case no one assignment is found
     * @throws NullPointerException in case the <code>objectID</code> property
     *         for this object is null or empty
     * @since 8.4
     */
    public void load() throws PersistenceException, NullPointerException {
        if (StringUtils.isEmpty(this.objectID)) {
            LOGGER.error("Assignment.load() failed to find assignment because its ID is not emtpy or null.");
            throw new NullPointerException("Assignment load operation failed; missing id");
        }
        final AssignmentFinder finder = new AssignmentFinder();
        final TextFilter objectIDFilter = new TextFilter("objectIDFilter", AssignmentFinder.OBJECT_ID_COLUMN, false);
        objectIDFilter.setSelected(true);
        objectIDFilter.setComparator((TextComparator) TextComparator.EQUALS);
        objectIDFilter.setValue(this.objectID);

        finder.addFinderFilter(objectIDFilter);

        final Collection<Assignment> assignments = finder.findAll();
        if (CollectionUtils.isEmpty(assignments)) {
            LOGGER.error("Assignment.load() failed to find assignment with id: " + this.objectID);
            throw new PersistenceException("No assignment exists with assignment id " + this.objectID);
        }
        Assignment assignment = assignments.iterator().next();
        Assignment.copyAssignments(assignment, this);
        this.dateCreated = assignment.dateCreated;
        this.isPersisted = true;
        this.modifiedBy = assignment.modifiedBy;
        this.modifiedDate = assignment.modifiedDate;
        this.objectName = assignment.objectName;
        this.objectType = assignment.objectType;
        this.percentAssignedDecimal = assignment.percentAssignedDecimal;
        this.personId = assignment.personId;
        this.personName = assignment.personName;
        this.assignorId = assignment.assignorId;
        this.assignorName = assignment.assignorName;
        this.primaryOwner = assignment.primaryOwner;
        this.role = assignment.role;
        this.spaceID = assignment.spaceID;
        this.spaceName = assignment.spaceName;
        this.status = assignment.status;
    }
    


    /**
     * This method is in charge to copy all properties from one object into
     * other. It takes into account to not copy the <code>objectID</code>
     * property in case it's present at <code>target</code> object.
     * 
     * @param source the source object from where the properties will be copied
     * @param target the target object to where the properties will be copied on
     * @throws IllegalArgumentException when one of the parameters is null
     * @since 8.4
     */
    public static void copyAssignments(Assignment source, Assignment target) throws IllegalArgumentException {
        if (source == null || target == null) {
            LOGGER.error(
                            "Assignment.copyAssignments() failed to copy from one assignment into other because one of them is null.");
            throw new IllegalArgumentException(
                    "Is not possible to copy one assignment object to other because one of them is null.");
        }

        // Copy the objectID property only when it's not present at "target" object.
        if (StringUtils.isEmpty(target.objectID)) {
            target.objectID = source.objectID;
        }

        // Copy the remaining properties.
        target.dateCreated = source.dateCreated;
        target.isPersisted = true;
        target.modifiedBy = source.modifiedBy;
        target.modifiedDate = source.modifiedDate;
        target.objectName = source.objectName;
        target.objectType = source.objectType;
        target.percentAssignedDecimal = source.percentAssignedDecimal;
        target.personId = source.personId;
        target.personName = source.personName;
        target.assignorId = source.assignorId;
        target.assignorName = source.assignorName;
        target.primaryOwner = source.primaryOwner;
        target.role = source.role;
        target.spaceID = source.spaceID;
        target.spaceName = source.spaceName;
        target.status = source.status;
    }
    
    /**
     * Updates the status of the assignment.
     * @throws IllegalStateException if person ID or object ID is null
     */
    public void updateStatus(String status) throws PersistenceException {
    	updateStatus(status, null);
    }
    
    /**
     * Updates the status and percent complete of the assignment.
     * @throws IllegalStateException if person ID or object ID is null
     */
    public void updateStatus(String status, BigDecimal percentComplete) throws PersistenceException {

        if (personId == null || objectID == null) {
            throw new IllegalStateException("Person ID and object ID is required");
        }
        String query = "update pn_assignment set status_id=" + status;
        if(percentComplete != null) {	
        	query +=" , percent_complete="+ percentComplete;
        }
        query +=" where person_id=" + personId + " and object_id=" + objectID;
        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

        } catch (SQLException sqle) {
        	Logger.getLogger(Assignment.class).error("Assignment.updateStatus failed " + sqle);

        } finally {
            db.release();

        }
    }
    /**
     * Convert the assignment type to something more presentable.
     * @return the display name for the current object type; if the object
     * type is null then the empty string is returned
     */
    private String getPrettyAssignmentType() {
        String displayName = "";
        if (objectType != null) {
            AssignmentType type = AssignmentType.forObjectType(objectType);
            if (type != null) {
                displayName = type.getDisplayName();
            }
        }
        return displayName;
    }

    public AssignmentType getAssignmentType() {
        return AssignmentType.forObjectType(objectType);
    }

    /**
     * Populates this Assignment from the specified result set.
     * Calls {@link #populateAssignment}.
     * @throws SQLException if there is a problem reading the data from the
     * result set row
     */
    final void populate(ResultSet result) throws SQLException {
        setPersonID(result.getString(AssignmentFinder.PERSON_ID_COL_ID));
        setAssignorID(result.getString(AssignmentFinder.ASSIGNOR_ID_COL_ID));
        setObjectID(result.getString(AssignmentFinder.OBJECT_ID_COL_ID));
        setStatus(AssignmentStatus.getForID(result.getString(AssignmentFinder.STATUS_ID_COL_ID)));
        BigDecimal percentAllocated = result.getBigDecimal(AssignmentFinder.PERCENT_ALLOCATED_COL_ID);
        if(percentAllocated != null){
        	setPercentAssignedDecimal(percentAllocated.divide(BigDecimal.valueOf(100), 5, BigDecimal.ROUND_HALF_UP));
        } else {
        	setPercentAssignedDecimal(BigDecimal.valueOf(0));
        }
        setPersonRole(result.getString(AssignmentFinder.ROLE_COL_ID));
        setPrimaryOwnerString(result.getString(AssignmentFinder.IS_PRIMARY_OWNER_COL_ID));
        setSpaceID(result.getString(AssignmentFinder.SPACE_ID_COL_ID));
        setSpaceName(result.getString(AssignmentFinder.SPACE_NAME_COL_ID));

        setObjectType(result.getString(AssignmentFinder.OBJECT_TYPE_COL_ID));
        setObjectName(result.getString(AssignmentFinder.OBJECT_NAME_COL_ID));
        setPersonName(result.getString(AssignmentFinder.DISPLAY_NAME_COL_ID));
        setAssignorName(result.getString(AssignmentFinder.ASSIGNOR_NAME_COL_ID));

        modifiedBy = result.getString(AssignmentFinder.MODIFIED_BY_DISPLAY_NAME);
        modifiedDate = DatabaseUtils.getTimestamp(result, AssignmentFinder.MODIFIED_DATE_COL_ID);

        dateCreated = DatabaseUtils.getTimestamp(result, AssignmentFinder.DATE_CREATED_COL_ID);

        isPersisted = true;

        // Now do sub-class specific items
        populateAssignment(result);
    }

    /**
     * Populates this Assignment from the specified pnAssignment.
     */
//    public final void populate(PnAssignment pnAssignment, String assignorName, String assigneeName, String objectName, String objectType, String spaceName, String spaceType, String timeZoneId) {
//        setPersonID(String.valueOf(pnAssignment.getComp_id().getPersonId()));
//        setSpaceID(String.valueOf(pnAssignment.getComp_id().getSpaceId()));
//        setObjectID(String.valueOf(pnAssignment.getComp_id().getObjectId()));
//        setAssignorID(String.valueOf(pnAssignment.getPnAssignor().getPersonId()));
//
//        setStatus(AssignmentStatus.getForID(String.valueOf(pnAssignment.getStatusId())));
//        setPercentAssigned(pnAssignment.getPercentAllocated());
//        setPersonRole(pnAssignment.getRole());
//        setPrimaryOwnerString(pnAssignment.getIsPrimaryOwner()==1 ? "true" : "false");
//        
//        setSpaceName(spaceName);
//        setObjectType(objectType);
//        setObjectName(objectName);
//        setPersonName(assigneeName);
//        setAssignorName(assignorName);
//        
//        populateAssignment(pnAssignment, timeZoneId);
//    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;

        final Assignment assignment = (Assignment)o;

        if (objectID != null ? !objectID.equals(assignment.objectID) : assignment.objectID != null) return false;
        if (percentAssignedDecimal != null && !(percentAssignedDecimal.floatValue() == assignment.percentAssignedDecimal.floatValue())) return false;
        if (personId != null ? !personId.equals(assignment.personId) : assignment.personId != null) return false;
        if (assignorId != null ? !assignorId.equals(assignment.assignorId) : assignment.assignorId != null) return false;        
        if (primaryOwner != null ? !primaryOwner.equals(assignment.primaryOwner) : assignment.primaryOwner != null) return false;
        if (role != null ? !role.equals(assignment.role) : assignment.role != null) return false;
        if (spaceID != null ? !spaceID.equals(assignment.spaceID) : assignment.spaceID != null) return false;
        if (status != null ? !status.equals(assignment.status) : assignment.status != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (personId != null ? personId.hashCode() : 0);
        result = 29* result + (assignorId != null ? assignorId.hashCode() : 0);
        result = 29 * result + (spaceID != null ? spaceID.hashCode() : 0);
        result = 29 * result + (objectID != null ? objectID.hashCode() : 0);
        result = 29 * result + (percentAssignedDecimal != null ? percentAssignedDecimal.hashCode() : 0); 
        result = 29 * result + (role != null ? role.hashCode() : 0);
        result = 29 * result + (status != null ? status.hashCode() : 0);
        result = 29 * result + (primaryOwner != null ? primaryOwner.hashCode() : 0);
        return result;
    }

    /**
     * Provides a way for a concrete implementation of this class to populate
     * specific properties.
     * @param result the result set containing the additional data items
     * to populate
     * @throws SQLException if there is a problem reading the data from the
     * result set row
     */
    protected abstract void populateAssignment(ResultSet result) throws SQLException;
    
    /**
     * Provides a way for a concrete implementation of this class to populate
     * specific properties.
     * @param timeZoneId 
     */
//    protected abstract void populateAssignment(PnAssignment pnAssignment, String timeZoneId);

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Indicates whether this assignment has been persisted in the database
     * or whether it is a new assignment.
     * @return true if this assigned is persisted; false if it has not yet
     * been persisted
     */
    public boolean isPersisted() {
        return this.isPersisted;
    }
}
