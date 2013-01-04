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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.AttributeStoreException;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IllegalAttributeValueException;
import net.project.base.attribute.TextAttribute;
import net.project.base.property.PropertyProvider;


/**
 * This class handles the dirty work of creating and managing attributes of Person objects.
 * The work was handed off to this class primarily to remove and abstract the implementation details of the 
 * attribute "stuff" from the Person class.
 * 
 * @author Phil Dixon
 * @see net.project.resource.Person
 * @since Gecko
 */
class PersonAttributeManager implements IPersonAttributes {


    /* ------------------------------- Constructor(s)  ------------------------------- */ 

    /**
     * Empty constructor to support bean compliance.
     */
    PersonAttributeManager() {
        // do nothing
    }


    /* ------------------------------- Attribute Interface Methods  ------------------------------- */ 

    /**
     * Returns an Attributes collection with all of the attributes \
     * supported by the Person object.
     * @return A populated attribute collection; includes Address attirubtes
     * @since Gecko
     */
    public AttributeCollection getAttributes() {

        AttributeCollection attributeCollection = new AttributeCollection();

        // First, add all of the supported Person attributes

        attributeCollection.add(
            new TextAttribute(EMAIL_ATTRIBUTE, PropertyProvider.get(EMAIL_ATTRIBUTE_PROPERTY_NAME), true) );

        attributeCollection.add(
            new TextAttribute(USERNAME_ATTRIBUTE, PropertyProvider.get(USERNAME_ATTRIBUTE_PROPERTY_NAME), true) );

        attributeCollection.add(
            new TextAttribute(PREFIX_ATTRIBUTE, PropertyProvider.get(PREFIX_ATTRIBUTE_PROPERTY_NAME)) );

        attributeCollection.add(
            new TextAttribute(FIRSTNAME_ATTRIBUTE, PropertyProvider.get(FIRSTNAME_ATTRIBUTE_PROPERTY_NAME), true) );

        attributeCollection.add(
            new TextAttribute(LASTNAME_ATTRIBUTE, PropertyProvider.get(LASTNAME_ATTRIBUTE_PROPERTY_NAME), true) );

        attributeCollection.add(
            new TextAttribute(MIDDLENAME_ATTRIBUTE, PropertyProvider.get (MIDDLENAME_ATTRIBUTE_PROPERTY_NAME)) );

        attributeCollection.add(
            new TextAttribute(SUFFIX_ATTRIBUTE, PropertyProvider.get (SUFFIX_ATTRIBUTE_PROPERTY_NAME)) );

        attributeCollection.add(
            new TextAttribute(DISPLAYNAME_ATTRIBUTE, PropertyProvider.get (DISPLAYNAME_ATTRIBUTE_PROPERTY_NAME)) );

        attributeCollection.add(
            new TextAttribute(TIMEZONE_ATTRIBUTE, PropertyProvider.get (TIMEZONE_ATTRIBUTE_PROPERTY_NAME)) );

        attributeCollection.add(
            new TextAttribute(LANGUAGE_ATTRIBUTE, PropertyProvider.get (LANGUAGE_ATTRIBUTE_PROPERTY_NAME)) );

        // Next, add all of the Address attributes

        AddressAttributeManager aam = new AddressAttributeManager();
        attributeCollection.addAll(aam.getAttributes());

        // Finally, return the collection

        return attributeCollection;
    }


    public void setAttributeValue(IAttribute iattr, IAttributeValue iattrval) throws IllegalAttributeValueException {
    }

    public void storeAttributes() throws AttributeStoreException {
    }

    public void clearAttributeValues() {
    }
}
