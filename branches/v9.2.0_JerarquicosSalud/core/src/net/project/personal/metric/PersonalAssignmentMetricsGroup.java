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

 package net.project.personal.metric;

import net.project.calendar.metric.MeetingAssignmentMetrics;
import net.project.metric.MetricsGroup;
import net.project.metric.MetricsType;
import net.project.metric.MetricsTypeException;
import net.project.persistence.PersistenceException;
import net.project.schedule.metric.TaskAssignmentMetrics;
import net.project.security.User;

/**
 * Concrete implementation of the <code>MetricsGroup</code>.
 * <p>
 * Represents a collection of personal assignment metrics -- specifically <code>Metrics</code> of type
 * <code>MetricsType.ASSIGNMENT</code>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class PersonalAssignmentMetricsGroup extends MetricsGroup {

    /**
     * User for which to load personal assignment metrics
     */
    private final User user;

    /**
     * Returns a loaded MetricsGroup for all personal AssignmentMetrics
     *
     * @param user for which to load metrics
     * @return a loaded MetricsGroup for all personal AssignmentMetrics
     * @throws PersistenceException if load operation fails
     * @throws net.project.metric.MetricsTypeException if metrics of the wrong type are loaded.
     */
    public static PersonalAssignmentMetricsGroup makeLoaded(User user) throws PersistenceException, MetricsTypeException {

        PersonalAssignmentMetricsGroup metricsGroup = new PersonalAssignmentMetricsGroup(user);
        metricsGroup.loadMetrics();

        return metricsGroup;
    }

    /**
     * Creates a new PersonalAssignmentMetricsGroup.
     * <p.
     * Private to ensure use of static factory method.
     *
     * @param user
     */
    private PersonalAssignmentMetricsGroup(User user) {
        super(MetricsType.PERSONAL_ASSIGNMENT);
        this.user = user;
    }

    /**
     * Loads personal assignment metrics.
     * <p>
     * Metrics loaded are:
     * <br>In Progress Task Assignments
     * <br>Completed Task Assignemnts
     *
     * @throws PersistenceException if DB load operation fails
     * @throws MetricsTypeException if a Metrics of the wrong type is loaded.
     */
    private void loadMetrics() throws PersistenceException, MetricsTypeException {

        PersonalAssignmentGroupTotalMetrics total = new PersonalAssignmentGroupTotalMetrics(this.user);

        // first load the various metrics for this group.
        PersonalAssignmentMetrics inProgressTask = TaskAssignmentMetrics.makeInProgressAssignmentMetrics(this.user);
        PersonalAssignmentMetrics completedTask = TaskAssignmentMetrics.makeCompletedMetrics(this.user);
        PersonalAssignmentMetrics meeting = MeetingAssignmentMetrics.makeMeetingMetrics(this.user);

        // next add a final metric which totals the others.
        total.addMetricsToTotal (inProgressTask);
        total.addMetricsToTotal(completedTask);
        total.addMetricsToTotal(meeting);
        total.load();

        // finally add all metrics to the group
        add(inProgressTask);
        add(completedTask);
        add(meeting);
        add(total);
    }

}
