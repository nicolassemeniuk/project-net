/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package net.project.schedule.calc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link ScheduleEntryWorkModifier}.
 * 
 * @author Tim Morrow
 */
public class ScheduleEntryWorkModifierTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryWorkModifierTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * 
     * @param args a <code>String[]</code> value which contains the command line options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit test believes it is comprised of.  In most
     * cases, it is only the current class, though it can include others.
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleEntryWorkModifierTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryWorkModifier#calculateDuration(net.project.util.TimeQuantity)}.
     */
    public void testCalculateDuration() {

        ScheduleEntryWorkModifier workModifier;
        ScheduleEntryHelper scheduleEntry;
        Date startDate;
        Date expectedEndDate;
        TimeQuantity newWork;
        TimeQuantity expectedDuration;
        TestWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();

        // Null duration
        try {
            new ScheduleEntryWorkModifier(new Task(), new TestWorkingTimeCalendarProvider()).calculateDuration(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Task starts on Monday April 12th 2004 @ 8:00 AM
        startDate = makeDateTime("04/12/04 8:00 AM");

        //
        // No assigmments
        // Duration remains the same, even when work changes (unless duration currently zero)

        // Simulate creating a new task with zero duration; no dates
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setDuration(new TimeQuantity(0, TimeQuantityUnit.DAY));
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setStartTimeD(null);
        scheduleEntry.setEndTimeD(null);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, new TestWorkingTimeCalendarProvider());
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());


        //sjmittal: duration is now changed in case work is modified and task is not fixed duration
        // Task Work: 8 hours
        // No Assignments
        // Duration: 1 day
        // Change Work to 16 hours
        // Expected Duration: 2 days 
        // Expected End Date: Monday April 12th @ 5:00 PM
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/13/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.recalculate();

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, new TestWorkingTimeCalendarProvider());
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // Task Work: 16 hours
        // No Assignments
        // Duration: 2 day
        // Change Work to 8 hours
        // Expected Duration: 1 day
        // Expected End Date: Tuesday April 13th @ 5:00 PM
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/12/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(2, TimeQuantityUnit.DAY));
        scheduleEntry.recalculate();

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, new TestWorkingTimeCalendarProvider());
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // Changing work on a task with 0 duration and 0 work with no assignments
        // will always calculate duration from work
        // Task Work: 0 hours
        // No Assignments
        // Duration: 0 day
        // Change Work to 8 hours
        // Expected Duration: 1 day
        // Expected End Date: Monday April 12th @ 5:00 PM
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/12/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(0, TimeQuantityUnit.DAY));
        scheduleEntry.recalculate();

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, new TestWorkingTimeCalendarProvider());
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        //
        // 1 assignment 100%
        //

        // Task Work: 8 hours
        // Assignment 1: 8 hours, 100%
        // Duration: 1 day
        // Change Work to 16 hours
        // Expected Duration: 2 days
        // Expected End Date: Tuesday April 13th @ 5:00 PM
        // Expected Assignment 1: 16 hours, 100%
        // Expected Work: 16 hours
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/13/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{16});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{100});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // Task Work: 8 hours
        // Assignment 1: 8 hours, 100%
        // Duration: 1 day
        // Change Work to 8 weeks
        // Expected Duration: 40 days
        // Expected End Date: Friday June 4th @ 5:00 PM
        // Expected Assignment 1: 320 hours, 100%
        // Expected Work: 8 weeks
        newWork = new TimeQuantity(8, TimeQuantityUnit.WEEK);
        expectedDuration = new TimeQuantity(40, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("06/04/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{320});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{100});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        //
        // 2 assignments
        //

        // 16 hour task, 2 assignment @ 100%, 1 day duration
        // 1: 8 hours 100%
        // 2: 8 hours 100%
        // New Work: 32 hour
        // Expected Duration: 2 day
        // Expected End Date: Tuesday April 13th @ 5:00 PM
        // 1: 16 hours
        // 2: 16 hours
        newWork = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/13/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(new TestWorkingTimeCalendarProvider(), startDate);
        scheduleEntry.addAssignment(100, 8);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{16, 16});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{100, 100});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // 24 hour task
        // 1: 8 hours 50%
        // 2: 16 hours 100%
        // Current Duration: 2 days
        // New Work: 48 hour
        // 1: 16 hours 50%
        // 2: 32 hours 100%
        // Expected Duration: 4 day
        // Expected End Date: Thursday April 15th @ 5:00 PM
        newWork = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/15/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(new TestWorkingTimeCalendarProvider(), startDate);
        scheduleEntry.addAssignment(50, 8);
        scheduleEntry.addAssignment(100, 16);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{16, 32});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{50, 100});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // 24 hour task
        // 1: 16 hours 50%
        // 2: 8 hours 100%
        // Current Duration: 4 days
        // New Work: 48 hour
        // 1: 32 hours 50%
        // 2: 16 hours 100%
        // Expected Duration: 8 day
        // Expected End Date: Wednesday April 21st @ 5:00 PM
        newWork = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(8, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/21/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(new TestWorkingTimeCalendarProvider(), startDate);
        scheduleEntry.addAssignment(50, 16);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{32, 16});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{50, 100});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        // 8 hour task, 1 day duration
        // 1: 8 hours @ 100%
        // 2: 0 hours @ 50%
        // New Work: 16 hour
        // Expected Duration: 2 day
        // Expected End Date: Tuesday April 13th @ 5:00 PM
        // 1: 16 hours @ 100%
        // 2: 0 hours @ 50%
        // Assignments with zero work don't receive any as long as there is
        // one other assignment with some work
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/13/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(new TestWorkingTimeCalendarProvider(), startDate);
        scheduleEntry.addAssignment(100, 8);
        scheduleEntry.addAssignment(50, 0);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{16, 0});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{100, 50});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        //
        // 3 assignments
        //

        // 48 hour task
        // 1: 24 hours 75%
        // 2: 16 hours 25%
        // 3: 8 hours 150%
        // Current Duration: 8 days
        // New Work: 24 hour
        // 1: 12 hours
        // 2: 8 hours
        // 3: 4 hours
        // Expected Duration: 4 days
        // Expected End Date: Thursday April 15th @ 5:00 PM
        newWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        expectedEndDate = makeDateTime("04/15/04 5:00 PM");

        scheduleEntry = new ScheduleEntryHelper(new TestWorkingTimeCalendarProvider(), startDate);
        scheduleEntry.addAssignment(75, 24);
        scheduleEntry.addAssignment(25, 16);
        scheduleEntry.addAssignment(150, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{12, 8, 4});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{75, 25, 150});
        assertEquals(newWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

    }

    /**
     * Tests {@link ScheduleEntryWorkModifier#calculatePercentage(net.project.util.TimeQuantity)}.
     */
    public void testCalculatePercentage() {

        ScheduleEntryWorkModifier workModifier;
        ScheduleEntryHelper scheduleEntry;
        Date startDate;
        TimeQuantity newWork;
        TimeQuantity duration;
        TestWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();

        // Null duration
        try {
            new ScheduleEntryWorkModifier(new Task(), new TestWorkingTimeCalendarProvider()).calculatePercentage(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Task starts on Monday April 12th 2004 @ 8:00 AM
        startDate = makeDateTime("04/12/04 8:00 AM");

        //
        // No assigmments
        //

        // Changing work on a task with 0 duration and 0 work with no assignments
        // will always calculate duration from work
        // Task Work: 0 hours
        // No Assignments
        // Duration: 0 day
        // Change Work to 16 hours
        // Expected Duration: 2 day
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(0, TimeQuantityUnit.DAY));

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        // Simulate creating a task; null dates
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(0, TimeQuantityUnit.DAY));
        scheduleEntry.setStartTimeD(null);
        scheduleEntry.setEndTimeD(null);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        // Changing work on task that already has duration, but has no assignments
        // will not affect duration; the work will change only
        // Task Work: 8 hours
        // Duration: 1 day
        // No Assignments
        // Change Work to 16 hours
        // Expected Duration: 1 days
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        //
        // 1 assignment
        //

        // Task Work: 8 hours
        // Assignment 1: 8 hours, 100%
        // Duration: 1 day
        // Change Work to 16 hours
        // Expected Assignment 1: 16 hours, 200%
        // Expected Work: 16 hours
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{16});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{200});
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        // Task Work: 16 hours
        // Assignment 1: 16 hours, 50%
        // Duration: 4 day
        // Change Work to 8 hours
        // Expected Assignment 1: 8 hours, 25%
        // Expected Work: 8 hours
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(4, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(50, 16);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{8});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{25});
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        // Task Work: 8 hours
        // Assignment 1: 8 hours, 100%
        // Duration: 1 day
        // Change Work to 8 weeks
        // Expected Assignment 1: 320 hours, 4000%
        // Expected Work: 8 weeks
        newWork = new TimeQuantity(8, TimeQuantityUnit.WEEK);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(100, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{320});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{4000});
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        //
        // 2 assignments
        //

        // Task Work: 24 hours
        // Assignment 1: 8 hours, 50%
        // Assignment 2: 16 hours, 100%
        // Duration: 2 day
        // Change Work to 36 hours
        // Expected Assignment 1: 12 hours, 75%
        // Expected Assignment 2: 24 hours, 150%
        newWork = new TimeQuantity(36, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(50, 8);
        scheduleEntry.addAssignment(100, 16);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{12, 24});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{75, 150});
        assertEquals(newWork, scheduleEntry.getWorkTQ());

        //
        // 3 assignments
        //

        // Task Work: 48 hours
        // Assignment 1: 24 hours, 75%
        // Assignment 2: 16 hours, 25%
        // Assignment 3: 8 hours, 150%
        // Duration: 8 day
        // Change Work to 24 hours
        // Expected Assignment 1: 12 hours, 38%
        // Expected Assignment 2: 8 hours, 13%
        // Expected Assignment 2: 4 hours, 75%
        newWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(8, TimeQuantityUnit.DAY);

        scheduleEntry = new ScheduleEntryHelper(provider, startDate);
        scheduleEntry.addAssignment(75, 24);
        scheduleEntry.addAssignment(25, 16);
        scheduleEntry.addAssignment(150, 8);

        workModifier = new ScheduleEntryWorkModifier(scheduleEntry, provider);
        workModifier.calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertAssignmentWork(scheduleEntry.getAssignments(), new int[]{12, 8, 4});
        assertAssignmentPercentage(scheduleEntry.getAssignments(), new int[]{38, 13, 75});
        assertEquals(newWork, scheduleEntry.getWorkTQ());

    }

    //
    // Helper Methods
    //

    private static Date makeDateTime(String dateTime) {
        return WorkingTimeCalendarDefinitionTest.makeDateTime(dateTime);
    }

    private static void assertAssignmentWork(Collection assignments, int[] workHourValues) {
        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            assertEquals("Unexpected work for assignment " + nextAssignment.getPersonID(),
                    new TimeQuantity(workHourValues[index++], TimeQuantityUnit.HOUR).toString(),
                    nextAssignment.getWork().toString());
        }
    }

    private static void assertAssignmentPercentage(Collection assignments, int[] percentageValues) {
        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            assertEquals("Unexpected percentage for assignment " + nextAssignment.getPersonID(),
                    new BigDecimal(percentageValues[index++]),
                    nextAssignment.getPercentAssigned());
        }
    }

    //
    // Nested, top-level classes
    //

    /**
     * Provides a helper schedule entry that allows easy addition of assignments
     * and keeps duration and task work in sync.
     */
    private static class ScheduleEntryHelper extends Task {

        private final TestWorkingTimeCalendarProvider provider;
        private int nextAssignmentID;

        /**
         * Creates a new helper with no assignments and the specified task start date.
         * @param provider the calendar provider
         * @param startDate the date on which the task starts
         */
        private ScheduleEntryHelper(TestWorkingTimeCalendarProvider provider, Date startDate) {
            this.provider = provider;
            super.setStartTimeD(startDate);
        }

        /**
         * Adds an assignment with the specified percent assigned and work hours,
         * with default timezone and default calendar definition.
         * <p>
         * The task work and duration is always kept up-to-date.
         * </p>
         * @param percentAssigned the percent assigned (0..100)
         * @param workHours the number of hours of work for this assignment
         */
        private void addAssignment(int percentAssigned, int workHours) {
            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPersonID(String.valueOf(++nextAssignmentID));
            assignment.setPercentAssigned(percentAssigned);
            assignment.setWork(new TimeQuantity(workHours, TimeQuantityUnit.HOUR));
            assignment.setTimeZone(this.provider.getDefaultTimeZone());
            provider.addResourceCalendarDefintion(assignment.getPersonID(), this.provider.getDefault());
            addAssignment(assignment);

            recalculate();
        }

        /**
         * Recalculates assignment end time, schedule entry end time, task work and duration.
         */
        void recalculate() {
            // Calculate assignment end date
            calculateEndDate(provider);

            // Now recalculate task work and duration (only necessary if we have assignments)
            if (!getAssignments().isEmpty()) {
                calculateDuration(this.provider);

                TimeQuantity taskWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
                for (Iterator iterator = getAssignments().iterator(); iterator.hasNext();) {
                    ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                    taskWork = taskWork.add(nextAssignment.getWork());
                }
                setWork(taskWork);
            }

        }

    }
}
