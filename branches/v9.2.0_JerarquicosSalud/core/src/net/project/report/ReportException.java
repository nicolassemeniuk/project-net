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

import net.project.base.PnetException;

/**
 * This class is thrown whenever an exception has occurred while creating a
 * report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportException extends PnetException {
    /**
     * Standard constructor.
     */
    public ReportException() {
        super();
    }

    /**
     * Common constructor to be used when exception has a message describing why
     * the exception occurred.
     *
     * @param message a <code>String</code> value containing a message which
     * explains why the error has occurred.
     */
    public ReportException(String message) {
        super(message);
    }

    /**
     * Common constructor to be used when the exception has been caused by
     * another exception.
     *
     * @param message a <code>String</code> value containing a message which
     * explains why the error has occurred.
     * @param cause a <code>Throwable</code> value which caused this exception.
     */
    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

}
