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

 package net.project.schedule.mvc.handler.tasklist;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.database.DBBean;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.MaterialAssignment;
import net.project.material.MaterialAssignmentList;
import net.project.notification.DeliveryException;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentRoster;
import net.project.resource.AssignmentStatus;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskAssignmentNotification;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.mvc.handler.taskview.TaskAssignmentProcessingHandler;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a handler for processing submitted values from the Assign Materials dialog.
 */
public class AssignMaterialsDialogHandlerProcessing extends TaskAssignmentProcessingHandler {

    /** Default view. */
    private String viewName = "/schedule/include/AssignMaterialsDialogProcessing.jsp";

    public AssignMaterialsDialogHandlerProcessing(HttpServletRequest request) {
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
        return this.viewName;
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
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY);
        
        assignmentManager = populateAssignmentManager(objectID);
        if (assignmentManager.isUserInAssignmentList(SessionManager.getUser().getID())) {
            // Check modify permission if user is not assigned task
            // An exception is thrown if they have no permission
            SessionManager.getSecurityProvider().securityCheck(objectID, String.valueOf(Module.SCHEDULE), Action.MODIFY);
        }
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
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws ControllerException, PersistenceException {
        Schedule schedule = (Schedule)getSessionVar("schedule");
        User user = (User)getSessionVar("user");
        Space space = user.getCurrentSpace();

        // The IDs that were originally selected on Schedule Main
        String idList = request.getParameter("idList");
        if (Validator.isBlankOrNull(idList)) {
            throw new IllegalStateException("Missing request attribute 'idList'");
        }

        // Construct the list of person id's we are working on.
        Collection resourceIDs = Arrays.asList(request.getParameterValues("resource"));        
        
        // Construct the list of object ids we are working on.
        idList = idList.trim();
        idList = idList.replaceAll(" ", ",");

        // Get the schedule entries for the selected IDs
        Collection scheduleEntries = getScheduleEntries(schedule, idList);

        // Make sure to clear out any resources that already exist before
        boolean isReplaceExisting = (request.getParameter("replaceExisting").equals("1"));        
        
        // For each selected schedule entry, update the assignments
        for (Iterator iterator = scheduleEntries.iterator(); iterator.hasNext();)
        {
            ScheduleEntry scheduleEntry = (ScheduleEntry) iterator.next();        	
            MaterialAssignmentList materialAssignmentList = scheduleEntry.getMaterialAssignmentsList();
        	// Add new material assignments
            ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());               
            
            // Clear all previous material assignments
        	if(isReplaceExisting)
        	{
        		for(MaterialAssignment materialAssignment : materialAssignmentList)
        		{
        			materialAssignment.setRecordStatus("D");
        			materialAssignment.setModifiedBy(user.getID());
        			materialAssignment.setModifiedDate(new Date());
        		}
        	}

            // For each resource, add or update it
            for (Iterator resourceIDIterator = resourceIDs.iterator(); resourceIDIterator.hasNext();)
            {
                String resourceId = (String) resourceIDIterator.next();
                
	        	if(calc.existsMaterialAssignment(resourceId))
	        	{
	        		MaterialAssignment assignment = calc.getMaterialAssignment(resourceId);
	                assignment.setRecordStatus("A");        		
	                assignment.setModifiedBy(user.getID());
	                assignment.setModifiedDate(new Date());        		
	                assignment.setAssignorId(user.getID());
	        	}
	        	else
	        	{
	                // Doesn't exists, create a new assignment
	            	MaterialAssignment assignment = new MaterialAssignment();
	                assignment.setSpaceId(scheduleEntry.getSpaceID());
	                assignment.setObjectId(scheduleEntry.getID());
	            	assignment.setMaterialId(resourceId);
	                assignment.setPercentAssigned(BigDecimal.valueOf(100.00));
	                assignment.setRecordStatus("A");
	                assignment.setStartDate(scheduleEntry.getStartTime());
	                assignment.setEndDate(scheduleEntry.getEndTime());
	                assignment.setDateCreated(new Date());
	                assignment.setModifiedBy(user.getID());
	                assignment.setModifiedDate(new Date());
	                assignment.setAssignorId(user.getID());
	                
	                //Check if this assignment is in conflict with existing assignments.
	                assignment.setOverassigned(ServiceFactory.getInstance().getPnMaterialAssignmentService().isOverassigned(assignment.getStartDate(), assignment.getEndDate(), assignment.getSpaceId(), assignment.getMaterialId()));            
	                
	                calc.assignmentMaterialAdded(assignment);        		
	        	}
        		                
            }        	

            scheduleEntry.store(false, schedule);
        }

