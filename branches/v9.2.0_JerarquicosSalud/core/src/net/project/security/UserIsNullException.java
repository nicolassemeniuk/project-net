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

 package net.project.security;

import net.project.base.PnetException;
import net.project.util.ErrorLogger;
import net.project.xml.XMLUtils;

public class UserIsNullException extends PnetException {

    private final String NAME = "UserIsNullException";

    /*****************************************************************************************************************
    *****                                     CONSTRUCTORS                                                                        *****
    *****************************************************************************************************************/

    public UserIsNullException() {
	super();
    }

    public UserIsNullException (String message) {

	super();

	setName(this.NAME);
	setMessage(message);
	setSeverity (ErrorLogger.HIGH);

	log ( getName(), getMessage());

    } // end UserIsNullException()


    /*****************************************************************************************************************
    *****                                            Overridden Exception Methods                                               *****
    *****************************************************************************************************************/

    // perhaps override Throwable.getLocalizedMessage()

    public void log() {
	super.log ( getName(), getMessage());
    } // end log

    /*****************************************************************************************************************
    *****                                 Implementing XML rendering methods                                               *****
    *****************************************************************************************************************/

    public String getXML() {

	StringBuffer xml = new StringBuffer();
	String tab = null;

	tab = "\t";
	xml.append (tab + "<exception>\n");

	tab = "\t\t";

	xml.append(tab + "<name>" + XMLUtils.escape ( getName() ) + "</name>\n");
	xml.append(tab + "<message>" + XMLUtils.escape ( getMessage() ) + "</message>\n");
	xml.append(tab + "<severity>" + XMLUtils.escape ( getSeverity() ) + "</severity>\n");
	xml.append(tab + "<stack_trace>" + XMLUtils.escape ( "STACK_TRACE_HERE" ) + "</stack_trace>\n");

	tab = "\t";
	xml.append (tab + "</exception>\n\n");

	return xml.toString();

    } // end getXML()

} // end UserIsNullException
