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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/

package net.project.space;

/**
  *  Defines types of Spaces.
  */
public interface ISpaceTypes
{

    /** A project workspace */
    public static final String PROJECT_SPACE = "project";

    /* A personal workspace */
    public static final String PERSONAL_SPACE = "person";

    /* A business workspace */
    public static final String BUSINESS_SPACE = "business";

    /* A financial workspace */
    public static final String FINANCIAL_SPACE = "financial";
    
    /** provides a workspace for managing methodology templates. */
    public static final String METHODOLOGY_SPACE = "methodology";

    /** Application space encapsulates application-wide functionality */
    public static final String APPLICATION_SPACE = "application";

    /** 
     * Configuration space encapsulates properties and settings for a particular
     * configuration of the application (for example branding) 
     */
    public static final String CONFIGURATION_SPACE = "configuration";

    /** Salesmode Company space */
    public static final String COMPANY_SPACE = "company";

    /** A concrete generic Space for user-defined, custom Spaces. */
    public static final String GENERIC_SPACE = "generic";

    /** A contract workspace contains one or more contacts being worked on */
    public static final String CONTRACT_SPACE = "contract";

    /** A Customer workspace */
    public static final String CUSTOMER_SPACE = "customer";

    /** A Vendor space */
    public static final String VENDOR_SPACE = "vendor";

    /** An Equipment space */
    public static final String EQUIPMENT_SPACE = "equipment";

    /** A Facility space */
    public static final String FACILITY_SPACE = "facility";

    /** An enterprise space. */
    public static final String ENTERPRISE_SPACE = "enterprise";
    
    public static final String RESOURCES_SPACE = "resources";
}
