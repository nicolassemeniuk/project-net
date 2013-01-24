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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.ObjectType;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * This class tests the cross-space functionality of TaskEndpointCalculation to
 * ensure that it works correctly.
 *
 * @author Matthew Flower
 * @since Version 8.0
 */
public class CSTaskEndpointCalculationTest extends TestCase {
    static TaskDependencyType FS = TaskDependencyType.FINISH_TO_START;
    static TaskConstraintType ASAP = TaskConstraintType.AS_SOON_AS_POSSIBLE;
    static TaskConstraintType ALAP = TaskConstraintType.AS_LATE_AS_POSSIBLE;
    static TaskConstraintType MSO = TaskConstraintType.MUST_START_ON;
    static TaskConstraintType SNET = TaskConstraintType.START_NO_EARLIER_THAN;
    static TimeQuantity TQ_0D = new TimeQuantity(0, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_1D = new TimeQuantity(1, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_2D = new TimeQuantity(2, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_3D = new TimeQuantity(3, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_5D = new TimeQuantity(5, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_10D = new TimeQuantity(10, TimeQuantityUnit.DAY);
    static TimeQuantity TQ_0H = new TimeQuantity(0, TimeQuantityUnit.HOUR);
    static TimeQuantity TQ_8H = new TimeQuantity(8, TimeQuantityUnit.HOUR);
    static TimeQuantity TQ_16H = new TimeQuantity(16, TimeQuantityUnit.HOUR);
    static TimeQuantity TQ_24H = new TimeQuantity(24, TimeQuantityUnit.HOUR);
    static TimeQuantity TQ_40H = new TimeQuantity(40, TimeQuantityUnit.HOUR);
    static TimeQuantity TQ_80H = new TimeQuantity(80, TimeQuantityUnit.HOUR);

    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public CSTaskEndpointCalculationTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     *
     * @param args a <code>String[]</code> value which contains the command line
     *             options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit
     * test believes it is comprised of.  In most cases, it is only the current
     * class, though it can include others.
     *
     * @return a <code>Test</code> object which is really a TestSuite of unit
     *         tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CSTaskEndpointCalculationTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }



    /**
     * Parses a date in the short format for US locale assuming PST/PDT
     * timezone.
     * @param dateString the date to parse
     * @return the parsed date or null if there was a problem parsing; the
     * date's time components are set to midnight
     */
    public static Date makeDate(String dateString) {
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Date date = null;
    
        try {
            //Calendar cal = new GregorianCalendar(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy K:mm a");
            sdf.setTimeZone(timeZone);
    
            date = sdf.parse(dateString);
    
        } catch (ParseException e) {
            date = null;
        } catch(NullPointerException npe) {
            date = null;
        }
    
        return date;
    }

    public static ScheduleEntry createTask(Schedule schedule, String name, String startDate, String endDate, TaskConstraintType constraint, TimeQuantity work, TimeQuantity duration) {
        ScheduleEntry se = new Task();
        se.setID(name);
        se.setName(name);
        se.setStartTimeD(makeDate(startDate));
        se.setEndTimeD(makeDate(endDate));
        se.setConstraintType(constraint);
        se.setWork(work);
        se.setDuration(duration);
        se.setPlanID(schedule.getID());

        PredecessorList pl = new PredecessorList();
        pl.setLoaded(true);
        se.setPredecessors(pl);

        SuccessorList sl = new SuccessorList();
        sl.setLoaded(true);
        se.setSuccessors(sl);

        schedule.taskList.add(se);

        return se;
    }

    private SummaryTask createSummaryTask(Schedule schedule, String name, String startDate, String endDate, TaskConstraintType constraint, TimeQuantity work, TimeQuantity duration) {
        SummaryTask st = new SummaryTask();
        st.setID(name);
        st.setName(name);
        st.setStartTimeD(makeDate(startDate));
        st.setEndTimeD(makeDate(endDate));
        st.setConstraintType(constraint);
        st.setWork(work);
        st.setDuration(duration);
        st.setPlanID(schedule.getID());

        PredecessorList pl = new PredecessorList();
        pl.setLoaded(true);
        st.setPredecessors(pl);

        SuccessorList sl = new SuccessorList();
        sl.setLoaded(true);
        st.setSuccessors(sl);

        st.setChildren(new HashSet());

        schedule.taskList.add(st);

        return st;
    }


    public static void linkTasks(ScheduleEntry predecessor, ScheduleEntry successor, TaskDependencyType type, TimeQuantity lag) {
        TaskDependency td = new TaskDependency(successor, predecessor, type, lag);
        predecessor.getSuccessorsNoLazyLoad().add(td);
        td = new TaskDependency(successor, predecessor, type, lag);
        successor.getPredecessorsNoLazyLoad().add(td);
    }

    /**
     * Make the provided task a child of the provided summary task.
     */
    public static void adoptChild(SummaryTask summaryTask, ScheduleEntry se) {
        summaryTask.getChildrenNoLazyLoad().add(se);
        se.setParentTaskID(summaryTask.getID());
    }

    public static void addAsSchedulePredecessor(ScheduleEntry predecessor, Schedule schedule, TaskDependencyType type, TimeQuantity lag) {
        TaskDependency td = new TaskDependency(schedule.getID(), predecessor, type, lag);
        schedule.getPredecessorList().add(td);
    }

    public static void setFromShare(ScheduleEntry entry, String sharingSpaceID, String spaceName, String objectID) {
        entry.setFromShare(true);
        entry.setSharingSpaceID(sharingSpaceID);
        entry.setSharingSpaceName(spaceName);
        entry.setSharedObjectID(objectID);
    }

    /**
     * Adds an assignment to the specified scheduleEntry.
     * All assignments are assigned to person with ID <code>1</code> and
     * the default time zone.
     * @param scheduleEntry the scheduleEntry to which to add the assignment
     * @param percentAssigned percentage assigned (out of 100) for the
     * assignment
     * @param work the amount of work that the assignment will perform
     */
    private void addAssignment(ScheduleEntry scheduleEntry, int percentAssigned, TimeQuantity work) {
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setTimeZone(TimeZone.getDefault());
        assignment.setPersonID("1");
        assignment.setPercentAssigned(percentAssigned);
        assignment.setWork(work);
        assignment.setObjectType(ObjectType.TASK);
        scheduleEntry.addAssignment(assignment);
    }



    public static Schedule createSchedule(String id, String scheduleName, TaskConstraintType startConstraintType, String startDate) {
        Schedule schedule = new Schedule();
        schedule.setID(id);
        schedule.setName(scheduleName);
        schedule.setScheduleStartDate(makeDate(startDate));
        schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));
        schedule.setStartConstraint(startConstraintType);
        schedule.setStartConstraintDate(makeDate(startDate));
        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        return schedule;
    }
    public static Set createNetwork(String[] scheduleNames) {
        Set network = new HashSet();
        for (int i = 0; i < scheduleNames.length; i++) {
            String scheduleName = scheduleNames[i];
            network.add(scheduleName);
        }

        return network;
    }

    public static Map createCache(Schedule[] schedules) {
        Map cache = new HashMap();

        for (int i = 0; i < schedules.length; i++) {
            Schedule schedule = schedules[i];
            cache.put(schedule.getID(), schedule);
        }

        return cache;
    }

    /**
     * This test is the most simple case of an cross project task being shared.
     * Make sure that dates are correct in both schedules afterwards.
     */
    public void testTaskBaseCase() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");

        //Create the tasks with the dates set incorrectly.  We want endpoint
        //calculation to fix them.
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/1/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        setFromShare(a1, "b", "b", "b1");

        //This is the space providing the schedule entry to a1
        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/5/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/1/05 8:00 AM", "1/1/05 8:00 AM", ASAP, TQ_8H, TQ_1D);

        //Create structures that would normally be loaded from the database.
        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set scheduleNetwork = createNetwork(new String[] { "a", "b" });

        //Do the endpoint calculation
        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(scheduleNetwork);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        //Check Work
        assertEquals(TQ_8H, a1.getWorkTQ());
        assertEquals(TQ_8H, b1.getWorkTQ());

        //Check Start Dates
        assertEquals(makeDate("1/5/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 8:00 AM"), scheduleB.getScheduleStartDate());

        //Check End dates
        assertEquals(makeDate("1/5/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleB.getScheduleEndDate());
    }

    /**
     * This method tests a normal case of task endpoint calculation with cross
     * space sharing.
     *
     *  A1A1--------+
     *              v
     *              AB2AB2
     *
     *         B1B1-+
     *              v
     *              B2B2B2
     *
     * @throws PersistenceException
     */
    public void testMiddleTaskCase() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/1/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry ab2 = createTask(scheduleA, "ab2", "1/3/05 8:00 AM", "1/1/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        setFromShare(ab2, "b", "b", "b2");
        linkTasks(a1, ab2, FS, TQ_0D);

        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/5/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/5/05 8:00 AM", "1/5/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/5/05 8:00 AM", "1/5/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, b2, FS, TQ_0D);

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set scheduleNetwork = createNetwork(new String[] {"a", "b"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(scheduleNetwork);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        //Check Start Dates
        assertEquals(makeDate("1/3/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), ab2.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 8:00 AM"), scheduleB.getScheduleStartDate());

        //Check End dates
        assertEquals(makeDate("1/3/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), ab2.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleB.getScheduleEndDate());
    }

    /**
     * This test attempts to see if "external pusher" behavior works correctly
     * with TaskEndpointCalculation.
     *
     * <h3>What is an external pusher?</h3>
     * Imagine these tasks:
     *
     * <pre>
     * A1A1A1-+
     *        v
     *        A2A2A2-+
     *               v
     *               A3A3A3
     *
     *               A4A4A4-+
     *                      v
     *                      AB1AB1-+
     *                             v
     *                             B2B2B2
     * </pre>
     *
     * Where A1, A2, A3, A4 are in schedule A and B1 is in Schedule B.
     * Furthermore, all tasks except A4 are ASAP tasks.  AB4 is an ALAP task.
     * There are FS dependencies too: A1->A2, A2->A3, A4->B1, B1->B2.  B1 appears
     * in both schedule A and Schedule B.
     *
     * The A4->B1 relationship means that A4 "pushes" B2.  Without using my lingo,
     * it means that you cannot determine the EST of B1 as you walk forward through
     * the task tree.  You MUST walk forward then backwards.  After that, you can
     * assign an EST for B1.
     *
     * This relationship exists in schedules that don't use cross-space
     * dependencies too.  Any knowledge I might have   
     * 
     * 
     * Michael Baranov: this is what really happens here (the above is somewhat confusing):
     * 
     *  Schedule A:
     *  a1 --> a2 --> a3
     *         a4 --> ab1
     *  Schedule B:
     *                b1 --> b2 
     *               
     * b1 is a 'link' to ab1
     * b1 is "pushed" forward by a4 (which is ALAP)
     * 
     */
    public void testExternalPusher() throws PersistenceException {
        //Create the "A" schedule and it's tasks.
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");

        //When we create the tasks, we incorrectly set the dates on purpose.  We
        //want to make sure that they will be reset the correct values.
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a2 = createTask(scheduleA, "a2", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        linkTasks(a1, a2, FS, TQ_0D);
        ScheduleEntry a3 = createTask(scheduleA, "a3", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        linkTasks(a2, a3, FS, TQ_0D);

        ScheduleEntry a4 = createTask(scheduleA, "a4", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry ab1 = createTask(scheduleA, "ab1", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);

        linkTasks(a4, ab1, FS, TQ_0D);

        //Create the "B" schedule and it's tasks.
        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");

        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, b2, FS, TQ_0D);

        setFromShare(b1, "a", "a", "ab1");
        //Create structures that would normally be loaded from the database.
        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set scheduleNetwork = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(scheduleNetwork);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        //Check work
        assertEquals(TQ_8H, a1.getWorkTQ());
        assertEquals(TQ_8H, a2.getWorkTQ());
        assertEquals(TQ_8H, a3.getWorkTQ());
        assertEquals(TQ_8H, a4.getWorkTQ());
        assertEquals(TQ_8H, b1.getWorkTQ());
        assertEquals(TQ_8H, b2.getWorkTQ());

        //Check start dates
        assertEquals(makeDate("1/3/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), a2.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), a3.getStartTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), a4.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), ab1.getStartTime());
        
        assertEquals(makeDate("1/5/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());

        //Check end dates
        assertEquals(makeDate("1/3/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a2.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), a3.getEndTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a4.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), ab1.getEndTime());
        
        assertEquals(makeDate("1/5/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleB.getScheduleEndDate());
    }

    /**
     * If the predecessor for a task is an invited task, make sure it will get
     * satisfied if the external task for the task has a predecessor too.
     */
    public void testInvitedPredecessor() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a2 = createTask(scheduleA, "a2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(a1, a2, FS, TQ_0D);
        setFromShare(a2, "b", "Schedule B", "b1");

        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/4/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, b2, FS, TQ_0D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB});
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        //Check start dates
        assertEquals(makeDate("1/3/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), a2.getStartTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), b2.getStartTime());

        //Check end dates
        assertEquals(makeDate("1/3/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a2.getEndTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), b2.getEndTime());
    }

    /**
     * Test that when a schedule is invited that it's beginning and end times
     * reflect the actual beginning and end times of the actual schedule.
     *
     * This is being done by setting its times incorrectly and letting TEC
     * recalculate them.  Specifically, we are simulating the lengthening of task
     * b1 from 8 hours of work to 16 hours of work (and 2 days of duration).
     *
     * Both schedule A and task ab should reflect these changes once we are done.
     */
    public void testInvitedSchedulePredecessorWorkChange() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.AS_SOON_AS_POSSIBLE, "1/4/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a2 = createTask(scheduleA, "a2", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(a1, a2, FS, TQ_0D);

        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/4/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB });
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        //Check start dates
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), a2.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), a2.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/6/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleB.getScheduleEndDate());
    }

    /**
     * Test that when the total work or total duration of a schedule is changed
     * that it is reflected in the "shared schedule tasks".
     *
     * In this tests we are setting up everything to reflect reality before task
     * a3 is created.  Then we are checking that everything gets updated as it
     * should to reflect the recalculation.
     */
    public void testInvitedScheduleTaskAdded() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.AS_SOON_AS_POSSIBLE, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a2 = createTask(scheduleA, "a2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a3 = createTask(scheduleA, "a3", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        //Set up schedule A's task dependencies
        linkTasks(a1, a2, FS, TQ_0D);
        linkTasks(a2, a3, FS, TQ_0D);

        //Set up schedule B
        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.AS_SOON_AS_POSSIBLE, "1/4/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        setFromShare(ab, "a", "Schedule A", "a");

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] {"a", "b"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        //Make sure the work and duration changed as we thought it would
        assertEquals(TQ_3D, ab.getDurationTQ());
        assertEquals(TQ_24H, ab.getWorkTQ());
    }

    /**
     * The purpose of this unit test is to test whether or not a shared schedule
     * that has no tasks inside of it will still work correctly if it has no
     * predecessors.
     */
    public void testEmptySharedScheduleNoPredecessors() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.AS_SOON_AS_POSSIBLE, "1/3/05 8:00 AM");

        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.MUST_START_ON, "1/3/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_0H, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] {"a", "b"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), ab.getEndTime());
    }

    /**
     * This test determines whether TaskEndpointCalculation can handle a change
     * in the start date of a schedule when the schedule has been shared into
     * another schedule.  Does it update itself correctly and does it update
     * any successor tasks in its external schedule.
     *
     * This test assumes that the start date of scheduleA was originally 1/3 and
     * was switched to 1/4.
     */
    public void testInvitedScheduleStartDateChanged() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", TaskConstraintType.MUST_START_ON, "1/4/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleB = createSchedule("b", "Schedule B", TaskConstraintType.AS_SOON_AS_POSSIBLE, "1/3/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(ab, b1, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB });
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        //Every date should have changed as the result of the change to the start date in scheduleA
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), b1.getEndTime());
    }

    /**
     * Make sure that if two shared schedules are included in a summary task and
     * there is a change to a predecessor to the whole lot that all the dates
     * will turn out correctly.  Here is a picture of what we are talking about:
     *
     * A
     * --
     * A1
     *
     * B
     * --
     * B1
     *
     * C
     * --
     * C1--+
     *     v
     *     C2SUMMARY------+
     *     ACAC-+         |
     *          v         |
     *          BCBC      |
     *                    v
     *                    C3C3
     */
    public void testSharedSchedulesInASummaryTask() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/4/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleB = createSchedule("b", "Schedule B", ASAP, "1/5/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleC = createSchedule("c", "Schedule C", ASAP, "1/3/05 8:00 AM");
        //This is the changed schedule
        ScheduleEntry c1 = createTask(scheduleC, "c1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", SNET, TQ_8H, TQ_1D);
        c1.setConstraintDate(makeDate("1/4/05 8:00 AM"));  //This is the big change

        SummaryTask c2 = createSummaryTask(scheduleC, "c2", "1/4/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        linkTasks(c1, c2, FS, TQ_0D);
        addAsSchedulePredecessor(c1, scheduleA, FS, TQ_0D);

        ScheduleEntry ac = createTask(scheduleC, "ac", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry bc = createTask(scheduleC, "bc", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(ac, bc, FS, TQ_0D);
        addAsSchedulePredecessor(ac, scheduleB, FS, TQ_0D);

        setFromShare(ac, "a", "Schedule A", "a");
        setFromShare(bc, "b", "Schedule B", "b");
        adoptChild(c2, ac);
        adoptChild(c2, bc);

        ScheduleEntry c3 = createTask(scheduleC, "c3", "1/7/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(c2, c3, FS, TQ_1D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB, scheduleC });
        Set network = createNetwork(new String[] { "a", "b", "c" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/4/05 8:00 AM"), c1.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), c1.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), c2.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), c2.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), ac.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), ac.getEndTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), bc.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), bc.getEndTime());
        //C3 starts on Monday because of lag
        assertEquals(makeDate("1/10/05 8:00 AM"), c3.getStartTime());
        assertEquals(makeDate("1/10/05 5:00 PM"), c3.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/6/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleC.getScheduleStartDate());
        assertEquals(makeDate("1/10/05 5:00 PM"), scheduleC.getScheduleEndDate());

    }
    /**
     * This is effectively the same task as testSharedSchedulesInASummaryTask,
     * except this version lacks the link between c1 and c2.  This originally
     * came up as a problem during testing because I had forgotten to link the
     * two tasks -- after thinking about it briefly, there was no reason that
     * it shouldn't be working too.
     */
    public void testSharedSchedulesInASummaryTask2() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/4/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleB = createSchedule("b", "Schedule B", ASAP, "1/5/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleC = createSchedule("c", "Schedule C", ASAP, "1/3/05 8:00 AM");
        //This is the changed schedule
        ScheduleEntry c1 = createTask(scheduleC, "c1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", SNET, TQ_8H, TQ_1D);
        c1.setConstraintDate(makeDate("1/4/05 8:00 AM"));  //This is the big change

        SummaryTask c2 = createSummaryTask(scheduleC, "c2", "1/4/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        ScheduleEntry ac = createTask(scheduleC, "ac", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry bc = createTask(scheduleC, "bc", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(ac, bc, FS, TQ_0D);
        addAsSchedulePredecessor(ac, scheduleB, FS, TQ_0D);

        setFromShare(ac, "a", "Schedule A", "a");
        setFromShare(bc, "b", "Schedule B", "b");
        adoptChild(c2, ac);
        adoptChild(c2, bc);

        ScheduleEntry c3 = createTask(scheduleC, "c3", "1/7/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(c2, c3, FS, TQ_1D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB, scheduleC });
        Set network = createNetwork(new String[] { "a", "b", "c" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/4/05 8:00 AM"), c1.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), c1.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), c2.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), c2.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), ac.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), ac.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), bc.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), bc.getEndTime());
        //C3 starts on Monday because of lag
        assertEquals(makeDate("1/7/05 8:00 AM"), c3.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), c3.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/5/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleC.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 5:00 PM"), scheduleC.getScheduleEndDate());

    }

    /**
     * This test sets the TaskConstraintType of a schedule to "Must start on"
     * and checks that the shared schedule is marked with a warning that its
     * values are no longer correct because its shared predecessors should be
     * pressing it forward.
     */
    public void testSharedScheduleConstrainedStartOverflow() {
    }

    /**
     * Make sure that Shared Schedules that span a weekend have the correct
     * durations shown.
     */
    public void testSharedScheduleOverWeekend() {
    }

    public void testAllPredecessorsOfASharedScheduleBeingChecked() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/4/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a2 = createTask(scheduleA, "a2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry a3 = createTask(scheduleA, "a3", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(a2, a3, FS, TQ_0D);

        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/4/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_3D, TQ_2D);
        setFromShare(ab, "a", "Schedule A", "a");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/5/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(ab, b1, FS, TQ_0D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB });
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleA);

        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/5/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/6/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), a2.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), a2.getEndTime());
        assertEquals(makeDate("1/5/05 8:00 AM"), a3.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), a3.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/5/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), b1.getEndTime());
    }

    /**
     * Check if a ScheduleEntry has a "Pusher" that the final dates turn out
     * correct for the shared schedule.
     * 
     * Michael Baranov:
     * ab is a 'link' to a (schedule A)
     * a1 is 'pushed' by b1, which is ALAP 
     */
    public void testSharedSchedulePusher() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a"); 
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/3/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_40H, TQ_5D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB });
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/7/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/7/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), a1.getEndTime());        
        assertEquals(makeDate("1/7/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 5:00 PM"), scheduleA.getScheduleEndDate());

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/6/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), b2.getEndTime());
    }

    /**
     * A schedule being shared inside of a shared schedule that is shared to a
     * third schedule.  The schedule is being pushed in all three places.
     *
     * This confirms that shared schedules work more than just in the "top level"
     * of tasks being processed.
     */
    public void testDoubleSharedSchedulePusher() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_16H, TQ_2D);

        Schedule scheduleB = createSchedule("b", "Schedule B", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/3/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_40H, TQ_5D);

        Schedule scheduleC = createSchedule("c", "Schedule C", MSO, "1/3/05 8:00 AM");
        ScheduleEntry c1 = createTask(scheduleC, "c1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry bc = createTask(scheduleC, "bc", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry c2 = createTask(scheduleC, "c2", "1/3/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_80H, TQ_10D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB, scheduleC});
        Set network = createNetwork(new String[] {"a", "b", "c"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleC);

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleC.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 8:00 AM"), c1.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), c1.getEndTime());
        assertEquals(makeDate("1/10/05 8:00 AM"), bc.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), bc.getEndTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), c2.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), c2.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleC.getScheduleEndDate());

        assertEquals(makeDate("1/10/05"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/12/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/12/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/13/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/10/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleB.getScheduleEndDate());

        assertEquals(makeDate("1/13/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/13/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleA.getScheduleEndDate());
    }


    /**
     * This is the same test as {@link #testDoubleSharedSchedulePusher} except
     * that it ensures that all the dates are calculated correctly if the middle
     * schedule is the one being recalculated.
     */
    public void testDoubleSharedSchedulePusherRecalcMiddle() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_16H, TQ_2D);

        Schedule scheduleB = createSchedule("b", "Schedule B", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/3/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_40H, TQ_5D);

        Schedule scheduleC = createSchedule("c", "Schedule C", MSO, "1/3/05 8:00 AM");
        ScheduleEntry c1 = createTask(scheduleC, "c1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry bc = createTask(scheduleC, "bc", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry c2 = createTask(scheduleC, "c2", "1/3/05 8:00 AM", "1/5/05 5:00 PM", ASAP, TQ_80H, TQ_10D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB, scheduleC});
        Set network = createNetwork(new String[] {"a", "b", "c"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleC.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 8:00 AM"), c1.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), c1.getEndTime());
        assertEquals(makeDate("1/10/05 8:00 AM"), bc.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), bc.getEndTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), c2.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), c2.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleC.getScheduleEndDate());

        assertEquals(makeDate("1/10/05"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/12/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/12/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/13/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/10/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleB.getScheduleEndDate());

        assertEquals(makeDate("1/13/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/13/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/14/05 5:00 PM"), scheduleA.getScheduleEndDate());
    }

    /**
     * There is a summary task which has a regular ALAP task and a shared
     * schedule as children.  This test ensures that the handling is correct for
     * all of the child tasks and for the summary task.
     */
    public void testSummaryTaskInternalSharedSchedulePusher() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, "1/3/05 8:00 AM");
        ScheduleEntry a1 = createTask(scheduleA, "a1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");
        SummaryTask summaryTaskB1 = createSummaryTask(scheduleB, "Summary Task B1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_16H, TQ_2D);
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ALAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        adoptChild(summaryTaskB1, b1);
        adoptChild(summaryTaskB1, ab);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/3/05 8:00 AM", "1/7/05 5:00 PM", ASAP, TQ_40H, TQ_5D);

        Map cache = createCache(new Schedule[] { scheduleA, scheduleB });
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/6/05 8:00 AM"), summaryTaskB1.getStartTime());
        assertEquals(makeDate("1/6/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/6/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/7/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), summaryTaskB1.getEndTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), scheduleB.getScheduleEndDate());

        assertEquals(makeDate("1/7/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/7/05 8:00 AM"), a1.getStartTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), a1.getEndTime());
        assertEquals(makeDate("1/7/05 5:00 PM"), scheduleA.getScheduleEndDate());
    }


    /**
     * Do a task endpoint calculation when you have an empty schedule set as
     * MSO with no start date.
     */
    public void testEmptySNoTasks() {
        Schedule scheduleA = createSchedule("a", "Schedule A", MSO, null);
        Map cache = createCache(new Schedule[] {scheduleA});
        Set network = createNetwork(new String[] {"a"});

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        try {
            tec.recalculateTaskTimesNoLoad(scheduleA);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown while running testEmptySNoTasks");
        }
    }

    public void testEmptySImportedIntoAnotherS() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", MSO, null);
        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/4/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/4/05 8:00 AM", "1/4/05 8:00 AM", MSO, TQ_0D, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/4/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), ab.getEndTime());
    }

    public void testEmptySStartOfChain() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", MSO, null);
        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/3/05 8:00 AM", "1/3/05 8:00 AM", ASAP, TQ_0D, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(ab, b1, FS, TQ_0D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        linkTasks(b1, b2, FS, TQ_0D);

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), ab.getStartTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), ab.getEndTime());
        assertEquals(makeDate("1/3/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/3/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), b2.getEndTime());
    }

    public void testEmptySMiddleOfChain() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, null);
        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/4/05 8:00 AM", "1/4/05 8:00 AM", ASAP, TQ_0D, TQ_0D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);

        setFromShare(ab, "a", "Schedule A", "a");
        linkTasks(b1, ab, FS, TQ_0D);
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        linkTasks(ab, b2, FS, TQ_0D);

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/3/05 5:00 PM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/3/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/3/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/3/05 5:00 PM"), ab.getStartTime());
        assertEquals(makeDate("1/3/05 5:00 PM"), ab.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), b2.getEndTime());
    }

    public void testEmptySEndOfChain() throws PersistenceException {
        Schedule scheduleA = createSchedule("a", "Schedule A", ASAP, null);
        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");
        ScheduleEntry b1 = createTask(scheduleB, "b1", "1/3/05 8:00 AM", "1/3/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry b2 = createTask(scheduleB, "b2", "1/4/05 8:00 AM", "1/4/05 5:00 PM", ASAP, TQ_8H, TQ_1D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/5/05 8:00 AM", "1/5/05 8:00 AM", ASAP, TQ_0D, TQ_0D);

        linkTasks(b1, b2, FS, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        addAsSchedulePredecessor(b1, scheduleA, FS, TQ_0D);
        linkTasks(b2, ab, FS, TQ_0D);

        Map cache = createCache(new Schedule[] {scheduleA, scheduleB});
        Set network = createNetwork(new String[] { "a", "b" });

        TaskEndpointCalculation tec = new TaskEndpointCalculation();
        tec.setCache(cache);
        tec.setScheduleNetwork(network);
        tec.recalculateTaskTimesNoLoad(scheduleB);

        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleA.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleA.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), scheduleB.getScheduleStartDate());
        assertEquals(makeDate("1/4/05 5:00 PM"), scheduleB.getScheduleEndDate());
        assertEquals(makeDate("1/3/05 8:00 AM"), b1.getStartTime());
        assertEquals(makeDate("1/3/05 5:00 PM"), b1.getEndTime());
        assertEquals(makeDate("1/4/05 8:00 AM"), b2.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), b2.getEndTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), ab.getStartTime());
        assertEquals(makeDate("1/4/05 5:00 PM"), ab.getEndTime());
    }

    public void testEmptySSummaryTaskChild() {
        Schedule scheduleA = createSchedule("a", "Schedule A", MSO, "1/4/05 8:00 AM");
        Schedule scheduleB = createSchedule("b", "Schedule B", MSO, "1/3/05 8:00 AM");

        SummaryTask b1 = createSummaryTask(scheduleB, "b1", "1/4/05 8:00 AM", "1/4/05 8:00 AM", ASAP, TQ_0H, TQ_0D);
        ScheduleEntry ab = createTask(scheduleB, "ab", "1/4/05 8:00 AM", "1/4/05 8:00 AM", MSO, TQ_0H, TQ_0D);
        setFromShare(ab, "a", "Schedule A", "a");
        adoptChild(b1, ab);
    }
}
