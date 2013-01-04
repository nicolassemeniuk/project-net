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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceAllocationList;
import net.project.resource.ResourceAssignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

public class ReschedulerTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ReschedulerTest(String s) {
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
        TestSuite suite = new TestSuite(ReschedulerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Test the resolution of a schedule entry assignment overallocation by
     * reducing the percent assignment.
     *
     * @throws PersistenceException
     */
    public void testOverallocationResolution1() throws PersistenceException {
        Schedule schedule = new Schedule();
        PnCalendar cal = new PnCalendar();

        //Set up the schedule
        schedule.setID(WellKnownObjects.TEST_PLAN_ID);
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));

        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setTimeZone(TimeZone.getTimeZone("PST"));
        sea1.setPersonID("1");
        sea1.setObjectID("101");
        sea1.setPercentAssigned(100);
        sea1.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        
        ScheduleEntry se1 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se1.setStartTimeD(cal.getTime());
        se1.setEarliestStartDate(cal.getTime());
        sea1.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        se1.setEndTimeD(cal.getTime());
        sea1.setEndTime(cal.getTime());
        se1.setID("101");
        se1.getAssignmentList().addAssignment(sea1);
        se1.setPlanID(schedule.getID());

        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setTimeZone(TimeZone.getTimeZone("PST"));
        sea2.setPersonID("1");
        sea2.setObjectID("102");
        sea2.setPercentAssigned(25);
        sea2.setWork(new TimeQuantity(10, TimeQuantityUnit.HOUR));

        ScheduleEntry se2 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se2.setStartTimeD(cal.getTime());
        se2.setEarliestStartDate(cal.getTime());
        sea2.setStartTime(cal.getTime());
        cal.set(2003, 0, 10, 17, 0, 0);
        se2.setEndTimeD(cal.getTime());
        sea2.setEndTime(cal.getTime());
        se1.setLatestFinishDate(cal.getTime());
        se2.setLatestFinishDate(cal.getTime());
        se2.setID("102");
        se2.getAssignmentList().addAssignment(sea2);
        se2.setPlanID(schedule.getID());

        Map entryMap = new HashMap();
        entryMap.put(se1.getID(), se1);
        entryMap.put(se2.getID(), se2);
        schedule.setEntryMap(entryMap);

