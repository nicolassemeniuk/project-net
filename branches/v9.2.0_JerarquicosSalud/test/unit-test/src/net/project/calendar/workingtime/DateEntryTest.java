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
|    $Revision: 16593 $
|        $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|      $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>DateEntryTest</code>.
 * @author
 * @since
 */
public class DateEntryTest extends TestCase {

    public DateEntryTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DateEntryTest.class);

        return suite;
    }

    /**
     * Tests constructor.
     */
    public void testDateEntrySingleDay() {

        DayOfYear startDayOfYear;
        DayOfYear expectedDayOfYear;
        DateEntry dateEntry;

        // Null Date
        dateEntry = null;
        try {
            dateEntry = new DateEntry(null, true, true);
        } catch (NullPointerException e) {
            // Expected
        }
        assertNull(dateEntry);

        dateEntry = new DateEntry(new DayOfYear(2003, Calendar.SEPTEMBER, 23), true, true);
        assertTrue(dateEntry.getStartDayOfYear() != null);
        assertTrue(dateEntry.isWorkingDay());
        assertDefaultWorkingTimes(dateEntry);

        dateEntry = new DateEntry(new DayOfYear(2003, Calendar.SEPTEMBER, 23), false, true);
        assertTrue(dateEntry.getStartDayOfYear() != null);
        assertFalse(dateEntry.isWorkingDay());

        // Check two dates
        startDayOfYear = new DayOfYear(2003, Calendar.JUNE, 18);
        expectedDayOfYear = new DayOfYear(2003, Calendar.JUNE, 18);
        dateEntry = new DateEntry(startDayOfYear, true, true);
        assertEquals(expectedDayOfYear, dateEntry.getStartDayOfYear());
        assertDefaultWorkingTimes(dateEntry);

        //
        // Now check no default working times
        //
        startDayOfYear = new DayOfYear(2003, Calendar.JUNE, 18);
        expectedDayOfYear = new DayOfYear(2003, Calendar.JUNE, 18);
        dateEntry = new DateEntry(startDayOfYear, true, false);
        assertEquals(expectedDayOfYear, dateEntry.getStartDayOfYear());
        assertTrue("Expected empty working times collection", dateEntry.getWorkingTimes().isEmpty());

    }

    /**
     * Tests constructor.
     */
    public void testDateEntrySpanDays() {

        DayOfYear startDayOfYear;
        DayOfYear endDayOfYear;
        DateEntry dateEntry;

        //
        // Null Dates
        //

        dateEntry = null;
        try {
            dateEntry = new DateEntry(null, new DayOfYear(2003, Calendar.SEPTEMBER, 23), true, true);
            fail("Unexepcetd success with null start date");
        } catch (NullPointerException e) {
            // Expected
        }
        assertNull(dateEntry);

        dateEntry = null;
        try {
            dateEntry = new DateEntry(new DayOfYear(2003, Calendar.SEPTEMBER, 23), null, true, true);
            fail("Unexepcetd success with null end date");
        } catch (NullPointerException e) {
            // Expected
        }
        assertNull(dateEntry);

        dateEntry = null;
        try {
            dateEntry = new DateEntry(null, null, true, true);
            fail("Unexepcetd success with null dates");
        } catch (NullPointerException e) {
            // Expected
        }
        assertNull(dateEntry);

        //
        // end date before start date
        //

        dateEntry = null;
        startDayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 23);
        endDayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 22);
        try {
            dateEntry = new DateEntry(startDayOfYear, endDayOfYear, true, true);
            fail("Unexepcetd success when end date before start date");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(dateEntry);

        //
        // Dates equal
        //

        startDayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 23);
        endDayOfYear = startDayOfYear;
        dateEntry = new DateEntry(startDayOfYear, endDayOfYear, true, true);
        assertTrue("Start date was null", dateEntry.getStartDayOfYear() != null);
        assertTrue("End date was null", dateEntry.getEndDayOfYear() != null);
        assertEquals(dateEntry.getStartDayOfYear(), dateEntry.getEndDayOfYear());
        assertTrue("Date entry was not a working day", dateEntry.isWorkingDay());
        assertDefaultWorkingTimes(dateEntry);

        startDayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 23);
        endDayOfYear = startDayOfYear;
        dateEntry = new DateEntry(startDayOfYear, endDayOfYear, false, true);
        assertTrue("Start date was null", dateEntry.getStartDayOfYear() != null);
        assertTrue("End date was null", dateEntry.getEndDayOfYear() != null);
        assertEquals(dateEntry.getStartDayOfYear(), dateEntry.getEndDayOfYear());
        assertTrue("Date entry was a working day", !dateEntry.isWorkingDay());

    }

    public void testGetSpanDates() {

        DateEntry dateEntry;
        DayOfYear startDate;
        DayOfYear endDate;
        Collection spanDayOfYear;
        DayOfYear[] spanDayOfYearArray;

        // Start Date: 6/18/03
        // End Date: 6/18/03
        // Expected: 1 date: 6/18/03
        startDate = new DayOfYear(2003, Calendar.JUNE, 18);
        endDate = startDate;
        dateEntry = new DateEntry(startDate, endDate, true, true);
        spanDayOfYear = dateEntry.getSpanDayOfYear();
        spanDayOfYearArray = (DayOfYear[]) spanDayOfYear.toArray(new DayOfYear[]{});

        assertEquals(1, spanDayOfYear.size());
        assertEquals(new DayOfYear(2003, Calendar.JUNE, 18), spanDayOfYearArray[0]);

        // Start Date: 6/18/03
        // End Date: 6/21/03
        // Expected: 4 dates: 6/18/03, 6/19/03, 6/20/03, 6/21/03
        startDate = new DayOfYear(2003, Calendar.JUNE, 18);
        endDate = new DayOfYear(2003, Calendar.JUNE, 21);
        dateEntry = new DateEntry(startDate, endDate, true, true);
        spanDayOfYear = dateEntry.getSpanDayOfYear();
        spanDayOfYearArray = (DayOfYear[]) spanDayOfYear.toArray(new DayOfYear[]{});

        assertEquals(4, spanDayOfYear.size());
        assertEquals(new DayOfYear(2003, Calendar.JUNE, 18), spanDayOfYearArray[0]);
        assertEquals(new DayOfYear(2003, Calendar.JUNE, 19), spanDayOfYearArray[1]);
        assertEquals(new DayOfYear(2003, Calendar.JUNE, 20), spanDayOfYearArray[2]);
        assertEquals(new DayOfYear(2003, Calendar.JUNE, 21), spanDayOfYearArray[3]);

    }

    public void testToString() {
        DateEntry dateEntry;

        dateEntry = new DateEntry(new DayOfYear(2003, Calendar.SEPTEMBER, 23), true, true);
        assertNotNull(dateEntry.toString());
    }

    public void testEqualsHashcode() {

        DateEntry dateEntry1;
        DateEntry dateEntry2;

        // Test Equality

        // Instance Equality
        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), new DayOfYear(2003, Calendar.JUNE, 19), true, true);
        assertTrue(dateEntry1.equals(dateEntry1));

        // Regular equality
        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), new DayOfYear(2003, Calendar.JUNE, 19), true, true);
        dateEntry2 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), new DayOfYear(2003, Calendar.JUNE, 19), true, true);
        assertTrue(dateEntry1.equals(dateEntry2));
        assertEquals(dateEntry1.hashCode(), dateEntry2.hashCode());

        // Test inequality

        // Wrong class
        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), new DayOfYear(2003, Calendar.JUNE, 19), true, true);
        assertFalse(dateEntry1.equals(new Object()));

        // Regular inequality
        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 19), true, true);
        dateEntry2 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 20), true, true);
        assertFalse(dateEntry1.equals(dateEntry2));
        assertFalse(dateEntry1.hashCode() == dateEntry2.hashCode());

        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 19), new DayOfYear(2003, Calendar.JUNE, 20), true, true);
        dateEntry2 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 20), new DayOfYear(2003, Calendar.JUNE, 21), true, true);
        assertFalse(dateEntry1.equals(dateEntry2));
        assertFalse(dateEntry1.hashCode() == dateEntry2.hashCode());

        dateEntry1 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), true, true);
        dateEntry2 = new DateEntry(new DayOfYear(2003, Calendar.JUNE, 18), false, true);
        assertFalse(dateEntry1.equals(dateEntry2));
        assertFalse(dateEntry1.hashCode() == dateEntry2.hashCode());

    }

    public void testIsWorkingTimeOnOrAfter() {

        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        DateEntry entry;

        //
        // Non working single day
        //
        entry = new DateEntry(new DayOfYear(2003, Calendar.AUGUST, 19), false, true);

        // Before day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDate("08/17/03"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // On day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/18/03 12:00 PM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // After day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDate("08/20/03"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        //
        // Non working day, span days
        //
        entry = new DateEntry(new DayOfYear(2003, Calendar.AUGUST, 19), new DayOfYear(2003, Calendar.AUGUST, 21), false, true);

        // Before start day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDate("08/18/03"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // Middle day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDate("08/20/03"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // After end day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDate("08/22/03"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));


        //
        // Working day, Single day
        //
        entry = new DateEntry(new DayOfYear(2003, Calendar.AUGUST, 19), true, true);

        // Before day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/18/03 8:00 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On, before working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 7:59 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On, start of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 8:00 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On, middle of working time (happesn to be in non working time)
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 12:30 PM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On, end of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 5:00 PM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // On, after end of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 5:01 PM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // After day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/20/03 8:00 AM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));


        //
        // Span days
        //
        entry = new DateEntry(new DayOfYear(2003, Calendar.AUGUST, 19), new DayOfYear(2003, Calendar.AUGUST, 21), true, true);

        // Before start day
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/18/03 8:00 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On start, before working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 7:59 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On start, start of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/19/03 8:00 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On middle day, before start of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/20/03 7:00 AM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On middle day, after end of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/20/03 6:00 PM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On end day, middle working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/21/03 12:30 PM"));
        assertTrue(entry.isWorkingTimeOnOrAfter(cal));

        // On end, at end of working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/21/03 5:00 PM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // On end, after working time
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/21/03 6:00 PM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));

        // After end date
        cal.setTime(WorkingTimeCalendarDefinitionTest.makeDateTime("08/22/03 8:00 AM"));
        assertFalse(entry.isWorkingTimeOnOrAfter(cal));
    }

    public void testIsOverlappedBy() {

        DateEntry entry;
        DateEntry newEntry;

        // Single date, single date, not overlapped
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        assertFalse(entry.isOverlappedBy(newEntry));

        // Single date, single date, overlapped
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Single date, date range, not overlapped
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertFalse(entry.isOverlappedBy(newEntry));

        // Single date, date range, overlapped; date within range
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 29));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Single date, date range, overlapped; date on start date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Single date, date range, overlapped; date on end date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, single date, not overlapped
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 27));
        assertFalse(entry.isOverlappedBy(newEntry));

        // Date range, single date, overlapped; date within range
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 29));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, single date, overlapped; date on start date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, single date, overlapped; date on end date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, date range, not overlapped
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 28), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertFalse(entry.isOverlappedBy(newEntry));

        // Date range, date range, overlapped; starts between
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 26), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, date range, overlapped; ends between
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.AUGUST, 27));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 23), new DayOfYear(2003, Calendar.AUGUST, 26));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, date range, overlapped; starts and ends between
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Date range, date range, overlapped; spans across
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 20), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Exact same ranges
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Starts on end date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 26), new DayOfYear(2003, Calendar.AUGUST, 30));
        assertTrue(entry.isOverlappedBy(newEntry));

        // Ends on start date
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 26), new DayOfYear(2003, Calendar.AUGUST, 30));
        newEntry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(2003, Calendar.AUGUST, 22), new DayOfYear(2003, Calendar.AUGUST, 26));
        assertTrue(entry.isOverlappedBy(newEntry));

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

}
