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

import net.project.calendar.vcal.property.CalendarProperty;
import net.project.calendar.vcal.property.ProdIDProperty;
import net.project.calendar.vcal.property.Property;
import net.project.calendar.vcal.property.VersionProperty;

/**
 * VCalendar is a vCalendar representation of a calendar with one or more entities.
 */
public class VCalendar implements java.io.Serializable, ICalendarConstants {

    /** vCalendar file extension */
    private static final String VCAL_FILE_EXTENSION = "vcs";

    /** vCalendar content type */
    private static final String VCAL_CONTENT_TYPE = "text/x-vCalendar";

    /** Name of this vCalendar */
    private String name = null;

    /** vCalendar Entities */
    private ArrayList entities = new ArrayList();

    /** vCalendar Properties */
    private ArrayList properties = new ArrayList();

    /**
     * Creates a new vCalendar with no entities with default properties.
     * Adds {@link VersionProperty} and {@link ProdIDProperty}
     */
    public VCalendar() {
        // Add the version property
        addProperty(new VersionProperty(VCAL_VERSION));
        
        // Add the product id property
        addProperty(new ProdIDProperty(VCAL_PRODUCTIDENTIFIER));
    }

    /**
     * Sets the name of this VCalendar.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this VCalendar.
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Adds a calendar entity to this VCalendar.
     * @param entity the entity to add
     */
    public void addEntity(VCalendarEntity entity) {
        this.entities.add(entity);
    }

    /**
     * Adds a property to this VCalendar.
     * @param property the property to add
     */
    public void addProperty(CalendarProperty property) {
        this.properties.add(property);
    }

    /**
     * Returns this vCalendar in string form.  This is compliant with
     * vCalendar 1.0 specification.
     * @return the vCalendar string
     */
    public String getRendition() {
        StringBuffer s = new StringBuffer();

        s.append(BEGIN + ":" + VCALENDAR + CRLF);
        s.append(getPropertiesRendition());        
        s.append(getEntitiesRendition());
        s.append(END + ":" + VCALENDAR + CRLF);

        return s.toString();
    }

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

    private String getEntitiesRendition() {
        StringBuffer s = new StringBuffer();
        VCalendarEntity entity = null;

        Iterator it = this.entities.iterator();
        while (it.hasNext()) {
            entity = (VCalendarEntity) it.next();
            s.append(entity.getRendition());
        }

        return s.toString();
    }

    /**
     * Returns a name for this vCalendar, suitable for attaching to an email.
     * @return the name constructed as <code>name + ".vcs"<code>
     */
    String getNameForAttachment() {
        return getName() + "." + VCAL_FILE_EXTENSION;
    }

    /**
     * Returns the content type for thhis vCalendar.
     * @return content type <code>text/x-vCalendar</code>
     */
    String getContentType() {
        return VCAL_CONTENT_TYPE;
    }

}
