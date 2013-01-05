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
package net.project.resource.filters.assignments;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;

/**
 * Filter to have the Assignment Filter only load assignments that have certain
 * statuses.
 *
 * @see net.project.resource.AssignmentStatus
 */
public class AssignmentStatusFilter extends StringDomainFilter {
    /**
     * Public constructor which creates a new filter with a unique identifier.
     *
     * @param id a <code>String</code> containing a unique identifier for this
     *
     */
    public AssignmentStatusFilter(String id, String nameToken, ColumnDefinition column, TextComparator operator) {
        super(id, nameToken, column, operator);
    }
}
