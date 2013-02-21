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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.mvc.view.taskcalculate.MaterialAssignmentChangeView;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

import org.apache.log4j.Logger;

/**
 * Provides a base class for handlers performing round-trips when material assignments change.
 */
abstract class AbstractMaterialAssignmentChangeHandler extends Handler {

    private static final Logger LOGGER = Logger.getLogger(AbstractMaterialAssignmentChangeHandler.class);

    AbstractMaterialAssignmentChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * This implementation throws an <code>UnsupportedOperationException</code>
     * always since this class overrides {@link #getView()} to return a view.
     * @return never returns
     * @throws UnsupportedOperationException always
     */
    public String getViewName() {
        throw new UnsupportedOperationException("Cannot return a simple view name.  Use getView() instead.");
    }

    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new MaterialAssignmentChangeView();
    }

    /**
     * Validates security was checked for module <code>SCHEDULE</code> with
     * action <code>modify</code> on the specified objectID.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        // Currently view permission to allow assignees to edit assignments without
        // requiring task modification permission
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
    }

    /**
     * Provides a template method for invoking a task calculation for changing an assignment
     * and placing the result in the model.
     * <p>
     * Expects the following session attributes:
     * <ul>
     * <li>scheduleEntry - the schedule entry to recalculate
     * <li>schedule - the current schedule
     * </ul>
     * Expects the following request attributes:
     * <ul>
     * <li>id - the schedule entry ID
     * <li>resourceID - the id of the resource being added or removed
     * </ul>
     * Adds the following to the model:
     * <ul>
     * <li>errorReporter - errors that occurred; no other attributes are added to the model in the event
     * of an error
     * <li>scheduleEntry - the schedule entry modified
     * </ul>
     * @param request
     * @param response
     * @return the model map
     * @throws Exception
     */
    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        ErrorReporter errorReporter = new ErrorReporter();
        model.put("errorReporter", errorReporter);

        // Get the schedule entry currently in session
        ScheduleEntry scheduleEntry = (ScheduleEntry) request.getSession().getAttribute("scheduleEntry");
        if (scheduleEntry == null) {
            throw new ControllerException("Missing session attribute scheduleEntry");
        }

        // Get the current schedule from the session
        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        schedule.reloadIfNeeded();
//        if (schedule == null) {
//            throw new ControllerException("Missing session attribute schedule");
//        }

        // Get the ID of the schedule entry on which security was checked
        // And make sure that is the one being editied
        String scheduleEntryID = (String) getVar("id");

        // If specified ID is empty and the schedule entry's is not
        // Or the specified ID is not the same as the schedule entry's
        // Then we have a problem
        if ((Validator.isBlankOrNull(scheduleEntryID) && !Validator.isBlankOrNull(scheduleEntry.getID())) || (!Validator.isBlankOrNull(scheduleEntryID) && !scheduleEntry.getID().equals(scheduleEntryID))) {
            throw new ControllerException("Specified schedule entry ID " + scheduleEntryID + " differs from session schedule entry " + scheduleEntry.getID());
        }

        // Get the ID of the material being added / removed
        String materialID = request.getParameter("materialID");
        if (Validator.isBlankOrNull(materialID)) {
            throw new ControllerException("Missing request parameter materialID");
        }

        // Construct a map of resourceID to max allocation percentage
//        Map<String, BigDecimal> maxAllocationMap = parseMaxAllocations(request);
        // Save current assignment percentages
//        Map<String, BigDecimal> oldAssignmentPercentages = getCurrentAssignmentPercentages(scheduleEntry.getAssignments());

        // Now actual perform the change
        doHandleRequest(request, schedule, scheduleEntry, materialID, errorReporter);

        // Only add the results if no errors were found
        if (!errorReporter.errorsFound()) {
            model.put("scheduleEntry", scheduleEntry);
            model.put("overallocatedMaterialsExist", scheduleEntry.getMaterialAssignments().overAssignationExists());
//            model.put("oldMaterialAssignment", oldAssignmentPercentages);
//            model.put("maxAllocationMap", maxAllocationMap);

        }

        return model;
    }

    /**
     * Implementing classes should perform the appropriate modifications.
     * <p>
     * If any errors occur, errorReporter should be updated.
     * </p>
     * @param request
     * @param schedule
     * @param scheduleEntry
     * @param resourceID
     * @param timeZoneId
     * @param errorReporter
     * @throws ControllerException
     */
    protected abstract void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String materialID, ErrorReporter errorReporter) throws ControllerException;


    /**
     * Constructs a map of assignment resourceID to percent assigned decimal values.
     * @param assignments the assignments from which to construct the map
     * @return the map where each key is a <code>String</code> resourceID and each
     * element a <code>BigDecimal</code> representing the percentage assigned decimal
     * value fetched through {@link net.project.resource.ScheduleEntryAssignment#getPercentAssignedDecimal()}
     */
//    private static Map<String, BigDecimal> getCurrentAssignmentPercentages(Collection<?> assignments) {
//        Map<String, BigDecimal> assignmentPercentages = new HashMap<String, BigDecimal>(assignments.size());
//        for (Iterator<?> iterator = assignments.iterator(); iterator.hasNext();) {
//            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
//            assignmentPercentages.put(nextAssignment.getPersonID(), nextAssignment.getPercentAssignedDecimal());
//        }
//        return assignmentPercentages;
//    }

    /**
     * Parses from the request the current max allocation values of a subset of resources.
     * <p>
     * The included resources are all those currently assigned, any resource being unassigned.
     * This allows us to recalculate max allocation.
     * </p>
     * @param request the request from which to parse max allocations
     * @return a map where each key is a <code>String</code> resourceID and each element is a
     * <code>BigDecimal</code> maximum percentage allocated
     */
//    private static Map<String, BigDecimal> parseMaxAllocations(HttpServletRequest request) {
//        // Parse max allocations
//        Map<String, BigDecimal> maxAllocationMap = new HashMap<String, BigDecimal>();
//        for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); ) {
//            String parameterName = (String) e.nextElement();
//
//            if (parameterName.startsWith("max_alloc_value_")) {
//                String allocResourceID = parameterName.substring("max_alloc_value_".length());
//                BigDecimal maxAllocDecimal = new BigDecimal(request.getParameter(parameterName));
//
//                // Now store in map
//                maxAllocationMap.put(allocResourceID, maxAllocDecimal);
//            }
//        }
//
//        if (LOGGER.isDebugEnabled()) {
//            StringBuffer output = new StringBuffer("Max allocations: ");
//            for (Iterator<String> iterator = maxAllocationMap.keySet().iterator(); iterator.hasNext();) {
//                String resourceID = (String) iterator.next();
//                output.append(resourceID).append("=").append(maxAllocationMap.get(resourceID)).append(" ");
//            }
//            LOGGER.debug(output.toString());
//        }
//
//        return maxAllocationMap;
//    }

}
