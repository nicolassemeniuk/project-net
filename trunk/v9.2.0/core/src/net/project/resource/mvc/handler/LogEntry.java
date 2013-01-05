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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 14865 $
|       $Date: 2006-03-31 09:49:17 +0530 (Fri, 31 Mar 2006) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.handler;

/**
 * Used in the "Specify Hours" jsp page for Assignment Time updating.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class LogEntry {
    private String startTime;
    private String endTime;
    private String comments;

    public LogEntry(String startTime, String endTime, String comments) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getComments() {
        return comments;
    }
    
    
}
