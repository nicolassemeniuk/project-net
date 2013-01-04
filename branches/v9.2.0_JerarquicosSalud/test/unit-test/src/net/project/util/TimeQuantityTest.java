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

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests the functions in the {@link net.project.util.TimeQuantity}
 * class.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TimeQuantityTest extends TestCase {
    public TimeQuantityTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TimeQuantityTest.class);

        return suite;
    }

    /**
     * Tests {@link TimeQuantity#TimeQuantity(java.lang.Number, net.project.util.TimeQuantityUnit)}.
     */
    public void testInitNumber() {

        // BFD-2029
        // The following problem was identified:
        // new TimeQuantity(new Double(0.5555), TimeQuantityUnit.DAY).getAmount().toString()
        // returns: 0.55549999999999999378275106209912337362766265869140625
        // The reason is that when we create the amount as a BigDecimal, we pass
        // Number.doubleValue(); we lose precision
        // This has the problem that setScale(3, ROUND_HALF_UP) results in
        // 0.555 instead of 0.556

        TimeQuantity tq;

        // Check 0.5555 is represented correctly
        tq = new TimeQuantity(new Double(0.5555), TimeQuantityUnit.DAY);
        assertEquals("0.5555", tq.getAmount().toString());
        assertEquals(TimeQuantityUnit.DAY, tq.getUnits());
        assertEquals("0.556", tq.getAmount().setScale(3, BigDecimal.ROUND_HALF_UP).toString());

    }

    /**
     * Tests {@link TimeQuantity#TimeQuantity(double, net.project.util.TimeQuantityUnit)}.
     */
    public void testInitDouble() {

        TimeQuantity tq;

        // Check 0.5555 is represented correctly
        tq = new TimeQuantity(0.5555, TimeQuantityUnit.DAY);
        assertEquals("0.5555", tq.getAmount().toString());
        assertEquals(TimeQuantityUnit.DAY, tq.getUnits());
        assertEquals("0.556", tq.getAmount().setScale(3, BigDecimal.ROUND_HALF_UP).toString());

    }

    /** Test of getUnits method, of class net.project.util.TimeQuantity. */
    public void testGetUnits() {
        TimeQuantity tq = new TimeQuantity(1, TimeQuantityUnit.SECOND);
        assertEquals(tq.getUnits(), TimeQuantityUnit.SECOND);

        TimeQuantity tq2 = new TimeQuantity(new BigDecimal(1), TimeQuantityUnit.SECOND);
        assertEquals(tq2.getUnits(), TimeQuantityUnit.SECOND);
    }

    /** Test of getAmount method, of class net.project.util.TimeQuantity. */
    public void testGetAmount() {
        TimeQuantity tq = new TimeQuantity(5, TimeQuantityUnit.HOUR);
        assertEquals(tq.getAmount().intValue(), 5);

        tq = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assertEquals(tq.getAmount().intValue(), 0);

        tq = new TimeQuantity(-1, TimeQuantityUnit.HOUR);
        assertEquals(tq.getAmount().intValue(), -1);
    }

    /** Test of toString method, of class net.project.util.TimeQuantity. */
    public void testToString() {
        //MAFTODO: cannot write this test until we implement a mock object for SessionManager
    }

    /** Test of convertTo method, of class net.project.util.TimeQuantity. */
    public void testConvertTo() {
        TimeQuantity tq = new TimeQuantity(1, TimeQuantityUnit.WEEK);
        TimeQuantity convertedTQ = tq.convertTo(TimeQuantityUnit.DAY, 0);
        assertEquals(7, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.DAY, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.STANDARD_WORK_WEEK);
        convertedTQ = tq.convertTo(TimeQuantityUnit.STANDARD_WORK_DAY,0);
        assertEquals(5, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.STANDARD_WORK_DAY, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.DAY);
        convertedTQ = tq.convertTo(TimeQuantityUnit.DAY, 0);
        assertEquals(1, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.DAY, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.DAY);
        convertedTQ = tq.convertTo(TimeQuantityUnit.HOUR, 0);
        assertEquals(24, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.HOUR, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        convertedTQ = tq.convertTo(TimeQuantityUnit.MINUTE, 0);
        assertEquals(60, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.MINUTE, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        convertedTQ = tq.convertTo(TimeQuantityUnit.SECOND, 0);
        assertEquals(60, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.SECOND, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.SECOND);
        convertedTQ = tq.convertTo(TimeQuantityUnit.MILLISECOND, 0);
        assertEquals(1000, convertedTQ.getAmount().intValue());
        assertEquals(TimeQuantityUnit.MILLISECOND, convertedTQ.getUnits());

        tq = new TimeQuantity(1, TimeQuantityUnit.MILLISECOND);
        convertedTQ = tq.convertTo(TimeQuantityUnit.SECOND, 3);
        assertEquals(new BigDecimal("0.001"), convertedTQ.getAmount());
        assertEquals(TimeQuantityUnit.SECOND, convertedTQ.getUnits());

        tq = new TimeQuantity(90, TimeQuantityUnit.MINUTE);
        convertedTQ = tq.convertTo(TimeQuantityUnit.HOUR, 1);
        assertEquals(new BigDecimal("1.5"), convertedTQ.getAmount());
        assertEquals(TimeQuantityUnit.HOUR, convertedTQ.getUnits());
    }

    /**
     * Tests {@link TimeQuantity#add(TimeQuantity)}.
     */
    public void testAddNoScaling() {

        // Same units

        // 1ms + 1ms = 2 ms
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.MILLISECOND),
                new TimeQuantity(1, TimeQuantityUnit.MILLISECOND).add(new TimeQuantity(1, TimeQuantityUnit.MILLISECOND)));

        // 2s + 2s = 4s
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.SECOND),
                new TimeQuantity(2, TimeQuantityUnit.SECOND).add(new TimeQuantity(2, TimeQuantityUnit.SECOND)));

        // 3min + 3min = 6min
        assertEquals(new TimeQuantity(6, TimeQuantityUnit.MINUTE),
                new TimeQuantity(3, TimeQuantityUnit.MINUTE).add(new TimeQuantity(3, TimeQuantityUnit.MINUTE)));

        // 4hour+ 4hour = 8hours
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR),
                new TimeQuantity(4, TimeQuantityUnit.HOUR).add(new TimeQuantity(4, TimeQuantityUnit.HOUR)));

        // Different units; smaller unit is to the left so we don't lose precision

        // 1ms + 1s = 1001ms
        assertEquals(new TimeQuantity(1001, TimeQuantityUnit.MILLISECOND),
                new TimeQuantity(1, TimeQuantityUnit.MILLISECOND).add(new TimeQuantity(1, TimeQuantityUnit.SECOND)));

        // 1s + 1min = 61s
        assertEquals(new TimeQuantity(61, TimeQuantityUnit.SECOND),
                new TimeQuantity(1, TimeQuantityUnit.SECOND).add(new TimeQuantity(1, TimeQuantityUnit.MINUTE)));

        // 1min + 1hour = 61min
        assertEquals(new TimeQuantity(61, TimeQuantityUnit.MINUTE),
                new TimeQuantity(1, TimeQuantityUnit.MINUTE).add(new TimeQuantity(1, TimeQuantityUnit.HOUR)));

        // Different units; smaller unit to right; may require scaling

        // No scaling required
        // 1s + 1000ms = 2 s
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.SECOND),
                new TimeQuantity(1, TimeQuantityUnit.SECOND).add(new TimeQuantity(1000, TimeQuantityUnit.MILLISECOND)));

        // 1s + 500ms = <1.5s>
        assertEquals(new TimeQuantity(1.5, TimeQuantityUnit.SECOND),
            new TimeQuantity(1, TimeQuantityUnit.SECOND).add(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND)));
    }

    /**
     * Tests {@link TimeQuantity#add(TimeQuantity, int, int)}.
     */
    public void testAddScaling() {

        // 1s + 500ms = 1.5s
        assertEquals(new TimeQuantity(new BigDecimal("1.5"), TimeQuantityUnit.SECOND),
                new TimeQuantity(1, TimeQuantityUnit.SECOND).add(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND), 1, BigDecimal.ROUND_HALF_UP));

        // 1m + 500ms = 1.008m
        assertEquals(new TimeQuantity(new BigDecimal("1.008"), TimeQuantityUnit.MINUTE),
                new TimeQuantity(1, TimeQuantityUnit.MINUTE).add(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 1m + 1s = 1.017m
        assertEquals(new TimeQuantity(new BigDecimal("1.017"), TimeQuantityUnit.MINUTE),
                new TimeQuantity(1, TimeQuantityUnit.MINUTE).add(new TimeQuantity(1, TimeQuantityUnit.SECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 1h + 3500ms = 1.001h
        assertEquals(new TimeQuantity(new BigDecimal("1.001"), TimeQuantityUnit.HOUR),
                new TimeQuantity(1, TimeQuantityUnit.HOUR).add(new TimeQuantity(3500, TimeQuantityUnit.MILLISECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 1h + 300s = 1.083h
        assertEquals(new TimeQuantity(new BigDecimal("1.083"), TimeQuantityUnit.HOUR),
                new TimeQuantity(1, TimeQuantityUnit.HOUR).add(new TimeQuantity(300, TimeQuantityUnit.SECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 1h + 59m = 1.983h
        assertEquals(new TimeQuantity(new BigDecimal("1.983"), TimeQuantityUnit.HOUR),
                new TimeQuantity(1, TimeQuantityUnit.HOUR).add(new TimeQuantity(59, TimeQuantityUnit.MINUTE), 3, BigDecimal.ROUND_HALF_UP));

        // 1.191s + 9ms = 1.2s
        assertEquals(new TimeQuantity(new BigDecimal("1.2"), TimeQuantityUnit.SECOND),
            new TimeQuantity(new BigDecimal("1.191"), TimeQuantityUnit.SECOND).add(new TimeQuantity(9, TimeQuantityUnit.MILLISECOND)));


    }

    /**
     * Tests {@link TimeQuantity#subtract(TimeQuantity)}.
     */
    public void testSubtractNoScaling() {

        // Same Units

        // 1ms - 1ms = 0 ms
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.MILLISECOND),
                new TimeQuantity(1, TimeQuantityUnit.MILLISECOND).subtract(new TimeQuantity(1, TimeQuantityUnit.MILLISECOND)));

        // 10ms - 1ms = 9 ms
        assertEquals(new TimeQuantity(9, TimeQuantityUnit.MILLISECOND),
                new TimeQuantity(10, TimeQuantityUnit.MILLISECOND).subtract(new TimeQuantity(1, TimeQuantityUnit.MILLISECOND)));

        // 4s - 2s = 2s
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.SECOND),
                new TimeQuantity(4, TimeQuantityUnit.SECOND).subtract(new TimeQuantity(2, TimeQuantityUnit.SECOND)));

        // 6min - 3min = 3min
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.MINUTE),
                new TimeQuantity(6, TimeQuantityUnit.MINUTE).subtract(new TimeQuantity(3, TimeQuantityUnit.MINUTE)));

        // 8hour - 4hour = 4hours
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR),
                new TimeQuantity(8, TimeQuantityUnit.HOUR).subtract(new TimeQuantity(4, TimeQuantityUnit.HOUR)));

        // 8h - 3.2hr = 4.8h
        assertEquals(new TimeQuantity(4.8, TimeQuantityUnit.HOUR),
                new TimeQuantity(8, TimeQuantityUnit.HOUR).subtract(new TimeQuantity(3.2, TimeQuantityUnit.HOUR)));

        // 12h - 0.5day = 0 hr
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR),
            new TimeQuantity(new BigDecimal(12), TimeQuantityUnit.HOUR).subtract(new TimeQuantity(new BigDecimal(50/100f), TimeQuantityUnit.DAY)));

        // Different units; smaller unit is to the left so we don't lose precision

        // 1001ms - 1s = 1ms
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.MILLISECOND),
                new TimeQuantity(1001, TimeQuantityUnit.MILLISECOND).subtract(new TimeQuantity(1, TimeQuantityUnit.SECOND)));

        // 61s - 1min = 1s
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.SECOND),
                new TimeQuantity(61, TimeQuantityUnit.SECOND).subtract(new TimeQuantity(1, TimeQuantityUnit.MINUTE)));

        // 61min - 1hour = 1min
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.MINUTE),
                new TimeQuantity(61, TimeQuantityUnit.MINUTE).subtract(new TimeQuantity(1, TimeQuantityUnit.HOUR)));

        // Different units; smaller unit to right; may require scaling

        // No scaling required
        // 2s - 1000ms = 1s
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.SECOND),
                new TimeQuantity(2, TimeQuantityUnit.SECOND).subtract(new TimeQuantity(1000, TimeQuantityUnit.MILLISECOND)));

        // Scaling required;
        // 2s - 500ms = <1.5s>
        assertEquals(new TimeQuantity(1.5, TimeQuantityUnit.SECOND),
            new TimeQuantity(2, TimeQuantityUnit.SECOND).subtract(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND)));
    }

    /**
     * Tests {@link TimeQuantity#subtract(TimeQuantity, int, int)}.
     */
    public void testSubtractScaling() {

        // 2s - 500ms = 1.5s
        assertEquals(new TimeQuantity(new BigDecimal("1.5"), TimeQuantityUnit.SECOND),
                new TimeQuantity(2, TimeQuantityUnit.SECOND).subtract(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND), 1, BigDecimal.ROUND_HALF_UP));

        // 2m - 500ms = 1.992
        assertEquals(new TimeQuantity(new BigDecimal("1.992"), TimeQuantityUnit.MINUTE),
                new TimeQuantity(2, TimeQuantityUnit.MINUTE).subtract(new TimeQuantity(500, TimeQuantityUnit.MILLISECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 2m - 1s = 1.983m
        assertEquals(new TimeQuantity(new BigDecimal("1.983"), TimeQuantityUnit.MINUTE),
                new TimeQuantity(2, TimeQuantityUnit.MINUTE).subtract(new TimeQuantity(1, TimeQuantityUnit.SECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 2h - 3500ms = 1.999h
        assertEquals(new TimeQuantity(new BigDecimal("1.999"), TimeQuantityUnit.HOUR),
                new TimeQuantity(2, TimeQuantityUnit.HOUR).subtract(new TimeQuantity(3500, TimeQuantityUnit.MILLISECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 2h - 300s = 1.917h
        assertEquals(new TimeQuantity(new BigDecimal("1.917"), TimeQuantityUnit.HOUR),
                new TimeQuantity(2, TimeQuantityUnit.HOUR).subtract(new TimeQuantity(300, TimeQuantityUnit.SECOND), 3, BigDecimal.ROUND_HALF_UP));

        // 2h - 59m = 1.017h
        assertEquals(new TimeQuantity(new BigDecimal("1.017"), TimeQuantityUnit.HOUR),
                new TimeQuantity(2, TimeQuantityUnit.HOUR).subtract(new TimeQuantity(59, TimeQuantityUnit.MINUTE), 3, BigDecimal.ROUND_HALF_UP));

    }

    /**
     * Tests {@link TimeQuantity#multiply(java.math.BigDecimal)}.
     */
    public void testMultiply() {
        TimeQuantity quantity = new TimeQuantity(1, TimeQuantityUnit.DAY);
        BigDecimal multiplicand = new BigDecimal(50f/100);

        assertEquals(new TimeQuantity(new BigDecimal(0.5), TimeQuantityUnit.DAY),
            quantity.multiply(multiplicand));
    }


    /**
     * Tests {@link TimeQuantity#divide(java.math.BigDecimal, int, int)}.
     */
    public void testDivide1() {

        TimeQuantity quantity;
        BigDecimal divisor;
        TimeQuantity expected;

        // 4 hrs / 2 = 2 hrs
        quantity = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        divisor = new BigDecimal(2);
        expected = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 2 hrs / 4 = 0.5 hrs
        quantity = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        divisor = new BigDecimal(4);
        expected = new TimeQuantity(0.5, TimeQuantityUnit.HOUR);
        assertEquals(expected, quantity.divide(divisor, 1, BigDecimal.ROUND_UNNECESSARY));

        //
        // Test the rounding mechanism
        // 3 hrs / 9
        quantity = new TimeQuantity(3, TimeQuantityUnit.HOUR);
        divisor = new BigDecimal(9);

        // 3 hrs / 9 = 0.34 rounded up
        expected = new TimeQuantity(new BigDecimal("0.34"), TimeQuantityUnit.HOUR);
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UP));

        // 3 hrs / 9 = 0.33 round half up
        expected = new TimeQuantity(new BigDecimal("0.33"), TimeQuantityUnit.HOUR);
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_HALF_UP));

        // 1.8 hrs / 9 = 0.20 scale 2, round unecessary
        quantity = new TimeQuantity(1.8, TimeQuantityUnit.HOUR);
        divisor = new BigDecimal(9);
        expected = new TimeQuantity(new BigDecimal("0.20"), TimeQuantityUnit.HOUR);
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

    }

    /**
     * Tests {@link TimeQuantity#divide(net.project.util.TimeQuantity, int, int)}.
     */
    public void testDivide2() {

        TimeQuantity quantity;
        TimeQuantity divisor;
        BigDecimal expected;

        //
        // Same unit
        //

        // 4hrs/2hrs = 2
        quantity = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("2.00");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 3hrs/2hrs = 1.5
        quantity = new TimeQuantity(3, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("1.50");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 1hr/3hrs = 0.33
        quantity = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(3, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("0.33");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_HALF_UP));

        // 2hr/3hrs = 0.67
        quantity = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(3, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("0.67");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_HALF_UP));

        //
        // Bigger Unit divided by smaller unit
        //

        // 1hrs / 30 minutes = 2.00
        quantity = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(30, TimeQuantityUnit.MINUTE);
        expected = new BigDecimal("2.00");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 1hrs / 30 seconds = 120.00
        quantity = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(30, TimeQuantityUnit.SECOND);
        expected = new BigDecimal("120.00");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 1 minute / 59 seconds = 1.0169491525423728813559322033898
        quantity = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        divisor = new TimeQuantity(59, TimeQuantityUnit.SECOND);
        expected = new BigDecimal("1.016949152542373");
        assertEquals(expected, quantity.divide(divisor, 15, BigDecimal.ROUND_HALF_UP));

        // 1 minute / 300 seconds = 0.20
        quantity = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        divisor = new TimeQuantity(300, TimeQuantityUnit.SECOND);
        expected = new BigDecimal("0.20");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 5.33 hours / 480 minutes = 0.67
        quantity = new TimeQuantity(5.33, TimeQuantityUnit.HOUR);
        divisor = new TimeQuantity(480, TimeQuantityUnit.MINUTE);
        expected = new BigDecimal("0.67");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_HALF_UP));

        //
        // Smaller Unit divided by bigger unit
        //

        // 30 minutes / 1hr = 0.50
        quantity = new TimeQuantity(30, TimeQuantityUnit.MINUTE);
        divisor = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("0.50");
        assertEquals(expected, quantity.divide(divisor, 2, BigDecimal.ROUND_UNNECESSARY));

        // 30 seconds / 1hr = 0.0083333333333333333333333333333333
        quantity = new TimeQuantity(30, TimeQuantityUnit.SECOND);
        divisor = new TimeQuantity(1, TimeQuantityUnit.HOUR);
        expected = new BigDecimal("0.0083");
        assertEquals(expected, quantity.divide(divisor, 4, BigDecimal.ROUND_HALF_UP));

        // 59 seconds / 1 minute = 0.98333333333333333333333333333333
        quantity = new TimeQuantity(59, TimeQuantityUnit.SECOND);
        divisor = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        expected = new BigDecimal("0.9833");
        assertEquals(expected, quantity.divide(divisor, 4, BigDecimal.ROUND_HALF_UP));

        // 300 seconds / 1 minute = 5
        quantity = new TimeQuantity(300, TimeQuantityUnit.SECOND);
        divisor = new TimeQuantity(1, TimeQuantityUnit.MINUTE);
        expected = new BigDecimal("5");
        assertEquals(expected, quantity.divide(divisor, 0, BigDecimal.ROUND_UNNECESSARY));


    }

    /** Test of the stratify() method of TimeQuantity. */
    public void testStratify() {
        TimeQuantity fourAndAHalfHours = new TimeQuantity(new BigDecimal("4.5"), TimeQuantityUnit.HOUR);
        List strata = fourAndAHalfHours.stratify();

        assertEquals(2, strata.size());
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), strata.get(0));
        assertEquals(new TimeQuantity(30, TimeQuantityUnit.MINUTE), strata.get(1));

        TimeQuantity onePoint125Hours = new TimeQuantity(new BigDecimal("1.125"), TimeQuantityUnit.HOUR);
        strata = onePoint125Hours.stratify();

        assertEquals(3, strata.size());
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), strata.get(0));
        assertEquals(new TimeQuantity(7, TimeQuantityUnit.MINUTE), strata.get(1));
        assertEquals(new TimeQuantity(30, TimeQuantityUnit.SECOND), strata.get(2));
    }

    /** Test the compareTo() method of TimeQuantity. */
    public void testCompareTo() {
        TimeQuantity tq = new TimeQuantity(1, TimeQuantityUnit.HOUR);

        assertEquals(0, tq.compareTo(new TimeQuantity(1, TimeQuantityUnit.HOUR)));
        assertEquals(0, tq.compareTo(new TimeQuantity(60, TimeQuantityUnit.MINUTE)));
        assertEquals(0, tq.compareTo(new TimeQuantity(3600, TimeQuantityUnit.SECOND)));

        assertTrue(tq.compareTo(new TimeQuantity(0.5, TimeQuantityUnit.HOUR)) > 0);
        assertTrue(tq.compareTo(new TimeQuantity(59, TimeQuantityUnit.MINUTE)) > 0);
        assertTrue(tq.compareTo(new TimeQuantity(3599, TimeQuantityUnit.SECOND)) > 0);

        assertTrue(tq.compareTo(new TimeQuantity(1.1, TimeQuantityUnit.HOUR)) < 0);
        assertTrue(tq.compareTo(new TimeQuantity(61, TimeQuantityUnit.MINUTE)) < 0);
        assertTrue(tq.compareTo(new TimeQuantity(3601, TimeQuantityUnit.SECOND)) < 0);

        tq = new TimeQuantity(12, TimeQuantityUnit.HOUR);

        assertTrue(tq.compareTo(new TimeQuantity(0.5, TimeQuantityUnit.DAY)) == 0);
    }

    public void testDynamicScale() {
        TimeQuantity tqToTest = new TimeQuantity(new BigDecimal("1"), TimeQuantityUnit.HOUR);
        assertEquals(0, TimeQuantity.dynamicScale(tqToTest).getAmount().scale());

        tqToTest = new TimeQuantity(new BigDecimal("1.10000000"), TimeQuantityUnit.HOUR);
        assertEquals(1, TimeQuantity.dynamicScale(tqToTest).getAmount().scale());
        assertEquals(tqToTest, TimeQuantity.dynamicScale(tqToTest));

        tqToTest = new TimeQuantity(new BigDecimal("1.11000000"), TimeQuantityUnit.HOUR);
        assertEquals(2, TimeQuantity.dynamicScale(tqToTest).getAmount().scale());
        assertEquals(tqToTest, TimeQuantity.dynamicScale(tqToTest));

        tqToTest = new TimeQuantity(new BigDecimal("1.11100000"), TimeQuantityUnit.HOUR);
        assertEquals(3, TimeQuantity.dynamicScale(tqToTest).getAmount().scale());
        assertEquals(tqToTest, TimeQuantity.dynamicScale(tqToTest));

        tqToTest = new TimeQuantity(new BigDecimal("1.9999999999"), TimeQuantityUnit.HOUR);
        assertEquals(10, TimeQuantity.dynamicScale(tqToTest).getAmount().scale());
        assertEquals(tqToTest, TimeQuantity.dynamicScale(tqToTest));

    }
}
