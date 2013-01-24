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
package net.project.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;

public class DateRangeTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public DateRangeTest(String s) {
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
     *         tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(DateRangeTest.class);
        return suite;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        Application.login();
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
            java.text.DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
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


    public void testToTimeQuantity() {
        GregorianCalendar cal = new GregorianCalendar();
        Date startDate, endDate;
        DateRange dr;
        TimeQuantity expected;

        //Test standard whole-number operation
        cal.set(2004, 0, 1, 0, 0, 0);
        startDate = cal.getTime();
        cal.add(GregorianCalendar.HOUR, 1);
        endDate = cal.getTime();
        expected = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        dr = new DateRange(startDate, endDate);
        assertEquals(expected, dr.getTimeQuantity(TimeQuantityUnit.HOUR, 2));

        //Make sure that units other than hour work ok
        expected = new TimeQuantity(3600, TimeQuantityUnit.SECOND);
        assertEquals(expected, dr.getTimeQuantity(TimeQuantityUnit.SECOND, 2));

        expected = new TimeQuantity(0.04167, TimeQuantityUnit.DAY);
        assertEquals(expected, dr.getTimeQuantity(TimeQuantityUnit.DAY, 5));

        //Make sure that ranges spanning days work ok
        cal.setTime(startDate);
        cal.add(GregorianCalendar.DATE, 10);
        endDate = cal.getTime();
        dr = new DateRange(startDate, endDate);
        expected = new TimeQuantity(240, TimeQuantityUnit.HOUR);
        assertEquals(expected, dr.getTimeQuantity(TimeQuantityUnit.HOUR, 2));

        //Fractional hour test
        cal.set(2004, 0, 1, 0, 0, 0);
        startDate = cal.getTime();
        cal.add(GregorianCalendar.MINUTE, 90);
        endDate = cal.getTime();
        expected = new TimeQuantity(1.5, TimeQuantityUnit.HOUR);
        dr = new DateRange(startDate, endDate);
        assertEquals(expected, dr.getTimeQuantity(TimeQuantityUnit.HOUR, 1));
    }

}
