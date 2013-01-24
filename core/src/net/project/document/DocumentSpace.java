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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * A DocumentSpace contains all the containers and objects that belong to
 * a space.
 * It has a root container that is the root ancestor of all other containers
 * and objects in the document space.  It mostly provides wrappers for
 * manipulating Containers within a particular document space.
 */
public class DocumentSpace implements java.io.Serializable {

    private String objectID = null;
    private String name = null;
    private String rootContainerID = null;

    private ContainerCollection containerCollection = null;

    /**
     * Creates a new, empty DocumentSpace.
     */
    public DocumentSpace () {
        // do nothing
    }

    /**
     * Creates a new DocumentSpace with the specified ID.
     */
    public DocumentSpace (String objectID) {
        this.objectID = objectID;
    }


    //
    // Implementing other getter/setter methods
    //


    /**
     * Returns this DocumentSpace's id.
     * @return the id
     */
    public String getID () {
        return objectID;
    }


    /**
     * Sets the id of this DocumentSpace.
     * @param objectID the id
     */
    public void setID (String objectID) {
        this.objectID = objectID;
    }


    /**
     * Sets this DocumentSpace's name.
     * @param name the name
     */
    public void setName (String name) {
        this.name = name;
    }


    /**
     * Returns the name of this DocumentSpace.
     * @return the name
     */
    public String getName () {
        return this.name;
    }


    //
    // Collection Management Methods
    //

    /**
     * Loads the collection of containers within this document space.
     * After loading, the containers are available by calling {@link #getContainer}
     * @see ContainerCollection#load
     */
    public void loadContainerCollection () {

        ContainerCollection collection = new ContainerCollection ();

        collection.setDocSpaceID (this.objectID);
        collection.load();

        this.containerCollection = collection;

    }


    /**
     * Returns the XML form of the container collection in this document space.
     * This loads the container collection if not already loaded.
     * @see ContainerCollection#getXML
     */
    public String getXMLContainerCollection() {

        if (this.containerCollection == null)
            loadContainerCollection();

        return( this.containerCollection.getXML() );
    }


    /**
     * Returns the XML form of the container collection in this document space
     * formatted for the Applet.
     * This loads the container collection if not already loaded.
     * @see ContainerCollection#getAppletXML
     */
    public String getAppletXMLContainerCollection(String spaceName) {

        if (this.containerCollection == null)
            loadContainerCollection();

        return( this.containerCollection.getAppletXML(spaceName) );
    }


    /**
     * Returns the XML form of the sub folders of the specified parent container
     * where that container is in this document space.
     * This loads the container collection if not already loaded.
     * @see ContainerCollection#getAppletXMLForSubfolders
     */
    public String getAppletXMLForSubfolders(String parentContainerID, String spaceName) {

        if (this.containerCollection == null)
            loadContainerCollection();

        return this.containerCollection.getAppletXMLForSubfolders(parentContainerID, spaceName);
    }


    /**
     * Returns the (unloaded) Root Container for this document space.
     * @return the root container
     */
    public Container getRootContainer () {

        Container root = new Container ( getRootContainerID() );

        return root;
    }


    /**
     * Set the top level container for this document space.
     *
     * @since Gecko Update 2 (ProductionLink)
     * @param rootContainerID a <code>String</code> value containing the id
     * of the top-level container.
     */
    public void setRootContainerID(String rootContainerID) {
        this.rootContainerID = rootContainerID;
    }

    /**
     * Returns the id of the Root Container for this document space.
     * @return the id of the root container
     */
    public String getRootContainerID () {

        String rootID = null;

        if (this.rootContainerID == null) {

            // get the root container
            String qstrGetRootContainerID = "select doc_container_id  from pn_doc_space_has_container  where doc_space_id = " 
                                            + this.objectID + " and is_root = 1" ;

            DBBean db = new DBBean();
            try {

                // get the root container ID
                db.executeQuery (qstrGetRootContainerID);

                if (db.result.next())
                    rootID = db.result.getString("doc_container_id");


            } catch (SQLException sqle) {
            	Logger.getLogger(DocumentSpace.class).debug("DocSpace.getRootContainerID() threw an SQL exception: " + sqle);
            
            } finally {
                db.release();

            }

            this.rootContainerID = rootID;

        } // end if

        return(rootContainerID);

    } // end getRootContainerID


