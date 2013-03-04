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

 package net.project.schedule.mvc.handler;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.PredecessorList;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.SummaryTask;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskDependenciesFetcher;
import net.project.schedule.TaskDependency;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.mvc.handler.taskedit.ReadOnlyState;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.util.VisitException;

import org.apache.log4j.Logger;


/**
 * This class accepts the parameters that the user entered on the TaskEdit page
 * and saves them in the database.
 *
 * @author Matthew Flower
 * @since Version 7.6.2
 */
public abstract class AbstractTaskEditProcessingHandler extends Handler {
    /** Stores the errors that we find during processing. */
    protected ErrorReporter errorReporter = new ErrorReporter();
    /**
     * A special location that we should redirect to or pass on after saving
     * the task.
     */
    private String refLink;


    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     * code access to the request parameters.
     */
    public AbstractTaskEditProcessingHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Indicates if the calling page asked us to redirect to a certain page
     * upon the completion of processing.
     *
     * @return a <code>boolean</code> indicating if we should redirect to a
     * certain page after we complete processing.
     */
    public boolean hasRefLink() {
        return !Validator.isBlankOrNull(refLink);
    }

    /**
     * Get the encoded version of the referrer link.  Use this if you want to
     * pass the refLink as a variable.
     *
     * @return a <code>String</code> containing the URLEncoded view of the
     * String we should be redirecting to after the update/create process is
     * completed.
     */
    public String getReferringLinkEncoded() {
        return refLink;
    }

