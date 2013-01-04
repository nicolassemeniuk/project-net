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
|   $Revision: 20950 $
|       $Date: 2010-06-16 10:05:15 -0300 (mié, 16 jun 2010) $
|     $Author: nilesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document;

import java.sql.SQLException;

import net.project.base.UniqueNameConstraintException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * The DocumentCopier copies {@link Document} objects
 */
public class DocumentCopier implements ICopier {
    
    /** SQL statement for copying document object data.
     */    
    private static final String DOCUMENT_COPY_SQL = "{call document.copy_document(?, ?, ?, ?, ? , ?)}";

    /**
     * Copies the object with the specified id into the target container.
     * After copying, a new copy of the object will exist with <code>targetContainerID</code>
     * being its parent container id.<br>
     * <b>Note:</b> No COMMIT or ROLLBACK is performed in the DBBean
     * @param db the DBBean in which to perform the transaction.
     * @param sourceObjectID the id of the object to copy
     * @param targetContainerID the id of the container that will be the parent
     * container of the new copy
     * @param currentUser the current user performing the copy
     * @return the new object ID of the copied object
     * @throws PersistenceException if there is a problem copying
     * @throws CopyException if there is a problem copying
     */
    public String copy(net.project.database.DBBean db, String sourceObjectID, String targetContainerID, net.project.security.User currentUser) throws PersistenceException, CopyException {

    	Logger.getLogger(DocumentCopier.class).debug("DocumentCopier.copy(): Copying document with id: " + sourceObjectID + 
            " to container with id: " + targetContainerID);

        String newObjectID = null;
        String newFileHandle = null;

        try {
            // Copy the document in the database
            newObjectID = copyDocument(db, sourceObjectID, targetContainerID, currentUser);

            // Update the filehandle of the newly copied document
            newFileHandle = updateFileHandle(db, newObjectID);

            // Now copy the physical file associated with the document
            FileManager.copyFileForDocument(db, sourceObjectID, newObjectID);
        
        } catch (SQLException sqle) {
            // Error in the database
        	Logger.getLogger(DocumentCopier.class).error("DocumentCopier.copy threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying document " + getErroneousDocumentName(sourceObjectID) + ": " + sqle, sqle);
        
        } catch (net.project.base.UniqueNameConstraintException unce) {
            // Error copying physical document due to a name collision
            throw new CopyException("Unable to copy document " + getErroneousDocumentName(sourceObjectID) + ".  A document with the same name already exists.");

        } catch (FileCopyException fce) {
            // Error copying physical file
        	Logger.getLogger(DocumentCopier.class).error("DocumentCopier.copy threw a FileCopyException: " + fce);
            throw new CopyException("The document '" + getErroneousDocumentName(sourceObjectID) + "' could not be copied.  The physical file was not found in the document vault.");
        
        }

        return newObjectID;
    }


    /**
     * Copies the document.
     * <b>Note:</b> Does NOT commit or rollback.
     * @param db the DBBean in which to perform the transaction.
     * @param sourceObjectID the id of the object to copy
     * @param targetContainerID the id of the container that will be the parent
     * container of the new copy
     * @param currentUser the current user performing the copy
     * @return the new document id
     * @throws SQLException if there is a problem in the database
     * @throws UniqueNameConstraintException if the document name already exists
     * in the target container
     */
    private String copyDocument(net.project.database.DBBean db, String sourceObjectID, String targetContainerID, net.project.security.User currentUser) throws SQLException, net.project.base.UniqueNameConstraintException {

        String newObjectID = null;
        int errorCode = net.project.base.DBErrorCodes.OPERATION_SUCCESSFUL;

        int index = 0;
        int newObjectIDIndex = 0;
        int errorCodeIndex = 0;
        String dicussionGroupDescription = PropertyProvider.get("prm.document.create.discussiongroup.description");

        db.prepareCall(DOCUMENT_COPY_SQL);
        db.cstmt.setString(++index, sourceObjectID);
        db.cstmt.setString(++index, targetContainerID);
        db.cstmt.setString(++index, currentUser.getID());
        db.cstmt.setString(++index, dicussionGroupDescription );
        db.cstmt.registerOutParameter((newObjectIDIndex = ++index), java.sql.Types.VARCHAR);
        db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
        db.executeCallable();

        newObjectID = db.cstmt.getString(newObjectIDIndex);
        errorCode = db.cstmt.getInt(errorCodeIndex);

        try {
            // Handle any erroneous status codes
            // This method will throw an exception if there are any issues
            net.project.database.DBExceptionFactory.getException("DocumentCopier.copy()", errorCode);
        
        } catch (net.project.base.UniqueNameConstraintException unce) {
            // Propogate this error
            throw unce;

        } catch (Exception pe) {
            Logger.getLogger(DocumentCopier.class).error("DocumentCopier.copy failed : " + pe.getMessage());
        }

        return newObjectID;
    }


    /**
     * Updates the new copy with a valid file handle. <br>
     * <b>Does not COMMIT or ROLLBACK</b>.<br>
     * This method is necessary since the stored procedure is unable to insert
     * the correct file handle (due to the business logic involved).
     * Normally, when a document is uploaded, the file handle is stored
     * when the document is stored.  Here, the document has already been
     * created by the stored procedure.
     * Yet another reason to not use stored procedures when business logic
     * must be duplicated or cannot even be coded in the stored procedure.
     * @param db the DBBean in which to perform the transaction
     * @param documentID the id of the document whose file handle to update
     * @return the fileHandle that was assigned
     * @throws SQLException if there is a problem generating a file handle
     * or updating the file handle
     */
    private String updateFileHandle(net.project.database.DBBean db, String documentID) throws SQLException {
        
        String fileHandle = null;
        StringBuffer query = new StringBuffer();

        // Query to update file handle
        query.append("update pn_doc_content_element ");
        query.append("set file_handle = ? ");
        query.append("where doc_content_id = ");
        query.append("(select doc_content_id ");
        query.append("from pn_doc_by_space_view ");
        query.append("where doc_id = ?) ");
        
        // Generate a new file handle that is not already in use based on
        // the document file folder
        // We must use the current DBBean since the document is not committed
        // yet
        fileHandle = FileManager.createFileHandle(DocumentManager.getRootStoragePath(db, documentID));

        int index = 0;
        db.prepareStatement(query.toString());
        db.pstmt.setString(++index, fileHandle);
        db.pstmt.setString(++index, documentID);
        db.executePrepared();
        
        return fileHandle;
    }

    
    /**
     * Returns the name of a document for which an error occurred while copying.
     * Note - the source document ID should always be used here.
     * @param documentID the id of the document for which to get its name
     * @return the document name or an empty string if the name could not be
     * loacted.
     */
    private String getErroneousDocumentName(String documentID) {
        String name = "";
        
        try {
            net.project.document.Document doc = new Document(documentID);
            name = doc.getName();
        } catch (PersistenceException pe) {
            // Suck it up; this method is used when error handling
        }

        return name;
    }
    
}
