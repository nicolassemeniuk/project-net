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

 package net.project.schedule.conflict;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAllocationList;
import net.project.resource.ResourceAssignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryDateCalculator;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.calc.IDateCalculator;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;

/**
 * This class tries to resolve a resource allocation in a task by moving the
 * task to another start date.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class MoveStartDateForward implements IOverallocationResolution {
    /** Calendar object used to do date arithmetic. */
    private PnCalendar cal = new PnCalendar();
    /**
     * Indicates if the completion date of the schedule will change if this
     * resolution is applied.
     */
    private boolean doesScheduleEndDateChange = false;
    /**
     * The schedule entry which contains has a person working on it who is
     * overallocated.
     */
    private final ScheduleEntry scheduleEntry;
    /** The assignment that is overallocated. */
    private final ScheduleEntryAssignment assignment;
    /** The schedule which contains the overallocated schedule entry. */
    private final Schedule schedule;
    /** A map of person id's to <code>ResourceAllocationList</code> objects. */
    private final Map resourceAllocationMap;

    /** The new completion date of the schedule if this resolution is applied. */
    private Date newScheduleEndDate;
    /** The new start date of the schedule entry if this resolution is applied. */
    private Date newScheduleEntryStartDate;

    public MoveStartDateForward(Schedule schedule, ScheduleEntry scheduleEntry, ScheduleEntryAssignment assignment, Map resourceAllocationMap) {
        this.scheduleEntry = scheduleEntry;
        this.assignment = assignment;
        this.schedule = schedule;
        this.resourceAllocationMap = resourceAllocationMap;
    }

    /**
     * This method indicates if applying this overallocation fix will cause the
     * end date of the schedule to change.
     *
     * @return a <code>boolean</code> indicating if applying this overallocation
     * fix will cause the end date of the schedule to change.
     */
    public boolean doesScheduleEndDateChange() {
        return doesScheduleEndDateChange;
    }

    /**
     * This method will return the new end date of the schedule if this fix is
     * applied.
     *
     * @return a <code>Date</code> object which will be the new completion date
     * of this schedule if this overallocation fix is applied.
     */
    public Date newScheduleEndDate() {
        return newScheduleEndDate;
    }

    /**
     * Get the new start date of the schedule entry that we are changing if we
     * apply this fix.
     *
     * @return a <code>Date</code> object which contains the date on which the
     * schedule entry will begin if this fix is applied.
     */
    public Date getNewScheduleEntryStartDate() {
        return newScheduleEntryStartDate;
    }

    /**
     * Do whatever is necessary to resolve the conflict using the current
     * conflict resolution method.
     *
     * @param schedule a <code>Schedule</code> which contains the schedule
     * entry we are going to "fix".
     * @param entry a <code>ScheduleEntry</code> that we are going to
     * resolve the overallocating in.
     */
    public void resolveConflict(Schedule schedule, ScheduleEntry entry) throws PersistenceException {
        entry.setConstraintType(TaskConstraintType.MUST_START_ON);
        entry.setConstraintDate(newScheduleEntryStartDate);
        entry.store(true, schedule);
    }

    /**
     * Get a description of what this conflict resolution method would do to
     * solve the conflict.
     *
     * @return a <code>String</code> containing an human-readable description
     * of what this IConflictResolution object will do to resolve the
     * overallocation.
     */
    public String getDescription() {
        DateFormat df = SessionManager.getUser().getDateFormatter();
        return PropertyProvider.get("prm.schedule.conflict.movestartdateforward.description",
            scheduleEntry.getName(), df.formatDate(newScheduleEntryStartDate));
    }

    /**
     * Indicates if this conflict resolution method can be applied in order to
     * solve a problem.
     *
     * @return a <code>boolean</code> indicating if this resolution method would
     * be at all helpful.
     */
    public boolean isApplicable() throws PersistenceException {
        //Determine whether changing the task using this method will cause the
        //schedule end date to change.
        doesScheduleEndDateChange = scheduleEntry.isCriticalPath();

        //If the schedule entry *HAS* to start on a certain date, it can't be
        //changed.
        if (scheduleEntry.getConstraintType().isDateConstrained()) {
            return false;
        }

        Map assignmentMap = scheduleEntry.getAssignmentList().getAssignmentMap();

        //Find the beginning of the range of dates we are going to check.
        //No need to check the first day -- we already know it doesn't
        //work.
        Date firstDay = DateUtils.addDay(cal.startOfDay(scheduleEntry.getStartTime()), 1);
        //The maximum distance in the future we will check for a
        //task move is 1 year after the end of the current schedule
        //end date.  This is an arbitrary end date -- we just needed
        //to make sure we weren't looking forever.
        Date lastDay = DateUtils.addYear((schedule.getScheduleEndDate() != null ? schedule.getScheduleEndDate() : scheduleEntry.getEndTime()), 1);
        IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
        lastDay = cal.startOfDay(taskDateCalculator.calculateStartDate(lastDay));

        IDateCalculator assignmentDateCalculator = assignment.getDateCalculator(schedule.getWorkingTimeCalendarProvider());

        boolean foundDate = false;
        datefinder: for (Date currentDate = firstDay;
             (currentDate.before(lastDay) || currentDate.equals(lastDay));
             currentDate = DateUtils.addDay(currentDate, 1)) {

            Date newEndDate = assignmentDateCalculator.calculateFinishDate(currentDate);

            //Iterate from this potential start date to the end date.  See if
            //this would resolve overallocation for everyone or for this user
            //only.
            dateIterator: for (Date allocationDate = cal.startOfDay(currentDate);
                 allocationDate.before(newEndDate) || allocationDate.equals(newEndDate);
                 allocationDate = DateUtils.addDay(allocationDate, 1)) {


                //Now check everyone's allocation on these days
                Set assigneeIDs = assignmentMap.keySet();
                for (Iterator it = assigneeIDs.iterator(); it.hasNext();) {
                    String assigneeID = (String)it.next();
                    ResourceAssignment ra = ((ResourceAllocationList)resourceAllocationMap.get(assigneeID)).getAllocationForDate(allocationDate);
                    ScheduleEntryAssignment sea = (ScheduleEntryAssignment)assignmentMap.get(assigneeID);

                    int thisAssigneesPercent = (ra == null ? 0 : ra.getPercentAssigned());
                    Date endAssignmentDate = cal.endOfDay(sea.getEndTime());

                    //If the current date is after the original end of the
                    //assignment, then the amount of assignment derived
                    //from the ResourceAllocation object is wrong because
                    //it doesn't include work for this assignment.  Fix it.
                    if (allocationDate.after(endAssignmentDate)) {
                        thisAssigneesPercent += sea.getPercentAssignedInt();
                    }

                    if (thisAssigneesPercent > 100) {
                        //If we start on this date, there will be an overallocation.  Keep looking.
                        continue datefinder;
                    }
                }
            }

            //We've found a day that is okay.  Use it and stop iterating
            try {
                currentDate = scheduleEntry.getResourceCalendar(schedule.getWorkingTimeCalendarProvider()).ensureWorkingTimeStart(currentDate);
            } catch (NoWorkingTimeException e) {
                //We'll just leave the date at the beginning of the day.
            }
            newScheduleEntryStartDate = currentDate;
            foundDate = true;
            break datefinder;
        }

        if (foundDate) {
            ScheduleEntry testEntry = (ScheduleEntry)schedule.getEntryMap().get(scheduleEntry.getID());

            if (testEntry == null) {
                //Force reload of schedule
                schedule.loadEntries(true, true);
                testEntry = (ScheduleEntry)schedule.getEntryMap().get(scheduleEntry.getID());
            }
            testEntry.setConstraintType(TaskConstraintType.MUST_START_ON);
            testEntry.setConstraintDate(newScheduleEntryStartDate);

            schedule.calculatePotentialSchedule();
            newScheduleEndDate = schedule.getScheduleEndDate();
            return true;
        } else {
            return false;
        }
    }
}