    /**
     * Returns a Container object for the specified container id.
     * @param containerID the id of the container to get
     * @return the container for the specified id
     * @see ContainerObjectFactory#makeObject
     */
    public Container getContainer (String containerID) {

        Container container = null;
        ContainerObjectFactory factory = new ContainerObjectFactory();

        try {
            container = (Container) factory.makeObject (containerID);
        } catch (PersistenceException pe) {
        	Logger.getLogger(DocumentSpace.class).debug("DocumentSpace.getContainer() threw a PersistenceException");
        }

        return container;

    }


    /**
     * Returns the count of objects in the specified container and all child
     * containers, not counting the containers themselves.
     * The container collection for this document space is loaded if necessary
     * @param containerID the id of the container to count objects in
     * @return the count of objects in the container
     * @see ContainerCollection#getCountObjectsInContainerTree
     */
    public int getCountObjectsInContainerTree (String containerID) {

        if (this.containerCollection == null)
            loadContainerCollection();

        return containerCollection.getCountObjectsInContainerTree(containerID);

    }


    /**
     * Returns the string representation of the path to the object with the
     * specified id.
     * @param objectID the id of the object to get the path to
     * @return the string representation of the path
     * @see DocumentManager#getPath
     */
    public String getPath (String objectID) {

        return( DocumentManager.getPath (objectID) );

    }

    /**
     * Returns a list of all the containers in this document space.
     * This is equivalent to calling <code>getDescendantContainers(getRootContainerID())</code>
     * @see #getDescendantContainers
     */
    public ArrayList getAllContainers() {
        return getDescendantContainers(getRootContainerID());
    }


    /**
     * Returns a list of containers that are descendants of the specified
     * parent container id.  It is a depth-first traversal of the container
     * hierarchy.
     * This loads the container collection if not already loaded.
     * @param parentContainerID the id of the parent container for which to
     * get descendant containers
     * @return the list of containers that are the sub folders
     * @see ContainerCollection#getContainersForParentContainer
     */
    public ArrayList getDescendantContainers(String parentContainerID) {

        if (this.containerCollection == null)
            loadContainerCollection();

        return this.containerCollection.getContainersForParentContainer(parentContainerID, ContainerCollection.TraversalOrder.DEPTH_FIRST);
    }


    /**
     * Returns a List of ContainerEntry objects which are ancestors of
     * the container with specified ID.  This includes the ContainerEntry
     * representing container with specified ID and the Root Container.
     * This loads the container collection if not already loaded.
     * @param containerID to get ancestors for
     * @return List of ContainerEntry objects
     * @see ContainerCollection#getAncestorContainers
     */
    public List getAncestorContainers(String containerID) {
        if (this.containerCollection == null) {
            loadContainerCollection();
        }

        return this.containerCollection.getAncestorContainers(containerID);
    }


    /** Copies the contents of a document space.
     * Assumes the target document space already exists and its <b>root container
     * already exits</b>.
     * @param toDocSpace document space to which to copy the contents; the
     * id of this object must be available.
     * @param currentUser the current user performing the copy
     * @throws DocumentException if there is a problem copying the doc space;
     * note that the entire doc space is processed even when an error occurs,
     * it does NOT abort on the first error
     */
    public void copyContents(DocumentSpace toDocSpace, User currentUser) throws DocumentException {

        Container fromRootContainer = this.getRootContainer();
        Container toRootContainer = toDocSpace.getRootContainer();

        // Get the collection of containers that are descendants of the root container
        // in the "from" space
        ArrayList fromContainers = this.getDescendantContainers(fromRootContainer.getID());
        
        // This map is used to figure out the new parent of a container being copied
        HashMap containerCopyMap = new HashMap(fromContainers.size());
        
        // First copy the root container's objects and initialize the map of 
        // from/to container ids with the from and to root container ids
        fromRootContainer.setUser(currentUser);
        fromRootContainer.copyObjectsIntoContainer(toRootContainer.getID());
        containerCopyMap.put(fromRootContainer.getID(), toRootContainer.getID());

        // Now copy the rest of the containers and their objects
        copyContainers(fromContainers, containerCopyMap, currentUser);

    }

