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

 package net.project.document;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

/**
 * A ContainerCollection maintains a collection of containers as ContainerEntry
 * objects. <p> <b>Example</b>: To load all the containers for a document space
 * and get the <code>ContainerEntry</b> objects as a List:<br>
 * <code><pre>
 *     ContainerCollection c = new ContainerCollection(documentSpaceID);
 *     c.load();
 *     myList = c.getContainerList();
 * </code></pre>
 */
public class ContainerCollection implements Serializable {
    /**
     * Current document space ID
     */
    private String docSpaceID = null;

    /**
     * The hashOfContainers is a HashMap where each key is a String container ID
     * and each value is a ContainerEntry object representing the Container with
     * that ID.
     */
    private HashMap hashOfContainers = null;

    /**
     * The parentContainerHash is a HashMap where each key is a String container
     * ID and each value is an ArrayList, where the entries in that ArrayList
     * are ContainerEntry objects which are children of the container
     * represented by the key.
     */
    private HashMap parentContainerHash = null;
    private boolean isLoaded = false;


    /**
     * Creates a new, empty ContainerCollection.
     */
    public ContainerCollection() {
    }

    /**
     * Creates a new, loaded ContainerCollection for the specified document
     * space ID.  The containers in the specified document space are loaded.
     *
     * @param docSpaceID the id of the document space for which the containers
     * are loaded.
     * @see #load
     */
    public ContainerCollection(String docSpaceID) {
        setDocSpaceID(docSpaceID);
        load();
    }

    /**
     * Sets the document space ID for which containers are to be loaded.
     *
     * @param docSpaceID the id of the document space for which the containers
     * are to be loaded.
     */
    public void setDocSpaceID(String docSpaceID) {
        this.docSpaceID = docSpaceID;
    }


    /**
     * Populates this container collection with all non-hidden containers in the
     * current document space. Assumes {@link #setDocSpaceID} has been called.
     */
    public void load() {

        ContainerEntry entry = null;
        HashMap containerHash = new HashMap();
        HashMap parentHash = new HashMap();

        DBBean db = new DBBean();
        try {
            // No auto-commit so that we can rollback the temporary data later
            db.setAutoCommit(false);

            // This call returns a reference cursor to all non-hidden
            // Containers in the specified document space id
            db.prepareCall("{ ? = call DOCUMENT.GET_CONTAINER_LIST (?) }");

            db.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            db.cstmt.setString(2, this.docSpaceID);

            db.executeCallable();

            db.result = (ResultSet) db.cstmt.getObject(1);

            // For each returned container, build a ContainerEntry object
            // and add to both HashMaps
            while (db.result.next()) {

                entry = new ContainerEntry();
                entry.setID(db.result.getString("object_id"));
                entry.setName(db.result.getString("object_name"));
                entry.setContainerID(db.result.getString("parent_id"));
                entry.setType(ContainerObjectType.CONTAINER_OBJECT_TYPE);

                if (db.result.getInt("is_root") == 1) {
                    entry.setIsRoot(true);
                } else {
                    entry.setIsRoot(false);
                }

                // add this container to the HashMap
                containerHash.put(entry.getID(), entry);

                // add an entry for this container in the parentHash
                addEntryToHash(parentHash, entry.getContainerID(), entry);

            }    // end while()

            isLoaded = true;

            // Now rollback temporary data
            db.rollback();

        } catch (SQLException sqle) {
            isLoaded = false;
            Logger.getLogger(ContainerCollection.class).debug("ContainerCollection.buildContainerCollection() threw a SQL exception: " + sqle);

        } finally {

            try {
                // Final rollback in event of any problems
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }

            db.release();

        }

        this.hashOfContainers = containerHash;
        this.parentContainerHash = parentHash;

    }   // end load()


    /**
     * Returns a list of containers whose parent container is the container with
     * the specified id.
     *
     * @param parentContainerID the id of the container that is the parent for
     * all containers in the returned list
     * @param order the traversal order
     * @return a List of ContainerEntry objects
     */
    public ArrayList getContainersForParentContainer(String parentContainerID, TraversalOrder order) {
        ArrayList resultList = new ArrayList();
        getDescendantContainers(resultList, parentContainerID, order);
        return (resultList);
    }


