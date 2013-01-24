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
 package net.project.schedule.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.DayOfYear;
import net.project.calendar.workingtime.Time;
import net.project.calendar.workingtime.WorkingTime;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.calendar.workingtime.WorkingTimeCalendarEntry;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryDateCalculatorTest;
import net.project.schedule.ScheduleEntryWorkingTimeCalendarTest;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.test.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

/**
 * Tests {@link ScheduleEntryDurationModifier}.
 * 
 * @author Tim Morrow
 */
public class ScheduleEntryDurationModifierTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryDurationModifierTest(String s) {
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
        TestSuite suite = new TestSuite(ScheduleEntryDurationModifierTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork(net.project.util.TimeQuantity)} with a null
     * duration value.
     */
    public void testCalculateWorkNullDuration() {
        try {
            new ScheduleEntryDurationModifier(new Task(), new TestWorkingTimeCalendarProvider()).calculateWork(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork} with a
     * new (empty) task.
     */
    public void testCalculateWorkNewTask() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;

        provider = new TestWorkingTimeCalendarProvider();

        //
        // Empty Task with no resource
        // Changing duration should not change work
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 0 hours
        // Empty assignments
        // Expected Start Date: null
        // Expected End Date: null
        scheduleEntry = new Task();

        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertNull(scheduleEntry.getStartTime());
        assertNull(scheduleEntry.getEndTime());
    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork} with increased
     * durations and assignments with default working time calendars.
     */
    public void testCalculateWorkIncreasedDuration() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;


        provider = new TestWorkingTimeCalendarProvider();

        //
        // Task with no resource
        // Changing duration should not change work (unless changing it to zero)
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 8 hours
        // Empty assignments
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTaskNoAssignment();
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Update to 0 days
        // Expected Duration: 0 days
        // Expected Work: 0 hours
        // Empty assignments
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 8:00 AM
        scheduleEntry = new Helper().makeTaskNoAssignment();
        expectedDuration = new TimeQuantity(0, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(0, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getEndTime());

        //
        // Task with 1 resource @ 100%
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 16 hours
        // Assignment 1 Work: 16 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask1();
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Update to 3 days
        // Expected Duration: 3 days
        // Expected Work: 24 hours
        // Assignment 1 Work: 24 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/7/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask1();
        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/7/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 8hr Task with 1 resources @ 50%
        //

        // Update to 4 days
        // Expected Duration: 4 days
        // Expected Work: 16 hours
        // Assignment 1 Work: 16 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/8/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask2();
        expectedDuration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(4, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/8/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 16hr task with 2 resources @ 100%
        //

        // Update to 3 days
        // Expected Duration: 3 days
        // Expected Work: 48 hours
        // Assignment 1 work: 24 hours
        // Assignment 2 work: 24 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/7/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask3();
        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(48, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = expectedAssignmentWork1;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/7/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // Check duration units are honored
        //

        // Task with 1 resource @ 100%
        // Update to 1 week
        // Expected Duration: 1 week
        // Expected Work: 40 hours
        // Assignment 1 Work: 40 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/9/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask1();
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.WEEK);
        expectedWork = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(1, TimeQuantityUnit.WEEK));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/9/04 5:00 PM"), scheduleEntry.getEndTime());

    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork} with assignments with custom
     * working time calendars.
     */
    public void testCalculateWorkIncreasedDurationCustomWorkingTime() {

        Helper helper;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        Date expectedEndDate;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        //
        // Task 4 with 2 resources @ 100%
        // 16 hrs of work
        // Currently a 2 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th
        // R1:   X         8
        // R2:   8
        //
        // Increase Duration to 3 days
        // Expected
        //     Mon 5th  Tues 6th  Wed 7th
        // R1:   X         8        8
        // R2:   8
        // All additional work assigned to R1
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/05/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SATURDAY, Calendar.SUNDAY});


        // Increase Duration to 3 days
        // Expected Duration: 3 days
        // Expected Work: 24 hours
        // Expected End Date: Wednesday April 7th @ 5:00 PM
        // Assignment 1 Work: 16 hours
        // Assignment 2 Work: 8 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/07/04 5:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());

        //
        // Task 4 with 2 resources @ 100%
        // 16 hrs of work
        // Currently a 2 day task because of working time:
        //     Mon 5th  Tues 6th
        // R1:   X         8
        // R2:   8         X
        //
        // Increase Duration to 3 days
        // Expected
        //     Mon 5th  Tues 6th  Wed 7th
        // R1:   X         8        8
        // R2:   8         X        8
        // Both resources receive 8 hours because both their next working times
        // are on same day
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/05/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04"}, null);

