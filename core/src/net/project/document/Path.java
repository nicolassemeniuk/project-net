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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

/**
 * A Path represents the display path to an object in the document vault.
 */
public class Path 
        implements java.io.Serializable, net.project.persistence.IXMLPersistence {


    /**
     * The collection of nodes in the path (from the top down).
     */
    private ArrayList path = new ArrayList();

    /** 
     * Represents the number of levels in the path.
     */
    private int treeLevel = -1;

    /**
     * The current object that this path is for.
     */
    private String objectID = null;

    /**
     * Root container can optionally be specified to artificially limit the top
     * of the path.  This allows us to browse system folders without seeing the
     * system folder hierarchy.
     */
    private String rootContainerID = null;


    /**
     * Creates an empty Path.
     */
    public Path () {
        // Nothing to initialze
    }


    /**
     * Creates a loaded Path for the object with the specified id.
     * @param objectID the id of the object whose path to get
     */
    public Path (String objectID) {
        setObject (objectID);
        load();
    }


    /**
     * Creates a loaded Path for the specified container object.
     * @param co the container object whose path to get
     */
    public Path (IContainerObject co) {
        setObject (co);
        load();
    }


    /**
     * Specifies the object for which to get the path.
     * This does not load the Path.
     * @param objectID the id of the object whose path to get
     * @see #load
     */
    public void setObject (String objectID) {
        this.objectID = objectID;
    }


    /**
     * Specifies the container object for which to get the path.
     * This does not load the Path.
     * @param co the container object whose path to get
     * @see #load
     */
    public void setObject (IContainerObject co) {
        this.objectID = co.getID();
    }

    /**
     * Set the root object id for this path.  By setting this value, 
     * you can artificially cause this path object to stop at a given
     * id and declare it as being "The Top Folder".  Previously, this
     * was only done when you had traversed the tree all the way to
     * the top.
     *
     * @author Matthew Flower
     * @since Gecko Update 2
     * @param rootContainerID a <code>String</code> value containing the
     * id of the container that we are going to call the "Top Folder"
     */
    public void setRootContainerID(String rootContainerID) {
        this.rootContainerID = rootContainerID;
    }

    /**
     * Get the top folder for this path.  If this ID is null, the path
     * object will trace the path until it finds a node that has no
     * parent. 
     *
     * @author Matthew Flower
     * @since Gecko Update 2
     * @return a <code>String</code> value
     */
    public String getRootContainerID() {
        return this.rootContainerID;
    }

    /**
     * Loads the path for the current object.
     * Assumes the object has been set.
     * @see #setObject
     */
    public void load () {
        
        DBBean db = new DBBean();
        PathNode node = null;
        ArrayList loadedPath = new ArrayList();

        treeLevel = 0;

        try {
            int index = 0;
            int refCursorIndex = 0;

            // Do not automatically commit; this allows us to rollback
            // after getting the path to remove any temporary data
            db.setAutoCommit(false);

            // Get the path information back from the database
            // This stored procedure inserts data into a temporary table
            // then returns a reference cursor
            db.prepareCall ("{ ? = call DOCUMENT.GET_PATH_FOR_OBJECT (?,?) }");
            db.cstmt.registerOutParameter((refCursorIndex = ++index), oracle.jdbc.OracleTypes.CURSOR);
            db.cstmt.setString(++index, this.objectID);
            db.cstmt.setString(++index, this.rootContainerID);
            db.executeCallable();
            db.result = (ResultSet) db.cstmt.getObject (refCursorIndex);

            // Now use the path info to construct the path
            while (db.result.next()) {
                node = new PathNode ( db.result.getString("object_id"), db.result.getString("object_name") );
                loadedPath.add(node);
                treeLevel++;
            }

            // Now rollback the temporary data
            db.rollback();

            // Reverse the order of the path elements and store in instance variable
            Collections.reverse(loadedPath);
            this.path = loadedPath;

        } catch (SQLException sqle) {
        	Logger.getLogger(Path.class).error("Path.load() threw a SQLException: " + sqle);

        } finally {
            
            try {
                // Perform final rollback in the event of any error
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }
            
            db.release();
        }

    }


    /**
     * Returns the depth of the path.
     * Assumes {@link #load} has been called.
     * @return the path depth
     */
    public int getTreeLevel () {
        return this.treeLevel;
    }


    //
    // Implementing IXMLPersistence
    //


    /**
     * Returns this Path as XML.
     * Note: Currently this does not include the XML version tag; previously
     * it did not.  Including the XML version tag may cause other XML that is
     * including this Path's XML to break
     * @return the XML
     */
    public String getXML() {
        // Do not include version tag
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns this Path as XML, excluding the XML version tag.
     * @return the XML
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }


    /**
     * Returns an xml document representing this path.
     * @return the xml path
     * @throws XMLDocumentException if there is a problem building the xml
     */
    private XMLDocument getXMLDocument() {
        PathNode node = null;
        int currentLevel = 0;
        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("path");
            doc.addElement("jsp_root_url", SessionManager.getJSPRootURL());
            doc.addElement("path_size", Conversion.intToString(this.path.size()));

            // Add all the nodes in the path to the xml document
            Iterator it = this.path.iterator();
            while (it.hasNext()) {
                node = (PathNode) it.next();
                currentLevel++;

                doc.startElement("node");
                doc.addElement("object_id", node.getID());
                doc.addElement("name", PropertyProvider.get(node.getName()));
                doc.addElement("level", Conversion.intToString(currentLevel));
                doc.endElement();

            }

            // End "path"
            doc.endElement();
        
        } catch (XMLDocumentException xde) {
        	Logger.getLogger(Path.class).error("Path.getXMLDocument() threw an XMLDocumentException: " + xde);

        }

        return doc;
    }


    /**
     * Returns this Path as a string.  This is each path element name
     * separated by "/".
     * @return the path string
     */
    public String toString () {
        StringBuffer pathString = new StringBuffer();
        PathNode node = null;

        // Iterate over all path elements, building string path
        Iterator listIterator = this.path.iterator();
        while (listIterator.hasNext()) {
            node = (PathNode) listIterator.next();
            
            if (pathString.length() > 0) {
                pathString.append("/");
            }

            pathString.append(PropertyProvider.get(node.getName()));
        }
        
        return pathString.toString();
    }

}
