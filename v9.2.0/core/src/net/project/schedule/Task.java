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

import java.io.Serializable;


/**
 * Task object. Task objects will be displayed on the space schedule. Types
 * are summary - duration > 0 milestone - duration = 0
 *
 * @author AdamKlatzkin
 */
public class Task extends ScheduleEntry implements Serializable {

    /**
     * Get the type of task
     *
     * @return
     */
    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    //The clone method is implemented in ScheduleEntry.  If any substantial
    //functionality is added to this class, it needs to be "cloneable".
    public Object clone() {
        return super.clone();
    }
}
