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

import net.project.util.VisitException;

/**
 * Defines an interface that has the ability to visit mosts items that are part
 * of a schedule.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface IScheduleVisitor {
    /**
     * Visit a schedule entry.
     *
     * @param task a <code>ScheduleEntry</code> that we are going to visit.
     * @throws VisitException if any errors occurs while visiting this task.
     */
    public void visitTask(ScheduleEntry task) throws VisitException;

    /**
     * Visit the schedule.
     *
     * @param schedule a <code>Schedule</code> object that we are going to
     * visit.
     * @throws VisitException if any errors occurs while visiting this schedule.
     */
    public void visitSchedule(Schedule schedule) throws VisitException;

    /**
     * Visit a Task Dependency for a task.
     *
     * @param dependency a <code>TaskDependency</code> that we are going to
     * visit.
     * @throws VisitException if any errors occurs while visiting this task
     * dependency.
     */
    public void visitTaskDependency(TaskDependency dependency) throws VisitException;

    /**
     * Visit the TaskDependencyList.
     *
     * @param dependencyList a <code>PredecessorList</code> that we are going to
     * visit.
     * @throws VisitException if any errors occurs while visiting this task
     * constraint.
     */
    public void visitTaskDependencyList(PredecessorList dependencyList) throws VisitException;
}
