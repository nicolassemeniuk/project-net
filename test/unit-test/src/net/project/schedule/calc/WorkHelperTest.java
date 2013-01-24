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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link WorkHelper}.
 * 
 * @author Tim Morrow
 */
public class WorkHelperTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public WorkHelperTest(String s) {
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
        TestSuite suite = new TestSuite(WorkHelperTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link WorkHelper#getConvertedWork(net.project.schedule.ScheduleEntry)}.
     */
    public void testGetConvertedWorkScheduleEntry() {
        ScheduleEntry scheduleEntry = new Task();

        try {
            WorkHelper.getConvertedWork((ScheduleEntry) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.MONTH));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(scheduleEntry).getUnits());

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(scheduleEntry).getUnits());

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(scheduleEntry).getUnits());

    }

    /**
     * Tests {@link WorkHelper#setConvertedWork(net.project.schedule.ScheduleEntry,net.project.util.TimeQuantity)}.
     */
    public void testSetConvertedWorkScheduleEntry() {

        TimeQuantity work;
        TimeQuantityUnit unit;
        ScheduleEntry scheduleEntry = new Task();

        try {
            WorkHelper.setConvertedWork((ScheduleEntry) null, new TimeQuantity(1, TimeQuantityUnit.DAY));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            WorkHelper.setConvertedWork(new Task(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        unit = TimeQuantityUnit.DAY;
        WorkHelper.setConvertedWork(scheduleEntry, work);
        assertEquals(unit, scheduleEntry.getWorkTQ().getUnits());

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        unit = TimeQuantityUnit.WEEK;
        WorkHelper.setConvertedWork(scheduleEntry, work);
        assertEquals(unit, scheduleEntry.getWorkTQ().getUnits());

        scheduleEntry.setWork(new TimeQuantity(1, TimeQuantityUnit.MONTH));
        unit = TimeQuantityUnit.MONTH;
        WorkHelper.setConvertedWork(scheduleEntry, work);
        assertEquals(unit, scheduleEntry.getWorkTQ().getUnits());
    }

    /**
     * Tests {@link WorkHelper#getConvertedWork(net.project.resource.ScheduleEntryAssignment)}.
     */
    public void testGetConvertedWorkAssignment() {

        ScheduleEntryAssignment assignment;

        // Null
        try {
            WorkHelper.getConvertedWork((ScheduleEntryAssignment) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        assignment = new ScheduleEntryAssignment();

        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.HOUR));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(assignment).getUnits());

        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(assignment).getUnits());

        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(assignment).getUnits());

        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.MONTH));
        assertEquals(TimeQuantityUnit.HOUR, WorkHelper.getConvertedWork(assignment).getUnits());
    }

    /**
     * Tests {@link WorkHelper#setConvertedWork(net.project.resource.ScheduleEntryAssignment, net.project.util.TimeQuantity)}.
     */
    public void testSetConvertedWorkAssignment() {

        ScheduleEntryAssignment assignment;

        try {
            WorkHelper.setConvertedWork((ScheduleEntryAssignment) null, new TimeQuantity(1, TimeQuantityUnit.HOUR));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            WorkHelper.setConvertedWork(new ScheduleEntryAssignment(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));

        WorkHelper.setConvertedWork(assignment, new TimeQuantity(1, TimeQuantityUnit.HOUR));
        assertEquals(TimeQuantityUnit.DAY, assignment.getWork().getUnits());

        WorkHelper.setConvertedWork(assignment, new TimeQuantity(1, TimeQuantityUnit.DAY));
        assertEquals(TimeQuantityUnit.DAY, assignment.getWork().getUnits());

        WorkHelper.setConvertedWork(assignment, new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(TimeQuantityUnit.DAY, assignment.getWork().getUnits());

        WorkHelper.setConvertedWork(assignment, new TimeQuantity(1, TimeQuantityUnit.MONTH));
        assertEquals(TimeQuantityUnit.DAY, assignment.getWork().getUnits());

    }

    /**
     * Tests {@link WorkHelper#updateWorkFromAssignments(net.project.schedule.ScheduleEntry)}.
     */
    public void testUpdateWorkFromAssignments() {

        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;

        // Null
        try {
            WorkHelper.updateWorkFromAssignments(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Zero assignments, work is zero
        scheduleEntry = new Task();
        scheduleEntry.setWork(new TimeQuantity(4, TimeQuantityUnit.DAY));
        WorkHelper.updateWorkFromAssignments(scheduleEntry);
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), scheduleEntry.getWorkTQ());

        // Assignment in hours
        // Work in days

        // 1 assignment
        scheduleEntry = new Task();
        scheduleEntry.setWork(new TimeQuantity(4, TimeQuantityUnit.DAY));

        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.addAssignment(assignment);

        WorkHelper.updateWorkFromAssignments(scheduleEntry);
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getWorkTQ());

        // 2 assignments
        scheduleEntry = new Task();
        scheduleEntry.setWork(new TimeQuantity(4, TimeQuantityUnit.DAY));

        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.addAssignment(assignment);
        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.addAssignment(assignment);

        WorkHelper.updateWorkFromAssignments(scheduleEntry);
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getWorkTQ());

        // Assignment in days
        // Work in days
        scheduleEntry = new Task();
        scheduleEntry.setWork(new TimeQuantity(4, TimeQuantityUnit.DAY));

        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.addAssignment(assignment);
        assignment = new ScheduleEntryAssignment();
        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.addAssignment(assignment);

        WorkHelper.updateWorkFromAssignments(scheduleEntry);
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getWorkTQ());

    }
}
