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
/**
 * 
 */
package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.project.base.ObjectType;
import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.hibernate.service.IAssignResourceService;
import net.project.notification.DeliveryException;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStatus;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskAssignmentNotification;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


/**
 *
 */
@Service(value="assignResourceService")
public class AssignResourceServiceImpl implements IAssignResourceService{

	private static Logger log;
	
	public AssignResourceServiceImpl() {
		log = Logger.getLogger(AssignResourceServiceImpl.class);
	}
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IResourceAssignService#assignResourcesToTasks(java.lang.String, java.lang.String, java.lang.String, net.project.schedule.Schedule, boolean, net.project.resource.RosterBean, net.project.security.SecurityProvider, net.project.security.User)
	 */
	public void assignResourcesToTasks(String idList, String assignData, String resourceList, Schedule schedule, boolean isReplaceOrAddNew, RosterBean roster, User user, Space space) throws ControllerException, PersistenceException {
		
		if (roster == null) {
            roster = new RosterBean();
        }
		
		String[] resourceIdList = resourceList.split(",");
		String[] percentAssignedDataList = assignData.split(",");

		// The IDs that were originally selected on Schedule Main
		if (Validator.isBlankOrNull(idList)) {
			throw new IllegalStateException("Missing request attribute 'idList'");
		}
		// Construct the list of person id's we are working on.
		Collection resourceIDs = Arrays.asList(resourceIdList);

		// First check all the percent values are valid
		Map percentAssignedDecimals = parsePercentValues(percentAssignedDataList, resourceIDs);

		// Make sure to clear out any resources that already exist before
		boolean isReplaceExisting = isReplaceOrAddNew;

		// Construct the list of object ids we are working on.
		idList = idList.trim();
		idList = idList.replaceAll(" ", ",");

		// Get the schedule entries for the selected IDs
		Collection scheduleEntries = getScheduleEntries(schedule, idList);

		TaskEndpointCalculation taskEndpointCalculation = new TaskEndpointCalculation();
		// For each selected schedule entry, update the assignments
		for (Iterator iterator = scheduleEntries.iterator(); iterator.hasNext();) {
			ScheduleEntry nextScheduleEntry = (ScheduleEntry) iterator.next();
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(nextScheduleEntry, schedule
					.getWorkingTimeCalendarProvider());

			Map assignmentsMap = nextScheduleEntry.getAssignmentList().getAssignmentMap();
			if (isReplaceExisting) {
				// If we're replacing assignments, we can first delete all existing assignments
				// We must use schedule entry calculator to ensure that duration and work
				// behave appropriately for the task calculation type

				// We need a copy of the list since removal will alter it
				LinkedList currentAssignments = new LinkedList(nextScheduleEntry.getAssignments());
				for (Iterator assignmentIterator = currentAssignments.iterator(); assignmentIterator.hasNext();) {
					ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) assignmentIterator.next();
					boolean toRemove = true;

					// check this assignment in the list of new resource ids
					for (Iterator resourceIDIterator = resourceIDs.iterator(); resourceIDIterator.hasNext();) {
						String nextResourceID = (String) resourceIDIterator.next();
						ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentsMap
								.get(nextResourceID);

						// only delete the assignment which is not selected again bfd-2603
						if (nextAssignment.equals(assignment)) {
							toRemove = false;
							break;
						}
					}
					if (toRemove) {
						calc.assignmentRemoved(nextAssignment);
						taskEndpointCalculation.recalculateTaskTimesNoLoad(schedule);
					}
				}
			}

			// For each resource, add or update it
			for (Iterator resourceIDIterator = resourceIDs.iterator(); resourceIDIterator.hasNext();) {
				String nextResourceID = (String) resourceIDIterator.next();

				// Get the already-parsed percentage
				BigDecimal percentAssignedDecimal = (BigDecimal) percentAssignedDecimals.get(nextResourceID);

				ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentsMap.get(nextResourceID);
				if (assignment == null) {
					String timeZoneId = (String) schedule.getTimeZone().getID();
					// Schedule Entry does not have the resource. Create an assignment and add it
					assignment = makeAssignment(nextResourceID, nextScheduleEntry, space, user);
					assignment.setStatus(AssignmentStatus.ASSIGNED);
					if (timeZoneId.equals("")) {
						assignment.setTimeZone(schedule.getTimeZone());
					} else {
						assignment.setTimeZone(TimeZone.getTimeZone(timeZoneId));
					}

					if (nextScheduleEntry.getTaskCalculationType().equals(
							TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN)) {
						// Fixed Duration, Effort Driven tasks: percentage is calculated automatically
						// It cannot be specified
						try {
							calc.assignmentAdded(null, assignment);
						} catch (NoWorkingTimeException e) {
							log.error("error occured while adding assignment in calc" + e.getMessage());
						}

						// then update the percent assigned
						// Commented for bfd-3091
						// calc.assignmentPercentageChanged(percentAssignedDecimal, assignment);

						// recalculate it for fix of bfd 3316
						taskEndpointCalculation.recalculateTaskTimesNoLoad(schedule);

					} else {
						// All other task types, percentage is specified
						try {
							// sjmittal: always first add using 100% and then modify the %
							calc.assignmentAdded(new BigDecimal("1.00"), assignment);
							calc.assignmentPercentageChanged(percentAssignedDecimal, assignment);
						} catch (NoWorkingTimeException e) {
							log.error("error occured while adding assignment in calc 2" + e.getMessage());
						}

						// recalculate it for fix of bfd 3316
						taskEndpointCalculation.recalculateTaskTimesNoLoad(schedule);
					}

				} else {
					// Schedule Entry already has resource assignment. Update its percentage.
					calc.assignmentPercentageChanged(percentAssignedDecimal, assignment);

					// recalculate it for fix of bfd 3316
					taskEndpointCalculation.recalculateTaskTimesNoLoad(schedule);
				}
			}

			// Now that we've updated the assignments, store the schedule entry
			// The schedule entry itself may have changed, and its store will store
			// the assignments
			nextScheduleEntry.store(false, schedule);

			// Now send the notifications
			if (PropertyProvider.getBoolean("prm.schedule.taskassignments.notification.enabled", true)) {
				try {
					sendEmailNotifications(nextScheduleEntry, user, roster);
				} catch (Exception e) {
					// sjmittal catch it as we still have to commit rest of the transaction
				}
			}
		}
			
