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
 * Interface which contains statics for all attribute ID's supported by the Person object.
 * The inteface extends IAddressAttributes to pick up settable attributes for a persons address.
 * <br>
 * The interface also defines statics which represent the name of the tokens which will be looked up to provide labels
 * for each attribute.
 * <br>
 * While this interface doesn't enforce anything, it provides constants for reuse.
 * 
 * @author Phil Dixon
 * @see net.project.resource.Address
 * @see net.project.resource.IAddressAttributes
 * @since Gecko
 */
public interface IPersonAttributes extends IAddressAttributes {

    /* ------------------------------- General Person Attributes  ------------------------------- */        

    public static final String EMAIL_ATTRIBUTE = "person.email";

    public static final String EMAIL_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.email";

    public static final String USERNAME_ATTRIBUTE = "person.username";

    public static final String USERNAME_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.login.username";
    
    public static final String PREFIX_ATTRIBUTE = "person.prefixName";

    public static final String PREFIX_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.nameprefix";

    public static final String FIRSTNAME_ATTRIBUTE = "person.firstname";

    public static final String FIRSTNAME_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.firstname";

    public static final String LASTNAME_ATTRIBUTE = "person.lastname";

    public static final String LASTNAME_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.lastname";
    
    public static final String MIDDLENAME_ATTRIBUTE = "person.middlename";

    public static final String MIDDLENAME_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.middlename";

    public static final String SUFFIX_ATTRIBUTE = "person.suffix";

    public static final String SUFFIX_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.namesuffix";

    public static final String DISPLAYNAME_ATTRIBUTE = "person.displayname";

    public static final String DISPLAYNAME_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.personal.displayname";
    
    public static final String TIMEZONE_ATTRIBUTE = "person.timezoneCode";

    public static final String TIMEZONE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.localization.timezone";

    public static final String LANGUAGE_ATTRIBUTE = "person.languageCode";

    public static final String LANGUAGE_ATTRIBUTE_PROPERTY_NAME = "prm.global.registration.localization.language";
}
