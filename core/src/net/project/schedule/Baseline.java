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

 package net.project.schedule;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;
import net.project.xml.XMLUtils;

/**
 * Represents the state of an object at a set, named, time in the past.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class Baseline implements IXMLPersistence {
    private String id;
    private String baselinedObjectID;
    private String name;
    private String description;
    private Date creationDate;
    private Date modifiedDate;
    private String recordStatus;
    private boolean isDefaultForObject;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getBaselinedObjectID() {
        return baselinedObjectID;
    }

    public void setBaselinedObjectID(String baselinedObjectID) {
        this.baselinedObjectID = baselinedObjectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public boolean isDefaultForObject() {
        return isDefaultForObject;
    }

    public void setDefaultForObject(boolean defaultForObject) {
        isDefaultForObject = defaultForObject;
    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.STORE_BASELINE(?,?,?,?,?,?,?); end;");

            db.cstmt.setString(1, baselinedObjectID);
            db.cstmt.setString(2, name);
            db.cstmt.setString(3, description);
            db.cstmt.setString(4, SessionManager.getUser().getID());
            db.cstmt.setBoolean(5, isDefaultForObject);
            db.cstmt.setString(6, recordStatus);
            db.cstmt.registerOutParameter(7, Types.NUMERIC);

            if (id != null) {
                db.cstmt.setLong(7, Long.parseLong(id));
            }

            db.executeCallable();

            this.id = db.cstmt.getString(7);
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to store baseline", sqle);
        } finally {
            db.release();
        }
    }

    public static void baselinePlan(String planID, String baselineID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.STORE_PLAN_BASELINE(?,?); end;");
            db.cstmt.setString(1, planID);
            db.cstmt.setString(2, baselineID);
            db.executeCallable();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to baseline plan", sqle);
        } finally {
            db.release();
        }
    }

    public static void baselineTask(String taskID, String baselineID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.STORE_TASK_BASELINE(?,?); end;");
            db.cstmt.setString(1, taskID);
            db.cstmt.setString(2, baselineID);
            db.executeCallable();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to baseline task", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Get the current default baseline id for a given object ID.  (This is
     * probably a plan ID aka a schedule ID.)
     */
    public static String getDefaultBaselineID(String objectID) throws PersistenceException {
        String baselineID = null;
        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select baseline_id " +
                "from pn_baseline " +
                "where object_id = ? " +
                "  and is_default_for_object = 1"
            );
            db.pstmt.setString(1, objectID);
            db.executePrepared();

            if (db.result.next()) {
                baselineID = db.result.getString(1);
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            db.release();
        }

        return baselineID;
    }

    public static void remove(String baselineID) {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.REMOVE_BASELINE(?); end;");
            db.cstmt.setString(1, baselineID);
            db.executeCallable();
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<baseline>");
        xml.append("<id>").append(id).append("</id>");
        xml.append("<baselinedObjectID>").append(baselinedObjectID).append("</baselinedObjectID>");
        xml.append("<name>").append(HTMLUtils.escape(name)).append("</name>");
        xml.append("<description>").append(HTMLUtils.escape(description)).append("</description>");
        xml.append("<creationDate>").append(XMLUtils.formatISODateTime(creationDate)).append("</creationDate>");
        xml.append("<modifiedDate>").append(XMLUtils.formatISODateTime(modifiedDate)).append("</modifiedDate>");
        xml.append("<recordStatus>").append(recordStatus).append("</recordStatus>");
        xml.append("<isDefaultForObject>").append(XMLUtils.format(isDefaultForObject)).append("</isDefaultForObject>");        
        xml.append("</baseline>");

        return xml.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Baseline)) {
            return false;
        }

        final Baseline baseline = (Baseline) o;

        if (isDefaultForObject != baseline.isDefaultForObject) {
            return false;
        }
        if (baselinedObjectID != null ? !baselinedObjectID.equals(baseline.baselinedObjectID) : baseline.baselinedObjectID != null) {
            return false;
        }
        if (creationDate != null ? !creationDate.equals(baseline.creationDate) : baseline.creationDate != null) {
            return false;
        }
        if (description != null ? !description.equals(baseline.description) : baseline.description != null) {
            return false;
        }
        if (id != null ? !id.equals(baseline.id) : baseline.id != null) {
            return false;
        }
        if (modifiedDate != null ? !modifiedDate.equals(baseline.modifiedDate) : baseline.modifiedDate != null) {
            return false;
        }
        if (name != null ? !name.equals(baseline.name) : baseline.name != null) {
            return false;
        }
        if (recordStatus != null ? !recordStatus.equals(baseline.recordStatus) : baseline.recordStatus != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 29 * result + (baselinedObjectID != null ? baselinedObjectID.hashCode() : 0);
        result = 29 * result + (name != null ? name.hashCode() : 0);
        result = 29 * result + (description != null ? description.hashCode() : 0);
        result = 29 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 29 * result + (modifiedDate != null ? modifiedDate.hashCode() : 0);
        result = 29 * result + (recordStatus != null ? recordStatus.hashCode() : 0);
        result = 29 * result + (isDefaultForObject ? 1 : 0);
        return result;
    }
    
    /**
     * Method to get default baseline name
     *  
     * @param objectID schduleId to pass 
     * @return baseline name
     * @throws PersistenceException
     */
    public static String getDefaultBaselineName(String objectID) throws PersistenceException {
        String baselineName = null;
        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select name " +
                "from pn_baseline " +
                "where object_id = ? " +
                "  and is_default_for_object = 1"
            );
            db.pstmt.setString(1, objectID);
            db.executePrepared();

            if (db.result.next()) {
            	baselineName = db.result.getString(1);
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            db.release();
        }

        return baselineName;
    }
}
