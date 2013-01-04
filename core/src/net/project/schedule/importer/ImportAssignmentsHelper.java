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
package net.project.schedule.importer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStatus;
import net.project.resource.Roster;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.soa.schedule.Project.Assignments.Assignment;
import net.project.space.Space;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper for importing assignments for a particular schedule entry.
 * <p/> There is a sizeable amount of logic to deal with many-to-one assignment
 * mappings and missing assignment mappings.
 * 
 */
class ImportAssignmentsHelper {

	private final ResourceResolver resourceResolver;
    
    private final Map assignorMapper;

	private final Space currentSpace;

	private final Roster roster;

	private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;

	private final ErrorReporter errorReporter;

	/**
	 * 
	 * @param resourceResolver
	 *            for looking up mapped person IDs for MSP assignment IDs
	 * @param currentSpace
	 *            for creating assignments in the correct space
	 * @param roster
	 *            for looking up the timezone of persons when creating an
	 *            assignment
	 * @param workingTimeCalendarProvider
	 *            the calendar provider required for getting a calendar used to
	 *            calculate how much work can be completed within the schedule
	 *            dates
	 * @param errorReporter
	 *            for gathering errors; this will be updated if any error occurs
	 */
	ImportAssignmentsHelper(ResourceResolver resourceResolver, Map assignorMapper, Space currentSpace, Roster roster, IWorkingTimeCalendarProvider workingTimeCalendarProvider, ErrorReporter errorReporter) {
		this.resourceResolver = resourceResolver;
        this.assignorMapper = assignorMapper;
		this.currentSpace = currentSpace;
		this.roster = roster;
		this.workingTimeCalendarProvider = workingTimeCalendarProvider;
		this.errorReporter = errorReporter;
	}

	/**
	 * Imports the assignments for the specified schedule entry based on the
	 * specified MSP assignments. <p/> Note when multiple MSP assignments are
	 * mapped to one resource, the resource's percentage must be recalculated to
	 * complete the sum of their new work within the task dates. However, since
	 * we haven't yet imported any working time calendars, we use an absolute
	 * default working time calendar. We chose not to use the current schedule
	 * working time calendar, since importing typically occurs prior to having
	 * one, or in order to replace one, so the current schedule calendar is
	 * almost always the wrong one to use. <br/> Similarly, when assignments are
	 * not mapped their work must be distributed to the remaining assignments,
	 * requiring percentages to be recalculated in order to ensure accuracy.
	 * <p/> Using a default calendar may have the effect of altering durations
	 * when an endpoint calculation occurs, assuming the multiply-mapped
	 * resource's working time calendar or the new schedule working time
	 * calendar is non-default for the schedule entry's dates.
	 * 
	 * @param entry
	 *            the schedule entry for which to create assignments
	 * @param mspAssignments
	 *            the assignments to import
	 */
	void importAssignmentsForTask(ScheduleEntry entry, Collection mspAssignments) {

		// We're going to save the assignments created for the entry
		// because multiple mappings to one person requires us to
		// update assignment work
		// Keys are Integer personIDs, values are ScheduleEntryAssignments
		Map<Integer, ScheduleEntryAssignment> currentAssignmentMap = new HashMap<Integer, ScheduleEntryAssignment>();

		// Maintan those resourceIDs for which work was merged, indicating that
		// we must recompute percentage properly to take care of unusual
		// percentage
		// versus work situations
		// Values are Integer personIDs
		Set<Integer> mergedWorkResourceIDs = new HashSet<Integer>();

		// Keep a total of the work belonging to ignored assignments
		// This work must be distributed amongst the others
		TimeQuantity ignoredWorkSum = new TimeQuantity(0, TimeQuantityUnit.SECOND);

		for (Iterator iterator = mspAssignments.iterator(); iterator.hasNext();) {
			Assignment mspAssignment = (Assignment) iterator.next();

			// Look for a resource mapping for this assignment
			Integer mappedPersonID = resourceResolver.getPersonID(new Integer(mspAssignment.getResourceUID().intValue()));
			if (mappedPersonID == null) {
				// No person mapped for this mspAssignment ID
				// We need to address this by later assignment this person's
				// work to the remaining assignments
				ignoredWorkSum = ignoredWorkSum.add(constructWork(mspAssignment.getWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime())));

			} else {
				// We got a mapping to a person
				ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) currentAssignmentMap.get(mappedPersonID);
				if (assignment == null) {
					// First mapping for person; create their assignment
					assignment = new ScheduleEntryAssignment();
					assignment.setSpaceID(currentSpace.getID());
					assignment.setPersonID(String.valueOf(mappedPersonID));
					assignment.setObjectID(String.valueOf(entry.getID()));
                    //sjmittal: add the assignor while importing
                    assignment.setAssignorID(String.valueOf(assignorMapper.get(mspAssignment.getResourceUID().intValue())));
					assignment.setPersonRole("");
					assignment.setStatus(AssignmentStatus.ASSIGNED);
					assignment.setStartTime(mspAssignment.getStart().toGregorianCalendar().getTime());
					assignment.setEndTime(mspAssignment.getFinish().toGregorianCalendar().getTime());
					assignment.setWork(constructWork(mspAssignment.getWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime())));
					assignment.setWorkComplete(constructWork(mspAssignment.getActualWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime())));
					assignment.setPercentAssigned(Math.round((mspAssignment.getUnits() * 100)));

