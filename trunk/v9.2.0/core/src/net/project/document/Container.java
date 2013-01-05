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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.events.EventType;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.service.ServiceFactory;
import net.project.link.ILinkableObject;
import net.project.methodology.model.LinkContainer;
import net.project.methodology.model.ObjectLink;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.xml.XMLUtils;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

public class Container implements IContainerObject, Serializable, ILinkableObject, IJDBCPersistence, IXMLPersistence {
    protected String name = null;
    protected String objectID = null;
    protected String description = null;
    protected String objectType = null;
    protected String containerID = null;
    protected String containerName = null;
    protected String numObjects = null;
    protected String isSystem = "0";
    protected String modifiedByID = null;
    protected String modifiedBy = null;
    protected java.util.Date modifiedDate = null;
    protected java.util.Date crc = null;
    private RecordStatus recordStatus = null;
    private String tempRecordStatus = null;
    private boolean isLoaded = false;
    protected User user = null;
    protected ArrayList contents = null;

    private String sortBy = null;
    private String sortOrder = null;

    private int errorCode = -1;
    private boolean listDeleted = false;
    
    /**
     * A list of objects to exclude from this container.
     */
    private List excludeObjects = new ArrayList();

    /*****************************************************************************************************************
     *****                                     CONSTRUCTORS                                                                        *****
     *****************************************************************************************************************/

    /**
     * Constructs an empy Container object.
     */
    public Container() {
    }

    /**
     * Constructs a Container object with a given an object ID.
     * The objectID must represent an item which exists in the database.
     *
     * @param   objectID    objectID of container in the database
     */
    public Container(String objectID) {

        this.objectID = objectID;
        loadProperties();
    }

    /**
     * Constructs a Container object with a given an object ID.
     * The objectID must represent an item which exists in the database.
     *
     * @param   objectID    objectID of container in the database
     * @param	loadContents	 if true then load this Container's contents
     */
    public Container(String objectID, boolean loadContents) throws PersistenceException {

        this.objectID = objectID;
        loadProperties();

        if (loadContents)
            loadContents();

    }


    /*****************************************************************************************************************
     *****                                     Implementing IContainerObject                                                      *****
     *****************************************************************************************************************/

    /**
     * Get the URL for recreating this object
     *
     * @return The URL for recreating this object
     */
    public String getURL() {
        return URLFactory.makeURL(this.objectID, ObjectType.CONTAINER);
    }

    /**
     * Get the objectID of this Container
     *
     * @return The objectID of the Container
     */
    public String getID() {
        return this.objectID;
    }

    /**
     * Set the objectID of the Container
     *
     * @param objectID
     */
    public void setID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * Get the name of the Container
     *
     * @return The name of the Container
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the object
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the Parent Container of this object.  May return null if
     * this is the parent Container.
     *
     * @return The name of the Parent Container of this object
     */
    public String getContainerName() {
        return (this.containerName);
    }

    /**
     * Get the objectID of the Parent Container of this object
     *
     * @return objectID of the parent container
     */
    public String getContainerID() {
        return this.containerID;
    }

    /**
     * Set the objectID of the parent container of this object
     *
     * @param containerID
     */
    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    /**
     * Get the "type" of this object
     *
     * @return type of the container
     * @see ContainerObjectType
     */
    public String getType() {
        return this.objectType;
    }

    /**
     * Set the "type" of this object
     *
     * @param objectType
     * @see ContainerObjectType
     */
    public void setType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Tests if this object (IContainerObject) is a type of ContainerObjectType.
     *
     * @param objectType
     * @return true if this object isTypeOf objectType
     * @see ContainerObjectType
     */
    public boolean isTypeOf(String objectType) {

        boolean typeMatch = false;

        if (objectType.equals(ContainerObjectType.CONTAINER_OBJECT_TYPE))
            typeMatch = true;

        return typeMatch;
    }


    public String getDateModified() {

        String dateStr = PropertyProvider.get("prm.document.container.getdatemodified.datetimeseparator.symbol", new Object[]{SessionManager.getUser().getDateFormatter().formatDate(this.modifiedDate),
                                                                                                                              SessionManager.getUser().getDateFormatter().formatTime(this.modifiedDate)});

        return dateStr;
    }

