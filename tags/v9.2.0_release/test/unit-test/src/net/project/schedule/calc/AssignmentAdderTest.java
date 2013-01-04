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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

/**
 * Tests {@link AssignmentAdder}.
 * 
 * @author Tim Morrow
 */
public class AssignmentAdderTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentAdderTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentAdderTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link AssignmentAdder#AssignmentAdder(net.project.resource.ScheduleEntryAssignment, net.project.schedule.ScheduleEntry, net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}.
     */
    public void testAssignmentAdder() {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;

        // Assignment not in schedule
        new AssignmentAdder(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider());

        // Assignment already in schedule entry
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        try {
            new AssignmentAdder(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }


    }

    public void testNullPercentageFails() throws Exception {
        // Null percentage
        try {
            new AssignmentAdder(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider()).addAssignment(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testCalculateDurationZeroAssignmentsConstantWork() throws Exception {
        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        BigDecimal percentageAssigned;
        TimeQuantity duration;
        TimeQuantity expectedDuration;
        TimeQuantity work;
        TimeQuantity expectedAssignmentWork1;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // Zero assignments
        //
        // When duration >= work, and 100% specified, percentage is recalculated and duration remains same

        // 1 day task, 8 hour work, zero assignments
        // Add assignment at 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignment();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // 1 day task, 0 hour work, zero assignments
        // Add assignment at 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignmentNoWork();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // 1 day task, 3 hour work, zero assignments
        // Add assignment at 100%
        // Work: 3 hour
        // Expected Assignment 1 Work: 3 hour
        // Expected Assignment 1 Percentage: 38%
        // Expected Duration: 1 day
        work = new TimeQuantity(3, TimeQuantityUnit.HOUR);
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), work);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("0.38");
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(0.9875000000, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration.toString(), scheduleEntry.getDurationTQ().toString());

        //
        // When duration >= work and percentage != 100% specified, duration is recalculated
        //

        // 1 day task, 8 hour work, zero assignments
        // Add assignment at 50%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTaskNoAssignment();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("0.50");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //
        // When duration < work, duration recalculated, percentage as added
        //

        // 0.33 day task, 8 hour work, zero assignments
        // Add assignment at 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Assignment 1 Percentage: 100%
        // Expected Duration: 1 day
        duration = new TimeQuantity(new BigDecimal("0.33"), TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        scheduleEntry = helper.makeTaskNoAssignment(duration, work);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // 0.5 day task, 8 hour work, zero assignments
        // Add assignment at 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Assignment 1 Percentage: 100%
        // Expected Duration: 1 day
        duration = new TimeQuantity(new BigDecimal("0.50"), TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        scheduleEntry = helper.makeTaskNoAssignment(duration, work);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // 2 day task, 32 hour work, zero assignments
        // Add assignment at 25%
        // Work: 32 hour
        // Expected Assignment 1 Work: 32 hour
        // Expected Assignment 1 Percentage: 25%
        // Expected Duration: 16 days
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        scheduleEntry = helper.makeTaskNoAssignment(duration, work);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("0.25");
        expectedAssignmentWork1 = work;
        expectedDuration = new TimeQuantity(16, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
    }

    /*public void testCalculateDurationOneAssignmentConstantWork() throws Exception {
        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        BigDecimal percentageAssigned;
        TimeQuantity expectedDuration;
        TimeQuantity work;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment at 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 4 hour
        // Expected Assignment 2 Work: 4 hour
        // Expected Duration: 0.5 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("1.00");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(0.5, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment at 0%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Assignment 2 Work: 0 hour
        // Expected Duration: 1 day
        //sjmittal: this handling is changed such that if task has work and the assignment getting
        //added does not get any work as in this case, the same assignment is not added and 
        //no working time excpetion is thrown. So expected above won't be true any more
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("0.00");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        try {
            new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
            fail();
        } catch (NoWorkingTimeException e) {
        }
        assertEquals(work, scheduleEntry.getWorkTQ());
//        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
//        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
//        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // Task Work: 8 hour
        // Assignment 1: 4 hour @ 50%
        // Duration: 2 day
        // Add assignment at 75%
        // Work: 8 hour
        // Expected Assignment 1 Work: 3.2 hours
        // Expected Assignment 2 Work: 4.8 hours
        // Expected Duration: 0.8 day
        scheduleEntry = helper.makeTask2();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("0.75");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(3.2, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4.8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(0.8, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("0.50"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

    }*/

    /**
     * Tests {@link AssignmentAdder#calculateDurationConstantWork(java.math.BigDecimal)}.
     */
    public void testCalculateDurationTwoAssignmentsConstantWork() throws Exception {
        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        BigDecimal percentageAssigned;
        TimeQuantity expectedDuration;
        TimeQuantity work;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity expectedAssignmentWork3;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment at 150%
        // Work: 16 hour
        // Expected Assignment 1 Work: 4.57 hours
        // Expected Assignment 2 Work: 4.57 hours
        // Expected Assignment 3 Work: 6.86 hours
        // Expected Duration: 0.57 day
        scheduleEntry = helper.makeTask3();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("1.50");

        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(4.5714285715, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4.5714285714, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(6.8571428571, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(0.5708333333, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration.toString(), scheduleEntry.getDurationTQ().toString());

        //
        // Custom working time
        //

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Add assignment at 100%
        // Work: 16 hour
        // Expected Assignment 1 Work: 4 hours
        // Expected Assignment 2 Work: 4 hours
        // Expected Assignment 3 Work: 8 hours
        // Expected Duration: 1.5 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("1.00");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.5, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Add assignment at 50%
        // Work: 16 hour
        // Expected Assignment 1 Work: 6.4 hours
        // Expected Assignment 2 Work: 6.4 hours
        // Expected Assignment 3 Work: 3.2 hours
        // Expected Duration: 0.8 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("0.50");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(5.3333333334, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(5.3333333333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(5.3333333333, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.6666666667, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration.toString(), scheduleEntry.getDurationTQ().toString());

        // Test Unit Conversion

        // 1 day task, 1 day work, zero assignments
        // Add assignment at 100%
        // Work: 1 day
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        // Expected Task Work: 1 day
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //Initially: 8h work; 2d duration; no assignments; Fixed units
        //Ensure: 8h work; 1d duration; 1 assignment
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(2, TimeQuantityUnit.DAY), new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), scheduleEntry.getWorkTQ());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{new TimeQuantity(8, TimeQuantityUnit.HOUR)});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00")});

        //Initially: 8h work; 0.5 duration; no assignments; Fixed duration
        //Ensure: 8h work; 0.5 duration; 1 assignment @ 200%
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(0.5, TimeQuantityUnit.DAY), new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(null);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), scheduleEntry.getWorkTQ());
        assertEquals(new TimeQuantity(0.5, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{new TimeQuantity(8, TimeQuantityUnit.HOUR)});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("2.0000000000")});
    }

    /**
     * Tests {@link AssignmentAdder#calculatePercentageConstantWork()}.
     */
    /*public void testCalculatePercentageConstantWork() throws Exception {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity duration;
        TimeQuantity work;
        BigDecimal expectedPercentageAssigned1;
        BigDecimal expectedPercentageAssigned2;
        BigDecimal expectedPercentageAssigned3;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity expectedAssignmentWork3;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // Zero assignments
        //

        // 1 day task, 8 hour work, zero assignments
        // Add assignment @ 100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignment();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("1");
        expectedAssignmentWork1 = work;

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1});

        // 1 day task, 0 hour work, zero assignments
        // Add assignment @100%
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignmentNoWork();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("1");
        expectedAssignmentWork1 = work;

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1});

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Work: 8 hour
        // Add assignment @ 100%
        // Expected Assignment 1: 4 hour @ 100%
        // Expected Assignment 2: 4 hour @ 100%
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("2");
        duration = new TimeQuantity(0.5, TimeQuantityUnit.DAY);//<=> (4/1.00)/8
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("1.00");
        expectedPercentageAssigned2 = new BigDecimal("1");
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2});

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 50%
        // Duration: 2 day
        // Work: 8 hour
        // Add assignment @ 50%
        // Expected Assignment 1: 4 hour @ 50%
        // Expected Assignment 2: 4 hour @ 50%
        scheduleEntry = helper.makeTask2();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("2");
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);//<=> (4/.50)/8
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("0.50");
        expectedPercentageAssigned2 = new BigDecimal("0.5");
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(0.5));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Work: 16 hour
        // Add assignment @ 100 %
        // Expected Assignment 1: 5.33 hour @ 100%
        // Expected Assignment 2: 5.33 hour @ 100%
        // Expected Assignment 3: 5.33 hour @ 100%
        scheduleEntry = helper.makeTask3();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        duration = new TimeQuantity(0.667, TimeQuantityUnit.DAY);//<=> (5.33/1.00)/8
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("1.00");
        expectedPercentageAssigned2 = new BigDecimal("1.00");
        expectedPercentageAssigned3 = new BigDecimal("1");
        expectedAssignmentWork1 = new TimeQuantity(5.333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(5.333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(5.333, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2, expectedPercentageAssigned3});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 12 hour
        // Assignment 1: 4 hour @ 25%
        // Assignment 2: 8 hour @ 50%
        // Duration: 2 days
        // Add assignment 50%
        // Expected Assignment 1: 2.4 hour @ 25%
        // Expected Assignment 2: 4.8 hour @ 50%
        // Expected Assignment 3: 4.8 hour @ 50%
        scheduleEntry = helper.makeTask9();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        duration = new TimeQuantity(1.2, TimeQuantityUnit.DAY);//<=> (2.4/.25)/8
        work = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("0.25");
        expectedPercentageAssigned2 = new BigDecimal("0.50");
        expectedPercentageAssigned3 = new BigDecimal("0.5");
        expectedAssignmentWork1 = new TimeQuantity(2.40, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4.80, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(4.80, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(0.5));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2, expectedPercentageAssigned3});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 28 hour
        // Assignment 1: 4 hour @ 25%
        // Assignment 2: 24 hour @ 150%
        // Duration: 2 days
        // Add assignment @ 100%
        // Expected Assignment 1: 2.55 hour @ 25%
        // Expected Assignment 2: 15.27 hour @ 150%
        // Expected Assignment 3: 10.18 hour @ 100%
        scheduleEntry = helper.makeTask11();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        duration = new TimeQuantity(1.273, TimeQuantityUnit.DAY); //<=> (2.55/.25)/8
        work = new TimeQuantity(28, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("0.25");
        expectedPercentageAssigned2 = new BigDecimal("1.50");
        expectedPercentageAssigned3 = new BigDecimal("1");
        expectedAssignmentWork1 = new TimeQuantity( 2.5454545455, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(15.2727272727, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(10.1818181818, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2, expectedPercentageAssigned3});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        //
        // Custom Working Times
        //

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},new String[]{"4/5/04"}, null);

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment with working time calendar 2
        // Work: 8 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Assignment 2 Work: 0 hours @ 100%
        //sjmittal: this handling is changed such that if task has work and the assignment getting
        //added does not get any work as in this case, the same assignment is not added and 
        //no working time excpetion is thrown. So expected above won't be true any more
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");

        // Add calendar for resource "2"
        helper.provider.addResourceCalendarDefintion("2", calendarDef2);

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("1.00");
        expectedPercentageAssigned2 = new BigDecimal("1");
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        try {
            new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
            fail();
        } catch (NoWorkingTimeException e) {
        }
        assertEquals(work, scheduleEntry.getWorkTQ());
//        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
//        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
//        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 50%
        // Duration: 2 day
        // Add assignment with working time calendar 2 @ 50%
        // Work: 8 hour
        // Expected Assignment 1 Work: 5.33 hours @ 50%
        // Expected Assignment 2 Work: 2.67 hours @ 50%
        scheduleEntry = helper.makeTask2();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");
        duration = new TimeQuantity(1.667, TimeQuantityUnit.DAY); //in FUED only the duration changes
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("0.50");
        expectedPercentageAssigned2 = new BigDecimal("0.5");
        expectedAssignmentWork1 = new TimeQuantity(5.3333333333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2.6666666667, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(0.5));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 12 hour
        // Assignment 1: 4 hour @ 25% (default working time)
        // Assignment 2: 8 hour @ 50% (monday is non-working time)
        // Duration: 3 days due to working times
        // Add assignment @ 50 %
        // Expected Assignment 1: 2 hour @ 25%
        // Expected Assignment 2: 4 hour @ 50%
        // Expected Assignment 3: 6 hour @ 50%
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        scheduleEntry = helper.makeTask10(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("3");
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY); //in FUED only the duration changes
        work = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedPercentageAssigned1 = new BigDecimal("0.25");
        expectedPercentageAssigned2 = new BigDecimal("0.50");
        expectedPercentageAssigned3 = new BigDecimal("0.5");
        expectedAssignmentWork1 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(6, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(0.5));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1, expectedPercentageAssigned2, expectedPercentageAssigned3});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        //
        // Test Unit Conversion
        //

        // 1 day task, 1 day work, zero assignments
        // Add assignment @ 100%
        // Task Work: 1 day
        // Expected Assignment 1 Work: 8 hour @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedPercentageAssigned1 = new BigDecimal("1");
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedPercentageAssigned1});

    }*/

    /**
     * Tests {@link AssignmentAdder#calculateWorkFixedUnit(java.math.BigDecimal)}.
     */
    /*public void testCalculateWorkFixedUnit() throws Exception {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        BigDecimal percentageAssigned;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity expectedAssignmentWork3;

        // Null percentage
        try {
            assignment = new ScheduleEntryAssignment();
            scheduleEntry = new Task();
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
            assignment.setPersonID("1");
            new AssignmentAdder(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider()).addAssignment(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // Zero assignments
        //

        // 1 day task, 8 hour expectedWork, zero assignments
        // Add assignment at 100%
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignment();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // 1 day task, 0 hour work, zero assignments
        // Add assignment at 100%
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignmentNoWork();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment at 50%
        // Expected Work: 12 hour
        // Expected Assignment 1 Work: 8 hour @ 100%
        // Expected Assignment 2 Work: 4 hour @ 50%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment at 150%
        // Work: 28 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Assignment 2 Work: 8 hours @ 100%
        // Expected Assignment 3 Work: 12 hours @ 150%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask3();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("1.50");
        expectedWork = new TimeQuantity(28, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //
        // Custom working time
        //

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Add assignment at 100% (with default calendar)
        // Work: 32 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Assignment 2 Work: 8 hours @ 100%
        // Expected Assignment 3 Work: 16 hours @ 100%
        // Expected Duration: 2 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("1.00");
        expectedWork = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Add assignment at 50% (default working time)
        // Work: 24 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Assignment 2 Work: 8 hours @ 100%
        // Expected Assignment 3 Work: 8 hours @ 50%
        // Expected Duration: 2 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), new BigDecimal("1.00"), percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());

        //
        // Test Unit Conversion
        //

        // 1 day task, 1 day work, zero assignments
        // Add assignment at 100%
        // Expected Work: 1 day
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1.00");
        expectedWork = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());


    }*/

    /**
     * Tests {@link AssignmentAdder#calculateWorkFixedDuration(java.math.BigDecimal)}.
     */
    public void testCalculateWorkFixedDuration() throws Exception {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        BigDecimal percentageAssigned;
        TimeQuantity duration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity expectedAssignmentWork3;

        // Null percentage
        try {
            scheduleEntry = new Task();
            assignment = new ScheduleEntryAssignment();
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
            assignment.setPersonID("1");
            scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
            new AssignmentAdder(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider()).addAssignment(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // Zero assignments
        //

        // 1 day task, 8 hour expectedWork, zero assignments
        // Duration: 1 day
        // Add assignment at 100%
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hour @ 100%
        scheduleEntry = helper.makeTaskNoAssignment();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // 1 day task, 0 hour work, zero assignments
        // Add assignment at 100%
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTaskNoAssignmentNoWork();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        //
        // Custom Working Times
        //

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Add calendar for resource "2"
        helper.provider.addResourceCalendarDefintion("2", calendarDef2);

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Add assignment @ 100% with working time calendar 2
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Assignment 2 Work: 0 hours @ 100%
        //sjmittal: this handling is changed such that if task has work and the assignment getting
        //added does not get any work as in this case, the same assignment is not added and 
        //no working time excpetion is thrown. So expected above won't be true any more
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("1");
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        try {
            new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.0));
        } catch (NoWorkingTimeException e) {
        }
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
//        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
//        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
//        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("1.00"), percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 50%
        // Duration: 2 day
        // Add assignment @ 150% with working time calendar 2
        // Expected Work: 20 hour
        // Expected Assignment 1 Work: 8 hours @ 50%
        // Expected Assignment 2 Work: 12 hours @ 150%
        scheduleEntry = helper.makeTask2();
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("2");
        percentageAssigned = new BigDecimal("1.5");
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(20, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(12, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(new BigDecimal(1.5));
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("0.50"), percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        // Task Work: 12 hour
        // Assignment 1: 4 hour @ 25% (default working time)
        // Assignment 2: 8 hour @ 50% (monday is non-working time)
        // Duration: 3 days due to working times
        // Add assignment @ 50% (default working time)
        // Expected Assignment 1: 4 hour @ 25%
        // Expected Assignment 2: 8 hour @ 50%
        // Expected Assignment 3: 12 hour @ 50%
        // Expected Work: 24 hours
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        scheduleEntry = helper.makeTask10(calendarDef1, calendarDef2);
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("3");
        percentageAssigned = new BigDecimal("0.5");
        duration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(12, TimeQuantityUnit.HOUR);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{new BigDecimal("0.25"), new BigDecimal("0.50"), percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

        //
        // Test Unit Conversion
        //

        // 1 day task, 1 day work, zero assignments
        // Duration: 1 day
        // Add assignment at 100%
        // Expected Work: 1 day
        // Expected Assignment 1 Work: 8 hour @ 100%
        scheduleEntry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(WellKnownObjects.TEST_TASK_ID);
        assignment.setPersonID("1");
        percentageAssigned = new BigDecimal("1");
        expectedWork = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentAdder(assignment, scheduleEntry, helper.provider).addAssignment(percentageAssigned);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork1});
        assertWorkSum(scheduleEntry.getAssignments(), scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{percentageAssigned});
        assertEquals(duration, scheduleEntry.getDurationTQ());

    }

//    public void testAdd1AssignmentDistributeUnallocatedWorkComplete() {
//        ScheduleEntryDurationModifierTest.Helper helper = new ScheduleEntryDurationModifierTest.Helper();
//        ScheduleEntry scheduleEntry = helper.makeTaskNoAssignment();
//
//        //If 4 hours are unallocated and only 1 assignment is added, that 1
//        //assignment should get all of the work complete
//        scheduleEntry.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        scheduleEntry.setUnallocatedWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
//        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
//        assignment.setPercentAssigned(100);
//        scheduleEntry.addAssignment(assignment);
//
//        new AssignmentAdder(assignment, scheduleEntry, helper.provider).calculateWorkComplete();
//        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assignment.getWorkComplete());
//
//        //1 assignment [8 hr work, 50%]
//        scheduleEntry = helper.makeTaskNoAssignment();
//        scheduleEntry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
//        scheduleEntry.setUnallocatedWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
//        assignment = new ScheduleEntryAssignment();
//        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
//        assignment.setPercentAssigned(50);
//        scheduleEntry.addAssignment(assignment);
//
//        new AssignmentAdder(assignment, scheduleEntry, helper.provider).calculateWorkComplete();
//        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assignment.getWorkComplete());
//    }

//    public void testAdd2AssignmentsDistributeUnallocatedWorkComplete() {
//        ScheduleEntryDurationModifierTest.Helper helper = new ScheduleEntryDurationModifierTest.Helper();
//        ScheduleEntry scheduleEntry;
//        ScheduleEntryAssignment assignment1;
//        ScheduleEntryAssignment assignment2;
//
//        //4 Unallocated hours and 2 assignments should distribute the work complete
//        //evenly with 2 hours per assignment.
//        scheduleEntry = helper.makeTaskNoAssignment();
//        scheduleEntry.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        scheduleEntry.setUnallocatedWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        assignment1 = new ScheduleEntryAssignment();
//        assignment1.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        assignment1.setPercentAssigned(100);
//        assignment2 = new ScheduleEntryAssignment();
//        assignment2.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        assignment2.setPercentAssigned(100);
//        scheduleEntry.addAssignment(assignment1);
//        scheduleEntry.addAssignment(assignment2);
//
//        new AssignmentAdder(assignment1, scheduleEntry, helper.provider).calculateWorkComplete();
//        new AssignmentAdder(assignment2, scheduleEntry, helper.provider).calculateWorkComplete();
//
//        assertEquals(new TimeQuantity(2, TimeQuantityUnit.HOUR), assignment1.getWorkComplete());
//        assertEquals(new TimeQuantity(2, TimeQuantityUnit.HOUR), assignment2.getWorkComplete());
//
//        //4 Unallocated hours with unevenly distributed assignments  [3 hr, 1hr]
//        scheduleEntry = helper.makeTaskNoAssignment();
//        scheduleEntry.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        scheduleEntry.setUnallocatedWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        assignment1 = new ScheduleEntryAssignment();
//        assignment1.setWork(new TimeQuantity(6, TimeQuantityUnit.HOUR));
//        assignment1.setPercentAssigned(100);
//        assignment2 = new ScheduleEntryAssignment();
//        assignment2.setWork(new TimeQuantity(2, TimeQuantityUnit.HOUR));
//        assignment2.setPercentAssigned(100);
//        scheduleEntry.addAssignment(assignment1);
//        scheduleEntry.addAssignment(assignment2);
//
//        new AssignmentAdder(assignment1, scheduleEntry, helper.provider).calculateWorkComplete();
//        new AssignmentAdder(assignment2, scheduleEntry, helper.provider).calculateWorkComplete();
//
//        assertEquals(new TimeQuantity(3, TimeQuantityUnit.HOUR), assignment1.getWorkComplete());
//        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), assignment2.getWorkComplete());
//
//        //Two assignees, one with no work
//        scheduleEntry = helper.makeTaskNoAssignment();
//        scheduleEntry.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        scheduleEntry.setUnallocatedWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
//        assignment1 = new ScheduleEntryAssignment();
//        assignment1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
//        assignment1.setPercentAssigned(100);
//        assignment2 = new ScheduleEntryAssignment();
//        assignment2.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
//        assignment2.setPercentAssigned(100);
//        scheduleEntry.addAssignment(assignment1);
//        scheduleEntry.addAssignment(assignment2);
//
//        new AssignmentAdder(assignment1, scheduleEntry, helper.provider).calculateWorkComplete();
//        new AssignmentAdder(assignment2, scheduleEntry, helper.provider).calculateWorkComplete();
//
//        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assignment1.getWorkComplete());
//        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assignment2.getWorkComplete());
//    }

    //
    // Helper methods
    //

    private void assertWork(Collection assignments, TimeQuantity[] timeQuantities) {
        AssignmentModifierTest.assertWork(Arrays.asList(timeQuantities), assignments);
    }

    public void assertWorkSum(Collection assignments, TimeQuantity taskWork) {
        taskWork = ScheduleTimeQuantity.convertToHour(taskWork);
        TimeQuantity assignmentWorkSum = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        //Sum the work of the assignments
        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
            assignmentWorkSum = assignmentWorkSum.add(ScheduleTimeQuantity.convertToHour(assn.getWork()));
        }

        //Do the comparison.  We don't just use the assertEquals because it doesn't show enough
        //decimal precision to be helpful.
        if (!assignmentWorkSum.equals(taskWork)) {
            System.out.println("Assignment Work Sum = " + assignmentWorkSum.toShortString(10,10));
            System.out.println("Task Work           = " + taskWork.toShortString(10,10));
            assertEquals(assignmentWorkSum, taskWork);
        }
    }

    private void assertPercentage(Collection assignments, BigDecimal[] percentages) {
        AssignmentModifierTest.assertPercentage(assignments, Arrays.asList(percentages));
    }

}
