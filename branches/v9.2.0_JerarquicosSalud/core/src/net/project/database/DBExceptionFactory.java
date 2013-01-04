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

 package net.project.database;

import java.sql.SQLException;

import net.project.base.DBErrorCodes;
import net.project.base.UniqueNameConstraintException;
import net.project.base.UpdateConflictException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.persistence.RecordLockedException;
import net.project.util.ErrorLogger;

public class DBExceptionFactory {
    public static void getException(String methodName, int errorCode) throws net.project.base.PnetException {
        switch (errorCode) {
            case DBErrorCodes.OPERATION_SUCCESSFUL: {
                // do nothing, operation was successful
                break;
            }
            case DBErrorCodes.UPDATE_FAILED_RECORD_OUT_OF_SYNC: {
                throw new UpdateConflictException(methodName + "threw an UpdateConflictException with error code: " +
                    DBErrorCodes.getName(errorCode), ErrorLogger.LOW);
            }
            case DBErrorCodes.UPDATE_FAILED_RECORD_LOCKED: {
                throw new RecordLockedException(methodName + "threw a RecordLockedException with error code: " +
                    DBErrorCodes.getName(errorCode), ErrorLogger.LOW);
            }
            case DBErrorCodes.DOCUMENT_UNIQUE_NAME_CONSTRAINT: {
                net.project.base.UniqueNameConstraintException ex =
                    new UniqueNameConstraintException(methodName + " threw a UniqueNameConstraintException with error code: " +
                    DBErrorCodes.getName(errorCode), ErrorLogger.LOW);
                ex.setUserMessage(PropertyProvider.get("prm.document.error.uniquename.message"));
                throw ex;

            }
            default: {
                throw new PersistenceException(methodName + ": threw an Unhandled database exception with errorCode: " +
                    net.project.util.Conversion.intToString(errorCode), ErrorLogger.HIGH);
            }
        } // end switch
    } // end getException

    public static void getException(String methodName, SQLException sqle) throws net.project.base.PnetException {
        throw new PersistenceException(methodName + ": threw an Unhandled database exception with error: " + sqle, sqle);
    }


} // end class DBException









