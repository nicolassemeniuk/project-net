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
+----------------------------------------------------------------------*/
package net.project.base.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.gui.html.IHTMLOption;

/**
 * Provides an Enumeration Property Types.
 * A Property Type is a classification of a property.
 * It also determines whether the property value is stored in a clob column
 * or not.
 *
 * @author Tim Morrow
 * @since Version 7.5
 */
class PropertyType implements IHTMLOption, Serializable {

    //
    // Static members
    //

    private static Collection propertyTypeCollection = new ArrayList();

    public static final PropertyType BOOLEAN = new PropertyType("boolean", "Boolean", "A true/false value", false);

    public static final PropertyType CSS = new PropertyType("css", "CSS", "The path to a CSS resource", false);

    public static final PropertyType HREF = new PropertyType("href", "HREF", "A URI", false);

    public static final PropertyType IMAGEPATH = new PropertyType("imagepath", "Image Path", "The path to an image resource", false);

    public static final PropertyType LARGETEXT = new PropertyType("largetext", "Large text", "An unlimited quantity of text", true);

    public static final PropertyType TEXT = new PropertyType("text", "Text", "Up to 250 characters of text", false);

    /**
     * Returns the PropertyType with the specified id.
     * @param id the id of the value to find
     * @return the PropertyType with matching id, or null if no value is
     * found with that id
     */
    public static PropertyType findByID(String id) {
        PropertyType foundPropertyType = null;
        boolean isFound = false;

        for (Iterator it = PropertyType.propertyTypeCollection.iterator(); it.hasNext() && !isFound;) {
            PropertyType nextPropertyType = (PropertyType) it.next();
            if (nextPropertyType.getID().equals(id)) {
                foundPropertyType = nextPropertyType;
                isFound = true;
            }
        }

        return foundPropertyType;
    }

    public static Collection getAllPropertyTypes() {
        return Collections.unmodifiableCollection(PropertyType.propertyTypeCollection);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private final String id;

    /**
     * The display name.
     */
    private final String name;

    /**
     * The description.
     */
    private String description;

    /**
     * Indicates whether the value of a property of this type is stored in
     * a clob.
     */
    private final boolean isClobStorage;

    /**
     * Creates a new PropertyType.
     * @param id the id
     * @param description the description of the property
     */
    private PropertyType(String id, String name, String description, boolean isClobStorage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isClobStorage = isClobStorage;
        propertyTypeCollection.add(this);
    }

    /**
     * Returns the internal id of this PropertyType.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the name of this PropertyType.
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the description of this PropertyType.
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Indicates whether a value of a property of this type is stored in
     * a clob.
     * @return true if the value is stored in a clob; false otherwise
     */
    boolean isClobStorage() {
        return this.isClobStorage;
    }

    /**
     * Indicates whether the specified object is a PropertyType with
     * matching ID.
     * @param o the PropertyType object to compare
     * @return true if the specified PropertyType has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof PropertyType) {
                PropertyType propertyType = (PropertyType) o;
                if (id.equals(propertyType.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    //
    // Implementing IHtmlOption
    //

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     * @return the id
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     * @return the display name
     */
    public String getHtmlOptionDisplay() {
        return getName();
    }

    //
    // End IHtmlOption
    //

}
