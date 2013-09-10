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

import info.bliki.wiki.model.WikiModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Identifiable;
import net.project.base.ObjectType;
import net.project.base.PnetRuntimeException;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.ICalendarEntry;
import net.project.calendar.vcal.VCalendar;
import net.project.calendar.vcal.VEvent;
import net.project.calendar.vcal.property.DescriptionProperty;
import net.project.calendar.vcal.property.EndDateTimeProperty;
import net.project.calendar.vcal.property.StartDateTimeProperty;
import net.project.calendar.vcal.property.SummaryProperty;
import net.project.calendar.vcal.property.TimeTransparencyProperty;
import net.project.calendar.vcal.property.TimeZoneProperty;
import net.project.calendar.workingtime.DaysWorked;
import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.crossspace.IExportableObject;
import net.project.crossspace.interdependency.ExternalDependencies;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.events.EventType;
import net.project.form.assignment.FormAssignment;
import net.project.gui.html.HTMLOption;
import net.project.hibernate.service.ServiceFactory;
import net.project.link.ILinkableObject;
import net.project.material.MaterialAssignment;
import net.project.material.MaterialAssignmentList;
import net.project.notification.EventCodes;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentManager;
import net.project.resource.PersonalPropertyMap;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.HTMLUtils;
import net.project.util.Node;
import net.project.util.NodeFactory;
import net.project.util.NumberFormat;
import net.project.util.TemplateFormatter;
import net.project.util.TextFormatter;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.util.VisitException;
import net.project.wiki.AddonConfiguration;
import net.project.xml.XMLConstructionException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Abstract class which represents entries that should appear in a schedule.
 * This class is related to {@link net.project.schedule.IScheduleEntry} in that
 * this class provides the "writable" version of that interface.
 *
 * @author Carlos Montemui�o
 * @author Matthew Flower
 * @since Version 7.6
 */
public abstract class ScheduleEntry implements ICalendarEntry, ILinkableObject, IScheduleEntry, IExportableObject, Serializable, Identifiable {

    //
    // Static Members
    //

    /**
	 * Updates a date with a time value appropriate for the constraint type.
	 * <p/> Start-based constraints receive a time of <code>8:00 AM</code>.<br/>
	 * End-based constraints receive a time of <code>5:00 PM</code>.
	 * 
	 * @param constraintType
	 *            the constraint type on which to base the time component
	 * @param constraintDate
	 *            the date to update
	 * @param timeZone
	 *            the time zone to use for converting the date to a calendar
	 * @return the date with updated time component
	 * @throws NullPointerException
	 *             if any parameter is null
	 */
    public static Date updateConstraintTime(TaskConstraintType constraintType, Date constraintDate, TimeZone timeZone) {

        if (constraintType == null || constraintDate == null || timeZone == null) {
            throw new NullPointerException("Missing required parameter");
        }

        Calendar cal = new GregorianCalendar(timeZone);
        cal.setTime(constraintDate);

        if (constraintType.equals(TaskConstraintType.FINISH_NO_LATER_THAN) ||
                constraintType.equals(TaskConstraintType.FINISH_NO_EARLIER_THAN) ||
                constraintType.equals(TaskConstraintType.MUST_FINISH_ON)) {

            cal.set(Calendar.HOUR_OF_DAY, 17);
            cal.set(Calendar.MINUTE, 0);

        } else if (constraintType.equals(TaskConstraintType.MUST_START_ON) ||
                constraintType.equals(TaskConstraintType.START_NO_EARLIER_THAN) ||
                constraintType.equals(TaskConstraintType.START_NO_LATER_THAN)) {

            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
        }

        return cal.getTime();
    }

