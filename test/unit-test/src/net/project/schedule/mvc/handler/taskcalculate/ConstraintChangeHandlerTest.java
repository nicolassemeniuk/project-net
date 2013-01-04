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

import java.util.Calendar;
import java.util.Date;
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
 * Tests {@link ConstraintChangeHandler}.
 * 
 * @author Tim Morrow
 */
public class ConstraintChangeHandlerTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ConstraintChangeHandlerTest(String s) {
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
        TestSuite suite = new TestSuite(ConstraintChangeHandlerTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ConstraintChangeHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     */
    public void testHandleRequest() throws Exception {

        HttpServletRequest request;
        HttpServletResponse response;
        Schedule schedule;
        ScheduleEntry scheduleEntry;
        Map model;

        schedule = new Schedule();
        schedule.setID("11");
        schedule.setWorkingTimeCalendarProvider(new TestWorkingTimeCalendarProvider());

        scheduleEntry = new Task();
        scheduleEntry.setID("1");
        scheduleEntry.setPlanID(schedule.getID());

        //
        // Missing constraint Type
        //
        //try {
            response = new MockHttpServletResponse();
            request = constructRequest(
                    "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                    schedule,
                    scheduleEntry,
                    null,
                    null,
                    0, 0);

            model = new ConstraintChangeHandler(request).handleRequest(request, response);
            //sjmittal: now error reporter is set
            assertTrue(((ErrorReporter) model.get("errorReporter")).errorsFound());
          //  fail("Expected ControllerException");
        //} catch (ControllerException e) {
            // Expected
        //}

        //
        // Non-ASAP constraint, missing date
        //
        //try {
            response = new MockHttpServletResponse();
            request = constructRequest(
                    "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                    schedule,
                    scheduleEntry,
                    TaskConstraintType.START_NO_EARLIER_THAN,
                    null,
                    0, 0);

            model = new ConstraintChangeHandler(request).handleRequest(request, response);
            //sjmittal: now error reporter is set
            assertTrue(((ErrorReporter) model.get("errorReporter")).errorsFound());
           // fail("Expected ControllerException");
        //} catch (ControllerException e) {
            // Expected
        //}


        //
        // ASAP constraint
        //
        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                TaskConstraintType.AS_SOON_AS_POSSIBLE,
                new Date(),
                0, 0);

        model = new ConstraintChangeHandler(request).handleRequest(request, response);
        assertEquals(((ScheduleEntry) model.get("scheduleEntry")).getConstraintType(), TaskConstraintType.AS_SOON_AS_POSSIBLE);


        //
        // Non-ASAP constraint
        //
        response = new MockHttpServletResponse();
        request = constructRequest(
                "/servlet/ScheduleController/TaskCalculate/ConstraintChange?action="+Action.MODIFY+"&module=" + Module.SCHEDULE,
                schedule,
                scheduleEntry,
                TaskConstraintType.START_NO_EARLIER_THAN,
                DateUtils.parseDate("06/07/04"), 8, 0);

        model = new ConstraintChangeHandler(request).handleRequest(request, response);
        assertEquals(((ScheduleEntry) model.get("scheduleEntry")).getConstraintType(), TaskConstraintType.START_NO_EARLIER_THAN);
        assertEquals(((ScheduleEntry) model.get("scheduleEntry")).getConstraintDate(), DateUtils.parseDateTime("06/07/04 8:00 AM"));

    }

    
    /**
     * Helper method to create an HttpServletRequest suitable for handling by
     * the ConstraintChangeHandler.
     * @param baseURL the base URL (including handler, security parameters) to put in the request
     * @param schedule the schedule to put in session
     * @param scheduleEntry the schedule entry to put in session
     * @param constraintType the constraint type to set
     * @param constraintDate the constraint date (if applicable)
     * @param hour the hour value (0 - 23) required if a constraint date was specified
     * @param minute the minute value (0 - 59) required if a constraint date was specified
     * @return a request with a populated session
     */
    private static HttpServletRequest constructRequest(String baseURL, Schedule schedule, ScheduleEntry scheduleEntry,
            TaskConstraintType constraintType, Date constraintDate, int hour, int minute) {

        String constraintParameters = "";

        if (constraintType != null) {
            constraintParameters += "&constraintTypeID=" + constraintType.getID();
        }

        if (constraintDate != null) {
            constraintParameters += "&constraintDateString=" + DateFormat.getInstance().formatDate(constraintDate);
            constraintParameters += "&constraintDateTime_hour=" + hour;
            constraintParameters += "&constraintDateTime_minute=" + minute;
            constraintParameters += "&constraintDateTime_ampm=" + Calendar.AM;
            constraintParameters += "&constraintDateTime_timeZoneID=" + TimeZone.getTimeZone("America/Los_Angeles").getID();
        }

        MockHttpServletRequest request = Application.requestPage(baseURL +
                "&id=" + scheduleEntry.getID() +
                "&taskCalculationTypeID=" + TaskCalculationType.FixedElement.UNIT.getID() +
                "&effortDriven=" + Boolean.TRUE +
                constraintParameters
        );

        request.getSession().setAttribute("schedule", schedule);
        request.getSession().setAttribute("scheduleEntry", scheduleEntry);

        return request;
    }
    
}
