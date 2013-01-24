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

 package net.project.schedule;

import net.project.base.compatibility.Compatibility;

/**
 * This class is a utility for the task list to be able to see if a task should
 * be visible in the task list or if its tree node should be hidden.
 *
 * @author Matthew Flower
 * @since Version 8.0.2
 */
public class VisibilityListUtil {
    private VisibilityList visList;

    public VisibilityListUtil() {
        Schedule schedule = (Schedule)Compatibility.getSessionProvider().getAttribute("schedule");
        visList = schedule.getVisibilityList();
    }

    public boolean isHidden(String id) {
        return !visList.isVisible(id);
    }

    public boolean childrenVisible(String id) {
        return visList.isChildrenVisible(id);
    }
}
