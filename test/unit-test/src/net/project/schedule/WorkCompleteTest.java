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
|   $Revision: 17104 $
|       $Date: 2008-03-25 16:29:57 +0530 (Tue, 25 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.admin.ApplicationSpace;
import net.project.application.Application;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

public class WorkCompleteTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public WorkCompleteTest(String s) {
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
        TestSuite suite = new TestSuite(WorkCompleteTest.class);
        return suite;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        Application.login();

        // Ensure the plan is created
        WellKnownObjects.ensurePlan(WellKnownObjects.TEST_SPACE_ID, WellKnownObjects.TEST_PLAN_ID);

        SessionManager.getUser().setCurrentSpace(ApplicationSpace.DEFAULT_APPLICATION_SPACE);
    }

    public void testWorkCompleteLivespan() throws PersistenceException, NoWorkingTimeException {
        ScheduleEntry entry = new Task();
        IWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(entry, provider);

        Schedule schedule = new Schedule();
        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        // We use a custom working time calendar provider that always
        // returns the default working time calendar
        schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));
        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);

        entry.setName("Test Work Complete");
        entry.setStartTimeD(new Date());
        entry.setEndTimeD(new Date());
        entry.setPlanID(WellKnownObjects.TEST_PLAN_ID);
        entry.setPriority(TaskPriority.PRIORITY_NORMAL.getID());
        entry.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
        entry.setTaskCalculationType(schedule.getDefaultTaskCalculationType());

        calc.workChanged(new TimeQuantity(20, TimeQuantityUnit.HOUR));
        calc.workCompleteChanged(new TimeQuantity(10, TimeQuantityUnit.HOUR));

        entry.store(false, schedule);
        String taskID = entry.getID();

        //Now that we have a schedule entry, let's load it up and make sure that
        //everything that is supposed to be there is there.
        TaskFinder finder = new TaskFinder();
        calc = new ScheduleEntryCalculator(entry, provider);

        assertEquals(new TimeQuantity(10, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());
        assertEquals(new TimeQuantity(10, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        assertEquals(0, entry.getAssignmentList().size());

        //Now add an assignment and make sure that the unallocated work is
        //distributed correctly
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        assignment.setObjectID(taskID);
        assignment.setSpaceID(ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID);
        assignment.setPercentAssigned(100);

        calc.assignmentAdded(assignment.getPercentAssignedDecimal(), assignment);

        assertEquals(new TimeQuantity(20, TimeQuantityUnit.HOUR), assignment.getWork());
        //sjmittal: work complete only comes from actual log of work and not from adding an assignment
        //to the task and deriving from task's work complte
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assignment.getWorkComplete());
        //sjmittal: the unallocated work moves to task now
        assertEquals(new TimeQuantity(10, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());

        entry.store(false, schedule);

        //Finally, remove the assignment and make sure everything works.
        // Note that a recent change ensures that task work remains equal to the last
        // assignment's work after removal
        calc.assignmentRemoved(assignment);

        assertEquals(new TimeQuantity(20, TimeQuantityUnit.HOUR), entry.getWorkTQ());
        //This value changed recently, though this is correct.  Even though we removed
        //the assignment there is still work that has been completed.
        assertEquals(new TimeQuantity(10, TimeQuantityUnit.HOUR), entry.getWorkCompleteTQ());
        assertEquals(new TimeQuantity(10, TimeQuantityUnit.HOUR), entry.getUnallocatedWorkComplete());

        entry.store(false, schedule);
    }
}
