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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+--------------------------------------------------------------------------------------*/

package net.project.resource;


/**
 * Interface which contains statics for all attribute ID's supported by the Address object.
 * While this interface doesn't enforce anything, it provides constants for reuse.
 * 
 * @author Phil Dixon
 * @see net.project.resource.Address
 * @since Gecko
 */
public interface IAddressAttributes {


    /* ------------------------------- General Person Attributes  ------------------------------- */        

    public static final String ADDRESS1_ATTRIBUTE = "address.address1";

    public static final String ADDRESS1_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line1";

    public static final String ADDRESS2_ATTRIBUTE = "address.address2";

    public static final String ADDRESS2_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line2";

    public static final String ADDRESS3_ATTRIBUTE = "address.address3";

    public static final String ADDRESS3_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line3";

    public static final String ADDRESS4_ATTRIBUTE = "address.address4";

    public static final String ADDRESS4_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line4";

    public static final String ADDRESS5_ATTRIBUTE = "address.address5";

    public static final String ADDRESS5_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line5";

    public static final String ADDRESS6_ATTRIBUTE = "address.address6";

    public static final String ADDRESS6_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line6";

    public static final String ADDRESS7_ATTRIBUTE = "address.address7";

    public static final String ADDRESS7_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.line7";
    
    public static final String CITY_ATTRIBUTE = "address.city";

    public static final String CITY_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.city";
    
    public static final String CITY_DISTRICT_ATTRIBUTE = "address.cityDistrict";

    public static final String CITY_DISTRICT_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.citydistrict";

    public static final String REGION_ATTRIBUTE = "address.region";

    public static final String REGION_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.region";

    public static final String STATE_ATTRIBUTE = "address.state";

    public static final String STATE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.state";

    public static final String ZIPCODE_ATTRIBUTE = "address.zipcode";

    public static final String ZIPCODE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.zipcode";

    public static final String COUNTRY_ATTRIBUTE = "address.country";

    public static final String COUNTRY_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.country";

    public static final String OFFICE_PHONE_ATTRIBUTE = "address.officePhone";

    public static final String OFFICE_PHONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.workphone";

    public static final String HOME_PHONE_ATTRIBUTE = "address.homePhone";

    public static final String HOME_PHONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.homephone";
    
    public static final String FAX_PHONE_ATTRIBUTE = "address.faxPhone";

    public static final String FAX_PHONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.workfax";

    public static final String PAGER_PHONE_ATTRIBUTE = "address.pagerPhone";

    public static final String PAGER_PHONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.pagerphone";

    public static final String MOBILE_PHONE_ATTRIBUTE = "address.mobilePhone";

    public static final String MOBILE_PHONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.mobilephone";

    public static final String PAGER_EMAIL_ATTRIBUTE = "address.pagerEmail";

    public static final String PAGER_EMAIL_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.pageremail";

    public static final String WEBSITE_URL_ATTRIBUTE = "address.websiteURL";

    public static final String WEBSITE_URL_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.address.websiteurl";
}