        // Increase Duration to 3 days
        // Expected Duration: 3 days
        // Expected Work: 32 hours
        // Expected End Date: Wednesday April 7th @ 5:00 PM
        // Assignment 1 Work: 16 hours
        // Assignment 2 Work: 16 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/07/04 5:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(16, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());

        //
        // Task 4 with 2 resources @ 100%
        // 16 hrs of work
        // Currently a 2 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   X         8                  X
        // R2:   8         X
        //
        // Increase Duration to 4 days
        // Expected
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   X         8        8         X
        // R2:   8         x        8         8
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/05/04", "04/08/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04"}, null);

        // Increase Duration to 4 days
        // Expected Duration: 4 days
        // Expected Work: 40 hours
        // Expected End Date: Thursday April 8th @ 5:00 PM
        // Assignment 1 Work: 16 hours
        // Assignment 2 Work: 24 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/08/04 5:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(24, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(4, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());

        //
        // Task 4 with 2 resources @ 100%
        // 32 hrs of work
        // Currently a 3 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         X         8         X
        // R2:   8         8         X
        //
        // Increase Duration to 4 days
        // Expected
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         X         8         X
        // R2:   8         8         X         8  <-- Fills in here to avoid "split task"
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04", "04/08/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/07/04"}, null);

        // Increase Duration to 4 days
        // Expected Duration: 4 days
        // Expected Work: 40 hours
        // Expected End Date: Thursday April 8th @ 5:00 PM
        // Assignment 1 Work: 16 hours
        // Assignment 2 Work: 24 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask8(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/07/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/08/04 5:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(24, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(4, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

        //
        // Task 4 with 2 resources @ 100%
        // 16 hrs of work
        // Currently a 1 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8        (8)         (12)      X     (12)
        // R2:   8        (8)           X      (20)    (8)
        //
        // Increase Duration to 5 days
        // Expected.  Notice that work is reduced by less than a whole day on Thursday 8th
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         8        12         X        (12)
        // R2:   8         8        X         12/(20)    (8)
        calendarDef1 = makeSpecialCalendar1();
        calendarDef2 = makeSpecialCalendar2();

        // Increase Duration to 5 days
        // Expected Duration: 5 days
        // Expected Work: 56 hours
        // Expected End Date: Thursday April 8th @ 3:00 PM
        // Assignment 1 Work: 28 hours
        // Assignment 2 Work: 28 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/05/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(5, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(56, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/08/04 3:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(28, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(28, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(5, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());

        //
        // Task 4 with 2 resources @ 100%
        // 16 hrs of work
        // Currently a 1 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8        (8)      (4.5)      X        (8)
        // R2:   8        (8)        X       (4)       (8)
        //
        // Increase Duration to 3 days (+2 days)
        // Expected.  Notice that work is reduced by less than a whole hour on Thursday 8th
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         8        4.5       X
        // R2:   8         8         X       3.5/(4)
        calendarDef1 = makeSpecialCalendar3();
        calendarDef2 = makeSpecialCalendar4();

