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

import java.util.ArrayList;
import java.util.Iterator;

import net.project.calendar.vcal.VCalException;
import net.project.calendar.vcal.parameter.EncodingParameter;
import net.project.calendar.vcal.parameter.Parameter;

/**
 * This represents a vCalendar property.
 * A property is the definition of an individual attribute describing the event 
 * or to do associated with the vCalendar.
 */
public abstract class Property implements java.io.Serializable, net.project.calendar.vcal.ICalendarConstants {

    private String name = null;
    private ArrayList values = new ArrayList();
    private ArrayList parameters = new ArrayList();

    /**
     * Creates a new Property with the specified name.  The name is used
     * when rendering the property.
     * @param name the name of the property
     */
    protected Property(String name) {
        setName(name);
    }

    /**
     * Creates a new Property with the specified name and value.
     * @param name the name of the property
     * @param value the value of the property
     */
    protected Property(String name, PropertyValue value) {
        setName(name);
        addValue(value);
    }

    /**
     * Creates a new Property with the specified name and a String value.
     * @param name the name of the property
     * @param value the String value of the property
     */
    protected Property(String name, String value) {
        this(name, new SimplePropertyValue(value));
    }
    

    /**
     * Sets this property's name.
     * @param name the property's name
     */
    private void setName(String name) {
        this.name = name;
    }

    /** 
     * Returns this property's name.
     * @return the property's name
     */
    public String getName() {
        return this.name;
    }

    /** 
     * Adds a value to this property.
     * @param value the property value to add
     */
    public void addValue(PropertyValue value) {
        try {
            // Added Quoted-Printable parameter to this property
            // if its value is quoted-printable
            if (value.isQuotedPrintable()) {
                addParameter(new EncodingParameter(EncodingParameter.QUOTED_PRINTABLE));
            }
        } catch (VCalException vce) {
            // No parameters added
        }
        this.values.add(value);
    }

    /**
     * Adds a parameter to this property.
     * @param parameter the parameter to add
     * @throws VCalException if the parameter is not supported by this property
     */
    public void addParameter(Parameter parameter) throws VCalException  {
        this.parameters.add(parameter);
    }

    /**
     * Returns the rendition of this property.
     * A property takes the following form:<br>
     * <code>PropertyName [';' PropertyParameters] ':' PropertyValue</code><br>
     * as shown in the following example:<br>
     * <code>DTSTART:19960415T083000</code>
     */
    public String getRendition() {
        StringBuffer s = new StringBuffer();

        s.append(getName());
        s.append(getParametersRendition());
        s.append(":" + getValuesRendition());
        s.append(CRLF);

        return s.toString();
    }

    private String getParametersRendition() {
        StringBuffer s = new StringBuffer();
        Parameter parameter = null;

        Iterator it = this.parameters.iterator();
        while (it.hasNext()) {
            parameter = (Parameter) it.next();
            s.append(";");
            s.append(parameter.getRendition());
        }
        return s.toString();
    }

    private String getValuesRendition() {
        StringBuffer s = new StringBuffer();
        PropertyValue value = null;

        Iterator it = this.values.iterator();
        while (it.hasNext()) {
            value = (PropertyValue) it.next();
            if (s.length() > 0) {
                s.append(";");
            }
            s.append(value.getRendition());
        }

        return s.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Property &&
            obj != null &&
            ((Property) obj).getName().equals(this.getName())) {

            return true;
        }
        return false;
    }
}
