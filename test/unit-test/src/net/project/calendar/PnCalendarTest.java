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
|   $Revision: 17093 $
|       $Date: 2008-03-24 13:23:06 +0530 (Mon, 24 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.security.SessionManager;
import net.project.security.User;

public class PnCalendarTest extends TestCase {
    public PnCalendarTest(String s) {
        super(s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PnCalendarTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        //Do a login to set up the application context.
        Application.login();
    }

    public void testGetTime() {
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new GregorianCalendar(2002, 11, 4, 0, 0, 0).getTime());

        // Test that the get method is consistent
        assertEquals(new GregorianCalendar(2002, 11, 4, 0, 0, 0).getTime(), cal.getTime());
    }

    public void testStartOfMonth() {
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        GregorianCalendar gcal = new GregorianCalendar(2002, 11, 4, 0, 0, 0);
        //sjmittal: both the calendars have to be in same timezone
        gcal.setTimeZone(cal.getTimeZone());
        Date originalDate = gcal.getTime();
        gcal.set(2002, 11, 1, 0, 0, 0);
        Date expectedDate = gcal.getTime();

        //
        //Test startOfMonth(Date)
        //
        assertEquals(expectedDate, cal.startOfMonth(originalDate));
        //Make sure it works even if a different date has already been set
        cal.setTime(new GregorianCalendar(2002, 1, 1, 0, 0, 0).getTime());
        assertEquals(expectedDate, cal.startOfMonth(originalDate));

        //Test startOfMonth(int,int)
        assertEquals(expectedDate, cal.startOfMonth(11, 2002));

        //Test startOfMonth()
        // Note:  cal.startOfMonth() is coded to return the start of the
        // current month, NOT the setTime.  In other words, it overrides the
        // set time with the current time
        // This method will be deprecated soon.  Meanwhile, fixing Unit test
        // to confirm behavior continues to work as designed
        GregorianCalendar todayCal = new GregorianCalendar();
        //sjmittal: both the calendars have to be in same timezone
        todayCal.setTimeZone(cal.getTimeZone());
        todayCal.setTime(new Date());
        todayCal.set(Calendar.DAY_OF_MONTH, 1);
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        Date todayDate = todayCal.getTime();

        // Set the time to a dummy value to ensure it doesn't use that value
        cal.setTime(originalDate);
        assertEquals(todayDate, cal.startOfMonth());
    }

    public void testGetQuarter() {
        User user = new User();
        user.setTimeZone(TimeZone.getTimeZone("PST"));
        user.setLocale(Locale.US);
        PnCalendar cal = new PnCalendar(user);

        // 15 Jan 2002 = Q1 2002
        cal.set(2002, 0, 15);

        CalendarQuarter quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(0, 2002), quarter);

        // 31 March 2002 = Q1 2002
        cal.set(2002, 2, 31);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(0, 2002), quarter);

        // 1 April 2002 = Q2 2002
        cal.set(2002, 3, 1);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(1, 2002), quarter);

        // 30 June 2002 = Q2 2002
        cal.set(2002, 5, 30);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(1, 2002), quarter);

        // 1 July 2002 = Q3 2002
        cal.set(2002, 6, 1);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(2, 2002), quarter);

        // 31 September 2002 = Q3 2002
        cal.set(2002, 8, 1);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(2, 2002), quarter);

        // 1 November 2002 = Q4 2002
        cal.set(2002, 10, 1);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(3, 2002), quarter);

        // 31 December 2002 = Q4 2002
        cal.set(2002, 11, 31);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(3, 2002), quarter);

        // 1 January 2003 = Q1 2003
        cal.set(2003, 0, 1);
        quarter = cal.getQuarter(cal.getTime());
        assertEquals(new CalendarQuarter(0, 2003), quarter);
    }

}
