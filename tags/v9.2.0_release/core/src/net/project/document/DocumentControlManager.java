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

import javax.servlet.http.HttpServletResponse;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.PnetExceptionTypes;
import net.project.base.RecordStatus;
import net.project.base.UpdateConflictException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.events.EventType;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorLogger;

import org.apache.log4j.Logger;


/**
 * The <code>DocumentControlManager</code> is provided as a centralized
 * controller for all document vault-based activities.  Any action which can be
 * performed against any object in the document vault, should be called through
 * this class. <br> This object will verify security authorization, validate any
 * necessary business rules, then call back through the object which was passed
 * in (IContainerObject) and perform the actual action. <p> <b>For Example:</b>
 * When checking out a document, the developer would do so by way of
 * <code>documentControlManager.checkOut (document)</code>. <b>The manager will
 * verify security, that the document is not already checked out, and that the
 * document is loaded.  If all checks are ok, the check out operation will be
 * allowed to proceed via a callback to the document object
 * (<code>document.checkOut()</code>)
 *
 * @author Phil Dixon
 * @since The beginning of time
 */
public class DocumentControlManager {

    /**
     * The user which is attempting to act on the document object
     */
    private User user = null;


    /* ------------------------------- Constructor(s)  ------------------------------- */


    /**
     * Constructs a DocumentControlManager. No-arg constructor provided for bean
     * compliance.
     *
     * @since The beginning of time
     */
    public DocumentControlManager() {
        // do nothing
    }


    /* ------------------------------- Getter / Setter Methods  ------------------------------- */

    /**
     * Initializes the document control manager by setting the user
     *
     * @param user The user acting on the document object
     * @since The beginning of time
     */
    public void setUser(User user) {
        this.user = user;
    }


    /* ------------------------------- Document Control Management Implementaiton  ------------------------------- */