    /**
     * Returns the sum of the number objects in the container with the specified
     * ID and <b>ALL</b> child containers of that (not including any of the
     * containers themselves
     *
     * @param containerID the id of the container which represents the root of
     * the container tree in which to sum objects
     * @return the sum of objects in the specified container and all its
     *         children; or < 0 if there is a problem counting the objects
     */
    public int getCountObjectsInContainerTree(String containerID) {

        ArrayList resultList = new ArrayList();
        int totalCount = -1;

        // Populate resultList with all child containers of the container with 
        // ID of containerID
        getDescendantContainers(resultList, containerID, TraversalOrder.BREADTH_FIRST);

        // Build a query that counts objects in the specified container and
        // all the child containers.  Note that the containers themseleves
        // will be counted.
        String qstrGetCountObjectsInFolderList = "select count (object_id) object_count from pn_doc_container_list_view " +
            " where doc_container_id = " + containerID;

        for (int i = 0; i < resultList.size(); i++) {
            qstrGetCountObjectsInFolderList += " or doc_container_id = " + (String) (((ContainerEntry) resultList.get(i)).getID());
        }

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrGetCountObjectsInFolderList);

            if (db.result.next()) {
                totalCount = db.result.getInt("object_count");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ContainerCollection.class).debug("ContainerCollection.getCountObjectsInContainerTree() threw a SQL exception: " + sqle);

        } finally {
            db.release();

        }

        // Subtract out the number of child containers searched since
        // the count would have included them
        totalCount -= resultList.size();

        return totalCount;
    }


    /**
     * Return a ContainerEntry from the collection given its objectID
     *
     * @param objectID the id of the container to get
     * @return the ContainerEntry object
     */
    public ContainerEntry getContainerEntry(String objectID) {
        return ((ContainerEntry) hashOfContainers.get(objectID));
    }

    /**
     * Return a ContainerEntry from the collection that is the parent container
     * of the specified entry.
     *
     * @param entry the entry whose parent container to get
     * @return the parent ContainerEntry object
     */
    public ContainerEntry getParentContainerEntry(ContainerEntry entry) {
        return (getContainerEntry(entry.getContainerID()));
    }


    /**
     * Returns a list of ContainerEntry objects where the container with the
     * specified ID is a descendant of every container in the list. The List
     * includes the container with specified ID itself.  The List also includes
     * the Root container.  The list is ordered with the Root container as the
     * first element.
     *
     * @param containerID the id of the container to fetch the parent containers
     * for
     * @return the List of ContainerEntry objects including the specified
     *         container
     */
    public List getAncestorContainers(String containerID) {
        ArrayList ancestorList = new ArrayList();
        
        // Add all ancestors of container to list
        addAncestorContainers(ancestorList, containerID);

        return ancestorList;
    }

    /**
     * Adds a container and its ancestors to the specified list.
     *
     * @param ancestorList the list to add to
     * @param containerID id of the container to add, including all its ancestors
     */
    private void addAncestorContainers(List ancestorList, String containerID) {
        if (containerID == null) {
            return;
        }

        // Fetch the ContainerEntry with specified ID from collection
        ContainerEntry containerEntry = getContainerEntry(containerID);
        
        // If that container is in this collection
        // then add it and add the ancestors of its parent
        if (containerEntry != null) {
            // Add at index 0 so that greatest ancestors are at top
            ancestorList.add(0, containerEntry);
            
            // Stop when we reach the root
            if (containerEntry.isRoot()) {
                return;
            } else {
                addAncestorContainers(ancestorList, containerEntry.getContainerID());
            }

        }

        return;
    }

    /**
     * **************************************************************************************************************
     * ****                                 Implementing redering methods
     *                                                  *****
     * ***************************************************************************************************************
     */

    public String getAppletXMLForSubfolders(String parentContainerID, String spaceName) {

        StringBuffer xml = new StringBuffer();

        ArrayList list = getContainersForParentContainer(parentContainerID, TraversalOrder.BREADTH_FIRST);
        Iterator i = list.iterator();

        ContainerEntry entry = null;

        xml.append("<?xml version=\"1.0\" ?>\n\n");

        xml.append("<subfolder_collection>\n");

        while (i.hasNext()) {

            entry = (ContainerEntry) i.next();

            if (entry != null) {
                xml.append(entry.getAppletXML(spaceName));
            }

        } // end while

        xml.append("</subfolder_collection>");

        return xml.toString();

    } // end getAppletXMLForSubfolders

    public String getXML() {

        StringBuffer xml = new StringBuffer();
        Iterator i = toArrayList().iterator();
        ContainerEntry entry = null;

        xml.append("<?xml version=\"1.0\" ?>\n\n");

        xml.append("<container_collection>");

        while (i.hasNext()) {

            entry = (ContainerEntry) i.next();

            if (entry != null) {
                xml.append(entry.getXML());
            }

        } // end while

        xml.append("</container_collection>");

        return xml.toString();
    } // getXML()

    public String getAppletXML(String spaceName) {

        StringBuffer xml = new StringBuffer();
        Iterator i = toArrayList().iterator();
        ContainerEntry entry = null;

        xml.append("<?xml version=\"1.0\" ?>\n\n");

        xml.append("<container_collection>");

        while (i.hasNext()) {

            entry = (ContainerEntry) i.next();

            if (entry != null) {
                xml.append(entry.getAppletXML(spaceName));
            }

        } // end while

        xml.append("</container_collection>");

        return xml.toString();
    } // getXML()


    //
    // Utility Methods
    //

    /**
     * Returns the collection of ContainerEntry objects as an ArrayList.
     *
     * @return the List of ContainerEntry objects
     */
    private ArrayList toArrayList() {
        return new ArrayList(hashOfContainers.values());
    }


    /**
     * Adds the specified entry to a hash where the values in the hash are
     * actually Lists.<br> If the specified key is not present in the hash, then
     * that key is added and the entry is added as the first item in a List.
     * The List becomes the value for the key.<br> If the specified key is
     * present then the entry is added to the existing List which is already the
     * value for they key.
     *
     * @param hash the HashMap to add the entry to
     * @param key the key to add the entry to
     * @param entry the entry to add
     */
    private void addEntryToHash(HashMap hash, String key, ContainerEntry entry) {

        Object tmp = null;
        ArrayList tmpList = null;

        // get any entry currently hashed to this key
        tmp = hash.get(key);

        if (tmp == null) {
            // Key not present in hash
            // Create a new ArrayList and add the entry as the first value
            tmpList = new ArrayList();
            tmpList.add(entry);

            // Put that arraylist as the value for the key
            hash.put(key, tmpList);

        } else {
            // Key is present in hash
            // Add the entry to the existing arraylist
            tmpList = (ArrayList) tmp;
            tmpList.add(entry);

            // Replace the value for the key with the updated arraylist
            hash.remove(key);
            hash.put(key, tmpList);
        }

    } // end addEntryToHash


    /**
     * Updates a list adding all the descendant containers of the container with
     * specified id.
     *
     * @param resultList the list to add to
     * @param containerID the id of the container for which to get all
     * descendants
     * @param order the traversal order
     */
    private void getDescendantContainers(ArrayList resultList, String containerID, TraversalOrder order) {

        // Load this collection if necessary
        if (!isLoaded) {
            this.load();
        }

        // If the container is present in the parent hash (i.e. it is a parent
        // of one or more containers) then get the child containers and
        // add them and their children

        if (this.parentContainerHash.containsKey(containerID)) {
            ContainerEntry entry = null;
            ArrayList immediateChildren = (ArrayList) this.parentContainerHash.get(containerID);

            if (immediateChildren != null) {

                // In the case of Breadth first, all siblings are added
                // first.
                if (order.equals(TraversalOrder.BREADTH_FIRST)) {
                    resultList.addAll(immediateChildren);

                } else {
                    // Each child is added later for orders other than
                    // breadth first

                }

                // Loop over each immediate child and add all their children
                // to the resultList
                Iterator childrenIt = immediateChildren.iterator();
                while (childrenIt.hasNext()) {
                    entry = (ContainerEntry) childrenIt.next();

                    if (entry != null) {

                        // In the case of Depth first, we haven't yet added
                        // the children.  We add each sibling just before
                        // adding its children
                        if (!order.equals(TraversalOrder.BREADTH_FIRST)) {
                            resultList.add(entry);
                        }

                        // Now add all children of current entry
                        getDescendantContainers(resultList, entry.getID(), order);
                    }

                } //end while

            }

        }

    } // end getDescendantContainers


    /**
     * Enumeration of traversal orders
     */
    public static class TraversalOrder {
        // Next ordinal number
        private static int nextOrd = 0;

        private int id = 0;

        /**
         * Creates a new TraversalOrder constant.
         */
        private TraversalOrder() {
            this.id = TraversalOrder.nextOrd++;
        }

        /**
         * @return true if specified object is a TraversalOrder object that
         *         matches this one.
         */
        public boolean equals(Object obj) {
            if (obj instanceof TraversalOrder &&
                obj != null &&
                ((TraversalOrder) obj).id == this.id) {

                return true;
            }

            return false;
        }

        /**
         * Depth First means that a container's descendants will be listed
         * earlier than a container's siblings
         */
        public static final TraversalOrder DEPTH_FIRST = new TraversalOrder();

        /**
         * Breadth First means that a container's siblings will be listed
         * earlier than a container's descendants.
         */
        public static final TraversalOrder BREADTH_FIRST = new TraversalOrder();

    }

}   // end class ContainerCollection
