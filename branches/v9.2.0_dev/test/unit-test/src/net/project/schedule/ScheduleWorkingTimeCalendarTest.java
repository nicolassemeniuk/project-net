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
package net.project.schedule;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.security.User;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

public class ScheduleWorkingTimeCalendarTest extends TestCase {
    public ScheduleWorkingTimeCalendarTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleWorkingTimeCalendarTest.class);
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
     * Tests {@link ScheduleWorkingTimeCalendar#ensureWorkingTimeStart}.
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
            IWorkingTimeCalendar workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider());

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

            workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

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
        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition()));

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
        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition()));

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
     * Tests {@link ScheduleWorkingTimeCalendar#ensureWorkingTimeEnd}.
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

            workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider());

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

            workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

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
        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition()));

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
        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition()));

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
     * Tests {@link ScheduleWorkingTimeCalendar#getDuration}.
     */
    public void testGetDuration() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar workingTimeCal;
        Calendar cal = new GregorianCalendar(user.getTimeZone());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //
        // Default Calendar
        //

        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider());
        assertDurationMatchesWork(workingTimeCal, new PnCalendar(user));

        // Additional tests

        // Issue:  A calendar who's time was after the end time of every day
        // caused an infinite loop
        // Set calendar to 9 PM on a working day
        cal.set(2003, Calendar.SEPTEMBER, 9, 21, 0);
        assertEquals(new BigDecimal("1.0000000000"), workingTimeCal.getDuration(cal.getTime(), new TimeQuantity(1, TimeQuantityUnit.DAY)));

    }

    public void testGetDurationNonDefaultTimes() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar workingTimeCal;

        //
        // 24 hour Calendar
        //
        workingTimeCal = new ScheduleWorkingTimeCalendar(makeProvider(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition()));
        assertDurationMatchesWork(workingTimeCal, new PnCalendar(user));

    }

    private static void assertDurationMatchesWork(ScheduleWorkingTimeCalendar workingTimeCal, Calendar cal) {
        // For a calendar with 100% assignment, work and duration
        // are identical

        // Test a large number of dates starting on every day of week, calculating
        // duration for a range of work from 1 to 31 days
        // Start on Sunday February 2nd 2003
        cal.set(2003, 1, 2, 8, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate = cal.getTime();

        // Iterate through seven days of the week, sunday through monday.
        // For each day of the week, add work of between 1 and 30 days
        // Confirm duration matches manual duration
        for (int i = 0; i < 7; i++) {
            // Reset calendar to start date + day of week
            cal.setTime(startDate);
            cal.add(Calendar.DAY_OF_MONTH, i);

            // Add work of 1 to 31 days to current date to calculate duration
            for (int workDayValue = 1; workDayValue <= 31; workDayValue++) {
                TimeQuantity work = new TimeQuantity(new BigDecimal(workDayValue), TimeQuantityUnit.DAY);

                BigDecimal expected = work.getAmount();
                BigDecimal result = workingTimeCal.getDuration(cal.getTime(), work);

                assertTrue("Expected start date " + cal.getTime() + " with work " + work.getAmount() + " days to return " + expected + " but got " + result, expected.compareTo(result) == 0);
            }

        }
    }

    /**
     * Tests getDuration when there is no working days of week in the calendar.
     */
    public void testGetDurationNoWorkingDayOfWeek() {

        WorkingTimeCalendarDefinition calendarDef;
        ScheduleWorkingTimeCalendar scheduleCal;

        //
        // No day of week, no dates
        //
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                (String[]) null,
                (String[]) null);

        scheduleCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

        // Work 1 day
        // Expected: zero days worked
        assertEquals(new BigDecimal("0.0000000000"), scheduleCal.getDuration(new Date(), new TimeQuantity(1, TimeQuantityUnit.DAY)));


        //
        // No day of week, one date is a working day
        //
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                (String[]) null,
                new String[]{"08/19/03"});

        scheduleCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

        // Work: 2 days
        // Expected: 1 day (only 1 day available)
        assertEquals(new BigDecimal("1.0000000000"), scheduleCal.getDuration(WorkingTimeCalendarDefinitionTest.makeDateTime("08/18/03 8:00 AM"), new TimeQuantity(2, TimeQuantityUnit.DAY)));

    }

    public void testAddWorkZeroWork() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // No Resource Assignment
        //

        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 day
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 hour
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 day
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.WEEK);
        assertEquals(expected, resourceCal.addWork(startDate, work));

    }

    public void testAddWorkPositiveWork() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // No Resource Assignment
        //

        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 1 day
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday May 6th @ 1:00 PM
        // Work: 4 days
        // Expected: Monday May 12th @ 12:00 PM
        cal.set(2003, 4, 6, 13, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 12, 12, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday May 6th @ 8:00 AM
        // Work: 16 days
        // EndDate: Tuesday May 27th @ 5:00 PM
        cal.set(2003, 4, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 27, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hours
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hours
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, 4, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 6, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 2:40 PM
        // Work: 8 hours
        // Expected: Tuesday May 6th @ 2:40 PM
        cal.set(2003, 4, 5, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, 4, 6, 14, 40);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

    }

    public void testAddWorkNegativeWork() {
        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // No Resource Assignment
        //

        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: -1 day
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-1, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 12th @ 12:00 PM
        // Work: -4 days
        // Expected: Tuesday May 6th @ 1:00 PM
        cal.set(2003, 4, 12, 12, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 6, 13, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-4, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday May 27th @ 5:00 PM
        // Work: -16 days
        // EndDate: Tuesday May 6th @ 8:00 AM
        cal.set(2003, 4, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 6, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: -8 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Work: -16 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, 4, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday May 5th @ 2:40 PM
        // Work: -8 hours
        // Expected: Tuesday May 6th @ 2:40 PM
        cal.set(2003, 4, 6, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, 4, 5, 14, 40);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

    }


    /**
     * Tests addWork with every minute of an hour.  This ensures that
     * fractional hours are handled correctly. There could be problems with
     * times like 40 minutes, which is 2/3 hour or 0.66666...
     */
    public void testAddWorkEveryMinute() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // Positive Work; No Assignments
        //

        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());

        // Work: 1 hour
        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);

        for (int i=0; i < 60; i++) {
            // Start date is Monday May 5th @ 1:00 PM initially, adding 1 minute
            // each iteration
            cal.set(2003, 4, 5, 13, i);
            startDate = cal.getTime();

            // Expected date is one hour after start date
            cal.set(2003, 4, 5, 14, i);
            expected = cal.getTime();

            assertEquals(expected, resourceCal.addWork(startDate, work));
        }

        //
        // Negative Work; No Assignments
        //

        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());

        // Work: -1 hour
        work = new TimeQuantity(-1, TimeQuantityUnit.HOUR);

        for (int i = 0; i < 60; i++) {
            // Start date is Monday May 5th @ 4:00 PM initially, adding 1 minute
            // each iteration
            cal.set(2003, 4, 5, 16, i);
            startDate = cal.getTime();

            // Expected date is one hour before start date
            cal.set(2003, 4, 5, 15, i);
            expected = cal.getTime();

            assertEquals(expected, resourceCal.addWork(startDate, work));
        }

    }

    /**
     * Tests addWork() method with large work.
     */
    public void testAddWorkLargeValues() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