					TimeZone tz = this.roster.getPerson(assignment.getPersonID()).getTimeZone();
					// If the timezone is null, it was null for the person in
					// the roster.
					// (Maybe because they haven't registered?) Set it to a
					// default for now.
					if (tz == null) {
						tz = this.workingTimeCalendarProvider.getDefaultTimeZone();

						if (tz == null) {
							tz = SessionManager.getUser().getTimeZone();
						}
					}
					assignment.setTimeZone(tz);

					// Save this assignment, we may need to update it
					currentAssignmentMap.put(mappedPersonID, assignment);

				} else {
					// Person already mapped from at least one msp Assignment
					if (assignment.getWork().isZero()) {
						// Current Assignment has zero work; we'll just take the
						// work and percentage of
						// the new assignment as-is
						assignment.setStartTime(mspAssignment.getStart().toGregorianCalendar().getTime());
						assignment.setEndTime(mspAssignment.getFinish().toGregorianCalendar().getTime());
						assignment.setWork(constructWork(mspAssignment.getActualWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime())));
						assignment.setPercentAssigned(Math.round((mspAssignment.getUnits()*100)));

					} else if (constructWork(mspAssignment.getActualWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime())).isZero()) {
						// New assignment work is zero; there is nothing to add
						// or adjust

					} else {
						// Current and new assignment both have work
						// There is work to add and a percentage to adjust
						// Note that we aren't recomputing percentage yet; we do
						// that later once
						// all the work is determined

						// We used to calulate the percentage on the fly, but
						// that failed when
						// we found percentages totally unrelated to work and
						// duration
						// (e.g. assigned 36% to 0.02h over 40 days duration)
						// Cleaner to always calculate percentage based on total
						// work and duration
						mergedWorkResourceIDs.add(mappedPersonID);

						TimeQuantity currentWork = assignment.getWork();
						TimeQuantity additionalWork = constructWork(mspAssignment.getActualWork().getTimeInMillis(mspAssignment.getStart().toGregorianCalendar().getTime()));
						assignment.setWork(currentWork.add(additionalWork));
					}

				}

			}

		}

		// Now determine which assignments we have to recompute percentages for
		Set recomputePercentageResourceIDs;
		if (!ignoredWorkSum.isZero()) {
			// Not all assignments mapped; going to be distributing work
			// Percentages must match work and duration for this to behave
			// We have to recompute percentage for all assignments
			recomputePercentageResourceIDs = currentAssignmentMap.keySet();

		} else {
			// All assignments mapped; only recompute those that have merged
			// work
			// Assignments with one-to-one mappings don't need recomputed
			recomputePercentageResourceIDs = mergedWorkResourceIDs;
		}

		// Now recompute assignment percentages
		recomputePercentages(entry, Collections.unmodifiableMap(currentAssignmentMap), Collections.unmodifiableCollection(recomputePercentageResourceIDs));

		// Now deal with the ignored assignment work
		distributeUnmappedWork(entry, Collections.unmodifiableCollection(currentAssignmentMap.values()), ignoredWorkSum);

		// Now store all the assignments for this schedule entry
		storeAssignments(entry, Collections.unmodifiableCollection(currentAssignmentMap.values()));
	}

	/**
	 * Recomputes assignments percentages for those assignments in the specified
	 * set. <p/> Percentages are updated
	 * 
	 * @param entry
	 *            the schedule entry to which the assignments belong, required
	 *            for determining new percentages
	 * @param assignmentMap
	 *            a map for locating assignments by ID; keys are
	 *            <code>Integer</code> person IDs, values are
	 *            <code>ScheduleEntryAssignment</code>s
	 * @param recomputePercentagePersonIDs
	 *            the ids of assignments to recompute percentages for; each
	 *            element is an <code>Integer</code> person ID
	 */
	private void recomputePercentages(ScheduleEntry entry, Map assignmentMap, Collection recomputePercentagePersonIDs) {

		for (Iterator iterator = recomputePercentagePersonIDs.iterator(); iterator.hasNext();) {
			Integer personID = (Integer) iterator.next();
			ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) assignmentMap.get(personID);
			TimeZone tz = nextAssignment.getTimeZone();

			DefinitionBasedWorkingTimeCalendar definitionBasedWorkingTimeCalendar = new DefinitionBasedWorkingTimeCalendar(tz, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());
            // New percentage is:
			// Current work / (work that can be completed within duration at
			// 100%)
			// Thus, an assignment who now has a total of 16 hours of work on a
			// previously
			// 1 day duration task will now have to work at 200% to complete the
			// work
			TimeQuantity possibleWork = new WorkCalculatorHelper(definitionBasedWorkingTimeCalendar).getWork(entry.getStartTime(), entry.getEndTime(), new BigDecimal("1.00"));
			TimeQuantity currentWork = nextAssignment.getWork();
			BigDecimal percentageDecimal;
			if (!possibleWork.isZero()) {
				percentageDecimal = currentWork.divide(possibleWork, 2, BigDecimal.ROUND_HALF_UP);
			} else {
				percentageDecimal = new BigDecimal("0.00");
			}

			nextAssignment.setPercentAssignedDecimal(percentageDecimal);
		}
	}

	/**
	 * Distributes the unmapped work to the mapped assignments.
	 * 
	 * @param entry
	 *            the schedule entry to which the assignments belong, required
	 *            for the purposes of recomputing percentages
	 * @param assignments
	 *            the mapped assignments, where each element is a
	 *            <code>ScheduleEntryAssignment</code>
	 * @param unmappedWork
	 *            the work to distribute the the assignments
	 */
	private void distributeUnmappedWork(ScheduleEntry entry, Collection assignments, TimeQuantity unmappedWork) {
		if (!unmappedWork.isZero()) {
			// It work must be distributed to the others
			// We create a fake assignment who has all the work
			ScheduleEntryAssignment assignmentToRemove = new ScheduleEntryAssignment();
			assignmentToRemove.setPersonID("1000");
			assignmentToRemove.setWork(unmappedWork);

			// Create a schedule entry containing all the assignments we've
			// created
			// And the fake assignment
			entry.addAssignments(assignments);
			entry.addAssignment(assignmentToRemove);

			// Now remove the fake assignment distributing the work as
			// appropriate
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(entry, workingTimeCalendarProvider);
			calc.assignmentRemoved(assignmentToRemove, TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
		}
	}

	/**
	 * Stores each assignment. <p/> The ErrorReporter is updated if any problems
	 * occur
	 * 
	 * @param entry
	 *            the entry for which assignments are assigned, used for error
	 *            reporting
	 * @param assignments
	 *            the assignments to store, where each element is a
	 *            <code>ScheduleEntryAssignment</code>
	 */
	private void storeAssignments(ScheduleEntry entry, Collection assignments) {
		for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
			ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) iterator.next();

			try {
				assignment.store();
			} catch (PersistenceException e) {
				ErrorDescription ed = new ErrorDescription("Unable to store assignment for person with ID \""
						+ assignment.getPersonID() + "\" " + "on task \"" + entry.getName() + "\"");
				errorReporter.addError(ed);
			}

		}
	}

	/**
	 * Helper method that constructs a work amount from a work value stored in
	 * an XML. <p/> The value is divided by 1000 to get a minute value, then a
	 * time quantity constructed with a scale of 2.
	 * 
	 * @param storedWorkValue
	 *            the raw value in the XML column
	 * @return the time quantity for that work with a scale of 2
	 */
	private TimeQuantity constructWork(double storedWorkValue) {
		BigDecimal workAmountSeconds = new BigDecimal(String.valueOf(storedWorkValue)).movePointLeft(3);
		return new TimeQuantity(workAmountSeconds, TimeQuantityUnit.SECOND).convertTo(TimeQuantityUnit.HOUR, 2);
	}
}
