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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Workflow Rule Status.  Read only.
 */
public class RuleStatus implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    /* Persistent properties */
    private String ruleStatusID;
    private String name = null;
    private String description = null;

    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    /* End of persistent properties */

    private User user = null;
    private boolean isLoaded = false;

    /* The defined rule status values */
    public static final RuleStatus ENFORCED = new RuleStatus("100");
    public static final RuleStatus DISABLED = new RuleStatus("200");

    /**
     * Create a new RuleStatus object.  This is private since there are a finite
     * number of these and are pre-created when this class is
     * referenced.
     */
    private RuleStatus(String ruleStatusID) {
        this.ruleStatusID = ruleStatusID;
    }

    /**
     * Returns RuleStatus object with ID matching the specified ID or null
     * if there is no RuleStatus for that ID
     * @param ruleStatusID of rule status to return
     */
    static RuleStatus forID(String ruleStatusID) {
        RuleStatus tempRuleStatus = new RuleStatus(ruleStatusID);
        if (RuleStatus.ENFORCED.equals(tempRuleStatus)) {
            return RuleStatus.ENFORCED;
        } else if (RuleStatus.DISABLED.equals(tempRuleStatus)) {
            return RuleStatus.DISABLED;
        }
        return null;
    }

    /**
     * RuleStatus objects are equal if their IDs are the same
     */
    public boolean equals(java.lang.Object obj) {
        /* If object is a RuleStatus and
           its ID equals this ID, it matches */
        if (this == obj) {
            return true;
        }
        if (obj instanceof RuleStatus &&
            ((RuleStatus)obj).getID().equals(this.getID())) {
            return true;
        }
        return false;
    }

    /**
     * Return the name
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the description
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the current user.
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return True if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Return the strictness ID
     * @return this strictness ID
     */
    public String getID() {
        return this.ruleStatusID;
    }


    /**********************************************************************************
     *  Implementing IJDBCPersistance
     **********************************************************************************/
    public void setID(java.lang.String id) {
        // DO NOTHING
        // The ID will alreay have been set for this strictness record
    }

    /**
     * Load the Strictness record from the underlying JDBC object.
     *
     * @throws PersistenceException Thrown to indicate a failure loading from
     * the database, a system-level error.
     */
    public void load() throws net.project.persistence.PersistenceException {
        DBBean db = new DBBean();

        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("SELECT rule_status_id, status_name, status_description, created_by_id, ");
        queryBuff.append("created_datetime, modified_by_id, modified_datetime, crc, record_status ");
        queryBuff.append("FROM pn_workflow_rule_status ");
        queryBuff.append("WHERE rule_status_id = " + this.ruleStatusID + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                this.ruleStatusID = db.result.getString("rule_status_id");
                this.name = PropertyProvider.get(db.result.getString("status_name"));
                this.description = db.result.getString("status_description");
                this.createdBy = db.result.getString("created_by_id");
                this.createdDatetime = db.result.getTimestamp("created_datetime");
                this.modifiedBy = db.result.getString("modified_by_id");
                this.modifiedDatetime = db.result.getTimestamp("modified_datetime");

                this.isLoaded = true;
            } else {
                // Record not found
                LoadException le = new LoadException("RuleStatus.load(): Record not found for id " +
                    this.ruleStatusID);
                le.setErrorCode(RULE_STATUS_LOAD_ERROR);
                throw le;
            }

        } catch (SQLException sqle) {
            this.isLoaded = false;
            Logger.getLogger(RuleStatus.class).error("RuleStatus.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Rule Status load operation failed.", sqle);

        } finally {
            db.release();
        }

    } // load()

    /**
     * @throws PersistenceException Thrown to indicate a failure storing to the database, a system-level error.
     */
    public void store() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("RuleStatus.store(): Store functionality not permitted");
    }

    /**
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("RuleStatus.remove(): Remove functionality not permitted");
    }

    /*
        Implement IXMLPersistence
     */

    /**
     * Return the Strictness as an XML string including
     * the XML version tag.
     * @return the XML string
     */
    public java.lang.String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return the Strictness as an XML string
     * @return the XML string
     */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<rule_status>\n");
        xml.append("<rule_status_id>" + XMLUtils.escape(this.ruleStatusID) + "</rule_status_id>\n");
        xml.append("<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(this.description) + "</description>\n");
        xml.append("</rule_status>\n");

        return xml.toString();
    }

}

