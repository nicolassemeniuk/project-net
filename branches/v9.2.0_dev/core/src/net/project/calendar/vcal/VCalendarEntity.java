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
package net.project.calendar.vcal;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.calendar.vcal.property.Property;

/**
 * A VCalendarEntity is either a vEvent or vToDo.
 */
public abstract class VCalendarEntity implements java.io.Serializable, ICalendarConstants {

    /** Properties of this entity */
    private ArrayList properties = new ArrayList();

    /**
     * Adds a property to this entity.
     * @param property the property to add
     */
    public void addProperty(Property property) {
        this.properties.add(property);
    }

    /**
     * Returns this entity's vCalendar rendition.
     * @return the rendition
     */
    public String getRendition() {
        StringBuffer s = new StringBuffer();
        s.append(BEGIN + ":");
        s.append(getType());
        s.append(CRLF);

        s.append(getPropertiesRendition());

        s.append(END + ":");
        s.append(getType());
        s.append(CRLF);

        return s.toString();
    }

    /**
     * Returns the type of this entity as a string.
     * @return the entity type
     */
    public abstract String getType();

    /**
     * Returns the rendition of all of this entity's properties.
     * @return the properties rendition
     */
    private String getPropertiesRendition() {
        StringBuffer s = new StringBuffer();
        Property property = null;

        Iterator it = properties.iterator();
        while (it.hasNext()) {
            property = (Property) it.next();
            s.append(property.getRendition());
        }

        return s.toString();
    }
}
