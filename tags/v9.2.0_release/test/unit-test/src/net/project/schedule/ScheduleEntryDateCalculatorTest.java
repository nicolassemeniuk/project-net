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
|   $Revision: 17111 $
|       $Date: 2008-03-26 14:00:33 +0530 (Wed, 26 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.Module;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.IDateCalculator;
import net.project.security.Action;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link ScheduleEntryDateCalculator}.
 */
public class ScheduleEntryDateCalculatorTest extends TestCase {

    public ScheduleEntryDateCalculatorTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleEntryDateCalculatorTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateFinishDate(java.util.Date)} for tasks
     * without assignments.
     * <p/>
     * In this case, the work is immaterial; dates always based directly on duration.
     */
    public void testCalculateFinishDateZeroAssignments() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity duration;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        // StartDate: Monday May 5th @ 8:00 AM
        // Duration: 2 days
        // Work: 0 hours
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        provider = new TestWorkingTimeCalendarProvider();

        dateCalc = makeDateCalculator(duration, work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Duration: 2 days
        // Work: 32 hours
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);

        provider = new TestWorkingTimeCalendarProvider();

        dateCalc = makeDateCalculator(duration, work, provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));
    }

    public void testCalculateFinishDateZeroWork() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        //
        // Custom Assignment %; schedule resource calendar
        //

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 day
        // 2 people @ 100%
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        provider = new TestWorkingTimeCalendarProvider();

        dateCalc = makeDateCalculator(work, makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 100}, provider), provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 hour
        // 1 person @ 100%, 1 person @ 50%
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        provider = new TestWorkingTimeCalendarProvider();
        dateCalc = makeDateCalculator(work, makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 50}, provider), provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 0 day
        // 1 person @ 75%
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.WEEK);
        provider = new TestWorkingTimeCalendarProvider();
        dateCalc = makeDateCalculator(work, makeAssignmentsDefaultWorkingTimeCalendar(new int[]{75}, provider), provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

    }

    public void testCalculateFinishDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        //
        // Custom Assignment % - 200%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 100}, provider);

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 1 day
        // Expected:  Monday May 5th @ 12:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 12, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 1:00 PM
        // Work: 4 days
        // Expected: Thursday May 8th @ 12:00 PM
        cal.set(2003, Calendar.MAY, 6, 13, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 8, 12, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 8:00 AM
        // Work: 16 days
        // EndDate: Thursday May 15th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 15, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hours
        // Expected:  Monday May 5th @ 12:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 12, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hours
        // Expected:  MOnday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        //
        // Custom Assignment % - 150%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 50}, provider);

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 1 day
        // Expected:  Monday May 5th @ 14:20 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 14, 20);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 1:00 PM
        // Work: 4 days
        // Expected: Friday May 9th @ 9:20 AM
        cal.set(2003, Calendar.MAY, 6, 13, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 9, 9, 20);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 8:00 AM
        // Work: 16 days
        // EndDate: Tuesday May 20th @ 14:20 PM
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 20, 14, 20);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 8 hours
        // Expected:  Monday May 5th @ 14:20 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 14, 20);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 16 hours
        // Expected:  Tuesday May 6th @ 10:40 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 10, 40);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 2:40 PM
        // Work: 1 day
        // Expected:  Tuesday May 6th @ 11:00 AM
        cal.set(2003, Calendar.MAY, 5, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 11, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        //
        // Custom Assignment % - < 100%
        //

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 1 day
        // Assignment %: 50%
        // Expected:  Tuesday May 6th @ 17:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{50}, provider);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 4 days
        // Assignment %: 25%
        // Expected: (16 total days) Monday May 26th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 26, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{25}, provider);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 4 hours
        // Assignment %: 10%
        // Expected:  (40 hours) Friday May 9th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 9, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{10}, provider);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        //
        // Assignment %: 0%
        // No work can be done

        // StartDate: Monday May 5th @ 8:00 AM
        // Work: 5 days
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = startDate;
        work = new TimeQuantity(5, TimeQuantityUnit.DAY);
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{0}, provider);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));
    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateStartDate(java.util.Date)} for tasks
     * without assignments.
     * <p/>
     * In this case, the work is immaterial; dates always based directly on duration.
     */
    public void testCalculateStartDateZeroAssignments() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity duration;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Duration: 2 days
        // Work: 0 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        provider = new TestWorkingTimeCalendarProvider();

        dateCalc = makeDateCalculator(duration, work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Duration: 2 days
        // Work: 32 hours
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);

        provider = new TestWorkingTimeCalendarProvider();

        dateCalc = makeDateCalculator(duration, work, provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));
    }

    public void testCalculateStartDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        //
        // Custom Assignment % - 200%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 100}, provider);

        // StartDate: Monday May 5th @ 12:00 PM
        // Work: 1 day
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 12, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Thursday May 8th @ 12:00 PM
        // Work: 4 days
        // Expected: Tuesday May 6th @ 1:00 PM
        cal.set(2003, Calendar.MAY, 8, 12, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 13, 0);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Thursday May 15th @ 5:00 PM
        // Work: 16 days
        // EndDate: Tuesday May 6th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 15, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 12:00 PM
        // Work: 8 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 12, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: MOnday May 5th @ 5:00 PM
        // Work: 16 hours
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Custom Assignment % - 150%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 50}, provider);

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 1 day
        // Expected:  Monday May 5th @ 10:40 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 10, 40);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday May 13th @ 2:40 PM
        // Work: 4 days
        // Expected: Friday May 9th @ 8:20 AM
        cal.set(2003, Calendar.MAY, 13, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 9, 8, 20);
        expected = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday May 20th @ 14:20 PM
        // Work: 16 days
        // EndDate: Tuesday May 6th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 20, 14, 20);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 5:00 PM
        // Work: 8 hours
        // Expected:  Monday May 5th @ 10:40 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 10, 40);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // StartDate: Tuesday May 13th @ 2:40 PM
        // Work: 16 hours
        // Expected: Monday May 12th @ 11:00 AM
        cal.set(2003, Calendar.MAY, 13, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 12, 11, 0);
        expected = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        // This was reportedly causing an infinite loop
        // in the application

        // StartDate: Wednesday January 8th @ 2:40 PM
        // Work: -8 hours
        // Expected: Wednesday January 8th @ 8:20 AM
        cal.set(2003, Calendar.JANUARY, 8, 14, 40);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JANUARY, 8, 8, 20);
        expected = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Zero Assignment %
        // Only possible when no work is done

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{0}, provider);

        // StartDate: Monday May 12th @ 5:00 PM
        // Work: 5 days
        // Expected:  Tuesday May 6th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 12, 17, 0);
        startDate = cal.getTime();
        expected = startDate;
        work = new TimeQuantity(5, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));

        //
        // Miscellaneous Assignment %
        //

        // 25%
        // StartDate: Thursday May 22nd @ 5:00 PM
        // Work: 1 days
        // Expected:  Monday May 19th @ 8:00 AM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{25}, provider);
        cal.set(2003, Calendar.MAY, 22, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 19, 8, 0);
        expected = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateStartDate(startDate));


    }


    /**
     * Tests calculateDate with every minute of an hour.  This ensures that
     * fractional hours are handled correctly. There could be problems with
     * times like 40 minutes, which is 2/3 hour or 0.66666...
     */
    public void testCalculateFinishDateEveryMinute() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        //
        // Positive Work;
        // Custom Assignment % - 200%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 100}, provider);

        // Work: 1 hour
        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);

        for (int i = 0; i < 60; i++) {
            // Start date is Monday May 5th @ 1:00 PM initially, adding 1 minute
            // each iteration
            cal.set(2003, Calendar.MAY, 5, 13, i);
            startDate = cal.getTime();

            // Expected date is one half hour after start date (since assignment % is 200%)
            // when i is 0..29, hour is 13 and minute is 30..59
            // when i is 30..59, hour is 14 and minute is 0..29
            int endHour = 13 + ((i + 30) / 60);
            int endMinute = (endHour == 13 ? (i + 30) : (i % 30));
            cal.set(2003, Calendar.MAY, 5, endHour, endMinute);
            expected = cal.getTime();

            assertEquals(expected, dateCalc.calculateFinishDate(startDate));
        }

        //
        // Calculate Start from Finish
        // Custom Assignment % - 200%
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100, 100}, provider);

        // Work: 1 hour
        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);

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

            assertEquals(expected, dateCalc.calculateStartDate(startDate));
        }

    }

    public void testCalculateFinishDateLargeValues() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TimeQuantity work;
        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        //
        // Positive Work
        //

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 365 days
        // Assignment: 100%
        // Expected:  Friday November 5th 2004 @ 5:00 PM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100}, provider);
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 10, 5, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(365, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 12145 days (Makes end date max value @ 12/31/49)
        // Assignment: 100%
        // Expected:  Friday December 31st 2049 @ 5:00 PM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100}, provider);
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(12145, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday June 16th 2003 @ 8:00 AM
        // Work: 900 days
        // Assignment: 25%
        // Expected:  Friday March 31st 2017 @ 5:00 PM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{25}, provider);
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2017, 2, 31, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(900, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday January 2nd 1984 @ 8:00 AM
        // Work: 17220 days
        // Assignment: 100%
        // Expected: Friday December 31st 2049 @ 5:00 PM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{100}, provider);
        cal.set(1984, 0, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, 11, 31, 17, 0);
        expected = cal.getTime();
        work = new TimeQuantity(17220, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Monday January 2nd 1984 @ 8:00 AM
        // Work: 397,121,895.83 hours
        // Assignment: 288,271%
        // Note: MSP reports the result as Friday December 31st 2049 @ 5:00 PM
        // This appears to be incorrect.  The duration is 3443 weeks, 4 days, 7 hours, 55 minutes, 8 seconds and some change
        // A 5 minute difference on a 66 year duration isn't too bad
        // Expected: Friday December 31st 2049 @ 4:55 PM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{288271}, provider);
        cal.set(1984, Calendar.JANUARY, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2049, Calendar.DECEMBER, 31, 16, 55);
        expected = cal.getTime();
        work = new TimeQuantity(397121895.83, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

        // StartDate: Friday October 3rd 2003 @ 8:00 AM
        // Work: 1,666,666,666.67 hours
        // Assignment: 2,000,000,000 %
        // Expected: Wednesday October 17th 2003 @ 11:20 AM
        provider = new TestWorkingTimeCalendarProvider();
        assignments = makeAssignmentsDefaultWorkingTimeCalendar(new int[]{2000000000}, provider);
        cal.set(2003, Calendar.OCTOBER, 3, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.OCTOBER, 17, 11, 20);
        expected = cal.getTime();
        work = new TimeQuantity(1666666666.67, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expected, dateCalc.calculateFinishDate(startDate));

    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateFinishDate(java.util.Date)} and
     * {@link ScheduleEntryDateCalculator#calculateStartDate(java.util.Date)}
     * with custom working time calendars.
     */
    public void testCalculateDateCustomCalendar() {

        // Reset seconds and milliseconds to zero for all dates and times
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;

        TestWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;
        ScheduleEntryWorkingTimeCalendarTest.ResourceHelper resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();

        //
        // One resource 50% with calendar 1
        //

        provider = new TestWorkingTimeCalendarProvider();
        assignments = Arrays.asList(new ScheduleEntryAssignment[]{ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 50, TimeZone.getTimeZone("America/Los_Angeles"), resourceHelper.calendarDef1, provider)});

        // Start Date: Thursday June 19th @ 8:00 AM
        // Work: 2 days
        // Expected: Sunday June 22nd @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 22, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        //
        // Two resources
        // Resource1:  100%, calendar 1
        // Resource2:  100%, calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(100, 100);
        provider = resourceHelper.getProvider();

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 0
        // Expected: Monday June 16th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 2 days
        // Expected: Thursday June 19th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 19, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // Start Date: Friday June 27th @ 5:00 PM
        // Work: 4 days
        // Expected: Tuesday June 24th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 24, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Tuesday June 10th @ 8:00 AM
        // Work: 200 days
        // Expected: Thursday November 27th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 10, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.NOVEMBER, 27, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(200, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        //
        // Two resources
        // Resource1:  100%, calendar 1
        // Resource2:  50%, calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(100, 50);
        provider = resourceHelper.getProvider();

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 2 days
        // Expected: Friday June 20th @ 10:40 AM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 20, 10, 40);
        expectedDate = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // Start Date: Friday June 27th @ 5:00 PM
        // Work: 4 days
        // Expected: Monday June 23rd @ 10:40 AM
        cal.set(2003, Calendar.JUNE, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 23, 10, 40);
        expectedDate = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Tuesday June 10th @ 8:00 AM
        // Work: 200 days
        // Expected: Monday January 26th 2004 @ 10:40 AM
        cal.set(2003, Calendar.JUNE, 10, 8, 0);
        startDate = cal.getTime();
        cal.set(2004, 0, 26, 10, 40);
        expectedDate = cal.getTime();
        work = new TimeQuantity(200, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        //
        // Two resources
        // Resource1:  10%, calendar 1
        // Resource2:  25%, calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(10, 25);
        provider = resourceHelper.getProvider();

        // Start Date: Monday June 16th @ 8:00 AM
        // Work: 2 days
        // Expected: Tuesday June 24th @ 2:43 PM
        cal.set(2003, Calendar.JUNE, 16, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 24, 14, 43);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish
        // Start Date: Friday June 27th @ 5:00 PM
        // Work: 4 days
        // Expected: Tuesday June 10th @ 1:34 PM
        cal.set(2003, Calendar.JUNE, 27, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 10, 13, 34);
        expectedDate = cal.getTime();
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

    }

    /**
     * Reproduces scenario for BFD 2009.
     * <p>
     * <b>Note:</b> The actual method names have changed since this error occurred; however, the scenario
     * is still valid for testing.
     * </p>
     * <p>
     * The following exception is thrown: <code><pre>
     * java.lang.IllegalArgumentException: Cannot retard more hours than remaining hours in day
     *     at net.project.calendar.workingtime.WorkingTimeCalendarEntry.retardHours(WorkingTimeCalendarEntry.java:667)
     *     at net.project.calendar.workingtime.WorkingTimeCalendarDefinition.allocateHoursInDay(WorkingTimeCalendarDefinition.java:1115)
     *     at net.project.calendar.workingtime.WorkingTimeCalendarDefinition.allocateEarlierHoursInDay(WorkingTimeCalendarDefinition.java:1030)
     *     at net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar.calculateDate(DefinitionBasedWorkingTimeCalendar.java:406)
     *     at net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar.addDuration(DefinitionBasedWorkingTimeCalendar.java:300)
     *     at net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar.calculateDate(DefinitionBasedWorkingTimeCalendar.java:259)
     *     at net.project.resource.ScheduleEntryAssignment$ResourceWorkingTimeCalendar.calculateDate(ScheduleEntryAssignment.java:462)
     *     at net.project.schedule.ScheduleEntryWorkingTimeCalendar.calculateDate(ScheduleEntryWorkingTimeCalendar.java:318)
     * </pre></code>
     * </p>
     * <p>
     * The problem was discovered to be as follows:
     * <li>Starting date is on a date that is normally a non working day, but has a date entry overriding that.
     * <li>It will take over 1 week (40 hours) of duration for the resource to complete the work based on
     * their allocation.
     * <li>A rounding error has to occur when subtracting the exceptional work hours from the remaining work
     * such that the calculated amount of remaining work is less than 30 seconds worth.
     * </p>
     * <p>
     * Reason: <br>
     * A method was called which failed with less than 30 seconds of work because it got rounded down to zero
     * minutes.  An attempt was made to allocate zero minutes on a non working day (which has zero minutes available);
     * this was the first problem. The system detected that no work could be allocated on a non working day
     * and threw an error.
     * </p>
     * <p>
     * Solution: <br>
     * <li>Detect the attempt to allocate zero work earlier, throw a better error
     * <li>Change the determination of isWorkingRemaining to only indicate so if there is 30 seconds or more work
     * </p>
     */
    public void testCalculateFinishDateBFD2009() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;
        int resourcePercentage;
        WorkingTimeCalendarDefinition def;

        IDateCalculator dateCalc;
        Collection assignments;
        TestWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();

        // BFD-2009
        // A problem was identified with a customer MPD file
        // Here are the exact conditions:

        // Standard calendar with Sun March 21st and Saturday March 27th as working times
        // Start on Sun March 21st @ 16:55 pm
        // 25 hours work
        // One resource 52%
        // Expected Sat March 27th @ 5:00 pm

        resourcePercentage = 52;
        def = WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                new int[]{Calendar.SATURDAY, Calendar.SUNDAY},
                null,
                new String[]{"3/21/04", "3/27/04"});

        assignments = Arrays.asList(
                new ScheduleEntryAssignment[]{ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", resourcePercentage, TimeZone.getTimeZone("America/Los_Angeles"), def, provider)}
        );

        // Start Date: Sunday March 21 @ 16:55 PM
        // Work: 25 hours
        // Expected: Saturday Martch 27 @ 17:00 PM
        cal.set(2004, Calendar.MARCH, 21, 16, 55);
        startDate = cal.getTime();
        cal.set(2004, Calendar.MARCH, 27, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(25, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateFinishDate(java.util.Date)} with
     * hierarchical working times definition going forward in time.
     */
    public void testCalculateFinishDateCalendarHierarchy() {

        // Reset seconds and milliseconds to zero for all dates and times
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;

        IDateCalculator dateCalc;
        Collection assignments;
        IWorkingTimeCalendarProvider provider;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1
        // Resource2:  100%, hierarchy calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(100, 100);

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day
        // Expected: Monday June 2nd @ 12:00 PM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 12, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 3 days
        // Expected: Wednesday June 4th @ 12:00 PM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 4, 12, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 10 days
        // Expected: Wednesday June 11th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 11, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Friday 6th June @ 8:00 AM
        // Work: 6 days
        // Expected: Friday 13th June @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 13, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Friday 20th June @ 8:00 AM
        // Work: 8 days
        // Expected: Saturday June 28th @ 5:00 PM
        cal.set(2003, Calendar.JUNE, 20, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 28, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        //
        // Two resources
        // Resource1:  30%, hierarchy calendar 1
        // Resource2:  40%, hierarchy calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(30, 40);

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day
        // Expected: Wednesday June 4th @ 11:26 AM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 4, 11, 26);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 2 day
        // Expected: Thursday June 5th @ 3:51 PM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 5, 15, 51);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 3 day
        // Expected: Wednesday June 11th @ 10:17 AM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 11, 10, 17);
        expectedDate = cal.getTime();
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 4 day
        // Expected: Friday June 13th @ 2:43 PM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 13, 14, 43);
        expectedDate = cal.getTime();
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 5 day
        // Expected: Monday June 16th @ 9:09 AM
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 16, 9, 9);
        expectedDate = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 2 days
        // Expected: Monday June 23rd @ 3:51 PM
        cal.set(2003, Calendar.JUNE, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 23, 15, 51);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 3 days
        // Expected: Saturday June 28th @ 10:17 AM
        cal.set(2003, Calendar.JUNE, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 28, 10, 17);
        expectedDate = cal.getTime();
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 4 days
        // Expected: Monday June 30th @ 2:43 PM
        cal.set(2003, Calendar.JUNE, 19, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 30, 14, 43);
        expectedDate = cal.getTime();
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateStartDate(java.util.Date)} with
     * hierarchical working times definition going backward in time.
     */
    public void testCalculateStartDateCalendarHierarchy() {

        // Reset seconds and milliseconds to zero for all dates and times
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;

        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1
        // Resource2:  100%, hierarchy calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(100, 100);

        // Start Date: Monday June 2nd @ 5:00 PM
        // Work: 1 day
        // Expected: Monday June 2nd @ 1:00 PM
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 13, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Wednesday June 4th @ 5:00 PM
        // Work: 3 days
        // Expected: Monday June 2nd @ 1:00 PM
        cal.set(2003, Calendar.JUNE, 4, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 13, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Monday June 16th @ 5:00 PM
        // Work: 10 days
        // Expected: Friday June 6th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 6, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Monday 16th June @ 5:00 PM
        // Work: 6 days
        // Expected: Thursday June 12th @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 12, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Saturday June 28th @ 5:00 PM
        // Work: 8 days
        // Expected: Friday 20th June @ 8:00 AM
        cal.set(2003, Calendar.JUNE, 28, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 20, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        //
        // Two resources
        // Resource1:  30%, hierarchy calendar 1
        // Resource2:  40%, hierarchy calendar 2
        //

        assignments = resourceHelper.makeTwoAssignments(30, 40);

        // Start Date: Wednesday June 4th @ 5:00 PM
        // Work: 1 day
        // Expected: Monday June 2nd @ 1:34 PM
        cal.set(2003, Calendar.JUNE, 4, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 13, 34);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Thursday June 5th @ 5:00 PM
        // Work: 2 days
        // Expected: Monday June 2nd @ 9:09 AM
        cal.set(2003, Calendar.JUNE, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 9, 9);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Wednesday June 11th @ 5:00 PM
        // Work: 3 days
        // Expected: Monday June 2nd @ 2:43 PM
        cal.set(2003, Calendar.JUNE, 11, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 14, 43);
        expectedDate = cal.getTime();
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Friday June 13th @ 5:00 PM
        // Work: 4 days
        // Expected: Monday June 2nd @ 10:17 AM
        cal.set(2003, Calendar.JUNE, 13, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 10, 17);
        expectedDate = cal.getTime();
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Monday June 16th @ 5:00 PM
        // Work: 5 day
        // Expected: Monday June 2nd @ 3:51 PM
        cal.set(2003, Calendar.JUNE, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 15, 51);
        expectedDate = cal.getTime();
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Monday June 23rd @ 5:00 PM
        // Work: 2 days
        // Expected: Thursday June 19th @ 9:09 AM
        cal.set(2003, Calendar.JUNE, 23, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 19, 9, 9);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Saturday June 28th @ 5:00 PM
        // Work: 3 days
        // Expected: Thursday June 19th @ 2:43 PM
        cal.set(2003, Calendar.JUNE, 28, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 19, 14, 43);
        expectedDate = cal.getTime();
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // Start Date: Monday June 30th @ 5:00 PM
        // Work: 4 days
        // Expected: Thursday June 19th @ 10:17 AM
        cal.set(2003, Calendar.JUNE, 30, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 19, 10, 17);
        expectedDate = cal.getTime();
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#calculateFinishDate(java.util.Date)} and
     * {@link ScheduleEntryDateCalculator#calculateStartDate(java.util.Date)}
     * with various time zones.
     */
    public void testCalculateDateTimeZones() {

        // Reset seconds and milliseconds to zero for all dates and times
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expectedDate;
        TimeQuantity work;
        TimeZone timeZone1;
        TimeZone timeZone2;

        IWorkingTimeCalendarProvider provider;
        IDateCalculator dateCalc;
        Collection assignments;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();
        timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
        timeZone2 = TimeZone.getTimeZone("Europe/London");

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1, PST
        // Resource2:  100%, hierarchy calendar 2, GMT
        //
        assignments = resourceHelper.makeTwoAssignments(100, timeZone1, 100, timeZone2);

        // Zero work

        // Start Date: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 0 day
        // Expected: Monday June 2nd @ 8:00 AM PST
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Positive work

        // Start Date: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 1 day; 4 hours each resource
        // Cal1 works: 6/2 @ 8:00 AM PST - 6/2 @ 12:00 PM PST
        // Cal2 works: 6/2 @ 4:00 PM GMT - 6/4 @ 11:00 AM GMT (3:00 AM PST)
        // June 3rd is Non working day for both
        // Expected: Wednesday June 4th @ 3:00 AM PST
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 4, 3, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Start Date: Monday June 2nd @ 12:00 AM PST (8:00 AM GMT)
        // Work: 1 day; 4 hours each resource
        // Cal1 works: 6/2 @ 8:00 AM PST - 6/2 @ 12:00 PM PST
        // Cal2 works: 6/2 @ 8:00 AM GMT - 6/2 @ 12:00 PM GMT (4:00 AM PST)
        // Expected: Latest date: Monday June 2nd @ 12:00 PM PST
        cal.set(2003, Calendar.JUNE, 2, 0, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 12, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // StartDate: Tuesday May 6th @ 8:00 AM
        // Work: 16 days; 8 days (64 hours each)
        // Cal1 works: Wed 5/7 @ 8:00 AM PST - Mon 5/19 @ 5:00 PM PST
        // Cal2 works: Tue 5/6 @ 4:00 PM GMT - Thu 5/15 @ 3:00 PM GMT (7:00 AM PST)
        // EndDate: Latest Date: Monday 5/19 @ 5:00 PM PST
        cal.set(2003, Calendar.MAY, 6, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 19, 17, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateFinishDate(startDate));

        // Calculate Start from Finish

        // StartDate: Thursday September 25th @ 5:00 PM PST (9/26 @ 1:00 AM GMT)
        // Work: 4 hours; 2 hours each
        // Cal1 works: Thu 9/25 @ 5:00 PM PST - Thu 9/25 @ 3:00 PM PST
        // Cal2 works: Thu 9/25 @ 5:00 PM GMT - Thu 9/25 @ 3:00 PM GMT (7:00 AM PST)
        // Expected: Earliest date: Thursday September 25th @ 5:00 AM PST
        cal.set(2003, Calendar.SEPTEMBER, 25, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 25, 7, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));

        // StartDate: Thursday September 25th @ 5:00 PM PST (9/26 @ 1:00 AM GMT)
        // Work: 2 days; 8 hours each
        // Cal1 works: Thu 9/25 @ 5:00 PM PST - Thu 9/25 @ 8:00 AM PST
        // Cal2 works: Thu 9/25 @ 5:00 PM GMT - Thu 9/25 @ 8:00 AM GMT (12:00 AM PST)
        // Expected: Earliest date: Thursday September 25th @ 12:00 AM PST
        cal.set(2003, Calendar.SEPTEMBER, 25, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 25, 0, 0);
        expectedDate = cal.getTime();
        work = new TimeQuantity(2, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        assertEquals(expectedDate, dateCalc.calculateStartDate(startDate));


    }

    /**
     * Tests that {@link ScheduleEntryDateCalculator#addWorkAndupdateAssignmentDates(java.util.Date)} works
     * even when there are no assignments.
     */
    public void testAddWorkAndUpdateAssignmentDatesZeroAssignments() {

        Date startDate;
        TimeQuantity duration;
        TimeQuantity work;
        Date expectedFinishDate;

        IWorkingTimeCalendarProvider provider;
        ScheduleEntryDateCalculator dateCalc;

        provider = new TestWorkingTimeCalendarProvider();

        //
        // With no resources, the date returned should simply be the date by which
        // the task's duration could be completed by a 100% resource
        //

        // Start Date: Monday June 2nd @ 8:00 AM
        // Duration: 2 days
        // Work: 0 hours
        // Expected End Date: Tuesday June 3rd @ 5:00 PM
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        dateCalc = makeDateCalculator(duration, work, provider);
        expectedFinishDate = dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/03/03 5:00 PM"), expectedFinishDate);
    }

    public void testAddWorkAndUpdateAssignmentDatesPositiveWork() {

        Date startDate;
        TimeQuantity work;

        List assignments;
        IWorkingTimeCalendarProvider provider;
        ScheduleEntryDateCalculator dateCalc;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1
        // Resource2:  100%, hierarchy calendar 2
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(100, 100));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);

        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 3 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 10 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/11/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/07/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Friday 6th June @ 8:00 AM
        // Work: 6 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM");
        work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/12/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/13/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Friday 20th June @ 8:00 AM
        // Work: 8 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM");
        work = new TimeQuantity(8, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/28/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        //
        // Two resources
        // Resource1:  30%, hierarchy calendar 1
        // Resource2:  40%, hierarchy calendar 2
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(30, 40));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 11:26 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 11:26 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 2 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 3 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/11/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/07/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 4 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/12/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/13/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 5 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 2 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/21/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 3 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM");
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/28/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 19th @ 8:00 AM
        // Work: 4 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM");
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/30/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/24/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

    }

    /**
     * Tests addWorkAndUpdateAssignmentDates().
     */
    public void testAddWorkAndUpdateAssignmentDatesNegativeWork() {

        //
        // Note:
        // We don't actually test with negative work values
        // Instead, we set the start date to be the start date that would
        // have been produced by calculating from the end date (with negative
        // work) and test the correct end dates
        Date startDate;
        TimeQuantity work;

        List assignments;
        IWorkingTimeCalendarProvider provider;
        ScheduleEntryDateCalculator dateCalc;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1
        // Resource2:  100%, hierarchy calendar 2
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(100, 100));

        // Start Date: Monday June 2nd @ 1:00 PM
        // Work: 1 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM");
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 1:00 PM
        // Work: 3 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM");
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Friday June 6th @ 8:00 AM
        // Work: 10 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM");
        work = new TimeQuantity(10, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Thursday June 12th @ 8:00 AM
        // Work: 6 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/12/03 8:00 AM");
        work = new TimeQuantity(6, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/12/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/13/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Friday June 20th @ 8:00 AM
        // Work: 8 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM");
        work = new TimeQuantity(8, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/28/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/20/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        //
        // Two resources
        // Resource1:  30%, hierarchy calendar 1
        // Resource2:  40%, hierarchy calendar 2
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(30, 40));

        // Start Date: Monday June 2nd @ 1:34 PM
        // Work: 1 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:34 PM");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:34 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 1:34 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 9:09 AM
        // Work: 2 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 9:09 AM");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 2:43 PM
        // Work: 3 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 2:43 PM");
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/11/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/07/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 10:17 AM
        // Work: 4 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 10:17 AM");
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/12/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/13/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 3:51 PM
        // Work: 5 day
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 3:51 PM");
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/16/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Thursday June 19th @ 9:09 AM
        // Work: 2 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 9:09 AM");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 9:09 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/21/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Thursday June 19th @ 2:43 PM
        // Work: 3 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 2:43 PM");
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/28/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 2:43 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/23/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Thursday June 19th @ 10:17 AM
        // Work: 4 days
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 10:17 AM");
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/30/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/19/03 10:17 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/24/03 5:00 PM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

    }

    /**
     * Tests {@link ScheduleEntryDateCalculator#addWorkAndupdateAssignmentDates(java.util.Date)}
     * with different time zones.
     */
    public void testAddWorkAndUpdateAssignmentDatesTimeZones() {

        Date startDate;
        TimeQuantity work;

        List assignments;
        IWorkingTimeCalendarProvider provider;
        ScheduleEntryDateCalculator dateCalc;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy resourceHelper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = resourceHelper.getProvider();

        TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
        TimeZone timeZone2 = TimeZone.getTimeZone("Europe/London");

        //
        // Two resources
        // Resource1:  100%, hierarchy calendar 1, PST
        // Resource2:  100%, hierarchy calendar 2, GMT
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(100, timeZone1, 100, timeZone2));

        // Positive work

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day; 4 hours each
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(1, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        // First assignee 6/2 @ 8:00 AM - 6/2 @ 12:00 PM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        // Second assignee 6/2 @ 4:00 PM - 6/4 @ 11:00 AM GMT == 6/2 @ 8:00 AM - 6/4 @ 3:00 AM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 3:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 3 days; 12 hours each
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(3, TimeQuantityUnit.DAY);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        // First assignee 6/2 @ 8:00 AM - 6/4 @ 12:00 PM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 12:00 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        // Second assignee 6/2 @ 4:00 PM - 6/5 @ 11:00 AM GMT = 6/2 @ 8:00 AM - 6/5 @ 3:00 AM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 3:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        //
        // Two resources
        // Resource1:  30%, hierarchy calendar 1, PST
        // Resource2:  40%, hierarchy calendar 2, GMT
        //

        assignments = new ArrayList(resourceHelper.makeTwoAssignments(30, timeZone1, 40, timeZone2));

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 1 day; 11.429 hours each (11h 26m)
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        // First assignee 6/2 @ 8:00 AM - 6/4 @ 11:26 AM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/04/03 11:26 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        // Second assignee 6/2 @ 4:00 PM - 6/5 10:26 AM GMT == 6/2 @ 8:00 AM - 6/5 2:26 AM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 2:26 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

        // Start Date: Monday June 2nd @ 8:00 AM
        // Work: 2 day; 22.857 hours each (22h 51m)
        startDate = WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM");
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        dateCalc = makeDateCalculator(work, assignments,  provider);
        dateCalc.addWorkAndupdateAssignmentDates(startDate);
        // First assignee 6/2 @ 8:00 AM - 6/5 3:51 PM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(0)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/05/03 3:51 PM"), ((ScheduleEntryAssignment) assignments.get(0)).getEndTime());
        // Second assignee 6/2 @ 4:00 PM - 6/6 2:51 PM GMT == 6/2 @ 8:00 AM - 6/6 @ 6:51 AM PST
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/02/03 8:00 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getStartTime());
        assertEquals(WorkingTimeCalendarDefinitionTest.makeDateTime("06/06/03 6:51 AM"), ((ScheduleEntryAssignment) assignments.get(1)).getEndTime());

    }

    //
    // Helper Methods
    //

    /**
     * Makes a collection of assignments with various percentages where each
     * assignment's resource calendar is the default calendar.
     * <p>
     * Updates the specified provider to contain calendars for the assignees.
     * The time zone used is <code>America/Los_Angeles</code>.
     * </p>
     * @param percentages the percentages from which to create assignments
     * @param provider the provider to update such that the calendar definition will
     * be returned for the assignee
     * @return the collection of assignments where each assignment has
     * one of the percentage values
     */
    private static Collection makeAssignmentsDefaultWorkingTimeCalendar(int[] percentages, TestWorkingTimeCalendarProvider provider) {

    	  MockHttpServletRequest request = Application.requestPage("/servlet/ScheduleController/TaskView?action="+
          		Action.VIEW+"&module="+Module.SCHEDULE);
          
          if(request.getSession().getAttribute("schedule") == null){
  	        Schedule schedule = new Schedule();
  	        schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  	        schedule.setWorkingTimeCalendarProvider(provider);
  	        schedule.setResourceCalendar(SchedulePropertiesHelper.SCHEDULE_RESOURCE_CALENDAR);
  	        request.getSession(true).setAttribute("schedule", schedule);
          }

        List assignments = new ArrayList();

        for (int i = 0; i < percentages.length; i++) {
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("" + i, percentages[i], TimeZone.getTimeZone("America/Los_Angeles"), WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition(), provider));
        }

        return assignments;
    }

    /**
     * Creates a date calculator for the specified duration with zero assignments.
     * @param duration the duration of the task
     * @param work the work of the task
     * @param provider
     * @return the task
     */
    private static ScheduleEntryDateCalculator makeDateCalculator(TimeQuantity duration, TimeQuantity work, IWorkingTimeCalendarProvider provider) {

        return new ScheduleEntryDateCalculator(createTask(duration, work), provider);
    }

    /**
     * Creates a date calculator for the specified amount of task work and assignments.
     * <p>
     * The work is distributed amongst the assignments based on their relative % assignments.
     * </p>
     * @param work
     * @param assignments
     * @param provider
     * @return
     */
    private static ScheduleEntryDateCalculator makeDateCalculator(TimeQuantity work, Collection assignments, IWorkingTimeCalendarProvider provider) {

        return new ScheduleEntryDateCalculator(createTask(work, assignments), provider);
    }

    private static ScheduleEntry createTask(TimeQuantity duration, TimeQuantity work) {
        ScheduleEntry scheduleEntry = createTask(work, Collections.EMPTY_SET);
        scheduleEntry.setDuration(duration);
        return scheduleEntry;
    }

    /**
     * Creates a schedule entry with the specified amount of task work distributed amongst the assignments
     * based on their relative percentages.
     *
     * @param work the total task work
     * @param assignments the assignments to add to task and update work
     * @return the schedule entry with specified work and assignments
     */
    public static ScheduleEntry createTask(TimeQuantity work, Collection assignments) {
        ScheduleEntry task;
        task = new Task();
        task.setWork(work);
        task.addAssignments(assignments);

        BigDecimal totalPercentageAssigned = new BigDecimal("0");
        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            totalPercentageAssigned = totalPercentageAssigned.add(nextAssignment.getPercentAssigned());
        }

        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

            TimeQuantity assignmentWork;
            if (nextAssignment.getPercentAssigned().signum() == 0) {
                // Zero percent assigned; only possible with zero work
                assignmentWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
            } else {
                // (work hours * percent assigned) / total percent assigned
                assignmentWork = task.getWorkTQ().multiply(nextAssignment.getPercentAssigned()).divide(totalPercentageAssigned, 3, BigDecimal.ROUND_HALF_UP);
            }

            nextAssignment.setWork(assignmentWork);
        }
        return task;
    }

}