    /**
     * Returns the current record status.
     * @return the record status
     */
    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }
    
    public void setTempRecordStatus(String id) {
        RecordStatus status = RecordStatus.findByID(id);
        if(status != null)
            tempRecordStatus = id;
    }

    /**
     * Specifies a container object to exclude from the loaded container.
     * This is useful to exclude a container when moving the container to
     * another container.
     * @param objectToExclude the object to exclude
     */
    public void setExcludeObject(IContainerObject objectToExclude) {
        this.excludeObjects.clear();
        this.excludeObjects.add(objectToExclude);
    }

    /**
     * Remove an object from this container.  NOTE, this does not remove (or soft delete)
     * the object from the system.  It simply removes the object from THIS container.
     *
     * @param objectID	Any object which implements the interface IContainerObject
     * @return boolean	True if successful, false if not
     */

    public boolean removeObject(String objectID) {

        boolean isOK = false;
        String containerID = this.objectID;


        DBBean db = new DBBean();
        try {
            db.prepareCall("begin  P_REMOVE_OBJECT_FROM_CONTAINER (?, ?); end;");

            db.cstmt.setString(1, objectID);
            db.cstmt.setString(2, containerID);
            db.executeCallable();

            isOK = true;

        } catch (SQLException sqle2) {

        	Logger.getLogger(Container.class).debug("Container.remove(): unable to execute stored procedure");
            isOK = false;

        } finally {
            db.release();
        } // end finally

        return isOK;

    } // end remove()


    /**
     * Set the User for this object
     *
     * @param user
     * @see net.project.security.User
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the User for this object
     *
     * @param userID
     * @see net.project.security.User
     */
    public void setUser(String userID) {
        this.user = new User();

        this.user.setID(userID);
        try {
            this.user.load();
        } // end try
        catch (PersistenceException pe) {
        	Logger.getLogger(Container.class).debug("Container.setUser() threw a PersistenceException");
        } // end catch

    } // setUser()

    /**
     * Indicates whether the current Container is a system-only container.
     * System containers are generally used only for special purposes, such as
     * holding a project icon.  They cannot be seen while browsing the document
     * space.
     *
     * @since Gecko Update 2 (ProductionLink)
     * @see #setIsSystem
     * @return a <code>String</code> value containing a 0 or 1 indicating whether
     * this is a system container.
     */
    public String isSystem() {
        return this.isSystem;
    }

    /**
     * Indicates that this container is a system container.  System containers
     * are used for holding special files, such as project icons.
     *
     * @since Gecko Update 2 (ProductionLink)
     * @see #isSystem
     * @param isSystem a <code>String</code> value indicating whether this is a
     * system container.  "0" is false, "1" is true.
     */
    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }

    /*****************************************************************************************************************
     *****                                     Implementing INTERFACE IJDBCPersistence                                *****
     *****************************************************************************************************************/

    /**
     * Load this container from the database.  This will load the container's properties
     * and the container's contents.
     *
     * @see #loadProperties
     * @see #loadContents
     */
    public void load() throws PersistenceException {

        loadProperties();
        loadContents();

    } // end load()

    public void store() throws PersistenceException {

        try {
            create();
        } catch (PnetException pe) {
            throw new PersistenceException("Container.store() threw an exception; " + pe, pe);
        }

    } // end store()


    /**
     * Remove this Container from the database.
     * <p>
     * If the object is a container then it is removed recursively; that is
     * all contained objects will be removed.
     * </p>
     */
    public void remove() throws PersistenceException {
       
        if (this.contents == null) 
                loadContents();

        for(Iterator iter = contents.iterator(); iter.hasNext();) {
            ContainerEntry entry = (ContainerEntry) iter.next();
            
            if(ContainerObjectType.DOCUMENT_OBJECT_TYPE.equals(entry.objectType)) {
                Document doc = new Document();
                doc.objectID = entry.objectID;
                doc.setUser(this.user);
                if(listDeleted)
                    doc.setListDeleted();
                if(recordStatus.equals(RecordStatus.DELETED) && "H".equals(tempRecordStatus)) {
                    doc.setTempRecordStatus("H");
                } else {
                    doc.setTempRecordStatus(tempRecordStatus);
                }
                doc.load();
                //sjmittal: we need to check this cos in list deleted view we get all the 
                //containers and objects beneath the top contanier recursively
                //so if and grand child has already been un deleted we just need to ignore it.
                if (doc.isLoaded())
                	doc.remove();
            } else if (ContainerObjectType.CONTAINER_OBJECT_TYPE.equals(entry.objectType)) {
                //recursively delete the container also
                Container cont = new Container(entry.objectID, false);
                cont.objectID = entry.objectID;
                cont.setUser(this.user);
                if(listDeleted)
                    cont.setListDeleted();
                if(recordStatus.equals(RecordStatus.DELETED) && "H".equals(tempRecordStatus)) {
                    cont.setTempRecordStatus("H");
                } else {
                    cont.setTempRecordStatus(tempRecordStatus);
                }
                //sjmittal: we need to check this also cos the folder might have been
                //undeleted before only because of some recursive call.
                //eg a
                //     a1
                //        a2
                //now here when un delete of a1 is called it un deletes a2 also
                //so again calling un delete of a2 when its reched by a is not needed
                //again note the in list deleted view we get all the 
                //containers and objects beneath the top contanier recursively
                //so this step is necessary
                if(cont.recordStatus.equals(RecordStatus.DELETED) || !listDeleted) {
	                cont.loadContents();
	                cont.remove();
                }
            }
        }
        
        DBBean db = new DBBean();
        
        try {
            db.prepareCall("{call DOCUMENT.REMOVE_CONTAINER(?,?,?)}");
            int index = 0;
            db.cstmt.setString(++index, objectID);
            db.cstmt.setString(++index, tempRecordStatus);
            db.cstmt.setString(++index, this.user.getID());
            db.executeCallable();

        } catch (SQLException e) {
            throw new PersistenceException("Error removing container: " + e, e);

        } finally {
            db.release();
        }

        DocumentEvent event = new DocumentEvent();
        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.containerID);
        event.setTargetObjectType(this.objectType);
        event.setTargetObjectXML(this.getXMLBody());
        
        net.project.events.DocumentEvent documentEvent = null;
        
        if(tempRecordStatus.equals(RecordStatus.ACTIVE.getID())) {
	        
            event.setCode(EventCodes.UNDO_REMOVE_CONTAINER);
	        event.setName(EventCodes.getName(EventCodes.UNDO_REMOVE_CONTAINER));
	        event.setNotes(PropertyProvider.get("prm.document.container.undoremove.event.notes") + "\"" + this.name + "\"");
	        
	        documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.CONTAINER, EventType.UNDO_REMOVE_CONTAINER);
        } else {
            
	        event.setCode(EventCodes.REMOVE_CONTAINER);
	        event.setName(EventCodes.getName(EventCodes.REMOVE_CONTAINER));
	        event.setNotes(PropertyProvider.get("prm.document.container.remove.event.notes") + "\"" + this.name + "\"");
	        
	        documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.CONTAINER, EventType.FOLDER_DELETED);
        }
        event.setUser(this.user);
        event.store();
        
		// publishing event to asynchronous queue
        try {
        	if(this.isSystem().equals("0")){
	        	documentEvent.setObjectID(objectID);
	        	documentEvent.setObjectType(ObjectType.CONTAINER);
	        	documentEvent.setName(this.name);
	        	documentEvent.setObjectRecordStatus("D");
	        	documentEvent.publish();
        	}
		} catch (EventException e) {
			Logger.getLogger(Container.class).error("Container.Document Deletion Event Publishing Failed!"+ e.getMessage());
		}
    }


    /*****************************************************************************************************************
     *****                           Implementing other getter/setter methods                                               *****
     *****************************************************************************************************************/

    /**
     * Set the meta-data description for this container
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    public String getModifiedBy() {
        return this.modifiedBy;
    }

    /**
     * Get the description of this Container
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Returns the number of objects contained by this container.
     * NOTE: this call is NOT recursive
     */
    public String getNumObjects() {
        return this.numObjects;
    }

    /**
     * Gets an ArrayList of the objects contained by this container
     * @return ArrayList
     */
    public ArrayList getContents() {
        return contents;
    }

    /**
     * Returns an ArrayList of the objects of type "ContainerObjectType" contained by this container
     *
     * @param objectType	 the ContainerObjectType of this object
     * @return ArrayList
     */
    public ArrayList getObjects(String objectType) {

        ArrayList objectList = new ArrayList();
        Iterator iterator = contents.iterator();

        IContainerEntry tmpObj;

        while (iterator.hasNext()) {

            tmpObj = (IContainerEntry) iterator.next();

            if (objectType.equals(tmpObj.getType()))
                objectList.add(tmpObj);

        } // end while

        return objectList;
    } // end getObjects


    /**
     * Returns an ArrayList of all objects of type "document" contained by this container
     *
     * @return ArrayList
     */
    public ArrayList getDocuments() {

        ArrayList documentList = null;

        documentList = getObjects(ContainerObjectType.DOCUMENT_OBJECT_TYPE);

        return documentList;
    } // end getDocuments();


    /**
     * Returns an ArrayList of all objects of type "container" contained by this container
     *
     * @return ArrayList
     */
    public ArrayList getContainers() {

        ArrayList containerList = null;

        containerList = getObjects(ContainerObjectType.CONTAINER_OBJECT_TYPE);

        return containerList;

    } //   end getContainers();


    /**
     * Return an IContainerObject (specified by objectID) from this container.
     *
     * @param objectID	The object ID of the element you want
     * @return IContainerObject
     */
    public IContainerObject getElement(String objectID) {

        IContainerObject co = null;

        try {
            ContainerObjectFactory factory = null;
            if(listDeleted == true)
                factory = new ContainerObjectFactory(true);
            else
                factory = new ContainerObjectFactory();
            co = factory.makeObject(objectID);
        } catch (PersistenceException pe) {

            // PnDebug.log("Container.getElement() threw a persistence exception: ", true);
            Logger.getLogger(DocumentManager.class).debug("Container.getElement() failed when trying to load the object: " + pe);

        }

        return co;

    } // end getElement


    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    /*****************************************************************************************************************
     *                                      Implementing create / load methods                                                      *
     ******************************************************************************************************************
     * public boolean create();
     * public boolean loadProperties();
     * public booelan loadContents();
     * public boolean add (IContainerObject containerObject, String containerID);
     * public boolean add (IContainerObject containerObject);
     *****************************************************************************************************************/

    public String create() throws PnetException {

        DocumentEvent event = new DocumentEvent();
        String newContainerID = null;

        DBBean db = new DBBean();
        try {
            // create an executable JDBC statement
            db.prepareCall("begin  DOCUMENT.CREATE_CONTAINER (?,?,?,?,?,?,?,?); end;");

            db.cstmt.setString(1, this.containerID);
            db.cstmt.setString(2, this.user.getID());
            db.cstmt.setString(3, this.name);
            db.cstmt.setString(4, this.description);
            db.cstmt.setString(5, this.user.getCurrentSpace().getID());
            db.cstmt.setString(6, this.isSystem());
            db.cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);

            db.executeCallable();
            newContainerID = db.cstmt.getString(7);
            this.errorCode = db.cstmt.getInt(8);

        } // end try
        catch (SQLException sqle) {

            // PnDebug.display("Container.create() threw an SQL exception: " + sqle);
        	Logger.getLogger(Container.class).debug("Container.create() threw an SQL exception: " + sqle);
            throw new PersistenceException(" Container.create()::Document Creation Operation Failed!", sqle);

        } // end catch
        finally {
            db.release();
        }

        // Handle (throw) any database exceptions
        try {
            DBExceptionFactory.getException("Container.create()", this.errorCode);
        } catch (net.project.base.UniqueNameConstraintException une) {
        	//bug 2978
            //throw new net.project.base.PnetException(PropertyProvider.get("prm.document.container.error.uniquename.message"), une);
        	net.project.base.PnetException ex = 
        		new net.project.base.PnetException(
        				PropertyProvider.get("prm.document.container.error.uniquename.message"),
        				une);
        	ex.setReasonCode(-100);
        	throw ex;
        }

        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.containerID);
        event.setTargetObjectType(this.objectType);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.CREATE_CONTAINER);
        event.setName(EventCodes.getName(EventCodes.CREATE_CONTAINER));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.container.create.event.notes") + "\"" + this.name + "\"");

        event.store();
        
        // publishing event to asynchronous queue
        try {
        	if(this.isSystem().equals("0")){
	        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.CONTAINER, EventType.FOLDER_CREATED);
	        	documentEvent.setObjectID(newContainerID);
	        	documentEvent.setObjectType(ObjectType.CONTAINER);
	        	documentEvent.setName(this.name);
	        	documentEvent.setObjectRecordStatus("A");
	        	documentEvent.publish();
        	}
		} catch (EventException e) {
			Logger.getLogger(Container.class).error(" Container.create()::Document Creation Event Publishing Failed!", e);
		}

        return (newContainerID);

    } // end create()


    public void modify() throws PnetException {

        DocumentEvent event = new DocumentEvent();

        DBBean db = new DBBean();
        try {
            // create an executable JDBC statement
            db.prepareCall("begin  DOCUMENT.MODIFY_CONTAINER (?,?,?,?,?,?,?,?); end;");

            db.cstmt.setString(1, this.containerID);
            db.cstmt.setString(2, this.objectID);
            db.cstmt.setString(3, this.name);
            db.cstmt.setString(4, this.description);
            db.cstmt.setString(5, this.user.getID());
            db.cstmt.setString(6, this.isSystem());
            Timestamp stamp = new Timestamp(this.crc.getTime());
            db.cstmt.setTimestamp(7, stamp);
            db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);

            db.executeCallable();
            this.errorCode = db.cstmt.getInt(8);

        } catch (SQLException sqle) {

            // PnDebug.display("Container.modify() threw an SQL exception: " + sqle);
        	Logger.getLogger(Container.class).debug("Container.modify() threw an SQL exception: " + sqle);
            throw new PersistenceException("An Attempt to modify a document failed!", sqle);

        } finally {
            db.release();
        }

        // Handle (throw) any database exceptions
        DBExceptionFactory.getException("Container.modify()", this.errorCode);

        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.objectType);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.MODIFY_PROPERTIES);
        event.setName(EventCodes.getName(EventCodes.MODIFY_PROPERTIES));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.container.modify.event.notes") + "\"" + this.name + "\"");

        event.store();
        
		// publishing event to asynchronous queue
        try {
        	if(this.isSystem().equals("0")){
	        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.CONTAINER, EventType.EDITED);
	        	documentEvent.setObjectID(this.objectID);
	        	documentEvent.setObjectType(ObjectType.CONTAINER);
	        	documentEvent.setName(this.name);
	        	documentEvent.setObjectRecordStatus("A");
	        	documentEvent.publish();
        	}	
		} catch (EventException e) {
			Logger.getLogger(Container.class).error(" Container.modify()::Document Modify Event Publishing Failed!", e);
		}

    } // end modify()

    public void loadProperties() {

        String qstrLoadContainerProperties = "select container.doc_container_id, container.container_name, " +
                "container.container_description, count.num_objects, " +
                "container.date_modified as date_modified, container.modified_by_id, person.display_name as modified_by, container.crc, " +
                "container.record_status " +
                "from pn_doc_container container, pn_person person, " +
                "(select count(object_id) as num_objects from pn_doc_container_has_object where doc_container_id = " + this.objectID + ") count " +
                "where container.doc_container_id = " + this.objectID +
                " and person.person_id = container.modified_by_id";


        DBBean db = new DBBean();
        try {
            // get name, objectID and description
            db.executeQuery(qstrLoadContainerProperties);

            if (db.result.next()) {

                this.name = PropertyProvider.get(db.result.getString("container_name"));
                this.objectID = db.result.getString("doc_container_id");
                this.description = db.result.getString("container_description");
                this.numObjects = db.result.getString("num_objects");
                this.modifiedBy = db.result.getString("modified_by");
                this.modifiedByID = db.result.getString("modified_by_id");
                this.modifiedDate = db.result.getTimestamp("date_modified");
                this.crc = db.result.getTimestamp("crc");
                this.recordStatus = RecordStatus.findByID(db.result.getString("record_status"));
                
                this.isLoaded = true;
            } // end if

            this.objectType = ObjectType.CONTAINER;

        } catch (SQLException sqle) {
            // PnDebug.log("Container.loadProperties() threw an SQL exception: " + sqle, true);
        	Logger.getLogger(Container.class).info("Container.loadProperties() threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }

        // seperated this because an SQL exception here is OK...

        try {

            db.prepareCall("{ ? = call DOCUMENT.GET_PARENT_CONTAINER_INFO (?) }");

            db.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            db.cstmt.setString(2, this.objectID);

            db.executeCallable();

            // in this case, the rowtype is from pn_doc_container
            db.result = (java.sql.ResultSet) db.cstmt.getObject(1);

            if (db.result.next()) {

                this.containerName = PropertyProvider.get(db.result.getString("container_name"));
                this.containerID = db.result.getString("doc_container_id");

            } // end  if

        } // end try
        catch (SQLException sqle) {
            // do nothing
        } // end catch
        finally {
            db.release();
        } // end finally

    } // end loadProperties()

    public void setListDeleted() {
        this.listDeleted = true;
    }
    
    public void unSetListDeleted() {
        this.listDeleted = false;
    }


    /**
     * Loads all of the objects in this container.
     * @throws PersistenceException if there is a problem loading the objects
     */
    public void loadContents() throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("select object_id, object_type, name, format,app_icon_url, url, ");
        query.append("version, is_checked_out, checked_out_by,status,");
        query.append("checked_out_by_id, author, initcap (to_char(date_modified, 'MON DD, YYYY')) as date_modified_string, ");
        if(!listDeleted)
            query.append(" date_modified , file_size, doc_container_id, short_file_name,has_links,");
        else {
            query.append(" (select name from pn_doc_del_container_list_view pv where pv.object_id = v.doc_container_id) AS parentname,");
            query.append(" date_modified , modified_by, file_size, doc_container_id, short_file_name,has_links,");
        }
        query.append("has_workflows, has_discussions, description, comments");
        if(!listDeleted)
            query.append(" from pn_doc_container_list_view ");
        else
            query.append(" from pn_doc_del_container_list_view v");

        if(!listDeleted) {
            query.append(" where doc_container_id = " + this.objectID + " and is_hidden = 0 ");
        }
        else  {
            query.append(" where doc_container_id in (select doc_container_id from pn_doc_container_has_object start with doc_container_id = ");
            query.append( this.objectID + " connect by doc_container_id = prior object_id) " +" and is_hidden = 0 ");
        }

        if (!this.excludeObjects.isEmpty()) {

            // Add in any excluded objects to the query

            query.append("and object_id not in (");

            boolean isAfterFirst = false;
            for (Iterator it = this.excludeObjects.iterator(); it.hasNext();) {
                IContainerObject nextObject = (IContainerObject) it.next();

                if (isAfterFirst) {
                    query.append(", ");
                }
                query.append(nextObject.getID());

                isAfterFirst = true;
            }

            query.append(") ");
        }

        loadContents(query.toString());

    } // end loadContents()


    /**
     * Load all containers maintained by this container from the database.
     */
    public void loadContainers() throws PersistenceException {

        String qstrLoadContainerObjects = "select object_id, object_type, name, format,app_icon_url, url, " +
                "version, is_checked_out, checked_out_by,status," +
                "checked_out_by_id, author, initcap (to_char(date_modified, 'MON DD, YYYY')) as date_modified_string, " +
                " date_modified , file_size, doc_container_id, short_file_name,has_links," +
                "has_workflows, has_discussions, description, comments " +
                " from pn_doc_container_list_view " +
                " where doc_container_id = " + this.objectID + " and is_hidden = 0 " +
                " and object_type = '" + ContainerObjectType.CONTAINER_OBJECT_TYPE + "'";

        loadContents(qstrLoadContainerObjects);

    }


    /**
     * Loads all objects in this container, excluding containers themselves.
     */
    public void loadNonContainers() throws PersistenceException {

        String qstrLoadContainerObjects = "select object_id, object_type, name, format,app_icon_url, url, " +
                "version, is_checked_out, checked_out_by,status," +
                "checked_out_by_id, author, initcap (to_char(date_modified, 'MON DD, YYYY')) as date_modified_string, " +
                " date_modified , file_size, doc_container_id, short_file_name,has_links," +
                "has_workflows, has_discussions, description, comments " +
                " from pn_doc_container_list_view " +
                " where doc_container_id = " + this.objectID + " and is_hidden = 0 " +
                " and object_type <> '" + ContainerObjectType.CONTAINER_OBJECT_TYPE + "'";

        loadContents(qstrLoadContainerObjects);

    }


    /**
     * Loads the contents of this container using the specified query.
     */
    private void loadContents(String query) throws PersistenceException {

        // list of ContainerEntry objects
        ArrayList elements = new ArrayList();
        ContainerEntry entry = null;
        ContainerEntrySort sorter = new ContainerEntrySort();

        DBBean db = new DBBean();
        try {
            sorter.setSortBy(this.sortBy);
            sorter.setSortOrder(this.sortOrder);

            // get the contents of the container
            // will sort by name ASC by default
            db.executeQuery(sorter.makeSortedDBQuery(query));

            while (db.result.next()) {

                // collect all of the object entries for this container
                entry = new ContainerEntry();

                entry.objectID = db.result.getString("object_id");
                entry.containerID = this.objectID;
                entry.objectType = db.result.getString("object_type");
                entry.name = db.result.getString("name");
                if(listDeleted) {
                	entry.parentName = db.result.getString("parentname");
                }
                entry.format = PropertyProvider.get(db.result.getString("format"));
                entry.appIconURL = db.result.getString("app_icon_url");
                entry.url = db.result.getString("url");
                if (db.result.getInt("version") != 0)
                    entry.version = db.result.getString("version");
                entry.isCko = db.result.getString("is_checked_out");
                if (db.result.getInt("checked_out_by_id") != 0)
                    entry.ckoByID = db.result.getString("checked_out_by_id");
                entry.ckoBy = db.result.getString("checked_out_by");
                entry.status = PropertyProvider.get(db.result.getString("status"));
                entry.author = db.result.getString("author");
                entry.setLastModifiedDate(db.result.getTimestamp("date_modified"));
                if(listDeleted)
                    entry.lastModifiedBy = db.result.getString("modified_by");
                if (db.result.getInt("file_size") != 0)
                    entry.size = db.result.getString("file_size");
                entry.shortFileName = db.result.getString("short_file_name");
                entry.hasLinks = db.result.getString("has_links");
                entry.hasDiscussions = db.result.getString("has_discussions");
                entry.hasWorkflows = db.result.getString("has_workflows");
                entry.description = db.result.getString("description");
                entry.comments = db.result.getString("comments");

                elements.add(entry);

            } // end while
        } catch (SQLException sqle) {
            //PnDebug.display("Container.loadContents() threw an SQL exception: " + sqle);
        	Logger.getLogger(Container.class).error("Container.loadContents() threw an SQL exception: " + sqle);
            throw new PersistenceException("Container load operation failed!", sqle);
        } finally {
            db.release();
        } // end finally

        contents = elements;
    }


    /**
     * Add an object to a container.  This method is used when you don't want to add an object to THIS container.
     * The object MUST already exist in the database
     *
     * @param containerObject	Any object which implements the interface IContainerObject
     * @param containerID	The container ID of the container you want to add the object to
     * @return boolean	True if successful, false if not
     */

    public boolean add(IContainerObject containerObject, String containerID) {

        boolean isOK = false;
        String objectID = containerObject.getID();

        DBBean db = new DBBean();
        try {
            // Create a callable statement
            db.prepareCall("begin  DOCUMENT.ADD_OBJECT_TO_CONTAINER (?, ?); end;");

            db.cstmt.setString(1, objectID);
            db.cstmt.setString(2, containerID);

            db.executeCallable();

            isOK = true;
        } catch (SQLException sqle2) {

            // PnDebug.log("Container.add(): unable to execute stored procedure", true);
        	Logger.getLogger(Container.class).info("Container.add(): unable to execute stored procedure");
            isOK = false;

        } finally {
            db.release();
        } // end finally

        return isOK;

    } // end add()


    /**
     * Add an object to THIS container.
     * The object MUST already exist in the database
     *
     * @param containerObject	Any object which implements the interface IContainerObject
     * @return boolean	True if successful, false if not
     */

    public boolean add(IContainerObject containerObject) {

        return add(containerObject, this.objectID);

    } // end add()




    /*****************************************************************************************************************
     *****                                 Implementing redering methods                                                         *****
     *****************************************************************************************************************/

    /**
     * Returns the XML representation of this Container, including the
     * XML version tag.
     * @return the XML representation
     */
    public String getXML() {
        return getXML(false, null);
    }

    /**
     * Returns the XML representation of this Container not including the
     * XML version tag.
     * @return the XML representation
     */
    public String getXMLBody() {
        return getXMLBody(false, null);
    }

    /**
     * Returns the XML representation of this Container tailored for the applet.
     * The XML includes the XML version tag.  This is equivalent to calling
     * <code>getXML(true, spaceName)</code>.
     * @param spaceName the spaceName to use for representing the container
     * name of root containers (which otherwise don't have a name)
     * @return the XML representation
     */
    public String getAppletXML(String spaceName) {
        return getXML(true, spaceName);
    }

    /**
     * Returns the XML representation of this Container, indicating
     * whether to tailor for the applet.
     * The XML includes the XML version tag.
     * @param isApplet true means the XML is tailored for the applet
     * @param spaceName the spaceName to use for representing the container
     * name of root containers (which otherwise don't have a name)
     * @return the XML representation
     */
    public String getXML(boolean isApplet, String spaceName) {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append(getXMLBody(isApplet, spaceName));

        return xml.toString();
    } // getXML()


    /**
     * Returns the XML representation of this Container indicating whether
     * to tailor for the applet.  The XML does not include the XML version
     * tag.
     * @param isApplet true means the XML is tailored for the applet
     * @param spaceName the spaceName to use for representing the container
     * name of root containers (which otherwise don't have a name)
     * @return the XML representation
     */
    public String getXMLBody(boolean isApplet, String spaceName) {
        StringBuffer xml = new StringBuffer();

        xml.append("<container>\n");

        if (!isApplet) {
            xml.append("<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>\n");
            xml.append(getXMLProperties());
        }

        try {
            xml.append(getXMLContents(isApplet, spaceName));

        } catch (DocumentException de) {
            // PnDebug.display("Container.getXML(): unable to load contents of this container: " + de);
        	Logger.getLogger(Container.class).debug("Container.getXML(): unable to load contents of this container: " + de);

        }

        xml.append("</container>");

        return xml.toString();
    } // getXMLBody()


    /**
     * Get a XML representation of all of the contents of this container
     * Convenience method
     *
     * @return xmlString
     */
    public String getXMLContents() throws DocumentException {
        return getXMLContents(false, null);
    }

    /**
     * Get a XML representation of all of the contents of this container
     *
     * @return xmlString
     */

    public String getXMLContents(boolean isApplet, String spaceName) throws DocumentException {

        StringBuffer xml = new StringBuffer();
        Iterator iterator = null;
        ContainerEntry entry;

        if (this.contents == null) {
            try {
                loadContents();
            } catch (PersistenceException pe) {
                throw new DocumentException("Container.getXMLContents() unable to load the contents of this container: " + pe);
            }
        } // end if


        iterator = this.contents.iterator();

        xml.append("<container_contents>\n");

        //Add the entry for the current folder.  We need this so we can set
        //permissions for the top folder.
        ContainerEntry thisFolder = ContainerObjectFactory.makeEntryFromContainer(this);
        xml.append(thisFolder.getXML());

        // output the columns of the container
        if (!isApplet)
            xml.append(ContainerEntrySort.getXML());

        // get XML for all of the contents of this container

        while (iterator.hasNext()) {

            entry = (ContainerEntry) iterator.next();

            if (isApplet)
                xml.append(entry.getAppletXML(spaceName));
            else
                xml.append(entry.getXML());

        } // end while

        xml.append("</container_contents>\n");

        return (xml.toString());
    } // end getContentsXML()


    /**
     * Get a XML representation of all of this container
     *
     * @return xmlString
     */
    public String getXMLProperties() {

        StringBuffer xml = new StringBuffer();
        String tab = null;

        tab = "\t";
        xml.append(tab + "<container_properties>\n");

        tab = "\t\t";
        Path path = new Path(this);
        xml.append(path.getXMLBody());
        xml.append(tab + "<object_type>" + XMLUtils.escape(this.objectType) + "</object_type>\n");
        xml.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");
        xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append(tab + "<parent_container_id>" + XMLUtils.escape(this.containerID) + "</parent_container_id>\n");
        xml.append(tab + "<parent_container_name>" + XMLUtils.escape(this.containerName) + "</parent_container_name>\n");
        xml.append(tab + "<modified_by>" + XMLUtils.escape(this.modifiedBy) + "</modified_by>\n");
        xml.append(tab + "<modified_by_id>" + XMLUtils.escape(this.modifiedByID) + "</modified_by_id>\n");
        xml.append(tab + "<date_modified>" +
                XMLUtils.formatISODateTime(this.modifiedDate) + "</date_modified>\n");
        xml.append(tab + "<num_objects>" + XMLUtils.escape(this.numObjects) + "</num_objects>\n");
        xml.append(tab + "<app_url>" + XMLUtils.escape(SessionManager.getAppURL()) + "</app_url>\n");

        tab = "\t";
        xml.append(tab + "</container_properties>\n\n");

        return xml.toString();
    } // end getXML()


    /*****************************************************************************************************************
     *****                                                  Utility Methods                                                                  *****
     *****************************************************************************************************************/


    public boolean isLoaded() {
        return this.isLoaded;
    }

    public boolean isRoot() {
        return (this.containerID == null);
    }

    public String toString() {
        IContainerObject tmpObj;
        Iterator iterator = null;
        String str = "";
        int count = 0;
        try{
	        iterator = contents.iterator();
	        while (iterator.hasNext()) {
	            tmpObj = (IContainerObject) iterator.next();
	            count++;
	            str = str + count + "  " + tmpObj.getType() + "  " + tmpObj.getName() + "  " + tmpObj.getID() + "\n";
	        } // end while
        }catch (Exception e) {
        	Logger.getLogger(Container.class).info("Container.toString(): unable to convert in string");
        	return str;
		}
        return str;
    } // end toString()


    /**
     * Copies this container and its objects only (that is, not its child
     * containers) such that the new copy is a child of the container with
     * specified id. <br>
     * This is used when copying a container and its parent has already been
     * copied and its id is specified here.
     * @param newParentContainerID the id of the container that the copy must be
     * a child of
     * @return the id of the copied container
     * @throws DocumentException if there is a problem copying the
     * container and its contents
     */
    protected String copyWithObjectsIntoContainer(String newParentContainerID) throws DocumentException {

        DBBean db = new DBBean();
        String newContainerID = null;

        try {
            // Copy the container such that it is contained by container with id
            // of newParentContainerID
            newContainerID = ContainerObjectFactory.makeCopier(ContainerObjectType.CONTAINER_OBJECT_TYPE).copy(db, getID(), newParentContainerID, this.user);

            // Ensure copy of container is completed since we must now copy
            // this container's objects into the new container
            db.commit();

            // Now copy all objects in this container into the new copy of
            // this container
            copyObjectsIntoContainer(newContainerID);

        } catch (SQLException sqle) {
        	Logger.getLogger(Container.class).error("Container.copyWithObjectsToParent threw an SQLException: " + sqle);
            throw new DocumentException("Error copying container: " + sqle, sqle);

        } catch (PersistenceException pe) {
            throw new DocumentException("Error copying container: " + pe, pe);

        } finally {
            db.release();

        }

        return newContainerID;
    }


    /**
     * Copies objects using specified DBBean.
     * @param targetParentContainerID the id of the container which is the parent
     * of each of the copied objects from this container
     * @throws DocumentException if there is a problem copying any of the objects
     * in this container.  Note that this method attempts to copy ALL the objects
     * in the container - it gathers errors that occur while copying
     */
    protected void copyObjectsIntoContainer(String targetParentContainerID)
            throws DocumentException {

        StringBuffer errors = new StringBuffer();
        boolean errorOccurred = false;

        DBBean db = new DBBean();

        try {
            // Load the objects in this container
            loadNonContainers();

            // Ensure a connection is open and auto-commit is OFF
            db.openConnection();
            db.setAutoCommit(false);

            // Iterate over all objects in this container
            // Copy each object, committing or rolling-back after each copy
            // This is necessary since each copy may involve multiple updates
            // in the database;  if a transaction is not rolled-back in the
            // event of an error in any part, then partial data may be committed.
            // If we might rollback after any copy, then we must also commit
            // after any copy to ensure that successful copies aren't rolled-back
            // as the result of a later erroneous copy

            Iterator objectIt = getContents().iterator();
            while (objectIt.hasNext()) {
                ContainerEntry entry = (ContainerEntry) objectIt.next();

                try {
                    // Copy the object such that is contained by container with id
                    // of newContainerID (that is, the container just copied)
                    ICopier copier = ContainerObjectFactory.makeCopier(entry.objectType);
                    String newObjectId = copier.copy(db, entry.getID(), targetParentContainerID, this.user);
					// Now commit all changes
                    db.commit();
                    List<PnObjectLink> result = ServiceFactory.getInstance().getPnObjectLinkService().getObjectLinksByParent(Integer.valueOf(entry.getID()));
                    List<ObjectLink> links = new ArrayList<ObjectLink>();
					for (PnObjectLink p : result) {
						ObjectLink ol = new ObjectLink();
						ol.setContext(p.getComp_id().getContext());
						ol.setFromObjectIdOld(p.getComp_id().getFromObjectId());
						ol.setFromObjectIdNew(Integer.valueOf(newObjectId));
						ol.setToObjectIdOld(p.getComp_id().getToObjectId());
						links.add(ol);
					}
					
					List<PnObjectLink> resultTo = ServiceFactory.getInstance().getPnObjectLinkService().getObjectLinksByChild(Integer.valueOf(entry.getID()));
					for (PnObjectLink p : resultTo) {
						ObjectLink ol = new ObjectLink();
						ol.setContext(p.getComp_id().getContext());
						ol.setFromObjectIdOld(p.getComp_id().getFromObjectId());
						ol.setToObjectIdOld(p.getComp_id().getToObjectId());
						ol.setToObjectIdNew(Integer.valueOf(newObjectId));
						links.add(ol);
					}
					
					LinkContainer.getInstance().setLinks(LinkContainer.DOCUMENT,links);
					
					//List<ObjectLink> temp = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
					
                } catch (PersistenceException pe) {
                    // Database problem copying
                    // Note the error but continue with the next object
                    errors.append(pe.getMessage() + "\n");
                    errorOccurred = true;

                } catch (CopyException ce) {
                    // There was a problem copying a specific object.
                    // Note the error but continue with the next object
                    errors.append(ce.getMessage() + "\n");
                    errorOccurred = true;

                } finally {
                    try {
                        db.rollback();
                    } catch (SQLException sqle) {
                        // A problem occurred rolling back
                        // Ignore and continue
                    }

                }

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Container.class).error("Container.copyObjectsIntoContainer() threw a SQLException: " + sqle);
            throw new DocumentException("Error copying container contents: " + sqle, sqle);

        } catch (PersistenceException pe) {
            throw new DocumentException("Error copying container contents: " + pe, pe);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Do nothing; most likely another SQLException will have occurred
            }

            db.release();
        }

        // If a problem occurred, throw a DocumentException with all the error
        // messages
        if (errorOccurred) {
            throw new DocumentException(errors.toString());
        }

    }
    
    
    public boolean getContainerInfo(String contID, String contName) {

    	boolean isFound = false;
        String qstrLoadContainerProperties = "select 1 " +
                "from pn_doc_container " +
                "where doc_container_id in " +
                "(select object_id from pn_doc_container_has_object where doc_container_id = " + contID + ")" +
                " and container_name = '"+ contName +"'"+
                " and record_status = 'A'";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadContainerProperties);
            if (db.result.next()) {
            	isFound = true;
            }
        } catch (SQLException sqle) {
            // PnDebug.log("Container.loadProperties() threw an SQL exception: " + sqle, true);
        	Logger.getLogger(Container.class).info("Container.getContainerInfo() threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }
        return(isFound);
    }

    public String getContainerID(String parentContID, String contName) {

    	String contID = null;
        String qstrLoadContainerProperties = "select doc_container_id " +
                "from pn_doc_container " +
                "where doc_container_id in " +
                "(select object_id from pn_doc_container_has_object where doc_container_id = " + parentContID + ")" +
                " and container_name = '"+ contName +"'"+
                " and record_status = 'A'";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadContainerProperties);
            if (db.result.next()) {
            	contID = db.result.getString("doc_container_id");
            }
        } catch (SQLException sqle) {
            // PnDebug.log("Container.loadProperties() threw an SQL exception: " + sqle, true);
        	Logger.getLogger(Container.class).info("Container.getContainerInfo() threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }
        return(contID);
    }
    
    public String getContainerID(String contName) {

    	String contID=null;
        String qstrLoadContainerProperties = "select doc_container_id " +
                "from pn_doc_container " +
                "where container_name = '"+ contName +"'"+
                " and record_status = 'A'";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadContainerProperties);
            if (db.result.next()) {
            	contID = db.result.getString("doc_container_id");
            }
        } catch (SQLException sqle) {
            // PnDebug.log("Container.loadProperties() threw an SQL exception: " + sqle, true);
        	Logger.getLogger(Container.class).info("Container.getContainerInfo() threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }
        return(contID);
    }
    
} // end class Container
