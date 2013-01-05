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
package net.project.calendar;

import java.util.Date;

/**
 * Represents a block of time between a starting Date/Time and ending Date/Time.
 *
 * @deprecated as of version 7.4.  This object is unused, unsupported, and can
 * be deleted at any time.
 * @author Bern McCarty
 * @since 01/2000
 */
public class TimeCell {
    protected Date startDate = null;
    protected Date endDate = null;

    public void setStartTime(Date start) {
        startDate = start;
    }

    public Date getStartTime() {
        return startDate;
    }

    public void setEndTime(Date end) {
        endDate = end;
    }

    public Date getEndTime() {
        return endDate;
    }


}