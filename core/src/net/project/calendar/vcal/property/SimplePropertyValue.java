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

import net.project.calendar.vcal.VCalendarUtils;

/**
 * SimplePropertyValue represents a string value.  Its rendition
 * will format the string as quoted printable if required.
 */
public class SimplePropertyValue extends PropertyValue {
    
    private String value = null;

    SimplePropertyValue(String value) {
        if (value == null) {
            value = "";
        }
        if (VCalendarUtils.isQuotedPrintableRequired(value)) {
            this.value = VCalendarUtils.makeQuotedPrintable(value);
        } else {
            this.value = value;
        }
    }

    /** 
     * Returns the value. The value is Quoted-Printable if {@link #isQuotedPrintable}
     * is <code><b>true</b></code>
     * @return the value
     */
    String getRendition() {
        return value;
    }

    /**
     * Indicates whether the rendition of this value will be encoded Quoted-printable.
     * @return true if the value will be encoded Quoted-printable; false otherwsie
     */
    boolean isQuotedPrintable() {
        return VCalendarUtils.isQuotedPrintableRequired(value);
    }
}
