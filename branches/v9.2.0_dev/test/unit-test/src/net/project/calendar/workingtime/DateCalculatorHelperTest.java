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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.schedule.ScheduleEntryWorkingTimeCalendarTest;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link DateCalculatorHelper}.
 */ 
public class DateCalculatorHelperTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public DateCalculatorHelperTest(String s) {
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
        TestSuite suite = new TestSuite(DateCalculatorHelperTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with zero work.
     */
    public void testCalculateDateZeroWork() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DateCalculatorHelper dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        work = new TimeQuantity(0, TimeQuantityUnit.DAY);

        // StartDate: Monday May 5th @ 8:00 AM
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();

        // 100% assigned
        assignmentPercentage = new BigDecimal("1");
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // 0% assigned
        assignmentPercentage = new BigDecimal("0");
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // 50% assigned
        assignmentPercentage = new BigDecimal("0.5");
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // 2000% assigned
        assignmentPercentage = new BigDecimal("20");
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

    }

    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with various work amounts and assignment percentages.
     */
    public void testCalculateDatePositiveWork() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DateCalculatorHelper dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        //
        // 100% Assignment
        //
        assignmentPercentage = new BigDecimal("1");

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hrs
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hrs
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 40 hrs
        // Expected:  Friday May 9th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 9, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 50% Assignment
        //
        assignmentPercentage = new BigDecimal("0.5");

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hrs
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hrs
        // Expected:  Thursday May 8th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 8, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 40 hrs
        // Expected:  Friday May 16th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 10% Assignment
        //
        assignmentPercentage = new BigDecimal("0.1");

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hrs
        // Expected: Friday May 16th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hrs
        // Expected: Friday May 30th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 30, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 40 hrs
        // Expected: Friday July 11th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JULY, 11, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 0% Assignment
        //
        assignmentPercentage = new BigDecimal("0");

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

    }


    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with various negative work amounts and assignment percentages.
     */
    public void testCalculateDateNegativeWork() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DateCalculatorHelper dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        //
        // 100% Assignment
        //
        assignmentPercentage = new BigDecimal("1");

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Work: 16 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Friday May 9th @ 5:00 PM
        // Work: 40 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 9, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 50% Assignment
        //
        assignmentPercentage = new BigDecimal("0.5");

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Thursday May 8th @ 5:00 PM
        // Work: 16 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 8, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Friday May 16th @ 5:00 PM
        // Work: 40 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 10% Assignment
        //
        assignmentPercentage = new BigDecimal("0.1");

        // StartDate: Friday May 16th @ 5:00 PM
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Friday May 30th @ 5:00 PM
        // Work: 16 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 30, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-16, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Friday July 11th @ 5:00 PM
        // Work: 40 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.JULY, 11, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(-40, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // 0% Assignment
        //
        assignmentPercentage = new BigDecimal("0");

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(-8, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

    }

    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with every minute of an hour.
     * This ensures that fractional hours are handled correctly.
     * There could be problems with times like 40 minutes, which is 2/3 hour or 0.66666 etc.
     */
    public void testCalculateDateEveryMinute() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DateCalculatorHelper dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        //
        // Positive Work
        // 200% Assignment
        // Work: 1 hour
        //
        assignmentPercentage = new BigDecimal("2");
        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);

        for (int i = 0; i < 60; i++) {
            // Start date is Monday May 5th @ 1:00 PM initially, adding 1 minute each iteration
            cal.set(2003, Calendar.MAY, 5, 13, i);
            startDate = cal.getTime();

            // Expected date is one half hour after start date (since assignment % is 200%)
            // when i is 0..29, hour is 13 and minute is 30..59
            // when i is 30..59, hour is 14 and minute is 0..29
            int endHour = 13 + ((i + 30) / 60);
            int endMinute = (endHour == 13 ? (i + 30) : (i % 30));
            cal.set(2003, Calendar.MAY, 5, endHour, endMinute);
            expected = cal.getTime();

            assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));
        }

        //
        // Negative Work
        // 200% Assignment
        // Work: -1 hour
        //
        assignmentPercentage = new BigDecimal("2");
        work = new TimeQuantity(-1, TimeQuantityUnit.HOUR);

        for (int i = 0; i < 60; i++) {
            // Start date is Monday May 5th @ 4:00 PM initially, adding 1 minute
            // each iteration
            cal.set(2003, Calendar.MAY, 5, 16, i);
            startDate = cal.getTime();

            // Expected date is one half hour before start date (since assignment % is 200%)
            // when i is 0..29, hour is 15 and minute is 30..59
            // when i is 30..59, hour is 16 and minute is 0..29
            int endHour = 15 + ((i + 30) / 60);
            int endMinute = (endHour == 15 ? (i + 30) : (i % 30));
            cal.set(2003, Calendar.MAY, 5, endHour, endMinute);
            expected = cal.getTime();

            assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));
        }

    }


    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with large work values.
     */
    public void testCalculateDateLargeValues() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DateCalculatorHelper dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition()));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 365 days
        // Assignment: 100%
        // Expected:  Friday November 5th 2004 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, Calendar.NOVEMBER, 5, 17, 0);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("1");
        work = new TimeQuantity(365, TimeQuantityUnit.DAY);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 12145 days (Makes end date max value @ 12/31/49)
        // Assignment: 100%
        // Expected:  Friday December 31st 2049 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("1");
        work = new TimeQuantity(12145, TimeQuantityUnit.DAY);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 900 days
        // Assignment: 25%
        // Expected:  Friday March 31st 2017 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2017, 2, 31, 17, 0);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("0.25");
        work = new TimeQuantity(900, TimeQuantityUnit.DAY);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Monday January 2nd 1984 @ 8:00 AM
        // Work: 17220 days
        // Assignment: 100%
        // Expected: Friday December 31st 2049 @ 5:00 PM
        cal.set(1984, 0, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("1");
        work = new TimeQuantity(17220, TimeQuantityUnit.DAY);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // This test tests the limit dates of MS Project
        // StartDate: Monday January 2nd 1984 @ 8:00 AM
        // Work: 397,122,129.6 hours
        // Assignment: 288,271%
        // Expected: Friday December 31st 2049 @ 5:00 PM
        cal.set(1984, Calendar.JANUARY, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, Calendar.DECEMBER, 31, 17, 0);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("2882.71");
        work = new TimeQuantity(397122129.6, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // StartDate: Friday October 3rd 2003 @ 8:00 AM
        // Work: 1,666,666,666.67 hours
        // Assignment: 2,000,000,000 %
        // Expected: Wednesday October 17th 2003 @ 11:20 AM
        cal.set(2003, Calendar.OCTOBER, 3, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.OCTOBER, 17, 11, 20);
        expected = cal.getTime();
        assignmentPercentage = new BigDecimal("20000000.00");
        work = new TimeQuantity(1666666666.67, TimeQuantityUnit.HOUR);
        assertEquals(expected, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

    }

    /**
     * Tests {@link DateCalculatorHelper#calculateDate(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * with custom working time calendars.
     */
    public void testCalculateDateCustomCalendar() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;
        BigDecimal assignmentPercentage;
        DateCalculatorHelper dateCalcHelper;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelper resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();

        //
        // Resource 50% with calendar 1
        //
        dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, resourceHelper.calendarDef1));
        assignmentPercentage = new BigDecimal("0.5");

        // Start Date: Thursday June 19th @ 8:00 AM
        // Work: 2 days
        // Expected: Sunday June 22nd @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 22, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        assertEquals(expectedDate, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        //
        // Resource:  100%, calendar 2
        //
        dateCalcHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, resourceHelper.calendarDef2));
        assignmentPercentage = new BigDecimal("1");

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 0
        // Expected: Monday June 16th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        assertEquals(expectedDate, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 4 days
        // Expected: Thursday June 19th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 19, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        assertEquals(expectedDate, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // Calculate Start from Finish
        // Start Date: Friday June 27th @ 5:00 PM
        // Work: 4 days
        // Expected: Sunday June 22nd @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 22, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(-4, TimeQuantityUnit.DAY);
        assertEquals(expectedDate, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

        // Start Date: Tuesday June 10th 2003 @ 8:00 AM
        // Work: 200 days
        // Expected: Thursday May 20th 2004 @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 10, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, Calendar.MAY, 20, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(200, TimeQuantityUnit.DAY);
        assertEquals(expectedDate, dateCalcHelper.calculateDate(startDate, work, assignmentPercentage));

    }

}
