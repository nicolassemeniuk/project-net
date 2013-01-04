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
 package net.project.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;

/**
 * Unit testing class to test the {@link net.project.util.DateUtils} class.
 * There are still several tests to be written, this is still quite incomplete.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class DateUtilsTest extends TestCase {
    /**
     * Standard constructor.
     *
     * @param testName the name of the test.
     */
    public DateUtilsTest(java.lang.String testName) {
        super(testName);
    }

    /**
     * Method to allow this test to be run from the command line.
     *
     * @param args a <code>String[]</code> object containing command line
     * arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        Application.login();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DateUtilsTest.class);

        return suite;
    }

    public void testAddYear() {
        Date date1, date2;
        PnCalendar cal = new PnCalendar();

        cal.set(2002,1,15);
        date1 = cal.getTime();
        cal.set(2003,1,15);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addYear(date1,1));

        cal.set(2002,1,15);
        date1 = cal.getTime();
        cal.set(2001,1,15);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addYear(date1,-1));
    }

    public void testAddMonth() {
        Date date1, date2;
        PnCalendar cal = new PnCalendar();

        cal.set(2002,1,15);
        date1 = cal.getTime();
        cal.set(2002,2,15);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addMonth(date1,1));

        cal.set(2002,1,15);
        date1 = cal.getTime();
        cal.set(2001,12,15);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addMonth(date1,-1));
    }

    public void testAddDay() {
        Date date1, date2;
        PnCalendar cal = new PnCalendar();

        cal.set(2002,11,31);
        date1 = cal.getTime();
        cal.set(2002,11,30);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addDay(date1, -1));

        cal.set(2002,0,14);
        date1 = cal.getTime();
        cal.set(2002,0,15);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addDay(date1, 1));

        cal.set(2002,11,31);
        date1 = cal.getTime();
        cal.set(2003,0,1);
        date2 = cal.getTime();
        assertEquals(date2, DateUtils.addDay(date1, 1));
    }

    /**
     * Test the DateUtils.daysBetween method.
     */
    public void testDaysBetween() {
        GregorianCalendar cal = new PnCalendar();
        Date now = new Date();
        cal.setTime(now);
        cal.add(GregorianCalendar.DATE, 1);
        Date tomorrow = cal.getTime();


        //Check to make sure that days treats its two parameters the same.
        //Changing the order shouldn't change the value
        assertEquals(1, DateUtils.daysBetween(now, tomorrow));
        assertEquals(1, DateUtils.daysBetween(tomorrow, now));

        //
        //Check for days between around daylight savings time changes
        //
        Date date1, date2;

        //Time goes forward 1 hour on April 4 2004 at 2 am.
        cal.set(2004, 3, 3, 2, 0, 0);   // 4/3/2004 2am
        date1 = cal.getTime();
        cal.set(2004, 3, 4, 3, 0, 0);   // 4/4/2004 3am  (There isn't a 2 am)
        date2 = cal.getTime();
        assertEquals(2, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 3, 3, 3, 0, 0);   // 4/3/2004 3am
        date1 = cal.getTime();
        cal.set(2004, 3, 4, 3, 0, 0);   // 4/4/2004 3am
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 3, 4, 0, 0, 0);   // 4/4/2004 midnight
        date1 = cal.getTime();
        cal.set(2004, 3, 5, 0, 0, 0);   // 4/5/2004 midnight
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 3, 4, 1, 0, 0);   // 4/4/2004 1am
        date1 = cal.getTime();
        cal.set(2004, 3, 5, 1, 0, 0);   // 4/5/2004 1am
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 3, 4, 3, 0, 0);   // 4/4/2004 3am (There isn't a 2 am)
        date1 = cal.getTime();
        cal.set(2004, 3, 5, 2, 0, 0);   // 4/5/2004 2am
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        //Time goes back 1 hour on October 31 2004 at 2am
        cal.set(2004, 9, 31, 1, 0, 0);  // 10/31/2004 1am
        date1 = cal.getTime();
        cal.set(2004, 10, 1, 1, 0, 0);  // 11/1/2004 1am
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 9, 31, 2, 0, 0);  // 10/31/2004 2am
        date1 = cal.getTime();
        cal.set(2004, 10, 1, 2, 0, 0);  // 11/1/2004 2am
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        //
        //Check for daysBetween not in the same year
        //
        cal.set(2003, 11, 31, 0, 0, 0);  // 12/31/2004 midnight
        date1 = cal.getTime();
        cal.set(2004, 0, 1, 0, 0, 0);    // 1/1/2004 midnight (Happy New Year!)
        date2 = cal.getTime();
        assertEquals(1, DateUtils.daysBetween(date1, date2));

        cal.set(2004, 0, 1, 0, 0, 0);    // 1/1/2004 midnight
        date1 = cal.getTime();
        cal.set(2004, 11, 31, 0, 0, 0);  // 12/31/2004 midnight
        date2 = cal.getTime();
        assertEquals(365, DateUtils.daysBetween(date1, date2));  //It's a leap year, that why there are 365, not 364.

        cal.set(2004, 0, 1, 0, 0, 0);    // 1/1/2004 midnight
        date1 = cal.getTime();
        cal.set(2005, 0, 1, 0, 0, 0);    // 1/1/2005 midnight
        date2 = cal.getTime();
        assertEquals(366, DateUtils.daysBetween(date1, date2));

        cal.set(2003, 0, 1, 0, 0, 0);
        date1 = cal.getTime();
        cal.set(2005, 0, 1, 0, 0, 0);
        date2 = cal.getTime();
        assertEquals(731, DateUtils.daysBetween(date1, date2)); //365+366=731

        cal.set(2003, 0, 1, 0, 0, 0);
        date1 = cal.getTime();
        cal.set(2005, 0, 2, 0, 0, 0);
        date2 = cal.getTime();
        assertEquals(732, DateUtils.daysBetween(date1, date2)); //365+366=731

    }

    public void testMonthsBetween() {
        GregorianCalendar cal = new PnCalendar();
        Date d1, d2;

        //0 days between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 0, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(0, DateUtils.monthsBetween(d1, d2));

        //1 day between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 0, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(1, DateUtils.monthsBetween(d1, d2));

        //1 month between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 1, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(1, DateUtils.monthsBetween(d1, d2));

        //1 month + 1 day between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 1, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(2, DateUtils.monthsBetween(d1, d2));

        //2 months
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 2, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(2, DateUtils.monthsBetween(d1, d2));

        //2 months + 1 day
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 2, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(3, DateUtils.monthsBetween(d1, d2));

        //1 year
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2005, 0, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(12, DateUtils.monthsBetween(d1, d2));

        //1 year + 1 day
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2005, 0, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(13, DateUtils.monthsBetween(d1, d2));

        //1 year + 1 month + 1 day
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2005, 1, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(14, DateUtils.monthsBetween(d1, d2));

        //2 years
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2006, 0, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(24, DateUtils.monthsBetween(d1, d2));

        //2 years + 1 month
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2006, 1, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(25, DateUtils.monthsBetween(d1, d2));
    }

    public void testQuartersBetween() {
        GregorianCalendar cal = new PnCalendar();
        Date q1, q2;

        //1 day apart in same quarter
        cal.set(2003, 0, 1, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2003, 0, 2, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(1, DateUtils.quartersBetween(q1, q2));

        //1 day apart in different quarters
        cal.set(2003, 2, 28, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2003, 3, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(2, DateUtils.quartersBetween(q1, q2));

        //1 day apart in different years
        cal.set(2003, 11, 31, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2004, 0, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(2, DateUtils.quartersBetween(q1, q2));

        //1 year + 1 month (in the same quarter)
        cal.set(2003, 0, 1, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2004, 1, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(5, DateUtils.quartersBetween(q1, q2));

        //1 year + 1 month (in different quarters)
        cal.set(2003, 0, 1, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2004, 3, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(6, DateUtils.quartersBetween(q1, q2));

        //13 months apart
        cal.set(2003, 10, 1, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2004, 11, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(5, DateUtils.quartersBetween(q1, q2));


        //30 months apart
        cal.set(2000, 1, 1, 0, 0, 0);
        q1 = cal.getTime();
        cal.set(2002, 6, 1, 0, 0, 0);
        q2 = cal.getTime();
        assertEquals(11, DateUtils.quartersBetween(q1, q2));
    }

    public void testYearsBetween() {
        GregorianCalendar cal = new PnCalendar();
        Date d1, d2;

        //Two dates being the same = Zero years between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 0, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(0, DateUtils.yearsBetween(d1, d2));

        //One month between = One year between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 1, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(1, DateUtils.yearsBetween(d1, d2));

        //Eleven months between = One year between
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2004, 10, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(1, DateUtils.yearsBetween(d1, d2));

        //Twelve months = 1 year
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2005, 0, 1, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(1, DateUtils.yearsBetween(d1, d2));

        //Twelve months plus 1 day = 2 years
        cal.set(2004, 0, 1, 0, 0, 0);
        d1 = cal.getTime();
        cal.set(2005, 0, 2, 0, 0, 0);
        d2 = cal.getTime();
        assertEquals(2, DateUtils.yearsBetween(d1, d2));
    }

    /**
     * Test the DateUtils.getDatabaseDateString() method.
     */
    public void testGetDatabaseDateString() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2002, 11, 25, 18, 0);
        System.out.println(cal.getTime().toString());

        assertEquals("TO_DATE('12/25/2002 18:00', 'MM/DD/YYYY HH24:MI')",
            DateUtils.getDatabaseDateString(cal.getTime()));
    }

    public void testAddTimeQuantityToDate() {
        GregorianCalendar cal = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        TimeQuantity tq;

        cal.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        cal.set(2003, 1, 6, 0, 0, 0);
        cal2.set(2003, 1, 6, 6, 0, 0);
        tq = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        assertEquals(cal2.getTime(), DateUtils.addTimeQuantityToDate(cal.getTime(), tq));

        cal.set(2003, 1, 6, 0, 0, 0);
        cal2.set(2003, 1, 6, 0, 30, 0);
        tq = new TimeQuantity(30, TimeQuantityUnit.MINUTE);
        assertEquals(cal2.getTime(), DateUtils.addTimeQuantityToDate(cal.getTime(), tq));

        cal.set(2003, 1, 6, 0, 0, 0);
        cal2.set(2003, 1, 6, 0, 0, 30);
        tq = new TimeQuantity(30, TimeQuantityUnit.SECOND);
        assertEquals(cal2.getTime(), DateUtils.addTimeQuantityToDate(cal.getTime(), tq));

        cal.set(2003, 1, 6, 0, 0, 0);
        cal2.set(2003, 1, 6, 1, 30, 0);
        tq = new TimeQuantity(1.5, TimeQuantityUnit.HOUR);
        assertEquals(cal2.getTime(), DateUtils.addTimeQuantityToDate(cal.getTime(), tq));

        cal.set(2003, 1, 6, 0, 0, 0);
        cal2.set(2003, 1, 6, 1, 7, 30);
        tq = new TimeQuantity(1.125, TimeQuantityUnit.HOUR);
        assertEquals(cal2.getTime(), DateUtils.addTimeQuantityToDate(cal.getTime(), tq));
    }

    public void testIsSameDay() {

        Calendar cal1;
        Calendar cal2;

        //
        // Null Values
        //

        try {
            DateUtils.isSameDay(null, null);
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            DateUtils.isSameDay(null, new GregorianCalendar());
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            DateUtils.isSameDay(new GregorianCalendar(), null);
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        //
        // Different years
        //

        cal1 = new GregorianCalendar();
        cal1.set(2003, Calendar.AUGUST, 1);
        cal2 = new GregorianCalendar();
        cal2.set(2002, Calendar.AUGUST, 1);
        assertFalse(DateUtils.isSameDay(cal1, cal2));

        //
        // Same year only
        //

        cal1 = new GregorianCalendar();
        cal1.set(2003, Calendar.AUGUST, 1);
        cal2 = new GregorianCalendar();
        cal2.set(2003, Calendar.SEPTEMBER, 1);
        assertFalse(DateUtils.isSameDay(cal1, cal2));

        //
        // Same year and month only
        //

        cal1 = new GregorianCalendar();
        cal1.set(2003, Calendar.AUGUST, 1);
        cal2 = new GregorianCalendar();
        cal2.set(2003, Calendar.AUGUST, 2);
        assertFalse(DateUtils.isSameDay(cal1, cal2));


        //
        // Identical calendars
        //

        cal1 = new GregorianCalendar();
        cal2 = cal1;
        assertTrue(DateUtils.isSameDay(cal1, cal2));

        //
        // Same Dates
        //

        cal1 = new GregorianCalendar();
        cal1.setTime(new Date());
        cal2 = new GregorianCalendar();
        cal2.setTime(cal1.getTime());
        assertTrue(DateUtils.isSameDay(cal1, cal2));


        //
        // Same year month and day; different times (by 1 second)
        //

        cal1 = new GregorianCalendar();
        cal1.set(Calendar.MILLISECOND, 0);
        cal1.set(2003, Calendar.AUGUST, 1, 12, 30, 0);
        cal2 = new GregorianCalendar();
        cal2.set(Calendar.MILLISECOND, 0);
        cal2.set(2003, Calendar.AUGUST, 1, 12, 30, 1);
        assertTrue(DateUtils.isSameDay(cal1, cal2));

    }

    public void testIsEarlierDay() {

        Calendar cal1;
        Calendar cal2;

        cal1 = new GregorianCalendar();
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        cal2 = new GregorianCalendar();
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        //
        // Null Values
        //

        try {
            DateUtils.isEarlierDay(null, null);
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            DateUtils.isEarlierDay(null, new GregorianCalendar());
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            DateUtils.isEarlierDay(new GregorianCalendar(), null);
            fail("Expected NullPointerException; none thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        // Later year, everything else earlier
        cal1.set(2004, Calendar.JANUARY, 1);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertFalse(DateUtils.isEarlierDay(cal1, cal2));

        // Earlier year, everything else later
        cal1.set(2002, Calendar.MARCH, 1);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertTrue(DateUtils.isEarlierDay(cal1, cal2));

        // Same year; later month
        cal1.set(2003, Calendar.MARCH, 1);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertFalse(DateUtils.isEarlierDay(cal1, cal2));

        // Same year; earlier month
        cal1.set(2003, Calendar.JANUARY, 1);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertTrue(DateUtils.isEarlierDay(cal1, cal2));

        // Same year; same month; later day
        cal1.set(2003, Calendar.FEBRUARY, 2);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertFalse(DateUtils.isEarlierDay(cal1, cal2));

        // Same year; same month; equal day
        cal1.set(2003, Calendar.FEBRUARY, 1);
        cal2.set(2003, Calendar.FEBRUARY, 1);
        assertFalse(DateUtils.isEarlierDay(cal1, cal2));

        // Same year; same month; earlier day
        cal1.set(2003, Calendar.FEBRUARY, 1);
        cal2.set(2003, Calendar.FEBRUARY, 2);
        assertTrue(DateUtils.isEarlierDay(cal1, cal2));

        // Ensure it works with 1 millisecond earlier, but different day
        cal2.set(2003, Calendar.FEBRUARY, 1, 0, 0, 0);
        cal1 = (Calendar) cal2.clone();
        cal1.add(Calendar.MILLISECOND, -1);
        assertTrue(DateUtils.isEarlierDay(cal1, cal2));

    }

    public void testIsOnOrBetween() {

        Calendar cal;
        Date date;
        Date startDate;
        Date endDate;

        // Timezone irrelevant; all dates use same time zone
        cal = new GregorianCalendar();
        cal.set(Calendar.MILLISECOND, 0);

        // Before start date (by 1 second)
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 0);
        date = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        startDate = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        endDate = cal.getTime();
        assertFalse(DateUtils.isOnOrBetween(date, startDate, endDate));

        // On start date
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        date = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        startDate = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        endDate = cal.getTime();
        assertTrue(DateUtils.isOnOrBetween(date, startDate, endDate));

        // On end date
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        date = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        startDate = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        endDate = cal.getTime();
        assertTrue(DateUtils.isOnOrBetween(date, startDate, endDate));

        // Properly between start and end date
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 2);
        date = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        startDate = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        endDate = cal.getTime();
        assertTrue(DateUtils.isOnOrBetween(date, startDate, endDate));

        // After end date
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 4);
        date = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 1);
        startDate = cal.getTime();
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 3);
        endDate = cal.getTime();
        assertFalse(DateUtils.isOnOrBetween(date, startDate, endDate));

        // Use some real dates
        cal.set(2003, Calendar.AUGUST, 27, 12, 30, 0);
        date = cal.getTime();
        cal.set(2003, Calendar.JULY, 31, 12, 30, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.SEPTEMBER, 1, 12, 30, 0);
        endDate = cal.getTime();
        assertTrue(DateUtils.isOnOrBetween(date, startDate, endDate));

    }

    public void testIsEqual() {
        Date date1 = null;
        Date date2 = null;
        assertTrue(DateUtils.isEqual(date1, date2));

        date1 = null;
        date2 = new Date();
        assertFalse(DateUtils.isEqual(date1, date2));

        date1 = new Date();
        date2 = null;
        assertFalse(DateUtils.isEqual(date1, date2));

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        date1 = DateUtils.makeDate(timeZone, "1/3/05 8:00 AM");
        date2 = DateUtils.makeDate(timeZone, "1/4/05 8:01 AM");
        assertFalse(DateUtils.isEqual(date1, date2));

        date1 = DateUtils.makeDate(timeZone, "1/3/05 8:00 AM");
        date2 = DateUtils.makeDate(timeZone, "1/3/05 8:00 AM");
        assertTrue(DateUtils.isEqual(date1, date2));

        date1 = DateUtils.makeDate(timeZone, "1/3/05 8:00 AM");
        date2 = date1;
        assertTrue(DateUtils.isEqual(date1, date2));
    }
}
