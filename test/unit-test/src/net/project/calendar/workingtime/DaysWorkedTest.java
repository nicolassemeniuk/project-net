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
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>DaysWorkedTest</code>.
 *
 * @author Tim Morrow
 * @since 7.6.3
 */
public class DaysWorkedTest extends TestCase {

    public DaysWorkedTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DaysWorkedTest.class);

        return suite;
    }

    /**
     * Tests addDay
     */
    public void testAddNonDefaultDay() {

        DaysWorked daysWorked;

        // Null Paramaters
        try {
            daysWorked = new DaysWorked();
            daysWorked.addDay(null, null, null);
            fail("Unexpected success with null parameters");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            daysWorked = new DaysWorked();
            daysWorked.addDay(null, new AggregatedWorkingTimes(), TimeZone.getDefault());
            fail("Unexpected success with null parameters");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            daysWorked = new DaysWorked();
            daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), null, TimeZone.getDefault());
            fail("Unexpected success with null parameters");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            daysWorked = new DaysWorked();
            daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new AggregatedWorkingTimes(), null);
            fail("Unexpected success with null parameters");
        } catch (NullPointerException e) {
            // Expected
        }

        // Non-null parameters
        daysWorked = new DaysWorked();
        daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new AggregatedWorkingTimes(), TimeZone.getDefault());

    }

    public void testAdd() {

        DaysWorked daysWorked1, daysWorked2;
        DayOfYear dayOfYear1;
        DayOfYear dayOfYear2;
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

        // First days worked: a date with default times
        // Second days worked: a different date with default times
        daysWorked1 = new DaysWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked1.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone);

        daysWorked2 = new DaysWorked();
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        daysWorked2.addDay(dayOfYear2, makeDefaultTimesWorked(), timeZone);

        daysWorked1.add(daysWorked2);

        assertEquals(new SimpleTimeQuantity(16, 0), daysWorked1.getDuration());


        // First days worked: a date with default times
        // Second days worked: SAME date with SAME hours
        daysWorked1 = new DaysWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked1.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone);

        daysWorked2 = new DaysWorked();
        daysWorked2.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone);

        daysWorked1.add(daysWorked2);

        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked1.getDuration());

    }

    /**
     * Tests {@link DaysWorked#add(IDaysWorked)} with different time zones.
     */
    public void testAddTimeZones() {

        DaysWorked daysWorked1, daysWorked2;
        DayOfYear dayOfYear1;
        DayOfYear dayOfYear2;
        TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
        TimeZone timeZone2 = TimeZone.getTimeZone("Europe/London");

        // First days worked: a date with default times PST
        // Second days worked: same date with default times GMT
        // Total of 16 hours, overlapped by 1 hour:
        // GMT works (in PST times): 9/24 0-1, 1-2, 2-3, 3-4, 5-6, 6-7, 7-8, 8-9
        // PST works (in PST times): 9/24 8-9, 9-10, 10-11, 11-12, 13-14, 14-15, 15-16, 16-17
        // Union is 15 hours
        daysWorked1 = new DaysWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked1.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone1);
        daysWorked2 = new DaysWorked();
        daysWorked1.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone2);
        daysWorked1.add(daysWorked2);
        assertEquals(new SimpleTimeQuantity(15, 0), daysWorked1.getDuration());

        // First days worked: a date with default times PST
        // Second days worked: a different date with default times GMT
        // No overlapping times
        daysWorked1 = new DaysWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked1.addDay(dayOfYear1, makeDefaultTimesWorked(), timeZone1);
        daysWorked2 = new DaysWorked();
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        daysWorked2.addDay(dayOfYear2, makeDefaultTimesWorked(), timeZone2);
        daysWorked1.add(daysWorked2);
        assertEquals(new SimpleTimeQuantity(16, 0), daysWorked1.getDuration());

    }

    public void testGetDuration() {

        DaysWorked daysWorked;
        AggregatedWorkingTimes timesWorked;
        DayOfYear dayOfYear1;
        DayOfYear dayOfYear2;
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

        // No days
        daysWorked = new DaysWorked();
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());

        // A day, no times
        daysWorked = new DaysWorked();
        daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new AggregatedWorkingTimes(), timeZone);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());

        // A day, default times
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), timesWorked, timeZone);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());

        // Same day more than once
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());

        // 2 different days, both default times
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
        assertEquals(new SimpleTimeQuantity(16, 0), daysWorked.getDuration());

        // Day 1: Default
        // Day 2: 8:00-12:00 13:00-13:40
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 12, 0));
        timesWorked.add(new WorkingTime(13, 0, 13, 40));
        daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
        assertEquals(new SimpleTimeQuantity(12, 40), daysWorked.getDuration());

    }

    public void testGetTotalDays() {
        DaysWorked daysWorked;
        AggregatedWorkingTimes timesWorked;
        DayOfYear dayOfYear1;
        DayOfYear dayOfYear2;
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

        // No days
        daysWorked = new DaysWorked();
        assertEquals(new BigDecimal("0.00"), daysWorked.getTotalDays().setScale(2));

        // A day, no times
        daysWorked = new DaysWorked();
        daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new AggregatedWorkingTimes(), timeZone);
        assertEquals(new BigDecimal("0.00"), daysWorked.getTotalDays().setScale(2));

        // A day, default times
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), timesWorked, timeZone);
        assertEquals(new BigDecimal("1.00"), daysWorked.getTotalDays().setScale(2));

        // Same day more than once
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        assertEquals(new BigDecimal("1.00"), daysWorked.getTotalDays().setScale(2));

        // 2 different days, both default times
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
        assertEquals(new BigDecimal("2.00"), daysWorked.getTotalDays().setScale(2));

        // Day 1: Default
        // Day 2: 8:00-12:00 13:00-13:40
        daysWorked = new DaysWorked();
        timesWorked = makeDefaultTimesWorked();
        dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
        dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 12, 0));
        timesWorked.add(new WorkingTime(13, 0, 13, 40));
        daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
        assertEquals(new SimpleTimeQuantity(12, 40), daysWorked.getDuration());
        assertEquals(new BigDecimal("1.58"), daysWorked.getTotalDays().setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void testGetTotalMinutes() {
            DaysWorked daysWorked;
            AggregatedWorkingTimes timesWorked;
            DayOfYear dayOfYear1;
            DayOfYear dayOfYear2;
            TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

            // No days
            daysWorked = new DaysWorked();
            assertEquals(new BigDecimal("0"), daysWorked.getTotalMinutes());

            // A day, no times
            daysWorked = new DaysWorked();
            daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new AggregatedWorkingTimes(), timeZone);
            assertEquals(new BigDecimal("0"), daysWorked.getTotalMinutes());

            // A day, default times
            daysWorked = new DaysWorked();
            timesWorked = makeDefaultTimesWorked();
            daysWorked.addDay(new DayOfYear(2003, Calendar.SEPTEMBER, 24), timesWorked, timeZone);
            assertEquals(new BigDecimal("480"), daysWorked.getTotalMinutes());

            // Same day more than once
            daysWorked = new DaysWorked();
            timesWorked = makeDefaultTimesWorked();
            dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
            daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
            assertEquals(new BigDecimal("480"), daysWorked.getTotalMinutes());

            // 2 different days, both default times
            daysWorked = new DaysWorked();
            timesWorked = makeDefaultTimesWorked();
            dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
            daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
            daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
            assertEquals(new BigDecimal("960"), daysWorked.getTotalMinutes());

            // Day 1: Default
            // Day 2: 8:00-12:00 13:00-13:40
            daysWorked = new DaysWorked();
            timesWorked = makeDefaultTimesWorked();
            dayOfYear1 = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            daysWorked.addDay(dayOfYear1, timesWorked, timeZone);
            dayOfYear2 = new DayOfYear(2003, Calendar.SEPTEMBER, 25);
            timesWorked = new AggregatedWorkingTimes();
            timesWorked.add(new WorkingTime(8, 0, 12, 0));
            timesWorked.add(new WorkingTime(13, 0, 13, 40));
            daysWorked.addDay(dayOfYear2, timesWorked, timeZone);
            assertEquals(new SimpleTimeQuantity(12, 40), daysWorked.getDuration());
            assertEquals(new BigDecimal("760"), daysWorked.getTotalMinutes());
        }

    private static AggregatedWorkingTimes makeDefaultTimesWorked() {
        AggregatedWorkingTimes timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 12, 0));
        timesWorked.add(new WorkingTime(13, 0, 17, 0));
        return timesWorked;
    }

}
