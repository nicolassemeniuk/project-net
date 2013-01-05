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
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * A RuleType
  * There is a finite set of RuleType objects defined here as constants.
  * Consequently the constructor for this method is private.
  */
public class RuleType 
        implements Serializable, IJDBCPersistence, IXMLPersistence, ErrorCodes {
    
    /** An authorization rule */
    public static final RuleType AUTHORIZATION_RULE_TYPE = new RuleType("100");

    /* Persistent Properties */
    String ruleTypeID = null;
    String name = null;
    String description = null;
    String notes = null;
    String tableName = null;
    // Database modifiable only
    String createdBy = null;
    java.util.Date createdDatetime = null;
    String modifiedBy = null;
    java.util.Date modifiedDatetime = null;
    java.util.Date crc = null;
    String recordStatus = null;
    /* End of persistent properties */

    boolean isLoaded = false;
    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = null;

    /**
      * Return a RuleType object based on the supplied ruleTypeID
      * or null if there is no rule for the specified ruleTypeID
      * @param ruleTypeID the rule type ID
      * @return the RuleType object
      * @throws RuleException if the ruleTypeID is unknown
      */
    public static RuleType forID(String ruleTypeID) throws RuleException {
        RuleType ruleType = null;
        if (RuleType.AUTHORIZATION_RULE_TYPE.equals(new RuleType(ruleTypeID))) {
            ruleType = RuleType.AUTHORIZATION_RULE_TYPE;
        } else {
            throw new RuleException("Unknown rule with id: " + ruleTypeID);
        }
        return ruleType;
    }

    /**
      * Return the class name for the specified rule type
      * @param ruleType the rule type to get the class for
      * @return the rule class name (with no package included)
      * e.g. "AuthorizationRule"
      * @throws RuleException if the ruleType is unknown
      */
    public static String getRuleClassName(RuleType ruleType) throws RuleException {
        String ruleClassName = null;
        if (ruleType.equals(RuleType.AUTHORIZATION_RULE_TYPE)) {
            ruleClassName = "AuthorizationRule";
        } else {
            throw new RuleException("Unknown rule type: " + ruleType.getName());
        }
        return ruleClassName;
    }

    /**
      * Creates a new RuleType
      * This is privately used to create each individual RuleType
      * The RuleType is NOT automatically loaded.
      */
    private RuleType(String ruleTypeID) {
        this.db = new DBBean();
        this.ruleTypeID = ruleTypeID;
    }

    /**
      * Indicates whether a rule type is equal to this one
      * @param obj the reference object (which should be of type RuleType)
      * @return true if this rule type is the same as the obj argument;
      * false otherwise
      */
    public boolean equals(Object obj) {
        // If obj is a rule AND that rule's typeID equals this rule's typeID
        // then they are equal (i.e. represent the same record in the database)
        if (this == obj) {
            return true;
        }
        if (obj instanceof RuleType &&
            ((RuleType)obj).getID().equals(this.getID())) {
            return true;
        }
        return false;
    }
    
    /**
      * Return the rule type id
      * @return the rule type id
      */
    public String getID() {
        return this.ruleTypeID;
    }

    
    /**
      * Getter for property name.
      * @return Value of property name.
      */
    public String getName() {
        return this.name;
    }
    
    /**
      * Setter for property name.
      * @param name New value of property name.
      */
    public void setName(String name) {
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
        this.description = description;
    }

    /**
      * Getter for property notes.
      * @return Value of property notes.
      */
    public String getNotes() {
        return this.notes;
    }
    
    /**
      * Setter for property notes.
      * @param notes New value of property notes.
      */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
      * Getter for property table name.
      * @return Value of property table name.
      */
    public String getTableName() {
        return this.tableName;
    }
    
    /**
      * Setter for property table name.
      * @param name New value of property table name.
      */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
      * Clear all properties and set isLoaded to false
      */
    public void clear() {
        ruleTypeID = null;
        name = null;
        description = null;
        tableName = null;
        
        createdBy = null;
        createdDatetime = null;
        modifiedBy = null;
        modifiedDatetime = null;
        crc = null;
        recordStatus = null;
        
        isLoaded = false;
    }
    
    /**
      * Set the database ID of the object
      * @param id the id
      */
    public void setID(String id) {
        this.ruleTypeID = id;
    }

    /**
      * Load the rule type
      * @throws net.project.persistence.PersistenceException if a problem occurs
      */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select rule_type_id, table_name, rule_type_name, ");
        queryBuff.append("rule_type_description, notes, created_by_id, created_datetime, ");
        queryBuff.append("modified_by_id, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_rule_type ");
        queryBuff.append("WHERE rule_type_id = " + this.ruleTypeID + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                this.ruleTypeID = db.result.getString("rule_type_id");
                this.name = db.result.getString("rule_type_name");
                this.description = db.result.getString("rule_type_description");
                this.notes = PropertyProvider.get(db.result.getString("notes"));
                this.createdBy = db.result.getString("created_by_id");
                this.createdDatetime = (java.util.Date)db.result.getTimestamp("created_datetime");
                this.modifiedBy = db.result.getString("modified_by_id");
                this.modifiedDatetime = (java.util.Date)db.result.getTimestamp("modified_datetime");
                this.crc = (java.util.Date)db.result.getTimestamp("crc");
                this.recordStatus = db.result.getString("record_status");
                this.isLoaded = true;
            } else {
                // Record not found
                LoadException le = new LoadException("RuleType.load(): Record not found for id " +
                                                       this.ruleTypeID);
                le.setErrorCode(RULE_TYPE_LOAD_ERROR);
                throw le;
            }

        } catch (SQLException sqle)  {
            this.isLoaded = false;
            Logger.getLogger(RuleType.class).error("RuleType.load() threw an SQL exception: " + sqle);
            throw new PersistenceException ("RuleType load operation failed.", sqle);

        } finally {
           db.release();
       }
    } //load()

    /**
      * Store not supported
      * @throws PersistenceException if it is called
      */
    public void store() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("RuleType.store(): Store capability not permitted");
    }

    /**
      * Remove not supported
      * @throws PersistenceException if it is called
      */
    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("RuleType.remove(): Remove capability not permitted");
    }
    
    /**
         Converts the object to XML representation
         This method returns the object as XML text.
         @return XML representation of the object
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
        Converts the object to XML node representation without the xml header tag.
        This method returns the object as XML text.
         @return XML node representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<rule_type>\n");
        xml.append(getXMLElements());
        xml.append("</rule_type>\n");
        return xml.toString();
    }

    /**
      * Return XML elements without rule_type tag
      * @return xml string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<rule_type_id>" + XMLUtils.escape(this.ruleTypeID) + "</rule_type_id>");
        xml.append("<name>" + XMLUtils.escape(this.name) + "</name>");
        xml.append("<description>" + XMLUtils.escape(this.description) + "</description>");
        xml.append("<notes>" + XMLUtils.escape(this.notes) + "</notes>");
        xml.append("<table_name>" + XMLUtils.escape(this.tableName) + "</table_name>");
        xml.append("<created_by_id>" + XMLUtils.escape(this.createdBy) + "</created_by_id>\n");
        xml.append("<created_datetime>" + XMLUtils.escape(Conversion.dateToString(this.createdDatetime)) + "</created_datetime>\n");
        xml.append("<modified_by_id>" + XMLUtils.escape(this.modifiedBy) + "</modified_by_id>\n");
        xml.append("<modified_datetime>" + XMLUtils.escape(Conversion.dateToString(this.modifiedDatetime)) + "</modified_datetime>\n");
        xml.append("<crc>" + XMLUtils.escape(Conversion.dateToString(this.crc)) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(this.recordStatus) + "</record_status>");
        return xml.toString();
    }
}
