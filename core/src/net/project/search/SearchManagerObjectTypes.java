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
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.space.SpaceType;
import net.project.space.SpaceTypes;

/**
 * <code>SearchManagerObjectTypes</code> defines the types of objects that can be searched
 * using the object manager.  This class was separated to simplify the extension of SearchManager
 * to add additional search types.  (Additional search types need only add their entry here.)
 *
 * To add a new object type to search:
 *   1. Add the object tyep to the SUPPORTED_OBJECT_TYPES list.
 *   2. Implement the IObjectSearch for the new object type.
 *   3. Add the new object type to the ObjectSearchFactory.
 *
 * @author Matthew Flower
 * @since Gecko (11/01/2001)
 */
public class SearchManagerObjectTypes {
    private static final HashMap SUPPORTED_OBJECT_TYPES = new HashMap();

    //Static initializer
    static {
        SUPPORTED_OBJECT_TYPES.put(ObjectType.CALENDAR, new SearchObjectType(ObjectType.CALENDAR, "prm.user.search.type.option.calendar.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.DELIVERABLE, new SearchObjectType(ObjectType.DELIVERABLE, "prm.user.search.type.option.deliverables.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.POST, new SearchObjectType(ObjectType.POST, "prm.user.search.type.option.discussions.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.DOCUMENT, new SearchObjectType(ObjectType.DOCUMENT, "prm.user.search.type.option.documents.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.FORM_DATA, new SearchObjectType(ObjectType.FORM_DATA, "prm.user.search.type.option.forms.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.TASK, new SearchObjectType(ObjectType.TASK, "prm.user.search.type.option.tasks.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.BLOG, new SearchObjectType(ObjectType.BLOG, "prm.user.search.type.option.blogs.value"));
        SUPPORTED_OBJECT_TYPES.put(ObjectType.WIKI, new SearchObjectType(ObjectType.WIKI, "prm.user.search.type.option.wiki.value"));
    }

    /**
     * Return the list of object types that are supported in search.
     *
     * @return a <code>HashMap</code> value containing the supported search
     * object types.
     * @see net.project.search.SearchObjectType
     */
    public static HashMap getSupportedObjectTypes() {
        return SUPPORTED_OBJECT_TYPES;
    }

    /**
     * Return the list of object types that are supported in search for the Space
     *
     * @return a <code>HashMap</code> value containing the supported search
     * object types.
     * @see net.project.search.SearchObjectType
     */
    public static HashMap getSupportedObjectTypesForSpace(SpaceType spaceType) {
        HashMap supportedObjectTypeForSpace = new HashMap();

        Iterator itr = SUPPORTED_OBJECT_TYPES.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry)itr.next();
            String objectType = (String)me.getKey();

            if (spaceType != null && spaceType.getID().equals(SpaceTypes.PERSONAL.getID())) {
                if ((objectType.equals(ObjectType.CALENDAR) || objectType.equals(ObjectType.FORM_DATA) || objectType.equals(ObjectType.DOCUMENT)) && !objectType.equals(ObjectType.WIKI)) {
                    supportedObjectTypeForSpace.put(objectType, me.getValue());
                }
            } else if (spaceType != null && spaceType.getID().equals(SpaceTypes.BUSINESS.getID())) {
                if (!objectType.equals(ObjectType.TASK) && !objectType.equals(ObjectType.DELIVERABLE) && !objectType.equals(ObjectType.WIKI) && !objectType.equals(ObjectType.BLOG)) {
                    supportedObjectTypeForSpace.put(objectType, me.getValue());
                }
            } else {
                return SUPPORTED_OBJECT_TYPES;
            }
        }
        return supportedObjectTypeForSpace;
    }
}
