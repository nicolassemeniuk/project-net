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
package net.project.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of ScheduleView classes.
 * 
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ScheduleView {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();

    /** Entries should be displayed without hierarchy. */
    public static final ScheduleView HIERARCHICAL = new ScheduleView("1", "ScheduleView");
    /** Entries should be displayed with hierarchy, fully expanded. */
    public static final ScheduleView FLAT = new ScheduleView("2", "ScheduleView");
    /** Entries should be displayed with GANNT View. */
    public static final ScheduleView GANTT = new ScheduleView("3", "ScheduleView");
    public static final ScheduleView DEFAULT = HIERARCHICAL;

    /**
     * Get the ScheduleView that corresponds to a given ID.
     * 
     * @param id a <code>String</code> value which is the primary key of a
     * <code>ScheduleView</code> we want to find.
     * @return a <code>ScheduleView</code> corresponding to the supplied ID, or
     *         the DEFAULT <code>ScheduleView</code> if one cannot be found.
     */
    public static ScheduleView getForID(String id) {
        ScheduleView toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            ScheduleView type = (ScheduleView) it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this ScheduleView */
    private String id;
    /** A token used to find a human-readable name for this ScheduleView */
    private String displayToken;

    /**
     * Private constructor which creates a new ScheduleView instance.
     * 
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private ScheduleView(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        types.add(this);
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
     * Return a human-readable display name for this ScheduleView.
     * 
     * @return a <code>String</code> containing a human-readable version of this
     *         ScheduleView.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
