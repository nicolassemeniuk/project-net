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
 * EncodingParameter is a Parameter
 */
public class EncodingParameter extends Parameter implements net.project.calendar.vcal.ICalendarConstants {

    public static final EncodingValue SEVEN_BIT = new EncodingValue("7-BIT");
    public static final EncodingValue EIGHT_BIT = new EncodingValue("8-BIT");
    public static final EncodingValue QUOTED_PRINTABLE = new EncodingValue("QUOTED-PRINTABLE");
    public static final EncodingValue BASE64 = new EncodingValue("BASE64");

    public EncodingParameter() {
        super(PARAM_ENCODING);
    }

    public EncodingParameter(EncodingValue value) {
        super(PARAM_ENCODING, value.getValue());
    }

    public static class RoleValue extends ParameterValue {
        RoleValue(String value) {
            super(value);
        }
    }

    public static class EncodingValue extends ParameterValue {
        EncodingValue(String value) {
            super(value);
        }
    }
}

