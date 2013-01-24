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

 
package net.project.base;

import net.project.util.Conversion;


public class DBErrorCodes {

    public static final int OPERATION_SUCCESSFUL = 0;

    public static final int UPDATE_FAILED_RECORD_OUT_OF_SYNC = 1000;
    
    /** Record was being updated by someone else */
    public static final int UPDATE_FAILED_RECORD_LOCKED = 1010;

    public static final int PLSQL_EXCEPTION = 2000;

    // if new document object fails unique name constraint
    public static final int DOCUMENT_UNIQUE_NAME_CONSTRAINT = 5001;

    public static final int UNABLE_TO_EXTEND_INDEX = -1654;

    public static final int UNIQUE_CONSTRAINT_VIOLATED = 103;

    public static String getName (int errorCode) {

	String name = null;

	switch (errorCode) {

	case OPERATION_SUCCESSFUL:
	    {
	    name = "Database Operation was successful.";
	    break;
	    } // end case

        case UNIQUE_CONSTRAINT_VIOLATED:
	    {
	    name = ". Database unique constraint violated";
	    break;
	    } // end case

        case UPDATE_FAILED_RECORD_OUT_OF_SYNC:
	    {
	    name = Conversion.intToString(UPDATE_FAILED_RECORD_OUT_OF_SYNC) + ": Database record out of sync.";
	    break;
	    } // end case
        case UPDATE_FAILED_RECORD_LOCKED:
            {
            name = Conversion.intToString(UPDATE_FAILED_RECORD_LOCKED) + ": Database record locked.";
            break;
            }
	case DOCUMENT_UNIQUE_NAME_CONSTRAINT:
	    {
		name = Conversion.intToString(DOCUMENT_UNIQUE_NAME_CONSTRAINT) + 
		    ": Unique name constraint violated.";
		break;
	    } // end case

	case PLSQL_EXCEPTION:
	    {
		name = Conversion.intToString(PLSQL_EXCEPTION) + 
		    ": The stored procedure threw a PL/SQL Exception.";
		break;
	    } // end case

	case UNABLE_TO_EXTEND_INDEX:
	    {
		name = "ORA: " + Conversion.intToString(PLSQL_EXCEPTION) + ", Unable to extend index";
	    }

	} // end switch

	return name;
    } // end getName()

} // end class EventCodes
