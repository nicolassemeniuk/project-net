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
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.security.User;

/**
 * Tests {@link DefinitionBasedWorkingTimeCalendar}.
 */
public class DefinitionBasedWorkingTimeCalendarTest extends TestCase {
    public DefinitionBasedWorkingTimeCalendarTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DefinitionBasedWorkingTimeCalendarTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    private static User makeUser() {
        User user = new User();
        user.setTimeZone(TimeZone.getTimeZone("PST"));
        user.setLocale(Locale.US);
        return user;
    }

    /**
     * Tests {@link DefinitionBasedWorkingTimeCalendar#ensureWorkingTimeStart}.
     */
    public void testEnsureWorkingTimeStart() throws NoWorkingTimeException {

        try {
            // NOTE:
            // Remember that months start at 0

            User user = makeUser();
            // Reset seconds and milliseconds to zero for all dates and times
            PnCalendar cal = new PnCalendar(user);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date expectedDate;
            Date inputDate;

            //
            // Default Calendar
            //
            IWorkingTimeCalendar workingTimeCal = makeCalendar();

            // Working time returned unmodified
            // Input / Expected: Tuesday May 6th @ 2:30 PM (working time)
            cal.set(2003, 4, 6, 14, 30);
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Non-working time before first working time
            // Input:  Tuesday May 6th @ 7:59 AM (non-working time)
            // Expect: Tuesday May 6th @ 8:00 AM
            cal.set(2003, 4, 6, 7, 59);
            inputDate = cal.getTime();
            cal.set(2003, 4, 6, 8, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Non-working time after last working time
            // Input:  Tuesday May 6th @ 5:01 PM (non-working time)
            // Expect: Wednesday May 7th @ 8:00 AM
            cal.set(2003, 4, 6, 17, 01);
            inputDate = cal.getTime();
            cal.set(2003, 4, 7, 8, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Non-working time on Saturday
            // Input:  Saturday May 3rd @ 12:00 PM (non-working time)
            // Expect: Monday May 5th @ 8:00 AM
            cal.set(2003, 4, 3, 12, 00);
            inputDate = cal.getTime();
            cal.set(2003, 4, 5, 8, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Non-working time on Friday after last working time
            // Input:  Friday May 2nd @ 5:00 PM (non-working time)
            // Expect: Monday May 5th @ 8:00 AM
            cal.set(2003, 4, 2, 17, 00);
            inputDate = cal.getTime();
            cal.set(2003, 4, 5, 8, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Non-working time on Wednesday between working times
            // Input:  Wednesday May 7th @ 12:30 PM (non-working time)
            // Expect: Wednesday May 7th @ 1:00 PM
            cal.set(2003, 4, 7, 12, 30);
            inputDate = cal.getTime();
            cal.set(2003, 4, 7, 13, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            //
            // Custom Calendar
            //
            // Sunday and Monday are non working days
            // Tuesday 17th June 2003 and Wednesday 18th June 2003 are non working days
            // Sunday 22nd June 2003 and Monday 23rd June 2003 are working days
            WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                    new int[]{Calendar.SUNDAY, Calendar.MONDAY},
                    new String[]{"6/17/03", "6/18/03"},
                    new String[]{"6/22/03", "6/23/03"});

            workingTimeCal = makeCalendar(calendarDef);

            // Start Date: Sunday 15th June @ 9:00 AM
            // Expected: Thursday 19th June @ 8:00 AM
            cal.set(2003, 5, 15, 9, 0);
            inputDate = cal.getTime();
            cal.set(2003, 5, 19, 8, 0);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Start Date: Wednesday 18th June @ 3:00 PM
            // Expected: Thursday 19th June @ 8:00 AM
            cal.set(2003, 5, 18, 15, 0);
            inputDate = cal.getTime();
            cal.set(2003, 5, 19, 8, 0);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

            // Start Date: Sunday 22nd June @ 9:00 AM
            // Expected: Sunday 22nd June @ 9:00 AM (unchanged)
            cal.set(2003, 5, 22, 9, 0);
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        } catch (NoWorkingTimeException e) {
            fail("Unhandled exception: " + e);

        }

    }

    public void testEnsureWorkingTimeStartNonDefaultTimes() throws NoWorkingTimeException {

        User user = makeUser();
        // Reset seconds and milliseconds to zero for all dates and times
        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;
        Date inputDate;
        IWorkingTimeCalendar workingTimeCal;

        // 24 hour calendar
        workingTimeCal = makeCalendar(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition());

        // All times are working times
        // Loop minute by minute from Sunday 7th September @ 0:00 for 1 week
        cal.set(2003, Calendar.SEPTEMBER, 7, 0, 0);
        for (int i = 0; i <= (60 * 24 * 7); i++) {
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
            cal.add(Calendar.MINUTE, 1);
        }

        // Nightshift calendar
        workingTimeCal = makeCalendar(WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition());

        // Input:    Sunday @ 12:00 AM
        // Expected: Monday @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 7, 0, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 8, 23, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Monday @ 12:00 AM
        // Expected: Monday @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 8, 0, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 8, 23, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Monday @ 11:00 PM
        // Expected: Monday @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 8, 23, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 8, 23, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Tuesday @ 12:00 AM
        // Expected: Tuesday @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 9, 0, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 9, 0, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Tuesday @ 3:00 AM
        // Expected: Tuesday @ 4:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 9, 3, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 9, 4, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Tuesday @ 7:30 AM
        // Expected: Tuesday @ 7:30 AM
        cal.set(2003, Calendar.SEPTEMBER, 9, 7, 30);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 9, 7, 30);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Tuesday @ 8:00 AM
        // Expected: Tuesday @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 9, 8, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 9, 23, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

        // Input:    Saturday @ 8:00 AM
        // Expected: Monday @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 13, 8, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));

    }

    /**
     * Tests {@link DefinitionBasedWorkingTimeCalendar#ensureWorkingTimeEnd}.
     */
    public void testEnsureWorkingTimeEnd() {

        try {
            // NOTE:
            // Remember that months start at 0

            User user = makeUser();
            IWorkingTimeCalendar workingTimeCal;
            PnCalendar cal = new PnCalendar(user);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date expectedDate;
            Date inputDate;

            //
            // Default Calendar
            //

            workingTimeCal = makeCalendar();

            // Working time returned unmodified
            // Input / Expected: Tuesday May 6th @ 2:30 PM (working time)
            cal.set(2003, 4, 6, 14, 30);
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Non-working time after working time
            // Input:  Tuesday May 6th @ 5:01 PM (non-working time)
            // Expect: Tuesday May 6th @ 5:00 PM
            cal.set(2003, 4, 6, 17, 01);
            inputDate = cal.getTime();
            cal.set(2003, 4, 6, 17, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Non-working time before first working time
            // Input:  Tuesday May 6th @ 7:59 AM (non-working time)
            // Expect: Monday May 5th @ 5:00 PM
            cal.set(2003, 4, 6, 7, 59);
            inputDate = cal.getTime();
            cal.set(2003, 4, 5, 17, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Non-working time on Sunday
            // Input:  Sunday May 4th @ 12:00 PM (non-working day)
            // Expect: Friday May 2nd @ 5:00 PM
            cal.set(2003, 4, 4, 12, 00);
            inputDate = cal.getTime();
            cal.set(2003, 4, 2, 17, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Non-working time on Monday before first working time
            // Input:  Monday May 5th @ 7:59 AM (non-working time)
            // Expect: Friday May 2nd @ 5:00 PM
            cal.set(2003, 4, 5, 7, 59);
            inputDate = cal.getTime();
            cal.set(2003, 4, 2, 17, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Non-working time on Wednesday between working times
            // Input:  Wednesday May 7th @ 12:30 PM (non-working time)
            // Expect: Wednesday May 7th @ 12:00 PM
            cal.set(2003, 4, 7, 12, 30);
            inputDate = cal.getTime();
            cal.set(2003, 4, 7, 12, 00);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            //
            // Custom Calendar
            //
            // Sunday and Monday are non working days
            // Tuesday 17th June 2003 and Wednesday 18th June 2003 are non working days
            // Sunday 22nd June 2003 and Monday 23rd June 2003 are working days
            WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                    new int[]{Calendar.SUNDAY, Calendar.MONDAY},
                    new String[]{"6/17/03", "6/18/03"},
                    new String[]{"6/22/03", "6/23/03"});

            workingTimeCal = makeCalendar(calendarDef);

            // Start Date: Thursday 19th June @ 8:00 AM
            // Expected: Saturday 14th June @ 5:00 PM
            cal.set(2003, 5, 19, 8, 0);
            inputDate = cal.getTime();
            cal.set(2003, 5, 14, 17, 0);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Start Date: Monday 16th June @ 2:00 PM
            // Expected: Saturday 14th June @ 5:00 PM
            cal.set(2003, 5, 16, 14, 0);
            inputDate = cal.getTime();
            cal.set(2003, 5, 14, 17, 0);
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

            // Start Date: Monday 23rd June @ 12:00 PM
            // Expected: Monday 23rd June @ 12:00 PM (unchanged)
            cal.set(2003, 5, 23, 12, 0);
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        } catch (NoWorkingTimeException e) {
            fail("Unhandled exception: " + e);

        }

    }

    public void testEnsureWorkingTimeEndNonDefaultTimes() throws NoWorkingTimeException {

        User user = makeUser();
        IWorkingTimeCalendar workingTimeCal;
        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;
        Date inputDate;

        //
        // 24 hour Calendar
        //
        workingTimeCal = makeCalendar(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition());

        // All times are working times
        // Loop minute by minute from Sunday 7th September @ 0:00 for 1 week
        cal.set(2003, Calendar.SEPTEMBER, 7, 0, 0);
        for (int i = 0; i <= (60 * 24 * 7); i++) {
            inputDate = cal.getTime();
            expectedDate = cal.getTime();
            assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));
            cal.add(Calendar.MINUTE, 1);
        }

        // Nightshift calendar
        workingTimeCal = makeCalendar(WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition());

        // Input:    Sunday @ 12:00 AM
        // Expected: Saturday @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 14, 0, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 13, 8, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        // Input:    Saturday @ 8:00 AM
        // Expected: Saturday @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 13, 8, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 13, 8, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        // Input:    Saturday @ 4:00 AM
        // Expected: Saturday @ 3:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 13, 4, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 13, 3, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        // Input:    Saturday @ 0:00 AM
        // Expected: Saturday @ 0:00 AM
        // Despite being the start of working time, it is also the end of
        // working time from the previous day
        cal.set(2003, Calendar.SEPTEMBER, 13, 0, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 13, 0, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        // Input:    Friday @ 11:00 PM
        // Expected: Friday @ 8:00 AM
        // Despite being the start of working time, it is also the end of
        // working time from the previous day
        cal.set(2003, Calendar.SEPTEMBER, 12, 23, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 12, 8, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));

        // Input:    Monday @ 11:00 PM
        // Expected: Saturday @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        inputDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 13, 8, 0);
        expectedDate = cal.getTime();
        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeEnd(inputDate));


    }

    /**
     * Tests the getStartOfNextWorkingTime() method.
     */
    public void testGetStartOfNextWorkingTime() throws NoWorkingTimeException {

        PnCalendar cal = new PnCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(PnCalendar.MILLISECOND, 0);
        Date startDate, expected;
        IWorkingTimeCalendar resourceCal;

        //
        // Default Calendar
        //

        resourceCal = makeCalendar();

        //Start Date: Monday Jan 6 2003 5:00 pm
        //Expected: Tuesday Jan 7 2003 8:00 am
        cal.set(2003, 0, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 7, 8, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getStartOfNextWorkingTime(startDate));

        //Start Date: Friday Jan 10 2003 5:00 pm
        //Expected: Monday Jan 13 2003 8:00 am
        cal.set(2003, 0, 10, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 13, 8, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getStartOfNextWorkingTime(startDate));

        //StartDate: Sunday Jan 12 2003 5:00 pm
        //Expected: Monday Jan 13 2003 8:00 pm
        cal.set(2003, 0, 12, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 13, 8, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getStartOfNextWorkingTime(startDate));

        //
        // Custom Calendar
        //
        // Sunday and Monday are non working days
        // Tuesday 17th June 2003 and Wednesday 18th June 2003 are non working days
        // Sunday 22nd June 2003 and Monday 23rd June 2003 are working days
        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY},
                new String[]{"6/17/03", "6/18/03"},
                new String[]{"6/22/03", "6/23/03"});

        resourceCal = makeCalendar(calendarDef);

        // Start Date: Sunday June 15th @ 1:00 AM
        // Expected: Thursday June 19th @ 8:00 AM
        cal.set(2003, 5, 15, 1, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 19, 8, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getStartOfNextWorkingTime(startDate));

        // Start Date: Sunday June 22nd @ 12:30 PM
        // Expected: Sunday June 22nd @ 1:00 PM
        cal.set(2003, 5, 22, 12, 30);
        startDate = cal.getTime();
        cal.set(2003, 5, 22, 13, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getStartOfNextWorkingTime(startDate));

    }

    public void testGetStartOfNextWorkingTimeNonDefaultTimes() throws NoWorkingTimeException {
        // TODO: Write Unit Test
    }

    /**
     * Tests the getEndOfPreviousWorkingTime() method.
     */
    public void testGetEndOfPreviousWorkingTime() throws NoWorkingTimeException {

        PnCalendar cal = new PnCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(PnCalendar.MILLISECOND, 0);
        Date startDate, expected;
        IWorkingTimeCalendar resourceCal;

        resourceCal = makeCalendar();

        //
        // Default Calendar
        //

        //Start Date: Tuesday Jan 7 2003 8:00 am
        //Expected: Monday Jan 6 2003 5:00 pm
        cal.set(2003, 0, 7, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 6, 17, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getEndOfPreviousWorkingTime(startDate));

        //Start Date: Monday: Jan 13 2003 8:00 am
        //Expected: Friday: Jan 10 2003 5:00 pm
        cal.set(2003, 0, 13, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 0, 10, 17, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getEndOfPreviousWorkingTime(startDate));

        //
        // Custom Calendar
        //
        // Sunday and Monday are non working days
        // Tuesday 17th June 2003 and Wednesday 18th June 2003 are non working days
        // Sunday 22nd June 2003 and Monday 23rd June 2003 are working days
        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY},
                new String[]{"6/17/03", "6/18/03"},
                new String[]{"6/22/03", "6/23/03"});

