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
 * Tests {@link PercentageCalculatorHelper}.
 */
public class PercentageCalculatorHelperTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public PercentageCalculatorHelperTest(String s) {
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
        TestSuite suite = new TestSuite(PercentageCalculatorHelperTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link PercentageCalculatorHelper#getPercentage(java.util.Date, java.util.Date, net.project.util.TimeQuantity)}.
     */
    public void testCalculatePercentage() {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date endDate;
        TimeQuantity work;

        PercentageCalculatorHelper percentageCalcHelper = new PercentageCalculatorHelper(timeZone, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

        //
        // 8 hours work
        //
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Expected: 1.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("1.00"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 12:00 PM
        // Expected: 2.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 12, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("2.00"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Tuesday June 3rd @ 5:00 PM
        // Expected: 0.50
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 3, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("0.50"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        //
        // 1 hour work
        //
        work = new TimeQuantity(1, TimeQuantityUnit.HOUR);

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Expected: 0.13
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("0.13"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 12:00 PM
        // Expected: 0.25
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 12, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("0.25"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Tuesday June 3rd @ 5:00 PM
        // Expected: 0.06
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 3, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("0.06"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        //
        // 40 hrs work
        //
        work = new TimeQuantity(40, TimeQuantityUnit.HOUR);

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Expected: 5.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("5.00"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 12:00 PM
        // Expected: 10.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 12, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("10.00"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Tuesday June 3rd @ 5:00 PM
        // Expected: 2.50
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 3, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("2.50"), percentageCalcHelper.getPercentage(startDate, endDate, work));

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Friday June 13th @ 5:00 PM
        // Expected: 0.50
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 13, 17, 0);
        endDate = cal.getTime();
        assertEquals(new BigDecimal("0.50"), percentageCalcHelper.getPercentage(startDate, endDate, work));

    }
}
