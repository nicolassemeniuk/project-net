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
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateFormat;
import net.project.util.FileUtils;
import net.project.xml.XMLUtils;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

/**
 * The DocumentManager provides facilities for manipulating Document objects.
 */
public class DocumentManager implements Serializable {
	/** The unique name of the session singleton document manager instance */
	public static final String DOCUMENT_MANAGER_SESSION_OBJECT_NAME = "docManager";

	public static final String WIKI_SYSTEM_FOLDER_NAME = "_system.wiki.folder";

	public static final String BLOG_SYSTEM_FOLDER_NAME = "_system.blog.folder";

	public static final String DIRECTORY_SYSTEM_FOLDER_NAME = "_system.directory.folder";

	private boolean listDeleted = false;
	
	public boolean isWikiDocument = false;

	/**
	 * Returns the session-scoped DocumentManager object singleton.
	 * 
	 * @return the DocumentManager in use for this session
	 */
	public static DocumentManager getInstance() {
		return SessionManager.getDocumentManager();
	}

	/**
	 * Returns root document storage path for specified id. This is the absolute
	 * path to the <b>folder</b> containing the file for this object. This
	 * returns the same path as
	 * {@link FileManager#getCompleteDocumentStoragePath} however, it is much
	 * more efficient - only one query is used.
	 * 
	 * @param docID
	 *            the id of the document
	 * @return the storage path
	 * @throws PersistenceException
	 *             if there is a problem getting the root storage path
	 */
	static String getRootStoragePath(String docID) throws PersistenceException {

		String rootStoragePath = null;
		DBBean db = new DBBean();

		try {
			rootStoragePath = getRootStoragePath(db, docID);

		} catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).error(
					"DocumentManager.getRootStoragePath threw an SQLException: "
							+ sqle);
			throw new PersistenceException(
					"Error getting root storage path for document with id: "
							+ docID, sqle);

		} finally {
			db.release();

		}

		return rootStoragePath;
	}

	/**
	 * Returns root document storage path for specified id using the specified
	 * DBBean to perform the read in. <br>
	 * <b>Note:</b> Does NOT commit or rollback. This is the absolute path to
	 * the <b>folder<b> containing the file for this object. This returns the
	 * same path as {@link FileManager#getCompleteDocumentStoragePath} however,
	 * it is much more efficient - only one query is used.
	 * 
	 * @param docID
	 *            the id of the document
	 * @param db
	 *            the DBBean to use; useful when the document is created but not
	 *            yet committed.
	 * @return the storage path
	 * @throws SQLException
	 *             if there is a problem getting the root storage path
	 */
	static String getRootStoragePath(DBBean db, String docID)
			throws SQLException {

		return DocumentManager.loadCompleteStoragePath(db, docID).getRootPath();
	}

	/**
	 * Returns complete document storage path for specified id. This is the
	 * absolute path to the file for this object. This returns the same path as
	 * {@link FileManager#getCompleteDocumentStoragePath} however, it is much
	 * more efficient - only one query is used.
	 * 
	 * @param docID
	 *            the id of the document
	 * @return the storage path
	 * @throws PersistenceException
	 *             if there is a problem getting the complete path
	 */
	static String getCompleteDocumentStoragePath(String docID)
			throws PersistenceException {

		String completeStoragePath = null;
		DBBean db = new DBBean();

		try {
			completeStoragePath = getCompleteDocumentStoragePath(db, docID);

		} catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).error(
					"DocumentManager.getCompleteDocumentStoragePath threw an SQLException: "
							+ sqle);
			throw new PersistenceException(
					"Error getting complete storage path for document with id: "
							+ docID, sqle);

		} finally {
			db.release();

		}

		return completeStoragePath;
	}

	/**
	 * Returns complete document storage path for specified id using the
	 * specified DBBean to perform the read in. <br>
	 * <b>Note:</b> Does NOT commit or rollback. This is the absolute path to
	 * the file for this object. This returns the same path as
	 * {@link FileManager#getCompleteDocumentStoragePath} however, it is much
	 * more efficient - only one query is used.
	 * 
	 * @param docID
	 *            the id of the document
	 * @return the storage path
	 * @throws SQLException
	 *             if there is a problem accessing the database
	 */
	static String getCompleteDocumentStoragePath(DBBean db, String docID)
			throws SQLException {

		return DocumentManager.loadCompleteStoragePath(db, docID).toString();
	}

	/**
	 * Loads the complete storage path object.
	 * 
	 * @param db
	 *            the DBBean containing the open connection from which to read
	 *            the complete storage path; this enables constructing storage
	 *            path for documents not yet committed in persistence store
	 * @param docID
	 *            the id of the document to load storage path for
	 * @throws SQLException
	 *             if there is a problem reading the path
	 */
	private static CompleteStoragePath loadCompleteStoragePath(DBBean db,
			String docID) throws SQLException {

		StringBuffer query = new StringBuffer();
		DocumentManager.CompleteStoragePath path = new CompleteStoragePath();

		query
				.append("select d.repository_path, shds.space_id, d.doc_space_id, d.file_handle ");
		query
				.append("from pn_doc_by_space_view d, pn_space_has_doc_space shds ");
		query.append("where d.doc_id = ? ");
		query.append("and shds.doc_space_id = d.doc_space_id ");

		int index = 0;
		db.prepareStatement(query.toString());
		db.pstmt.setString(++index, docID);
		db.executePrepared();

		if (db.result.next()) {
			path.repositoryPath = db.result.getString("repository_path");
			path.spaceID = db.result.getString("space_id");
			path.docSpaceID = db.result.getString("doc_space_id");
			path.storageID = docID;
			path.fileHandle = db.result.getString("file_handle");

		} else {
			Logger.getLogger(DocumentManager.class).error(
					"DocumentManager.getCompleteDocumentStoragePath "
							+ "found no storage path for document with id: "
							+ docID);
			throw new SQLException(
					"Unable to find storage path for document with id: "
							+ docID);

		}

		return path;
	}

	/**
	 * Helper method to construct the root storage path as a string based on the
	 * supplied path information.
	 * 
	 * @param repositoryBasePath
	 *            the path to the repository that object is in
	 * @param spaceID
	 *            the space that object belongs to
	 * @param docSpaceID
	 *            the doc space that object belongs to
	 * @param storageID
	 *            the storage path id for object
	 * @return the root storage path
	 */
	static String makeRootStoragePath(String repositoryBasePath,
			String spaceID, String docSpaceID, String storageID) {

		// the storage hierarchy is as follows:
		// repository base path -> space_id -> doc_space_id -> storageID (i.e.
		// doc id) -> file_handle
		DocumentManager.RootStoragePath path = new RootStoragePath();

		path.repositoryPath = repositoryBasePath;
		path.spaceID = spaceID;
		path.docSpaceID = docSpaceID;
		path.storageID = storageID;

		return path.toString();
	}

	//
	// Instance variables
	//

	private User user = null;

	private Container currentContainer = null;

	private IContainerObject currentObject = null;

	private DocumentSpace docSpace = null;

	// this is a HACK
	private String currentSpaceID = null;

	private Space currentSpace = null;

	private String currentObjectID = null;

	private final Hashtable navigator = new Hashtable();

	private String cancelPage = null;

	/**
	 * Indicates whether currently visiting a system container. This is used to
	 * decide whether to reset to the workspace's root container when navigating
	 * to the main document vault
	 */
	private boolean isVisitSystemContainer = false;

	private String containerSortBy = null;

	private String containerSortOrder = null;

	private final Hashtable containerCache = new Hashtable();

	private final Hashtable objectTypeCache = new Hashtable();

	//
	// Constructors
	//

	/**
	 * Creates a new, empty DocumentManager.
	 */
	public DocumentManager() {
		// do nothing
	}

	//
	// Instance methods
	//

	/**
	 * Adds a file to a space. The file is assumed to be already uploaded with
	 * the file location specified by <code>fileParam</code>.
	 * 
	 * @param spaceID
	 *            the id of the space to add the file to
	 * @throws PnetException
	 *             if there is a problem adding the file
	 */
	public String addFileToSpace(long contentLength, String clientFilePath,
			String writtenFilePath, String contentType, String spaceID)
			throws PnetException {

		setSpace(spaceID);
		Document document = upload(contentLength, clientFilePath,
				writtenFilePath, contentType);
		return addDocumentToSpace(document, spaceID);
	}
	
	/**
	 * Function to upload wiki image 
	 * Adds a file to a space. The file is assumed to be already uploaded with
	 * the file location specified by <code>fileParam</code>.
	 * 
	 * @param spaceID
	 *            the id of the space to add the file to
	 * @param isWikiDocument
	 *           for wiki image uplaod              
	 * @throws PnetException
	 *             if there is a problem adding the file
	 */
	public String addFileToSpace(long contentLength, String clientFilePath,
			String writtenFilePath, String contentType, String spaceID, boolean isWikiDocument)
			throws PnetException {
		this.isWikiDocument = isWikiDocument;
		return addFileToSpace(contentLength, clientFilePath, writtenFilePath, contentType, spaceID);
	}

	/**
	 * Adds the document to the space with the specified ID.
	 * 
	 * @param document
	 *            the document to add
	 * @param spaceID
	 *            the id of the space to which to add the document
	 * @throws PnetException
	 *             if there is a problem getting the system container id for the
	 *             space or there is a problem storing or adding the document to
	 *             the system
	 */
	private String addDocumentToSpace(Document document, String spaceID)
			throws PnetException {

		String spaceContainerID = getSystemContainerIDForSpace(spaceID);

		document.setName(document.getShortFileName());
		document.setContainerID(spaceContainerID);
		document.setAuthorID(getUser().getID());
		document.setStatusID(Document.STATUS_COMPLETE);
		document.setTypeID(Document.TYPE_GENERAL);
		document.setUser(getUser());
		document.setIgnoreNameConstraint(true);
		document.setWikiDocument(this.isWikiDocument);
		// store the temporary record in the database (just in case)
		document.tmpStore(document.getID());
		// add the document to the system, in the current container
		// if it was successfully saved to the server's storage
		document.add();

		return document.getID();
	}

	private String addDocumentToSpace(Document document, String spaceID,
			int module) throws PnetException {

		String spaceContainerID = getSystemContainerIDForSpace(spaceID);

		document.setName(document.getShortFileName());
		if (spaceContainerID == null)
			document.setContainerID(getCustomContainerIDForModule(spaceID,
					module));
		else
			document.setContainerID(spaceContainerID);
		document.setAuthorID(getUser().getID());
		document.setStatusID(Document.STATUS_COMPLETE);
		document.setTypeID(Document.TYPE_GENERAL);
		document.setUser(getUser());
		document.setIgnoreNameConstraint(true);

		// store the temporary record in the database (just in case)
		document.tmpStore(document.getID());
		// add the document to the system, in the current container
		// if it was successfully saved to the server's storage
		document.add();

		return document.getID();
	}

	public String getCustomContainerIDForModule(String spaceId, int module)
			throws PnetException {
		String containerId = null;

		if (module == Module.DIRECTORY) {
			containerId = getSystemContainerIDForSpace(spaceId,
					DIRECTORY_SYSTEM_FOLDER_NAME, false);
			if (containerId == null)
				containerId = createSystemContainerForSpace(spaceId,
						DIRECTORY_SYSTEM_FOLDER_NAME, getCurrentContainerID());
		} else if (module == Module.BLOG) {
			containerId = getSystemContainerIDForSpace(spaceId,
					BLOG_SYSTEM_FOLDER_NAME, false);
			if (containerId == null)
				containerId = createSystemContainerForSpace(spaceId,
						BLOG_SYSTEM_FOLDER_NAME, getCurrentContainerID());
		} else if (module == Module.WIKI) {
			containerId = getSystemContainerIDForSpace(spaceId,
					WIKI_SYSTEM_FOLDER_NAME, false);
			if (containerId == null)
				containerId = createSystemContainerForSpace(spaceId,
						WIKI_SYSTEM_FOLDER_NAME, getCurrentContainerID());
		}

		return containerId;
	}

	public String addFileToSpace(long contentLength, String clientFilePath,
			String writtenFilePath, String contentType, String spaceId,
			int module) throws PnetException {
		setSpace(spaceId);

		Document document = upload(contentLength, clientFilePath,
				writtenFilePath, contentType);
		return addDocumentToSpace(document, spaceId, module);
	}

	/**
	 * Creates a new document for an uploaded file. The file is assumed to be
	 * already uploaded with the file location specified by
	 * <code>fileParam</code>.
	 * 
	 * @param fileParam
	 *            the information about the uploaded file
	 * @return a Document representing the uploaded file
	 * @throws FileUploadStorageException
	 *             if there is a problem storing the uploaded file
	 * @throws DocumentException
	 */
	public Document upload(long contentLength, String clientFilePath,
			String writtenFilePath, String contentType)
			throws FileUploadStorageException, DocumentException {

		return upload(new Document(), contentLength, clientFilePath,
				writtenFilePath, contentType);
	}

	/**
	 * Adds an uploaded file to a document. The file is assumed to be already
	 * uploaded with the file location specified by <code>fileParam</code>.
	 * 
	 * @param document
	 *            the document with which to associate the uploaded file
	 * @param contentLength
	 *            the size of the document
	 * @param clientFilePath
	 *            the location on the clients machine where the file was loaded
	 *            from.
	 * @param writtenFilePath
	 *            the location of the file that is available to this jvm.
	 * @param contentType
	 *            the mime type of the document.
	 * @return the Document to which the uploaded file was added
	 * @throws FileUploadStorageException
	 *             if there is a problem storing the uploaded file
	 * @throws DocumentException
	 */
	public Document upload(Document document, long contentLength,
			String clientFilePath, String writtenFilePath, String contentType)
			throws FileUploadStorageException, DocumentException {

		return new FileManager().upload(this, document, contentLength,
				clientFilePath, writtenFilePath, contentType);
	}

	public void move(String objectID, String containerID)
			throws DocumentException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.move(objectID, containerID);

	}

	public void viewVersion(Document document, String versionID,
			HttpServletResponse response) throws DocumentException,
			PersistenceException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.viewVersion(document, versionID, response);

	}

	public void checkInDocument(Document document) throws PnetException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.checkIn(document);

	}

	public void checkOutDocument(Document document) throws PnetException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.checkOut(document);

	}

	public void undoCheckOutDocument(Document document) throws PnetException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.undoCheckOut(document);

	}

	public void modifyObject(IContainerObject co) throws PnetException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.modify(co);

	}

	/**
	 * Removes the container object.
	 * <p>
	 * If the object is a container then it is removed recursively; that is all
	 * contained objects will be removed.
	 * </p>
	 * 
	 * @param co
	 *            the container object
	 * @throws PnetException
	 *             if there is a problem removing
	 */
	public void removeObject(IContainerObject co, String status)
			throws PnetException {

		DocumentControlManager dcm = new DocumentControlManager();

		dcm.setUser(getUser());
		dcm.remove(co, status);

	}

	/**
	 * Copies a document space that is in the specified "from" space to the
	 * specified "to" space.
	 * 
	 * @param fromSpaceID
	 *            the id of the space containing the doc space to copy
	 * @param toSpaceID
	 *            the id of the space to which to copy the doc space
	 * @throws DocumentException
	 *             if there is a problem copying the document space. Note that
	 *             the entire document space will have been processed; it does
	 *             NOT abort on the first error
	 */
	public void copyDocSpace(String fromSpaceID, String toSpaceID)
			throws DocumentException {
		// Get the document spaces for the from space and to space
		DocumentSpace fromDocSpace = getDefaultDocumentSpace(fromSpaceID);
		DocumentSpace toDocSpace = getDefaultDocumentSpace(toSpaceID);

		fromDocSpace.copyContents(toDocSpace, SessionManager.getUser());
	}

	/**
	 * **************************************************************************************************************
	 * **** State/Session management methods *****
	 * ***************************************************************************************************************
	 */

	public Hashtable getNavigator() {
		return this.navigator;
	}

	public void setCancelPage(String cancelPage) {
		this.cancelPage = cancelPage;
	}

	public String getCancelPage() {
		return this.cancelPage;
	}

	// sjmittal (96/14/2007): I am not sure why this method creates the object
	// when method getCurrentObject already does that job!!!
	// this would result in duplicate calls to create an object
	// and may impact the performance.
	// commenting this as of now
	public void setCurrentObjectID(String objectID) {

		// ContainerObjectFactory factory = null;
		// if(listDeleted == true)
		// factory = new ContainerObjectFactory(true);
		// else
		// factory = new ContainerObjectFactory();
		// IContainerObject co;

		this.currentObjectID = objectID;

		// try {
		// co = factory.makeObject(objectID);
		// setCurrentObject(co);
		// } catch (PersistenceException pe) {
		// Logger.getLogger(DocumentManager.class).debug("DocumentMangager.setCurrentObjectID()
		// failed when trying to load the object: " + pe);
		// }

	} // setCurrentObjectID()

	public void setCurrentObject(IContainerObject currentObject) {
		this.currentObject = currentObject;
	}

	public String getCurrentObjectID() {
		return this.currentObjectID;
	}

	public void setListDeleted() {
		this.listDeleted = true;
	}

	public void unSetListDeleted() {
		this.listDeleted = false;
	}

	public IContainerObject getCurrentObject() {

		IContainerObject obj = null;

		if (this.currentObject == null) {
			if (currentContainer != null) {
				obj = currentContainer.getElement(this.currentObjectID);
			} else {
				Logger
						.getLogger(DocumentManager.class)
						.debug(
								"DocumentManager.getCurrentObject():  currentContainer is NULL");
			}
		} else if (this.currentObjectID != null) {
			ContainerObjectFactory factory = null;
			if (listDeleted == true)
				factory = new ContainerObjectFactory(true);
			else
				factory = new ContainerObjectFactory();
			IContainerObject co;

			try {
				co = factory.makeObject(this.currentObjectID);
				setCurrentObject(co);
			} catch (PersistenceException pe) {
				Logger.getLogger(DocumentManager.class).debug(
						"DocumentMangager.getCurrentObject() failed when trying to load the object: "
								+ pe);
			}
			obj = this.currentObject;
		}
		return obj;
	} // getCurrentObject()

	private void setSpace(String spaceID) {

		try {
			Space space = SpaceFactory.constructSpaceFromID(spaceID);
			space.load();

			setSpace(space);
		} catch (PersistenceException pe) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.setSpace(String) threw a PersistanceException: "
							+ pe);
		}

	} // end setSpace

	public void setSpace(Space space) {

		boolean spaceHasChanged;

		if (currentSpaceID != null) {
			spaceHasChanged = !(currentSpaceID.equals(space.getID()));
		} else {
			spaceHasChanged = true;
		}

		if (spaceHasChanged) {

			this.currentSpace = space;

			// THIS IS A HUGE HACK WHICH WILL BE REPLACED BY GETTING A "COPY" OF
			// THE SPACE AS A LOCAL INSTANCE VARIABLE, RATHER THAN A REFERENCE
			this.currentSpaceID = space.getID();

			this.currentObjectID = null;
			this.docSpace = null;
			this.currentContainer = null;

		} // end if

	} // end setSpace()

	public Space getSpace() {
		return this.currentSpace;
	}

	public void setDocumentSpace(DocumentSpace docSpace) {
		this.docSpace = docSpace;
	}

	public DocumentSpace getDocumentSpace() {

		// if there is no document space, you must at LEAST have the current
		// "space" set

		if (this.docSpace == null) {

			// this assumes that currentSpace has been set
			this.docSpace = getDefaultDocumentSpace();

		}

		return this.docSpace;

	} // end getDocumentSpace

	public String getDocumentSpaceID() {

		if (this.docSpace == null) {
			this.docSpace = getDefaultDocumentSpace();
		}

		return this.docSpace.getID();
	}

	/**
	 * Set the container id that will be the root for this document space. If
	 * this is being set, it is probably an artifical path being made to hide
	 * some part of the path. (For example, if we are browsing a system folder.)
	 * 
	 * @param containerID
	 *            a <code>String</code> value containing the id of a container
	 *            that we want to be our new "Top Folder".
	 */
	public void setRootContainerID(String containerID) {
		this.docSpace.setRootContainerID(containerID);
	}

	public String getRootContainerID() {
		return (this.docSpace.getRootContainerID());
	}

	/**
	 * Get default document space for a give space. this right now is broken,
	 * because it assumes one and only one doc space per space. MUST FIX
	 */
	public DocumentSpace getDefaultDocumentSpace() {
		return getDefaultDocumentSpace(this.currentSpace.getID());
	}

	public DocumentSpace getDefaultDocumentSpace(String spaceID) {

		DBBean db = new DBBean();
		String docSpaceID = null;
		String qstrGetDefaultDocSpaceBySpaceID = "select doc_space_id from pn_space_has_doc_space where space_id = "
				+ spaceID;

		try {

			// first get the properties for THIS container
			db.executeQuery(qstrGetDefaultDocSpaceBySpaceID);

			if (db.result.next()) {
				docSpaceID = db.result.getString("doc_space_id");
			}

		} catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getDefaultDocumentSpace() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		}

		// now that we have the ID, let's create the doc space
		this.docSpace = new DocumentSpace(docSpaceID);

		return (this.docSpace);
	}

	/**
	 * Get the default container for a particular docSpace. if there is no doc
	 * space, error out (should throw)
	 */
	private Container getDefaultContainer() {
		Container defaultContainer;

		// if the doc space isn't null set default container to the root
		// for the doc space
		if (this.docSpace != null) {
			defaultContainer = this.docSpace.getRootContainer();
		} else {
			this.docSpace = getDefaultDocumentSpace();
			defaultContainer = this.docSpace.getRootContainer();
		}

		if (this.currentContainer == null) {
			if (listDeleted)
				defaultContainer.setListDeleted();
			else
				defaultContainer.unSetListDeleted();
			this.currentContainer = defaultContainer;
		}

		return defaultContainer;
	}

	private String getSystemContainerIDForSpace(String spaceID)
			throws PersistenceException {
		String containerID = (String) containerCache.get(spaceID);
		if (containerID != null) {
			return containerID;
		}

		String qstrGetSystemContainerID = "select doc_container_id from pn_doc_container"
				+ " where container_name = '" + spaceID + "'";

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrGetSystemContainerID);

			if (db.result.next()) {
				containerID = db.result.getString("doc_container_id");
			} // end if
			else if (user.getCurrentSpace().getType().equals(
					Space.PROJECT_SPACE)) {
				throw new PersistenceException("Corrupt document space");
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getSystemContainerIDForSpace() threw a SQL Exception: "
							+ sqle);
		} finally {
			db.release();
		}

		if (containerID != null) {
			containerCache.put(spaceID, containerID);
		}
		return containerID;
	}

	/**
	 * Get a system container for a certain container type.
	 * 
	 * @param spaceID
	 *            a <code>String</code> value
	 * @param containerType
	 *            a <code>ContainerType</code> value
	 * @return a <code>String</code> value
	 * @throws PersistenceException
	 *             if an error occurs
	 */
	public String getSystemContainerIDForSpace(String spaceID,
			ContainerType containerType) throws PersistenceException {
		String folderName = containerType.getFolderName(spaceID);
		return getSystemContainerIDForSpace(spaceID, folderName, false);
	}

	/**
	 * Get a system container for a certain container type.
	 * 
	 * @param spaceID
	 *            a <code>String</code> value
	 * @param containerType
	 *            a <code>ContainerType</code> value
	 * @param createIfNonExistent
	 *            a <code>boolean</code> value that indicates we should create
	 *            the system container if it doesn't already exist.
	 * @return a <code>String</code> value
	 * @throws PersistenceException
	 *             if an error occurs
	 */
	public String getSystemContainerIDForSpace(String spaceID,
			ContainerType containerType, boolean createIfNonExistent)
			throws PersistenceException {
		String folderName = containerType.getFolderName(spaceID);
		return getSystemContainerIDForSpace(spaceID, folderName,
				createIfNonExistent);
	}

	/**
	 * Get a system container for a given space.
	 * 
	 * @param spaceID
	 *            a <code>String</code> value
	 * @param folderName
	 *            a <code>String</code> value
	 * @param createIfNonExistent
	 *            a <code>boolean</code> value that indicates we should create
	 *            the system container if it doesn't already exist.
	 * @return a <code>String</code> value
	 * @throws PersistenceException
	 *             if an error occurs
	 */
	public String getSystemContainerIDForSpace(String spaceID,
			String folderName, boolean createIfNonExistent)
			throws PersistenceException {

		String containerID = (String) containerCache.get(folderName);
		if (containerID != null) {
			return containerID;
		}

		String qstrGetBudgetFolderForSpace = "select c.doc_container_id, c.container_name"
				+ " from pn_doc_container c, pn_space_has_doc_space shds, pn_doc_space_has_container dshc"
				+ " where shds.space_id = "
				+ spaceID
				+ " and dshc.doc_space_id = shds.doc_space_id and c.doc_container_id = dshc.doc_container_id"
				+ " and c.container_name = '" + folderName + "'";

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrGetBudgetFolderForSpace);

			if (db.result.next()) {
				containerID = db.result.getString("doc_container_id");
			} else {
				if (createIfNonExistent) {
					containerID = createSystemContainerForSpace(spaceID,
							folderName);
				} else if ((user.getCurrentSpace().getType()
						.equals(Space.PROJECT_SPACE))
						&& (!folderName.equals(ContainerType.SYSTEM_CONTAINER
								.getFolderName(spaceID)))) {
					// This is older behavior inherited from the previous
					// method.
					// If there isn't a system container for a form, it means it
					// wasn't created during form
					// creation, which would be a problem.
					throw new PersistenceException("Corrupt document space");
				}
			}
		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getSystemContainerIDForSpace() threw a SQL Exception: "
							+ sqle);
			throw new PersistenceException(
					"Could not retrieve System Container ID", sqle);
		} finally {
			db.release();
		}
		if (containerID != null) {
			containerCache.put(folderName, containerID);
		}
		return containerID;

	}

	/**
	 * Create the system container for the specified space and container type.
	 * This method assumes that there isn't already a container of this type. If
	 * this container exists, a PersistenceException will be thrown.
	 * 
	 * @param spaceID
	 *            a <code>String</code> value containing the id of the space
	 *            for which we are creating the system container.
	 * @param folderName
	 *            the path to the folder we are going to create.
	 * @return a <code>String</code> value containing the id of the newly
	 *         created system folder.
	 * @throws PersistenceException
	 *             if an error occurs while saving the new container.
	 */
	private String createSystemContainerForSpace(String spaceID,
			String folderName) throws PersistenceException {
		try {
			Container container = new Container();
			container.setUser(SessionManager.getUser());
			container.setName(folderName);
			container.setContainerID(getSystemContainerIDForSpace(spaceID));
			container.setDescription("System container for this space");
			container.setIsSystem("1");

			return container.create();
		} catch (PnetException pnete) {
			Logger.getLogger(DocumentManager.class).error(
					"PnetException thrown while trying to create a container.");
			throw new PersistenceException(
					"Unable to create new folder due to an unexpected "
							+ "error.", pnete);
		}
	}

	private String createSystemContainerForSpace(String spaceID,
			String folderName, String containerId) throws PersistenceException {
		try {
			Container container = new Container();
			container.setUser(SessionManager.getUser());
			container.setName(folderName);
			container.setContainerID(containerId);
			container.setDescription("System container for this space");
			container.setIsSystem("1");

			return container.create();
		} catch (PnetException pnete) {
			Logger.getLogger(DocumentManager.class).error(
					"PnetException thrown while trying to create a container.");
			throw new PersistenceException(
					"Unable to create new folder due to an unexpected "
							+ "error.", pnete);
		}
	}

	public int getModuleFromContainerID(String containerid)
			throws PersistenceException {

		if (containerid
				.equals(getSystemContainerIDForSpace(this.currentSpaceID))) {

			if (this.currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
				return Module.BUSINESS_SPACE;
			} else if (this.currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
				return Module.PROJECT_SPACE;
			} else {
				return Module.DOCUMENT;
			}
		} else {
			if (listDeleted)
				return Module.TRASHCAN;
			else
				return Module.DOCUMENT;
		}
	}

	public String getCurrentContainerID() {

		if (this.currentContainer == null) {
			this.currentContainer = getDefaultContainer();
		}

		return (currentContainer.getID());
	}

	/**
	 * this method returns the current container in scope if a current container
	 * hasn't been set, this method returns the default container for this doc
	 * space (top level container)
	 */
	public Container getCurrentContainer() {

		// if no container is currently in scope then set it to the default
		// container for this docSpace
		// and return it, otherwise, return the currentContainer

		if (this.currentContainer == null) {
			this.currentContainer = getDefaultContainer();
		}

		return this.currentContainer;
	}

	public void setCurrentContainer(String containerID) {
		this.currentContainer = new Container(containerID);
		if (listDeleted)
			this.currentContainer.setListDeleted();
		else
			this.currentContainer.unSetListDeleted();
		
		if(this.docSpace == null) {
			this.docSpace = new DocumentSpace(containerID);
		}
	}

	public void setCurrentContainer(Container container) {
		this.currentContainer = container;
		if (listDeleted)
			this.currentContainer.setListDeleted();
		else
			this.currentContainer.unSetListDeleted();

	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		User theUser;

		if (this.user != null) {
			theUser = user;
		} else {
			theUser = SessionManager.getUser();
		}

		return theUser;
	}

	public void setContainerSortBy(String sortBy) {

		if (sortBy.equals(this.containerSortBy)) {
			flipContainerSortOrder();
		} else {
			this.containerSortOrder = null;
		}

		this.containerSortBy = sortBy;
	} // end setContainerSortBy

	public String getContainerSortBy() {

		return this.containerSortBy;
	}

	public String getContainerSortOrder() {
		if (this.containerSortOrder == null) {
			flipContainerSortOrder();
		}

		return this.containerSortOrder;
	}

	private static String getDefaultContainerSortOrder() {
		return ContainerEntrySort.getDefaultSortOrder();
	}

	private void flipContainerSortOrder() {

		String order = this.containerSortOrder;

		if (order == null) {
			this.containerSortOrder = getDefaultContainerSortOrder();
		} else if (order.equals(ContainerEntrySort.ORDER_ASC)) {
			this.containerSortOrder = ContainerEntrySort.ORDER_DESC;
		} else if (order.equals(ContainerEntrySort.ORDER_DESC)) {
			this.containerSortOrder = ContainerEntrySort.ORDER_ASC;
		} else {
			this.containerSortOrder = getDefaultContainerSortOrder();
		}

	} // end flipContainerSortOrder

	/**
	 * Specifies whether currently visiting a system container.
	 * 
	 * @param isVisitSystemContainer
	 *            true if the current container is a system container; false
	 *            otherwise
	 */
	public void setVisitSystemContainer(boolean isVisitSystemContainer) {
		this.isVisitSystemContainer = isVisitSystemContainer;
	}

	/**
	 * Indicates whether currently visiting a system container. Used to
	 * determine when to reset the document vault to the workspace's root
	 * container.
	 * 
	 * @return true if currently visiting a system container; false otherwise
	 */
	public boolean isVisitSystemContainer() {
		return this.isVisitSystemContainer;
	}

	public String getXMLDocsCheckedOutByUser() {

		DBBean tmpDB = new DBBean();
		StringBuffer xml = new StringBuffer();
		xml.ensureCapacity(1000);

		String objectID;

		// BFD-2018
		// Removed pn_space_view join out of pn_doc_by_space_view since this is
		// the only code
		// location that requires it; (despite the name, pn_doc_by_space_view is
		// a view of a document by
		// doc_space, not by space); then took advantage of fact that pn_object
		// record_status is now being
		// kept updated by triggers. Resulting select has a lower cost and
		// faster execution
		final String qstrSelectDocsCKOByUser = "select "
				+ "dsv.doc_id, dsv.doc_name, dsv.doc_status, dsv.checked_out_by, dsv.object_type, "
				+ "dsv.checked_out_by_id, dsv.date_checked_out, dsv.CHECKOUT_DUE cko_due, dsv.format_name, dsv.app_icon_url "
				+ "from pn_doc_by_space_view dsv, pn_space_has_doc_space shds, pn_object ob "
				+ "where dsv.checked_out_by_id = ? "
				+ "and shds.doc_space_id = dsv.doc_space_id "
				+ "and ob.object_id = shds.space_id "
				+ "and ob.record_status = ? " + "order by lower(doc_name)";

		xml.append(IXMLPersistence.XML_VERSION);

		DBBean db = new DBBean();
		try {

			// first get the docs checked out for this user
			db.prepareStatement(qstrSelectDocsCKOByUser);

			// at the same time prepare the call to get the space info
			// associated with this document
			tmpDB
					.prepareCall("{ ? = call DOCUMENT.GET_PARENT_SPACE_FOR_OBJECT (?) }");

			int index = 0;
			db.pstmt.setInt(++index, Integer.parseInt(this.user.getID()));
			db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
			db.executePrepared();

			xml.append("<document_list>");

			while (db.result.next()) {

				xml.append("<document>");

				objectID = db.result.getString("doc_id");

				xml.append("<name>"
						+ XMLUtils.escape(db.result.getString("doc_name"))
						+ "</name>\n");
				xml.append("<path>" + XMLUtils.escape(getPath(objectID))
						+ "</path>\n");
				xml.append("<object_id>" + XMLUtils.escape(objectID)
						+ "</object_id>\n");
				xml.append("<object_type>"
						+ XMLUtils.escape(db.result.getString("object_type"))
						+ "</object_type>\n");
				xml.append("<status>"
						+ XMLUtils.escape(PropertyProvider.get(db.result
								.getString("doc_status"))) + "</status>\n");
				xml.append("<format>"
						+ XMLUtils.escape(PropertyProvider.get(db.result
								.getString("format_name"))) + "</format>\n");
				xml.append("<app_icon_url>"
						+ XMLUtils.escape(db.result.getString("app_icon_url"))
						+ "</app_icon_url>\n");
				// xml.append ("<cko_by>" + XMLUtils.escape (
				// db.result.getString("checked_out_by") ) + "</cko_by>\n");
				xml.append("<cko_date>"
						+ DateFormat.getInstance().formatDateMedium((Date) db.result
								.getTimestamp("date_checked_out"))
						+ "</cko_date>\n");
				xml.append("<cko_return>"
						+ DateFormat.getInstance().formatDateMedium((Date) db.result
								.getTimestamp("cko_due")) + "</cko_return>\n");
				xml.append("<url>"
						+ URLFactory.makeURL(db.result.getString("doc_id"),
								ObjectType.DOCUMENT) + "</url>\n");

				// now get/execute the
				tmpDB.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
				tmpDB.cstmt.setString(2, objectID);

				tmpDB.executeCallable();

				tmpDB.result = (ResultSet) tmpDB.cstmt.getObject(1);

				if (tmpDB.result.next()) {
					xml.append("<space_name>"
							+ XMLUtils.escape(tmpDB.result.getString("name"))
							+ "</space_name>\n");
					xml.append("<space_id>"
							+ XMLUtils.escape(tmpDB.result
									.getString("object_id")) + "</space_id>\n");
					xml.append("<space_type>"
							+ XMLUtils.escape(tmpDB.result
									.getString("object_type"))
							+ "</space_type>\n");
				}

				xml.append("</document>");

			} // end while

			xml.append("</document_list>");
		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getXMLDocsCheckedOutByUser() threw a PersistenceException: "
							+ sqle);
		} // end catch
		finally {
			db.release();
			tmpDB.release();
		} // end finally

		return xml.toString();

	} // end getXMLDOcsCheckedOutByUser()

	/**
	 * **************************************************************************************************************
	 * **** Utility methods *****
	 * ***************************************************************************************************************
	 */

	public static String getStoragePathForContainerObject(String objectID) {
		DBBean db = new DBBean();
		String storagePath = null;
		String qstrGetPath = "select old_storage_id from pn_document where doc_id = "
				+ objectID;

		try {
			db.executeQuery(qstrGetPath);

			if (db.result.next()) {
				storagePath = db.result.getString("old_storage_id");
			}
		} catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getStoragePathForContainerObject() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		} // end finally

		if ((storagePath == null) || storagePath.equals("")) {
			storagePath = objectID;
		}

		return storagePath;
	}

	public static String getSpaceIDForContainerObject(String objectID) {

		DBBean db = new DBBean();
		String spaceID = null;
		String tmpDocSpaceID;
		String qstrGetSpaceID;

		tmpDocSpaceID = getDocSpaceIDForContainerObject(objectID);

		qstrGetSpaceID = "select space_id from pn_space_has_doc_space"
				+ " where doc_space_id = " + tmpDocSpaceID;

		try {

			db.executeQuery(qstrGetSpaceID);

			if (db.result.next()) {
				spaceID = db.result.getString("space_id");
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getSpaceIDForContainerObject() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		} // end finally

		return spaceID;

	} // end getSpaceIDForContainerObject()

	public static DocumentSpace getDocSpaceForContainerObject(String containerID) {

		DocumentSpace docSpace = new DocumentSpace();
		docSpace.setID(getDocSpaceIDForContainerObject(containerID));

		return docSpace;
	}

	public static String getDocSpaceIDForContainerObject(String objectID) {

		DBBean db = new DBBean();
		String docSpaceID = null;
		String qstrGetDocSpaceID;
		String tmpContainerID;

		if (isObjectTypeOf(objectID, ObjectType.CONTAINER)) {
			tmpContainerID = objectID;
		} else {
			tmpContainerID = getContainerID(objectID);
		}

		qstrGetDocSpaceID = "select doc_space_id from pn_doc_space_has_container"
				+ " where doc_container_id = " + tmpContainerID;

		try {

			db.executeQuery(qstrGetDocSpaceID);

			if (db.result.next()) {
				docSpaceID = db.result.getString("doc_space_id");
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getDocSpaceIDForContainerObject() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		} // end finally

		return docSpaceID;

	} // end getDocSpaceIDForContainerObject()

	public static String getPath(String objectID) {
		Path path = new Path(objectID);

		return path.toString();
	} // end getPath();

	public String getDocFormatID(String mimeType, String shortFileName) {

		String formatID = null;
		String ext = FileUtils.getFileExt(shortFileName);

		String qstrGetFormatIDByExt = "select doc_format_id, format_name from pn_doc_format where file_ext = lower('"
				+ ext + "')";
		String qstrGetFormatIDByMime = "select doc_format_id, format_name from pn_doc_format where mime_type = lower('"
				+ mimeType + "')";

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrGetFormatIDByExt);

			if (db.result.next()) {
				formatID = db.result.getString("doc_format_id");
			} // end if

			if (formatID == null || formatID.equals("")) {

				db.executeQuery(qstrGetFormatIDByMime);

				if (db.result.next()) {
					formatID = db.result.getString("doc_format_id");
				}

			} // end if

			if (formatID == null || formatID.equals("")) {
				formatID = "-999";
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getDocFormatID() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		}

		return formatID;

	} // end getDocFormatID

	public static String getContainerID(String objectID) {

		DBBean db = new DBBean();
		String containerID = null;
		String qstrGetContainerID = "select doc_container_id from pn_doc_container_has_object where object_id = "
				+ objectID;

		try {

			db.executeQuery(qstrGetContainerID);

			if (db.result.next()) {
				containerID = db.result.getString("doc_container_id");
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.getContainerID() threw an SQL exception: "
							+ sqle);
		} // end catch
		finally {
			db.release();
		}

		return containerID;

	} // end getContainerID

	private static boolean isObjectTypeOf(String objectID, String type) {

		DBBean db = new DBBean();
		boolean match = false;
		String objectType = null;
		String qstrGetObjectType = "select object_type from pn_object where object_id = "
				+ objectID;

		try {

			// first get the properties for THIS container
			db.executeQuery(qstrGetObjectType);

			if (db.result.next()) {
				objectType = db.result.getString("object_type");
			}

		} // end try
		catch (SQLException sqle) {
			Logger.getLogger(DocumentManager.class).debug(
					"DocumentManager.isObjectTypeOf() threw an SQL exception:"
							+ sqle);
		} // end catch
		finally {
			db.release();
		}

		if (type.equals(objectType)) {
			match = true;
		}

		return match;
	} // end isTypeOf

	public String getType(String objectID) {

		String objectType;
		String qstrGetObjectType;

		objectType = (String) this.objectTypeCache.get(objectID);

		// if the objectType hasn't been stored in the hash yet, get it from the
		// db
		// and then cache the result.
		if (objectType == null) {

			DBBean db = new DBBean();
			try {
				qstrGetObjectType = "select object_type from pn_object where object_id = "
						+ objectID;

				// first get the properties for THIS container
				db.executeQuery(qstrGetObjectType);

				if (db.result.next()) {
					objectType = db.result.getString("object_type");
				}

				// store DB result in CACHE
				this.objectTypeCache.put(objectID, objectType);

			} // end try
			catch (SQLException sqle) {
				Logger.getLogger(DocumentManager.class).debug(
						"DocumentManager.getType() threw an SQL exception: "
								+ sqle);
			} // end catch
			finally {
				db.release();
			}

		} // end if objectType == null

		return objectType;

	} // end getType()

	//
	// Private nested top-level classes
	//

	/**
	 * RootStoragePath structure
	 */
	private static class RootStoragePath {
		String repositoryPath = null;

		String spaceID = null;

		String docSpaceID = null;

		String storageID = null;

		/**
		 * Creates a new, empty RootStoragePath.
		 */
		RootStoragePath() {
			// Nothing
		}

		/**
		 * Constructs the root storage path based on the supplied path
		 * information. The root storage path is the path up to, but not
		 * including, the file handle.
		 * 
		 * @return the root storage path
		 */
		public String toString() {
			StringBuffer rootPath = new StringBuffer();

			rootPath.append(this.repositoryPath);
			rootPath.append(FileManager.SLASH);
			rootPath.append(this.spaceID);
			rootPath.append(FileManager.SLASH);
			rootPath.append(this.docSpaceID);
			rootPath.append(FileManager.SLASH);
			rootPath.append(this.storageID);

			return rootPath.toString();
		}
	}

	/**
	 * CompleteStoragePath structure. This is the RootStoragePath plus the file
	 * handle.
	 */
	private static class CompleteStoragePath extends RootStoragePath {
		String fileHandle;

		/**
		 * Creates a new, empty CompleteStoragePath.
		 */
		private CompleteStoragePath() {
			// Nothing
		}

		/**
		 * Returns the root path only.
		 * 
		 * @return the root path
		 */
		private String getRootPath() {
			return super.toString();
		}

		/**
		 * Constructs the complete storage path based on the root storage path
		 * and the file handle.
		 * 
		 * @return the complete storage path
		 */
		public String toString() {
			StringBuffer path = new StringBuffer();
			path.append(super.toString());
			path.append(FileManager.SLASH);
			path.append(this.fileHandle);
			return path.toString();
		}
	}

}
