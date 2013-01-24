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

 package net.project.schedule.mvc.handler.taskedit;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.Task;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * Creates the view for a user to enter a new task.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class TaskCreateHandler extends AbstractTaskEditHandler {
    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     * code access to the request parameters.
     */
    public TaskCreateHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     * a view that we are going to redirect to after processing the request.
     */
    public String getViewName() {
        return super.getViewName();
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.CREATE);
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
     * @throws java.lang.Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        model.put("id", "");
        model.put("handler", "/servlet/ScheduleController/TaskEditProcessing/Create");

        ScheduleEntry scheduleEntry = getScheduleEntry(request);

        super.handleRequest(scheduleEntry, model, request);

        // Sometimes a phase to select is passed in (from Process Phase)
        // When creating a task; if there is a phaseID, then we'll set
        // this tasks's selected phase ID to that
        String preselectPhaseID = request.getParameter("phaseID");
        if (preselectPhaseID != null) {
            scheduleEntry.selectPhase(preselectPhaseID);
        }

        return model;
    }

    private ScheduleEntry getScheduleEntry(HttpServletRequest request) throws PersistenceException, SQLException {
        //Check to make sure that there isn't a scheduleEntry attribute in the
        //request
        ScheduleEntry scheduleEntry = (ScheduleEntry)request.getAttribute("scheduleEntry");
        if (scheduleEntry == null) {
            scheduleEntry = (ScheduleEntry)request.getSession().getAttribute("scheduleEntry");
        }

        //If we still haven't found a schedule entry, we will need to create one.
        if (scheduleEntry == null) {
            scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.TASK);
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        } else {
            if (Validator.isBlankOrNull((String)request.getAttribute("donterase"))) {
                if (scheduleEntry.getType().equals(TaskType.TASK)) {
                    scheduleEntry.clear();
                } else {
                    scheduleEntry = new Task();
                    request.getSession().setAttribute("scheduleEntry", scheduleEntry);
                }
            }
        }
        
        Schedule schedule = (Schedule)getSessionVar("schedule");
        // Set up default values (only when schedule is available in session)
        if(schedule == null) {
            Space currentSpace = ((User)getSessionVar("user")).getCurrentSpace();
            //Load schedule
            schedule = new Schedule();
            schedule.setSpace(currentSpace);
            schedule.load();
            setSessionVar("schedule", schedule);
        }
        IWorkingTimeCalendar workingTimeCalendar = schedule.getWorkingTimeCalendar();
        //note startTime would never be null
        Date startTime = schedule.getScheduleStartDate();
        if (workingTimeCalendar.isWorkingDay(startTime)) {
            scheduleEntry.setStartTimeD(workingTimeCalendar.getStartOfWorkingDay(startTime));
        } else {
            scheduleEntry.setStartTimeD(workingTimeCalendar.getStartOfNextWorkingDay(startTime));
        }

        Date endTime = scheduleEntry.getStartTime();
        scheduleEntry.setEndTimeD(workingTimeCalendar.getEndOfWorkingDay(endTime));
        
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        
        //If the user asked that this task be created after a given sequence number
        //make sure we put it there
        String createAfterID = request.getParameter("createAfter");
        if (!Validator.isBlankOrNull(createAfterID)) {

            ScheduleEntry neighborAbove = (ScheduleEntry)schedule.getEntryMap().get(createAfterID);
            if(neighborAbove != null){
	            scheduleEntry.setSequenceNumber(neighborAbove.getSequenceNumber()+1);
	            //Figure out what the parent task id of the task below is
	            TaskFinder tf = new TaskFinder();
	            List entries = tf.findBySequenceNumber(schedule.getID(), scheduleEntry.getSequenceNumber());
	            ScheduleEntry neighborBelow = (entries.size() != 0 ? (ScheduleEntry)entries.get(0) : null);
	
	            //Now we have to figure out which schedule entry has a greater
	            //indent level, the one above or the one below.  We will place
	            //our entry at the location with the greater depth.
	            //  Task 1            Task 1           Task 1
	            //    [Insert]        [Insert]         [Insert]
	            //    Task 2        Task 2             Task 2
	            //
	            //This information won't be available if the user didn't load
	            //the schedule in "Indented Hierarchical Format"
	            //
	            int levelAbove = getScheduleEntryHierarchyDepth(neighborAbove);
	            int levelBelow = getScheduleEntryHierarchyDepth(neighborBelow);
	
	            //Set the parent to be the parent of whichever task has a greater
	            //hierarchy depth.
	            if (levelAbove > levelBelow || levelAbove == levelBelow) {
	                scheduleEntry.setParentTaskID(neighborAbove.getParentTaskID());
	            } else {
	                scheduleEntry.setParentTaskID(neighborBelow.getParentTaskID());
	            }
            }

        }

        return scheduleEntry;
    }

    /**
     * Figure out what the hierarchy level of a task is.  We will always do this
     * from the database, because we cannot trust what is being build for the
     * task list.  (Perhaps some of the parents were filtered out of the list?)
     *
     * @param entry a <code>ScheduleEntry</code> for which we are looking up the
     * hierarchical depth.
     */
    public int getScheduleEntryHierarchyDepth(ScheduleEntry entry) throws SQLException {
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
}
