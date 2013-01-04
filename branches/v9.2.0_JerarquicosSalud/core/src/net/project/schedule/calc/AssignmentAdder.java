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

 package net.project.schedule.calc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.DateCalculatorHelper;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.SimpleTimeQuantity;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentWorkLogFinder;
import net.project.resource.AssignmentWorkLogUtils;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryDateCalculator;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class for adding an assignment to a schedule entry.
 * <p>
 * This task computes updated assignment work, percentages, task work and duration, where necessary.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
class AssignmentAdder {

    private final ScheduleEntryAssignment assignment;
    private final ScheduleEntry scheduleEntry;
    private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;
    
    /**
     * Creates a new assignment adder indicating that the specified assignment is being added to the
     * specified schedule entry.
     * <p>
     * The assignment work, percentage and dates will be calculated and the assignment will be
     * added to the schedule entry.
     * </p>
     * @param assignment the assignment to add
     * @param scheduleEntry the schedule entry to which to add the assignment
     * @param provider the provider of resource and schedule working time calendars
     * @throws IllegalArgumentException if the schedule entry already has an assignment for
     * the resource person ID of the assignment being added
     */
    public AssignmentAdder(ScheduleEntryAssignment assignment, ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider provider) {

        // Check to see if the schedule entry already has an assignment for this assignment resource
        if (scheduleEntry.getAssignmentList().containsForResource(assignment)) {
            throw new IllegalArgumentException("Schedule entry with ID " + scheduleEntry.getID() + " already has an assignment for resource " + assignment.getPersonID());
        }

        this.assignment = assignment;
        this.scheduleEntry = scheduleEntry;
        this.workingTimeCalendarProvider = provider;
    }

    public void addAssignment(BigDecimal newPercentageDecimal) throws NoWorkingTimeException {
        TaskCalculationType calcType = scheduleEntry.getTaskCalculationType();

        //Illegal Operation checks
        if (calcType.isFixedDuration() && calcType.isEffortDriven() && newPercentageDecimal != null) {
            throw new IllegalArgumentException("Cannot specify assignment percentage when adding assignment to Fixed Duration, Effort Driven task");
        }
        if (!calcType.isFixedDuration() && newPercentageDecimal == null) {
            throw new IllegalArgumentException("Assignment unit (percentage of effort) must be specified for fixed unit and fixed work tasks.");
        }

        //We will automatically calculate percentage for a fixed duration, non-effort driven task
        if (calcType.isFixedDuration() && calcType.isEffortDriven()) {
            newPercentageDecimal = determineDefaultAssignedPercentage();
        }

        /**
         * Figure out how much work the assignment could possibly do.  They
         * aren't going to necessarily do this much, but it does serve to show
         * how work can be distributed in a fair way.
         */
        TimeQuantity potentialWork = null;
        if (calcType.isFixedDuration()) {
            //We must take into account this assignment's schedule so we don't
            //violate the fixed duration condition.
            potentialWork = calculatePossibleWorkForResource(newPercentageDecimal);
        } else {
            potentialWork = calculatePossibleWorkForSchedule(newPercentageDecimal);
        }
        
        /**
         * Sachin: If the potential work to share or add is 0 but schedule entry does have
         * some work, this means that the assignment cannot be added to the schedule enty
         * we throw and exception here to be handled by the calling handler.
         */
        if(TimeQuantity.O_HOURS.equals(potentialWork) && scheduleEntry.getWorkTQ().getAmount().signum() != 0) {
            String args[] = {assignment.getPersonID(), scheduleEntry.getNameMaxLength40(), scheduleEntry.getStartTimeString(), scheduleEntry.getEndTimeString()};
            throw new NoWorkingTimeException(PropertyProvider.get("prm.resource.addassignment.error.noworkingtime.message", args));            
        }

        if (calcType.isEffortDriven()) {
            shareAssignmentWork(potentialWork, newPercentageDecimal);
        } else {
            addAssignmentWork(potentialWork, newPercentageDecimal);
        }

        // Recalculate assignment end dates
        ScheduleEntryDateCalculator dateCalc = new ScheduleEntryDateCalculator(this.scheduleEntry, this.workingTimeCalendarProvider);
        dateCalc.addWorkAndupdateAssignmentDates(scheduleEntry.getStartTime());
    }

