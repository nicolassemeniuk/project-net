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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.security.domain;


/**
 * Container for the data of a single option list value
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class DomainOption {
    private String id = "";
    private String name = "";
    private boolean selected = false;

    /**
    * Gets the value of id
    *
    * @return the value of id
    */
    public String getID() {
        return this.id;
    }

    /**
    * Sets the value of id
    *
    * @param id Value to assign to this.id
    */
    public void setID(String id){
        this.id = id;
    }

    /**
    * Gets the value of name
    *
    * @return the value of name
    */
    public String getName() {
        return this.name;
    }

    /**
    * Sets the value of name
    *
    * @param name Value to assign to this.name
    */
    public void setName(String name){
        this.name = name;
    }

    /**
    * Gets the value of selected
    *
    * @return the value of selected
    */
    public boolean isSelected() {
        return this.selected;
    }

    /**
    * Sets the value of selected
    *
    * @param argSelected Value to assign to this.selected
    */
    public void setSelected(boolean argSelected){
        this.selected = argSelected;
    }

    /**
     * Returns this option as an HTML option.
     * Of the form:
     * <code>&lt;option value="id"&gt;name&lt;/option&gt;</code>
     */
    public String getHTMLOption() {
        StringBuffer value = new StringBuffer();
        value.append("<option value=\"").append(getID()).append("\"");
        if (isSelected()) {
            value.append(" SELECTED ");
        }
        value.append(">").append(getName()).append("</option>");
        
        return value.toString();
    }
}
