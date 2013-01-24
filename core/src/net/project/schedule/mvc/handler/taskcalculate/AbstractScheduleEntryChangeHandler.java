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

 package net.project.schedule.mvc.handler.taskcalculate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.mvc.view.taskcalculate.ScheduleEntryChangeView;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.Validator;

/**
 * Provides a base class for round-trip modifications to ScheduleEntry.
 * @author Tim Morrow
 * @since Version 7.7.0
 */
abstract class AbstractScheduleEntryChangeHandler extends Handler {

    public AbstractScheduleEntryChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * This implementation throws an <code>UnsupportedOperationException</code>
     * always since this class overrides {@link #getView()} to return a view.
     * @return never returns
     * @throws UnsupportedOperationException always
     */
    public String getViewName() {
        throw new UnsupportedOperationException("PercentCompleteChangedHandler cannot return a simple view name.  Use getView() instead.");
    }

    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new ScheduleEntryChangeView();
    }

    /**
     * Validates security was checked for module <code>SCHEDULE</code> with
     * action <code>modify</code> on the specified objectID.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
    }

    /**
     * Template method for handling request when modifying ScheduleEntry.
     * <p>
     * Expects the following session attributes:
     * <ul>
     * <li>user - the current user
     * <li>scheduleEntry - the schedule entry to recalculate
     * <li>schedule - the current schedule
     * </ul>
     * Adds the following to the model:
     * <ul>
     * <li>scheduleEntry - the schedule entry modified
     * </ul>
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        ErrorReporter errorReporter = new ErrorReporter();
        model.put("errorReporter", errorReporter);

        // Get the user currently in session
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new ControllerException("Missing session attribute user");
        }

        // Get the schedule entry currently in session
        ScheduleEntry scheduleEntry = (ScheduleEntry) request.getSession().getAttribute("scheduleEntry");
        if (scheduleEntry == null) {
            throw new ControllerException("Missing session attribute scheduleEntry");
        }

        // Get the current schedule from the session
        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        if (schedule == null) {
            throw new ControllerException("Missing session attribute schedule");
        }

        // Get the ID of the schedule entry on which security was checked
        // And make sure that is the one being editied
        // If specified ID is empty and the schedule entry's is not
        // Or the specified ID is not the same as the schedule entry's
        // Then we have a problem
        String scheduleEntryID = (String) getVar("id");
        if ((Validator.isBlankOrNull(scheduleEntryID) && !Validator.isBlankOrNull(scheduleEntry.getID())) || (!Validator.isBlankOrNull(scheduleEntryID) && !scheduleEntry.getID().equals(scheduleEntryID))) {
            throw new ControllerException("Specified schedule entry ID " + scheduleEntryID + " differs from session schedule entry " + scheduleEntry.getID());
        }

        // Now parse the task calculation type
        // We always accept the value from the Task Edit page, since the user
        // expects any value they set to affect the calculation, regardless of whether
        // they have submitted or not
        String taskCalculationTypeID = request.getParameter("taskCalculationTypeID");
        if (Validator.isBlankOrNull(taskCalculationTypeID)) {
            throw new ControllerException("Missing request parameter 'taskCalculationTypeID'");
        }
        String effortDriven = request.getParameter("effortDriven");
        if (Validator.isBlankOrNull(effortDriven)) {
            throw new ControllerException("Missing request parameter 'effortDriven'");
        }
        scheduleEntry.setTaskCalculationType(TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.forID(taskCalculationTypeID), Boolean.valueOf(effortDriven).booleanValue()));

        // Now delegate to implementation make the actual changes
        doHandleRequest(request, schedule, scheduleEntry, scheduleEntryID, errorReporter, user, createClonedSchedule(schedule, scheduleEntry));

        model.put("scheduleEntry", scheduleEntry);

        return model;
    }

    /**
     * Implementing classes should modify the schedule entry as appropriate.
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be null if
     * a new schedule entry is being created
     * @param errorReporter an error reporter to which to add any errors
     * @param user the current user
     * @param clonedSchedule a clone of the schedule, also containing the session scheduleEntry object
     * such that updating the session scheduleEntry object will update the schedule clone;
     * this clone is a "sandbox" which can be recalculated without affecting the session schedule.
     * Once the session scheduleEntry is stored, the session schedule should be recalculated
     * @throws ControllerException if there is a problem handling the request
     */
    protected abstract void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter, User user, Schedule clonedSchedule) throws ControllerException;

    /**
     * Creates a clone of the schedule to permit endpoint calculations without affecting
     * the session schedule.
     * <p/>
     * The specified scheduleEntry is added or replaced in the clone such that recalculating
     * the schedule will automatically see the modified scheduleEntry.
     * <p/>
     * Tasks being created will have null task IDs and will be placed in the schedule with a null ID.
     * @param schedule the schedule to clone
     * @param scheduleEntry the scheduleEntry to ensure is present in the clone
     * @return the cloned schedule
     */
    private Schedule createClonedSchedule(Schedule schedule, ScheduleEntry scheduleEntry) throws PersistenceException {
        Schedule calcSchedule = (Schedule)getSessionVar("calcSchedule");

        if (schedule.isFiltered()) {
            //Make sure our current calculation schedule isn't null.
            if (calcSchedule == null || calcSchedule.getID() != schedule.getID() || calcSchedule.getLoadTime().before(schedule.getLoadTime())) {

                calcSchedule = (Schedule)schedule.clone();
                calcSchedule.clearFinderFilterList();
                calcSchedule.loadAll();
            }
            //If we get this far, there is a calculation schedule and it is up to date.
        } else {
            //The schedule isn't filtered -- it could probably be used for calculation
            calcSchedule = (Schedule)schedule.clone();
        }
        setSessionVar("calcSchedule", calcSchedule);

        //Now we have a proper calculation schedule.  We need to not modify this
        //schedule directory -- always modify a clone.  Loading is expensive,
        //cloning is relatively cheap.  We always need the calc schedule to be
        //pristene.  By making a copy of the calc schedule it means that the
        //next time we do this calculation we won't have to load it from
        //scratch again.
        Schedule clonedSchedule = (Schedule)calcSchedule.clone();
        Map currentTasks = new HashMap(clonedSchedule.getEntryMap());
        currentTasks.put(scheduleEntry.getID(), scheduleEntry);
        clonedSchedule.setEntryMap(currentTasks);

        return clonedSchedule;
    }

    protected BigDecimal parseWorkPercentComplete(HttpServletRequest request, ErrorReporter errorReporter) {
        BigDecimal percentComplete = null;
        try {
            String percentCompleteParam = request.getParameter("workPercentComplete");

            if (Validator.isBlankOrNull(percentCompleteParam)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.percentcomplete.required.message"));
            } else if (Validator.isNegative(percentCompleteParam)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message"));
            } else {
                NumberFormat nf = NumberFormat.getInstance();
                if (!percentCompleteParam.endsWith("%")) {
                    percentCompleteParam = percentCompleteParam + "%";
                }
                percentComplete = new BigDecimal(nf.parsePercent(percentCompleteParam).toString()); 

            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidpercentcomplete.message"));
        }
        return percentComplete;
    }

    protected TimeQuantity parseWorkComplete(HttpServletRequest request, ErrorReporter errorReporter) {
        TimeQuantity workComplete = null;
        try {
            String workCompleteAmountString = request.getParameter("workCompleteAmount");
            String workCompleteUnits = request.getParameter("workCompleteUnitsID");

            if (Validator.isBlankOrNull(workCompleteAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.required.message"));

            } else if (Validator.isBlankOrNull(workCompleteUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcompleteunits.required.message"));
                
            } else if(Validator.isNegative(workCompleteAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workcomplete.negative.message"));
                
            } else {
                workComplete = TimeQuantity.parse(workCompleteAmountString, workCompleteUnits);
                
            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkcomplete.message"));
        }
        return workComplete;
    }

    protected TimeQuantity parseWork(HttpServletRequest request, ErrorReporter errorReporter) {
        TimeQuantity work = null;
        try {
            String workAmountString = request.getParameter("workAmount");
            String workUnits = request.getParameter("workUnitsID");

            if (Validator.isBlankOrNull(workAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.work.required.message"));

            } else if (Validator.isBlankOrNull(workUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.workunits.required.message"));
                
            } else if(Validator.isNegative(workAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.work.negative.message"));
                
            }else {
                work = TimeQuantity.parse(workAmountString, workUnits);
                
            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invalidworkamount.message"));
        }
        return work;
    }
    
    protected TimeQuantity parseDuration(HttpServletRequest request, ErrorReporter errorReporter) {
        TimeQuantity duration = null;
        try {
            String durationAmountString = request.getParameter("durationAmount");
            String durationUnits = request.getParameter("durationUnitsID");

            if (Validator.isBlankOrNull(durationAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.duration.required.message"));

            } else if (Validator.isBlankOrNull(durationUnits)){
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.durationunits.required.message"));
                
            } else if(Validator.isNegative(durationAmountString)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.duration.negative.message"));
                
            } else {
                duration = TimeQuantity.parse(durationAmountString, durationUnits);

            }
        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.invaliddurationamount.message"));
        }
        return duration;
    }
}