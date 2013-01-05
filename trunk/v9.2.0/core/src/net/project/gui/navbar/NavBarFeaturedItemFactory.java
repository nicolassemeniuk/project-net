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
|
+----------------------------------------------------------------------*/
package net.project.gui.navbar;

import net.project.base.ObjectType;
import net.project.base.PnetException;

/**
 * <code>NavBarFeaturedItemFactory</code> is responsible for creating objects that
 * will render themselves as a Navbar Item.  Generally, the items that can be
 * created are objects that implement the menu bar for a specific ObjectType
 * object type.  (These are spaces and tools for spaces.)
 *
 * @author Matthew Flower (11/12/2001)
 * @since Gecko
 */
public class NavBarFeaturedItemFactory {
    /**
     * Create a NavBarItem object for a certain object type.  Valid object types
     * are constants in {@link net.project.base.ObjectType}.
     *
     * @param spaceID The space for which we are going to load a tool object.
     * This field is required as it is used to load a collection of tools objects
     * for that space.  For example, if you were loading all form objects for
     * space number 1, this would be set to 1.
     * @param type a <code>String</code> value representing the type of object
     * that we are going to load.  If you wanted to load a FeaturedFormItems
     * object, this would be {@link net.project.base.ObjectType#FORM}.
     * @param url a <code>String</code> value that is the base URL that the
     * featured item will point to when it is clicked.
     * @return a <code>NavBarItem</code> value specific to the type passed in.
     * @exception PnetException if the type parameter is not an object type that
     * we support the creation of.
     */
    public static NavBarItem create(String spaceID, String type, String url) throws PnetException {
        if (type == ObjectType.FORM) {
            return (NavBarItem)(new FeaturedFormItems(spaceID, url));
        } else {
            //We have only implemented forms at this point
            throw new PnetException("Unsupported object type: " + type);
        }
    }
}
