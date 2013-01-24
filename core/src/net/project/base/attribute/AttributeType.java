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


/**
 * Simple class which represents a type-safe enumeration of AttributeType Constants.
 * This method is used in place of String constants so that type-safe attributes are passed around and
 * so that additional properties (such as display name) could be added to the Type at a later time.
  *
 * @author Phil Dixon
 * @since Gecko
 */
public class AttributeType implements java.io.Serializable {

    /** String representing the type of the attribute */
    public String typeID = null;

    /* ------------------------------- Constructor(s)  ------------------------------- */        

    /**
     * Creates a new, empty AttributeType.
     */
    public AttributeType() {
        // do nothing
    }

    /**
     * Creates a new AttributeType with the specified typeID and displayName
     * 
     * @param typeID Type of the Attribute
     * @param displayName
     */
    private AttributeType (String typeID) {
        this.typeID = typeID;
    }

    /* ------------------------------- Search and Setters  ------------------------------- */        

    /**
     * Returns the id of this attribute type.
     * @return the id of this attribute type
     */
    public String getID() {
        return this.typeID;
    }



    /**
     * Overrides default implementation of equals to match the typeID strings.
     * 
     * @param obj    Type to check
     * @return True if object is instanceof AttributeType and the typeID of the specified object is equal to the current object.
     * @since Gecko
     */
    public boolean equals (Object object) {

        boolean isEqual = false;
        AttributeType attributeType = null;

        attributeType = (object instanceof AttributeType) ? (AttributeType) object : null;

        if (attributeType != null 
            && attributeType.getID().equals (this.getID())) {
    
            isEqual = true;

        } else {
            isEqual = false;
        }

        return isEqual;
    }


    /* ------------------------------- Static AttributeType Constants  ------------------------------- */        

    //
    // Public static constants located at end of class to ensure
    // other statics are initialized first before calling constructor.
    //

    /**
     * Text Attribute Type.
     * This should only be used for comparison to other AttributeType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final AttributeType TEXT = new AttributeType("text");

    /**
     * Date Attribute Type.
     * This should only be used for comparison to other AttributeType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final AttributeType DATE = new AttributeType("date");

    /**
     * Number Attribute Type.
     * This should only be used for comparison to other AttributeType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final AttributeType NUMBER = new AttributeType("number");

    /**
     * Boolean Attribute Type.
     * This is not a real facility type.  It is used when an attempt to locate
     * a facility type fails.
     */
    public static final AttributeType BOOLEAN = new AttributeType("boolean");

    /**
     * Domain Attribute Type.
     * This is not a real facility type.  It is used when an attempt to locate
     * a facility type fails.
     */
    public static final AttributeType DOMAIN = new AttributeType("domain");

    /**
     * Person Attribute Type.
     * This is not a real facility type.  It is used when an attempt to locate
     * a facility type fails.
     */
    public static final AttributeType PERSON = new AttributeType("person");

}

