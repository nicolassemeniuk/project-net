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
|    $Revision: 17091 $
|        $Date: 2008-03-24 12:05:39 +0530 (Mon, 24 Mar 2008) $
|      $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junitx.util.PrivateAccessor;

/**
 * Tests <code>DayOfYearTest</code>.
 * @author
 * @since
 */
public class DayOfYearTest extends TestCase {

    public DayOfYearTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DayOfYearTest.class);

        return suite;
    }

    /**
     * Tests {@link DayOfYear#DayOfYear(Date, TimeZone)} with null parameters.
     */
    public void testDayOfYearVariant1Null() {

        try {
            new DayOfYear(null, null);
            fail("Exepcted NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new DayOfYear(null, TimeZone.getDefault());
            fail("Exepcted NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new DayOfYear(new Date(), null);
            fail("Exepcted NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

    }

    /**
     * Tests {@link DayOfYear#DayOfYear(Date, TimeZone)}.
     */
    public void testDayOfYearVariant1() {
        // All other permutations tested by testToDate

        // Check no error occurs
        new DayOfYear(new Date(), TimeZone.getDefault());
    }

    /**
     * Tests {@link DayOfYear#DayOfYear(int, int, int)}.
     * @throws NoSuchFieldException if an unexpected error occurs checking
     * results
     */
    public void testDayOfYearVariant2() throws NoSuchFieldException {

        // Illegal Values
        try {
            new DayOfYear(2003, Calendar.JANUARY, 32);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new DayOfYear(2003, Calendar.FEBRUARY, 29);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new DayOfYear(2003, Calendar.SEPTEMBER, 31);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new DayOfYear(2003, Calendar.JANUARY, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new DayOfYear(2003, 12, 31);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new DayOfYear(2003, -1, 31);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new DayOfYear(-1, Calendar.JANUARY, 1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Legal values
        // We check the private fields here to confirm that the date
        // is _exactly_ as specified
        assertFields(2003, Calendar.JANUARY, 31, new DayOfYear(2003, Calendar.JANUARY, 31));
        assertFields(2004, Calendar.FEBRUARY, 29, new DayOfYear(2004, Calendar.FEBRUARY, 29));

    }

    /**
     * Asserts that the internal values of year, month and day match
     * the specified parameters.
     * @param yearValue the expected year
     * @param monthValue the expected month
     * @param dayValue the expected day
     * @param dayOfYear the dayOfYear to check
     * @throws NoSuchFieldException if there is a problem checking
     */
    private static void assertFields(int yearValue, int monthValue, int dayValue, DayOfYear dayOfYear)
            throws NoSuchFieldException {
        assertEquals("Unexpected year", yearValue, ((Integer) PrivateAccessor.getField(dayOfYear, "year")).intValue());
        assertEquals("Unexpected month", monthValue, ((Integer) PrivateAccessor.getField(dayOfYear, "month")).intValue());
        assertEquals("Unexpected day", dayValue, ((Integer) PrivateAccessor.getField(dayOfYear, "day")).intValue());
    }

    /**
     * Tests {@link DayOfYear#getDateForStore(DayOfYear)} and
     * Tests {@link DayOfYear#makeFromStoredDate(Date)}.
     */
    public void testStoredDate() {

        DayOfYear dayOfYear;

        try {
            DayOfYear.getDateForStore(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            DayOfYear.makeFromStoredDate(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Check conversion results in equal dayOfYear
        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        assertEquals(dayOfYear, DayOfYear.makeFromStoredDate(DayOfYear.getDateForStore(dayOfYear)));

    }

    /**
     * Tests {@link DayOfYear#toDate(TimeZone)}.
     */
    public void testToDate() {

        Date date;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);

        // Same date but at midnight for same time zone
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(2003, Calendar.SEPTEMBER, 23, 17, 11, 55);
        date = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        assertEquals(cal.getTime(), new DayOfYear(date, TimeZone.getTimeZone("America/Los_Angeles")).toDate(TimeZone.getTimeZone("America/Los_Angeles")));

        // Same date when same day of year in two time zones
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0, 0);
        date = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        assertEquals(cal.getTime(), new DayOfYear(date, TimeZone.getTimeZone("Europe/London")).toDate(TimeZone.getTimeZone("America/Los_Angeles")));

        // Different date when different day of year in two time zones
        // September 23rd at 11:00 PM PDT is September 24th GMT
        // Which, when converted back as PDT is different
        cal.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(2003, Calendar.SEPTEMBER, 23, 23, 0, 0);
        date = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        assertFalse(cal.getTime().equals(new DayOfYear(date, TimeZone.getTimeZone("Europe/London")).toDate(TimeZone.getTimeZone("America/Los_Angeles"))));

    }

    /**
     * Tests {@link DayOfYear#equals(Object)}.
     */
    public void testEquals() {

        DayOfYear dayOfYear;
        Date date1;
        Date date2;
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.MILLISECOND, 0);

        // Identity Equality
        dayOfYear = new DayOfYear(new Date(), TimeZone.getDefault());
        assertEquals(dayOfYear, dayOfYear);

        // Same date Equality
        date1 = new Date();
        assertEquals(new DayOfYear(date1, TimeZone.getDefault()), new DayOfYear(date1, TimeZone.getDefault()));

        // In following tests, time zone doesn't matter
        // As long as we use the same time zone for both

        // Different year inequality
        cal.set(2003, Calendar.SEPTEMBER, 23, 16, 51, 55);
        date1 = cal.getTime();
        cal.set(2004, Calendar.SEPTEMBER, 23, 16, 51, 55);
        date2 = cal.getTime();
        assertFalse(new DayOfYear(date1, timeZone).equals(new DayOfYear(date2, timeZone)));

        // Different month inequality
        cal.set(2003, Calendar.SEPTEMBER, 23, 16, 51, 55);
        date1 = cal.getTime();
        cal.set(2003, Calendar.OCTOBER, 23, 16, 51, 55);
        date2 = cal.getTime();
        assertFalse(new DayOfYear(date1, timeZone).equals(new DayOfYear(date2, timeZone)));

        // Different day inequality
        cal.set(2003, Calendar.SEPTEMBER, 23, 16, 51, 55);
        date1 = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 24, 16, 51, 55);
        date2 = cal.getTime();
        assertFalse(new DayOfYear(date1, timeZone).equals(new DayOfYear(date2, timeZone)));

        // Different times don't matter equality
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0, 0);
        date1 = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 23, 59, 59);
        date2 = cal.getTime();
        assertTrue(new DayOfYear(date1, timeZone).equals(new DayOfYear(date2, timeZone)));

        // Confirm equality when using different time zones but
        // that happen to represent the same day
        cal.set(2003, Calendar.SEPTEMBER, 23, 0, 0, 0);
        date1 = cal.getTime();
        assertTrue(new DayOfYear(date1, timeZone).equals(new DayOfYear(date1, TimeZone.getTimeZone("Europe/London"))));

        // Confirm equality when using different time zones and different dates
        // that happen to represent the same day
        cal.set(2003, Calendar.SEPTEMBER, 24, 0, 0, 0);
        date1 = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 23, 17, 0, 0);
        date2 = cal.getTime();
        assertTrue(new DayOfYear(date1, timeZone).equals(new DayOfYear(date2, TimeZone.getTimeZone("Europe/London"))));

    }

    /**
     * Tests {@link DayOfYear#hashCode()}.
     */
    public void testHashCode() {

        DayOfYear dayOfYear1;
        DayOfYear dayOfYear2;
        Date date;

        // Equal objects, equal hashcode
        date = new Date();
        dayOfYear1 = new DayOfYear(date, TimeZone.getTimeZone("America/Los_Angeles"));
        dayOfYear2 = new DayOfYear(date, TimeZone.getTimeZone("America/Los_Angeles"));
        assertEquals(dayOfYear1, dayOfYear2);
        assertEquals(dayOfYear1.hashCode(), dayOfYear2.hashCode());

    }

    /**
     * Tests {@link DayOfYear#toString()}.
     */
    public void testToString() {
        assertNotNull(new DayOfYear(new Date(), TimeZone.getDefault()).toString());
    }

    /**
     * Tests {@link DayOfYear#isBefore(DayOfYear)}.
     */
    public void testIsBefore() {
        DayOfYear dayOfYear;

        try {
            dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear.isBefore(null);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        assertTrue(dayOfYear.isBefore(new DayOfYear(2003, Calendar.SEPTEMBER, 25)));
        assertTrue(dayOfYear.isBefore(new DayOfYear(2003, Calendar.OCTOBER, 1)));
        assertTrue(dayOfYear.isBefore(new DayOfYear(2004, Calendar.SEPTEMBER, 1)));
        assertTrue(dayOfYear.isBefore(new DayOfYear(2003, Calendar.OCTOBER, 25)));
        assertTrue(dayOfYear.isBefore(new DayOfYear(2004, Calendar.OCTOBER, 25)));
        assertTrue(dayOfYear.isBefore(new DayOfYear(2004, Calendar.OCTOBER, 1)));

        assertFalse(dayOfYear.isBefore(dayOfYear));
        assertFalse(dayOfYear.isBefore(new DayOfYear(2003, Calendar.SEPTEMBER, 23)));
        assertFalse(dayOfYear.isBefore(new DayOfYear(2003, Calendar.AUGUST, 25)));
        assertFalse(dayOfYear.isBefore(new DayOfYear(2002, Calendar.SEPTEMBER, 25)));

    }

    /**
     * Tests {@link DayOfYear#isAfter(DayOfYear)}.
     */
    public void testIsAfter() {
        DayOfYear dayOfYear;

        try {
            dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear.isAfter(null);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        assertTrue(dayOfYear.isAfter(new DayOfYear(2003, Calendar.SEPTEMBER, 23)));
        assertTrue(dayOfYear.isAfter(new DayOfYear(2003, Calendar.AUGUST, 29)));
        assertTrue(dayOfYear.isAfter(new DayOfYear(2002, Calendar.SEPTEMBER, 29)));
        assertTrue(dayOfYear.isAfter(new DayOfYear(2003, Calendar.AUGUST, 23)));
        assertTrue(dayOfYear.isAfter(new DayOfYear(2002, Calendar.AUGUST, 23)));

        assertFalse(dayOfYear.isAfter(dayOfYear));
        assertFalse(dayOfYear.isAfter(new DayOfYear(2003, Calendar.SEPTEMBER, 25)));
        assertFalse(dayOfYear.isAfter(new DayOfYear(2003, Calendar.OCTOBER, 1)));
        assertFalse(dayOfYear.isAfter(new DayOfYear(2004, Calendar.SEPTEMBER, 1)));

    }

    /**
     * Tests {@link DayOfYear#getAllBetween(DayOfYear)}.
     */
    public void testGetAllBetween() {
        DayOfYear dayOfYear;
        Collection results;

        try {
            dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear.getAllBetween(null);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);

        // Earlier date, returns first date
        results = dayOfYear.getAllBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23));
        assertTrue("Expected 1 day of year", (results.size() == 1));
        assertTrue(results.toArray()[0].equals(dayOfYear));

        // Same date returns first date
        results = dayOfYear.getAllBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 24));
        assertTrue("Expected 1 day of year", (results.size() == 1));
        assertTrue(results.toArray()[0].equals(dayOfYear));

        // Later date returns all
        results = dayOfYear.getAllBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 25));
        assertTrue("Expected 2 results", (results.size() == 2));
        assertTrue(results.toArray()[0].equals(new DayOfYear(2003, Calendar.SEPTEMBER, 24)));
        assertTrue(results.toArray()[1].equals(new DayOfYear(2003, Calendar.SEPTEMBER, 25)));

        // Later date returns all
        dayOfYear = new DayOfYear(2003, Calendar.DECEMBER, 1);
        results = dayOfYear.getAllBetween(new DayOfYear(2004, Calendar.JANUARY, 31));
        Object[] resultArray = results.toArray();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(2003, Calendar.DECEMBER, 1, 0, 0, 0);
        int count = 0;
        do {
            assertEquals(resultArray[count], new DayOfYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        } while (cal.get(Calendar.MONTH) < Calendar.FEBRUARY);

    }

    /**
     * Tests {@link DayOfYear#isOnOrBetween(DayOfYear, DayOfYear)}
     * with null parameters.
     */
    public void testIsOnOrBetweenNull() {
        DayOfYear dayOfYear;

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);

        try {
            dayOfYear.isOnOrBetween(null, null);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear.isOnOrBetween(dayOfYear, null);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
            dayOfYear.isOnOrBetween(null, dayOfYear);
            fail("Exepected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

    }

    /**
     * Tests {@link DayOfYear#isOnOrBetween(DayOfYear, DayOfYear)}.
     */
    public void testIsOnOrBetween() {
        DayOfYear dayOfYear;

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);

        // Same date
        assertTrue(dayOfYear.isOnOrBetween(dayOfYear, dayOfYear));
        // Starts On
        assertTrue(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 24), new DayOfYear(2003, Calendar.SEPTEMBER, 25)));
        // Ends on
        assertTrue(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23), new DayOfYear(2003, Calendar.SEPTEMBER, 24)));
        // Between
        assertTrue(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23), new DayOfYear(2003, Calendar.SEPTEMBER, 25)));
        // Between Months
        assertTrue(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.AUGUST, 25), new DayOfYear(2003, Calendar.OCTOBER, 25)));
        // Between Years
        assertTrue(dayOfYear.isOnOrBetween(new DayOfYear(2002, Calendar.SEPTEMBER, 26), new DayOfYear(2004, Calendar.SEPTEMBER, 26)));

        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 25), new DayOfYear(2003, Calendar.SEPTEMBER, 26)));
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 22), new DayOfYear(2003, Calendar.SEPTEMBER, 23)));
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.OCTOBER, 23), new DayOfYear(2003, Calendar.OCTOBER, 25)));
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2004, Calendar.AUGUST, 1), new DayOfYear(2004, Calendar.OCTOBER, 1)));

        // End parameter before start parameter; always false
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23), new DayOfYear(2003, Calendar.SEPTEMBER, 22)));
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23), new DayOfYear(2003, Calendar.AUGUST, 25)));
        assertFalse(dayOfYear.isOnOrBetween(new DayOfYear(2003, Calendar.SEPTEMBER, 23), new DayOfYear(2002, Calendar.SEPTEMBER, 25)));

    }

    /**
     * Tests {@link DayOfYear#previousDay()}.
     */
    public void testPreviousDay() {
        DayOfYear dayOfYear;

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        assertEquals(new DayOfYear(2003, Calendar.SEPTEMBER, 23), dayOfYear.previousDay());

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 1);
        assertEquals(new DayOfYear(2003, Calendar.AUGUST, 31), dayOfYear.previousDay());

        dayOfYear = new DayOfYear(2003, Calendar.JANUARY, 1);
        assertEquals(new DayOfYear(2002, Calendar.DECEMBER, 31), dayOfYear.previousDay());

        // Leap Year
        dayOfYear = new DayOfYear(2004, Calendar.MARCH, 1);
        assertEquals(new DayOfYear(2004, Calendar.FEBRUARY, 29), dayOfYear.previousDay());
        dayOfYear = new DayOfYear(2004, Calendar.FEBRUARY, 29);
        assertEquals(new DayOfYear(2004, Calendar.FEBRUARY, 28), dayOfYear.previousDay());
    }

    /**
     * Tests {@link DayOfYear#nextDay()}.
     */
    public void testNextDay() {
        DayOfYear dayOfYear;

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 24);
        assertEquals(new DayOfYear(2003, Calendar.SEPTEMBER, 25), dayOfYear.nextDay());

        dayOfYear = new DayOfYear(2003, Calendar.SEPTEMBER, 30);
        assertEquals(new DayOfYear(2003, Calendar.OCTOBER, 1), dayOfYear.nextDay());

        dayOfYear = new DayOfYear(2003, Calendar.DECEMBER, 31);
        assertEquals(new DayOfYear(2004, Calendar.JANUARY, 1), dayOfYear.nextDay());

        // Leap Year
        dayOfYear = new DayOfYear(2004, Calendar.FEBRUARY, 28);
        assertEquals(new DayOfYear(2004, Calendar.FEBRUARY, 29), dayOfYear.nextDay());
        dayOfYear = new DayOfYear(2004, Calendar.FEBRUARY, 29);
        assertEquals(new DayOfYear(2004, Calendar.MARCH, 1), dayOfYear.nextDay());
    }

}