		// Recalculate the tasks in the projects to ensure that the change hasn't
		// changed the end dates of any tasks.
		// recalculating now at each schedule entry resource assignment
		if (schedule.isAutocalculateTaskEndpoints()) {
			schedule.recalculateTaskTimes();
		}
	}

	private Map parsePercentValues(String[] percentAssignedDataList, Collection resourceIDs) throws ControllerException {
		Map percentAssignedMap = new HashMap();
		int index = 0;
		for (Iterator iterator = resourceIDs.iterator(); iterator.hasNext();) {
			String resourceID = (String) iterator.next();
			String percentValue = percentAssignedDataList[index++];
			if (Validator.isBlankOrNull(percentValue)) {
				throw new ControllerException("Missing request parameter percent_" + resourceID);
			}
			try {
				int percentAssigned = ScheduleEntryAssignment.parsePercentAssigned(percentValue);
				if (!ScheduleEntryAssignment.isValidPercentAssigned(percentAssigned)) {
					// Out of range
				} else {
					// Successful parse; add to map
					percentAssignedMap.put(resourceID, new BigDecimal(percentAssigned).movePointLeft(2));
				}
			} catch (ParseException e) {
				// Invalid number
			}
		}
		return percentAssignedMap;
	}


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

	private static ScheduleEntryAssignment makeAssignment(String resourceID, ScheduleEntry scheduleEntry, Space space,
			User user) {
		ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
		assignment.setPersonID(resourceID);
		// set assignor to the user in session
		assignment.setAssignorID(user.getID());
		assignment.setObjectID(scheduleEntry.getID());
		assignment.setSpaceID(space.getID());
		assignment.setSpaceName(space.getName());
		assignment.setObjectType(ObjectType.TASK);
		return assignment;
	}

	/*
	 * protected AssignmentManager populateAssignmentManager(String entryID) throws PersistenceException {
	 * AssignmentManager assignmentManager = new AssignmentManager(); assignmentManager.reset();
	 * assignmentManager.setObjectID(entryID); assignmentManager.loadAssigneesForObject(); //Store this assignment
	 * manager in the session -- it is going to be used //again in the handling page
	 * request.getSession().setAttribute("assignmentManager", assignmentManager); return assignmentManager; }
	 */
	private void sendEmailNotifications(ScheduleEntry scheduleEntry, User user, RosterBean roster)
			throws NotificationException {
		DeliveryException firstDeliveryException = null;
		List failedResourceNames = new LinkedList();

		for (Iterator it = scheduleEntry.getAssignmentList().getAssignments().iterator(); it.hasNext();) {
			ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
			TaskAssignmentNotification notification = new TaskAssignmentNotification();
			notification.initialize(scheduleEntry, assignment, user);
			notification.attach(new net.project.calendar.vcal.VCalAttachment(scheduleEntry.getVCalendar()));
			try {
				notification.post();
			} catch (DeliveryException e) {
				// Problem delivering email, possibly due to bad email configuration
				// We have to look up roster by any ID since resourceIDs may yet to have registered
				if (firstDeliveryException == null) {
					firstDeliveryException = e;
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
		}
	}
	
	public void assignResourcesToSingleTask(ScheduleEntry scheduleEntry, User user, RosterBean roster, Schedule schedule, String[] resourceIDs, String[] percentAssignedDataList, String primaryOwnerID, Space space) throws Exception {
		ErrorReporter errorReporter = new ErrorReporter();
		if (roster == null) {
			roster = new RosterBean();
		}
		Map assignmentMap = scheduleEntry.getAssignmentList().getAssignmentMap();

		// First Update all the assignments for the specified resourceIDs
		if (resourceIDs != null) {
			Collection resourceIDList = Arrays.asList(resourceIDs);
			// First check all the percent values are valid
			Map percentAssignedDecimals = parsePercentValues(percentAssignedDataList, resourceIDList);
			// then check all the timezones are valid
			TaskEndpointCalculation taskEndpointCalculation = new TaskEndpointCalculation();
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule
					.getWorkingTimeCalendarProvider());

			for (Iterator iterator = resourceIDList.iterator(); iterator.hasNext();) {
				String resourceID = (String) iterator.next();
				ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentMap.get(resourceID);
				if (assignment == null) {
					// String timeZoneId = (String)
					// timeZones.get(nextResourceID);
					// Schedule Entry does not have the resource. Create an
					// assignment and add it
					assignment = makeAssignment(resourceID, scheduleEntry, space, user);
					assignment.setStatus(AssignmentStatus.ASSIGNED);
					assignment.setTimeZone(schedule.getTimeZone());
					assignment.setPersonRole("");
					if (StringUtils.isNotEmpty(primaryOwnerID) && assignment.getPersonID().equals(primaryOwnerID)) {
						assignment.setPrimaryOwner(true);
					}
					assignment.setTimeZone(roster.getAnyPerson(assignment.getPersonID()).getTimeZone());
					// All other task types, percentage is specified
					try {
						// always first add using 100% and then modify the %
						if (scheduleEntry.getTaskCalculationType().equals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN)) {
							calc.assignmentAdded(null, assignment);
						} else {
							if(percentAssignedDecimals.get(resourceID) == null || percentAssignedDecimals.get(resourceID).equals(new BigDecimal("1.00"))){
                        		calc.assignmentAdded(new BigDecimal("1.00"), assignment);
                        	} else {
                        		calc.assignmentAdded((BigDecimal)percentAssignedDecimals.get(resourceID), assignment);
                        	}
						}
						calc.assignmentPercentageChanged((BigDecimal) percentAssignedDecimals.get(resourceID),
								assignment);
                        
                        //if there will be any NoWorkingTimeException it will have to catch where from this mehtod is calling. 
					} catch (NoWorkingTimeException e) {
						log.error("error occured while adding assignment in calc 2" + e.getMessage());
                        errorReporter.addError(e.getMessage());
                        throw new NoWorkingTimeException(e.getMessage());
					}
					// recalculate it for fix of bfd 3316
					taskEndpointCalculation.recalculateTaskTimesNoLoad(schedule);
				}
			}
		}

		if (!errorReporter.errorsFound()) {
			// We have to store the schedule entry, since work, dates and duration 
			// May have changed due to modified assignments This will store the assignments too.
			scheduleEntry.store(false, schedule);
			// Now send the notifications
			if (PropertyProvider.getBoolean("prm.schedule.taskassignments.notification.enabled", true)) {
				try {
					sendEmailNotifications(scheduleEntry, user, roster);
				} catch (Exception e) {
					// catch it as we still have to calculate end points
				}
			}
		}
	}
}
