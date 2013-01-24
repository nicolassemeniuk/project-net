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
 * Tests <code>AggregatedWorkingTimesTest</code>.
 * @author
 * @since
 */
public class AggregatedWorkingTimesTest extends TestCase {

    public AggregatedWorkingTimesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AggregatedWorkingTimesTest.class);

        return suite;
    }

    /**
     * Tests {@link AggregatedWorkingTimes#getDuration} with no overlapping working times.
     */
    public void testGetDuration() {

        AggregatedWorkingTimes timesWorked;

        // Empty
        timesWorked = new AggregatedWorkingTimes();
        assertEquals(new SimpleTimeQuantity(0, 0), timesWorked.getDuration());

        // Default working times
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 12, 0));
        timesWorked.add(new WorkingTime(13, 0, 17, 0));
        assertEquals(new SimpleTimeQuantity(8, 0), timesWorked.getDuration());

        // 24 hour
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(0, 0, 24, 0));
        assertEquals(new SimpleTimeQuantity(24, 0), timesWorked.getDuration());

        // Nightshift
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(0, 0, 3, 0));
        timesWorked.add(new WorkingTime(4, 0, 8, 0));
        timesWorked.add(new WorkingTime(23, 0, 24, 0));
        assertEquals(new SimpleTimeQuantity(8, 0), timesWorked.getDuration());

        // One minute
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 8, 1));
        assertEquals(new SimpleTimeQuantity(0, 1), timesWorked.getDuration());

        // Sequential times
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(9, 0, 10, 0));
        assertEquals(new SimpleTimeQuantity(2, 0), timesWorked.getDuration());

    }

    /**
     * Tests {@link AggregatedWorkingTimes#getDuration} with overlapping working times.
     */
    public void testGetDurationOverlapped() {

        AggregatedWorkingTimes timesWorked;

        // 8:00 - 9:00, 8:00 - 9:00; 1 hour worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        assertEquals(new SimpleTimeQuantity(1, 0), timesWorked.getDuration());

        // 8:00 - 9:00, 8:00 - 8:30; 1 hour worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(8, 0, 8, 30));
        assertEquals(new SimpleTimeQuantity(1, 0), timesWorked.getDuration());

        // 8:00 - 9:00, 8:30 - 8:45; 1 hour worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(8, 30, 8, 45));
        assertEquals(new SimpleTimeQuantity(1, 0), timesWorked.getDuration());

        // 8:00 - 9:00, 8:30 - 9:0; 1 hour worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(8, 30, 9, 0));
        assertEquals(new SimpleTimeQuantity(1, 0), timesWorked.getDuration());

        // 8:30 - 8:31, 8:00 - 9:00; 1 hour worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 30, 8, 31));
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        assertEquals(new SimpleTimeQuantity(1, 0), timesWorked.getDuration());

        // Span all
        // 8:00 - 9:00, 10:00 - 11:00, 7:00 - 12:00; 5 hours worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(10, 0, 11, 0));
        timesWorked.add(new WorkingTime(7, 0, 12, 0));
        assertEquals(new SimpleTimeQuantity(5, 0), timesWorked.getDuration());

        // Span partial, all
        // 8:00 - 9:00, 10:00 - 11:00, 12:00 - 13:00, 7:00 - 14:00; 7 hours worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 0, 9, 0));
        timesWorked.add(new WorkingTime(10, 0, 11, 0));
        timesWorked.add(new WorkingTime(12, 0, 13, 0));
        timesWorked.add(new WorkingTime(7, 0, 14, 0));
        assertEquals(new SimpleTimeQuantity(7, 0), timesWorked.getDuration());

        // Span all
        // 0:00 - 24:00, 0:00 - 24:00; 24 hours worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(0, 0, 24, 0));
        timesWorked.add(new WorkingTime(0, 0, 24, 0));
        assertEquals(new SimpleTimeQuantity(24, 0), timesWorked.getDuration());

        // Complex
        // 8:01 - 8:02, 9:01 - 9:13, 9:05 - 9:15; 15 minutes worked
        timesWorked = new AggregatedWorkingTimes();
        timesWorked.add(new WorkingTime(8, 1, 8, 2));
        timesWorked.add(new WorkingTime(9, 1, 9, 13));
        timesWorked.add(new WorkingTime(9, 5, 9, 15));
        assertEquals(new SimpleTimeQuantity(0, 15), timesWorked.getDuration());

    }

}
