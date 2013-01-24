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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.persistence.PersistenceException;
import net.project.schedule.PredecessorList;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.SuccessorList;
import net.project.schedule.Task;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskDependency;
import net.project.schedule.TaskDependencyType;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.test.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link ScheduleEntryConstraintModifier}.
 * 
 * @author Tim Morrow
 */
public class ScheduleEntryConstraintModifierTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryConstraintModifierTest(String s) {
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
        TestSuite suite = new TestSuite(ScheduleEntryConstraintModifierTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#ScheduleEntryConstraintModifier(net.project.schedule.Schedule, net.project.schedule.ScheduleEntry)}.
     */
    public void testScheduleEntryDateModifier() {

        try {
            new ScheduleEntryConstraintModifier(null, new Task());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new ScheduleEntryConstraintModifier(new Schedule(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#startDateChanged(java.util.Date,java.util.TimeZone)} for
     * a task with no dependencies.
     */
    public void testStartDateChanged() {

        ScheduleEntryConstraintModifier dateModifier;
        Date newStartDate;
        Date expectedStartDate;
        Date expectedEndDate;
        TaskConstraintType expectedConstraintType;
        Date expectedConstraintDate;
        Helper helper;

        //
        // Null parameters
        //
        try {
            dateModifier = new ScheduleEntryConstraintModifier(new Schedule(), new Task());
            dateModifier.startDateChanged(null, TimeZone.getDefault());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            dateModifier = new ScheduleEntryConstraintModifier(new Schedule(), new Task());
            dateModifier.startDateChanged(new Date(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new Helper();

        // Task without constraint
        // Start: 06/07/04 @ 8:00 AM
        // Duration: 1 day
        // Change Start Date to 06/08/04
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));

        newStartDate = DateUtils.parseDate("06/08/04");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.startDateChanged(newStartDate, helper.getTimeZone());
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

        // Task already has constraint (Start No Later Than)
        // Constraint is replaced
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"), TaskConstraintType.START_NO_LATER_THAN);

        newStartDate = DateUtils.parseDate("06/08/04");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.startDateChanged(newStartDate, helper.getTimeZone());
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#startDateChanged(java.util.Date,java.util.TimeZone)}
     * with a task dependency.
     */
    public void testStartDateChangedWithDependency() {

        ScheduleEntryConstraintModifier dateModifier;
        Date newStartDate;
        Date expectedStartDate;
        Date expectedEndDate;
        TaskConstraintType expectedConstraintType;
        Date expectedConstraintDate;
        Helper helper;

        helper = new Helper();

        // Task 1
        // Start: 06/07/04 @ 8:00 AM
        // 5 Days duration
        // Task 2
        // Depends on task 1 Finish to Start
        // Expected Start: 06/14/04 @ 8:00 AM
        // 1 Day duration
        //
        // Change Task 2 Start Date to 6/8/04
        // Task cannot start on 6/8/04 due to dependency
        // The date will not change, although the constraint will be added

        helper.scheduleEntry1.populate(new TimeQuantity(5, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.scheduleEntry2.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.addSuccessor(helper.scheduleEntry1, helper.scheduleEntry2, TaskDependencyType.FINISH_TO_START);
        helper.recalculate();
        // Check Task 2 is ok
        assertEquals(DateUtils.parseDateTime("06/14/04 8:00 AM"), helper.scheduleEntry2.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/14/04 5:00 PM"), helper.scheduleEntry2.getEndTime());

        // Now make the change
        newStartDate = DateUtils.parseDate("06/08/04");
        expectedStartDate = DateUtils.parseDateTime("06/14/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/14/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry2);
        dateModifier.startDateChanged(newStartDate, helper.getTimeZone());
        helper.scheduleEntry2.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

        //
        // Same Tasks
        // Change Task 2 Start Date to 6/21/04 @ 8:00 AM
        // Date will change
        helper.scheduleEntry1.populate(new TimeQuantity(5, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.scheduleEntry2.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.addSuccessor(helper.scheduleEntry1, helper.scheduleEntry2, TaskDependencyType.FINISH_TO_START);
        helper.recalculate();
        // Check Task 2 is ok
        assertEquals(DateUtils.parseDateTime("06/14/04 8:00 AM"), helper.scheduleEntry2.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/14/04 5:00 PM"), helper.scheduleEntry2.getEndTime());

        // Now make the change
        newStartDate = DateUtils.parseDate("06/21/04");
        expectedStartDate = DateUtils.parseDateTime("06/21/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/21/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/21/04 8:00 AM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry2);
        dateModifier.startDateChanged(newStartDate, helper.getTimeZone());
        helper.scheduleEntry2.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#endDateChanged(java.util.Date,java.util.TimeZone)} for
     * a task with no dependencies.
     */
    public void testEndDateChanged() {

        ScheduleEntryConstraintModifier dateModifier;
        Date newEndDate;
        Date expectedStartDate;
        Date expectedEndDate;
        TaskConstraintType expectedConstraintType;
        Date expectedConstraintDate;
        Helper helper;

        //
        // Null parameter
        //
        try {
            dateModifier = new ScheduleEntryConstraintModifier(new Schedule(), new Task());
            dateModifier.endDateChanged(null, TimeZone.getDefault());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            dateModifier = new ScheduleEntryConstraintModifier(new Schedule(), new Task());
            dateModifier.endDateChanged(new Date(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        helper = new Helper();

        // Task without constraint
        // Start: 06/07/04 @ 8:00 AM
        // End: 06/07/04 @ 5:00 PM
        // Duration: 1 day
        // Change End Date to 06/08/04 @ 5:00 PM
        // Expected Start Date: 06/08/04 @ 8:00 AM
        // Expected End Date: 06/08/04 @ 5:00 PM
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));

        newEndDate = DateUtils.parseDate("06/08/04");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.FINISH_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/08/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.endDateChanged(newEndDate, helper.getTimeZone());
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

        // Task already has constraint (Must Finish On)
        // Constraint is replaced
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"), TaskConstraintType.MUST_FINISH_ON);

        newEndDate = DateUtils.parseDate("06/08/04");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.FINISH_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/08/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.endDateChanged(newEndDate, helper.getTimeZone());
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);

    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#endDateChanged(java.util.Date,java.util.TimeZone)}
     * with a task dependency.
     */
    public void testEndDateChangedWithDependency() {

        ScheduleEntryConstraintModifier dateModifier;
        Date newEndDate;
        Date expectedStartDate;
        Date expectedEndDate;
        TaskConstraintType expectedConstraintType;
        Date expectedConstraintDate;
        Helper helper;

        helper = new Helper();

        // Task 1
        // Start: 06/07/04 @ 8:00 AM
        // 5 Days duration
        // End: 6/11/04 @ 5:00 PM
        // Task 2
        // Depends on task 1 Finish to Start
        // Start: 06/14/04 @ 8:00 AM
        // 1 Day duration
        // End: 06/14/04 @ 5:00 PM
        //
        // Change Task 2 End Date to 6/10/04
        // Task cannot finish on 6/10/04 due to dependency
        // The date will not change, although the constraint will be added
        helper.scheduleEntry1.populate(new TimeQuantity(5, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.scheduleEntry2.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.addSuccessor(helper.scheduleEntry1, helper.scheduleEntry2, TaskDependencyType.FINISH_TO_START);
        helper.recalculate();
        // Check Task 2 is ok
        assertEquals(DateUtils.parseDateTime("06/14/04 8:00 AM"), helper.scheduleEntry2.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/14/04 5:00 PM"), helper.scheduleEntry2.getEndTime());

        // Now make the change
        newEndDate = DateUtils.parseDate("06/10/04");
        expectedStartDate = DateUtils.parseDateTime("06/14/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/14/04 5:00 PM");
        expectedConstraintType = TaskConstraintType.FINISH_NO_EARLIER_THAN;
        expectedConstraintDate = DateUtils.parseDateTime("06/10/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry2);
        dateModifier.endDateChanged(newEndDate, helper.getTimeZone());
        helper.scheduleEntry2.assertValues(expectedStartDate, expectedEndDate, expectedConstraintType, expectedConstraintDate);


    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#constraintChanged(net.project.schedule.TaskConstraintType, java.util.Date)}
     * for a task with no dependencies.
     */
    public void testConstraintChanged() {

        ScheduleEntryConstraintModifier dateModifier;
        Date constraintDate;
        TaskConstraintType constraintType;
        Date expectedStartDate;
        Date expectedEndDate;
        Helper helper;

        //
        // Null Parameters
        //

        try {
            new ScheduleEntryConstraintModifier(new Schedule(), new Task()).constraintChanged(null, new Date(),TimeZone.getDefault());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new ScheduleEntryConstraintModifier(new Schedule(), new Task()).constraintChanged(TaskConstraintType.AS_SOON_AS_POSSIBLE, null,TimeZone.getDefault());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }


        helper = new Helper();

        // Task without constraint
        // Start: 06/07/04 @ 8:00 AM
        // End: 06/07/04 @ 5:00 PM
        // Duration: 1 day
        // Set Constraint to Start No Earlier Than @ 06/08/04 @ 8:00 AM
        // Expected Start Date: 06/08/04 @ 8:00 AM
        // Expected End Date: 06/08/04 @ 5:00 PM
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));

        constraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        constraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.constraintChanged(constraintType, constraintDate,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, constraintType, constraintDate);

        // Task already has constraint (Must Start On)
        // Constraint is replaced
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"), TaskConstraintType.MUST_START_ON);

        constraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        constraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.constraintChanged(constraintType, constraintDate,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, constraintType, constraintDate);

        // Start No Earlier 6/8/04 @ 8:00 AM
        // Start: 06/08/04 @ 8:00 AM
        // End: 06/08/04 @ 5:00 PM
        // changed to As Soon As Possible
        // Expected Start Date: 06/07/04 @ 8:00 AM
        // Expected End Date: 06/07/04 @ 5:00 PM

        // First Setup the SNE constraint
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.constraintChanged(TaskConstraintType.START_NO_EARLIER_THAN, DateUtils.parseDateTime("06/08/04 8:00 AM"),TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry1.assertValues(DateUtils.parseDateTime("06/08/04 8:00 AM"), DateUtils.parseDateTime("06/08/04 5:00 PM"), TaskConstraintType.START_NO_EARLIER_THAN, DateUtils.parseDateTime("06/08/04 8:00 AM"));

        // Now Switch it to ASAP
        constraintType = TaskConstraintType.AS_SOON_AS_POSSIBLE;
        expectedStartDate = DateUtils.parseDateTime("06/07/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/07/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.constraintChanged(constraintType, null,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, constraintType, null);
    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#constraintChanged(net.project.schedule.TaskConstraintType, java.util.Date)}
     * for a task with no dependencies.
     */
    public void testConstraintChangedWithDependency() {

        ScheduleEntryConstraintModifier dateModifier;
        Date constraintDate;
        TaskConstraintType constraintType;
        Date expectedStartDate;
        Date expectedEndDate;
        Helper helper;

        helper = new Helper();

        // Empty Task
        // 1 Day Duration, 8 hours Work
        helper.scheduleEntry1.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), null);
        constraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        constraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedStartDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/08/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry1);
        dateModifier.constraintChanged(constraintType, constraintDate,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry1.assertValues(expectedStartDate, expectedEndDate, constraintType, constraintDate);

        helper = new Helper();

        // Task 1
        // Start: 06/07/04 @ 8:00 AM
        // 5 Days duration
        // Task 2
        // Depends on task 1 Finish to Start
        // Expected Start: 06/14/04 @ 8:00 AM
        // 1 Day duration
        //
        // Change Task 2 Start No Earlier on 6/8/04 @ 8:00 AM
        // Task cannot start on 6/8/04 due to dependency
        // The date will not change, although the constraint will be added

        helper.scheduleEntry1.populate(new TimeQuantity(5, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.scheduleEntry2.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.addSuccessor(helper.scheduleEntry1, helper.scheduleEntry2, TaskDependencyType.FINISH_TO_START);
        helper.recalculate();
        // Check Task 2 is ok
        assertEquals(DateUtils.parseDateTime("06/14/04 8:00 AM"), helper.scheduleEntry2.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/14/04 5:00 PM"), helper.scheduleEntry2.getEndTime());

        // Now make the change
        constraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        constraintDate = DateUtils.parseDateTime("06/08/04 8:00 AM");
        expectedStartDate = DateUtils.parseDateTime("06/14/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/14/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry2);
        dateModifier.constraintChanged(constraintType, constraintDate,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry2.assertValues(expectedStartDate, expectedEndDate, constraintType, constraintDate);

        helper = new Helper();

        //
        // Same Tasks
        // Change Task 2 Start No Earlier on 6/21/04 @ 8:00 AM
        // Date will change
        helper.scheduleEntry1.populate(new TimeQuantity(5, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.scheduleEntry2.populate(new TimeQuantity(1, TimeQuantityUnit.DAY), DateUtils.parseDateTime("06/07/04 8:00 AM"));
        helper.addSuccessor(helper.scheduleEntry1, helper.scheduleEntry2, TaskDependencyType.FINISH_TO_START);
        helper.recalculate();
        // Check Task 2 is ok
        assertEquals(DateUtils.parseDateTime("06/14/04 8:00 AM"), helper.scheduleEntry2.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/14/04 5:00 PM"), helper.scheduleEntry2.getEndTime());

        // Now make the change
        constraintType = TaskConstraintType.START_NO_EARLIER_THAN;
        constraintDate = DateUtils.parseDateTime("06/21/04 8:00 AM");
        expectedStartDate = DateUtils.parseDateTime("06/21/04 8:00 AM");
        expectedEndDate = DateUtils.parseDateTime("06/21/04 5:00 PM");

        dateModifier = new ScheduleEntryConstraintModifier(helper.schedule, helper.scheduleEntry2);
        dateModifier.constraintChanged(constraintType, constraintDate,TimeZone.getTimeZone("America/Los_Angeles"));
        helper.scheduleEntry2.assertValues(expectedStartDate, expectedEndDate, constraintType, constraintDate);

    }

    /**
     * Tests {@link ScheduleEntryConstraintModifier#constraintChanged(net.project.schedule.TaskConstraintType, java.util.Date)}
     * for constraints that don't permit a constraint date to be set.
     */
    public void testConstraintChangedConstraintsWithoutDates() {

        try {
            new ScheduleEntryConstraintModifier(new Schedule(), new Task())
                    .constraintChanged(TaskConstraintType.AS_SOON_AS_POSSIBLE, new Date(),TimeZone.getTimeZone("America/Los_Angeles"));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new ScheduleEntryConstraintModifier(new Schedule(), new Task())
                    .constraintChanged(TaskConstraintType.AS_LATE_AS_POSSIBLE, new Date(),TimeZone.getTimeZone("America/Los_Angeles"));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a test helper for maintaining relationships between schedule entries.
     */
    private static class Helper {

        final IWorkingTimeCalendarProvider provider;
        final Schedule schedule;
        final TestTask scheduleEntry1;
        final TestTask scheduleEntry2;

        private Helper() {
            provider = new TestWorkingTimeCalendarProvider();

            schedule = new Schedule();
            schedule.setID("12");
            schedule.setWorkingTimeCalendarProvider(provider);
            schedule.setScheduleStartDate(DateUtils.parseDateTime("06/07/04 8:00 AM"));

            Map scheduleEntryMap = new HashMap();
            scheduleEntry1 = new TestTask(scheduleEntryMap, "1");
            scheduleEntry1.setPlanID(schedule.getID());
            scheduleEntry2 = new TestTask(scheduleEntryMap, "2");
            scheduleEntry2.setPlanID(schedule.getID());
            schedule.setEntryMap(scheduleEntryMap);
        }

        private void addSuccessor(ScheduleEntry predecessor, ScheduleEntry successor, TaskDependencyType dependencyType) {
            try {
                TaskDependency dependency = new TaskDependency(successor, predecessor, dependencyType, new TimeQuantity(0, TimeQuantityUnit.DAY));
                predecessor.getSuccessors().add(dependency);
                successor.getDependenciesNoLazyLoad().add(dependency);
            } catch (PersistenceException e) {
                fail("Error adding successor: " + e);
            }

        }

        private void recalculate() {
            try {
                new TaskEndpointCalculation().recalculateTaskTimesNoLoad(schedule);
            } catch (PersistenceException e) {
                fail("Error recalculating schedule: " + e);
            }
        }

        private TimeZone getTimeZone() {
            return TimeZone.getTimeZone("America/Los_Angeles");
        }

        /**
         * Provides a Test Task that initializes SuccessorList and PredecessorList.
         */
        private class TestTask extends Task {

            /**
             * Creates a new Test task.
             * @param scheduleEntryMap a map to which to add this task
             * @param taskID the id of the task
             */
            TestTask(Map scheduleEntryMap, String taskID) {
                super();
                setID(taskID);
                SuccessorList successors = new SuccessorList();
                successors.setLoaded(true);
                PredecessorList predecessors = new PredecessorList();
                predecessors.setLoaded(true);
                setSuccessors(successors);
                setPredecessors(predecessors);

                setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);

                scheduleEntryMap.put(getID(), this);
            }

            /**
             * Populate this task with the specified values.
             * <p/>
             * Calculates the end date automatically.
             * @param duration
             * @param startDate
             */
            void populate(TimeQuantity duration, Date startDate) {
                setDuration(duration);
                setStartTimeD(startDate);
                calculateEndDate(Helper.this.provider);
                setWork(ScheduleTimeQuantity.convertToHour(duration));
            }

            /**
             * Populate this task with the specified values.
             * <p/>
             * Calculates the end date automatically.
             * @param duration
             * @param startDate
             * @param constraintType
             */
            private void populate(TimeQuantity duration, Date startDate, TaskConstraintType constraintType) {
                populate(duration, startDate);
                setConstraintType(constraintType);
            }

            /**
             * Assert that the specified values are found.
             * @param expectedStartDate
             * @param expectedEndDate
             * @param expectedConstraintType
             * @param constraintDate
             */
            private void assertValues(Date expectedStartDate, Date expectedEndDate,
                    TaskConstraintType expectedConstraintType, Date constraintDate) {
                assertEquals(getTaskDescription() + "Start Date", expectedStartDate, getStartTime());
                assertEquals(getTaskDescription() + "End Date", expectedEndDate, getEndTime());
                assertEquals(getTaskDescription() + "Constraint Type", expectedConstraintType, getConstraintType());
                assertEquals(getTaskDescription() + "Constraint Date", constraintDate, getConstraintDate());
            }

            private String getTaskDescription() {
                return "Task ID " + getID() + " ";
            }

        }

    }

}
