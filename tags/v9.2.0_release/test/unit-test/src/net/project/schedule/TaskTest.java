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
 package net.project.schedule;

import java.beans.IntrospectionException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.admin.ApplicationSpace;
import net.project.application.Application;
import net.project.base.PnetException;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentList;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.SetMethodWatcher;
import net.project.utils.WellKnownObjects;

public class TaskTest extends TestCase {
    private static final TimeQuantity UNASSOCIATED_WORK = new TimeQuantity(1,TimeQuantityUnit.HOUR);
	private static final boolean SHARE_READ_ONLY = true;
	private static final String SHARED_OBJECT_ID = "123456";
	private final String TEST_TASK_ID = "1234";;
    private final String TEST_TASK_VERSION_ID = "5678";
    private final String TEST_TASK_NAME = "Name";
    private final String TEST_TASK_DESCRIPTION = "Description";
    private final Date TEST_TASK_START_TIME = net.project.test.util.DateUtils.makeDate("1/3/05 8:00 AM");
    private final Date TEST_TASK_END_TIME = net.project.test.util.DateUtils.makeDate("1/3/05 5:00 PM");
    private final TimeQuantity TEST_TASK_DURATION = new TimeQuantity(30, TimeQuantityUnit.DAY);
    private final TimeQuantity TEST_TASK_WORK = new TimeQuantity(9, TimeQuantityUnit.DAY);
    private final int TEST_TASK_PERCENT_COMPLETE = 50;
    private final BigDecimal TEST_TASK_WORK_PERCENT_COMPLETE = new BigDecimal("0.06250");
    private final TimeQuantity TEST_TASK_WORK_COMPLETE = new TimeQuantity(4.5, TimeQuantityUnit.HOUR);
    private final String TEST_TASK_PHASE_NAME = "Phase Name";
    private final Date TEST_TASK_ACTUAL_START_TIME = net.project.test.util.DateUtils.makeDate("1/3/05 8:30 AM");
    private final Date TEST_TASK_ACTUAL_END_TIME = net.project.test.util.DateUtils.makeDate("1/4/05 8:15 AM");
    private final String TEST_TASK_PARENT_TASK_ID = "12341";
    private final String TEST_TASK_PARENT_TASK_NAME = "Task 1234-1";
    private final String TEST_TASK_HIERARCHY_LEVEL = "AAA";
    private final boolean TEST_TASK_IS_ORPHAN = true;
    private final String TEST_TASK_IMPORT_TASK_ID = "12342";
    private final String TEST_TASK_IMPORT_PARENT_TASK_ID = "12343";
    private final String TEST_TASK_COMMENT = "asdkjl;kjqw;lekjr;lqjkl2;k4j";
    private final Date TEST_TASK_DEADLINE = net.project.test.util.DateUtils.makeDate("1/1/2005 1:00 AM");
    private final PredecessorList TEST_TASK_PREDECESSOR_LIST = new PredecessorList();
    private final SuccessorList TEST_TASK_SUCCESSOR_LIST = new SuccessorList();
    private final String TEST_TASK_PLAN_ID = "100000";
    private final Date TEST_TASK_EARLY_START = net.project.test.util.DateUtils.makeDate("12/30/04 8:00 AM");
    private final Date TEST_TASK_EARLY_FINISH = net.project.test.util.DateUtils.makeDate("12/30/04 5;00 PM");
    private final Date TEST_TASK_LATE_START = net.project.test.util.DateUtils.makeDate("1/3/05 8:02 AM");
    private final Date TEST_TASK_LATE_FINISH = net.project.test.util.DateUtils.makeDate("1/3/05 5:02 PN");
    private final int TEST_TASK_SEQ_NUM = 999;
    private final boolean TEST_TASK_CRITICAL_PATH = true;
    private final boolean TEST_TASK_SEND_NOTIFICATIONS = false;
    private final boolean TEST_TASK_IGNORE_TIME_PORTION = true;
    private final boolean TEST_TASK_ASSIGNEES_LOADED = true;
    private final boolean TEST_TASK_IS_MILESTONE = true;
    private final String TEST_TASK_SHARING_SPACE_NAME = "Sharing Space 1";
    private final String TEST_TASK_SHARING_SPACE_ID = "203874029384092384";
    private final boolean TEST_TASK_IS_FROM_SHARE = true;
    private final TimeQuantity TEST_TASK_BASELINE_DURATION = new TimeQuantity(23498792, TimeQuantityUnit.SECOND);
    private final TimeQuantity TEST_TASK_BASELINE_WORK = new TimeQuantity(923764, TimeQuantityUnit.MINUTE);
    private final Date TEST_TASK_BASELINE_START = net.project.test.util.DateUtils.makeDate("12/1/04 8:00 AM");
    private final Date TEST_TASK_BASELINE_END = net.project.test.util.DateUtils.makeDate("12/1/04 5:00 PM");
    private final TimeQuantity TEST_TASK_UNALLOCATED_WORK_COMPLETE = new TimeQuantity(4, TimeQuantityUnit.HOUR);
    private final AssignmentList TEST_TASK_ASSIGNMENT_LIST = new AssignmentList();
    private final String TEST_TASK_SPACE_ID = "9594949";

    public TaskTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        Application.login();

        // Ensure the plan is created
        WellKnownObjects.ensurePlan(WellKnownObjects.TEST_SPACE_ID, WellKnownObjects.TEST_PLAN_ID);

        SessionManager.getUser().setCurrentSpace(ApplicationSpace.DEFAULT_APPLICATION_SPACE);

        //Set up some test variables
        TaskDependency td = new TaskDependency();
        td.setTaskID("1234");
        td.setDependencyID("2222");
        TEST_TASK_PREDECESSOR_LIST.add(td);

