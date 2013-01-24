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

 /*----------------------------------------------------------------------+
|    Envelope.java                                                                       
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.workflow;

public class EnvelopePriority {

    public static final EnvelopePriority LOW = new EnvelopePriority("100");
    public static final EnvelopePriority NORMAL = new EnvelopePriority("200");
    public static final EnvelopePriority HIGH = new EnvelopePriority("300");

    /** Tracks the next number to assign to order */
    private static int nextOrd = 0;
    
    /** The database ID of this priority */
    private String priorityID = null;
    private int ord = 0;

    private EnvelopePriority(String priorityID) {
        this.priorityID = priorityID;
        this.ord = nextOrd++;
    }

    public String getID() {
        return this.priorityID;
    }

    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof EnvelopePriority &&
            ((EnvelopePriority) obj).getID().equals(this.priorityID)) {
            return true;
        }
        return false;
    }

}