        //Set up the resource allocation lists for this user we've created.
        Map resourceAllocationLists = new HashMap();
        ResourceAllocationList ral = new ResourceAllocationList();
        cal.set(2003, 0, 6, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 7, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 8, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 25, "1"));
        cal.set(2003, 0, 9, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 25, "1"));
        cal.set(2003, 0, 10, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 25, "1"));
        resourceAllocationLists.put("1", ral);

        //Set up a list of the overallocated assignments
        List overallocatedAssignments = new ArrayList();
        overallocatedAssignments.add(sea1);

        List resolutions = Rescheduler.findOverallocationResolutions(
            schedule, se1, resourceAllocationLists, overallocatedAssignments);
        ChangeAllocationPercentage fix = (ChangeAllocationPercentage)findFirstInstanceOf(resolutions, ChangeAllocationPercentage.class);
        assertNotNull(fix);
        assertEquals(75, fix.getNewPercentAssigned());
    }

    /**
     * Test that an overallocation resolution won't be applied if there is
     * 201%+ of a person's day already assigned.  (If a person has 201%
     * assigned, even if this task wasn't assigned, it wouldn't fix anything.)
     *
     * @throws PersistenceException
     */
    public void testOverallocationResolution2() throws PersistenceException {
        Schedule schedule = new Schedule();
        PnCalendar cal = new PnCalendar();

        //Set up the schedule
        schedule.setID(WellKnownObjects.TEST_PLAN_ID);
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));

        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setTimeZone(TimeZone.getTimeZone("PST"));
        sea1.setPersonID("1");
        sea1.setObjectID("101");
        sea1.setPercentAssigned(100);
        sea1.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));

        ScheduleEntry se1 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se1.setStartTimeD(cal.getTime());
        se1.setEarliestStartDate(cal.getTime());
        sea1.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        se1.setEndTimeD(cal.getTime());
        sea1.setEndTime(cal.getTime());
        se1.setID("101");
        se1.getAssignmentList().addAssignment(sea1);
        se1.setPlanID(schedule.getID());

        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setTimeZone(TimeZone.getTimeZone("PST"));
        sea2.setPersonID("1");
        sea2.setObjectID("102");
        sea2.setPercentAssigned(100);
        sea2.setWork(new TimeQuantity(40, TimeQuantityUnit.HOUR));

        ScheduleEntry se2 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se2.setStartTimeD(cal.getTime());
        se2.setEarliestStartDate(cal.getTime());
        sea2.setStartTime(cal.getTime());
        cal.set(2003, 0, 10, 17, 0, 0);
        se2.setEndTimeD(cal.getTime());
        sea2.setEndTime(cal.getTime());
        se1.setLatestFinishDate(cal.getTime());
        se2.setLatestFinishDate(cal.getTime());
        se2.setID("102");
        se2.getAssignmentList().addAssignment(sea2);
        se2.setPlanID(schedule.getID());

        ScheduleEntryAssignment sea3 = new ScheduleEntryAssignment();
        sea3.setTimeZone(TimeZone.getTimeZone("PST"));
        sea3.setPersonID("1");
        sea3.setObjectID("103");
        sea3.setPercentAssigned(25);
        sea3.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));

        ScheduleEntry se3 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se3.setStartTimeD(cal.getTime());
        se3.setEarliestStartDate(cal.getTime());
        sea3.setStartTime(cal.getTime());
        cal.set(2003, 0, 10, 17, 0, 0);
        se3.setEndTimeD(cal.getTime());
        sea3.setEndTime(cal.getTime());
        se3.setLatestFinishDate(cal.getTime());
        se3.setID("103");
        se3.getAssignmentList().addAssignment(sea3);
        se3.setPlanID(schedule.getID());

        Map entryMap = new HashMap();
        entryMap.put(se1.getID(), se1);
        entryMap.put(se2.getID(), se2);
        entryMap.put(se3.getID(), se3);
        schedule.setEntryMap(entryMap);

        //Set up the resource allocation lists for this user we've created.
        Map resourceAllocationLists = new HashMap();
        ResourceAllocationList ral = new ResourceAllocationList();
        cal.set(2003, 0, 6, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 225, "1"));
        cal.set(2003, 0, 7, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 225, "1"));
        cal.set(2003, 0, 8, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 9, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 10, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        resourceAllocationLists.put("1", ral);

        //Set up a list of the overallocated assignments
        List overallocatedAssignments = new ArrayList();
        overallocatedAssignments.add(sea1);

        List resolutions = Rescheduler.findOverallocationResolutions(
            schedule, se1, resourceAllocationLists, overallocatedAssignments);
        ChangeAllocationPercentage fix = (ChangeAllocationPercentage)findFirstInstanceOf(resolutions, ChangeAllocationPercentage.class);
        assertNull(fix);
    }


    /**
     * Test the resolution of a schedule entry assignment overallocation by
     * changing the start date of the task to a later date.
     *
     * @throws PersistenceException
     */
    public void testOverallocationResolution3() throws PersistenceException {
        Schedule schedule = new Schedule();
        PnCalendar cal = new PnCalendar();
        cal.set(PnCalendar.MILLISECOND, 0);

        //Set up the schedule
        schedule.setID(WellKnownObjects.TEST_PLAN_ID);
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));

        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setTimeZone(TimeZone.getTimeZone("PST"));
        sea1.setPersonID("1");
        sea1.setObjectID("101");
        sea1.setPercentAssigned(25);
        sea1.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));

        ScheduleEntry se1 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se1.setStartTimeD(cal.getTime());
        se1.setEarliestStartDate(cal.getTime());
        sea1.setStartTime(cal.getTime());
        cal.set(2003, 0, 7, 17, 0, 0);
        se1.setEndTimeD(cal.getTime());
        sea1.setEndTime(cal.getTime());
        se1.setID("101");
        se1.getAssignmentList().addAssignment(sea1);
        se1.setPlanID(schedule.getID());

        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setTimeZone(TimeZone.getTimeZone("PST"));
        sea2.setPersonID("1");
        sea2.setObjectID("102");
        sea2.setPercentAssigned(100);
        sea2.setWork(new TimeQuantity(40, TimeQuantityUnit.HOUR));

        ScheduleEntry se2 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se2.setStartTimeD(cal.getTime());
        se2.setEarliestStartDate(cal.getTime());
        sea2.setStartTime(cal.getTime());
        cal.set(2003, 0, 10, 17, 0, 0);
        se2.setEndTimeD(cal.getTime());
        sea2.setEndTime(cal.getTime());
        se1.setLatestFinishDate(cal.getTime());
        se2.setLatestFinishDate(cal.getTime());
        se2.setID("102");
        se2.getAssignmentList().addAssignment(sea2);
        se2.setPlanID(schedule.getID());

        Map entryMap = new HashMap();
        entryMap.put(se1.getID(), se1);
        entryMap.put(se2.getID(), se2);
        schedule.setEntryMap(entryMap);

        //Set up the resource allocation lists for this user we've created.
        Map resourceAllocationLists = new HashMap();
        ResourceAllocationList ral = new ResourceAllocationList();
        cal.set(2003, 0, 6, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 7, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 8, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        cal.set(2003, 0, 9, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        cal.set(2003, 0, 10, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        resourceAllocationLists.put("1", ral);

        //Set up a list of the overallocated assignments
        List overallocatedAssignments = new ArrayList();
        overallocatedAssignments.add(sea2);

        List resolutions = Rescheduler.findOverallocationResolutions(
            schedule, se2, resourceAllocationLists, overallocatedAssignments);

        //Make sure there is a "MoveStartDateForward" resolution here
        MoveStartDateForward resolution = (MoveStartDateForward)findFirstInstanceOf(resolutions, MoveStartDateForward.class);
        assertNotNull(resolution);
        cal.set(2003, 0, 8, 11, 0, 0);
        assertEquals(cal.getTime(), resolution.getNewScheduleEntryStartDate());
    }


    /**
     * Test the resolution of a schedule entry assignment overallocation by
     * changing the start date of the task to an earlier date.
     *
     * @throws PersistenceException
     */
    public void testOverallocationResolution4() throws PersistenceException {
        Schedule schedule = new Schedule();
        PnCalendar cal = new PnCalendar();
        cal.set(PnCalendar.MILLISECOND, 0);

        //Set up the schedule
        schedule.setID(WellKnownObjects.TEST_PLAN_ID);
        schedule.setTimeZone(TimeZone.getTimeZone("PST"));

        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setTimeZone(TimeZone.getTimeZone("PST"));
        sea1.setPersonID("1");
        sea1.setObjectID("101");
        sea1.setPercentAssigned(25);
        sea1.setWork(new TimeQuantity(4, TimeQuantityUnit.HOUR));

        ScheduleEntry se1 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se1.setEarliestStartDate(cal.getTime());
        cal.set(2003, 0, 13, 8, 0, 0);
        se1.setStartTimeD(cal.getTime());
        sea1.setStartTime(cal.getTime());
        cal.set(2003, 0, 14, 17, 0, 0);
        se1.setEndTimeD(cal.getTime());
        se1.setLatestFinishDate(cal.getTime());
        sea1.setEndTime(cal.getTime());
        se1.setID("101");
        se1.getAssignmentList().addAssignment(sea1);
        se1.setPlanID(schedule.getID());

        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setTimeZone(TimeZone.getTimeZone("PST"));
        sea2.setPersonID("1");
        sea2.setObjectID("102");
        sea2.setPercentAssigned(100);
        sea2.setWork(new TimeQuantity(40, TimeQuantityUnit.HOUR));

        ScheduleEntry se2 = new Task();
        cal.set(2003, 0, 6, 8, 0, 0);
        se2.setEarliestStartDate(cal.getTime());
        cal.set(2003, 0, 8, 8, 0, 0);
        se2.setStartTimeD(cal.getTime());
        sea2.setStartTime(cal.getTime());
        cal.set(2003, 0, 14, 17, 0, 0);
        se2.setEndTimeD(cal.getTime());
        sea2.setEndTime(cal.getTime());
        se2.setLatestFinishDate(cal.getTime());
        se2.setID("102");
        se2.getAssignmentList().addAssignment(sea2);
        se2.setPlanID(schedule.getID());

        Map entryMap = new HashMap();
        entryMap.put(se1.getID(), se1);
        entryMap.put(se2.getID(), se2);
        schedule.setEntryMap(entryMap);

        //Set up the resource allocation lists for this user we've created.
        Map resourceAllocationLists = new HashMap();
        ResourceAllocationList ral = new ResourceAllocationList();
        cal.set(2003, 0, 8, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        cal.set(2003, 0, 9, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        cal.set(2003, 0, 10, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 100, "1"));
        cal.set(2003, 0, 13, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        cal.set(2003, 0, 14, 0, 0, 0);
        ral.addAllocation(new ResourceAssignment(cal.getTime(), 125, "1"));
        resourceAllocationLists.put("1", ral);

        //Set up a list of the overallocated assignments
        List overallocatedAssignments = new ArrayList();
        overallocatedAssignments.add(sea2);

        List resolutions = Rescheduler.findOverallocationResolutions(
            schedule, se2, resourceAllocationLists, overallocatedAssignments);

        //Make sure there is a resolution that moves the start date backwards.
        MoveStartDateBackward resolution = (MoveStartDateBackward)findFirstInstanceOf(resolutions, MoveStartDateBackward.class);
        assertNotNull(resolution);
        cal.set(2003, 0, 6, 11, 0, 0);
        assertEquals(cal.getTime(), resolution.getNewScheduleEntryStartDate());
        assertFalse(resolution.doesScheduleEndDateChange());
    }

    private Object findFirstInstanceOf(List listOfObjects, Class classToLookFor) {
        Object foundObject = null;

        for (Iterator it = listOfObjects.iterator(); it.hasNext();) {
            Object currentObject = it.next();
            if (currentObject.getClass().equals(classToLookFor)) {
                foundObject = currentObject;
                break;
            }
        }

        return foundObject;
    }
}