    //sjmittal: due to decoupling no work complete is ever re distributed among assignments
//    public void calculateWorkComplete() {
//        redistributeWorkComplete(scheduleEntry, workingTimeCalendarProvider);
//        assignment.setDistributedWorkComplete(assignment.getWorkCompleteDeltaToStore());
//    }

//    public static void redistributeWorkComplete(ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider provider) {
//	    //First, figure out the amount of work that needs to be distributed
//	    TimeQuantity workCompleteToDistribute = scheduleEntry.getUnallocatedWorkComplete();
//	    workCompleteToDistribute = ScheduleTimeQuantity.convertToHour(workCompleteToDistribute);
//	
//	    //If work has already been distributed before, add it back into the pool
//	    //of all work to be distributed.  We need to do this because if we add
//	    //multiple assignments, we need to distribute this work evenly.
//	    for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
//	        ScheduleEntryAssignment assn = (ScheduleEntryAssignment)it.next();
//	        workCompleteToDistribute = workCompleteToDistribute.add(ScheduleTimeQuantity.convertToHour(assn.getWorkCompleteDeltaToStore()));
//	        assn.setWorkCompleteDeltaToStore(new TimeQuantity(0, TimeQuantityUnit.HOUR));
//	    }
//	
//	    //Now, redistribute all the work complete to the assignments, keeping
//        //track of the undistributed work
//	    TimeQuantity undistributedWork = workCompleteToDistribute;
//	    TimeQuantity seWork = ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkTQ());
//	
//	    for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
//	        ScheduleEntryAssignment assn = (ScheduleEntryAssignment)it.next();
//	        BigDecimal multiplier;
//	
//	        if (scheduleEntry.getWorkTQ().isZero()) {
//	            multiplier = new BigDecimal(1);
//	        } else {
//	            multiplier = ScheduleTimeQuantity.convertToHour(assn.getWork()).divide(seWork, 10, BigDecimal.ROUND_HALF_UP);
//	        }
//	        TimeQuantity assnWorkComplete = workCompleteToDistribute.multiply(multiplier);
//	        //sjmittal:it should be previous work complete plus new assignment
//	        assn.setWorkComplete(assn.getWorkComplete().add(assnWorkComplete));
//	
//	        if (!assn.getWorkCompleteDeltaToStore().isZero()) {
//	            assn.calculateWorkCompleteDeltaDates(provider, scheduleEntry.getStartTime());
//	        }
//	
//	        //Keep track of how much undistributed work there is so we can set it
//	        //back into the schedule entry if needed.
//	        undistributedWork = undistributedWork.subtract(assnWorkComplete);
//	    }
//	    
//	    scheduleEntry.setUnallocatedWorkComplete(undistributedWork);
//	}

    /**
     * Determines the percentage assigned when adding a resource with the intention of
     * recalculating percentages.
     * <p>
     * This value is equal to the largest current assignment percentage, or 100% if there are no
     * assignments.
     * </p>
     * <p>
     * For example, given two assignments at 50%, the percentage of the added resource will initally be 50%.
     * Given two assignments at 50% and 75%, the percentage of the added resource will initally be 75%.
     * Given two assignments at 50% and 150%, the percentage of the added resource will initally be 150%.
     * </p>
     * @return the largest assignment percentage or <code>1.00</code>
     */
    private BigDecimal determineDefaultAssignedPercentage() {
        BigDecimal largestValue;

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            largestValue = new BigDecimal("1.00");

        } else {
            largestValue = null;

            // Iterate over all assignments stopping if we find an inconsistent percentage assigned value
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext(); ) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

