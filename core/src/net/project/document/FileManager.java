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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetExceptionTypes;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.ErrorLogger;
import net.project.util.FileUtils;

import org.apache.log4j.Logger;

public class FileManager {
    public static final String SLASH = File.separator;

    /**
     * Copies a physical file for one document to another. Copies the physical
     * file belonging to the document with <code>sourceDocumentID</code> to the
     * appropriate physical file name for the document with
     * <code>targetDocumentID</code>.<br> <b>Preconditions</b>: <ul> <li>The
     * document with <code>targetDocumentID</code> already exists with a valid
     * file handle etc.</li> <li>There is not already a file for that
     * document</li> </ul> <b>Postconditions</b>: <ul> <li>A file exists for
     * document with id <code>targetDocumentID</code></li> </ul>
     *
     * @param sourceDocumentID the id of the document whose file is copied
     * @param targetDocumentID the id of the document that receives the copy
     * @throws PersistenceException if there is a problem determining the
     * storage path for the source or target documents
     * @throws FileCopyException if there is a problem copying the physical
     * file
     */
    public static void copyFileForDocument(String sourceDocumentID, String targetDocumentID)
        throws PersistenceException, FileCopyException {

        FileManager.copyFile(DocumentManager.getCompleteDocumentStoragePath(sourceDocumentID),
            DocumentManager.getCompleteDocumentStoragePath(targetDocumentID),
            DocumentManager.getRootStoragePath(targetDocumentID));
    }

    /**
     * Copies a physical file for one document to another using the specified
     * file handle for the target document.  This method facilitates the copying
     * of documents where the new file handle is not yet committed in the
     * database. <br> Copies the physical file belonging to the document with
     * <code>sourceDocumentID</code> to the appropriate physical file name for
     * the document with <code>targetDocumentID</code>.<br>
     * <b>Preconditions</b>: <ul> <li>The document with <code>targetDocumentID</code>
     * already exists </li> <li>There is not already a file for that
     * document</li> </ul> <b>Postconditions</b>: <ul> <li>A file exists for
     * document with id <code>targetDocumentID</code></li> </ul>
     *
     * @param db the DBBean in which to read the target document's file handle
     * @param sourceDocumentID the id of the document whose file is copied
     * @param targetDocumentID the id of the document that receives the copy
     * @throws SQLException if there is a problem determining the storage path
     * for the source or target documents
     * @throws FileCopyException if there is a problem copying the physical
     * file
     */
    static void copyFileForDocument(DBBean db, String sourceDocumentID, String targetDocumentID)
        throws SQLException, FileCopyException {

        FileManager.copyFile(DocumentManager.getCompleteDocumentStoragePath(db, sourceDocumentID),
            DocumentManager.getCompleteDocumentStoragePath(db, targetDocumentID),
            DocumentManager.getRootStoragePath(db, targetDocumentID));
    }

    /**
     * Copies a physical file on disk, creating directories for the target
     * file.
     *
     * @param sourceFilePath the path to the source file; this file must exist
     * and be readable
     * @param targetFilePath the path to the target file; this file must not
     * already exist.
     * @param targetDirectoryPath the path that will be created if necessary;
     * this should be the path to the directory in which the file will be
     * created, however it can actually be any directory
     * @throws FileCopyException if there is a problem copying, for example, the
     * source file does not exist or is not readable, or the target file already
     * exists
     */
    private static void copyFile(String sourceFilePath, String targetFilePath, String targetDirectoryPath)
        throws FileCopyException {

        FileManager.createDirectories(targetDirectoryPath);

        try {
            FileUtils.copy(sourceFilePath, targetFilePath);
        } catch (IOException e) {
            Logger.getLogger(FileManager.class).debug("Error copying files: " + e, e);
            throw new FileCopyException("Error copying files: " + e, e);
        }
    }

