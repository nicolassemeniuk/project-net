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
package net.project.report;

import java.util.HashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of ReportScope classes.
 * 
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportScope {
    /**
     * This list contains all of the possible types for this typed enumeration.
     */
    private static Map types = new HashMap();
    public static final ReportScope SPACE = new ReportScope("10", "Space");
    public static final ReportScope GLOBAL = new ReportScope("20", "Global");
    public static final ReportScope DEFAULT = SPACE;

    /**
     * Get the ReportScope that corresponds to a given ID.
     * 
     * @param id a <code>String</code> value which is the primary key of a
     * <code>ReportScope</code> we want to find.
     * @return a <code>ReportScope</code> corresponding to the supplied ID, or
     *         the DEFAULT <code>ReportScope</code> if one cannot be found.
     */
    public static ReportScope getForID(String id) {
        ReportScope toReturn = (ReportScope) types.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }
        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /**
     * A Unique identifier for this ReportScope
     */
    private String id;
    /**
     * A token used to find a human-readable name for this ReportScope
     */
    private String displayToken;

    /**
     * Private constructor which creates a new ReportScope instance.
     * 
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private ReportScope(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        types.put(id, this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     * 
     * @return a <code>String</code> value containing the unique id for this
     *         type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this ReportScope.
     * 
     * @return a <code>String</code> containing a human-readable version of this
     *         ReportScope.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