        resourceCal = makeCalendar(calendarDef);

        // Start Date: Thursday June 19th @ 8:00 AM
        // Expected: Saturday June 14th @ 5:00 PM
        cal.set(2003, 5, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 14, 17, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getEndOfPreviousWorkingTime(startDate));

        // Start Date: Monday June 23rd @ 12:30 PM
        // Expected: Monday June 23rd @ 12:00 PM
        cal.set(2003, 5, 23, 12, 30);
        startDate = cal.getTime();
        cal.set(2003, 5, 23, 12, 0);
        expected = cal.getTime();
        assertEquals(expected, resourceCal.getEndOfPreviousWorkingTime(startDate));

    }

    public void testGetEndOfPreviousWorkingTimeNonDefaultTimes() throws NoWorkingTimeException {
        // TODO: Write Unit Test
    }

    /**
     * Tests getStartOfNextWorkingDay when there is no working time in the calendar.
     */
    public void testGetStartOfNextWorkingDayNoWorkingDayOfWeek() {

        WorkingTimeCalendarDefinition calendarDef;
        IWorkingTimeCalendar scheduleCal;

        //
        // No day of week, no dates
        //
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                (String[]) null,
                (String[]) null);

        scheduleCal = makeCalendar(calendarDef);

        // With no calendar, same date should be returned
        Date date = new Date();
        assertEquals(date, scheduleCal.getStartOfNextWorkingDay(date));

        //
        // No day of week, one date is a working day
        //
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                null,
                new String[]{"08/19/03"});

        scheduleCal = makeCalendar(calendarDef);
        date = WorkingTimeCalendarDefinitionTest.makeDateTime("08/18/03 5:00 PM");
        Date expected = WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 8:00 AM");
        assertEquals(expected, scheduleCal.getStartOfNextWorkingDay(date));
    }

    public void testGetStartOfNextWorkingDayNonDefaultTimes() {
        // TODO: Write Unit Test
    }

    //
    // Helper methods
    //

    /**
     * Makes a calendar definition based on the default working time calendar definition.
     * @return the working time calendar
     */
    private static IWorkingTimeCalendar makeCalendar() {
        return makeCalendar(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());
    }

    /**
     * Makes a calendar definition based on the specified working time calendar definition.
     * @param calendarDef the calendar definition to use for the calendar
     * @return the working time calendar
     */
    private static IWorkingTimeCalendar makeCalendar(WorkingTimeCalendarDefinition calendarDef) {
        IWorkingTimeCalendarProvider provider = TestWorkingTimeCalendarProvider.make(calendarDef);
        return new DefinitionBasedWorkingTimeCalendar(provider.getDefaultTimeZone(), provider.getDefault());
    }
}