                BigDecimal nextPercentage = nextAssignment.getPercentAssignedDecimal();
                if (largestValue == null) {
                    // First assignment
                    largestValue = nextPercentage;

                } else if (nextPercentage.compareTo(largestValue) > 0) {
                    // Assignment percentage is greater
                    largestValue = nextPercentage;

                }

            }

            // Check to see if the largest value is over 100%; if so, we use 100% -- (sjmittal) why ??!!
            // Further this fixes bfd 2490
//            if (largestValue.compareTo(new BigDecimal("1.00")) > 0) {
//                largestValue = new BigDecimal("1.00");
//            }

        }

        return largestValue;
    }

    /**
     * Calculates the amount of work that a resource could do between the schedule
     * entry's start and end dates using the schedule default work hours per day
     * at the specified percentage.
     * @param percentageAssignedDecimal the percentage assigned to use to compute
     * the possible work completed
     * @return the computed work that can be done
     */
    private TimeQuantity calculatePossibleWorkForSchedule(BigDecimal percentageAssignedDecimal) {
        /*
        //sjmittal: we should get work between schedule entry's start and end time using the assignment's working calender
        //and not construct any WorkingTimeCalendarDefinition because same is present inside WorkingTimeCalendar
        DefinitionBasedWorkingTimeCalendar calendar =  this.assignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);
        TimeQuantity work = new WorkCalculatorHelper(calendar).getWork(scheduleEntry.getStartTime(), this.scheduleEntry.getEndTime(), percentageAssignedDecimal);
        TimeQuantity possibleWork = work.convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
        */
        WorkingTimeCalendarDefinition calDef = workingTimeCalendarProvider.getForResourceID(this.assignment.getPersonID());
        TimeZone timeZone = assignment.determineTimeZone(this.workingTimeCalendarProvider); 
        if(calDef == null) {
            calDef = workingTimeCalendarProvider.getDefault();
            // if resource do not have personal or project calendar then use timezone of schedule 
            timeZone = workingTimeCalendarProvider.getDefaultTimeZone();
        }

		SimpleTimeQuantity workingTimeAmountForDateRange = calDef.getWorkingTimeAmountForDateRange(scheduleEntry.getStartTime(), scheduleEntry.getEndTime(), timeZone);
		TimeQuantity possibleWork = new TimeQuantity(workingTimeAmountForDateRange.toHour(), TimeQuantityUnit.HOUR).multiply(percentageAssignedDecimal);
        return possibleWork;
    }

