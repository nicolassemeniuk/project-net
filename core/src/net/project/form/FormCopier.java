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
package net.project.form;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * The FormCopier copies {@link Form} objects
 */
public class FormCopier implements java.io.Serializable {
    
    private static final String FORM_COPY_SQL = "{call forms.copy_form_within_space(?, ?, ?, ?, ?)}";
    private static final String FORM_COPY_TO_OTHER_SPACE ="{ call forms.copy_form_to_space(?, ?, ?, ?, ?, ?)}";

    /**
     * Copies the form with the specified id.
     * After copying, a new copy of the form will exist in the same space
     * <b>Note:</b> No COMMIT or ROLLBACK is performed in the DBBean
     * @param db the DBBean in which to perform the transaction.
     * @param sourceFormID the id of the form to copy
     * @param spaceID the space from and to which to copy
     * @param currentUser the current user performing the copy
     * @return the new object ID of the copied form
     * @throws PersistenceException if there is a databae error during copy
     */
    public String copy(DBBean db, String sourceFormID, String spaceID, User currentUser) 
            throws PersistenceException {

    	Logger.getLogger(FormCopier.class).debug("FormCopier.copy(): Copying form with id: " + sourceFormID);
        
        String newObjectID = null;
        int errorCode = net.project.base.DBErrorCodes.OPERATION_SUCCESSFUL;
        
        try {
            int index = 0;
            int newObjectIDIndex = 0;
            int errorCodeIndex = 0;

            db.prepareCall(FORM_COPY_SQL);
            db.cstmt.setString(++index, spaceID);
            db.cstmt.setString(++index, sourceFormID);
            db.cstmt.setString(++index, currentUser.getID());
            db.cstmt.registerOutParameter((newObjectIDIndex = ++index), java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();

            newObjectID = db.cstmt.getString(newObjectIDIndex);
            errorCode = db.cstmt.getInt(errorCodeIndex);

            // Handle any erroneous status codes
            // This method will throw an exception if there are any issues
            net.project.database.DBExceptionFactory.getException("FormCopier.copy()", errorCode);

        } catch (SQLException sqle) {
        	Logger.getLogger(FormCopier.class).error("FormCopier.copy threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying form with id: " + sourceFormID, sqle);        
        
        } catch (PersistenceException pe) {
            // Propogate PersistenceExceptions
            throw pe;
        
        } catch (net.project.base.PnetException pe) {
            // Other kinds of exceptions
            throw new PersistenceException("Error copying form with id: " + sourceFormID + " - " + pe.getMessage(), pe);
        
        }

        return newObjectID;
    }


    /**
     * Copies the form with the specified id.
     * After copying, a new copy of the form will exist in the same space
     * <b>Note:</b> No COMMIT or ROLLBACK is performed in the DBBean
     * @param db the DBBean in which to perform the transaction.
     * @param sourceFormID the id of the form to copy
     * @param spaceID the space from and to which to copy
     *
     * @param currentUser the current user performing the copy
     * @return the new object ID of the copied form
     * @throws PersistenceException if there is a databae error during copy
     */
    public String copy(DBBean db, String sourceFormID, String fromSpaceID, String toSpaceID, User currentUser) 
            throws PersistenceException {

    	Logger.getLogger(FormCopier.class).error("FormCopier.copy(): Copying form with id: " + sourceFormID);
        
        String newObjectID = null;
        int errorCode = net.project.base.DBErrorCodes.OPERATION_SUCCESSFUL;
        
        try {
            int index = 0;
            int newObjectIDIndex = 0;
            int errorCodeIndex = 0;

            db.prepareCall(FORM_COPY_TO_OTHER_SPACE);
            db.cstmt.setString(++index, fromSpaceID);
            db.cstmt.setString(++index, toSpaceID);
            db.cstmt.setString(++index, sourceFormID);
            db.cstmt.setString(++index, currentUser.getID());
            db.cstmt.registerOutParameter((newObjectIDIndex = ++index), java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();

            newObjectID = db.cstmt.getString(newObjectIDIndex);
            errorCode = db.cstmt.getInt(errorCodeIndex);

            // Handle any erroneous status codes
            // This method will throw an exception if there are any issues
            net.project.database.DBExceptionFactory.getException("FormCopier.copy()", errorCode);

        } catch (SQLException sqle) {
        	Logger.getLogger(FormCopier.class).error("FormCopier.copy threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying form with id: " + sourceFormID, sqle);        
        
        } catch (PersistenceException pe) {
            // Propogate PersistenceExceptions
            throw pe;
        
        } catch (net.project.base.PnetException pe) {
            // Other kinds of exceptions
            throw new PersistenceException("Error copying form with id: " + sourceFormID + " - " + pe.getMessage(), pe);
        
        }

        return newObjectID;
    }

}

