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

 package net.project.base;

import net.project.space.ISpaceTypes;

/**
 * This class contains constants which represent the various "secured" modules in PRMServer.
 * All new modules must be added to this file and supported as an "objectType" or the security system will not
 * work.
 * 
 * @author Lots of people
 * @since The beginning of time
 */
public class Module implements java.io.Serializable {
    protected String m_id = null;            
    protected String m_description = null;             
    protected String m_name = null;

    public static final int DOCUMENT = 10;
    public static final int DISCUSSION = 20;
    public static final int FORM = 30;
    public static final int PROCESS = 40;
    public static final int ROSTER = 50;
    public static final int SCHEDULE = 60;
    public static final int CALENDAR = 70;
    public static final int BUDGET = 90;
    public static final int METRIC = 100;
    public static final int NEWS = 110;
    public static final int WEATHER = 120;
    public static final int RESOURCE = 130;
    public static final int DIRECTORY = 140;
    public static final int PROJECT_SPACE = 150;
    public static final int PERSONAL_SPACE = 160;
    public static final int BUSINESS_SPACE = 170;
    public static final int SECURITY = 180;
    public static final int METHODOLOGY_SPACE = 190;
    public static final int WORKFLOW = 200;    
    public static final int VOTE = 210;
    public static final int MESSAGE = 220;
    public static final int SALES = 230;
    public static final int APPLICATION_SPACE = 240;
    public static final int CONFIGURATION_SPACE = 250;
    public static final int MATERIAL = 260;
    /** User Authentication Domain */
    public static final int DOMAIN = 300;
    public static final int REPORT = 310;
    public static final int ENTERPRISE_SPACE = 320;
    public static final int TRASHCAN = 330;
    public static final int WIKI = 340;    
    public static final int RESOURCES_SPACE = 360;
    public static final int BLOG = 380;  
    public static final int TIME_SUMMARY_REPORT = 390; 
    
    
    /** Set the module's database id */
    public void setId(String id) {
        m_id = id;
    }
    
    /** Get the module's database id */
    public String getId() {
        return m_id;
    }


    /** Set the description */
    public void setDescription(String description) {
        m_description = description;
    }

    /** Get the description */
    public String getDescription() {
        return m_description;
    }


     /** Set the module name */
    public void setName(String name) {
        m_name = name;
    }

    /** Get the module name */
    public String getName() {
        return m_name;
    }

    /**
     * Get the module for the space type as you would find in the ISpaceTypes
     * class.
     *
     * @param spaceType a <code>String</code> value containing the space type
     * as defined in the ISpaceType class.
     * @return a <code>int</code> value that contains the integer id of the
     * module that corresponds to the space that you passedc in to the spaceType
     * parameters.
     */
    public static int getModuleForSpaceType(String spaceType) {
        if (spaceType.equals(ISpaceTypes.APPLICATION_SPACE)) {
            return APPLICATION_SPACE;
        } else if (spaceType.equals(ISpaceTypes.BUSINESS_SPACE)) {
            return BUSINESS_SPACE;
        } else if (spaceType.equals(ISpaceTypes.CONFIGURATION_SPACE)) {
            return CONFIGURATION_SPACE;
        } else if (spaceType.equals(ISpaceTypes.METHODOLOGY_SPACE)) {
            return METHODOLOGY_SPACE;
        } else if (spaceType.equals(ISpaceTypes.PERSONAL_SPACE)) {
            return PERSONAL_SPACE;
        } else if (spaceType.equals(ISpaceTypes.PROJECT_SPACE)) {
            return PROJECT_SPACE;
        } else if (spaceType.equals(ISpaceTypes.ENTERPRISE_SPACE)) {
            return ENTERPRISE_SPACE;
        } else if (spaceType.equals(ISpaceTypes.RESOURCES_SPACE)) {
        	return RESOURCES_SPACE;
        } else {
            return -1;
        }
    }
    
    /*------------------- future methods -----------------------*/

    /*
        Returns an ArrayList of ObjectTypes owned by this Module.
    public ArrayList getOwnedObjectTypes()
    {
        // Do database query to get pn_object_types owned by this module
        // select * from pn_module_has_object_type mhot, pn_object_type ot
        // where mhot.module_id = m_id and ot.object_type = mhot.object_type and mhot.is_owner = 1
        return null;
    }
    */


    /*
        Returns an ArrayList of ObjectTypes supported or used by this Module.
    public ArrayList getSupportedObjectTypes()
    {
        // Do database query to get pn_object_types supported by this module
        // select * from pn_module_has_object_type mhot, pn_object_type ot
        // where mhot.module_id = m_id and ot.object_type = mhot.object_type
        return null;
    }
    */

    
}


