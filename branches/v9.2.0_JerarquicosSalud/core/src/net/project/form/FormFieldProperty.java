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

 package net.project.form;


/**  
    A form field property.  A property has a name, a value and type.      
*/
public class FormFieldProperty implements java.io.Serializable
{
    protected String m_name = null;
    protected String m_value = null;
    protected String m_type = null;

    /** 
       Construct a FormField.
       @param name the name of the property
       @param value the value of the property.
     */
    public FormFieldProperty(String name, String value)
    {
        m_name = name;
        m_value = value;
    }


    /** 
      Construct a FormField.
      @param name the name of the property
      @param value the value of the property.
      @param type the type of the property (ie. "in-tag")
    */
    public FormFieldProperty(String name, String value, String type)
    {
        m_name = name;
        m_value = value;
        m_type = type;
    }


    public String getName()
    {
        return m_name;
    }

    public String getValue()
    {
        return m_value;
    }

    public String getType()
    {
        return m_type;
    }

}
