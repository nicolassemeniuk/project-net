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
package net.project.calendar.vcal.property;

import net.project.calendar.vcal.VCalException;
import net.project.calendar.vcal.parameter.ExpectParameter;
import net.project.calendar.vcal.parameter.Parameter;
import net.project.calendar.vcal.parameter.RSVPParameter;
import net.project.calendar.vcal.parameter.RoleParameter;
import net.project.calendar.vcal.parameter.StatusParameter;
import net.project.calendar.vcal.parameter.TypeParameter;
import net.project.calendar.vcal.parameter.ValueParameter;

/**
 * The property defines an attendee to a group event or to do. The default
 * property value is an (RFC 822) address.
 */
public class AttendeeProperty extends Property implements net.project.calendar.vcal.ICalendarConstants {

    public AttendeeProperty() {
        super(FIELD_ATTENDEE);
    }

    public AttendeeProperty(String value) {
        super(FIELD_ATTENDEE, value);
    }

    public void addParameter(Parameter parameter) throws VCalException {
        if (parameter instanceof RoleParameter ||
            parameter instanceof StatusParameter ||
            parameter instanceof RSVPParameter ||
            parameter instanceof ExpectParameter ||
            parameter instanceof ValueParameter ||
            parameter instanceof TypeParameter) {

            super.addParameter(parameter);
        
        } else {
            throw new VCalException("Attendee Property does not support parameter " + parameter.getName());
        
        }
    }
}
