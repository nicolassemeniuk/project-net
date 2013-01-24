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

 package net.project.base.mvc;

import java.util.HashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of ViewType classes.
 *
 * @author Matthew Flower
 * @since Version 8.0
 */
public class ViewType {
    /**
     * This list contains all of the possible types for this typed enumeration.
     */
    private static Map types = new HashMap();
    public static final ViewType INCLUDING_VIEW = new ViewType("include", "");
    public static final ViewType FORWARDING_VIEW = new ViewType("forward", "");
    public static final ViewType NOT_SPECIFIED = new ViewType("unspecified", "");
    public static final ViewType DEFAULT = INCLUDING_VIEW;

    /**
     * Get the ViewType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>ViewType</code> we want to find.
     * @return a <code>ViewType</code> corresponding to the supplied ID, or the
     *         DEFAULT <code>ViewType</code> if one cannot be found.
     */
    public static ViewType getForID(String id) {
        ViewType toReturn = (ViewType) types.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }
        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /**
     * A Unique identifier for this ViewType
     */
    private String id;
    /**
     * A token used to find a human-readable name for this ViewType
     */
    private String displayToken;

    /**
     * Private constructor which creates a new ViewType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private ViewType(String id, String displayToken) {
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
     * Return a human-readable display name for this ViewType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     *         ViewType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