//        long start = System.currentTimeMillis();

        //
        // Positive Work
        //

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 365 days
        // Assignment: 100%
        // Expected:  Friday November 5th 2004 @ 5:00 PM
        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());
        cal.set(2003, 5, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 10, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(365, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 12145 days (Makes end date max value @ 12/31/49)
        // Assignment: 100%
        // Expected:  Friday December 31st 2049 @ 5:00 PM
        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());
        cal.set(2003, 5, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(12145, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday January 2nd 1984 @ 8:00 AM
        // Work: 17220 days
        // Assignment: 100%
        // Expected: Friday December 31st 2049 @ 5:00 PM
        resourceCal =  new ScheduleWorkingTimeCalendar(makeProvider());
        cal.set(1984, 0, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(17220, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

//        System.out.println("Big work total: " + (System.currentTimeMillis() - start));
    }

    /**
     * Tests addWork() with a calendar with custom day of week entries.
     */
    public void testAddWorkCustomCalendarDayOfWeek() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // Saturday, Sunday non working days
        //
        resourceCal = new ScheduleWorkingTimeCalendar(
                makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY})));

        // StartDate: Friday June 13th @ 8:00 AM
        // Work: 2 days
        // Expected:  Monday June 16th @ 5:00 PM
        cal.set(2003, 5, 13, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 16, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday June 20th @ 5:00 PM
        // Work: -6 days
        // Expected:  Friday June 13th @ 8:00 AM
        cal.set(2003, 5, 20, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 13, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-6, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        //
        // Sunday, Monday, Tuesday, Wednesday, Saturday non working days
        //
        resourceCal = new ScheduleWorkingTimeCalendar(
                makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.SATURDAY})));

        // StartDate: Sunday June 15th @ 8:00 AM
        // Work: 10 days
        // Expected:  Friday July 18th @ 5:00 PM
        cal.set(2003, 5, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 6, 18, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday June 20th @ 5:00 PM
        // Work: -3 days
        // Expected:  Friday June 13th @ 8:00 AM
        cal.set(2003, 5, 20, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 13, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-3, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        //
        // Every day non working day
        // This is a special case; work calculation is impossible
        // addWork() returns the original date in this case
        //
        resourceCal = new ScheduleWorkingTimeCalendar(
                makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY})));

        // StartDate: Sunday June 15th @ 8:00 AM
        // Work: 10 days
        // Expected:  Sunday June 15th @ 8:00 AM
        cal.set(2003, 5, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 15, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        //
        // Every day available
        //
        resourceCal = new ScheduleWorkingTimeCalendar(
                makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{})));

        // StartDate: Tuesday June 17th 2003 @ 8:00 AM
        // Work: 400 days
        // Expected:  Tuesday July 20th 2004 @ 5:00 PM
        cal.set(2003, 5, 17, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 6, 20, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(400, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday December 8th @ 5:00 PM
        // Work: -175 days
        // Expected:  Tuesday June 17th @ 8:00 AM
        cal.set(2003, 11, 8, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 17, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-175, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

    }

    /**
     * Tests addWork() with a calendar with custom date of entries.
     */
    public void testAddWorkCustomCalendarDate() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // Saturday, Sunday non working days
        // Non Working Days: June 17th 2003
        //
        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(
                WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                        new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                        new String[]{"06/17/03"}, null)));

        // StartDate: Monday June 16th @ 8:00 AM
        // Work: 2 days
        // Expected:  Wednesday June 18th @ 5:00 PM
        cal.set(2003, 5, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, 5, 18, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        //
        // Saturday, Sunday, Wednesday non working days
        // Non Working Dates: Week of Monday 23rd June - Friday 27th June; Month of December 2003
        // Working Dates: Every Wednesday in July (2, 9, 16, 23, 30); Sunday 7th September - Saturday 13th September

        Date[] nonWorkingDates = new Date[5 + 31];

        // Add 5 days from Mon 23rd June to Friday 27th June to non working days
        cal.set(2003, 5, 23, 0, 0);
        for (int i = 0; i < 5; i++) {
            nonWorkingDates[i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        // Add month of December 2003
        cal.set(2003, 11, 1, 0, 0);
        for (int i = 0; i < 31; i++) {
            nonWorkingDates[5 + i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Date[] workingDates = new Date[5 + 7];
        cal.set(2003, 6, 2, 0, 0);
        workingDates[0] = cal.getTime();
        cal.set(2003, 6, 9, 0, 0);
        workingDates[1] = cal.getTime();
        cal.set(2003, 6, 16, 0, 0);
        workingDates[2] = cal.getTime();
        cal.set(2003, 6, 23, 0, 0);
        workingDates[3] = cal.getTime();
        cal.set(2003, 6, 30, 0, 0);
        workingDates[4] = cal.getTime();

        // Add September 7th - September 13th
        cal.set(2003, 8, 7, 0, 0);
        for (int i = 0; i < 7; i++) {
            workingDates[5 + i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.WEDNESDAY, Calendar.SATURDAY},
                nonWorkingDates,
                workingDates);

        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

        // StartDate: Monday June 16th @ 8:00 AM
        // Work: 140 days
        // Expected: Monday March 15th 2004 @ 5:00 PM
        cal.set(2003, 5, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 2, 9, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(140, TimeQuantityUnit.DAY);
        assertEquals(expected, resourceCal.addWork(startDate, work));


    }

    public void testAddWorkNonDefaultTimes() {

        User user = makeUser();
        ScheduleWorkingTimeCalendar resourceCal;

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;

        //
        // 24 hour calendar
        //
        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(
                WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition()));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 48 hours
        // Expected: Friday September 19th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 16 hours
        // Expected: Friday September 17th @ 4:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 16, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: -0 hours
        // Expected: Wednesday September 17th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-0, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // Issue: 09/16/2003
        // Found an infinite loop on negative work with 24 hour calendars

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: -24 hours
        // Expected: Monday September 16th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-24, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: -16 hours
        // Expected: Tuesday September 16th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: -48 hours
        // Expected: Monday September 15th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-48, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        //
        // Nightshift calendar
        //
        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(
                WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition()));

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: 8 hours
        // Expected: Thursday September 18th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 18, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday September 15th @ 11:00 PM
        // Work: 40 hours
        // Expected: Saturday September 20th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday September 15th @ 11:00 PM
        // Work: 41 hours
        // Expected: Tuesday September 23rd @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(41, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday September 15th @ 8:00 AM
        // Work: 4 hours
        // Expected: Tuesday September 16th @ 3:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 3, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Monday September 15th @ 8:00 AM
        // Work: 5 hours
        // Expected: Tuesday September 16th @ 5:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 5, 0);
        expected = cal.getTime();
        work = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday September 19th @ 12:00 AM
        // Work: 7 hours
        // Expected: Friday September 19th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(7, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday September 19th @ 11:00 PM
        // Work: 8 hours
        // Expected: Saturday September 20th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday September 19th @ 11:00 PM
        // Work: 9 hours
        // Expected: Tuesday September 23rd @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(9, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // Negative time

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: -0 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-0, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Thursday September 18th @ 8:00 AM
        // Work: -8 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 18, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Saturday September 20th @ 8:00 AM
        // Work: -40 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-40, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday September 23rd @ 12:00 AM
        // Work: -41 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-41, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday September 16th @ 3:00 AM
        // Work: -4 hours
        // Expected: Monday September 15th @ 11:00 pm
        cal.set(2003, Calendar.SEPTEMBER, 16, 3, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-4, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday September 16th @ 5:00 AM
        // Work: -5 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 16, 5, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-5, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Friday September 19th @ 8:00 AM
        // Work: -7 hours
        // Expected: Friday September 19th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-7, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Saturday September 20th @ 8:00 AM
        // Work: -8 hours
        // Expected: Friday September 19th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

        // StartDate: Tuesday September 23rd @ 12:00 AM
        // Work: -9 hours
        // Expected: Friday September 19th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-9, TimeQuantityUnit.HOUR);
        assertEquals(expected, resourceCal.addWork(startDate, work));

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

        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider());

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

        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

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

        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider());

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

        resourceCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

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

        scheduleCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));

        // With no calendar, same date should be returned
        Date date = new Date();
        assertEquals(date, scheduleCal.getStartOfNextWorkingDay(date));

        //
        // No day of week, one date is a working day
        //
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                (String[]) null,
                new String[]{"08/19/03"});

        scheduleCal = new ScheduleWorkingTimeCalendar(makeProvider(calendarDef));
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
     * Makes a calendar definition provider based on the default working time calendar definition.
     * @return the working time calendar provider
     */
    private static IWorkingTimeCalendarProvider makeProvider() {
        return makeProvider(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());
    }

    /**
     * Makes a calendar definition provider based on the specified working time calendar definition.
     * @return the working time calendar provider
     */
    private static IWorkingTimeCalendarProvider makeProvider(WorkingTimeCalendarDefinition calendarDef) {
        return TestWorkingTimeCalendarProvider.make(calendarDef);
    }

}
