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

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

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
import net.project.link.ILinkableObject;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.util.ErrorLogger;
import net.project.workflow.IWorkflowable;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

public class Document implements ILinkableObject, IContainerObject, IDocument, IManagedObject, IJDBCPersistence, IXMLPersistence, Serializable, IWorkflowable {
    protected static final String DATE_FORMAT_STRING = "M/d/y";

    protected String name = null;
    protected String objectID = null;
    protected String objectType = null;
    protected String containerID = null;
    protected String containerName = null;
    protected String description = null;
    protected String notes = null;
    private RecordStatus recordStatus = null;
    private String tempRecordStatus = null;
    protected String author = null;
    protected String authorID = null;
    protected String type = null;
    protected String typeID = null;
    protected String status = null;
    protected String statusID = null;
    protected String availability = null;
    protected String availabilityID = null;
    protected String OSFilePath = null;
    protected String fileSize = null;
    protected String fileFormat = null;
    protected String fileFormatID = null;
    protected String mimeType = null;
    protected String appIconURL = null;
    protected boolean isCko = false;
    protected String ckoByID = null;
    protected String ckoBy = null;
    protected java.util.Date ckoDate = null;
    protected java.util.Date ckoReturn = null;
    protected String fileHandle = null;
    protected String repositoryBaseID = null;
    protected String repositoryBasePath = null;
    protected String currentVersionID = null;
    protected String numTimesViewed = null;
    protected String versionNum = null;
    protected String versionLabel = null;
    protected String shortFileName = null;
    protected java.util.Date creationDate = null;
    protected java.util.Date lastModified = null;
    protected String modifiedByID = null;
    protected String contentID = null;
    protected String historyID = null;
    protected java.util.Date crc = null;
    private String activeWorkflows = null;
    private String discussionCount = null;

    private boolean listDeleted = false;
    protected boolean ignoreNameConstraint = false;

    /**
     * Indicates whether to ignore user mismatch constraints on check in and
     * undo check out
     */
    private boolean ignoreUserMismatchConstraint = false;

    private boolean isLoaded = false;
    private String tmpRowID = null;

    protected ArrayList links = null;
    public VersionCollection versions = null;
    public EventCollection events = null;

    private int errorCode = -1;
    private User user = null;

    public static final String STATUS_IN_PROCESS = "200";
    public static final String STATUS_COMPLETE = "400";
    public static final String TYPE_GENERAL = "100";

    private boolean isWikiDocument = false;
    
    /**
     * **************************************************************************************************************
     * ****                                          CONSTRUCTORS *****
     * ***************************************************************************************************************
     */

    public Document() {
    }

    public Document(String objectID) throws PersistenceException {

        // set the objectID
        this.objectID = objectID;

        // load the object from the database
        load();
    }

    /**
     * **************************************************************************************************************
     * ****                                     Implementing INTERFACE
     * IContainerObject                                 *****
     * ***************************************************************************************************************
     */

    public void setID(String objectID) {
        this.objectID = objectID;
    }

