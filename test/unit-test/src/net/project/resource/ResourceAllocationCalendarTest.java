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
package net.project.resource;

import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;

public class ResourceAllocationCalendarTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public ResourceAllocationCalendarTest(String s) {
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
     * tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ResourceAllocationCalendarTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    public void testFindRowAndCol() {
        ResourceAllocationCalendar rac = new ResourceAllocationCalendar();
        rac.setCalendar(new PnCalendar(TimeZone.getTimeZone("PST"), Locale.US));
        PnCalendar cal = new PnCalendar(TimeZone.getTimeZone("PST"), Locale.US);

        //June 2003 starts on a Sunday
        cal.set(2003, 5, 1, 0, 0, 0);
        RowCol rowCol = new RowCol(1, 0);
        assertEquals(rowCol, rac.findRowAndCol(cal.getTime()));

        cal.set(2003, 5, 2, 0, 0, 0);
        rowCol = new RowCol(1, 1);
        assertEquals(rowCol, rac.findRowAndCol(cal.getTime()));

        cal.set(2003, 5, 8, 0, 0, 0);
        rowCol = new RowCol(2, 0);
        assertEquals(rowCol, rac.findRowAndCol(cal.getTime()));

        cal.set(2003, 5, 9, 0, 0, 0);
        rowCol = new RowCol(2, 1);
        assertEquals(rowCol, rac.findRowAndCol(cal.getTime()));
    }

}
