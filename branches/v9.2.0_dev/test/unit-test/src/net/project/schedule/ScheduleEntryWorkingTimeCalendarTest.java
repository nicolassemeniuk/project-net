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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.resource.ScheduleEntryAssignment;

public class ScheduleEntryWorkingTimeCalendarTest extends TestCase {
    public ScheduleEntryWorkingTimeCalendarTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleEntryWorkingTimeCalendarTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryWorkingTimeCalendar#isWorkingDay}.
     */
    public void testIsWorkingDay() {

        // Reset seconds and milliseconds to zero for all dates and times
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        IWorkingTimeCalendar workingTimeCal;

        ResourceHelperHierarchy helper = new ResourceHelperHierarchy();

        // Create a working time calendar for two resources each at 100%
        // Using hierarchical calendar definitions
        // A day is a working day if it is a working day for at least one
        // of the resources
        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(helper.makeTwoAssignments(100, 100), helper.getProvider());

        // Start on June 1st
        cal.set(2003, Calendar.JUNE, 1, 0, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 2nd
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 3rd
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 4th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 5th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 6th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 7th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 8th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 9th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 10th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 11th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 12th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 13th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 14th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 15th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 16th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 17th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 18th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 19th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 20th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 21st
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 22nd
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 23rd
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 24th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 25th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 26th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 27th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 28th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 29th
        cal.add(Calendar.DATE, 1);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 30th
        cal.add(Calendar.DATE, 1);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));

    }

    /**
     * Tests {@link ScheduleEntryWorkingTimeCalendar#isWorkingDay} for
     * various time zones.
     */
    public void testIsWorkingDayTimeZones() {

        TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
        TimeZone timeZone2 = TimeZone.getTimeZone("Europe/London");

        // The calendar we use to make expected dates
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // The schedule entry working time calendar
        IWorkingTimeCalendar workingTimeCal;
        ResourceHelperHierarchy helper = new ResourceHelperHierarchy();

        // Create a working time calendar for two resources each at 100%
        // Using hierarchical calendar definitions
        // A day is a working day if it is a working day for at least one
        // of the resources
        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(helper.makeTwoAssignments(100, timeZone1, 100, timeZone2), helper.getProvider());

        // June 1st @ 12:00 AM PST (8:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 1, 0, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 1st @ 8:00 AM PST (4:00 PM GMT)
        cal.set(2003, Calendar.JUNE, 1, 8, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 1st @ 4:00 PM PST (June 2nd @ 12:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 1, 16, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));

        // June 2nd @ 12:00 AM PST (8:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 2, 0, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 2nd @ 8:00 AM PST (4:00 PM GMT)
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 2nd @ 4:00 PM PST (June 3rd @ 12:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 2, 16, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));

        // June 3rd @ 12:00 AM PST (8:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 3, 0, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 3rd @ 8:00 AM PST (4:00 PM GMT)
        cal.set(2003, Calendar.JUNE, 3, 8, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 3rd @ 4:00 PM PST (June 2nd @ 12:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 3, 16, 0);
        assertTrue(workingTimeCal.isWorkingDay(cal.getTime()));

        // June 14th @ 12:00 AM PST (8:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 14, 0, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 14th @ 8:00 AM PST (4:00 PM GMT)
        cal.set(2003, Calendar.JUNE, 14, 8, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));
        // June 14th @ 4:00 PM PST (June 15th @ 12:00 AM GMT)
        cal.set(2003, Calendar.JUNE, 14, 16, 0);
        assertFalse(workingTimeCal.isWorkingDay(cal.getTime()));

    }

    /**
     * Tests {@link ScheduleEntryWorkingTimeCalendar#ensureWorkingTimeStart}.
     * @throws NoWorkingTimeException
     */
