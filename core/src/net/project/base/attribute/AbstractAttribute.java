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

import net.project.datatransform.csv.transformer.AbstractDataTransformer;
import net.project.persistence.IXMLPersistence;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
 * A AbstractAttribute class is the skeleton for constructing various Attributes.
 * <b>Note:</b> All objects which extend this abstract class must implement their constructor to setType
 * to the appropriate AttributeType.
 *
 * @author Deepak Pandey
 * @author Phil Dixon
 * @see net.project.base.attribute.AttributeType
 * @since emu
 */
public abstract class AbstractAttribute implements IAttribute, IXMLPersistence, java.io.Serializable {
    /** The unique ID of the attribute */
    protected String id = null;
    /** The human-readable display name of the attribute */
    protected String displayName = null;
    /** The attribute type of the attribute */
    protected AttributeType type = null;
    /** The (optional) category for the attribute */
    protected AttributeCategory category = null;
    /** Indicates whether the attribute is required */
    protected boolean isRequired = false;

    /**
     * Default, no-arg constructor for Bean Spec Compliance.
     * By default, the attribute is not Required
     * NOTE, all object that extend this abstract class must setType to the appropriate type
     * @since Gecko
     */
    public AbstractAttribute() {
        setIsRequired(false);
        initializeType();
    }

    /**
     * Construct a new attribute with the ID and displayName specified.
     * By default, this attribute will not be required.
     *
     * @param id     The ID of the attribute
     * @param displayName
     *               The displayName of the attribute
     * @since Gecko
     */
    public AbstractAttribute(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;

        setIsRequired(false);
        initializeType();
    }

    /**
     * Construct a new attribute with the ID and displayName specified.
     * By default, this attribute will not be required.
     *
     * @param id     The ID of the attribute
     * @param displayName
     * @param required True if the attribute is required
     * @since Gecko
     */
    public AbstractAttribute(String id, String displayName, boolean required) {

        this.id = id;
        this.displayName = displayName;

        setIsRequired(required);
        initializeType();
    }

    /* ------------------------------- Other methods ------------------------------------ */

    /**
     * Indicates whether specified object is equal to this one.
     * Two <code>AbstractAttribute</code>s are equal if their IDs
     * are equal.
     * @return true if the specified object is an <code>AbstractAttribute</code>
     * and is equal to this; false otherwise
     */
    public boolean equals(Object obj) {
        // Note:  It is critical that if the logic of equals() changes,
        // hashCode() is also changed to ensure that two equal() objects
        // return the same hashCode
        // If this does not hold true, HashMap key lookups will fail

        boolean isEqual = false;

        if (obj == this) {
            // Object identity, always equal
            isEqual = true;

        } else if (obj != null &&
            obj instanceof IAttribute &&
            ((IAttribute)obj).getID().equals(this.getID())) {

            // If IDs match, equal
            isEqual = true;
        }

        return isEqual;
    }

    /**
     * Returns the hashcode of this <code>AbstractAttribute</code>s ID
     * since equality is based on IDs.
     * @return the hashcode
     * @see java.lang.String#hashCode
     */
    public int hashCode() {
        return getID().hashCode();
    }

    /**
     * Returns the properties of this attribute.
     * For debug purposes only; the structure is undefined.
     * @return string representation of attribute
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("id: ").append(getID()).append("\n");
        result.append("displayName: ").append(getDisplayName()).append("\n");
        result.append("isRequired: ").append(String.valueOf(isRequired));
        return result.toString();
    }

    /**
     * Must be implemented by each subclass to initiale the Attribute Type member
     * @since Gecko
     */
    protected abstract void initializeType();

    public abstract AbstractDataTransformer getDataTransformer(String id,IAttribute iAttribute);

    /**
     * Returns the Displayname for the current Object
     * @return String
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the ID for the current Object
     * @return String
     */
    public String getID() {
        return this.id;
    }

    /**
     * Return the AttributeType of this Attribute
     *
     * @see net.project.base.attribute.AttributeType
     * @return Return the AttributeType of this Attribute
     * @since Gecko
     */
    public AttributeType getType() {
        return this.type;
    }

    /**
     * Set the AttributeType of this Attribute
     *
     * @param type   The AttributeType of this Attribute
     * @see net.project.base.attribute.AttributeType
     * @since Gecko
     */
    protected void setType(AttributeType type) {
        this.type = type;
    }

    /**
     * Set's the presentation category for this attribute
     *
     * @param category Presentation category.
     * @see net.project.base.attribute.AttributeCategory
     * @since Gecko
     */
    public void setCategory(AttributeCategory category) {
        this.category = category;
    }

    /**
     * Get's the presentation category for this attribute
     *
     * @return category Presentation category.
     * @see net.project.base.attribute.AttributeCategory
     * @since Gecko
     */
    public AttributeCategory getAttributeCategory() {
        return this.category;
    }

    /**
     * Returns true if the specified IAttribute is in the specified AttributeCategory.
     *
     * @param category The attribute category to be tested against.
     * @return True if the attribute is in the category, false if not.
     *
     * @see net.project.base.attribute.AttributeCategory
     * @since Gecko
     */
    public boolean inCategory(AttributeCategory category) {
        return (this.category != null && this.category.equals(category)) ? true : false;
    }

    /**
     * Returns true if the attribute is required, false if not
     *
     * @return  Returns true if the attribute is required, false if not
     * @since Gecko
     */
    public boolean isRequired() {
        return this.isRequired;
    }


    /**
     * Set's the attribute required based on the specified flag
     *
     * @param required
     * @since Gecko
     */
    public void setIsRequired(boolean required) {
        this.isRequired = required;
    }

    /**
     * Returns a well-formed XML document representing the attribute
     *
     * @return  a well-formed XML document representing the attribute
     * @since Gecko
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(XML_VERSION);
        xml.append(getXMLBody());

        return xml.toString();
    }

    /**
     * Returns an XML document with all of the attributes of this object
     *
     * @return An XML document with all of the attributes of this object
     * @since Gecko
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<Attribute>");

        xml.append("<id>");
        xml.append(XMLUtils.escape(this.id));
        xml.append("</id>");

        xml.append("<displayName>");
        xml.append(XMLUtils.escape(this.displayName));
        xml.append("</displayName>");

        xml.append("<type>");
        xml.append(XMLUtils.escape(this.type.getID()));
        xml.append("</type>");

        if (this.category != null) {
            category.getXMLBody();
        }


        xml.append("<isRequired>");
        xml.append(XMLUtils.escape(Conversion.booleanToString(this.isRequired)));
        xml.append("</isRequired>");

        xml.append("</Attribute>");

        return xml.toString();
    }
}

