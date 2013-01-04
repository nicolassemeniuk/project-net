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
 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.calendar.PnCalendar;
import net.project.security.User;

/**
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class WorkingTimeCalendarEntryTest extends TestCase {

    public WorkingTimeCalendarEntryTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(WorkingTimeCalendarEntryTest.class);

        return suite;
    }

    private static User makeUser() {
        User user = new User();
        user.setTimeZone(TimeZone.getTimeZone("PST"));
        user.setLocale(Locale.US);
        return user;
    }

    /**
     */
    public void testMakeNonWorkingDayOfWeek() {

        DayOfWeekEntry day;

        try {
            WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(0);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(8);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(1, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(2, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.TUESDAY);
        assertEquals(3, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.WEDNESDAY);
        assertEquals(4, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.THURSDAY);
        assertEquals(5, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.FRIDAY);
        assertEquals(6, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SATURDAY);
        assertEquals(7, day.getDayNumber());
        assertFalse(day.isWorkingDay());

    }

    /**
     */
    public void testMakeDefaultWorkingDayOfWeek() {

        DayOfWeekEntry day;

        try {
            WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(0);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(8);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(1, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(2, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.TUESDAY);
        assertEquals(3, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.WEDNESDAY);
        assertEquals(4, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.THURSDAY);
        assertEquals(5, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.FRIDAY);
        assertEquals(6, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

        day = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.SATURDAY);
        assertEquals(7, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertDefaultWorkingTimes(day);

    }

    public void testMakeWorkingDayOfWeek() {

        DayOfWeekEntry day;

        try {
            WorkingTimeCalendarEntry.makeWorkingDayOfWeek(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            WorkingTimeCalendarEntry.makeWorkingDayOfWeek(8);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(1, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(2, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.TUESDAY);
        assertEquals(3, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.WEDNESDAY);
        assertEquals(4, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.THURSDAY);
        assertEquals(5, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.FRIDAY);
        assertEquals(6, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

        day = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.SATURDAY);
        assertEquals(7, day.getDayNumber());
        assertTrue(day.isWorkingDay());
        assertTrue("Expected empty working times", day.getWorkingTimes().isEmpty());

    }

    public void testMakeNonWorkingDay() {

        DateEntry dateEntry;

        try {
            WorkingTimeCalendarEntry.makeNonWorkingDate(null);
            fail("Unexpected success");
        } catch (NullPointerException e) {
            // Expected
        }

        dateEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        assertFalse(dateEntry.isWorkingDay());

    }

    public void testMakeWorkingDate() {

        DateEntry dateEntry;

        try {
            WorkingTimeCalendarEntry.makeWorkingDate(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        dateEntry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        assertTrue(dateEntry.isWorkingDay());
        assertTrue("Expected empty working times", dateEntry.getWorkingTimes().isEmpty());

    }

    public void testSetWorkingTimes() {

        WorkingTimeCalendarEntry entry;
        Collection workingTimes;

        // Null
        try {
            entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
            entry.setWorkingTimes(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Empty times; expect exception
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        try {
            entry.setWorkingTimes(Collections.EMPTY_LIST);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Two entries; Zero hours in times
        workingTimes = Arrays.asList(
                new WorkingTime[]{
                    new WorkingTime(7, 0, 7, 0),
                    new WorkingTime(8, 0, 8, 0)
                }
        );
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        try {
            entry.setWorkingTimes(workingTimes);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Out of order entries
        workingTimes = Arrays.asList(
                new WorkingTime[]{
                    new WorkingTime(7, 0, 8, 0),
                    new WorkingTime(9, 0, 10, 0),
                    new WorkingTime(3, 0, 4, 0)
                }
        );
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        try {
            entry.setWorkingTimes(workingTimes);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Various; 5 hours 27 minutes of times
        workingTimes = Arrays.asList(
                new WorkingTime[]{
                    new WorkingTime(0, 0, 1, 0),
                    new WorkingTime(1, 12, 2, 47),
                    new WorkingTime(17, 1, 19, 23),
                    new WorkingTime(23, 15, 23, 45)
                }
        );
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(workingTimes);
        assertEquals(new SimpleTimeQuantity(5, 27), entry.getWorkingHours());
        assertEquals(4, entry.getWorkingTimes().size());

        // 24 hour working times; MSP allows 0:00 as an upper bound
        workingTimes = Arrays.asList(
                new WorkingTime[]{
                    new WorkingTime(0, 0, 24, 0)
                }
        );
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(workingTimes);
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getWorkingHours());
        assertEquals(1, entry.getWorkingTimes().size());

        // 12 hour working times; MSP allows 0:00 as an upper bound
        workingTimes = Arrays.asList(
                new WorkingTime[]{
                    new WorkingTime(12, 0, 24, 0)
                }
        );
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(workingTimes);
        assertEquals(new SimpleTimeQuantity(12, 0), entry.getWorkingHours());
        assertEquals(1, entry.getWorkingTimes().size());

    }

    public void testGetWorkingTimes() {

        WorkingTimeCalendarEntry entry;
        Collection workingTimes;
        WorkingTime time1;
        WorkingTime time2;

        // Initially empty
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertTrue("Expected working times to be empty; found " + entry.getWorkingTimes().size(), entry.getWorkingTimes().isEmpty());

        // Two entries
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        time1 = new WorkingTime(0, 0, 8, 0);
        time2 = new WorkingTime(12, 0, 20, 0);
        workingTimes = Arrays.asList(new WorkingTime[]{time1, time2});
        entry.setWorkingTimes(workingTimes);
        assertEquals(2, entry.getWorkingTimes().size());

        // Check positions
        int count = 0;
        for (Iterator it = entry.getWorkingTimes().iterator(); it.hasNext(); count++) {
            WorkingTime nextworkingTime = (WorkingTime) it.next();
            switch (count) {
                case 0:
                    nextworkingTime.equals(new WorkingTime(0, 0, 24, 0));
                    break;
                case 1:
                    nextworkingTime.equals(new WorkingTime(12, 0, 20, 0));
                    break;
            }
        }


    }

    public void testIsWorkingTime() {

        WorkingTimeCalendarEntry entry;

        // Non working day
        // All times are non-working
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        checkIsWorkingTimeNone(entry);

        // Working day
        // Default working times
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        checkIsWorkingTimeDefault(entry);

        // Non working date
        // All times are non-working
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        checkIsWorkingTimeNone(entry);

        // Working date
        // Default working times
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        addDefaultTimes(entry);
        checkIsWorkingTimeDefault(entry);
    }

    public void testIsWorkingTimeNonDefaultTime() {

        WorkingTimeCalendarEntry entry;

        // 24 hour; 0:00 - 23:59 are working times; 24:00 is not
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        for (int i = 0; i < (60 * 24); i++) {
            assertTrue(entry.isWorkingTime(makeTime(i)));
        }
        assertFalse(entry.isWorkingTime(new Time(24, 0)));

        // Night shift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertTrue(entry.isWorkingTime(new Time(0, 0)));
        assertTrue(entry.isWorkingTime(new Time(2, 59)));
        assertFalse(entry.isWorkingTime(new Time(3, 0)));
        assertFalse(entry.isWorkingTime(new Time(3, 1)));
        assertFalse(entry.isWorkingTime(new Time(3, 59)));
        assertTrue(entry.isWorkingTime(new Time(4, 0)));
        assertTrue(entry.isWorkingTime(new Time(4, 1)));
        assertTrue(entry.isWorkingTime(new Time(7, 59)));
        assertFalse(entry.isWorkingTime(new Time(8, 0)));
        assertFalse(entry.isWorkingTime(new Time(8, 1)));
        assertFalse(entry.isWorkingTime(new Time(22, 59)));
        assertTrue(entry.isWorkingTime(new Time(23, 0)));
        assertTrue(entry.isWorkingTime(new Time(23, 59)));
        assertFalse(entry.isWorkingTime(new Time(24, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertFalse(entry.isWorkingTime(new Time(7, 59)));
        assertTrue(entry.isWorkingTime(new Time(8, 0)));
        assertFalse(entry.isWorkingTime(new Time(8, 1)));

    }

    public void testIsWorkingTimeForEnd() {

        WorkingTimeCalendarEntry entry;

        // Null
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        try {
            entry.isWorkingTimeForEnd(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Non working day of week; false always
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertFalse(entry.isWorkingTimeForEnd(new Time(0, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(12, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(13, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(17, 0)));

        // Working day of week
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertFalse(entry.isWorkingTimeForEnd(new Time(0, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(8, 1)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(10, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(11, 59)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(12, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(13, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(13, 1)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(17, 0)));

    }

    public void testIsWorkingTimeForEndNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;

        // 24 hour; 0:00 is not, every other time is
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertFalse(entry.isWorkingTimeForEnd(new Time(0, 0)));
        for (int i = 1; i < (60 * 24); i++) {
            assertTrue("Expected time " + makeTime(i) + " to be working time for end time; was not", entry.isWorkingTimeForEnd(makeTime(i)));
        }
        assertFalse(entry.isWorkingTimeForEnd(new Time(24, 0)));

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertFalse(entry.isWorkingTimeForEnd(new Time(0, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(0, 1)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(2, 59)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(3, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(3, 1)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(4, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(4, 1)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(7, 59)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 1)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(23, 0)));
        assertTrue(entry.isWorkingTimeForEnd(new Time(23, 59)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(24, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(7, 59)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 0)));
        assertFalse(entry.isWorkingTimeForEnd(new Time(8, 1)));

    }

    public void testGetWorkingHours() {

        WorkingTimeCalendarEntry entry;

        // Non working day
        // 0 hours
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHours());

        // Working day, default times
        // 8 hours
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHours());

        // Non working date
        // 0 hours
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHours());

        // Working date, default times
        // 8 hours
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        addDefaultTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHours());
    }

    public void testGetWorkingHoursNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getWorkingHours());

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHours());

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getWorkingHours());

    }

    public void testHasNextWorkingTime() {

        WorkingTimeCalendarEntry entry;

        // Non working day of week
        // Has no next working time
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        checkHasNextWorkingTimeNone(entry);

        // Working day of week, default times
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        checkHasNextWorkingTimeDefault(entry);

        // Non working date
        // Has no next working time
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        checkHasNextWorkingTimeNone(entry);

        // Working date, default times
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        addDefaultTimes(entry);
        checkHasNextWorkingTimeDefault(entry);
    }

    public void testHasNextWorkingTimeNonDefaultTimes() {
        WorkingTimeCalendarEntry entry;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertTrue(entry.hasNextWorkingTime(new Time(0, 0)));
        // Never has a next working time from 00:01 to 24:00
        for (int i = 1; i <= (60 * 24); i++) {
            assertFalse(entry.hasNextWorkingTime(makeTime(i)));
        }

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertTrue(entry.hasNextWorkingTime(new Time(0, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(3, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(4, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(8, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(9, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(22, 59)));
        assertTrue(entry.hasNextWorkingTime(new Time(23, 0)));
        assertFalse(entry.hasNextWorkingTime(new Time(23, 59)));
        assertFalse(entry.hasNextWorkingTime(new Time(24, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertTrue(entry.hasNextWorkingTime(new Time(0, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(7, 59)));
        assertTrue(entry.hasNextWorkingTime(new Time(8, 0)));
        assertFalse(entry.hasNextWorkingTime(new Time(8, 1)));

    }

    /**
     * Checks that there are no next working times for the specified entry.
     * @param entry the entry to check
     */
    private static void checkHasNextWorkingTimeNone(WorkingTimeCalendarEntry entry) {
        assertFalse(entry.hasNextWorkingTime(new Time(0, 0)));
        assertFalse(entry.hasNextWorkingTime(new Time(12, 30)));
        assertFalse(entry.hasNextWorkingTime(new Time(17, 0)));
    }

    /**
     * Checks the next working time for default working times.
     * @param entry the entry with default working times
     */
    private static void checkHasNextWorkingTimeDefault(WorkingTimeCalendarEntry entry) {
        assertTrue(entry.hasNextWorkingTime(new Time(0, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(7, 59)));
        assertTrue(entry.hasNextWorkingTime(new Time(8, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(12, 0)));
        assertTrue(entry.hasNextWorkingTime(new Time(12, 30)));
        assertTrue(entry.hasNextWorkingTime(new Time(12, 59)));
        assertTrue(entry.hasNextWorkingTime(new Time(13, 00)));
        assertFalse(entry.hasNextWorkingTime(new Time(15, 30)));
        assertFalse(entry.hasNextWorkingTime(new Time(17, 00)));
        assertFalse(entry.hasNextWorkingTime(new Time(23, 00)));
    }

    public void testHasPreviousWorkingTime() {

        WorkingTimeCalendarEntry entry;

        // Non working day of week
        // Has no previous working time
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        checkHasPreviousWorkingTimeNone(entry);

        // Working day of week, default times
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        checkHasPreviousWorkingTimeDefault(entry);

        // Non working date
        // Has no previous working time
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        checkHasPreviousWorkingTimeNone(entry);

        // Working date, default times
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        addDefaultTimes(entry);
        checkHasPreviousWorkingTimeDefault(entry);
    }

    public void testHasPreviousWorkingTimeNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        // Never has a previous working time
        for (int i = 0; i < (60 * 24); i++) {
            assertFalse("Expected to find no previous time for " + makeTime(i), entry.hasPreviousWorkingTime(makeTime(i)));
        }
        assertTrue(entry.hasPreviousWorkingTime(new Time(24, 0)));

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertTrue(entry.hasPreviousWorkingTime(new Time(24, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(23, 59)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(23, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(8, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(4, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(3, 1)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(3, 0)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(2, 59)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(0, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(8, 2)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(8, 1)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(8, 0)));

    }

    private static void checkHasPreviousWorkingTimeNone(WorkingTimeCalendarEntry entry) {
        assertFalse(entry.hasPreviousWorkingTime(new Time(23, 59)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(17, 01)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(17, 0)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(8, 0)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(7, 59)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(0, 0)));
    }

    private static void checkHasPreviousWorkingTimeDefault(WorkingTimeCalendarEntry entry) {
        assertTrue(entry.hasPreviousWorkingTime(new Time(23, 59)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(17, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(13, 0)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(12, 30)));
        assertTrue(entry.hasPreviousWorkingTime(new Time(12, 00)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(10, 30)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(8, 0)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(7, 59)));
        assertFalse(entry.hasPreviousWorkingTime(new Time(0, 0)));
    }

    public void testUpdateWithStartOfNextWorkingTime() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;

        // Non working day
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);

        try {
            entry.updateWithStartOfNextWorkingTime(cal, new Time(8, 30));
            fail("Unexpected success");

        } catch (IllegalStateException e) {
            // Expected
        }

        // working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);

        // 8:00 AM next working time after midnight
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(0, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8:00 AM next working time after 7:59 AM
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(7, 59));
        assertEquals(expectedDate, cal.getTime());

        // 8:00 AM next working time after 8:00 AM
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1:00 PM next working time after 12:00 PM
        cal.set(2003, 4, 5, 13, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(12, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1:00 PM next working time after 12:59 PM
        cal.set(2003, 4, 5, 13, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(12, 59));
        assertEquals(expectedDate, cal.getTime());

        // 1:00 PM next working time after 1:00 PM
        cal.set(2003, 4, 5, 13, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(13, 0));
        assertEquals(expectedDate, cal.getTime());

        // No next working time after 5:00 PM
        try {
            cal.set(2003, 4, 5, 0, 0, 0);
            entry.updateWithStartOfNextWorkingTime(cal, new Time(17, 00));
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

    }

    public void testUpdateWithStartOfNextWorkingTimeNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        //
        // 24 hour
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        entry.updateWithStartOfNextWorkingTime(cal, new Time(0, 0));
        assertEquals(expectedDate, cal.getTime());
        // There is no next working time betwen 00:01 and 23:59
        for (int i = 1; i < (60 * 24); i++) {
            try {
                entry.updateWithStartOfNextWorkingTime(cal, makeTime(i));
                fail("Expected IllegalStateException");
            } catch (IllegalStateException e) {
                // Expected
            }
        }

        //
        // Nightshift
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);

        // 0:00 AM next after midnight
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(0, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4:00 AM next after 3:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(3, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4:00 AM next after 3:59 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(3, 59));
        assertEquals(expectedDate, cal.getTime());

        // 4:00 AM next after 4:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 11:00 PM next after 10:59 PM
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(22, 59));
        assertEquals(expectedDate, cal.getTime());

        // 11:00 PM next after 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(23, 0));
        assertEquals(expectedDate, cal.getTime());

        // No next working time after 11:01 PM
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.updateWithStartOfNextWorkingTime(cal, new Time(23, 1));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }

        //
        // One minute
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));

        // 8:00 AM next after midnight
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(0, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8:00 AM next after 7:59 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(7, 59));
        assertEquals(expectedDate, cal.getTime());

        // 8:00 AM next after 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfNextWorkingTime(cal, new Time(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // No next working time after 8:01 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.updateWithStartOfNextWorkingTime(cal, new Time(8, 1));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }

    }

    public void testUpdateWithEndOfPreviousWorkingTime() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;

        // Non working day
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);

        try {
            entry.updateWithEndOfPreviousWorkingTime(cal, new Time(12, 30));
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);

        // 5:00 PM previous working time end before 11:59 PM
        cal.set(2003, 4, 5, 17, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(23, 59));
        assertEquals(expectedDate, cal.getTime());

        // 5:00 PM previous working time end before 5:00 PM
        cal.set(2003, 4, 5, 17, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(17, 0));
        assertEquals(expectedDate, cal.getTime());

        // 12:00 PM previous working time end before 3:30 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(15, 30));
        assertEquals(expectedDate, cal.getTime());

        // 12:00 PM previous working time end before 1:00 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(13, 00));
        assertEquals(expectedDate, cal.getTime());

        // 12:00 PM previous working time end before 12:30 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(12, 30));
        assertEquals(expectedDate, cal.getTime());

        // 12:00 PM previous working time before 12:00 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(12, 0));
        assertEquals(expectedDate, cal.getTime());

        // No previous working time before 11:59
        try {
            cal.set(2003, 4, 5, 0, 0, 0);
            entry.updateWithEndOfPreviousWorkingTime(cal, new Time(11, 59));
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        try {
            cal.set(2003, 4, 5, 0, 0, 0);
            entry.updateWithEndOfPreviousWorkingTime(cal, new Time(8, 0));
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

    }

    public void testUpdateWithEndOfPreviousWorkingTimeNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        //
        // 24 hour
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(24, 0));
        assertEquals(expectedDate, cal.getTime());

        // No previous working time with 0:00 to 23:59
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        for (int i = 0; i < (60 * 24); i++) {
            try {
                entry.updateWithEndOfPreviousWorkingTime(cal, makeTime(i));
                fail("Unexpected success");
            } catch (IllegalStateException e) {
                // Expected
            }
        }

        //
        // Nightshift
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);

        // 4th @ 0:00 is previous working time end before 3rd @ 24:00
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(24, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8:00 AM is previous end before any time 8:00 AM - 23:59 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(23, 59));
        assertEquals(expectedDate, cal.getTime());
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(12, 0));
        assertEquals(expectedDate, cal.getTime());
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // 3:00 AM is previous end before any time 3:00 AM - 7:59 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 3, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(7, 59));
        assertEquals(expectedDate, cal.getTime());
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(3, 1));
        assertEquals(expectedDate, cal.getTime());
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfPreviousWorkingTime(cal, new Time(3, 0));
        assertEquals(expectedDate, cal.getTime());

        // No previous working time before 3:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.updateWithEndOfPreviousWorkingTime(cal, new Time(2, 59));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }

        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.updateWithEndOfPreviousWorkingTime(cal, new Time(0, 0));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }

    }

    public void testUpdateWithStartOfFirstWorkingTime() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;

        // Non working day
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);

        try {
            entry.updateWithStartOfFirstWorkingTime(cal);
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.updateWithStartOfFirstWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

    }

    public void testUpdateWithStartOfFirstWorkingTimeNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 11, 11);
        entry.updateWithStartOfFirstWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 11, 11);
        entry.updateWithStartOfFirstWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithStartOfFirstWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

    }

    public void testUpdateWithEndOfLastWorkingTime() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        // Non working day of week; has no working times
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        try {
            entry.updateWithEndOfLastWorkingTime(cal);
            fail("Expected IllegalStatException");
        } catch (IllegalStateException e) {
            // Expected
        }

        // working day
        // Date:     September 2, 2003 @ 14:27:12.011
        // Expected: September 2, 2003 @ 17:00:00.000
        cal.set(2003, Calendar.SEPTEMBER, 2, 17, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        expectedDate = cal.getTime();

        cal.set(2003, Calendar.SEPTEMBER, 2, 14, 27, 12);
        cal.set(Calendar.MILLISECOND, 11);
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        entry.updateWithEndOfLastWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

    }

    public void testUpdateWithEndOfLastWorkingTimeNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfLastWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfLastWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.updateWithEndOfLastWorkingTime(cal);
        assertEquals(expectedDate, cal.getTime());

    }

    public void testAdvanceHours() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;

        // Non working day
        // Throws exception
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        try {
            cal.set(2003, 4, 5, 10, 30, 0);
            entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
            fail("Unexepcted success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);

        // 1 hour from midnight is 9:00 AM
        cal.set(2003, 4, 5, 9, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 0, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 8:00 AM is 9:00 AM
        cal.set(2003, 4, 5, 9, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 8:30 AM is 9:30 AM
        cal.set(2003, 4, 5, 9, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 30, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 7:00 AM is 12:00 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 7, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 8:00 AM is 12:00 PM
        cal.set(2003, 4, 5, 12, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 3.5 hours from 8:00 AM is 11:30 PM
        cal.set(2003, 4, 5, 11, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(3, 30));
        assertEquals(expectedDate, cal.getTime());

        // Test spanning times

        // 4 hours from 9:00 AM is 2:00 PM
        cal.set(2003, 4, 5, 14, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 9, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 10:30 AM is 15:30 PM
        cal.set(2003, 4, 5, 15, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 10, 30, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 12:00 PM is 5:00 PM
        cal.set(2003, 4, 5, 17, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 12, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 3.5 hours from 10:00 AM is 2:30 PM
        cal.set(2003, 4, 5, 14, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 10, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(3, 30));
        assertEquals(expectedDate, cal.getTime());

        // 7 hours from 8:00 AM is 4:00 PM
        cal.set(2003, 4, 5, 16, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(7, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8 hours from 8:00 AM is 5:00 PM
        cal.set(2003, 4, 5, 17, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 8, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // 9 hours from 8:00 AM is not allowed
        try {
            cal.set(2003, 4, 5, 8, 0, 0);
            entry.advanceHours(cal, new SimpleTimeQuantity(9, 0));
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // 1 hour from 16:01 is not allowed
        try {
            cal.set(2003, 4, 5, 16, 1, 0);
            entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    public void testAdvanceHoursNoNDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        //
        // 24 hour
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);

        // One minute from 00:00 is 00:01
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // 12 hours from 00:00 is 12:00
        cal.set(2003, Calendar.SEPTEMBER, 3, 12, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(12, 0));
        assertEquals(expectedDate, cal.getTime());

        // 24 hours from 00:00 is 00:00 on next day
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(24, 0));
        assertEquals(expectedDate, cal.getTime());

        // 12 hours from 12:00 is 00:00 on next day
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 12, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(12, 0));
        assertEquals(expectedDate, cal.getTime());

        // 24 hours 1 minute from 0:00 is error
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.advanceHours(cal, new SimpleTimeQuantity(24, 1));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // 1 hour from 23:30 is error
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 30);
        try {
            entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        //
        // Nightshift
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);

        // One minute from 00:00 is 00:01
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // 3 hours 1 minute from 00:00 is 4:01
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(3, 1));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 2:30 is 4:30
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 30);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 2, 30);
        entry.advanceHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 6.5 hours from 1:00 AM is 23:30
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 30);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 1, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(6, 30));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 4:00 AM is 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8 hours from 00:00 is 00:00 next day
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // 31 minutes from 23:30 is not allowed
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 30);
        try {
            entry.advanceHours(cal, new SimpleTimeQuantity(0, 31));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        //
        // One minute
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));

        // One minute from 00:00 is 8:01
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // One minute from 08:00 is 8:01
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 1);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        entry.advanceHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // Two minutes is not allowed
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        try {
            entry.advanceHours(cal, new SimpleTimeQuantity(0, 20));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    public void testRetardHours() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date expectedDate;

        // Non working day
        // Throws exception
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        try {
            cal.set(2003, 4, 5, 15, 30, 0);
            entry.retardHours(cal, new SimpleTimeQuantity(4, 0));
            fail("Unexepcted success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);

        // 1 hour from 11:00 PM is 4:00 PM
        cal.set(2003, 4, 5, 16, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 23, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 5:00 PM is 4:00 PM
        cal.set(2003, 4, 5, 16, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 4:30 PM is 3:30 PM
        cal.set(2003, 4, 5, 15, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 16, 30, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 5:00 PM is 1:00 PM
        cal.set(2003, 4, 5, 13, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 3.5 hours from 5:00 PM is 1:30 PM
        cal.set(2003, 4, 5, 13, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(3, 30));
        assertEquals(expectedDate, cal.getTime());

        // Test spanning times

        // 4 hours from 4:00 PM is 11:00 AM
        cal.set(2003, 4, 5, 11, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 16, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 4:30 PM is 10:30 AM
        cal.set(2003, 4, 5, 10, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 15, 30, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 4 hours from 1:00 PM is 8:00 AM
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 13, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(4, 0));
        assertEquals(expectedDate, cal.getTime());

        // 3.5 hours from 3:00 PM is 10:30 AM
        cal.set(2003, 4, 5, 10, 30, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 15, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(3, 30));
        assertEquals(expectedDate, cal.getTime());

        // 7 hours from 4:00 PM is 8:00 AM
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 16, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(7, 0));
        assertEquals(expectedDate, cal.getTime());

        // 8 hours from 5:00 PM is 8:00 AM
        cal.set(2003, 4, 5, 8, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, 4, 5, 17, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(8, 0));
        assertEquals(expectedDate, cal.getTime());

        // 9 hours from 5:00 PM is not allowed
        try {
            cal.set(2003, 4, 5, 17, 0, 0);
            entry.retardHours(cal, new SimpleTimeQuantity(9, 0));
            fail("Unexpected success");
        } catch (Exception e) {
            // Expected
        }

        // 1 hour from 8:59 AM is not allowed
        try {
            cal.set(2003, 4, 5, 8, 59, 0);
            entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
            fail("Unexpected success");
        } catch (Exception e) {
            // Expected
        }

    }

    public void testRetardHoursNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;
        PnCalendar cal = new PnCalendar(makeUser());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expectedDate;

        //
        // 24 hour
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);

        // 24 hours from 0:00 is 0:00 on previous day
        cal.set(2003, Calendar.SEPTEMBER, 2, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(24, 0));
        assertEquals(expectedDate, cal.getTime());

        // 12 hours from 12:00 is 0:00
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 12, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(12, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 23:00 is 22:00
        cal.set(2003, Calendar.SEPTEMBER, 3, 22, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 23 hours 59 minutes from 23:59 is 0:00
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 59);
        entry.retardHours(cal, new SimpleTimeQuantity(23, 59));
        assertEquals(expectedDate, cal.getTime());

        //
        // Nightshift
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);

        // 1 hour from 0:00 is 23:00 on previous day
        cal.set(2003, Calendar.SEPTEMBER, 2, 23, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 hour from 23:00 is 7:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 7, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 23, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(1, 0));
        assertEquals(expectedDate, cal.getTime());

        // 1 minute from 4:00 AM is 2:59 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 2, 59);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 4, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // 3 hours from 3:00 AM is 0:00
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 3, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(3, 0));
        assertEquals(expectedDate, cal.getTime());

        //
        // One minute
        //
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));

        // One minute from 9:00 AM is 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 9, 0);
        entry.retardHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // One minute from 8:01 AM is 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expectedDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 1);
        entry.retardHours(cal, new SimpleTimeQuantity(0, 1));
        assertEquals(expectedDate, cal.getTime());

        // One minute from 8:00 AM is error
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        try {
            entry.retardHours(cal, new SimpleTimeQuantity(0, 1));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Two minutes from 9:00 AM is error
        // One minute from 8:00 AM is error
        cal.set(2003, Calendar.SEPTEMBER, 3, 9, 0);
        try {
            entry.retardHours(cal, new SimpleTimeQuantity(0, 2));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    public void testGetRemainingWorkingHours() {

        WorkingTimeCalendarEntry entry;

        // Non working day
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(8, 0)));

        // Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getRemainingWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getRemainingWorkingHours(new Time(7, 59)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getRemainingWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(7, 30), entry.getRemainingWorkingHours(new Time(8, 30)));
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getRemainingWorkingHours(new Time(9, 0)));
        assertEquals(new SimpleTimeQuantity(6, 15), entry.getRemainingWorkingHours(new Time(9, 45)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getRemainingWorkingHours(new Time(12, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getRemainingWorkingHours(new Time(12, 30)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getRemainingWorkingHours(new Time(12, 59)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getRemainingWorkingHours(new Time(13, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getRemainingWorkingHours(new Time(14, 0)));
        assertEquals(new SimpleTimeQuantity(2, 0), entry.getRemainingWorkingHours(new Time(15, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getRemainingWorkingHours(new Time(16, 0)));
        assertEquals(new SimpleTimeQuantity(0, 15), entry.getRemainingWorkingHours(new Time(16, 45)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(23, 59)));

    }

    public void testGetRemainingWorkingHoursNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getRemainingWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(12, 0), entry.getRemainingWorkingHours(new Time(12, 0)));
        assertEquals(new SimpleTimeQuantity(2, 47), entry.getRemainingWorkingHours(new Time(21, 13)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(24, 0)));

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getRemainingWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getRemainingWorkingHours(new Time(1, 0)));
        assertEquals(new SimpleTimeQuantity(5, 0), entry.getRemainingWorkingHours(new Time(3, 0)));
        assertEquals(new SimpleTimeQuantity(5, 0), entry.getRemainingWorkingHours(new Time(4, 0)));
        assertEquals(new SimpleTimeQuantity(4, 30), entry.getRemainingWorkingHours(new Time(4, 30)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getRemainingWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getRemainingWorkingHours(new Time(22, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getRemainingWorkingHours(new Time(23, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(24, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getRemainingWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getRemainingWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(8, 2)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getRemainingWorkingHours(new Time(24, 0)));

    }

    public void testGetEarlierWorkingHours() {

        WorkingTimeCalendarEntry entry;

        // Non working day; always zero
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(12, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(23, 59)));

        // Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getEarlierWorkingHours(new Time(8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 30), entry.getEarlierWorkingHours(new Time(8, 30)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getEarlierWorkingHours(new Time(9, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getEarlierWorkingHours(new Time(12, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getEarlierWorkingHours(new Time(12, 30)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getEarlierWorkingHours(new Time(13, 0)));
        assertEquals(new SimpleTimeQuantity(4, 1), entry.getEarlierWorkingHours(new Time(13, 1)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getEarlierWorkingHours(new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getEarlierWorkingHours(new Time(23, 0)));

        // Test minute by minute.  8:00 AM - 12:00 PM
        for (int i= 0; i <= (4 * 60); i++) {
            BigDecimal calcHours = new BigDecimal(i).divide(new BigDecimal(60), 3, BigDecimal.ROUND_HALF_UP);
            int hour = (8 + i / 60);
            int minute = (0 + i % 60);
            assertEquals(new SimpleTimeQuantity(calcHours), entry.getEarlierWorkingHours(new Time(hour, minute)));
        }

        // Test minute by minute.  1:00 PM - 5:00 PM
        for (int i= 0; i <= (4 * 60); i++) {
            BigDecimal calcHours = new BigDecimal(i).divide(new BigDecimal(60), 3, BigDecimal.ROUND_HALF_UP).add(new BigDecimal("4"));
            int hour = (13 + i / 60);
            int minute = (0 + i % 60);
            assertEquals(new SimpleTimeQuantity(calcHours), entry.getEarlierWorkingHours(new Time(hour, minute)));
        }

    }

    public void testGetEarlierWorkingHoursNonDefaultTimes() {

        WorkingTimeCalendarEntry entry;

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getEarlierWorkingHours(new Time(1, 0)));
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getEarlierWorkingHours(new Time(24, 0)));

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getEarlierWorkingHours(new Time(1, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getEarlierWorkingHours(new Time(3, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getEarlierWorkingHours(new Time(4, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getEarlierWorkingHours(new Time(5, 0)));
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getEarlierWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getEarlierWorkingHours(new Time(23, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getEarlierWorkingHours(new Time(24, 0)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getEarlierWorkingHours(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getEarlierWorkingHours(new Time(8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getEarlierWorkingHours(new Time(24, 0)));

    }

    public void testGetTimesWorkedAfter() {

        WorkingTimeCalendarEntry entry;
        SimpleTimeQuantity work;


        //
        // Work = 24 hours (work for the rest of the day)
        //
        work = new SimpleTimeQuantity(24, 0);

        // Non working day; zero hours worked
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // Default Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(7, 59), entry.getTimesWorkedAfter(new Time(8, 1), work).getDuration());
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getTimesWorkedAfter(new Time(9, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getTimesWorkedAfter(new Time(12, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getTimesWorkedAfter(new Time(13, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(16, 59), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(17, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(16, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(12, 0), entry.getTimesWorkedAfter(new Time(12, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(5, 0), entry.getTimesWorkedAfter(new Time(3, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getTimesWorkedAfter(new Time(6, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(8, 1), work).getDuration());

        //
        // Miscellaneous work
        //
        work = new SimpleTimeQuantity(8, 0);

        // Non working day; zero hours worked
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // Default Working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(7, 59), entry.getTimesWorkedAfter(new Time(8, 1), work).getDuration());
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getTimesWorkedAfter(new Time(9, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getTimesWorkedAfter(new Time(12, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getTimesWorkedAfter(new Time(13, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(16, 59), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(17, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // 24 hour
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(12, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // Nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(5, 0), entry.getTimesWorkedAfter(new Time(3, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getTimesWorkedAfter(new Time(6, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getTimesWorkedAfter(new Time(23, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(24, 0), work).getDuration());

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(0, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getTimesWorkedAfter(new Time(8, 1), work).getDuration());

        //
        // Test specific issues
        //

        // Issue: We were having a problem with a work of 2.666
        // It was returning a value of 2.650
        // That work translates to 2 hours 39.96 minutes, which is 2 hours 40 minutes
        // when rounded.  2 hours 40 minutes is 2.667 when converted back
        work = new SimpleTimeQuantity(2, 40);
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(2, 40), entry.getTimesWorkedAfter(new Time(8, 0), work).getDuration());

    }

    public void testGetWorkingHoursBetween() {

        WorkingTimeCalendarEntry entry;

        // Non working day
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(24, 0)));

        // Default working day
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(24, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHoursBetween(new Time(8, 0), new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getWorkingHoursBetween(new Time(8, 0), new Time(13, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getWorkingHoursBetween(new Time(12, 0), new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(4, 0), entry.getWorkingHoursBetween(new Time(10, 0), new Time(15, 0)));
        assertEquals(new SimpleTimeQuantity(3, 30), entry.getWorkingHoursBetween(new Time(10, 45), new Time(15, 15)));

        // 24 hour day
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        add24HourTimes(entry);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(24, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(24, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHoursBetween(new Time(8, 0), new Time(16, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getWorkingHoursBetween(new Time(23, 0), new Time(24, 0)));

        // Night shift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        addNightshiftTimes(entry);
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(8, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(24, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(3, 0)));
        assertEquals(new SimpleTimeQuantity(7, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(23, 0)));
        assertEquals(new SimpleTimeQuantity(1, 0), entry.getWorkingHoursBetween(new Time(23, 0), new Time(24, 0)));
        assertEquals(new SimpleTimeQuantity(5, 30), entry.getWorkingHoursBetween(new Time(2, 0), new Time(23, 30)));

        // One minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(0, 0), new Time(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), entry.getWorkingHoursBetween(new Time(9, 0), new Time(10, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getWorkingHoursBetween(new Time(7, 0), new Time(10, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), entry.getWorkingHoursBetween(new Time(8, 0), new Time(8, 1)));

    }

    /**
     * Checks that no times are working times for the specified entry
     * @param entry the entry to check
     */
    private static void checkIsWorkingTimeNone(WorkingTimeCalendarEntry entry) {
        assertFalse(entry.isWorkingTime(new Time(0, 0)));
        assertFalse(entry.isWorkingTime(new Time(8, 0)));
        assertFalse(entry.isWorkingTime(new Time(10, 0)));
        assertFalse(entry.isWorkingTime(new Time(12, 0)));
        assertFalse(entry.isWorkingTime(new Time(12, 30)));
        assertFalse(entry.isWorkingTime(new Time(13, 0)));
        assertFalse(entry.isWorkingTime(new Time(15, 0)));
        assertFalse(entry.isWorkingTime(new Time(17, 0)));
        assertFalse(entry.isWorkingTime(new Time(17, 30)));
    }

    /**
     * Checks that the default working times are correct for the specified
     * entry.
     * @param entry the entry to check
     */
    private static void checkIsWorkingTimeDefault(WorkingTimeCalendarEntry entry) {
        assertFalse(entry.isWorkingTime(new Time(0, 0)));
        assertFalse(entry.isWorkingTime(new Time(7, 59)));
        assertTrue(entry.isWorkingTime(new Time(8, 0)));
        assertTrue(entry.isWorkingTime(new Time(10, 0)));
        assertFalse(entry.isWorkingTime(new Time(12, 0)));
        assertFalse(entry.isWorkingTime(new Time(12, 30)));
        assertTrue(entry.isWorkingTime(new Time(13, 0)));
        assertTrue(entry.isWorkingTime(new Time(15, 0)));
        assertFalse(entry.isWorkingTime(new Time(17, 0)));
        assertFalse(entry.isWorkingTime(new Time(17, 30)));
    }

    /**
     * Adds default working time (8:00 - 12:00, 13:00 - 17:00).
     * @param entry the entry to which to add the times; existing times
     * are replaced
     */
    static void addDefaultTimes(WorkingTimeCalendarEntry entry) {
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(8, 0, 12, 0),
            new WorkingTime(13, 0, 17, 0)
        }));
    }

    /**
     * Adds 24-hour working time (0:00 - 24:00).
     * @param entry the entry to which to add the times; existing times
     * are replaced
     */
    static void add24HourTimes(WorkingTimeCalendarEntry entry) {
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(0, 0, 24, 0)));
    }

    /**
     * Adds night shift times (0:00 - 3:00, 4:00 - 8:00, 23:00 - 24:00).
     * @param entry the entry to which to add the times; existing times
     * are replaced
     */
    static void addNightshiftTimes(WorkingTimeCalendarEntry entry) {
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(0, 0, 3, 0),
            new WorkingTime(4, 0, 8, 0),
            new WorkingTime(23, 0, 24, 0)
        }));
    }

    /**
     * Asserts that default working times are present in the entry.
     * @param entry the entry to check
     */
    private static void assertDefaultWorkingTimes(WorkingTimeCalendarEntry entry) {
        WorkingTime[] workingTimes = (WorkingTime[]) entry.getWorkingTimes().toArray(new WorkingTime[]{});
        assertEquals(workingTimes[0], new WorkingTime(8, 0, 12, 0));
        assertEquals(workingTimes[1], new WorkingTime(13, 0, 17, 0));
    }

    /**
     * Makes a time based on the specified minutes elapsed in the day.
     * @param minutes the minutes in the range 0 .. (60 * 24)
     * @return the Time representing those elapsed minutes
     */
    private static Time makeTime(int minutes) {
        return new Time((minutes / 60), (minutes % 60));
    }

}
