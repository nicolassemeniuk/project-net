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
|   $Revision: 17094 $
|       $Date: 2008-03-24 13:31:48 +0530 (Mon, 24 Mar 2008) $
|     $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>WorkingTime</code>.
 * @author Tim Morrow
 * @since Version 7.4
 */
public class WorkingTimeTest extends TestCase {

    public WorkingTimeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(WorkingTimeTest.class);

        return suite;
    }

    /**
     * Tests the constructor.
     */
    public void testWorkingTimeVariant1() {

        WorkingTime workingTime;

        // Success
        workingTime = new WorkingTime(0, 0, 0, 0);
        assertEquals(0, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(0, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(23, 59, 23, 59);
        assertEquals(23, workingTime.getStartTime().getHour());
        assertEquals(59, workingTime.getStartTime().getMinute());
        assertEquals(23, workingTime.getEndTime().getHour());
        assertEquals(59, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(11, 27, 19, 45);
        assertEquals(11, workingTime.getStartTime().getHour());
        assertEquals(27, workingTime.getStartTime().getMinute());
        assertEquals(19, workingTime.getEndTime().getHour());
        assertEquals(45, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(0, 0, 24, 0);
        assertEquals(0, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(24, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        // Failure
        workingTime = null;
        try {
            workingTime = new WorkingTime(0, 60, 0, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(workingTime);

        workingTime = null;
        try {
            workingTime = new WorkingTime(0, 0, 0, 60);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(workingTime);

        workingTime = null;
        try {
            workingTime = new WorkingTime(8, 0, 7, 59);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(workingTime);

        workingTime = null;
        try {
            workingTime = new WorkingTime(24, 0, 0, 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertNull(workingTime);

    }

    public void testWorkingTimeVariant2() {

        WorkingTime workingTime;

        // Success
        workingTime = new WorkingTime(new Time(0, 0), new Time(0, 0));
        assertEquals(0, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(0, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(new Time(23, 59), new Time(23, 59));
        assertEquals(23, workingTime.getStartTime().getHour());
        assertEquals(59, workingTime.getStartTime().getMinute());
        assertEquals(23, workingTime.getEndTime().getHour());
        assertEquals(59, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(new Time(24, 0), new Time(24, 0));
        assertEquals(24, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(24, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(new Time(11, 27), new Time(19, 45));
        assertEquals(11, workingTime.getStartTime().getHour());
        assertEquals(27, workingTime.getStartTime().getMinute());
        assertEquals(19, workingTime.getEndTime().getHour());
        assertEquals(45, workingTime.getEndTime().getMinute());

        workingTime = new WorkingTime(new Time(0, 0), new Time(24, 0));
        assertEquals(0, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(24, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        // Failure
        try {
            new WorkingTime((Time) null, (Time) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new WorkingTime(null, new Time(0, 0));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new WorkingTime(new Time(0, 0), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

    }

    public void testWorkingTimeVariant3() {

        WorkingTime workingTime;
        Calendar startDateCal;
        Calendar endDateCal;

        startDateCal = Calendar.getInstance(WorkingTime.TIME_CONVERSION_TIMEZONE);
        startDateCal.set(Calendar.SECOND, 0);
        startDateCal.set(Calendar.MILLISECOND, 0);
        endDateCal = (Calendar) startDateCal.clone();

        // Null checks
        try {
            new WorkingTime((Date) null, (Date) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new WorkingTime(new Date(), null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new WorkingTime(null, new Date());
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // End before start
        startDateCal.set(2003, Calendar.SEPTEMBER, 8, 8, 0);
        endDateCal.set(2003, Calendar.SEPTEMBER, 8, 7, 0);
        try {
            workingTime = new WorkingTime(startDateCal.getTime(), endDateCal.getTime());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Success

        startDateCal.set(2003, Calendar.SEPTEMBER, 8, 8, 0);
        endDateCal.set(2003, Calendar.SEPTEMBER, 8, 8, 0);
        workingTime = new WorkingTime(startDateCal.getTime(), endDateCal.getTime());
        assertEquals(8, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(8, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        startDateCal.set(2003, Calendar.SEPTEMBER, 8, 8, 0);
        endDateCal.set(2003, Calendar.SEPTEMBER, 8, 12, 0);
        workingTime = new WorkingTime(startDateCal.getTime(), endDateCal.getTime());
        assertEquals(8, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(12, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

        startDateCal.set(2003, Calendar.SEPTEMBER, 2, 13, 0);
        endDateCal.set(2003, Calendar.SEPTEMBER, 2, 17, 0);
        workingTime = new WorkingTime(startDateCal.getTime(), endDateCal.getTime());
        assertEquals(13, workingTime.getStartTime().getHour());
        assertEquals(0, workingTime.getStartTime().getMinute());
        assertEquals(17, workingTime.getEndTime().getHour());
        assertEquals(0, workingTime.getEndTime().getMinute());

    }
    public void testGetDuration() {

        WorkingTime workingTime;

        // Zero duration
        workingTime = new WorkingTime(0, 0, 0, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getDuration());

        workingTime = new WorkingTime(8, 0, 8, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getDuration());

        // Misc durations
        workingTime = new WorkingTime(0, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(12, 0), workingTime.getDuration());

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getDuration());

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getDuration());

        workingTime = new WorkingTime(0, 0, 23, 30);
        assertEquals(new SimpleTimeQuantity(23, 30), workingTime.getDuration());

        workingTime = new WorkingTime(12, 45, 13, 15);
        assertEquals(new SimpleTimeQuantity(0, 30), workingTime.getDuration());

        workingTime = new WorkingTime(12, 07, 13, 29);
        assertEquals(new SimpleTimeQuantity(1, 22), workingTime.getDuration());

        workingTime = new WorkingTime(0, 0, 24, 0);
        assertEquals(new SimpleTimeQuantity(24, 0), workingTime.getDuration());

        workingTime = new WorkingTime(12, 0, 24, 0);
        assertEquals(new SimpleTimeQuantity(12, 0), workingTime.getDuration());

        workingTime = new WorkingTime(23, 59, 24, 0);
        assertEquals(new SimpleTimeQuantity(0, 1), workingTime.getDuration());

    }

    public void testContainsTime() {

        WorkingTime workingTime;

        // Unsual case; start and end are equal time
        // No time is ever in this working time
        workingTime = new WorkingTime(0, 0, 0, 0);
        assertFalse(workingTime.containsTime(new Time(0, 0)));
        assertFalse(workingTime.containsTime(new Time(0, 1)));

        workingTime = new WorkingTime(8, 0, 8, 0);
        assertFalse(workingTime.containsTime(new Time(0, 0)));
        assertFalse(workingTime.containsTime(new Time(0, 1)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertTrue(workingTime.containsTime(new Time(8, 0)));
        assertTrue(workingTime.containsTime(new Time(10, 30)));
        assertTrue(workingTime.containsTime(new Time(11, 59)));
        assertFalse(workingTime.containsTime(new Time(12, 0)));

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertTrue(workingTime.containsTime(new Time(13, 0)));
        assertTrue(workingTime.containsTime(new Time(15, 30)));
        assertTrue(workingTime.containsTime(new Time(16, 59)));
        assertFalse(workingTime.containsTime(new Time(17, 0)));

        workingTime = new WorkingTime(13, 10, 13, 12);
        assertFalse(workingTime.containsTime(new Time(13, 9)));
        assertTrue(workingTime.containsTime(new Time(13, 10)));
        assertTrue(workingTime.containsTime(new Time(13, 11)));
        assertFalse(workingTime.containsTime(new Time(13, 12)));

        workingTime = new WorkingTime(13, 15, 14, 5);
        assertFalse(workingTime.containsTime(new Time(13, 0)));
        assertFalse(workingTime.containsTime(new Time(13, 4)));
        assertFalse(workingTime.containsTime(new Time(13, 5)));
        assertFalse(workingTime.containsTime(new Time(13, 14)));
        assertTrue(workingTime.containsTime(new Time(13, 15)));
        assertTrue(workingTime.containsTime(new Time(13, 30)));
        assertTrue(workingTime.containsTime(new Time(13, 45)));
        assertTrue(workingTime.containsTime(new Time(13, 59)));
        assertTrue(workingTime.containsTime(new Time(14, 0)));
        assertTrue(workingTime.containsTime(new Time(14, 1)));
        assertTrue(workingTime.containsTime(new Time(14, 4)));
        assertFalse(workingTime.containsTime(new Time(14, 5)));
        assertFalse(workingTime.containsTime(new Time(14, 6)));
        assertFalse(workingTime.containsTime(new Time(14, 10)));
        assertFalse(workingTime.containsTime(new Time(14, 15)));
        assertFalse(workingTime.containsTime(new Time(14, 30)));

        // Contains all times but 24:00
        workingTime = new WorkingTime(0, 0, 24, 0);
        for (int i = 0; i < (60*24); i++) {
            assertTrue(workingTime.containsTime(new Time((i / 60), (i % 60))));
        }
        assertFalse(workingTime.containsTime(new Time(24, 0)));

    }

    public void testStartAfter() {
        WorkingTime workingTime;

        workingTime = new WorkingTime(0, 0, 0, 0);
        assertFalse(workingTime.isStartAfter(new Time(0, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertTrue(workingTime.isStartAfter(new Time(7, 59)));
        assertFalse(workingTime.isStartAfter(new Time(8, 0)));
        assertFalse(workingTime.isStartAfter(new Time(12, 0)));
        assertFalse(workingTime.isStartAfter(new Time(17, 0)));

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertTrue(workingTime.isStartAfter(new Time(0, 0)));
        assertTrue(workingTime.isStartAfter(new Time(12, 59)));
        assertFalse(workingTime.isStartAfter(new Time(13, 0)));
        assertFalse(workingTime.isStartAfter(new Time(15, 30)));
        assertFalse(workingTime.isStartAfter(new Time(17, 0)));

        workingTime = new WorkingTime(13, 15, 14, 5);
        assertTrue(workingTime.isStartAfter(new Time(13, 0)));
        assertTrue(workingTime.isStartAfter(new Time(13, 5)));
        assertFalse(workingTime.isStartAfter(new Time(13, 15)));
        assertFalse(workingTime.isStartAfter(new Time(14, 15)));

        //
        // 24 hour time; start of working time is not after any times
        //

        workingTime = new WorkingTime(0, 0, 24, 0);
        for (int i = 0; i < (60 * 24); i++) {
            int hour = i / 60;
            int minute = i % 60;
            assertFalse(workingTime.isStartAfter(new Time(hour, minute)));
        }

    }

    public void testIsEndOnOrBefore() {

        WorkingTime workingTime;

        workingTime = new WorkingTime(0, 0, 0, 0);
        assertTrue(workingTime.isEndOnOrBefore(new Time(0, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertTrue(workingTime.isEndOnOrBefore(new Time(13, 0)));
        assertTrue(workingTime.isEndOnOrBefore(new Time(12, 0)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(11, 59)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(8, 0)));

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertTrue(workingTime.isEndOnOrBefore(new Time(17, 1)));
        assertTrue(workingTime.isEndOnOrBefore(new Time(17, 0)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(16, 59)));

        workingTime = new WorkingTime(13, 15, 14, 5);
        assertTrue(workingTime.isEndOnOrBefore(new Time(14, 15)));
        assertTrue(workingTime.isEndOnOrBefore(new Time(14, 5)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(14, 4)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(13, 15)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(13, 14)));
        assertFalse(workingTime.isEndOnOrBefore(new Time(0, 0)));

        //
        // 24 hour time; end is not on or before any times except 24:00
        //

        workingTime = new WorkingTime(0, 0, 24, 0);
        for (int i = 0; i < (60 * 24); i++) {
            int hour = i / 60;
            int minute = i % 60;
            assertFalse(workingTime.isEndOnOrBefore(new Time(hour, minute)));
        }
        assertTrue(workingTime.isEndOnOrBefore(new Time(24, 0)));

    }

    public void testGetRemainingDuration() {

        WorkingTime workingTime;

        workingTime = new WorkingTime(0, 0, 0, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getRemainingDuration(new Time(0, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getRemainingDuration(new Time(8, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), workingTime.getRemainingDuration(new Time(9, 0)));
        assertEquals(new SimpleTimeQuantity(3, 30), workingTime.getRemainingDuration(new Time(8, 30)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getRemainingDuration(new Time(11, 45)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getRemainingDuration(new Time(12, 0)));

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getRemainingDuration(new Time(13, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), workingTime.getRemainingDuration(new Time(14, 0)));
        assertEquals(new SimpleTimeQuantity(3, 30), workingTime.getRemainingDuration(new Time(13, 30)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getRemainingDuration(new Time(16, 45)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getRemainingDuration(new Time(17, 0)));

        workingTime = new WorkingTime(13, 30, 14, 15);
        assertEquals(new SimpleTimeQuantity(0, 45), workingTime.getRemainingDuration(new Time(13, 30)));
        assertEquals(new SimpleTimeQuantity(0, 30), workingTime.getRemainingDuration(new Time(13, 45)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getRemainingDuration(new Time(14, 0)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getRemainingDuration(new Time(14, 15)));

        // 0:00 - 24:00; can calculate remaining for all minutes in day
        workingTime = new WorkingTime(0, 0, 24, 0);
        for (int i = 0; i <= (60 * 24); i++) {
            // 24 hours minus the hours calculated from the loop counter
            BigDecimal calcRemaining = new BigDecimal("24").subtract(new BigDecimal(i).divide(new BigDecimal(60), 3, BigDecimal.ROUND_HALF_UP));
            assertEquals(new SimpleTimeQuantity(calcRemaining), workingTime.getRemainingDuration(new Time((i / 60), (i % 60))));
        }

    }

    public void testGetEarlierDuration() {

        WorkingTime workingTime;

        workingTime = new WorkingTime(0, 0, 0, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getEarlierDuration(new Time(0, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getEarlierDuration(new Time(12, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), workingTime.getEarlierDuration(new Time(11, 0)));
        assertEquals(new SimpleTimeQuantity(3, 30), workingTime.getEarlierDuration(new Time(11, 30)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getEarlierDuration(new Time(8, 15)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getEarlierDuration(new Time(8, 0)));

        workingTime = new WorkingTime(13, 0, 17, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getEarlierDuration(new Time(17, 0)));
        assertEquals(new SimpleTimeQuantity(3, 0), workingTime.getEarlierDuration(new Time(16, 0)));
        assertEquals(new SimpleTimeQuantity(3, 30), workingTime.getEarlierDuration(new Time(16, 30)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getEarlierDuration(new Time(13, 15)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getEarlierDuration(new Time(13, 0)));

        workingTime = new WorkingTime(13, 30, 14, 15);
        assertEquals(new SimpleTimeQuantity(0, 45), workingTime.getEarlierDuration(new Time(14, 15)));
        assertEquals(new SimpleTimeQuantity(0, 30), workingTime.getEarlierDuration(new Time(14, 0)));
        assertEquals(new SimpleTimeQuantity(0, 15), workingTime.getEarlierDuration(new Time(13, 45)));
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getEarlierDuration(new Time(13, 30)));

        workingTime = new WorkingTime(0, 0, 24, 0);
        for (int i = 0; i <= (60 * 24); i++) {
            // 24 hours minus the hours calculated from the loop counter
            BigDecimal calcRemaining = new BigDecimal(i).divide(new BigDecimal(60), 3, BigDecimal.ROUND_HALF_UP);
            assertEquals(new SimpleTimeQuantity(calcRemaining), workingTime.getEarlierDuration(new Time((i / 60), (i % 60))));
        }

    }

    public void testIsStartBetween() {

        WorkingTime workingTime1;
        WorkingTime workingTime2;

        // Success
        workingTime1 = new WorkingTime(0, 0, 0, 0);
        workingTime2 = new WorkingTime(0, 0, 0, 0);
        assertTrue(workingTime2.isStartBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 0, 9, 0);
        assertTrue(workingTime2.isStartBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 30, 8, 31);
        assertTrue(workingTime2.isStartBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 45, 10, 0);
        assertTrue(workingTime2.isStartBetween(workingTime1));

        // Failure
        workingTime1 = new WorkingTime(0, 0, 0, 0);
        workingTime2 = new WorkingTime(12, 0, 12, 0);
        assertFalse(workingTime2.isStartBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(7, 0, 9, 0);
        assertFalse(workingTime2.isStartBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(10, 0, 11, 0);
        assertFalse(workingTime2.isStartBetween(workingTime1));

    }

    public void testIsEndBetween() {

        WorkingTime workingTime1;
        WorkingTime workingTime2;

        // Success
        workingTime1 = new WorkingTime(0, 0, 0, 0);
        workingTime2 = new WorkingTime(0, 0, 0, 0);
        assertTrue(workingTime2.isEndBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 0, 9, 0);
        assertTrue(workingTime2.isEndBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 30, 8, 31);
        assertTrue(workingTime2.isEndBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(7, 0, 8, 30);
        assertTrue(workingTime2.isEndBetween(workingTime1));

        // Failure
        workingTime1 = new WorkingTime(0, 0, 0, 0);
        workingTime2 = new WorkingTime(12, 0, 12, 0);
        assertFalse(workingTime2.isEndBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(8, 30, 9, 30);
        assertFalse(workingTime2.isEndBetween(workingTime1));

        workingTime1 = new WorkingTime(8, 0, 9, 0);
        workingTime2 = new WorkingTime(10, 0, 11, 0);
        assertFalse(workingTime2.isEndBetween(workingTime1));

    }

    public void testIsStartBefore() {

        WorkingTime workingTime1;
        WorkingTime workingTime2;

        // Success
        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 11, 30);
        assertTrue(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 12, 30);
        assertTrue(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 13, 0);
        assertTrue(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 14, 0);
        assertTrue(workingTime2.isStartBefore(workingTime1));

        // Failure
        workingTime1 = new WorkingTime(0, 0, 12, 0);
        workingTime2 = new WorkingTime(0, 0, 12, 0);
        assertFalse(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(0, 0, 12, 0);
        workingTime2 = new WorkingTime(1, 0, 12, 0);
        assertFalse(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(0, 0, 12, 0);
        workingTime2 = new WorkingTime(12, 0, 13, 0);
        assertFalse(workingTime2.isStartBefore(workingTime1));

        workingTime1 = new WorkingTime(0, 0, 12, 0);
        workingTime2 = new WorkingTime(13, 0, 14, 0);
        assertFalse(workingTime2.isStartBefore(workingTime1));

    }

    public void testIsEndAfter() {

        WorkingTime workingTime1;
        WorkingTime workingTime2;

        // Success
        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(13, 0, 14, 0);
        assertTrue(workingTime2.isEndAfter(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(12, 30, 14, 0);
        assertTrue(workingTime2.isEndAfter(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(1, 0, 14, 0);
        assertTrue(workingTime2.isEndAfter(workingTime1));

        // Failure
        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(12, 0, 13, 0);
        assertFalse(workingTime2.isEndAfter(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 12, 30);
        assertFalse(workingTime2.isEndAfter(workingTime1));

        workingTime1 = new WorkingTime(12, 0, 13, 0);
        workingTime2 = new WorkingTime(11, 0, 11, 30);
        assertFalse(workingTime2.isEndAfter(workingTime1));

    }

    public void testGetEndTimeForWork() {

        WorkingTime workingTime;

        // Success
        workingTime = new WorkingTime(0, 0, 0, 0);
        assertEquals(new Time(0, 0), workingTime.getEndTimeForWork(new SimpleTimeQuantity(0, 0)));

        workingTime = new WorkingTime(0, 0, 12, 0);
        assertEquals(new Time(12, 0), workingTime.getEndTimeForWork(new SimpleTimeQuantity(12, 0)));

        workingTime = new WorkingTime(0, 0, 24, 0);
        assertEquals(new Time(24, 0), workingTime.getEndTimeForWork(new SimpleTimeQuantity(24, 0)));

        workingTime = new WorkingTime(8, 0, 9, 0);
        assertEquals(new Time(9, 0), workingTime.getEndTimeForWork(new SimpleTimeQuantity(1, 0)));

        workingTime = new WorkingTime(8, 0, 9, 0);
        assertEquals(new Time(8, 30), workingTime.getEndTimeForWork(new SimpleTimeQuantity(0, 30)));

        workingTime = new WorkingTime(8, 0, 9, 10);
        assertEquals(new Time(8, 1), workingTime.getEndTimeForWork(new SimpleTimeQuantity(0, 1)));

        // Failure
        workingTime = new WorkingTime(0, 0, 0, 0);
        try {
            workingTime.getEndTimeForWork(null);
            fail("Expected NullPointerException");
        } catch (Exception e) {
            // Expected
        }

        workingTime = new WorkingTime(0, 0, 0, 0);
        try {
            workingTime.getEndTimeForWork(new SimpleTimeQuantity(1, 0));
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            // Expected
        }

        workingTime = new WorkingTime(8, 0, 9, 0);
        try {
            workingTime.getEndTimeForWork(new SimpleTimeQuantity(1, 1));
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            // Expected
        }

        // Test Specific Issues

        // Issue: 2.666 is 2 hours 39.96 minutes, rounds to 2 hours 40 minutes
        // Previously it was being truncated to 2 hours 39 minutes
        // Resulting in a time 1 minute short
        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new Time(10, 40), workingTime.getEndTimeForWork(new SimpleTimeQuantity(2, 40)));

    }

    public void testGetIntersectingHours() {

        WorkingTime workingTime;

        // None
        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getIntersectingHours(new WorkingTime(6, 0, 8, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(0, 0), workingTime.getIntersectingHours(new WorkingTime(12, 0, 13, 0)));

        // Starts in
        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(2, 0), workingTime.getIntersectingHours(new WorkingTime(10, 0, 14, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(2, 0), workingTime.getIntersectingHours(new WorkingTime(10, 0, 12, 0)));

        // Ends in
        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(2, 0), workingTime.getIntersectingHours(new WorkingTime(6, 0, 10, 0)));

        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(2, 0), workingTime.getIntersectingHours(new WorkingTime(8, 0, 10, 0)));

        // Entirely within
        workingTime = new WorkingTime(0, 0, 24, 0);
        assertEquals(new SimpleTimeQuantity(24, 0), workingTime.getIntersectingHours(new WorkingTime(0, 0, 24, 0)));

        workingTime = new WorkingTime(0, 0, 24, 0);
        assertEquals(new SimpleTimeQuantity(2, 0), workingTime.getIntersectingHours(new WorkingTime(10, 0, 12, 0)));

        // Span all
        workingTime = new WorkingTime(8, 0, 12, 0);
        assertEquals(new SimpleTimeQuantity(4, 0), workingTime.getIntersectingHours(new WorkingTime(6, 0, 14, 0)));



    }
}
