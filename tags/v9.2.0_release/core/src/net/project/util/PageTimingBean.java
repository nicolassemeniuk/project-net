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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides timing mechanism for the JSP pages
 * 
 * @author BrianConneen    07/00
 */

public class PageTimingBean
    implements java.io.Serializable {

    // *************************************************************
    // PRIVATE members
    // *************************************************************
    private long startTime;

    private SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd_HH.mm.ss");

    /**
     * Constructs a new object and initializes the time related variables.
     */
    public PageTimingBean () {

        startTime = System.currentTimeMillis();
    }


    /**
     * Returns the Page Timing Statistics.
     *
     * @return the Page Timing Statistics
     */
    public String toString() {
        long endTime = System.currentTimeMillis();
        String requestTimestamp = formatter.format(new Date(startTime));
        String pageTimingStatistics =  " REQUEST TIMESTAMP | " + requestTimestamp + " PROCESSING TIME | " + (endTime - startTime)+ " ms";
        return pageTimingStatistics;
    }

}
