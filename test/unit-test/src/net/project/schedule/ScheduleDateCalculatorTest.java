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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.schedule.calc.IDateCalculator;
import net.project.security.User;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link ScheduleDateCalculator}.
 */
public class ScheduleDateCalculatorTest extends TestCase {
    public ScheduleDateCalculatorTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleDateCalculatorTest.class);
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

    public void testCalculateFinishDate() {

        User user = makeUser();

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        provider = makeProvider();

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 day
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 1 day
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 1:00 PM
        // Work: 4 days
        // Expected: Monday May 12th @ 12:00 PM
        cal.set(2003, Calendar.MAY, 6, 13, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 12, 12, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 8:00 AM
        // Work: 16 days
        // EndDate: Tuesday May 27th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 27, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hours
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hours
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 2:40 PM
        // Work: 8 hours
        // Expected: Tuesday May 6th @ 2:40 PM
        cal.set(2003, Calendar.MAY, 5, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 14, 40);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

    }

    public void testCalculateStartDate() {
        User user = makeUser();

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        provider = makeProvider();

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 0 day
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 1 day
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 12th @ 12:00 PM
        // Work: 4 days
        // Expected: Tuesday May 6th @ 1:00 PM
        cal.set(2003, Calendar.MAY, 12, 12, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 13, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday May 27th @ 5:00 PM
        // Work: 16 days
        // EndDate: Tuesday May 6th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 8 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Work: 16 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 2:40 PM
        // Work: 8 hours
        // Expected: Tuesday May 6th @ 2:40 PM
        cal.set(2003, Calendar.MAY, 6, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 14, 40);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

    }

    /**
     * Tests {@link ScheduleDateCalculator#calculateFinishDate(java.util.Date)} and
     * {@link ScheduleDateCalculator#calculateStartDate(java.util.Date)}
     * with a calendar with custom day of week entries.
     */
    public void testCalculateDateCustomCalendarDayOfWeek() {

        User user = makeUser();

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        //
        // Saturday, Sunday non working days
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}));

        // StartDate: Friday June 13th @ 8:00 AM
        // Work: 2 days
        // Expected:  Monday June 16th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 13, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 16, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // StartDate: Friday June 20th @ 5:00 PM
        // Work: 6 days
        // Expected:  Friday June 13th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 20, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 13, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Sunday, Monday, Tuesday, Wednesday, Saturday non working days
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.SATURDAY}));

        // StartDate: Sunday June 15th @ 8:00 AM
        // Work: 10 days
        // Expected:  Friday July 18th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JULY, 18, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // StartDate: Friday June 20th @ 5:00 PM
        // Work: 3 days
        // Expected:  Friday June 13th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 20, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 13, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Every day non working day
        // This is a special case; work calculation is impossible
        // calculateDate() returns the original date in this case
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY}));

        // StartDate: Sunday June 15th @ 8:00 AM
        // Work: 10 days
        // Expected:  Sunday June 15th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 15, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        //
        // Every day available
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{}));

        // StartDate: Tuesday June 17th 2003 @ 8:00 AM
        // Work: 400 days
        // Expected:  Tuesday July 20th 2004 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 17, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 6, 20, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(400, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // StartDate: Monday December 8th @ 5:00 PM
        // Work: 175 days
        // Expected:  Tuesday June 17th @ 8:00 AM
        cal.set(2003, Calendar.DECEMBER, 8, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 17, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(175, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

    }

    /**
     * Tests {@link ScheduleDateCalculator#calculateFinishDate(java.util.Date)}
     * with a calendar with custom date of entries.
     */
    public void testCalculateFinishDateCustomCalendarDate() {

        User user = makeUser();

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        //
        // Saturday, Sunday non working days
        // Non Working Days: June 17th 2003
        //
        provider = makeProvider(
                WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                        new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                        new String[]{"06/17/03"}, null));

        // StartDate: Monday June 16th @ 8:00 AM
        // Work: 2 days
        // Expected:  Wednesday June 18th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 18, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        //
        // Saturday, Sunday, Wednesday non working days
        // Non Working Dates: Week of Monday 23rd June - Friday 27th June; Month of December 2003
        // Working Dates: Every Wednesday in July (2, 9, 16, 23, 30); Sunday 7th September - Saturday 13th September

        Date[] nonWorkingDates = new Date[5 + 31];

        // Add 5 days from Mon 23rd June to Friday 27th June to non working days
        cal.set(2003, Calendar.JUNE, 23, 0, 0);
        for (int i = 0; i < 5; i++) {
            nonWorkingDates[i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        // Add month of December 2003
        cal.set(2003, Calendar.DECEMBER, 1, 0, 0);
        for (int i = 0; i < 31; i++) {
            nonWorkingDates[5 + i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Date[] workingDates = new Date[5 + 7];
        cal.set(2003, Calendar.JULY, 2, 0, 0);
        workingDates[0] = cal.getTime();
        cal.set(2003, Calendar.JULY, 9, 0, 0);
        workingDates[1] = cal.getTime();
        cal.set(2003, Calendar.JULY, 16, 0, 0);
        workingDates[2] = cal.getTime();
        cal.set(2003, Calendar.JULY, 23, 0, 0);
        workingDates[3] = cal.getTime();
        cal.set(2003, Calendar.JULY, 30, 0, 0);
        workingDates[4] = cal.getTime();

        // Add September 7th - September 13th
        cal.set(2003, Calendar.SEPTEMBER, 7, 0, 0);
        for (int i = 0; i < 7; i++) {
            workingDates[5 + i] = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.WEDNESDAY, Calendar.SATURDAY},
                nonWorkingDates,
                workingDates);

        provider = makeProvider(calendarDef);

        // StartDate: Monday June 16th @ 8:00 AM
        // Work: 140 days
        // Expected: Monday March 15th 2004 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 2, 9, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(140, TimeQuantityUnit.DAY);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

    }

    /**
     * Tests {@link ScheduleDateCalculator#calculateFinishDate(java.util.Date)} and
     * {@link ScheduleDateCalculator#calculateStartDate(java.util.Date)} with
     * non-default working times.
     */
    public void testCalculateDateNonDefaultTimes() {

        User user = makeUser();

        PnCalendar cal = new PnCalendar(user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        //
        // 24 hour calendar
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.make24HourCalendarDefinition());

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 48 hours
        // Expected: Friday September 19th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 16 hours
        // Expected: Friday September 17th @ 4:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 16, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // Issue: 09/16/2003
        // Found an infinite loop on negative work with 24 hour calendars

        // Calculate Start from Finish
        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 24 hours
        // Expected: Monday September 16th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // Calculate Start from Finish
        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 16 hours
        // Expected: Tuesday September 16th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // Calculate Start from Finish
        // StartDate: Wednesday September 17th @ 12:00 AM
        // Work: 48 hours
        // Expected: Monday September 15th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Nightshift calendar
        //
        provider = makeProvider(WorkingTimeCalendarDefinitionTest.makeNightshiftCalendarDefinition());

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: 8 hours
        // Expected: Thursday September 18th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 18, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday September 15th @ 11:00 PM
        // Work: 40 hours
        // Expected: Saturday September 20th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday September 15th @ 11:00 PM
        // Work: 41 hours
        // Expected: Tuesday September 23rd @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(41, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday September 15th @ 8:00 AM
        // Work: 4 hours
        // Expected: Tuesday September 16th @ 3:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 3, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday September 15th @ 8:00 AM
        // Work: 5 hours
        // Expected: Tuesday September 16th @ 5:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 15, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 16, 5, 0);
        expected = cal.getTime();
        work = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Friday September 19th @ 12:00 AM
        // Work: 7 hours
        // Expected: Friday September 19th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(7, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Friday September 19th @ 11:00 PM
        // Work: 8 hours
        // Expected: Saturday September 20th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Friday September 19th @ 11:00 PM
        // Work: 9 hours
        // Expected: Tuesday September 23rd @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(9, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish

        // StartDate: Wednesday September 17th @ 11:00 PM
        // Work: 0 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Thursday September 18th @ 8:00 AM
        // Work: 8 hours
        // Expected: Wednesday September 17th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 18, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 17, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Saturday September 20th @ 8:00 AM
        // Work: 40 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday September 23rd @ 12:00 AM
        // Work: 41 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(41, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday September 16th @ 3:00 AM
        // Work: 4 hours
        // Expected: Monday September 15th @ 11:00 pm
        cal.set(2003, Calendar.SEPTEMBER, 16, 3, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday September 16th @ 5:00 AM
        // Work: 5 hours
        // Expected: Monday September 15th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 16, 5, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 15, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Friday September 19th @ 8:00 AM
        // Work: 7 hours
        // Expected: Friday September 19th @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 0, 0);
        expected = cal.getTime();
        work = new TimeQuantity(7, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Saturday September 20th @ 8:00 AM
        // Work: 8 hours
        // Expected: Friday September 19th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 20, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday September 23rd @ 12:00 AM
        // Work: 9 hours
        // Expected: Friday September 19th @ 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 19, 23, 0);
        expected = cal.getTime();
        work = new TimeQuantity(9, TimeQuantityUnit.HOUR);
        dateCalc = new ScheduleDateCalculator(work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

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
