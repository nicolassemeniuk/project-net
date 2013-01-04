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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * A list of security Actions for a particular object type.
 *
 * @see java.util.ArrayList
 */
public class ActionList extends ArrayList implements Serializable, IXMLPersistence {

    /** the ObjectType context. */
    private ObjectType objectType = null;
    /** Translate XML and XSL into HTML. */
    private XMLFormatter formatter = null;

    // This cache is safe as static because it loads global default actions that are the same
    // across all spaces
    private static Hashtable defaultActionCache = new Hashtable();

    private boolean isLoaded = false;


    /**
     * Construct a group list of unknown size.  Grows as needed.
     */
    public ActionList() {
        super();
        formatter = new XMLFormatter();
    }

    /**
     * Construct a group list of specified size.  Grows as needed.
     */
    public ActionList(int size) {
        super(size);
        formatter = new XMLFormatter();
    }


    /**
     * Set the User context for the GroupCollection.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;

        // new context; we need to clear the list.
        isLoaded = false;
        this.clear();
    }

    /**
     * Gets the default permissions for an object type
     *
     * @return String that represents bitMaskActions
     */
    public String getDefaultActions() {
        String actions = null;
        String query;   

        // attempt to retrieve from cache
        if ((actions = (String) defaultActionCache.get(objectType.getType())) != null) {
            return actions;
        }

        query = "SELECT default_permission_actions " +
            "FROM pn_object_type " +
            "WHERE object_type = ? ";

        DBBean db = new DBBean();
        try {

            int index = 0;

            db.prepareStatement(query);
            db.pstmt.setString(++index, objectType.getType());
            db.executePrepared();

            if (db.result.next()) {
                actions = db.result.getString(1);
            }

            // store in cache
            synchronized (defaultActionCache) {
                defaultActionCache.put(objectType.getType(), actions);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ActionList.class).error("ActionList.getDefaultActions failed " + sqle);
        } finally {
            db.release();
        }

        return actions;
    }

    /**
     * Get all action types from database
     */
    public void loadAllActions() {
        String query;
        Action action = null;
        this.clear();
        query = "SELECT action_id, name, description, bit_mask FROM pn_security_action";

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query);
            db.executePrepared();
            while (db.result.next()) {
                action = new Action();

                action.setID(db.result.getString("action_id"));
                action.setName(db.result.getString("name"));
                action.setDescription(PropertyProvider.get(db.result.getString("description")));
                action.setBitMask(db.result.getInt("bit_mask"));
                this.add(action);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ActionList.class).error("ActionList.loadAllActions failed " + sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Get the XML list of actions
     *
     * @return XML
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<permittedActions>\n");

        for (int i = 0; i < this.size(); i++) {
            xml.append(((Action) this.get(i)).getXMLBody());
        }

        xml.append("</permittedActions>\n");
        return xml.toString();
    }


    /**
     * Returns an Action based on its bitmask or null if the action for that
     * bitmask is not found.  Assumes this ActionList is loaded.  Returns the
     * first matching action if more than one has the same bitmask (should be
     * impossible).
     *
     * @param bitmask the bitmask of the action to get
     * @return the action for the specified bitmask or null
     */
    public Action getActionForBitmask(String bitmask) {
        Action foundAction = null;
        boolean isFound = false;
        int bitMaskNumber = Integer.valueOf(bitmask).intValue();

        Iterator it = this.iterator();
        while (it.hasNext() & !isFound) {
            Action nextAction = (Action) it.next();

            if (nextAction.getBitMask() == bitMaskNumber) {
                foundAction = nextAction;
                isFound = true;
            }

        }

        return foundAction;
    }

    /************************************************************************************************
     *  Implementing IJDBCPersistence.
     ***********************************************************************************************/

    /**
     * Is the list loaded from persistence.
     *
     * @return true if load, false otherwise.
     */
    public boolean isLoaded() {
        return isLoaded;
    }


    /**
     * load the list from persistence
     */
    public void load() {
        StringBuffer query = new StringBuffer();
        Action action = null;

        query.append("select a.action_id, a.name, a.description, a.bit_mask ");
        query.append("from pn_object_type_supports_action otsa, pn_security_action a ");
        query.append("where otsa.object_type = ? and a.action_id = otsa.action_id ");
        query.append("order by presentation_seq ");

        this.clear();

        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, objectType.getType());
            db.executePrepared();

            while (db.result.next()) {
                action = new Action();

                action.setID(db.result.getString("action_id"));
                action.setName(db.result.getString("name"));
                action.setDescription(PropertyProvider.get(db.result.getString("description")));
                action.setBitMask(db.result.getInt("bit_mask"));
                this.add(action);
            }

            isLoaded = true;

        } catch (SQLException sqle) {
        	Logger.getLogger(ActionList.class).error("ActionList.load failed " + sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Gets the presentation of the component This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text
     *
     * @return presetation of the component
     */
    public String getPresentation() {
        return formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component. This method
     * accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML
     * representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        formatter.setStylesheet(styleSheetFileName);
    }

}