//    public void testEnsureWorkingTimeStart() throws NoWorkingTimeException {
//
//        // Reset seconds and milliseconds to zero for all dates and times
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        Date expectedDate;
//        Date inputDate;
//        IWorkingTimeCalendar workingTimeCal;
//
//        TestWorkingTimeCalendarProvider provider;
//        ResourceHelper resourceHelper = new ResourceHelper();
//
//        //
//        // No resources
//        //
//
//        // Create a calendar with an empty collection of assignments
//        // Uses default calendar
//        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(Arrays.asList(new ScheduleEntryAssignment[]{}), new TestWorkingTimeCalendarProvider());
//
//        // Start Date: Friday June 20th @ 5:00 PM
//        // Expected: Monday June 23rd @ 8:00 AM
//        cal.set(2003, Calendar.JUNE, 20, 17, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 23, 8, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//
//        //
//        // One resource 50% with calendar 1
//        //
//
//        provider = resourceHelper.getProvider();
//        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(
//                Arrays.asList(new ScheduleEntryAssignment[]{makeAssignment("1", 50, TimeZone.getTimeZone("America/Los_Angeles"), resourceHelper.calendarDef1, provider)}), provider);
//
//        // Start Date: Saturday June 14th @ 5:00 PM
//        // Expected: Thursday June 16th @ 8:0 AM
//        cal.set(2003, Calendar.JUNE, 14, 17, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//        //
//        // Two resources
//        // Resource1:  100%, calendar 1
//        // Resource2:  100%, calendar 2
//        //
//
//        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(resourceHelper.makeTwoAssignments(100, 100), resourceHelper.getProvider());
//
//        // Start Date: Monday June 16th @ 8:00 AM
//        // Expected: Monday June 16th @ 8:00 AM
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//        // Start Date: Saturday June 14th @ 5:00 PM
//        // Expected: Monday June 16th @ 8:00 AM
//        cal.set(2003, Calendar.JUNE, 14, 17, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//    }

