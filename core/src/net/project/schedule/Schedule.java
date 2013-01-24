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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Identifiable;
import net.project.base.ObjectType;
import net.project.base.PnetRuntimeException;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.TimeQuantityFinderSorter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.crossspace.IExportableObject;
import net.project.crossspace.interdependency.ExternalDependencies;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.events.EventType;
import net.project.gui.html.HTMLOption;
import net.project.methodology.model.LinkContainer;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonalPropertyMap;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.report.PhaseFilter;
import net.project.schedule.report.TaskTypeFilter;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.DateUtils;
import net.project.util.Node;
import net.project.util.NodeFactory;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Object which represents a schedule as shown in a workplan.  Contains
 * summaries, milestones & tasks.
 *
 * @author Carlos Montemui�o
 * @author Roger Bly
 * @author Matthew Flower
 * @author Tim Morrow
 * @since 03/00
 */
public class Schedule implements IJDBCPersistence, IXMLPersistence, IExportableObject, Serializable, Identifiable {
    //
    // Static Members
    //

    // This is not a schedule entry but can be used as a parameter in the array passed to loadEntries
    //MAFTODO: we need to get rid of this variable.
    /**
     * A <code>String</code> used to represent all schedule entry types.  This
     * is sometimes used when loading schedule entries.
     *
     * @deprecated as of Version 7.6.  Please use
     * {@link net.project.schedule.TaskType#ALL} instead.
     */
    public static final String ALL = "all";

    /** Constant used to signify that the sort should occur in ascending order. */
    public static final int ORDER_ASCENDING = 0;
    /** Constant used to signify that the sort should occur in descending order. */
    public static final int ORDER_DESCENDING = 1;

    /** List of database columns we can sort by. */
    public static final List SORTABLE_COLUMNS =
        Arrays.asList(new ColumnDefinition[]{
            TaskFinder.SEQUENCE_NUMBER_COLUMN,
            TaskFinder.NAME_COLUMN, TaskFinder.TYPE_COLUMN,
            TaskFinder.WORK_COLUMN, TaskFinder.DURATION_COLUMN,
            TaskFinder.DATE_START_COLUMN, TaskFinder.DATE_FINISH_COLUMN,
            TaskFinder.WORK_PERCENT_COMPLETE_COLUMN});

    /** Entries should be displayed without hierarchy. */
    public static final int HIERARCHY_VIEW_FLAT = 0;
    /** Entries should be displayed with hierarchy, fully expanded. */
    public static final int HIERARCHY_VIEW_EXPANDED = 1;
    /** Entries should be displayed with GANNT View. */
    public static final int HIERARCHY_VIEW_GANTT = 2;
//    public static final int HIERARCHY_VIEW_FLAT_AJAX = 3;
//    public static final int HIERARCHY_VIEW_EXPANDED_AJAX = 4;

    /**
     * An array of TaskType id's that we support.
     *
     * @deprecated as of Version 7.6.  Please use the {@link net.project.schedule.TaskType}
     * object directly instead of using these ids.
     */
    public static final String[] ENTRY_TYPES = {TaskType.SUMMARY.getID(), TaskType.MILESTONE.getID(), TaskType.TASK.getID()};

    //
    // Instance members
    //

    /**
     * The plan id of this schedule.
     */
    protected String id = null;
    /**
     * A user-supplied name for this schedule.
     */
    protected String name = null;
    /**
     * A prose description of this schedule.
     */
    protected String description = null;
    /**
     * The date on which the processing of this schedule should start.
     */
    private Date startDate = null;
    /**
     * The date on which the entries in this schedule should be completed.
     */
    private Date endDate = null;

    private Date earliestStartDate;
    private Date earliestFinishDate;
    private Date latestStartDate;
    private Date latestFinishDate;
    private TaskConstraintType startConstraint = TaskConstraintType.MUST_START_ON;
    private Date startConstraintDate;

    /**
     * This variable indicates if any warnings have been set for this schedule.
     * for ex: if the schedule is dependent and made dirty by endpointcalculation
     * of another schedule this would be set to 1.
     */
    private int warnings = 0;

    /**
     * This variable indicates if tasks should be automatically calculating
     * their end dates and durations based on task dependencies and constraints.
     */
    private boolean autocalculateTaskEndpoints = true;
    /**
     * The default working time calendar for this schedule.
     */
    private String defaultCalendarID = null;
    /**
     * The time zone used for calculating dates with base working time
     * calendars.
     */
    private TimeZone timeZone = null;

    /**
     * This variable is used when loading schedule entries.  It represents the
     * order in which a list of tasks should be loaded.
     */
    protected ColumnDefinition orderBy = (ColumnDefinition)SORTABLE_COLUMNS.get(0); // date_start
    /**
     * This variable is used in combination with the orderBy variable.  It
     * indicates if the schedule entries should be ordered in ascending or
     * descending order.
     */
    protected boolean orderAscending = true;
    /**
     * Is set to true when this schedule's properties have been loaded from the
     * database.
     */
    protected boolean isLoaded = false;
    /** the personal space, business space, project space, etc. context for the schedule. */
    protected Space space = null;
    /** Holds the tasks for this schedule. */
    protected TaskList taskList = new TaskList();

    /** Current hierarchy view. */
    private int hierarchyView = HIERARCHY_VIEW_FLAT;

    /**
     * Indicates when loading schedule entries that only entries with a status
     * of 'A' (active) should be loaded.
     */
    private boolean filterOpenItemsOnly = false;

    /** Maximum number of entries to load (-1 means load all). */
    private int maximumEntries = -1;

    /**
     * A provider of WorkingTimeCalendars, used to improve efficiency of
     * calculating with working times.
     */
    private IWorkingTimeCalendarProvider provider = null;

    /** A list of filters to apply to the task finder. */
    private FinderFilterList finderFilterList = new FinderFilterList();
    
    /** A finder listener to apply to the task finder. By default its an empty finder listener */
    private transient FinderListener finderListener = new FinderListenerAdapter();

    /** The baseline for this plan. */
    private String baselineID;
    /** The date on which the schedule was originally slated to begin. */
    private Date baselineStart;
    /** The date on which the schedule was originally intended to end. */
    private Date baselineEnd;
    /** The last date the schedule was modified */
    private Date lastModified;
    /** The userID of the last person who modified the schedule. */
    private String lastModifiedByID;
    /** The name of the person who last modified the schedule. */
    private String lastModifiedDisplayName;
    /** The time at which this schedule was loaded. */
    private Date loadTime;

    /** Predecessors which must be complete before this schedule starts. */
    private PredecessorList predecessorList = new PredecessorList();

    /** Indicates if this schedule is being shared by any other projects. */
    private boolean isShared = false;
    private boolean editingWarning = true;
    private boolean unAssignedWorkcapture = false;
    private BigDecimal hoursPerDay;
    private BigDecimal hoursPerWeek;
    private BigDecimal daysPerMonth;
    private String resourceCalendar;

    /**
     * The default task calculation type for all newly created tasks.
     * Changing this value does not affect any existing tasks.
     */
    private TaskCalculationType defaultTaskCalculationType;
    
    
    /**
     * Creates a new instance of a schedule.
     */
    public Schedule() {
    }

    /**
     * Clears out any information stored in this schedule.
     */
    public void clear() {
        id = null;
        name = null;
        description = null;
        startDate = null;
        endDate = null;
        autocalculateTaskEndpoints = true;
        warnings = 0;
        defaultCalendarID = null;
        timeZone = null;
        orderBy = (ColumnDefinition)SORTABLE_COLUMNS.get(0);
        orderAscending = true;
        isLoaded = false;
        space = null;
        taskList.clear();
        hierarchyView = HIERARCHY_VIEW_FLAT;
        filterOpenItemsOnly = false;
        maximumEntries = -1;
        provider = null;
        finderFilterList = new FinderFilterList();
        finderListener = new FinderListenerAdapter();
        earliestStartDate = null;
        earliestFinishDate = null;
        latestStartDate = null;
        latestFinishDate = null;
        startConstraint = TaskConstraintType.MUST_START_ON;
        startConstraintDate = null;
        predecessorList = new PredecessorList();
        editingWarning = true;
        unAssignedWorkcapture = true;
        hoursPerDay = BigDecimal.valueOf(8); 
        hoursPerWeek = BigDecimal.valueOf(40);
        daysPerMonth = BigDecimal.valueOf(20);
        resourceCalendar = SchedulePropertiesHelper.PERSONAL_RESOURCE_CALENDAR;

    }

    /**
     * Gets the id of this schedule.
     *
     * @return String the schedule database id
     */
    public String getID() {
        return id;
    }

    public String getType() {
        return ObjectType.SCHEDULE;
    }

    /**
     * Sets the id of this schedule.
     *
     * @param id the schedule database id
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Set the space context for this schedule: personal space, business space,
     * project space, etc.
     * <p>
     * If the current space or space id is null or the specified space
     * is different (has a different ID) from the current space,
     * the <code>loaded</code> flag is set to false.
     * </p>
     *
     * @param space a <code>Space</code> in which this schedule resides.
     */
    public void setSpace(Space space) {
        // Reset loaded flag if the space has changed
        if ((this.space == null) || (this.space.getID() == null) || (this.space.getID().equals(space.getID()))) {
            this.isLoaded = false;
        }
        this.space = space;
    }

    /**
     * Get the space in which this schedule resides.
     *
     * @return a <code>Space</code> in which this schedule resides.
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Get the space Id this schedule: personal space, business space, project
     * space, etc.
     *
     * @return String the space context id
     */
    public String getSpaceId() {
        return (this.space == null ? null : this.space.getID());
    }

    /**
     * Sets the maximum number of entries to be loaded for each entry type.
     *
     * @param maximumEntries the maximum number of entries to be loaded. Use
     * -1 to load all entries.  Note that this number applies to each entry type.
     * For example, if <code>setMaximumEntries(3)</code> is used, then 3 milestone
     * entries and 3 task entries may be loaded for a total of 6 entries.
     */
    public void setMaximumEntries(int maximumEntries) {
        this.maximumEntries = maximumEntries;
    }

    /**
     * Returns the maximum number of entries that are to be loaded.
     *
     * @return a <code>int</code> indicating the maximum number of entries to
     * load.
     */
    public int getMaximumEntries() {
        return this.maximumEntries;
    }

    /**
     * Get the list of Schedule entries.
     *
     * @return ArrayList the schedule entries
     */
    public List getEntries() {
        return new LinkedList(this.taskList.getList());
    }

    /**
     * Returns the tasklist object for this tasks of this schedule.
     *
     * @return
     */
    public TaskList getTaskList() {
        return taskList;
    }

    public ColumnVisibilityList getColumnVisibilityList() {
        ColumnVisibilityList columnVisibilityList = new ColumnVisibilityList();
        columnVisibilityList.construct(getColumnsForJSONView());
        return columnVisibilityList;
    }
    
    public ColumnPositionList getColumnPositionList() {
        ColumnPositionList columnPositionList = new ColumnPositionList();
        columnPositionList.construct(getColumnsForJSONView());
        return columnPositionList;
    }
    
