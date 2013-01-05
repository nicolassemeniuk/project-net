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
package net.project.calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CalendarQuarterTest extends TestCase {
    public CalendarQuarterTest(String s) {
        super(s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CalendarQuarterTest.class);
        return suite;
    }

    public void testAdd() {
        CalendarQuarter q = new CalendarQuarter(0, 2000);
        //Check to see if adding a month works
        assertEquals(new CalendarQuarter(0, 1998), q.add(-8));
        assertEquals(new CalendarQuarter(1, 1998), q.add(-7));
        assertEquals(new CalendarQuarter(2, 1998), q.add(-6));
        assertEquals(new CalendarQuarter(3, 1998), q.add(-5));
        assertEquals(new CalendarQuarter(0, 1999), q.add(-4));
        assertEquals(new CalendarQuarter(1, 1999), q.add(-3));
        assertEquals(new CalendarQuarter(2, 1999), q.add(-2));
        assertEquals(new CalendarQuarter(3, 1999), q.add(-1));
        assertEquals(new CalendarQuarter(0, 2000), q.add(0));
        assertEquals(new CalendarQuarter(1, 2000), q.add(1));
        assertEquals(new CalendarQuarter(2, 2000), q.add(2));
        assertEquals(new CalendarQuarter(3, 2000), q.add(3));
        assertEquals(new CalendarQuarter(0, 2001), q.add(4));
        assertEquals(new CalendarQuarter(1, 2001), q.add(5));
        assertEquals(new CalendarQuarter(2, 2001), q.add(6));
        assertEquals(new CalendarQuarter(3, 2001), q.add(7));
    }

    public void testSubtract1() {
        CalendarQuarter q = new CalendarQuarter(3, 2000);

        assertEquals(-8, q.subtract(new CalendarQuarter(3, 2002)));
        assertEquals(-7, q.subtract(new CalendarQuarter(2, 2002)));
        assertEquals(-6, q.subtract(new CalendarQuarter(1, 2002)));
        assertEquals(-5, q.subtract(new CalendarQuarter(0, 2002)));
        assertEquals(-4, q.subtract(new CalendarQuarter(3, 2001)));
        assertEquals(-3, q.subtract(new CalendarQuarter(2, 2001)));
        assertEquals(-2, q.subtract(new CalendarQuarter(1, 2001)));
        assertEquals(-1, q.subtract(new CalendarQuarter(0, 2001)));
        assertEquals(0, q.subtract(new CalendarQuarter(3, 2000)));
        assertEquals(1, q.subtract(new CalendarQuarter(2, 2000)));
        assertEquals(2, q.subtract(new CalendarQuarter(1, 2000)));
        assertEquals(3, q.subtract(new CalendarQuarter(0, 2000)));
        assertEquals(4, q.subtract(new CalendarQuarter(3, 1999)));
        assertEquals(5, q.subtract(new CalendarQuarter(2, 1999)));
        assertEquals(6, q.subtract(new CalendarQuarter(1, 1999)));
        assertEquals(7, q.subtract(new CalendarQuarter(0, 1999)));
        assertEquals(8, q.subtract(new CalendarQuarter(3, 1998)));
        assertEquals(9, q.subtract(new CalendarQuarter(2, 1998)));
        assertEquals(10, q.subtract(new CalendarQuarter(1, 1998)));
        assertEquals(11, q.subtract(new CalendarQuarter(0, 1998)));
        assertEquals(12, q.subtract(new CalendarQuarter(3, 1997)));

        q = new CalendarQuarter(0, 2000);

        assertEquals(-2, q.subtract(new CalendarQuarter(2, 2000)));
        assertEquals(-1, q.subtract(new CalendarQuarter(1, 2000)));
        assertEquals(0, q.subtract(new CalendarQuarter(0, 2000)));
        assertEquals(1, q.subtract(new CalendarQuarter(3, 1999)));
        assertEquals(2, q.subtract(new CalendarQuarter(2, 1999)));
        assertEquals(3, q.subtract(new CalendarQuarter(1, 1999)));
        assertEquals(4, q.subtract(new CalendarQuarter(0, 1999)));
        assertEquals(5, q.subtract(new CalendarQuarter(3, 1998)));
        assertEquals(6, q.subtract(new CalendarQuarter(2, 1998)));
        assertEquals(7, q.subtract(new CalendarQuarter(1, 1998)));
        assertEquals(8, q.subtract(new CalendarQuarter(0, 1998)));
        assertEquals(9, q.subtract(new CalendarQuarter(3, 1997)));
        assertEquals(10, q.subtract(new CalendarQuarter(2, 1997)));
        assertEquals(11, q.subtract(new CalendarQuarter(1, 1997)));
        assertEquals(12, q.subtract(new CalendarQuarter(0, 1997)));
    }

    public void testSubtract2() {
        CalendarQuarter q = new CalendarQuarter(0, 2001);

        assertEquals(new CalendarQuarter(3, 2000), q.subtract(1));
        assertEquals(new CalendarQuarter(2, 2000), q.subtract(2));
        assertEquals(new CalendarQuarter(1, 2000), q.subtract(3));
        assertEquals(new CalendarQuarter(0, 2000), q.subtract(4));
        assertEquals(new CalendarQuarter(3, 1999), q.subtract(5));
        assertEquals(new CalendarQuarter(2, 1999), q.subtract(6));
        assertEquals(new CalendarQuarter(1, 1999), q.subtract(7));
        assertEquals(new CalendarQuarter(0, 1999), q.subtract(8));
        assertEquals(new CalendarQuarter(3, 1998), q.subtract(9));
        assertEquals(new CalendarQuarter(2, 1998), q.subtract(10));
        assertEquals(new CalendarQuarter(1, 1998), q.subtract(11));
        assertEquals(new CalendarQuarter(0, 1998), q.subtract(12));
    }

    public void testMax() {
        assertEquals(new CalendarQuarter(1, 2000),
            CalendarQuarter.max(new CalendarQuarter(1, 2000), new CalendarQuarter(3,1999)));
        assertEquals(new CalendarQuarter(1, 2000),
            CalendarQuarter.max(new CalendarQuarter(3, 1999), new CalendarQuarter(1,2000)));
        assertEquals(new CalendarQuarter(3, 2000),
            CalendarQuarter.max(new CalendarQuarter(2, 2000), new CalendarQuarter(3,2000)));
        assertEquals(new CalendarQuarter(3, 2000),
            CalendarQuarter.max(new CalendarQuarter(3, 2000), new CalendarQuarter(2,2000)));
    }

    public void testMin() {
        assertEquals(new CalendarQuarter(1, 2000),
            CalendarQuarter.min(new CalendarQuarter(1, 2000), new CalendarQuarter(1, 2001)));
        assertEquals(new CalendarQuarter(1, 2000),
            CalendarQuarter.min(new CalendarQuarter(1, 2001), new CalendarQuarter(1, 2000)));
        assertEquals(new CalendarQuarter(2, 2000),
            CalendarQuarter.min(new CalendarQuarter(2, 2000), new CalendarQuarter(3, 2000)));
        assertEquals(new CalendarQuarter(2, 2000),
            CalendarQuarter.min(new CalendarQuarter(2, 2000), new CalendarQuarter(3, 2000)));
    }

    public void testCompareTo() {
        CalendarQuarter quarter1 = new CalendarQuarter(3, 2000);
        CalendarQuarter quarter2 = new CalendarQuarter(2, 2001);

        assertEquals(-1, quarter1.compareTo(quarter2));
        assertEquals(1, quarter2.compareTo(quarter1));
        assertEquals(0, quarter1.compareTo(quarter1));

        quarter1 = new CalendarQuarter(3,2000);
        quarter2 = new CalendarQuarter(2,2000);

        assertEquals(1, quarter1.compareTo(quarter2));
        assertEquals(-1, quarter2.compareTo(quarter1));
        assertEquals(0, quarter1.compareTo(quarter1));
    }

}
