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
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link AssignmentModifyWorkHandler}.
 * 
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentModifyWorkHandlerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentModifyWorkHandlerTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentModifyWorkHandlerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link AssignmentModifyWorkHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * <p>
     * Note: In this test we are not testing the underlying calculation, mainly just that the request parameters
     * are processed and the model is updated correctly.
     * </p>
     */
    public void testHandleRequest() throws Exception {

        HttpServletRequest request;
        HttpServletResponse response;

        Schedule schedule;
        ScheduleEntry scheduleEntry;
        ScheduleEntryAssignment assignment;
        AssignmentRoster assignmentRoster;
        String modifiedWorkAmount;
        String modifiedWorkUnitsID;

        schedule = new Schedule();
        schedule.setWorkingTimeCalendarProvider(new TestWorkingTimeCalendarProvider());
        schedule.setID("11");

        scheduleEntry = new Task();
        scheduleEntry.setID("1");
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        scheduleEntry.setPlanID(schedule.getID());

        assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("100");
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        scheduleEntry.addAssignments(Collections.singleton(assignment));

        assignmentRoster = new AssignmentRoster();

        // Setting percentage to 100%
        modifiedWorkAmount = "8";
        modifiedWorkUnitsID = String.valueOf(TimeQuantityUnit.HOUR.getUniqueID());

        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/AssignmentModifyWork?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                assignment.getPersonID(),
                modifiedWorkAmount,
                modifiedWorkUnitsID,
                assignmentRoster);

        // Check model contents
        Map model = new AssignmentModifyWorkHandler(request).handleRequest(request, response);

        // Model entry is same as specifed
        ScheduleEntry modelEntry = (ScheduleEntry) model.get("scheduleEntry");
        assertEquals(scheduleEntry.getID(), modelEntry.getID());

        // Entry contains the assignment we added
        assertTrue(scheduleEntry.getAssignmentList().getAssignmentMap().containsKey(assignment.getPersonID()));

        assertNotNull(model.get("assignmentRoster"));

        // Old assignments contains assignment modified
        assertTrue(((Map) model.get("oldAssignmentPercentages")).containsKey(assignment.getPersonID()));

        // Max allocation map contains entry for assignment
        assertTrue(((Map) model.get("maxAllocationMap")).get(assignment.getPersonID()).equals(new BigDecimal("0")));

    }

    /**
     * Helper method to create an HttpServletRequest suitable for handling by
     * the AssignmentModifyPercentHandler.
     * <p>
     * The request includes a <code>max_alloc_value_ ...</code> parameter for the specified
     * resourceID of <code>0</code>.
     * </p>
     * @param baseURL the base URL (including handler, security parameters) to put in the request
     * @param schedule the schedule to put in session
     * @param scheduleEntry the schedule entry to put in session
     * @param resourceID the id of the assignment being added/removed
     * @param workAmount the amount of work
     * @param workUnitsID the id of the selected work units
     * @param assignmentRoster the assignment roster to put in session
     * @return a request with a populated session
     */
    private static HttpServletRequest constructRequest(String baseURL, Schedule schedule, ScheduleEntry scheduleEntry, String resourceID, String workAmount, String workUnitsID, AssignmentRoster assignmentRoster) {

        MockHttpServletRequest request = Application.requestPage(baseURL +
                "&id=" + scheduleEntry.getID() +
                "&resourceID=" + resourceID +
                "&workAmount=" + workAmount +
                "&workUnitsID=" + workUnitsID +
                "&max_alloc_value_" + resourceID + "=0&timeZoneId="+TimeZone.getDefault().getID()
        );

        request.getSession().setAttribute("schedule", schedule);
        request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        request.getSession().setAttribute("assignmentRoster", assignmentRoster);

        return request;
    }

}
