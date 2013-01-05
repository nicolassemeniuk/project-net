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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>TimeTest</code>.
 * @author
 * @since
 */
public class TimeTest extends TestCase {

    public TimeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TimeTest.class);

        return suite;
    }

    /**
     * Tests constructor.
     */
    public void testTime() {

        try {
            new Time(-1, -1);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        try {
            new Time(-1, 70);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        try {
            new Time(-1, 15);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        try {
            new Time(12, -1);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        try {
            new Time(12, 70);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        try {
            new Time(24, 1);
            fail("Unexpected success");
        } catch (IllegalArgumentException e) {
            // Success
        }

        // Now test successes
        Time t;

        t = new Time(0, 0);
        assertEquals(0, t.getHour());
        assertEquals(0, t.getMinute());

        t = new Time(0, 15);
        assertEquals(0, t.getHour());
        assertEquals(15, t.getMinute());

        t = new Time(0, 59);
        assertEquals(0, t.getHour());
        assertEquals(59, t.getMinute());

        t = new Time(12, 30);
        assertEquals(12, t.getHour());
        assertEquals(30, t.getMinute());

        t = new Time(23, 59);
        assertEquals(23, t.getHour());
        assertEquals(59, t.getMinute());

        t = new Time(24, 0);
        assertEquals(24, t.getHour());
        assertEquals(0, t.getMinute());

    }

    /**
     * Test calculateDuration.
     */
    public void testCalculateDuration() {

        Time startTime;
        Time endTime;

        startTime = new Time(23, 59);
        endTime = new Time(23, 59);
        assertEquals(new SimpleTimeQuantity(0, 0), startTime.calculateDuration(endTime));

        startTime = new Time(0, 0);
        endTime = new Time(23, 59);
        assertEquals(new SimpleTimeQuantity(23, 59), startTime.calculateDuration(endTime));

        startTime = new Time(1, 15);
        endTime = new Time(2, 15);
        assertEquals(new SimpleTimeQuantity(1, 0), startTime.calculateDuration(endTime));

        startTime = new Time(1, 15);
        endTime = new Time(23, 58);
        assertEquals(new SimpleTimeQuantity(22, 43), startTime.calculateDuration(endTime));

        startTime = new Time(0, 0);
        endTime = new Time(24, 0);
        assertEquals(new SimpleTimeQuantity(24, 0), startTime.calculateDuration(endTime));

        startTime = new Time(8, 0);
        endTime = new Time(24, 0);
        assertEquals(new SimpleTimeQuantity(16, 0), startTime.calculateDuration(endTime));

        startTime = new Time(17, 0);
        endTime = new Time(24, 0);
        assertEquals(new SimpleTimeQuantity(7, 0), startTime.calculateDuration(endTime));

        startTime = new Time(23, 58);
        endTime = new Time(24, 0);
        assertEquals(new SimpleTimeQuantity(0, 2), startTime.calculateDuration(endTime));

    }

    public void testEquals() {

        Time t;

        // Positives
        t = new Time(0, 0);
        assertTrue(t.equals(new Time(0, 0)));

        t = new Time(23, 59);
        assertTrue(t.equals(new Time(23, 59)));

        t = new Time(12, 17);
        assertTrue(t.equals(new Time(12, 17)));

        // Negatives
        t = new Time(0, 0);
        assertFalse(t.equals(new Time(12, 12)));

        t = new Time(12, 30);
        assertFalse(t.equals(new Time(0, 30)));

        t = new Time(23, 59);
        assertFalse(t.equals(new Time(11, 59)));

    }
    public void testIsOnOrAfter() {

        Time time1;
        Time time2;

        // Test positives
        time1 = new Time(0, 0);
        time2 = new Time(0, 0);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(0, 1);
        time2 = new Time(0, 0);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(23, 59);
        time2 = new Time(0, 0);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(13, 01);
        time2 = new Time(12, 59);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(18, 30);
        time2 = new Time(17, 31);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(17, 23);
        time2 = new Time(17, 23);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(24, 0);
        time2 = new Time(23, 59);
        assertTrue(time1.isOnOrAfter(time2));

        time1 = new Time(24, 0);
        time2 = new Time(24, 0);
        assertTrue(time1.isOnOrAfter(time2));

        // Test negatives
        time1 = new Time(17, 30);
        time2 = new Time(17, 31);
        assertFalse(time1.isOnOrAfter(time2));

        time1 = new Time(23, 58);
        time2 = new Time(23, 59);
        assertFalse(time1.isOnOrAfter(time2));

        time1 = new Time(10, 59);
        time2 = new Time(11, 01);
        assertFalse(time1.isOnOrAfter(time2));

        time1 = new Time(23, 59);
        time2 = new Time(24, 0);
        assertFalse(time1.isOnOrAfter(time2));

    }

    public void testIsAfter() {

        Time time1;
        Time time2;

        // Test positives
        time1 = new Time(0, 1);
        time2 = new Time(0, 0);
        assertTrue(time1.isAfter(time2));

        time1 = new Time(23, 59);
        time2 = new Time(0, 0);
        assertTrue(time1.isAfter(time2));

        time1 = new Time(17, 0);
        time2 = new Time(16, 59);
        assertTrue(time1.isAfter(time2));

        time1 = new Time(24, 0);
        time2 = new Time(0, 0);
        assertTrue(time1.isAfter(time2));

        time1 = new Time(24, 0);
        time2 = new Time(23, 59);
        assertTrue(time1.isAfter(time2));

        // Test negatives
        time1 = new Time(0, 0);
        time2 = new Time(0, 1);
        assertFalse(time1.isAfter(time2));

        time1 = new Time(0, 0);
        time2 = new Time(23, 59);
        assertFalse(time1.isAfter(time2));

        time1 = new Time(15, 31);
        time2 = new Time(16, 01);
        assertFalse(time1.isAfter(time2));

        time1 = new Time(24, 0);
        time2 = new Time(24, 0);
        assertFalse(time1.isAfter(time2));

    }

    public void testIsOnOrBefore() {

        Time time1;
        Time time2;

        // Test positives
        time1 = new Time(0, 0);
        time2 = new Time(0, 1);
        assertTrue(time1.isOnOrBefore(time2));

        time1 = new Time(0, 0);
        time2 = new Time(0, 0);
        assertTrue(time1.isOnOrBefore(time2));

        time1 = new Time(23, 58);
        time2 = new Time(23, 59);
        assertTrue(time1.isOnOrBefore(time2));

        time1 = new Time(11, 59);
        time2 = new Time(12, 00);
        assertTrue(time1.isOnOrBefore(time2));

        time1 = new Time(24, 0);
        time2 = new Time(24, 0);
        assertTrue(time1.isOnOrBefore(time2));

        // Test negatives
        time1 = new Time(23, 01);
        time2 = new Time(0, 59);
        assertFalse(time1.isOnOrBefore(time2));

        time1 = new Time(17, 32);
        time2 = new Time(17, 31);
        assertFalse(time1.isOnOrBefore(time2));

        time1 = new Time(24, 0);
        time2 = new Time(23, 59);
        assertFalse(time1.isOnOrBefore(time2));

    }
    public void testIsBefore() {

        Time time1;
        Time time2;

        // Test positives
        time1 = new Time(0, 0);
        time2 = new Time(0, 1);
        assertTrue(time1.isBefore(time2));

        time1 = new Time(23, 58);
        time2 = new Time(23, 59);
        assertTrue(time1.isBefore(time2));

        time1 = new Time(11, 59);
        time2 = new Time(12, 00);
        assertTrue(time1.isBefore(time2));

        time1 = new Time(23, 59);
        time2 = new Time(24, 0);
        assertTrue(time1.isBefore(time2));

        // Test negatives
        time1 = new Time(13, 17);
        time2 = new Time(13, 17);
        assertFalse(time1.isBefore(time2));

        time1 = new Time(23, 01);
        time2 = new Time(0, 59);
        assertFalse(time1.isBefore(time2));

        time1 = new Time(17, 32);
        time2 = new Time(17, 31);
        assertFalse(time1.isBefore(time2));

        time1 = new Time(24, 0);
        time2 = new Time(24, 0);
        assertFalse(time1.isBefore(time2));

    }
}