        /** Copies the contents of a document space.
     * Assumes the target document space already exists and its <b>root container
     * already exits</b>.
     * @param toContainerID document space to which to copy the contents; the
     * id of this object must be available.
     * @param currentUser the current user performing the copy
     * @throws DocumentException if there is a problem copying the doc space;
     * note that the entire doc space is processed even when an error occurs,
     * it does NOT abort on the first error
     */
    public void copyContents(String fromContainerID, String toContainerID, User currentUser) throws DocumentException {

	Container fromContainer = new Container (fromContainerID);
        Container toContainer = new Container (toContainerID);

        // Get the collection of containers that are descendants of the containerID
        // in the "from" space
	ArrayList fromContainers = this.getDescendantContainers(fromContainerID);
        
        // This map is used to figure out the new parent of a container being copied
        HashMap containerCopyMap = new HashMap(fromContainers.size());
        
        // First copy the root container's objects and initialize the map of 
        // from/to container ids with the from and to root container ids
        fromContainer.setUser(currentUser);
        fromContainer.copyObjectsIntoContainer(toContainer.getID());
        containerCopyMap.put(fromContainer.getID(), toContainer.getID());

        // Now copy the rest of the containers and their objects
        copyContainers(fromContainers, containerCopyMap, currentUser);
    }




    /**
     * Copies all the containers in the specified collection using and modifying
     * the specified map for determining each new container's new parent.
     * It is assumed that the map is initialized with the parent id of the
     * first container (and the container's new parent id)
     * in the collection and that the container collection is
     * ordered in such a way that all parent containers are listed before their
     * children.  If this is not the case, then an error will occur when
     * a parent id cannot be found in the map for a new container.
     * @param fromContainers the collection of containers to copy
     * @param containerCopyMap the mapping of old and new container ids, used
     * to determine the new parent id of a new container;  the map is updated
     * as the copy proceeds.
     * @param currentUser the current user performing the copy
     * @throws DocumentException if there is a problem copying the containers
     */
    private void copyContainers(Collection fromContainers, HashMap containerCopyMap, User currentUser) throws DocumentException {

        Container fromContainer = null;
        ContainerEntry entry = null;
        String containerID = null;
        String newContainerID = null;
        String fromParentID = null;
        String toParentID = null;
        
        StringBuffer errors = new StringBuffer();
        boolean hasErrorOccurred = false;

        // Iterate over all containers, copying each container to its new
        // parent container id
        Iterator containerIt = fromContainers.iterator();
        while (containerIt.hasNext()) {
            entry = (ContainerEntry) containerIt.next();
            
            // Get the IDs needed to perform the copy
            containerID = entry.getID();
            fromParentID = entry.getContainerID();
            toParentID = (String) containerCopyMap.get(fromParentID);

            if (toParentID != null) {
           
                try {
                    // Copy the current container with id of containerID, placing it
                    // as a child of the container with id "toParentID".
                    fromContainer = new Container(containerID);
                    fromContainer.setUser(currentUser);
                    newContainerID = fromContainer.copyWithObjectsIntoContainer(toParentID);
            
                    // Now update map so that current container's child containers 
                    // may locate their newly created parent container
                    containerCopyMap.put(containerID, newContainerID);
                
                } catch (DocumentException de) {
                    // There was a problem copying objects
                    // Collect the error and continue
                    errors.append(de.getMessage() + "\n");
                    hasErrorOccurred = true;
                }
                    
            
            } else {
                // No mapping found; this means that a source container's parent
                // was not successfully copy.  We therefore skip that source
                // container
            }
            
        }
        
        // Throw any errors as exception
        if (hasErrorOccurred) {
            throw new DocumentException(errors.toString());
        }
    }
}
