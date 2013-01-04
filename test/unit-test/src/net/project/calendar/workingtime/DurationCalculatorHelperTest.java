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
 * Tests {@link DurationCalculatorHelper}.
 */
public class DurationCalculatorHelperTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public DurationCalculatorHelperTest(String s) {
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
        TestSuite suite = new TestSuite(DurationCalculatorHelperTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link DurationCalculatorHelper#getDaysWorked(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}.
     */
    public void testGetDaysWorked() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        DurationCalculatorHelper durationCalcHelper = new DurationCalculatorHelper(timeZone, helper.calendarDef1);

        //
        // 100% assignment
        //
        assignmentPercentage = new BigDecimal("1");

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("0.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 2 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("2.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 4 hours
        // Expected: 0.5 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("0.50"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        //
        // 25%
        //
        assignmentPercentage = new BigDecimal("0.25");

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 8 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("8.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        //
        // 0%
        //
        assignmentPercentage = new BigDecimal("0.00");

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("0.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        //
        // Miscellaneous
        //

        // Issue: Infinite loop
        // Start date: Tuesday December 3rd @ 8:00 AM
        // Assignment %: 112
        // Work: 1.18 hours
        // Expected: 0.13 days  (work / (%/100)) / 8
        cal.set(2002, Calendar.DECEMBER, 3, 8, 0);
        assignmentPercentage = new BigDecimal("1.12");
        work = new TimeQuantity(1.18, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("0.13"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2, BigDecimal.ROUND_HALF_UP));

        // Issue: Unusual combinations
        // Start date: Monday October 6th @ 8:00 AM
        // Work factor: 20,000,000 (2 billion %)
        // Work: 1,666,666,666 hrs
        // Expected: 83.333 hrs == 10.42 days
        cal.set(2003, Calendar.OCTOBER, 6, 8, 0);
        assignmentPercentage = new BigDecimal("20000000.00");
        work = new TimeQuantity(1666666666, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("10.42"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2, BigDecimal.ROUND_HALF_UP));

        // Issue: Long calculation combinations
        // When importing, duration may be calculated prior to importing assignments
        // As a result, extremely long work values may be present.  Normally MSP limits
        // work / allocation % to a total of about 65 years (1984 - 2049) thus calculations
        // are reasonable.  However, prior to importing assignments, we use a default of 100%.
        // Thus, work might be as long as 1.67 billion hours but 100% assigned resulting in almost
        // 800,000 years of duration
        //
        // We expect an exception when (work hours / work factor) exceeds 578160 hours
        // Start date: Monday January 2nd @ 8:00 AM
        // Work factor: 1.00 (100%)
        // Work: 1,666,666,666 hrs
        cal.set(1984, Calendar.JANUARY, 2, 8, 0);
        assignmentPercentage = new BigDecimal("1.00");
        work = new TimeQuantity(1666666666, TimeQuantityUnit.HOUR);
        try {
            durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Tests {@link DurationCalculatorHelper#getDaysWorked(java.util.Date, net.project.util.TimeQuantity, java.math.BigDecimal)}
     * for a different time zone.
     * <p>
     * The time zone doesn't actually affect duration for an individual assignee.
     * </p>
     */
    public void testGetDaysWorkedTimeZones() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        TimeQuantity work;
        BigDecimal assignmentPercentage;

        DurationCalculatorHelper durationCalcHelper;
        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();

        //
        // 100% assignment, Europe/London (GMT)
        //
        durationCalcHelper = new DurationCalculatorHelper(TimeZone.getTimeZone("Europe/London"), helper.calendarDef1);
        assignmentPercentage = new BigDecimal("1.00");

        // StartDate: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("0.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

        // StartDate: Monday June 2nd @ 8:00 AM (4:00 PM GMT)
        // Work: 8 hours
        // Expected: 1 day
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assertEquals(new BigDecimal("1.00"), durationCalcHelper.getDaysWorked(startDate, work, assignmentPercentage).getTotalDays().setScale(2));

    }
}
