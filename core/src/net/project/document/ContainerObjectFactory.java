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

import net.project.base.ObjectType;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;


class ContainerObjectFactory implements java.io.Serializable {
    
    private boolean listDeleted = false;

    public ContainerObjectFactory() {
        // do nothing
    }

    public ContainerObjectFactory(boolean listDeleted) {
        this.listDeleted = listDeleted;
    }

    public IContainerObject makeObject(String objectID, String objectType) throws PersistenceException {

        IContainerObject tmpObj = null;
        ContainerObjectType coType = new ContainerObjectType();
//Avinash:-- Handling for null objectType-------------------
        if(objectType!=null){
//Avinash:--------------------------------------------------	
        if (objectType.equals(coType.DOCUMENT_OBJECT_TYPE))
            tmpObj = makeDocument(objectID);

        else if (objectType.equals(coType.CONTAINER_OBJECT_TYPE))
            tmpObj = makeContainer(objectID);

        else if (objectType.equals(coType.BOOKMARK_OBJECT_TYPE))
            tmpObj = makeBookmark(objectID);

        else
            tmpObj = null;
//Avinash:--------------------------------------------------
        }
//Avinash:--------------------------------------------------
        return tmpObj;

    }
    
    public IContainerObject makeObject(String objectID) throws PersistenceException {

        DocumentManagerBean dm = net.project.security.SessionManager.getDocumentManager();
        String objectType = dm.getType(objectID);

        return (makeObject(objectID, objectType));

    }


    public static ContainerEntry makeEntryFromContainer(Container container) {
        ContainerEntry entry = new ContainerEntry();
        entry.objectID = container.objectID;
        entry.objectType = ObjectType.CONTAINER;
        entry.name = container.name;
        entry.isRoot = false;  // Don't know how to check this one

        return entry;
    }

    /**
     * Returns a copier object for copying objects of the specified type.
     * @param objectType the object type for which to make a copier
     * @throws DocumentException if the objectType is unknown
     * @see ContainerObjectType for object type constants
     */
    public static ICopier makeCopier(String objectType) throws DocumentException {
        ICopier copier = null;

        if (objectType.equals(ContainerObjectType.CONTAINER_OBJECT_TYPE)) {
            copier = new ContainerCopier();

        } else if (objectType.equals(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {
            copier = new DocumentCopier();

        } else if (objectType.equals(ContainerObjectType.BOOKMARK_OBJECT_TYPE)) {
            copier = new BookmarkCopier();

        } else {
        	Logger.getLogger(ContainerObjectFactory.class).debug("ContainerObjectFactory.makeCopier: Unhandled object type: " + objectType);
            throw new DocumentException("Unable to copy objects of type " + objectType);

        }

        return copier;
    }


    /*****************************************************************************************************************
     *****                                               Private Methods                                                                  *****
     *****************************************************************************************************************/

    private DocumentBean makeDocument(String objectID) throws PersistenceException {

        DocumentBean document = new DocumentBean();
        if(listDeleted)
            document.setListDeleted();

        document.setID(objectID);
        document.load();

        return (document);

    }

    private BookmarkBean makeBookmark(String objectID) throws PersistenceException {

        BookmarkBean bookmark = new BookmarkBean();

        bookmark.setID(objectID);
        bookmark.load();

        return (bookmark);

    }


    private Container makeContainer(String objectID) throws PersistenceException {

        Container container = new Container();
        if(listDeleted)
            container.setListDeleted();

        container.setID(objectID);
        container.loadProperties();

        return (container);
    }

} // end class ContainerObjectFactory
