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

 package net.project.schedule.mvc.handler.main;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.channel.ScopeType;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.SummaryTask;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskPriority;
import net.project.schedule.TaskType;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * Adds a new task to a schedule.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class QuickAddHandler extends MainProcessingHandler {
    public QuickAddHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Validates security was checked for module <code>SCHEDULE</code> with
     * action <code>CREATE</code>.
     * @see net.project.base.mvc.Handler#validateSecurity for details
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(String.valueOf(Module.SCHEDULE), String.valueOf(Action.CREATE));
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        passThru(model, "module");
        model.put("action", String.valueOf(Action.VIEW));

        Schedule schedule = (Schedule)getSessionVar("schedule");

        ErrorReporter errorReporter = new ErrorReporter();
        request.getSession().setAttribute("errorReporter", errorReporter);

        //Check the validity of the fields passed
        String taskName = request.getParameter("name").trim();
        String startTimeString = request.getParameter("startTimeString");
        String endTimeString = request.getParameter("endTimeString");
        String work = request.getParameter("work");
        String workUnits = request.getParameter("workUnits");
        String[] selected = request.getParameterValues("selected");
        if(null == selected){
            selected = StringUtils.split((String)request.getParameter("selectedIds"),',');
        }

        //Get the information from the request so we can populate the source page if
        //there is an error.
        errorReporter.clear();
        errorReporter.populateFromRequest(request);

        validateParameters(errorReporter, taskName, startTimeString, endTimeString, work, selected);

        if (errorReporter.errorsFound()) {
            errorReporter.setOverallError(PropertyProvider.get("prm.schedule.quickadd.work.errorsfound.message"));

        } else {
            TimeQuantity newWork = TimeQuantity.parse(work, workUnits);

            // Construct the schedule entry for the specified dates (if specified)
            ScheduleEntry scheduleEntry = createScheduleEntry(startTimeString, endTimeString, newWork, schedule);

            //Determine where in the schedule we want to insert this new task
            if (selected != null && selected.length > 0) {

                ScheduleEntry neighborAbove = (ScheduleEntry)schedule.getEntryMap().get(selected[0]);
                PersonProperty property = PersonProperty.getFromSession(request.getSession());
                property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
                property.prefetchForContextPrefix("prm.schedule.main");
                String[] expanded = property.get("prm.schedule.main", "node" + neighborAbove.getID() + "expanded");
                boolean isNeighborAboveExpanded = true;
                if(expanded != null && expanded.length > 0)
                    isNeighborAboveExpanded = Boolean.parseBoolean(expanded[0]);
                addScheduleEntryBelow(schedule, scheduleEntry, neighborAbove, isNeighborAboveExpanded);
            }

            scheduleEntry.setName(taskName);
            scheduleEntry.setPlanID(schedule.getID());
            scheduleEntry.setPriority(TaskPriority.PRIORITY_NORMAL.getID());
            scheduleEntry.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
            scheduleEntry.setIgnoreTimePortionOfDate(true);
            scheduleEntry.setTaskCalculationType(schedule.getDefaultTaskCalculationType());

            // Update work and recalculate duration as appropriate
            if (schedule.isAutocalculateTaskEndpoints()) {
                // For autocalculate, duration is based on the work
                ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
                calc.workChanged(newWork);

            } else {
                // For non-autocalculate, duration is always 1 day
                scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
            }

            scheduleEntry.store(true, schedule);
        }


        return model;
    }

    /**
	 * This method figures out the parent id of the scheduleEntry based on the neighborAbove.
	 *  It does this in the following way:
	 * 
	 * It figures out which schedule entry has a greater indent level, the one above or the one below. 
	 * It then places scheduleEntry at the location with the greater depth. 
	 *   Task 1            Task 1           Task 1
	 *     [Insert]        [Insert]         [Insert]
	 *     Task 2        Task 2             Task 2
	 * 
	 * This information won't be available if the user didn't load the schedule in "Indented Hierarchical Format"
	 * 
	 * 
	 * @param schedule the <code>Schedule</code> in which we are adding this new <code>ScheduleEntry</code>
     * @param scheduleEntry the <code>ScheduleEntry</code> that we intend to add 
     * @param neighborAbove the <code>ScheduleEntry</code> below which we intend to add this new entry
     * @param isNeighborAboveExpanded the <code>boolean</code> to state if above entry is in expanded state
	 * @throws PersistenceException any Excpetion that occurs
	 * @throws SQLException any Excpetion that occurs
	 */
	public static void addScheduleEntryBelow(Schedule schedule, ScheduleEntry scheduleEntry, ScheduleEntry neighborAbove, boolean isNeighborAboveExpanded) throws PersistenceException, SQLException {
        if(isNeighborAboveExpanded)
            scheduleEntry.setSequenceNumber(neighborAbove.getSequenceNumber()+1);
        else
            scheduleEntry.setSequenceNumber(neighborAbove.getSequenceNumber()+((SummaryTask)neighborAbove).getChildren().size()+1);

		//Figure out what the parent task id of the task below is
		TaskFinder tf = new TaskFinder();
		List entries = tf.findBySequenceNumber(schedule.getID(), scheduleEntry.getSequenceNumber());
		ScheduleEntry neighborBelow = (entries.size() != 0 ? (ScheduleEntry)entries.get(0) : null);


		int levelAbove = getScheduleEntryHierarchyDepth(neighborAbove);
		int levelBelow = getScheduleEntryHierarchyDepth(neighborBelow);

		//Set the parent to be the parent of whichever task has a greater
		//hierarchy depth.
		if (levelAbove > levelBelow || levelAbove == levelBelow || !isNeighborAboveExpanded) {
		    scheduleEntry.setParentTaskID(neighborAbove.getParentTaskID());
		} else {
		    scheduleEntry.setParentTaskID(neighborBelow.getParentTaskID());
		}
	}

	/**
     * Figure out what the hierarchy level of a task is.  We will always do this
     * from the database, because we cannot trust what is being build for the
     * task list.  (Perhaps some of the parents were filtered out of the list?)
     *
     * @param entry a <code>ScheduleEntry</code> for which we are looking up the
     * hierarchical depth.
     */
    public static int getScheduleEntryHierarchyDepth(ScheduleEntry entry) throws SQLException {
        int depth = 0;

        if (entry != null) {
            DBBean db = new DBBean();
            try {
                db.prepareStatement(
                    "select "+
                    "  count(*)-1 as depth "+
                    "from "+
                    "  pn_task t "+
                    "where "+
                    "  t.record_status = 'A' "+
                    "connect by "+
                    "  task_id = prior parent_task_id "+
                    "start with "+
                    "  task_id = ? "
                );
                db.pstmt.setString(1, entry.getID());
                db.executePrepared();

                if (db.result.next()) {
                    depth = db.result.getInt("depth");
                }
            } finally {
                db.release();
            }
        }

        return depth;
    }

    /**
     * Creates a schedule entry of type TASK given the specified start and end times.
     * <p>
     * If either time is null, the current time is used.  A Milestone is created if work is zero
     * and autocalculate is true, or the start and end time are equal.  The work is set.
     * </p>
     * @param startTimeString the start time formatted for the user's locale; it will be parsed (if not null)
     * @param endTimeString the end time formatted for the user's locale; it will be parsed (if not null)
     * @return the schedule entry with start and end date ste
     */
    public static ScheduleEntry createScheduleEntry(String startTimeString, String endTimeString, TimeQuantity work, Schedule schedule) {

        Date startDate;
        Date endDate;
        boolean isMilestone;

        DateFormat df = SessionManager.getUser().getDateFormatter();

        IWorkingTimeCalendar workingTimeCalendar = schedule.getWorkingTimeCalendar();
        if (Validator.isBlankOrNull(startTimeString)) {
            // Previous validation ensured that this will only occur for autocalculate schedules
            // In this case, we'll use schedule start date
            // We need the date to allow other things such as duration calculation to occur
            // correctly
            startDate = schedule.getScheduleStartDate();
        } else {
            // We have an entered date; parse it
            try {
                startDate = df.parseDateString(startTimeString);
                // We have an entered date; parse it and please set the correct start time
                // sjmittal fix for bfd 2495
                startDate = df.parseDateString(startTimeString);
                try {
                    startDate = workingTimeCalendar.ensureWorkingTimeStart(startDate);
                }
                catch (NoWorkingTimeException e1) {
                }
            } catch (InvalidDateException e) {
                //This value was already validated in the "validateParameters()"
                //method, so it shouldn't be possible for it not to parse.
                throw ((IllegalArgumentException)new IllegalArgumentException(
                    "The start time passed into this method was not able to be " +
                    "parsed by the parseDateString() function, even though it " +
                    "was correclty parsed by validation.").initCause(e));
            }
        }

        if (Validator.isBlankOrNull(endTimeString)) {
            // Autocalculate date; use start date
            endDate = startDate;

        } else {
            // We have an entered date; parse it and please set the correct end time
            // sjmittal fix for bfd 2495
            try {
                endDate = df.parseDateString(endTimeString);
                try {
                    endDate = workingTimeCalendar.ensureWorkingTimeStart(endDate);
                    //sjmittal: add one minute to it and get to the end of the day
                    GregorianCalendar cal = new GregorianCalendar(schedule.getTimeZone());
                    cal.setTime(endDate);
                    cal.add(Calendar.MINUTE,1);
                    endDate = workingTimeCalendar.getEndOfWorkingDay(cal.getTime());
                }
                catch (NoWorkingTimeException e1) {
                }
            } catch (InvalidDateException e) {
                //This value was already validated in the "validateParameters()"
                //method, so it shouldn't be possible for it not to parse.
                throw ((IllegalArgumentException)new IllegalArgumentException(
                    "The start time passed into this method was not able to be " +
                    "parsed by the parseDateString() function, even though it " +
                    "was correclty parsed by validation.").initCause(e));
            }
        }

        // Construct the Task entry
        ScheduleEntry scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.TASK);
        scheduleEntry.setWork(work);
        scheduleEntry.setStartTimeD(startDate);
        scheduleEntry.setEndTimeD(endDate);

        // We create a milestone if work is zero and autocaclulate is true or work is zero and the start and end time
        // are the same
        isMilestone = (work.isZero() && (schedule.isAutocalculateTaskEndpoints() || (scheduleEntry.getStartTime().equals(scheduleEntry.getEndTime()))));
        scheduleEntry.setMilestone(isMilestone);

        return scheduleEntry;
    }

    private void validateParameters(ErrorReporter errorReporter, String taskName, String startTimeString, String endTimeString, String work, String[] selectedValues) {

        Date startDate = null;
        Date endDate = null;

        if (Validator.isBlankOrNull(taskName)) {
            errorReporter.addError(new ErrorDescription("name", PropertyProvider.get("prm.schedule.quickadd.taskname.requiredfield.message")));
        }

        if (!schedule.isAutocalculateTaskEndpoints()) {
            if (Validator.isBlankOrNull(startTimeString)) {
                errorReporter.addError(new ErrorDescription("startTimeString", PropertyProvider.get("prm.schedule.quickadd.startdate.requiredfield.message")));
            } else {
                try {
                    startDate = DateFormat.getInstance().parseDateString(startTimeString);
                } catch (InvalidDateException e) {
                    errorReporter.addError(new ErrorDescription("startTimeString", PropertyProvider.get("prm.schedule.quickadd.startdate.validdate.message")));
                }
            }

            if (Validator.isBlankOrNull(endTimeString)) {
                errorReporter.addError(new ErrorDescription("endTimeString", PropertyProvider.get("prm.schedule.quickadd.enddate.requiredfield.message")));
            } else {
                try {
                    endDate = DateFormat.getInstance().parseDateString(endTimeString);
                } catch (InvalidDateException e) {
                    errorReporter.addError(new ErrorDescription("endTimeString", PropertyProvider.get("prm.schedule.quickadd.enddate.validdate.message")));
                }
            }

            if (startDate != null && endDate != null && endDate.before(startDate)) {
                errorReporter.addError(new ErrorDescription("startTimeString", PropertyProvider.get("prm.schedule.quickadd.dates.endbeforestart.message")));
            }
        }

        if (Validator.isBlankOrNull(work)) {
            errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.quickadd.work.required.message")));
        } else {
            if (!Validator.isNumeric(work)) {
                errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.quickadd.work.validnumber.message")));
            } else if(Validator.isNegative(work)) {
                errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.quickadd.work.negative.message")));
            }
        }

        if (selectedValues != null && selectedValues.length > 1) {
            errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.schedule.main.verifyselection.oneorzero.message")));
        }
    }
}
