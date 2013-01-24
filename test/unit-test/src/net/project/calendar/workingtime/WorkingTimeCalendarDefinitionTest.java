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
|     $RCSfile$
|    $Revision: 17095 $
|        $Date: 2008-03-24 13:45:22 +0530 (Mon, 24 Mar 2008) $
|      $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests <code>WorkingTimeCalendarDefinitionTest</code>.
 * @author
 * @since
 */
public class WorkingTimeCalendarDefinitionTest extends TestCase {

    public WorkingTimeCalendarDefinitionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(WorkingTimeCalendarDefinitionTest.class);

        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     */
    public void testMakeDefaultWorkingTimeCalendarDefinition() {

        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Check dates starting on Sunday 15th June 2003 to Saturday 21st June 2003
        Calendar cal = newCalendar();

        cal.setTime(makeDate("06/15/03"));
        assertFalse(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertFalse(calendarDef.isWorkingDay(cal));

    }

    public void testMakeBaseCalendar() {
        WorkingTimeCalendarDefinition calendarDef;

        try {
            WorkingTimeCalendarDefinition.makeBaseCalendar(null);
            fail("Unexpected success with null value");
        } catch (NullPointerException e) {
            // Expected
        }

        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        assertTrue("Day of week entries not empty", calendarDef.getDayOfWeekEntries().isEmpty());
        assertTrue("Date entries not empty", calendarDef.getDateEntries().isEmpty());

    }

    public void testAddEntry() {

        WorkingTimeCalendarDefinition calendarDef;
        WorkingTimeCalendarEntry entry;
        Calendar cal;

        // Day of week entry
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        calendarDef.addEntry(entry);
        assertEquals(entry, calendarDef.getDayOfWeek(Calendar.SUNDAY));

        // Date entry
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30));
        calendarDef.addEntry(entry);
        cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDate("7/30/03"));
        assertEquals(entry, calendarDef.getDate(cal));

    }

    public void testAddDayOfWeekEntry() {

        WorkingTimeCalendarDefinition calendarDef;
        DayOfWeekEntry entry;

        // Non Working Day Of Week
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        calendarDef.addDayOfWeekEntry(entry);
        assertEquals(entry, calendarDef.getDayOfWeek(Calendar.SUNDAY));

        // Working Day Of Week
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.SUNDAY);
        calendarDef.addDayOfWeekEntry(entry);
        assertEquals(entry, calendarDef.getDayOfWeek(Calendar.SUNDAY));

    }

    public void testAddDateEntry() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry entry;
        DayOfYear startDayOfYear;
        DayOfYear endDayOfYear;
        Calendar cal;

        // Single date 7/30/03
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        startDayOfYear = new DayOfYear(2003, Calendar.JULY, 30);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(startDayOfYear);
        calendarDef.addDateEntry(entry);

        cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDate("7/30/03"));
        assertEquals(entry, calendarDef.getDate(cal));

        // Span Dates
        // Ensure that the entry is returned for each date in its range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        startDayOfYear = new DayOfYear(2003, Calendar.JULY, 30);
        endDayOfYear = new DayOfYear(2003, Calendar.AUGUST, 3);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(startDayOfYear, endDayOfYear);
        calendarDef.addDateEntry(entry);

        cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDate("7/30/03"));
        assertEquals(entry, calendarDef.getDate(cal));
        cal.setTime(makeDate("7/31/03"));
        assertEquals(entry, calendarDef.getDate(cal));
        cal.setTime(makeDate("8/1/03"));
        assertEquals(entry, calendarDef.getDate(cal));
        cal.setTime(makeDate("8/1/03"));
        assertEquals(entry, calendarDef.getDate(cal));
        cal.setTime(makeDate("8/1/03"));
        assertEquals(entry, calendarDef.getDate(cal));

    }

    public void testGetDayOfWeek() {

        WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Day zero is illegal
        try {
            calendarDef.getDayOfWeek(0);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Day 8 is illegal
        try {
            calendarDef.getDayOfWeek(8);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Days 1..7
        assertEquals(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY) ,calendarDef.getDayOfWeek(Calendar.SUNDAY));
        assertEquals(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY) ,calendarDef.getDayOfWeek(Calendar.MONDAY));
        assertEquals(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.TUESDAY) ,calendarDef.getDayOfWeek(Calendar.TUESDAY));
        assertEquals(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.WEDNESDAY) ,calendarDef.getDayOfWeek(Calendar.WEDNESDAY));
        assertEquals(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.THURSDAY) ,calendarDef.getDayOfWeek(Calendar.THURSDAY));
        assertEquals(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.FRIDAY) ,calendarDef.getDayOfWeek(Calendar.FRIDAY));
        assertEquals(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SATURDAY) ,calendarDef.getDayOfWeek(Calendar.SATURDAY));

    }

    public void testGetDate() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry entry;
        Calendar cal = newCalendar();

        // Non working days: Sunday, Saturday
        // Non working dates: Wednesday June 18th, Saturday June 21st
        // Working dates: Sunday June 22nd, Tuesday June 24th
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03", "06/21/03"},
                new String[]{"06/22/03", "06/24/03"});

        // Null parameter
        try {
            calendarDef.getDate(null);
            fail("Unexpected success");
        } catch (NullPointerException e) {
            // Expected
        }


        // Sunday June 15th - no date
        cal.setTime(makeDate("06/15/03"));
        assertNull(calendarDef.getDate(cal));

        // Wednesday June 18th, Non working day
        cal.setTime(makeDate("06/18/03"));
        assertEquals(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JUNE, 18)), calendarDef.getDate(cal));

        // Saturday June 21st, Non working day
        cal.setTime(makeDate("06/21/03"));
        assertEquals(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JUNE, 21)), calendarDef.getDate(cal));

        // Sunday June 22nd, Working day
        cal.setTime(makeDate("06/22/03"));
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 22));
        WorkingTimeCalendarEntryTest.addDefaultTimes(entry);
        assertEquals(entry, calendarDef.getDate(cal));

        // Tuesday June 24th, Working day
        cal.setTime(makeDate("06/24/03"));
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 24));
        WorkingTimeCalendarEntryTest.addDefaultTimes(entry);
        assertEquals(entry, calendarDef.getDate(cal));

        // Wednesday June 25th - No date
        cal.setTime(makeDate("06/25/03"));
        assertNull(calendarDef.getDate(cal));

    }

    public void testGetDayOfWeekEntries() {

        WorkingTimeCalendarDefinition calendarDef;
        Collection dayOfWeekEntries;

        // Default calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();
        dayOfWeekEntries = calendarDef.getDayOfWeekEntries();
        assertEquals(7, dayOfWeekEntries.size());

        // Two entries
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY));
        calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY));
        dayOfWeekEntries = calendarDef.getDayOfWeekEntries();
        assertEquals(2, dayOfWeekEntries.size());

    }

    public void testGetDateEntries() {

        WorkingTimeCalendarDefinition calendarDef;
        Collection dateEntries;

        // Default calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();
        dateEntries = calendarDef.getDateEntries();
        assertEquals(0, dateEntries.size());

        // Two entries
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30)));
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 1), new DayOfYear(2003, Calendar.AUGUST, 3)));
        dateEntries = calendarDef.getDateEntries();
        assertEquals(2, dateEntries.size());

        // Overlapping entries not allowed

        // Overlap single date, single date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30)));
        try {
            calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30)));
            fail("Unexpected success adding overlapping date entry");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Overlap single date, date range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30)));
        try {
            calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 29), new DayOfYear(2003, Calendar.JULY, 31)));
            fail("Unexpected success adding overlapping date entry");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Overlap date range, single date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 29), new DayOfYear(2003, Calendar.JULY, 31)));
        try {
            calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 30)));
            fail("Unexpected success adding overlapping date entry");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Overlap date range, date range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 29), new DayOfYear(2003, Calendar.JULY, 31)));
        try {
            calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JULY, 31), new DayOfYear(2003, Calendar.AUGUST, 2)));
            fail("Unexpected success adding overlapping date entry");
        } catch (IllegalStateException e) {
            // Expected
        }

    }

    public void testIsWorkingDay() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();

        //
        // Test Empty Calendar
        //

        Boolean isWorkingDay = null;
        try {
            calendarDef = new WorkingTimeCalendarDefinition();
            cal.setTime(makeDate("06/15/03"));
            isWorkingDay = new Boolean(calendarDef.isWorkingDay(cal));
        } catch (IllegalStateException e) {
            // Expected
        }
        assertNull(isWorkingDay);


        //
        // Test Default Calendar
        //

        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Start with Sunday June 15th
        cal.setTime(makeDate("06/15/03"));
        assertFalse(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertTrue(calendarDef.isWorkingDay(cal));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertFalse(calendarDef.isWorkingDay(cal));

        //
        // Test Customized Calendar
        //

        // Non working days: None
        // Non working dates: None
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{});
        // Start with Sunday June 15th
        cal.setTime(makeDate("06/15/03"));

        for (int i = 0; i < 6; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            assertTrue(calendarDef.isWorkingDay(cal));
        }

        // Non working days: Sunday, Saturday
        // Non working dates: Wednesday June 18th, Saturday June 21st
        // Working dates: Sunday June 22nd, Tuesday June 24th
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03", "06/21/03"},
                new String[]{"06/22/03", "06/24/03"});

        // Sunday June 15th is Non working day
        cal.setTime(makeDate("06/15/03"));
        assertFalse(calendarDef.isWorkingDay(cal));

        // Wednesday June 18th is Non working day
        cal.setTime(makeDate("06/18/03"));
        assertFalse(calendarDef.isWorkingDay(cal));

        // Saturday June 21st is Non working day
        cal.setTime(makeDate("06/21/03"));
        assertFalse(calendarDef.isWorkingDay(cal));

        // Sunday June 22nd is working day
        cal.setTime(makeDate("06/22/03"));
        assertTrue(calendarDef.isWorkingDay(cal));

        // Tuesday June 24th is working day
        cal.setTime(makeDate("06/24/03"));
        assertTrue(calendarDef.isWorkingDay(cal));

    }

    /**
     * Tests the default calendar.
     */
    public void testIsWorkingTimeDefault() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Create the default calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Sunday June 15th
        cal.setTime(makeDateTime("06/15/03 9:00 AM"));
        assertFalse(calendarDef.isWorkingTime(cal));

        // Monday 16th .. Friday 20th
        for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            cal.add(Calendar.DAY_OF_WEEK, 1);
            checkDefaultWorkingTimes(calendarDef, cal);
        }

        // Saturday June 21st
        cal.setTime(makeDateTime("06/21/03 9:00 AM"));
        assertFalse(calendarDef.isWorkingTime(cal));

    }

    /**
     * Tests a calendar that has every day as a working day with no
     * working date exceptions.
     */
    public void testIsWorkingTimeAllWorkingDays() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Create the calendar with all working days
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{});

        // Start with Sunday June 15th
        // Test each date to have default working times
        cal.setTime(makeDate("06/15/03"));
        for (int i = 0; i < 6; i++) {
            checkDefaultWorkingTimes(calendarDef, cal);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

    }

    /**
     * Tests a calendar that has custom date entries.
     */
    public void testIsWorkingTimeCustomCalendar() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Non working days: Sunday, Saturday
        // Non working dates: Wednesday June 18th, Saturday June 21st
        // Working dates: Sunday June 22nd, Tuesday June 24th
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03", "06/21/03"},
                new String[]{"06/22/03", "06/24/03"});

        // Sunday June 15th is Non working time all day
        cal.setTime(makeDate("06/15/03"));
        assertFalse(calendarDef.isWorkingTime(cal));

        // Wednesday June 18th is Non working time all day
        cal.setTime(makeDate("06/18/03"));
        assertFalse(calendarDef.isWorkingTime(cal));

        // Saturday June 21st is Non working time all day
        cal.setTime(makeDate("06/21/03"));
        assertFalse(calendarDef.isWorkingTime(cal));

        // Sunday June 22nd is working day with default times
        cal.setTime(makeDate("06/22/03"));
        checkDefaultWorkingTimes(calendarDef, cal);

        // Tuesday June 24th is working day
        cal.setTime(makeDate("06/24/03"));
        checkDefaultWorkingTimes(calendarDef, cal);

    }

    public void testIsWorkingTimeForEnd() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //
        // Default Calendar
        //

        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Sunday June 15th; no time is working time for end
        cal.setTime(makeDateTime("06/15/03 9:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // Monday 16th is a working day

        // 8:00 AM is NOT working time for an end time
        cal.setTime(makeDateTime("06/16/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 7:59 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 8:01 AM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 12:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // 1:00 PM is not working time for an end time
        cal.setTime(makeDateTime("06/16/03 1:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 12:59 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 1:01 PM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));

        cal.setTime(makeDateTime("06/16/03 5:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        //
        // Custom Calendar
        //

        // Non working days: Sunday, Saturday
        // Non working dates: Wednesday June 18th, Saturday June 21st
        // Working dates: Sunday June 22nd, Tuesday June 24th
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03", "06/21/03"},
                new String[]{"06/22/03", "06/24/03"});

        // Sunday June 15th; no time is working time for end
        cal.setTime(makeDateTime("06/15/03 9:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // Wednesday June 18th; no time is working time for end
        cal.setTime(makeDateTime("06/18/03 9:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // Saturday June 21st; no time is working time for end
        cal.setTime(makeDateTime("06/21/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // Sunday June 22nd; it is a working day
        cal.setTime(makeDateTime("06/22/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/22/03 8:01 AM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/22/03 12:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/22/03 1:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/22/03 1:01 PM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/22/03 5:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

        // Tuesday June 24th; it is a working day
        cal.setTime(makeDateTime("06/24/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/24/03 8:01 AM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/24/03 12:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/24/03 1:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/24/03 1:01 PM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("06/24/03 5:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

    }

    public void testIsWorkingTimeForEndNonDefaultTimes() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        WorkingTimeCalendarEntry entry;


        // Non working days: Sunday, Saturday
        // Working dates: Monday September 1st (24 hour),
        //   Tuesday September 2nd (nightshift), Wednesday September 3rd (8:00 - 8:01)
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 1));
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 2));
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 3));
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        calendarDef.addEntry(entry);

        // Monday September 1st
        cal.setTime(makeDateTime("9/1/03 0:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/1/03 12:00 PM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));

        // Tuesday September 2nd
        cal.setTime(makeDateTime("9/2/03 0:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 2:00 AM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 3:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 3:30 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 4:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 4:30 AM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 11:00 PM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/2/03 11:30 PM"));
        assertTrue(calendarDef.isWorkingTimeForEnd(cal));

        // Wednesday September 2nd
        cal.setTime(makeDateTime("9/3/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));
        cal.setTime(makeDateTime("9/3/03 8:01 AM"));
        assertFalse(calendarDef.isWorkingTimeForEnd(cal));

    }

    public void testGetWorkingHoursInWeek() {

        WorkingTimeCalendarDefinition calendarDef;

        // Default Calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();
        assertEquals(new SimpleTimeQuantity(40, 0), calendarDef.getWorkingHoursInWeek());

        // Sun, Sat, Mon, Wed are non working days
        calendarDef = makeCalendarDefForNonWorkingDays(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY, Calendar.MONDAY, Calendar.WEDNESDAY});
        assertEquals(new SimpleTimeQuantity(24, 0), calendarDef.getWorkingHoursInWeek());

        // No non working days
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{});
        assertEquals(new SimpleTimeQuantity(56, 0), calendarDef.getWorkingHoursInWeek());

    }

    public void testGetWorkingHoursInWeekNonDefaultTimes() {

        WorkingTimeCalendarDefinition calendarDef;
        WorkingTimeCalendarEntry entry;

        calendarDef = new WorkingTimeCalendarDefinition();
        calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY));
        calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SATURDAY));

        // Monday 24 hours
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        // Tuesday nightshift
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.TUESDAY);
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);

        // Wednesday 1 minute
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.WEDNESDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        calendarDef.addEntry(entry);

        // Thursday default
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.THURSDAY);
        calendarDef.addEntry(entry);

        // Friday 11 hours 12 minutes
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.FRIDAY);
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(1, 0, 4, 0),
            new WorkingTime(4, 30, 8, 30),
            new WorkingTime(9, 15, 13, 27)
        }));
        calendarDef.addEntry(entry);

        // Total is 51 hours 13 minutes
        assertEquals(new SimpleTimeQuantity(51, 13), calendarDef.getWorkingHoursInWeek());

    }

    public void testUpdateWithStartOfNextWorkingTime() throws NoWorkingTimeException {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;

        // Default Calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Start: Monday June 16th @ 7:00 AM
        // Expected: Monday June 16th @ 8:00 AM
        cal.set(2003, 5, 16, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 7, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Monday June 16th @ 12:30 PM
        // Expected: Monday June 16th @ 1:00 PM
        cal.set(2003, 5, 16, 13, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 12, 30);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Monday June 16th @ 5:00 PM
        // Expected: Tuesday June 17th @ 8:00 AM
        cal.set(2003, 5, 17, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 17, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Friday June 20th @ 5:00 PM
        // Expected: Monday June 23rd @ 8:00 AM
        cal.set(2003, 5, 23, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 20, 17, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // Wednesday June 18th is non working day
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03"},
                null);

        // Start: Tuesday June 17th @ 11:00 PM
        // Expected: Thursday June 19th @ 8:00 AM
        cal.set(2003, 5, 19, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 17, 23, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // All but Monday are non working days
        calendarDef = makeCalendarDefForNonWorkingDays(
                new int[]{Calendar.SUNDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY});

        // Start: Monday June 16th @ 5:00 PM
        // Expected: Monday June 23rd @ 8:00 AM
        cal.set(2003, 5, 23, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 17, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // No working days in week, working date available but over a week
        // in future
        // This will cause an exception
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                null,
                new String[]{"06/24/03"});

        try {
            cal.set(2003, 5, 16, 17, 0);
            calendarDef.updateWithStartOfNextWorkingTime(cal);
            fail("Unexpected success");
        } catch (NoWorkingTimeException e) {
            // Expected
        }


        // Custom Calendar
        // A specific Sunday date is a working day, overriding day of week
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                null,
                new String[]{"06/22/03"});

        // Start: Friday June 20th @ 5:00 PM
        // Expected: Sunday June 22nd @ 8:00 AM
        cal.set(2003, 5, 22, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 20, 17, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testUpdateWithStartOfNextWorkingTimeNonDefaultTimes() throws NoWorkingTimeException {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;
        WorkingTimeCalendarEntry entry;

        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 1));
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 2));
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 3));
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        calendarDef.addEntry(entry);

        // Start: Sunday August 31st @ 12:00 PM
        // Expected: Monday September 1st @ 12:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 1, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 31, 12, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Monday September 1st @ 1:00 AM
        // Expected: Same
        cal.set(2003, Calendar.SEPTEMBER, 1, 1, 0);
        expected = cal.getTime();
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Tuesday September 2nd @ 12:00 AM
        // Expected: Same
        cal.set(2003, Calendar.SEPTEMBER, 2, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 2, 0, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Tuesday September 2nd @ 3:00 AM
        // Expected: 4:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 2, 4, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 2, 3, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Tuesday September 2nd @ 4:00 AM
        // Expected: Same
        cal.set(2003, Calendar.SEPTEMBER, 2, 4, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 2, 4, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Tuesday September 2nd @ 9:00 AM
        // Expected: 11:00 PM
        cal.set(2003, Calendar.SEPTEMBER, 2, 23, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 2, 9, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Tuesday September 2nd @ 11:00 PM
        // Expected: Same
        cal.set(2003, Calendar.SEPTEMBER, 2, 23, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 2, 23, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Wednesday September 3rd @ 12:00 AM
        // Expected: 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 0, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Wednesday September 3rd @ 8:00 AM
        // Expected: Same
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 0);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Wednesday September 3rd @ 8:01 AM
        // Expected: Thursday September 4th @ 8:00 AM
        cal.set(2003, Calendar.SEPTEMBER, 4, 8, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 3, 8, 1);
        calendarDef.updateWithStartOfNextWorkingTime(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testUpdateWithEndOfPreviousWorkingTime() throws NoWorkingTimeException {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;

        // Default Calendar
        calendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();

        // Start: Monday June 16th @ 6:00 PM
        // Expected: Monday June 16th @ 5:00 PM
        cal.set(2003, 5, 16, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 18, 0);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Monday June 16th @ 12:30 PM
        // Expected: Monday June 16th @ 12:00 PM
        cal.set(2003, 5, 16, 12, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 12, 30);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Start: Monday June 23rd @ 8:00 AM
        // Expected: Friday June 20th @ 5:00 PM
        cal.set(2003, 5, 20, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 23, 8, 0);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // Wednesday June 18th is non working day
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/18/03"},
                null);

        // Start: Thursday June 19th @ 8:00 AM
        // Expected: Tuesday June 17th @ 5:00 PM
        cal.set(2003, 5, 17, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 19, 8, 0);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // All but Monday are non working days
        calendarDef = makeCalendarDefForNonWorkingDays(
                new int[]{Calendar.SUNDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY});

        // Start: Monday June 23rd @ 8:00 AM
        // Expected: Monday June 16th @ 5:00 PM
        cal.set(2003, 5, 16, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 23, 8, 0);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());

        // Custom Calendar
        // No working days in week, working date available but over a week
        // in past
        // This will cause an exception
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY},
                null,
                new String[]{"06/13/03"});

        try {
            // Monday June 23rd @ 8:00 AM
            cal.set(2003, 5, 23, 8, 0);
            calendarDef.updateWithEndOfPreviousWorkingTime(cal);
            fail("Unexpected success");
        } catch (NoWorkingTimeException e) {
            // Expected
        }


        // Custom Calendar
        // A specific Sunday date is a working day, overriding day of week
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                null,
                new String[]{"06/22/03"});

        // Start: Monday June 23rd @ 8:00 AM
        // Expected: Sunday June 22nd @ 5:00 PM
        cal.set(2003, 5, 22, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 23, 8, 0);
        calendarDef.updateWithEndOfPreviousWorkingTime(cal);
        assertEquals(expected, cal.getTime());


    }

    public void testUpdateWithEndOfLastWorkingTimeForDay() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;

        // Calendar has Sunday, Saturday Non working day
        // Sunday 22nd June is working day
        // Friday 20th June is non working day
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/20/03"},
                new String[]{"06/22/03"});

        // Sunday June 15th; non working day (by day of week) causes error
        try {
            cal.set(2003, 5, 15, 8, 0);
            calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Monday June 16th; working day (by day of week)
        // Expected: Monday June 16th @ 5:00 PM
        cal.set(2003, 5, 16, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 8, 0);
        calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

        // Friday June 20th; non working day (by date) causes error
        try {
            cal.set(2003, 5, 20, 8, 0);
            calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Sunday June 22nd; working day (by date)
        // Expected: Sunday June 22nd @ 5:00 PM
        cal.set(2003, 5, 22, 17, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 22, 8, 0);
        calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testUpdateWithEndOfLastWorkingTimeForDayNonDefaultTimes() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;
        DateEntry entry1;

        //
        // 24 hour
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry1 = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4));
        WorkingTimeCalendarEntryTest.add24HourTimes(entry1);
        calendarDef.addDateEntry(entry1);

        // Exected 0:00 on next day
        cal.set(2003, Calendar.SEPTEMBER, 5, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

        //
        // Nighshift
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry1 = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4));
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry1);
        calendarDef.addDateEntry(entry1);

        // Expected 0:00 on next day
        cal.set(2003, Calendar.SEPTEMBER, 5, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 4, 8, 0);
        calendarDef.updateWithEndOfLastWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testUpdateWithStartOfFirstWorkingTimeForDay() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;

        // Calendar has Sunday, Saturday Non working day
        // Sunday 22nd June is working day
        // Friday 20th June is non working day
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/20/03"},
                new String[]{"06/22/03"});

        // Sunday June 15th; non working day (by day of week) causes error
        try {
            cal.set(2003, 5, 15, 12, 0);
            calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Monday June 16th; working day (by day of week)
        // Expected: Monday June 16th @ 8:00 AM
        cal.set(2003, 5, 16, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 16, 12, 0);
        calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

        // Friday June 20th; non working day (by date) causes error
        try {
            cal.set(2003, 5, 20, 12, 0);
            calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
            fail("Unexpected success");
        } catch (IllegalStateException e) {
            // Expected
        }

        // Sunday June 22nd; working day (by date)
        // Expected: Sunday June 22nd @ 8:00 AM
        cal.set(2003, 5, 22, 8, 0);
        expected = cal.getTime();
        cal.set(2003, 5, 22, 12, 0);
        calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testUpdateWithStartOfFirstWorkingTimeForDayNonDefaultTimes() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected;
        WorkingTimeCalendarEntry entry1;

        //
        // 24 hour
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry1 = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4));
        WorkingTimeCalendarEntryTest.add24HourTimes(entry1);
        calendarDef.addEntry(entry1);

        // Expected 0:00
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 4, 12, 0);
        calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

        //
        // Nighshift
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        entry1 = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4));
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry1);
        calendarDef.addEntry(entry1);

        // Expected 0:00
        cal.set(2003, Calendar.SEPTEMBER, 4, 0, 0);
        expected = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 4, 12, 0);
        calendarDef.updateWithStartOfFirstWorkingTimeForDay(cal);
        assertEquals(expected, cal.getTime());

    }

    public void testAllocateLaterHoursInDay() throws NoWorkingTimeException {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        BigDecimal factor;
        BigDecimal workHours;
        Allocation allocation;

        //
        // Default calendar
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Sunday 9/7/03 Non working day
        // Expecte date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/7/03 8:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 12:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(36, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 5:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Monday 9/8/03 @ 8:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 11:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 8:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

    }

    public void testAllocateLaterHoursInDayNonDefaultTimes() throws NoWorkingTimeException {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        BigDecimal factor;
        BigDecimal workHours;
        Allocation allocation;

        //
        // 24 hours
        //
        calendarDef = make24HourCalendarDefinition();

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Friday 9/5/03 working day
        // Expected date: Saturday 9/6/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/6/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(16, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Saturday 9/6/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/6/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(24, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Saturday 9/6/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 12:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/6/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(28, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Saturday 9/6/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 5:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/6/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(33, 0), allocation.getActualTimeRemaining());

        // Friday 9/5/03 working day
        // Expected date: Saturday 9/6/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/5/03 11:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/6/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(39, 0), allocation.getActualTimeRemaining());

        //
        // Nightshift
        //
        calendarDef = makeNightshiftCalendarDefinition();

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Saturday @ 12:00 AM
        // Expected date: Monday 9/8/03 @ 11:00 PM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/6/03 12:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 11:00 PM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(33, 0), allocation.getActualTimeRemaining());

        // Saturday @ 8:00 AM
        // Expected date: Monday 9/8/03 @ 11:00 PM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/6/03 8:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/8/03 11:00 PM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

        // Monday @ 11:00 PM
        // Expected date: Tuesday 9/9/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/8/03 11:00 PM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/9/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(39, 0), allocation.getActualTimeRemaining());

        // Tuesday @ 12:00 AM
        // Expected date: Wednesday 9/10/03 @ 12:00 AM
        allocation = new Allocation(factor, workHours);
        cal.setTime(makeDateTime("9/9/03 12:00 AM"));
        calendarDef.allocateLaterHoursInDay(cal, allocation);
        assertEquals(makeDateTime("9/10/03 12:00 AM"), cal.getTime());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

    }

    public void testAllocateEarlierHoursInDay() {
        // TODO Implement testAllocateEarlierHoursInDay
    }

    public void testAllocateEarlierHoursInDayNonDefaultTimes() {
        // TODO Implement testAllocateEarlierHoursInDayNonDefaultTimes
    }

    public void testCalculateExceptionHours() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        WorkingTimeCalendarDefinition calendarDef;

        // Calendar has Sunday, Saturday Non working day
        // Sunday 22nd June, Monday 23rd June is working day
        // Friday 20th June, Saturday 21st June are non working days
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SUNDAY, Calendar.SATURDAY},
                new String[]{"06/20/03", "06/21/03"},
                new String[]{"06/22/03", "06/23/03"});

        //
        // No exceptions
        //

        // Start Date: Thursday June 19th
        // End Date: Thursday June 19th
        // Expected: 0
        assertEquals(new SimpleTimeQuantity(0, 0), calendarDef.calculateExceptionHours(makeDateTime("06/19/03 12:00 AM"), makeDateTime("06/19/03 12:00 AM"), timeZone));

        // Start Date: Wednesday June 18th
        // End Date: Thursday June 19th
        // Expected: 0
        assertEquals(new SimpleTimeQuantity(0, 0), calendarDef.calculateExceptionHours(makeDateTime("06/18/03 12:00 AM"), makeDateTime("06/19/03 12:00 AM"), timeZone));

        // Start Date: Thursday June 19th
        // End Date: Wednesday June 18th
        // Expected: 0
        assertEquals(new SimpleTimeQuantity(0, 0), calendarDef.calculateExceptionHours(makeDateTime("06/19/03 12:00 AM"), makeDateTime("06/18/03 12:00 AM"), timeZone));

        //
        // Positive Exceptions (more hours available than normal)
        //

        // Start Date: Sunday June 22nd
        // End Date: Sunday June 22nd
        // Expected: 8
        assertEquals(new SimpleTimeQuantity(8, 0), calendarDef.calculateExceptionHours(makeDateTime("06/22/03 12:00 AM"), makeDateTime("06/23/03 12:00 AM"), timeZone));

        // Start Date: Monday June 23rd
        // End Date: Saturday June 21st
        // Expected: 8
        assertEquals(new SimpleTimeQuantity(8, 0), calendarDef.calculateExceptionHours(makeDateTime("06/23/03 12:00 AM"), makeDateTime("06/21/03 12:00 AM"), timeZone));

        //
        // Negative Exceptions (fewer hours available than normal)
        //

        // Start Date: Friday June 20th
        // End Date: Friday June 20th
        // Expected: -8
        assertEquals(new SimpleTimeQuantity(8, 0, true), calendarDef.calculateExceptionHours(makeDateTime("06/20/03 12:00 AM"), makeDateTime("06/21/03 12:00 AM"), timeZone));

        // Start Date: Saturday June 21st
        // End Date: Friday June 13th
        // Expected: -8
        assertEquals(new SimpleTimeQuantity(8, 0, true), calendarDef.calculateExceptionHours(makeDateTime("06/21/03 12:00 AM"), makeDateTime("06/13/03 12:00 AM"), timeZone));

        //
        // Cancelled out exceptions (same hours as normal)
        //

        // Start Date: Thursday June 19th
        // End Date: Thursday June 16th
        // Expected: 0
        assertEquals(new SimpleTimeQuantity(0, 0), calendarDef.calculateExceptionHours(makeDateTime("06/19/03 12:00 AM"), makeDateTime("06/16/03 12:00 AM"), timeZone));

        //
        // Specify time portion
        //
        assertEquals(new SimpleTimeQuantity(8, 0, true), calendarDef.calculateExceptionHours(makeDateTime("06/20/03 8:00 AM"), makeDateTime("06/20/03 5:00 PM"), timeZone));
        assertEquals(new SimpleTimeQuantity(4, 0, true), calendarDef.calculateExceptionHours(makeDateTime("06/20/03 8:00 AM"), makeDateTime("06/20/03 12:00 PM"), timeZone));
        assertEquals(new SimpleTimeQuantity(0, 0), calendarDef.calculateExceptionHours(makeDateTime("06/20/03 12:00 PM"), makeDateTime("06/22/03 12:00 PM"), timeZone));
        assertEquals(new SimpleTimeQuantity(4, 0), calendarDef.calculateExceptionHours(makeDateTime("06/20/03 12:00 PM"), makeDateTime("06/23/03 12:00 PM"), timeZone));


    }

    public void testCalculateExceptionHoursNonDefaultTimes() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        WorkingTimeCalendarDefinition calendarDef;
        WorkingTimeCalendarEntry entry;

        // Day of week entries
        // Sunday: Non working day
        // Saturday: Non working day
        // Monday: Working day, 24 hour times
        // Tuesday: Working day, 3 hours
        // Wednesday - Friday: Working day, default times

        // Date entries
        // Sunday 15th June - Working day (3 hours)
        // Monday 16th June - Working day (3 hours)
        // Tuesday 17th June - Working day (nightshift)
        // Tuesday 24th June - Non working day
        // Wednesday 25th June - Non working day
        // Thursday 26th June - Working day (24 hours)

        calendarDef = new WorkingTimeCalendarDefinition();
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SATURDAY);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.TUESDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(9, 0, 12, 0)));
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.WEDNESDAY);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.THURSDAY);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.FRIDAY);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 15));
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(9, 0, 12, 0)));
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 16));
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(9, 0, 12, 0)));
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 17));
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JUNE, 24));
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.JUNE, 25));
        calendarDef.addEntry(entry);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.JUNE, 26));
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        // Start Date: Sunday June 15th @ 12:00 AM
        // End Date: Sunday June 15th @ 1:00 PM
        // Expected: 3
        assertEquals(new SimpleTimeQuantity(3, 0), calendarDef.calculateExceptionHours(makeDateTime("06/15/03 12:00 AM"), makeDateTime("06/15/03 1:00 PM"), timeZone));

        // Start Date: Sunday June 15th @ 9:00 AM
        // End Date: Sunday June 15th @ 10:00 AM
        // Expected: 1
        assertEquals(new SimpleTimeQuantity(1, 0), calendarDef.calculateExceptionHours(makeDateTime("06/15/03 9:00 AM"), makeDateTime("06/15/03 10:00 AM"), timeZone));

        // Start Date: Monday June 16th @ 12:00 AM
        // End Date: Wednesday June 18th @ 12:00 AM
        // Expected: -16
        assertEquals(new SimpleTimeQuantity(16, 0, true), calendarDef.calculateExceptionHours(makeDateTime("06/16/03 12:00 AM"), makeDateTime("06/18/03 12:00 AM"), timeZone));

        // Start Date: Tuesday June 24th @ 12:00 AM
        // End Date: Friday June 27th @ 12:00 AM
        // Expected: 5
        assertEquals(new SimpleTimeQuantity(5, 0), calendarDef.calculateExceptionHours(makeDateTime("06/24/03 12:00 AM"), makeDateTime("06/27/03 12:00 AM"), timeZone));

    }

    public void testUpdateDayOfWeekInherit() {

        WorkingTimeCalendarDefinition calendarDef;

        // Make base calendar
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY});

        // Inherit on a base calendar
        try {
            calendarDef.updateDayOfWeekInherit(Calendar.SUNDAY);
        } catch (IllegalStateException e) {
            // expected
        }

        // Make non-base calendar
        // Monday is a non working day
        // Sunday is working Day
        // All other day of weeks inherited from default base calendar
        calendarDef = makeCalendarDef(
                new int[]{Calendar.MONDAY},
                new int[]{Calendar.SUNDAY},
                null, null, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

        // Start by assuring status of calendar
        assertFalse("Expected Sunday to not be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.SUNDAY));
        assertFalse("Expected Monday to not be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.MONDAY));
        assertTrue("Expected Tuesday to be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.TUESDAY));
        assertTrue("Expected Wednesday to be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.WEDNESDAY));
        assertTrue("Expected Thursday to be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.THURSDAY));
        assertTrue("Expected Friday to be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.FRIDAY));
        assertTrue("Expected Saturday to be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.SATURDAY));

        // Present day, non working day
        calendarDef.updateDayOfWeekInherit(Calendar.MONDAY);
        assertTrue("Expected Monday to now be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.MONDAY));

        // Present day, working day
        calendarDef.updateDayOfWeekInherit(Calendar.SUNDAY);
        assertTrue("Expected Sunday to now be inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.SUNDAY));

        // Absent day
        calendarDef.updateDayOfWeekInherit(Calendar.TUESDAY);
        assertTrue("Expected Tuesday to remain inherited from parent calendar", calendarDef.isInheritedDayOfWeek(Calendar.TUESDAY));

    }

    public void testUpdateDayOfWeekWorkingDay() {

        WorkingTimeCalendarDefinition calendarDef;
        Collection workingTimes;

        //
        // Base calendar
        //

        // Sunday and Monday are non working days; all others working days
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY});
        assertFalse(calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.WEDNESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.THURSDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());

        // Update non working day to working day with custom working times
        workingTimes = Collections.singleton(new WorkingTime(9, 0, 18, 0));
        calendarDef.updateDayOfWeekWorkingDay(Calendar.SUNDAY, workingTimes);
        assertTrue("Expected Sunday to be working day", calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.SUNDAY).getWorkingTimes());

        // Update working day to remain working day; but new working times
        workingTimes = Collections.singleton(new WorkingTime(9, 0, 18, 0));
        calendarDef.updateDayOfWeekWorkingDay(Calendar.TUESDAY, workingTimes);
        assertTrue("Expected Tuesday to remain a working day", calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.TUESDAY).getWorkingTimes());

        //
        // Non-base calendar
        //
        workingTimes = Collections.singleton(new WorkingTime(9, 0, 18, 0));

        // Monday is a non working day
        // Sunday is working Day
        // All other day of weeks inherited from default base calendar
        calendarDef = makeCalendarDef(
                new int[]{Calendar.MONDAY},
                new int[]{Calendar.SUNDAY},
                null, null, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

        // Start by assuring status of calendar
        assertTrue(calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.WEDNESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.THURSDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());

        // Update non working day to working day
        calendarDef.updateDayOfWeekWorkingDay(Calendar.MONDAY, workingTimes);
        assertTrue("Expected Monday to be working day", calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.MONDAY).getWorkingTimes());

        // Update working day to remain working day
        calendarDef.updateDayOfWeekWorkingDay(Calendar.TUESDAY, workingTimes);
        assertTrue("Expected Tuesday to remain a working day", calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.TUESDAY).getWorkingTimes());

        // Update inherited non working day to be non-inherited and working day
        calendarDef.updateDayOfWeekWorkingDay(Calendar.SATURDAY, workingTimes);
        assertTrue(!calendarDef.isInheritedDayOfWeek(Calendar.SATURDAY));
        assertTrue("Expected Saturday to be working day", calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.SATURDAY).getWorkingTimes());

        // Update inherited working day to be non-inherited and working day
        calendarDef.updateDayOfWeekWorkingDay(Calendar.FRIDAY, workingTimes);
        assertTrue(!calendarDef.isInheritedDayOfWeek(Calendar.FRIDAY));
        assertTrue("Expected Friday to be working day", calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());
        assertEntriesEqual(workingTimes, calendarDef.getDayOfWeek(Calendar.FRIDAY).getWorkingTimes());

    }

    public void testUpdateDayOfWeekNonWorkingDay() {

        WorkingTimeCalendarDefinition calendarDef;

        //
        // Base calendar
        //

        // Sunday and Monday are non working days; all others working days
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY});
        assertFalse(calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.WEDNESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.THURSDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());

        // Update non working day to remain non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.SUNDAY);
        assertFalse("Expected Sunday to remain non working day", calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());

        // Update working day to be non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.TUESDAY);
        assertFalse("Expected Tuesday to be a non working day", calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());

        //
        // Non-base calendar
        //

        // Monday is a non working day
        // Sunday is working Day
        // All other day of weeks inherited from default base calendar
        calendarDef = makeCalendarDef(
                new int[]{Calendar.MONDAY},
                new int[]{Calendar.SUNDAY},
                null, null, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

        // Start by assuring status of calendar
        assertTrue(calendarDef.getDayOfWeek(Calendar.SUNDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.WEDNESDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.THURSDAY).isWorkingDay());
        assertTrue(calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());
        assertFalse(calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());

        // Update non working day to remain non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.MONDAY);
        assertFalse("Expected Monday to remain non working day", calendarDef.getDayOfWeek(Calendar.MONDAY).isWorkingDay());

        // Update working day to be non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.TUESDAY);
        assertFalse("Expected Tuesday to be a non working day", calendarDef.getDayOfWeek(Calendar.TUESDAY).isWorkingDay());

        // Update inherited non working day to be non-inherited but remain non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.SATURDAY);
        assertTrue(!calendarDef.isInheritedDayOfWeek(Calendar.SATURDAY));
        assertFalse("Expected Saturday remain non working day", calendarDef.getDayOfWeek(Calendar.SATURDAY).isWorkingDay());

        // Update inherited working day to be non-inherited non working day
        calendarDef.updateDayOfWeekNonWorkingDay(Calendar.FRIDAY);
        assertTrue(!calendarDef.isInheritedDayOfWeek(Calendar.FRIDAY));
        assertFalse("Expected Friday to be non working day", calendarDef.getDayOfWeek(Calendar.FRIDAY).isWorkingDay());

    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#updateDateEntries} where
     * there are zero current date entries.
     */
    public void testUpdateDateEntriesZeroExisting() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry newEntry1;
        Collection expectedEntries;


        // Calendar is initially empty
        // Update with new single date entry
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        calendarDef.updateDateEntries(newEntry1);
        // Expect one entry, same as the entry we added
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());

        // Calendar is initially empty
        // Update with new range date entry
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27), new DayOfYear(2003, Calendar.AUGUST, 29));
        calendarDef.updateDateEntries(newEntry1);
        // Expect one entry, same as the entry we added
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());

    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#updateDateEntries} where
     * there is a single date entry, both for a date and date range.
     */
    public void testUpdateDateEntriesOneExisting() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry existingEntry1;
        DateEntry expectedEntry1;
        DateEntry expectedEntry2;
        DateEntry newEntry1;
        Collection expectedEntries;

        // -- Begin Test
        // Add new new single date, no overlap
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.updateDateEntries(newEntry1);

        // Expect two entries, our original and new one
        expectedEntries = Arrays.asList(new DateEntry[]{existingEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Edit existing date, changing it to a different single date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        newEntry1.setEntryID("1");
        calendarDef.updateDateEntries(newEntry1);

        // Expect one entry; our new one
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new single date, same as existing date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        calendarDef.updateDateEntries(newEntry1);

        // Expect one entry; our new one
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, no overlap
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        calendarDef.updateDateEntries(newEntry1);

        // Expect two entries; existing and new one
        expectedEntries = Arrays.asList(new DateEntry[]{existingEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Edit existing date, changing it to date range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry1.setEntryID("1");
        calendarDef.updateDateEntries(newEntry1);

        // Expect one entry; our new one
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, overlap
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 26), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.updateDateEntries(newEntry1);

        // Expect one entry; our new one; it replaces the existing entry due to overlap
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        //
        // 1 date entry; date range
        //

        // -- Begin Test
        // Add new single date, no overlap
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 29));
        calendarDef.updateDateEntries(newEntry1);

        // Expect two entries
        expectedEntries = Arrays.asList(new DateEntry[]{existingEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Edit existing date range, changing it to a single date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 23));
        newEntry1.setEntryID("1");
        calendarDef.updateDateEntries(newEntry1);

        // Expect one entry
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new single date, within existing date range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 23));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 3 entries (8/20/03 - 8/22/03, new entry, 8/24/03 - 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 22));
        expectedEntry2 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 24), new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, expectedEntry2, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new single date, on start date of range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (new entry, 8/21/03 - 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 21), new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new single date, on end date of range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (8/20/03 - 8/27/03, new entry)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 27));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, no overlap
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 29), new DayOfYear(2003, Calendar.SEPTEMBER, 5));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries
        expectedEntries = Arrays.asList(new DateEntry[]{existingEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Edit existing range, changing it to another range
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        existingEntry1.setEntryID("1");
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 29), new DayOfYear(2003, Calendar.SEPTEMBER, 5));
        newEntry1.setEntryID("1");
        calendarDef.updateDateEntries(newEntry1);

        // Expected 1 entries; the new range
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, fully overlapping existing, beyond dates
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 18), new DayOfYear(2003, Calendar.SEPTEMBER, 5));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 1 entries; the new range
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, exactly equal to existing
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 1 entries; the new range
        expectedEntries = Arrays.asList(new DateEntry[]{newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, overlap midway through date to beyond date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.SEPTEMBER, 05));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (8/20/03 - 8/24/03, new entry)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 24));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, overlap midway through date to exact end date
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (8/20/03 - 8/24/03, new entry)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 24));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, begin before, overlap midway through
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 15), new DayOfYear(2003, Calendar.AUGUST, 24));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (new entry, 8/25/03 - 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, begin exactly on start date, overlap midway through
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 24));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (new entry, 8/25/03 - 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test


        // -- Begin Test
        // Add new range, entirely within existing
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 3 entries (8/20/03 - 8/21/03, new entry, 8/27/03 - 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 21));
        expectedEntry2 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27), new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, expectedEntry2, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test

        // -- Begin Test
        // Add new range, entirely within existing, but only one day before and one day after
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 28));
        calendarDef.addDateEntry(existingEntry1);

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 21), new DayOfYear(2003, Calendar.AUGUST, 27));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 3 entries (8/20/03, new entry, 8/28/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20));
        expectedEntry2 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, expectedEntry2, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test

    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#updateDateEntries} where
     * there are multiple date entries, both dor a date and date range.
     */
    public void testUpdateDateEntriesMultipleExisting() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry existingEntry1;
        DateEntry expectedEntry1;
        DateEntry expectedEntry2;
        DateEntry newEntry1;
        Collection expectedEntries;

        //
        // 3 date entries; all single dates
        //

        // -- Begin Test
        // Add new range, overlap two
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        calendarDef.addDateEntry(existingEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 01)));
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 10)));
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 15)));

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 10), new DayOfYear(2003, Calendar.AUGUST, 15));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 2 entries (8/01/03, new entry)
        expectedEntries = Arrays.asList(new DateEntry[]{existingEntry1, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test




        //
        // 3 date entrys; all ranges
        //

        // -- Begin Test
        // Add new range, overlap midway through first, fully through second, to midway through third
        calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults("Test");
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 01), new DayOfYear(2003, Calendar.AUGUST, 05)));
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 10), new DayOfYear(2003, Calendar.AUGUST, 14)));
        calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 19), new DayOfYear(2003, Calendar.AUGUST, 23)));

        newEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 03), new DayOfYear(2003, Calendar.AUGUST, 21));
        calendarDef.updateDateEntries(newEntry1);

        // Expected 3 entries (8/01/03 - 8/02/03, new entry, 8/22/03 - 8/23/03)
        expectedEntry1 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 01), new DayOfYear(2003, Calendar.AUGUST, 02));
        expectedEntry2 = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 23));
        expectedEntries = Arrays.asList(new DateEntry[]{expectedEntry1, expectedEntry2, newEntry1});
        assertEntriesEqual(expectedEntries, calendarDef.getDateEntries());
        // -- End Test

    }


    /**
     * Asserts that the specified entries collections are equals, irrespective of
     * order.
     * @param expectedEntries the expected entries
     * @param actualEntries the actual entries
     */
    private static void assertEntriesEqual(Collection expectedEntries, Collection actualEntries) {

        // We compare based on sets to perform to ignore order
        Set expectedEntriesSet = new HashSet();
        expectedEntriesSet.addAll(expectedEntries);

        Set actualEntriesSet = new HashSet();
        actualEntriesSet.addAll(actualEntries);

        assertEquals("Found duplicate entries in actual date entries collection", actualEntries.size(), actualEntriesSet.size());
        assertEquals("Expected date entries (size = " + expectedEntries.size() + ") are not the same as actual date entries (size = " + actualEntries.size() + ").",
                expectedEntriesSet, actualEntriesSet);

    }

    public void testUpdateDaysWorked() {

        WorkingTimeCalendarDefinition calendarDef;
        DaysWorked daysWorked;
        Allocation allocation;
        Calendar cal;
        BigDecimal factor;
        BigDecimal workHours;

        //
        // Default Calendar
        //
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Non Working day Sunday September 7th @ 0:00 AM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 40 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/7/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 0:00 AM
        // Expected:  hours worked    = 8 hours
        //            hours remaining = 32 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 8:00 AM
        // Expected:  hours worked    = 8 hours
        //            hours remaining = 32 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 12:00 PM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 36 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(36, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 5:00 PM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 40 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 5:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(40, 0), allocation.getActualTimeRemaining());

        // 200%, 40 hours
        factor = new BigDecimal("2");
        workHours = new BigDecimal("40");

        // Non Working day Sunday September 7th @ 0:00 AM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 40 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/7/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(20, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 0:00 AM
        // Expected:  hours worked    = 8 hours
        //            hours remaining = 24 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(12, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 1:00 PM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 16 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 1:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(16, 0), allocation.getActualTimeRemaining());

        // 25%, 40 hours
        factor = new BigDecimal("0.25");
        workHours = new BigDecimal("40");

        // Non Working day Sunday September 7th @ 0:00 AM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 160 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/7/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(160, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 0:00 AM
        // Expected:  hours worked    = 8 hours
        //            hours remaining = 152 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(152, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 1:00 PM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 159 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 1:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(156, 0), allocation.getActualTimeRemaining());

        // 100%, 4 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("4");

        // Non Working day Sunday September 7th @ 0:00 AM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 4 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/7/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 0:00 AM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 0 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 8:00 AM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 0 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 12:00 PM
        // Expected:  hours worked    = 4 hours
        //            hours remaining = 0 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(4, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), allocation.getActualTimeRemaining());

        // Working day Friday September 5th @ 5:00 PM
        // Expected:  hours worked    = 0 hours
        //            hours remaining = 4 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 5:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(4, 0), allocation.getActualTimeRemaining());

        //
        // Other values
        //
        factor = new BigDecimal("0.25");
        workHours = new BigDecimal("0.667");

        // Working day Friday September 5th @ 8:00 AM
        // Expected:  hours worked    = 0.667
        //            hours remaining = 0
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(2, 40), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), allocation.getActualTimeRemaining());

        // Issue: Infinite loop
        factor = new BigDecimal("1.12");
        workHours = new BigDecimal("1.8");

        // Working day Tuesday December 3rd @ 8:00 AM
        // Expected:  hours worked    = 1.8
        //            hours remaining = 0
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("12/3/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(1, 36), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(0, 0), allocation.getActualTimeRemaining());

    }

    public void testUpdateDaysWorkedNonDefaultTimes() {

        WorkingTimeCalendarDefinition calendarDef;
        DaysWorked daysWorked;
        Allocation allocation;
        Calendar cal;
        BigDecimal factor;
        BigDecimal workHours;

        //
        // 24 hour calendar
        //
        calendarDef = make24HourCalendarDefinition();

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Working day @ 0:00 AM
        // Expected:  hours worked    = 24 hours
        //            hours remaining = 16 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(24, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(16, 0), allocation.getActualTimeRemaining());

        // Working day @ 8:00 AM
        // Expected:  hours worked    = 16 hours
        //            hours remaining = 24 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef = make24HourCalendarDefinition();
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(16, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(24, 0), allocation.getActualTimeRemaining());

        // Working day @ 11:00 PM
        // Expected:  hours worked    = 1 hours
        //            hours remaining = 39 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 11:00 PM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef = make24HourCalendarDefinition();
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(1, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(39, 0), allocation.getActualTimeRemaining());

        // 25%, 10 hours
        factor = new BigDecimal("0.25");
        workHours = new BigDecimal("10");

        // Working day @ 0:00 AM
        // Expected:  hours worked    = 24 hours
        //            hours remaining = 16 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(24, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(16, 0), allocation.getActualTimeRemaining());

        // Working day @ 8:00 AM
        // Expected:  hours worked    = 16 hours
        //            hours remaining = 24 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(16, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(24, 0), allocation.getActualTimeRemaining());

        //
        // Nightshift calendar
        //
        calendarDef = makeNightshiftCalendarDefinition();

        // 100%, 40 hours
        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Working day @ 0:00 AM
        // Expected:  hours worked    = 8 hours
        //            hours remaining = 32 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(8, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(32, 0), allocation.getActualTimeRemaining());

        // Working day @ 10:00 AM
        // Expected:  hours worked    = 1 hours
        //            hours remaining = 39 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 10:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(1, 0), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(39, 0), allocation.getActualTimeRemaining());

        //
        // 1 minute day
        //
        calendarDef = make24HourCalendarDefinition();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        WorkingTimeCalendarEntry entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 5));
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(8, 0, 8, 1)));
        calendarDef.addEntry(entry);

        factor = new BigDecimal("1");
        workHours = new BigDecimal("40");

        // Working day @ 0:00 AM
        // Expected:  hours worked    = 0.017 hours
        //            hours remaining = 39.983 hours
        cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(makeDateTime("9/5/03 12:00 AM"));
        daysWorked = new DaysWorked();
        allocation = new Allocation(factor, workHours);
        calendarDef.updateDaysWorked(daysWorked, cal, allocation);
        assertEquals(new SimpleTimeQuantity(0, 1), daysWorked.getDuration());
        assertEquals(new SimpleTimeQuantity(39, 59), allocation.getActualTimeRemaining());

    }

    public void testRemoveDateEntry() {

        WorkingTimeCalendarDefinition calendarDef;
        DateEntry dateEntry;
        String removeID;

        // Null entry ID
        try {
            calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY});
            calendarDef.removeDateEntry(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // No date entries
        try {
            calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.MONDAY});
            calendarDef.removeDateEntry("1");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Some date entries; none matching ID
        try {
            calendarDef = makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.MONDAY}, new String[]{"9/2/03"}, new String[]{"9/3/03"});
            calendarDef.removeDateEntry("1");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }


        // Some date entries; remove a single date
        calendarDef = makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.MONDAY}, new String[]{"9/2/03"}, new String[]{"9/3/03"});
        dateEntry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4));
        WorkingTimeCalendarEntryTest.addDefaultTimes(dateEntry);
        dateEntry.setEntryID("1");
        calendarDef.addDateEntry(dateEntry);
        dateEntry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 5));
        WorkingTimeCalendarEntryTest.addDefaultTimes(dateEntry);
        dateEntry.setEntryID("2");
        calendarDef.addDateEntry(dateEntry);

        removeID = "1";
        calendarDef.removeDateEntry(removeID);
        assertEquals(3, calendarDef.getDateEntries().size());
        for (Iterator it = calendarDef.getDateEntries().iterator(); it.hasNext();) {
            DateEntry nextEntry = (DateEntry) it.next();

            if (nextEntry.getEntryID() != null && nextEntry.getEntryID().equals(removeID)) {
                fail("Expected entry with ID " + removeID + " to have been removed");
            }

        }

        // Some date entries; remove a span date
        calendarDef = makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.MONDAY}, new String[]{"9/2/03"}, new String[]{"9/3/03"});
        dateEntry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.SEPTEMBER, 4), new DayOfYear(2003, Calendar.SEPTEMBER, 30));
        dateEntry.setEntryID("1");
        calendarDef.addDateEntry(dateEntry);
        dateEntry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(2003, Calendar.OCTOBER, 1));
        dateEntry.setEntryID("2");
        calendarDef.addDateEntry(dateEntry);

        removeID = "1";
        calendarDef.removeDateEntry(removeID);
        assertEquals(3, calendarDef.getDateEntries().size());
        for (Iterator it = calendarDef.getDateEntries().iterator(); it.hasNext();) {
            DateEntry nextEntry = (DateEntry) it.next();

            if (nextEntry.getEntryID() != null && nextEntry.getEntryID().equals(removeID)) {
                fail("Expected entry with ID " + removeID + " to have been removed");
            }

        }

    }

    public void testIsWorkingTimeAvailable() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Default calendar; no dates
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        assertTrue(calendarDef.isWorkingTimeAvailable(cal));

        // Hierarchical calendar with no Day of weeks
        // 1 date available
        // Saturday, Sunday, Monday are NON working days
        // Base calendar has Tuesday - Friday as non working days
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY},
                null,
                null, new String[]{"9/6/03"},
                makeCalendarDefForNonWorkingDays(new int[]{Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY}));

        cal.setTime(makeDateTime("9/5/03 8:00 AM"));
        assertTrue(calendarDef.isWorkingTimeAvailable(cal));

        cal.setTime(makeDateTime("9/6/03 8:00 AM"));
        assertTrue(calendarDef.isWorkingTimeAvailable(cal));

        cal.setTime(makeDateTime("9/6/03 1:00 PM"));
        assertTrue(calendarDef.isWorkingTimeAvailable(cal));

        // No working time after 9/6/03 ends
        cal.setTime(makeDateTime("9/6/03 5:00 PM"));
        assertFalse(calendarDef.isWorkingTimeAvailable(cal));

        cal.setTime(makeDateTime("9/10/03 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeAvailable(cal));

        cal.setTime(makeDateTime("9/5/04 8:00 AM"));
        assertFalse(calendarDef.isWorkingTimeAvailable(cal));

    }

    public void testIsWorkingDayOfWeekDefined() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar cal = newCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Default calendar
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SUNDAY, Calendar.SATURDAY});
        assertTrue(calendarDef.isWorkingDayOfWeekDefined());

        // No working days
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY});
        assertFalse(calendarDef.isWorkingDayOfWeekDefined());

        // Hierarchical no working days
        calendarDef = makeCalendarDef(
                new int[]{Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY},
                null,
                null, null,
                makeCalendarDefForNonWorkingDays(new int[]{Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY}));

        assertFalse(calendarDef.isWorkingDayOfWeekDefined());

        // One Working day of week, but without any times
        calendarDef = makeCalendarDefForNonWorkingDays(new int[]{Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY});
        calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY));
        assertFalse(calendarDef.isWorkingDayOfWeekDefined());

    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#getWorkingTimeAmountForDateRange(java.util.Calendar, java.util.Calendar)}
     * for every minute from Monday @ 12:00 am to Friday @ 11:59 pm.
     */
    public void testGetWorkingTimeAmountForDateRangeEntireWeek() {

        WorkingTimeCalendarDefinition calendarDef;
        Calendar startCal;
        Calendar endCal;

        startCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        endCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        // Default calendar
        calendarDef = makeCalendarDefForNonWorkingDays(new int[] {Calendar.SUNDAY, Calendar.SATURDAY});

        // Test Every minute from Monday Janurary 6th 2003 @ 12:00 AM
        // To Friday January 10th 2003 @ 11:59 PM (120 hours)
        startCal.setTime(makeDateTime("1/6/03 12:00 AM"));
        endCal.setTime(makeDateTime("1/6/03 12:00 AM"));
        int elapsedMinutesInDay = 0;

        for (int i = 0; i < (5 * 24 * 60); i++) {
            // Figure out which day we're on and which minute of the day we're at
            int day = (i / (24 * 60));
            int dayMinute = (i % (24 * 60));

            if (dayMinute == 0) {
                // We hit midnight, start over
                elapsedMinutesInDay = 0;

            } else if (dayMinute > 0 && dayMinute <= 480 || (dayMinute > 720 && dayMinute <= 780) || (dayMinute > 1020)) {
                // 12:01am - 8:00am, 12:01pm - 1:00pm, 5:01pm - 12:00am
                // Non-working time period; the elapsed minutes do not increment

            } else if (dayMinute > 480 && dayMinute <= 720) {
                // 8:01am - 12:00pm
                // Work for day is (minute - 8 hours)
                elapsedMinutesInDay = (dayMinute - (8 * 60));

            } else if (dayMinute > 780 && dayMinute <= 1020) {
                // 1:01pm - 5:00pm
                // Work for day is (minute - 9 hours)
                elapsedMinutesInDay = (dayMinute - (9 * 60));
            }

            // Figure out the number of minutes in previous day
            int previousDayMinutes = (day * (8 * 60));

            // Total work today's minutes + yesterday's minutes
            SimpleTimeQuantity expected = new SimpleTimeQuantity(elapsedMinutesInDay).add(new SimpleTimeQuantity(previousDayMinutes));
            assertEquals(expected, calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal));
            endCal.add(Calendar.MINUTE, 1);
        }

    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#getWorkingTimeAmountForDateRange(java.util.Calendar, java.util.Calendar)}
     * for various spans.
     */
    public void testGetWorkingTimeAmountForDateRange() {
        WorkingTimeCalendarDefinition calendarDef;
        SimpleTimeQuantity tq;
        Calendar startCal;
        Calendar endCal;

        startCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        endCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        // Default calendar
        calendarDef = makeCalendarDefForNonWorkingDays(new int[] {Calendar.SUNDAY, Calendar.SATURDAY});

        // Zero hours
        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(0), tq);

        // 1 Day
        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/6/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/7/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        // 2 Days
        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/7/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(16, 0), tq);

        // 1 Week
        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/10/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(40, 0), tq);

        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/10/03 10:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(40, 0), tq);

        // 1 Day over weekend
        startCal.setTime(makeDateTime("1/10/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/13/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        // 2 Days over weekend
        startCal.setTime(makeDateTime("1/10/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/13/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(16, 0), tq);

        // 1 Day over year
        startCal.setTime(makeDateTime("12/31/02 8:00 AM"));
        endCal.setTime(makeDateTime("1/1/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        // 2 Days over year
        startCal.setTime(makeDateTime("12/31/02 8:00 AM"));
        endCal.setTime(makeDateTime("1/1/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(16, 0), tq);

        //
        //Test some with date reversals
        //

        // 1 Day
        endCal.setTime(makeDateTime("1/6/03 5:00 PM"));
        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

    }

    public void testSubtractWorkingTimeForDateRange() {
        WorkingTimeCalendarDefinition calendarDef;
        TimeQuantity tq;
        Calendar startCal;
        Calendar endCal;

        startCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        endCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        // Default calendar
        calendarDef = makeCalendarDefForNonWorkingDays(new int[] {Calendar.SUNDAY, Calendar.SATURDAY});

        // 1 Day
        startCal.setTime(makeDateTime("1/6/03 5:00 PM"));
        endCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(-8, TimeQuantityUnit.HOUR), tq);

        startCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/6/03 5:00 PM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        startCal.setTime(makeDateTime("1/7/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/6/03 8:00 AM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(-8, TimeQuantityUnit.HOUR), tq);

        // 1 Working Day, 1 weekend day
        startCal.setTime(makeDateTime("4/4/04 8:00 AM"));
        endCal.setTime(makeDateTime("4/5/04 5:00 PM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        startCal.setTime(makeDateTime("4/5/04 5:00 PM"));
        endCal.setTime(makeDateTime("4/4/04 8:00 AM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(-8, TimeQuantityUnit.HOUR), tq);

        startCal.setTime(makeDateTime("4/6/04 8:00 AM"));
        endCal.setTime(makeDateTime("4/4/04 8:00 AM"));
        tq = calendarDef.subtractWorkingTimeForDateRange(startCal, endCal);
        assertEquals(new TimeQuantity(-8, TimeQuantityUnit.HOUR), tq);
    }

    /**
     * Tests {@link WorkingTimeCalendarDefinition#getWorkingTimeAmountForDateRange(java.util.Calendar, java.util.Calendar)}
     * for various spans with a custom working time calendar.
     */
    public void testGetWorkingTimeAmountForDateRangeCustomCalendar() {
        WorkingTimeCalendarDefinition calendarDef;
        SimpleTimeQuantity tq;
        Calendar startCal;
        Calendar endCal;

        startCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        endCal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        // Standard Working Days of Week
        // Wednesday January 8th 2003 is non-working day
        // Sunday January 12th 2003 is working day
        calendarDef = makeCalendarDef(new int[] {Calendar.SUNDAY, Calendar.SATURDAY},
                new String[] {"1/8/03"},
                new String[]{"1/12/03"});

        //
        // Zero hours
        //

        // Wednesday 8th @ 8:00am to Wednesday 8th @ 5:00pm
        startCal.setTime(makeDateTime("1/8/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/8/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(0, 0), tq);

        // Wednesday 8th @ 12:01pm to Wednesday 8th @ 12:59pm
        startCal.setTime(makeDateTime("1/8/03 12:01 PM"));
        endCal.setTime(makeDateTime("1/8/03 12:59 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(0, 0), tq);

        // Tuesday 5th @ 5:00pm to Thursday 9th @ 8:00am
        startCal.setTime(makeDateTime("1/7/03 5:00 PM"));
        endCal.setTime(makeDateTime("1/9/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(0, 0), tq);

        // Friday 10th @ 5:00pm to Sunday 12th @ 8:00am
        startCal.setTime(makeDateTime("1/10/03 5:00 PM"));
        endCal.setTime(makeDateTime("1/12/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(0, 0), tq);


        //
        // 1 Day
        //

        // Sunday 12th @ 8:00am to Sunday 12th @ 5:00pm
        startCal.setTime(makeDateTime("1/12/03 8:00 AM"));
        endCal.setTime(makeDateTime("1/12/03 5:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        // Friday 10th @ 5:00pm to Monday 13th @ 8:00am
        startCal.setTime(makeDateTime("1/10/03 5:00 PM"));
        endCal.setTime(makeDateTime("1/13/03 8:00 AM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

        // Friday 10th @ 12:00pm to Sunday 12th @ 1:00pm
        startCal.setTime(makeDateTime("1/10/03 12:00 PM"));
        endCal.setTime(makeDateTime("1/12/03 1:00 PM"));
        tq = calendarDef.getWorkingTimeAmountForDateRange(startCal, endCal);
        assertEquals(new SimpleTimeQuantity(8, 0), tq);

    }


    //
    // Static Helper methods
    //

    /**
     * Helper method that checks that the date on the specified calendar has
     * default working times.
     * @param calendarDef the working time calendar definition to use
     * @param calendar the calendar set to the correct date to check
     */
    private static void checkDefaultWorkingTimes(WorkingTimeCalendarDefinition calendarDef, Calendar calendar) {

        Calendar cal = (Calendar) calendar.clone();

        // 7:00 AM
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        assertFalse(calendarDef.isWorkingTime(cal));

        // 7:59 AM
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 59);
        assertFalse(calendarDef.isWorkingTime(cal));

        // 8:00 AM
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        assertTrue(calendarDef.isWorkingTime(cal));

        // 11:59 AM
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 59);
        assertTrue(calendarDef.isWorkingTime(cal));

        // 12:00 PM
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        assertFalse(calendarDef.isWorkingTime(cal));

        // 12:59 PM
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 59);
        assertFalse(calendarDef.isWorkingTime(cal));

        // 1:00 PM
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 0);
        assertTrue(calendarDef.isWorkingTime(cal));

        // 4:59 PM
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 59);
        assertTrue(calendarDef.isWorkingTime(cal));

        // 5:00 PM
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 0);
        assertFalse(calendarDef.isWorkingTime(cal));

        // 11:59 PM
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        assertFalse(calendarDef.isWorkingTime(cal));

    }

    /**
     * Creates a new calendar with time zone <code>America/Los_Angeles</code>.
     * @return the calendar
     */
    private static Calendar newCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    /**
     * Creates a WorkingTimeCalendarDefinition for the specified non working days
     * @param nonWorkingDays the array where each entry is an integer in
     * the range 0..7 corresponding to Sunday .. Saturday.  Those entries
     * are added as non working days to the calendar definition.  Remaining
     * days of the week are added as default working days.
     * @return the calendar definition
     */
    public static WorkingTimeCalendarDefinition makeCalendarDefForNonWorkingDays(int[] nonWorkingDays) {

        // Add all days of week to set
        Set daysOfWeek = new HashSet();
        for (int i = 1; i <= 7; i++) {
            daysOfWeek.add(new Integer(i));
        }

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = new WorkingTimeCalendarDefinition();

        // Add all the non working days
        for (int i = 0; i < nonWorkingDays.length; i++) {
            calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(nonWorkingDays[i]));
            daysOfWeek.remove(new Integer(nonWorkingDays[i]));
        }

        // Add the remaining days as working days
        for (Iterator it = daysOfWeek.iterator(); it.hasNext(); ) {
            calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(((Integer) it.next()).intValue()));
        }

        return calendarDef;
    }

    /**
     * Creates a WorkingTimeCalendarDefinition for the specified non working days,
     * non working dates and working dates
     * <p>
     * Interprets all date strings with time zone <code>America/Los_Angeles</code>.
     * </p>
     * @param nonWorkingDays the array where each entry is an integer in
     * the range 0..7 corresponding to Sunday .. Saturday.  Those entries
     * are added as non working days to the calendar definition.  Remaining
     * days of the week are added as default working days.
     * @param nonWorkingDateStrings an array of non working date strings assumed to be in
     * SHORT format for US locale; ignored when null or empty
     * @param workingDateStrings an array of working date strings assumed to be in
     * SHORT format for US locale; ignored when null or empty
     * @return the calendar definition
     */
    public static WorkingTimeCalendarDefinition makeCalendarDef(int[] nonWorkingDays, String[] nonWorkingDateStrings, String[] workingDateStrings) {

        Date[] nonWorkingDates = null;
        Date[] workingDates = null;

        // Convert non working strings to dates
        if (nonWorkingDateStrings != null) {
            nonWorkingDates = new Date[nonWorkingDateStrings.length];
            for (int i = 0; i < nonWorkingDateStrings.length; i++) {
                nonWorkingDates[i] = makeDate(nonWorkingDateStrings[i]);
            }
        }

        // Convert working strings to dates
        if (workingDateStrings != null) {
            workingDates = new Date[workingDateStrings.length];
            for (int i = 0; i < workingDateStrings.length; i++) {
                workingDates[i] = makeDate(workingDateStrings[i]);
            }
        }

        return makeCalendarDef(nonWorkingDays, nonWorkingDates, workingDates);
    }

    /**
     * Creates a WorkingTimeCalendarDefinition for the specified non working days,
     * non working dates and working dates
     * <p>
     * Assumes all dates were constructed with time zone <code>America/Los_Angeles</code>.
     * </p>
     * @param nonWorkingDays the array where each entry is an integer in
     * the range 0..7 corresponding to Sunday .. Saturday.  Those entries
     * are added as non working days to the calendar definition.  Remaining
     * days of the week are added as default working days.
     * @param nonWorkingDates an array of non working dates; ignored when null or empty
     * @param workingDates an array of working dates; ignored when null or empty
     * @return the calendar definition
     */
    public static WorkingTimeCalendarDefinition makeCalendarDef(int[] nonWorkingDays, Date[] nonWorkingDates, Date[] workingDates) {

        WorkingTimeCalendarDefinition calendarDef = makeCalendarDefForNonWorkingDays(nonWorkingDays);

        if (nonWorkingDates != null) {
            // Add non working dates
            for (int i = 0; i < nonWorkingDates.length; i++) {
                calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(nonWorkingDates[i], TimeZone.getTimeZone("America/Los_Angeles"))));
            }
        }

        if (workingDates != null) {
            // Add working dates
            for (int i = 0; i < workingDates.length; i++) {
                DateEntry entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(workingDates[i], TimeZone.getTimeZone("America/Los_Angeles")));
                WorkingTimeCalendarEntryTest.addDefaultTimes(entry);
                calendarDef.addDateEntry(entry);
            }
        }

        return calendarDef;
    }

    /**
     * Creates a WorkingTimeCalendarDefinition with the specified base calendar.
     * <p>
     * Interprets all date strings with time zone <code>America/Los_Angeles</code>.
     * </p>
     * @param nonWorkingDays the non working days of week; when null, no
     * non working days of week are added
     * @param workingDays the working days of week; when null, no working days
     * of week are added
     * @param nonWorkingDateStrings the non working dates assumed to be in
     * SHORT format for US locale
     * @param workingDateStrings the working dates assumed to be in SHORt format
     * for US locale
     * @param baseCalendarDef the base calendar; required
     * @return the calendar definition
     */
    public static WorkingTimeCalendarDefinition makeCalendarDef(int[] nonWorkingDays, int[] workingDays,
            String[] nonWorkingDateStrings, String[] workingDateStrings,
            WorkingTimeCalendarDefinition baseCalendarDef) {

        if (baseCalendarDef == null) {
            throw new NullPointerException("Base calendar definitions are required");
        }

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = new WorkingTimeCalendarDefinition(baseCalendarDef);

        // Add all the non working days
        if (nonWorkingDays != null) {
            for (int i = 0; i < nonWorkingDays.length; i++) {
                calendarDef.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(nonWorkingDays[i]));
            }
        }

        // Add all the working days
        if (workingDays != null) {
            for (int i = 0; i < workingDays.length; i++) {
                DayOfWeekEntry entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(workingDays[i]);
                WorkingTimeCalendarEntryTest.addDefaultTimes(entry);
                calendarDef.addDayOfWeekEntry(entry);
            }
        }

        // Add non working dates
        if (nonWorkingDateStrings != null) {
            for (int i = 0; i < nonWorkingDateStrings.length; i++) {
                calendarDef.addDateEntry(WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(makeDate(nonWorkingDateStrings[i]), TimeZone.getTimeZone("America/Los_Angeles"))));
            }
        }

        // Add working dates
        if (workingDateStrings != null) {
            for (int i = 0; i < workingDateStrings.length; i++) {
                DateEntry entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(makeDate(workingDateStrings[i]), TimeZone.getTimeZone("America/Los_Angeles")));
                WorkingTimeCalendarEntryTest.addDefaultTimes(entry);
                calendarDef.addDateEntry(entry);
            }
        }

        return calendarDef;
    }

    /**
     * Parses a date in the short format for US locale assuming PST/PDT
     * timezone.
     * @param dateString the date to parse
     * @return the parsed date or null if there was a problem parsing; the
     * date's time components are set to midnight
     */
    public static Date makeDate(String dateString) {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

        Date date;

        try {
            Calendar cal = new GregorianCalendar(timeZone);
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            df.setTimeZone(timeZone);

            // Set the calendar to the parsed date string, resetting time
            // components to midnight
            cal.setTime(df.parse(dateString));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            date = cal.getTime();

        } catch (ParseException e) {
            date = null;
        }

        return date;
    }

    /**
     * Parses a date in the short format for US locale assuming PST/PDT
     * timezone.
     * @param dateTimeString the date and time to parse
     * @return the parsed date or null if there was a problem parsing
     */
    public static Date makeDateTime(String dateTimeString) {
        Date date;

        try {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            date = df.parse(dateTimeString);
        } catch (ParseException e) {
            date = null;
        }

        return date;
    }

    /**
     * Creates a working time calendar definition containing a 24 hour work
     * week.
     * @return the calendar definition with Sunday .. Saturday with 00:00 - 24:00
     */
    public static WorkingTimeCalendarDefinition make24HourCalendarDefinition() {
        WorkingTimeCalendarEntry entry;

        WorkingTimeCalendarDefinition calendarDef = new WorkingTimeCalendarDefinition();

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.SUNDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.TUESDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.WEDNESDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.THURSDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.FRIDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.SATURDAY);
        WorkingTimeCalendarEntryTest.add24HourTimes(entry);
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Creates a working time calendar containing a nightshift work week.
     * <p>
     * Defines as follows:
     * <li>Sunday : non working day
     * <li>Monday : 11:00 PM - 12:00 AM
     * <li>Tuesday..Friday : 12:00 AM - 3:00 AM, 4:00 AM - 8:00 AM, 11:00 PM - 12:00 AM
     * <li>Saturday : 12:00 AM - 3:00 AM, 4:00 AM - 8:00 AM
     * @return the calendar definition
     */
    public static WorkingTimeCalendarDefinition makeNightshiftCalendarDefinition() {
        WorkingTimeCalendarEntry entry;

        WorkingTimeCalendarDefinition calendarDef = new WorkingTimeCalendarDefinition();

        entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.MONDAY);
        entry.setWorkingTimes(Collections.singleton(new WorkingTime(23, 0, 24, 0)));
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.TUESDAY);
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.WEDNESDAY);
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.THURSDAY);
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.FRIDAY);
        WorkingTimeCalendarEntryTest.addNightshiftTimes(entry);
        calendarDef.addEntry(entry);

        entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(Calendar.SATURDAY);
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(0, 0, 3, 0),
            new WorkingTime(4, 0, 8, 0)
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }
}
