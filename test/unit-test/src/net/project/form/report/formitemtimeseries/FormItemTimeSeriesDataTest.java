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
package net.project.form.report.formitemtimeseries;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FormItemTimeSeriesDataTest extends TestCase {
    private FormItemTimeSeriesData data = new FormItemTimeSeriesData();
    private Date threeDaysAgo;
    private Date twoDaysAgo;
    private Date oneDayAgo;
    private Date today;

    public FormItemTimeSeriesDataTest(String s) {
        super(s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FormItemTimeSeriesDataTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
        Calendar cal = new GregorianCalendar();
        cal.add(GregorianCalendar.DAY_OF_MONTH, -3);
        threeDaysAgo = cal.getTime();
        data.addInitialValue(threeDaysAgo, "Apple", new Integer(5));
        data.addInitialValue(threeDaysAgo, "Orange", new Integer(1));

        cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
        twoDaysAgo = cal.getTime();
        data.addDelta(twoDaysAgo, "Apple", new Integer(-1));
        //Test putting two deltas at the same time.  They should be added together.
        data.addDelta(twoDaysAgo, "Apple", new Integer(5));
        data.addDelta(twoDaysAgo, "Orange", new Integer(10));
        data.addDelta(twoDaysAgo, "Grape", new Integer(5));

        cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
        oneDayAgo = cal.getTime();
        data.addDelta(oneDayAgo, "Apple", new Integer(4));
        data.addDelta(oneDayAgo, "Orange", new Integer(-20));

        cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
        today = cal.getTime();
        data.addDelta(today, "Apple", new Integer(15));
        data.addDelta(today, "Orange", new Integer(0));
    }

    public void testGetCountOnDate() {
        assertEquals(new Integer(5), data.getCountOnDate(threeDaysAgo, "Apple"));
        assertEquals(new Integer(9), data.getCountOnDate(twoDaysAgo, "Apple"));
        assertEquals(new Integer(13), data.getCountOnDate(oneDayAgo, "Apple"));
        assertEquals(new Integer(28), data.getCountOnDate(today, "Apple"));

        //Get one out of order now to make sure the cache fallback works
        assertEquals(new Integer(9), data.getCountOnDate(twoDaysAgo, "Apple"));

        assertEquals(new Integer(0), data.getCountOnDate(threeDaysAgo, "Grape"));
        assertEquals(new Integer(5), data.getCountOnDate(twoDaysAgo, "Grape"));
        assertEquals(new Integer(5), data.getCountOnDate(oneDayAgo, "Grape"));
        assertEquals(new Integer(5), data.getCountOnDate(today, "Grape"));

        assertEquals(new Integer(1), data.getCountOnDate(threeDaysAgo, "Orange"));
        assertEquals(new Integer(11), data.getCountOnDate(twoDaysAgo, "Orange"));
        assertEquals(new Integer(-9), data.getCountOnDate(oneDayAgo, "Orange"));
        assertEquals(new Integer(-9), data.getCountOnDate(today, "Orange"));

    }

    public void testGetAllDatesInSeries() {
        Set dates = data.getAllDatesInSeries();
        Iterator it = dates.iterator();

        //Test to make sure that all of the dates are there and that they are in order
        try {
            assertEquals(threeDaysAgo, it.next());
            assertEquals(twoDaysAgo, it.next());
            assertEquals(oneDayAgo, it.next());
            assertEquals(today, it.next());
        } catch (NoSuchElementException e) {
            fail("Not all dates are in series");
        }

        assertFalse("Too many dates found in the series", it.hasNext());
    }

    public void testGetAllFieldValues() {
        Set fieldValues = data.getAllFieldValues();
        Iterator it = fieldValues.iterator();

        try {
            assertEquals("Apple", it.next());
            assertEquals("Grape", it.next());
            assertEquals("Orange", it.next());
        } catch (NoSuchElementException e) {
            fail("Not all elements are in series");
        }
    }
}
