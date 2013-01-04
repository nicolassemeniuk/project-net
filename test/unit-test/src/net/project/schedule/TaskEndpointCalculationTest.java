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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.ObjectType;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.ServiceFactoryImpl;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.test.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.util.*;

/**
 * This unit test constructs a variety of combinations of tasks, dependencies,
 * and constraints.  It then tests to see if the start and end date of each
 * task is correct.
 * <p/>
 * This set of tests is rather large.  For that reason, as cross project
 * functionality is added, I am adding the additional tests in a different test
 * class.
 *
 * @author Matthew Flower
 * @see net.project.schedule.CSTaskEndpointCalculationTest
 * @since Version 7.6
 */
public class TaskEndpointCalculationTest extends TestCase {
    private GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public TaskEndpointCalculationTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        Application.login();
        ServiceFactory.init(ServiceFactoryImpl.class);
        ServiceFactory.getInstance().setBeanFactory(new XmlBeanFactory(new ClassPathResource("test-bussinessContext.xml")));
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

    public static Test suite() {
        TestSuite suite = new TestSuite(TaskEndpointCalculationTest.class);
        return suite;
    }

    class TestObjects {
        SummaryTask summaryTask = new SummaryTask();
        SummaryTask summaryTask2 = new SummaryTask();
        ScheduleEntry task1 = new Task();
        ScheduleEntry task2 = new Task();
        ScheduleEntry task3 = new Task();
        ScheduleEntry task4 = new Task();
        Schedule schedule = new Schedule();
        TaskEndpointCalculation calc = new TaskEndpointCalculation();
        private Map scheduleEntries = new HashMap();

        /**
         * Initializes tasks with the specified schedule start date.
         * All task work is 8 hours.
         *
         * @param scheduleStartDate the start date for the schedule
         */
        public TestObjects(Date scheduleStartDate) {
            this(scheduleStartDate, new TimeQuantity(8, TimeQuantityUnit.HOUR),
                    new TimeQuantity(8, TimeQuantityUnit.HOUR));
        }

        /**
         * Initializes tasks with the specified schedule start date, customizing
         * the work for 2 tasks (and the duration).
         * <p/>
         * The duration for those two tasks is based on the work and a "default" working time
         * calendar.  For example, 16 hours of work will set the task duration to 2 days. <br/>
         * All remaining task work defaults to 8 hours and duration to 1 day.
         *
         * @param scheduleStartDate the start date for the schedule
         * @param t1Work            the work value for <code>task1</code>
         * @param t2Work            the work value for <code>task2</code>
         */
        public TestObjects(Date scheduleStartDate, TimeQuantity t1Work, TimeQuantity t2Work) {

            // Previously, calculating a date on a task with no assignments was based on
            // work alone.  Now it is based on duration only.  To maintain test compatibility
            // we'll default task duration to an amount equivalent to task work.
            TimeQuantity t1Duration = ScheduleTimeQuantity.convertToUnit(t1Work, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP);
            TimeQuantity t2Duration = ScheduleTimeQuantity.convertToUnit(t2Work, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP);

            task1.setID("1");
            PredecessorList pl1 = new PredecessorList();
            pl1.setLoaded(true);
            task1.setPredecessors(pl1);
            SuccessorList sl1 = new SuccessorList();
            sl1.setLoaded(true);
            task1.setSuccessors(sl1);
            task1.setWork(t1Work);
            task1.setDuration(t1Duration);

            task2.setID("2");
            PredecessorList pl2 = new PredecessorList();
            pl2.setLoaded(true);
            task2.setPredecessors(pl2);
            SuccessorList sl2 = new SuccessorList();
            sl2.setLoaded(true);
            task2.setSuccessors(sl2);
            task2.setWork(t2Work);
            task2.setDuration(t2Duration);

            task3.setID("3");
            PredecessorList pl3 = new PredecessorList();
            pl3.setLoaded(true);
            task3.setPredecessors(pl3);
            SuccessorList sl3 = new SuccessorList();
            sl3.setLoaded(true);
            task3.setSuccessors(sl3);
            task3.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
            task3.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));

