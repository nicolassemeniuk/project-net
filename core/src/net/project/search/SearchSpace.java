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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.search;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.xml.XMLUtils;

/**
 * SearchSpace is a space that will be searched.  It has a name and description
 * for presentation to user.  It maintains a set of object types to be searched
 * and the specific Search Objects used to perform the actual search.
 *
 * @author Tim
 * @since 04/2001
 */
public class SearchSpace implements java.io.Serializable {
    private String spaceID = null;
    private String name = null;
    private String description = null;
    private ArrayList searchObjectTypes = null;
    private ArrayList searchObjects = null;

    public SearchSpace() {
        searchObjectTypes = new ArrayList();
        searchObjects = new ArrayList();
    }

    public void setID(String spaceID) {
        this.spaceID = spaceID;
    }

    public String getID() {
        return this.spaceID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Add an object type to be searched in this space.
     * @param searchObjectType the object type to be searched in this space
     */
    public void addSearchObjectType(String searchObjectType) {
        this.searchObjectTypes.add(searchObjectType);
    }

    void setSearchObjectTypes(ArrayList searchObjectTypes) {
        this.searchObjectTypes = searchObjectTypes;
    }

    /**
     * Return the list of object types to be searched in this space
     * @return the list of search object types, each element is of type SearchObjectType
     */
    public ArrayList getSearchObjectTypes() {
        return this.searchObjectTypes;
    }

    /**
     * Add a specific search object to be searched in this space.  Each object
     * should be of a type supported by this search space, but no checking is
     * performed.
     * @param searchObject the object to be searched in this space
     */
    public void addSearchObject(IObjectSearch searchObject) {
        this.searchObjects.add(searchObject);
    }

    /**
     * Return this list of objects to be searched in this space
     * @return the list of search objects.
     */
    public ArrayList getSearchObjects() {
        return this.searchObjects;
    }

    /**
     * Returns sum of search result count for all objects in search space
     */
    public int getResultCount() {
        IObjectSearch searchObject = null;
        int resultCount = 0;

        Iterator it = searchObjects.iterator();
        while (it.hasNext()) {
            searchObject = (IObjectSearch)it.next();
            resultCount += searchObject.getResultCount();
        }
        return resultCount;
    }

    /**
     * Returns XML results for each search object
     */
    public String getXMLResults(int start, int end) {
        IObjectSearch searchObject = null;
        StringBuffer xml = new StringBuffer();

        xml.append("<SearchSpace>\n");
        xml.append("<SpaceID>" + getID() + "</SpaceID>\n");
        xml.append("<Name>" + XMLUtils.escape(getName()) + "</Name>\n");
        xml.append("<Description>" + XMLUtils.escape(getDescription()) + "</Description>\n");

        Iterator it = searchObjects.iterator();
        while (it.hasNext()) {
            searchObject = (IObjectSearch)it.next();

            xml.append("<SearchObject>\n");
            xml.append("<Name>" + XMLUtils.escape(searchObject.getDisplayName()) + "</Name>");
            String xmlResults = searchObject.getXMLResults(start, end);
            if (xmlResults != null) {
                xml.append(xmlResults);
            }
            xml.append("</SearchObject>\n");
        }

        xml.append("</SearchSpace>\n");

        return xml.toString();
    }

}
