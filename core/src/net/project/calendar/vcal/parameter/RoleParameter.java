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
|                                                                       
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.vcal.parameter;

/**
 * RoleParameter is a Parameter
 */
public class RoleParameter extends Parameter implements net.project.calendar.vcal.ICalendarConstants {

    public static final RoleValue ATTENDEE = new RoleValue("ATTENDEE");
    public static final RoleValue ORGANIZER = new RoleValue("ORGANIZER");
    public static final RoleValue OWNER = new RoleValue("OWNER");
    public static final RoleValue DELEGATE = new RoleValue("DELEGATE");

    public RoleParameter() {
        super(PARAM_ROLE);
    }

    public RoleParameter(RoleValue value) {
        super(PARAM_ROLE, value.getValue());
    }

    public static class RoleValue extends ParameterValue {
        RoleValue(String value) {
            super(value);
        }
    }
}

