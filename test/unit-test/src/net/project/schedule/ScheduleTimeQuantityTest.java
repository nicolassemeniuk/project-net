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
 package net.project.schedule;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests {@link ScheduleTimeQuantity}.
 * 
 * @author Tim Morrow
 */
public class ScheduleTimeQuantityTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleTimeQuantityTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * 
     * @param args a <code>String[]</code> value which contains the command line options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit test believes it is comprised of.  In most
     * cases, it is only the current class, though it can include others.
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleTimeQuantityTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleTimeQuantity#convertToHour(net.project.util.TimeQuantity)}.
     */
    public void testDownConvert() {

        TimeQuantity tq;
        TimeQuantity expected;

        try {
            ScheduleTimeQuantity.convertToHour(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // MONTH to hour
        tq = new TimeQuantity(2, TimeQuantityUnit.MONTH);
        expected = new TimeQuantity(320, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        // WEEK to hour
        tq = new TimeQuantity(2, TimeQuantityUnit.WEEK);
        expected = new TimeQuantity(80, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        // DAY to hour
        tq = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expected = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        // HOUR or less unchanged
        tq = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        tq = new TimeQuantity(60, TimeQuantityUnit.MINUTE);
        expected = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        tq = new TimeQuantity(1800, TimeQuantityUnit.SECOND);
        expected = new TimeQuantity(0.5, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

        tq = new TimeQuantity(360000, TimeQuantityUnit.MILLISECOND);
        expected = new TimeQuantity(0.1, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToHour(tq));

    }

    public void testUpConvert() {

        TimeQuantity tq;
        TimeQuantity expected;

        try {
            ScheduleTimeQuantity.convertToUnit(null, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            ScheduleTimeQuantity.convertToUnit(new TimeQuantity(2, TimeQuantityUnit.DAY), null, 2, BigDecimal.ROUND_HALF_UP);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // MILLISECOND to SECOND
        tq = new TimeQuantity(1000, TimeQuantityUnit.MILLISECOND);
        expected = new TimeQuantity(1, TimeQuantityUnit.SECOND);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.SECOND, 2, BigDecimal.ROUND_HALF_UP));
        // MILLISECOND to MINUTE
        tq = new TimeQuantity(60000, TimeQuantityUnit.MILLISECOND);
        expected = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MINUTE, 2, BigDecimal.ROUND_HALF_UP));
        // MILLISECOND to HOUR
        tq = new TimeQuantity(3600000, TimeQuantityUnit.MILLISECOND);
        expected = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));

        // SECOND to MINUTE
        tq = new TimeQuantity(60, TimeQuantityUnit.SECOND);
        expected = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MINUTE, 2, BigDecimal.ROUND_HALF_UP));
        // SECOND to HOUR
        tq = new TimeQuantity(3600, TimeQuantityUnit.SECOND);
        expected = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));

        // MINIUTE TO HOUR
        tq = new TimeQuantity(120, TimeQuantityUnit.MINUTE);
        expected = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));

        // HOUR to DAY
        tq = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(1.5, TimeQuantityUnit.DAY);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP));
        // HOUR to WEEK
        tq = new TimeQuantity(60, TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(1.5, TimeQuantityUnit.WEEK);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.WEEK, 2, BigDecimal.ROUND_HALF_UP));
        // HOUR to MONTH
        tq = new TimeQuantity(320, TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(2, TimeQuantityUnit.MONTH);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MONTH, 2, BigDecimal.ROUND_HALF_UP));

        // DAY to WEEK
        tq = new TimeQuantity(10, TimeQuantityUnit.DAY);
        expected = new TimeQuantity(2, TimeQuantityUnit.WEEK);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.WEEK, 2, BigDecimal.ROUND_HALF_UP));
        // DAY to MONTH
        tq = new TimeQuantity(30, TimeQuantityUnit.DAY);
        expected = new TimeQuantity(1.5, TimeQuantityUnit.MONTH);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MONTH, 2, BigDecimal.ROUND_HALF_UP));

        // WEEK to MONTH
        tq = new TimeQuantity(2, TimeQuantityUnit.WEEK);
        expected = new TimeQuantity(0.5, TimeQuantityUnit.MONTH);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MONTH, 2, BigDecimal.ROUND_HALF_UP));

        // MONTH to MONTH
        tq = new TimeQuantity(2, TimeQuantityUnit.MONTH);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MONTH, 2, BigDecimal.ROUND_HALF_UP));
        // MONTH to WEEK
        tq = new TimeQuantity(2, TimeQuantityUnit.MONTH);
        expected = new TimeQuantity(8, TimeQuantityUnit.WEEK);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.WEEK, 2, BigDecimal.ROUND_HALF_UP));

        // WEEK to WEEK
        tq = new TimeQuantity(2, TimeQuantityUnit.WEEK);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.WEEK, 2, BigDecimal.ROUND_HALF_UP));
        // WEEK to DAY
        tq = new TimeQuantity(2, TimeQuantityUnit.WEEK);
        expected = new TimeQuantity(10, TimeQuantityUnit.DAY);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP));

        // DAY to DAY
        tq = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.DAY, 2, BigDecimal.ROUND_HALF_UP));
        // DAY to HOUR
        tq = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expected = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));

        // HOUR to HOUR
        tq = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));
        // HOUR to MINUTE
        tq = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(120, TimeQuantityUnit.MINUTE);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MINUTE, 2, BigDecimal.ROUND_HALF_UP));

        // MINUTE to MINUTE
        tq = new TimeQuantity(2, TimeQuantityUnit.MINUTE);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MINUTE, 2, BigDecimal.ROUND_HALF_UP));
        // MINUTE to SECOND
        tq = new TimeQuantity(2, TimeQuantityUnit.MINUTE);
        expected = new TimeQuantity(120, TimeQuantityUnit.SECOND);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.SECOND, 2, BigDecimal.ROUND_HALF_UP));

        // SECOND to SECOND
        tq = new TimeQuantity(2, TimeQuantityUnit.SECOND);
        expected = tq;
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.SECOND, 2, BigDecimal.ROUND_HALF_UP));
        // SECOND to MILLISECOND
        tq = new TimeQuantity(2, TimeQuantityUnit.SECOND);
        expected = new TimeQuantity(2000, TimeQuantityUnit.MILLISECOND);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.MILLISECOND, 2, BigDecimal.ROUND_HALF_UP));

    }

    public void testSameUnit() {

        TimeQuantity tq;
        TimeQuantity expected;

        // No scaling needed
        tq = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));

        // Scaling
        tq = new TimeQuantity(new BigDecimal("1.123"), TimeQuantityUnit.HOUR);
        expected = new TimeQuantity(new BigDecimal("1.12"), TimeQuantityUnit.HOUR);
        assertEquals(expected, ScheduleTimeQuantity.convertToUnit(tq, TimeQuantityUnit.HOUR, 2, BigDecimal.ROUND_HALF_UP));
    }

}