    /**
     * Get the unencoded view of the page that should be redirected to after
     * the create/update process is complete.
     *
     * @return a <code>String</code> containing the page we should redirect to
     * after we've completed without errors.
     */
    public String getReferringLink() {
        String decodedURL = refLink;

        try {
            decodedURL = URLDecoder.decode(refLink, SessionManager.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
        	Logger.getLogger(AbstractTaskEditProcessingHandler.class).debug("Unable to decode referrer link: " + e);
        }

        return decodedURL;
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
     * @throws AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws PnetException if any other error occurred.
     */
    public abstract void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException;

    /**
     * Add the necessary elements to the model that are required to render the
     * view that follows task edit. In this cases, this includes saving the
     * task.
     * 
     * @param model
     *            a <code>Map</code> to which we are going to add parameters.
     * @param entry
     *            a <code>ScheduleEntry</code> that we are going to store in
     *            the database.
     * @param request
     *            the <code>HttpServletRequest</code> that resulted from the
     *            user submitting the page.
     * @param response
     *            the <code>HttpServletResponse</code> that will allow us to
     *            pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception
     *             if any error occurs.
     */
    public Map handleRequest(Map model, ScheduleEntry entry, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Variables that will help determine the location to which we will be
        //redirected.
        refLink = request.getParameter("refLink");
        
//        if (refLink == null || refLink.equals("")) {
//        	refLink = (String) getVar("referer");
//        }

        Schedule schedule = (Schedule)getSessionVar("schedule");
        User user = SessionManager.getUser();

        //Parse the fields into the schedule entry
        parseRequestIntoTask(entry, request, user, schedule, false);

        if (errorReporter.errorsFound()) {
            model.put("scheduleEntry", entry);
            model.put("errorReporter", errorReporter);
            passThru(model, "module");
            passThru(model, "action");
            passThru(model, "id");
            return model;
        } else {
            entry.store(schedule.isAutocalculateTaskEndpoints(), schedule);
        }

        return model;
    }

    /**
     * Fill up the task object with the parameters that the user submitted in
     * the request.
     *
     * @param entry a <code>ScheduleEntry</code> that we are going to populate
     * @param request a <code>HttpServletRequest</code> which is our source for
     * the data we are using to fill up the schedule entry.
     * @param user the <code>User</code> who is creating or updating the task.
     * @param schedule a <code>Schedule</code> in which we are creating the
     * task.
     * @param blankDatesAllowed a <code>boolean</code> indicating if we require
     * the start and finish date to be present.  We won't want them to be
     * present if we are using automatic calculation.
     * @throws InvalidDateException
     * @throws PersistenceException
     * @throws VisitException
     */
    public void parseRequestIntoTask(ScheduleEntry entry, HttpServletRequest request, User user, Schedule schedule, boolean blankDatesAllowed) throws InvalidDateException, PersistenceException, VisitException, ParseException {
        if(entry == null)
            return;

        ReadOnlyState readOnlyState = new ReadOnlyState(entry, schedule);
        Map attributeValueMap = new HashMap();
        DateFormat df = user.getDateFormatter();
        boolean datesRequired = !schedule.isAutocalculateTaskEndpoints();

        entry.setName(request.getParameter("name").trim());
        entry.setDescription(request.getParameter("description"));
        entry.setPriority(request.getParameter("priority"));
        entry.selectPhase(request.getParameter("phaseSelection"));
        entry.setMilestone(request.getParameter("milestone") != null && request.getParameter("milestone").equalsIgnoreCase("true"));

        //fix for bfd 5752, note for shared task dates are also disabled so we just check for disability of start date
        //if the same is null then the dates are not supposed to be checked
        attributeValueMap.put("name","startTimeString");
        readOnlyState.filter(attributeValueMap);
        if(attributeValueMap.get("disabled") != null && !((Boolean) attributeValueMap.get("disabled")).booleanValue()) {
            checkStartAndEndDates(entry, request, df, datesRequired, blankDatesAllowed);
        }

        String duration = request.getParameter("duration");
        String durationUnits = request.getParameter("duration_units");
        attributeValueMap.put("name","duration");
        readOnlyState.filter(attributeValueMap);
        if(attributeValueMap.get("disabled") != null && !((Boolean) attributeValueMap.get("disabled")).booleanValue()) {

            if (Validator.isBlankOrNull(duration)) {
                errorReporter.addError(new ErrorDescription("duration", PropertyProvider.get("prm.schedule.taskedit.error.duration.required.message")));
            }else {
                if (!Validator.isNumeric(duration)) {
                    errorReporter.addError(new ErrorDescription("duration", PropertyProvider.get("prm.schedule.taskedit.error.duration.validnumber.message")));
                } else if(Validator.isNegative(duration)) {
                    errorReporter.addError(new ErrorDescription("duration", PropertyProvider.get("prm.schedule.taskedit.error.duration.negative.message")));
                } else if (Validator.isBlankOrNull(durationUnits)){
                    errorReporter.addError(new ErrorDescription("duration units", PropertyProvider.get("prm.schedule.taskedit.error.durationunits.required.message")));
                } else {
                    //every thing ok!! now safe to set duration
                    try {
                        entry.setDuration(TimeQuantity.parse(duration, durationUnits));
                    } catch (ParseException e) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invaliddurationamount.message"));
                    }
                }
            }

        }

        String work = request.getParameter("work");
        String workUnits = request.getParameter("work_units");
        attributeValueMap.put("name","work");
        readOnlyState.filter(attributeValueMap);
        if(attributeValueMap.get("disabled") != null && !((Boolean) attributeValueMap.get("disabled")).booleanValue()) {

            if (Validator.isBlankOrNull(work)) {
                errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.taskedit.error.work.required.message")));
            } else {
                if (!Validator.isNumeric(work)) {
                    errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.taskedit.error.work.validnumber.message")));
                } else if(Validator.isNegative(work)) {
                    errorReporter.addError(new ErrorDescription("work", PropertyProvider.get("prm.schedule.taskedit.error.work.negative.message")));
                } else if (Validator.isBlankOrNull(workUnits)){
                    errorReporter.addError(new ErrorDescription("work units", PropertyProvider.get("prm.schedule.taskedit.error.workunits.required.message")));
                } else {
                    //every thing ok!! now safe to set work
                    try {
                        entry.setWork(TimeQuantity.parse(work, workUnits));
                    } catch (ParseException e) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkamount.message"));
                    }
                }
            }

        }

        String workComplete = request.getParameter("work_complete");
        String workCompleteUnits = request.getParameter("work_complete_units");
        attributeValueMap.put("name","work_complete");
        readOnlyState.filter(attributeValueMap);
        if(attributeValueMap.get("disabled") != null && !((Boolean) attributeValueMap.get("disabled")).booleanValue()) {

            if (Validator.isBlankOrNull(workComplete)) {
                errorReporter.addError(new ErrorDescription("work complete", PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.required.message")));
            } else {
                if (!Validator.isNumeric(workComplete)) {
                    errorReporter.addError(new ErrorDescription("work complete", PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.validnumber.message")));
                } else if(Validator.isNegative(workComplete)) {
                    errorReporter.addError(new ErrorDescription("work complete", PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.negative.message")));
                } else if (Validator.isBlankOrNull(workCompleteUnits)){
                    errorReporter.addError(new ErrorDescription("work complete units", PropertyProvider.get("prm.schedule.taskedit.error.workcompleteunits.required.message")));
                } else {
                    //every thing ok!! now safe to set work complete
                    try {
                        entry.setWorkComplete(TimeQuantity.parse(workComplete, workCompleteUnits));
                    } catch (ParseException e) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkcomplete.message"));
                    }
                }
            }

        }

        if (entry.getWorkTQ().getAmount().signum() == 0) {
            NumberFormat nf = NumberFormat.getInstance();
        	String percentCompleteParam = request.getParameter("percentComplete");
            try {
            	if (!percentCompleteParam.endsWith("%")) {
                    percentCompleteParam = percentCompleteParam + "%";
                }
                entry.setPercentComplete(new BigDecimal(nf.parsePercent(percentCompleteParam).toString()));
            } catch (NullPointerException npe) {
                entry.setPercentComplete(null);
            } catch (ParseException e) {
                entry.setPercentComplete(null);
            }
        }

        // Task Calculation Type
        // On Summary tasks, no value is passed since it is not possible to change it
        String taskCalculationTypeFixedElementID = request.getParameter("taskCalculationTypeFixedElementID");
        String effortDriven = request.getParameter("effortDriven");
        if (!Validator.isBlankOrNull(taskCalculationTypeFixedElementID)) {
            boolean isEffortDriven = (effortDriven != null && effortDriven.equals("true"));
            // Construct the calculation type from the fixed element and boolean
            TaskCalculationType taskCalculationType = TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.forID(taskCalculationTypeFixedElementID), isEffortDriven);
            entry.setTaskCalculationType(taskCalculationType);
        }

        //Add the task dependencies
        int i = 1;
        PredecessorList tdl = entry.getPredecessors();
        //Remove any previously stored dependencies
        tdl.clear();
        tdl.setTaskID(entry.getID());
        tdl.setLoaded(true);
        List dependentTaskNames = new ArrayList(); 
        String currentTaskName = request.getParameter("dependency_" + i + "_task_id");
        while ((currentTaskName != null)) {
            if (currentTaskName.trim().length() == 0) {
                //We don't save blank dependencies
                currentTaskName = request.getParameter("dependency_" + (++i) + "_task_id");
                continue;
            }

            //sjmittal fix for bfd 3084 if the task name is present more than once
            //we would consider only the first dependency type
            if(dependentTaskNames.contains(currentTaskName)) {
                // We don't save dependencies allready added
                currentTaskName = request.getParameter("dependency_" + (++i) + "_task_id");
                continue;
            }
            dependentTaskNames.add(currentTaskName);

            TaskDependency td = new TaskDependency();

            td.setDependencyID(request.getParameter("dependency_" + i + "_task_id"));
            td.setTaskID(entry.getID());
            td.setTaskName(schedule.getEntry(td.getDependencyID()).getName());
            td.setDependencyTypeID(request.getParameter("dependency_" + i + "_type"));

            TimeQuantity lag;
            String lagAmount = request.getParameter("dependency_" + i + "_lag");
            String lagUnitID = request.getParameter("dependency_" + i + "_lag_units");
            if (Validator.isBlankOrNull(lagAmount)) {
                lag = new TimeQuantity(0, TimeQuantityUnit.DAY);
            } else {
                try {
                    lag = TimeQuantity.parse(lagAmount, lagUnitID);
                } catch (ParseException e) {
                    // Invalid lag amount; lag will be zero
                    errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidlagamount.message", lagAmount, td.getTaskNameMaxLength40()));
                    lag = new TimeQuantity(0, TimeQuantityUnit.DAY);
                }
            }
            td.setLag(lag);

            tdl.add(td);

            currentTaskName = request.getParameter("dependency_" + (++i) + "_task_id");
        }

        //Test to see if there is a cycle, we have to make sure that there is
        //a schedule will all its tasks loaded
        Schedule fullyLoadedSchedule = new Schedule();
        fullyLoadedSchedule.setID(schedule.getID());
        fullyLoadedSchedule.setSpace(schedule.getSpace());
        fullyLoadedSchedule.loadEntries();
        List dependenciesWithCycles = entry.getDependenciesWithCycles(fullyLoadedSchedule);
        if (dependenciesWithCycles.size() > 0) {
            ArrayList summaryTaskCycles = new ArrayList();

            //Make a list of the ancestry
            Set ancestry = new LinkedHashSet();
            ScheduleEntry current = entry;
            while (current != null) {
                ancestry.add(current.getID());
                current = (ScheduleEntry)schedule.getEntryMap().get(current.getParentTaskID());
            }

            //Construct a list of dependency names
            ArrayList cyclicDependencies = new ArrayList();
            for (Iterator it = dependenciesWithCycles.iterator(); it.hasNext();) {
                Object o = it.next();

                if (o instanceof TaskDependency) {
                    TaskDependency td = (TaskDependency)o;

                    //If this task dependency error occurred because an ancestry
                    //conflict, report the error as an ancestry conflict.
                    if (ancestry.contains(td.getDependencyID())) {
                        summaryTaskCycles.add(td.getTaskName() + " (" + td.getDependencyType().getAbbreviation() + "," + td.getLagString() + ")");
                    } else {
                        cyclicDependencies.add(td.getTaskName() + " (" + td.getDependencyType().getAbbreviation() + "," + td.getLagString() + ")");
                    }
                }
            }

            //Add an error with these dependencies
            if (!cyclicDependencies.isEmpty()) {
                ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.taskedit.error.cycle.message", Conversion.toCommaSeparatedString(cyclicDependencies)));
                errorReporter.addError(ed);
            }

            if (!summaryTaskCycles.isEmpty()) {
                String badDependency = (String)summaryTaskCycles.get(0);
                ScheduleEntry parentTask = (ScheduleEntry)schedule.getEntryMap().get(entry.getParentTaskID());

                ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.taskedit.error.cycle.subtaskrelationship", badDependency, parentTask.getName()));
                errorReporter.addError(ed);
            }

            //Remove all dependencies with cycles
            tdl.removeAll(dependenciesWithCycles);
        }

        // Now test to see if we link to children
        if (entry instanceof SummaryTask) {
            TaskDependenciesFetcher dcd = new TaskDependenciesFetcher(schedule.getEntryMap());
            List depsToChildren = dcd.getDependenciesToChildren((SummaryTask) entry);
            Iterator it = depsToChildren.iterator();

            while (it.hasNext()) {
                TaskDependency badDependency = (TaskDependency) it.next();

                String description = badDependency.getTaskName() + " (" + badDependency.getDependencyType().getAbbreviation() + "," + badDependency.getLagString() + ")";
                ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.taskedit.error.cycle.subtaskrelationship", description, entry.getName()));
                errorReporter.addError(ed);
            }
        }

        // Now set the constraints
        String taskConstraintType = request.getParameter("constraintTypeID");
        if (taskConstraintType != null && taskConstraintType != "")
            entry.setConstraintType(TaskConstraintType.getForID(taskConstraintType));
        if (entry.getConstraintType() != null && entry.getConstraintType().isDateConstrained()) {
            // Only need to process the date if the constraint needs one

            String constraintDateString = request.getParameter("constraintDateString");
            if (constraintDateString != null && constraintDateString != "" && !checkTimeStringValid(constraintDateString, df, true)) {
                // Invalid date
                ErrorDescription ed = new ErrorDescription("constraintDateString", PropertyProvider.get("prm.schedule.taskedit.error.constraint.message", new Object[]{constraintDateString}));
                errorReporter.addError(ed);

            } else {
                // Parse the date and time (possibly null if the date string is null)
                Date constraintDate = TimeBean.parseDateTime(request, "constraintDateString", "constraintDateTime", true);

                if (entry.getConstraintDate() != null && !isTimeSpecified(request, "constraintDateTime")) {
                    // If the user didn't specify a time, we want to set the time based on the constraint type
                    constraintDate = ScheduleEntry.updateConstraintTime(entry.getConstraintType(), constraintDate, TimeBean.getTimeZone(request, "constraintDateTime"));
                }

                entry.setConstraintDate(constraintDate);
            }
        } else {
            entry.setConstraintDate(null);
        }


        // Check if the deadline date entered by the user is legal
        String deadlineString = request.getParameter("deadlineString");
        if (deadlineString != null && deadlineString != "" && !checkTimeStringValid(deadlineString, df, true)) {
            ErrorDescription ed = new ErrorDescription("deadlineString", PropertyProvider.get("prm.schedule.taskedit.error.deadline.message", new Object[]{deadlineString}));
            errorReporter.addError(ed);
        } else {
            entry.setDeadline(TimeBean.parseDateTime(request, "deadlineString", "deadlineTime", true));
        }
     
        // set id of charge code applied on this task
        entry.setChargeCodeId(request.getParameter("chargecode"));
    }

    /**
     * Make sure that the user has provided valid start and end dates if we are
     * not using autocalculation.  When we are using autocalculation, we don't
     * need them because the calculation will be providing those dates.
     *
     * @param entry a <code>ScheduleEntry</code> that will have its start and
     * end dates set if they are available.
     * @param request a <code>HttpServletRequest</code> object which will be the
     * place from which we will fetch the user-supplied variables.
     * @param df a <code>DateFormat</code> object that will be used to parse the
     * dates from strings into Date objects.
     * @param datesRequired a <code>boolean</code> value indicating if we really
     * need the start and end dates.
     * @param blankDatesAllowed
     */
    private void checkStartAndEndDates(ScheduleEntry entry, HttpServletRequest request, DateFormat df, boolean datesRequired, boolean blankDatesAllowed) {

        //If we are using autocalculation, we don't need the start or end dates
        if (datesRequired) {
            // Check if the start date entered by the user is legal
            if (!checkTimeStringValid(request.getParameter("startTimeString"), df, blankDatesAllowed)) {
                ErrorDescription ed = new ErrorDescription("startTimeString", PropertyProvider.get("prm.schedule.taskedit.error.startdate.message", new Object[]{request.getParameter("startTimeString")}));
                errorReporter.addError(ed);
            } else {
                try {
                    entry.setStartTimeD(df.parseDateString(request.getParameter("startTimeString")));
                } catch (InvalidDateException e) {
                    throw (IllegalArgumentException) new IllegalArgumentException("Error parsing start date after validation successful").initCause(e);
                }
            }

            // Check if the end date entered by the user is legal
            if (!checkTimeStringValid(request.getParameter("endTimeString"), df, blankDatesAllowed)) {
                ErrorDescription ed = new ErrorDescription("endTimeString", PropertyProvider.get("prm.schedule.taskedit.error.enddate.message", new Object[]{request.getParameter("endTimeString")}));
                errorReporter.addError(ed);
            } else {
                try {
                    entry.setEndTimeD(df.parseDateString(request.getParameter("endTimeString")));
                } catch (InvalidDateException e) {
                    throw (IllegalArgumentException) new IllegalArgumentException("Error parsing end date after validation successful").initCause(e);
                }
            }

            if (entry.getEndTime().before(entry.getStartTime())) {
                ErrorDescription ed = new ErrorDescription("startTimeString", PropertyProvider.get("prm.schedule.taskedit.error.endbeforestart.message"));
                errorReporter.addError(ed);
            }

            entry.setIgnoreTimePortionOfDate(true);
        } else {
            entry.setIgnoreTimePortionOfDate(false);
        }
        Date actualStart = null;
        try {
            if (!Validator.isBlankOrNull(request.getParameter("actualStartDate"))) {
				actualStart = df.parseDateString(request.getParameter("actualStartDate"));
				entry.setActualStartTimeD(actualStart);
			} else {
				entry.setActualStartTimeD(null);
			}
        } catch (InvalidDateException e) {
            ErrorDescription ed = new ErrorDescription("actualStartDate", PropertyProvider.get("prm.schedule.taskedit.error.actualstartdate.message", new Object[]{request.getParameter("actualStartDate")}));
            errorReporter.addError(ed);
        }
        Date actualEnd = null;
        try {
            if (!Validator.isBlankOrNull(request.getParameter("actualEndDate"))) {
				actualEnd = df.parseDateString(request.getParameter("actualEndDate"));
				entry.setActualEndTimeD(actualEnd);
			} else {
				entry.setActualEndTimeD(null);
            }
        } catch (InvalidDateException e) {
            ErrorDescription ed = new ErrorDescription("actualEndDate", PropertyProvider.get("prm.schedule.taskedit.error.actualenddate.message", new Object[]{request.getParameter("actualEndDate")}));
            errorReporter.addError(ed);
        }
        if ((actualStart != null) && (actualEnd != null)) {
            if (actualEnd.before(actualStart)) {
                ErrorDescription ed = new ErrorDescription("actualStartDate", PropertyProvider.get("prm.project.process.modifyphase.actualstartenddatesvalidation.message", new Object[] {request.getParameter("actualStartDate") }));
                errorReporter.addError(ed);
            }
        }
    }

    /**
         * Check to see if the string passed to this method is a valid date
         * string.
         * 
         * @param dateToCheck
         *                a <code>String</code> containing the date
         *                representation we want to convert to a proper date
         *                object.
         * @param df
         *                a <code>DateFormatter</code> that is locale-aware
         *                and knows what a proper date string looks like.
         * @param allowEmpty
         *                a <code>boolean</code> indicating if we are allowing
         *                empty strings.
         * @return a <code>boolean</code> that indicates if the string is a
         *         valid date (or blank if the allowEmpty flag is set.)
         */
    public boolean checkTimeStringValid(String dateToCheck, DateFormat df, boolean allowEmpty) {
        boolean toReturn = allowEmpty;
        if ((dateToCheck != null) && (!dateToCheck.equals(""))) {
            return (df.isLegalDate(dateToCheck));
        }
        return toReturn;
    }

    /**
     * Load the schedule entry with the given id, unless it has been passed in
     * the request.  If the id is null, create a new one.
     *
     * @param request a <code>HttpServletReqeust</code> which contains the
     * information passed to the controller.
     * @param id the primary key of the task entry to load.
     * @return a <code>ScheduleEntry</code> that corresponds to the primary key
     *
     * @throws PersistenceException
     */
    protected ScheduleEntry getScheduleEntry(String id, HttpServletRequest request) throws PersistenceException {
        ScheduleEntry entry;
        if (Validator.isBlankOrNull(id)) {
            //This is a new task
            entry = ScheduleEntryFactory.createFromType(TaskType.TASK);
        } else {
            TaskFinder tf = new TaskFinder();
            entry = tf.findObjectByID(id, true, true, true);
        }
        return entry;
    }

    /**
     * Indicates whether a time was selected for the time field.
     * @param request the current request
     * @param timeFieldName the name of the time input taglib
     * @return true if the user selected a time
     */
    private boolean isTimeSpecified(HttpServletRequest request, String timeFieldName) {
        // When time is optional, parseTime returns null when none was specified
        return (TimeBean.parseTime(request, timeFieldName, new Date(), true) != null);
    }
}
