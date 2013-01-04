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
|   $Revision: 20493 $
|       $Date: 2010-03-01 12:22:30 -0300 (lun, 01 mar 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.document.Document;
import net.project.link.ILinkableObject;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;


/**
 A type of object such as document, form, milestone, task, etc.
 */
public class ObjectType implements java.io.Serializable {

    //
    // Static Members
    //

    // All objects.  For searching and other engines that need to operate on ALL supported objects.
    public static final String ALL = "all";

    // Calendar Module
    public static final String CALENDAR = "calendar";
    public static final String MEETING = "meeting";
    public static final String EVENT = "event";

    // Schedule Module
    public static final String SCHEDULE = "schedule";
    public static final String PLAN = "plan";
    public static final String TASK = "task";
    public static final String MILESTONE = "milestone";

    // Process Module
    public static final String PHASE = "phase";
    public static final String GATE = "gate";
    public static final String DELIVERABLE = "deliverable";
    public static final String PROCESS = "process";

    // Discussion Module
    public static final String DISCUSSION = "discussion";
    public static final String DISCUSSION_GROUP = "discussion_group";
    public static final String POST = "post";

    // Document Module
    public static final String DOCUMENT = "document";
    public static final String CONTAINER = "doc_container";
    public static final String DOCUMENT_SPACE = "doc_space";
    public static final String BOOKMARK = "bookmark";

    // Form Module
    public static final String FORM = "form";
    public static final String FORM_LIST = "form_list";
    public static final String FORM_FIELD = "form_field";
    public static final String FORM_DATA = "form_data";
    public static final String FORM_DATA_VERSION = "form_data_version";
    public static final String FORM_FILTER_VALUE = "form_filter_value";
    public static final String FORM_DOMAIN = "form_domain";
    public static final String FORM_DOMAIN_VALUE = "form_domain_value";


    // Workflow Module
    public static final String WORKFLOW = "workflow";
    public static final String WORKFLOW_STEP = "workflow_step";
    public static final String WORKFLOW_TRANSITION = "workflow_transition";
    public static final String WORKFLOW_RULE = "workflow_rule";
    public static final String WORKFLOW_ENVELOPE = "workflow_envelope";

    // net.project.resource.person
    public static final String PERSON = "person";

    // net.project.project
    public static final String PROJECT = "project";

    public static final String METHODOLOGY = "methodology";

    // net.project.business
    public static final String BUSINESS = "business";

    // News Module
    public static final String NEWS = "news";

    // net.project.admin.ApplicationSpace
    public static final String APPLICATION = "application";
    public static final String DOMAIN = "domain";

    // net.project.configuration.ConfigurationSpace
    public static final String CONFIGURATION = "configuration";

    // net.project.license.License
    public static final String LICENSE = "license";

    // Salesmode Message Module
    public static final String MESSAGE = "message";

    // Salesmode Sales Module
    public static final String SALES_REPORT = "salesreport";
    public static final String ORDER = "order";
    public static final String ORDER_LINE = "orderline";

    // Product
    public static final String PRODUCT = "product";

    // Company
    public static final String COMPANY = "company";

    // Inventory Item
    public static final String INVENTORY_ITEM = "inventory_item";

    // Product Price
    public static final String PRODUCT_PRICE = "product_price";

    // Warehouse
    public static final String WAREHOUSE = "warehouse";

    // Salesmode Schedule Module
    public static final String SCHEDULE_ENTRY = "schedule";

    // Report module
    public static final String REPORT = "report";

    //Enterprise space
    public static final String ENTERPRISE_SPACE = "enterprise";
    
    //Trashcan module
    public static final String TRASHCAN = "trashcan";
    
    //Timesheet object
    public static final String TIMESHEET = "timesheet";
    public static final String ACTIVITY = "activity";

    // Blog object
    public static final String BLOG = "blog";
    public static final String BLOG_ENTRY = "blog_entry";
    public static final String BLOG_COMMENT = "blog_comment";
    
    // Wiki object
    public static final String WIKI = "wiki";

    /**
     * Returns the Module that the specified object belongs to.
     * @param object the object to get the module for
     * @return the module
     * @see Module
     */
    public static int getModuleFromObject(ILinkableObject object) {
        if(object == null)
            return -1;
        
        if(object instanceof Document) {
            if (((Document) object).isDeleted()) 
                return getModuleFromType(TRASHCAN);
            else
                return getModuleFromType(object.getType());
        } else {
            return getModuleFromType(object.getType());
        }
            
    }
    /**
     * Returns the Module that the specified object type belongs to.
     * @param objectType the object type to get the module for
     * @return the module
     * @see Module
     */
    public static int getModuleFromType(String objectType) {
        if (objectType.equalsIgnoreCase(CALENDAR))
            return Module.CALENDAR;
        else if (objectType.equalsIgnoreCase(MEETING))
            return Module.CALENDAR;
        else if (objectType.equalsIgnoreCase(EVENT))
            return Module.CALENDAR;
        else if (objectType.equalsIgnoreCase(SCHEDULE))
            return Module.SCHEDULE;
        else if (objectType.equalsIgnoreCase(TASK))
            return Module.SCHEDULE;
        else if (objectType.equalsIgnoreCase(MILESTONE))
            return Module.SCHEDULE;
        else if (objectType.equalsIgnoreCase(PHASE))
            return Module.PROCESS;
        else if (objectType.equalsIgnoreCase(GATE))
            return Module.PROCESS;
        else if (objectType.equalsIgnoreCase(DELIVERABLE))
            return Module.PROCESS;
        else if (objectType.equalsIgnoreCase(PROCESS))
            return Module.PROCESS;
        else if (objectType.equalsIgnoreCase(DISCUSSION))
            return Module.DISCUSSION;
        else if (objectType.equalsIgnoreCase(POST))
            return Module.DISCUSSION;
        else if (objectType.equalsIgnoreCase(DOCUMENT))
            return Module.DOCUMENT;
        else if (objectType.equalsIgnoreCase(BOOKMARK))
            return Module.DOCUMENT;
        else if (objectType.equalsIgnoreCase(CONTAINER))
            return Module.DOCUMENT;
        else if (objectType.equalsIgnoreCase(DOCUMENT_SPACE))
            return Module.DOCUMENT;
        else if (objectType.equalsIgnoreCase(FORM))
            return Module.FORM;
        else if (objectType.equalsIgnoreCase(FORM_LIST))
            return Module.FORM;
        else if (objectType.equalsIgnoreCase(FORM_FIELD))
            return Module.FORM;
        else if (objectType.equalsIgnoreCase(FORM_DATA))
            return Module.FORM;
        else if (objectType.equalsIgnoreCase(WORKFLOW))
            return Module.WORKFLOW;
        else if (objectType.equalsIgnoreCase(WORKFLOW_STEP))
            return Module.WORKFLOW;
        else if (objectType.equalsIgnoreCase(WORKFLOW_TRANSITION))
            return Module.WORKFLOW;
        else if (objectType.equalsIgnoreCase(WORKFLOW_RULE))
            return Module.WORKFLOW;
        else if (objectType.equalsIgnoreCase(WORKFLOW_ENVELOPE))
            return Module.WORKFLOW;
        else if (objectType.equalsIgnoreCase(NEWS))
            return Module.NEWS;
        else if (objectType.equalsIgnoreCase(MESSAGE))
            return Module.MESSAGE;
        else if (objectType.equalsIgnoreCase(SALES_REPORT))
            return Module.SALES;
        else if (objectType.equalsIgnoreCase(ORDER))
            return Module.SALES;
        else if (objectType.equalsIgnoreCase(ORDER_LINE))
            return Module.SALES;
        else if (objectType.equalsIgnoreCase(APPLICATION))
            return Module.APPLICATION_SPACE;
        else if (objectType.equalsIgnoreCase(CONFIGURATION))
            return Module.CONFIGURATION_SPACE;
        else if (objectType.equalsIgnoreCase(ENTERPRISE_SPACE))
            return Module.ENTERPRISE_SPACE;
        else if (objectType.equalsIgnoreCase(TRASHCAN))
            return Module.TRASHCAN;
        else
            return -1;

    }

    /**
     * Returns all object types loaded from the database.
     * @return a collection where each element is an <code>ObjectType</code>;
     * the collection is unmodifiable
     */
    public static Collection getAll() throws PersistenceException {

        List allObjectTypes = new ArrayList();
        DBBean db = new DBBean();

        try {

            db.executeQuery("select object_type, object_type_desc from pn_object_type");

            while (db.result.next()) {
                ObjectType objectType = new ObjectType();
                objectType.setType(db.result.getString("object_type"));
                objectType.setDescriptionPropertyName(db.result.getString("object_type_desc"));
                allObjectTypes.add(objectType);
            }


        } catch (SQLException sqle) {
            throw new PersistenceException("Error loading all object types: " + sqle, sqle);

        } finally {
            db.release();
        }

        return Collections.unmodifiableCollection(allObjectTypes);
    }

    //
    // Instance Members
    //

    /**
     * The unique type ID of this ObjectType.
     */
    private String type = null;

    /**
     * The name of the property that provides this ObjectType's display
     * description.
     */
    private String descriptionPropertyName = null;

    /**
     * Specifies the type ID for this ObjectType.
     * This is one of the string constants defined in this class.
     * @param type the type ID
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns this ObjectType's type ID.
     * @return one of the string contstants defined in this class.
     */
    public String getType() {
        return type;
    }

    /**
     * Specifies the name of the property that provides the description for
     * this object type.
     * @param descriptionPropertyName the property that provides the description
     */
    public void setDescriptionPropertyName(String descriptionPropertyName) {
        this.descriptionPropertyName = descriptionPropertyName;
    }

    /**
     * Returns the display description of this ObjectType.
     * @return the display description.
     */
    public String getDescription() {
        return PropertyProvider.get(this.descriptionPropertyName);
    }

    /**
     * Loads this ObjectType.
     * Assumes that <code>type</code> has been set.
     */
    public void load() {

        String query = "select object_type, object_type_desc " +
                "from pn_object_type where object_type = '" + type + "'";

        DBBean db = new DBBean();

        try {

            db.setQuery(query);
            db.executeQuery();
            if (db.result.next()) {
                setType(db.result.getString("object_type"));
                setDescriptionPropertyName(db.result.getString("object_type_desc"));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectType.class).error("ObjectType.load: SQL Exception: " + sqle);

        } finally {
            db.release();
        }
    }

}