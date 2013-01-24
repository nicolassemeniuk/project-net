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
package net.project.link;

import java.sql.SQLException;

import net.project.base.ObjectFactory;
import net.project.database.DBBean;

import org.apache.log4j.Logger;

/**
 * This class is used for Managing links between objects.
 * @author Brian Conneen
 * @since Version 2
 */

public class LinkManager {

    /**
     * Creates an empty LinkManager.
     */
    public LinkManager() {
        // Do nothing
    }


    /**
     * Get all the child links for the specified object.
     *
     * @param object the object that is the parent
     * @return a DisplayLinks containing the found linked objects or null
     * if the specified object is null.  The link list is empty if
     * there is an error loading the link
     */
    public DisplayLinks getLinksFrom(ILinkableObject object, int context) {

        if (object == null) {
            return null;
        }

        DisplayLinks linkList = new DisplayLinks();

        addFromLinks(object, context, linkList);

        return linkList;
    }

    private void addFromLinks(ILinkableObject object, int context, DisplayLinks linkList) {

        // Note:
        // Due to most objects not setting pn_object.record_status = 'A'
        // when deleting, the record_status filter here has almost no effect
        String query = "SELECT ol.to_object_id, ob.object_type " +
                "FROM pn_object_link ol, pn_object ob " +
                "WHERE ol.from_object_id = " + object.getID() + " " +
                "AND ol.context = " + context + " " +
                "AND ob.object_id = ol.to_object_id " +
                "AND ob.record_status = 'A' ";

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);

            while (db.result.next()) {
                String objectID = db.result.getString("to_object_id");
                String objectType = db.result.getString("object_type");
                ILinkableObject nextObject = ObjectFactory.makeLinkableObject(objectType, objectID);

                // Sometimes objects are null if they cannot be loaded
                // For example, a FormList that has been deleted
                if (nextObject != null) {
                    linkList.add(nextObject, true);
                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(LinkManager.class).error("DocumentSearch.doSimpleSearch() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Get all the parent links for the specified object
     *
     * @param object the object that is the child
     * @return a DisplayLinks containing the found linked objects or null
     * if the specified object is null.  The link list is empty if
     * there is an error loading the link
     */
    public DisplayLinks getLinksTo(ILinkableObject object, int context) {

        if (object == null) {
            return null;
        }

        DisplayLinks linkList = new DisplayLinks();

        addToLinks(object, context, linkList);

        return linkList;

    }

    private void addToLinks(ILinkableObject object, int context, DisplayLinks linkList) {
        // Note:
        // Due to most objects not setting pn_object.record_status = 'A'
        // when deleting, the record_status filter here has almost no effect
        String query = "SELECT ol.from_object_id, ob.object_type " +
                "FROM pn_object_link ol, pn_object ob " +
                "WHERE ol.to_object_id = " + object.getID() + " " +
                "AND ol.context = " + context + " " +
                "AND ob.object_id = ol.from_object_id " +
                "AND ob.record_status = 'A' ";

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);

            while (db.result.next()) {
                String objectID = db.result.getString("from_object_id");
                String objectType = db.result.getString("object_type");
                ILinkableObject nextObject = ObjectFactory.makeLinkableObject(objectType, objectID);

                // Sometimes objects are null if they cannot be loaded
                // For example, a FormList that has been deleted
                if (nextObject != null) {
                    linkList.add(nextObject, false);
                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(LinkManager.class).error("LinkManager.getParents() threw an SQL exception: " + sqle);

        } finally {
            db.release();
        }
    }


    /**
     * Returns all parent and child links for the linkable object.
     *
     * @param object the object that is the child
     * @return a DisplayLinks containing the found linked objects or null
     * if the specified object is null.  The link list is empty if
     * there is an error loading the link
     */
    DisplayLinks getAllLinks(ILinkableObject object, int context) {

        if (object == null) {
            return null;
        }

        DisplayLinks linkList = new DisplayLinks();

        addFromLinks(object, context, linkList);
        addToLinks(object, context, linkList);

        return linkList;

    }



    /**
     * Create a parent to child link between the two specified objects.
     * Has no effect if both object IDs are null.
     * @param fromObjectID the id of the object to create the link from (parent).
     * @param toObjectID the id of the object to create the link to (child).
     */
    void addObjectLink(String fromObjectID, String toObjectID, int context) {

        if ((fromObjectID == null) || (toObjectID == null)) {
            return;
        }

        String query = "INSERT INTO pn_object_link (from_object_id, to_object_id, context)" +
                "VALUES(" + fromObjectID + "," + toObjectID + "," + context + ") ";

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);

        } catch (SQLException sqle) {
        	Logger.getLogger(LinkManager.class).error("LinkManager.addObjectLink() threw an SQL exception: " + sqle);

        } finally {
            db.release();
        }

    }


    /**
     * Remove a parent to child link between the two specified objects.
     *
     * @param fromObjectID the id of the object to remove the link from (parent).
     * @param toObjectID the id of the object to remove the link to (child).
     */
    void removeObjectLink(String fromObjectID, String toObjectID, int context) {

        if ((fromObjectID == null) || (toObjectID == null)) {
            return;
        }

        String query = "DELETE FROM pn_object_link " +
                "WHERE from_object_id = " + fromObjectID + " " +
                "AND to_object_id = " + toObjectID + " " +
                "AND context = " + context;

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);

        } catch (SQLException sqle) {
        	Logger.getLogger(LinkManager.class).error("LinkManager.removeLink() threw an SQL exception: " + sqle);

        } finally {
            db.release();
        }

        return;
    }

}