    public VisibilityList getVisibilityList() {
        VisibilityList visibilityList = new VisibilityList();
        visibilityList.construct(taskList);

        return visibilityList;
    }

    /**
     * Get a map of id's to schedule entries.
     *
     * @return a <code>Map</code> object which maps task id's to schedule
     * entries.
     */
    public Map getEntryMap() {
        return this.taskList.getMap();
    }

    /**
     * This method is provided for unit test purposes.  It will allow you to set
     * the task entry map to anything you like.
     *
     * @param scheduleEntryMap a <code>Map</code> object which has been
     * populated with schedule entries.
     */
    public void setEntryMap(Map scheduleEntryMap) {
        taskList.clear();
        taskList.addAll(scheduleEntryMap.values());
        taskList.calculateHierarchyLevel();
    }

    /**
     * Return the number of schedule entries in this schedule.
     *
     * @return a <code>int</code> value indicating the number of schedule entries
     * in this schedule.
     */
    public int size() {
        return this.taskList.size();
    }

    /**
     * Get the minimum time for which this schedule will return tasks.  No task
     * return should have a start time earlier than the one returned from this
     * method.
     *
     * @return a <code>Date</code> value indicating the earliest start date of
     * tasks returned from this schedule.
     */
    public Date getEarliestTaskStartTime() {
        Date st;

        if (getEarliestTaskStartTimeMS() > 0) {
            st = new Date(getEarliestTaskStartTimeMS());
        } else {
            st = new Date();
        }

        return st;
    }

    /**
     * Get the start time of the earliest task in the schedule.
     *
     * @return a <code>long</code> containing a millisecond representation of
     * the start time of the task in the schedule with the earliest start time.
     */
    public long getEarliestTaskStartTimeMS() {
        Date earliestStartDate = taskList.getEarliestStartDate();
        return (earliestStartDate != null ? earliestStartDate.getTime() : -1);
    }

    /**
     * Get the latest finish time of any task in the schedule.
     *
     * @return the <code>Date</code> on which the latest task in the schedule
     * completes.
     */
    public Date getLatestTaskEndTime() {
        return taskList.getLatestEndDate();
    }

    /**
     * Get the latest finish date of any schedule in the scheduling list in
     * milliseconds.
     *
     * @return a <code>long</code> value containing a millisecond representation
     * of the latest task end time in the schedule.
     */
    public long getLatestTaskEndTimeMS() {
        Date latestEndDate = taskList.getLatestEndDate();
        return (latestEndDate != null ? latestEndDate.getTime() : -1);
    }

    /**
     * Get the Entry in the schedule indentified by the specified task_id.
     *
     * @param taskID the database ID of the schedule entry to return.
     * @return the Task in the schedule with the passed task_id, null if a Task
     * with the specified task_id is not in the Schedule.
     */
    public ScheduleEntry getEntry(String taskID) {
        return taskList.get(taskID);
    }

    /**
     * Set the schedule name.
     *
     * @param name a <code>String</code> containing the name of the schedule.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the schedule name.
     *
     * @return a <code>String</code> containing the name of the schedule.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the schedule description.
     *
     * @param description the description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the schedule description.
     *
     * @return a <code>String</code> containing the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the start date to use as a constraint when loading schedule entries.
     * If you do not set a start date before loading schedule entries, no limit
     * will be applied to the query.
     *
     * @param startDateFilter  the start date constraint
     */
    public void setStartDateFilter(java.util.Date startDateFilter) {
        DateFilter df = new DateFilter("sdf", TaskFinder.DATE_START_COLUMN, false);
        df.setDateRangeStart(startDateFilter);
        df.setSelected(true);

        finderFilterList.replace("sdf", df);
    }

    /**
     * Set the start date filter to use when loading schedule entries.  This
     * method allows a bit more flexibility that its overloaded method in that
     * it can specify a complete date range for the start date.
     *
     * @param startDateRange a <code>DateRange</code> object indicating the range
     * of start dates that we want to filter on when searching for schedule
     * entries.
     */
    public void setStartDateFilter(DateRange startDateRange) {
        if (startDateRange != null) {
            finderFilterList.replace("sdf", startDateRange.getDateFilter("sdf", TaskFinder.DATE_START_COLUMN, false));
        } else {
            finderFilterList.remove("sdf");
        }
    }

    /**
     * Set the start date to use as a constraint when loading schedule entries.
     * If you do not set a start date before loading schedule entries, no limit will
     * be applied to the query.
     *
     * @param startDateFilter  the start date constraint
     * @deprecated as of Version 7.6.  Please use {@link #setStartDateFilter}
     * instead.
     */
    public void setStartDate(java.util.Date startDateFilter) {
        setStartDateFilter(startDateFilter);
    }

    /**
     * Get the start date that will be used as a constraint when loading
     * schedule entries.
     *
     * @return the start date constraint
     */
    public java.util.Date getStartDateFilter() {
        return ((DateFilter)finderFilterList.get("sdf")).getDateRangeStart();
    }
    
    public String getStartDateFormatted() {
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	if(startDate != null) {
    		return sdf.format(startDate);
    	} else {
    		return org.apache.commons.lang.StringUtils.EMPTY;
    	}
    }

    /**
     * Get the start date that will be used as a constraint when loading
     * schedule entries.
     *
     * @return the start date constraint
     * @deprecated as of Version 7.6.  Please use {@link #getStartDateFilter}
     * instead.
     */
    public java.util.Date getStartDate() {
        return getStartDateFilter();
    }

    /**
     * Set the end date to use as a constraint when loading schedule entries.
     * If you do not set a end date before loading schedule entries, no limit
     * will be applied to the query.
     *
     * @param endDateFilter  the end date constraint
     */
    public void setEndDateFilter(java.util.Date endDateFilter) {
        DateFilter df = new DateFilter("edf", TaskFinder.DATE_FINISH_COLUMN, false);
        df.setDateRangeFinish(endDateFilter);
        df.setSelected(true);

        finderFilterList.replace("edf", df);
    }

    /**
     * Set the end date range for which we want to load schedule entries.
     *
     * @param endDateRange a <code>DateRange</code> which indicates the range of
     * end dates for schedule entries that we want to load.
     */
    public void setEndDateFilter(DateRange endDateRange) {
        if (endDateRange != null) {
            finderFilterList.replace("edf", endDateRange.getDateFilter("edf", TaskFinder.DATE_FINISH_COLUMN, false));
        } else {
            finderFilterList.remove("edf");
        }
    }

    /**
     * Set the end date to use as a constraint when loading schedule entries.
     * If you do not set a end date before loading schedule entries, no limit
     * will be applied to the query.
     *
     * @param endDateFilter  the end date constraint
     * @deprecated as of Version 7.6.  Please use {@link #setEndDateFilter}
     * instead.
     */
    public void setEndDate(java.util.Date endDateFilter) {
        setEndDateFilter(endDateFilter);
    }

    /**
     * Get the end date that will be used as a constraint when loading schedule
     * entries.
     *
     * @return the end date constraint
     */
    public java.util.Date getEndDateFilter() {
        DateFilter endDateFilter = (DateFilter)finderFilterList.get("edf");
        return (endDateFilter != null ? endDateFilter.getDateRangeFinish() : null);
    }

    /**
     * Get the end date that will be used as a constraint when loading schedule
     * entries.
     *
     * @return the end date constraint
     * @deprecated as of Version 7.6.  Please use {@link #getEndDateFilter}
     * instead.
     */
    public java.util.Date getEndDate() {
        return getEndDateFilter();
    }


    /**
     * Get the date on which this schedule should begin.  This date is used as
     * the start date for all tasks in this schedule that do not have a
     * predecessor.
     *
     * @return a <code>Date</code> corresponding to the start date of this
     * schedule.
     */
    public Date getScheduleStartDate() {
        if (this.startDate == null ) {
            if(taskList.size() > 0) {
                this.startDate = taskList.getEarliestStartDate();
            } else {
                // sjmittal: it can still be null
                Date startDate = new Date();
                //Make sure that the working day always starts at the beginning of the
                //day
                IWorkingTimeCalendar workingTimeCalendar = getWorkingTimeCalendar();
                if (workingTimeCalendar.isWorkingDay(startDate)) {
                    startDate = workingTimeCalendar.getStartOfWorkingDay(startDate);
                } else {
                    startDate = workingTimeCalendar.getStartOfNextWorkingDay(startDate);
                }
                return startDate;
            }
        }
        return this.startDate;
    }

    public Date getEarliestTaskStartDate() {
        Date startDate = null;

        if (taskList.size() > 0) {
            startDate = taskList.getEarliestStartDate();
        }

        return startDate;
    }

