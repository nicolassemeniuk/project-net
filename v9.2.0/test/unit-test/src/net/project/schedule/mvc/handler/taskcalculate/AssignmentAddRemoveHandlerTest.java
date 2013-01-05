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
 package net.project.schedule.mvc.handler.taskcalculate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.Module;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.mockobjects.MockHttpServletResponse;
import net.project.resource.AssignmentRoster;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.Action;
import net.project.test.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link AssignmentAddRemoveHandler}.
 * 
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentAddRemoveHandlerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentAddRemoveHandlerTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentAddRemoveHandlerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link AssignmentAddRemoveHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     * by adding an assignment.
     */
    public void testHandleRequestAdd() throws Exception {

        HttpServletRequest request;
        HttpServletResponse response;

        Schedule schedule;
        ScheduleEntry scheduleEntry;
        String resourceID;
        AssignmentRoster assignmentRoster;

        schedule = new Schedule();
        schedule.setWorkingTimeCalendarProvider(new TestWorkingTimeCalendarProvider());
        schedule.setID("11");

        scheduleEntry = new Task();
        scheduleEntry.setID("1");
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        scheduleEntry.setPlanID(schedule.getID());
        scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.calculateEndDate(schedule.getWorkingTimeCalendarProvider());
        // Assignment we're adding
        resourceID = "100";

        assignmentRoster = new AssignmentRoster();

        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/AssignmentAddRemove?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                resourceID,
                true,
                assignmentRoster);

        // Check model contents
        Map model = new AssignmentAddRemoveHandler(request).handleRequest(request, response);

        // Model entry is same as specifed
        ScheduleEntry modelEntry = (ScheduleEntry) model.get("scheduleEntry");
        assertEquals(scheduleEntry.getID(), modelEntry.getID());
        // Entry contains the assignment we added
        assertTrue(scheduleEntry.getAssignmentList().getAssignmentMap().containsKey(resourceID));

        assertNotNull(model.get("assignmentRoster"));

        assertTrue(((Map) model.get("oldAssignmentPercentages")).isEmpty());

        // Max allocation map contains entry for assignment
        assertTrue(((Map) model.get("maxAllocationMap")).get(resourceID).equals(new BigDecimal("0")));

    }

    /**
     * Tests {@link AssignmentAddRemoveHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     * by removing an assignment.
     */
    public void testHandleRequestRemove() throws Exception {

        HttpServletRequest request;
        HttpServletResponse response;

        Schedule schedule;
        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        AssignmentRoster assignmentRoster;

        schedule = new Schedule();
        schedule.setWorkingTimeCalendarProvider(new TestWorkingTimeCalendarProvider());
        schedule.setID("11");

        scheduleEntry = new Task();
        scheduleEntry.setID("1");
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        scheduleEntry.setPlanID(schedule.getID());

        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("100");
        scheduleEntry.addAssignments(Collections.singleton(assignment));

        assignmentRoster = new AssignmentRoster();

        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/AssignmentAddRemove?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                assignment.getPersonID(),
                false,
                assignmentRoster);

        // Check model contents
        Map model = new AssignmentAddRemoveHandler(request).handleRequest(request, response);

        // Model entry is same as specifed
        ScheduleEntry modelEntry = (ScheduleEntry) model.get("scheduleEntry");
        assertEquals(scheduleEntry.getID(), modelEntry.getID());

        // Assignments are empty
        assertTrue(scheduleEntry.getAssignments().isEmpty());

        assertNotNull(model.get("assignmentRoster"));

        // Old assignments contains assignment just removed
        assertTrue(((Map) model.get("oldAssignmentPercentages")).containsKey(assignment.getPersonID()));

        // Max allocation map contains entry for assignment
        assertTrue(((Map) model.get("maxAllocationMap")).get(assignment.getPersonID()).equals(new BigDecimal("0")));

    }

    /**
     * Helper method to create an HttpServletRequest suitable for handling by
     * the AssignmentAddRemoveHandler.
     * <p>
     * The request includes a <code>max_alloc_value_ ...</code> parameter for the specified
     * resourceID of <code>0</code>.
     * </p>
     * @param baseURL the base URL (including handler, security parameters) to put in the request
     * @param schedule the schedule to put in session
     * @param scheduleEntry the schedule entry to put in session
     * @param resourceID the id of the assignment being added/removed
     * @param isAdd true if the assignment is to be added, false if to be removed
     * @param assignmentRoster the assignment roster to put in session
     * @return a request with a populated session
     */
    private static HttpServletRequest constructRequest(String baseURL, Schedule schedule, ScheduleEntry scheduleEntry, String resourceID, boolean isAdd, AssignmentRoster assignmentRoster) {

        MockHttpServletRequest request = Application.requestPage(baseURL +
                "&id=" + scheduleEntry.getID() +
                "&resourceID=" + resourceID +
                "&mode=" + (isAdd ? "add" : "remove") +
                "&max_alloc_value_" + resourceID + "=0&timeZoneId="+TimeZone.getDefault().getID()
        );

        request.getSession().setAttribute("schedule", schedule);
        request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        request.getSession().setAttribute("assignmentRoster", assignmentRoster);

        return request;
    }

}
