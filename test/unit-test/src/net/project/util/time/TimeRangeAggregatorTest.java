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
package net.project.util.time;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.PnCalendar;

public class TimeRangeAggregatorTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public TimeRangeAggregatorTest(String s) {
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
        TestSuite suite = new TestSuite(TimeRangeAggregatorTest.class);
        return suite;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        Application.login();
    }

    /**************************************************************************/
    class TimeRangeValue implements ITimeRangeValue {
        private Date startDate, endDate;
        private BigDecimal value;

        public TimeRangeValue(Date startDate, Date endDate, BigDecimal value) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.value = value;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public BigDecimal getValue() {
            return value;
        }
    }

    public void testAggregation1() {
        PnCalendar startCal = new PnCalendar();
        PnCalendar endCal = new PnCalendar();
        startCal.set(PnCalendar.MILLISECOND, 0);
        endCal.set(PnCalendar.MILLISECOND, 0);
        TimeRangeAggregator trAggregator = new TimeRangeAggregator();

        //----------------------------------------------------------------------
        //Start with one simple day.  Can the aggregator find the amount of time
        //in a given hour.
        //----------------------------------------------------------------------
        startCal.set(2004, 0, 1, 8, 0, 0);
        endCal.set(2004, 0, 1, 17, 0, 0);
        trAggregator.insert(new TimeRangeValue(startCal.getTime(), endCal.getTime(), new BigDecimal(100)));

        // [VS] [TS] [TE] [VE]
        startCal.set(2004, 0, 1, 10, 0, 0);
        endCal.set(2004, 0, 1, 11, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS] [VS] [TE] [VE]
        startCal.set(2004, 0, 1, 5, 0, 0);
        endCal.set(2004, 0, 1, 9, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS] [TS] [VE] [TE]
        startCal.set(2004, 0, 1, 15, 0, 0);
        endCal.set(2004, 0, 1, 18, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS] [VS] [VE] [TE]
        startCal.set(2004, 0, 1, 4, 0, 0);
        endCal.set(2004, 0, 1, 20, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS] [TE] [VS] [VE]
        startCal.set(2004, 0, 1, 4, 0, 0);
        endCal.set(2004, 0, 1, 8, 0, 0);
        assertEquals(new BigDecimal(0), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS] [VE] [TS] [TE]
        startCal.set(2004, 0, 1, 17, 0, 0);
        endCal.set(2004, 0, 1, 20, 0, 0);
        assertEquals(new BigDecimal(0), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VTS] [TE] [VE]
        startCal.set(2004, 0, 1, 8, 0, 0);
        endCal.set(2004, 0, 1, 9, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS] [TS] [VTE]
        startCal.set(2004, 0, 1, 16, 0, 0);
        endCal.set(2004, 0, 1, 17, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));

        //----------------------------------------------------------------------
        //Now put a second amount of time in that day
        //----------------------------------------------------------------------
        startCal.set(2004, 0, 1, 9, 0, 0);
        endCal.set(2004, 0, 1, 11, 0, 0);
        trAggregator.insert(new TimeRangeValue(startCal.getTime(), endCal.getTime(), new BigDecimal(50)));

        // [VS1] [VS2] [TS1] [TE1] [VE2] [VE1]
        startCal.set(2004, 0, 1, 10, 0, 0);
        endCal.set(2004, 0, 1, 10, 30, 0);
        assertEquals(new BigDecimal(150), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS1] [VS2] [TS1] [VE2] [TE1] [VE1]
        startCal.set(2004, 0, 1, 10, 30, 0);
        endCal.set(2004, 0, 1, 14, 0, 0);
        assertEquals(new BigDecimal(150), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS1] [VS2] [VE2] [TS1] [VE1] [TE1]
        startCal.set(2004, 0, 1, 16, 0, 0);
        endCal.set(2004, 0, 1, 18, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS1] [VS1] [VS2] [VE2] [VE1] [TE1]
        startCal.set(2004, 0, 1, 4, 0, 0);
        endCal.set(2004, 0, 1, 20, 0, 0);
        assertEquals(new BigDecimal(150), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS1] [TE1] [VS1] [VS2] [VE2] [VE1]
        startCal.set(2004, 0, 1, 4, 0, 0);
        endCal.set(2004, 0, 1, 8, 0, 0);
        assertEquals(new BigDecimal(0), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS1] [VS2] [VE2] [VE1] [TS1] [TE1]
        startCal.set(2004, 0, 1, 17, 0, 0);
        endCal.set(2004, 0, 1, 20, 0, 0);
        assertEquals(new BigDecimal(0), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));

        //----------------------------------------------------------------------
        // Now put a third time range inside of the larger range but not
        // adjacent to the second range.
        //----------------------------------------------------------------------
        startCal.set(2004, 0, 1, 13, 0, 0);
        endCal.set(2004, 0, 1, 15, 0, 0);
        trAggregator.insert(new TimeRangeValue(startCal.getTime(), endCal.getTime(), new BigDecimal(25)));

        // [VS1] [VS2] [VE2] [TS1] [TE1] [VS3] [VE3] [VE1]
        startCal.set(2004, 0, 1, 11, 30, 0);
        endCal.set(2004, 0, 1, 12, 0, 0);
        assertEquals(new BigDecimal(100), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS1] [VS2] [TS1] [TE1] [VE2] [VS3] [VE3] [VE1]
        startCal.set(2004, 0, 1, 10, 0, 0);
        endCal.set(2004, 0, 1, 10, 30, 0);
        assertEquals(new BigDecimal(150), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [VS1] [VS2] [VE2] [VS3] [TS1] [TE1] [VE3] [VE1]
        startCal.set(2004, 0, 1, 13, 0, 0);
        endCal.set(2004, 0, 1, 13, 30, 0);
        assertEquals(new BigDecimal(125), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));
        // [TS1] [VS1] [VS2] [VE2] [VS3] [VE3] [VE1] [TE1]  --> 150 because we are testing CONCURRENCY, not aggregate.
        startCal.set(2004, 0, 1, 4, 0, 0);
        endCal.set(2004, 0, 1, 20, 0, 0);
        assertEquals(new BigDecimal(150), trAggregator.findMaximumConcurrent(startCal.getTime(), endCal.getTime()));

    }

}
