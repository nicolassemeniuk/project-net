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
package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link WorkCalculatorHelper}.
 */ 
public class WorkCalculatorHelperTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public WorkCalculatorHelperTest(String s) {
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
        TestSuite suite = new TestSuite(WorkCalculatorHelperTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link WorkCalculatorHelper#getWork(java.util.Date, java.util.Date, java.math.BigDecimal)}.
     */
    public void testGetWork() {

        Date startDate;
        Date endDate;
        TimeQuantity expected;

        TimeZone timeZone;
        BigDecimal assignmentPercentage;

        timeZone = TimeZone.getTimeZone("America/Los_Angeles");

        // Default Calendar
        WorkCalculatorHelper workCalcHelper = new WorkCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(timeZone, WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[] {Calendar.SATURDAY, Calendar.SUNDAY})));

        //
        // 100% assigned
        //
        assignmentPercentage = new BigDecimal("1.00");

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 8 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(8, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Friday April 2nd 2004 @ 5:00 PM
        // End: Monday April 5th 2004 @ 8:00 AM
        // Expected: 0 hours
        startDate = makeDateTime("4/2/04 5:00 PM");
        endDate = makeDateTime("4/5/04 8:00 AM");
        expected = new TimeQuantity(0, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Wednesday March 31st 2004 @ 12:30 PM
        // End: Wednesday April 7th 5th 2004 @ 12:30 PM
        // Expected: 40 hours
        startDate = makeDateTime("3/31/04 12:30 PM");
        endDate = makeDateTime("4/7/04 12:30 PM");
        expected = new TimeQuantity(40, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        //
        // 50% assigned
        //
        assignmentPercentage = new BigDecimal("0.50");

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 4 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(4, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Friday April 2nd 2004 @ 5:00 PM
        // End: Monday April 5th 2004 @ 8:00 AM
        // Expected: 0 hours
        startDate = makeDateTime("4/2/04 5:00 PM");
        endDate = makeDateTime("4/5/04 8:00 AM");
        expected = new TimeQuantity(0, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Wednesday March 31st 2004 @ 12:30 PM
        // End: Wednesday April 7th 5th 2004 @ 12:30 PM
        // Expected: 20 hours
        startDate = makeDateTime("3/31/04 12:30 PM");
        endDate = makeDateTime("4/7/04 12:30 PM");
        expected = new TimeQuantity(20, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        //
        // 10% assigned
        //
        assignmentPercentage = new BigDecimal("0.10");

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 0.8 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(new BigDecimal("0.8"), TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Friday April 2nd 2004 @ 5:00 PM
        // End: Monday April 5th 2004 @ 8:00 AM
        // Expected: 0 hours
        startDate = makeDateTime("4/2/04 5:00 PM");
        endDate = makeDateTime("4/5/04 8:00 AM");
        expected = new TimeQuantity(0, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Wednesday March 31st 2004 @ 12:30 PM
        // End: Wednesday April 7th 5th 2004 @ 12:30 PM
        // Expected: 4 hours
        startDate = makeDateTime("3/31/04 12:30 PM");
        endDate = makeDateTime("4/7/04 12:30 PM");
        expected = new TimeQuantity(4, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        //
        // 200% assigned
        //
        assignmentPercentage = new BigDecimal("2.00");

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Expected: 16 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(16, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Friday April 2nd 2004 @ 5:00 PM
        // End: Monday April 5th 2004 @ 8:00 AM
        // Expected: 0 hours
        startDate = makeDateTime("4/2/04 5:00 PM");
        endDate = makeDateTime("4/5/04 8:00 AM");
        expected = new TimeQuantity(0, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

        // Start: Wednesday March 31st 2004 @ 12:30 PM
        // End: Wednesday April 7th 5th 2004 @ 12:30 PM
        // Expected: 80 hours
        startDate = makeDateTime("3/31/04 12:30 PM");
        endDate = makeDateTime("4/7/04 12:30 PM");
        expected = new TimeQuantity(80, TimeQuantityUnit.HOUR).convertTo(TimeQuantityUnit.MINUTE, 0, BigDecimal.ROUND_UNNECESSARY);
        assertEquals(expected, workCalcHelper.getWork(startDate, endDate, assignmentPercentage));

    }


    //
    // Helper Methods
    //

    private static Date makeDateTime(String dateString) {
        return WorkingTimeCalendarDefinitionTest.makeDateTime(dateString);
    }

}
