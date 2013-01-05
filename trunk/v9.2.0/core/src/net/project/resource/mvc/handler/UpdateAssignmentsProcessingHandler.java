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

 package net.project.resource.mvc.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.blog.AddWebLogEntryHelper;
import net.project.calendar.CalendarBean;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.database.DBBean;
import net.project.form.assignment.FormAssignment;
import net.project.persistence.PersistenceException;
import net.project.resource.ActivityAssignment;
import net.project.resource.Assignment;
import net.project.resource.AssignmentStatus;
import net.project.resource.AssignmentWorkLogDAO;
import net.project.resource.AssignmentWorkLogEntry;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryDateCalculator;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.TaskFinder;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.DateUtils;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.lang.StringUtils;

/**
 * Handler for the submission of the UpdateAssignments.jsp page.  This will
 * update the database with completion for various tasks.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class UpdateAssignmentsProcessingHandler extends Handler {
    /**
     * The largest amount of work that may be entered for any given day is 24 hours.
     * Allowing users to enter more than this may cause other problems with allocation calculations.
     */
    public static final TimeQuantity MAXIMUM_WORK_HOURS = new TimeQuantity(new BigDecimal("24"), TimeQuantityUnit.HOUR);

    /** PlanID to resource calendar mapping. */
    private ResourceWorkingTimeCalendarProvider calendarProvider;
    /** Map of object ID to plan ID's. */
    private Map planIDMap;
    /** Map of object ID's to assignments. */
    private Map assignmentMap;
    /** View that this handler forwards to when it's done. */
    private String view = "/personal/Main.jsp";
    /** To find the task for the objects selected. */
    private TaskFinder finder = new TaskFinder();

    /** Collection of any errors found while processing the request. */
    protected ErrorReporter errors;
    
    private List assignmentWorkId;
    
    private String[] commentArray;
    
    public UpdateAssignmentsProcessingHandler(HttpServletRequest request) {
        super(request);
        calendarProvider = (ResourceWorkingTimeCalendarProvider)request.getSession().getAttribute("updateAssignmentsCalendarProvider");
        planIDMap = (Map)request.getSession().getAttribute("updateAssignmentsPlanIDMap");
        assignmentMap = (Map)request.getSession().getAttribute("updateAssignmentsMap");
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the
     *         request.
     */
    public String getViewName() {
        return view;
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just consist
     * of verifying that the parameters that were used to access this page were
     * correct (that is, that the requester didn't try to "spoof it" by using a
     * module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that was
     * passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user
     * didn't have the proper credentials to view this page, or if they tried to
     * spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(module, action, objectID);
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
        errors = new ErrorReporter();
        
        String mutlitpleWorkSubmitted = request.getParameter("multipleWorkSubmitted");
        if(StringUtils.isNotEmpty(mutlitpleWorkSubmitted)) {
        	commentArray = mutlitpleWorkSubmitted.split(",");
        }
        
        processUserChanges(model, request);
        if(PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled") && CollectionUtils.isNotEmpty(assignmentWorkId)){
	        AddWebLogEntryHelper.saveMultpleWrokSubmittedBlogEntry(request.getParameter("objectId"), request.getParameter("subject"), 
	        		request.getParameter("isImportant"),  request.getParameter("spaceId"), commentArray, request.getParameter("taskIdArray"), 
	        		request.getParameter("spaceIdArray"), assignmentWorkId, request.getParameter("content"));
        }

        String returnTo = request.getParameter("returnTo");
        if (!Validator.isBlankOrNull(returnTo) && !"null".equals(returnTo)) {
            view = returnTo;
            String returnTo2 = request.getParameter("returnTo2");
            if (!Validator.isBlankOrNull(returnTo2)) {
                model.put("returnTo", returnTo2);
            }
        } else {
            model.put("module", String.valueOf(Module.PERSONAL_SPACE));
            model.put("action", String.valueOf(Action.VIEW));
        }

        if (errors.errorsFound()) {
            errors.populateFromRequest(request);
            passThruAll(model);
            model.put("errorReporter", errors);

            view = "/servlet/AssignmentController/CurrentAssignments/Update";
        }
        Long startDate = DateFormat.getInstance().parseDateString(DateFormat.getInstance().formatDate((java.util.Date)request.getSession().getAttribute("startOfWeek"))).getTime();
        String personId = StringUtils.isNotEmpty(request.getParameter("personId")) ? request.getParameter("personId") : SessionManager.getUser().getID();
        String assignmentStatus = request.getParameter("assignmentStatus");
        boolean isEditMode = PropertyProvider.getBoolean("prm.resource.timesheet.editmode.isenabled");
        boolean enableBlogsRightTab = Boolean.valueOf(request.getParameter("enableBlogsRightTab")).booleanValue();
        boolean isActualWork = Boolean.valueOf(request.getParameter("isActualWork")).booleanValue();
        boolean isPercentComplete = Boolean.valueOf(request.getParameter("isPercentComplete")).booleanValue();
        //Decide redirection
        if(PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled") && PropertyProvider.getBoolean("prm.resource.timesheet.monthlyview.isenabled")) {
        	CalendarBean calendarBean = new CalendarBean();
        	calendarBean.setTimeZone(SessionManager.getUser().getTimeZone());
        	String requestDate = calendarBean.formatDateAs((java.util.Date)request.getSession().getAttribute("startOfWeek"), "MMddyyyy");
        	response.sendRedirect(SessionManager.getJSPRootURL() + "/timesheet/CalendarTimeSheet?DisplayDate="+requestDate);
        } 
        return model;
    }

    /**
     * Store the amount of time the user reported for each assignment.
     *
     * @param request a <code>HttpServletRequest</code> object that is going to
     * be used to figure out how much time the user reported.
     * @throws PersistenceException
     */
    private void processUserChanges(Map model, HttpServletRequest request) throws PersistenceException {
        Map scheduleCache = new HashMap();
        Map taskCache = new HashMap();
        Set tasksToStore = new HashSet();
        Set workLogsToStore = new HashSet();
        
        try{
	        processWorkAssigned(request, taskCache, tasksToStore);
	        TimeQuantity totalWork = createWorkLog(request, taskCache, scheduleCache, tasksToStore, workLogsToStore);
	        storeUserChanges(workLogsToStore, tasksToStore, scheduleCache);
	        
	        //add the pervious work performed in the week to total work
	        Map summaryDateValues = (Map)request.getSession().getAttribute("summaryDateValues");
	        Iterator workValues = summaryDateValues.values().iterator();
	        while(workValues.hasNext()) {
	            TimeQuantity summayWorkQuantity = (TimeQuantity) workValues. next();
	            totalWork = totalWork.add(summayWorkQuantity);
	        }
	        model.put("totalWorkLogged", totalWork);
        } catch (Exception e) {
			errors.addError(new ErrorDescription("Error Occured while getting the work log information"));
		}
        
    }

    private void storeUserChanges(Set workLogsToStore, Set tasksToStore, Map scheduleCache) throws PersistenceException {
        if (!errors.errorsFound()) {
            DBBean db = new DBBean();
            try {
                db.setAutoCommit(false);

                //Store the work logs that need to be stored
                assignmentWorkId = new ArrayList<Integer>();
                for (Iterator it = workLogsToStore.iterator(); it.hasNext();) {
                    List workLogs = (List) it.next();
                    assignmentWorkId.add(new AssignmentWorkLogDAO().store(workLogs, db));
                }
                
                // Converting Set tasksToStore to scheduleEntryToStoreArray array.
                // which will use in next loop to decide that schedule is to recalculate or not. 
                Object [] scheduleEntryToStoreArray = tasksToStore.toArray();

                //Store the tasks that need to be stored
                for(int index = 0 ; index< scheduleEntryToStoreArray.length ; index++){
                	ScheduleEntry entry = (ScheduleEntry) scheduleEntryToStoreArray[index];

                    //Find the schedule related to the schedule entry for this assignment
                    String planID = (String)planIDMap.get(entry.getID());
                    Schedule schedule = findSchedule(scheduleCache, planID);
                    IWorkingTimeCalendarProvider provider = schedule.getWorkingTimeCalendarProvider();

                    if (!entry.getAssignmentList().isEmpty()) {
                        //sjmittal: for fix of bfd 4938
                        //why duration needs to be re computed when capturing work!!
//                        entry.calculateDuration(provider);
                        //sjmittal fix for bfd 3103
                        //please set the actual end date also
                		if (entry.getWorkPercentComplete().toString().compareTo("100%") == 0) {
                			Date endDate = entry.getResourceCalendar(provider).getEndOfWorkingDay(new Date());
                			endDate = DateUtils.max(endDate, entry.getEndTime());
                			entry.setActualEndTimeD(endDate);
                			// duration needs to be recalculated when work percent complete changed to 100 %
                            entry.calculateDuration(provider);
                		}
                    }
                    entry.store(true, schedule, db);
                    // This is to recalculate task assignment start date after task get work captured.
                    // Since recalculation will not effective until task stored schedule entry is committed and reloaded.
                    // Commit stored schedule entry and recalculate schedule.
                    // This condition is applied to avoid same schedue reloadnig and recalculation multiple time.
                    // In last iteration this conditon will be true so db.commit() will definitely execute.
                    if(scheduleEntryToStoreArray.length - 1 == index
                        || !planIDMap.get(entry.getID()).equals(planIDMap.get(((ScheduleEntry) scheduleEntryToStoreArray[index + 1]).getID()))){
                        db.commit();
                        schedule.reloadIfNeeded();
                        schedule.recalculateTaskTimes();
                    }
                }

                db.commit();
            } catch (SQLException sqle) {
                try {
                    db.rollback();
                } catch (SQLException e) {
                    // Pass on original SQLException
                }
                throw new PersistenceException("Unexpected SQLException", sqle);
            } finally {
                db.release();
            }
        }
    }

    /**
     * Helper method to make sure that a task isn't loaded twice before we store
     * it.
     *
     * @param taskCache a <code>Map</code> of tasks that we have already loaded
     * during this update.
     * @param taskID a <code>String</code> containing the id of a task that we
     * wish to find.
     * @return a <code>ScheduleEntry</code> which matches the task id provided
     * to this method.
     * @throws PersistenceException if there is an error loading the task id.
     */
    private ScheduleEntry findTask(Map taskCache, String taskID) throws PersistenceException {
        ScheduleEntry entry = (ScheduleEntry)taskCache.get(taskID);

        if (entry == null) {
            entry = finder.findObjectByID(taskID, false, true);
            taskCache.put(taskID, entry);
        }

        return entry;
    }

    private TimeQuantity createWorkLog(HttpServletRequest request, Map taskCache, Map scheduleCache, Set tasksToStore, Set workLogsToStore) throws PersistenceException {
        //Collect all the dates, work, and id's.
        MultiMap map = parseWorkLogInfoFromRequest(request);

        //get the total work logged for timesheet total work
        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        //Iterate through the work log entries so we can save the entries
        //and update the task
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String taskID = (String) it.next();
            List rawLogEntries = (List)map.get(taskID);
            Collections.sort(rawLogEntries);

            //Update the assignment with the work that has been done
            Assignment assignment = (Assignment)assignmentMap.get(taskID);
            if (assignment instanceof ScheduleEntryAssignment) {
                ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;

                WorkLogSummary summary = createWorkLogEntries(rawLogEntries, seAssignment, taskID, workLogsToStore);
                totalWork = totalWork.add(summary.newWorkReported);

                // Now find the schedule entry for the assignment
                // We have to load all assignments since we're going to be adding one
                // and recalculating duration
                ScheduleEntry entry = findTask(taskCache, taskID);

                //Update up the actual start and estimated finish, if needed
                if (seAssignment.getActualStart() == null || seAssignment.getActualStart().after(summary.earliestWork)) {
                    seAssignment.setActualStart(summary.earliestWork);
                    WorkingTimeCalendarDefinition def = calendarProvider.getForPlanID((String)planIDMap.get(taskID));
                    seAssignment.calculateEstimatedFinish(def, calendarProvider.getDefaultTimeZone());

                    Schedule schedule = findSchedule(scheduleCache, entry.getPlanID());
                    IWorkingTimeCalendarProvider provider = schedule.getWorkingTimeCalendarProvider();
                    ScheduleEntryDateCalculator calc = new ScheduleEntryDateCalculator(entry, provider);
                    calc.addWorkAndupdateAssignmentDates(summary.earliestWork);
                }
                if(!Validator.isBlankOrNull(request.getParameter("pc"))){
                	seAssignment.setWork(new TimeQuantity(new BigDecimal(request.getParameter("wkUpdate"+taskID)), TimeQuantityUnit.HOUR));
                	seAssignment.setWorkComplete(new TimeQuantity(new BigDecimal(request.getParameter("wkUpdate"+taskID)), TimeQuantityUnit.HOUR));
                } else {
                	seAssignment.setWorkComplete(seAssignment.getWorkComplete().add(summary.newWorkReported));
                }

                if (entry.getAssignmentList().containsForResource(seAssignment)) {
                    //sjmittal: only add the work complete of the assignment is assigned to that task

                    TimeQuantityUnit unallocatedWorkUnit = entry.getUnallocatedWorkComplete().getUnits();
                    
                    //get the new work un allocated
                    if(summary.newWorkReported.compareTo(entry.getUnallocatedWorkComplete()) > 0) {
                        //if new work > un allocated work 
                        //then new work to be added to total work complete - un allocated work
                        TimeQuantity workUnallocated = entry.getWorkCompleteTQ().subtract(entry.getUnallocatedWorkComplete());
                        entry.setWorkComplete(workUnallocated.add(summary.newWorkReported));                        
                        //set un allocated work to be zero
                        entry.setUnallocatedWorkComplete(ScheduleTimeQuantity.convertToUnit(TimeQuantity.O_HOURS, unallocatedWorkUnit, 3, BigDecimal.ROUND_HALF_UP));
                    } else if(summary.newWorkReported.compareTo(TimeQuantity.O_HOURS) > 0) {
                        //if new work is +ive but less then the un allocated work
                        //redeuce the un allocated work by new work
                        TimeQuantity workUnallocated = entry.getUnallocatedWorkComplete().subtract(summary.newWorkReported);                        
                        entry.setUnallocatedWorkComplete(workUnallocated);
                        //don't change the total work comple
                    } else {
                        //if new work is -ive reduce the total work by new work
                        entry.setWorkComplete(entry.getWorkCompleteTQ().add(summary.newWorkReported));
                        // don't change the un allocated work
                    }
                    
                }

                //Check the actual start date of the task.  If any of the reported
                //dates are before the current actual start date, then change it
                Date earliestDate = entry.getActualStartTime();
                for (Iterator it2 = rawLogEntries.iterator(); it2.hasNext();) {
                    DateUpdate dateUpdate = (DateUpdate) it2.next();
                    if (earliestDate == null) {
                        earliestDate = dateUpdate.workStart;
                    } else {
                        earliestDate = DateUtils.min(earliestDate, dateUpdate.workStart);
                    }
                }
                if (earliestDate != null) {
                    entry.setActualStartTimeD(earliestDate);
                }

                tasksToStore.add(entry);
            } else if (assignment instanceof FormAssignment) {
                FormAssignment fAssignment = (FormAssignment) assignment;
                
                WorkLogSummary summary = createWorkLogEntries(request, rawLogEntries, fAssignment, taskID, workLogsToStore);
                totalWork = totalWork.add(summary.newWorkReported);
            } else if (assignment instanceof ActivityAssignment) {
                ActivityAssignment aAssignment = (ActivityAssignment) assignment;
                
                WorkLogSummary summary = createWorkLogEntries(request, rawLogEntries, aAssignment, taskID, workLogsToStore);
                totalWork = totalWork.add(summary.newWorkReported);
            }
        }
        
        return totalWork;
    }
    
    private WorkLogSummary createWorkLogEntries(HttpServletRequest request, List values, ActivityAssignment assignment, String id, Set workLogsToStore) throws PersistenceException {
        Date earliestStartDate = null;
        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        String personID = SessionManager.getUser().getID();

        //store the new adhoc activity created in memory in the db first
        if(!assignment.isLoaded()) {
            String name = request.getParameter("activityId" + id);
            if(Validator.isBlankOrNull(name)) {
                errors.addError(new ErrorDescription(PropertyProvider.get("prm.resource.timesheet.javascript.name.alert.message")));
            } else {
                //sjmittal: if this is successful then we need to populate
                //some of the needed fields for the assignment too
                //this method should set the object id for activity
                assignment.setName(name);
                assignment.setPersonID(personID);
                assignment.setSpaceID(personID);
                assignment.store();
    
                //activity is by default accepted, 
                //person assigned is always the primary owner
                //and <x> percent assigned
                assignment.setStatus(AssignmentStatus.ACCEPTED);
                assignment.setPercentAssigned(100); //sjmittal TODO:
                assignment.setPrimaryOwner(true);
            }
        } else {
            //store the assignment here as in 
            // storeUserChanges we store the worklog entries and
            // tasks and its assignments
            assignment.store();
        }
        
        //Update the work log with the work that has been done
        List workLogs = new LinkedList();
        for (Iterator it2 = values.iterator(); it2.hasNext();) {
            DateUpdate dateUpdate = (DateUpdate) it2.next();

            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
                earliestStartDate = dateUpdate.workStart;
            }

            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
            workLog.setAssigneeID(personID);
            
            workLog.setObjectID(assignment.getObjectID());
            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
            workLog.setHoursWorked(dateUpdate.workAmount);
            workLog.setComment(dateUpdate.comment);
            workLog.setRemainingWork(TimeQuantity.O_HOURS);
            workLog.setModifiedByID(SessionManager.getUser().getID());
            workLog.setScheduledWork(dateUpdate.workAmount);

            // Figure out percent complete
            // Since an unplanned activity they're 100% 
            BigDecimal currentPercentComplete = new BigDecimal("1.00");;
            workLog.setPercentComplete(currentPercentComplete);

            workLogs.add(workLog);

            //Keep track of the new work reported
            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
        }

        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
        workLogsToStore.add(workLogs);
        return summary;
    }
    
    private WorkLogSummary createWorkLogEntries(HttpServletRequest request, List values, FormAssignment assignment, String id, Set workLogsToStore) throws PersistenceException {
        Date earliestStartDate = null;
        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        String personID = SessionManager.getUser().getID();
        
        TimeQuantity cumulativeWorkDelta = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        
        //Update the work log with the work that has been done
        List workLogs = new LinkedList();
        for (Iterator it2 = values.iterator(); it2.hasNext();) {
        	 DateUpdate dateUpdate = (DateUpdate) it2.next();
        	
            cumulativeWorkDelta = cumulativeWorkDelta.add(dateUpdate.workAmount);
            TimeQuantity currentWorkComplete = assignment.getWorkComplete().add(cumulativeWorkDelta);
            TimeQuantity remainingWork = assignment.getWork().subtract(currentWorkComplete);        	
        	           

            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
                earliestStartDate = dateUpdate.workStart;
            }

            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
            workLog.setAssigneeID(personID);
            workLog.setObjectID(assignment.getObjectID());
            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
            workLog.setHoursWorked(dateUpdate.workAmount);
            workLog.setComment(dateUpdate.comment);
            workLog.setRemainingWork(remainingWork);
            workLog.setModifiedByID(SessionManager.getUser().getID());
            workLog.setScheduledWork(assignment.getWork());
                
            //Figure out percent complete
            BigDecimal currentPercentComplete;
            if (assignment.getWork().isZero()) {
                // Currently has no work; then they're 100% complete because they've done
                // some work.  This occurs when adding a new assignment while updating work complete
                currentPercentComplete = new BigDecimal("1.00");
            } else {
                // Currently has work; percent complete is ratio of work complete to work, at most 100%
                currentPercentComplete = currentWorkComplete.divide(assignment.getWork(), 5, BigDecimal.ROUND_HALF_UP);
                currentPercentComplete = currentPercentComplete.min(new BigDecimal("1.00"));
            }
            workLog.setPercentComplete(currentPercentComplete);

            workLogs.add(workLog);

            //Keep track of the new work reported
            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
        }

        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
        workLogsToStore.add(workLogs);
        
        assignment.setWorkComplete(assignment.getWorkComplete().add(summary.newWorkReported));
        //store the assignment here as in 
        // storeUserChanges we store the worklog entries and
        // tasks and its assignments
        assignment.store();
        
        return summary;
    }

    private WorkLogSummary createWorkLogEntries(List values, ScheduleEntryAssignment assignment, String id, Set workLogsToStore) {
        Date earliestStartDate = null;
        TimeQuantity newWorkReported = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        TimeQuantity cumulativeWorkDelta = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        String personID = SessionManager.getUser().getID();

        //Update the work log with the work that has been done
        List workLogs = new LinkedList();
        for (Iterator it2 = values.iterator(); it2.hasNext();) {
            DateUpdate dateUpdate = (DateUpdate) it2.next();

            cumulativeWorkDelta = cumulativeWorkDelta.add(dateUpdate.workAmount);
            TimeQuantity currentWorkComplete = assignment.getWorkComplete().add(cumulativeWorkDelta);
            TimeQuantity remainingWork = assignment.getWork().subtract(currentWorkComplete);

            if (earliestStartDate == null || earliestStartDate.after(dateUpdate.workStart)) {
                earliestStartDate = dateUpdate.workStart;
            }

            AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
            String userID = request.getParameter("personId");
            workLog.setAssigneeID((StringUtils.isNotEmpty(userID) && !"null".equals(userID)) ? request.getParameter("personId") : personID);
            workLog.setObjectID(id);
            workLog.setDatesWorked(new DateRange(dateUpdate.workStart, dateUpdate.workEnd));
            workLog.setHoursWorked(dateUpdate.workAmount);
            workLog.setComment(dateUpdate.comment);
            workLog.setRemainingWork(remainingWork);
            workLog.setModifiedByID((StringUtils.isNotEmpty(userID) && !"null".equals(userID)) ? request.getParameter("personId") : personID);
            workLog.setScheduledWork(assignment.getWork());
            workLog.setLogDate(dateUpdate.workStart);

            // Figure out percent complete
            BigDecimal currentPercentComplete;
            if (assignment.getWork().isZero()) {
                // Currently has no work; then they're 100% complete because they've done
                // some work.  This occurs when adding a new assignment while updating work complete
                currentPercentComplete = new BigDecimal("1.00");
            } else {
                // Currently has work; percent complete is ratio of work complete to work, at most 100%
                currentPercentComplete = currentWorkComplete.divide(assignment.getWork(), 5, BigDecimal.ROUND_HALF_UP);
                currentPercentComplete = currentPercentComplete.min(new BigDecimal("1.00"));
            }
            workLog.setPercentComplete(currentPercentComplete);

            workLogs.add(workLog);

            //Keep track of the new work reported
            newWorkReported = newWorkReported.add(dateUpdate.workAmount);
        }

        WorkLogSummary summary = new WorkLogSummary(newWorkReported, earliestStartDate);
        workLogsToStore.add(workLogs);
        return summary;
    }

    private Schedule findSchedule(Map scheduleCache, String planID) throws PersistenceException {
        Schedule schedule = (Schedule)scheduleCache.get(planID);
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setID(planID);
            schedule.load();
            scheduleCache.put(planID, schedule);
        }
        return schedule;
    }

    private void processWorkAssigned(HttpServletRequest request, Map taskCache, Set tasksToStore) throws PersistenceException {
        //Iterate through the work changes and make sure that all tasks
        //now have the correct amount of work assigned to them.
        Map workChanges = collectWorkChanges(request);

        for (Iterator it = workChanges.keySet().iterator(); it.hasNext(); ) {
            String taskID = (String)it.next();
            Assignment assignment = (Assignment)assignmentMap.get(taskID);
            if (assignment instanceof ScheduleEntryAssignment) {
                ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;

                ScheduleEntry entry = findTask(taskCache, taskID);
                String workUpdateString = request.getParameter("wkUpdate"+taskID);
                
                // Calculate Fixed duration assignments assigned percerntage
                TaskCalculationType calcType = entry.getTaskCalculationType();
                if (calcType.isFixedDuration()) {
	                BigDecimal percentAssignedDecimal = seAssignment.getPercentAssignedDecimal();
	                // Now calculate percentage based on the ratio of their new work to old work
	                BigDecimal newPercentAssigned = percentAssignedDecimal.multiply(new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR).divide(seAssignment.getWork(), 3, BigDecimal.ROUND_HALF_UP));
	                seAssignment.setPercentAssignedDecimal(newPercentAssigned);
                }
                
                //Make sure there wasn't an update to the amount of work for the assignment.
                
                TimeQuantity newWork, workDelta = null;
                if (!Validator.isBlankOrNull(workUpdateString)) {
                    newWork = new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR);
                    workDelta = newWork.subtract(ScheduleTimeQuantity.convertToHour(seAssignment.getWork()));
                    seAssignment.setWork(newWork);
                }

                //Only store an assignment if there really was one.
                if (entry.getAssignmentList().containsForResource(seAssignment)) {
                    //Make sure that the scheduleEntryAssignment has our copy of the
                    //assignment.  It has all of the important update.
                    entry.getAssignmentList().replaceAssignment(seAssignment);

                    if (workDelta != null) {
                        //sjmittal: add the delta work to the tasks work since the task is assigned
                        entry.setWork(entry.getWorkTQ().add(workDelta));
                    }

                    //Storing the task will automatically store the assignment
                    tasksToStore.add(entry);
                } else if (workDelta != null) {
                    //sjmittal: add it to unassocitated work since the task is not assigned
                    //and there is some delta work
                    entry.setUnassociatedWorkComplete(entry.getUnassociatedWorkComplete().add(workDelta));

                    tasksToStore.add(entry);
                }

//              if (workDelta != null) {
//              entry.setWork(entry.getWorkTQ().add(workDelta));
//              tasksToStore.add(entry);
//              }
            } else if (assignment instanceof FormAssignment) {
                FormAssignment fAssignment = (FormAssignment) assignment;
                
                String workUpdateString = request.getParameter("wkUpdate"+taskID);
                TimeQuantity newWork;
                if (!Validator.isBlankOrNull(workUpdateString)) {
                    newWork = new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR);
                    fAssignment.setWork(newWork);
                    //store any work changes here as you may not get a chance to do that later
                    fAssignment.store();
                }
            } else if (assignment instanceof ActivityAssignment) {
                ActivityAssignment aAssignment = (ActivityAssignment) assignment;
                
                String workUpdateString = request.getParameter("wkUpdate"+taskID);
                TimeQuantity newWork;
                if (!Validator.isBlankOrNull(workUpdateString)) {
                    newWork = new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR);
                    aAssignment.setWork(newWork);
                }
            }
        }
    }


    private Map collectWorkChanges(HttpServletRequest request) {
        Map workUpdate = new HashMap();

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String)parameterNames.nextElement();

            if (paramName.startsWith("wkUpdate")) {
                String workUpdateString = request.getParameter(paramName);

                if (!Validator.isBlankOrNull(workUpdateString)) {
                    TimeQuantity newWork = new TimeQuantity(new BigDecimal(workUpdateString), TimeQuantityUnit.HOUR);
                    String id = paramName.substring(8);
                    workUpdate.put(id, newWork);
                }
            }
        }

        return workUpdate;
    }

    /**
     * From the request object, grab the hours that users claims they've worked
     * on various days.  Put the information into a form that we can use to store
     * it in the database.
     * <p/>
     * This method will update {@link #errors} if any problems occur processing
     * any work values.  In that case the state of the map is undefined.
     * @param request a <code>HttpServletRequest</code> that we'll use to find
     * what times the user has reported.
     * @return a <code>MultiMap</code> which maps object id strings to multiple
     * time quantities that the user has worked.
     */
    private MultiMap parseWorkLogInfoFromRequest(HttpServletRequest request) {
        MultiMap map = new MultiHashMap();
        NumberFormat nf = NumberFormat.getInstance();
        DateFormat df = DateFormat.getInstance();
        int commentInteger = 0; 
        
        String mutlitpleWorkSubmittedComments = request.getParameter("multipleWorkSubmitted");
        if(StringUtils.isNotEmpty(mutlitpleWorkSubmittedComments)) {
        	commentArray = mutlitpleWorkSubmittedComments.split(",");
        }
        
        Enumeration parameterNames = request.getParameterNames();
        if(PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled")) {
	        while (parameterNames.hasMoreElements()) {
	            String paramName = (String) parameterNames.nextElement();
	            if(paramName.startsWith("dateupdH")) {
	            	StringTokenizer tok = new StringTokenizer(paramName.substring(8), "H");
	            	String id = tok.nextToken();
	            	String dateSeconds = tok.nextToken();
	            	String workUpdateString = request.getParameter("workToUpdate"+id+"U"+dateSeconds);
		            if(StringUtils.isNotEmpty(workUpdateString)){
		            	try{
			            	Date workCapturedDate = new Date(Long.parseLong(dateSeconds));
			            	PnCalendar calendar = new PnCalendar(calendarProvider.getDefaultTimeZone());
			            	Date dateStart = calendar.startOfDay(workCapturedDate);
							Date dateEnd = calendar.endOfDay(workCapturedDate);
			            	TimeQuantity newWork = new TimeQuantity(nf.parseNumber(workUpdateString), TimeQuantityUnit.HOUR);
			            	if (newWork.abs().compareTo(MAXIMUM_WORK_HOURS) > 0) {
	                            // Positive or negative value exceeds maximum we allow for one day
	                            // Some limit necessary to prevent schedule calculation errors with massive work values
	                            errors.addError(new ErrorDescription(paramName, PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", workUpdateString, MAXIMUM_WORK_HOURS.formatAmount())));
		                    }
			            	map.put(id, new DateUpdate(newWork, dateStart, dateEnd, commentArray[commentInteger]));
							commentInteger++;
		            	}catch (Exception e) {
		            		errors.addError(new ErrorDescription(paramName, PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", workUpdateString, MAXIMUM_WORK_HOURS.formatAmount())));
						}
		            }
	        	}
	        } //End Of while
        } else {
        	String workUpdateString[] = request.getParameterValues("workToUpdate");
        	String id = request.getParameter("objectID");
        	String dateSeconds[] = request.getParameterValues("dateSeconds");
        	String personId = request.getParameter("personId");
        	for(int index = 0; index < workUpdateString.length; index++) {
        	 if(StringUtils.isNotEmpty(workUpdateString[index])){
	            	try{
		            	Date workCapturedDate = new Date(Long.parseLong(dateSeconds[index]));
		            	PnCalendar calendar = new PnCalendar(calendarProvider.getDefaultTimeZone());
		            	Date dateStart = calendar.startOfDay(workCapturedDate);
						Date dateEnd = calendar.endOfDay(workCapturedDate);
		            	TimeQuantity newWork = new TimeQuantity(nf.parseNumber(workUpdateString[index]), TimeQuantityUnit.HOUR);
		            	if (newWork.abs().compareTo(MAXIMUM_WORK_HOURS) > 0) {
                         // Positive or negative value exceeds maximum we allow for one day
                         // Some limit necessary to prevent schedule calculation errors with massive work values
                         errors.addError(new ErrorDescription(workUpdateString[index], PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", workUpdateString[index], MAXIMUM_WORK_HOURS.formatAmount())));
	                    }
		            	map.put(id, new DateUpdate(newWork, dateStart, dateEnd));
	            	}catch (Exception e) {
	            		errors.addError(new ErrorDescription(workUpdateString[index], PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", workUpdateString[index], MAXIMUM_WORK_HOURS.formatAmount())));
					}
	            }
        	}
        }

        return map;
    }


    /**
     * A data structure containing the information that the user entered (or
     * implied) when they were entering data.
     *
     * @author Matthew Flower
     * @since Version 7.7.0
     */
    private class DateUpdate implements Comparable {
        TimeQuantity workAmount;
        Date workStart;
        Date workEnd;
        String comment;

        public DateUpdate(TimeQuantity workAmount, Date workStart, Date workEnd, String comment) {
            this.workAmount = workAmount;
            this.workStart = workStart;
            this.workEnd = workEnd;
            this.comment = comment;
        }

        public DateUpdate(TimeQuantity workAmount, Date workStart, Date workEnd) {
            this(workAmount,workStart,workEnd, "");
        }

        public int compareTo(Object o) {
            DateUpdate co = (DateUpdate)o;

            return this.workEnd.compareTo(co.workEnd);
        }
    }

    /**
     * Data structure that reports some summary data found while processing the
     * work logs that need to be stored.
     */
    private class WorkLogSummary {
        final TimeQuantity newWorkReported;
        final Date earliestWork;

        public WorkLogSummary(TimeQuantity newWorkReported, Date earliestWork) {
            this.newWorkReported = newWorkReported;
            this.earliestWork = earliestWork;
        }
    }
}
