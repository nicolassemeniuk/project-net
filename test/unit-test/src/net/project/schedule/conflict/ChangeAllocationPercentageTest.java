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
 package net.project.schedule.conflict;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAllocationList;
import net.project.resource.ResourceAssignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

public class ChangeAllocationPercentageTest extends TestCase {
    private GregorianCalendar cal = new GregorianCalendar();

    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public ChangeAllocationPercentageTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     *
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
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
     * tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ChangeAllocationPercentageTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
        cal.set(GregorianCalendar.MILLISECOND, 0);
    }

    private Schedule createSchedule() {
        Schedule schedule = new Schedule();
        schedule.setID(WellKnownObjects.TEST_PLAN_ID);

        return schedule;
    }

    private void createAllocations(String personID, int percentAllocation, ResourceAllocationList ral, Date startDate, Date endDate) {
        Date currentDate = startDate;

        while (currentDate.before(endDate) || currentDate.equals(endDate)) {
            ResourceAssignment ra = new ResourceAssignment();
            ra.setResourceID(personID);
            ra.setPercentAssigned(percentAllocation);
            ra.setAssignmentDate(currentDate);
            ral.addAllocation(ra);

            currentDate = DateUtils.addDay(currentDate, 1);
        }
    }

    /**
     * This is the simplest test we will run.
     *
     * M,T - 75% on the task
     * W,R,F - 125% total, 50% on an unnamed task
     *
     * The task's LFT is Friday
     *
     * @throws PersistenceException
     */
    public void testChangeAllocationPercentage() throws PersistenceException {
        Date startDate, endDate;
        ScheduleEntry entry = new Task();
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setTimeZone(TimeZone.getDefault());
        assignment.setPersonID("1");
        ResourceAllocationList ral = new ResourceAllocationList();

        cal.set(2003, 0, 6, 8, 0, 0);
        startDate = cal.getTime();
        assignment.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        endDate = cal.getTime();
        assignment.setEndTime(cal.getTime());
        assignment.setWork(new TimeQuantity(12, TimeQuantityUnit.HOUR));
        assignment.setPercentAssigned(75);
        createAllocations("1", 125, ral, startDate, endDate);

        //Wed-Fri person #1 has 125 percent allocation
        cal.set(2003, 0, 8, 0, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 10, 0, 0, 0);
        endDate = cal.getTime();
        createAllocations("1", 50, ral, startDate, endDate);

        //Set up the schedule entry
        entry.setID("1");
        entry.setCriticalPath(false);
        cal.set(2003, 0, 6, 8, 0, 0);
        entry.setStartTimeD(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        entry.setEndTimeD(cal.getTime());
        cal.set(2003, 0, 10, 17, 0, 0);
        entry.setLatestFinishDate(cal.getTime());
        entry.getAssignmentList().addAssignment(assignment);

        //Set up the schedule
        cal.set(2003, 0, 30, 0, 0, 0);
        Schedule schedule = createSchedule();
        schedule.setScheduleEndDate(cal.getTime());
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));
        Map entryMap = new HashMap();
        entryMap.put(entry.getID(), entry);
        schedule.setEntryMap(entryMap);

        //Now see if the change allocation percentage will switch to the
        //correct percentage
        ChangeAllocationPercentage cap = new ChangeAllocationPercentage(schedule, entry, assignment,  ral);
        assertTrue(cap.isApplicable());
        assertFalse(cap.doesScheduleEndDateChange());
        assertEquals(cap.getNewPercentAssigned(), 50);
    }

    /**
     * This test ensures that we notice when the schedule end date would change
     * as the result of the change in allocation.
     *
     * @throws PersistenceException
     */
    public void testEndDateChanges() throws PersistenceException {
        Date startDate, endDate;
        ScheduleEntry entry = new Task();
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        assignment.setTimeZone(TimeZone.getDefault());
        ResourceAllocationList ral = new ResourceAllocationList();

        cal.set(2003, 0, 6, 8, 0, 0);
        startDate = cal.getTime();
        assignment.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        endDate = cal.getTime();
        assignment.setEndTime(cal.getTime());
        assignment.setWork(new TimeQuantity(12, TimeQuantityUnit.HOUR));
        assignment.setPercentAssigned(75);
        createAllocations("1", 125, ral, startDate, endDate);

        //Set up the schedule entry
        entry.setID("1");
        entry.setCriticalPath(false);
        cal.set(2003, 0, 6, 8, 0, 0);
        entry.setStartTimeD(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        entry.setEndTimeD(cal.getTime());
        entry.setLatestFinishDate(cal.getTime());
        entry.getAssignmentList().addAssignment(assignment);
        entry.setWork(new TimeQuantity(12, TimeQuantityUnit.HOUR));

        //Set up the schedule
        Schedule schedule = createSchedule();
        cal.set(2003, 0, 7, 17, 0, 0);
        schedule.setScheduleEndDate(cal.getTime());
        schedule.setTimeZone(TimeZone.getDefault());
        Map entryMap = new HashMap();
        entryMap.put(entry.getID(), entry);
        schedule.setEntryMap(entryMap);
        entry.setPlanID(schedule.getID());

        //Now see if the change allocation percentage will switch to the
        //correct percentage
        ChangeAllocationPercentage cap = new ChangeAllocationPercentage(schedule, entry, assignment,  ral);
        assertTrue(cap.isApplicable());
        assertTrue(cap.doesScheduleEndDateChange());
        assertEquals(cap.getNewPercentAssigned(), 50);
        cal.set(2003, 0, 8, 20, 0, 0);
        assertEquals(cal.getTime(), cap.getNewScheduleEndDate());
    }

    /**
     * This method tests to ensure the ChangeAllocationPercentage can tell when
     * it is not applicable.  This occurs when the user already is assigned 100%
     * elsewhere.
     */
    public void testNotApplicable() throws PersistenceException {
        Date startDate, endDate;
        ScheduleEntry entry = new Task();
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setTimeZone(TimeZone.getDefault());
        ResourceAllocationList ral = new ResourceAllocationList();

        cal.set(2003, 0, 6, 8, 0, 0);
        startDate = cal.getTime();
        assignment.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        endDate = cal.getTime();
        assignment.setEndTime(cal.getTime());
        assignment.setWork(new TimeQuantity(12, TimeQuantityUnit.HOUR));
        assignment.setPercentAssigned(75);
        createAllocations("1", 175, ral, startDate, endDate);

        //Set up the schedule entry
        entry.setID("1");
        entry.setCriticalPath(false);
        cal.set(2003, 0, 6, 8, 0, 0);
        entry.setStartTimeD(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        entry.setEndTimeD(cal.getTime());
        entry.setLatestFinishDate(cal.getTime());
        entry.getAssignmentList().addAssignment(assignment);
        entry.setWork(new TimeQuantity(12, TimeQuantityUnit.HOUR));

        //Set up the schedule
        Schedule schedule = createSchedule();
        cal.set(2003, 0, 7, 17, 0, 0);
        schedule.setScheduleEndDate(cal.getTime());
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));
        Map entryMap = new HashMap();
        entryMap.put(entry.getID(), entry);
        schedule.setEntryMap(entryMap);

        //Now see if the change allocation percentage will switch to the
        //correct percentage
        ChangeAllocationPercentage cap = new ChangeAllocationPercentage(schedule, entry, assignment,  ral);
        assertFalse(cap.isApplicable());
    }
}
