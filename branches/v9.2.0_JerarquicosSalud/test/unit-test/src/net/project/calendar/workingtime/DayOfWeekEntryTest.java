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

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class DayOfWeekEntryTest extends TestCase {

    public DayOfWeekEntryTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DayOfWeekEntryTest.class);

        return suite;
    }

    public void testAssertValidDayNumber() {

        int dayNumber;

        //
        // Failures
        //

        try {
            dayNumber = -1;
            DayOfWeekEntry.assertValidDayNumber(dayNumber);
            fail("Unexpected success with day number " + dayNumber);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            dayNumber = 0;
            DayOfWeekEntry.assertValidDayNumber(dayNumber);
            fail("Unexpected success with day number " + dayNumber);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            dayNumber = 8;
            DayOfWeekEntry.assertValidDayNumber(dayNumber);
            fail("Unexpected success with day number " + dayNumber);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        //
        // Successes
        //

        for (int i = 1; i <= 7; i++) {
            try {
                DayOfWeekEntry.assertValidDayNumber(i);
            } catch (IllegalArgumentException e) {
                fail("Expected " + i + " to be a valid day number, but it was not");
            }
        }

    }
    /**
     * Tests constructor.
     */
    public void testDayOfWeekEntry() {

        DayOfWeekEntry day;

        day = null;
        try {
            day = new DayOfWeekEntry(0, false, true);
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(day);

        day = new DayOfWeekEntry(Calendar.SUNDAY, false, true);
        assertEquals(1, day.getDayNumber());
        assertFalse(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        assertEquals(2, day.getDayNumber());
        assertTrue(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.TUESDAY, true, true);
        assertEquals(3, day.getDayNumber());
        assertTrue(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.WEDNESDAY, true, true);
        assertEquals(4, day.getDayNumber());
        assertTrue(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.THURSDAY, true, true);
        assertEquals(5, day.getDayNumber());
        assertTrue(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.FRIDAY, true, true);
        assertEquals(6, day.getDayNumber());
        assertTrue(day.isWorkingDay());

        day = new DayOfWeekEntry(Calendar.SATURDAY, false, true);
        assertEquals(7, day.getDayNumber());
        assertFalse(day.isWorkingDay());

    }

    public void testToString() {
        DayOfWeekEntry day;

        day = new DayOfWeekEntry(Calendar.SUNDAY, false, true);
        assertNotNull(day.toString());
    }

    public void testEqualsHashcode() {

        DayOfWeekEntry day1;
        DayOfWeekEntry day2;

        // Test Equality

        // Instance Equality
        day1 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        assertTrue(day1.equals(day1));

        // Regular equality
        day1 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        day2 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        assertTrue(day1.equals(day2));
        assertEquals(day1.hashCode(), day2.hashCode());

        // Test inequality

        // Wrong class
        day1 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        assertFalse(day1.equals(new Object()));

        // Regular inequality
        day1 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        day2 = new DayOfWeekEntry(Calendar.TUESDAY, true, true);
        assertFalse(day1.equals(day2));

        day1 = new DayOfWeekEntry(Calendar.MONDAY, true, true);
        day2 = new DayOfWeekEntry(Calendar.MONDAY, false, true);
        assertFalse(day1.equals(day2));

    }


}
