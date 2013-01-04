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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$NAME.java,v $
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.report;

/**
 * This exception should be thrown when a report http request does not contain
 * sufficient information to create the report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class MissingReportDataException extends Exception {
    /**
     * Standard constructor that creates an exception with an error message.
     *
     * @param message a <code>String</code> value that contains an error message
     * which states the reason why this error has occurred.
     */
    public MissingReportDataException(String message) {
        super(message);
    }
}
