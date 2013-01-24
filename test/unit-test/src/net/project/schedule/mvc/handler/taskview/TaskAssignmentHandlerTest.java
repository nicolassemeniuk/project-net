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
|   $Revision: 17102 $
|       $Date: 2008-03-25 16:17:07 +0530 (Tue, 25 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskview;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.Module;
import net.project.database.DBBean;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.mockobjects.MockHttpServletResponse;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;
import net.project.schedule.mvc.ScheduleController;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;

public class TaskAssignmentHandlerTest extends TestCase {
    /**
     * This is the ID of the task that we are going to use for our test.  This
     * will be the earliest task from the database which we are going to find
     * during the setup of this unit test.
     */
    private String taskID;
    private String secondTaskID;
    /**
     * This is the id of the space that corresponds to the {@link #taskID}
     * parameter.
     */
    private String spaceID;
    private Space taskSpace;

    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public TaskAssignmentHandlerTest(String s) {
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
        TestSuite suite = new TestSuite(TaskViewHandlerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.registerServlet("/servlet/ScheduleController");
        Application.login();

        //Find the task id we are going to pick on.
        setupTaskAndSpaceID();
    }

    private void setupTaskAndSpaceID() {
        DBBean db = new DBBean();
        try {
            db.executeQuery("select min(task_id) as task_id, max(task_id) as task_id2 from pn_task where record_status = 'A'");
            if (db.result.next()) {
                taskID = db.result.getString("task_id");
                secondTaskID = db.result.getString("task_id2");

                if (taskID.equals(secondTaskID)) {
                    throw new RuntimeException("There was only one task in the " +
                        "database.  We need more than this for this unit test to" +
                        "run.  (Run the TaskTest unit test a couple of times and " +
                        "that will suffice.)");
                }

                //Now find the space id that corresponds to this task
                TaskFinder tf = new TaskFinder();
                ScheduleEntry se = (ScheduleEntry)tf.findByID(taskID).get(0);

                spaceID = se.getSpaceID();
                taskSpace = SpaceFactory.constructSpaceFromID(spaceID);
            } else {
                taskID = "";
                spaceID = "";
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to setup task and space id: "+e);
        } finally {
            db.release();
        }
    }

    public void testScheduleEntryLoaded() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = Application.requestPage(
            "/servlet/ScheduleController/TaskView/Assignments?action="+Action.VIEW+"&module="+
            Module.SCHEDULE+"&id="+taskID
        );
        SessionManager.getUser().setAuthenticated(true);
        SessionManager.getUser().setCurrentSpace(taskSpace);

        //Set up a schedule entry which isn't pointed to the correct id
        TaskFinder tf = new TaskFinder();
        ScheduleEntry se = (ScheduleEntry)tf.findByID(secondTaskID).get(0);
        request.getSession().setAttribute("scheduleEntry", se);

        //Run the request
        ScheduleController controller = new ScheduleController();
        controller.controlRequest(request, response);

        //Now make sure that the correct id is loaded and put into the session
        ScheduleEntry currentEntryInSession = (ScheduleEntry)request.getSession().getAttribute("scheduleEntry");
        assertNotNull(currentEntryInSession);
        assertEquals(taskID, currentEntryInSession.getID());
    }
}
