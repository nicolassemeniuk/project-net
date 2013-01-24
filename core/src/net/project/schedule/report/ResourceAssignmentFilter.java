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

 package net.project.schedule.report;

import net.project.space.Space;

/**
 * Provides a user-type filter for filtering resource assignments.
 * <p/>
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class ResourceAssignmentFilter extends UserFilter {

    public ResourceAssignmentFilter(String id, Space userSource) {
        super(id, userSource);
    }

    public ResourceAssignmentFilter(String id, String nameToken, Space userSource) {
        super(id, nameToken, userSource);
    }

    /**
     * Default constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this
     * filter.
     * @param userSource a <code>Space</code> value that we will use to load
     * users for the list of users to filter on.
     * @param loadSpaceUsersOnly a <code>boolean</code> indicating that we should
     * only load users in the given space.
     */
    public ResourceAssignmentFilter(String id, Space userSource, boolean loadSpaceUsersOnly) {
        super(id, userSource, loadSpaceUsersOnly);
    }

    protected String getColumnName() {
        return "a.person_id";
    }

    public String getWhereClause() {
        String whereClause = "";

        if (isSelected()) {
            String idList = formatSelectedUsersIDCsv();

            if (idList.length() > 0) {
                whereClause = "("+idList+")";
            }
        }

        return whereClause;
    }

}