    //
    // Instance Members
    //
    /** The task id of this entry.  This is the entry's primary key as well. */
    private String id = null;
    /**
     * The task version id for a specific version of this entry.  This is likely
     * to be blank.
     */
    private String taskVersionID = null;
    /** This is the plan id of the schedule that this task resides in. */
    private String planID = null;
    /** Contains the user-friendly name for this task. */
    private String name = null;
    /** A prose description for this task. */
    private String description = null;
    /** The date the schedule entry is slated to start. */
    protected Date startTime = null;
    /** The date on which the schedule entry is scheduled to be complete. */
    protected Date endTime = null;
    /**
     * The amount of work that will be required to complete this task.  Note
     * that when "work" is specified, it refers to "working hours".  For example,
     * if a task is scheduled to take 1 week to complete, this probably refers
     * to 40 hours, not 168 hours.
     */
    private TimeQuantity work = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);

    /**
     * The amount of work that has already been completed for this task.
     */
    private TimeQuantity workComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);

    /**
     * The date on which work on this task actually began. (As opposed to when
     * it was scheduled to begin.)
     */
    protected Date actualStart = null;
    /**
     * The date on which this task actually was finished.  (As opposed to when
     * it was scheduled to be completed.
     */
    protected Date actualFinish = null;
    /** The number of working days it will take to complete this task. */
    private TimeQuantity duration = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.DAY);
    /**
     * The hours of work that have not been allocated to someone working on this
     * task.
     */
    private TimeQuantity unallocatedWorkComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
    /**
     * If a user is assigned, does some work, and is unassigned the work that the
     * user completed always belongs to the user.  If there is 8 hours of work,
     * you do 2 hours and unassign yourself there is only 6 hours left to do.
     * This field is a denormalized place where that work lives.  Without this
     * field the remaining assignees would have 8 hours divide up between them
     * instead of 6 hours.
     */
    private TimeQuantity unassociatedWorkComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
    /**
     * A relative rating of the "importance" of this task.  This has no bearing
     * on the order in which tasks are performed.
     */
    private TaskPriority priority = TaskPriority.PRIORITY_NORMAL;
    /** The date on which this task was created. */
    protected Date dateCreated = null;
    /** The date on which this task was last modified. */
    protected Date dateModified = null;
    /** The primary key of the user that last modified this task. */
    protected int modifiedById = 0;
    /** The name of the user that last modified the task. */
    protected String modifiedBy = null;
    /**
     * Indicates if this task is on the critical path.  Being on the "critical
     * path" implies that if this task is completed late, it will affect the
     * completion date of the schedule.
     */
    private boolean criticalPath = false;
    /**
     * The natural order of the tasks in the workplan.  Users have the ability
     * to modify this.
     */
    private int sequenceNumber = 0;
    /**
     * This is used primarily when doing schedule endpoint calculation.  It
     * represents the earliest date that a task can start.  This field is
     * included in the object because it is useful for debugging purposes.
     */
    private Date earliestStartDate;
    /** Earliest possible finish time before constraint is taken into account. */
    private Date earliestFinishDate;
    /** Latest possible start time before constraints are taken into account. */
    private Date latestStartDate;
    /** Latest finish time before constraints are taken into account. */
    private Date latestFinishDate;
    /**
     * This variable indicates whether we should be paying attenting to the
     * times on the start and end date of the task.  If we should, we will use
     * the start of working time on that day for the start date and the end of
     * the working day for the end date.
     */
    private boolean ignoreTimePortionOfDate;

    /** Primary key of the phase that the task belongs to. */
    protected String phaseID = null;
    /** Name of the phase that the task appear in. */
    protected String phaseName = null;
    /** sequence of the phase that the task appear in. */
    protected int phaseSequence = 0;

    /**
     * Indicates whether user actually made a phase selection.
     * This is used to determine whether storage of phases is required.
     */
    private boolean isPhaseSelected = false;
    /** Indicates whether we attempted to load phases. */
    private boolean isPhasesLoaded = false;

    /** optional id of parent task */
    private String parentTaskID = null;
    /** The name of the summary task that this task belongs to. */
    private String parentTaskName = null;
    /**
     * The id of the space that this schedule is part of.  This may or may not
     * be set by default and may need to be lazy loaded.
     */
    protected String spaceID = null;
    /**
     * The hierarchy level of the task.  "0" if no hierarchy, "1".."n" if
     * hierarchy This is only set by Schedule when loading a list of entries.
     */
    private String hierarchyLevel = null;
    /** Indicates whether task has no parent in a hierarchical structure */
    private boolean isOrphan = true;
    /**
     * import task id - this is specific the the Import file used it may be
     * used later to build a hierarchy that is used to update the task with a
     * real task id.
     */
    private String importTaskID = null;
    /**
     * import parent task id - thei is specific to the Import file used. it
     * may be used later to build a hierarchy that is used to update the task
     * with a real parent task id.
     */
    private String importParentTaskID = null;
    /** A comment being added to a task. */
    private String comment = null;
    /** The tasks's comments */
    private TaskComments comments = null;
    /** The tasks that must be complete before this task starts. */
    private PredecessorList predecessorList = new PredecessorList();
    /** The tasks that will start after this task is complete. */
    private SuccessorList successorList = new SuccessorList();
    /** Capture any constraints which affect the start or end date of this task */
    private TaskConstraintType constraintType = TaskConstraintType.DEFAULT_TASK_CONSTRAINT;
    /** Some task constraints require a date, such as TaskConstraintType.FINISH_NO_LATER_THAN.
     This field gives a place to hold that information. */
    private Date constraintDate;
    /** Separate date that can apply an additional constraint. */
    private Date deadline;

    /** Field that deal with assignees */
    private boolean assigneesLoaded = false;
    private AssignmentList assignments = new AssignmentList();
    
    /** Field to store material assignments */
    private boolean materialAssigneesLoaded = false;
    private MaterialAssignmentList materialAssignments = new MaterialAssignmentList();
    


    /** Allow notifications to be turned off, which will greatly speed imports. */
    boolean sendNotifications = true;

    /**
     * This task's current record status.
     */
    private RecordStatus recordStatus = null;
    /**
     * Indicates if this task has been loaded from the database or if it is a
     * new task.
     */
    protected boolean isLoaded = false;
    /**
     * Indicates if to get full XML body (including assignments and predecessors)
     * If this is set to true then getXMLBody would load these if not loaded yet
     * @see {@link #getXMLBody()}
     */
    protected boolean isFullXMLBody = true;
    /**
     * The percentage complete based on the duration.
     * This is a value where 0 is 0% and 1 is 100%
     */
    private BigDecimal percentComplete = null;
    /**
     * The percentage complete based on the work and work complete.
     */
    private BigDecimal workPercentComplete = new BigDecimal(0);
    /**
     * Indicates if this schedule entry is a milestone
     */
    private boolean isMilestone = false;
    /** The id of the current baseline for this schedule entry. */
    private String baselineID = null;
    /** Start Date of the baseline for this schedule entry. */
    private Date baselineStart = null;
    /** End Date of the baseline for this schedule entry. */
    private Date baselineEnd = null;
    /** Work at the time the baseline was established. */
    private TimeQuantity baselineWork = null;
    /** Duration at the time the baseline was established. */
    private TimeQuantity baselineDuration = null;
    /** Outdenting status for current schedule entry*/
    private boolean unindentStatus = false;

    /**
     * Instance of IWorkingTimeCalendar provider to be used when a schedule
     * IWorkingTimeCalendarProvider is not available.
     */
    protected IWorkingTimeCalendarProvider defaultCalendarProvider;
    /**
     * Indicates if this schedule entry is an "external task" which was created
     * by using a share from another space.
     */
    private boolean isFromShare = false;
    private String sharedObjectID = null;
    private String sharingSpaceID = null;
    private String sharingSpaceName = null;
    private boolean isShareReadOnly = false;
    
    private String taskModifiedBy = null;
    
    private String formattedModifiedDate;

    /*
     * This schedule entry's calculation type, used when modifing work, duration
     * or assignments to determine what values should change.
     */
    private TaskCalculationType calculationType;
    
    private String wbs = null;
    
    private String wbsLevel = null;
    
    private boolean expanded = false; 
    
    private boolean visible = true;
    
    private String rowClass = "";
    
    private TimeQuantity startVariance;
    
    private TimeQuantity endVariance;
    
    private String evenEntryCss = null;
    
    /**
     * the id of charge code applied to this task
     * this id is stored in PnObjectHasChargeCode along with id of this task.
     * this charge code is created in root business of project where this task is created
     * if no charge codes are created then charge code selection is not posible so this field will not persist in database.
     */
    private String chargeCodeId = null;
    
    /**
     * the name of charge code applied to this task
     * the name of charge code field is added here 
     * to display applied charge code name in work plan two pane view under charge code header.
     */
    private String chargeCodeName = null;
	private boolean materialAssignmentsLoaded;

    //public boolean worker = true;
    /**
     * Reset the state of the task object's internal variables to their
     * default values.
     */
    public void clear() {
        this.id = null;
        this.taskVersionID = null;
        this.name = null;
        this.description = null;
        this.startTime = null;
        this.endTime = null;
        this.duration = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.DAY);
        this.work = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
        this.workComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
        this.workPercentComplete = new BigDecimal(0);
        this.actualStart = null;
        this.actualFinish = null;
        this.priority = TaskPriority.PRIORITY_NORMAL;
        this.dateCreated = null;
        this.dateModified = null;
        this.modifiedBy = null;
        this.modifiedById = 0;
        this.unallocatedWorkComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
        this.unassociatedWorkComplete = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
        this.phaseID = null;
        this.phaseName = null;
        this.phaseSequence = 0;
        this.isPhaseSelected = false;
        this.isPhasesLoaded = false;
        this.parentTaskID = null;
        this.parentTaskName = null;
        this.hierarchyLevel = null;
        this.isOrphan = false;
        this.importTaskID = null;
        this.importParentTaskID = null;
        this.comment = null;
        this.comments = null;
        this.isLoaded = false;
        this.earliestStartDate = null;
        this.earliestFinishDate = null;
        this.latestStartDate = null;
        this.latestFinishDate = null;
        this.predecessorList.clear();
        this.successorList.clear();
        this.constraintType = TaskConstraintType.DEFAULT_TASK_CONSTRAINT;
        this.constraintDate = null;
        this.deadline = null;
        this.spaceID = null;
        this.assignments.clear();
        this.materialAssignments.clear();
        this.percentComplete = null;
        this.isMilestone = false;
        this.sharedObjectID = null;
        this.sharingSpaceID = null;
        this.sharingSpaceName = null;
        this.isFromShare = false;
        this.isShareReadOnly = false;
        this.baselineID = null;
        this.baselineStart = null;
        this.baselineEnd = null;
        this.baselineWork = null;
        this.baselineDuration = null;
        this.wbs = null;
        this.chargeCodeId = null;
        this.chargeCodeName = null;
    }

    /**
     * Gets the id of this task.
     *
     * @return String the task database id
     */
    @Override
	public String getID() {
        return this.id;
    }

    /**
     * Sets the id of this task.
     *
     * @param id the task database id
     */
    @Override
	public void setID(String id) {
        if ((this.id == null) || (!this.id.equals(id))) {
            this.id = id;
            this.predecessorList.setTaskID(id);
            this.isLoaded = false;
            setAssigneesLoaded(false);
        }
    }

    /**
     * Get the version of this task.  This is going to be null most of the time,
     * it is only intended to be used for the history channel.
     *
     * @return a <code>String</code> containing the id of the schedule entry that
     * appears in the pn_task_version table.
     */
    public String getTaskVersionID() {
        return this.taskVersionID;
    }

    /**
     * Set the task version id of this task.
     *
     * @param taskVersionID a <code>String</code> containing the primary key of
     * a specific archived version of this schedule entry.
     */
    public void setTaskVersionID(String taskVersionID) {
        this.taskVersionID = taskVersionID;
    }

    /**
     * Sets the id of the plan (schedule) the task belongs to.
     *
     * @param id the schedule database id
     * @since 03/00
     */
    public void setPlanID(String id) {
        this.planID = id;
    }

    public String getPlanID() {
        return this.planID;
    }

    /**
     * Get the id of the space the task belongs to.
     *
     * @return a <code>String</code> value containing the id of the space that
     * the task belongs to.
     */
    @Override
	public String getSpaceID() {
        return this.spaceID;
    }

    /**
     * Set the id of the space this task belongs to.
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Returns the type id of this schedule entry's task type.
     * @return the id of the task type
     * @see #getTaskType()
     */
    @Override
	public String getType() {
        return getTaskType().getID();
    }

    public abstract TaskType getTaskType();

    /**
     * Indicates if this task is a milestone.
     *
     * @return a <code>boolean</code> indicating if this task is a milestone.
     */
    public boolean isMilestone() {
        return this.isMilestone;
    }

    /**
     * Indicate if this task is a milestone.
     *
     * @param isMilestone a <code>boolean</code> indicating if this task is a
     * milestone.
     */
    public void setMilestone(boolean isMilestone) {
        this.isMilestone = isMilestone;
    }


    /**
     * Get the task name
     *
     * @return String the name
     * @since 03/00
     */
    @Override
	public String getName() {
        return this.name;
    }

    /**
	 * This method also returns the name of the task, but if the name is longer
	 * than 40 characters, it truncates it to 40 characters and appends "...".
	 * 
	 * @return a <code>String</code> containing the task name, unless the task
	 *         name is longer than 40 characters.
	 */
    @Override
	public String getNameMaxLength40() {
        return TextFormatter.truncateString(this.name, 40);
    }

    /**
     * Set the task name
     *
     * @param name the name
     * @since 03/00
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the task description.
     *
     * @return String the description
     */
    @Override
	public String getDescription() {
        return this.description;
    }

    /**
     * Get the task URL.
     *
     * @return String the URL.
     */
    @Override
	public String getURL() {
        return URLFactory.makeURL(this.id, ObjectType.TASK);
    }

    /**
     * Set the task description.
     *
     * @param description the description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets this task's priority from a string priority id.
     *
     * @param priority the priority id as a string.
     */
    public void setPriority(String priority) {
    	this.priority = TaskPriority.getForID(priority);
    }

    /**
     * Sets this task's priority.
     *
     * @param priority the priority id
     */
    public void setPriority(int priority) {
        this.priority = TaskPriority.getForID(priority);
    }

    public TaskPriority getPriority() {
        return this.priority;
    }

    /**
     * Returns this tasks's priority display name.
     *
     * @return the priority display name.
     */
    public String getPriorityString() {
        return this.priority.toString();
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Set the planned task start time
     *
     * @param time a Date object indicating the starting date.
     */
    public void setStartTimeD(java.util.Date time) {
        startTime = time;
    }

    /**
     * Set the planned task end time
     *
     * @param time a Date object indicating the planned end date for this task.
     */
    public void setEndTimeD(Date time) {
        endTime = time;
    }

    /**
     * Set the actual start time
     *
     * @param actualStartTime a Date object which indicates the date at which
     * the task actually started, as opposed to the planned start time.
     */
    public void setActualStartTimeD(Date actualStartTime) {
        actualStart = actualStartTime;
    }
    
    

    public void setMaterialAssigneesLoaded(boolean materialAssigneesLoaded) {
		this.materialAssigneesLoaded = materialAssigneesLoaded;
	}

	public Date getActualStartTime() {
        return actualStart;
    }

    /**
     * Get the date on which the user actually started work on this task.
     *
     * @return a <code>String</code> which contains the date on which one or
     * more assignees started working on this task.
     */
    public String getActualStartDateString() {
        DateFormat df = DateFormat.getInstance();
        return df.formatDate(getActualStartTime());
    }
    
    public String getActualStartDateStringMedium() {
        return DateFormat.getInstance().formatDateMedium(getActualStartTime());
    }

    
    public String getBaselineStartDateString() {
        DateFormat df = DateFormat.getInstance();
        return df.formatDate(getBaselineStart());
    }
    
    public String getBaselineStartDateStringMedium() {
        return DateFormat.getInstance().formatDateMedium(getBaselineStart());
    }
    
    public String getBaselineEndDateString() {
        DateFormat df = DateFormat.getInstance();
        return df.formatDate(getBaselineEnd());
    }
    
    public String getBaselineEndDateStringMedium() {
        return DateFormat.getInstance().formatDateMedium(getBaselineEnd());
    }
    
    public String getTaskCalculationTypeString() {
    	return this.getTaskCalculationType().formatDisplay();
    }
    
    public String getBaselineWorkString() {
        return getBaselineWork() != null ? getBaselineWork().toShortString(0, 2) : "";
    }
    
    public String getBaselineWorkUnitString() {
        return getBaselineWork() != null ? XMLUtils.escape(baselineWork.getUnits().getPluralName()) : "";
    }
    
    public String getWorkVarianceString() {
        return getWorkVariance() != null ? getWorkVariance().toShortString(0, 2) : "";
    }
    
    public String getWorkVarianceUnitString() {
        return getWorkVariance() != null ? XMLUtils.escape(getWorkVariance().getUnits().getPluralName()) : "";
    }
    
    public String getBaselineDurationString() {
        return getBaselineDuration() != null ? getBaselineDuration().toShortString(0, 2) : "";
    }
    
    public String getBaselineDurationUnitString() {
        return getBaselineDuration() != null ? XMLUtils.escape(baselineDuration.getUnits().getPluralName()) : "";
    }
 
    public String getDurationVarianceString() {
        return getDurationVariance() != null ? getDurationVariance().toShortString(0, 2) : "";
    }

    public String getDurationVarianceUnitString() {
        return getDurationVariance() != null ? XMLUtils.escape(getDurationVariance().getUnits().getPluralName()) : "";
    }
    
    public String getDependenciesString(){
    	return getPredecessorsNoLazyLoad().getSeqCSVString();
    }
    
    public String getResourceListString() {
    	String resourceList = "";
        for (Iterator it = getAssignmentList().iterator(); it.hasNext();) {
            Assignment assignment = (Assignment) it.next();
            resourceList += HTMLUtils.escape(assignment.getPersonName());
            if(it.hasNext())
                resourceList += ", ";
        }
    	return resourceList;
    }

    /**
     * Set the actual task end time
     *
     * @param time a Date object which indicates when the task actually ended,
     * as opposed to the planned end time.
     */
    public void setActualEndTimeD(java.util.Date time) {
        actualFinish = time;
    }

    public Date getActualEndTime() {
        return actualFinish;
    }

    /**
     * Get the date in which all assignees actually completed working on this
     * task.
     *
     * @return a <code>String</code> object containing the date on which work
     * completed for this task.
     */
    public String getActualEndDateString() {
        DateFormat df = DateFormat.getInstance();
        return df.formatDate(getActualEndTime());
    }
    
    public String getActualEndDateStringMedium() {
        return DateFormat.getInstance().formatDateMedium(getActualEndTime());
    }
    

    /**
     * Get a formatted String representation of the start time
     *
     * @return a <code>String</code> value containing a date formatted to the
     * current user's preferences which indicates the planned start time of the
     * task.
     */
    public String getStartTimeString() {
        return SessionManager.getUser().getDateFormatter().formatDate(startTime);
    }
    
    /**
     * To get formated start time for pattern 
     */
    public String getStartTimeStringFormatted() {
        return SessionManager.getUser().getDateFormatter().formatDateMedium(startTime);
    }


    /**
     * get the planned task start time
     *
     * @return a <code>java.util.Date</code> object which contains the planned
     * start time as a date object
     */
    @Override
	public java.util.Date getStartTime() {
        return this.startTime;
    }

    /**
     * get the planned task end time
     *
     * @return a <code>java.util.Date</code> value containing the planned end
     * time.
     */
    @Override
	public java.util.Date getEndTime() {
        return this.endTime;
    }

    /**
     * Get a formatted String representation of the end time
     *
     * @return a <code>String</code> value containing the planned end time of
     * this task formatted for the user's time preferences.
     */
    public String getEndTimeString() {
        return ((SessionManager.getUser()).getDateFormatter()).formatDate(endTime);
    }
    
    /**
     * To get end time for pattern
     */
    public String getEndTimeStringFormatted() {
        return ((SessionManager.getUser()).getDateFormatter()).formatDateMedium(endTime);
    }
    /**
     * Returns the work amount formatted for the current user's locale.
     * @return a <code>String</code> value containing the amount of work (in the
     * appropriate selected units) that the task will require to be completed.
     * @see #getWorkTQ()
     */
    public String getWork() {
        return NumberFormat.getInstance().formatNumber(work.getAmount().doubleValue());
    }

    /**
     * Given an <code>TimeQuantity</code>, this helper method calculates the
     * equivalent number of hours for such amount, and returns its
     * <code>String</code> representation, formatted to the user's locale.
     * <p>
     * For instance, given a task with work represented in days, to get same
     * work in hours:
     * 
     * <pre>
     * // First, create some dummy ScheduleEntry instance. Likely, you will have
     * // this information in a list retrieved from the database. 
     * ScheduleEntry entry = new ScheduleEntry();
     * TimeQuantity work = new TimeQuantity(&quot;2&quot;, TimeQuantityUnit.DAY);
     * entry.setWork(work);
     * 
     * // Now, get the &quot;work&quot; amount in hours (we had created this entry in &quot;days&quot;).
     * String workInHours = entry.getAmountUnitInHours(this.work);
     * </pre>
     * 
     * @param quantity the <code>TimeQuantity</code> quantity to get its
     *        representation in hours
     * @return a <code>String</code> representation, formatted to the user's
     *         locale, of the number of hours equivalen to a specific
     *         <code>TimeQuantity</code>
     * @since 2.5
     */
    public String getAmountUnitInHours(TimeQuantity quantity){
        BigDecimal unitBase = TimeQuantityUnit.HOUR.getBase();
        TimeQuantityUnit quantityUnit = quantity.getUnits();
        if(quantityUnit.equals(TimeQuantityUnit.DAY)){
            unitBase = TimeQuantityUnit.STANDARD_WORK_DAY.getBase();
        } else{
            if(quantityUnit.equals(TimeQuantityUnit.WEEK)){
                unitBase = TimeQuantityUnit.STANDARD_WORK_WEEK.getBase();
            }
        }
        return NumberFormat.getInstance().formatNumber(quantity.getAmount().multiply(unitBase).doubleValue());
    }
    
    
    /**
     * Get the total amount of work required for this task as a time quantity.
     *
     * @return a <code>TimeQuantity</code> containing the total amount of work
     * required to complete this task from start to finish.
     */
    @Override
	public TimeQuantity getWorkTQ() {
        return work;
    }

    public TimeQuantity getWorkRemaining() {
        return work.subtract(workComplete);
    }

    /**
     * Specifies this task's work.
     * <p>
     * <b>Note:</b> No recalculations are performed (that is, duration, dates or
     * assignments are not changed).  This method should only be called during
     * persistence operations or after work has already been calculated.
     * </p>
     * @param work
     */
    public void setWork(TimeQuantity work) {
        boolean oldWorkIsZero = this.work == null || this.work.isZero();
        boolean newWorkIsZero = work == null || work.isZero();

        this.work = work;

        if (!newWorkIsZero) {
            computeWorkComplete();
        } else if (newWorkIsZero && !oldWorkIsZero) {
            workPercentComplete = new BigDecimal(0);
        } else {
            //Don't touch work complete.  It might have been modified by the user
        }
    }

    /**
     * Get the units used for measuring work as a string
     *
     * @return a <code>String</code> containing the unit type of work in
     * human-readable form.  This should be "Hour", "Day", or "Week".
     */
    public String getWorkUnitsString() {
        return work.getUnits().getPluralName();
    }

    /**
     * Returns the work complete amount formatted for the current user's locale.
     * @return a <code>String</code> value containing the amount of work complete 
     * (in the appropriate selected units) that the task has completed.
     * @see #getWorkCompleteTQ()
     */
    public String getWorkComplete() {
        return NumberFormat.getInstance().formatNumber(workComplete.getAmount().doubleValue());
    }

    /**
     * Get the Work Complete field as a Time Quantity.
     *
     * @return a <code>TimeQuantity</code> object containing the amount of
     * work already completed.
     */
    public TimeQuantity getWorkCompleteTQ() {
        return workComplete;
    }

    /**
     * Set the number of complete work units required for this task.
     *
     * @param workComplete a <code>TimeQuantity</code> object containing the
     * total quantity of work required to complete this task.
     */
    public void setWorkComplete(TimeQuantity workComplete) {
        this.workComplete = workComplete;

        if (!work.isZero()) {
            computeWorkComplete();
        }
    }

    /**
     * Get the units used for measuring work complete as a string
     *
     * @return a <code>String</code> value containing "Hour", "Day", or "Week".
     */
    public String getWorkCompleteUnitsString() {
        return workComplete.getUnits().getPluralName();
    }

    /**
     * Get the duration of time alloted for this task formatted for the
     * user's locale, containing the units spelled-out.
     * <p>
     * Milestone tasks return the name of the milestone instead.
     * </p>
     * @return a <code>String</code> value containing the duration in days with
     * the unit type e.g: "6 Days".
     * @see TimeQuantity#toString()
     */
    public String getDurationFormatted() {
        if (getTaskType().equals(TaskType.MILESTONE)) {
            return TaskType.MILESTONE.getName();
        }
        return duration.toShortString(0,2);
    }

    /**
     * Get the duration of this task as a <code>TimeQuantity</code> object.
     *
     * @return a <code>TimeQuantity</code> object containing the duration of
     * this task.
     */
    public TimeQuantity getDurationTQ() {
        return duration;
    }
    
    /**
     * Returns the duration amount formatted for the current user's locale.
     * @return a <code>String</code> value containing the amount of duration (in the
     * appropriate selected units) that the task will require to be completed.
     * @see #getDurationTQ()
     */
    public String getDuration() {
        return NumberFormat.getInstance().formatNumber(duration.getAmount().doubleValue());
    }
    
    /**
     * Get the units used for measuring duration as a string
     *
     * @return a <code>String</code> containing the unit type of duration in
     * human-readable form.  This should be "Hour", "Day", or "Week".
     */
    public String getDurationUnitsString() {
        return duration.getUnits().getPluralName();
    }

    /**
     * Sets the task duration.
     * <p>
     * <b>Note:</b> This method does not perform any recalculations of work or assignments;
     * it should only be called to specify a value during persistence operations or
     * after recalculating the other values.
     * </p>
     * @param duration the task duration
     */
    public void setDuration(TimeQuantity duration) {
        this.duration = duration;
    }

    /**
     * Get the actual duration for this schedule entry.
     */
    public TimeQuantity getActualDuration() {
        return getDurationTQ().multiply(getWorkPercentCompleteDecimal());
    }

    /**
     * Set the duration percent complete for this task.
     *
     * @param percentComplete a <code>BigDecimal</code> containing the percent
     * complete.  Valid range should be between 0.0 and 1.0.
     */
    public void setPercentComplete(BigDecimal percentComplete) {
        //Return methods expect a scale of 5.  Accommodate them.  We won't truncate
        //scale yet -- though it seems like that will come soon enough.
        if (percentComplete != null && percentComplete.scale() < 5) {
            percentComplete = percentComplete.setScale(5);
        }

        this.percentComplete = percentComplete;
    }

    /**
     * Returns an int containing the percent complete of the entry.
     * @return the duration percent complete where <code>100</code> is 100%.
     */
    public double getPercentComplete() {
        return getPercentCompleteDecimal().multiply(new BigDecimal(100)).doubleValue();
    }

    public BigDecimal getPercentCompleteDecimal() {
        return (percentComplete == null ? getWorkPercentCompleteDecimal() : percentComplete);
    }

    /**
     * Specifies the duration percent complete.
     * @param percentComplete the percent complete where <code>100</code> is 100%.
     */
    public void setPercentCompleteInt(int percentComplete) {
        setPercentComplete(new BigDecimal(percentComplete).divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_UP));

        // If there isn't any work, changing the percentage complete will only
        // change the normal "percent complete" field.  Otherwise, the
        // "work percent complete" field will change too.
        if (work.getAmount().signum() != 0) {
            throw new IllegalStateException("Setting percentage complete for " +
                    "schedule entries that have work is not supported (yet).");
        }
    }

    /**
     * Indicates whether this task is complete.
     * <p>
     * For milestones with zero work, this is based on the percent complete value.
     * For other tasks this is based on the work complete
     * </p>
     * @return true if the task is complete (its percent complete is >= 100);
     * false if the task is not complete (percent complete < 100)
     */
    public boolean isComplete() {
        boolean isComplete;
        if (isMilestone() && getWorkTQ().getAmount().signum() == 0) {
            isComplete = (getPercentCompleteDecimal().compareTo(BigDecimal.ONE) >= 0);
        } else {
            // Previously tasks with work amounts based completion
            // on ratio of actual duration to duration.
            // However, actual duration cannot be computed any more but based
            // on the earlier calculation, (actual duration / duration) = 100
            // only when work complete = work
            // Thus it is safe to say that a task with work is complete when
            // (work complete / work) = 100
            isComplete = (getWorkPercentCompleteDecimal().compareTo(BigDecimal.ONE) >= 0);
        }
        return isComplete;
    }

    /**
     * Set the work percent complete for this schedule entry.  This value will
     * be ignored unless the work for this schedule entry is zero.
     *
     * @param workPercentComplete a <code>BigDecimal</code> containing the
     * percentage of work complete.  100% = 1.0
     */
    public void setWorkPercentComplete(BigDecimal workPercentComplete) {
        if (workPercentComplete.scale() < 5) {
            workPercentComplete = workPercentComplete.setScale(5);
        }

        this.workPercentComplete = workPercentComplete;
    }

    /**
     * Returns the work percent complete value.
     * @return the work percent complete where 1.0 = 100%
     */
    public BigDecimal getWorkPercentCompleteDecimal() {
        return this.workPercentComplete;
    }

    /**
     * Get the formatted percent of work complete for the task.
     *
     * @return a <code>String</code> value containing the percent complete
     * including percent sign (e.g.: "95%") formatted for the user's locale
     */
    public String getWorkPercentComplete() {
        return NumberFormat.getInstance().formatPercent(workPercentComplete.doubleValue(), 0, 2);
    }

    /**
     * Get the integer version of the percent complete ratio.
     *
     * @return a <code>int</code> value indicating the amount of work on this
     * task is complete between zero and 100.
     */
    public double getWorkPercentCompleteDouble() {
        // Return the ratio in percent
        return workPercentComplete.movePointRight(2).doubleValue();
    }

    /**
     * Computes what the % work complete should be given the work and work
     * complete.
     */
    private void computeWorkComplete() {
        if (!work.isZero()) {
            TimeQuantity workComplete = (this.workComplete == null ? TimeQuantity.O_HOURS : this.workComplete);

            BigDecimal numerator = ScheduleTimeQuantity.convertToHour(workComplete).getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
            BigDecimal denominator = ScheduleTimeQuantity.convertToHour(this.work).getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
            // There is some work
            // Ratio is (work complete) / (work)
            // We convert both values to hours based on the schedule settings
            // for converting units to hours
            workPercentComplete = numerator.divide(denominator, 5, BigDecimal.ROUND_HALF_UP);

            // Ensure that we never have a ratio greater than 1;
            // This means that entering more work complete than available work will
            // never show more than 100% complete
            workPercentComplete = workPercentComplete.min(BigDecimal.ONE);
        }
    }

    public TaskConstraintType getConstraintType() {
        return constraintType;
    }

    /**
     * Get the id of the constraint type for this task.  These ID's are used to
     * store constraints in the database.
     *
     * @return a <code>String</code> value containing the constraint type id.
     */
    public String getConstraintTypeID() throws PersistenceException {
        return constraintType.getID();
    }

    /**
     * Set the type of constraint for this task.
     *
     * @param type a <code>TaskConstraintType</code> object which specifies the
     * type of task constraint that is to be applied to this task.
     */
    public void setConstraintType(TaskConstraintType type) {
        this.constraintType = type;
    }

    /**
     * Get the date associated with the constraint type.  For example, if you
     * were using the "FINISH_NO_LATER_THAN" constraint type, this date would
     * be the date you couldn't finish later than.
     *
     * @return a <code>Date</code> value that indicates the constraint date.
     * @see #getConstraintDateString()
     * @see #setConstraintDate(java.util.Date)
     */
    public Date getConstraintDate() {
        return this.constraintDate;
    }

    /**
     * Set the date associated with the constraint type.  For example, if you
     * were using the "FINISH_NO_LATER_THAN" constraint type, this date would
     * be the date you couldn't finish later than.
     *
     * @param constraintDate a <code>Date</code> value that indicates the
     * constaint date.
     * @see #getConstraintDate
     */
    public void setConstraintDate(Date constraintDate) {
        this.constraintDate = constraintDate;
    }

    /**
     * Get the constraint date, formatted according to the current user's date
     * formatting rules.
     *
     * @return a <code>String</code> value containing the constraint date formatted
     * the the user's current locale.
     * @see #getConstraintDate()
     */
    public String getConstraintDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(constraintDate);
    }

    /**
     * Get the deadline in which the task needs to be completed.
     *
     * @return a <code>Date</code> value which indicates the latest possible date
     * that the task must be completed.
     * @see #getDeadlineString()
     * @see #setDeadline(java.util.Date)
     */
    public Date getDeadline() {
        return this.deadline;
    }

    /**
     * Set the deadline in which the task needs to be completed.
     *
     * @param deadline a <code>Date</code> value indicating the date that the
     * task must be completed.
     * @see #getDeadline()
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Get the deadline for this task formatted in the date format for the
     * current user.
     *
     * @return a <code>String</code> value containing the deadline date formatted
     * for the current user.
     * @see #getDeadline
     */
    public String getDeadlineString() {
        return SessionManager.getUser().getDateFormatter().formatDate(deadline);
    }

    /**
     * Returns the current schedule entry's phase ID.
     *
     * @return the phase ID (if this task belongs to a phase)
     */
    protected String getPhaseID() {
        return this.phaseID;
    }

    /**
     * Sets selected phase that this schedule entry will belong to.
     * <p>
     * If the specified phaseID is different from the current phaseID
     * then phases will be stored during schedule entry store.
     * </p>
     * @param phaseID the phase id of phase schedule entry will belong to
     */
    public void selectPhase(String phaseID) {
        if ("0".equals(phaseID)) {
            phaseID = null;
        }

        this.phaseID = phaseID;
        this.isPhaseSelected = true;
    }

    /**
     * Returns a collection of phase IDs to which this schedule entry belongs.
     * @return a collection where each element is a <code>String</code> phase
     * ID; if this task belongs to no phase then an empty collection is returned
     */
    public ArrayList getSelectedPhases() {
        ArrayList phaseList = new ArrayList();

        if (getPhaseID() != null) {
            phaseList.add(new HTMLOption(getPhaseID(), getPhaseName()));
        }
        return phaseList;
    }


    /**
	 * This method is responsible to set the <code>phaseName</code> instance
	 * variable.
	 * 
	 * @param thePhaseName
	 *            the phase name to set.
	 * @since 8.2.0
	 */
	public void setPhaseName(String thePhaseName) {
		this.phaseName = thePhaseName;
	}
    
    /**
	 * Return selected phase name lazy loading if phaseID or phase name has
	 * never been viewed before.
	 * 
	 * @return the phase name for this tasks's phase or null if there is no
	 *         phase
	 */
    public String getPhaseName() {
        // Ensure phase is loaded
        return this.phaseName;
    }

    /**
     * Set this task's parent task id
     * @param parentTaskID id of parent task
     */
    public void setParentTaskID(String parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    /**
     * Return this task's parent task id
     * @return task id of parent task
     */
    public String getParentTaskID() {
        return this.parentTaskID;
    }

    public boolean hasParent() {
        return !Validator.isBlankOrNull(parentTaskID);
    }

    /**
     * Get the name of the parent of the current task.
     *
     * @return The name of the parent of the current task.
     */
    public String getParentTaskName() {
        return this.parentTaskName;
    }

    /**
     * Get the name of the parent of the current task.
     *
     * @return The name of the parent of the current task.
     */
    public String getParentTaskNameMaxLength40() {
        return TextFormatter.truncateString(this.parentTaskName, 40);
    }

    public void setParentTaskName(String parentTaskName) {
        this.parentTaskName = parentTaskName;
    }

    /**
     * Set this tasks's hierarchy level
     *
     * @param hierarchyLevel a <code>String</code> value containing the hierarchy
     * level of this task.
     */
    void setHierarchyLevel(String hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    /**
     * Return this tasks's hierarchy level
     *
     * @return a <code>String</code> value containing the hierarchy level of this
     * task.
     */
    public String getHierarchyLevel() {
        return this.hierarchyLevel;
    }

    /**
     * Set whether this task is an orphan in a hierarchical structure. This
     * means the tasks's immediate parent is not being a displayed.
     *
     * @param isOrphan a <code>boolean</code> value indicating whether this task's
     * hierarchical parent is being displayed.
     */
    void setOrphan(boolean isOrphan) {
        this.isOrphan = isOrphan;
    }

    /**
     * Indicates whether this task is an orphan in a hierarchical structure.
     * Will always be true in a flat structure
     *
     * @return a <code>boolean</code> value indicating whether this task's
     * hierarchical parent is being displayed.
     */
    public boolean isOrphan() {
        return this.isOrphan;
    }

    /**
     * Specifies this loaded task's record status.
     * @param recordStatus the current record status
     */
    void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Returns this task's current record status.
     * @return the record status
     */
    @Override
	public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    public void setImportTaskID(String importTaskID) {
        this.importTaskID = importTaskID;
    }

    public String getImportTaskID() {

        return this.importTaskID;
    }

    public void setImportParentTaskID(String importParentTaskID) {
        this.importParentTaskID = importParentTaskID;
    }

    public String getImportParentTaskID() {

        return this.importParentTaskID;
    }

    /**
     * Sets a comment for this task to be added to the task's comments.
     * Strings over 4000 characters will be truncated to 4000 characters.
     *
     * @param comment the comment to add.
     */
    public void setComment(String comment) {

        // Ensure comment has text; this avoids adding a comments row in
        // the database with empty comments text.
        if (comment != null && comment.trim().length() > 0) {

            // Truncate at 4000 characters (that is the maximum storage currently
            // in our database)
            if (comment.length() > 4000) {
                this.comment = comment.substring(0, 4000);
            } else {
                this.comment = comment;
            }
        } else {
            this.comment = null;
        }
    }

    /**
     * Return the current comment that will be added to the task when stored.
     *
     * @return the comment
     */
    protected String getComment() {

        return this.comment;
    }

    /**
     * Set the current user id who has modified the task when stored.
     *
     * @return the comment
     */
    public void setModifiedByID(int ID) {
        this.modifiedById = ID;
    }

    /**
     * Return the current user id who has modified the task when stored.
     *
     * @return the comment
     */
    public int getModifiedByID() {
        return this.modifiedById;
    }
    
    /**
     * Get a list of tasks dependencies that must be completed before this task
     * can be started.  This method will load the dependencies if they aren't
     * already loaded.
     *
     * @return a <code>PredecessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects.
     * @throws net.project.persistence.PersistenceException if there is trouble loading the dependencies from the database.
     */
    public PredecessorList getPredecessors() throws PersistenceException {
        if ((!predecessorList.isLoaded()) && (predecessorList.size() == 0)) {
            predecessorList.setTaskID(getID());
            predecessorList.load();
        }

        return predecessorList;
    }

    /**
     * Get a list of tasks dependencies that must be completed before this task
     * can be started.  This method will not load the dependencies if they aren't
     * already loaded.
     *
     * @return a <code>PredecessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects.
     */
    public PredecessorList getPredecessorsNoLazyLoad() {
        return predecessorList;
    }

    /**
     * Get the task dependencies that are currently associated with this task.
     * This method will load the dependencies if they aren't already loaded.
     *
     * @deprecated as of Version 7.6.  Use {@link #getPredecessors} instead.
     * This name of this method is not clear enough.
     * @return a <code>PredecessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects.
     * @throws net.project.persistence.PersistenceException if there is trouble loading the dependencies from the database.
     */
    @Deprecated
	public PredecessorList getDependencies() throws PersistenceException {
        return getPredecessors();
    }

    /**
     * Get the list of dependencies, but do not autoload them if they aren't
     * already loaded.
     *
     * @return a <code>PredecessorList</code> for this Task.
     */
    public PredecessorList getDependenciesNoLazyLoad() {
        return predecessorList;
    }

    /**
     * Get a list of the tasks that will begin after this task has been
     * completed.
     *
     * @return a <code>SuccessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects,
     * @throws net.project.persistence.PersistenceException if there is a problem loading the dependent tasks from the database.
     */
    public SuccessorList getSuccessors() throws PersistenceException {
        if ((!successorList.isLoaded()) && (successorList.size() == 0)) {
            successorList.setTaskID(getID());
            successorList.load();
        }

        return successorList;
    }

    /**
     * Get a list of the tasks that will begin after this task has been
     * completed.
     *
     * @return a <code>SuccessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects,
     */
    public SuccessorList getSuccessorsNoLazyLoad() {
        return successorList;
    }

    /**
     * Get a list of the tasks that are dependent on this task.
     *
     * @deprecated as of Version 7.6.  The name of this method is not very self
     * explanatory.  It is being replaced by {@link #getSuccessors}.
     * @return a <code>SuccessorList</code> object containing zero or more
     * <code>TaskDependency</code> objects,
     * @throws net.project.persistence.PersistenceException if there is a problem loading the dependent tasks from the database.
     */
    @Deprecated
	public SuccessorList getDependentTasks() throws PersistenceException {
        return getSuccessors();
    }

    /**
     * Set the list of tasks that must be complete before this task can begin.
     * This objects in this list will be of type
     * {@link TaskDependency} so we can store additional
     * information about the relationship, such as lag or
     * {@link TaskDependencyType}.
     *
     * @param taskDependencyList The new list of dependencies for this task.
     */
    public void setPredecessors(PredecessorList taskDependencyList) {
        this.predecessorList = taskDependencyList;
    }

    /**
     * Set the list of tasks that are slated to begin after this task has
     * completed. This objects in this list will be of type
     * {@link TaskDependency} so we can store additional
     * information about the relationship, such as lag or
     * {@link TaskDependencyType}.
     *
     * @param successorList a <code>List</code> of tasks that will begin after
     * this task is completed.
     */
    public void setSuccessors(SuccessorList successorList) {
        this.successorList = successorList;
    }

    /**
     * Returns all the comments associated with this task. Comments are loaded
     * from persitent store if necessary.
     *
     * @return a <code>TaskComments</code> object containing all of the task
     * comments associated with this task.
     * @throws net.project.persistence.PersistenceException if there is a problem loading the comments
     */
    public TaskComments getComments() throws PersistenceException {

        if (this.comments == null) {
            loadComments();
        }

        return this.comments;
    }
    
    

    
    public boolean isMaterialAssigneesLoaded() {
		return materialAssigneesLoaded;
	}

	@Override
	public void addToJSONStore(final NodeFactory factory, final PersonalPropertyMap propertyMap, final IWorkingTimeCalendarProvider provider, final boolean forFlatView, final boolean childrenVisible) {
        final Node newNode = factory.nextNode();
        final Map map = newNode.getMap();
        //sachin: note formatting is done in default date format as the dates are handled in javacript
        //and we need a standard format, since some of the user specific formats are not available in the 
        //javascript date formatting apis we are using
        final DateFormat df = DateFormat.getInstance();
        final NumberFormat nf = NumberFormat.getInstance();
        
        //Add all the property values
        newNode.setTitle(XMLUtils.escape(this.getNameMaxLength40()));
        newNode.set("id", this.id);
        newNode.set("sequence", this.sequenceNumber);
        newNode.set("name", XMLUtils.escape(this.getNameMaxLength40()));
        newNode.set("isMilestone", (isMilestone() ? "1" : "0"));
        
        if(map.containsKey("phase")) {
            newNode.set("phase", XMLUtils.escape(this.getPhaseName()));
        }
        if(map.containsKey("priority")) {
            newNode.set("priority", XMLUtils.escape(this.getPriorityString()));
        }
        if(map.containsKey("calculationType")) {
            newNode.set("calculationType", this.getTaskCalculationType().formatDisplay());
        }
        if (map.containsKey("startDate")) {
            newNode.set("startDate", df.formatDate(this.startTime, DateFormat.DEFAULT_DATE_FORMAT));
        }
        if (map.containsKey("actualStartDate")) {
            newNode.set("actualStartDate", df.formatDate(this.actualStart, DateFormat.DEFAULT_DATE_FORMAT));
        }        
        if (map.containsKey("baselineStartDate")) {
            newNode.set("baselineStartDate", df.formatDate(this.baselineStart, DateFormat.DEFAULT_DATE_FORMAT));
        }   
        if (map.containsKey("startVariance")) {
            TimeQuantity startVarianceTQ = this.getStartDateVariance(provider);
            newNode.set("startVariance", NumberFormat.formatSimpleNumber(startVarianceTQ.getAmount().doubleValue(), 0, 2));
            newNode.set("startVarianceUnits", XMLUtils.escape(startVarianceTQ.getUnits().getPluralName()));
        }         
        if (map.containsKey("endDate")) {
            newNode.set("endDate", df.formatDate(this.endTime, DateFormat.DEFAULT_DATE_FORMAT));
        }
        if (map.containsKey("actualEndDate")) {
            newNode.set("actualEndDate", df.formatDate(this.actualFinish, DateFormat.DEFAULT_DATE_FORMAT));
        }
        if (map.containsKey("baselineEndDate")) {
            newNode.set("baselineEndDate", df.formatDate(this.baselineEnd, DateFormat.DEFAULT_DATE_FORMAT));
        }
        if (map.containsKey("endVariance")) {
            TimeQuantity endVarianceTQ = this.getStartDateVariance(provider);
            newNode.set("endVariance", NumberFormat.formatSimpleNumber(endVarianceTQ.getAmount().doubleValue(), 0,2));
            newNode.set("endVarianceUnits", XMLUtils.escape(endVarianceTQ.getUnits().getPluralName()));
        }         
        if(map.containsKey("work")) {
            newNode.set("work", NumberFormat.formatSimpleNumber(this.getWorkTQ().getAmount().doubleValue(), 0, 2));
            newNode.set("workUnits", XMLUtils.escape(this.getWorkUnitsString()));
        }
        if(map.containsKey("baselineWork")) {
            newNode.set("baselineWork", baselineWork != null ? NumberFormat.formatSimpleNumber(baselineWork.getAmount().doubleValue(), 0, 2) : "");
            newNode.set("baselineWorkUnits", baselineWork != null ? XMLUtils.escape(baselineWork.getUnits().getPluralName()) : "");
        }
        if(map.containsKey("workVariance")) {
            TimeQuantity workVarianceTQ = getWorkVariance();
            newNode.set("workVariance", baselineWork != null ? NumberFormat.formatSimpleNumber(workVarianceTQ.getAmount().doubleValue() ,0 ,2) : "");
            newNode.set("workVarianceUnits", baselineWork != null ? XMLUtils.escape(workVarianceTQ.getUnits().getPluralName()) : "");
        }
        if(map.containsKey("workComplete")) {
            newNode.set("workComplete", NumberFormat.formatSimpleNumber(this.getWorkCompleteTQ().getAmount().doubleValue(), 0, 2));
            newNode.set("workCompleteUnits", XMLUtils.escape(this.getWorkCompleteUnitsString()));
        }
        if(map.containsKey("duration")) {
            newNode.set("duration", NumberFormat.formatSimpleNumber(this.getDurationTQ().getAmount().doubleValue(), 0, 2));
            newNode.set("durationUnits", XMLUtils.escape(getDurationUnitsString()));  
        }
        if(map.containsKey("baselineDuration")) {
            newNode.set("baselineDuration", baselineDuration != null ? NumberFormat.formatSimpleNumber(baselineDuration.getAmount().doubleValue(), 0, 2) : "");
            newNode.set("baselineDurationUnits", baselineDuration != null ? XMLUtils.escape(baselineDuration.getUnits().getPluralName()) : "");
        }
        if(map.containsKey("durationVariance")) {
            TimeQuantity duratyionVarianceTQ = getDurationVariance();
            newNode.set("durationVariance", baselineWork != null ? NumberFormat.formatSimpleNumber(duratyionVarianceTQ.getAmount().doubleValue(), 0, 2) : "");
            newNode.set("durationVarianceUnits", baselineWork != null ? XMLUtils.escape(duratyionVarianceTQ.getUnits().getPluralName()) : "");
        }
        if(map.containsKey("workPercentComplete")) {
            newNode.set("workPercentComplete", NumberFormat.formatSimpleNumber(this.getWorkPercentCompleteDecimal().multiply(new BigDecimal(100)).doubleValue(), 0, 2));
        }
        if(map.containsKey("statusNotifiers")) {
            newNode.set("statusNotifiers", "1");
            String tooltip;
            
            newNode.set("hasAssignments", getAssignmentList().size() > 0 ? "1" : "0");
            tooltip = getAssignmentsTooltip();
            newNode.set("ATP", tooltip);
            
            newNode.set("hasDependencies", !getPredecessorsNoLazyLoad().isEmpty() ? "1" : "0");  
            tooltip = getPredecessorsNoLazyLoad().getXMLBody();
            newNode.set("DTP", tooltip);

            newNode.set("isDateConstrained", constraintType.isDateConstrained() ? "1" : "0"); 
            tooltip = PropertyProvider.get("prm.schedule.workplan.calendargraphic.taskcontsraint.tooltip") + " " 
            + getConstraintType().getName()
            + (getConstraintType().isDateConstrained() ? " (" + getConstraintDateString() + ")" : "");
            newNode.set("DCTP", tooltip);
            
            newNode.set("CriticalPath", isCriticalPath() ? "1" : "0");
            
            newNode.set("AfterDeadline", isPastDeadline() ? "1" : "0");
            tooltip = PropertyProvider.get("prm.schedule.list.afterdeadline.message", getDeadlineString()); 
            newNode.set("ADTP", tooltip);
            
            newNode.set("ExternalTask", isFromShare ? "1" : "0");
            tooltip = XMLUtils.escape(getSharingSpaceName());
            newNode.set("ETTP", tooltip);
        }
        if(map.containsKey("wbs")){
            newNode.set("wbs", XMLUtils.escape(this.getWBS()));
        }
        //always set the task list decoration info
        setRowClass(propertyMap, newNode);
        //end seting the task list decoration info
        if(map.containsKey("resources")) {
            String resourceList = "";
            for (Iterator it = getAssignmentList().iterator(); it.hasNext();) {
                Assignment assignment = (Assignment) it.next();
                resourceList +=assignment.getPersonName();
                if(it.hasNext())
                    resourceList += ", ";
            }
            newNode.set("resources", resourceList);
        }
        if(map.containsKey("dependencies")) {
            String dependencyList = getPredecessorsNoLazyLoad().getSeqCSVString();
            newNode.set("dependencies", dependencyList);  
        }
        newNode.set("taskType", getType());
        newNode.set("treeVisibility", childrenVisible);
       
        // Determine whether this newNode will be added to received root
        if(!forFlatView && org.apache.commons.lang.StringUtils.isNumeric(this.parentTaskID)) {
            int parentTaskId = Integer.parseInt(this.parentTaskID);
            List<Node> nodes = factory.getNodes();
            for (Node targetParent : nodes) {
                if(parentTaskId == Integer.parseInt((String)targetParent.getMap().get("id"))){
                    targetParent.add(newNode);
                    break;
                }
            }
        }
    }
    
    private void setRowClass(PersonalPropertyMap map, Node node) {
        if(isComplete() && map.propertyExists("completedTasksColor")) {
            node.set("rowClass", map.getProperty("completedTasksColor"));
        } else if(getEndTime() != null && getEndTime().before(new Date()) && map.propertyExists("lateTasksColor")) {
            node.set("rowClass", map.getProperty("lateTasksColor"));
        } else if(getEndTime() != null && (new Date().after(DateUtils.addDay(getEndTime(), -7))) && (new Date().before(getEndTime())) && map.propertyExists("tasksComingDueColor")) {
            node.set("rowClass", map.getProperty("tasksComingDueColor"));
        } else if(isPastDeadline() && map.propertyExists("afterDeadlineColor")) {
            node.set("rowClass", map.getProperty("afterDeadlineColor"));
        } else if(getAssignmentList().size() == 0 || assignments.getTotalPercentage().signum() == 0 && map.propertyExists("unassignedTasksColor")) {
            node.set("rowClass", map.getProperty("unassignedTasksColor"));
        } else if(isCriticalPath() && map.propertyExists("isCriticalPathColor")) {
            node.set("rowClass", map.getProperty("isCriticalPathColor"));
        } else if(constraintType.isDateConstrained() && map.propertyExists("isDateConstrainedColor")) {
            node.set("rowClass", map.getProperty("isDateConstrainedColor"));
        } else if(isFromShare && map.propertyExists("isExternalTaskColor")) {
            node.set("rowClass", map.getProperty("isExternalTaskColor"));
        } else {
            node.set("rowClass", "");
        }
    }
    
    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    @Override
	public String getXMLBody() {
        final DateFormat df = DateFormat.getInstance();
        StringBuilder xml = new StringBuilder();
        xml.append("<" + getType() + ">\n"); // use the real task type
        xml.append("<id>" + id + "</id>\n");
        xml.append("<sequence>" + sequenceNumber + "</sequence>");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");

        // call getTaskType because it will mask "summary" --> "task"
        xml.append("<type>" + XMLUtils.escape(getTaskType().getName()) + "</type>\n");
        xml.append("<isMilestone>" + (isMilestone() ? "1" : "0") + "</isMilestone>");
        xml.append("<isSummaryTask>" +  (getTaskType().equals(TaskType.SUMMARY) ? "1" : "0") + "</isSummaryTask>");
        xml.append("<startDate>" + df.formatDate(startTime) + "</startDate>\n");
        xml.append("<endDate>" + SessionManager.getUser().getDateFormatter().formatDate(endTime) + "</endDate>\n");
        xml.append("<startTime>" + SessionManager.getUser().getDateFormatter().formatTime(startTime).toLowerCase() + "</startTime>\n");
        xml.append("<endTime>" + SessionManager.getUser().getDateFormatter().formatTime(endTime).toLowerCase() + "</endTime>\n");
        xml.append("<duration>" + getDurationFormatted() + "</duration>\n");
        xml.append("<durationInHours>").append(XMLUtils.escape(this.isMilestone ? "-1.0" : this.getAmountUnitInHours(this.duration))).append("</durationInHours>\n");
        if(this.recordStatus == null || !this.recordStatus.equals(RecordStatus.DELETED)){
            xml.append("<url>" + URLFactory.makeURL(id, ObjectType.TASK) + "</url>");
        }
        
        // Confirms to ISO 8061 standards for Date-time

        if (this.startTime == null) {
            xml.append("<startDateTime/>\n");
        } else {
            xml.append("<startDateTime>" + XMLUtils.formatISODateTime(startTime) + "</startDateTime>\n");
        }

        if (this.endTime == null) {
            xml.append("<endDateTime/>\n");
        } else {
            xml.append("<endDateTime>" + XMLUtils.formatISODateTime(endTime) + "</endDateTime>\n");
        }

        //xml.append("<durationUnitsId>" + durationUnitsId + "</durationUnitsId>\n");
        xml.append("<work>" + XMLUtils.escape(getWork()) + "</work>\n");
        xml.append("<workUnits>" + XMLUtils.escape(getWorkUnitsString()) + "</workUnits>\n");
        xml.append("<workInHours>").append(XMLUtils.escape(this.getAmountUnitInHours(this.work))).append("</workInHours>\n");
        xml.append("<workComplete>" + XMLUtils.escape(getWorkComplete()) + "</workComplete>\n");
        xml.append("<workCompleteUnits>" + XMLUtils.escape(getWorkCompleteUnitsString()) + "</workCompleteUnits>\n");

        xml.append("<workString>" + getWorkTQ().toShortString(0,2) + "</workString>\n");
        xml.append("<workCompleteString>" + getWorkCompleteTQ().toShortString(0,2) + "</workCompleteString>\n");
        xml.append("<workRemainingString>").append(this.getWorkRemaining().toShortString(0, 2)).append("</workRemainingString>");

        xml.append("<workVariance>" + getWorkVariance().toShortString(0,2) + "</workVariance>\n");
        xml.append("<workVarianceAmount>" + getWorkVariance().getAmount() + "</workVarianceAmount>\n");
        xml.append("<durationVariance>" + getDurationVariance().toShortString(0,2) + "</durationVariance>\n");
        xml.append("<durationVarianceAmount>" + getDurationVariance().getAmount() + "</durationVarianceAmount>\n");

        if (this.actualStart == null) {
            xml.append("<actualStart/>\n");
            xml.append("<actualStartDate/>\n");
        } else {
            xml.append("<actualStart>" + XMLUtils.formatISODateTime(this.actualStart) + "</actualStart>\n");
            xml.append("<actualStartDate>" + df.formatDate(this.actualStart) + "</actualStartDate>\n");
        }

        if (this.actualFinish == null) {
            xml.append("<actualFinish/>\n");
            xml.append("<actualFinishDate/>\n");
        } else {
            xml.append("<actualFinish>" + XMLUtils.formatISODateTime(this.actualFinish) + "</actualFinish>\n");
            xml.append("<actualFinishDate>" + df.formatDate(this.actualFinish) + "</actualFinishDate>\n");
        }

        xml.append("<priority>" + priority + "</priority>\n");
        xml.append("<priorityString>" + XMLUtils.escape(getPriorityString()) + "</priorityString>\n");
        xml.append("<percentComplete>" + getPercentComplete() + "</percentComplete>\n");
        xml.append("<workPercentComplete>" + getWorkPercentCompleteDecimal().multiply(new BigDecimal(100)) + "</workPercentComplete>\n");

        if (this.dateCreated == null) {
            xml.append("<dateCreated/>\n");
        } else {
            xml.append("<dateCreated>" + XMLUtils.formatISODateTime(dateCreated) + "</dateCreated>\n");
        }

        if (this.dateModified == null) {
            xml.append("<dateModified/>\n");
        } else {
            xml.append("<dateModified>" + XMLUtils.formatISODateTime(dateModified) + "</dateModified>\n");
        }
        if (this.calculationType != null) {
            xml.append("<calculationType>" + XMLUtils.escape(this.calculationType.formatDisplay()) + "</calculationType>");
        }
        
        xml.append("<modifiedById>" + modifiedById + "</modifiedById>\n");
        xml.append("<modifiedBy>" + XMLUtils.escape(modifiedBy) + "</modifiedBy>");
        xml.append("<phase_id>" + XMLUtils.escape(getPhaseID()) + "</phase_id>");
        xml.append("<phase_name>" + XMLUtils.escape(phaseName) + "</phase_name>");
        xml.append("<parent_task_id>" + XMLUtils.escape(parentTaskID) + "</parent_task_id>");
        xml.append("<parent_task_name>" + XMLUtils.escape(parentTaskName) + "</parent_task_name>");
        xml.append("<hierarchy_level>" + XMLUtils.escape(getHierarchyLevel()) + "</hierarchy_level>");
        xml.append("<is_orphan>" + (isOrphan() ? "1" : "0") + "</is_orphan>");
        if(isFullXMLBody) {
            try {
                xml.append("<assigneeString>").append(XMLUtils.escape(getAssigneeString())).append("</assigneeString>");
            } catch (PersistenceException pe) {
            	Logger.getLogger(ScheduleEntry.class).error("Un unexpected exception occurred while attempting to get the Assignee String: " + pe);
                throw new XMLConstructionException("Un unexpected exception occurred while attempting to get the Assignee String: " + pe, pe);
            }
            try {
                xml.append(getPredecessors().getXMLBody());
            } catch (PersistenceException pe) {
                Logger.getLogger(ScheduleEntry.class).error("Un unexpected exception occurred while attempting to get the Predecessors String: " + pe);
                throw new XMLConstructionException("Un unexpected exception occurred while attempting to get the Predecessors String: " + pe, pe);
            }
        }
        xml.append("<TaskConstraint>");

        xml.append("  <tooltip>").append(PropertyProvider.get("prm.schedule.workplan.calendargraphic.taskcontsraint.tooltip") + "&#13;").append(constraintType.getName()).append((constraintType.isDateConstrained() ? " (" + getConstraintDateString() + ")" : "")).append("</tooltip>");
        xml.append("  <type>").append(constraintType.getName()).append("</type>");
        if (constraintDate == null) {
            xml.append("  <constraintDate/>\n");
        } else {
            xml.append("  <constraintDate>").append(XMLUtils.formatISODateTime(constraintDate)).append("</constraintDate>");
        }

        xml.append("  <deadline>").append(XMLUtils.formatISODateTime(deadline)).append("</deadline>");
        xml.append("</TaskConstraint>");

        xml.append("<Space>\n");
        xml.append("<name>" + XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getName()) + "</name>\n");
        xml.append("<SpaceType>" + XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getSpaceType().getName()) + "</SpaceType>\n");
        xml.append("</Space>\n");

        //Add information about the status of the task.
        xml.append("<StatusNotifiers>");
        xml.append("  <OverAllocated/>");
        if (assignments.getAssignments().size() == 0 || assignments.getTotalPercentage().signum() == 0) {
            xml.append("  <UnassignedWork/>");
        }
        if (isCriticalPath()) {
            xml.append("  <CriticalPath/>");
        }
        if (isPastDeadline()) {
            xml.append("  <AfterDeadline/>");
        }
        if (constraintType.isDateConstrained()) {
            xml.append("  <isDateConstrained/>");
        }
        if (getEndTime() != null) {
            if ((new Date().after(DateUtils.addDay(getEndTime(), -7))) && (new Date().before(getEndTime())) && (!isComplete())) {

                xml.append("  <ComingDue/>");
            }
            if (getEndTime().before(new Date()) && !isComplete()) {
                xml.append("  <Late/>");
            }
        }
        if (isComplete()) {
            xml.append("  <Completed/>");
        }
        if(isFullXMLBody) {
            try {
                if (!getPredecessors().isEmpty()) {
                    xml.append("  <HasDependencies/>");
                }
            } catch (PersistenceException e) {
                //this should not be reached as same is caught and thrown above
                throw new PnetRuntimeException("Unable to load dependencies.");
            }
            if (getAssignmentList().size() > 0) {
                xml.append("  <HasAssignments>");
                String tooltip = getAssignmentsTooltip();
                xml.append("    <AssignmentTooltip>").append(tooltip).append("</AssignmentTooltip>");
                xml.append("  </HasAssignments>");
            }
        }
        if (isFromShare) {
            xml.append("<ExternalTask fromSpaceID=\""+sharingSpaceID+"\" "+
                    "fromSpaceName=\""+XMLUtils.escape(sharingSpaceName)+"\"/>");
        }

        xml.append("</StatusNotifiers>");

        xml.append("</" + getType() + ">\n");
        return xml.toString();
    }

    public String getAssignmentsTooltip() {
        String tooltip = "";
        NumberFormat nf = NumberFormat.getInstance();
        for (Iterator it = getAssignmentList().iterator(); it.hasNext();) {
        	Assignment assignment = (Assignment) it.next();
        	
            //ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
        	
            //We need to check the person name here because when assignments
            //are being created, a person name isn't being populated.
            if (assignment.getPersonName() != null) {
                tooltip += assignment.getPersonName().replaceAll("@", "&40;").replaceAll("\\|", "&#166;");
            }
            tooltip += "@" + nf.formatPercent(assignment.getPercentAssignedDecimal().doubleValue(), 0 , 3);
            if (assignment instanceof ScheduleEntryAssignment){	
            	if(((ScheduleEntryAssignment)assignment).getWork().getUnits().equals(TimeQuantityUnit.MINUTE)){
	            	tooltip += "@" + ScheduleTimeQuantity.convertToHour(((ScheduleEntryAssignment)assignment).getWork()).toShortString(0, 2);
	            	tooltip += "@" + ScheduleTimeQuantity.convertToHour(((ScheduleEntryAssignment)assignment).getWorkComplete()).toShortString(0,2);
            	} else {
            		tooltip += "@" + ((ScheduleEntryAssignment)assignment).getWork().toShortString(0, 2);
	            	tooltip += "@" + ((ScheduleEntryAssignment)assignment).getWorkComplete().toShortString(0,2);
            	}
            } else {
            	tooltip += "@" + ((FormAssignment)assignment).getWork().toShortString(0, 2);
            	tooltip += "@" + ((FormAssignment)assignment).getWorkComplete().toShortString(0,2);                    	
            }
   
            if (it.hasNext()) {
                tooltip += "|";
            }
        }
        return tooltip;
    }
    
    public String getMaterialAssignmentsTooltip() {
        String tooltip = "";
        NumberFormat nf = NumberFormat.getInstance();
        for (Iterator<MaterialAssignment> it = getMaterialAssignmentsList().iterator(); it.hasNext();) {
        	MaterialAssignment materialAssignment = it.next();
            if (materialAssignment.getMaterialName() != null && materialAssignment.getRecordStatus().equals("A")) {
                tooltip += materialAssignment.getMaterialName().replaceAll("\\|", "&#166;");
            }
  
            if (it.hasNext()) {
                tooltip += "|";
            }
        }
        return tooltip;
    }    

    /**
     * Converts the object to XML representation. This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    @Override
	public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Check if the task for the last set ID is loaded
     *
     * @return a <code>boolean</code> value indicating whether this task has been
     * loaded from the database.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    public List getDependenciesWithCycles(Schedule schedule) throws VisitException, PersistenceException {
        CyclicDependencyDetectorV2 crd = new CyclicDependencyDetectorV2();
        return crd.getCyclicDependencies(this, schedule);
    }

    /**
     * Returns a working time calendar for this SchedulEntry.
     * <p>
     * The working time calendar is based on the assignments in this schedule
     * entry.  It is essentially an aggregate of calendars for the assigned
     * resources.  All resource working time calendars as well as the schedule
     * working time calendar are considered when calculating dates and durations.
     * </p>
     * @param workingTimeCalendarProvider the provider of working time calendars
     * for the schedule and resources
     * @return the working time calendar based on the resource assignments
     * of this task
     */
    public IWorkingTimeCalendar getResourceCalendar(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        return new ScheduleEntryWorkingTimeCalendar(getAssignments(), workingTimeCalendarProvider);
    }

    /**
     * Indicates if this path is on the critical path.  To be on the critical
     * path in Project.net parlance implies that the earliest and latest start
     * dates are identical, so there is no room for error in the completion of
     * the tasks predecessors.
     *
     * @return <code>boolean</code> indicating if this task is on the critical
     * path.
     */
    public boolean isCriticalPath() {
        return criticalPath;
    }

    /**
     * Indicate if the task is on the critical path.  This isn't really intended
     * to be part of the public API -- it is set during calculation.
     *
     * @param criticalPath a <code>boolean</code> indicating if the task is on
     * the critical path.
     */
    public void setCriticalPath(boolean criticalPath) {
        this.criticalPath = criticalPath;
    }

    public boolean isPastDeadline() {
    	//return !(deadline == null || deadline.after(this.endTime)) && !isComplete();
        return !(deadline == null || deadline.after(new Date())) && !isComplete();
    }

    /**
     * In the workplan list view or hierarchical view, all tasks have a number
     * which indicates what order they are in.  This is that number.  The
     * number is visible to the user.
     *
     * @return a <code>int</code> indicating what sequence number belongs to this
     * task.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * In the workplan list view or hierarchical view, all tasks have a number
     * which indicates what order they are in.  This is that number.  The
     * number is visible to the user.
     *
     * @param sequenceNumber a <code>int</code> indicating the ordering of
     * this task in an otherwise unordered list of tasks.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Indicates if this task was really created in another plan but was imported
     * using "AddExternal".
     *
     * @return a <code>boolean</code> variable indicated if this is an external
     * task.
     */
    public boolean isFromShare() {
        return isFromShare;
    }

    /**
     * Indicate if this task is really from another schedule.
     *
     * @param fromShare a <code>boolean</code> indicating if this task is really
     * an external task or schedule.
     */
    public void setFromShare(boolean fromShare) {
        isFromShare = fromShare;
    }

    public String getSharingSpaceName() {
        return sharingSpaceName;
    }

    public void setSharingSpaceName(String sharingSpaceName) {
        this.sharingSpaceName = sharingSpaceName;
    }

    public String getSharingSpaceID() {
        return sharingSpaceID;
    }

    public void setSharingSpaceID(String sharingSpaceID) {
        this.sharingSpaceID = sharingSpaceID;
    }

    public String getSharedObjectID() {
        return sharedObjectID;
    }

    public void setSharedObjectID(String sharedObjectID) {
        this.sharedObjectID = sharedObjectID;
    }

    public boolean isShareReadOnly() {
        return isShareReadOnly;
    }

    public void setShareReadOnly(boolean shareReadOnly) {
        isShareReadOnly = shareReadOnly;
    }
    
    

    /**
     * This method returns all schedules that this task is a member in.
     *
     * @return a <code>List</code> of schedule id's in which this task is a
     * member.
     */
    public List findScheduleMembership() throws PersistenceException {
        List schedules = new ArrayList();

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                    "select " +
                    "  plan_id " +
                    "from " +
                    "  pn_plan_has_task " +
                    "where " +
                    "  task_id = ?"
            );
            db.pstmt.setString(1, id);
            db.executePrepared();

            while (db.result.next()) {
                schedules.add(db.result.getString("plan_id"));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while loading " +
                    "schedule membership for task " + this.toString() + ", ", sqle);
        } finally {
            db.release();
        }

        return Collections.unmodifiableList(schedules);
    }

    /**
     * Calculates the duration of this task based on the start date of the
     * task and the amount of work allocated.
     * <p/>
     * This method updates the duration value available by {@link #getDurationTQ}. <br>
     * If the curent start time is null or the work is null, duration is set to
     * zero days.
     * <p/>
     * Duration cannot be calculated when there are no assignments; duration is always
     * as-specified by the user.  Calculating duration without assignments would simply result in
     * the same duration value because without assignments, days worked is computed using
     * the duration.
     * @param workingTimeCalendarProvider the working time calendar provider
     * that provides calendars for the schedule and resources
     * @see #getDaysWorked
     * @throws IllegalStateException if this task has no assignments
     */
    public final void calculateDuration(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        if (getAssignments().isEmpty()) {
            throw new IllegalStateException("Cannot calculate duration when there are no assignments; duration is always as specified and need not relate to work and would not change if recalculated");
        }
        TimeQuantity timeQuantityInDays = new TimeQuantity(getDaysWorked(workingTimeCalendarProvider).getTotalDays(), TimeQuantityUnit.DAY);
        TimeQuantityUnit originalUnit = getDurationTQ().getUnits();
        TimeQuantity timeQuantityInUnit = ScheduleTimeQuantity.convertToUnit(timeQuantityInDays, originalUnit, 3, BigDecimal.ROUND_HALF_UP);
        setDuration(timeQuantityInUnit);
    }

    public boolean isInternallyConsistent(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        boolean consistent = true;

        if (!getAssignments().isEmpty()) {
            TimeQuantity duration = new TimeQuantity(getDaysWorked(workingTimeCalendarProvider).getTotalDays(), TimeQuantityUnit.DAY);

            if (!duration.equals(this.duration)) {
                consistent = false;
            }
        }

        return consistent;
    }

    /**
     * Calculates task work calculated by calculating the sum of the
     * work performed by each assignment at their assignment percentages
     * between the current task dates.
     * <p>
     * When there are no assignments, a single 100% assignment
     * is assumed.
     * </p>
     * <p>
     * Assignment work is updated (if there are any assignments).
     * Task work is updated.
     * </p>
     * @param workingTimeCalendarProvider the provider from which to get working time calendars
     */
    public final void calculateWork(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {

        TimeQuantity taskWork = new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);

        for (Iterator it = this.assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            // Calculate and update assignment
            nextAssignment.calculateWork(workingTimeCalendarProvider, getStartTime(), getEndTime());
            taskWork = taskWork.add(nextAssignment.getWork());
        }

        setWork(taskWork);
    }

    /**
     * Calculates the end date based on assignments performing their work, or based on duration
     * if there are no assignments.
     * <p/>
     * Updates this schedule entry end date and assignment end dates.
     * <p/>
     * If the start time is null (that is, has not yet been computed), then no end date can be calculated.
     * <p/>
     * The end date is based on the latest date that all assignments complete their work on.
     * When this schedule entry has no assignments, the end date is based on a 100% assignment
     * completing an amount of work equal to the duration using the schedule default working time calendar.
     * @param workingTimeCalendarProvider the provider from which to get working time calendars
     */
    public void calculateEndDate(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        if (getStartTime() != null) {
            ScheduleEntryDateCalculator dateCalc = new ScheduleEntryDateCalculator(this, workingTimeCalendarProvider);
            Date endDate = dateCalc.addWorkAndupdateAssignmentDates(getStartTime());
            setEndTimeD(endDate);
            dateCalc.updateMaterialAssignmentDates(getStartTime(), endDate);
        }
    }

    /**
     * Returns the days worked by this schedule entry.
     * <p/>
     * The days worked is the aggregate of unique days/hours/minutes worked by
     * all resources assigned to the task.  The days worked by a task without
     * any resource assignments are computed based on a 100% assignment completing
     * an amount of work equal to the duration using the schedule working time calendar.
     * <p/>
     * For example, if the work is 1 day but one resource works Monday and
     * another resource works Tuesday (due to their working time calendars) the
     * days worked will be 2 days. If both resources work Monday, the days worked will be 1 day.
     * <p/>
     * If the curent start time is null or the work is null, days worked
     * is zero days.
     * </p>
     * @param workingTimeCalendarProvider the working time calendar provider to
     * use to get working times for the resources in this schedule entry
     * @return the days worked; never null
     */
    public final IDaysWorked getDaysWorked(IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        IDaysWorked daysWorked;

        if ((startTime != null) && (this.work != null)) {
            IWorkingTimeCalendar cal = getResourceCalendar(workingTimeCalendarProvider);

            Date startTime;
            if (ignoreTimePortionOfDate) {
                // Ensure start time is at start of working day
                // We automatically move the time to the next working day
                // for the purposes of calculating duration, but we don't update
                // the time value fot the task

                if (!cal.isWorkingDay(this.startTime)) {
                    startTime = cal.getStartOfNextWorkingDay(this.startTime);
                } else {
                    startTime = cal.getStartOfWorkingDay(this.startTime);
                }

            } else {
                // Use exact start time; we're assuming that times
                // have been calculated correctly
                startTime = this.startTime;
            }

            daysWorked = getDaysWorked(startTime, workingTimeCalendarProvider);

        } else {
            // Zero days worked
            daysWorked = new DaysWorked();
        }

        return daysWorked;
    }

    /**
     * Calculates the duration based on assignment work beginning on the specified
     * start date.
     * <p/>
     * When there are no assignments, a 100% assignment completing an amount of work
     * equal to the duration is used to compute days worked.  Work value is purposefully ignored
     * since without assignments, work and duration are unrelated.
     * @param startDate the date on which work begins
     * @return the days worked
     */
    private IDaysWorked getDaysWorked(Date startDate, IWorkingTimeCalendarProvider workingTimeCalendarProvider) {

        IDaysWorked totalDaysWorked = new DaysWorked();

        if (getAssignments().isEmpty()) {
            // No assignments; schedule entry days worked is based on an amount
            // of work equal to the duration
            // Create a 100% assignment with all the work
            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPercentAssigned(100);
            assignment.setWork(ScheduleTimeQuantity.convertToHour(getDurationTQ()));
            assignment.setStartTime(getStartTime());
            assignment.setEndTime(getEndTime());
            totalDaysWorked.add(assignment.getDaysWorked(startDate, workingTimeCalendarProvider));

        } else {
            // Some assignments; schedule entry days worked based on those assignments
            // completing their work
            for (Iterator it = getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();

                // Get the days worked by this assignment
                IDaysWorked daysWorked = nextAssignment.getDaysWorked(startDate, workingTimeCalendarProvider);

                // Update the total days worked structure for this assignment
                totalDaysWorked.add(daysWorked);
            }
        }

        // Calculate the total hours worked for all assignments
        return totalDaysWorked;
    }

    /**
     * Loads the comments for this task.
     *
     * @throws net.project.persistence.PersistenceException if the comments cannot be loaded from the
     * database.
     */
    private void loadComments() throws PersistenceException {
        TaskComments taskComments = new TaskComments();
        taskComments.setSpace(SessionManager.getUser().getCurrentSpace());
        taskComments.setTask(this);
        taskComments.load();
        this.comments = taskComments;
    }

    /**
     * Returns this Task as a vCalendar with a single vEvent entity.
     *
     * @return the VCalendar representation of this Task
     */
    @Override
	public VCalendar getVCalendar() {
        VCalendar vcal = new VCalendar();

        // Give it a name for attachment purposes
        vcal.setName(getName());

        // Specify the timezone property
        vcal.addProperty(new TimeZoneProperty(SessionManager.getUser().getTimeZone()));

        // Add the VEvent to the vCalendar
        vcal.addEntity(getVCalendarEntity());

        return vcal;
    }

    /**
     * Returns this Task as a VCalendarEntity. <b>Note:</b> A Task would
     * ideally be a vToDo.  However, since MS Outlook 2000 does not appear to
     * support vToDos, it is being returned as a vEvent.
     *
     * @return the VCalendarEntity representaiton of this Task (a vEvent)
     */
    @Override
	public net.project.calendar.vcal.VCalendarEntity getVCalendarEntity() {

        VEvent event = new VEvent();

        // Add the appropriate properties to the event
        event.addProperty(new SummaryProperty(getName()));
        event.addProperty(new DescriptionProperty(getDescription()));
        event.addProperty(new StartDateTimeProperty(getStartTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));
        event.addProperty(new EndDateTimeProperty(getEndTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));

        // Task should not show up as busy
        event.addProperty(new TimeTransparencyProperty(TimeTransparencyProperty.TRANSPARENT));

        return event;
    }

    /**
     * Get a comma-delimited list of the people that are assigned to this task.
     *
     * @return A <code>String</code> variable containing a list of resource names
     * assigned to this task.
     */
    @SuppressWarnings("rawtypes")
	public String getAssigneeString() throws PersistenceException {
        if (!isAssigneesLoaded()) {
            loadAssignments();
            setAssigneesLoaded(true);
        }

        Iterator assignees = assignments.iterator();
        StringBuffer assigneeString = new StringBuffer();

        while (assignees.hasNext()) {
            //ScheduleEntryAssignment o = (ScheduleEntryAssignment)assignees.next();
        	Assignment o = (Assignment)assignees.next();
            
            assigneeString.append(o.getPersonName());
            if (assignees.hasNext()) {
                assigneeString.append(", ");
            }
        }

        return assigneeString.toString();
    }

    public void loadAssignments() throws PersistenceException {
        AssignmentManager assignmentsManager = new AssignmentManager();
        assignmentsManager.setObjectID(getID());
        assignmentsManager.loadAssigneesForObject();
        addAssignments(assignmentsManager.getAssignments());
    }
    
    public void loadMaterialAssignments(){
    	this.materialAssignments.clear();
    	this.materialAssignments.load(getSpaceID(), getID());
    	setMaterialAssigneesLoaded(true);
    }
    
    /**
     * Adds all the assignments in the specified collection to this task.
     * <p>
     * The current assignments are cleared before adding.
     * </p>
     * @param assignments the assignments to add; each element must be
     * an <code>Assignment</code>
     */
    @SuppressWarnings("rawtypes")
	public void addAssignments(Collection assignments) {
        this.assignments.clear();
        this.assignments.addAllAssignments(assignments);
        setAssigneesLoaded(true);
    }
    
    public void addMaterialAssignments(MaterialAssignmentList assignments){
    	this.materialAssignments.clear();
    	this.materialAssignments = assignments;
    	setMaterialAssigneesLoaded(true);
    }
    


    /**
     * Adds the specified assignment to this task.
     * @param assignment the assignment to add
     */
    public void addAssignment(ScheduleEntryAssignment assignment) {
        this.assignments.addAssignment(assignment);
    }

    /**
     * Removes the assignment with matching resource person ID.
     * <p>
     * After calling, there will be one fewer assignment.
     * </p>
     * @param assignment the assignment to remove
     */
    public void removeAssignment(ScheduleEntryAssignment assignment) {
        this.assignments.removeAssignment(assignment);
    }

    /**
     * Returns the assignments for this schedule entry.
     * @return an unmodifiable collection where each element is a
     * <code>{@link ScheduleEntryAssignment}</code>.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getAssignments() {
        return Collections.unmodifiableCollection(this.assignments.getAssignments());
    }

	public ArrayList<MaterialAssignment> getActiveMaterialAssignments() {
        return this.materialAssignments.getActiveMaterialAssignments();
    } 	
        
    public AssignmentList getAssignmentList() {
        return this.assignments;
    }

	public MaterialAssignmentList getMaterialAssignmentsList() {
		return this.materialAssignments;
	}    
    
    public void setAssignmentList(AssignmentList assignmentList) {
        this.assignments = assignmentList;
    }

    /**
     * Indicates if we should be sending notifications when this task is stored.
     * Currently, we don't send notifications when we are doing imports from MS
     * Project because they take too long to process.
     *
     * @return a <code>boolean</code> indicating if we should be sending
     * notification to interested parties when this schedule entry is stored.
     */
    public boolean isSendNotifications() {
        return sendNotifications;
    }

    public void setSendNotifications(boolean sendNotifications) {
        this.sendNotifications = sendNotifications;
    }

    /**
     * Accepts an <code>IScheduleVisitor</code> and visits this Task using that visitor.
     * @param visitor an IScheduleVisitor with which to visit this Task
     */
    public void accept(IScheduleVisitor visitor) throws VisitException {
        visitor.visitTask(this);
    }

    /**
     * Indicates whether the specified object is a task is equal to this task.
     * Equality is based on ID equlity.
     * If both tasks have a null ID, they are considered equal.
     * @param o the task to compare
     * @return true if the specified task has the same ID; false otherwise
     */
    @Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleEntry)) return false;

        final ScheduleEntry task = (ScheduleEntry)o;

        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        if (work != null ? !work.equals(task.work) : task.work != null) return false;
        if (duration != null ? !duration.equals(task.duration) : task.duration != null) return false;
        if (startTime != null ? !startTime.equals(task.startTime) : task.startTime != null) return false;
        if (endTime != null ? !endTime.equals(task.endTime) : task.endTime != null) return false;
        if (workPercentComplete != null ? !workPercentComplete.equals(task.workPercentComplete) : task.workPercentComplete != null) return false;
        if (workComplete != null ? !workComplete.equals(task.workComplete) : task.workComplete != null) return false;
        if (priority != null ? !priority.equals(task.priority) : task.priority != null) return false;
        return true;
    }

    @Override
	public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    @Override
	public String toString() {
        return (name == null ? "Unnamed Task" : name) + "(id:" + id + ",start:" + startTime + ",end:" + endTime + ")";
    }

    /**
     * If a start date and end date has been entered for this task, ignore any
     * "time" portion of the start and end date.  Instead assume that the start
     * date always begins at the beginning of the day and that the finish date
     * always occurs at the end of the work day.
     *
     * This method is introduced to help continue supporting the schedules which
     * don't use autocalculation.
     *
     * @return a <code>boolean</code> indicating if the start and end dates
     * ignore the times.
     */
    public boolean isIgnoreTimePortionOfDate() {
        return ignoreTimePortionOfDate;
    }

    /**
     * If a start date and end date has been entered for this task, ignore any
     * "time" portion of the start and end date.  Instead assume that the start
     * date always begins at the beginning of the day and that the finish date
     * always occurs at the end of the work day.
     *
     * This method is introduced to help continue supporting the schedules which
     * don't use autocalculation.
     *
     * @param ignoreTimePortionOfDate a <code>boolean</code> indicating if the
     * start and end dates ignore the times.
     */
    public void setIgnoreTimePortionOfDate(boolean ignoreTimePortionOfDate) {
        this.ignoreTimePortionOfDate = ignoreTimePortionOfDate;
    }



    /**
     * Indicates if the assignments have been loaded.
     *
     * @return a <code>boolean</code> indicating if the assignments have been
     * loaded.
     */
    public boolean isAssigneesLoaded() {
        return assigneesLoaded;
    }

    /**
     * Indicate that the assignees have been loaded from the database.
     *
     * @param assigneesLoaded a <code>boolean</code> indicating that the
     * assignment loading has been completed.
     */
    public void setAssigneesLoaded(boolean assigneesLoaded) {
        this.assigneesLoaded = assigneesLoaded;

        if (assigneesLoaded) {
            assignments.setLastSavedState();
        }
    }
    
    public void setMaterialAssignmentssLoaded(boolean materialAssignmentsLoaded) {
        this.materialAssignmentsLoaded = materialAssignmentsLoaded;
//
//        if (materialAssignmentsLoaded) {
//            assignments.setLastSavedState();
//        }
    }

    /**
     * Get the earliest date on which this task could start without interfering
     * with the rest of the tasks that surround it.
     *
     * @return a <code>Date</code> field which represents the earliest date on
     * which the task can start.
     */
    public Date getEarliestStartDate() {
        return earliestStartDate;
    }

    /**
     * Get the earliest date on which this task could start without interfering
     * with the rest of the tasks attached to the start or finish of the task.
     *
     * @param earliestStartDate a <code>Date</code> field which represents the earliest
     * date on which the task can start.
     */
    public void setEarliestStartDate(Date earliestStartDate) {
        this.earliestStartDate = earliestStartDate;
    }

    /**
     * Get the earliest date on which this schedule entry can finish without
     * changing the dates of any successors or predecessors attached to this
     * task.
     *
     * @return a <code>Date</code> object which contains the earliest date on
     * which this schedule entry can finish without affecting predecessors or
     * successors.
     */
    public Date getEarliestFinishDate() {
        return earliestFinishDate;
    }

    /**
     * Set the earliest finish date on which this schedule entry can finish
     * without changing the dates of any successors or predecessors attached to
     * this task.
     *
     * @param earliestFinishDate a <code>Date</code> object which contains the earliest
     * date on which this schedule entry can finish without affecting predecessors
     * or successors.
     */
    public void setEarliestFinishDate(Date earliestFinishDate) {
        this.earliestFinishDate = earliestFinishDate;
    }

    /**
     * Get the latest date on which this task could start without interfering
     * with the rest of the tasks that surround it.
     *
     * @return a <code>Date</code> field which represents the latest date on
     * which the task can start.
     */
    public Date getLatestStartDate() {
        return latestStartDate;
    }

    /**
     * Get the latest date on which this task could start without interfering
     * with the rest of the tasks attached to the start or finish of the task.
     *
     * @param latestStartDate a <code>Date</code> field which represents the latest
     * date on which the task can start.
     */
    public void setLatestStartDate(Date latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    /**
     * Get the latest date on which this schedule entry can finish without
     * changing the dates of any successors or predecessors attached to this
     * task.
     *
     * @return a <code>Date</code> object which contains the latest date on
     * which this schedule entry can finish without affecting predecessors or
     * successors.
     */
    public Date getLatestFinishDate() {
        return latestFinishDate;
    }

    /**
     * Set the latest finish date on which this schedule entry can finish
     * without changing the dates of any successors or predecessors attached to
     * this task.
     *
     * @param latestFinishDate a <code>Date</code> object which contains the latest
     * date on which this schedule entry can finish without affecting predecessors
     * or successors.
     */
    public void setLatestFinishDate(Date latestFinishDate) {
        this.latestFinishDate = latestFinishDate;
    }

    /**
     * Return the date that this task was last modified.
     *
     * @return a <code>Date</code> indicating when the task was last modified.
     */
    public Date getLastModifiedDate() {
        return this.dateModified;
    }

    /**
     * Set the date that the schedule entry was to start when the schedule entry
     * was baselined.
     *
     * @return a <code>Date</code> from the point when the baseline was
     * established.
     */
    public Date getBaselineStart() {
        return baselineStart;
    }

    /**
     * Set the date when this task is "supposed" to start.  This date is not
     * stored when the schedule entry is stored.  You'll need to use the
     * baseline object to populate this field.
     *
     * @param baselineStart a <code>Date</code> indidcating when the baseline
     * start date is for this schedule entry.
     */
    public void setBaselineStart(Date baselineStart) {
        this.baselineStart = baselineStart;
    }

    /**
     * Get the amount of time between when the schedule entry was supposed to
     * start and when it actually started.
     *
     * @param provider an <code>IWorkingTimeCalendarProvider</code> for the
     * schedule.  This allows us to compute variance.
     * @return the <code>TimeQuantity</code> that occurred between the two dates.
     * Note that this is the amount of distinct working time, not calendar time.
     */
    public TimeQuantity getStartDateVariance(IWorkingTimeCalendarProvider provider) {
        if (baselineStart != null) {
            return provider.getDefault().subtractWorkingTimeForDateRange(baselineStart, startTime);
        }
        return new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.DAY);
    }

    /**
     * Get the date when the baseline says this schedule entry is supposed to
     * start.
     *
     * @return a <code>Date</code> indicating when this schedule is entry is
     * supposed to start according to the baseline.
     */
    public Date getBaselineEnd() {
        return baselineEnd;
    }

    /**
     * Set the date when the schedule entry is supposed to start according to a
     * baseline.  Note that this field is not stored when the schedule entry is
     * saved, it is here for display purposes only.  Use the Baseline object to
     * update this field.
     *
     * @param baselineEnd a <code>Date</code> indicating when this schedule
     * entry is supposed to end.
     */
    public void setBaselineEnd(Date baselineEnd) {
        this.baselineEnd = baselineEnd;
    }

    /**
     * Get the amount of time between when the schedule entry was supposed to
     * be completed and when it currently is going to be (or already was)
     * completed.
     *
     * @param provider a <code>IWorkingTimeCalendarProvider</code> for the
     * schedule.  This allows us to compute the amount of time between the
     * baseline end date and end date taking working time into account.
     * @return a <code>TimeQuantity</code> which tells us the difference between
     * the planned end date and the end date.
     */
    public TimeQuantity getEndDateVariance(IWorkingTimeCalendarProvider provider) {
        if (baselineEnd != null) {
            return provider.getDefault().subtractWorkingTimeForDateRange(baselineEnd, endTime);
        }
        return new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.DAY);
    }

    /**
     * Get the amount of work that was supposed to be done in order to complete
     * this schedule entry.
     *
     * @return a <code>TimeQuantity</code> corresponding to the amount of work
     * that was anticipated when this schedule entry was baselined.
     */
    public TimeQuantity getBaselineWork() {
        return baselineWork;
    }

    /**
     * Set the amount of work that is supposed to be done in order to complete
     * this schedule entry.  Note that this value is not stored in the database.
     * To store baseline information, use the Baseline object.
     *
     * @param baselineWork a <code>TimeQuantity</code> indicating the amount of
     * work that should be done in order to complete the TimeQuantity.
     */
    public void setBaselineWork(TimeQuantity baselineWork) {
        this.baselineWork = baselineWork;
    }

    /**
     * Returns the difference between the amount of work we anticipated as
     * needing and the amount we actually needed in order to finish the task.
     *
     * @return a <code>TimeQuantity</code> indicating the difference between the
     * planned work and the real work.
     */
    public TimeQuantity getWorkVariance() {
        if (baselineWork != null) {
            return work.subtract(baselineWork);
        }
        return new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
    }

    /**
     * Get the duration that is suspected this schedule entry will take to
     * complete, according to the current default baseline.
     *
     * @return a <code>TimeQuantity</code> indicating how much duration should
     * pass before the schedule entry is completed.
     */
    public TimeQuantity getBaselineDuration() {
        return baselineDuration;
    }

    /**
     * Set the duration that is anticipated as needing to pass before the
     * schedule entry will be complete.  Note that this field will not be stored
     * when the schedule entry is stored.
     *
     * @param baselineDuration a <code>TimeQuantity</code> anticipating how much
     * time will be needed for the schedule entry to be completed.
     */
    public void setBaselineDuration(TimeQuantity baselineDuration) {
        this.baselineDuration = baselineDuration;
    }

    /**
     * Get the difference between the duration that was planned and the amount
     * of duration that occurred when the project was executed.  If the task
     * isn't already complete, the comparison is based on the current estimate
     * of completion.
     *
     * @return a <code>TimeQuantity</code> representing the difference between
     * the scheduled and real amounts of variance.
     */
    public TimeQuantity getDurationVariance() {
        if (baselineDuration != null) {
            return duration.subtract(baselineDuration);
        }
        return new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.HOUR);
    }

    /**
     * Get the id of the current baseline for this task.
     *
     * @return a <code>String</code> containing the current baseline for this
     * task.
     */
    public String getBaselineID() {
        return baselineID;
    }

    /**
     * Set the current baseline for this schedule entry.  This value isn't
     * stored in the database -- it is here for display purposes only.
     *
     * @param baselineID a <code>String</code> containing the current baseline
     * id for the schedule entry.
     */
    void setBaselineID(String baselineID) {
        this.baselineID = baselineID;
    }

    /**
     * Resets the baseline of this schedule entry to its current values.  This
     * makes it so we don't have to do a database roundtrip to get the values.
     */
    public void resetBaseline() {
        baselineStart = startTime;
        baselineEnd = endTime;
        baselineWork = work;
        baselineDuration = duration;
    }

    /**
     * Store the task object to the database.
     * @throws net.project.persistence.PersistenceException if the object is unable to store the task
     * in the database.
     * @deprecated as of 7.6.3; Use {@link #store(boolean, Schedule)} instead.
     * Since no schedule is specified, it assumes a schedule with a time zone
     * based on the user's default time zone and no specific working time
     * calendar.  This is not correct in most cases.  Instead, a loaded
     * schedule is required which provides the default schedule working time
     * calendar
     */
    @Deprecated
	@Override
	public void store() throws PersistenceException {
        Schedule schedule = new Schedule();
        schedule.setTimeZone(SessionManager.getUser().getTimeZone());
        store(false, schedule);
    }

    /**
     * Store the task object to the database.
     * @param db the db bean object
     * @throws java.sql.SQLException if the object is unable to store the task
     * in the database.
     * @deprecated as of 7.6.3; Use {@link #store(boolean, Schedule)} instead.
     * Since no schedule is specified, it assumes a schedule with a time zone
     * based on the user's default time zone and no specific working time
     * calendar.  This is not correct in most cases.  Instead, a loaded
     * schedule is required which provides the default schedule working time
     * calendar
     */
    @Deprecated
	@Override
	public void store(DBBean db) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setTimeZone(SessionManager.getUser().getTimeZone());
        try {
            store(false, schedule, db);
        } catch (PersistenceException e) {
            throw new SQLException(e.toString());
        }
    }

    /**
     * Store the task object to the database.
     *
     * @param autocalculateDates a <code>boolean</code> indicating whether we
     * should automatically recalculate all dates in this schedule when this
     * task is stored.
     * @param schedule a <code>Schedule</code> object.
     * @throws net.project.persistence.PersistenceException if the object is unable to store the task
     * in the database.
     */
    public void store(boolean autocalculateDates, Schedule schedule) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(autocalculateDates, schedule, db);
            db.commit();
            // store the id of charge code applied on this task along with id of task. 
            if(org.apache.commons.lang.StringUtils.isNotEmpty(this.chargeCodeId)){
            	ServiceFactory.getInstance().getPnObjectHasChargeCodeService().save(Integer.valueOf(this.id), Integer.valueOf(chargeCodeId), Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ScheduleEntry.class).error("Error occurred while storing task: " + sqle);
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(ScheduleEntry.class).debug("Unable to roll back task.");
            }
            // throw an invalid task name exception, if the value is too large for task name column
            // ORA-12899, ORA-01461 are the error codes of database exception for large data
            if(sqle.getMessage().indexOf("12899") > 0 || sqle.getMessage().indexOf("01461") > 0){
            	throw new PersistenceException(PropertyProvider.get("prm.schedule.taskview.invalidtaskname.message"), sqle);
            }
            throw new PersistenceException(PropertyProvider.get("prm.schedule.taskview.errorstoringtask.message"), sqle);
        } finally {
            db.release();
        }
        
        /*
         * Recalculate the start time, end time, and duration for all tasks
         * related to this task.
         */
        if (autocalculateDates) {
            if (schedule.isAutocalculateTaskEndpoints()) {
                TaskEndpointCalculation calc = new TaskEndpointCalculation();
                calc.recalculateTaskTimes(schedule, true);
            }
        }
    }

    /**
	 * Store the task object to the database.
	 * 
	 * @param autocalculateDates
	 *            a <code>boolean</code> indicating whether we should
	 *            automatically recalculate all dates in this schedule when this
	 *            task is stored.
	 * @param schedule
	 *            a <code>Schedule</code> object.
	 * @throws net.project.persistence.PersistenceException
	 *             if the object is unable to store the task in the database.
	 */
	public void store(boolean autocalculateDates, Schedule schedule, DBBean db) throws SQLException, PersistenceException {

		/*
		 * PCD: the getStartOfWorkingDay() code defaults the startTime for the
		 * WorkingTimeCalendar. This is most important in the case where this is
		 * the first task being stored in a schedule which doesn't have a
		 * defined schedule start date. The task will default to an 8am
		 * startTime -- as will the schedule. Subsequent changes to the schedule
		 * startTime OR the project working time calendar will override this
		 * settings within taskEndpointCalculation.
		 */
		if (autocalculateDates) {
			IWorkingTimeCalendar workingTimeCalendar = schedule.getWorkingTimeCalendar();
			if (this.startTime == null) {
				Date startTime = new Date();
				if (workingTimeCalendar.isWorkingDay(startTime)) {
					setStartTimeD(workingTimeCalendar.getStartOfWorkingDay(startTime));
				} else {
					setStartTimeD(workingTimeCalendar.getStartOfNextWorkingDay(startTime));
				}
			}
			if (this.endTime == null) {
				Date endTime = getStartTime();
				setEndTimeD(workingTimeCalendar.getEndOfWorkingDay(endTime));
			}
		}
		int index = 0;
		int idIndex;
		int seqIndex;
		TaskEvent event = new TaskEvent();
		
		net.project.events.TaskEvent taskEvent = null;
		
		/*
		 * call Stored Procedure to insert or update all the tables involved in
		 * storing a meeting.
		 */
		db.prepareCall("{call SCHEDULE.STORE_TASK(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)}");
		DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager.getUser().getID());
		DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager.getUser().getCurrentSpace().getID());
		DatabaseUtils.setInteger(db.cstmt, ++index, this.planID);
		DatabaseUtils.setInteger(db.cstmt, ++index, this.id);
		db.cstmt.setString(++index, this.name);
		db.cstmt.setString(++index, this.description);
		db.cstmt.setString(++index, this.getType());
		DatabaseUtils.setInteger(db.cstmt, ++index, this.priority.getID());
		DatabaseUtils.setBigDecimal(db.cstmt, ++index, this.duration.getAmount());
		db.cstmt.setInt(++index, this.duration.getUnits().getUniqueID());
		db.cstmt.setDouble(++index, this.work.getAmount().doubleValue());
		db.cstmt.setInt(++index, this.work.getUnits().getUniqueID());
		db.cstmt.setDouble(++index, this.workComplete.getAmount().doubleValue());
		db.cstmt.setInt(++index, this.workComplete.getUnits().getUniqueID());
		/* now store work / workcomplete MS values */
		db.cstmt.setDouble(++index, ScheduleTimeQuantity.convertToUnit(this.work, TimeQuantityUnit.MILLISECOND, 3, BigDecimal.ROUND_HALF_UP).getAmount().doubleValue());
		db.cstmt.setDouble(++index, ScheduleTimeQuantity.convertToUnit(this.workComplete, TimeQuantityUnit.MILLISECOND, 3, BigDecimal.ROUND_HALF_UP).getAmount().doubleValue());
		DatabaseUtils.setBigDecimal(db.cstmt, ++index, this.getWorkPercentCompleteDecimal().multiply(new BigDecimal("100")));
		DatabaseUtils.setBigDecimal(db.cstmt, ++index, this.getPercentCompleteDecimal().multiply(new BigDecimal("100")));
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.endTime);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.startTime);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.actualStart);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.actualFinish);
		db.cstmt.setBoolean(++index, this.criticalPath);
		db.cstmt.setString(++index, "A");
		DatabaseUtils.setInteger(db.cstmt, ++index, this.parentTaskID);
		db.cstmt.setBoolean(++index, this.ignoreTimePortionOfDate);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.earliestStartDate);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.earliestFinishDate);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.latestStartDate);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.latestFinishDate);
		db.cstmt.setBoolean(++index, isMilestone());
		DatabaseUtils.setInteger(db.cstmt, ++index, this.constraintType.getID());
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.constraintDate);
		DatabaseUtils.setTimestamp(db.cstmt, ++index, this.deadline);
		DatabaseUtils.setInteger(db.cstmt, ++index, this.phaseID);
		db.cstmt.setDouble(++index, this.unallocatedWorkComplete.getAmount().doubleValue());
		db.cstmt.setInt(++index, this.unallocatedWorkComplete.getUnits().getUniqueID());
		db.cstmt.setDouble(++index, this.unassociatedWorkComplete.getAmount().doubleValue());
		db.cstmt.setInt(++index, this.unassociatedWorkComplete.getUnits().getUniqueID());
		if (this.calculationType == null) {
			// Required
			throw new NullPointerException("Attempted to store a schedule entry without a task calculation type");
		}
		DatabaseUtils.setInteger(db.cstmt, ++index, this.calculationType.getID());
        db.cstmt.setString(++index, this.wbs);
        db.cstmt.setString(++index, this.wbsLevel);
        db.cstmt.setBoolean(++index, isUnindentStatus());
		if (this.sequenceNumber != 0) {
			db.cstmt.setInt(++index, this.sequenceNumber);
		} else {
			db.cstmt.setNull(++index, Types.INTEGER);
		}
		seqIndex = index;
		db.cstmt.registerOutParameter(seqIndex, Types.INTEGER);
		db.cstmt.registerOutParameter((idIndex = ++index), Types.INTEGER);
		db.executeCallable();

		/*
		 * Checks whether the task is being newly created or existing one is
		 * being updated
		 */
		boolean checkForStatus;
		if (this.id == null || (this.id != null && this.id.trim().equals(""))) {
			checkForStatus = true;
		} else {
			checkForStatus = false;
		}
		// Get task id and error code
		setSequenceNumber(db.cstmt.getInt(seqIndex));
		setID(Integer.toString(db.cstmt.getInt(idIndex)));
		
		storeComment(db);
		storeDependencies(db);
		storeAssignments(db, autocalculateDates);
		storeMaterialAssignments();
		
		// bfd: 2362: Creating a task gets logged twice in task history
        //TaskEndpointCalculation makes sendNotifications before again calling
        //to store the modified tasks
		if (this.sendNotifications) {
			if (checkForStatus) {
				//event.setSpaceID(getSpaceID());
				//Avoid null userID
				event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
				event.setTargetObjectID(id);
				event.setTargetObjectType(EventCodes.CREATE_TASK);
				event.setTargetObjectXML(this.getXMLBody());
				event.setEventType(EventCodes.CREATE_TASK);
				event.setUser(SessionManager.getUser());
				event.setDescription("New Task created : \"" + this.name + "\"");
				event.store(db);
				
				taskEvent = (net.project.events.TaskEvent) EventFactory.getEvent(ObjectType.TASK, EventType.NEW);
				taskEvent.setObjectID(this.id);
				taskEvent.setObjectType(ObjectType.TASK);
				taskEvent.setSpaceID(schedule.getSpaceId());
			} else {
				event.setTargetObjectID(this.id);
				event.setSpaceID(getSpaceID());
				event.setTargetObjectType(EventCodes.MODIFY_TASK);
				event.setTargetObjectXML(this.getXMLBody());
				event.setEventType(EventCodes.MODIFY_TASK);
				event.setUser(SessionManager.getUser());
				event.setDescription("Task Modified : \"" + this.name + "\"");
				event.store(db);
				
				taskEvent = (net.project.events.TaskEvent) EventFactory.getEvent(ObjectType.TASK, EventType.EDITED);
				taskEvent.setObjectID(this.id);
				taskEvent.setObjectType(ObjectType.TASK);
				taskEvent.setSpaceID(schedule.getSpaceId());
			}
			
			// publishing event to asynchronous queue
	        try {
	        	taskEvent.setTaskName(this.name);
	        	taskEvent.setObjectRecordStatus("A");
	        	taskEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(ScheduleEntry.class).error("ScheduleEntry.store():: Task Create/Modify Event Publishing Failed! "+ e.getMessage());
			}
		}
        /*
         * Make sure that anywhere this task is shared (imported ) it is updated.
         * as per bfd 3058
         */
        ExternalDependencies.update(this, db);
    }

    /**
	 * Store any task assignments for this method. <p/> Deletes assignments that
	 * have been removed, adds assignments that have been added and updates
	 * assignments, only if the list of assignments has been modified.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem trying to save the assignments.
	 */
    private void storeAssignments(DBBean db, boolean isAutocalculate) throws PersistenceException, SQLException {
        // If autocalculation is turned on, the assignment's times hsave already
        // been updated.
        if (!isAutocalculate) {
            for (Iterator it = assignments.iterator(); it.hasNext();) {
                ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();

                // TODO: review the following comment.
                // This is slightly wrong. This code is in here for schedules
                //that aren't autocalculated, but it is screwing us up in autocalculate
                //schedules because we store from there with the autocalculate flag
                //turned off.
                if (assignment.getStartTime() == null) {
                    assignment.setStartTime(getStartTime());
                }
                if (assignment.getEndTime() == null) {
                    assignment.setEndTime(getEndTime());
                }
            }
        }

        assignments.store(db);
    }
    
    private void storeMaterialAssignments()
    {
    	ServiceFactory.getInstance().getPnMaterialAssignmentService().saveMaterialAssignments(materialAssignments);
    }

    private void storeDependencies(DBBean db) throws SQLException, PersistenceException {
        getPredecessors().store(db);
    }

    /**
	 * Store this task's comment. Only non-null comments are stored.
	 * 
	 * @param db
	 *            a <code>DBBean</code> already in transaction. (It MUST be in
	 *            a transaction.)
	 * @throws net.project.persistence.PersistenceException
	 *             if there is a problem storing.
	 */
	private void storeComment(DBBean db) throws PersistenceException {
		if (getComment() != null) {
			TaskComment taskComment = new TaskComment();
			taskComment.setTaskID(getID());
			taskComment.setCreatedByID(SessionManager.getUser().getID());
			taskComment.setCreatedDatetime(new Date());
			taskComment.setText(this.comment);
			taskComment.store(db);
		}
	}

    /**
	 * Load the task from the database
	 * 
	 * @throws net.project.persistence.PersistenceException
	 *             if there is a problem loading the task
	 */
	@Override
	public void load() throws PersistenceException {
		this.isLoaded = false;
		this.predecessorList.clear();
		TaskFinder tf = new TaskFinder();
		this.isLoaded = tf.findByID(this.id, this, false, true, true);
		if (!isLoaded()) {
			throw new PersistenceException("could not load task");
        }
    }

    public void remove(boolean delayConsistencyCleanup, String isSummary, String deleteTask) throws PersistenceException {
		if (this.id == null) {
			throw new NullPointerException("taskId is null");
		}
		DBBean db = new DBBean();
		try {
			db.openConnection();
			db.connection.setAutoCommit(false);
			remove(db, delayConsistencyCleanup, isSummary, deleteTask);
			db.commit();
		} catch (SQLException sqle) {
			if (db.connection != null) {
				try {
					db.connection.rollback();
				} catch (SQLException sqle2) {
				}
			}
			this.isLoaded = false;
			Logger.getLogger(ScheduleEntry.class).error("Task.remove() threw an SQL exception: " + sqle);
			throw new PersistenceException("Task remove failed.", sqle);
		} finally {
			db.release();
		}
	}

    public void remove(DBBean db, boolean delayConsistencyCleanup, String isSummary, String deleteTask) throws SQLException, PersistenceException {
		this.isLoaded = false;
        /* Standard delete query */
        String query = "UPDATE pn_task SET record_status='D' WHERE task_id=?";// + this.id;
		/* Fetch All Child tasks */
        String childQuery = "select task_id from pn_task START WITH parent_task_id = ? CONNECT BY parent_task_id = PRIOR task_id";
        //String childQuery = "UPDATE pn_task SET record_status='D' WHERE parent_task_id=" + this.id;
		/* Make sure if a parent is deleted, its children know they are orphans */
        String updateChildrenQuery = "update pn_task set parent_task_id = null where parent_task_id = " + this.id;
		/* Make sure if all the children are deleted, a parent task becomes a regular task */
        String childlessParentUpdate = "update pn_task " + "set task_type = " + 
        "(select decode(count(*), 0, 'task', 'summary')" + "   from pn_task " + "  where parent_task_id = " + this.parentTaskID + " and record_status = 'A') " +
        "where task_id = " + this.parentTaskID;
		//sjmittal first ensure that the work and duration is updated to that when it was last task
		if(this.parentTaskID != null) {
		    TaskFinder finder = new TaskFinder();
		    ScheduleEntry parentTask = finder.findObjectByID(this.parentTaskID);
	        ScheduleEntryHistory history = new ScheduleEntryHistory();
	        history.setTaskID(this.parentTaskID);
	        history.setTaskType(TaskType.TASK.getID());
	        history.loadForTaskType();
	        ScheduleEntry lastTask = history.getLastHistoryEntry();
	        
	        //sjmittal minor fix while testing multiple delete
	        //if parent task is deleted before child task.
	        //or if there exist an history item of last task
	        if(parentTask != null && lastTask != null) {
		        //update duration and work
		        BigDecimal percentWorkComplete = parentTask.getPercentCompleteDecimal();
		        parentTask.setWork(lastTask.getWorkTQ());
		        //reset the % complete as this might have been changed
		        parentTask.setPercentComplete(percentWorkComplete);
		        parentTask.setWorkComplete(parentTask.getWorkTQ().multiply(percentWorkComplete));
		        //set the duration
		        parentTask.setDuration(lastTask.getDurationTQ());
	            //set the start date and the end 
	            parentTask.setStartTimeD(lastTask.getStartTime());
	            parentTask.setEndTimeD(lastTask.getEndTime());
		        parentTask.store();
	        }
		}
        /* Find all the child tasks where this schedule entry is shared. */
        List<String> childObjectIDs = new ArrayList();
        if((isSummary.equals("yes")) && (deleteTask.equals("0"))) {
            db.prepareStatement(childQuery);
            db.pstmt.setString(1, this.id);
            db.executePrepared();
            while (db.result.next()) {
                childObjectIDs.add(db.result.getString(1));
            }
        }
        
        //handle the task dependency
        db.prepareStatement("delete from pn_task_dependency where task_id = ? or dependency_id = ?");
		db.pstmt.setString(1, this.id);
		db.pstmt.setString(2, this.id);
		db.executePrepared();
		
        //next handle the task shares
		String deleteShareSQL = null;
		if (this.isFromShare) {
			deleteShareSQL = "delete from pn_shared where imported_object_id = " + this.id;
            //sjmittal: the children task also can be from share and it may make sense
            //to remove the entries from pn_shared table if these are deleted
		}
		/* Find all the places where this schedule entry is shared. */
		db.prepareStatement("select imported_object_id from pn_shared where exported_object_id = ?");
		db.pstmt.setString(1, this.id);
		db.executePrepared();
		List sharedObjectIDs = new ArrayList();
		while (db.result.next()) {
			sharedObjectIDs.add(db.result.getString(1));
		}
        if((isSummary.equals("yes")) && (deleteTask.equals("0"))) {
            //sjmittal: get the shared task of children too as these also may be deleted
            for(String childTaskId: childObjectIDs) {
                db.pstmt.setString(1, childTaskId);
                db.executePrepared();
                while (db.result.next()) {
                    sharedObjectIDs.add(db.result.getString(1));
                }
            }
        }
		/* Delete all of this task's and its children (if deleted) shares */
		for (Iterator it = sharedObjectIDs.iterator(); it.hasNext();) {
			String sharedObjectID = (String) it.next();
			//sjmittal fix for bfd 3174, we need to get the object form the db
			//so that the parent task id is fetched and its also gets updated
			//subsequently
			TaskFinder finder = new TaskFinder();
			ScheduleEntry task = finder.findObjectByID(sharedObjectID);
			task.remove(db, false, "no", "0");
		}
        //Penultimate step is to delete the task and adjust shared, parent and childern accordingly   
		if ((deleteTask.equals("0")) || ((isSummary.equals("yes")) && (deleteTask.equals("1")))) {
            //delete task and its childrend by setting the status D
            db.prepareStatement(query);
            db.pstmt.setString(1, this.id);
            db.executePrepared();
            for(String childTaskId: childObjectIDs) {
                db.pstmt.setString(1, childTaskId);
                db.executePrepared();
                
                // publishing event to remove task
                net.project.events.TaskEvent taskEvent = (net.project.events.TaskEvent) EventFactory.getEvent(ObjectType.TASK, EventType.DELETED);
        		taskEvent.setObjectID(childTaskId);		
        		taskEvent.setObjectType(ObjectType.TASK);
        		//taskEvent.setTaskName(this.name);
        		taskEvent.setSpaceID(getSpaceID());	
        		
        		// publishing event to asynchronous queue
                try {
                	taskEvent.setObjectRecordStatus("D");
        			taskEvent.publish();
        		} catch (EventException e) {
        			Logger.getLogger(ScheduleEntry.class).error("ScheduleEntry.remove() :: Task Remove Event Publishing Failed! "+ e.getMessage());
        		}
            }
            db.createStatement();
            //delete from pn_shared
            if (this.isFromShare) {
                db.stmt.addBatch(deleteShareSQL);
            }
//            if ((isSummary.equals("yes")) && (deleteTask.equals("0"))) {
//                db.stmt.addBatch(childQuery);
//            }
            //update the children and parent if needed
            db.stmt.addBatch(updateChildrenQuery);
            db.stmt.addBatch(childlessParentUpdate);
            db.stmt.executeBatch();
            db.connection.commit();
        }
        //Lastly handle the assignments
        AssignmentManager assignmentManager = new AssignmentManager();
		assignmentManager.setObjectID(this.id);
		assignmentManager.deleteAssignmentsForObject();
        //handle the delete of assignments if children are also deleted
        if((isSummary.equals("yes")) && (deleteTask.equals("0"))) {
            for(String childTaskId: childObjectIDs) {
                assignmentManager.setObjectID(childTaskId);
                assignmentManager.deleteAssignmentsForObject();
            }
        }
		/* load the Task so that we can get the correct XML */
		load();
		TaskEvent event = new TaskEvent();
		event.setSpaceID(getSpaceID());
		event.setTargetObjectID(this.id);
		event.setTargetObjectType(EventCodes.REMOVE_TASK);
		event.setTargetObjectXML(this.getXMLBody());
		event.setEventType(EventCodes.REMOVE_TASK);
		event.setUser(SessionManager.getUser());
		event.setDescription("Task deleted : \"" + this.name + "\"");
		event.store(db);
		
		net.project.events.TaskEvent taskEvent = (net.project.events.TaskEvent) EventFactory.getEvent(ObjectType.TASK, EventType.DELETED);
		taskEvent.setObjectID(this.id);		
		taskEvent.setObjectType(ObjectType.TASK);
		taskEvent.setTaskName(this.name);
		taskEvent.setSpaceID(getSpaceID());	
		
		// publishing event to asynchronous queue
        try {
        	taskEvent.setObjectRecordStatus("D");
			taskEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(ScheduleEntry.class).error("ScheduleEntry.remove() :: Task Remove Event Publishing Failed! "+ e.getMessage());
		}
		
		if (!delayConsistencyCleanup) {
			/* Now update the sequence numbers to make sure they are all correct */
			db.prepareCall("begin schedule.recalculate_sequence_numbers(?); end;");
			db.cstmt.setString(1, this.planID);
			db.executeCallable();
			db.prepareCall("begin SCHEDULE.FIX_SUMMARY_TASK_TYPES(?); end;");
			db.cstmt.setString(1, this.id);
			db.executeCallable();
//			db.prepareStatement("delete from pn_shared where imported_object_id = ?");
//			db.pstmt.setString(1, this.id);
//			db.executePrepared();
		}
	}

    /**
	 * Soft delete the task from the database
	 * 
	 * @throws net.project.persistence.PersistenceException
	 *             if the task cannot be soft deleted from the database.
	 * @throws java.lang.NullPointerException
	 *             if the task_id has not been set prior to calling the
	 *             <code>remove()</code> method.
	 */
    @Override
	public void remove() throws PersistenceException {
        remove(false, "No", "0");
    }

    /**
	 * This methods converts a ScheduleEntry from being one type (such as a
	 * milestone) into another type (such as a SummaryTask).
	 * 
	 * @param type
	 *            a <code>TaskType</code> that this schedule entry will
	 *            become.
	 * @return a <code>ScheduleEntry</code> object which is a clone of the
	 *         current one casted to the correct type.
	 */
    public ScheduleEntry as(TaskType type) {
        ScheduleEntry se;

        if (this.getTaskType().equals(type)) {
            se = this;
        } else {
            se = ScheduleEntryFactory.createFromType(type);

            setFieldsFromScheduleEntry(se);
        }

        return se;
    }

    @Override
	public Object clone() {
        ScheduleEntry clone = ScheduleEntryFactory.createFromType(getTaskType());
        setFieldsFromScheduleEntry(clone);

        return clone;
    }

    public void setFieldsFromScheduleEntry(ScheduleEntry se) {
		se.id = this.id;
		se.taskVersionID = this.taskVersionID;
		se.name = this.name;
		se.description = this.description;
		se.startTime = this.startTime;
		se.endTime = this.endTime;
		se.duration = this.duration;
		se.work = this.work;
		se.workComplete = this.workComplete;
		se.actualStart = this.actualStart;
		se.actualFinish = this.actualFinish;
		se.priority = this.priority;
		se.dateCreated = this.dateCreated;
		se.dateModified = this.dateModified;
		se.modifiedById = this.modifiedById;
		se.unallocatedWorkComplete = this.unallocatedWorkComplete;
		se.unassociatedWorkComplete = this.unassociatedWorkComplete;
		se.phaseID = this.phaseID;
		se.phaseName = this.phaseName;
		se.phaseSequence = this.phaseSequence;
		se.isPhaseSelected = this.isPhaseSelected;
		se.isPhasesLoaded = this.isPhasesLoaded;
		se.parentTaskID = this.parentTaskID;
		se.parentTaskName = this.parentTaskName;
		se.hierarchyLevel = this.hierarchyLevel;
		se.isOrphan = this.isOrphan;
		se.importTaskID = this.importTaskID;
		se.importParentTaskID = this.importParentTaskID;
		se.comment = this.comment;
		se.comments = this.comments;
		se.isLoaded = this.isLoaded;
		se.workPercentComplete = this.workPercentComplete;
		se.isMilestone = this.isMilestone;
		se.predecessorList = (PredecessorList) this.predecessorList.clone();
		se.successorList = (SuccessorList) this.successorList.clone();
		se.constraintType = this.constraintType;
		se.constraintDate = this.constraintDate;
		se.deadline = this.deadline;
		se.spaceID = this.spaceID;
		se.planID = this.planID;
		se.earliestStartDate = this.earliestStartDate;
		se.earliestFinishDate = this.earliestFinishDate;
		se.latestStartDate = this.latestStartDate;
		se.latestFinishDate = this.latestFinishDate;
		se.sequenceNumber = this.sequenceNumber;
		se.criticalPath = this.criticalPath;
		se.sendNotifications = this.sendNotifications;
		se.ignoreTimePortionOfDate = this.ignoreTimePortionOfDate;
		se.assigneesLoaded = this.assigneesLoaded;
		se.materialAssigneesLoaded = this.materialAssigneesLoaded;
		se.sharedObjectID = this.sharedObjectID;
		se.sharingSpaceName = this.sharingSpaceName;
		se.sharingSpaceID = this.sharingSpaceID;
		se.isFromShare = this.isFromShare;
		se.baselineStart = this.baselineStart;
		se.baselineEnd = this.baselineEnd;
		se.baselineWork = this.baselineWork;
		se.baselineDuration = this.baselineDuration;
		se.calculationType = this.calculationType;
        se.recordStatus = this.recordStatus;
		se.isShareReadOnly = this.isShareReadOnly;
		se.expanded = this.expanded;
		se.wbs = this.wbs;
		se.wbsLevel = this.wbsLevel;
		se.visible = this.visible;
		se.rowClass = this.rowClass;
		se.startVariance = this.startVariance;
		se.endVariance = this.endVariance;
		se.evenEntryCss = this.evenEntryCss;
		    
		for (Iterator it = this.assignments.iterator(); it.hasNext();) {
			ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
			se.assignments.addAssignment((ScheduleEntryAssignment) assignment.clone());
		}
		
		for(Iterator<MaterialAssignment> iterator = materialAssignments.getIterator(); iterator.hasNext();)
		{
			MaterialAssignment materialAssignment = iterator.next();
			se.materialAssignments.add((MaterialAssignment) materialAssignment.clone());				
		}
	}

    /**
	 * Update the properties of a schedule entry that is only a "share" of this
	 * schedule entry. Shares are actually copied, but we update them in a few
	 * places. We only copy some fields because we don't want to override things
	 * that might be different in the share, such as the name of the task and
	 * any predecessors.
	 */
    public void updateSharingProperties(ScheduleEntry se) {

        se.startTime = startTime;
        se.endTime = endTime;
        if(se.isShareReadOnly) {
        	se.actualFinish = actualFinish;
        	se.actualStart = actualStart;
        }
        
        se.duration = duration;
        
        se.work = work;
        se.workComplete = workComplete;
        se.unallocatedWorkComplete = unallocatedWorkComplete;
        se.workPercentComplete = workPercentComplete;

        se.priority = priority;
        se.isMilestone = isMilestone;
        se.deadline = deadline;

        //sjmittal: constraint now is start and end date fixed so setting this is not needed
//      se.constraintDate = startTime;
        se.calculationType = calculationType;
        se.constraintType = TaskConstraintType.START_AND_END_DATES_FIXED;
        
        se.earliestStartDate = earliestStartDate;
        se.earliestFinishDate = earliestFinishDate;
        se.latestStartDate = latestStartDate;
        se.latestFinishDate = latestFinishDate;
        
        se.criticalPath = criticalPath;
    }

    /**
     * Specifies the calculation type to use when certain properties of this
     * schedule entry are modified.
     * <p>
     * It is used when work, duration are modified or assignments are modified, added or removed.
     * </p>
     * @param calculationType the calculation type
     */
    public void setTaskCalculationType(TaskCalculationType calculationType) {
        this.calculationType = calculationType;
    }

    /**
     * Returns the current calculation type.
     * @return the calculation type
     * @see #setTaskCalculationType(net.project.schedule.calc.TaskCalculationType)
     */
    public TaskCalculationType getTaskCalculationType() {
        return this.calculationType;
    }

    public void setUnallocatedWorkComplete(TimeQuantity unallocatedWorkComplete) {
        //sjmittal: what if some work was *actually* done and work again set as 0, -ive un allocated work would go in here
//        assert unallocatedWorkComplete.compareTo(TimeQuantity.O_HOURS) >= 0 : "Unallocated work complete cannot be set to less than 0 hours.";
        this.unallocatedWorkComplete = unallocatedWorkComplete;
    }

    public TimeQuantity getUnallocatedWorkComplete() {
        return unallocatedWorkComplete;
    }

    public TimeQuantity getUnassociatedWorkComplete() {
        return unassociatedWorkComplete;
    }

    public void setUnassociatedWorkComplete(TimeQuantity unassociatedWork) {
        //sjmittal: this is taken care in the caller method for uniformity commenting this line
//        assert unassociatedWork.compareTo(TimeQuantity.O_HOURS) >= 0 : "Unassociated work cannot be set to less than 0 hours";
        this.unassociatedWorkComplete = unassociatedWork;
    }


    /**Author: Avinash Bhamare,  Date: 31st Mar 2006
       *bfd: 2997 AVINASH added function to get vCalender specific to user*/
    /**
     * Returns this Task as a vCalendar with a single vEvent entity.
     *
     * @return the VCalendar representation of this Task
     */
    public VCalendar getVCalendarForUser(ScheduleEntryAssignment assignment) {
        VCalendar vcal = new VCalendar();

        // Give it a name for attachment purposes
        vcal.setName(getName());

        // Specify the timezone property
        vcal.addProperty(new TimeZoneProperty(SessionManager.getUser().getTimeZone()));

        // Add the VEvent to the vCalendar
        //Author: Avinash Bhamare,  Date: 31st Mar 2006
        //bfd-2997 : passing parameter assignment to new method getVCalendarEntityForUser
        vcal.addEntity(getVCalendarEntityForUser(assignment));

        return vcal;
    }

    /**Author: Avinash Bhamare,  Date: 31st Mar 2006
      *bfd: 2997 AVINASH added function to get vCalender specific to user*/
    /**
     * Returns this Task as a VCalendarEntity. <b>Note:</b> A Task would
     * ideally be a vToDo.  However, since MS Outlook 2000 does not appear to
     * support vToDos, it is being returned as a vEvent.
     *
     * @return the VCalendarEntity representaiton of this Task (a vEvent)
     */
    public net.project.calendar.vcal.VCalendarEntity getVCalendarEntityForUser(ScheduleEntryAssignment assignment) {

        VEvent event = new VEvent();

        // Add the appropriate properties to the event
        event.addProperty(new SummaryProperty(getName()));
        event.addProperty(new DescriptionProperty(getDescription()));
        event.addProperty(new StartDateTimeProperty(getStartTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));

        event.addProperty(new EndDateTimeProperty(assignment.getEndTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));

        // Task should not show up as busy
        event.addProperty(new TimeTransparencyProperty(TimeTransparencyProperty.TRANSPARENT));

        return event;
    }
    
	/** 
	 * To get the name of person while showing history.
	 * @return name of person who had done modification as java.lang.String
	 */
	public String getModifiedBy() {
		return this.modifiedBy;
	}

    /**
     * @return the formattedModifiedDate
     */
    public String getFormattedModifiedDate() {
        return formattedModifiedDate;
    }

    /**
     * @param formattedModifiedDate the formattedModifiedDate to set
     */
    public void setFormattedModifiedDate(String formattedModifiedDate) {
        this.formattedModifiedDate = formattedModifiedDate;
    }

    /**
     * @return the wBS
     */
    public String getWBS() {
        return this.wbs;
    }

    /**
     * @param wbs the wBS to set
     */
    public void setWBS(String wbs) {
        this.wbs = wbs;
    }
    
    
    /**
     * @return wbsLevel
     */
    public String getWBSLevel() {
        return wbsLevel;
    }

    
    /**
     * @param value the wbsLevel to set
     */
    public void setWBSLevel(String value) {
        this.wbsLevel = value;
    }
    
    public boolean isNumSpacers() {
		return Integer.valueOf(this.hierarchyLevel) > 1;
	}
    
    public Integer getSpaceWidth(){
    	if (Integer.valueOf(this.hierarchyLevel) > 1) {
			return (Integer.valueOf(this.hierarchyLevel) - 1) * 20;
		} else {
			return null;
		}
    }
    
    public boolean isSummaryTask(){
    	return getTaskType().equals(TaskType.SUMMARY);
    }
    
    public boolean isExpanded(){
    	return this.expanded;
    }
    
    public void setExpanded(boolean expanded){
    	this.expanded = expanded;
    }
    
    public boolean isVisible(){
    	return this.visible;
    }
    
    public void setVisible(boolean visible){
    	this.visible = visible;
    }
    
    public String getSummaryTaskCss() {
		if (isSummaryTask()) {
			return "summaryTask";
		} else {
			return "";
		}
	}
    
    public String getTreeVisibilityClass(){
    	if(this.visible){
    		return "visible";
    	}else{
    		return "hidden";
    	}
    }
    
    public void setRowClass(String rowClass){
    	this.rowClass = rowClass;
    }
    
    public String getRowClass(){
    	return this.rowClass;
    }
    
    public boolean isHasAssignments(){
    	return getAssignmentList().size() > 0;
    }
    
    public boolean isHasMaterialAssignments(){
    	return getMaterialAssignmentsList().hasActive();
    }    
    
    public boolean isHasDependencies(){
    	return !getPredecessorsNoLazyLoad().isEmpty();
    }
    
    public String getDependentIds(){
    	return getPredecessorsNoLazyLoad().getDependentCSVIds();
    }
    
    public String getDependentTaskInfo(){
    	return getPredecessorsNoLazyLoad().getDependentTaskInfo();
    }
    
    public String getDateConstrainedTooltip(){
    	return PropertyProvider.get("prm.schedule.workplan.calendargraphic.taskcontsraint.tooltip") + " " 
        + getConstraintType().getName()
        + (getConstraintType().isDateConstrained() ? " (" + getConstraintDateString() + ")" : "");
    }
    
    public String getAfterdeadlineToolTip(){
    	return PropertyProvider.get("prm.schedule.list.afterdeadline.message", getDeadlineString());
    }
    
    public boolean isNameLengthMoreThan40(){
    	return this.name.length() > 40;
    }
   
	/**
	 * @return the endVariance
	 */
	public String getEndVarianceString() {
		return endVariance.toShortString(0, 2);
	}
	
	/**
	 * @return the endVarianceTQ
	 */
	public TimeQuantity getEndVarianceTQ() {
		return endVariance;
	}

	/**
	 * @param endVariance the endVariance to set
	 */
	public void setEndVariance(TimeQuantity endVariance) {
		this.endVariance = endVariance;
	}

	/**
	 * @return the startVariance
	 */
	public String getStartVarianceString() {
		return startVariance.toShortString(0, 2);
	}
	
	/**
	 * @return the startVariance
	 */
	public TimeQuantity getStartVarianceTQ() {
		return startVariance;
	}

	/**
	 * @param startVariance the startVariance to set
	 */
	public void setStartVariance(TimeQuantity startVariance) {
		this.startVariance = startVariance;
	}
	/**
	 * @param evenEntryCss the evenEntryCss to set
	 */
	public void setEvenEntryCss(String evenEntryCss) {
		this.evenEntryCss = evenEntryCss;
	}
	
	/**
	 * @return the evenEntryCss
	 */
	public String getEvenEntryCss() {
		if( evenEntryCss == null ){
			return  this.sequenceNumber % 2 == 0 ? "dashboard-even": "";
		} else {
			return evenEntryCss;
		}
	}
	
	/**
	 * Return formatted baseline work
	 * @return formatted string with the abbreaviated work units string
	 */
	public String getFormattedBaselineWork() {
		return getBaselineWork() != null ? getBaselineWork().toShortString(0, 2) : "";
	}

	/**
	 * @return the phaseSequence
	 */
	public int getPhaseSequence() {
		return phaseSequence;
	}
	
	/**
	 * details of this schedule entry object in html format.
	 * By usnig a template taskDetails.tml 
	 * @param servletContext
	 * @return <code>String</code>HTML of detaill for this object. 
	 */
	public String getDetails(ServletContext servletContext){
		return new TemplateFormatter(servletContext, "/details/template/task-detail.tml").transForm(this);
	}

	/**
	 * get outdenting status for the current schedule entry
	 * @return the unindentStatus
	 */
	public boolean isUnindentStatus() {
		return unindentStatus;
	}

	/**
	 * set the outdenting status  
	 * @param unindentStatus the unindentStatus to set
	 */
	public void setUnindentStatus(boolean unindentStatus) {
		this.unindentStatus = unindentStatus;
	}

	/**
	 * @return <code>String</code> the chargeCodeId
	 */
	public String getChargeCodeId() {
		return chargeCodeId;
	}

	/**
	 * @param chargeCodeId the chargeCodeId to set
	 */
	public void setChargeCodeId(String chargeCodeId) {
		this.chargeCodeId = chargeCodeId;
	}

	/**
	 * @return <code>String</code> the chargeCodeName
	 */
	public String getChargeCodeName() {
		return chargeCodeName;
	}

	/**
	 * @param chargeCodeName the chargeCodeName to set
	 */
	public void setChargeCodeName(String chargeCodeName) {
		this.chargeCodeName = chargeCodeName;
	}
	
	public String getDescWithHyperLinkURL(){
		//Using Wiki Model for hyperlink generation, if there is any URL in description 
		WikiModel wikiModel = new WikiModel(AddonConfiguration.DEFAULT_CONFIGURATION, null, null);
		return wikiModel.render(this.description);
	}

	public void setMaterialAssignments(MaterialAssignmentList materialAssignments) {
		this.materialAssignments = materialAssignments;
	}

}
