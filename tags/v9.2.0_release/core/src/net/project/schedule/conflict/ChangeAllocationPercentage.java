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

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAllocationList;
import net.project.resource.ResourceAssignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.IDateCalculator;
import net.project.util.DateUtils;
import net.project.util.NumberFormat;

/**
 * This conflict resolver attempts to resolve an overallocation conflict by
 * changing the percentage of allocation that a user is assigned to a schedule
 * entry.
 *
 * @author Matthew Flower
 * @since Version 7.6.3.
 */
public class ChangeAllocationPercentage implements IOverallocationResolution {
    private final Schedule schedule;
    private final ScheduleEntry scheduleEntry;
    private final ScheduleEntryAssignment assignment;
    private final ResourceAllocationList ral;
    private final int oldAssignmentPercentage;
    private PnCalendar calUtil = new PnCalendar();

    /**
     * This method indicates if the end date that is currently set in the
     * schedule will still be the end date if we changed to allocation to the
     * new percentage identified by the {@link #newAssignmentPercentage} field.
     */
    private boolean scheduleEndDateChanges = false;
    private Date newScheduleEndDate;
    private int newAssignmentPercentage;

    public ChangeAllocationPercentage(Schedule schedule, ScheduleEntry scheduleEntry, ScheduleEntryAssignment assignment, ResourceAllocationList ral) {

        this.schedule = schedule;
        this.scheduleEntry = scheduleEntry;
        this.assignment = assignment;
        this.newScheduleEndDate = schedule.getScheduleEndDate();
        this.ral = ral;
        this.newAssignmentPercentage = assignment.getPercentAssignedInt();
        this.oldAssignmentPercentage = assignment.getPercentAssignedInt();
    }

    /**
     * Do whatever is necessary to resolve the conflict using the current
     * conflict resolution method.
     *
     * @param schedule a <code>Schedule</code> object that we will use to
     * recalculate the schedule after changing the assignment.
     * @param entry a <code>ScheduleEntry</code> which contains the assignment
     * we are changing.
     */
    public void resolveConflict(Schedule schedule, ScheduleEntry entry) throws PersistenceException {
        ScheduleEntryAssignment newAssignment = (ScheduleEntryAssignment)entry.getAssignmentList().getAssignmentMap().get(assignment.getPersonID());
        newAssignment.setPercentAssigned(newAssignmentPercentage);
        newAssignment.store();
        schedule.recalculateTaskTimes();
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
        NumberFormat nf = NumberFormat.getInstance();
        return PropertyProvider.get("prm.schedule.overallocationresolution.changeallocationpercentage.description", new String[] {assignment.getPersonName(), nf.formatNumber(oldAssignmentPercentage), nf.formatNumber(newAssignmentPercentage)});
    }

    /**
     * Indicates if this conflict resolution method can be applied in order to
     * solve a problem.
     *
     * @return a <code>boolean</code> indicating if this resolution method would
     * be at all helpful.
     */
    public boolean isApplicable() throws PersistenceException {
        //If the schedule entry is on the critical path, any change will cause
        //the end date to be changed
        if (scheduleEntry.isCriticalPath()) {
            scheduleEndDateChanges = true;
        }

        //Figure out how far we are over 100.
        int maxAllocation = ral.getMaximumAllocation(scheduleEntry.getStartTime(), scheduleEntry.getEndTime());
        int proposedAllocationDelta = 100 - maxAllocation;
        int proposedAllocation = assignment.getPercentAssignedInt() + proposedAllocationDelta;

        //You might be asking yourself what in the heck this next line of code
        //is.  It is called a loop continuation identifier.  It allows you to
        //continue to a certain while loop.  This is necessary because I have
        //two embedded while loops here and if I just said break
        everReducingAllocationLoop: while (true) {
            //If the proposed allocation is less than zero, then the user has problems
            //other than this schedule entry.  They need to remove this assignment
            //entirely, which is done by a different resolver object.
            if (proposedAllocation <= 0) {
                return false;
            }

            //Find the new end date if we used this new proposed allocation percentage
            assignment.setPercentAssigned(proposedAllocation);

            IDateCalculator assignmentDateCalculator = assignment.getDateCalculator(schedule.getWorkingTimeCalendarProvider());
            Date oldEndDate = assignment.getEndTime();
            Date newEndDate = assignmentDateCalculator.calculateFinishDate(scheduleEntry.getStartTime());

            //See if that date really works by comparing each day to the resource calendar
            Date dateToCompare = calUtil.startOfDay(scheduleEntry.getStartTime());
            while (dateToCompare.before(newEndDate) || dateToCompare.equals(newEndDate)) {
                ResourceAssignment allocation = ral.getAllocationForDate(dateToCompare);
                int percentage = (allocation != null ? allocation.getPercentAssigned() : 0);

                //This allocation has the old overallocated percentage in it, make
                //sure we adjust.
                Date lateDateTest = calUtil.startOfDay(oldEndDate);
                if (dateToCompare.before(lateDateTest) || dateToCompare.equals(lateDateTest)) {
                    percentage += proposedAllocationDelta;
                }

                if (percentage > 100) {
                    //Even after changing the percentage of allocation, there is
                    //still an overallocation.  (Sigh) We'll try again to see if we
                    //can resolve it by lowering the allocation again.
                    proposedAllocationDelta = 100 - percentage;
                    proposedAllocation = proposedAllocationDelta + proposedAllocationDelta;

                    if (proposedAllocation <= 0) {
                        //This isn't going to work.  The only way is to remove the
                        //assignment, which we can't do here.
                        return false;
                    }

                    continue everReducingAllocationLoop;
                }

                dateToCompare = DateUtils.addDay(dateToCompare, 1);
            }

            //This new assignment percentage worked.  We'll store this in case
            //the user decides to apply this new percentage.
            newAssignmentPercentage = proposedAllocation;

            if (newEndDate.after(scheduleEntry.getLatestFinishDate())) {
                scheduleEndDateChanges = true;

                //Figure out what the new schedule end date would be if we made
                //these changes.
                schedule.calculatePotentialSchedule();
                newScheduleEndDate = schedule.getScheduleEndDate();
            }

            break everReducingAllocationLoop;
        }

        return true;
    }

    /**
     * This method indicates if applying this overallocation fix will cause the
     * end date of the schedule to change.
     *
     * @return a <code>boolean</code> indicating if applying this overallocation
     * fix will cause the end date of the schedule to change.
     */
    public boolean doesScheduleEndDateChange() {
        return scheduleEndDateChanges;
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

    public int getNewPercentAssigned() {
        return newAssignmentPercentage;
    }

    public Date getNewScheduleEndDate() {
        return newScheduleEndDate;
    }
}
