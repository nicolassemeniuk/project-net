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
 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 15404 $
|        $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|      $Author: deepak $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>SimpleTimeQuantityTest</code>.
 * @author
 * @since
 */
public class SimpleTimeQuantityTest extends TestCase {

    public SimpleTimeQuantityTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SimpleTimeQuantityTest.class);

        return suite;
    }

    /**
     * Tests {@link SimpleTimeQuantity#SimpleTimeQuantity(long, long)}.
     */
    public void testSimpleTimeQuantity1() {

        // Failures
        try {
            new SimpleTimeQuantity(0, 60);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new SimpleTimeQuantity(0, -1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new SimpleTimeQuantity(0, 500000);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Successes
        new SimpleTimeQuantity(0, 0);
        new SimpleTimeQuantity(0, 59);
        new SimpleTimeQuantity(500, 59);

    }

    /**
     * Tests {@link SimpleTimeQuantity#SimpleTimeQuantity(BigDecimal)}.
     */
    public void testSimpleTimeQuantity2() {

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(new BigDecimal("0")));
        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(new BigDecimal("0.000000")));
        assertEquals(new SimpleTimeQuantity(0, 1), new SimpleTimeQuantity(new BigDecimal("0.017")));
        assertEquals(new SimpleTimeQuantity(0, 30), new SimpleTimeQuantity(new BigDecimal("0.5")));
        assertEquals(new SimpleTimeQuantity(1, 0), new SimpleTimeQuantity(new BigDecimal("1")));
        assertEquals(new SimpleTimeQuantity(1, 58), new SimpleTimeQuantity(new BigDecimal("1.974")));

        // Assert 1.975 .. 1.99 are all 1 hour 59 minutes (58.5 to 59.4)
        for (int i = 975; i <= 990; i++) {
            assertEquals(new SimpleTimeQuantity(1, 59), new SimpleTimeQuantity(new BigDecimal("1." + i)));
        }

        assertEquals(new SimpleTimeQuantity(0, 0, true), new SimpleTimeQuantity(new BigDecimal("-0")));
        assertEquals(new SimpleTimeQuantity(1, 0, true), new SimpleTimeQuantity(new BigDecimal("-1")));
        assertEquals(new SimpleTimeQuantity(12, 30, true), new SimpleTimeQuantity(new BigDecimal("-12.5")));

    }

    public void testToHour() {
        assertEquals(new BigDecimal("0.0000000000"), new SimpleTimeQuantity(0, 0).toHour());
        assertEquals(new BigDecimal("0.0166666667"), new SimpleTimeQuantity(0, 1).toHour());
        assertEquals(new BigDecimal("0.5000000000"), new SimpleTimeQuantity(0, 30).toHour());
        assertEquals(new BigDecimal("0.9833333333"), new SimpleTimeQuantity(0, 59).toHour());
        assertEquals(new BigDecimal("1.0000000000"), new SimpleTimeQuantity(1, 0).toHour());
        assertEquals(new BigDecimal("1000000.0000000000"), new SimpleTimeQuantity(1000000, 0).toHour());

        assertEquals(new BigDecimal("-0.0000000000"), new SimpleTimeQuantity(0, 0, true).toHour());
        assertEquals(new BigDecimal("-0.0166666667"), new SimpleTimeQuantity(0, 1, true).toHour());
        assertEquals(new BigDecimal("-0.5000000000"), new SimpleTimeQuantity(0, 30, true).toHour());
        assertEquals(new BigDecimal("-0.9833333333"), new SimpleTimeQuantity(0, 59, true).toHour());
        assertEquals(new BigDecimal("-1.0000000000"), new SimpleTimeQuantity(1, 0, true).toHour());
        assertEquals(new BigDecimal("-1000000.0000000000"), new SimpleTimeQuantity(1000000, 0, true).toHour());
    }

    public void testToDay() {

        try {
            assertEquals(new BigDecimal("0.00"), new SimpleTimeQuantity(7, 0).toDay(0));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        assertEquals(new BigDecimal("0.0000000000"), new SimpleTimeQuantity(0, 0).toDay(8));
        assertEquals(new BigDecimal("1.0000000000"), new SimpleTimeQuantity(8, 0).toDay(8));
        assertEquals(new BigDecimal("1.5000000000"), new SimpleTimeQuantity(12, 0).toDay(8));
        assertEquals(new BigDecimal("1.5000000000"), new SimpleTimeQuantity(12, 0).toDay(8));
        assertEquals(new BigDecimal("0.8750000000"), new SimpleTimeQuantity(7, 0).toDay(8));
        assertEquals(new BigDecimal("1.0000000000"), new SimpleTimeQuantity(24, 0).toDay(24));
        assertEquals(new BigDecimal("0.3333333333"), new SimpleTimeQuantity(8, 0).toDay(24));

        assertEquals(new BigDecimal("-0.0000000000"), new SimpleTimeQuantity(0, 0, true).toDay(8));
        assertEquals(new BigDecimal("-1.0000000000"), new SimpleTimeQuantity(8, 0, true).toDay(8));
        assertEquals(new BigDecimal("-1.5000000000"), new SimpleTimeQuantity(12, 0, true).toDay(8));
        assertEquals(new BigDecimal("-1.5000000000"), new SimpleTimeQuantity(12, 0, true).toDay(8));
        assertEquals(new BigDecimal("-0.8750000000"), new SimpleTimeQuantity(7, 0, true).toDay(8));
        assertEquals(new BigDecimal("-1.0000000000"), new SimpleTimeQuantity(24, 0, true).toDay(24));
        assertEquals(new BigDecimal("-0.3333333333"), new SimpleTimeQuantity(8, 0, true).toDay(24));

    }

    public void testAdd() {

        try {
            assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).add(null));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).add(new SimpleTimeQuantity(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), new SimpleTimeQuantity(0, 0).add(new SimpleTimeQuantity(0, 1)));
        assertEquals(new SimpleTimeQuantity(1, 58), new SimpleTimeQuantity(0, 59).add(new SimpleTimeQuantity(0, 59)));
        assertEquals(new SimpleTimeQuantity(10, 30), new SimpleTimeQuantity(5, 30).add(new SimpleTimeQuantity(5, 0)));
        assertEquals(new SimpleTimeQuantity(1000000, 0), new SimpleTimeQuantity(500000, 0).add(new SimpleTimeQuantity(500000, 0)));

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).add(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(new SimpleTimeQuantity(0, 1, true), new SimpleTimeQuantity(0, 0).add(new SimpleTimeQuantity(0, 1, true)));
        assertEquals(new SimpleTimeQuantity(0, 1, true), new SimpleTimeQuantity(0, 1, true).add(new SimpleTimeQuantity(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 1, true).add(new SimpleTimeQuantity(0, 1)));
        assertEquals(new SimpleTimeQuantity(0, 2, true), new SimpleTimeQuantity(0, 1, true).add(new SimpleTimeQuantity(0, 1, true)));

        assertEquals(new SimpleTimeQuantity(0, 30), new SimpleTimeQuantity(5, 30).add(new SimpleTimeQuantity(5, 0, true)));
        assertEquals(new SimpleTimeQuantity(0, 30, true), new SimpleTimeQuantity(5, 0).add(new SimpleTimeQuantity(5, 30, true)));

        assertEquals(new SimpleTimeQuantity(8, 0, true), new SimpleTimeQuantity(16, 0, true).add(new SimpleTimeQuantity(8, 0)));
        assertEquals(new SimpleTimeQuantity(new BigDecimal("-8.000")), new SimpleTimeQuantity(new BigDecimal("-16")).add(new SimpleTimeQuantity(new BigDecimal("8"))));
    }

    public void testSubtract() {

        try {
            new SimpleTimeQuantity(0, 0).subtract(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).subtract(new SimpleTimeQuantity(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 1), new SimpleTimeQuantity(0, 1).subtract(new SimpleTimeQuantity(0, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 1).subtract(new SimpleTimeQuantity(0, 1)));
        assertEquals(new SimpleTimeQuantity(2, 56), new SimpleTimeQuantity(12, 27).subtract(new SimpleTimeQuantity(9, 31)));
        assertEquals(new SimpleTimeQuantity(0, 1, true), new SimpleTimeQuantity(3, 59).subtract(new SimpleTimeQuantity(4, 0)));

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).subtract(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(new SimpleTimeQuantity(0, 1), new SimpleTimeQuantity(0, 1).subtract(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(new SimpleTimeQuantity(0, 2), new SimpleTimeQuantity(0, 1).subtract(new SimpleTimeQuantity(0, 1, true)));
    }

    public void testDivide() {

        try {
            new SimpleTimeQuantity(0, 0).divide(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new SimpleTimeQuantity(0, 0).divide(new BigDecimal("0"));
            fail("Expected ArithmeticException");
        } catch (ArithmeticException e) {
            // Expected
        }

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).divide(new BigDecimal("1")));
        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).divide(new BigDecimal("100")));
        assertEquals(new SimpleTimeQuantity(1, 0), new SimpleTimeQuantity(2, 0).divide(new BigDecimal("2")));
        assertEquals(new SimpleTimeQuantity(5, 10), new SimpleTimeQuantity(5, 47).divide(new BigDecimal("1.12")));
        assertEquals(new SimpleTimeQuantity(0, 22), new SimpleTimeQuantity(0, 23).divide(new BigDecimal("1.05")));
        assertEquals(new SimpleTimeQuantity(137, 30), new SimpleTimeQuantity(123, 45).divide(new BigDecimal("0.9")));

        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).divide(new BigDecimal("-1")));
        assertEquals(new SimpleTimeQuantity(0, 0), new SimpleTimeQuantity(0, 0).divide(new BigDecimal("-100")));
        assertEquals(new SimpleTimeQuantity(1, 0, true), new SimpleTimeQuantity(2, 0).divide(new BigDecimal("-2")));
        assertEquals(new SimpleTimeQuantity(5, 10, true), new SimpleTimeQuantity(5, 47).divide(new BigDecimal("-1.12")));
        assertEquals(new SimpleTimeQuantity(0, 22, true), new SimpleTimeQuantity(0, 23).divide(new BigDecimal("-1.05")));
        assertEquals(new SimpleTimeQuantity(137, 30, true), new SimpleTimeQuantity(123, 45).divide(new BigDecimal("-0.9")));

        assertEquals(new SimpleTimeQuantity(0, 0, true), new SimpleTimeQuantity(0, 0, true).divide(new BigDecimal("1")));
        assertEquals(new SimpleTimeQuantity(0, 0, true), new SimpleTimeQuantity(0, 0, true).divide(new BigDecimal("100")));
        assertEquals(new SimpleTimeQuantity(1, 0, true), new SimpleTimeQuantity(2, 0, true).divide(new BigDecimal("2")));
        assertEquals(new SimpleTimeQuantity(5, 10, true), new SimpleTimeQuantity(5, 47, true).divide(new BigDecimal("1.12")));
        assertEquals(new SimpleTimeQuantity(0, 22, true), new SimpleTimeQuantity(0, 23, true).divide(new BigDecimal("1.05")));
        assertEquals(new SimpleTimeQuantity(137, 30, true), new SimpleTimeQuantity(123, 45, true).divide(new BigDecimal("0.9")));
    }

    public void testCompareTo() {

        try {
            new SimpleTimeQuantity(0, 0).compareTo(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        assertEquals(0, new SimpleTimeQuantity(0, 0).compareTo(new SimpleTimeQuantity(0, 0)));
        assertEquals(0, new SimpleTimeQuantity(0, 1).compareTo(new SimpleTimeQuantity(0, 1)));
        assertEquals(0, new SimpleTimeQuantity(1, 0).compareTo(new SimpleTimeQuantity(1, 0)));
        assertEquals(0, new SimpleTimeQuantity(147, 55).compareTo(new SimpleTimeQuantity(147, 55)));

        assertEquals(-1, new SimpleTimeQuantity(0, 0).compareTo(new SimpleTimeQuantity(0, 1)));
        assertEquals(-1, new SimpleTimeQuantity(0, 0).compareTo(new SimpleTimeQuantity(1, 0)));
        assertEquals(-1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(123, 58)));
        assertEquals(-1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(124, 1)));
        assertEquals(-1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(999, 0)));

        assertEquals(1, new SimpleTimeQuantity(0, 1).compareTo(new SimpleTimeQuantity(0, 0)));
        assertEquals(1, new SimpleTimeQuantity(2, 0).compareTo(new SimpleTimeQuantity(0, 0)));
        assertEquals(1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(1, 9)));
        assertEquals(1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(1, 57)));
        assertEquals(1, new SimpleTimeQuantity(999, 13).compareTo(new SimpleTimeQuantity(999, 12)));

        assertEquals(0, new SimpleTimeQuantity(0, 0, true).compareTo(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(0, new SimpleTimeQuantity(0, 0, true).compareTo(new SimpleTimeQuantity(0, 0)));
        assertEquals(0, new SimpleTimeQuantity(0, 0).compareTo(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(0, new SimpleTimeQuantity(0, 1, true).compareTo(new SimpleTimeQuantity(0, 1, true)));
        assertEquals(0, new SimpleTimeQuantity(1, 0, true).compareTo(new SimpleTimeQuantity(1, 0, true)));
        assertEquals(0, new SimpleTimeQuantity(147, 55, true).compareTo(new SimpleTimeQuantity(147, 55, true)));

        assertEquals(-1, new SimpleTimeQuantity(0, 1, true).compareTo(new SimpleTimeQuantity(0, 0)));
        assertEquals(-1, new SimpleTimeQuantity(123, 57, true).compareTo(new SimpleTimeQuantity(123, 58)));
        assertEquals(-1, new SimpleTimeQuantity(123, 57, true).compareTo(new SimpleTimeQuantity(1, 11)));

        assertEquals(1, new SimpleTimeQuantity(0, 1).compareTo(new SimpleTimeQuantity(0, 0, true)));
        assertEquals(1, new SimpleTimeQuantity(2, 0).compareTo(new SimpleTimeQuantity(3, 0, true)));
        assertEquals(1, new SimpleTimeQuantity(123, 57).compareTo(new SimpleTimeQuantity(1, 9, true)));
    }

    public void testEquals() {

        SimpleTimeQuantity quantity1;
        SimpleTimeQuantity quantity2;
        SimpleTimeQuantity quantity3;

        // Same instance
        quantity1 = new SimpleTimeQuantity(0, 0);
        assertTrue(quantity1.equals(quantity1));

        // Wrong class
        quantity1 = new SimpleTimeQuantity(0, 0);
        assertFalse(quantity1.equals(new Object()));

        // Null
        quantity1 = new SimpleTimeQuantity(0, 0);
        assertFalse(quantity1.equals(null));

        // Same values
        quantity1 = new SimpleTimeQuantity(0, 0);
        quantity2 = new SimpleTimeQuantity(0, 0);
        assertTrue(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(0, 30);
        quantity2 = new SimpleTimeQuantity(0, 30);
        assertTrue(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(1, 0);
        quantity2 = new SimpleTimeQuantity(1, 0);
        assertTrue(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(1, 30);
        quantity2 = new SimpleTimeQuantity(1, 30);
        assertTrue(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(500, 59);
        quantity2 = new SimpleTimeQuantity(500, 59);
        assertTrue(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(500, 59, true);
        quantity2 = new SimpleTimeQuantity(500, 59, true);
        assertTrue(quantity1.equals(quantity2));

        // Different values
        quantity1 = new SimpleTimeQuantity(0, 0);
        quantity2 = new SimpleTimeQuantity(0, 1);
        assertFalse(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(1, 0);
        quantity2 = new SimpleTimeQuantity(2, 0);
        assertFalse(quantity1.equals(quantity2));

        quantity1 = new SimpleTimeQuantity(1, 0, true);
        quantity2 = new SimpleTimeQuantity(1, 0);
        assertFalse(quantity1.equals(quantity2));

        // Symmetric
        quantity1 = new SimpleTimeQuantity(0, 0);
        quantity2 = new SimpleTimeQuantity(0, 0);
        assertTrue(quantity1.equals(quantity2));
        assertTrue(quantity2.equals(quantity1));

        // Transitive
        quantity1 = new SimpleTimeQuantity(1, 0);
        quantity2 = new SimpleTimeQuantity(1, 0);
        quantity3 = new SimpleTimeQuantity(1, 0);
        assertTrue(quantity1.equals(quantity2));
        assertTrue(quantity2.equals(quantity3));
        assertTrue(quantity1.equals(quantity3));

    }

    public void testHashCode() {

        SimpleTimeQuantity quantity1;

        // Equal objects have equal hashcodes
        assertEquals(new SimpleTimeQuantity(0, 0).hashCode(), new SimpleTimeQuantity(0, 0).hashCode());

        // Same object returns same hashcode
        quantity1 = new SimpleTimeQuantity(1, 10);
        assertEquals(quantity1.hashCode(), quantity1.hashCode());

        // Unequal objects may or may not have unequal hascodes; cannot test
    }
}
