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

import java.sql.SQLException;

import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * The ContainerCopier copies {@link Container} objects
 */
public class ContainerCopier implements ICopier {
    
    private static final String CONTAINER_COPY_SQL = "{call document.copy_container(?, ?, ?, ?, ?)}";

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
     * @throws PersistenceException if there is a databae error during copy
     * @throws CopyException if there is a problem copying
     */
    public String copy(net.project.database.DBBean db, String sourceObjectID, String targetContainerID, net.project.security.User currentUser) throws PersistenceException, CopyException {

    	Logger.getLogger(ContainerCopier.class).debug("ContainerCopier.copy(): Copying container with id: " + sourceObjectID + 
            " to container with id: " + targetContainerID);
        
        String newObjectID = null;
        int errorCode = net.project.base.DBErrorCodes.OPERATION_SUCCESSFUL;
        
        try {
            int index = 0;
            int newObjectIDIndex = 0;
            int errorCodeIndex = 0;

            db.prepareCall(CONTAINER_COPY_SQL);
            db.cstmt.setString(++index, sourceObjectID);
            db.cstmt.setString(++index, targetContainerID);
            db.cstmt.setString(++index, currentUser.getID());
            db.cstmt.registerOutParameter((newObjectIDIndex = ++index), java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();

            newObjectID = db.cstmt.getString(newObjectIDIndex);
            errorCode = db.cstmt.getInt(errorCodeIndex);

            // Handle any erroneous status codes
            // This method will throw an exception if there are any issues
            net.project.database.DBExceptionFactory.getException("DocumentCopier.copy()", errorCode);

        } catch (SQLException sqle) {
        	Logger.getLogger(ContainerCopier.class).error("ContainerCopier.copy threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying container with id: " + sourceObjectID, sqle);
        
        } catch (PersistenceException pe) {
            // Propogate PersistenceExceptions
            throw pe;
        
        } catch (net.project.base.PnetException pe) {
            // Other kinds of exceptions
            throw new CopyException("Error copying document with id: " + sourceObjectID + " - " + pe.getMessage());
        
        }

        return newObjectID;
    }

}

