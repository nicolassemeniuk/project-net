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

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.Module;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.mockobjects.MockHttpServletResponse;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.Action;
import net.project.test.util.DateUtils;
import net.project.util.DateFormat;
import net.project.util.ErrorReporter;

/**
 * Tests {@link DateChangeHandler}.
 * 
 * @author Tim Morrow
 */
public class DateChangeHandlerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public DateChangeHandlerTest(String s) {
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
        TestSuite suite = new TestSuite(DateChangeHandlerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link DateChangeHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     */
    public void testHandleRequest() throws Exception {

        HttpServletRequest request;
        HttpServletResponse response;
        Schedule schedule;
        ScheduleEntry scheduleEntry;
        Map model;

        schedule = new Schedule();
        schedule.setWorkingTimeCalendarProvider(new TestWorkingTimeCalendarProvider());
        schedule.setID("11");

        scheduleEntry = new Task();
        scheduleEntry.setID("1");
        scheduleEntry.setPlanID(schedule.getID());

        //
        // Missing date parameters
        //
        //try {
            response = new MockHttpServletResponse();
            request = constructRequest(
                    "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                    schedule,
                    scheduleEntry,
                    null,
                    null);

            model = new DateChangeHandler(request).handleRequest(request, response);
            //sjmittal: now error reporter is set
            assertTrue(((ErrorReporter) model.get("errorReporter")).errorsFound());
         //   fail("Expected ControllerException");
        //} catch (ControllerException e) {
            // Expected
        //}

        //
        // Both date parameters specified
        //
        //try {
            response = new MockHttpServletResponse();
            request = constructRequest(
                    "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                    schedule,
                    scheduleEntry,
                    new Date(),
                    new Date());

            model = new DateChangeHandler(request).handleRequest(request, response);
            //sjmittal: now error reporter is set
            assertTrue(((ErrorReporter) model.get("errorReporter")).errorsFound());
          //  fail("Expected ControllerException");
        //} catch (ControllerException e) {
            // Expected
        //}

        //
        // Start Date
        //
        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/DateChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                DateUtils.parseDate("06/07/04"),
                DateUtils.parseDate("06/08/04"));

        model = new DateChangeHandler(request).handleRequest(request, response);
        assertEquals(TaskConstraintType.START_NO_EARLIER_THAN, ((ScheduleEntry) model.get("scheduleEntry")).getConstraintType());
        assertEquals(DateUtils.parseDateTime("06/07/04 8:00 AM"), ((ScheduleEntry) model.get("scheduleEntry")).getConstraintDate());

        //
        // End Date
        //
        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/DateChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                null,
                DateUtils.parseDate("06/07/04"));

        model = new DateChangeHandler(request).handleRequest(request, response);
        assertEquals(TaskConstraintType.FINISH_NO_EARLIER_THAN, ((ScheduleEntry) model.get("scheduleEntry")).getConstraintType());
        assertEquals(DateUtils.parseDateTime("06/07/04 5:00 PM"), ((ScheduleEntry) model.get("scheduleEntry")).getConstraintDate());
    }

    
    /**
     * Helper method to create an HttpServletRequest suitable for handling by
     * the DateChangeHandler.
     * @param baseURL the base URL (including handler, security parameters) to put in the request
     * @param schedule the schedule to put in session
     * @param scheduleEntry the schedule entry to put in session
     * @param startDate the start date
     * @param endDate the end date
     * @return a request with a populated session
     */
    private static HttpServletRequest constructRequest(String baseURL, Schedule schedule, ScheduleEntry scheduleEntry,
            Date startDate, Date endDate) {

        String parameters = "";

        if (startDate != null) {
            parameters += "&startDateString=" + DateFormat.getInstance().formatDate(startDate);
        }

        if (endDate != null) {
            parameters += "&endDateString=" + DateFormat.getInstance().formatDate(endDate);
        }

        MockHttpServletRequest request = Application.requestPage(baseURL +
                "&id=" + scheduleEntry.getID() +
                "&taskCalculationTypeID=" + TaskCalculationType.FixedElement.UNIT.getID() +
                "&effortDriven=" + Boolean.TRUE +
                parameters
        );

        request.getSession().setAttribute("schedule", schedule);
        request.getSession().setAttribute("scheduleEntry", scheduleEntry);

        return request;
    }
    
}
