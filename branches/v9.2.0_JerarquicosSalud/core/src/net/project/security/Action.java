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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.security;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A set of actions a Role may perform on an object.  Subclasses define the
 * object. <p>
 *
 * The action bitMask can be set using the setBitMask(int) method.  The allowed
 * action bit_masks include: <PRE> VIEW -- view or read. MODIFY -- modify or
 * edit CREATE -- create a new child object. DELETE -- delete or remove. LIST --
 * list the children of the object. PROPERTIES -- view the properties of the
 * object without being able to VIEW the object itself. </PRE>
 *
 * @see net.project.security.Permission
 * @see net.project.base.ObjectType
 */
public class Action implements java.io.Serializable {
    /**
     * the database ID of the action
     */
    private String actionID = null;

    /**
     * the name of the action for presentation.  "view", "modify", etc.
     */
    private String name = null;

    /**
     * the long description of the action
     */
    private String description = null;

    /**
     * the bit mask value of the action.  VIEW, MODIFY, etc.
     */
    private int bitMask = 0;

    public static final int VIEW = 1;
    public static final int MODIFY = 2;
    public static final int CREATE = 4;
    public static final int DELETE = 8;
    public static final int LIST = 16;
    public static final int PROPERTIES = 32;
    public static final int MODIFY_PERMISSIONS = 64;
    public static final int SHARE = 128;
    public static final int REMOVE_DELETED = 512;
    public static final int UNDO_DELETED = 1024;
    public static final int LIST_DELETED = 256;
    
    public static final String VIEW_STRING = "view";
    public static final String LIST_DELETED_STRING = "list_Deleted";
    public static final String UNDO_DELETE_STRING = "undo_delete";
    public static final String MODIFY_STRING = "modify";
    public static final String CREATE_STRING = "create";
    public static final String DELETE_STRING = "delete";
    public static final String REMOVE_DELETE_STRING = "remove_Deleted";
    public static final String LIST_STRING = "list";
    public static final String PROPERTIES_STRING = "properties";
    public static final String MODIFY_PERMISSIONS_STRING = "modify_permissions";
    public static final String SHARE_STRING = "share";

    /**
     * set the action's database ID
     */
    public void setID(String id) {
        actionID = id;
    }

    /**
     * get the atcion's database ID
     */
    public String getID() {
        return actionID;
    }


    /**
     * set the action's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * the action's name
     */
    public String getName() {
        return name;
    }


    /**
     * Is this Actions equal to the passed Action?  Database IDs determine
     * equivelence.
     */
    public boolean equals(Action action) {
        // per java.lang.Object spec.
        if (action == null) {
            return false;
        }

        return (this.getID() == action.getID());
    }


    /**
     * set the action's description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * get the action's description
     */
    public String getDescription() {
        return description;
    }


    /**
     * set the action's description
     */
    public void setBitMask(int mask) {
        bitMask = mask;
    }

    /**
     * get the action's description
     */
    public int getBitMask() {
        return bitMask;
    }

    /**
     * Returns an XML representation of the object without the XML version tag.
     *
     * @return XML representation of the Action's properties.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<action bitMask= \"" + bitMask + "\">" + XMLUtils.escape(description) + "</action>\n");
        return xml.toString();
    }


    /**
     * Returns an XML representation of the object.
     *
     * @return XML representation of the Action.
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append(getXMLBody());
        return xml.toString();
    }

    public void store() {
        String query;

        // null group_id means we are storing the group for the first time.
        actionID = ObjectManager.dbCreateObject("action", "A", null);

        query = "insert into pn_security_action (action_id, name, description, bit_mask) " +
            "values (" + actionID + ", '" + name + "', '" + description + "'," + bitMask + ")";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(Action.class).error("Action.store failed " + sqle);
        } finally {
            db.release();
        }
    }

    public void loadByName() {
        String query;

        query = "SELECT action_id, description, bit_mask " +
            "FROM pn_security_action " +
            "WHERE name = '" + name + "'";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            actionID = db.result.getString(1);
            description = PropertyProvider.get(db.result.getString(2));
            bitMask = db.result.getInt(3);
        } catch (SQLException sqle) {
        	Logger.getLogger(Action.class).error("Action.loadByName failed " + sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Translate an action with an action name (such as "view" or "modify") to
     * an action number defined by Action.VIEW or Action.MODIFY. <p>One of:
     * <li>view</li> <li>modify</li> <li>create</li> <li>delete</li>
     * <li>list</li> <li>properties</li> <li>modify_permissions</li> </p>
     *
     * @param actionString a <code>String</code> value in the form of "view" or
     * "modify".
     * @return an <code>int</code> value which is the static integer value that
     *         corresponds to the string passed into this method.  The method
     *         returns -1 if the string does not correspond to a known action.
     */
    public static int actionStringToInt(String actionString) {
        int actionInt;

        if (actionString.equalsIgnoreCase(VIEW_STRING)) {
            actionInt = Action.VIEW;
        } else if (actionString.equalsIgnoreCase(LIST_DELETED_STRING)) {
            actionInt = Action.LIST_DELETED;
        } else if (actionString.equalsIgnoreCase(REMOVE_DELETE_STRING)) {
            actionInt = Action.REMOVE_DELETED;
        } else if (actionString.equalsIgnoreCase(UNDO_DELETE_STRING)) {
            actionInt = Action.UNDO_DELETED;
        } else if (actionString.equalsIgnoreCase(MODIFY_STRING)) {
            actionInt = Action.MODIFY;
        } else if (actionString.equalsIgnoreCase(CREATE_STRING)) {
            actionInt = Action.CREATE;
        } else if (actionString.equalsIgnoreCase(DELETE_STRING)) {
            actionInt = Action.DELETE;
        } else if (actionString.equalsIgnoreCase(LIST_STRING)) {
            actionInt = Action.LIST;
        } else if (actionString.equalsIgnoreCase(PROPERTIES_STRING)) {
            actionInt = Action.PROPERTIES;
        } else if (actionString.equalsIgnoreCase(MODIFY_PERMISSIONS_STRING)) {
            actionInt = Action.MODIFY_PERMISSIONS;
        } else if (actionString.equalsIgnoreCase(SHARE_STRING)) {
            actionInt = Action.SHARE;
        } else {
            actionInt = -1;
        }

        return actionInt;
    }
}