    public String getID() {
        return this.objectID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getContainerID() {
        return this.containerID;
    }

    public String getContainerName() {
        return this.containerName;
    }

    public void setType(String objectType) {
        this.objectType = objectType;
    }

    public String getType() {
        return this.objectType;
    }

    public String getTypeID() {
        return this.typeID;
    }


    public boolean isTypeOf(String objectType) {

        boolean typeMatch = false;

        if (objectType.equals(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {
            typeMatch = true;
        }

        return typeMatch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserID(String userID)
        throws PersistenceException {
        this.user = new User();

        this.user.setID(userID);
        try {
            this.user.load();
        } catch (PersistenceException pe) {
        	Logger.getLogger(Document.class).debug("Document.setUser() threw a PersistenceException");
            throw new PersistenceException("Could not load user from within the DocumentModule!", pe);
        }

    }

    public User getUser() {
        return this.user;
    }

    public String getUserID() {
        return this.user.getID();
    }


    /**
     * Persists the document
     *
     * @throws PnetException if there was a problem storing the document
     */
    public void add() throws PnetException {
        int ignore = 0;

        DBBean db = new DBBean();
        try {
            String dicussionGroupDescription = PropertyProvider.get("prm.document.create.discussiongroup.description");

            db.prepareCall("begin  DOCUMENT.CREATE_DOC (?,?,?,?,?,?,?); end;");

            if (this.ignoreNameConstraint) {
                ignore = 1;
            }

            db.cstmt.setString(1, this.tmpRowID);
            db.cstmt.setString(2, this.containerID);
            db.cstmt.setString(3, this.user.getID());
            db.cstmt.setString(4, this.user.getCurrentSpace().getID());
            db.cstmt.setInt(5, ignore);
            db.cstmt.setString(6, dicussionGroupDescription);

            db.cstmt.registerOutParameter(7, java.sql.Types.INTEGER);

            db.executeCallable();

            this.errorCode = db.cstmt.getInt(7);

            // reloads the object with current state
            load();

        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).debug("Document.add(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle);
            throw new PersistenceException("Document Add operation failed! ", sqle);

        } // end catch
        finally {
            db.release();
        } // end finally


        // Handle (throw) any database exceptions
        try {
            DBExceptionFactory.getException("Document.add()", this.errorCode);
        } catch (net.project.base.UniqueNameConstraintException une) {
            throw new net.project.base.PnetException(PropertyProvider.get("prm.document.error.uniquename.message"), une);
        }

        DocumentEvent event = new DocumentEvent();

        // first throw the event for the document creation so that the "create" event shows in the audit log
        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.CREATE_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.CREATE_DOCUMENT));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.create.event.notes") + "\"" + this.name + "\"");

        event.store();
        
		// publishing event to asynchronous queue
        try {
        	if(!this.isWikiDocument) {
        		// Event for ducument upload
        		net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.NEW);
	        	documentEvent.setObjectType(ObjectType.DOCUMENT);	
	        	documentEvent.setObjectID(this.objectID);
	        	documentEvent.setName(this.name);
	        	documentEvent.setObjectRecordStatus("A");
	        	documentEvent.publish();
        	}	
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.add() :: Document Add Event Publishing Failed!", e);
		}

        // then throw the event for the parent container so that subscription notificaiton still work for container.add()
        //sjmittal not sure if the lines below makes any sense cos document history target object id is referenced
        //by only document object id in the db schema
        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.containerID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.CREATE_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.CREATE_DOCUMENT));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.create.event.notes") + "\"" + this.name + "\"");

        event.store();
        
    } // end add()


    public void loadProperties() throws PersistenceException {
        load();
    }

    public void setListDeleted() {
        this.listDeleted = true;
    }
    
    public void unSetListDeleted() {
        this.listDeleted = false;
    }
    
    public boolean isDeleted() {
        return this.listDeleted;
    }


    /**
     * **************************************************************************************************************
     * ****                                     Implementing INTERFACE
     * IJDBCPersistence                                *****
     * ***************************************************************************************************************
     */


    public void load() throws PersistenceException {
        String str1 = (listDeleted) ? "pn_doc_deleted_by_space_view dsv" :"pn_doc_by_space_view dsv";
        String str2 = (listDeleted) ? "'D'" : "'A'";
        // get the document properties
        String qstrLoadDocumentProperties =
            "select " +
            "  dsv.doc_id, dsv.doc_name, dsv.doc_space_name, dsv.container_name, dsv.object_type," +
            "  dsv.repository_path, dsv.doc_status, dsv.record_status, dsv.doc_status_id, " +
            "  dsv.doc_version_id, dsv.doc_version_num, dsv.doc_version_label, " +
            "  dsv.doc_version_name, dsv.source_file_name, dsv.short_file_name, " +
            "  dsv.version_description, dsv.doc_container_id, count.num_times_viewed, " +
            "  dsv.date_modified as date_modified, dsv.modified_by_id, dsv.is_checked_out, " +
            "  dsv.checked_out_by_id, dsv.checked_out_by, dsv.checkout_due as cko_due, " +
            "  dsv.doc_author, dsv.app_icon_url, dsv.doc_author_id, dsv.doc_description, " +
            "  dsv.date_checked_out as date_checked_out, dsv.crc, dsv.doc_comment, " +
            "  dsv.doc_content_id, dsv.doc_format_id, dsv.file_size, dsv.file_handle, " +
            "  dsv.format_name, dsv.mime_type, workflow_count.active_count as active_workflows," +
            "  dsv.repository_id, dsv.current_version_id, discussions.discussion_count, " +
            "  dsv.date_created " +
            "from " + str1 +
            "  , " +
            "  (select count(doc_history_id) as num_times_viewed " +
            "     from pn_doc_history " +
            "    where doc_id = " + this.objectID + " " +
            "      and (action = '" + EventCodes.VIEW_DOCUMENT + "' " +
            "           or action = '" + EventCodes.VIEW_DOCUMENT_VERSION + "')) COUNT, " +
            "  (select count(*) as active_count " +
            "     from pn_envelope_has_object_view ehov " +
            "    where ehov.object_id = " + this.objectID + " and ehov.is_active = 1) workflow_count, " +
            "  (select COUNT(*) as discussion_count" +
            "     from pn_object_has_discussion d, pn_post p" +
            "    where d.object_id = " + objectID + " and p.record_status = "+ str2 +
            "      and d.discussion_group_id = p.discussion_group_id) discussions" +
            " where dsv.doc_id = " + this.objectID;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadDocumentProperties);

            if (db.result.next()) {

                this.name = db.result.getString("doc_name");
                this.objectID = db.result.getString("doc_id");
                this.objectType = db.result.getString("object_type");
                this.containerName = PropertyProvider.get(db.result.getString("container_name"));
                this.containerID = db.result.getString("doc_container_id");
                this.notes = db.result.getString("doc_comment");
                this.status = PropertyProvider.get(db.result.getString("doc_status"));
                this.statusID = db.result.getString("doc_status_id");
                this.recordStatus = RecordStatus.findByID(db.result.getString("record_status"));
                this.description = db.result.getString("doc_description");
                this.fileFormat = PropertyProvider.get(db.result.getString("format_name"));
                this.fileFormatID = db.result.getString("doc_format_id");
                this.fileSize = db.result.getString("file_size");
                this.OSFilePath = db.result.getString("source_file_name");
                this.shortFileName = db.result.getString("short_file_name");
                this.fileHandle = db.result.getString("file_handle");
                this.repositoryBaseID = db.result.getString("repository_id");
                this.repositoryBasePath = db.result.getString("repository_path");
                this.mimeType = db.result.getString("mime_type");
                this.appIconURL = db.result.getString("app_icon_url");
                this.isCko = DocumentUtils.stringToBoolean(db.result.getString("is_checked_out"));
                this.ckoDate = sqlDateToUtilDate(db.result.getTimestamp("date_checked_out"));

                this.ckoReturn = sqlDateToUtilDate(db.result.getTimestamp("cko_due"));

                this.ckoBy = db.result.getString("checked_out_by");
                this.ckoByID = db.result.getString("checked_out_by_id");
                this.author = db.result.getString("doc_author");
                this.authorID = db.result.getString("doc_author_id");
                this.currentVersionID = db.result.getString("current_version_id");
                this.versionLabel = db.result.getString("doc_version_label");
                this.versionNum = db.result.getString("doc_version_num");
                this.numTimesViewed = db.result.getString("num_times_viewed");
                this.activeWorkflows = db.result.getString("active_workflows");
                this.discussionCount = db.result.getString("discussion_count");

                this.lastModified = (java.util.Date) db.result.getTimestamp("date_modified");

                this.creationDate = db.result.getTimestamp("date_created");
                this.modifiedByID = db.result.getString("modified_by_id");
                this.crc = (java.util.Date) db.result.getTimestamp("crc");

                this.isLoaded = true;

            } // end if
            else {
            	Logger.getLogger(Document.class).debug("Document.load() could not load document properties");
            }

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(Document.class).debug("Document.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Could not load Document Properties ", sqle);
        } // end catch
        finally {
            db.release();
        } // end finally

    } // end load()


    public void store() throws PersistenceException {

        this.tmpStore();

        try {
            this.add();
        } catch (PnetException pe) {
            throw new PersistenceException("Document.store() threw an exception; " + pe, pe);
        }

    } // end store


    public void remove() throws PersistenceException {
        boolean isActuallyDeleted = false;

        if(recordStatus.equals(RecordStatus.DELETED) && "H".equals(tempRecordStatus)) {
            try {
                File file = getFileObject();
                File baseDir = file.getParentFile();
                isActuallyDeleted = file.delete();
                
                //now load the versions and delete the too
                if(this.versions == null || !this.versions.isLoaded() ) {
                    loadVersions();
                }
                Iterator versionIter = this.versions.collection.iterator();
                while(versionIter.hasNext()) {
                    DocumentVersion docVersion = (DocumentVersion) versionIter.next();
                    file = docVersion.getFileObject();
                    isActuallyDeleted = file.delete();
                }
                
                //now delete the base dir
                isActuallyDeleted = baseDir.delete();
                
            } catch(DocumentException doe) {
                throw new PersistenceException("Document.remove() unable to delete: " + doe, doe);
            }
            //still file is not deleted please throw an exception
            if(!isActuallyDeleted) {
                throw new PersistenceException("Document.remove() unable physicaly delete the file");
            }
        }
        makeDirty();

        DBBean db = new DBBean();

        try {
            db.prepareCall("{call DOCUMENT.REMOVE_DOC(?,?,?,?)}");
            db.cstmt.setString(1, objectID);
            db.cstmt.setTimestamp(2, utilDateToSqlDate(this.lastModified));
            db.cstmt.setString(3, tempRecordStatus);
            db.cstmt.setString(4, this.user.getID());
            db.executeCallable();
        } catch (SQLException sqle) {
            throw new PersistenceException("Document.remove() unable to execute: " + sqle, sqle);

        } finally {
            db.release();
        }

        DocumentEvent event = new DocumentEvent();
        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID); //should be object id for fix of bfd 3132
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        
        net.project.events.DocumentEvent documentEvent = null;

        if(tempRecordStatus.equals(RecordStatus.ACTIVE.getID())) {
	        
            event.setCode(EventCodes.UNDO_REMOVE_DOCUMENT);
	        event.setName(EventCodes.getName(EventCodes.UNDO_REMOVE_DOCUMENT));
	        event.setNotes(PropertyProvider.get("prm.document.document.undoremove.event.notes") + "\"" + this.name + "\"");

	        documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.UNDO_REMOVE_DOCUMENT);
        } else {
            
	        event.setCode(EventCodes.REMOVE_DOCUMENT);
	        event.setName(EventCodes.getName(EventCodes.REMOVE_DOCUMENT));
	        event.setNotes(PropertyProvider.get("prm.document.document.remove.event.notes") + "\"" + this.name + "\"");

	        documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.DELETED);
        }
        event.setUser(this.user);
        event.store();
        
        // publishing event to asynchronous queue
        try {
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(this.name);
        	documentEvent.setObjectRecordStatus("D");
        	documentEvent.publish();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }


    /**
     * **************************************************************************************************************
     * ****                                     Implementing INTERFACE
     * IManagedObject                                 *****
     * ***************************************************************************************************************
     */

    public boolean isCheckedOut() {
        return (this.isCko);
    }

    public String getCheckedOutByID() {
        return this.ckoByID;
    }

    public String getCheckedOutBy() {
        return this.ckoBy;
    }

    /*****************************************************************************************************************
     *****                                 Implementing INTERFACE IDocument                                              *****
     *****************************************************************************************************************/



    /**
     * Check's Out the existing document to the user
     */
    public void checkOut() throws PnetException {

        Timestamp stamp = null;
        DocumentEvent event = new DocumentEvent();
        String tmpNotes = null;

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin  DOCUMENT.CHECK_OUT (?,?,?,?,?,?); end;");

            db.cstmt.setString(1, this.objectID);
            db.cstmt.setString(2, this.user.getID());
            db.cstmt.setTimestamp(3, utilDateToSqlDate(this.ckoReturn));
            db.cstmt.setString(4, this.notes);
            stamp = new Timestamp(this.crc.getTime());
            db.cstmt.setTimestamp(5, stamp);
            db.cstmt.registerOutParameter(6, java.sql.Types.INTEGER);

            db.executeCallable();

            this.errorCode = db.cstmt.getInt(6);

        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).debug("Document.checkOut(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle);
            throw new CheckOutFailedException("Document.checkOut(): User: " + user.getID() + ", unable to execute stored procedure",
                ErrorLogger.HIGH);

        } // end catch
        finally {
            db.release();
        } // end finally


        // Handle (throw) any database exceptions
        DBExceptionFactory.getException("Document.checkOut()", this.errorCode);

        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.CHECK_OUT_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.CHECK_OUT_DOCUMENT));
        event.setUser(this.user);

        if (this.notes != null) {
            tmpNotes = this.notes;
        } else {
            tmpNotes = PropertyProvider.get("prm.document.document.checkout.event.notes");
        }

        event.setNotes(PropertyProvider.get("prm.document.document.comments.label") + tmpNotes);

        event.store();
        
		// publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.CHECKED_OUT);
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(tmpNotes);
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.checkOut() :: Document CheckOut Event Publishing Failed!", e);
		}

    } // end checkOut


    public void undoCheckOut() throws PnetException {

        Timestamp stamp = null;
        DocumentEvent event = new DocumentEvent();

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin  DOCUMENT.UNDO_CHECK_OUT (?,?,?,?); end;");

            db.cstmt.setString(1, this.objectID);
            db.cstmt.setString(2, user.getID());
            stamp = new Timestamp(this.crc.getTime());
            db.cstmt.setTimestamp(3, stamp);
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();

            this.errorCode = db.cstmt.getInt(4);


        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).debug("Document.undoCheckOut(): User: " + user.getID() + ", unable to execute stored procedure");
            throw new UndoCheckOutFailedException("Document.undoCheckOut(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle,
                ErrorLogger.HIGH);

        } // end catch
        finally {
            db.release();
        } // end finally

        // Handle (throw) any database exceptions
        DBExceptionFactory.getException("Document.undoCheckOut()", this.errorCode);


        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.UNDO_CHECK_OUT_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.UNDO_CHECK_OUT_DOCUMENT));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.undocheckout.event.notes") + "\"" + this.name + "\"");

        event.store();
        
        // publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.UNDO_CHECKED_OUT);
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(this.name);
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.undoCheckOut() :: Document UndoCheckOut Event Publishing Failed!", e);
		}

    } // end undoCheckOut


    public void checkIn() throws PnetException {

        Timestamp stamp = null;
        DocumentEvent event = new DocumentEvent();

        makeDirty();

        DBBean db = new DBBean();
        try {

            db.prepareCall("begin  DOCUMENT.CHECK_IN (?,?,?,?,?); end;");

            db.cstmt.setString(1, this.tmpRowID);
            db.cstmt.setString(2, user.getID());
            db.cstmt.setString(3, user.getCurrentSpace().getID());
            stamp = new Timestamp(this.crc.getTime());
            db.cstmt.setTimestamp(4, stamp);
            db.cstmt.registerOutParameter(5, java.sql.Types.INTEGER);

            db.executeCallable();

            this.errorCode = db.cstmt.getInt(5);

        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).debug("Document.checkIn(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle);
            throw new CheckInFailedException("Document.checkIn(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle,
                ErrorLogger.HIGH);

        } // end catch
        finally {
            db.release();
        } // end finally


        // Handle (throw) any database exceptions
        DBExceptionFactory.getException("Document.checkin()", this.errorCode);


        // log this checkin event
        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.CHECK_IN_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.CHECK_IN_DOCUMENT));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.comments.label") + "\"" + this.notes + "\"");

        event.store();
        
		// publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.CHECKED_IN);
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(this.notes);
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.checkInt() :: Document CheckIn Event Publishing Failed!", e);
		}

    } // end checkIn()


    public void modify() throws PnetException {

        DocumentEvent event = new DocumentEvent();
        Timestamp stamp = null;

        DBBean db = new DBBean();
        try {

            db.prepareCall("begin  DOCUMENT.MODIFY_PROPERTIES (?,?,?,?); end;");

            db.cstmt.setString(1, this.tmpRowID);
            db.cstmt.setString(2, user.getID());
            stamp = new Timestamp(this.crc.getTime());
            db.cstmt.setTimestamp(3, stamp);
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();

            this.errorCode = db.cstmt.getInt(4);
        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).debug("Document.modify(): User: " + user.getID() + ", unable to execute stored procedure: " + sqle);
            throw new PersistenceException("Document.modify threw an SQL exception: " + sqle, sqle);

        } // end catch
        finally {
            db.release();
        } // end finally


        // Handle (throw) any database exceptions
        if (this.errorCode == 5001) {
        	//if this is uniq doc error, just notify user
        	
        } else {
        	DBExceptionFactory.getException("Document.modify()", this.errorCode);
        }


        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.MODIFY_PROPERTIES);
        event.setName(EventCodes.getName(EventCodes.MODIFY_PROPERTIES));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.modify.event.notes") + "\"" + this.name + "\"");

        event.store();
        
		// publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.EDITED);
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(this.name);
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.modify() :: Document Modify Event Publishing Failed!", e);
		}

    } // end undoCheckOut

    /**
     * Send this <code>Document</code> to the user through the browser.
     *
     * @param response a <code>HttpServletResponse</code> value through which
     * the document will be sent.
     */
    public void streamToHttpResponse(HttpServletResponse response) throws DocumentException {
        FileManager fileManager = new FileManager();

        fileManager.setObjectID(this.objectID);
        fileManager.setFileHandle(this.fileHandle);
        fileManager.setContentType(this.mimeType);
        fileManager.setFileName(this.shortFileName);
        fileManager.setRepositoryPath(this.repositoryBasePath);

        fileManager.download(response);
    }

    /**
     * Send this <code>Document</code> to the user through the browser.
     *
     * @param response a <code>HttpServletResponse</code> value through which
     * the document will be sent.
     */
    public void imageStreamToHttpResponse(HttpServletResponse response) throws DocumentException {
        FileManager fileManager = new FileManager();

        fileManager.setObjectID(this.objectID);
        fileManager.setFileHandle(this.fileHandle);
        fileManager.setContentType(this.mimeType);
        fileManager.setFileName(this.shortFileName);
        fileManager.setRepositoryPath(this.repositoryBasePath);

        fileManager.downloadImage(response);
    }
    
    /**
     * Add an entry to the database indicating that this document has been
     * downloaded.
     */
    public void logDownloadEvent() {
        DocumentEvent event = new DocumentEvent();

        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.objectID);
        event.setTargetObjectType(this.type);
        event.setTargetObjectXML(this.getXMLBody());
        event.setCode(EventCodes.VIEW_DOCUMENT);
        event.setName(EventCodes.getName(EventCodes.VIEW_DOCUMENT));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.document.download.event.notes") + "\"" + this.name + "\"");

        // log this event to the database
        event.store();
        
		// publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.VIEWED);
        	documentEvent.setObjectID(this.objectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(this.name);
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(Document.class).error("Document.logDownloadEvent() :: Document View Document Event Publishing Failed!"+ e.getMessage());
		}
    }

    /**
     * This method writes the contents of the document to the response
     *
     * @throws DocumentException if the document has not been loaded
     */
    public void download(HttpServletResponse response) throws DocumentException {
        if (!isLoaded()) {
            throw new DocumentException("Document.download(): Document must first be loaded");
        }

        //Indicate in the database that this document has been downloaded.
        logDownloadEvent();

        //Send this document to the response object.
        streamToHttpResponse(response);
    }

    /**
     * This method returns a {@link java.io.File} object for the current
     * document.
     *
     * @return The abstract file object representing this document
     * @throws DocumentException if the document has not been loaded
     */
    public File getFileObject() throws DocumentException {

        if (!isLoaded()) {
            throw new DocumentException("Document.getFileObject(): Document must first be loaded");
        }

        FileManager fileManager = new FileManager();

        fileManager.setObjectID(this.objectID);
        fileManager.setFileHandle(this.fileHandle);
        fileManager.setContentType(this.mimeType);
        fileManager.setFileName(this.shortFileName);
        fileManager.setRepositoryPath(this.repositoryBasePath);

        return (fileManager.getFileObject());

    } // end getFileObject


    /**
     * **************************************************************************************************************
     * ****                                 Implementing INTERFACE
     * ILinkableObject                                       *****
     * ***************************************************************************************************************
     * added for compatibilty with ILinkableObject
     */

    // added for compatibilty with ILinkableObject
    public String getObjectName() {
        return (getName());
    }

    public String getObjectType() {
        return (getType());
    }

    /*========================================================================
    Implementing INTERFACE IWorkflowable
    =========================================================================*/

    /* All other IWorkflowable methods are implemented elsewhere */

    /**
     * Return the version ID property
     *
     * @return the version ID property
     */
    public String getVersionID() {
        return this.currentVersionID;
    }

    /**
     * Returns subtype.  This is a type-specific value.<br> Returns null if
     * there is no subtype
     *
     * @return the subtype property
     */
    public String getSubType() {
        return null;
    }

    /**
     * Indicates that Document does NOT provide its own presentation.
     *
     * @return false means that getPresentation() will return NULL and getXML()
     *         should be used instead.
     */
    public boolean isSpecialPresentation() {
        return false;
    }

    /**
     * Returns null always.
     *
     * @return null
     */
    public String getPresentation() {
        return null;
    }

    /**
     * **************************************************************************************************************
     * ****                                 Implementing other getter/setter
     * methods                                          *****
     * ***************************************************************************************************************
     */

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCkoReturn(String ckoReturn)
        throws net.project.util.InvalidDateException {

        if (ckoReturn != null && !ckoReturn.equals("")) {
            DateFormat dateFormatter = net.project.security.SessionManager.getUser().getDateFormatter();
            this.ckoReturn = dateFormatter.parseDateString(ckoReturn);
        } else {

            this.ckoReturn = null;
        }
    }

    public void setCkoReturn(java.util.Date ckoReturn) {
        this.ckoReturn = ckoReturn;
    }

    public String getOSFilePath() {
        return this.OSFilePath;
    }

    public String getAuthorID() {
        return this.authorID;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getFileHandle() {
        return this.fileHandle;
    }

    public String getStatus() {
        return this.status;
    }

    public String getCkoDate() {
        return dateToString(this.ckoDate, DATE_FORMAT_STRING);
    }

    public String getCkoReturn() {
        return dateToString(this.ckoReturn, DATE_FORMAT_STRING);
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStatusID() {
        return this.statusID;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileFormatID(String fileFormatID) {
        this.fileFormatID = fileFormatID;
    }

    public String getNotes() {
        return this.notes;
    }

    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    public void setTempRecordStatus(String id) {
        RecordStatus status = RecordStatus.findByID(id);
        if(status != null)
            tempRecordStatus = id;
    }

    public void setOSFilePath(String OSFilePath) {
        this.OSFilePath = OSFilePath;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
    }

    public void setFileHandle(String fileHandle) {
        this.fileHandle = fileHandle;
    }

    public void setRepositoryBaseID(String repositoryBaseID) {
        this.repositoryBaseID = repositoryBaseID;
    }

    public String getRepositoryBaseID() {
        return this.repositoryBaseID;
    }

    public void setRepositoryBasePath(String repositoryBasePath) {
        this.repositoryBasePath = repositoryBasePath;
    }

    public String getRepositoryBasePath() {
        return this.repositoryBasePath;
    }

    public String getShortFileName() {
        return this.shortFileName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getURL() {
        return URLFactory.makeURL(this.objectID, ObjectType.DOCUMENT);
    }

    public void setIgnoreNameConstraint(boolean flag) {
        this.ignoreNameConstraint = flag;
    }

    /**
     * Specify whether or not the <code>DocumentControlManager</code> should
     * ignore the user mismatch constraint. This specifically is used for check
     * in and check out when the space admin overrides the checkout of another
     * user. <br>
     *
     * @param ignoreMismatch
     * @see net.project.document.DocumentControlManager
     * @since Gecko
     */
    public void setIgnoreUserMismatchConstraint(boolean ignoreMismatch) {
        this.ignoreUserMismatchConstraint = ignoreMismatch;
    }

    /**
     * Retrnn whether or not the <code>DocumentControlManager</code> should
     * ignore the user mismatch constraint. This specifically is used for check
     * in and check out when the space admin overrides the checkout of another
     * user. <br>
     *
     * @return The a boolean indication of whether to ignore the constraint
     * @see net.project.document.DocumentControlManager
     * @since Gecko
     */
    public boolean getIgnoreUserMismatchConstraint() {
        return this.ignoreUserMismatchConstraint;
    }


    /**
     * **************************************************************************************************************
     * ****                                 Implementing storage methods *****
     * ***************************************************************************************************************
     */

    public void tmpStore(String tmpRowID) throws PersistenceException {

        this.tmpRowID = tmpRowID;

        tmpStore();
    }

    public void tmpStore() throws PersistenceException {

        if (tmpRowID == null) {
            this.tmpRowID = DocumentUtils.getNextSequenceValue();
        }

        if (this.objectID == null) {
            this.objectID = this.tmpRowID;
        }

        // make this object dirty
        this.makeDirty();

        DBBean db = new DBBean();
        try {
            db.prepareStatement("INSERT INTO pn_tmp_document (tmp_doc_id, doc_id, doc_version_id, doc_name, doc_description, source_file_name," +
                "modified_by_id, date_modified, is_checked_out, checked_out_by_id, date_checked_out, doc_comment, doc_content_id, doc_format_id, " +
                "doc_version_num, doc_version_label, file_size, file_handle, doc_history_id, doc_container_id, checkout_due, doc_status_id, doc_type_id, " +
                "doc_author_id, short_file_name, repository_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            db.pstmt.setString(1, this.tmpRowID);
            db.pstmt.setString(2, this.objectID);
            db.pstmt.setString(3, this.currentVersionID);
            db.pstmt.setString(4, this.name);
            db.pstmt.setString(5, this.description);
            db.pstmt.setString(6, this.OSFilePath);
            db.pstmt.setString(7, this.modifiedByID);
            db.pstmt.setTimestamp(8, utilDateToSqlDate(this.lastModified));
            db.pstmt.setString(9, Conversion.booleanToInteger(this.isCko).toString());
            db.pstmt.setString(10, this.ckoByID);
            db.pstmt.setTimestamp(11, utilDateToSqlDate(this.ckoDate));
            db.pstmt.setString(12, this.notes);
            db.pstmt.setString(13, this.contentID);
            db.pstmt.setString(14, this.fileFormatID);
            db.pstmt.setString(15, this.versionNum);
            db.pstmt.setString(16, this.versionLabel);
            db.pstmt.setString(17, this.fileSize);
            db.pstmt.setString(18, this.fileHandle);
            db.pstmt.setString(19, this.historyID);
            db.pstmt.setString(20, this.containerID);
            db.pstmt.setTimestamp(21, utilDateToSqlDate(this.ckoReturn));
            db.pstmt.setString(22, this.statusID);
            db.pstmt.setString(23, this.typeID);
            db.pstmt.setString(24, this.authorID);
            db.pstmt.setString(25, this.shortFileName);
            db.pstmt.setString(26, this.repositoryBaseID);


            db.executePrepared();


        } // end try
        catch (SQLException sqle) {

        	Logger.getLogger(Document.class).error("Document.tmpStore(): User: " + user.getID() + ", unable to store object in pn_tmp_document: " + sqle);
            throw new PersistenceException("Temporary storage of document '" + this.name + "' failed: " + sqle, sqle);

        } // end catch
        finally {
            db.release();
        } // end finally


    } // end tmpStore()


    /**
     * **************************************************************************************************************
     * ****                                 Implementing "versioning" methods
     * ***** ***************************************************************************************************************
     */

    public void loadVersions() {

        VersionCollection collection = new VersionCollection();
        
        if(isDeleted()) {
            collection.setListDeleted();
        }

        collection.setParentObjectID(this.objectID);
        collection.setCurrentVersionID(this.currentVersionID);
        collection.setUser(this.user);

        collection.load();

        this.versions = collection;
        
        collection.unSetListDeleted();

    } // end loadVersions


    public void downloadVersion(String versionID, HttpServletResponse response) throws DocumentException {

        IVersion version = getVersion(versionID);

        if (version == null) {
        	Logger.getLogger(Document.class).debug("VERSION IS NULL");
        }

//	if (!version.isLoaded())
//	    version.load();

        version.setUser(this.user);
        version.download(response);

    }

    public IVersion getCurrentVersion() {

        return this.versions.getCurrentVersion();

    }

    public IVersion getVersion(String versionID) {
        return (versions.getVersion(versionID));
    }


    /**
     * **************************************************************************************************************
     * ****                                 Implementing "event" methods *****
     * ***************************************************************************************************************
     */

    public void loadEvents() {

        EventCollection eventList = new EventCollection();
        
        if(isDeleted()) {
            eventList.setListDeleted();
        }

        eventList.setParentObjectID(this.objectID);
        eventList.setUser(this.user);

        eventList.load();

        this.events = eventList;
        
        eventList.unSetListDeleted();

    } // end loadEvents;


    /**
     * **************************************************************************************************************
     * ****                                 Implementing redering methods *****
     * ***************************************************************************************************************
     */


    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(IXMLPersistence.XML_VERSION);
        xml.append(getXMLBody());

        return xml.toString();
    }

    public String getAppletXML(String spaceName) {
        return getXML();  // I don't think the applet actually gets here, so ignore for now.  CK 7/00
    }

    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        xml.ensureCapacity(1000);

        xml.append("<document>");
        xml.append(getXMLProperties());
        xml.append(getXMLVersions());
        xml.append(getXMLEvents());
        xml.append("</document>");

        return xml.toString();

    }

    public String getXMLProperties() {

        StringBuffer xml = new StringBuffer();
        String tab = null;
        Path path = new Path();

        tab = "\t";
        xml.append(tab + "<document_properties>\n");

        xml.append("<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>");

        // now set path
        path.setRootContainerID(this.containerID);
        path.load();

        xml.append(path.getXMLBody());

        tab = "\t\t";
        xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");
        xml.append(tab + "<object_type>" + XMLUtils.escape(this.objectType) + "</object_type>\n");
        xml.append(tab + "<container_id>" + XMLUtils.escape(this.containerID) + "</container_id>\n");
        xml.append(tab + "<container_name>" + XMLUtils.escape(this.containerName) + "</container_name>\n");
        xml.append(tab + "<last_modified>" + XMLUtils.formatISODateTime(this.lastModified) + "</last_modified>\n");
        xml.append(tab + "<modified_by_id>" + XMLUtils.escape(this.modifiedByID) + "</modified_by_id>\n");
        xml.append(tab + "<description>" + XMLUtils.escape(this.description) + "</description>\n");
        xml.append(tab + "<notes>" + XMLUtils.escape(this.notes) + "</notes>\n");
        xml.append(tab + "<author>" + XMLUtils.escape(this.author) + "</author>\n");
        xml.append(tab + "<author_id>" + XMLUtils.escape(this.authorID) + "</author_id>\n");
        xml.append(tab + "<type>" + XMLUtils.escape(this.type) + "</type>\n");
        xml.append(tab + "<type_id>" + XMLUtils.escape(this.typeID) + "</type_id>\n");
        xml.append(tab + "<status>" + XMLUtils.escape(this.status) + "</status>\n");
        xml.append(tab + "<status_id>" + XMLUtils.escape(this.statusID) + "</status_id>\n");
        xml.append(tab + "<version>" + XMLUtils.escape(this.versionNum) + "</version>\n");
        xml.append(tab + "<os_file_path>" + XMLUtils.escape(this.OSFilePath) + "</os_file_path>\n");
        xml.append(tab + "<file_size>" + XMLUtils.escape(DocumentUtils.fileSizetoKBytes(this.fileSize)) + "</file_size>\n");
        xml.append(tab + "<file_format>" + XMLUtils.escape(this.fileFormat) + "</file_format>\n");
        xml.append(tab + "<mime_type>" + XMLUtils.escape(this.mimeType) + "</mime_type>\n");
        xml.append(tab + "<app_icon_url>" + XMLUtils.escape(this.appIconURL) + "</app_icon_url>\n");
        xml.append(tab + "<is_cko>" + XMLUtils.format(this.isCko) + "</is_cko>\n");
        xml.append(tab + "<cko_image_str>" + XMLUtils.escape(DocumentUtils.getCkoImage(this.isCko, this.ckoBy, this.ckoByID)) + "</cko_image_str>\n");
        xml.append(tab + "<cko_by_id>" + XMLUtils.escape(this.ckoByID) + "</cko_by_id>\n");
        xml.append(tab + "<cko_by>" + XMLUtils.escape(this.ckoBy) + "</cko_by>\n");

        if (this.ckoDate == null) {
            xml.append(tab + "<cko_date/>");
        } else {
            xml.append(tab + "<cko_date>" + XMLUtils.formatISODateTime(this.ckoDate) + "</cko_date>\n");
        }

        if (this.ckoReturn == null) {
            xml.append(tab + "<cko_return/>");
        } else {
            xml.append(tab + "<cko_return>" + XMLUtils.formatISODateTime(this.ckoReturn) + "</cko_return>\n");
        }

        // xml.append (tab + "<cko_return>" + XMLUtils.formatISODateTime (this.ckoReturn) + "</cko_return>\n");
//	xml.append (tab + "<cko_return>" + XMLUtils.escape ( this.ckoReturn.toString() ) + "</cko_return>\n");
        xml.append(tab + "<file_handle>" + XMLUtils.escape(this.fileHandle) + "</file_handle>\n");
        xml.append(tab + "<current_version_id>" + XMLUtils.escape(this.currentVersionID) + "</current_version_id>\n");
        xml.append(tab + "<num_times_viewed>" + XMLUtils.escape(this.numTimesViewed) + "</num_times_viewed>\n");
        xml.append(tab + "<active_workflows>" + XMLUtils.escape(this.activeWorkflows) + "</active_workflows>\n");
        xml.append(tab + "<discussion_count>" + XMLUtils.escape(discussionCount) + "</discussion_count>\n");
        xml.append(tab + "<version_num>" + XMLUtils.escape(this.versionNum) + "</version_num>\n");
        xml.append(tab + "<version_label>" + XMLUtils.escape(this.versionLabel) + "</version_label>\n");
        xml.append(tab + "<short_file_name>" + XMLUtils.escape(this.shortFileName) + "</short_file_name>\n");
        xml.append(tab + "<url>" + getURL() + "</url>\n");
        tab = "\t";
        xml.append(tab + "</document_properties>\n\n");

        return xml.toString();
    } // end getXML()


    public String getXMLVersions() {

        if (this.versions == null) {
            loadVersions();
        }

        return this.versions.getXML();
    }

    public String getXMLEvents() {

        if (this.events == null) {
            loadEvents();
        }

        return this.events.getXML();
    }


    /**
     * **************************************************************************************************************
     * ****                                 Implementing private utility methods
     * ***** ***************************************************************************************************************
     */

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void reset() {

        this.name = null;
        this.objectID = null;
        this.objectType = null;
        this.containerID = null;
        this.containerName = null;
        this.links = null;
        this.description = null;
        this.recordStatus = null;
        this.notes = null;
        this.author = null;
        this.authorID = null;
        this.type = null;
        this.typeID = null;
        this.status = null;
        this.availability = null;
        this.OSFilePath = null;
        this.fileSize = null;
        this.fileFormat = null;
        this.fileFormatID = null;
        this.mimeType = null;
        this.isCko = false;
        this.ckoByID = null;
        this.ckoBy = null;
        this.ckoDate = null;
        this.ckoReturn = null;
        this.fileHandle = null;
        this.statusID = null;
        this.currentVersionID = null;
        this.numTimesViewed = null;
        this.versionNum = null;
        this.versionLabel = null;
        this.shortFileName = null;
        this.creationDate = null;
        this.lastModified = null;
        this.modifiedByID = null;
        this.tmpRowID = null;
        this.contentID = null;
        this.historyID = null;

    } // end reset()

    /**
     * Updates the modified By user iD to the ID of the current user and updates
     * the last modified date to now. Typically called prior to performing a
     * database modification that accepts those attributes as parameters.
     */
    private void makeDirty() {
        this.modifiedByID = this.user.getID();
        this.lastModified = new java.util.Date();
    }

    //sjmittal: Using timestamp instead of Date as Date ignores the time component
    //which may lead to several bugs
    private java.sql.Timestamp utilDateToSqlDate(java.util.Date date) {

        java.sql.Timestamp sqlDate = null;

        if (date != null) {
            sqlDate = new java.sql.Timestamp(date.getTime());
        }

        return sqlDate;
    }

    private java.util.Date sqlDateToUtilDate(java.sql.Timestamp date) {

        java.util.Date utilDate = null;

        if (date != null) {
            utilDate = new java.util.Date(date.getTime());
        }

        return utilDate;
    }

    private String dateToString(java.util.Date date, String formatString) {

        DateFormat dateFormatter = this.user.getDateFormatter();
        String tmpString = null;

        tmpString = dateFormatter.formatDate(date, formatString);

        return tmpString;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    public java.util.Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(java.util.Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getErrorCode() {
    	return errorCode;
    }

	/**
	 * @param isWikiDocument the isWikiDocument to set
	 */
	public void setWikiDocument(boolean isWikiDocument) {
		this.isWikiDocument = isWikiDocument;
	}

} // end Class Document
