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
package net.project.schedule.report;

import net.project.base.property.PropertyProvider;
import net.project.space.Space;

/**
 * Provides a user-type filter for filtering resources (persons).
 */
public class ResourceFilter extends UserFilter {
    private static String NAME_TOKEN = "prm.schedule.report.common.filter.resourcefilter.name";

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param nameToken a <code>String</code> value which contains a token which
     * points to a human-readable name for this filter.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     */
    public ResourceFilter(String id, String nameToken, Space userSource) {
        super(id, nameToken, userSource);
    }

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     */
    public ResourceFilter(String id, Space userSource) {
        super(id, NAME_TOKEN, userSource);
    }

    protected String getColumnName() {
        return "p.person_id";
    }

    /**
     * Get a where clause for this filter that can be applied to a finder.  This
     * where clause will be empty (not null) if the filter isn't selected.
     *
     * @return a <code>String</code> containing a where clause, or an empty
     * string if the filter isn't selected.
     */
    public String getWhereClause() {
        String idList = formatSelectedUsersIDCsv();

        //Construct the where clause given the list of ids we just produced
        StringBuffer whereClause = new StringBuffer();
        if (idList.length() > 0) {
            whereClause.append("("+idList+")");
        }

        //Return our newly constructed where clause to the caller
        return whereClause.toString();
    }

    /**
     * Returns a description of this filter including the list of display names
     * of selected users.
     * @return a <code>String</code> containing a human-readable description of
     * what the filter does.
     */
    public String getFilterDescription() {
        StringBuffer nameList = new StringBuffer();
        nameList.append(formatSelectedUsersDisplayNames());
        return PropertyProvider.get("prm.schedule.report.common.filter.resourcefilter.description", nameList.toString());
    }
}