            task4.setID("4");
            PredecessorList pl4 = new PredecessorList();
            pl4.setLoaded(true);
            task4.setPredecessors(pl4);
            SuccessorList sl4 = new SuccessorList();
            sl4.setLoaded(true);
            task4.setSuccessors(sl4);
            task4.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
            task4.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));

            summaryTask.setID("10");
            summaryTask.setChildren(new LinkedHashSet());
            PredecessorList pl10 = new PredecessorList();
            pl10.setLoaded(true);
            summaryTask.setPredecessors(pl10);
            SuccessorList sl10 = new SuccessorList();
            sl10.setLoaded(true);
            summaryTask.setSuccessors(sl10);
            summaryTask.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));

            summaryTask2.setID("20");
            summaryTask2.setChildren(new LinkedHashSet());
            PredecessorList pl20 = new PredecessorList();
            pl20.setLoaded(true);
            summaryTask2.setPredecessors(pl20);
            SuccessorList sl20 = new SuccessorList();
            sl20.setLoaded(true);
            summaryTask2.setSuccessors(sl20);
            summaryTask2.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));

            // Prepare the schedule
            schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            schedule.setScheduleStartDate(scheduleStartDate);
            schedule.setAutocalculateTaskEndpoints(true);
            // We use a custom working time calendar provider that always
            // returns the default working time calendar
            schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

            scheduleEntries.put("1", task1);
            scheduleEntries.put("2", task2);
            scheduleEntries.put("3", task3);
            scheduleEntries.put("4", task4);
            scheduleEntries.put("10", summaryTask);
            scheduleEntries.put("20", summaryTask2);
            schedule.setEntryMap(scheduleEntries);

            schedule.setID("666");
            task1.setPlanID("666");
            task2.setPlanID("666");
            task3.setPlanID("666");
            task4.setPlanID("666");
            summaryTask.setPlanID("666");
            summaryTask2.setPlanID("666");
        }

        public void addSuccessor(ScheduleEntry t1, ScheduleEntry t2, TaskDependencyType tdt, TimeQuantity lag) throws PersistenceException {

            TaskDependency td = new TaskDependency(t2, t1, tdt, lag);
            t1.getSuccessors().add(td);
            ScheduleEntry successorTask = (ScheduleEntry) schedule.getEntryMap().get(td.getTaskID());
            successorTask.getDependenciesNoLazyLoad().add(td);
        }

        /**
         * Adds a finish to start relationship between t1 and t2 such that
         * t2 is a successor of t1.
         *
         * @param t1 the schedule entry to become the predecessor of t2
         * @param t2 the schedule entry to become the sucessor of t1
         * @throws PersistenceException if there is a problem adding the successor
         */
        public void addSuccessor(ScheduleEntry t1, ScheduleEntry t2) throws PersistenceException {
            addSuccessor(t1, t2, TaskDependencyType.FINISH_TO_START, new TimeQuantity(0, TimeQuantityUnit.DAY));
        }

        public void addSuccessor(ScheduleEntry t1, ScheduleEntry t2, TimeQuantity lag) throws PersistenceException {
            addSuccessor(t1, t2, TaskDependencyType.FINISH_TO_START, lag);
        }

        public void addSuccessor(ScheduleEntry t1, ScheduleEntry t2, TaskDependencyType tdt) throws PersistenceException {
            addSuccessor(t1, t2, tdt, new TimeQuantity(0, TimeQuantityUnit.DAY));
        }

        public void setConstraint(ScheduleEntry task, TaskConstraintType type, Date date) {
            task.setConstraintType(type);
            task.setConstraintDate(date);
        }

        /**
         * Adds an assignment to the specified scheduleEntry.
         * All assignments are assigned to person with ID <code>1</code> and
         * the default time zone.
         *
         * @param scheduleEntry   the scheduleEntry to which to add the assignment
         * @param percentAssigned percentage assigned (out of 100) for the
         *                        assignment
         * @param work            the amount of work that the assignment will perform
         */
        private ScheduleEntryAssignment addAssignment(ScheduleEntry scheduleEntry, int percentAssigned, TimeQuantity work) {
            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            assignment.setPersonID("1");
            assignment.setPercentAssigned(percentAssigned);
            assignment.setWork(work);
            assignment.setObjectType(ObjectType.TASK);
            scheduleEntry.addAssignment(assignment);

            return assignment;
        }

        /**
         * Recalculates all task times.
         * Empty summary tasks are not recalculated.
         */
        public void runCalculation() {
            try {
                //Remove summary tasks that aren't set up with subtasks
                if (summaryTask.getChildren().isEmpty()) {
                    scheduleEntries.remove("10");
                }
                if (summaryTask2.getChildren().isEmpty()) {
                    scheduleEntries.remove("20");
                }
                schedule.setEntryMap(scheduleEntries);

                calc.recalculateTaskTimesNoLoad(schedule);
            } catch (PersistenceException e) {
                e.printStackTrace();
                fail("Unable to recalculate task times, an unexpected persistence" +
                        "error occurred.  " + e.getMessage());
            }
        }

        /**
         * Makes the specified schedule entry a child of <code>summaryTask</code>.
         *
         * @param se the schedule entry to add to the summary task
         */
        public void adopt(ScheduleEntry se) {
            adopt(summaryTask, se);
        }

        /**
         * Makes the specified schedule entry a child of the specified summary
         * task.
         *
         * @param summaryTask the summary task to which to add the schedule entr
         * @param se          the schedule entry to add to the summary task
         */
        public void adopt(SummaryTask summaryTask, ScheduleEntry se) {
            summaryTask.getChildrenNoLazyLoad().add(se);
            se.setParentTaskID(summaryTask.getID());
        }
    }

    public void testSimpleFSRelationship() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(to.task1.getStartTime(), cal.getTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(to.task1.getEndTime(), cal.getTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(to.task2.getStartTime(), cal.getTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(to.task2.getEndTime(), cal.getTime());

        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
    }

    public void testFSRelationshipWithPositiveLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2, new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
    }

    public void testFSRelationshipWithNegativeLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2, new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSimpleFSRelationshipSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2, new TimeQuantity(0, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSRelationshipWithPositiveLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);  //Fri Jan 10, 2003
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2, new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
    }

    public void testFSRelationshipWithNegativeLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);  //Jan 6, 2003 (Definitely not a holiday)
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2, new TimeQuantity(-2, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithASAPConstraint() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task3);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());
    }

    public void testFSWithALAPConstraint() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.task2.setConstraintType(TaskConstraintType.AS_LATE_AS_POSSIBLE);
        to.addSuccessor(to.task1, to.task3);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());
    }

    public void testFSWithFNETConstraintTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Jan 6, 2003 (Definitely not a holiday)
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.task1.setConstraintType(TaskConstraintType.FINISH_NO_EARLIER_THAN);
        cal.set(2003, Calendar.JANUARY, 7, 17, 00, 00);
        to.task1.setConstraintDate(cal.getTime());

        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(to.task1.getStartTime(), cal.getTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(to.task1.getEndTime(), cal.getTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(to.task2.getStartTime(), cal.getTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(to.task2.getEndTime(), cal.getTime());
    }

    public void testFSWithFNETConstraintTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.task2.setConstraintType(TaskConstraintType.FINISH_NO_EARLIER_THAN);
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.task2.setConstraintDate(cal.getTime());

        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithFNETConstraintTest3() throws PersistenceException {
        // Task 1: 3 days, 24 hours
        // Task 2: 1 day, 8 hours
        // Finish to Start Task 1 --> Task 2
        // Task 2 Finish No Earlier Than Constraint of 1/8/03
        // Task 2 Finishes later because of task 1 work

        TimeQuantity t1Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.task2.setConstraintType(TaskConstraintType.FINISH_NO_EARLIER_THAN);
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.task2.setConstraintDate(cal.getTime());

        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        // Start: 1/6/03 @ 8:00 AM
        // End: 1/8/03 @ 5:00 PM
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        // Start: 1/9/03 @ 8:00 AM
        // End: 1/9/03 @ 5:00 PM
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithFNLTConstraintTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task1, to.task3);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFSWithFNLTConstraintTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task1, to.task3);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFSWithMFOConstraintTest() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithMSOConstraintTest() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithSNETConstraintTest() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFSWithSNLTConstraintTest() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_LATER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_START_ON, cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFSWithSNLTConstraintTest2() throws PersistenceException {
        // Task 1
        // Start: Monday January 6th 2003 @ 8:00 AM
        // Work: 5 days
        // End: Friday January 10th 2003 @ 5:00 PM
        // Finish to Start dependency from Task 1 to Task 2
        // Task 2
        // Constraint: Start No Later Than Saturday January 11th 2003 @ 12:00 PM
        // Expected End: Monday January 13th @ 5:00 PM
        // Task 2 starts on its constraint date even though it is a non working day
        TimeQuantity t1Work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 11, 12, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_LATER_THAN, cal.getTime());

        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 11, 12, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

    }

    public void testFSWithPusher() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        //Check all of the task times
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        assertFalse(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());
    }

    public void testFSPushingMultipleTasks() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3);
        to.addSuccessor(to.task3, to.task4);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task4, TaskConstraintType.MUST_START_ON, cal.getTime());

        to.runCalculation();

        //Check all of the task times
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task4.getLatestStartDate());
        assertEquals(cal.getTime(), to.task4.getStartTime());

        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task4.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task4.getEndTime());

        assertFalse(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());
        assertTrue(to.task4.isCriticalPath());
    }

    public void testSimpleSSRelationship() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithPositiveLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START,
                new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithNegativeLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSPositiveLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START,
                new TimeQuantity(5, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSSWithASAPConstraint() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);
        to.addSuccessor(to.task1, to.task3);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the critical path settings
        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithALAPConstraint() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);
        to.addSuccessor(to.task1, to.task3);
        to.addSuccessor(to.task2, to.task3);
        to.setConstraint(to.task2, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithFNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task1, to.task3, TaskDependencyType.START_TO_START);
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        //Check the third task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithFNLT() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);
        to.addSuccessor(to.task1, to.task3);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithMFO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSSWithMSO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSSWithSNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);

        to.runCalculation();

        //Check the first task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        //Check the second task to ensure the times are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSSWithSNLT() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_LATER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithMultipleLags() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START,
                new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSSWithNestedTwoSidedDependency() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);  //Mon Jan 6, 2003
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START,
                new TimeQuantity(1, TimeQuantityUnit.DAY));
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_START,
                new TimeQuantity(0, TimeQuantityUnit.DAY));
        to.addSuccessor(to.task1, to.task4);
        to.addSuccessor(to.task3, to.task4);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task4.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task4.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task4.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEndTime());
    }

    public void testSimpleSFRelationship() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH,
                new TimeQuantity(0, TimeQuantityUnit.DAY));

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());
    }

    public void testSFWithPositiveLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH,
                new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFWithNegativeLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6	, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFPositiveLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH,
                new TimeQuantity(6, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFNegativeLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH,
                new TimeQuantity(-2, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFWithASAP() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);
        to.addSuccessor(to.task1, to.task3);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSFWithALAP() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.setConstraint(to.task2, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);
        to.addSuccessor(to.task1, to.task3);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSFWithFNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);
        to.addSuccessor(to.task1, to.task3, TaskDependencyType.FINISH_TO_START);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSFWithFNLT() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.setConstraint(to.task2, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSFWithMFO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFWithMSO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFWithSNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSFWithSNLT() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.setConstraint(to.task2, TaskConstraintType.START_NO_LATER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.START_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testSimpleFFRelationship() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(0, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithPositiveLag() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_SOON_AS_POSSIBLE, null);
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithNegativeLagTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithNegativeLagTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_SOON_AS_POSSIBLE, null);
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(-1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithPositiveLagSpanningWeekend() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH,
                new TimeQuantity(1, TimeQuantityUnit.DAY));

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 13, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 13, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithASAP() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task3, TaskDependencyType.FINISH_TO_START);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.FINISH_TO_FINISH);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_START_ON, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFFWithALAP() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task3, TaskDependencyType.FINISH_TO_START);
        to.setConstraint(to.task2, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.FINISH_TO_FINISH);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_START_ON, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFFWithFNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH);
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithFNLT() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH);
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testFFWithMSO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task3, TaskDependencyType.FINISH_TO_FINISH);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.FINISH_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFFWithMFO() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task2, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_START);
        to.addSuccessor(to.task2, to.task3, TaskDependencyType.FINISH_TO_FINISH);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testFFWithSNET() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_START_ON, cal.getTime());

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    public void testZeroWorkTasksTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());

        assertEquals(to.task1.getStartTime(), to.task1.getEndTime());
        assertEquals(to.task2.getStartTime(), to.task2.getEndTime());

        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testZeroWorkTasksTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEndTime());
    }

    /**
     * Tests Finish-to-Start dependency type with lag with resource assignments.
     *
     * @throws PersistenceException
     */
    public void testFSWithLagConstrainedResource() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.MAY, 14, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_START, new TimeQuantity(2, TimeQuantityUnit.DAY));
        to.addAssignment(to.task2, 25, t2Work);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.MAY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.MAY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.MAY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.MAY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.MAY, 19, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.MAY, 22, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.MAY, 19, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.MAY, 22, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.MAY, 14, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 14, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.MAY, 19, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.MAY, 22, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithSingleRTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 100, t1Work);
        to.addAssignment(to.task2, 100, t2Work);
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

    }

    public void testFSWithSingleRTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 200, t1Work);
        to.addAssignment(to.task2, 200, t2Work);
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 12, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 12, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 13, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 13, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 12, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 6, 13, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(0.5, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(0.5, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

    }

    public void testFSWithSingleRTest3() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 50, t1Work);
        to.addAssignment(to.task2, 50, t2Work);
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

    }

    public void testFSWithTwoRTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithTwoRTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithTwoRTest3() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithMultiRTest1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithMultiRTest2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testFSWithMultiRTest3() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addAssignment(to.task1, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 200, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        //Check the late start and late finish times to ensure they are right
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        //Check the task start and finish times to ensure they are correct
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());

        to.task1.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        to.task2.calculateDuration(to.schedule.getWorkingTimeCalendarProvider());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testSimpleMustStartOnConstraint() throws PersistenceException {
        // Schedule start 5/1/03
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.MAY, 1, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        // Must Start on Monday May 19th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 19, 8, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.runCalculation();
        // Expected Start Monday May 19th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 19, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        // Expected finish Tuesday May 20th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 20, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        // Expected Duration 2 days
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // Must Start on Friday May 23rd @ 8:00 AM
        cal.set(2003, Calendar.MAY, 23, 8, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.runCalculation();
        // Expected Start Friday May 23rd @ 8:00 AM
        cal.set(2003, Calendar.MAY, 23, 8, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        // Expected finish Monday May 26th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 26, 17, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        // Expected Duration 2 days
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

        // MSO Constraint: Saturday May 24th @ 8:00 AM
        // Expected Start: Saturday May 24th @ 8:00 AM
        // Expected Finish: Tuesday May 27th @ 5:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 24, 8, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 24, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 27, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // MSO Constraint: Sunday May 25th @ 8:00 AM
        // Duration: 2 days
        // Expected Start: Sunday May 25th @ 8:00 AM
        // Expected Finish: Tuesday May 27th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 25, 8, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_START_ON, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 25, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 27, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

    }

    public void testSimpleMustFinishOnConstraint() throws PersistenceException {
        // Schedule start 5/1/03
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.MAY, 1, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        // Must Finish on Friday May 23rd @ 5:00 PM
        cal.set(2003, Calendar.MAY, 23, 17, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.runCalculation();
        // Expected Start Thursday May 22nd @ 8:00 AM
        cal.set(2003, Calendar.MAY, 22, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        // Expected Finish Friday May 23rd @ 5:00 PM
        cal.set(2003, Calendar.MAY, 23, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        // Expected Duration 2 days
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // Must Finish on Monday May 26th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 26, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.runCalculation();
        // Expected start Friday May 23rd @ 8:00 AM
        cal.set(2003, Calendar.MAY, 23, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        // Expected Finish Monday May 26th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 26, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        // Expected Duration 2 days
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

        // MFO Constraint: Saturday May 24th @ 5:00 PM
        // Expected Start: Thursday May 22nd @ 8:00 AM
        // Expected Finish: Saturday May 24th @ 5:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 24, 17, 0);
        to.setConstraint(to.task1, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 22, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 24, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // MFO Constraint: Sunday May 25th @ 5:00 PM
        // Duration: 2 days
        // Expected Start: Thursday May 22nd @ 8:00 AM
        // Expected Finish: Sunday May 25th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 25, 17, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 22, 8, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.MAY, 25, 17, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());

    }

    /**
     * Tests calculation with Finish No Earlier Than Constraint.
     *
     * @throws PersistenceException
     */
    public void testSimpleFinishNoEarlierConstraint() throws PersistenceException {

        // Schedule start 5/1/03
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.MAY, 1, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        // FNE Constraint: Monday May 26th @ 5:00 PM
        // Expected Start: Friday May 23rd @ 8:00 AM
        // Expected Finish: Monday May 26th @ 5:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 26, 17, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 23, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 26, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // FNE Constraint: Friday May 23rd @ 5:00 PM
        // Expected Start: Thursday May 22nd @ 8:00 AM
        // Expected Finish: Friday May 23rd @ 5:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 23, 17, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 22, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 23, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // FNE Constraint: Saturday May 24th @ 12:00 PM
        // Expected Start: Thursday May 22nd @ 8:00 AM
        // Expected Finish: Saturday May 24th @ 12:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 24, 12, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 22, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 24, 12, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

        // FNE Constraint: Sunday May 25th @ 8:00 PM
        // Expected Start: Thursday May 22nd @ 8:00 AM
        // Expected Finish: Sunday May 25th @ 8:00 PM
        // Duration: 2 days
        cal.set(2003, Calendar.MAY, 25, 20, 0);
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_EARLIER_THAN, cal.getTime());
        to.runCalculation();
        cal.set(2003, Calendar.MAY, 22, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.MAY, 25, 20, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());

    }

    public void testSimpleSummaryTask() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);
        to.adopt(to.task2);
        to.addSuccessor(to.task1, to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        assertTrue(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());

        TimeQuantity expectedWork = new TimeQuantity(new BigDecimal("16.00"), TimeQuantityUnit.HOUR);
        assertEquals(expectedWork, to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithPred() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.summaryTask);
        to.adopt(to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        assertTrue(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());

        assertEquals(new TimeQuantity(new BigDecimal("24.00"), TimeQuantityUnit.HOUR),
                to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithPredAndSucc() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.summaryTask);
        to.adopt(to.task2);
        to.addSuccessor(to.summaryTask, to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());
        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        assertTrue(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());
    }

    public void testSummaryTaskWithInternalSS() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_START);
        to.adopt(to.task1);
        to.adopt(to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        assertTrue(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());

        assertEquals(new TimeQuantity(new BigDecimal("24.00"), TimeQuantityUnit.HOUR),
                to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithInternalSF() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN,
                cal.getTime());
        to.addSuccessor(to.task1, to.task2, TaskDependencyType.START_TO_FINISH);
        to.adopt(to.task1);
        to.adopt(to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());

        assertFalse(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertFalse(to.task2.isCriticalPath());

        assertEquals(new TimeQuantity(new BigDecimal("16.00"), TimeQuantityUnit.HOUR),
                to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithInternalFF() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.task2, TaskDependencyType.FINISH_TO_FINISH);
        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());
        to.adopt(to.task1);
        to.adopt(to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        assertTrue(to.summaryTask.isCriticalPath());
        assertTrue(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());

        assertEquals(new TimeQuantity(new BigDecimal("16.00"), TimeQuantityUnit.HOUR),
                to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithPusher() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);

        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        to.addSuccessor(to.task1, to.summaryTask);
        to.addSuccessor(to.task2, to.task3);
        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.adopt(to.task2);
        to.adopt(to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());

        assertTrue(to.summaryTask.isCriticalPath());
        assertFalse(to.task1.isCriticalPath());
        assertTrue(to.task2.isCriticalPath());
        assertTrue(to.task3.isCriticalPath());

        assertEquals(new TimeQuantity(new BigDecimal("24.00"), TimeQuantityUnit.HOUR),
                to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());
    }

    public void testSummaryTaskWithInternalPusher() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.task2);
        to.addSuccessor(to.task2, to.task3);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        to.setConstraint(to.task3, TaskConstraintType.MUST_FINISH_ON, cal.getTime());
        to.adopt(to.task1);
        to.adopt(to.task2);
        to.adopt(to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
    }

    public void testSummaryConstraintTypeModifiesInternalStart() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.adopt(to.task1);
        to.adopt(to.task2);
        to.addSuccessor(to.task1, to.task2);
        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        //sjmittal: from 8.3 setting constraint to summary task is no longer valid.
        //for more details refer: SummaryTask#getConstraintType
        //so here setting the constraint to task1 instead
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 8, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 8, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testSummaryConstraintTypeModifiesInternalFinish() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        //sjmittal: from 8.3 setting constraint to summary task is no longer valid.
        //for more details refer: SummaryTask#getConstraintType
//        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        to.adopt(to.task1);
        to.addSuccessor(to.task1, to.task2);
        to.setConstraint(to.task1, TaskConstraintType.AS_LATE_AS_POSSIBLE, null);
        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());

        cal.set(2003, Calendar.JANUARY, 9, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());

        cal.set(2003, Calendar.JANUARY, 9, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());

        cal.set(2003, Calendar.JANUARY, 10, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 10, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testEmbeddedSummaryTask1() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.adopt(to.summaryTask, to.summaryTask2);
        to.adopt(to.summaryTask2, to.task3);
        to.adopt(to.summaryTask2, to.task4);
        to.addSuccessor(to.task3, to.task4);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask2.getStartTime());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.task4.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task4.getLatestStartDate());
        assertEquals(cal.getTime(), to.task4.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task4.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task4.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask2.getEndTime());
        assertEquals(cal.getTime(), to.task4.getEndTime());
    }

    public void testEmbeddedSummaryTask2() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.addSuccessor(to.task1, to.summaryTask);
        to.adopt(to.summaryTask, to.summaryTask2);
        to.adopt(to.summaryTask2, to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testEmbeddedSummaryTask3() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.adopt(to.summaryTask, to.task1);
        to.adopt(to.summaryTask, to.summaryTask2);
        to.adopt(to.summaryTask2, to.task2);
        to.adopt(to.summaryTask2, to.task3);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getStartTime());
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        assertEquals(cal.getTime(), to.task3.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task3.getLatestStartDate());
        assertEquals(cal.getTime(), to.task3.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getEndTime());
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(cal.getTime(), to.task3.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task3.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task3.getEndTime());

        assertEquals(new TimeQuantity(24, TimeQuantityUnit.HOUR), to.summaryTask.getWorkTQ());
        assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), to.summaryTask2.getWorkTQ());
    }

    public void testTaskDependsOnSummary() throws PersistenceException {
        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime());

        to.adopt(to.summaryTask, to.task1);
        to.adopt(to.summaryTask2, to.task2);
        to.addSuccessor(to.summaryTask, to.task2);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2003, Calendar.JANUARY, 6, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2003, Calendar.JANUARY, 7, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestStartDate());
        assertEquals(cal.getTime(), to.summaryTask2.getStartTime());
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2003, Calendar.JANUARY, 7, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.summaryTask2.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    /**
     * Tests the behavior of a summary task with one or more assignments.
     * <p/>
     * This test was traditionally a test that Project.net did not match MS
     * Projects handling of resources assigned to Summary Tasks.  As of March
     * 2006 however, we properly implement the MS Project way of doing things,
     * therefore this test is no longer "incorrect" as the test says it is.
     */
    public void testSummaryWithAssignmentsIncorrect() throws PersistenceException {
        // Schedule starts Monday January 6th 2003 @ 8:00 AM
        // summaryTask
        // task1, task2 each 1 day
        // task1 is predecessor of task 2
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);
        to.adopt(to.task2);
        to.addSuccessor(to.task1, to.task2);

        // Add a 100% assignment to the _SUMMARY_ task
        // This, in theory, would add an additional amount of work to
        // the Summary task's work (without affecting duration)
        // This is where the incorrect portion of this test occurs
        // We don't compute the extra work; we just want it to not throw an
        // exception
        // Our work value simply ignores assignments to a summary task
        to.addAssignment(to.summaryTask, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));

        to.runCalculation();

        // task1 start: January 6th @ 8:00 AM
        // task2 start: January 7th @ 8:00 AM
        // Summary start: January 6th @ 8:00 AM
        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 8, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());

        // task 1 end: January 6th @ 5:00 PM
        // task 2 end: January 7th @ 5:00 PM
        // Summary end: January 7th @ 5:00 PM
        cal.set(2003, Calendar.JANUARY, 6, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());

        // Expected summary work: 24 hours.
        //sjmittal: expected work should be 8 derived from task 1, 8 from task 2
        //and 8 added via assignment to summary task.
        //previously expected was 32 hrs and I am not sure how we can this figure
        //Now everything should match MS Project (sjmittal: not sure what MSP does in this case).
        TimeQuantity expectedWork = new TimeQuantity(new BigDecimal("24.00"), TimeQuantityUnit.HOUR);
        assertEquals(expectedWork, to.summaryTask.getWorkTQ());

        // Expected summary duration: 2 days
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());

    }

    public void testSummaryResources1() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);
        to.adopt(to.task2);

        to.task1.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        to.task1.setDuration(new TimeQuantity(2, TimeQuantityUnit.DAY));
        to.task2.setDuration(new TimeQuantity(2, TimeQuantityUnit.DAY));

        to.addAssignment(to.summaryTask, 100, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(16, TimeQuantityUnit.HOUR));

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());

        assertEquals(new TimeQuantity(40, TimeQuantityUnit.HOUR), to.summaryTask.getWorkTQ());
        //sjmittal: work complete has been 4 hrs only for task 1. So summary task work complete
        //should also be 4 hrs. Not sure how previously expected was 8 hrs
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), to.summaryTask.getWorkCompleteTQ());
        //sjmittal: would be 0.2 instead of 0.5 as it would be %complete * duration ie .1 * 2 days
        assertEquals(new TimeQuantity("0.2", TimeQuantityUnit.DAY), to.summaryTask.getActualDuration());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    public void testSummaryResources2() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        TimeQuantity t2Work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);
        to.adopt(to.task2);

        to.task1.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        to.task1.setDuration(new TimeQuantity(2, TimeQuantityUnit.DAY));
        to.task2.setDuration(new TimeQuantity(2, TimeQuantityUnit.DAY));

        ScheduleEntryAssignment assn1 = to.addAssignment(to.summaryTask, 100, new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task1, 50, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.addAssignment(to.task2, 100, new TimeQuantity(16, TimeQuantityUnit.HOUR));

        //We set a special id for assn1 because we need to be able to assign
        //a different working time calendar to that user.
        assn1.setPersonID("12345");

        //Here we set up a working time calendar so the resource assigned to the
        //summary task won't be working on Mondays.
        WorkingTimeCalendarDefinition calendarDef;
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY, Calendar.MONDAY});
        TestWorkingTimeCalendarProvider testProvider = new TestWorkingTimeCalendarProvider();
        testProvider.addResourceCalendarDefintion("12345", calendarDef);
        to.schedule.setWorkingTimeCalendarProvider(testProvider);

        to.runCalculation();

        cal.set(2003, Calendar.JANUARY, 6, 8, 0);
        assertEquals(cal.getTime(), to.task1.getStartTime());
        assertEquals(cal.getTime(), to.task2.getStartTime());
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        cal.set(2003, Calendar.JANUARY, 7, 17, 0);
        assertEquals(cal.getTime(), to.task1.getEndTime());
        assertEquals(cal.getTime(), to.task2.getEndTime());
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());

        //sjmittal: work would be 8 for tasl 1 + 16 for task 2 + 16 for assignment added to summary task itself
        //not sure how previously it was computed as 32
        assertEquals(new TimeQuantity(40, TimeQuantityUnit.HOUR), to.summaryTask.getWorkTQ());
        //sjmittal: since only 4 hrs of work is complete for tast 1 this should be 4 and not 6
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), to.summaryTask.getWorkCompleteTQ());
        //sjmittal: would be 0.2 instead of 0.5 as it would be %complete * duration ie .1 * 2 days
        assertEquals(new TimeQuantity("0.2", TimeQuantityUnit.DAY), to.summaryTask.getActualDuration());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task1.getDurationTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.task2.getDurationTQ());
    }

    /**
     * Tests that endpoint calculation updates summary task duration correctly.
     */
    public void testSummaryTaskDurationUpdate() {

        // No Assigments

        // Summary Task
        //   |-- Summary Task 2
        //         |-- Task 1  - 8 hours work, 1 day dur
        //         |-- Task 2  - 16 hours work, 2 day dur
        TestObjects to = new TestObjects(DateUtils.parseDateTime("06/14/04 8:00 AM"),
                new TimeQuantity(8, TimeQuantityUnit.HOUR), new TimeQuantity(16, TimeQuantityUnit.HOUR));
        to.adopt(to.summaryTask2, to.task1);
        to.adopt(to.summaryTask2, to.task2);
        to.adopt(to.summaryTask, to.summaryTask2);

        to.runCalculation();

        // Expected Summary Task 2 - 2 day dur
        // Expected Summary Task   - 2 day dur
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask2.getDurationTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), to.summaryTask.getDurationTQ());

    }

    /**
     * Make sure that if you set a constraint date on a summary task that the
     * constraint will be honored for its first children too.
     */
    public void testChildHonorsParentSNET() {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);

        cal.set(2004, Calendar.MAY, 3, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);

        cal.set(2004, Calendar.MAY, 17, 8, 0, 0);
        //sjmittal: from 8.3 setting constraint to summary task is no longer valid.
        //for more details refer: SummaryTask#getConstraintType
        //so here setting the constraint to task1 instead
        to.setConstraint(to.task1, TaskConstraintType.START_NO_EARLIER_THAN, cal.getTime());

        to.addAssignment(to.task1, 100, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        to.runCalculation();

        //Make sure that the summary task starts at the correct time.
        cal.set(2004, Calendar.MAY, 17, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getStartTime());
    }

    /**
     * Make sure that if you set a FNLT cosntraint date on a parent summary task
     * that the child will honor that constraint too.
     */
    public void testChildHonorsParentFNLT() {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(1, TimeQuantityUnit.DAY);

        cal.set(2004, Calendar.MAY, 21, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.adopt(to.task1);

        cal.set(2004, Calendar.MAY, 21, 17, 0, 0);
        //sjmittal: from 8.3 setting constraint to summary task is no longer valid.
        //for more details refer: SummaryTask#getConstraintType
        //so here setting the constraint to task1 instead
        to.setConstraint(to.task1, TaskConstraintType.FINISH_NO_LATER_THAN, cal.getTime());
        cal.set(2004, Calendar.MAY, 28, 17, 0, 0);
        to.setConstraint(to.task2, TaskConstraintType.MUST_FINISH_ON, cal.getTime());

        to.runCalculation();

        cal.set(2004, Calendar.MAY, 21, 8, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getStartTime());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2004, Calendar.MAY, 21, 17, 0, 0);
        assertEquals(cal.getTime(), to.summaryTask.getEndTime());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2004, Calendar.MAY, 28, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2004, Calendar.MAY, 28, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    //sjmittal: note this test case may not be valid anymore as per the recent
    //design changes for 8.2.0, hence disabling the test case.
    public void ntestActualStartAffectsStartTimes() throws PersistenceException {
        TimeQuantity t1Work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        TimeQuantity t2Work = new TimeQuantity(3, TimeQuantityUnit.DAY);

        cal.set(2004, Calendar.MAY, 3, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        TestObjects to = new TestObjects(cal.getTime(), t1Work, t2Work);
        to.addSuccessor(to.task1, to.task2);

        cal.set(2004, Calendar.MAY, 17, 8, 0, 0);
        to.task1.setActualStartTimeD(cal.getTime());
        cal.set(2004, Calendar.MAY, 17, 17, 0, 0);
        to.task1.setActualEndTimeD(cal.getTime());
        cal.set(2004, Calendar.MAY, 18, 8, 0, 0);
        to.task2.setActualStartTimeD(cal.getTime());
        cal.set(2004, Calendar.MAY, 20, 17, 0, 0);
        to.task2.setActualEndTimeD(cal.getTime());

        to.runCalculation();

        cal.set(2004, Calendar.MAY, 17, 8, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task1.getLatestStartDate());
        assertEquals(cal.getTime(), to.task1.getStartTime());

        cal.set(2004, Calendar.MAY, 17, 17, 0, 0);
        assertEquals(cal.getTime(), to.task1.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task1.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task1.getEndTime());

        cal.set(2004, Calendar.MAY, 18, 8, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestStartDate());
        assertEquals(cal.getTime(), to.task2.getLatestStartDate());
        assertEquals(cal.getTime(), to.task2.getStartTime());

        cal.set(2004, Calendar.MAY, 20, 17, 0, 0);
        assertEquals(cal.getTime(), to.task2.getEarliestFinishDate());
        assertEquals(cal.getTime(), to.task2.getLatestFinishDate());
        assertEquals(cal.getTime(), to.task2.getEndTime());
    }

    public void testChildTaskWorkCompleteUpdateUpdatesParent() {

    }

    /**
     * Tests endpoint calculation on a schedule with a task that is being created.
     * This task has no ID.
     * This is needed for round-trip date and duration calculations on "task create".
     */
    public void testTaskCreate() throws PersistenceException {
        TestObjects to = new TestObjects(DateUtils.parseDateTime("06/07/04 8:00 AM"));

        // New Task, 1 Day duration
        // Task has no ID (since absence of ID causes INSERT to occur instead of UPDATE)
        // We add it to the map with the <code>null</code> key.
        // This requires endpoint calculation to avoid accessing the map with null keys (which it does)
        ScheduleEntry newTask = new Task();
        newTask.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        to.scheduleEntries.put(null, newTask);
        to.schedule.setEntryMap(to.scheduleEntries);
        newTask.setPlanID(to.schedule.getID());

        to.runCalculation();

        assertEquals(DateUtils.parseDateTime("06/07/04 8:00 AM"), newTask.getStartTime());
        assertEquals(DateUtils.parseDateTime("06/07/04 5:00 PM"), newTask.getEndTime());
    }

    //This method allows you to test live data.  This can be useful, for example,
    //if you are trying to do some performance testing.  (You don't have to log
    //in everytime to set up everything.)  I am keeping this unit test commented
    //out because live data isn't always going to have those same ID's.
    //
    //One note -- if you want to cause a full recalculation with the worst case
    //scenario as far as storage, you'll need to set the calendar to different
    //dates every time you run this method.
//    public void testLiveRecalculationTest() throws PnetException {
//        ProjectSpace space = new ProjectSpace();
//        space.setID("30556");
//        Application.getUser().setCurrentSpace(space);
//
//        Schedule schedule = new Schedule();
//        schedule.setID("30570");
//        schedule.load();
//
//        //Change the schedule start date
//        cal.set(2000, 7, 1, 8, 0, 0);
//        schedule.setScheduleStartDate(cal.getTime());
//        schedule.store();
//
//        schedule.recalculateTaskTimes();
//    }
}

