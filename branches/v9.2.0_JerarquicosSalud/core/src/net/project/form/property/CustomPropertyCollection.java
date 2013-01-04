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
+----------------------------------------------------------------------*/
package net.project.form.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A CustomPropertyCollection is a collection of CustomProperty objects.
 * It provides mechanisms for loading and storing custom properties in
 * the database.
 */
class CustomPropertyCollection extends ArrayList implements Serializable {

    /**
     * Returns the PersistentCustomProperty whose PresentationHTMLName matches the
     * specified name or <code>null</code> if no property matches.
     *
     * @param name the HTML name to get the property for
     * @return the custom property with matching name or <code>null</code>
     */
    public PersistentCustomProperty getForHTMLName(String name) {

        PersistentCustomProperty foundProp = null;
        String propName = "prop_" + name;

        // Iterate over all properties in this collection or until we find
        // a matching property
        for (Iterator it = iterator(); it.hasNext() && foundProp == null;) {
            ICustomProperty nextProp = (ICustomProperty) it.next();

            // Since only PersistentProperties have names
            // Cast it and find out if it matches
            if (nextProp instanceof PersistentCustomProperty) {
                PersistentCustomProperty prop = (PersistentCustomProperty) nextProp;

                if (prop.getPresentationHTMLName() != null &&
                        prop.getPresentationHTMLName().equals(propName)) {

                    foundProp = prop;
                }
            }
        }

        return foundProp;
    }

}