        return Collections.EMPTY_MAP;
    }

    /**
     * Checks and parses the assignment percentage values found in the request to ensure they
     * are valid.
     * If any are not valid the errorReporter is updated; in that case the map will be incomplete.
     * @param request the request from which to find the assignment percentage values
     * @param resourceIDs the resourceIDs for which to get percent values
     * @param errorReporter the error reporter to update
     * @return a map where each key is a <code>resourceID</code> and each value is a <code>BigDecimal</code>
     * representing that resource's percent assigned decimal (that is, 100% = <code>1.00</code>)
     * @throws ControllerException if a percent value is missing from the request
     */
    private Map parsePercentValues(HttpServletRequest request, Collection resourceIDs, ErrorReporter errorReporter) throws ControllerException {

        Map percentAssignedMap = new HashMap();

        for (Iterator iterator = resourceIDs.iterator(); iterator.hasNext();) {
            String resourceID = (String) iterator.next();

            String percentValue = request.getParameter("percent_" + resourceID);
            if (Validator.isBlankOrNull(percentValue)) {
                throw new ControllerException("Missing request parameter percent_" + resourceID);
            }

            try {
                int percentAssigned = ScheduleEntryAssignment.parsePercentAssigned(percentValue);
                if (!ScheduleEntryAssignment.isValidPercentAssigned(percentAssigned)) {
                    // Out of range
                    errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.percentoutofrange.message"));
                } else {
                    // Successful parse; add to map
                    percentAssignedMap.put(resourceID, new BigDecimal(percentAssigned).movePointLeft(2));
                }

            } catch (ParseException e) {
                // Invalid number
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.invalidnumber.message", percentValue));
            }

        }

        return percentAssignedMap;
    }

    /**
     * Checks and parses the timezone ids found in the request to ensure they
     * are valid.
     * @param request the request from which to find the assignment time zones
     * @param resourceIDs the resourceIDs for which to get time zones
     * @param defaultTimeZone the default schedules time zone
     * @return a map where each key is a <code>resourceID</code> and each value is a <code>String</code>
     * representing that resource's time zone
     */
    private Map parseTimeZones(HttpServletRequest request, Collection resourceIDs, TimeZone defaultTimeZone) {

        Map timeZoneMap = new HashMap();

        for (Iterator iterator = resourceIDs.iterator(); iterator.hasNext();) {
            String resourceID = (String) iterator.next();

            String timeZoneId = request.getParameter("timezone_" + resourceID);
            if (Validator.isBlankOrNull(timeZoneId)) {
                timeZoneMap.put(resourceID, defaultTimeZone.getID());
            } else {
                timeZoneMap.put(resourceID, timeZoneId);
            }
        }

        return timeZoneMap;
    }
    
    /**
     * Returns the schedule entries for the specified IDs.
     * @param schedule the schedule from which to get schedule entries
     * @param scheduleEntryIDCSV a comma separated list of schedule entry IDs
     * @return a collection where each element is a <code>ScheduleEntry</code>
     * @throws IllegalArgumentException if a schedule entry is missing for an ID
     */
    private static Collection getScheduleEntries(Schedule schedule, String scheduleEntryIDCSV) {
        Map allScheduleEntries = schedule.getEntryMap();
        Collection scheduleEntries = new LinkedList();

        // Iterate over comma-separated list of schedule entry IDs, looking
        // for matching entries
        for (Iterator iterator = Arrays.asList(scheduleEntryIDCSV.split(",")).iterator(); iterator.hasNext();) {
            String nextID = ((String) iterator.next()).trim();
            if (allScheduleEntries.containsKey(nextID)) {
                scheduleEntries.add(allScheduleEntries.get(nextID));
            } else {
                throw new IllegalArgumentException("Schedule does not contain a schedule entry for ID " + nextID);
            }
        }

        return Collections.unmodifiableCollection(scheduleEntries);
    }

    /**
     * Creates a new assignment with the specified ID, for the specified schedule entry and space.
     * <p/>
     * Note: The assignment is _not_ added to the schedule entry.
     * @param resourceID the ID of the resource for which to create the assignment
     * @param scheduleEntry the schedule entry who's ID to set as the objectID
     * @param space the space to which the assignment will belong
     * @return the assignment
     */
    private static ScheduleEntryAssignment makeAssignment(String resourceID, ScheduleEntry scheduleEntry, Space space, User user) {
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPersonID(resourceID);
        //set assignor to the user in session
        assignment.setAssignorID(user.getID());
        assignment.setObjectID(scheduleEntry.getID());
        assignment.setSpaceID(space.getID());
        assignment.setObjectType(ObjectType.TASK);
        return assignment;
    }

    /**
     * Sends notifications to newly added assignments.
     * <p/>
     * When delivery errors occur (for example, sending email), error reporter is update.
     * @param scheduleEntry the schedule entry about which assignments will be notified of
     * their assignment to
     * @param user the current user performing the assignments
     * @param roster the roster used for locating resource names in the event of errors
     * @param errorReporter the error report to which to add errors
     * @throws NotificationException if a non-delivery notification exception occurred
     */
    private void sendEmailNotifications(ScheduleEntry scheduleEntry, User user, RosterBean roster, ErrorReporter errorReporter) throws NotificationException {

        DeliveryException firstDeliveryException = null;
        List failedResourceNames = new LinkedList();

        for (Iterator it = scheduleEntry.getAssignmentList().getAssignments().iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();

            // Only send notification if person not already assigned
            if (!assignmentManager.isUserInAssignmentList(assignment.getPersonID())) {
                TaskAssignmentNotification notification = new TaskAssignmentNotification();

                notification.initialize (scheduleEntry, assignment, user);
                notification.attach(new net.project.calendar.vcal.VCalAttachment(scheduleEntry.getVCalendar()));
                try {
                    notification.post();
                } catch (DeliveryException e) {
                    // Problem delivering email, possibly due to bad email configuration
                    // We have to look up roster by any ID since resourceIDs may yet to have registered
                    if (firstDeliveryException == null) {
                        firstDeliveryException = e;
                    }
                    failedResourceNames.add(roster.getAnyPerson(assignment.getPersonID()).getDisplayName());
                }
            }
        }

        if (firstDeliveryException != null) {
            // There was an error
            // Format the names of the failed resources
            StringBuffer failedResourceNamesFormatted = new StringBuffer();
            for (Iterator iterator = failedResourceNames.iterator(); iterator.hasNext();) {
                String name = (String) iterator.next();
                if (failedResourceNamesFormatted.length() > 0) {
                    failedResourceNamesFormatted.append(", ");
                }
                failedResourceNamesFormatted.append(name);
            }

            // Document the error, using only the first exception, but all the names
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.notification.message", failedResourceNamesFormatted.toString(), firstDeliveryException.formatOriginalCauseMessage()));
        }
    }
    
}