//    private boolean isAllWorkAccountedForByAssignments() {
//        //One slight problem that has been introduced by various features is the
//        //idea that we can have an assignment that does not encompass all of the
//        //work possible by the schedule entry.  This can occur if no one is invited
//        //to the task and someone logs some work.  An 8 hour task would become a
//        //9 hour task if someone logged 1 hour of work in this scenario -- but there
//        //would be a total of 1 hour of work assigned.
//        //
//        //To solve this problem, we need to try to detect it
//
//        //Check that the total assignment work equals the total amount of
//        //task work.  If not, we cannot use duration directly.
//        TimeQuantity assignmentWork = TimeQuantity.O_HOURS;
//        for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
//            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
//            if (assignment.getWork() == null) {
//                continue;
//            }
//            assignmentWork = assignmentWork.add(ScheduleTimeQuantity.convertToHour(assignment.getWork()));
//        }
//
//        return (assignmentWork.compareTo(ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkTQ())) != 0);
//    }

    /**
     * Calculates the amount of work that the assigned resource could do between the schedule
     * entry's start and end dates using their personal working time calendar working
     * at the specified percentage.
     * @param percentageAssignedDecimal the percentage assigned to use to compute
     * the possible work completed
     * @return the computed work that can be done (in hours)
     */
    private TimeQuantity calculatePossibleWorkForResource(BigDecimal percentageAssignedDecimal) {
        TimeQuantity possibleWork = null;

        //The task is a fixed duration task.  This means that we have to
        //look for the actual amount of time that a user can work in this
        //time period so we don't screw up the duration.

        //If work has been completed, we need to calculate the remaining duration
        //sjmittal: note that one may diectly update the work complete
        //in this case this work complete is un allocated, so really no actual work has taken place
        //thus to get the actual work we need to subtract the two figures
        if (ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkCompleteTQ()).subtract(scheduleEntry.getUnallocatedWorkComplete()).isZero()) {
            DefinitionBasedWorkingTimeCalendar calendar =  this.assignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);
            TimeQuantity work = new WorkCalculatorHelper(calendar).getWork(this.scheduleEntry.getStartTime(), this.scheduleEntry.getEndTime(), percentageAssignedDecimal);
            possibleWork = work.convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
        } else {
            //Some work has already transpired.  This makes it difficult to
            //determine how much duration is really remaining because it is quite
            //possible that all assignmees aren't up to date to the second as far
            //as how much work they've completed.

            //Until I can think of a better way, we'll choose the maximum amount
            //of time we reasonably can and figure that anyone who has done more
            //is working harder than they should.

            //Iterate through the assignment to figure out up to when work has
            //been reported.
            Date earliestAssignmentDate = scheduleEntry.getStartTime();
            for (Iterator it = scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
                DefinitionBasedWorkingTimeCalendar calendar =  assn.getWorkingTimeCalendar(workingTimeCalendarProvider);
                DateCalculatorHelper helper = new DateCalculatorHelper(calendar);
                Date currentAssignmentDate = helper.calculateDate(assn.getStartTime(), assn.getWorkComplete(), assn.getPercentAssignedDecimal());
                earliestAssignmentDate = DateUtils.min(earliestAssignmentDate, currentAssignmentDate);
            }

            //Now calculate how much work we think can be done
            DefinitionBasedWorkingTimeCalendar calendar =  this.assignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);
            TimeQuantity work = new WorkCalculatorHelper(calendar).getWork(earliestAssignmentDate, this.scheduleEntry.getEndTime(), percentageAssignedDecimal);
            possibleWork = work.convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
        }

        return possibleWork;
    }

    /**
     * Modifies assignment work to share a portion of each assignment's work
     * with the new assignment such that the new assignment's work is the specified
     * amount.
     * <p>
     * The assignment is added to the schedule entry.
     * </p>
     * <p>
     * The task work will remain constant since there will be no gain in work, just
     * a redivision of work.
     * Each assignment's work remains relative to all other assignment work.
     * </p>
     * @param percentAssignedDecimal the percentage assigned of the new assignment
     */
    private boolean shareAssignmentWork(TimeQuantity possibleAssignmentWork, BigDecimal percentAssignedDecimal) {
        boolean percentageCalculated = false;

        //Find work that the assignment has previously worked.
        TimeQuantity previousAssignmentWork = findPreviousAssignmentWork(scheduleEntry, assignment);

        //We store work that has been done by unassigned people in the "unassociated work"
        //bucket.  Now that this person is assigned again, remove their old work
        //from that bucket.
        TimeQuantity taskUnassociatedWork = scheduleEntry.getUnassociatedWorkComplete().subtract(previousAssignmentWork);
        
        //this does not makes sense.
        //Say assignment had worked for x hrs and then assigned.
        //then perhaps we modified another assignment and re-distiributed the work (but not submited)
        //it can be quite possible that the new work is less the then the previous assignment work
        //so now if we remove that assignment (new work becomes un associated work) and
        //then again assign it sometime later then the un associated work < previous assignment work
        //assert taskUnassociatedWork.getAmount().signum() >= 0 : "Task unassociated work cannot be less than zero";
        
        /**** assumptions for work distribution starts *****************/
        if(taskUnassociatedWork.getAmount().signum() >= 0)
        	scheduleEntry.setUnassociatedWorkComplete(taskUnassociatedWork);
        else 
        	scheduleEntry.setUnassociatedWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

//        if(possibleAssignmentWork.compareTo(previousAssignmentWork) > 0) {
        possibleAssignmentWork = possibleAssignmentWork.add(previousAssignmentWork);
//            assignment.setPreviousWorkComplete(previousAssignmentWork);
//        } else {
//        	possibleAssignmentWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
//        }
        //sjmittal: update respective work complete
        scheduleEntry.setWork(scheduleEntry.getWorkTQ().add(ScheduleTimeQuantity.convertToUnit(previousAssignmentWork, scheduleEntry.getWorkTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP))); 
        scheduleEntry.setWorkComplete(scheduleEntry.getWorkCompleteTQ().add(ScheduleTimeQuantity.convertToUnit(previousAssignmentWork, scheduleEntry.getWorkCompleteTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP)));        
        assignment.setWorkComplete(assignment.getWorkComplete().add(previousAssignmentWork));
        /**** assumptions for work distribution ends *****************/
        
        //Set the amount of work there are previous logs for.  (This shows up in getWorkComplete() too)
        //sjmittal: duplicate line
        //assignment.setPreviousWorkComplete(previousAssignmentWork);

        // Set their percentage
        this.assignment.setPercentAssignedDecimal(percentAssignedDecimal);
        this.scheduleEntry.addAssignment(this.assignment);

        if (scheduleEntry.getAssignmentList().size() == 1) {
            percentageCalculated = shareWorkFirstAssignment(possibleAssignmentWork);
        } else {
            percentageCalculated = shareWorkNotFirstAssignment(possibleAssignmentWork);
        }


        return percentageCalculated;
    }


    private boolean shareWorkFirstAssignment(TimeQuantity possibleAssignmentWork) {
        //Get work converted into the standard "hours".
        TimeQuantity taskWork = WorkHelper.getConvertedWork(this.scheduleEntry);
        if (taskWork.isZero()) {
            // First assignment, no task work
            // Task work set from what assignment can do
            WorkHelper.setConvertedWork(this.assignment, possibleAssignmentWork);
            WorkHelper.setConvertedWork(this.scheduleEntry, possibleAssignmentWork);
        } else {
            // Assignment completes the task work, but doesn't complete work that other
            // assignees have already completed.
            //sjmittal: note any un associated work has already been subtracted from the tasks work
            //while removing that task
            WorkHelper.setConvertedWork(this.assignment, taskWork);
            //.subtract(this.scheduleEntry.getUnassociatedWorkComplete()));
        }

        //Recalculate duration, if necessary
        boolean percentageRecalculated = false;
        if (scheduleEntry.getTaskCalculationType().isFixedDuration()) {
            BigDecimal oldAssignmentPercentage = assignment.getPercentAssignedDecimal();

            TimeQuantity possibleAssignmentDuration = assignment.getAssignmentDuration(workingTimeCalendarProvider, scheduleEntry.getStartTime(), scheduleEntry.getEndTime());

            BigDecimal newAssignmentPercentage;
            if (possibleAssignmentDuration.isZero()) {
                newAssignmentPercentage = new BigDecimal("0.0000000000");
            } else {
                newAssignmentPercentage = assignment.getWork().divide(possibleAssignmentDuration, 10, BigDecimal.ROUND_HALF_UP);
            }

            assignment.setPercentAssignedDecimal(newAssignmentPercentage);

            if (!oldAssignmentPercentage.equals(newAssignmentPercentage)) {
                percentageRecalculated = true;
            }
        }
        
        //We need to recalculate duration before adjusting for round-off error
        if (/*percentageRecalculated || */!scheduleEntry.getTaskCalculationType().isFixedDuration()) {
            scheduleEntry.calculateDuration(workingTimeCalendarProvider);
        }

        return percentageRecalculated;
    }

    /**
     * The assignment being added isn't the first assignment.  This means that
     * our primary concern is to figure out how much work is left to do and to
     * distribute it in a way that is appropriate.
     * 
     * @param possibleAssignmentWork
     */
    private boolean shareWorkNotFirstAssignment(TimeQuantity possibleAssignmentWork) {
        boolean percentageRecalculated = false;

        // Assignment work is amount that they could have done if they were working 100%
//        TimeQuantity assignmentWork = possibleAssignmentWork;
//        assignment.setWork(assignmentWork);

        //The main bulk of what is going to happen when sharing work when we aren't
        //adding the first assignment boils down to 3 cases:
        //  1. There is no assignment work, do nothing
        //  2. There is no task work -- let's create some
        //  3. There is task work, redistribute it
/*        if (assignment.getWork().getAmount().signum() == 0) {
            //If new assignment work is zero, then no adjustments are
            //necessary.  This is because the new assignment doesn't need any
            //work so there is no work to take away from everyone else.
        } else*/
        if (scheduleEntry.getWorkTQ().isZero()) {
            //If current task work is zero, we assume that the total amount of
            //work in the task is the amount of work that an assignment could
            //do in this amount of time.  I'm not really sure why there are other
            //assignments with zero work.
            WorkHelper.setConvertedWork(this.scheduleEntry, this.assignment.getWork());
        } else {
            //Calculate all remaining assignment work
            TimeQuantity allRemainingAssignmentWork = TimeQuantity.O_HOURS;
            TimeQuantity allPreviousAssignmentWork = TimeQuantity.O_HOURS;
            for (Iterator it = scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();

                if (assn.equals (this.assignment)) {
                    //Don't let previous assignment completed work skew the possible work.
                    //It would normally be subtracted.
                    allRemainingAssignmentWork = allRemainingAssignmentWork.add(possibleAssignmentWork);
                } else {
                    allRemainingAssignmentWork = allRemainingAssignmentWork.add(ScheduleTimeQuantity.convertToHour(assn.getWorkRemaining()));
                    allPreviousAssignmentWork = allPreviousAssignmentWork.add(ScheduleTimeQuantity.convertToHour(assn.getWorkRemaining()));
                }
            }
            TimeQuantity divisor = allRemainingAssignmentWork;
            TimeQuantity multiplier = allPreviousAssignmentWork;
            //allRemainingAssignmentWork = allRemainingAssignmentWork.subtract(possibleAssignmentWork);

            // We must adjust all assignments and compute the task work as the sum of those
            TimeQuantity computedTaskWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            // Adjust each assignment's work by the ratio of old work to new work
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

                TimeQuantity remainingAssignmentWork;
                if (nextAssignment.equals(this.assignment)) {
                    remainingAssignmentWork = possibleAssignmentWork;
                } else {
                    remainingAssignmentWork = nextAssignment.getWorkRemaining();
                    //remainingAssignmentWork = possibleAssignmentWork;
                }

                TimeQuantity newAssignmentWork;
                if (divisor.isZero()) {
                    newAssignmentWork = TimeQuantity.O_HOURS;
                } else {
                    newAssignmentWork = multiplier.multiply(remainingAssignmentWork.divide(divisor, 10, BigDecimal.ROUND_HALF_UP));
                }

                //Work complete isn't accounted for in the work we are going to distribute,
                //so it needs to be added manually.
                newAssignmentWork = newAssignmentWork.add(nextAssignment.getWorkComplete());

                nextAssignment.setWork(newAssignmentWork);
                
                //sjimittal: this looks better re calculate the % but not the duration for FD tasks
                if (scheduleEntry.getTaskCalculationType().isFixedDuration()) {
                    BigDecimal oldAssignmentPercentage = nextAssignment.getPercentAssignedDecimal();

                    TimeQuantity possibleAssignmentDuration = nextAssignment.getAssignmentDuration(workingTimeCalendarProvider, scheduleEntry.getStartTime(), scheduleEntry.getEndTime());

                    BigDecimal newAssignmentPercentage;
                    if (possibleAssignmentDuration.isZero()) {
                        newAssignmentPercentage = new BigDecimal("0.0000000000");
                    } else {
                        newAssignmentPercentage = nextAssignment.getWork().divide(possibleAssignmentDuration, 10, BigDecimal.ROUND_HALF_UP);
                    }
                    nextAssignment.setPercentAssignedDecimal(newAssignmentPercentage);

                    if (!oldAssignmentPercentage.equals(newAssignmentPercentage)) {
                        percentageRecalculated = true;
                    }
                }

                // We maintain precision when computing task work
                computedTaskWork = computedTaskWork.add(newAssignmentWork);
            }

            computedTaskWork = computedTaskWork.add(scheduleEntry.getUnassociatedWorkComplete());

            //We need to recalculate duration before adjusting for round-off error
            if (/*percentageRecalculated ||*/ !scheduleEntry.getTaskCalculationType().isFixedDuration()) {
                scheduleEntry.calculateDuration(workingTimeCalendarProvider);
            }

            adjustForRoundOffError(computedTaskWork);
        }

        return percentageRecalculated;
    }

    private void adjustForRoundOffError(TimeQuantity computedTaskWork) {
        //Adjust for round-off error.  Why do we have to do this in the first place?
        //It has to do with bad math.  Check this out:
        //     x,y,z are rational numbers
        //     x + y = z
        //This does not imply that
        //     round(x, 1) + round(y,1) = round(z,1)
        //e.g.
        //     2.55 + 2.55 = 3.10
        //     round(2.55, 1) + round(2.55, 1) = 3.20 <> 3.10
        TimeQuantity roundOffError = this.scheduleEntry.getWorkTQ();
        for (Iterator it = scheduleEntry.getAssignments().iterator(); it.hasNext();) {
            ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
            roundOffError = roundOffError.subtract(assn.getWork());
        }
        BigDecimal amount = roundOffError.getAmount();
        //sjmittal: should be (-0.00001, 0) U (0, 0.00001)
		if (amount.signum() != 0 && (amount.compareTo(new BigDecimal("0.00001")) < 0 && amount.compareTo(new BigDecimal("-0.00001")) > 0)) {
            //There is some round off error.  Assign it to the first assignee that has work but isn't complete
            for (Iterator it = scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();

                if (assn.getWork().getAmount().signum() != 0 && !assn.isComplete()) {
                    assn.setWork(assn.getWork().add(roundOffError));
                    computedTaskWork = computedTaskWork.add(roundOffError);
                    break;
                }
            }
        }


        WorkHelper.setConvertedWork(this.scheduleEntry, computedTaskWork);
    }

    /**
     * Find work that the person has previously been assigned and completed.
     * Seems how this person is no longer assigned, this work is accounted for
     * separately in the "Unassociated Work" category of the task.
     *
     * @return a <code>TimeQuantity</code> indicating the amount of time this
     * person has already worked on this task.
     */
    private static TimeQuantity findPreviousAssignmentWork(ScheduleEntry scheduleEntry, ScheduleEntryAssignment assignment) {

        //First check if the user has previous done work and is the source
        //of "unassociated work".  If so, then the work will revert back
        //to being theirs
        TimeQuantity unassociatedWork = TimeQuantity.O_DAYS;
        try {
            List assignmentLogs = new AssignmentWorkLogFinder().findByObjectIDPersonID(scheduleEntry.getID(), assignment.getPersonID());
            unassociatedWork = AssignmentWorkLogUtils.getWorkLoggedForAssignee(assignmentLogs, assignment.getPersonID());
        } catch (PersistenceException e) {
            throw new PnetRuntimeException(e);
        }
        return unassociatedWork;
    }

    /**
     * Sets the specified work in the new assignment and calculates a new task work
     * by adding up all assignment work.
     * <p>
     * The assignment is added to the schedule entry and their percent assigned is set to
     * the specified value.
     * </p>
     * @param additionalWork the new assignment's work
     * @param percentAssignedDecimal the percentage assigned
     */
    private void addAssignmentWork(TimeQuantity additionalWork, BigDecimal percentAssignedDecimal) {
        this.assignment.setWork(additionalWork);
        this.assignment.setPercentAssignedDecimal(percentAssignedDecimal);
        this.scheduleEntry.addAssignment(this.assignment);
        WorkHelper.updateWorkFromAssignments(this.scheduleEntry);
    }

}