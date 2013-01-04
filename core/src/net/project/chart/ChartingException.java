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
package net.project.chart;

import net.project.base.PnetException;

/**
 * Generic exception that is thrown if there is a problem producing a chart.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ChartingException extends PnetException {
    /**
     * Create a new charting exception with the message specified.
     *
     * @param message a <code>String</code> indicating the reason why the
     * exception has occurred.
     * @see #ChartingException(String, Throwable)
     */
    public ChartingException(String message) {
        super(message);
    }

    /**
     * Create a new charting exception with the message specified.
     *
     * @param message a <code>String</code> indicating the reason why the
     * exception has occurred.
     * @param cause a <code>Throwable</code> object which triggered the creation
     * of this exception.
     * @see #ChartingException(String)
     */
    public ChartingException(String message, Throwable cause) {
        super(message, cause);
    }
}