    /**
     * Creates the directories for the specified directory path if they do not
     * already exist.
     *
     * @param directoryPath the directory hierarchy to create
     */
    public static void createDirectories(String directoryPath) {
        if (!(new File(directoryPath)).exists()) {
            makePath(directoryPath);
        }
    }

    /**
     * Creates the directories for the specified path. Assumes the directories
     * do NOT exist.
     *
     * @param path the directory hierarchy to create
     */
    private static boolean makePath(String path) {
        File rootPath = new File(path);
        return rootPath.mkdirs();
    }

    public String objectID = null;
    public String fileName = null;
    public String fileHandle = null;
    public String contentType = null;
    public String repositoryPath = null;

    /**
     * Creates a new, empty FileManager.
     */
    public FileManager() {
        // do nothing
    }

    private BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight) {
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledBI.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}  
    
     public void downloadImage(HttpServletResponse response) throws DocumentException {
		try {
			File fDownload = new File(getCompleteDocumentStoragePath(this.objectID, this.fileHandle));
			int width = 200; // Integer.parseInt(PropertyProvider.get(""));
			int height = 0;
			String imageOutput = "png";
			BufferedImage bufferedImage = ImageIO.read(fDownload);
			// Calculate the new Height if not specified
			int calcHeight = height > 0 ? height : (width * bufferedImage.getHeight() / bufferedImage.getWidth());
			// Write the image
			ImageIO.write(createResizedCopy(bufferedImage, width, calcHeight), imageOutput, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
     
    public void download(HttpServletResponse response) throws DocumentException {
        // Declare these so they can be closed on exception (user cancel == io exception here)
        ServletOutputStream oStream = null;
        FileInputStream fStream = null;

        try {
            File fDownload = new File(getCompleteDocumentStoragePath(this.objectID, this.fileHandle));
			fStream = new FileInputStream(fDownload);

            byte[] buf = new byte[4096];
            int count;

            oStream = response.getOutputStream();

            // Use the file name in the download dialog

            response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");

            // Set the content length
            Long size = new Long(fDownload.length());
            response.setHeader("Content-length", size.toString());

            // Decided to send the actual content type to address
            // the VoloView (all) plugin issue
            // Phil Dixon (6/5/00)
            response.setContentType(contentType);


            // Send the file to the Servlet Output Stream
            while ((count = fStream.read(buf)) > 0) {
                oStream.write(buf, 0, count);
            }

        } catch (FileNotFoundException e) {
            Logger.getLogger(FileManager.class).debug("File not file: " + e, e);
            Logger.getLogger(FileManager.class).debug("net.project.document.FileDownload():  File not found - " + getCompleteDocumentStoragePath());
        } catch (IOException e) {
            Logger.getLogger(FileManager.class).debug("IOException: " + e, e);
        } finally {
            // Close the output streams
            try {
                if (null != fStream) {
                    fStream.close();
                }
                if (null != oStream) {
                    oStream.close();
                }
            } catch (IOException e) {
            	Logger.getLogger(FileManager.class).debug("net.project.document.FileDownload():  Error while closing - " + getCompleteDocumentStoragePath());
            }
        }

    }

    /**
     * Adds an uploaded file to the document vault and associates it with the
     * specified document.
     *
     * @param docManager the document manager used to determine the current
     * document space
     * @param document the document to which to add the uploaded file
     * @param contentLength the size of the uploaded document
     * @param clientFilePath the path to the file on the client's machine when
     * they uploaded.
     * @param writtenFilePath the path to the file in the local file system.
     * @param contentType the mime type of file that was uploaded
     *
     * @return the document to which the uploaded file was added
     * @throws FileUploadStorageException if there was a problem uploading the
     * file, for example if the uploaded file was zero bytes or was not moved
     * successfully to the document vault
     * @throws DocumentException
     */
    public Document upload(DocumentManager docManager, Document document, long
        contentLength, String clientFilePath, String writtenFilePath,
        String contentType) throws FileUploadStorageException, DocumentException {

        String fileStoragePath;
        String fileHandle;

        String fileFormatID;
        String shortFileName;
        String documentID;

        String repositoryID;
        String repositoryPath;

        // throw if filesize <= 0 -- that means the uploaded file was invalid
        if (contentLength <= 0) {

            FileUploadStorageException fuse = new FileUploadStorageException(
                "FileUploadStorageException: FileManager.upload() invalid " +
                "document (file size <= 0)", ErrorLogger.CRITICAL
            );

            Logger.getLogger(FileManager.class).debug(fuse.getName() + ": FileManager.upload() invalid document (file size <= 0)");

            fuse.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_SPACE_ID_NULL);
            fuse.setDisplayError("The document you attempted to upload was not valid.  Please try again.");
            fuse.log();

            throw (fuse);

        }

        // get fully qualified CLIENT path to this document
        // Interestingly, on some client OSes (like Unix) this path may
        // actually be already truncated to the filename
        // Then grab the filename part
        // We have to use a custom routine since the OSFilePath may contain
        // Windows- or Unix-style slashes; we can't assume it contains the
        // same style file separators as the application server platform
        if (clientFilePath == null) {
            shortFileName = "";
        } else {
            shortFileName = FileUtils.resolveNameFromPath(clientFilePath);
        }

        // get mime type
        fileFormatID = docManager.getDocFormatID(contentType, shortFileName);

        // get a documentID for this object
        if (document.getID() == null) {
            documentID = DocumentUtils.getNextSequenceValue();
        } else {
            documentID = document.getID();
        }

        // Now determine the location in the vault to which to add the document
        repositoryID = getNextRepositoryBaseID();
        repositoryPath = getPathForRepositoryID(repositoryID);

        // Check to ensure that all components have been specified successfully
        checkPathComponenents(repositoryPath, docManager.getSpace().getID(), docManager.getDocumentSpaceID(), documentID);

        // Determine root storage path
        fileStoragePath = DocumentManager.makeRootStoragePath(repositoryPath,
            docManager.getSpace().getID(), docManager.getDocumentSpaceID(), documentID);

        // Make those directories on disk
        FileManager.createDirectories(fileStoragePath);

        // get a fileHandleID
        fileHandle = FileManager.createFileHandle(fileStoragePath);

        // CREATE FILE IN THE APPROPRIATE DIRECTORY
        // Renames the uploaded temporary file to new location
        // in the document vault
        // File.renameTo used the OS-level "move" facility, and
        // works across disks and network locations
        File temporaryFile = new File(writtenFilePath);
        File targetFile = new File(fileStoragePath, fileHandle);

        try {
            // Now move the file to the new location
            FileUtils.move(temporaryFile, targetFile);

            document.setID(documentID);
            document.setFileSize(String.valueOf(contentLength));
            document.setFileFormatID(fileFormatID);
            document.setOSFilePath(clientFilePath);
            document.setShortFileName(shortFileName);
            document.setFileHandle(fileHandle);
            document.setRepositoryBaseID(repositoryID);
            document.setRepositoryBasePath(repositoryPath);

        } catch (IOException e) {
        	Logger.getLogger(FileManager.class).error("Error moving file after upload: " + e);
            throw new FileUploadStorageException("Failed to upload document.  OS-level file move failed.  Admin: make sure you have setup the temp upload and doc repository directories and that they are writable application server user process.: " + e, e);
        }

        return document;
    }


    /**
     * Checks that all components are specified correctly. Returns without
     * exception if all componenets are ok.
     *
     * @param repositoryPath the repository base path
     * @param spaceID the space id of the object
     * @param docSpaceID the doc space id of the object
     * @param objectID object's ID
     * @throws FileUploadStorageException if there is a problem with any
     * component
     */
    public void checkPathComponenents(String repositoryPath, String spaceID, String docSpaceID, String objectID)
        throws FileUploadStorageException {

        if (repositoryPath == null || repositoryPath.equals("")) {
            FileUploadStorageException fuse = new FileUploadStorageException();

            fuse.setMessage(fuse.getName() + ": FileManager.makeNewDocumentStoragePath() failed because repositoryPath was null");
            fuse.setSeverity(ErrorLogger.CRITICAL);
            fuse.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_SPACE_ID_NULL);
            fuse.setDisplayError("Critical Error: Please contact your Project.net administrator");
            fuse.log();

            throw (fuse);

        } else if (spaceID == null || spaceID.equals("")) {
            FileUploadStorageException fuse = new FileUploadStorageException();

            fuse.setMessage(fuse.getName() + ": FileManager.makeNewDocumentStoragePath() failed because spaceID was null");
            fuse.setSeverity(ErrorLogger.CRITICAL);
            fuse.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_SPACE_ID_NULL);
            fuse.setDisplayError("Critical Error: Please contact your Project.net administrator");
            fuse.log();

            throw (fuse);

        } else if (docSpaceID == null || docSpaceID.equals("")) {
            FileUploadStorageException fuse = new FileUploadStorageException();

            fuse.setMessage(fuse.getName() + ": FileManager.makeNewDocumentStoragePath() failed because docSpaceID was null");
            fuse.setSeverity(ErrorLogger.CRITICAL);
            fuse.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_DOC_SPACE_ID_NULL);
            fuse.setDisplayError("Critical Error: Please contact your Project.net administrator");
            fuse.log();

            throw (fuse);

        } else if (spaceID == null || spaceID.equals("")) {
            FileUploadStorageException fuse = new FileUploadStorageException();

            fuse.setMessage(fuse.getName() + ": FileManager.makeNewDocumentStoragePath() failed because objectID was null");
            fuse.setSeverity(ErrorLogger.CRITICAL);
            fuse.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_OBJECT_ID_NULL);
            fuse.setDisplayError("Critical Error: Please contact your Project.net administrator");
            fuse.log();

            throw (fuse);
        }

    }

    public String getRepositoryBasePath(String objectID) throws DocumentException {
        ContainerObjectFactory factory = new ContainerObjectFactory();
        Document document = null;
        String repositoryBasePath = null;

        if (this.repositoryPath == null) {
            try {
                document = (Document) factory.makeObject(objectID);
            } catch (PersistenceException pe) {
                throw new DocumentException("FileManager.getRepositoryBasePath(): could not load object from factory: " + pe);
            }

            if (document.getRepositoryBasePath() == null) {

                DocumentException de = new DocumentException();

                de.setMessage(de.getName() + ": FileManager.getRepositoryBasePath() could not get the base path for objectID: " + objectID);
                de.setSeverity(ErrorLogger.CRITICAL);
                de.setReasonCode(PnetExceptionTypes.FILE_MANAGER_FAILED_TO_GET_REPOSITORY_BASE);
                de.setDisplayError("Project.net was unable to upload your document.  Please try again.");
                de.log();

                throw (de);
            } else {
                repositoryBasePath = document.getRepositoryBasePath();
            }
        } else {
            repositoryBasePath = this.repositoryPath;
        }

        return repositoryBasePath;
    }


    public String getNextRepositoryBaseID() throws DocumentException {
        String repositoryBaseID = null;
        DBBean db = new DBBean();

        try {
            db.prepareCall("{ ? = call DOCUMENT.GET_NEXT_REPOSITORY_BASE_ID () }");
            db.cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            db.executeCallable();

            repositoryBaseID = db.cstmt.getString(1);
        } catch (SQLException sqle) {
        	Logger.getLogger(FileManager.class).debug("FileManager.getNextRepositoryBaseID() threw an SQL exception: " + sqle);

            DocumentException de = new DocumentException();

            de.setMessage(de.getName() + ": FileManager.getNextRepositoryBaseID() could not get a new base path for objectID: " + objectID);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.FILE_UPLOAD_FAILED_SPACE_ID_NULL);
            de.setDisplayError("Project.net FileManager encountered a critical error.  Please try again.");
            de.log();

            throw (de);

        } finally {
            db.release();
        }

        return repositoryBaseID;

    }


    public String getPathForRepositoryID(String repositoryID) throws DocumentException {

        DBBean db = new DBBean();
        String path = null;
        String qstrGetPathForRepositoryID = "select repository_path from pn_doc_repository_base" +
            " where repository_id = " + repositoryID;


        try {
            db.executeQuery(qstrGetPathForRepositoryID);

            if (db.result.next()) {
                path = db.result.getString(1);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FileManager.class).debug("FileManager.getPathForRepositoryID() threw an SQL exception: " + sqle);

            DocumentException de = new DocumentException();

            de.setMessage(de.getName() + ": FileManager.getPathForRepositoryID() could not get a new base path for repositoryID: " + repositoryID);
            de.setSeverity(ErrorLogger.CRITICAL);
            de.setReasonCode(PnetExceptionTypes.FILE_MANAGER_FAILED_TO_GET_REPOSITORY_BASE);
            de.setDisplayError("Project.net FileManager encountered a critical error.  Please try again.");
            de.log();

            throw (de);
        } finally {
            db.release();
        }

        return path;
    }

    public String getRootDocumentStoragePath(String objectID) throws DocumentException {
        String storageID = DocumentManager.getStoragePathForContainerObject(objectID);
        String spaceID = DocumentManager.getSpaceIDForContainerObject(objectID);
        String docSpaceID = DocumentManager.getDocSpaceIDForContainerObject(objectID);

        return DocumentManager.makeRootStoragePath(getRepositoryBasePath(objectID), spaceID, docSpaceID, storageID);
    }

    /**
     * Creates a new fileHandle by requesting a new ID from the DB sequence Will
     * first test for the existence of a file with that handle, if found, will
     * continue to increment until no contention is found.  NOTE:  Contention
     * shouldn't be a problem with a single db instance using the same vault.
     *
     * @param path The complete file path to the document (less the fileHandle)
     * @return a new (numeric id-based) fileHandle
     */
    static String createFileHandle(String path) {
        String handle = DocumentUtils.getNextSequenceValue();
        File file = new File(path + handle);

        // only enter into this loop if the file already exists
        while (file.exists()) {

            // log the collision
        	Logger.getLogger(FileManager.class).error("FileManager.createFileHandle(): COLLISION DETECTED. File Handle " +
                handle + " already exists.");

            // then try again
            handle = DocumentUtils.getNextSequenceValue();
            file = new File(path + handle);
        }

        return handle;
    }


    public String getCompleteDocumentStoragePath() throws DocumentException {
        return getCompleteDocumentStoragePath(this.objectID, this.fileHandle);
    }


    public String getCompleteDocumentStoragePath(String objectID, String fileHandle) throws DocumentException {
        StringBuffer completePath = new StringBuffer();

        completePath.append(getRootDocumentStoragePath(objectID));
        completePath.append(SLASH);
        completePath.append(fileHandle);

        return completePath.toString();
    }


    /**
     * This method returns a {@link java.io.File} object for the current
     * document.
     *
     * @param readOnly boolean flag indicating whether the file should be
     * returned read only
     * @return The abstract file object representing this document
     * @throws DocumentException if the document has not been loaded
     */
    public File getFileObject(boolean readOnly) throws DocumentException {
        File file = new File(getCompleteDocumentStoragePath(this.objectID, this.fileHandle));

        if (readOnly) {
            file.setReadOnly();
        }
        return (file);
    }


    /**
     * This method returns a {@link java.io.File} object for the current
     * document.
     *
     * @return The abstract file object representing this document
     * @throws DocumentException if the document has not been loaded
     */
    public File getFileObject() throws DocumentException {
        return getFileObject(true);
    }

    public void setFileHandle(String fileHandle) {
        this.fileHandle = fileHandle;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public void setRepositoryPath(String path) {
        this.repositoryPath = path;
    }
}
