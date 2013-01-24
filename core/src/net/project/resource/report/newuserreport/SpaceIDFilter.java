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
package net.project.resource.report.newuserreport;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.EmptyFinderFilter;

/**
 * A finder which will limit the scope of a new user report to only showing
 * information for a single space.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class SpaceIDFilter extends EmptyFinderFilter {
    /** Space ID for which we want to show records. */
    private String spaceID;
    /**
     * Column definition for the space ID column that we will use to help
     * construct the where clause.
     */
    private ColumnDefinition spaceIDColumn;

    /**
     * Standard constructor which creates a new SpaceIDFilter.
     *
     * @param spaceIDColumn a <code>ColumnDefinition</code> which will help us
     * to create the unique where clause to limit the scope of returned results
     * for a finder to a given column.
     * @param spaceID a <code>String</code> value containing the primary key for
     * the space we wish to show new users.
     */
    public SpaceIDFilter(ColumnDefinition spaceIDColumn, String spaceID) {
        super(null, spaceIDColumn.getNameToken());
        this.setSelected(true);
        this.spaceID = spaceID;
        this.spaceIDColumn = spaceIDColumn;
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        return spaceIDColumn.getColumnName() + " = " + spaceID;
    }

    public String getFilterDescription() {
        return getName();
    }

}