        TaskDependency td2 = new TaskDependency();
        td2.setTaskID("1234");
        td2.setDependencyID("3333");
        TEST_TASK_SUCCESSOR_LIST.add(td2);

        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setObjectID(TEST_TASK_ID);
        assignment.setPersonID("1");
        TEST_TASK_ASSIGNMENT_LIST.addAssignment(assignment);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TaskTest.class);

        return suite;
    }

    /**
     * Tests the ability to QuickAdd a task.
     */
    public void testQuickAdd() throws PnetException, ParseException {

        Schedule schedule = new Schedule();
        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        // We use a custom working time calendar provider that always
        // returns the default working time calendar
        schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));
        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);

        try {

            // Set up the values entered during quick add
            String taskName = "Task Test 1";
            Date startTime = WorkingTimeCalendarDefinitionTest.makeDateTime("08/11/03 8:00 AM");
            // End time is two days after start time
            Date endTime = new Date(startTime.getTime() + (1000 * 60 * 60 * 24 * 2));
            // Work is 16 hours
            TimeQuantity work = TimeQuantity.parse("16", String.valueOf(TimeQuantityUnit.HOUR.getUniqueID()));

            // Now create the task with the defaults used during quick add
            Task task = new Task();
            task.setName(taskName);
            task.setStartTimeD(startTime);
            task.setEndTimeD(endTime);
            task.setWork(work);
            task.setPlanID(WellKnownObjects.TEST_PLAN_ID);
            task.setPriority(TaskPriority.PRIORITY_NORMAL.getID());
            task.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
            task.setTaskCalculationType(schedule.getDefaultTaskCalculationType());
            task.store(false, schedule);

            assertNotNull("Task ID was null after storing", task.getID());

        } catch (PersistenceException e) {
            fail("Error creating task: " + e);
        }

    }

    /**
     * Tests the clear() method.
     */
    public void testClear() {
        ScheduleEntry task = new Task();
        task.setID("6610");
        task.setName("Task 1");
        PredecessorList tdl = new PredecessorList();
        tdl.add(new TaskDependency());
        task.setPredecessors(tdl);
        try {
            task.clear();
            assertEquals(null, task.getID());
            assertEquals(0, task.getPredecessors().size());
            assertNull(task.getName());
        } catch (PersistenceException e) {
            fail("PersistenceException thrown while loading task");
        }
    }

    /**
     * Tests the loading of a task.
     */
    public void testLoad() throws ParseException {

        // Name of task to create then check after loading
        String taskName = "Task Test 1";

        Schedule schedule = new Schedule();
        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        // We use a custom working time calendar provider that always
        // returns the default working time calendar
        schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        try {

            // Setup the values for the task
            Date startTime = WorkingTimeCalendarDefinitionTest.makeDateTime("08/11/03 8:00 AM");
            Date endTime = new Date(startTime.getTime() + (1000 * 60 * 60 * 24 * 4));
            TimeQuantity work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
            TimeQuantity workComplete = new TimeQuantity(2, TimeQuantityUnit.HOUR);

            // Create the task
            Task task = new Task();
            task.setName(taskName);
            task.setStartTimeD(startTime);
            task.setEndTimeD(endTime);
            task.setWork(work);
            task.setWorkComplete(workComplete);
            task.setPlanID(WellKnownObjects.TEST_PLAN_ID);
            task.setPriority(TaskPriority.PRIORITY_NORMAL.getID());
            task.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
            task.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
            task.store(false, schedule);

            // Grab the newly created task ID
            String taskID = task.getID();
            assertNotNull("Task ID was null after storing", taskID);

            // Now test the loading
            task = new Task();
            task.setID(taskID);
            task.load();

            //Make sure that the fields are as we expect them to be
            assertEquals(taskName, task.getName());
            assertEquals(startTime, task.getStartTime());
            assertEquals(endTime, task.getEndTime());
            assertEquals(work, task.getWorkTQ());
            assertEquals(workComplete, task.getWorkCompleteTQ());
            assertEquals(WellKnownObjects.TEST_PLAN_ID, task.getPlanID());
            assertEquals(TaskPriority.PRIORITY_NORMAL, task.getPriority());
            assertEquals(TaskConstraintType.AS_SOON_AS_POSSIBLE.getID(), task.getConstraintTypeID());
            assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, task.getTaskCalculationType());
            assertEquals(new BigDecimal("0.25000"), task.getWorkPercentCompleteDecimal());
            assertEquals(new BigDecimal("0.25000"), task.getPercentCompleteDecimal());


        } catch (PersistenceException e) {
            e.printStackTrace();
            fail("PersistenceException thrown while testing task load");

        }

    }

    /**
     * Tests getIntPercentComplete().
     */
    public void testGetWorkPercentCompleteInt() {
        ScheduleEntry task = new Task();

        // Try all combination of Month, week, day and hour
        task.setWork(new TimeQuantity(2, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.MONTH));
        assertEquals(new BigDecimal("0.50000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(new BigDecimal("0.12500"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("0.25000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.05000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.WEEK));
        task.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(new BigDecimal("0.50000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.WEEK));
        task.setWorkComplete(new TimeQuantity(5, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("0.50000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.WEEK));
        task.setWorkComplete(new TimeQuantity(20, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.25000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        task.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("0.50000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        task.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.25000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.50000"), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(9, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(1.8, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.20000"), task.getWorkPercentCompleteDecimal());

        // Try Zero work (results in zero percent)
        task.setWork(new TimeQuantity(0, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal(0), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(0, TimeQuantityUnit.WEEK));
        task.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal(0), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(0, TimeQuantityUnit.DAY));
        task.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal(0), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal(0), task.getWorkPercentCompleteDecimal());

        // More work complete than actual work (results in 100 percent)
        task.setWork(new TimeQuantity(1, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(21, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal(1), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        task.setWorkComplete(new TimeQuantity(41, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal(1), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        task.setWorkComplete(new TimeQuantity(17, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal(1), task.getWorkPercentCompleteDecimal());

        task.setWork(new TimeQuantity(15, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal(1), task.getWorkPercentCompleteDecimal());

        // Additional ratios
        task.setWork(new TimeQuantity(12, TimeQuantityUnit.MONTH));
        task.setWorkComplete(new TimeQuantity(96, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.05000"), task.getWorkPercentCompleteDecimal());
    }

    /**
     * Tests {@link ScheduleEntry#getPercentComplete()}.
     */
    public void testGetPercentCompleteInt() {

        ScheduleEntry task = new Task();

        task.setPercentComplete(new BigDecimal(0));
        assertEquals(new BigDecimal("0.00000"), task.getPercentCompleteDecimal());

        task.setPercentComplete(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01000"), task.getPercentCompleteDecimal());

        task.setPercentComplete(new BigDecimal("0.1"));
        assertEquals(new BigDecimal("0.10000"), task.getPercentCompleteDecimal());

        task.setPercentComplete(new BigDecimal("1"));
        assertEquals(new BigDecimal("1.00000"), task.getPercentCompleteDecimal());

        task.setPercentComplete(new BigDecimal("10"));
        assertEquals(new BigDecimal("10.00000"), task.getPercentCompleteDecimal());

    }

    // 03/31/2004 - Tim
    // Method was eliminated; percent complete is either set for Milestones
    // or unused for non-milestones
    // Previously non-milestone percent complete was calculated from actual duration;
    // but actual duration's calculation is invalid
    // We really need to introduce percent complete as a field, use it to caclculate
    // actual duration once and then use actual duration to calculate percent complete
    // after that
//    public void testCalculatePercentComplete() {
//
//        Calendar cal = new GregorianCalendar(SessionManager.getUser().getTimeZone());
//        cal.set(2003, Calendar.OCTOBER, 20, 8, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//
//        ScheduleEntry task;
//
//        // 1 Day duration
//        // 0 day work
//        // Expected: 0% complete
//        task = new Task();
//        task.setStartTimeD(cal.getTime());
//        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
//        task.setWorkComplete(new TimeQuantity(new BigDecimal(0), TimeQuantityUnit.DAY));
//        task.calculatePercentComplete();
//        assertEquals(0, task.getPercentComplete());
//
//        // 1 Day duration
//        // 4 hours work
//        // Expected: 50% complete
//        task = new Task();
//        task.setStartTimeD(cal.getTime());
//        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
//        task.setWorkComplete(new TimeQuantity(new BigDecimal(4), TimeQuantityUnit.HOUR));
//        task.calculatePercentComplete();
//        assertEquals(50, task.getPercentComplete());
//
//        // 1 Day duration
//        // 1 day work
//        // Expected: 100% complete
//        task = new Task();
//        task.setStartTimeD(cal.getTime());
//        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
//        task.setWorkComplete(new TimeQuantity(new BigDecimal(1), TimeQuantityUnit.DAY));
//        task.calculatePercentComplete();
//        assertEquals(100, task.getPercentComplete());
//
//        // 1 Day duration
//        // 2 day work
//        // Expected: 200% complete
//        task = new Task();
//        task.setStartTimeD(cal.getTime());
//        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
//        task.setWorkComplete(new TimeQuantity(new BigDecimal(2), TimeQuantityUnit.DAY));
//        task.calculatePercentComplete();
//        assertEquals(200, task.getPercentComplete());
//
//    }

    /**
     * Tests {@link ScheduleEntry#isComplete()}.
     */
    public void testIsComplete() {

        ScheduleEntry task;

        // Milestone task
        task = new Task();
        task.setMilestone(true);

        // No work, just percent complete
        task.setPercentComplete(new BigDecimal(0));
        assertFalse(task.isComplete());

        task.setPercentComplete(new BigDecimal("0.5"));
        assertFalse(task.isComplete());

        task.setPercentComplete(new BigDecimal("0.99"));
        assertFalse(task.isComplete());

        task.setPercentComplete(new BigDecimal("1"));
        assertTrue(task.isComplete());

        task.setPercentComplete(new BigDecimal("1.01"));
        assertTrue(task.isComplete());

        // With work; no longer based on percent complete
        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertFalse(task.isComplete());

        task.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assertFalse(task.isComplete());

        task.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertTrue(task.isComplete());

        // Regular task
        task = new Task();
        task.setMilestone(false);

        // For a non-milestone, percent complete is ignored
        task.setPercentComplete(new BigDecimal("1"));
        assertFalse(task.isComplete());

        // Based on work and work complete
        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertFalse(task.isComplete());

        task.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assertFalse(task.isComplete());

        task.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertTrue(task.isComplete());

    }



    /**
     * Tests a limited amount of functionality of {@link Task#calculateDuration}.
     * Most functionality is tested by {@link TaskEndpointCalculationTest}.
     */
    public void testCalculateDuration() {

        // Create a schedule that provides time zone and a system default
        // working time calendar
        // System default calendar is used because "defaultCalendarID" is null
        Schedule schedule = new Schedule();
        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        // We use a custom working time calendar provider that always
        // returns the default working time calendar
        schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();

        ScheduleEntry task;

        // No Assignments
        // Invalid to calculate duration without assignments; duration is not
        // based on work
        task = new Task();
        task.setStartTimeD(new Date());
        task.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        try {
            task.calculateDuration(schedule.getWorkingTimeCalendarProvider());
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Null start time, null work
        // Expected: 0 days
        task = makeScheduleEntry(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
        task.setWork(null);
        task.calculateDuration(schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), task.getDurationTQ());

        // Null start time
        // Expected: 0 days
        task = makeScheduleEntry(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
        task.calculateDuration(schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), task.getDurationTQ());

        // Null work
        // Expected: 0 days
        task = makeScheduleEntry(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
        task.setWork(null);
        task.setStartTimeD(new Date());
        task.calculateDuration(schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), task.getDurationTQ());

    }

    /**
     * Tests {@link ScheduleEntry#calculateWork(net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}.
     */
    public void testCalculateWork() {

        ScheduleEntry task;

        // Start July 15th 2003 @ 8:00 AM
        Calendar cal = new GregorianCalendar(SessionManager.getUser().getTimeZone());
        cal.set(2003, 6, 15, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TimeQuantity expected;

        TimeZone timeZone;
        TestWorkingTimeCalendarProvider provider;
        WorkingTimeCalendarDefinition calendarDef;
        ScheduleEntryAssignment assignment;
        ScheduleEntry scheduleEntry;

        timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        provider = new TestWorkingTimeCalendarProvider();
        // Default calendar
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[] {Calendar.SATURDAY, Calendar.SUNDAY});

        //
        // Single Assignment
        //

        // 100 % assigned
        assignment = ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef, provider);
        scheduleEntry = makeScheduleEntry(assignment);

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 6th 2004 @ 5:00 PM
        // Expected: 5 hours
        scheduleEntry.setStartTimeD(makeDateTime("5/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("5/5/04 5:00 PM"));
        expected = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        // 50% assigned
        assignment = ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 50, timeZone, calendarDef, provider);
        scheduleEntry = makeScheduleEntry(assignment);

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 6th 2004 @ 5:00 PM
        // Expected: 2.5 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(2.5, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        //
        // Two Assignments
        //

        // Assignment 1: 100%
        // Assignment 2: 100%
        scheduleEntry = makeScheduleEntry(
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef, provider),
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef, provider)
        );

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 10 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(10, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        // Assignment 1: 50%
        // Assignment 2: 50%
        scheduleEntry = makeScheduleEntry(
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 50, timeZone, calendarDef, provider),
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 50, timeZone, calendarDef, provider)
        );

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 5 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        // Assignment 1: 12%
        // Assignment 2: 13%
        scheduleEntry = makeScheduleEntry(
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 12, timeZone, calendarDef, provider),
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 13, timeZone, calendarDef, provider)
        );

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 1.25 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(1.25, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        // Assignment 1: 1000%
        // Assignment 2: 2000%
        scheduleEntry = makeScheduleEntry(
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 1000, timeZone, calendarDef, provider),
                ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 2000, timeZone, calendarDef, provider)
        );

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 150 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(150, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());


        //
        // 1000 Assignments @ 1%
        //
        ScheduleEntryAssignment[] assignments = new ScheduleEntryAssignment[1000];
        for (int i = 0; i < 1000; i++) {
            assignments[i] = ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 1, timeZone, calendarDef, provider);
        }
        scheduleEntry = makeScheduleEntry(assignments);

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 50 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/5/04 5:00 PM"));
        expected = new TimeQuantity(50, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Friday April 1st 2005 @ 5:00 PM
        // Expected: 20,770 hours
        scheduleEntry.setStartTimeD(makeDateTime("4/5/04 8:00 AM"));
        scheduleEntry.setEndTimeD(makeDateTime("4/1/05 5:00 PM"));
        expected = new TimeQuantity(20770, TimeQuantityUnit.HOUR);
        scheduleEntry.calculateWork(provider);
        assertEquals(expected, scheduleEntry.getWorkTQ());

    }


    /**
     * This method verifies the authenticity of the clone method by making sure
     * that a clone is the same as the original using the equals method.  It is
     * quite important, however, to also make sure that both clone and equals
     * are actually *USING* all of the methods they need to be.  We do this
     * using reflection.
     */
    public void testClone() throws ClassNotFoundException, IntrospectionException, PersistenceException {
        SetMethodWatcher watcher = new SetMethodWatcher(Class.forName("net.project.schedule.Task"));
        Task taskToClone = createTestTask(watcher);

        //How to check
        //1. PhaseID
        //2. PhaseName
        //3. Date Created
        //4. Date Modified
        //5. Modified By ID
        //6. Unallocated Work
        //7. Selected Phase ID
        //8. Is Phases Loaded
        //9. task.comments
        //10. isLoaded()

        //First, make sure we've actually registered all the set methods that
        //are available in task.  If we haven't, it means that someone added a
        //method and didn't know to update either this unit test and/or clone
        //and/or equals
        if (!watcher.allMethodsCalled()) {
            String methodsMissed = "The following methods were unaccounted for: \n";
            for (Iterator it = watcher.getUncalledMethod().iterator(); it.hasNext();) {
                String uncalledMethodName = (String)it.next();
                methodsMissed += "  " + uncalledMethodName + "\n";
            }

            fail(methodsMissed);
        }


        Task clone = (Task)taskToClone.clone();
        ensureEquals(clone);
    }

    private Task createTestTask(SetMethodWatcher watcher) {
        Task taskToClone = (Task)ScheduleEntryFactory.createFromType(TaskType.TASK);
        taskToClone.setID(TEST_TASK_ID);
        watcher.methodCalled("setID");
        taskToClone.setTaskVersionID(TEST_TASK_VERSION_ID);
        watcher.methodCalled("setTaskVersionID");
        taskToClone.setName(TEST_TASK_NAME);
        watcher.methodCalled("setName");
        taskToClone.setDescription(TEST_TASK_DESCRIPTION);
        watcher.methodCalled("setDescription");
        taskToClone.setStartTimeD(TEST_TASK_START_TIME);
        watcher.methodCalled("setStartTimeD");
        taskToClone.setEndTimeD(TEST_TASK_END_TIME);
        watcher.methodCalled("setEndTimeD");
        taskToClone.setDuration(TEST_TASK_DURATION);
        watcher.methodCalled("setDuration");
        watcher.skipMethod("setDurationAmount");
        watcher.skipMethod("setDurationUnits");
        //You should be able to set percent complete as long as work isn't set
        taskToClone.setWork(TEST_TASK_WORK);
        watcher.skipMethod("setPercentComplete");
        // We can set workPercentComplete, but it is recalculated as the
        // ratio of work to work complete wonce work is set
        taskToClone.setWorkPercentComplete(TEST_TASK_WORK_PERCENT_COMPLETE);
        watcher.methodCalled("setWorkPercentComplete");
        taskToClone.setWork(TEST_TASK_WORK);
        watcher.methodCalled("setWork");

        taskToClone.setWorkComplete(TEST_TASK_WORK_COMPLETE);
        watcher.methodCalled("setWorkComplete");

        // Work Percent Complete = Work Complete / Work
        BigDecimal expectedWorkPercentComplete = new BigDecimal("0.04018");

        //Now you shouldn't be able to set the percent complete
        try {
            watcher.methodCalled("setPercentCompleteInt");
            taskToClone.setPercentCompleteInt(TEST_TASK_PERCENT_COMPLETE);
            fail("We should have received an exception here.");
        } catch (IllegalStateException e) {
            //This exception is supposed to happen
        }

        taskToClone.setPhaseName(TEST_TASK_PHASE_NAME);
        watcher.methodCalled("setPhaseName");
        taskToClone.setActualStartTimeD(TEST_TASK_ACTUAL_START_TIME);
        watcher.methodCalled("setActualStartTimeD");
        taskToClone.setActualEndTimeD(TEST_TASK_ACTUAL_END_TIME);
        watcher.methodCalled("setActualEndTimeD");
        taskToClone.setPriority(TaskPriority.PRIORITY_LOW);
        watcher.methodCalled("setPriority");
        taskToClone.setParentTaskID(TEST_TASK_PARENT_TASK_ID);
        watcher.methodCalled("setParentTaskID");
        taskToClone.setParentTaskName(TEST_TASK_PARENT_TASK_NAME);
        watcher.methodCalled("setParentTaskName");
        taskToClone.setHierarchyLevel(TEST_TASK_HIERARCHY_LEVEL);
        watcher.methodCalled("calculateHierarchyLevel");
        taskToClone.setOrphan(TEST_TASK_IS_ORPHAN);
        watcher.methodCalled("setOrphan");
        taskToClone.setImportTaskID(TEST_TASK_IMPORT_TASK_ID);
        watcher.methodCalled("setImportTaskID");
        taskToClone.setImportParentTaskID(TEST_TASK_IMPORT_PARENT_TASK_ID);
        watcher.methodCalled("setImportParentTaskID");
        taskToClone.setComment(TEST_TASK_COMMENT);
        watcher.methodCalled("setComment");
        taskToClone.setConstraintType(TaskConstraintType.AS_LATE_AS_POSSIBLE);
        watcher.methodCalled("setConstraintType");
        taskToClone.setDeadline(TEST_TASK_DEADLINE);
        watcher.methodCalled("setDeadline");
        taskToClone.setPredecessors(TEST_TASK_PREDECESSOR_LIST);
        watcher.methodCalled("setPredecessors");
        taskToClone.setSuccessors(TEST_TASK_SUCCESSOR_LIST);
        watcher.methodCalled("setSuccessors");
        watcher.skipMethod("setConstraintDate");
        taskToClone.setPlanID(TEST_TASK_PLAN_ID);
        watcher.methodCalled("setPlanID");
        taskToClone.setEarliestStartDate(TEST_TASK_EARLY_START);
        watcher.methodCalled("setEarliestStartDate");
        taskToClone.setEarliestFinishDate(TEST_TASK_EARLY_FINISH);
        watcher.methodCalled("setEarliestFinishDate");
        taskToClone.setLatestStartDate(TEST_TASK_LATE_START);
        watcher.methodCalled("setLatestStartDate");
        taskToClone.setLatestFinishDate(TEST_TASK_LATE_FINISH);
        watcher.methodCalled("setLatestFinishDate");
        taskToClone.setSequenceNumber(TEST_TASK_SEQ_NUM);
        watcher.methodCalled("setSequenceNumber");
        taskToClone.setCriticalPath(TEST_TASK_CRITICAL_PATH);
        watcher.methodCalled("setCriticalPath");
        taskToClone.setSendNotifications(TEST_TASK_SEND_NOTIFICATIONS);
        watcher.methodCalled("setSendNotifications");
        taskToClone.setIgnoreTimePortionOfDate(TEST_TASK_IGNORE_TIME_PORTION);
        watcher.methodCalled("setIgnoreTimePortionOfDate");
        taskToClone.setAssigneesLoaded(TEST_TASK_ASSIGNEES_LOADED);
        watcher.methodCalled("setAssigneesLoaded");
        taskToClone.setMilestone(TEST_TASK_IS_MILESTONE);
        watcher.methodCalled("setMilestone");
        taskToClone.setSharingSpaceName(TEST_TASK_SHARING_SPACE_NAME);
        watcher.methodCalled("setSharingSpaceName");
        taskToClone.setSharingSpaceID(TEST_TASK_SHARING_SPACE_ID);
        watcher.methodCalled("setSharingSpaceID");
        taskToClone.setFromShare(TEST_TASK_IS_FROM_SHARE);
        watcher.methodCalled("setFromShare");
        watcher.skipMethod("setFieldsFromScheduleEntry");
        watcher.methodCalled("setRecalculateDuration");
        taskToClone.setBaselineDuration(TEST_TASK_BASELINE_DURATION);
        watcher.methodCalled("setBaselineDuration");
        taskToClone.setBaselineWork(TEST_TASK_BASELINE_WORK);
        watcher.methodCalled("setBaselineWork");
        taskToClone.setBaselineStart(TEST_TASK_BASELINE_START);
        watcher.methodCalled("setBaselineStart");
        taskToClone.setBaselineEnd(TEST_TASK_BASELINE_END);
        watcher.methodCalled("setBaselineEnd");
        taskToClone.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        watcher.methodCalled("setTaskCalculationType");
        taskToClone.setUnallocatedWorkComplete(TEST_TASK_UNALLOCATED_WORK_COMPLETE);
        watcher.methodCalled("setUnallocatedWorkComplete");
        taskToClone.setAssignmentList(TEST_TASK_ASSIGNMENT_LIST);
        watcher.methodCalled("setAssignmentList");
        taskToClone.setSpaceID(TEST_TASK_SPACE_ID);
        watcher.methodCalled("setSpaceID");
        
        taskToClone.setSharedObjectID(SHARED_OBJECT_ID);
        watcher.methodCalled("setSharedObjectID");
        taskToClone.setShareReadOnly(SHARE_READ_ONLY);
        watcher.methodCalled("setShareReadOnly");
        taskToClone.setUnassociatedWorkComplete(UNASSOCIATED_WORK);
        watcher.methodCalled("setUnassociatedWorkComplete");
        
        watcher.skipMethod("setModifiedByID");
        watcher.skipMethod("setUnindentStatus");
        watcher.skipMethod("setRowClass");
        watcher.skipMethod("setWBSLevel");
        watcher.skipMethod("setWBS");
        watcher.skipMethod("setExpanded");
        watcher.skipMethod("setEndVariance");
        watcher.skipMethod("setStartVariance");
        watcher.skipMethod("setVisible");
        watcher.skipMethod("setEvenEntryCss");
        watcher.skipMethod("setFormattedModifiedDate");
        watcher.skipMethod("setChargeCodeName");
        watcher.skipMethod("setChargeCodeId");
        return taskToClone;
    }

    private void ensureEquals(Task clone)  {
        assertEquals(TaskType.TASK, clone.getTaskType());
        assertEquals(TEST_TASK_ID, clone.getID());
        assertEquals(TEST_TASK_VERSION_ID, clone.getTaskVersionID());
        assertEquals(TEST_TASK_NAME, clone.getName());
        assertEquals(TEST_TASK_DESCRIPTION, clone.getDescription());
        assertEquals(TEST_TASK_START_TIME, clone.getStartTime());
        assertEquals(TEST_TASK_END_TIME, clone.getEndTime());
        assertEquals(TEST_TASK_DURATION, clone.getDurationTQ());
        assertEquals(TEST_TASK_WORK_PERCENT_COMPLETE, clone.getWorkPercentCompleteDecimal());
        assertEquals(TEST_TASK_WORK, clone.getWorkTQ());
        assertEquals(TEST_TASK_WORK_COMPLETE, clone.getWorkCompleteTQ());
        assertEquals(TEST_TASK_PHASE_NAME, clone.getPhaseName());
        assertEquals(TEST_TASK_ACTUAL_START_TIME, clone.getActualStartTime());
        assertEquals(TEST_TASK_ACTUAL_END_TIME, clone.getActualEndTime());
        assertEquals(TaskPriority.PRIORITY_LOW, clone.getPriority());
        assertEquals(TEST_TASK_PARENT_TASK_ID, clone.getParentTaskID());
        assertEquals(TEST_TASK_PARENT_TASK_NAME, clone.getParentTaskName());
        assertEquals(TEST_TASK_HIERARCHY_LEVEL, clone.getHierarchyLevel());
        assertEquals(TEST_TASK_IS_ORPHAN, clone.isOrphan());
        assertEquals(TEST_TASK_IMPORT_TASK_ID, clone.getImportTaskID());
        assertEquals(TEST_TASK_IMPORT_PARENT_TASK_ID, clone.getImportParentTaskID());
        assertEquals(TEST_TASK_COMMENT, clone.getComment());
        assertEquals(TEST_TASK_PREDECESSOR_LIST, clone.getPredecessorsNoLazyLoad());
        assertEquals(TEST_TASK_SUCCESSOR_LIST, clone.getSuccessorsNoLazyLoad());
        assertEquals(TaskConstraintType.AS_LATE_AS_POSSIBLE, clone.getConstraintType());
        assertEquals(TEST_TASK_DEADLINE, clone.getDeadline());
        assertEquals(TEST_TASK_PLAN_ID, clone.getPlanID());
        assertEquals(TEST_TASK_EARLY_START, clone.getEarliestStartDate());
        assertEquals(TEST_TASK_EARLY_FINISH, clone.getEarliestFinishDate());
        assertEquals(TEST_TASK_LATE_START, clone.getLatestStartDate());
        assertEquals(TEST_TASK_LATE_FINISH, clone.getLatestFinishDate());
        assertEquals(TEST_TASK_SEQ_NUM, clone.getSequenceNumber());
        assertEquals(TEST_TASK_CRITICAL_PATH, clone.isCriticalPath());
        assertEquals(TEST_TASK_SEND_NOTIFICATIONS, clone.sendNotifications);
        assertEquals(TEST_TASK_IGNORE_TIME_PORTION, clone.isIgnoreTimePortionOfDate());
        assertEquals(TEST_TASK_ASSIGNEES_LOADED, clone.isAssigneesLoaded());
        assertEquals(TEST_TASK_IS_MILESTONE, clone.isMilestone());
        assertEquals(TEST_TASK_ASSIGNMENT_LIST.size(), clone.getAssignmentList().getAssignments().size());
        assertEquals(TEST_TASK_BASELINE_START, clone.getBaselineStart());
        assertEquals(TEST_TASK_BASELINE_END, clone.getBaselineEnd());
        assertEquals(TEST_TASK_BASELINE_WORK, clone.getBaselineWork());
        assertEquals(TEST_TASK_BASELINE_DURATION, clone.getBaselineDuration());
        assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, clone.getTaskCalculationType());
        assertEquals(TEST_TASK_UNALLOCATED_WORK_COMPLETE, clone.getUnallocatedWorkComplete());
        
        assertEquals(SHARED_OBJECT_ID, clone.getSharedObjectID());
        assertEquals(SHARE_READ_ONLY, clone.isShareReadOnly());
        assertEquals(UNASSOCIATED_WORK, clone.getUnassociatedWorkComplete());
        
    }

    /**
     * Tests {@link ScheduleEntry#getDaysWorked(net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}.
     */
    public void testGetDaysWorked() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TimeQuantity work;
        Date startDate;

        Collection assignments;
        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        ScheduleEntry scheduleEntry;

        //
        // No assignments
        // In this case, TEST_TASK_DURATION is calculated by a 100% assignment performing an
        // amount of work equal to the TEST_TASK_DURATION
        //

        // StartDate: Monday June 2nd @ 8:00 AM
        // Duration: 0 days
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, Collections.EMPTY_SET);
        scheduleEntry.setStartTimeD(startDate);
        scheduleEntry.setDuration(new TimeQuantity(0, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("0.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Duration: 3 days
        // Work: 0 hours
        // Expected: 3 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, Collections.EMPTY_SET);
        scheduleEntry.setStartTimeD(startDate);
        scheduleEntry.setDuration(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("3.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Duration: 3 days
        // Work: 16 hours (ignored for this test)
        // Expected: 3 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, Collections.EMPTY_SET);
        scheduleEntry.setStartTimeD(startDate);
        scheduleEntry.setDuration(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(new BigDecimal("3.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        //
        // 100%, 100%
        //

        assignments = helper.makeTwoAssignments(100, 100);

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("0.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 1.5 day
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("1.5000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 8 hours
        // Expected: 0.875 day
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("0.8750000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 12 days
        // Expected: 9 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(12, TimeQuantityUnit.DAY);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("9.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        //
        // 25%, 35%
        //

        assignments = helper.makeTwoAssignments(25, 35);

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 5.16 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("5.1666666667"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        //
        // Miscellaneous
        //

        // Issue: Infinite loop
        // Start date: Tuesday December 3rd @ 8:00 AM
        // Work factor: 1.12
        // Work: 1.18 hours
        // Expected: 0.13 days  (work / factor) / 8
        assignments = helper.makeSingleAssignment(112);
        cal.set(2002, Calendar.DECEMBER, 3, 8, 0);
        work = new TimeQuantity(1.18, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("0.1312500000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // Issue: Unusual combinations
        // Start date: Monday October 6th @ 8:00 AM
        // Work factor: 20,000,000 (2 billion %)
        // Work: 1,666,666,666 hrs
        // Expected: 83.333 hrs == 10.42 days
        assignments = helper.makeSingleAssignment(2000000000);
        cal.set(2003, Calendar.OCTOBER, 6, 8, 0);
        work = new TimeQuantity(1666666666, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("10.4166666667"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // Issue: Long calculation combinations
        // When importing, it TEST_TASK_DURATION may be calculated prior to importing assignments
        // As a result, extremely long work values may be present.  Normally MSP limits
        // work / allocation % to a total of about 65 years (1984 - 2049) thus calculations
        // are reasonable.  However, prior to importing assignments, we use a default of 100%.
        // Thus, work might be as long as 1.67 billion hours but 100% assigned resulting in almost
        // 800,000 years of TEST_TASK_DURATION
        //
        // We expect an exception when (work hours / work factor) exceeds 578160 hours
        // Start date: Monday January 2nd @ 8:00 AM
        // Work factor: 1.00 (100%)
        // Work: 1,666,666,666 hrs
        assignments = helper.makeSingleAssignment(100);
        cal.set(1984, Calendar.JANUARY, 2, 8, 0);
        work = new TimeQuantity(1666666666, TimeQuantityUnit.HOUR);
        try {
            scheduleEntry = makeScheduleEntry(work, assignments);
            scheduleEntry.setStartTimeD(startDate);
            scheduleEntry.getDaysWorked(helper.getProvider());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Tests {@link ScheduleEntry#getDaysWorked(net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * for assignees in different time zones.
     */
    public void testGetDaysWorkedTimeZones() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TimeQuantity work;
        Date startDate;
        TimeZone timeZone1;
        TimeZone timeZone2;

        Collection assignments;
        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        ScheduleEntry scheduleEntry;

        timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
        timeZone2 = TimeZone.getTimeZone("Europe/London");

        //
        // 100% PST, 100% GMT
        //

        assignments = helper.makeTwoAssignments(100, timeZone1, 100, timeZone2);

        // StartDate: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("0.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM (4:00 PM GMT)
        // Work: 16 hours
        // Calendar 1 works 8 hours on Monday June 2nd
        // Calendar 2 overlaps 1 hour with calendar 1 and works 7 hours non-overlapped
        // Expected: 15 hours = 1.88 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("2.0000000000"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

        //
        // 25% PST, 35% GMT
        //

        assignments = helper.makeTwoAssignments(25, timeZone1, 35, timeZone2);

        // StartDate: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 16 hours
        //
        // Work Breakdown as follows:
        // Calendar1: 6.667 hours of work @ 25% = 26.668 actual hours
        // 6/2/03: 8-9, 9-10, 10-11, 11-12, 13-14, 14-15, 15-16, 16-17 (8 hours) Total:  8
        // 6/4/03: 8-9, 9-10, 10-11, 11-12, 13-14, 14-15, 15-16, 16-17 (8 hours)        16
        // 6/5/03: 8-9, 9-10, 10-11, 11-12, 13-14, 14-15, 15-16, 16-17 (8 hours)        24
        // 6/6/03: 8-9, 9-10, 10-10:40                                 (2.668 hours)    26.668
        //
        // Calendar2: 9.333 hours of work @ 35% = 26.666 actual hours
        // 6/2/03: 4-5 (8-9 PST)                                                      (1 hour)  Total: 1
        // 6/4/03:  8-9  (0-1 PST),  9-10 (1-2 PST), 10-11 (2-3 PST), 11-12 (3-4 PST),
        //         13-14 (5-6 PST), 14-15 (6-7 PST), 15-16 (7-8 PST), 16-17 (8-9 PST) (8 hours)        9
        // 6/5/03:  8-9  (0-1 PST),  9-10 (1-2 PST), 10-11 (2-3 PST), 11-12 (3-4 PST),
        //         13-14 (5-6 PST), 14-15 (6-7 PST), 15-16 (7-8 PST), 16-17 (8-9 PST) (8 hours)        17
        // 6/6/03:  8-9  (0-1 PST),  9-10 (1-2 PST), 10-11 (2-3 PST), 11-12 (3-4 PST),
        //         13-14 (5-6 PST), 14-15 (6-7 PST), 15-16 (7-8 PST), 16-17 (8-9 PST) (8 hours)        25
        // 6/7/03:  8-9  (0-1 PST), 9-9:40 (1-1:40 PST)                               (1.667 hours)    26.667
        //
        // Union of work hours:
        // 6/2/03: 8  hours   Total:  8
        // 6/4/03: 15 hours          23
        // 6/5/03: 15 hours          38
        // 6/6/03: 9 hours 40 mins   47h 40m
        // 6/7/03: 1 hour  40 mins   49h 20m  = 49.333 hours

        // Expected: 49.333 hours = 6.66 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        scheduleEntry = makeScheduleEntry(work, assignments);
        scheduleEntry.setStartTimeD(startDate);
        assertEquals(new BigDecimal("6.6666666667"), scheduleEntry.getDaysWorked(helper.getProvider()).getTotalDays());

    }

    public void testTaskSerialization() throws ClassNotFoundException {
        SetMethodWatcher watcher = new SetMethodWatcher(Class.forName("net.project.schedule.Task"));
        Task taskToSerialize = createTestTask(watcher);
        Task deserializedTask = null;

        try {
            //Serialize the object to the file system.
            FileOutputStream fos = new FileOutputStream("test.obj");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(taskToSerialize);

            //Now, reload the object from the file system.
            FileInputStream fis = new FileInputStream("test.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            deserializedTask = (Task)ois.readObject();


        } catch (IOException e) {
            fail("Unable to complete unit test due to IOException: " + e);
        } catch (ClassNotFoundException e) {
            fail("Unable to complete unit test due to ClassNotFoundException: " + e);
        }


        ensureEquals(deserializedTask);

    }

    public void testGetActualDuration() {
        Task task = new Task();

        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        task.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(6, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity("0.375", TimeQuantityUnit.DAY), task.getActualDuration());

        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        task.setWorkComplete(null);
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), task.getActualDuration());

        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), task.getActualDuration());

        task.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        task.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        task.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), task.getActualDuration());
    }

    //
    // Helper Methods
    //

    private static Date makeDateTime(String dateString) {
        return WorkingTimeCalendarDefinitionTest.makeDateTime(dateString);
    }

    /**
     * Makes a schedule entry with a single assignment.
     * @param assignment the assignment to add to the schedule entry
     * @return the schedule entry
     */
    private static ScheduleEntry makeScheduleEntry(ScheduleEntryAssignment assignment) {
        return makeScheduleEntry(new ScheduleEntryAssignment[]{assignment});
    }

    /**
     * Makes a schedule entry with two assignments.
     * @param assignment1 the first assginment
     * @param assignment2 the second assignment
     * @return the schedule entry
     */
    private static ScheduleEntry makeScheduleEntry(ScheduleEntryAssignment assignment1, ScheduleEntryAssignment assignment2) {
        return makeScheduleEntry(new ScheduleEntryAssignment[]{assignment1, assignment2});
    }

    /**
     * Makes a schedule entry with the specified assignments.
     * @param assignments the assignments to add to the schedule entry
     * @return the schedule entry
     */
    private static ScheduleEntry makeScheduleEntry(ScheduleEntryAssignment[] assignments) {
        ScheduleEntry task = new Task();
        task.addAssignments(Arrays.asList(assignments));
        return task;
    }

    /**
     * Makes a schedule entry with the specified work and assignments, distributing the work
     * amongst the assignments based on their percentages.
     * @param work the task work
     * @param assignments the assignments; must have percentage values set
     * @return the task with the specified assignments with work set appropriately
     */
    private static ScheduleEntry makeScheduleEntry(TimeQuantity work, Collection assignments) {
        return ScheduleEntryDateCalculatorTest.createTask(work, assignments);
    }

}
