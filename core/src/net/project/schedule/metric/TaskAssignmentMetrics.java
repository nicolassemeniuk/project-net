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

 package net.project.schedule.metric;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.finder.DateFilter;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.calendar.PnCalendar;
import net.project.metric.MetricsTypeException;
import net.project.metric.QuantityAndWorkMetric;
import net.project.metric.SimpleMetricType;
import net.project.metric.WeekAndMonthMetric;
import net.project.persistence.PersistenceException;
import net.project.personal.metric.PersonalAssignmentMetrics;
import net.project.personal.metric.PersonalAssignmentMetricsType;
import net.project.resource.AssignmentFinder;
import net.project.resource.ScheduleEntryAssignment;
import net.project.security.User;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.document.XMLDocument;

/**
 * This metrics represents task assignments for a given user.
 * <p>
 * The metrics include weekly and monthly QuantityAndWorkMetric values
 * <pre>
 * For Example:
 *
 * Item             Week Qty.   Week Work       Month Qty.  Month Work
 * New Assignments     17           43h             64          212h
 * </pre>
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class TaskAssignmentMetrics extends PersonalAssignmentMetrics {

    /**
     * Returns a loaded TaskAssignmentMetrics for all "in progress" tasks for the specified user.
     * <p>
     *
     * @param user
     * @return loaded task assignment metric for assignments in progress
     * @throws PersistenceException if there is a problem loading the metrics
     */
    public static TaskAssignmentMetrics makeInProgressAssignmentMetrics(User user) throws PersistenceException {
        return (getLoadedMetrics(user, TaskAssignmentMetricsType.IN_PROGRESS));
    }

    /**
     * Returns a loaded TaskAssignmentMetrics for all "completed" tasks for the specified user.
     *
     * @param user
     * @return loaded task assignment metric for completed assignments
     * @throws PersistenceException if there is a problem loading the metrics
     */
    public static TaskAssignmentMetrics makeCompletedMetrics(User user) throws PersistenceException {
        return (getLoadedMetrics(user, TaskAssignmentMetricsType.COMPLETED));
    }


    /**
     * Returns a loaded TaskAssignmentMetrics for all tasks for the specified user.
     *
     * @param user
     * @return loaded task assignment metric for all assignments
     * @throws PersistenceException if there is a problem loading the metrics
     */
    public static TaskAssignmentMetrics makeAllTaskMetrics(User user) throws PersistenceException {
        return (getLoadedMetrics(user, TaskAssignmentMetricsType.ALL));
    }

    /**
     * Returns a loaded Metrics for specified user and assignmentType.
     *
     * @param user
     * @param assignmentType
     * @return
     * @throws PersistenceException
     */
    private static TaskAssignmentMetrics getLoadedMetrics(User user, TaskAssignmentMetricsType assignmentType) throws PersistenceException {

        TaskAssignmentMetrics metrics = new TaskAssignmentMetrics(user, assignmentType);
        metrics.load();
        return metrics;
    }

    /**
     * Specifies whether to load in progress, completed or all assignments.
     * Defaults to ALL assignments
     */
    private final TaskAssignmentMetricsType assignmentType;


    /**
     * Private constructor to create a new Metrics with the specified assignmentType.
     * <p>
     * Note: This constructor internally sets the Metrics.metricType to <code>MetricsType.PERSONAL_ASSIGNMENT</code.
     *
     * @param user           User for which this Metrics is being created.
     * @param assignmentType The assignmentType of Metrics.
     */
    private TaskAssignmentMetrics(User user, TaskAssignmentMetricsType assignmentType) {
        super(user, PersonalAssignmentMetricsType.TASK);
        this.assignmentType = assignmentType;
    }

    /**
     * Loads the WeekAndMonthMetric for task assignments based on user and TaskAssignmentType
     * <p>
     * The <code>User</code> is the user specified in the constructor for this object.
     *
     * @throws PersistenceException
     */
    protected void loadMetrics () throws PersistenceException, MetricsTypeException {

        PnCalendar calendar = new PnCalendar(getUser());

        WeekAndMonthMetric metric = new WeekAndMonthMetric();

        metric.setWeek (getMetricByDateRange(getUser(), calendar.startOfWeek(), calendar.endOfWeek(), SimpleMetricType.WEEKLY));
        metric.setMonth (getMetricByDateRange(getUser(), calendar.startOfMonth(), calendar.endOfMonth(), SimpleMetricType.MONTHLY));

        addMetric (metric);
    }


    /**
     * Get the assignment completion status for this task assignment;
     *
     * @return AssigmentCompletionStatus
     */
    private TaskAssignmentMetricsType getAssignmentType() {
        return assignmentType;
    }

    /**
     * Return a populated QuantityAndWorkMetric for a user based on the specified date range.
     * This method does not perform a commit.
     *
     * @param user
     * @param startDate
     * @param endDate
     * @param assignmentType
     * @return populated QuantityAndWorkMetric
     * @throws PersistenceException
     */
    private QuantityAndWorkMetric getMetricByDateRange(User user, Date startDate, Date endDate, SimpleMetricType assignmentType) throws PersistenceException, MetricsTypeException {

        // set initial precision to seconds to account for potential loss of precision due to
        // scale rounding errors by the TimeQUantity class.
        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.SECOND);
        AssignmentFinder finder = new AssignmentFinder();
        int metricQuantity = 0;

        // Add filters and load assignments
        addFilters(finder, user, startDate, endDate);
        Collection assignments = finder.findAll();

        for (Iterator it = assignments.iterator(); it.hasNext(); ++metricQuantity) {

            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
            totalWork = totalWork.add(assignment.getWork());

        }

        return (new QuantityAndWorkMetric(metricQuantity, totalWork.convertTo(TimeQuantityUnit.HOUR, 2), assignmentType));
    }


    /**
     * Add filters for in progress assignments.
     * Note will include all late assignments.
     *
     * @param finder    an instantiated assignment finder
     * @param dueDate   the end of the filter date range
     */
    private void addInProgressTaskFilters(AssignmentFinder finder, Date dueDate) {

        // First add date range filter to the finder
        // will load all assignments "in progress" as of the due date specified
        // (note, by definition will include late tasks
        DateFilter df = new DateFilter("endDate", AssignmentFinder.END_DATE_COLUMN, false);

        df.setDateRangeFinish(dueDate);
        df.setSelected(true);
        finder.addFinderFilter(df);


        // finally add a "completion" filter
        NumberFilter isCompleteFilter = new NumberFilter("isCompleteFilter", AssignmentFinder.IS_COMPLETE_COLUMN, false);
        isCompleteFilter.setComparator(NumberComparator.EQUALS);

        // "0" means the assignment is not complete and thus in progress
        isCompleteFilter.setNumber(0);
        isCompleteFilter.setSelected(true);


        finder.addFinderFilter(isCompleteFilter);
    }

    /**
     * Add filters for in progress assignments.
     * Note will include all late assignments.
     *
     * @param finder    an instantiated assignment finder
     * @param startDate   the start of the filter date range
     * @param finishDate   the end of the filter date range
     */
    private void addCompletedTaskFilters(AssignmentFinder finder, Date startDate, Date finishDate) {

        // First add date range filter to the finder
        DateFilter df = new DateFilter("actualFinish", AssignmentFinder.ACTUAL_FINISH_COLUMN, false);

        df.setDateRangeFinish(startDate);
        df.setDateRangeFinish(finishDate);
        df.setSelected(true);
        finder.addFinderFilter(df);


        // finally add a "completion" filter
        NumberFilter isCompleteFilter = new NumberFilter("isCompleteFilter", AssignmentFinder.IS_COMPLETE_COLUMN, false);
        isCompleteFilter.setComparator(NumberComparator.EQUALS);

        isCompleteFilter.setNumber(1);
        isCompleteFilter.setSelected(true);

        finder.addFinderFilter(isCompleteFilter);
    }


    /**
     * Add filters for in progress assignments.
     * Note will include all late assignments.
     *
     * @param finder    an instantiated assignment finder
     * @param startDate   the start of the filter date range
     * @param finishDate   the end of the filter date range
     */
    private void addAllTaskFilters(AssignmentFinder finder, Date startDate, Date finishDate) {

        // First add date range filter to the finder
        DateFilter df = new DateFilter("endDate", AssignmentFinder.END_DATE_COLUMN, false);

        df.setDateRangeFinish(startDate);
        df.setDateRangeFinish(finishDate);
        df.setSelected(true);
        finder.addFinderFilter(df);

    }



    /**
     * Add filters for users, date range and assignment completion
     *
     * @param finder    an instantiated assignment finder
     * @param user      the user for which the assignment metric is being loaded
     * @param startDate the start of the filter date range
     * @param endDate   the end of the filter date range
     */
    private void addFilters(AssignmentFinder finder, User user, Date startDate, Date endDate) throws MetricsTypeException {

        if (getAssignmentType().equals(TaskAssignmentMetricsType.ALL)) {

            addAllTaskFilters(finder, startDate, endDate);

        } else if (getAssignmentType().equals(TaskAssignmentMetricsType.COMPLETED)) {

            addCompletedTaskFilters(finder, startDate, endDate);

        } else if (getAssignmentType().equals(TaskAssignmentMetricsType.IN_PROGRESS)) {

            addInProgressTaskFilters(finder, endDate);

        } else {

            throw new MetricsTypeException ("Unable to load metrics for the specified type.");
        }

        // Next filter on Task assignments only
        TextFilter objectTypeFilter = new TextFilter("assignmentType", AssignmentFinder.OBJECT_TYPE_COLUMN, false);
        objectTypeFilter.setComparator((TextComparator) TextComparator.EQUALS);
        objectTypeFilter.setValue(ObjectType.TASK);
        objectTypeFilter.setSelected(true);

        finder.addFinderFilter(objectTypeFilter);

        // next add a filter for the user
        NumberFilter userFilter = new NumberFilter("userFilter", AssignmentFinder.PERSON_ID_COLUMN, false);
        userFilter.setComparator(NumberComparator.EQUALS);
        userFilter.setNumber(new BigDecimal(user.getID()));
        userFilter.setSelected(true);

        finder.addFinderFilter(userFilter);

    }

    /**
     * Add Specific XML properties for this subclass of <code>PersonalAssignmentMetrics</code>
     * @param xml
     */
    protected void addElements (XMLDocument xml) {
        xml.addElement (this.assignmentType.getXMLDocument());
    }

}
