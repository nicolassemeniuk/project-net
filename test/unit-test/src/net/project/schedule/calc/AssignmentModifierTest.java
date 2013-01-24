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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link AssignmentModifier}.
 * 
 * @author Tim Morrow
 */
public class AssignmentModifierTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentModifierTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentModifierTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link AssignmentModifier#AssignmentModifier(net.project.resource.ScheduleEntryAssignment, net.project.schedule.ScheduleEntry, net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * (the constructor).
     */
    public void testAssignmentModifier() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;

        // No assignments to modify
        try {
            new AssignmentModifier(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Assignment doesn't belong to task
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("2");
        try {
            new AssignmentModifier(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Tests {@link AssignmentModifier#calculateDuration(java.math.BigDecimal)}.
     */
    public void testCalculateDurationPercentageChange() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity work;
        TimeQuantity assignmentWork1;
        TimeQuantity assignmentWork2;
        TimeQuantity expectedDuration;
        BigDecimal newPercentage;

        //
        // No assignments
        //
        try {
            new AssignmentModifier(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider()).calculateDuration((BigDecimal) null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Null Percentage
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        try {
            new AssignmentModifier(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider()).calculateDuration((BigDecimal) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 assignment
        //

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Duration 2 days
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork1 = work;
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 150%
        // Expected Duration 0.67 days
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork1 = work;
        expectedDuration = new TimeQuantity(new BigDecimal("0.6666666667"), TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("1.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration.toString(), scheduleEntry.getDurationTQ().toString());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 0%
        // Expected Duration 0 days
        // Work changes to 0 hour
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assignmentWork1 = work;
        expectedDuration = new TimeQuantity(0, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        //
        // 2 assignments
        //

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 2 to 50%
        // Expected Duration 2 days
        scheduleEntry = helper.makeTask3();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork2 = assignmentWork1;
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(1), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1, assignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{new BigDecimal("1.00"), newPercentage}));

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 2 to 125%
        // Expected Duration 1 day
        scheduleEntry = helper.makeTask3();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork2 = assignmentWork1;
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("1.25");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(1), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1, assignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{new BigDecimal("1.00"), newPercentage}));

        //
        // Test Unit Conversion
        //

        // Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Duration 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        assignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        // Work: 1 day
        // Assignment 1: 1 day @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Duration 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());
        ((ScheduleEntryAssignment) assignments.get(0)).setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));

        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        assignmentWork1 = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));
    }

    /**
     * Tests {@link AssignmentModifier#calculateDuration(java.math.BigDecimal)}
     * with custom working time calendars.
     */
    public void testCalculateDurationPercentageChangeCustomWorkingTime() {

        ScheduleEntry scheduleEntry;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;
        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        TimeQuantity work;
        TimeQuantity assignmentWork1;
        TimeQuantity assignmentWork2;
        TimeQuantity expectedDuration;
        BigDecimal newPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 2 assignments
        // Assignment 1: Standard calendar
        // Assignment 2: Standard, with Monday 5th April a non-working day
        //
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Modify percentage 1 to 50%
        // Expected Duration 2 days (same, due to working time)
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork2 = assignmentWork1;
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1, assignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage, new BigDecimal("1.00")}));

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Modify percentage 2 to 50%
        // Expected Duration 3 days
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignments = new ArrayList(scheduleEntry.getAssignments());

        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignmentWork2 = assignmentWork1;
        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(1), scheduleEntry, helper.provider)
                .calculateDuration(newPercentage);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork1, assignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{new BigDecimal("1.00"), newPercentage}));

    }

    /**
     * Tests {@link AssignmentModifier#calculateWork(java.math.BigDecimal)}.
     */
    public void testCalculateWorkPercentageChange() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity duration;
        BigDecimal newPercentage;

        //
        // No assignments
        //
        try {
            new AssignmentModifier(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider())
                    .calculateWork(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Null Percentage
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        try {
            new AssignmentModifier(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider())
                    .calculateWork(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 assignment
        //

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Assignment 1: 4 hours @ 50%
        // Exepcted Task Work: 4 hours
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 0%
        // Expected Assignment 1: 0 hours @ 0%
        // Exepcted Task Work: 0 hours
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.00");
        expectedWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        //
        // Test Unit Conversion
        //

        // Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Assignment 1: 4 hours @ 50%
        // Exepcted Task Work: 0.5 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(new BigDecimal("0.50"), TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));

        // Work: 1 day
        // Assignment 1: 1 day @ 100%
        // Duration: 1 day
        // Modify percentage 1 to 50%
        // Expected Assignment 1: 0.5 days @ 50%
        // Exepcted Task Work: 0.5 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());
        ((ScheduleEntryAssignment) assignments.get(0)).setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(new BigDecimal("0.50"), TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = expectedWork;

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage}));
    }

    /**
     * Tests {@link AssignmentModifier#calculateWork(java.math.BigDecimal)} with
     * custom working time calendars.
     */
    public void testCalculateWorkPercentageChangeCustomWorkingTime() {

        ScheduleEntry scheduleEntry;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity duration;
        BigDecimal newPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 2 assignments
        // Assignment 1: Standard calendar
        // Assignment 2: Standard, with Monday 5th April a non-working day
        //
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Modify percentage 1 to 50%
        // Expected Assignment 1: 4 hours @ 50%
        // Exepcted Task Work: 12 hours
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{newPercentage, new BigDecimal("1.00")}));

        // Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 2 days due to working time
        // Modify percentage 2 to 50%
        // Expected Assignment 2: 4 hours @ 50%
        // Exepcted Task Work: 12 hours
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(1), scheduleEntry, helper.provider)
                .calculateWork(newPercentage);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{new BigDecimal("1.00"), newPercentage}));
    }

    /**
     * Tests {@link AssignmentModifier#calculateDuration}.
     */
    public void testCalculateDurationWorkChange() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity newWork;
        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        BigDecimal assignmentPercentage1;

        //
        // No assignments
        //
        try {
            new AssignmentModifier(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider())
                    .calculateDuration((TimeQuantity) null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Null work
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        try {
            new AssignmentModifier(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider())
                    .calculateDuration((TimeQuantity) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 assignment
        //

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 16 hour
        // Expected Assignment 1: 16 hours @ 100%
        // Exepcted Task Work: 16 hours
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assignmentPercentage1 = new BigDecimal("1.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{assignmentPercentage1}));

        //
        // Test Unit Conversion
        //

        // Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 16 hour
        // Expected Assignment 1: 16 hours @ 100%
        // Exepcted Task Work: 2 days
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assignmentPercentage1 = new BigDecimal("1.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{assignmentPercentage1}));

        // Work: 1 day
        // Assignment 1: 8 hours @ 100%
        // Duration: 1 day
        // Modify work 1 to 2 days
        // Expected Assignment 1: 2 days @ 100%
        // Exepcted Task Work: 2 days
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        newWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assignmentPercentage1 = new BigDecimal("1.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculateDuration(newWork);
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{assignmentPercentage1}));
    }

    /**
     * Tests {@link AssignmentModifier#calculateDuration}.
     */
    public void testCalculatePercentageWorkChange() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity newWork;
        TimeQuantity expectedWork;
        TimeQuantity duration;
        BigDecimal expectedAssignmentPercentage1;

        //
        // No assignments
        //
        try {
            new AssignmentModifier(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider())
                    .calculatePercentage(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Null WOrk
        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        scheduleEntry = new Task();
        scheduleEntry.addAssignment(assignment);
        try {
            new AssignmentModifier(assignment, scheduleEntry, new TestWorkingTimeCalendarProvider())
                    .calculatePercentage(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 assignment
        //

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 16 hour
        // Expected Assignment 1: 16 hours @ 200%
        // Exepcted Task Work: 16 hours
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("2.0000000000");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));

        // Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 0 hour
        // Expected Assignment 1: 0 hours @ 100%
        // Exepcted Task Work: 0 hours
        scheduleEntry = helper.makeTask1();
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));

        // Work: 0 hour
        // Assignment 1: 0 hour @ 200%
        // Duration: 1 day
        // Modify work 1 to 8 hour
        // Expected Assignment 1: 8 hours @ 100%
        // Exepcted Task Work: 8 hours
        scheduleEntry = helper.makeTask12();
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setEndTimeD(WorkingTimeCalendarDefinitionTest.makeDateTime("04/05/04 5:00 PM"));
        assignments = new ArrayList(scheduleEntry.getAssignments());
        ((ScheduleEntryAssignment) assignments.get(0)).setPercentAssignedDecimal(new BigDecimal("2.00"));

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.00");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));

        //
        // Test Unit Conversion
        //

        // Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 16 hour
        // Expected Assignment 1: 16 hours @ 200%
        // Exepcted Task Work: 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedAssignmentPercentage1 = new BigDecimal("2.0000000000");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));


        // Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Modify work 1 to 2 days
        // Expected Assignment 1: 2 days @ 200%
        // Exepcted Task Work: 2 days
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedAssignmentPercentage1 = new BigDecimal("2.0000000000");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));

    }

    /**
     * Tests {@link AssignmentModifier#calculatePercentage(net.project.util.TimeQuantity)} with
     * custom working time calendars.
     */
    public void testCalculatePercentageWorkChangeCustomWorkingTime() {

        ScheduleEntry scheduleEntry;
        List assignments;
        ScheduleEntryDurationModifierTest.Helper helper;
        WorkingTimeCalendarDefinition calendarDef1;

        TimeQuantity newWork;
        TimeQuantity expectedWork;
        TimeQuantity duration;
        BigDecimal expectedAssignmentPercentage1;
        Date expectedEndDate;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"4/5/04"}, null);

        // Work: 0 hour
        // Assignment 1: 0 hour @ 100%
        // Duration: 1 day
        // Start Date: Monday April 5th @ 8:00 AM
        // End Date: Monday April 5th @ 5:00 PM
        // Assignment 1 Calendar: Monday April 5th is non-working day
        //
        // Modify assignment 1 work to 8 hour
        // Expected Assignment 1: 8 hours @ 100%
        // Expected Task Work: 8 hours
        // Expected End Date: Tuesday April 6th @ 5:00 PM
        // Expected DUration: 1 day
        scheduleEntry = helper.makeTask12(calendarDef1);
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));

        assignments = new ArrayList(scheduleEntry.getAssignments());

        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.00");
        expectedEndDate = WorkingTimeCalendarDefinitionTest.makeDateTime("04/06/04 5:00 PM");

        new AssignmentModifier((ScheduleEntryAssignment) assignments.get(0), scheduleEntry, helper.provider)
                .calculatePercentage(newWork);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{newWork}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), Arrays.asList(new BigDecimal[]{expectedAssignmentPercentage1}));
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
    }

    //
    // Helper Methods
    //

    /**
     * Asserts that the expected work in the specified collection equals
     * the assignment work for each corresponding assignment.
     * @param timeQuantities the expected work
     * @param assignments the assignment whose work to compare
     */
    static void assertWork(Collection timeQuantities, Collection assignments) {
        ScheduleEntryDurationModifierTest.assertWork(timeQuantities, assignments);
    }

    /**
     * Asserts that each assignment's percentage is equal to the corresponding value.
     *
     * @param assignments the ordered assignments whose percentage to check
     * @param percentageDecimals the percentage values in the same order as assignments
     */
    static void assertPercentage(Collection assignments, List percentageDecimals) {
        assertEquals("Number of assignments and percentage values must be equal", assignments.size(), percentageDecimals.size());

        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext(); index++) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
           assertEquals("Assignment " + index, percentageDecimals.get(index), nextAssignment.getPercentAssignedDecimal());
        }

    }

}
