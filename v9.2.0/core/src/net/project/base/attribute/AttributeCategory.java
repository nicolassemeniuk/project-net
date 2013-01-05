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
package net.project.base.attribute;

import net.project.persistence.IXMLPersistence;



/**
 * Class which represents the presentation category of an attribute.
 * <br>
 * Note, the category is specifically used for presentation only.  It essentially
 * represents a logical or contextual "grouping" property for presentation.
 * 
 * @author Phil Dixon
 * @see net.project.base.attribute.IAttribute
 * @since Gecko
 */
public class AttributeCategory implements IXMLPersistence, java.io.Serializable {

    /** The "id" of the presentation category */
    private String categoryID = null;
    /** The presentable label of the category */
    private String label = null;


    /* ------------------------------- Constructor(s)  ------------------------------- */

    /**
     * Instantiates an AttributeCategory object and sets the categoryID.
     * 
     * @param categoryID The ID (string based) of the IAttribute category.
     * @see net.project.base.attribute.IAttribute
     * @since Gecko
     */
    public AttributeCategory (String categoryID) {
        setID (categoryID);
    }


    /* ------------------------------- Getters and Setters  ------------------------------- */

    /**
     * Set's the ID of the category.
     * The ID should be used as the primary "sort" device in the presentation the
     * attribute
     * 
     * @param categoryID The id of the category
     * @since Gecko
     */
    public void setID (String categoryID) {
        this.categoryID = categoryID;
    }

    /**
     * Get's the ID of the category.
     * The ID should be used as the primary "sort" device in the presentation the
     * attribute
     * 
     * @return The id of the category
     * @since Gecko
     */
    public String getID() {
        return this.categoryID;
    }

    /**
     * Set's the display name of the category.
     * 
     * @param name   the display name of the category
     * @since Gecko
     */
    public void setName (String name) {
        this.label = name;
    }

    /**
     * Get's the display name of the category.
     * 
     * @return The display name of the category
     * @since Gecko
     */
    public String getName() {
        return this.label;
    }


    /* ------------------------------- IXMLPersistence Implementation  ------------------------------- */

    /**
         Converts the object to XML representation
         This method returns the object as XML text.
         @return XML representation of the object
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append (XML_VERSION);
        xml.append (getXMLBody());

        return xml.toString();
    }

    /**
        Converts the object to XML node representation without the xml header tag.
        This method returns the object as XML text.
         @return XML node representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append ("<AttributeCategory id=\"" + getID() + "\">");

        xml.append("<name>");
        xml.append (getName());
        xml.append("</name>");

        xml.append ("</AttributeCategory>");

        return xml.toString();
    }


    /* ------------------------------- Misc  ------------------------------- */

    /**
     * Overridden implementation of equals.
     * Does a String.equals() compare to make sure the categoryID's (represented by getID())
     * are equal
     * 
     * @param object The object (must be of type AttributeCategory) for comparison
     * @return True if the specified object is of type AttributeCollection and the ID's are equal.
     *         False if not.
     * @since Gecko
     */
    public boolean equals (Object object) {

        boolean match = false;

        if (object != null && object instanceof AttributeCategory) {

            AttributeCategory category = (AttributeCategory) object;

            if (category.getID().equals( this.categoryID )) {
                match = true;
            } else {
                match = false;
            }
        } else {
            match = false;
        }

        return match;
    }
}


