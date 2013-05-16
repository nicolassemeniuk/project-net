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

 package net.project.space;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides Collection of Spaces Types.  This class is designed to be used
 * statically, instantiations are not usually required.
 *
 * If you are attempting to create a new space, it would be a good idea to add
 * an entry to this table.  While this alone does not add a space, it is a
 * requirement to add your space here.
 *
 * @author deepak
 */

public class SpaceTypes implements ISpaceTypes {
    private static Map spaceTypeMap = new LinkedHashMap();

    /**
     *  Variables of SpaceType Nature
     */
    public static final SpaceType PERSONAL = new SpaceType(ISpaceTypes.PERSONAL_SPACE,"@prm.space.spacetypes.personal.name");
    public static final SpaceType RESOURCES = new SpaceType(ISpaceTypes.RESOURCES_SPACE, "@prm.space.spacetypes.resources.name");
    public static final SpaceType BUSINESS = new SpaceType(ISpaceTypes.BUSINESS_SPACE,"@prm.space.spacetypes.business.name");
    public static final SpaceType METHODOLOGY = new SpaceType(ISpaceTypes.METHODOLOGY_SPACE,"@prm.space.spacetypes.methodology.name");
    public static final SpaceType APPLICATION = new SpaceType(ISpaceTypes.APPLICATION_SPACE,"@prm.space.spacetypes.application.name");
    public static final SpaceType CONFIGURATION = new SpaceType(ISpaceTypes.CONFIGURATION_SPACE,"@prm.space.spacetypes.configuration.name");
    public static final SpaceType GENERIC = new SpaceType(ISpaceTypes.GENERIC_SPACE,"@prm.space.spacetypes.generic.name");
    public static final SpaceType PROJECT = new SpaceType(ISpaceTypes.PROJECT_SPACE,"@prm.space.spacetypes.project.name");
    public static final SpaceType ENTERPRISE = new SpaceType(ISpaceTypes.ENTERPRISE_SPACE,"@prm.space.spacetypes.enterprise.name");
    public static final SpaceType FINANCIAL = new SpaceType(ISpaceTypes.FINANCIAL_SPACE,"@prm.space.spacetypes.financial.name");    

    /**
     * Add a new space type to PRM.
     *
     * @param newSpaceType a <code>SpaceType</code> value that represents a new
     * space type.
     */
    public static void add(SpaceType newSpaceType) {
        spaceTypeMap.put(newSpaceType.getID(), newSpaceType);
    }

    /**
     * Get the space type that corresponds to the ISpaceType id for this space.
     *
     * @param id a <code>String</code> containing an ISpaceType string for this
     * space.
     * @return a <code>SpaceType</code> or null if a space isn't found.
     */
    public static SpaceType getForID(String id) {
        return (SpaceType)spaceTypeMap.get(id);
    }

    /**
        Converts the object to XML representation of the Space Collection.
        This method returns the Space as XML text.
        @return XML representation of the Space
     */
    public String getXML(){
        return( "<?xml version=\"1.0\" ?>\n" + getXMLBody() );
    }


    /**
      Converts the Space Types to XML representation without the XML version tag.
      This method returns the Space Types as XML text.
      @return XML representation
   */
    public static String getXMLBody(){
        StringBuffer xml = new StringBuffer(300);

        xml.append("<SpaceTypes>\n");
        Iterator itr = spaceTypeMap.values().iterator();

        while(itr.hasNext()){
            SpaceType spaceType =(SpaceType)itr.next();
            xml.append(spaceType.getXMLBody());
        }
        xml.append("</SpaceTypes>\n");
        return xml.toString();
    }

    /**
     * Return an iterator for all of the available space types.
     *
     * @return an <code>Iterator</code> object containing all available space type
     * objects.
     */
    public static Iterator iterator() {
        return spaceTypeMap.values().iterator();
    }

    /**
     * Returns all the space types as an "in" clause suitable for a select
     * statement.  For example:<code><pre>
     *  'personal', 'project', 'application'
     * </code></pre>
     * @return the "in" clause
     */
    public static String getSpaceTypesInClause() {
        StringBuffer query = new StringBuffer();
        Iterator it = spaceTypeMap.values().iterator();
        while (it.hasNext()) {
            SpaceType spaceType = (SpaceType)it.next();

            if (query.length() > 0) {
                query.append(", ");
            }

            query.append("'"+spaceType.getID()+"'");
        }

        return query.toString();
    }

}
