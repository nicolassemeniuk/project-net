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
 * Workflow strictness values.  Read only.
 * There is a well-defined set of Strictness record since these have
 * special meaning.  They are defined as public static finals in this
 * class.  The constructor is private to prevent any others from being
 * created.  Note - by default the defined Strictness-es are NOT loaded.
 */
public class Strictness implements IJDBCPersistence, IXMLPersistence, Serializable {

    /* Persistent properties of a strictness */
    private String strictnessID;
    private String name = null;
    private String description = null;

    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    /* End of persistent properties */

    private User user = null;
    private boolean isLoaded = false;
    private DBBean db = new DBBean();

    /* The defined strictness values */
    public static final Strictness RELAXED = new Strictness("100");
    public static final Strictness STRICT = new Strictness("200");

    /**
     * Create a new Strictness object.  This is private since there are a finite
     * number of strictness values and are pre-created when this class is
     * referenced.
     */
    private Strictness(String strictnessID) {
        this.strictnessID = strictnessID;
    }

    /**
     * Returns strictness object matching specified ID or null if there is
     * no matching object
     * @param strictnessID the strictness id to get the object for
     */
    static Strictness forID(String strictnessID) {
        Strictness tempStrictness = new Strictness(strictnessID);
        if (Strictness.RELAXED.equals(tempStrictness)) {
            return Strictness.RELAXED;
        } else if (Strictness.STRICT.equals(tempStrictness)) {
            return Strictness.STRICT;
        }
        return null;
    }

    /**
     * Strictness objects are equal if their IDs are the same
     */
    public boolean equals(java.lang.Object obj) {
        /* If object is a Strictness and
           its ID equals this ID, it matches */
        if (this == obj) {
            return true;
        }
        if (obj instanceof Strictness &&
            ((Strictness)obj).getID().equals(this.getID())) {
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
        return this.strictnessID;
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
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("SELECT strictness_id, strictness_name, strictness_description, created_by_id, ");
        queryBuff.append("created_datetime, modified_by_id, modified_datetime, record_status ");
        queryBuff.append("FROM pn_workflow_strictness ");
        queryBuff.append("WHERE strictness_id = " + this.strictnessID + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                this.strictnessID = db.result.getString("strictness_id");
                this.name = PropertyProvider.get(db.result.getString("strictness_name"));
                this.description = db.result.getString("strictness_description");
                this.createdBy = db.result.getString("created_by_id");
                this.createdDatetime = db.result.getTimestamp("created_datetime");
                this.modifiedBy = db.result.getString("modified_by_id");
                this.modifiedDatetime = db.result.getTimestamp("modified_datetime");

                this.isLoaded = true;
            }

        } catch (SQLException sqle) {
            this.isLoaded = false;
            Logger.getLogger(Strictness.class).error("Strictness.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Strictness load operation failed.", sqle);

        } finally {
            db.release();
        }

    } // load()

    /**
     * This method does nothing since it is not possible to modify
     * Strictness records.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void store() throws net.project.persistence.PersistenceException {
        // DO NOTHING.
    }

    /**
     * This method does nothing since it is not possible to delete
     * Strictness records.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws net.project.persistence.PersistenceException {
        // DO NOTHING
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
        return net.project.persistence.IXMLPersistence.XML_VERSION +
            "\n" + this.getXMLBody();
    }

    /**
     * Return the Strictness as an XML string
     * @return the XML string
     */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<strictness>\n");
        xml.append("<strictness_id>" + XMLUtils.escape(this.strictnessID) + "</strictness_id>\n");
        xml.append("<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(this.description) + "</description>\n");
        xml.append("</strictness>\n");

        return xml.toString();
    }

}
