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
 * This exception is thrown any time a request to make a chart has occurred but
 * insufficient parameters have been sent to the servlet that is supposed to
 * create that chart.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class MissingChartDataException extends PnetException {
    /**
     * Standard constructor.
     *
     * @param message a <code>String</code> value used to identify the cause of
     * this exception.
     */
    public MissingChartDataException(String message) {
        super(message);
    }

    /**
     * Standard constructor which will parse the <code>cause</code> parameter to
     * append its stack trace to the stack trace of this exception.
     *
     * @param message a <code>String</code> value used to identify the cause of
     * this exception.
     * @param cause a <code>Throwable</code> value which contains the exception
     * which caused this exception to be thrown.
     */
    public MissingChartDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