    /**
     * Remove the specified <code>IContainerObject</code> after validating
     * security and business rules. Actual object removal implemented via a
     * callback to the remove method of the IContainerObject itself. <p> If the
     * object is a container then it is removed recursively; that is all
     * contained objects will be removed. </p>
     *
     * @param co The object to be removed
     * @throws DocumentException If the business rules are not validated or if
     * the object being removed is not of a supported type.
     * @throws AuthorizationFailedException If the user perfoming the action
     * does not have the appropriate access permission
     * @see net.project.document.IContainerObject
     * @since The beginning of time
     */
    public void remove(IContainerObject co, String status) throws DocumentException, AuthorizationFailedException {

        if (co.isTypeOf(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {
            removeDocument((Document) co, status);
        } else if (co.isTypeOf(ContainerObjectType.CONTAINER_OBJECT_TYPE)) {
            removeContainer((Container) co, status);
        } else if (co.isTypeOf(ContainerObjectType.BOOKMARK_OBJECT_TYPE)) {
            removeBookmark((Bookmark) co);
        } else {
            throw new DocumentException("DocumentControlManager.remove() can not remove an unsupported object type");
        }
    }

    /**
     * Modify the specified <code>IContainerObject</code> after validating
     * security and business rules.
     *
     * @param co The object to be modified
     * @throws DocumentException If the business rules are not validated or if
     * the object being removed is not of a supported type.
     * @throws AuthorizationFailedException If the user perfoming the action
     * does not have the appropriate access permission
     * @throws UpdateConflictException If another user process has modified the
     * object.
     * @throws PnetException
     * @see net.project.document.IContainerObject
     * @since The beginning of time
     */
    public void modify(IContainerObject co) throws DocumentException, UpdateConflictException, PnetException,
        AuthorizationFailedException {

        if (co.isTypeOf(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {
            modifyDocument((Document) co);
        } else if (co.isTypeOf(ContainerObjectType.CONTAINER_OBJECT_TYPE)) {
            modifyContainer((Container) co);
        } else if (co.isTypeOf(ContainerObjectType.BOOKMARK_OBJECT_TYPE)) {
            modifyBookmark((Bookmark) co);
        } else {
            throw new DocumentException("DocumentControlManager.remove() can not modify an unsupported object type");
        }

    } // end modify()


    public void move(String objectID, String containerID) throws DocumentException, AuthorizationFailedException {

        verifyMove(objectID, containerID);

        // XXX: need a security check here but we only have id's, not objects?

        // if get here OK then...
        try {
            executeMove(objectID, containerID);
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Move failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

    } // move

    /**
     * Performs the actual checkout of the document. The method first verifies
     * that all criteria for checkout are met.  If they are (including security
     * access permission), the checkOut() method is then invoked on the document
     * passed in.
     *
     * @param document The document to be checked out.
     * @throws CheckOutFailedException If the document is not loaded or if the
     * document is already checked out.
     * @throws UpdateConflictException
     * @throws PnetException Will throw an exception if the checkout operation
     * itself fails.
     * @throws AuthorizationFailedException If the user does not have access or
     * permission to perform the requested action.
     * @since The beginning of time
     */
    public void checkOut(Document document) throws CheckOutFailedException, PnetException, AuthorizationFailedException {

        // verify that all rules are met.  If not, throw an exception
        verifyCheckOut(document);
        document.checkOut();
    }

    /**
     * Cancel the checkout of the specified document. The method first verifies
     * that all criteria for canceling checkout are met.  If they are (including
     * security access permission), the undoCheckOut() method is then invoked on
     * the document passed in.
     *
     * @param document The document to be operated upon
     * @throws UpdateConflictException
     * @throws PnetException Will throw an exception if the checkout operation
     * itself fails.
     * @throws UndoCheckOutFailedException If the document is not loaded, if the
     * document is not checked out or if the user attempting to cancel the
     * checkout is not the user that checked it out in the first place.
     * @throws AuthorizationFailedException If the user does not have access or
     * permission to perform the requested action.
     * @since The beginning of time
     */
    public void undoCheckOut(Document document)
        throws UndoCheckOutFailedException, PnetException, AuthorizationFailedException {

        // verify all biz rules are met (including security access)
        verifyUndoCheckOut(document);

        // now check the document out
        document.undoCheckOut();

        // finally, reset the ingore user mismatch constraint to false
        document.setIgnoreUserMismatchConstraint(false);
    }

    /**
     * Performs the check in of the document. The method first verifies that all
     * criteria for check in are met.  If they are (including security access
     * permission), the checkIn() method is then invoked on the document passed
     * in.
     *
     * @param document The document to be checked in.
     * @throws UpdateConflictException
     * @throws PnetException Will throw an exception if the checkout operation
     * itself fails.
     * @throws AuthorizationFailedException If the user does not have access or
     * permission to perform the requested action.
     * @throws CheckInFailedException Thrown if document is not loaded, the
     * document is not checked out or the user performing the operation is not
     * the user that checked the document out.
     * @since The beginning of time
     */
    public void checkIn(Document document) throws CheckInFailedException, PnetException, AuthorizationFailedException {

        verifyCheckIn(document);
        document.checkIn();
    }


    public void viewVersion(Document document, String versionID, HttpServletResponse response) throws
        DocumentException, AuthorizationFailedException, PersistenceException {

        verifyVersionView(document);

        if ((SessionManager.getSecurityProvider().isActionAllowed(document.getID(),
            Integer.toString(DocumentManager.getInstance().getModuleFromContainerID(document.getContainerID())),
            net.project.security.Action.VIEW))) {
            document.downloadVersion(versionID, response);
        } else {
            throw new AuthorizationFailedException(PropertyProvider.get("prm.document.documentcontrolmanager.viewversion.message"));
        }
    }





    /* ------------------------------- Implementing type specific conrol methods  ------------------------------- */


    /**
     * Remove document specified after validating security and business rules.
     * Actual document removed implemented via a callback to the remove method
     * of the document itself.
     *
     * @param document The document to be removed.
     * @throws DocumentException
     * @throws AuthorizationFailedException
     * @since The beginning of time
     */
    public void removeDocument(Document document, String status) throws DocumentException, AuthorizationFailedException {

        verifyRemoveDocument(document, status);

        try {
            document.setTempRecordStatus(status);
            document.remove();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Remove failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);
        }
    }

    /**
     * Remove container specified after validating security and business rules.
     * Actual container removed implemented via a callback to the remove method
     * of the container itself. <p> If the object is a container then it is
     * removed recursively; that is all contained objects will be removed. </p>
     *
     * @param container The container to be removed.
     * @throws DocumentException
     * @throws AuthorizationFailedException
     * @since The beginning of time
     */
    public void removeContainer(Container container, String status) throws DocumentException, AuthorizationFailedException {

        verifyRemoveContainer(container, status);

        try {
            container.setTempRecordStatus(status);
            container.remove();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Remove failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);
        }
    }

    /**
     * Remove bookmark specified after validating security and business rules.
     * Actual bookmark removed implemented via a callback to the remove method
     * of the bookmark itself.
     *
     * @param bookmark The document to be removed.
     * @throws DocumentException
     * @throws AuthorizationFailedException
     * @since The beginning of time
     */
    public void removeBookmark(Bookmark bookmark) throws DocumentException, AuthorizationFailedException {

        verifyRemoveBookmark(bookmark);

        try {
            bookmark.remove();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Remove failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);
        }
    }


    public void modifyDocument(Document document) throws DocumentException,
        PnetException, AuthorizationFailedException {

        verifyUpdateDocument(document);

        if (!(SessionManager.getSecurityProvider().isActionAllowed(document.getID(),
            Integer.toString(DocumentManager.getInstance().getModuleFromContainerID(document.getContainerID())),
            net.project.security.Action.MODIFY))) {
            throw new AuthorizationFailedException(PropertyProvider.get("prm.document.documentcontrolmanager.modifydocument.message"));
        }


        try {
            document.modify();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Modify failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

    } // end modifyDocument()


    public void modifyContainer(Container container) throws DocumentException,
        PnetException, AuthorizationFailedException {

        verifyUpdateContainer(container);


        if (!(SessionManager.getSecurityProvider().isActionAllowed(container.getID(),
            Integer.toString(DocumentManager.getInstance().getModuleFromContainerID(container.getContainerID())),
            net.project.security.Action.MODIFY))) {
            throw new AuthorizationFailedException(PropertyProvider.get("prm.document.documentcontrolmanager.modifycontainer.message"));
        }

        try {
            container.modify();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Modify failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

    } // end modifyContainer()


    public void modifyBookmark(Bookmark bookmark) throws DocumentException,
        PnetException, AuthorizationFailedException {

        verifyUpdateBookmark(bookmark);


        if (!(SessionManager.getSecurityProvider().isActionAllowed(bookmark.getID(),
            Integer.toString(DocumentManager.getInstance().getModuleFromContainerID(bookmark.getContainerID())),
            net.project.security.Action.MODIFY))) {
            throw new AuthorizationFailedException(PropertyProvider.get("prm.document.documentcontrolmanager.modifybookmark.message"));
        }

        try {
            bookmark.modify();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Modify failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

    } // end modifyContainer()


    public static void executeMove(String objectID, String containerID) throws PersistenceException {

        DocumentEvent event = new DocumentEvent();
        DBBean db = new DBBean();

        try {
            // Create a callable statement
            db.prepareCall("begin  DOCUMENT.MOVE_OBJECT (?, ?); end;");

            db.cstmt.setString(1, objectID);
            db.cstmt.setString(2, containerID);

            db.executeCallable();

            ContainerObjectFactory factory = new ContainerObjectFactory();
            IContainerObject containerObject = factory.makeObject(objectID);

            event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
            event.setTargetObjectID(objectID);
            event.setTargetObjectType(containerObject.getType());
            event.setTargetObjectXML(containerObject.getXMLBody());
            event.setCode(EventCodes.MOVE_OBJECT);
            event.setName(EventCodes.getName(EventCodes.MOVE_OBJECT));
            event.setUser(SessionManager.getUser());
            event.setNotes(PropertyProvider.get("prm.document.documentcontrolmanager.move.event.message", new Object[]{containerObject.getName(), containerObject.getContainerName()}));

            event.store();
            
    		// publishing event to asynchronous queue
            try {
            	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.MOVED);
            	documentEvent.setObjectID(objectID);
            	documentEvent.setObjectType(ObjectType.DOCUMENT);
            	documentEvent.setName(PropertyProvider.get("prm.document.documentcontrolmanager.move.event.message", new Object[]{containerObject.getName(), containerObject.getContainerName()}));
            	documentEvent.setObjectRecordStatus("A");
            	documentEvent.publish();
    		} catch (EventException e) {
    			Logger.getLogger(DocumentControlManager.class).error("Document.executeMove() :: Document Move Event Publishing Failed!"+ e.getMessage());
    		}
    		
        } // end try
        catch (SQLException sqle) {

            PersistenceException pe = new PersistenceException();

            pe.setMessage("Move failed because of an SQLException: " + sqle);
            pe.setSeverity(ErrorLogger.CRITICAL);
            pe.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            pe.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            pe.log();

            throw (pe);

        } // end catch
        finally {
            db.release();
        }
    } // end executeMove()


    public void verifyVersionView(Document document) throws DocumentException {

        if (!isLoaded(document)) {

            DocumentException de = new DocumentException();

            de.setMessage("View Version failed because the document is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);
        }
    }


    public void verifyMove(String objectID, String containerID) throws DocumentException {

        DocumentManager dm = new DocumentManager();
        String objectType = dm.getType(objectID);

        if (objectType.equals(ContainerObjectType.DOCUMENT_OBJECT_TYPE)) {
            verifyMoveDocument(objectID);
        } else if (objectType.equals(ContainerObjectType.CONTAINER_OBJECT_TYPE)) {
            verifyMoveContainer(objectID, containerID);
        }

    } // verify move


    public void verifyMoveDocument(String documentID) throws DocumentException {

        Document document = new Document();

        document.setID(documentID);

        try {
            document.load();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Move failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

        if (!isLoaded(document)) {

            DocumentException de = new DocumentException();

            de.setMessage("Move failed because the document is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);
        } else if (isCheckedOut(document) && (!userMatches(user.getID(), document.getCheckedOutByID()))) {

            DocumentException de = new DocumentException();

            de.setMessage(PropertyProvider.get("prm.document.documentcontrolmanager.verifymove.alreadycheckedout.message"));
            de.setSeverity(ErrorLogger.LOW);
            de.setReasonCode(PnetExceptionTypes.DOCUMENT_REMOVE_FAILED_CKO_BY_ANOTHER_USER);
            de.setDisplayError("You can not move this document because it is checked out by another user");
            de.log();

            throw (de);
        }

    } // end verifyMoveDocument


    public void verifyMoveContainer(String objectID, String containerID) throws DocumentException {
        Container container = new Container();

        //Make sure the user isn't trying to move the top folder.
        try {
            container.setContainerID(containerID);
            container.load();

            if (container.isRoot()) {
                DocumentException de = new DocumentException(PropertyProvider.get("prm.global.javascript.moveroot.error.message"));
                de.setSeverity(ErrorLogger.LOW);
                de.setDisplayError(PropertyProvider.get("prm.global.javascript.moveroot.error.message"));
                de.log();

                throw de;
            }
        } catch (PersistenceException e) {
            //If the folder cannot be loaded, is probably isn't the top folder.
        }

        if (isParentContainer(objectID, containerID)) {

            DocumentException de = new DocumentException();

            de.setMessage("Tried to move a folder into one of its children: ");
            de.setUserMessage("You may not move a folder into one of its children");
            de.setSeverity(ErrorLogger.LOW);
            de.setReasonCode(PnetExceptionTypes.DOCUMENT_MOVE_FAILED_TRIED_MOVING_OBJECT_INTO_ITS_CHILD);
            de.setDisplayError("You can't move a container into one of its children.");
            de.log();

            throw (de);

        }


        if (objectID.equals(containerID)) {

            DocumentException de = new DocumentException();

            de.setMessage("Tried to move a folder into itself: ");
            de.setUserMessage(PropertyProvider.get("prm.document.documentcontrolmanager.movecontainer.samefolder.message"));
            de.setSeverity(ErrorLogger.LOW);
            de.setReasonCode(PnetExceptionTypes.DOCUMENT_MOVE_FAILED_TRIED_MOVING_OBJECT_INTO_ITSELF);
            de.setDisplayError("You can't move a container onto itself.");
            de.log();

            throw (de);

        }

        container.setID(objectID);

        try {
            container.load();
        } catch (PersistenceException pe) {

            DocumentException de = new DocumentException();

            de.setMessage("Move failed because of a PersistenceException: " + pe);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.DB_EXCEPTION);
            de.setDisplayError("Critical Database Error, please notify your Project.net administrator.");
            de.log();

            throw (de);

        } // end catch

        if (!isLoaded(container)) {

            DocumentException de = new DocumentException();

            de.setMessage("Move failed because the document is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);
        }
    }

    /**
     * Validate the removal of the specified document.
     *
     * @param document The document to be removed.
     * @throws DocumentException If the document is not loaded or the document
     * is currently checked out.
     * @since Falcon
     */
    public void verifyRemoveDocument(Document document, String status) throws DocumentException {

        // first check security
        if("D".equals(status))
            checkSecurity(document, Action.DELETE, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovedocument.accessdenied.message"));
        else if("H".equals(status))
            checkSecurity(document, Action.REMOVE_DELETED, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovedocument.accessdenied.message"));
        else if("A".equals(status))
            checkSecurity(document, Action.UNDO_DELETED, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovedocument.accessdenied.message"));

        // is this document loaded
        if (!isLoaded(document)) {

            DocumentException de = new DocumentException();

            de.setMessage("Remove failed because the document is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);

        } else if (isCheckedOut(document) && (!userMatches(user.getID(), document.getCheckedOutByID()))) {

            DocumentException de = new DocumentException();

            de.setMessage(PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovedocument.checkedout.message"));
            de.setSeverity(ErrorLogger.LOW);
            de.setReasonCode(PnetExceptionTypes.DOCUMENT_REMOVE_FAILED_CKO_BY_ANOTHER_USER);
            de.setDisplayError("Remove failed because the document is checked out by another user");
            de.log();

            throw (de);
        } else {
        	//check for the parent containers status
        	// sjmittal: for fix of bfd 3363, check for the status of the container first
        	if("A".equals(status)) {

        		checkForParentStatus(document.containerID);
        	}

        }
    }

    /**
     * Validate the removal of the specified container.
     *
     * @param container The container to be removed.
     * @throws DocumentException If the container is not loaded.
     * @since Falcon
     */
    public void verifyRemoveContainer(Container container, String status) throws DocumentException {

        // first check security
        if("D".equals(status))
        	checkSecurity(container, Action.DELETE, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovecontainer.accessdenied.message"));
        else if("H".equals(status))
            checkSecurity(container, Action.REMOVE_DELETED, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovecontainer.accessdenied.message"));
        else if("A".equals(status))
            checkSecurity(container, Action.UNDO_DELETED, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovecontainer.accessdenied.message"));

        // is this document loaded
        if (!isLoaded(container)) {
            DocumentException de = new DocumentException("Remove failed because the container is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);
        } else if (container.isRoot()) {
            DocumentException de = new DocumentException(PropertyProvider.get("prm.global.javascript.removeroot.error.message"));
            de.setSeverity(ErrorLogger.LOW);
            de.setDisplayError(PropertyProvider.get("prm.global.javascript.removeroot.error.message"));
            de.log();

            throw de;
        } else {
        	//check for the parent containers status
        	// sjmittal: for fix of bfd 3363, check for the status of the parent object first
        	//only root container would have its parent = null
        	if("A".equals(status) && (container.containerID != null)) {

        		checkForParentStatus(container.containerID);
        	}
        }
        	
    }

	private void checkForParentStatus(String parentId) throws DocumentException {
		Container parentContainer = null;
		try {
			parentContainer = new Container(parentId, false);
			parentContainer.objectID = parentId;
			parentContainer.setUser(this.user);
			
		} catch (PersistenceException e) {
			
		    DocumentException de = new DocumentException("Remove failed because the parent container is not loaded");
		    de.setSeverity(ErrorLogger.CRITICAL);
		    de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
		    de.setDisplayError("Critical Error, please log in again.");
		    de.log();

		    throw (de);
		}
		
		if(parentContainer.getRecordStatus().equals(RecordStatus.DELETED)) {
		    DocumentException de = new DocumentException(PropertyProvider.get("prm.container.javascript.parentdelete.error.message", parentContainer.name));
		    de.setSeverity(ErrorLogger.LOW);
		    de.setDisplayError(PropertyProvider.get("prm.container.javascript.parentdelete.error.message", parentContainer.name));
		    de.log();

		    throw de;
		}
	}


	/**
     * Validate the removal of the specified bookmark.
     *
     * @param bookmark The bookmark to be removed.
     * @throws DocumentException If the bookmark is not loaded.
     * @since Falcon
     */
    public void verifyRemoveBookmark(Bookmark bookmark) throws DocumentException {

        // first check security
        checkSecurity(bookmark, Action.DELETE, PropertyProvider.get("prm.document.documentcontrolmanager.verifyremovebookmark.accessdenied.message"));

        // is this document loaded
        if (!isLoaded(bookmark)) {

            DocumentException de = new DocumentException();

            de.setMessage("Remove failed because the bookmark is not loaded");
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            de.setDisplayError("Critical Error, please log in again.");
            de.log();

            throw (de);
        }
    }


    public void verifyUpdateDocument(Document document) throws UpdateFailedException {


        // is this document loaded
        if (!isLoaded(document)) {

            UpdateFailedException ufe = new UpdateFailedException();

            ufe.setMessage("Update failed because the document is not loaded");
            ufe.setSeverity(ErrorLogger.CRITICAL);
            ufe.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            ufe.setDisplayError("Critical Error, please log in again.");
            ufe.log();

            throw (ufe);
        } // end if (not) loaded
        else if (isCheckedOut(document) && (!userMatches(user.getID(), document.getCheckedOutByID()))) {

            UpdateFailedException ufe = new UpdateFailedException();

            ufe.setMessage(PropertyProvider.get("prm.document.documentcontrolmanager.verifyupdatedocument.checkedout.message"));
            ufe.setDisplayError("You may not update the properties of a document which has been checked out by someone else.");
            ufe.setSeverity(ErrorLogger.LOW);
            ufe.setReasonCode(PnetExceptionTypes.UPDATE_FAILED_DOCUMENT_IS_CKO);

            throw (ufe);
        } // end if  checked out

    } // end verifyUpdateDocument


    public void verifyUpdateContainer(Container container) throws UpdateFailedException {


        // is this document loaded
        if (!isLoaded(container)) {

            UpdateFailedException ufe = new UpdateFailedException();

            ufe.setMessage("Update failed because the container is not loaded");
            ufe.setSeverity(ErrorLogger.CRITICAL);
            ufe.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            ufe.setDisplayError("Critical Error, please log in again.");
            ufe.log();

            throw (ufe);
        } // end if (not) loaded

    } // end verifyUpdateContainer


    public void verifyUpdateBookmark(Bookmark bookmark) throws UpdateFailedException {

        // is this document loaded
        if (!isLoaded(bookmark)) {

            UpdateFailedException ufe = new UpdateFailedException();

            ufe.setMessage("Update failed because the bookmark is not loaded");
            ufe.setSeverity(ErrorLogger.CRITICAL);
            ufe.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            ufe.setDisplayError("Critical Error, please log in again.");
            ufe.log();

            throw (ufe);
        }
    }

    /**
     * Validates whether all criteria (and business rules) are met for the
     * document checkout to be canceled.
     *
     * The method first verifies that all criteria for undo check out are met.
     * If they are (including security access permission), the undoCheckOut()
     * method is then invoked on the document passed in.
     *
     * @param document The document to be operated upon
     * @throws UndoCheckOutFailedException If the document is not loaded, if the
     * document is not checked out or if the user attempting to cancel the
     * checkout is not the user that checked it out in the first place.
     * @since The beginning of time
     */
    public void verifyUndoCheckOut(Document document) throws UndoCheckOutFailedException {

        // first check security
        checkSecurity(document, Action.MODIFY, "You do not have access to undo check out of the document");

        // is this document loaded
        if (!isLoaded(document)) {

            UndoCheckOutFailedException ucko = new UndoCheckOutFailedException();

            ucko.setMessage("Undo check out failed because the document is not loaded");
            ucko.setSeverity(ErrorLogger.CRITICAL);
            ucko.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            ucko.setDisplayError("Critical Error, please log in again.");
            ucko.log();

            throw (ucko);
        } else if (!isCheckedOut(document)) {

            UndoCheckOutFailedException ucko = new UndoCheckOutFailedException();

            ucko.setMessage("Undo check out failed because the document is not checked out");
            ucko.setDisplayError(PropertyProvider.get("prm.document.documentcontrolmanager.verifyundocheckout.notcheckedout.message"));
            ucko.setSeverity(ErrorLogger.LOW);
            ucko.setReasonCode(PnetExceptionTypes.UNDO_CHECK_OUT_FAILED_NOT_CHECKED_OUT);

            throw (ucko);
        }

        // skip the user mismatch constraint if the document has specified that the constraint
        // is to be ignored.  This will happen when a space admin overrides the checkout of another user.
        else if (!document.getIgnoreUserMismatchConstraint() &&
            !userMatches(this.user, document.getCheckedOutByID()) && !(this.user.isSpaceAdministrator())) {

            UndoCheckOutFailedException ucko = new UndoCheckOutFailedException();

            ucko.setMessage("Undo check out failed because the document was not checked out by you");
            ucko.setDisplayError(PropertyProvider.get("prm.document.documentcontrolmanager.verifyundocheckout.notcheckedoutbyyou.message"));
            ucko.setSeverity(ErrorLogger.LOW);
            ucko.setReasonCode(PnetExceptionTypes.UNDO_CHECK_OUT_FAILED_NOT_CKO_BY_USER);

            throw (ucko);
        }
    }

    /**
     * Validates whether all criteria (and business rules) are met for the
     * document to be checked out.
     *
     * The method first verifies that all criteria for checkout are met.  If
     * they are (including security access permission), the checkOut() method is
     * then invoked on the document passed in.
     *
     * @param document The document to be checked out.
     * @throws CheckOutFailedException If the document is not loaded or if the
     * document is already checked out.
     * @since The beginning of time
     */
    public void verifyCheckOut(Document document) throws CheckOutFailedException {

        // first verify security
        checkSecurity(document, Action.MODIFY, PropertyProvider.get("prm.document.documentcontrolmanager.verifycheckout.accessdenied.message"));

        // is this document loaded
        if (!isLoaded(document)) {

            CheckOutFailedException cofe = new CheckOutFailedException();

            cofe.setMessage("Check OUT failed because the document is not loaded");
            cofe.setSeverity(ErrorLogger.CRITICAL);
            cofe.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            cofe.setDisplayError("Critical Error, please log in again.");
            cofe.log();

            throw (cofe);
        } // end if (not) loaded
        else if (isCheckedOut(document)) {

            CheckOutFailedException cofe = new CheckOutFailedException();

            cofe.setMessage("Check out failed because the document is already checked out");
            cofe.setDisplayError(PropertyProvider.get("prm.document.documentcontrolmanager.verifycheckout.checkedout.message"));
            cofe.setSeverity(ErrorLogger.LOW);
            cofe.setReasonCode(PnetExceptionTypes.CHECK_OUT_FAILED_ALREADY_CHECKED_OUT);

            throw (cofe);
        } // end if checked out
    }

    /**
     * Validates whether all criteria (and business rules) are met for the
     * document to be checked in.
     *
     * The method first verifies that all criteria for checkin are met.  If they
     * are (including security access permission), the checkin() method is then
     * invoked on the document passed in.
     *
     * @param document The document to be checked in.
     * @throws CheckInFailedException Thrown if document is not loaded, the
     * document is not checked out or the user performing the operation is not
     * the user that checked the document out.
     * @since The beginning of time
     */
    public void verifyCheckIn(Document document) throws CheckInFailedException {

        // first verify security
        checkSecurity(document, Action.MODIFY, PropertyProvider.get("prm.document.documentcontrolmanager.verifycheckin.accessdenied.message"));
        
        // is this document loaded
        if (!isLoaded(document)) {

            CheckInFailedException cife = new CheckInFailedException();

            cife.setMessage("Check IN failed because the document is not loaded");
            cife.setSeverity(ErrorLogger.CRITICAL);
            cife.setReasonCode(PnetExceptionTypes.OBJECT_IS_NOT_LOADED);
            cife.setDisplayError("Critical Error, please log in again.");
            cife.log();

            throw (cife);
        } // end if (not) loaded
        else if (!isCheckedOut(document)) {

            CheckInFailedException cife = new CheckInFailedException();

            cife.setMessage("Check IN failed because the document is not checked out");
            cife.setDisplayError(PropertyProvider.get("prm.document.documentcontrolmanager.verifycheckin.notcheckedout.message"));
            cife.setSeverity(ErrorLogger.LOW);
            cife.setReasonCode(PnetExceptionTypes.CHECK_IN_FAILED_NOT_CHECKED_OUT);

            throw (cife);
        } else if (!userMatches(this.user, document.getCheckedOutByID())) {

            CheckInFailedException cife = new CheckInFailedException();

            cife.setMessage("Check IN failed because the document was not checked out by you");
            cife.setDisplayError(PropertyProvider.get("prm.document.documentcontrolmanager.verifycheckin.notcheckedoutbyyou.message"));
            cife.setSeverity(ErrorLogger.LOW);
            cife.setReasonCode(PnetExceptionTypes.CHECK_IN_FAILED_NOT_CKO_BY_USER);

            throw (cife);
        }
    }



    /* ------------------------------- Utility and Helper Methods  ------------------------------- */


    /**
     * Validate security permission/access to perform the requested action on
     * the object.
     *
     * @param objectID The ID of the object to be acted upon
     * @param moduleID The module the object belongs to
     * @param action The action the user is attempting to perform
     * @param message The message which will be displayed on the Interface
     * @throws AuthorizationFailedException If the user does not have the
     * required authorization or if the security check fails.
     * @see net.project.document.IContainerObject
     * @see net.project.security.Action
     * @since Gecko
     */
    public static void checkSecurity(String objectID, String moduleID, int action, String message) throws AuthorizationFailedException {

        SecurityProvider securityProvider = SecurityProvider.getInstance();
        boolean actionAllowed = securityProvider.isActionAllowed(objectID, moduleID, action);

        if (!actionAllowed) {
            throw new AuthorizationFailedException(message);
        }
    }

    /**
     * Validate security permission/access to perform the requested action on
     * the object. Note, the <code>net.project.base.Module</code>, in the case
     * of IContainerObjects, is derived -- there is no need to specify it.
     *
     * @param co An IContainerObject on which the user is attempting to act.
     * @param action The action the user is attempting to perform
     * @param message The message which will be displayed on the Interface
     * @throws AuthorizationFailedException If the user does not have the
     * required authorization or if the security check fails.
     * @see net.project.document.IContainerObject
     * @see net.project.security.Action
     * @see net.project.base.Module
     * @since Gecko
     */
    public void checkSecurity(IContainerObject co, int action, String message) throws AuthorizationFailedException {

        DocumentManager docManager = DocumentManager.getInstance();
        String moduleID;

        try {
            moduleID = Integer.toString(docManager.getModuleFromContainerID(co.getContainerID()));
        } catch (PersistenceException pe) {
            moduleID = null;
        }

        checkSecurity(co.getID(), moduleID, action, message);
    }


    public static boolean isParentContainer(String parentID, String childID) {

        DBBean db = new DBBean();
        boolean retval = false;

        try {

            db.prepareCall("{ ? = call DOCUMENT.IS_PARENT_CONTAINER(?,?) }");

            db.cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
            db.cstmt.setString(2, parentID);
            db.cstmt.setString(3, childID);

            db.executeCallable();



            // in this case, the rowtype is from pn_doc_container
            if (db.cstmt.getInt(1) == 1) {
                retval = true;
            } else {
                retval = false;
            }

        } // end try
        catch (SQLException sqle) {

            retval = false;
            Logger.getLogger(DocumentControlManager.class).error("DocumentControlManager.(): unable to execute stored function: " + sqle);

        } // end catch
        finally {
            db.release();
        } // end finally

        return retval;

    }


    public static boolean isCheckedOut(IManagedObject managedObject) {

        return (managedObject.isCheckedOut());

    } // isCheckedOut()


    public boolean userMatches(User user, String objectsUserID) {

        return userMatches(user.getID(), objectsUserID);

    } // end userMatches

    public static boolean userMatches(String currentUserID, String objectsUserID) {

        boolean isOK = false;

        if ((currentUserID != null) && (objectsUserID != null)) {

            if (currentUserID.equals(objectsUserID)) {
                isOK = true;
            }

        } // end if

        return isOK;

    } // end userMatches()


    public static boolean isLoaded(IContainerObject ico) {
        return (ico.isLoaded());
    } // end isLoaded()

} // end class DocumentControlManager

