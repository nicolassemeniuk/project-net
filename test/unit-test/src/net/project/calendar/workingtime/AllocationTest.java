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
|    $RCSfile$
|   $Revision: 15404 $
|       $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|     $Author: deepak $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class AllocationTest extends TestCase {

    public AllocationTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AllocationTest.class);

        return suite;
    }

    /**
     */
    public void testGetActualTimeRemaining() {

        Allocation alloc;

        // 100%
        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("0"));
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-0"));
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("16"));
        assertEquals(new SimpleTimeQuantity(16, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-8"));
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-16"));
        assertEquals(new SimpleTimeQuantity(16, 0), alloc.getActualTimeRemaining());

        // 200%
        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(4, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("16"));
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("-8"));
        assertEquals(new SimpleTimeQuantity(4, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("-16"));
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        // Fractional
        alloc = new Allocation(new BigDecimal("0.5"), new BigDecimal("32"));
        assertEquals(new SimpleTimeQuantity(64, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("-32"));
        assertEquals(new SimpleTimeQuantity(128, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("0.1"), new BigDecimal("200"));
        assertEquals(new SimpleTimeQuantity(2000, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("0.1"), new BigDecimal("4.571"));
        assertEquals(new SimpleTimeQuantity(45, 43), alloc.getActualTimeRemaining());

        // More complex fractions
        alloc = new Allocation(new BigDecimal("0.1"), new BigDecimal("4.571"));
        assertEquals(new SimpleTimeQuantity(45, 43), alloc.getActualTimeRemaining());

        // Test 0.000 to 0.999
        BigDecimal factor = new BigDecimal("0.1");
        for (int i = 0; i < 1000; i++) {
            BigDecimal work = new BigDecimal(i).divide(new BigDecimal("1000"), 3, BigDecimal.ROUND_HALF_UP);
            BigDecimal expectedTime = work.divide(factor, 3, BigDecimal.ROUND_HALF_UP);
            alloc = new Allocation(factor, work);
            assertEquals(new SimpleTimeQuantity(expectedTime), alloc.getActualTimeRemaining());
        }

    }

    public void testIsPositive() {

        Allocation alloc;

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("8"));
        assertTrue(alloc.isPositive());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("16"));
        assertTrue(alloc.isPositive());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-8"));
        assertFalse(alloc.isPositive());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("-16"));
        assertFalse(alloc.isPositive());

        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("16"));
        assertTrue(alloc.isPositive());

        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("-16"));
        assertFalse(alloc.isPositive());

    }

    public void testIsWorkRemaining() {

        Allocation alloc;

        // 100%
        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("8"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-8"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("0"));
        assertFalse(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("-0"));
        assertFalse(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("8"));
        alloc.zeroHoursRemaining();
        assertFalse(alloc.isWorkRemaining());

        // 200%
        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("16"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("-16"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("2"), new BigDecimal("-16"));
        alloc.zeroHoursRemaining();
        assertFalse(alloc.isWorkRemaining());

        // Fraction
        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("16"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("-16"));
        assertTrue(alloc.isWorkRemaining());

        alloc = new Allocation(new BigDecimal("0.25"), new BigDecimal("16"));
        alloc.zeroHoursRemaining();
        assertFalse(alloc.isWorkRemaining());

    }

    public void testZeroHoursRemaining() {

        Allocation alloc;

        alloc = new Allocation(new BigDecimal("1"), new BigDecimal("8"));
        alloc.zeroHoursRemaining();
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(new BigDecimal("2.5"), new BigDecimal("100"));
        alloc.zeroHoursRemaining();
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

    }

    public void testSubtractActualWeeksRemaining() {

        Allocation alloc;
        BigDecimal result;
        BigDecimal factor;
        BigDecimal hoursInWeek;

        //
        // 100%
        //

        factor = new BigDecimal("1");
        hoursInWeek = new BigDecimal("40");

        // 8 hours = 0 weeks, 8 hours remain
        alloc = new Allocation(factor, new BigDecimal("8"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("0"), result);
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        // 16 hours = 0 weeks, 16 hours remain
        alloc = new Allocation(factor, new BigDecimal("16"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("0"), result);
        assertEquals(new SimpleTimeQuantity(16, 0), alloc.getActualTimeRemaining());

        // 40 hours = 1 weeks, 0 hours remain
        alloc = new Allocation(factor, new BigDecimal("40"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        // 41 hours = 1 weeks, 1 hours remain
        alloc = new Allocation(factor, new BigDecimal("41"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(1, 0), alloc.getActualTimeRemaining());

        // 50 hours = 1 weeks, 10 hours remain
        alloc = new Allocation(factor, new BigDecimal("50"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(10, 0), alloc.getActualTimeRemaining());

        // 80 hours = 2 weeks, 0 hours remain
        alloc = new Allocation(factor, new BigDecimal("80"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("2"), result);
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        // 100 hours = 2 weeks, 20 hours remain
        alloc = new Allocation(factor, new BigDecimal("100"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("2"), result);
        assertEquals(new SimpleTimeQuantity(20, 0), alloc.getActualTimeRemaining());

        // 1005 hours = 25 weeks, 5 hours remain
        alloc = new Allocation(factor, new BigDecimal("1005"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("25"), result);
        assertEquals(new SimpleTimeQuantity(5, 0), alloc.getActualTimeRemaining());

        //
        // 200%
        //
        factor = new BigDecimal("2");
        hoursInWeek = new BigDecimal("40");

        // 8 hours = 0 weeks, 4 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("8"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("0"), result);
        assertEquals(new SimpleTimeQuantity(4, 0), alloc.getActualTimeRemaining());

        // 40 hours = 0 weeks, 20 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("40"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("0"), result);
        assertEquals(new SimpleTimeQuantity(20, 0), alloc.getActualTimeRemaining());

        // 80 hours = 1 weeks, 0 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("80"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        // 81 hours = 1 weeks, 0.5 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("81"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(0, 30), alloc.getActualTimeRemaining());

        //
        // 25%
        //
        factor = new BigDecimal("0.25");
        hoursInWeek = new BigDecimal("40");

        // 8 hours = 0 weeks, 32 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("8"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("0"), result);
        assertEquals(new SimpleTimeQuantity(32, 0), alloc.getActualTimeRemaining());

        // 10 hours = 1 week, 0 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("10"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        // 20 hours = 2 weeks, 0 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("20"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("2"), result);
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        // 12 hours = 1 weeks, 8 actual hours remain
        alloc = new Allocation(factor, new BigDecimal("12"));
        result = alloc.subtractActualWeeksRemaining(hoursInWeek);
        assertEquals(new BigDecimal("1"), result);
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

    }

    public void testSubtractActualHoursRemaining() {

        Allocation alloc;
        BigDecimal factor;

        // 100%
        factor = new BigDecimal("1");

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("0"));
        assertEquals(new SimpleTimeQuantity(8, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        assertEquals(new SimpleTimeQuantity(7, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("120"));
        alloc.subtractActualHoursRemaining(new BigDecimal("100"));
        assertEquals(new SimpleTimeQuantity(20, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("120"));
        alloc.subtractActualHoursRemaining(new BigDecimal("20"));
        alloc.subtractActualHoursRemaining(new BigDecimal("30"));
        alloc.subtractActualHoursRemaining(new BigDecimal("49"));
        assertEquals(new SimpleTimeQuantity(21, 0), alloc.getActualTimeRemaining());

        // 200%
        factor = new BigDecimal("2");

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("0"));
        assertEquals(new SimpleTimeQuantity(4, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        assertEquals(new SimpleTimeQuantity(3, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(4, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        assertEquals(new SimpleTimeQuantity(1, 0), alloc.getActualTimeRemaining());

        // Fraction
        factor = new BigDecimal("0.25");

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("0"));
        assertEquals(new SimpleTimeQuantity(32, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("1"));
        assertEquals(new SimpleTimeQuantity(31, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("8"));
        alloc.subtractActualHoursRemaining(new BigDecimal("2"));
        assertEquals(new SimpleTimeQuantity(30, 0), alloc.getActualTimeRemaining());

        alloc = new Allocation(factor, new BigDecimal("55"));
        alloc.subtractActualHoursRemaining(new BigDecimal("2"));
        alloc.subtractActualHoursRemaining(new BigDecimal("3"));
        alloc.subtractActualHoursRemaining(new BigDecimal("4"));
        assertEquals(new SimpleTimeQuantity(211, 0), alloc.getActualTimeRemaining());

        //
        // Misc
        //

        // Consider two assignments; 25% and 35% on a 16 hour task; each
        // assignee is given 6.667 and 9.333 hours of work respectively
        factor = new BigDecimal("0.35");
        alloc = new Allocation(factor, new BigDecimal("9.333"));
        alloc.subtractActualHoursRemaining(new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(18, 40), alloc.getActualTimeRemaining());
        alloc.subtractActualHoursRemaining(new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(10, 40), alloc.getActualTimeRemaining());
        alloc.subtractActualHoursRemaining(new BigDecimal("8"));
        assertEquals(new SimpleTimeQuantity(2, 40), alloc.getActualTimeRemaining());
        alloc.subtractActualHoursRemaining(new BigDecimal("2.666"));
        assertEquals(new SimpleTimeQuantity(0, 0), alloc.getActualTimeRemaining());


    }
}
