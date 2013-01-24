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
 * The FormFieldCopier copies {@link FormField} objects
 */
public class FormFieldCopier implements java.io.Serializable {
    
    private static final String FORM_FIELD_COPY_SQL = "{call forms.copy_field(?, ?, ?, ?, ?)}";

    /**
     * Copies the form field with the specified id into the specified form.
     * After copying, a new copy of the field will exist in the form with class
     * id <code>targetClassID</code><br>
     * <b>Note:</b> No COMMIT or ROLLBACK is performed in the DBBean
     * @param db the DBBean in which to perform the transaction.
     * @param sourceFormFieldID the id of the form field to copy
     * @param targetClassID the id of the form class to which to copy the
     * form field
     * @param currentUser the current user performing the copy
     * @return the new object ID of the copied form field
     * @throws PersistenceException if there is a databae error during copy
     */
    public String copy(DBBean db, String sourceFormFieldID, String targetClassID, User currentUser) 
            throws PersistenceException {

    	Logger.getLogger(FormFieldCopier.class).debug("FormFieldCopier.copy(): Copying form field with id: " + sourceFormFieldID + 
            " to form with id: " + targetClassID);
        
        String newObjectID = null;
        int errorCode = net.project.base.DBErrorCodes.OPERATION_SUCCESSFUL;
        
        try {
            int index = 0;
            int newObjectIDIndex = 0;
            int errorCodeIndex = 0;

            db.prepareCall(FORM_FIELD_COPY_SQL);
            db.cstmt.setString(++index, sourceFormFieldID);
            db.cstmt.setString(++index, targetClassID);
            db.cstmt.setString(++index, currentUser.getID());
            db.cstmt.registerOutParameter((newObjectIDIndex = ++index), java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();

            newObjectID = db.cstmt.getString(newObjectIDIndex);
            errorCode = db.cstmt.getInt(errorCodeIndex);

            // Handle any erroneous status codes
            // This method will throw an exception if there are any issues
            net.project.database.DBExceptionFactory.getException("FormFieldCopier.copy()", errorCode);

        } catch (SQLException sqle) {
        	Logger.getLogger(FormFieldCopier.class).error("FormFieldCopier.copy threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying form field with id: " + sourceFormFieldID, sqle);
        
        } catch (PersistenceException pe) {
            // Propogate PersistenceExceptions
            throw pe;
        
        } catch (net.project.base.PnetException pe) {
            // Other kinds of exceptions
            throw new PersistenceException("Error copying form field with id: " + sourceFormFieldID + " - " + pe.getMessage(), pe);
        
        }

        return newObjectID;
    }

}
