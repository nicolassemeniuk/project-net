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
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 17087 $
|       $Date: 2008-03-21 21:17:04 +0530 (Fri, 21 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.calc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.PnetRuntimeException;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

public class ScheduleEntryWorkCompleteModifierTest extends TestCase {
    private IWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryWorkCompleteModifierTest(String s) {
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
     *         tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleEntryWorkCompleteModifierTest.class);
        return suite;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        Application.login();
    }

    public void test0Assignments8Hours0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
    }

    public void test1Assignment8Hours0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        assertEquals(assn.getWorkComplete(), new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(entry.getWorkCompleteTQ(), new TimeQuantity(0, TimeQuantityUnit.HOUR));
    }

    public void test1Assignment8Hours4Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        entry.setStartTimeD(new Date());

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn.setStartTime(new Date());
        assn.setEndTime(new Date());
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(4, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn.getWorkComplete());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
    }

    public void test1Assignment8Hours8Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn.setStartTime(new Date());
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(8, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged        
        assertEquals(assn.getWorkComplete(), new TimeQuantity(0, TimeQuantityUnit.HOUR));
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(entry.getUnallocatedWorkComplete(), new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(entry.getWorkCompleteTQ(), new TimeQuantity(8, TimeQuantityUnit.HOUR));
    }

    public void test1Assignment8Hours9Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        try {
            ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(9, TimeQuantityUnit.HOUR));
            fail("We should have thrown an exception indicating that we cannot process this much work.");
        } catch(PnetRuntimeException e) {
            //This exception is supposed to happen, we expect it.
        }
    }

    public void test1Assignment8HoursFrom6To7Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(6, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(6, TimeQuantityUnit.HOUR));
        assn.setStartTime(new Date());
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(7, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(6, TimeQuantityUnit.HOUR), assn.getWorkComplete());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        assertEquals(new TimeQuantity(7, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
    }

    public void test1Assignment8HoursFrom7To6Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));
        assn.setStartTime(new Date());
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(6, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(assn.getWorkComplete(), new TimeQuantity(7, TimeQuantityUnit.HOUR));
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(entry.getUnallocatedWorkComplete(), new TimeQuantity(-1, TimeQuantityUnit.HOUR));
        assertEquals(entry.getWorkCompleteTQ(), new TimeQuantity(6, TimeQuantityUnit.HOUR));
    }

    public void test1Assignment8HoursFrom7To0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(assn.getWorkComplete(), new TimeQuantity(7, TimeQuantityUnit.HOUR));
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(entry.getUnallocatedWorkComplete(), new TimeQuantity(-7, TimeQuantityUnit.HOUR));
        assertEquals(entry.getWorkCompleteTQ(), new TimeQuantity(0, TimeQuantityUnit.HOUR));
    }

    public void test1Assignment8HoursFrom8To0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn = new ScheduleEntryAssignment();
        assn.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));
        //Now make sure everything worked out ok
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(assn.getWorkComplete(), new TimeQuantity(8, TimeQuantityUnit.HOUR));
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(entry.getUnallocatedWorkComplete(), new TimeQuantity(-8, TimeQuantityUnit.HOUR));
        assertEquals(entry.getWorkCompleteTQ(), new TimeQuantity(0, TimeQuantityUnit.HOUR));
    }

    public void test2AssignmentsEven16Hours0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsEven16Hours8Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(8, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsEven16HoursFrom7To8Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(3.5, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(3.5, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(8, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(3.5, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(3.5, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsEven16HoursFrom8To7Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(7, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(7, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(-1, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsEven16HoursFrom8to0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(-8, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsEven16HoursFrom16to0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(16, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(-16, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsUneven16HoursFrom7To8Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(8, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(7, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsUneven16HoursFrom8To16Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(16, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsUneven16HoursFrom16To0Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(16, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(13.3, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(13.3, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(2.7, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(2.7, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: moved out work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(-16, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(13.3, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(2.7, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test2AssignmentsUneven16HoursFrom8To14Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(14, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(14, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(6, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(7, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
    }

    public void test3AssignmentsUneven24HoursFrom10To19Complete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(24, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(7, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(2, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        ScheduleEntryAssignment assn3 = new ScheduleEntryAssignment();
        assn3.setPersonID("3");
        assn3.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn3.setWorkComplete(new TimeQuantity(1, TimeQuantityUnit.HOUR));
        assn3.setStartTime(new Date());
        assn3.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        assignments.add(assn3);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(19, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(19, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(9, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(7, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), assn3.getWorkComplete());
    }

    public void test3AssignmentsWithCompletion() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(24, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(14, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(2, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        ScheduleEntryAssignment assn3 = new ScheduleEntryAssignment();
        assn3.setPersonID("3");
        assn3.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn3.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assn3.setStartTime(new Date());
        assn3.setPercentAssigned(100);

        Collection assignments = new ArrayList(3);
        assignments.add(assn1);
        assignments.add(assn2);
        assignments.add(assn3);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(20, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(20, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(6, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assn3.getWorkComplete());
    }

    public void testVerifyBFD2435() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(24, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        ScheduleEntryAssignment assn3 = new ScheduleEntryAssignment();
        assn3.setPersonID("3");
        assn3.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assn3.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn3.setStartTime(new Date());
        assn3.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        assignments.add(assn3);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(24, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(24, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn3.getWorkComplete());
    }

    public void testVerifyBFD2435part2() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(32, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(10.67, TimeQuantityUnit.HOUR));

        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        assn1.setWork(new TimeQuantity(10.67, TimeQuantityUnit.HOUR));
        assn1.setWorkComplete(new TimeQuantity(10.67, TimeQuantityUnit.HOUR));
        assn1.setStartTime(new Date());
        assn1.setPercentAssigned(100);

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        assn2.setWork(new TimeQuantity(10.67, TimeQuantityUnit.HOUR));
        assn2.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn2.setStartTime(new Date());
        assn2.setPercentAssigned(100);

        ScheduleEntryAssignment assn3 = new ScheduleEntryAssignment();
        assn3.setPersonID("3");
        assn3.setWork(new TimeQuantity(10.67, TimeQuantityUnit.HOUR));
        assn3.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assn3.setStartTime(new Date());
        assn3.setPercentAssigned(100);

        Collection assignments = new LinkedList();
        assignments.add(assn1);
        assignments.add(assn2);
        assignments.add(assn3);
        entry.addAssignments(assignments);

        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(32, TimeQuantityUnit.HOUR));

        assertEquals(new TimeQuantity(32, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        //sjmittal: new work now goes to unalllocated work complete
        assertEquals(new TimeQuantity(21.33, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        //sjmittal: as per new design assignment work complete is derived only from actual work logged
        assertEquals(new TimeQuantity(10.67, TimeQuantityUnit.HOUR), assn1.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn2.getWorkComplete());
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assn3.getWorkComplete());
    }

    /**
     * Tests what happens when there are no assignments and the work complete
     * is changed.  What should happen is the work complete should be put into
     * the "Unallocated Work Complete" field.
     */
    public void test0AssignmentsChangeWorkComplete() {
        ScheduleEntry entry = new Task();
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));

        //Test 1, 0h complete -> 8h complete
        entry.setWorkComplete(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());

        //Test 2, 4h complete (unallocated) -> 12h complete
        entry.setUnallocatedWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(12, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(12, TimeQuantityUnit.HOUR),  entry.getUnallocatedWorkComplete());

        //Test 3, 12h complete (unallocated) -> 0h complete
        entry.setUnallocatedWorkComplete(new TimeQuantity(12, TimeQuantityUnit.HOUR));
        entry.setWorkComplete(new TimeQuantity(12, TimeQuantityUnit.HOUR));
        ScheduleEntryWorkCompleteModifier.computeWorkComplete(provider, entry, new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR),  entry.getUnallocatedWorkComplete());
    }
}