        // Increase Duration to 3 days
        // Expected Duration: 3 days
        // Expected Work: 40 hours
        // Expected End Date: Thursday April 8th @ 11:30 AM
        // Assignment 1 Work: 20.5 hours
        // Assignment 2 Work: 19.5 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask4(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/05/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(40, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/08/04 11:30 AM");
        expectedAssignmentWork1 = new TimeQuantity(new BigDecimal("20.5"), TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(new BigDecimal("19.5"), TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, (scheduleEntry.getWorkTQ().convertTo(TimeQuantityUnit.SECOND, 0, BigDecimal.ROUND_HALF_UP)).convertTo(TimeQuantityUnit.HOUR, 4));
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork} with increased
     * durations and assignments with default working time calendars for
     * large values of duration and work.
     */
    public void testCalculateWorkIncreasedDurationLargeValues() {

// TODO improve performance of 1000 -> 5000 day duration change
// TODO currently taking 36 seconds; problem due to length of time to calculate duration
// TODO with 20 assignments each performing 40,000 hours
// TODO create unit test on DurationCalculator with a date, 40,000 hours and 100% assigned
//        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
//        ScheduleEntry scheduleEntry;
//        TestWorkingTimeCalendarProvider provider = new TestWorkingTimeCalendarProvider();
//        TimeQuantity duration;
//        TimeQuantity expectedWork;
//        TimeQuantity expectedAssignmentWork;
//        Date expectedEndDate;
//        ScheduleEntryDurationModifier durationUpdater;
//
//        // Start: 4/12/04 @ 8:00 AM
//        // Work: 160,000 hours
//        // 20 assignments (8,000 hrs each)
//        // Duration: 1000 days
//        // End Date: 2/8/08 @ 5:00 PM
//        //
//        // Update Duration to 5,000 days
//        // Expected Work: 800,000 hours
//        // Each assignments: 40,000 hours
//        // End Date: 6/9/2023 @ 5:00 PM
//        Collection assignments = new LinkedList();
//        for (int i = 0; i < 20; i++) {
//            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
//            assignment.setPersonID("" + i);
//            assignment.setPercentAssigned(100);
//            assignment.setTimeZone(timeZone);
//            assignments.add(assignment);
//        }
//        scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(160000, TimeQuantityUnit.HOUR), assignments);
//        scheduleEntry.setStartTimeD(parseDateTime("04/12/04 8:00 AM"));
//        scheduleEntry.setEndTimeD(new ScheduleEntryDateCalculator(scheduleEntry, provider).addWorkAndupdateAssignmentDates(scheduleEntry.getStartTime()));
//        scheduleEntry.calculateDuration(provider);
//        assertEquals(new TimeQuantity(1000, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
//        assertEquals(parseDateTime("02/08/08 5:00 PM"), scheduleEntry.getEndTime());
//
//        duration = new TimeQuantity(5000, TimeQuantityUnit.DAY);
//        expectedWork = new TimeQuantity(800000, TimeQuantityUnit.HOUR);
//        expectedEndDate = parseDateTime("06/09/23 5:00 PM");
//        expectedAssignmentWork = new TimeQuantity(40000, TimeQuantityUnit.HOUR);
//
//        // Do the calculation
//        long start = System.currentTimeMillis();
//        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
//        durationUpdater.calculateWork(duration);
//        System.out.println("Elapsed: " + (System.currentTimeMillis() - start));
//
//        // Assert Results
//        assertEquals(duration, scheduleEntry.getDurationTQ());
//        assertEquals(expectedWork, (scheduleEntry.getWorkTQ().convertTo(TimeQuantityUnit.SECOND, 0, BigDecimal.ROUND_HALF_UP)).convertTo(TimeQuantityUnit.HOUR, 4));
//        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
//        // Now assert each assignment work is correct
//        TimeQuantity expectedWorkSeconds = expectedAssignmentWork.convertTo(TimeQuantityUnit.SECOND, 0);
//        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
//            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
//            assertEquals("Assignment " + nextAssignment.getPersonID(),
//                    expectedWorkSeconds, nextAssignment.getWork().convertTo(TimeQuantityUnit.SECOND, 0));
//        }

    }
    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork} with decreased
     * durations and assignments with default working time calendars.
     */
    public void testCalculateWorkDecreasedDuration() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;


        provider = new TestWorkingTimeCalendarProvider();

        //
        // 8hr Task with 1 resource @ 100%
        //

        // Update to 0 days
        // Expected Duration: 0 days
        // Expected Work: 0 hours
        // Assignment 1 Work: 0 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 8:00 AM
        scheduleEntry = new Helper().makeTask1();
        expectedDuration = new TimeQuantity(0, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(0, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getEndTime());

        //
        // 3 day task
        //

        // Decrease to 2 days
        // Expected Duration: 2 days
        // Expected Work: 16 hours
        // Assignment 1 Work: 16 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask5();
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Decrease to 1 day
        // Expected Duration: 1 day
        // Expected Work: 8 hours
        // Assignment 1 Work: 8 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask5();
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(1, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 16hr Task with 1 resources @ 50%
        // Duration is 4 days
        //

        // Decrease to 3 days
        // Expected Duration: 3 days
        // Expected Work: 12 hours
        // Assignment 1 Work: 12 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/7/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask6();
        expectedDuration = new TimeQuantity(3, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = expectedWork;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(3, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/7/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 64hr task with 2 resources @ 100%
        // Duration is 4 days
        //

        // Decrease to 2 days
        // Expected Duration: 2 days
        // Expected Work: 32 hours
        // Assignment 1 work: 16 hours
        // Assignment 2 work: 16 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask7();
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = expectedAssignmentWork1;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Infinite loop condition
        // A broken schedule entry where start and end date do not account for all the duration

        // Task with 1 resource @ 100%
        // 128 hours work, 16 day duration
        // Starts Monday April 5th @ 8:00 AM, Ends Monday April 5th @ 5:00 PM
        // Update to 1 day
        // We cannot predict results, except that no infinite loop should occur
        scheduleEntry = new Helper().makeTask13();
        scheduleEntry.setEndTimeD(DateUtils.parseDateTime("04/05/04 5:00 PM"));
        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculateWork(new TimeQuantity(1, TimeQuantityUnit.DAY));

    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculateWork(net.project.util.TimeQuantity)} with decreased
     * durations and assignments with custom working time calendars.
     */
    public void testCalculateWorkDecreasedDurationCustomWorkingTime() {
        Helper helper;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        Date expectedEndDate;
        TimeQuantity expectedDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        //
        // Task 8 with 2 resources @ 100%
        // 32 hrs of work
        // Currently a 3 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th
        // R1:   8         X         8
        // R2:   8         8
        //
        // Decrease Duration to 2 days
        // Expected
        //     Mon 5th  Tues 6th  Wed 7th
        // R1:   8         X
        // R2:   8         8
        // All additional work assigned to R1
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[]{Calendar.SATURDAY, Calendar.SUNDAY});


        // Decrease Duration to 2 days
        // Expected Duration: 2 days
        // Expected Work: 24 hours
        // Expected End Date: Tuesday April 6th @ 5:00 PM
        // Assignment 1 Work: 8 hours
        // Assignment 2 Work: 16 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask8(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/07/04 5:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/06/04 5:00 PM");
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(16, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());

        //
        // Task 4 with 2 resources @ 100%
        // 32 hrs of work
        // Currently a 3 day task because of working time:
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         4.5      X         3.5
        // R2:   8         X        4         X          4
        //
        // Decrease Duration to 2 days (-1 day)
        // Expected.  Notice that work is reduced by less than a whole hour on Thursday 8th
        //     Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1:   8         4.5       X
        // R2:   8         X       3.5/(4)
        calendarDef1 = makeSpecialCalendar5();
        calendarDef2 = makeSpecialCalendar6();

        // Decrease Duration to 2 days
        // Expected Duration: 2 days
        // Expected Work: 24 hours
        // Expected End Date: Wednesday April 7th @ 11:30 AM
        // Assignment 1 Work: 12.5 hours
        // Assignment 2 Work: 11.5 hours
        helper = new Helper();
        scheduleEntry = helper.makeTask8(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/09/04 12:00 PM"), scheduleEntry.getEndTime());

        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/07/04 11:30 AM");
        expectedAssignmentWork1 = new TimeQuantity(new BigDecimal("12.5"), TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(new BigDecimal("11.5"), TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculateWork(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(expectedDuration, scheduleEntry.getDurationTQ());
        assertEquals(expectedWork, (scheduleEntry.getWorkTQ().convertTo(TimeQuantityUnit.SECOND, 0, BigDecimal.ROUND_HALF_UP)).convertTo(TimeQuantityUnit.HOUR, 4));
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());
        assertWork(Arrays.asList(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2}), scheduleEntry.getAssignments());


    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with a null
     * duration value.
     */
    public void testCalculatePercentageNullDuration() {
        try {
            new ScheduleEntryDurationModifier(new Task(), new TestWorkingTimeCalendarProvider()).calculatePercentage(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with a new
     * task.
     */
    public void testCalculatePercentageNewTask() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity duration;
        TimeQuantity work;

        provider = new TestWorkingTimeCalendarProvider();

        //
        // Empty Task (1 Day duration)
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 0 hours
        // Empty assignments
        // Expected Start Date: null
        // Expected End Date: null
        scheduleEntry = new Task();
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));

        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertNull(scheduleEntry.getStartTime());
        assertNull(scheduleEntry.getEndTime());

    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with increased
     * durations.
     */
    public void testCalculatePercentageIncreasedDuration() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity duration;
        TimeQuantity work;

        provider = new TestWorkingTimeCalendarProvider();

        //
        // Task with no assignment
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 8 hours
        // Empty assignments
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTaskNoAssignment();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(new TimeQuantity(2, TimeQuantityUnit.DAY));
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Update to 0 days
        // Expected Duration: 0 days
        // Expected Work: 0 hours
        // Empty assignments
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 8:00 AM
        scheduleEntry = new Helper().makeTaskNoAssignment();
        duration = new TimeQuantity(0, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(new TimeQuantity(0, TimeQuantityUnit.DAY));
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertTrue(scheduleEntry.getAssignments().isEmpty());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getEndTime());

        //
        // 1 Assignment
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 8 hours
        // Assignment 1: 8 hours, 50%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask1();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{50});
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Update to 4 days
        // Expected Duration: 4 days
        // Expected Work: 8 hours
        // Assignment 1: 8 hours, 25%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/8/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask1();
        duration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        work = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{25});
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/8/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 2 Assignments
        //

        // Update to 2 days
        // Expected Duration: 2 days
        // Expected Work: 16 hours
        // Assignment 1: 8 hours, 50%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask3();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{50, 50});
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // Special case
        // 0 days, 0 work, 1 resource 50%
        // Update to 2 days; the work will change, the percentage will not
        // Expected Duration: 2 days
        // Expected Work: 16 hours
        // Assignment 1 Work: 16 hours
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask12();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertWork(Arrays.asList(new TimeQuantity[]{work}), scheduleEntry.getAssignments());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{100});
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with increased
     * durations and custom working time calendars.
     */
    public void testCalculatePercentageIncreasedDurationCustomWorkingTime() {

        Helper helper;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        Date expectedEndDate;
        TimeQuantity duration;
        TimeQuantity work;

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        //
        // Task 8 with 2 resources @ 100%
        // 32 hrs of work
        // Currently a 3 day task because of working time:
        //           Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1: 100%    8         X         8         X
        // R2: 100%    8         8         X
        //
        // Increase Duration to 4 days
        // Expected theoretical working hours to achive duration:
        //           Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1: 100%    8         X         8         X
        // R2:  67%    8         8         X        (8) <-- Resource works on this day
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04", "04/08/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/07/04"}, null);

        // Increase Duration to 4 days
        // Duration: 4 days
        // Work: 32 hours
        // Expected End Date: Thursday April 8th @ 5:00 PM
        // Assignment 1 %: 100%
        // Assignment 2 %: 67%
        helper = new Helper();
        scheduleEntry = helper.makeTask8(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/07/04 5:00 PM"), scheduleEntry.getEndTime());

        duration = new TimeQuantity(4, TimeQuantityUnit.DAY);
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/08/04 5:00 PM");

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{100, 67});
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());


    }

    /**
     * Tests {@link ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with decreased
     * durations.
     */
    public void testCalculatePercentageDecreasedDuration() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        TimeQuantity duration;
        TimeQuantity work;
        TimeQuantity assignmentWork;

        provider = new TestWorkingTimeCalendarProvider();

        //
        // 1 Assignment
        //

        // Update to 0 days
        // This is a special case; percentage is NOT changed, rather work is
        // Expected Duration: 0 days
        // Expected Work: 0 hours
        // Assignment 1: 0 hours, 100%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 8:00 AM
        scheduleEntry = new Helper().makeTask1();
        duration = new TimeQuantity(0, TimeQuantityUnit.DAY);
        work = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        assignmentWork = work;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{100});
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getEndTime());

        // 3 Day duration, 24 hour task
        // Change Duration to: 2 days
        // Work: 24 hours
        // Assignment 1: 24 hours, 150%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask5();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(24, TimeQuantityUnit.HOUR);
        assignmentWork = work;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{150});
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        // 4 Day duration, 16 hour task, 1 assignment @ 50%
        // Change Duration to: 2 days
        // Work: 16 hours
        // Assignment 1: 16 hours, 100%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/6/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask6();
        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignmentWork = work;

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{100});
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/6/04 5:00 PM"), scheduleEntry.getEndTime());

        //
        // 2 Assignments
        //

        // 4 Day duration, 64 hour task, 2 assignment @ 100%
        // Change Duration to: 1 days
        // Work: 64 hours
        // Assignment 1: 32 hours, 400%
        // Assignment 2: 32 hours, 400%
        // Expected Start Date: 4/5/04 @ 8:00 AM
        // Expected End Date: 4/5/04 @ 5:00 PM
        scheduleEntry = new Helper().makeTask7();
        duration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        work = new TimeQuantity(64, TimeQuantityUnit.HOUR);
        assignmentWork = new TimeQuantity(32, TimeQuantityUnit.HOUR);

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{400, 400});
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork, assignmentWork}), scheduleEntry.getAssignments());
        assertEquals(DateUtils.parseDateTime("4/5/04 8:00 AM"), scheduleEntry.getStartTime());
        assertEquals(DateUtils.parseDateTime("4/5/04 5:00 PM"), scheduleEntry.getEndTime());

    }

    /**
     * Tests {@link net.project.schedule.calc.ScheduleEntryDurationModifier#calculatePercentage(net.project.util.TimeQuantity)} with decreased
     * durations and custom working time calendars.
     */
    public void testCalculatePercentageDecreasedDurationCustomWorkingTime() {

        Helper helper;
        ScheduleEntry scheduleEntry;
        ScheduleEntryDurationModifier durationUpdater;
        Date expectedEndDate;
        TimeQuantity duration;
        TimeQuantity work;
        TimeQuantity assignmentWork;

        WorkingTimeCalendarDefinition calendarDef1;
        WorkingTimeCalendarDefinition calendarDef2;

        //
        // Task 8 with 2 resources @ 100%
        // 32 hrs of work
        // Currently a 3 day task because of working time:
        //           Mon 5th  Tues 6th  Wed 7th  Thurs 8th  Fri 9th
        // R1: 100%    8         X         8         X
        // R2: 100%    8         8         X
        //
        // Decrease Duration to 2 days
        // Only Assignment 1 will be changed
        //
        calendarDef1 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/06/04", "04/08/04"}, null);
        calendarDef2 = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SATURDAY, Calendar.SUNDAY}, new String[]{"04/07/04"}, null);

        // Decrease Duration to 2 days
        // Duration: 2 days
        // Work: 32 hours
        // Expected End Date: Tuesday April 6th @ 5:00 PM
        // Assignment 1 %: 200%
        // Assignment 2 %: 100%
        helper = new Helper();
        scheduleEntry = helper.makeTask8(calendarDef1, calendarDef2);
        assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
        assertEquals(DateUtils.parseDateTime("04/07/04 5:00 PM"), scheduleEntry.getEndTime());

        duration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        work = new TimeQuantity(32, TimeQuantityUnit.HOUR);
        assignmentWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedEndDate = DateUtils.parseDateTime("04/06/04 5:00 PM");

        durationUpdater = new ScheduleEntryDurationModifier(scheduleEntry, helper.provider);
        durationUpdater.calculatePercentage(duration);
        assertEquals(duration, scheduleEntry.getDurationTQ());
        assertEquals(work, scheduleEntry.getWorkTQ());
        assertPercentage(scheduleEntry.getAssignments(), new int[]{200, 100});
        assertWork(Arrays.asList(new TimeQuantity[]{assignmentWork, assignmentWork}), scheduleEntry.getAssignments());
        assertEquals(expectedEndDate, scheduleEntry.getEndTime());

    }

    //
    // Helper methods
    //

    /**
     * Asserts that each assignment's work is equal to the corresponding time quantity.
     *
     * @param timeQuantities the time quantities in the same order as assignments
     * @param assignments the ordered assignments whose work to check
     */
    static void assertWork(Collection timeQuantities, Collection assignments) {

        ArrayList timeQuantityList = new ArrayList(timeQuantities);

        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext(); index++) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            if (!timeQuantityList.get(index).equals(nextAssignment.getWork())) {
                System.out.println("Assignment " + index + " work: " + nextAssignment.getWork().toShortString(10,10));
                System.out.println("Expected " + index + " work:   " + ((TimeQuantity)timeQuantityList.get(index)).toShortString(10,10));
                assertEquals("Assignment " + index, timeQuantityList.get(index).toString(), nextAssignment.getWork().toString());
            }
        }

    }

    /**
     * Asserts that each assignment's percentage is equal to the corresponding value.
     *
     * @param assignments the ordered assignments whose percentage to check
     * @param percentageValues the percentage values in the same order as assignments
     */
    private static void assertPercentage(Collection assignments, int[] percentageValues) {

        if (assignments.size() != percentageValues.length) {
            throw new IllegalArgumentException("Number of assignments and percentage values must be equal");
        }

        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext(); index++) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
           assertEquals("Assignment " + index, percentageValues[index], nextAssignment.getPercentAssignedInt());
        }

    }


    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Wednesday 7th April, working time 8:00 AM - 8:00 PM (12 hours)
     * <li>Thursday 8th April, non working time
     * <li>Friday 9th April, working time 8:00 AM - 8:00 PM (12 hours)
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar1() {

        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(20, 0))
        }));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 9);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(20, 0))
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Wednesday 7th April, non working time
     * <li>Thursday 8th April, working time 3:00 AM - 11:00 PM (20 hours)
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar2() {
        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(3, 0), new Time(23, 0))
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Wednesday 7th April, working time 8:00 AM - 12:30 PM (4.5 hours)
     * <li>Thursday 8th April, non working time
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar3() {

        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(12, 30))
        }));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Wednesday 7th April, non working time
     * <li>Thursday 8th April, working time 8:00 AM - 12:00 PM (4 hours)
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar4() {
        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(12, 0))
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Tuesday 6th April, working time 8:00 AM - 12:00 PM, 1:00 PM - 1:30 PM (4.5 hours)
     * <li>Wednesday 7th April, non working time
     * <li>Thursday 8th April, working time 8:00 AM - 11:30 AM (3.5 hours)
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar5() {
        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 6);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(12, 0)),
            new WorkingTime(new Time(13, 0), new Time(13, 30))
        }));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(11, 30))
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    /**
     * Makes a working time calendar as follows:
     * <li>Saturday, Sunday non working days.
     * <li>Tuesday 6th April, non working time
     * <li>Wednesday 7th April, working time 8:00 AM - 12:00 PM (4 hours)
     * <li>Thursday 8th April, non working time
     * <li>Friday 9th April, working time 8:00 AM - 12:00 PM (4 hours)
     * @return working time calendar
     */
    public static WorkingTimeCalendarDefinition makeSpecialCalendar6() {
        WorkingTimeCalendarEntry entry;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));

        WorkingTimeCalendarDefinition calendarDef;

        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDef(new int[]{Calendar.SUNDAY, Calendar.SATURDAY}, (String[]) null, (String[]) null);

        cal.set(2004, Calendar.APRIL, 6);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 7);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(12, 0))
        }));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 8);
        entry = WorkingTimeCalendarEntry.makeNonWorkingDate(new DayOfYear(cal));
        calendarDef.addEntry(entry);

        cal.set(2004, Calendar.APRIL, 9);
        entry = WorkingTimeCalendarEntry.makeWorkingDate(new DayOfYear(cal));
        entry.setWorkingTimes(Arrays.asList(new WorkingTime[]{
            new WorkingTime(new Time(8, 0), new Time(12, 0))
        }));
        calendarDef.addEntry(entry);

        return calendarDef;
    }

    static class Helper {

        final TestWorkingTimeCalendarProvider provider;

        public Helper() {
            this.provider = new TestWorkingTimeCalendarProvider();
        }

        /**
         * Creates a task as follows:
         * 8 hour work, no assignments
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTaskNoAssignment() {
            ScheduleEntry scheduleEntry = makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.DAY), new TimeQuantity(8, TimeQuantityUnit.HOUR));
            assertEquals(DateUtils.parseDateTime("04/05/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * No assignments
         * Start Date: Monday April 5th @ 8:00 AM
         * @param duration the task duration
         * @param work the task work
         * @return the task
         */
        ScheduleEntry makeTaskNoAssignment(TimeQuantity duration, TimeQuantity work) {
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(work, Collections.EMPTY_SET);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.setDuration(duration);
            scheduleEntry.calculateEndDate(provider);
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 0 hour work, no assignments
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTaskNoAssignmentNoWork() {
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(0, TimeQuantityUnit.HOUR), Collections.EMPTY_SET);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.setEndTimeD(DateUtils.parseDateTime("04/05/04 5:00 PM"));
            scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 8 hour work, 1 assignment @ 100% (1 day duration)
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask1() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/05/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 8 hour work, 1 assignment @ 50%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 2 day
         * End Date: Tuesday April 6th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask2() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(50));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 16 hour work, 2 assignment @ 100%, 8 hours each
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask3() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(16, TimeQuantityUnit.HOUR), helper.makeTwoAssignments(100,100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/05/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 16 hour work, 2 assignment @ 100%, 8 hours each
         * Start Date: Monday April 5th @ 8:00 AM
         * @param calendarDef1 first assignment's working time calendar defintion
         * @param calendarDef2 second assignment's working time calendar defintion
         * @return the task
         */
        ScheduleEntry makeTask4(WorkingTimeCalendarDefinition calendarDef1, WorkingTimeCalendarDefinition calendarDef2) {
            TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
            Collection assignments = new LinkedList();
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef1, provider));
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("2", 100, timeZone, calendarDef2, provider));
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(16, TimeQuantityUnit.HOUR), assignments);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 24 hour work, 1 assignment @ 100%, 24 hours
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 3 days
         * End Date: Wednesday April 7th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask5() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(24, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/07/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 16 hour work, 1 assignment @ 50%, 16 hours
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 4 days
         * End Date: Thursday April 8th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask6() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(16, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(50));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(4, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/08/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 64 hour work, 2 assignment @ 100%, 32 hours each
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 4 days
         * End Date: Thursday April 8th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask7() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(64, TimeQuantityUnit.HOUR), helper.makeTwoAssignments(100,100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(4, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/08/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 32 hour work, 2 assignment @ 100%, 16 hours each
         * Start Date: Monday April 5th @ 8:00 AM
         * @param calendarDef1 first assignment's working time calendar defintion
         * @param calendarDef2 second assignment's working time calendar defintion
         * @return the task
         */
        ScheduleEntry makeTask8(WorkingTimeCalendarDefinition calendarDef1, WorkingTimeCalendarDefinition calendarDef2) {
            TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
            Collection assignments = new LinkedList();
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef1, provider));
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("2", 100, timeZone, calendarDef2, provider));
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(32, TimeQuantityUnit.HOUR), assignments);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 12 hour work, assignment 1: 4 hours @ 25%, assignment 2: 8 hours @ 50%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 2 days
         * End Date: Tuesday April 6th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask9() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(12, TimeQuantityUnit.HOUR), helper.makeTwoAssignments(25,50));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 12 hour work, assignment 1: 4 hours @ 25%, assignment 2: 8 hours @ 50%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 2 days
         * End Date: Tuesday April 6th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask10(WorkingTimeCalendarDefinition calendarDef1, WorkingTimeCalendarDefinition calendarDef2) {
            TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
            Collection assignments = new LinkedList();
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 25, timeZone, calendarDef1, provider));
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("2", 50, timeZone, calendarDef2, provider));
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(12, TimeQuantityUnit.HOUR), assignments);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 28 hour work, assignment 1: 4 hours @ 25%, assignment 2: 24 hours @ 150%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 2 days
         * End Date: Tuesday April 6th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask11() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(28, TimeQuantityUnit.HOUR), helper.makeTwoAssignments(25,150));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/06/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 0 hour work, 1 assignment @ 100% (0 day duration)
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 0 day
         * End Date: Monday April 5th @ 8:00 AM
         * @return the task
         */
        ScheduleEntry makeTask12() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(0, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/05/04 8:00 AM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 0 hour work, 1 assignment @ 100% (0 day duration)
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 0 day
         * End Date: Monday April 5th @ 8:00 AM
         * @return the task
         */
        ScheduleEntry makeTask12(WorkingTimeCalendarDefinition calendarDef1) {
            TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
            Collection assignments = new LinkedList();
            assignments.add(ScheduleEntryWorkingTimeCalendarTest.makeAssignment("1", 100, timeZone, calendarDef1, provider));
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(0, TimeQuantityUnit.HOUR), assignments);
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/05/04 8:00 AM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 128 hour work, 1 assignment @ 100% (16 day duration)
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 16 day
         * End Date: Monday April 26th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask13() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(128, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.calculateEndDate(provider);
            scheduleEntry.calculateDuration(this.provider);
            assertEquals(new TimeQuantity(16, TimeQuantityUnit.DAY), scheduleEntry.getDurationTQ());
            assertEquals(DateUtils.parseDateTime("04/26/04 5:00 PM"), scheduleEntry.getEndTime());
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 0 hour work, 2 assignments @ 100%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask14() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(0, TimeQuantityUnit.HOUR), helper.makeTwoAssignments(100, 100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.setEndTimeD(DateUtils.parseDateTime("04/05/04 5:00 PM"));
            scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
            return scheduleEntry;
        }

        /**
         * Creates a task as follows:
         * 8 hour work
         * Assignment 1: 0 hour @ 100%
         * Assignment 2: 8 hour @ 100%
         * Start Date: Monday April 5th @ 8:00 AM
         * Duration: 1 day
         * End Date: Monday April 5th @ 5:00 PM
         * @return the task
         */
        ScheduleEntry makeTask15() {
            ScheduleEntryWorkingTimeCalendarTest.ResourceHelper helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelper();
            ScheduleEntry scheduleEntry = ScheduleEntryDateCalculatorTest.createTask(new TimeQuantity(8, TimeQuantityUnit.HOUR), helper.makeSingleAssignment(100));
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);

            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPersonID("2");
            assignment.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
            assignment.setPercentAssignedDecimal(new BigDecimal("0.00"));
            scheduleEntry.addAssignment(assignment);

            scheduleEntry.setStartTimeD(DateUtils.parseDateTime("04/05/04 8:00 AM"));
            scheduleEntry.setEndTimeD(DateUtils.parseDateTime("04/05/04 5:00 PM"));
            scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
            return scheduleEntry;
        }
    }


}
