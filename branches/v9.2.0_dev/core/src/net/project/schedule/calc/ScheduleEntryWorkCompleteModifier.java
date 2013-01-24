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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.calc;

import java.math.BigDecimal;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Class to clean up all the dependencies in work complete when work complete
 * has been modified for a schedule entry.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
class ScheduleEntryWorkCompleteModifier {
    /**
     * Set the work complete for a schedule entry and update all the necessary
     * dependencies for the work complete.
     *
     * The work complete should not have already been added prior to calling
     * this method, otherwise it won't be able to work properly.
     *
     * @param scheduleEntry a <code>ScheduleEntry</code> which has a new work
     * complete value.
     * @param workComplete the new value for work complete.
     */
    public static void computeWorkComplete(IWorkingTimeCalendarProvider provider, ScheduleEntry scheduleEntry, TimeQuantity workComplete) {
        TimeQuantityUnit originalUnit = workComplete.getUnits();
        workComplete = ScheduleTimeQuantity.convertToHour(workComplete);
        TimeQuantity oldWorkComplete = ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkCompleteTQ());

        if (workComplete.compareTo(scheduleEntry.getWorkTQ()) > 0) {
            throw new PnetRuntimeException(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
        }

        //Figure out the delta between the old work and the new work
        TimeQuantity delta = workComplete.subtract(oldWorkComplete);

        if (delta.isZero()) {
            //Do nothing
        } else if (delta.getAmount().signum() < 0) {
            //The user has subtracted work.  This is fine, as long as any of the
            //assignees hasn't already done that work.  If they have, we need to
            //make sure that we don't make them be assigned to less work than they
            //have already completed
            handleNegativeWorkDelta(provider, scheduleEntry, delta);
        } else {
            handlePositiveWorkDelta(provider, scheduleEntry, delta);
        }

        //Now set the work complete for the schedule entry
        TimeQuantity newWorkComplete = oldWorkComplete.add(delta);
        newWorkComplete = ScheduleTimeQuantity.convertToUnit(newWorkComplete, originalUnit, 3, BigDecimal.ROUND_HALF_UP);
        scheduleEntry.setWorkComplete(newWorkComplete);
    }

    /**
     * Handle simple work delta distributes a positive amount of work between
     * the assignees according to their expected effort.  We have to guess
     * because we aren't specifying who really did the work.
     *
     * In some cases, this is quite easy.  A multiplier is created by dividing
     * the amount of work the assignee does by the amount of work in a task.
     * This fraction will represent the expected effort.
     *
     * Unfortunately, this (as everything else) becomes more difficult at times.
     * If an assignee has already done so much work that they cannot take their
     * share, the portion of the work they cannot take from their share stays in
     * the pool.  We loop repeatedly until we can resolve this.
     *
     * @param entry a <code>ScheduleEntry</code> which has new work complete
     * that we are going to allocate among its assignees.
     * @param delta a <code>TimeQuantity</code> containing the new amount of work
     * to distribute.
     */
    private static void handlePositiveWorkDelta(IWorkingTimeCalendarProvider provider, ScheduleEntry entry, TimeQuantity delta) {
//        assert delta.getAmount().signum() >= 0 : "Only positive work amounts are " + "handled by handlePositiveWorkDelta.";

        // This is additional work, distribute it among the assignees
        //sjmittal: not anymore due to de-coupling
//        List assignments = entry.getAssignmentList().getAssignments();
//
//        if (assignments.size() > 0) {
//            distributeDelta(delta, entry, assignments, provider);
//
//        } else {
            //There is no assignments to allocate this work to.  Save it until there is.
            entry.setUnallocatedWorkComplete(entry.getUnallocatedWorkComplete().add(delta));
//        }
    }

