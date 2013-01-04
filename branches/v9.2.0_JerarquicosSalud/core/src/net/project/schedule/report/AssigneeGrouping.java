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

import net.project.base.finder.FinderGrouping;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.schedule.ScheduleEntry;

/**
 * This class is an {@link net.project.base.finder.FinderGrouping} implementation
 * that allows a finder to group by task assignee string.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class AssigneeGrouping extends FinderGrouping {
    /**
     * This is the group name for the group that has no assignees.
     */
    private String TASKS_WITHOUT_RESOURCES_ASSIGNED = PropertyProvider.get("prm.schedule.report.common.grouping.byresource.blankresourcetitle.name"); //"Tasks without Resources Assigned";
    private static String GROUP_BY_RESOURCE_TOKEN = "prm.schedule.report.common.grouping.byresource.name";

    /**
     * Public constructor.
     *
     * @param id a <code>String</code> value that uniquely identifies this finder
     * grouping.  This is especially important when a grouping is going to be
     * rendered to the screen or stored in a list.
     * @param isDefaultGrouping a <code>boolean</code> value that indicates if
     * this grouping should be selected as the default when it is in a list of
     * groupings.
     */
    public AssigneeGrouping(String id, boolean isDefaultGrouping) {
        super(id, PropertyProvider.get(GROUP_BY_RESOURCE_TOKEN), isDefaultGrouping);
    }

    /**
     * Get the "value" associated with this grouping.  For example, if you were
     * grouping based on assignees, a group value for a certain task might be
     * "John Smith" whereas the grouping value for another task might be
     * "David Jones".  These values are used to indicate when a {@link net.project.base.finder.GroupingIterator}
     * has crossed the boundary of one group and has gone into another.
     *
     * @param currentObject a <code>Object</code> value from which the
     * implementer will determine the grouping value.
     * @return a <code>String</code> representation of the current value of the
     * object.
     * @throws PersistenceException if there is a problem loading the assignee
     * string.
     */
    public Object getGroupingValue(Object currentObject) throws PersistenceException {
        return (currentObject == null ? null : ((ScheduleEntry)currentObject).getAssigneeString());
    }

    /**
     * Get the human readable name of this grouping that can be used to signal
     * the user what the current grouping is.  Often, this value can be identical
     * to get grouping value.  This method differs in that it can also be used
     * to add additional text to the group for display purposes.
     *
     * @param currentObject a <code>Object</code> value from which the
     * implementer will determine the grouping value.
     * @return a <code>String</code> value used to get the human-readable form
     * of the current group.
     * @throws PersistenceException if there is a problem loading the assignee
     * string.
     */
    public String getGroupName(Object currentObject) throws PersistenceException {
        ScheduleEntry currentTask = (ScheduleEntry)currentObject;
        String toReturn;

        if ((currentTask == null) ||
            (currentTask.getAssigneeString() == null) ||
            (currentTask.getAssigneeString().trim().equals(""))) {
            toReturn = TASKS_WITHOUT_RESOURCES_ASSIGNED;
        } else
            toReturn = currentTask.getAssigneeString();

        return toReturn;
    }
}
