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
 * This class handles the dirty work of creating and managing attributes of Address objects.
 * The work was handed off to this class primarily to remove and abstract the implementation details of the
 * attribute "stuff" from the Address class.
 * 
 * @author Phil Dixon
 * @see net.project.resource.Address
 * @since Gecko
 */
class AddressAttributeManager implements IAddressAttributes {




    /* ------------------------------- Constructor(s)  ------------------------------- */ 

    /**
     * Empty constructor to support bean compliance.
     * @since The beginning of time
     */
    AddressAttributeManager() {
        // do nothing
    }


    /* ------------------------------- Attribute Interface Methods  ------------------------------- */ 

    /**
     * Builds an Attributes collection with all of the attributes supported by the Address object
     * 
     * @return A populated attribute collection
     * @since Gecko
     */
    public AttributeCollection getAttributes() {

        AttributeCollection attributeCollection = new AttributeCollection();

        attributeCollection.add 
        (new TextAttribute (ADDRESS1_ATTRIBUTE, PropertyProvider.get (ADDRESS1_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (ADDRESS2_ATTRIBUTE, PropertyProvider.get (ADDRESS2_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (ADDRESS3_ATTRIBUTE, PropertyProvider.get (ADDRESS3_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (ADDRESS4_ATTRIBUTE, PropertyProvider.get (ADDRESS4_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (ADDRESS5_ATTRIBUTE, PropertyProvider.get (ADDRESS5_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (ADDRESS6_ATTRIBUTE, PropertyProvider.get (ADDRESS6_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (ADDRESS7_ATTRIBUTE, PropertyProvider.get (ADDRESS7_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (CITY_ATTRIBUTE, PropertyProvider.get (CITY_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (CITY_DISTRICT_ATTRIBUTE, PropertyProvider.get (CITY_DISTRICT_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (REGION_ATTRIBUTE, PropertyProvider.get (REGION_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (STATE_ATTRIBUTE, PropertyProvider.get (STATE_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (COUNTRY_ATTRIBUTE, PropertyProvider.get (COUNTRY_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (ZIPCODE_ATTRIBUTE, PropertyProvider.get (ZIPCODE_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (OFFICE_PHONE_ATTRIBUTE, PropertyProvider.get (OFFICE_PHONE_ATTRIBUTE_PROPERTY_NAME), true));

        attributeCollection.add 
        (new TextAttribute (HOME_PHONE_ATTRIBUTE, PropertyProvider.get (HOME_PHONE_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (FAX_PHONE_ATTRIBUTE, PropertyProvider.get (FAX_PHONE_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (PAGER_PHONE_ATTRIBUTE, PropertyProvider.get (PAGER_PHONE_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (MOBILE_PHONE_ATTRIBUTE, PropertyProvider.get (MOBILE_PHONE_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (PAGER_EMAIL_ATTRIBUTE, PropertyProvider.get (PAGER_EMAIL_ATTRIBUTE_PROPERTY_NAME)));

        attributeCollection.add 
        (new TextAttribute (WEBSITE_URL_ATTRIBUTE, PropertyProvider.get (WEBSITE_URL_ATTRIBUTE_PROPERTY_NAME)));


        return attributeCollection;
    }


    public void setAttributeValue(IAttribute iattr, IAttributeValue iattrval) throws IllegalAttributeValueException {
    }

    public void storeAttributes() throws AttributeStoreException {
    }

    public void clearAttributeValues() {
    }
}