//    public void testEnsureWorkingTimeStartTimeZones() throws NoWorkingTimeException {
//
//        // Reset seconds and milliseconds to zero for all dates and times
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        Date expectedDate;
//        Date inputDate;
//        IWorkingTimeCalendar workingTimeCal;
//
//        ResourceHelper resourceHelper = new ResourceHelper();
//        TimeZone timeZone1;
//        TimeZone timeZone2;
//
//        //
//        // Two resources
//        // Resource1:  100%, calendar 1, America/Los_Angeles
//        // Resource2:  100%, calendar 2, Europe/London
//        //
//
//        timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
//        timeZone2 = TimeZone.getTimeZone("Europe/London");
//        workingTimeCal = new ScheduleEntryWorkingTimeCalendar(resourceHelper.makeTwoAssignments(100, timeZone1, 100, timeZone2), resourceHelper.getProvider());
//
//        // Start Date: Monday June 16th @ 8:00 AM PST (4:00 PM GMT)
//        // Already Working time for PST and GMT
//        // Expected: Monday June 16th @ 8:00 AM PST
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 16, 8, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//        // Start Date: Monday June 16th @ 3:00 PM PST (11:00 PM GMT)
//        // (Mondays and Tuesday 17th is Non working day for PST)
//        // Expected: Tuesday June 17th @ 8:00 AM GMT (12:00 AM PST)
//        cal.set(2003, Calendar.JUNE, 16, 15, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 17, 0, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//        // Start Date: Monday June 16th @ 1:00 AM PST (9:00 AM GMT)
//        // Next WT Start:  Wednesday 8:00 AM PST for PST / already in WT for GMT
//        // Expected: Monday June 16th @ 1:00 AM PST (9:00 AM GMT)
//        cal.set(2003, Calendar.JUNE, 16, 1, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 16, 1, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//        // Start Date: Wednesday June 18th @ 9:00 AM PST (5:00 PM GMT)
//        // 6/18 is NWD for Cal1; 6/18 is WD for Cal2
//        // Next WT Start:  Thursday 19th @ 8:00 AM PST for cal1 / Thursday 19th @ 8:00 AM GMT (12:00 AM PST) for cal2
//        // Expected: Thursday 19th @ 12:00 AM PST
//        cal.set(2003, Calendar.JUNE, 18, 9, 0);
//        inputDate = cal.getTime();
//        cal.set(2003, Calendar.JUNE, 19, 0, 0);
//        expectedDate = cal.getTime();
//        assertEquals(expectedDate, workingTimeCal.ensureWorkingTimeStart(inputDate));
//
//    }

    /**
     * Tests {@link ScheduleEntryWorkingTimeCalendar#ensureWorkingTimeEnd}.
     */
    public void testEnsureWorkingTimeEnd() {
        // Not done yet
    }


    /**
     * Tests the getStartOfNextWorkingTime() method.
     */
    public void testGetStartOfNextWorkingTime() throws NoWorkingTimeException {
        // Not done for ScheduleEntryWorkingTimeCalendar
    }

    /**
     * Tests the getEndOfPreviousWorkingTime() method.
     */
    public void testGetEndOfPreviousWorkingTime() throws NoWorkingTimeException {
        // Not done for ScheduleEntryWorkingTimeCalendar
    }

    //
    // Helper Methods
    //

    /**
     * Makes an assignment with the specified working time calendar definition.
     * <p>
     * Updates the specified provider to contain the calendar for the assignee.
     * @param personID the ID of the person for which to create an assignemtn
     * @param percentage the percentage assigned
     * @param timeZone the time zone for the assignee
     * @param calendarDef the working time calendar definition to be provided for
     * the assignee
     * @param provider the provider to update such that the calendar definition will
     * be returned for the assignee
     * @return the assignment
     */
    public static ScheduleEntryAssignment makeAssignment(String personID, int percentage, TimeZone timeZone, WorkingTimeCalendarDefinition calendarDef, TestWorkingTimeCalendarProvider provider) {
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPersonID(personID);
        assignment.setPercentAssigned(percentage);
        assignment.setTimeZone(timeZone);
        provider.addResourceCalendarDefintion(personID, calendarDef);
        return assignment;
    }


    /**
     * Constructs simple working time calendar definitions with no
     * hierarchy.
     */
    public static class ResourceHelper {

        /**
         * First working time calendar definition.
         * <li>Sunday and Monday are non working days
         * <li>Tuesday 17th June 2003 and Wednesday 18th June 2003 are non working days
         * <li>Sunday 22nd June 2003 and Monday 23rd June 2003 are working days
         */
        public final WorkingTimeCalendarDefinition calendarDef1;

        /**
         * Second working time calendar definition.
         * <li>Saturday, Sunday and Wednesday are non working days
         * <li>Wednesday 25th and Thursday 26th are non working days
         * <li>Wednesday 18th, Sunday 22nd and Saturday 28th are working days
         */
        public final WorkingTimeCalendarDefinition calendarDef2;

        /**
         * A working time calendar provided that alwyas provides the calendars
         * for the most recently created assignees.
         */
        private final TestWorkingTimeCalendarProvider provider;

        public ResourceHelper() {
            this(WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                         new int[]{Calendar.SUNDAY, Calendar.MONDAY},
                         new String[]{"6/17/03", "6/18/03"},
                         new String[]{"6/22/03", "6/23/03"}),
                 WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                         new int[]{Calendar.SUNDAY, Calendar.WEDNESDAY, Calendar.SATURDAY},
                         new String[]{"6/25/03", "6/26/03"},
                         new String[]{"6/18/03", "6/22/03", "6/28/03"})
            );
        }

        private ResourceHelper(WorkingTimeCalendarDefinition calendarDef1, WorkingTimeCalendarDefinition calendarDef2) {
            this.calendarDef1 = calendarDef1;
            this.calendarDef2 = calendarDef2;

            // Start with an empty provider
            provider = TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());
        }

        /**
         * Returns a privder that will provider calendar definitions
         * for the assignees of the most recently created assignments.
         * @return the provider of calendar definitions for assignees
         */
        TestWorkingTimeCalendarProvider getProvider() {
            return this.provider;
        }

        /**
         * Makes two assignments with the specified percentages and a
         * default time zone of <code>America/Los_Angeles</code>.
         * The first assignment uses working time calendar definition 1.
         * The second assignment uses working time calendar definition 2.
         * @param percentage1 the first percentage to use
         * @param percentage2 the second percentage to use
         * @return a collection of 2 <code>Assignment</code> elements; the
         * first element uses the first percentages and working time calendar
         * definition 1; the second element uses the second percentage and
         * working time calendar definition 2
         */
        public Collection makeTwoAssignments(int percentage1, int percentage2) {
            return makeTwoAssignments(percentage1, TimeZone.getTimeZone("America/Los_Angeles"), percentage2, TimeZone.getTimeZone("America/Los_Angeles"));
        }

        /**
         * Makes two assignments with the specified percentages and time zones.
         * The first assignment uses working time calendar definition 1.
         * The second assignment uses working time calendar definition 2.
         * @param percentage1 the percentage for the first assignee
         * @param timeZone1 the time zone for the first assignee
         * @param percentage2 the second percentage to use
         * @param timeZone2 the time zone for the second assignee
         * @return a collection of 2 <code>Assignment</code> elements; the
         * first element uses the first percentages and working time calendar
         * definition 1; the second element uses the second percentage and
         * working time calendar definition 2
         */
        protected Collection makeTwoAssignments(int percentage1, TimeZone timeZone1, int percentage2, TimeZone timeZone2) {
            List assignments = new ArrayList();
            assignments.add(makeAssignment("1", percentage1, timeZone1, calendarDef1, provider));
            assignments.add(makeAssignment("2", percentage2, timeZone2, calendarDef2, provider));
            return assignments;
        }

        /**
         * Makes a single assignment with the specified percentage and a
         * default time zone of <code>America/Los_Angeles</code>.
         * The assigment uses working time calendar definition 1.
         * @param percentage the percentage of the assignment
         * @return a collection of a single <code>Assignment</code>
         */
        public Collection makeSingleAssignment(int percentage) {
            return Collections.singleton(makeAssignment("1", percentage, TimeZone.getTimeZone("America/Los_Angeles"), calendarDef1, provider));
        }

    }

    /**
     * Provides a helper for testing hierarhical calendars.
     * <p>
     * Non working day in <b>bold</b>, customized days are <u>underlined</u>.<br>
     * The base calendar definition is as follows: <pre><code>
     * <u>June 2003</u>
     * <b>Sun</b>  Mon  Tue  Wed  Thu  Fri  <b>Sat</b>
     *  <b>1</b>    2    3    4    5    6    <b>7</b>
     *  <b><u>8</u>    <u>9</u>   <u>10</u<   <u>11</u>   <u>12</u>   <u>13</u>   <u>14</u></b>
     * <b>15</b>   16   17   18   19   20   <b>21</b>
     * <u>22</u>   <u>23</u>   <u>24</u>   <u>25</u>   <u>26</u>   <u>27</u>   <u>28</u>
     * <b>29</b>   30
     * </code></pre>
     * Calendar 1 is as follows: <pre><code>
     * <u>June 2003</u>
     * <b><u>Sun</u></b>  Mon  <b><u>Tue</u></b>  Wed  Thu  Fri  <b>Sat</b>
     *  <b>1</b>    2    <b>3</b>    4    5    6    <b>7</b>
     *  <b>8</b>    <b>9</b>   <b>10</b>   <u>11</u>   <u>12</u>   <u>13</u>   <b>14</b>
     * <b>15</b>   16   <b>17</b>   18   19   20   <b>21</b>
     * <b>22</b>   23   <b>24</b>   25   <b><u>26</u></b>   <b><u>27</u></b>   28
     * <b>29</b>   30
     * </code></pre>
     * Calendar 2 is as follows: <pre><code>
     * <u>June 2003</u>
     * <b>Sun</b>  Mon  Tue  Wed  Thu  Fri  <u>Sat</u>
     *  <b>1</b>    2    <b><u>3</u></b>    4    5    6    7
     *  <b>8</b>    <b>9</b>   <b>10</b>   <b>11</b>   <b>12</b>   <u>13</u>   14
     * <b>15</b>   16   17   18   19   <u>20</u>   21
     * 22   23   24   25   26   27   28
     * <b>29</b>   30
     * </code></pre>
     * </p>
     */
    public static class ResourceHelperHierarchy extends ResourceHelper {

        /**
         * Base schedule working times.
         * Non Working Days: Saturday, Sunday
         * Non working dates: Sunday June 8th - Saturday June 14th
         * Working date: Sunday June 22nd - Saturday June 28th
         */
        private static final WorkingTimeCalendarDefinition BASE_CALENDAR_DEF =
                WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                        new int[]{Calendar.SATURDAY, Calendar.SUNDAY},
                        new String[]{"6/8/03", "6/9/03", "6/10/03", "6/11/03", "6/12/03", "6/13/03", "6/14/03"},
                        new String[]{"6/22/03", "6/23/03", "6/24/03", "6/25/03", "6/26/03", "6/27/03", "6/28/03"});

        public ResourceHelperHierarchy() {
            super(WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                    new int[]{Calendar.SUNDAY, Calendar.TUESDAY},
                    new int[]{/* No specific working days */},
                    new String[]{"6/26/03", "6/27/03"},
                    new String[]{"6/11/03", "6/12/03", "6/13/03"},
                    BASE_CALENDAR_DEF),
                  WorkingTimeCalendarDefinitionTest.makeCalendarDef(
                          new int[]{}, new int[]{Calendar.SATURDAY},
                          new String[]{"6/3/03"},
                          new String[]{"6/13/03", "6/20/03"},
                          BASE_CALENDAR_DEF)
            );
        }

    }

}