    /**
     * Set the date on which this schedule should being execution.  This date
     * is used as the start date for all tasks in this schedule that do not
     * have a predecessor.
     *
     * @param startDate a <code>Date</code> corresponding to the start date of
     * this schedule.
     */
    public void setScheduleStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public TimeQuantity getStartDateVariance() {
        TimeQuantity variance = new TimeQuantity(0, TimeQuantityUnit.DAY);

        if (baselineStart != null && startDate != null && (!baselineStart.equals(startDate))) {
            variance = getWorkingTimeCalendarProvider().getDefault().subtractWorkingTimeForDateRange(baselineStart, startDate);

        }

        return ScheduleTimeQuantity.convertToUnit(variance, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get the date on which the tasks in this schedule should be completed.
     * This date is only provided for the user's edification.
     *
     * @return a <code>Date</code> value which indicates when the schedule
     * should be completed.
     */
    public Date getScheduleEndDate() {
        return endDate;
    }

    /**
     * Set the date on which the tasks in this schedule should be completed.
     * This date is only provided for the user's edification.
     *
     * @param endDate a <code>Date</code> value on which this schedule should
     * be completed.
     */
    public void setScheduleEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TimeQuantity getEndDateVariance() {
        TimeQuantity variance = new TimeQuantity(0, TimeQuantityUnit.DAY);

        if (baselineEnd != null && endDate != null && (!baselineEnd.equals(endDate))) {
            variance = getWorkingTimeCalendarProvider().getDefault().subtractWorkingTimeForDateRange(baselineEnd, endDate);
        }

        return ScheduleTimeQuantity.convertToUnit(variance, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP);
    }

    public Date getEarliestStartDate() {
        return earliestStartDate;
    }

    public void setEarliestStartDate(Date earliestStartDate) {
        this.earliestStartDate = earliestStartDate;
    }

    public Date getEarliestFinishDate() {
        return earliestFinishDate;
    }

    public void setEarliestFinishDate(Date earliestFinishDate) {
        this.earliestFinishDate = earliestFinishDate;
    }

    public Date getLatestStartDate() {
        return latestStartDate;
    }

    public void setLatestStartDate(Date latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    public Date getLatestFinishDate() {
        return latestFinishDate;
    }

    public void setLatestFinishDate(Date latestFinishDate) {
        this.latestFinishDate = latestFinishDate;
    }

    public TaskConstraintType getStartConstraint() {
        return startConstraint;
    }

    public void setStartConstraint(TaskConstraintType startConstraint) {
        this.startConstraint = startConstraint;
    }

    public Date getStartConstraintDate() {
        return startConstraintDate;
    }

    public void setStartConstraintDate(Date startConstraintDate) {
        this.startConstraintDate = startConstraintDate;
    }

    public PredecessorList getPredecessorList() {
        return predecessorList;
    }

    void setPredecessorList(PredecessorList predecessorList) {
        this.predecessorList = predecessorList;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    /**
     * Indicates if the scheduling module will automatically recalculate all of
     * the end date of every task when a change is made to dependencies,
     * constraints, or start date of the schedule.
     *
     * @return a <code>boolean</code> indicating whether the task end dates
     * should automatically recalculated.
     */
    public boolean isAutocalculateTaskEndpoints() {
        return autocalculateTaskEndpoints;
    }

    /**
     * Set a flag indicating to the scheduling module whether or not it should
     * recalculate the end date of every task when a change is made to
     * dependencies, constraints, or the start date of the schedule.
     *
     * @param autocalculateTaskEndpoints a <code>boolean</code> value
     * indicating whether the task end dates should be automatically
     * recalculated.
     */
    public void setAutocalculateTaskEndpoints(boolean autocalculateTaskEndpoints) {
        this.autocalculateTaskEndpoints = autocalculateTaskEndpoints;
    }

    /**
     * Sets the ID of the default working time calendar for this schedule.
     * @param defaultCalendarID the ID of the working time calendar used
     * by default when calculating task dates
     */
    public void setDefaultCalendarID(String defaultCalendarID) {
        this.defaultCalendarID = defaultCalendarID;
    }
    
    /**
     * Gets the warning code for this schedule
     * 
     * @return the warnings
     */
    public int getWarnings() {
        return warnings;
    }

    /**
     * Sets the warning code for this schedule
     * Note*: this method updates the schedule's warning in the
     * database so one should not use this method with care.
     * 
     * @param warnings the warnings to set
     */
    public void setWarnings(int warnings, DBBean db) throws PersistenceException {
        if(warnings != this.warnings) {
            this.warnings = warnings;
            try {
                db.prepareStatement("update pn_plan set overallocation_warning = ? where PLAN_ID = ?");
                db.pstmt.setInt(1, this.warnings);
                DatabaseUtils.setInteger(db.pstmt, 2, this.id);
                db.executePreparedUpdate();
            } catch (SQLException sqle) {
                Logger.getLogger(Schedule.class).error("Unexpected SQLException while updating " +
                        "schedule's warnings: " + sqle);
                throw new PersistenceException("Unexpected SQLException while " +
                        "updating warning: " + sqle, sqle);
            }
            
        }
    }

    /**
     * Returns this schedule's default working time calendar ID.
     * @return the ID of the default working time calendar or null if there is
     * none
     */
    public String getDefaultCalendarID() {
        return this.defaultCalendarID;
    }

    /**
     * Specifies the time zone for this schedule.
     * <p>
     * A specific time zone is required to ensure consistent recalculation
     * of start and end dates when using a base calendar, regardless
     * of the current user's time zone.
     * </p>
     * @param timeZone the time zone
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the time zone used for manipulating dates when
     * calculating working times with a base calendar.
     * <p>
     * The time zone is specified by the schedule properties.  If no time zone
     * has been specified then the time zone is populated with the current
     * user's default time zone when the schedule is loaded.
     * </p>
     * @return the schedule time zone; or the current user's time zone
     * if none has been selected but the schedule is loaded;
     * or null if the schedule has not been loaded
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Get the primary key of the baseline for this plan.  This may be null if a
     * baseline has not yet been established.
     *
     * @return a <code>String</code> containing the primary key of the baseline
     * for this plan.
     */
    public String getBaselineID() {
        return baselineID;
    }

    /**
     * Set the baseline id for this plan.
     *
     * @param baselineID a <code>String</code> containing the primary key for
     * the baseline of this plan.
     */
    public void setBaselineID(String baselineID) {
        this.baselineID = baselineID;
    }

    /**
     * Get the date on the schedule was supposed to start, according to the last
     * baseline.
     *
     * @return a <code>Date</code> indicating when the schedule was supposed to
     * start.
     */
    public Date getBaselineStart() {
        return baselineStart;
    }

    /**
     * Set the date that the schedule was supposed to start.  This isn't stored
     * here, you'll need to store a baseline to populate this field permanently.
     *
     * @param baselineStart a <code>Date</code> which is the date that the
     * schedule was supposed to be started.
     */
    public void setBaselineStart(Date baselineStart) {
        this.baselineStart = baselineStart;
    }

    public Date getBaselineEnd() {
        return baselineEnd;
    }

    public void setBaselineEnd(Date baselineEnd) {
        this.baselineEnd = baselineEnd;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedByID() {
        return lastModifiedByID;
    }

    public void setLastModifiedByID(String lastModifiedByID) {
        this.lastModifiedByID = lastModifiedByID;
    }

    public String getLastModifiedDisplayName() {
        return lastModifiedDisplayName;
    }

    public void setLastModifiedDisplayName(String lastModifiedDisplayName) {
        this.lastModifiedDisplayName = lastModifiedDisplayName;
    }

    /**
     * Specifies the default task calculation type which affects the task
     * calculation type of newly created tasks.
     * @param taskCalculationType the task calculation type for newly created tasks
     */
    public void setDefaultTaskCalculationType(TaskCalculationType taskCalculationType) {
        this.defaultTaskCalculationType = taskCalculationType;
    }

    /**
     * Returns the current default task calculation type.
     * @return the task calculation type that newly created tasks are set to
     */
    public TaskCalculationType getDefaultTaskCalculationType() {
        return this.defaultTaskCalculationType;
    }

    /**
     * Get the time at which the schedule was loaded from the database.
     *
     * @return a <code>Date</code> indicating when the schedule was last loaded.
     */
    public Date getLoadTime() {
        return loadTime;
    }

    /**
     * Set the time at which the schedule was loaded from the database.
     *
     * @param loadTime a <code>Date</code> when the schedule was loaded.
     */
    public void setLoadTime(Date loadTime) {
        this.loadTime = loadTime;
    }

    /**
     * Set the current hierarchy view.  This is used when loading entries.<br>
     * When set to HIERARCHY_VIEW_FLAT, entries are orderer according to selected
     * order.  Otherwise, entries are ordered according to hierarchy.
     *
     * @param hierarchyView the hierarchy view
     * @see net.project.schedule.Schedule#HIERARCHY_VIEW_EXPANDED
     * @see net.project.schedule.Schedule#HIERARCHY_VIEW_FLAT
     */
    public void setHierarchyView(int hierarchyView) {
        this.hierarchyView = hierarchyView;
    }

    /**
     * Return the current hierarchy view.
     *
     * @return a <code>int</code> corresponding to a type of hierarchy view we
     * are viewing.  The values that are possible should be
     * {@link #HIERARCHY_VIEW_EXPANDED}, {@link #HIERARCHY_VIEW_FLAT} and {@link #HIERARCHY_VIEW_GANTT}.
     */
    public int getHierarchyView() {
        return this.hierarchyView;
    }

    /**
     * With this value set, the schedule will only load schedule entries that
     * appear in this phase.  If set to null, phase id will be ignored.
     *
     * @param filterPhaseID a <code>String</code> containing the phase id of the
     * phase for which we wish to load tasks.
     */
    public void setFilterPhaseID(String filterPhaseID) {
        PhaseFilter pf = new PhaseFilter("phaseFilter");
        pf.setPhaseID(new String[] {filterPhaseID});

        finderFilterList.replace("phaseFilter", pf);
    }

    /**
     * Get the phase id for which we were loading tasks.  If set to null, we are
     * loading tasks from all phases.
     *
     * @return a <code>String</code> containing the phase id that all the
     * schedule entries we are loading belong to.  If null, we are loading all
     * schedule entries regardless of phase.
     */
    public String getFilterPhaseID() {
        PhaseFilter phaseFilter = (PhaseFilter)finderFilterList.get("phaseFilter");

        if (phaseFilter == null) {
            return null;
        } else {
            return (String)phaseFilter.getPhaseID().get(0);
        }
    }

    /**
     * Setting this flag to true indicates that we should only load tasks which
     * have a status of "active".
     *
     * @param filterOpenItemsOnly a <code>boolean</code> indicating if we should
     * only load "open" tasks.
     */
    public void setFilterOpenItemsOnly(boolean filterOpenItemsOnly) {
        this.filterOpenItemsOnly = filterOpenItemsOnly;
    }

    /**
     * Get a flag indicating if we are only loading tasks with an active status.
     *
     * @return a flag indicating if we are only loading tasks with an active
     * status.
     */
    public boolean getFilterOpenItemsOnly() {
        return this.filterOpenItemsOnly;
    }
    
    public List<String> getColumnsForJSONView()
    {
        List<String> columns = new ArrayList<String>();
        columns.add("sequence");
        columns.add("name");
        columns.add("phase");
        columns.add("priority");
        columns.add("calculationType");
        columns.add("startDate");
        columns.add("actualStartDate");
        columns.add("baselineStartDate");
        columns.add("startVariance");
        columns.add("endDate");
        columns.add("actualEndDate");
        columns.add("baselineEndDate");
        columns.add("endVariance");
        columns.add("work");
        columns.add("baselineWork");
        columns.add("workVariance");
        columns.add("workComplete");        
        columns.add("duration");  
        columns.add("baselineDuration");
        columns.add("durationVariance");
        columns.add("workPercentComplete");
        columns.add("statusNotifiers");
        columns.add("resources");
        columns.add("dependencies");
        columns.add("wbs");
        return columns;
    }

    public String getJSONForSchedule(final PersonalPropertyMap propertyMap ,int offSet, int range) {
        final List<String> columns = getColumnsForJSONView(); 
        ColumnVisibilityList columnVisibilityList = new ColumnVisibilityList();
        columnVisibilityList.construct(columns);
        
        VisibilityList visibilityList = getVisibilityList();
        Iterator iterator = this.taskList.getList().subList(offSet, range).iterator();
        final NodeFactory factory = new NodeFactory(columns);
        if (!this.taskList.isEmpty()) {
            while(iterator.hasNext()) {
                IScheduleEntry entry = (IScheduleEntry) iterator.next();
                boolean visible = visibilityList.isChildrenVisible(entry.getID());
                entry.addToJSONStore(factory, propertyMap, getWorkingTimeCalendarProvider(), getHierarchyView() == 0, visible);
            }
        }
        StringBuilder json = new StringBuilder();
        List<Node> nodes = factory.getNodes();
        if (CollectionUtils.isNotEmpty(nodes)) {
            int order = 1;
            for (Node node : nodes) {
                /* 
                 * Check whether this node has a parent. In such a case
                 * it will be added to the json represention by its parent.
                 */
                if (null == MapUtils.getString(node.getMap(), "_parent")) {
                    node.order(order);
                    order = node.getRight() + 1;
                    json.append(node.toJSON());
                    json.append(',');
                }
            }
            json.deleteCharAt(json.length()-1);
        }
        return json.toString();
    }
    
    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {

        IScheduleEntry entry;
        StringBuffer xml = new StringBuffer();
        StringBuffer entriesXml = new StringBuffer();

        // Holds the maximum hierarchy level of tasks in this schedule
        // 12/19/2001 - Tim
        // This is really for presentation purposes; when transforming XML it is
        // useful to know this value; calculating it can be costly for large schedules
        // It would be much better to avoid including this value in the XML, but
        // it speeds up XML transformation of 700 tasks by 5 seconds (almost 50%);
        // therefore it is worth it
        int maxHierarchyLevel = 0;
        int currentLevel;

        // First build the schedule entries XML
        // Done first so that we can calculate maxHierarchyLevel
        if (!taskList.isEmpty()) {
            Iterator it = taskList.iterator();

            while (it.hasNext()) {
                entry = (IScheduleEntry)it.next();
                entriesXml.append(entry.getXMLBody());

                // Record the maximum hierarchy level for tasks
                if (entry instanceof ScheduleEntry) {
                    currentLevel = Integer.valueOf(((ScheduleEntry)entry).getHierarchyLevel()).intValue();
                    if (currentLevel > maxHierarchyLevel) {
                        maxHierarchyLevel = currentLevel;
                    }
                }
            }
        }

        xml.append("<schedule>\n");

        // Add in the max hierarchy level
        xml.append("<max_hierarchy_level>" + maxHierarchyLevel + "</max_hierarchy_level>");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>");
        xml.append("<start_date>" + DateFormat.getInstance().formatDateMedium(getScheduleStartDate()) + "</start_date>");
        xml.append("<start_date_variance>" + getStartDateVariance().toShortString(0,5) + "</start_date_variance>");
        xml.append("<start_date_variance_amount>" + getStartDateVariance().formatAmount() + "</start_date_variance_amount>");
        xml.append("<end_date>" + DateFormat.getInstance().formatDateMedium(getScheduleEndDate()) + "</end_date>");
        xml.append("<end_date_variance>" + getEndDateVariance().toShortString(0,5) + "</end_date_variance>");
        xml.append("<end_date_variance_amount>" + getEndDateVariance().getAmount() + "</end_date_variance_amount>");
        xml.append("<warning>" + warnings + "</warning>");
        xml.append("<autocalculate>" + XMLUtils.format(autocalculateTaskEndpoints) + "</autocalculate>");
        xml.append("<timezone>" + timeZone.getID() + "</timezone>");
        xml.append("<baselineStart>" + DateFormat.getInstance().formatDateMedium(baselineStart) + "</baselineStart>");
        xml.append("<baselineEnd>" + DateFormat.getInstance().formatDateMedium(baselineEnd) + "</baselineEnd>");
        xml.append("<lastModified>" + DateFormat.getInstance().formatDateMedium(lastModified) + "</lastModified>");
        xml.append("<lastModifiedByID>" + lastModifiedByID + "</lastModifiedByID>");
        xml.append("<lastModifiedByDisplayName>" + XMLUtils.escape(lastModifiedDisplayName) + "</lastModifiedByDisplayName>");

        appendSubtypeXML(xml);

        // Add in the XML for the entries
        xml.append(entriesXml);

        xml.append("</schedule>\n");
        return xml.toString();
    }

    protected void appendSubtypeXML(StringBuffer xml) {}

    /**
     * Get the XML representation of this schedule.
     * The XML document will contain the properties of the schedule and nodes for each schedule Task.
     * @return XML representation of the schedule.
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Check if the schedule for the last set space is loaded.
     *
     * @return a <code>boolean</code> indicating if the schedule is loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * This would shift the planned start and end dates of all the tasks relative to the schedule start date, 
     * actuals would be reset. This method should be called only if the workplan is copied from any other workplan 
     * or imported via MSP, and we just want the dates shifted to the current start date. 
     * If some work has been performed on this workplan then it is advisable not to use this option. 
     *
     * @see #getScheduleStartDate()
     * @throws PersistenceException if there is an error communicating with the
     * database.
     */
    public void shiftWorkplan(Date startDate, boolean autocalculateTaskEndpoints) throws PersistenceException {
        net.project.events.TaskEvent taskEvent = null;
        //first load the workplan
        if(!isLoaded()) {
            loadAll();
        } else if(this.taskList.isEmpty()) {
            loadEntries();
        }
        
        //now shift the dates
        Calendar calendar = Calendar.getInstance(getTimeZone());
        long daysToShift = DateUtils.daysBetween(calendar, getScheduleStartDate(), startDate, true);
        if(getScheduleStartDate().compareTo(startDate) > 0) 
            daysToShift = -daysToShift;
        
        IWorkingTimeCalendar workingTimeCalendar = getWorkingTimeCalendar();
        if(workingTimeCalendar.isWorkingDay(startDate))
            startDate = workingTimeCalendar.getStartOfWorkingDay(startDate);
        else
            startDate = workingTimeCalendar.getStartOfNextWorkingDay(startDate);
        //set various start dates
        setScheduleStartDate(startDate);
        setEarliestStartDate(startDate);
        setLatestStartDate(startDate);
        setBaselineStart(null);
        //this is always MSO
        setStartConstraintDate(startDate);
        
        if(autocalculateTaskEndpoints) {
            //reset various end dates since it would be calculated while auto calc
            setScheduleEndDate(null);
            setEarliestFinishDate(null);
            setLatestFinishDate(null);
        } else {
            //set this now !!!
            calendar.setTime(this.endDate == null ? getScheduleStartDate() : this.endDate);
            calendar.add(Calendar.DAY_OF_YEAR, (int) daysToShift);
            Date endTime = workingTimeCalendar.getEndOfWorkingDay(calendar.getTime());
            setScheduleEndDate(endTime);
            setEarliestFinishDate(endTime);
            setLatestFinishDate(endTime);
        }
        setBaselineEnd(null);
        
        //now set the dates for tasks
        Iterator taskIterator = taskList.iterator();
        while(taskIterator.hasNext()) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) taskIterator.next();
            //reset the actuals
            scheduleEntry.setActualStartTimeD(null);
            scheduleEntry.setActualEndTimeD(null);
            scheduleEntry.setDeadline(null);
            scheduleEntry.setUnassociatedWorkComplete(TimeQuantity.O_HOURS);
            scheduleEntry.setUnallocatedWorkComplete(TimeQuantity.O_HOURS);
                    
            Iterator assignmentIterator = scheduleEntry.getAssignmentList().iterator();
            while(assignmentIterator.hasNext()) {
               ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentIterator.next();
               if(autocalculateTaskEndpoints) {
                   //this would be set in TEC
                   assignment.setStartTime(null);
                   assignment.setEndTime(null);
               } else {
                   //set this now!!                   
                   calendar.setTime(assignment.getStartTime());
                   calendar.add(Calendar.DAY_OF_YEAR, (int) daysToShift);
                   assignment.setStartTime(calendar.getTime());
                   calendar.setTime(assignment.getEndTime());
                   calendar.add(Calendar.DAY_OF_YEAR, (int) daysToShift);
                   assignment.setEndTime(calendar.getTime());
               }
               //reset actuals for assignment               
               assignment.setActualStart(null);
               assignment.setActualFinish(null);
               assignment.setEstimatedFinish(null);
            }
            
            //calculate and set various start dates
            calendar.setTime(scheduleEntry.getStartTime());
            calendar.add(Calendar.DAY_OF_YEAR, (int) daysToShift);
            workingTimeCalendar = scheduleEntry.getResourceCalendar(getWorkingTimeCalendarProvider());
            Date startTime = workingTimeCalendar.getStartOfWorkingDay(calendar.getTime());
            
            scheduleEntry.setStartTimeD(startTime);
            scheduleEntry.setBaselineStart(null);
            scheduleEntry.setEarliestStartDate(startTime);
            scheduleEntry.setLatestStartDate(startTime);
            
            //set constraint date
            if(scheduleEntry.getConstraintType().isDateConstrained() && scheduleEntry.getConstraintDate() != null) {
                calendar.setTime(scheduleEntry.getConstraintDate());
                calendar.add(Calendar.DAY_OF_YEAR, (int) daysToShift);
                Date constraintTime;
                List<TaskConstraintType> taskConstraintList = Arrays.asList(new TaskConstraintType[] { TaskConstraintType.FINISH_NO_EARLIER_THAN, TaskConstraintType.FINISH_NO_LATER_THAN, TaskConstraintType.MUST_FINISH_ON });
                
                if (taskConstraintList.contains(scheduleEntry.getConstraintType())) {
                    constraintTime = workingTimeCalendar.getEndOfWorkingDay(calendar.getTime());
                } else {
                    constraintTime = workingTimeCalendar.getStartOfWorkingDay(calendar.getTime());
                }
                    
                scheduleEntry.setConstraintDate(constraintTime);
            }
            
            //calculate and set various end dates
            //this would have also re calculated the planned start and end dates for the assignments
            scheduleEntry.calculateEndDate(getWorkingTimeCalendarProvider());
            
            scheduleEntry.setBaselineEnd(null);
            scheduleEntry.setEarliestFinishDate(scheduleEntry.getEndTime());
            scheduleEntry.setLatestFinishDate(scheduleEntry.getEndTime());

            //store entries and its assignments
            scheduleEntry.setSendNotifications(false);
            scheduleEntry.store(false, this);
            scheduleEntry.setSendNotifications(true);
            
            //publishing event to asynchronous queue
            try {
    	        taskEvent = (net.project.events.TaskEvent) EventFactory.getEvent(ObjectType.TASK, EventType.EDITED);
    			taskEvent.setObjectID(scheduleEntry.getID());
    			taskEvent.setObjectType(ObjectType.TASK);
    			taskEvent.setSpaceID(this.getSpaceId());
    			taskEvent.setObjectRecordStatus("A");
    	    	taskEvent.publish();
            }catch (EventException e) {
    			Logger.getLogger(Schedule.class).error("Schedule.shiftWorkplan():: Task Modify Event Publishing Failed! "+ e.getMessage());
    		}
        }
        
        // set auto calc end points mode
        setAutocalculateTaskEndpoints(autocalculateTaskEndpoints);
        
        //store schedule
        store();
        
        //recalculate everything by loading fresh from db
        if(isAutocalculateTaskEndpoints()) {
            recalculateTaskTimes();
        }
    }
    /***************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************/

    /**
     * Store persists the modifyable properties of a schedule to the database.
     * It does not have any affect on the schedule's entries unless the
     * schedule's {@link #autocalculateTaskEndpoints} flag has been set.  If
     * this flag has been turned on, all of the tasks will be recalculated.
     *
     * This method will not create a new schedule (at least right now it won't.)
     * Spaces still only have one space.  Schedules are created when the space
     * is created.
     *
     * @throws PersistenceException if there is an error communicating with the
     * database.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(Schedule.class).error("Unexpected SQLException while storing " +
                "schedule: " + sqle);
            try {
                db.rollback();
            } catch (SQLException e) {
                Logger.getLogger(ScheduleEntry.class).debug("Unable to roll back schedule.");
            }
            throw new PersistenceException("Unexpected SQLException while " +
                "storing schedule: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Store persists the modifyable properties of a schedule to the database.
     * It does not have any affect on the schedule's entries unless the
     * schedule's {@link #autocalculateTaskEndpoints} flag has been set.  If
     * this flag has been turned on, all of the tasks will be recalculated.
     *
     * This method will not create a new schedule (at least right now it won't.)
     * Spaces still only have one space.  Schedules are created when the space
     * is created.
     *
     * @param db a <code>DBBean</code> which should be used to run the commands in
     * this method.
     * @throws SQLException if there is an error communicating with the
     * database.
     */
    public void store(DBBean db) throws SQLException {
        if (Validator.isBlankOrNull(id)) {
            throw new RuntimeException("Internal Error: cannot save a schedule " +
                "with a null or blank id.  Creation of Schedules is not yet" +
                "supported by the Schedule object.");
        }

        db.prepareCall("{call SCHEDULE.STORE_PLAN(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)}");

        int index = 0;
        int idIndex;
        db.cstmt.setString(++index, name);
        db.cstmt.setString(++index, description);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, startDate);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, endDate);
        db.cstmt.setBoolean(++index, autocalculateTaskEndpoints);
        db.cstmt.setString(++index, defaultCalendarID);
        db.cstmt.setString(++index, this.timeZone.getID());
        db.cstmt.setString(++index, SessionManager.getUser().getID());
        db.cstmt.setNull(++index, Types.NUMERIC);
        db.cstmt.setString(++index, defaultTaskCalculationType.getID());

        DatabaseUtils.setTimestamp(db.cstmt, ++index, earliestStartDate);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, earliestFinishDate);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, latestStartDate);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, latestFinishDate);
        db.cstmt.setString(++index, startConstraint.getID());
        DatabaseUtils.setTimestamp(db.cstmt, ++index, startConstraintDate);
        db.cstmt.setBoolean(++index, editingWarning);
        db.cstmt.setBoolean(++index, unAssignedWorkcapture);
        db.cstmt.setBigDecimal(++index, hoursPerDay);
        db.cstmt.setBigDecimal(++index, hoursPerWeek);
        db.cstmt.setBigDecimal(++index, daysPerMonth);
        db.cstmt.setString(++index, resourceCalendar);
        
        db.cstmt.registerOutParameter((idIndex = ++index), Types.NUMERIC);
        DatabaseUtils.setInteger(db.cstmt, idIndex, getID());

        db.executeCallable();

        //Get the id
        id = db.cstmt.getString(idIndex);
        
        /*
         * Make sure that anywhere this schedule is shared (imported ) it is updated.
         */
        ExternalDependencies.update(this, db);
    }


    /**
     * Load the schedule properties. Note: This does not load the schedule's
     * entries.
     *
     * @throws PersistenceException if there is a problem loading the schedule
     * properties.
     */
    public void load() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            load(db);
        } catch (SQLException sqle) {
            throw new PersistenceException("error loading schedule from database for this space=" + space.getID(), sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Load the schedule properties. Note: This does not load the schedule's
     * entries.
     *
     * @throws SQLException if there is a problem loading the schedule
     * properties.
     */
    public void load(DBBean db) throws SQLException {
        String query;

        this.isLoaded = false;

        boolean loadFromSpace = false;
        if ((space == null || space.getID() == null) && id == null) {
            throw new NullPointerException("You must set space, space_id, or id to load a schedule");
        } else {
            if (space != null) {
                loadFromSpace = true;
            }
        }

        // get the schedule properties.
        query =
            "select " +
            "    p.plan_id, p.plan_name, p.plan_desc, p.date_start, " +
            "    p.date_end, p.autocalculate_task_endpoints, p.overallocation_warning, " +
            "    p.default_calendar_id, p.timezone_id, shp.space_id, p.default_task_calc_type_id, " +
            "    p.earliest_start_date, p.earliest_finish_date, p.latest_start_date, " +
            "    p.latest_finish_date, p.constraint_type_id, p.constraint_date, SYSDATE, " + 
            "    p.inline_editing_warning, p.un_assigned_workcapture, p.hours_per_day, " +
            "    p.hours_per_week, p.days_per_month, p.resource_calendar " +
            "from " +
            "    pn_space_has_plan shp, " +
            "    pn_plan p " +
            "where " +
            "    p.plan_id = shp.plan_id ";
        if (loadFromSpace) {
            query += "    and shp.space_id = ? ";
        } else {
            query += "    and p.plan_id = ? ";
        }

        int colID = 1;
        int PLAN_ID_COL = colID++;
        int PLAN_NAME_COL = colID++;
        int PLAN_DESC_COL = colID++;
        int DATE_START_COL = colID++;
        int DATE_END_COL = colID++;
        int AUTOCALC_COL = colID++;
        int WARNINGS_COL = colID++;
        int DEFAULT_CAL_ID_COL = colID++;
        int TIMEZONE_ID_COL = colID++;
        int SPACE_ID_COL = colID++;
        int DEFAULT_TASK_CALC_TYPE_COL = colID++;
        int EARLIEST_START_DATE_COL = colID++;
        int EARLIEST_FINISH_DATE_COL = colID++;
        int LATEST_START_DATE_COL = colID++;
        int LATEST_FINISH_DATE_COL = colID++;
        int CONSTRAINT_TYPE_ID_COL = colID++;
        int CONSTRAINT_DATE_COL = colID++;
        int LOAD_TIME_COL = colID++;
        int EDITING_WARNING_COL = colID++;
        int UN_ASSIGNED_WORKCAPTURE_COL = colID++;
        int HOURS_PER_DAY_COL = colID++;
        int HOURS_PER_WEEK_COL = colID++;
        int DAYS_PER_MONTH_COL = colID++;
        int RESOURCE_CALENDAR_COL = colID++;


        db.prepareStatement(query);
        if (loadFromSpace) {
            db.pstmt.setString(1, space.getID());
        } else {
            db.pstmt.setString(1, id);
        }
        db.executePrepared();

        if (db.result.next()) {
            id = db.result.getString(PLAN_ID_COL);
            name = db.result.getString(PLAN_NAME_COL);
            description = db.result.getString(PLAN_DESC_COL);
            startDate = DatabaseUtils.getTimestamp(db.result, DATE_START_COL);
            endDate = DatabaseUtils.getTimestamp(db.result, DATE_END_COL);
            autocalculateTaskEndpoints = db.result.getBoolean(AUTOCALC_COL);
            warnings = db.result.getInt(WARNINGS_COL);
            defaultCalendarID = db.result.getString(DEFAULT_CAL_ID_COL);
            defaultTaskCalculationType = TaskCalculationType.forID(db.result.getString(DEFAULT_TASK_CALC_TYPE_COL));
            loadTime = new Date(db.result.getTimestamp(LOAD_TIME_COL).getTime());
            earliestStartDate = DatabaseUtils.getTimestamp(db.result, EARLIEST_START_DATE_COL);
            earliestFinishDate = DatabaseUtils.getTimestamp(db.result, EARLIEST_FINISH_DATE_COL);
            latestStartDate = DatabaseUtils.getTimestamp(db.result, LATEST_START_DATE_COL);
            latestFinishDate = DatabaseUtils.getTimestamp(db.result, LATEST_FINISH_DATE_COL);
            startConstraint = TaskConstraintType.getForID(db.result.getString(CONSTRAINT_TYPE_ID_COL));
            startConstraintDate = DatabaseUtils.getTimestamp(db.result, CONSTRAINT_DATE_COL);
            editingWarning = db.result.getBoolean(EDITING_WARNING_COL);
            unAssignedWorkcapture = db.result.getBoolean(UN_ASSIGNED_WORKCAPTURE_COL);
            hoursPerDay = db.result.getBigDecimal(HOURS_PER_DAY_COL);
            hoursPerWeek = db.result.getBigDecimal(HOURS_PER_WEEK_COL);
            daysPerMonth = db.result.getBigDecimal(DAYS_PER_MONTH_COL);
            resourceCalendar = db.result.getString(RESOURCE_CALENDAR_COL);

            String timeZoneID = db.result.getString(TIMEZONE_ID_COL);
            if (timeZoneID != null) {
                setTimeZone(TimeZone.getTimeZone(timeZoneID));
            } else {
                setTimeZone(SessionManager.getUser().getTimeZone());
            }

            if (space == null) {
                space = SpaceFactory.constructSpaceFromID(db.result.getString(SPACE_ID_COL), db);
            }

            // Clear out current working time calendar provider so that it can be reloaded
            clearWorkingTimeCalendarProvider();

            findDependencies(db);
        } else {
            throw new SQLException("schedule not found in database for this space=" + space.getID());
        }

        this.isLoaded = true;
    }

    private void findDependencies(DBBean db) throws SQLException {
        //Find all objects this object is shared as.
        List sharedObjectIDs = new ArrayList();
        db.prepareStatement("select imported_object_id from pn_shared where exported_object_id = ?");
        db.pstmt.setString(1, getID());
        db.executePrepared();

        while (db.result.next()) {
            sharedObjectIDs.add(db.result.getString(1));
        }

        TaskDependencyFinder tdf = new TaskDependencyFinder();
        for (Iterator it = sharedObjectIDs.iterator(); it.hasNext();) {
            String sharedObjectID = (String) it.next();
            predecessorList.addAll(tdf.findByTaskID(db, sharedObjectID));
        }
    }


    /**
     * Load everything to do with this schedule from the database.
     *
     * @throws PersistenceException if there is a problem loading anything.
     */
    public void loadAll() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            loadAll(db);
        } catch (SQLException sqle) {
            throw new PersistenceException("", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Load everything to do with this schedule from the database.
     *
     * @throws SQLException if there is a problem loading anything.
     */
    public void loadAll(DBBean db) throws SQLException {
        load(db);
        loadEntries(true, true, db);
    }

    /**
     * Load the properties of a schedule as well as all tasks of a certain task
     * type.
     *
     * @param entryType a <code>TaskType[]</code> containing the types we wish
     * to load.
     * @throws PersistenceException if there is an error loading anything.
     */
    public void loadSchedule(TaskType[] entryType) throws PersistenceException {
        load();
        loadEntries(entryType);
    }

    /**
     * Remove a schedule -- NOT IMPLEMENTED.
     *
     * @throws PersistenceException never.
     */
    public void remove() throws PersistenceException {
    }

    /**
     * Sets the order that should be applied when loading entries.
     * <p>
     * Calling this with the same index will reverse the order direction for
     * the column at that index.  The column will remain the same.
     * Calling with a different index will set the order to ascending for
     * the column at that index.  The column will change.
     * </p>
     * @param index the column to order by, in the range <code>0..{@link #SORTABLE_COLUMNS}.size</code>
     */
    public void setOrder(String index) {
        int iOrder = Integer.parseInt(index);

        if (iOrder < SORTABLE_COLUMNS.size()) {
            // Get the column for the order index
            ColumnDefinition orderByColumn = (ColumnDefinition)SORTABLE_COLUMNS.get(iOrder);

            if (orderByColumn == orderBy) {
                // Order is for the same column
                // We reverse the order direction
                orderAscending = !orderAscending;

            } else {
                // We switched columns
                // Default to ascending order
                orderBy = orderByColumn;
                orderAscending = true;
            }
        }
    }

    /**
     * Get the column by which we are ordering the result set.
     *
     * @return a <code>ColumnDefinition</code> which identifies the database
     * column that the schedule entries are ordered upon.
     */
    public ColumnDefinition getOrderBy() {
        return orderBy;
    }

    /**
     * Set the sort direction to be applied to the order that should be applied when loading entries.
     *
     * @param direction 0 or 1.
     */
    public void setOrderDirection(int direction) {
        orderAscending = (direction == 0);
    }

    public int getOrderDirection() {
        return orderAscending ? ORDER_ASCENDING : ORDER_DESCENDING;
    }

    /**
     * Only load tasks of this specific task types.
     * <p>
     * Modifies the finderFilterList by adding or replacing a filter.
     * When Milestone is specified and one of Summary or Task then all tasks are included.
     * Otherwise, only Milestones or only Tasks are included.
     * </p>
     * @param type the type of tasks to load; when null or empty it is ignored
     */
    public void setTaskType(TaskType[] type) {

        if (type != null && type.length > 0) {
            TaskTypeFilter filter = new TaskTypeFilter("type");

            Collection entryTypeList = Arrays.asList(type);
            if (entryTypeList.contains(TaskType.MILESTONE) &&
                    (entryTypeList.contains(TaskType.TASK) || entryTypeList.contains(TaskType.SUMMARY))) {
                // Milestone and one of Task or Summary; that is equivalent to all types
                // We'll load all types by not adding a filter
                filter.setSelected(false);

            } else if (entryTypeList.contains(TaskType.MILESTONE)) {
                // Milestones only
                filter.setLoadMilestones(true);
                filter.setLoadTasks(false);
                filter.setSelected(true);

            } else {
                // Tasks only (including summary tasks)
                filter.setLoadMilestones(false);
                filter.setLoadTasks(true);
                filter.setSelected(true);
            }

            finderFilterList.replace("type", filter);
        }

    }

    /**
     * Loads schedule entries for the specified types of schedule entries also
     * loading dependencies and assignments with each schedule entry.
     * <p>
     * Equivalent to calling <pre><code>
     *     loadEntries(entryType, true, true)
     * </code></pre>
     * </p>
     * @param entryType the entry types to load
     * @throws PersistenceException if there is a problem loading the
     * schedule entries
     */
    public void loadEntries(TaskType[] entryType) throws PersistenceException {
        loadEntries(entryType, true, true);
    }

    /**
     * Loads schedule entries for the specified types of schedule entries.
     *
     * @param entryType the entry types to load
     * @param isLoadDependencies true to also load dependencies with each task;
     * false means dependencies would not be loaded.  Loading dependencies
     * may degrade the performance of loading entries.
     * @param isLoadAssignments true to also load assignments with each task;
     * false means assignments will not be loaded.  Loading assignments may
     * degrade the performance of loading entries.
     * @throws PersistenceException if there is a problem loading the
     * schedule entries
     */
    public void loadEntries(TaskType[] entryType, boolean isLoadDependencies, boolean isLoadAssignments) throws PersistenceException {
        FinderFilter existingFilter = finderFilterList.deepSearch("type");
        setTaskType(entryType);
        loadEntries(isLoadDependencies, isLoadAssignments);

        //Now restore the original type filter
        if (existingFilter != null) {
            finderFilterList.replace("type", existingFilter);
        } else {
            finderFilterList.remove("type");
        }
    }

    /**
     * Loads schedule entries for the specified types of schedule entries.
     */
    public void loadEntries() throws PersistenceException {
        loadEntries(true, true);
    }

    /**
     * Loads schedule entries for the specified types of schedule entries.
     *
     * @param isLoadDependencies true to also load dependencies with each task;
     * false means dependencies would not be loaded.  Loading dependencies
     * may degrade the performance of loading entries.
     * @param isLoadAssignments true to also load assignments with each task;
     * false means assignments will not be loaded.  Loading assignments may
     * degrade the performance of loading entries.
     * @throws PersistenceException if there is a problem loading the
     * schedule entries
     */
    public void loadEntries(boolean isLoadDependencies, boolean isLoadAssignments) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            loadEntries(isLoadDependencies, isLoadAssignments, db);
        } catch (SQLException sqle) {
            throw new PersistenceException(sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Loads schedule entries for the specified types of schedule entries.
     *
     * @param isLoadDependencies true to also load dependencies with each task;
     * false means dependencies would not be loaded.  Loading dependencies
     * may degrade the performance of loading entries.
     * @param isLoadAssignments true to also load assignments with each task;
     * false means assignments will not be loaded.  Loading assignments may
     * degrade the performance of loading entries.
     * @param db a <code>DBBean</code> which we will use to load entries.
     * @throws SQLException if there is a problem loading the
     * schedule entries
     */
    public void loadEntries(boolean isLoadDependencies, boolean isLoadAssignments, DBBean db) throws SQLException {
        this.taskList.clear();

        TaskFinder tf = new TaskFinder();
        addFinderSorter(tf);
        //tf.addFinderFilterList(createDateFilterList());
        tf.addFinderFilterList(finderFilterList);
        tf.addFinderListener(finderListener);

        List taskList = tf.findScheduleEntries(space.getID(), getFilterOpenItemsOnly(), hierarchyView, getMaximumEntries(), isLoadDependencies, isLoadAssignments, db);

        this.taskList.addAll(taskList);
        this.taskList.calculateHierarchyLevel();
        this.taskList.constructVisibility();
        this.taskList.calculateVariance(getWorkingTimeCalendarProvider());
    }

    private void addFinderSorter(TaskFinder tf) {
        if (orderBy == TaskFinder.WORK_COLUMN) {
            tf.addFinderSorter(new TimeQuantityFinderSorter(TaskFinder.WORK_COLUMN, TaskFinder.WORK_UNITS_COLUMN, !orderAscending, ScheduleTimeQuantity.DEFAULT_HOURS_PER_DAY.intValue(), ScheduleTimeQuantity.DEFAULT_HOURS_PER_WEEK.intValue()));
        } else if (orderBy == TaskFinder.DURATION_COLUMN) {
            tf.addFinderSorter(new TimeQuantityFinderSorter(TaskFinder.DURATION_COLUMN, TaskFinder.DURATION_UNITS_COLUMN, !orderAscending, ScheduleTimeQuantity.DEFAULT_HOURS_PER_DAY.intValue(), ScheduleTimeQuantity.DEFAULT_HOURS_PER_WEEK.intValue()));
        } else {
            tf.addFinderSorter(new FinderSorter(orderBy, !orderAscending));
        }
    }

    /**
     * Load the specified schedule entries.
     *
     * @param entryTypes an array of Strings indicating which types of schedule
     * entries should be loaded.  The only acceptable values in this array are
     * one of the Schedule.TASK, Schedule.SUMMARY, Schedule.MILESTONE,
     * Schedule.ALL.
     * @throws PersistenceException if there is an error loading the entries
     * from the database.  (Likely a database error, like lack of connectivity.)
     */
    public void loadEntries(String[] entryTypes) throws PersistenceException {
        TaskType[] types;
        Arrays.sort(entryTypes);
        if (Arrays.binarySearch(entryTypes, Schedule.ALL) > -1) {
            //All types are included in the list, add them all
            types = TaskType.ALL;
        } else {
            types = new TaskType[entryTypes.length];
            for (int i = 0; i < entryTypes.length; i++) {
                types[i] = TaskType.getForID(entryTypes[i]);
            }
        }

        loadEntries(types);
    }

    /**
     * Load the specified schedule entries.
     *
     * @param phaseID the phase to load tasks and milestone for.
     * @param type either MILESTONE, TASK, SUMMARY.
     * @throws PersistenceException if there is a problem loading the
     * schedule entries.
     */
    public void loadEntriesByPhase(String phaseID, TaskType type) throws PersistenceException {
        // new list about to be extracted.
        taskList.clear();
        setHierarchyView(HIERARCHY_VIEW_FLAT);

        FinderFilter existingPhaseFilter = finderFilterList.deepSearch("selectedPhaseID");
        PhaseFilter pf = new PhaseFilter("selectedPhaseID");
        pf.setPhaseID(phaseID);
        pf.setSelected(true);

        finderFilterList.replace("selectedPhaseID", pf);
        loadEntries(new TaskType[] { type });

        //Replace old filter
        if (existingPhaseFilter != null) {
            finderFilterList.replace("selectedPhaseID", existingPhaseFilter);
        } else {
            finderFilterList.remove("selectedPhaseID");
        }
    }

    /**
     * Remove an entry from the schedule, adding the ability to control whether
     * or not you should run a recalculation of start, end, work, and duration
     * values for all of the tasks in the schedule when you do so.
     *
     * @param id a <code>String</code> containing the id of the schedule entry
     * you want to delete.
     * @param recalculate a <code>boolean</code> indicating if you should
     * recalculate the start, end, work, and duration for all of the tasks in
     * the schedule.
     * @param delayConsistencyCleanup a <code>boolean</code> indicating if we
     * should fix the sequence numbers and task types for this plan after we
     * delete.
     * @param isSummary a <code>String</code> containing "yes" if task is summary type 
     * otherwise "no".
     * @param deleteTask a <code>String</code> containing code whether to outindent or 
     * delete child task
     * @throws PersistenceException if an error occurs removing the task from
     * the schedule.
     */
    public void removeEntry(String id, boolean recalculate, boolean delayConsistencyCleanup, String isSummary, String deleteTask) throws PersistenceException {
        ScheduleEntry entry = taskList.remove(id);
        if (entry != null) {
            entry.remove(delayConsistencyCleanup, isSummary, deleteTask);

            if (recalculate) {
                recalculateTaskTimes();
            }
        }
    }

    /**
     * Remove an entry from the schedule.
     *
     * @param id a <code>String</code> containing the id of the entry to delete.
     * @throws PersistenceException if there is a problem communicating with the
     * database in order to remove the task.
     */
    public void removeEntry(String id) throws PersistenceException {
        removeEntry(id, true, false, "No", "0");
    }

    /**
     * Remove zero or more entries from the schedule.
     *
     * @param idList a <code>String[]</code> containing the id's of tasks that
     * the calling method would like to delete.
     * @param isSummary a <code>String[]</code> containing true or false for the 
     * id's of tasks in that order are of type summary or not.
     * @param deleteTask a <code>String[]</code> containing code for the 
     * id's of tasks in that order to determine whether to outindent or delete child tasks.
     * @throws PersistenceException if there is a problem communicating with the
     * database in order to remove the task.
     */
    public void removeEntry(String idList[], String isSummary[], String deleteTask[]) throws PersistenceException {
    	String idList1[];

    	idList1=idList[0].toString().split(",");
        for (int i = 0; i < idList1.length; i++) {
            removeEntry(idList1[i], false, (i < idList1.length-1 ? true : false), isSummary[0], deleteTask[0]);
        }
        recalculateTaskTimes();
    }

    /**
	 * Get a <code>List</code> of <code>IHTMLOption</code> objects which
	 * contain the names of tasks in this schedule.
	 * 
	 * @param idsToIgnore
	 *            a <code>List</code> of task id's that should not be included
	 *            in this list.
	 * @return a <code>List</code> of <code>IHTMLOption</code> objects
	 *         corresponding to the tasks in this schedule.
	 */
	public List getTaskNameOptionList(List idsToIgnore, boolean includeEmptyOption) {
		List options = new ArrayList();
		if (idsToIgnore == null) {
			idsToIgnore = Collections.EMPTY_LIST;
		}
		if (includeEmptyOption) {
			options.add(new HTMLOption("",PropertyProvider.get("prm.schedule.workplan.task.modify.dependencies.none.option")));
		}
		for (Iterator it = this.taskList.iterator(); it.hasNext();) {
			ScheduleEntry entry = (ScheduleEntry) it.next();
			if (!idsToIgnore.contains(entry.getID())) {
				options.add(new HTMLOption(entry.getID(), entry
						.getNameMaxLength40()));
			}
		}
		return options;
	}

    /**
	 * Get the total amount of work being performed by the tasks currently
	 * loaded in this schedule.
	 * 
	 * @return a <code>TimeQuantity</code> which represents the amount of work
	 *         being performed in the project.
	 */
    public TimeQuantity getTotalWork() {
        return this.taskList.getTotalWork();
    }

    /**
	 * Get the total amount of work completed by the tasks currently loaded in
	 * this schedule.
	 * 
	 * @return a <code>TimeQuantity</code> which represents the total amount
	 *         of work completed thus far for this schedule.
	 */
    public TimeQuantity getTotalWorkComplete() {
        return this.taskList.getTotalWorkComplete();
    }

    /**
	 * Copy a schedule into a space, making copies of each task in the schedule.
	 * 
	 * @param toSpaceID
	 *            a <code>String</code> containing the space id to which we
	 *            are going to copy these tasks.
	 * @param planID
	 *            a <code>String</code> value containing the destination plan
	 *            id that we are copying into. This is necessary because a space
	 *            can have more than one schedule (plan). If this value is null
	 *            or blank, this method will find the first plan in the database
	 *            that matches.
	 * @param phaseIdMap
	 *            a <code>HashMap</code> with a map between old phases and new
	 *            phases. This object is needed to get the cross references
	 *            between process and schedule.
	 * @return the planID of the plan actually copied to; this is the specified
	 *         planID if not null or the planID of the target space if none was
	 *         specified
	 * @throws PersistenceException
	 *             if an error occurs while copying tasks.
	 * @throws MissingScheduleException
	 *             if no schedule can be found for the target space.
	 */
	public String copyToSpace(String toSpaceID, String planID, HashMap phaseIdMap) throws PersistenceException, MissingScheduleException {
		if (!this.isLoaded) {
			load();
		}
		DBBean db = new DBBean();
		try {
			/* If the user didn't pass in a plan id, try to find one. */
			if (Validator.isBlankOrNull(planID)) {
				/* Get the plan id of the target space */
				db.prepareStatement("select plan_id from pn_space_has_plan where space_id = ?");
				db.pstmt.setString(1, toSpaceID);
				db.executePrepared();

				if (db.result.next()) {
					planID = db.result.getString("plan_id");
				} else {
					throw new MissingScheduleException("Unable to find plan id for space id " + toSpaceID);
				}
			}
			/*
			 * Load the target schedule so that when tasks are stored,
			 * calculations are performed with the correct working time
			 * calendars
			 */
			Schedule targetSchedule = new Schedule();
			targetSchedule.setID(planID);
			targetSchedule.load();
			/*
			 * Maps the original tasks ids that the tasks current have with the
			 * new ones we'll get after we store.
			 */
			Map taskIDMap = new HashMap();

			for (Iterator it = this.taskList.iterator(); it.hasNext();) {
				ScheduleEntry task = (ScheduleEntry) it.next();
				String oldTaskID = task.getID();
				
				task.setID(null);				
				task.setPlanID(planID);
				/* Now copy the cross references with process module.*/				
				if(phaseIdMap != null && phaseIdMap.size() > 0){
					String newPhaseId = (String) phaseIdMap.get(task.getPhaseID());
					if(newPhaseId != null){
						task.selectPhase(newPhaseId);
					}
				}
                task.setSendNotifications(false);
				task.store(false, targetSchedule);
                task.setSendNotifications(true);
                // set links between documents and tasks
                /*
                List<ObjectLink> documentLinks = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
                for(ObjectLink o: documentLinks){
                	Integer toObjectIdOld = o.getToObjectIdOld();
                	if(toObjectIdOld.equals(Integer.valueOf(oldTaskID))){
                		documentLinks.remove(o);
                		o.setToObjectIdNew(Integer.valueOf(task.getID()));
                		documentLinks.add(o);
                		break;
                	}
				}
                */
                
                LinkContainer.getInstance().updateLinks(Integer.valueOf(oldTaskID), Integer.valueOf(task.getID()));
                
               // LinkContainer.getInstance().setLinks(LinkContainer.DOCUMENT,documentLinks);
                
				taskIDMap.put(oldTaskID, task.getID());
			}
			/*
			 * Although we've stored all the tasks with their new numbers, they
			 * still consider their parent tasks to be the ones in the old space
			 * (That is the original ID's. We need to change the id's for parent
			 * tasks and for constraints so this doesn't happen.
			 */
			for (Iterator it = this.taskList.iterator(); it.hasNext();) {
				boolean taskAltered = false;
				ScheduleEntry task = (ScheduleEntry) it.next();
				/* First, replace the parent id if there is one. */
				if (!Validator.isBlankOrNull(task.getParentTaskID())) {
					taskAltered = true;
					task.setParentTaskID((String) taskIDMap.get(task.getParentTaskID()));
				}
				/*
				 * Now look to see if there were any dependencies on other
				 * tasks.
				 */
				PredecessorList tdl = task.getDependenciesNoLazyLoad();
				if ((tdl != null) && (tdl.size() > 0)) {
					taskAltered = true;
					for (Iterator it2 = tdl.iterator(); it2.hasNext();) {
						TaskDependency td = (TaskDependency) it2.next();
						td.setDependencyID((String) taskIDMap.get(td.getDependencyID()));
						td.setTaskID(task.getID());
					}
				}
				if (taskAltered) {
                    task.setSendNotifications(false);
					task.store(false, targetSchedule);
                    task.setSendNotifications(true);
				}
			}
			/* Now copy other schedule properties */
			targetSchedule.setTimeZone(getTimeZone());
			targetSchedule.setDefaultTaskCalculationType(getDefaultTaskCalculationType());	
			targetSchedule.setScheduleStartDate(getScheduleStartDate());
			targetSchedule.setScheduleEndDate(getScheduleEndDate());
			targetSchedule.store();
		} catch (SQLException sqle) {
			throw new PersistenceException("Unable to copy schedule due to persistence error copying task: " + sqle, sqle);
		} finally {
			db.release();
		}
		return planID;
	}

    /**
	 * Get the working time calendar for this schedule as created from the
	 * default calendar ID during load.
	 * <p>
	 * This is the schedule default working time calendar. If there is none then
	 * a system-default calendar is returned.
	 * </p>
	 * 
	 * @return a <code>IWorkingTimeCalendar</code> object which can be used to
	 *         do date arithmetic. This is usually used only for tasks.
	 */
    public IWorkingTimeCalendar getWorkingTimeCalendar() {
        // Always create a calendar using the provider so that
        // if definitions change in the provider, we get the new one
        return new DefinitionBasedWorkingTimeCalendar(getWorkingTimeCalendarProvider().getDefaultTimeZone(), getWorkingTimeCalendarProvider().getDefault());
    }

    /**
     * Recalculate the start date, end date, and duration of all tasks in this
     * schedule.
     *
     * @throws PersistenceException if there is a problem updating the tasks.
     */
    public void recalculateTaskTimes() throws PersistenceException {
        TaskEndpointCalculation calc = new TaskEndpointCalculation();
        calc.recalculateTaskTimes(this, true);
    }

    /**
	 * Recalculate the start date, end date, and duration of all the tasks in
	 * this schedule. This method differs from {@link #recalculateTaskTimes} in
	 * that this method doesn't actually save the tasks. It is more of a
	 * calculator for hypothetical situations.
	 * 
	 * To use this method, make changes to the tasks prior to running this
	 * method. This method makes no attempt to roll back the schedule to its
	 * form as it exists in the database. You will need to reload it from the
	 * database.
	 * 
	 * @throws PersistenceException
	 */
    public void calculatePotentialSchedule() throws PersistenceException {
        new TaskEndpointCalculation().recalculateTaskTimesNoLoad(this);
    }

    /**
	 * Moves a task upward in a list of schedule entries that are ordered by
	 * sequence number. This method will "swap" schedule entry with the prior
	 * schedule entry at the same hierarchical level.
	 * 
	 * @param idList
	 *            a <code>String[]</code> of tasks that we are going to
	 *            promote.
	 * @throws PersistenceException
	 *             if there is an error promoting tasks.
	 */
    public void promoteTasks(String[] idList) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.move_task_up(?,?); end;");
            for (int i = 0; i < idList.length; i++) {
                db.cstmt.setString(1, idList[i]);
                db.cstmt.setString(2, this.id);
                db.cstmt.addBatch();
            }
            db.executeCallableBatch();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while trying to promote task: " + sqle,
					sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Moves a task downward in a list of schedule entries that are ordered by
     * sequence number.  This method will "swap" the schedule entry with the
     * next schedule entry that is at the same hierarchical level.
     *
     * @param idList a <code>String[]</code> of ids which we are going to
     * demote.
     * @throws PersistenceException if there is an error demoting tasks.
     */
    public void demoteTasks(String[] idList) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.move_task_down(?,?); end;");

            for (int i = 0; i < idList.length; i++) {
                db.cstmt.setString(1, idList[i]);
                db.cstmt.setString(2, id);
                db.cstmt.addBatch();
            }

            db.executeCallableBatch();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while trying to demote task: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * List of filters we are going to apply when we search for tasks.
     *
     * @return a <code>FinderFilterList</code> we're going to apply when we
     * search for tasks.
     */
    public FinderFilterList getFinderFilterList() {
        return finderFilterList;
    }

    /**
     * Set the list of filters we're going to apply when we search for tasks.
     *
     * @param finderFilterList a <code>FinderFilterList</code> that we will
     * click control of apply to the task finder.
     */
    public void setFinderFilterList(FinderFilterList finderFilterList) {
        this.finderFilterList = finderFilterList;
    }

    public void addFinderFilter(FinderFilter finderFilter) throws DuplicateFilterIDException {
        finderFilterList.add(finderFilter);
    }

    public void clearFinderFilterList() {
        finderFilterList = new FinderFilterList();
    }
    
    /**
     * The finder listener we are going to apply.
     * 
     * @return the finderListener
     */
    public FinderListener getFinderListener() {
        return finderListener;
    }

    /**
     * The finder listener to apply.
     * 
     * @param finderListener the finderListener to set
     */
    public void setFinderListener(FinderListener finderListener) {
        this.finderListener = finderListener;
    }
    
    public void clearFinderListener() {
        finderListener = new FinderListenerAdapter();
    }

    /**
     * Indicates whether or not the schedule was loaded with filtering.  There
     * are cases where a schedule might have been filtered for display purposes
     * (such as when the user filtered it using the filter pane.)  The problem
     * is that the schedule is also used for calculation purposes.  It is only
     * effective for calculation if it isn't filtered.
     *
     * @return a <code>boolean</code> value indicating if the schedule is
     * currently being filtered.
     */
    public boolean isFiltered() {
        boolean isFiltered = false;

        for (Iterator it = finderFilterList.getAllFilters().iterator(); it.hasNext();) {
            FinderFilter finderFilter = (FinderFilter) it.next();
            if (finderFilter.isSelected()) {
                isFiltered = true;
                break;
            }
        }

        return isFiltered;
    }

    /**
     * Specified the provider to use to find working time calendars.
     * <p>
     * <b>Note:</b> This should be used for testing only.
     * </p>
     * @param provider the provider
     */
    public void setWorkingTimeCalendarProvider(IWorkingTimeCalendarProvider provider) {
        this.provider = provider;
    }

    /**
     * Clears out the provider so that subsequent access will reload it.
     * This is typically performed after the schedule is reloaded.
     */
    private void clearWorkingTimeCalendarProvider() {
        this.provider = null;
    }

    /**
     * Returns a loaded IWorkingTimeCalendarProvider.
     * @return the provider
     * @throws PnetRuntimeException if there is a problem loading the provider
     */
    public IWorkingTimeCalendarProvider getWorkingTimeCalendarProvider() {

        if (this.provider == null) {

            try {
                this.provider = ScheduleWorkingTimeCalendarProvider.make(this);
            } catch (PersistenceException e) {
                throw new PnetRuntimeException("Error creating a working time calendar provider: " + e, e);
            }
        }

        return provider;
    }

    public Object clone() {
        Schedule clone = new Schedule();
        clone.autocalculateTaskEndpoints = this.autocalculateTaskEndpoints;
        clone.warnings = this.warnings;
        clone.description = this.description;
        clone.space = this.space;
        clone.orderAscending = this.orderAscending;
        clone.id = this.id;
        clone.timeZone = this.timeZone;
        clone.orderBy = this.orderBy;
        clone.hierarchyView = this.hierarchyView;
        clone.startDate = this.startDate;
        clone.endDate = this.endDate;
        clone.defaultCalendarID = this.defaultCalendarID;
        clone.name = this.name;
        clone.maximumEntries = this.maximumEntries;
        clone.filterOpenItemsOnly = this.filterOpenItemsOnly;
        clone.finderFilterList = (FinderFilterList)this.finderFilterList.clone();
        clone.lastModified = this.lastModified;
        clone.lastModifiedByID = this.lastModifiedByID;
        clone.lastModifiedDisplayName = this.lastModifiedDisplayName;
        clone.baselineStart = this.baselineStart;
        clone.baselineEnd = this.baselineEnd;
        clone.baselineID = this.baselineID;
        clone.defaultTaskCalculationType = this.defaultTaskCalculationType;
        clone.loadTime = this.loadTime;
        clone.latestStartDate = this.latestStartDate;
        clone.startConstraint = this.startConstraint;
        clone.earliestFinishDate = this.earliestFinishDate;
        clone.earliestStartDate = this.earliestStartDate;
        clone.startConstraintDate = this.startConstraintDate;
        clone.isShared = this.isShared;
        clone.latestFinishDate = this.latestFinishDate;
        clone.editingWarning = this.editingWarning;
        clone.unAssignedWorkcapture = this.unAssignedWorkcapture;
        clone.hoursPerDay = this.hoursPerDay;
        clone.hoursPerWeek = this.hoursPerWeek;
        clone.daysPerMonth = this.daysPerMonth;
        clone.resourceCalendar = this.resourceCalendar;

        //Make copies of all the entries in the entry map
        Map clonedEntryMap = new LinkedHashMap();
        for (Iterator it = this.taskList.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();
            ScheduleEntry clonedEntry = (ScheduleEntry)scheduleEntry.clone();
            clonedEntryMap.put(clonedEntry.getID(), clonedEntry);
        }
        clone.setEntryMap(clonedEntryMap);
        // Clone the working time calendar
        if (this.provider != null) {
            clone.provider = (IWorkingTimeCalendarProvider) this.provider.clone();
        }
        return clone;
    }
    
    public void setFieldsFromSchedule(ScheduleEntry task) {
		task.setName(this.name);
		task.setDescription(this.description);

		task.setStartTimeD(getScheduleStartDate());
		task.setEndTimeD(this.endDate == null ? getScheduleStartDate() : this.endDate);
		task.setBaselineStart(this.baselineStart);
		task.setBaselineEnd(this.baselineEnd);
		task.setBaselineID(this.baselineID);
		task.setLatestStartDate(this.latestStartDate);
		task.setLatestFinishDate(this.latestFinishDate);
		task.setEarliestStartDate(this.earliestStartDate);
		task.setEarliestFinishDate(this.earliestFinishDate);

		TimeQuantity totalWork = this.getTotalWork();
		task.setWork(totalWork);
		TimeQuantity totalWorkComplete = this.getTotalWorkComplete();
		task.setWorkComplete(totalWorkComplete);
        if(totalWork.getAmount().signum() != 0)
            task.setWorkPercentComplete(totalWorkComplete.divide(totalWork, 3, BigDecimal.ROUND_HALF_UP));
        else
            task.setWorkPercentComplete(new BigDecimal("0.00000"));
		
		//sjmittal: constraint now is start and end date fixed so setting this is not needed
//		task.setConstraintDate(this.startDate);
        //set the calculation type and constraint type
        task.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        task.setConstraintType(TaskConstraintType.START_AND_END_DATES_FIXED);
        
        if(totalWork.getAmount().signum() != 0) {
            double daysBetween = DateUtils.daysBetweenGantt(new PnCalendar(getTimeZone()), task.startTime, task.endTime);
            int days = (int) daysBetween;
            double hours = (daysBetween - days) * 24;
            TimeQuantity duration = new TimeQuantity(days + hours/8.0, TimeQuantityUnit.DAY);//8 hrs = 1 day
//            DurationCalculatorHelper helper = new DurationCalculatorHelper(getTimeZone(), getWorkingTimeCalendarProvider().getDefault());
//            IDaysWorked daysWorked = helper.getDaysWorked(task.startTime, totalWork, new BigDecimal(1.0));
//            TimeQuantity duration = new TimeQuantity(daysWorked.getTotalDays(), TimeQuantityUnit.DAY);            
            task.setDuration(duration);
        }
        else {
            task.setDuration(TimeQuantity.O_DAYS);
        }
	}

    public void reloadIfNeeded() throws PersistenceException {
		DBBean db = new DBBean();
		try {
			db.prepareCall("{ ? = call SCHEDULE.IS_UP_TO_DATE(?,?) }");
			db.cstmt.registerOutParameter(1, Types.NUMERIC);
			db.cstmt.setString(2, this.id);
			DatabaseUtils.setTimestamp(db.cstmt, 3, this.loadTime);
			db.executeCallable();
			if (db.cstmt.getBoolean(1)) {
				loadAll(db);
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Schedule.class).debug(sqle);
			throw new PersistenceException(sqle);
		} catch (Throwable t) {
			Logger.getLogger(Schedule.class).debug(t);
		} finally {
			db.release();
		}
	}

    public String toString() {
        return getName() + ": (" + getScheduleStartDate() + "-" + getScheduleEndDate() + "), " + this.taskList.size() + " tasks.";
    }
    
    public String getScheduleStartDateString(){
    	return DateFormat.getInstance().formatDate(getScheduleStartDate(), java.text.DateFormat.MEDIUM);
    }
    
    public String getScheduleEndDateString(){
    	return DateFormat.getInstance().formatDate(getScheduleEndDate(), java.text.DateFormat.MEDIUM);
    }
    
    public String getSchedueEntrieCountString(){
    	return NumberFormat.getInstance().formatNumber(getEntries().size());
    }
    
    public String getSchedueWorkSummaryString(){
    	TimeQuantity divisor = getTotalWork();
        if (divisor.getAmount().signum() == 0) {
            divisor = new TimeQuantity(1, divisor.getUnits());
        }

        Object[] params = new Object[] {
            getTotalWorkComplete().toShortString(0,2),
            getTotalWork().toShortString(0,2),
            (NumberFormat.getInstance().formatNumber((getTotalWorkComplete().getAmount().doubleValue() / divisor.getAmount().doubleValue()) * 100)+"%")
        };
        return PropertyProvider.get("prm.schedule.main.work.value", params);
    }
    
    public String getFirstEntryId(){
    	return getTaskList().first() == null ? "" : getTaskList().first().getID();
    }

	/**
	 * @return the unAssignedWorkcapture
	 */
	public boolean isUnAssignedWorkcapture() {
		return unAssignedWorkcapture;
	}

	/**
	 * @param unAssignedWorkcapture the unAssignedWorkcapture to set
	 */
	public void setUnAssignedWorkcapture(boolean unAssignedWorkcapture) {
		this.unAssignedWorkcapture = unAssignedWorkcapture;
	}

	/**
	 * @return the editingWarning
	 */
	public boolean isEditingWarning() {
		return editingWarning;
	}

	/**
	 * @param editingWarning the editingWarning to set
	 */
	public void setEditingWarning(boolean editingWarning) {
		this.editingWarning = editingWarning;
	}

	/**
	 * @return the daysPerMonth
	 */
	public BigDecimal getDaysPerMonth() {
		return daysPerMonth;
	}

	/**
	 * @param daysPerMonth the daysPerMonth to set
	 */
	public void setDaysPerMonth(BigDecimal daysPerMonth) {
		this.daysPerMonth = daysPerMonth;
	}

	/**
	 * @return the hoursPerDay
	 */
	public BigDecimal getHoursPerDay() {
		return hoursPerDay;
	}

	/**
	 * @param hoursPerDay the hoursPerDay to set
	 */
	public void setHoursPerDay(BigDecimal hoursPerDay) {
		this.hoursPerDay = hoursPerDay;
	}

	/**
	 * @return the hoursPerWeek
	 */
	public BigDecimal getHoursPerWeek() {
		return hoursPerWeek;
	}

	/**
	 * @param hoursPerWeek the hoursPerWeek to set
	 */
	public void setHoursPerWeek(BigDecimal hoursPerWeek) {
		this.hoursPerWeek = hoursPerWeek;
	}

	/**
	 * @return the resourceCalendar
	 */
	public String getResourceCalendar() {
		return resourceCalendar;
	}

	/**
	 * @param resourceCalendar the resourceCalendar to set
	 */
	public void setResourceCalendar(String resourceCalendar) {
		this.resourceCalendar = resourceCalendar;
	}
    
}
