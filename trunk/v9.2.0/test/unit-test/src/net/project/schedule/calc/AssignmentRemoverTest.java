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
 * Tests {@link AssignmentRemover}.
 * 
 * @author Tim Morrow
 */
public class AssignmentRemoverTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentRemoverTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentRemoverTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link AssignmentRemover#AssignmentRemover(net.project.resource.ScheduleEntryAssignment, net.project.schedule.ScheduleEntry, net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * (the constructor).
     */
    public void testAssignmentRemover() {

        // Assignment doesn't belong to task
        try {
            new AssignmentRemover(new ScheduleEntryAssignment(), new Task(), new TestWorkingTimeCalendarProvider());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Tests {@link AssignmentRemover#calculateDurationConstantWork()}.
     */
    public void testCalculateDurationConstantWork() {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity work;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork;
        BigDecimal expectedAssignmentPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 8 hours
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 2 days
        // Remove assignment
        // Expected Work: 8 hours
        // Expected Duration: 1 days
        // sjmittal: expected duration would be one day only as kduration would be recalculated for 8 hrs work
        //assuming a dummy assignment working at 100%
        scheduleEntry = helper.makeTask2();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Assignment 1 Work: 16 hours @ 100%
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTask3();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

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
        // Assignment 2: 8 hour @ 100% (custom working time)
        // Duration: 2 days due to working time
        // Remove Assignment 1
        // Expected Assignment 2 Work: 16 hours @ 100%
        // Expected Duration: 2 days (first day isn't worked)
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        //
        // Test Unit Conversion
        //

        // Task Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 1 day
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

    }

    /**
     * Tests {@link AssignmentRemover#calculatePercentageConstantWork()}.
     */
    public void testCalculatePercentageConstantWork() {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity work;
        TimeQuantity duration;
        TimeQuantity expectedAssignmentWork;
        BigDecimal expectedAssignmentPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Assignment 1 Work: 16 hours @ 200%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask3();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("2.0000000000");

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        // Task Work: 0 hour
        // Assignment 1: 0 hour @ 100%
        // Assignment 2: 0 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Assignment 1 Work: 0 hours @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask14();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("1.00");

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        // Special case: Task has work, but one assignment does all the work and that assignment is removed
        // In this case, the work is given to the remaining assignment
        // Task Work: 8 hour
        // Assignment 1: 0 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask15();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("1.00");

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

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
        // Assignment 2: 8 hour @ 100% (custom working time)
        // Duration: 2 days due to working time
        // Remove Assignment 2
        // Expected Assignment 1 Work: 16 hours @ 100%
        // Expected Duration: 2 days
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = work;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        //
        // Test Unit Conversion
        //

        // Task Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 1 day
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_WORK);
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

    }

    /**
     * Tests {@link net.project.schedule.calc.AssignmentRemover#calculateWorkFixedUnit()}.
     */
    public void testCalculateWorkFixedUnit() {

        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork;
        BigDecimal expectedAssignmentPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask3();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = expectedWork;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        //
        // 2 Assignments, all work complete
        scheduleEntry = helper.makeTask3();
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assignment = (ScheduleEntryAssignment)new ArrayList(scheduleEntry.getAssignments()).get(1);
        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);


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
        // Assignment 2: 8 hour @ 100% (custom working time)
        // Duration: 2 days due to working time
        // Remove Assignment 1
        // Expected Assignment 2 Work: 8 hours @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = expectedWork;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        //
        // Test Unit Conversion
        //

        // Task Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 1 day
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

    }

    /**
     * Tests {@link net.project.schedule.calc.AssignmentRemover#calculateWorkFixedDuration()}.
     */
    public void testCalculateWorkFixedDuration() {
        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifierTest.Helper helper;

        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork;
        BigDecimal expectedAssignmentPercentage;

        helper = new ScheduleEntryDurationModifierTest.Helper();

        //
        // 1 Assignment
        //

        // Task Work: 8 hour
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 8 hour
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

        //
        // 2 Assignments
        //

        // Task Work: 16 hour
        // Assignment 1: 8 hour @ 100%
        // Assignment 2: 8 hour @ 100%
        // Duration: 1 day
        // Remove Assignment 2
        // Expected Work: 8 hour
        // Expected Assignment 1 Work: 8 hours @ 100%
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask3();
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(1);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = expectedWork;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

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
        // Assignment 2: 8 hour @ 100% (custom working time)
        // Duration: 2 days due to working time
        // Remove Assignment 1
        // Expected Assignment 2 Work: 8 hours @ 100%
        // Expected Duration: 2 day
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork = expectedWork;
        expectedAssignmentPercentage = new BigDecimal("1.00");
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertWork(scheduleEntry.getAssignments(), new TimeQuantity[]{expectedAssignmentWork});
        assertPercentage(scheduleEntry.getAssignments(), new BigDecimal[]{expectedAssignmentPercentage});

        //
        // Test Unit Conversion
        //

        // Task Work: 1 day
        // Assignment 1: 8 hour @ 100%
        // Duration: 1 day
        // Remove assignment
        // Expected Work: 1 day
        // Expected Duration: 1 day
        scheduleEntry = helper.makeTask1();
        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assignment = (ScheduleEntryAssignment) new ArrayList(scheduleEntry.getAssignments()).get(0);
        expectedWork = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);

        new AssignmentRemover(assignment, scheduleEntry, helper.provider).removeAssignment(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(0, scheduleEntry.getAssignments().size());

    }
    //
    // Helper methods
    //

    private void assertWork(Collection assignments, TimeQuantity[] timeQuantities) {
        AssignmentModifierTest.assertWork(Arrays.asList(timeQuantities), assignments);
    }

    private void assertPercentage(Collection assignments, BigDecimal[] percentages) {
        AssignmentModifierTest.assertPercentage(assignments, Arrays.asList(percentages));
    }

}