    private static void handleNegativeWorkDelta(IWorkingTimeCalendarProvider provider, ScheduleEntry scheduleEntry, TimeQuantity delta) {
    	//sjmittal: no delta work is distributed among assignees any more due to decoupling
//        AssignmentList assignments = scheduleEntry.getAssignmentList();
//        TimeQuantity taskWork = ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkTQ());
//        Set unableToTakeWorkComplete = new HashSet();

//        boolean assigneeHasTooMuchWork = false;
//        TimeQuantity workAvailable = new TimeQuantity(0, TimeQuantityUnit.HOUR);
//        TimeQuantity totalWorkOfThoseWhoCan = new TimeQuantity(0, TimeQuantityUnit.HOUR);

//        if (assignments.size() > 0) {
//            for (Iterator it = assignments.getAssignments().iterator(); it.hasNext();) {
//                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
//
//                TimeQuantity assnWorkDelta;
//                if (!taskWork.isZero()) {
//                    assnWorkDelta = delta.multiply(
//                        assn.getWork().divide(taskWork, 10, BigDecimal.ROUND_HALF_UP))
//                        .convertToScale(2, BigDecimal.ROUND_HALF_UP);
//                } else {
//                    assnWorkDelta = TimeQuantity.O_HOURS;
//                }
//
//                if (assnWorkDelta.add(assn.getWorkComplete()).getAmount().signum() < 0) {
//                    //This assignee can have this much work subtracted
//                    assigneeHasTooMuchWork = true;
//                    unableToTakeWorkComplete.add(assn);
//                } else {
//                    workAvailable = workAvailable.add(assnWorkDelta.add(assn.getWorkComplete()));
//                    totalWorkOfThoseWhoCan = totalWorkOfThoseWhoCan.add(assn.getWork());
//                }
//            }
//
//            if (!assigneeHasTooMuchWork) {
//                distributeDelta(delta, scheduleEntry, assignments.getAssignments(), provider);
//            } else if (workAvailable.compareTo(delta.abs()) > 0) {
//                //There is more work that we can do.  This doesn't make sense,
//                //throw an error.
//                throw new PnetRuntimeException("There has been more work completed than was assigned, unable to allocate work");
//            }
//        } else {
            scheduleEntry.setUnallocatedWorkComplete(scheduleEntry.getUnallocatedWorkComplete().add(delta));
//        }
    }

//    private static void distributeDelta(TimeQuantity delta, ScheduleEntry entry, List assignments, IWorkingTimeCalendarProvider provider) {
//        //Establish the amount of work that was available to be distributed before
//        //we looped through the assignments.  If this doesn't change between
//        //iterations, we are going to fail because we aren't making any progress.
//        TimeQuantity preLoopWork = delta;
//        TimeQuantity workToAllocate = delta;
//
//        //Assignments with modified work complete
//        Set modifiedAssignments = new HashSet();
//
//        //This map relates a person id to a BigDecimal (containing a fraction)
//        //which indicates the amount of work the user should be doing.
//        Map expectedContribution = new HashMap();
//
//        do {
//            //Find assignees that cannot contribute to the work being distributed
//            //because:
//            //  1. The delta is positive and they have already completed their work
//            //  2. The delta is negative and they haven't done any work yet.
//            //
//            // We subtract their work from the task work that we use to figure out
//            // the expected contribution.  (If there are two assignees and 1 is done,
//            // the other shouldn't still get half of the work.)
//            TimeQuantity entryWork = ScheduleTimeQuantity.convertToHour(entry.getWorkTQ());
//            for (Iterator it = assignments.iterator(); it.hasNext();) {
//                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
//
//                if (delta.getAmount().signum() >= 0 && assn.getWorkRemaining().isZero()) {
//                    entryWork = entryWork.subtract(ScheduleTimeQuantity.convertToHour(assn.getWork()));
//                } else if (delta.getAmount().signum() < 0 && assn.getWorkComplete().isZero()) {
//                    entryWork = entryWork.subtract(ScheduleTimeQuantity.convertToHour(assn.getWork()));
//                }
//            }
//
//            //Calculate the expected contribution from each assignee.
//            for (Iterator it = assignments.iterator(); it.hasNext();) {
//                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
//                if (delta.getAmount().signum() >= 0 && assn.getWorkRemaining().isZero()) {
//                    expectedContribution.put(assn.getPersonID(), new BigDecimal(0));
//                } else if (delta.getAmount().signum() < 0 && assn.getWorkComplete().isZero()) {
//                    expectedContribution.put(assn.getPersonID(), new BigDecimal(0));
//                } else {
//                    TimeQuantity assnWork = ScheduleTimeQuantity.convertToHour(assn.getWork());
//                    BigDecimal contribution;
//                    if (entryWork.isZero()) {
//                        contribution = new BigDecimal("0.0000000000");
//                    } else {
//                        contribution = assnWork.divide(entryWork, 10, BigDecimal.ROUND_HALF_UP);
//                    }
//                    expectedContribution.put(assn.getPersonID(), contribution);
//                }
//            }
//
//            //Now, iterate through the assignments and allocate work
//            for (Iterator it = assignments.iterator(); it.hasNext();) {
//                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
//
//                BigDecimal contribution = (BigDecimal)expectedContribution.get(assn.getPersonID());
//                if (contribution.signum() != 0) {
//                    TimeQuantity newWork = preLoopWork.multiply(contribution);
//                    TimeQuantity remainingWork = ScheduleTimeQuantity.convertToHour(assn.getWorkRemaining());
//
//                    //Make sure we are giving more work than the user can take.
//                    if (newWork.compareTo(remainingWork) > 0) {
//                        newWork = remainingWork;
//                    }
//
//
//                    //There may be a slight round-off error (e.g. .333333333 * 24)
//                    //If so, adjust to make sure we are doing the correct amount of
//                    //work
//                    if (remainingWork.subtract(newWork).getAmount().signum() > 0 &&
//                        remainingWork.subtract(newWork).getAmount().compareTo(new BigDecimal("0.00001")) < 0) {
//                        newWork = newWork.add(remainingWork.subtract(newWork));
//                    }
//
//                    assn.setWorkComplete(assn.getWorkComplete().add(newWork));
//                    workToAllocate = workToAllocate.subtract(newWork);
//
//                    modifiedAssignments.add(assn);
//                }
//            }
//
//
//            if (preLoopWork.equals(workToAllocate) && workToAllocate.getAmount().compareTo(new BigDecimal("0.00001")) > 0) {
//                throw new PnetRuntimeException("Calculation error in " +
//                    "ScheduleEntryWorkCompleteModifier.HandleSimpleWorkDelta() " +
//                    "no work was allocated in this loop.");
//            } else {
//                preLoopWork = workToAllocate;
//            }
//        }
//        //sjmittal: well please check for -ive work to allocate also. for fix of bfd 3320
//        //also note number of decimal places is 3 instead of 5 (used elsewhere) for some wierd reason
//        while (workToAllocate.getAmount().compareTo(new BigDecimal("0.001")) > 0 ||
//        		workToAllocate.getAmount().compareTo(new BigDecimal("-0.001")) < 0);
//
//        //Double check to make sure that we really did divide the work correctly
//        //among the assignees
//        if (workToAllocate.getAmount().signum() < 0) {
//            Logger logger = Logger.getLogger(ScheduleEntryWorkCompleteModifier.class);
//            logger.debug("A partial work amount remains (erroneous).  We allocated " +
//                workToAllocate.getAmount().abs().toString() + " too much.");
//        }
//
//        //If we modified the work complete for an assignee, we need to generate a
//        //work log showing when the work was done.
//        //sjmittal: not anymore due to de-coupling
//        for (Iterator it = modifiedAssignments.iterator(); it.hasNext();) {
//            ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
//
//            //Calculate when the assignee did the work.
//            assn.calculateWorkCompleteDeltaDates(provider, entry.getStartTime());
//        }
//
//    }
}
