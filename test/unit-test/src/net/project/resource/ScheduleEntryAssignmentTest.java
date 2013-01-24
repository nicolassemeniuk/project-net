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
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.ObjectType;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.schedule.ScheduleEntryWorkingTimeCalendarTest;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.SetMethodWatcher;

public class ScheduleEntryAssignmentTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryAssignmentTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * 
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit 
     * test believes it is comprised of.  In most cases, it is only the current
     * class, though it can include others. 
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit
     * tests. 
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleEntryAssignmentTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    public void testIsModified1() {
        ScheduleEntryAssignment sea = new ScheduleEntryAssignment();
        sea.setPersonID("1");
        sea.setObjectID("2");
        sea.setSavedState();

        //Run the test
        sea.setPersonID("3");
        assertTrue(sea.isModified());
    }

    public void testIsModified2() {
        ScheduleEntryAssignment sea = new ScheduleEntryAssignment();
        sea.setPersonID("1");
        sea.setObjectID("2");
        sea.setSavedState();

        //Run the test
        sea.setObjectID("3");
        assertTrue(sea.isModified());
    }

    public void testIsModified3() {
        ScheduleEntryAssignment sea = new ScheduleEntryAssignment();
        sea.setPersonID("1");
        sea.setObjectID("2");
        sea.setStatus(AssignmentStatus.ASSIGNED);
        sea.setSavedState();

        //Run the test
        sea.setStatus(AssignmentStatus.ACCEPTED);
        assertTrue(sea.isModified());
    }

    public void testClone() throws ClassNotFoundException {
        SetMethodWatcher watcher = new SetMethodWatcher(Class.forName("net.project.resource.ScheduleEntryAssignment"));
        PnCalendar cal = new PnCalendar();

        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setSpaceName("SpaceName");
        watcher.methodCalled("setSpaceName");
        assignment.setPercentAssigned(73);
        watcher.methodCalled("setPercentAssigned");
        watcher.skipMethod("setPercentAssignedDecimal");
        assignment.setWork(new TimeQuantity(3.1, TimeQuantityUnit.DAY));
        watcher.methodCalled("setWork");
        assignment.setPrimaryOwner(true);
        watcher.methodCalled("setPrimaryOwner");
        watcher.skipMethod("setPrimaryOwnerString");
        assignment.setPersonID("92340980923");
        watcher.methodCalled("setPersonID");
        assignment.setPersonID("92340980923");
        watcher.methodCalled("setAssignorID");
        assignment.setObjectType(ObjectType.TASK);
        watcher.methodCalled("setObjectType");
        assignment.setSpaceID("23409828234");
        watcher.methodCalled("setSpaceID");
        assignment.setStatusID(AssignmentStatus.REJECTED.getID());
        watcher.methodCalled("setStatusID");
        watcher.skipMethod("setStatus");
        assignment.setPersonName("lkasdjflakjsdfalksdjf");
        watcher.methodCalled("setPersonName");
        assignment.setPersonName("lkasdjflakjsdfalksdjf");
        watcher.methodCalled("setAssignorName");
        assignment.setPersonRole("lkj23lkslkwnerl");
        watcher.methodCalled("setPersonRole");
        assignment.setObjectID("293847987234");
        watcher.methodCalled("setObjectID");
//        assignment.setTaskPercentComplete(100);
        watcher.methodCalled("setTaskPercentComplete");

        watcher.skipMethod("setObjectName"); //Deprecated

        Date endTime = cal.getTime();
        assignment.setEndTime(endTime);
        watcher.methodCalled("setEndTime");

        cal.add(PnCalendar.DATE, 13);
        Date startTime = cal.getTime();
        assignment.setStartTime(startTime);
        watcher.methodCalled("setStartTime");

        cal.add(PnCalendar.DATE,  11);
        Date actualStart = cal.getTime();
        assignment.setActualStart(actualStart);
        watcher.methodCalled("setActualStart");

        cal.add(PnCalendar.DATE, 7);
        Date actualFinish = cal.getTime();
        assignment.setActualFinish(actualFinish);
        watcher.methodCalled("setActualFinish");

        cal.add(PnCalendar.DATE, 5);
        Date estimatedFinish = cal.getTime();
        assignment.setEstimatedFinish(estimatedFinish);
        watcher.methodCalled("setEstimatedFinish");

        assignment.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        watcher.methodCalled("setTimeZone");

//        TimeQuantity totalWorkComplete = new TimeQuantity(5.5, TimeQuantityUnit.DAY);
//        assignment.setTotalWorkComplete(totalWorkComplete);
//        watcher.methodCalled("setTotalWorkComplete");

        BigDecimal percentComplete = new BigDecimal(67);
        assignment.setPercentComplete(percentComplete);
        watcher.methodCalled("setPercentComplete");

//        TimeQuantity totalWork = new TimeQuantity(55, TimeQuantityUnit.WEEK);
//        assignment.setTotalWork(totalWork);
//        watcher.methodCalled("setTotalWork");

        TimeQuantity workComplete = new TimeQuantity(37, TimeQuantityUnit.WEEK);
        assignment.setWorkComplete(workComplete);
        watcher.methodCalled("setWorkComplete");

        assignment.setComplete(true);
        watcher.methodCalled("setComplete");
        watcher.skipMethod("setWorkCompleteDeltaToStore");
        
        watcher.skipMethod("setModifiedBy");
        watcher.skipMethod("setModifiedDate");
        watcher.skipMethod("setIncludeTaskType");

//        assignment.setDistributedWorkComplete(new TimeQuantity(1, TimeQuantityUnit.DAY));
//        watcher.methodCalled("setDistributedWorkComplete");
        
//        assignment.setPreviousWorkComplete(TimeQuantity.O_DAYS);
//        watcher.methodCalled("setPreviousWorkComplete");
        
//        assignment.setFromShare(true);
//        watcher.methodCalled("setFromShare");
        
//        assignment.setShareReadOnly(true);
//        watcher.methodCalled("setShareReadOnly");

        ScheduleEntryAssignment clone = (ScheduleEntryAssignment)assignment.clone();
        assertEquals("SpaceName", clone.getSpaceName());
        assertEquals(73, clone.getPercentAssignedInt());
        assertEquals(new TimeQuantity(3.1, TimeQuantityUnit.DAY), clone.getWork());
        assertTrue(clone.isPrimaryOwner());
        assertEquals(endTime, clone.getEndTime());
        assertEquals("92340980923", clone.getPersonID());
        assertEquals(ObjectType.TASK, clone.getObjectType());
        assertEquals(startTime, clone.getStartTime());
        assertEquals("23409828234", clone.getSpaceID());
        assertEquals(AssignmentStatus.REJECTED.getID(), clone.getStatusID());
        assertEquals("lkasdjflakjsdfalksdjf", clone.getPersonName());
        assertEquals("lkj23lkslkwnerl", clone.getPersonRole());
        assertEquals("293847987234", clone.getObjectID());
        assertEquals(TimeZone.getTimeZone("America/Los_Angeles"), clone.getTimeZone());
//        assertEquals(100, clone.getTaskPercentComplete());
//        assertEquals(totalWork, clone.getTotalWork());
//        assertEquals(totalWorkComplete, clone.getTotalWorkComplete());
        assertEquals(percentComplete, clone.getPercentComplete());
        assertEquals(workComplete, clone.getWorkComplete());
        assertEquals(actualStart, clone.getActualStart());
        assertEquals(actualFinish, clone.getActualFinish());
        assertEquals(estimatedFinish, clone.getEstimatedFinish());
//        assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), clone.getDistributedWorkComplete());
//        assertEquals(TimeQuantity.O_DAYS, clone.getPreviousWorkComplete());
        assertEquals(true, clone.isComplete());

        //Make sure we've actually registered all the set methods that
        //are available in task.  If we haven't, it means that someone added a
        //method and didn't know to update either this unit test and/or clone
        //and/or equals
        if (!watcher.allMethodsCalled()) {
            String methodsMissed = "The following methods were unaccounted for: \n";
            for (Iterator it = watcher.getUncalledMethod().iterator(); it.hasNext();) {
                String uncalledMethodName = (String)it.next();
                methodsMissed += "  " + uncalledMethodName + "\n";
            }

            fail(methodsMissed);
        }
    }

    /**
     * Tests {@link ScheduleEntryAssignment#parsePercentAssigned}.
     */
    public void testParsePercentAssigned() throws ParseException {

        //
        // US Locale
        //
        Application.updateUserSettings(null, Locale.US);

        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned(null));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned(""));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0"));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("000"));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0.0"));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0."));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0,000"));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0,0.0"));
        // % works because all non-number values are discarded after the first
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0,0.0%"));
        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0X"));
        assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("100"));
        assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("100%"));
        // Position of grouping symbol doesn't matter; it is ignored
        assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("1,00.00%"));
        assertEquals(1000, ScheduleEntryAssignment.parsePercentAssigned("1,000.%"));

        // Check rounding
        assertEquals(1, ScheduleEntryAssignment.parsePercentAssigned("1.455555"));
        assertEquals(2, ScheduleEntryAssignment.parsePercentAssigned("1.5"));

        // Invalid
        try {
            ScheduleEntryAssignment.parsePercentAssigned("X");
            fail("Expected ParseException");
        } catch (ParseException e) {
            // Expected
        }

        //
        // Germany Locale
        //
        Application.updateUserSettings(null, Locale.GERMANY);

        assertEquals(0, ScheduleEntryAssignment.parsePercentAssigned("0.000,0"));
        assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("100"));
        assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("100%"));
        //assertEquals(100, ScheduleEntryAssignment.parsePercentAssigned("1.00,00%"));
        //assertEquals(1000, ScheduleEntryAssignment.parsePercentAssigned("1.000,%"));

        // Check rounding
        assertEquals(1, ScheduleEntryAssignment.parsePercentAssigned("1,455555"));
        assertEquals(2, ScheduleEntryAssignment.parsePercentAssigned("1,5"));

    }


    /**
     * Tests {@link ScheduleEntryAssignment#getDateCalculator(net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * with various work amounts and assignment percentages.
     */
    public void tesGetDateCalculatorGetFinishDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TestWorkingTimeCalendarProvider provider;
        ScheduleEntryAssignment assignment;

        provider = new TestWorkingTimeCalendarProvider();
        assignment = new ScheduleEntryAssignment();

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 100%
        // Work: 8 hrs
        // Expected:  Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(100);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 100%
        // Work: 0 hrs
        // Expected:  Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(100);
        assignment.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 50%
        // Work: 8 hrs
        // Expected:  Tuesday May 6th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(50);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 10%
        // Work: 8 hrs
        // Expected: Friday May 16th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(10);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateFinishDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 0%
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        assignment.setPercentAssigned(0);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateFinishDate(startDate));

    }


    /**
     * Tests {@link ScheduleEntryAssignment#getDateCalculator(net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * with various work amounts and assignment percentages.
     */
    public void testGetDateCalcultorStartDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date expected;
        TestWorkingTimeCalendarProvider provider;
        ScheduleEntryAssignment assignment;

        provider = new TestWorkingTimeCalendarProvider();
        assignment = new ScheduleEntryAssignment();

        // StartDate: Monday May 5th @ 5:00 PM
        // Assigned: 100%
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(100);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 5:00 PM
        // Assigned: 100%
        // Work: 0 hrs
        // Expected: Monday May 5th @ 5:00 PM
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 17, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(100);
        assignment.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateStartDate(startDate));
        //
        // 50% Assignment
        //

        // StartDate: Tuesday May 6th @ 5:00 PM
        // Assigned: 50%
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 6, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(50);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateStartDate(startDate));

        // StartDate: Friday May 16th @ 5:00 PM
        // Assigned: 10%
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 16, 17, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        expected = cal.getTime();
        assignment.setPercentAssigned(10);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateStartDate(startDate));

        // StartDate: Monday May 5th @ 8:00 AM
        // Assigned: 0%
        // Work: 8 hrs
        // Expected: Monday May 5th @ 8:00 AM
        cal.set(2003, Calendar.MAY, 5, 8, 0);
        startDate = cal.getTime();
        expected = cal.getTime();
        assignment.setPercentAssigned(0);
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(expected, assignment.getDateCalculator(provider).calculateStartDate(startDate));

    }


    /**
     * Tests {@link ScheduleEntryAssignment#getDaysWorked(java.util.Date, net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}.
     */
    public void testGetDaysWorked() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        ScheduleEntryAssignment assignment;
        TestWorkingTimeCalendarProvider provider;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = new TestWorkingTimeCalendarProvider();

        //
        // 100% assignment
        //
        assignment = makeAssignment(100, TimeZone.getTimeZone("America/Los_Angeles"), helper.calendarDef1, provider);

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 2 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("2.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

        //
        // 25%
        //
        assignment = makeAssignment(25, TimeZone.getTimeZone("America/Los_Angeles"), helper.calendarDef1, provider);

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 8 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("8.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

        //
        // 0%
        //
        assignment = makeAssignment(0, TimeZone.getTimeZone("America/Los_Angeles"), helper.calendarDef1, provider);

        // StartDate: Monday June 2nd @ 8:00 AM
        // Work: 16 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

    }

    /**
     * Tests {@link ScheduleEntryAssignment#getDaysWorked(java.util.Date, net.project.calendar.workingtime.IWorkingTimeCalendarProvider)}
     * for assignee in a different time zone.
     * <p>
     * The time zone doesn't actually affect duration for an individual assignee.
     * </p>
     */
    public void testGetDurationCalculatorTimeZones() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        ScheduleEntryAssignment assignment;
        TestWorkingTimeCalendarProvider provider;

        ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy helper = new ScheduleEntryWorkingTimeCalendarTest.ResourceHelperHierarchy();
        provider = new TestWorkingTimeCalendarProvider();

        //
        // 100% assignment, Europe/London (GMT)
        //
        assignment = makeAssignment(100, TimeZone.getTimeZone("Europe/London"), helper.calendarDef1, provider);

        // StartDate: Monday June 2nd @ 8:00 AM PST (4:00 PM GMT)
        // Work: 0 hours
        // Expected: 0 days
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(0, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("0.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

        // StartDate: Monday June 2nd @ 8:00 AM (4:00 PM GMT)
        // Work: 8 hours
        // Expected: 1 day
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(new BigDecimal("1.0000000000"), assignment.getDaysWorked(startDate, provider).getTotalDays());

    }

    /**
     * Tests {@link ScheduleEntryAssignment#calculateWork}.
     */
    public void testCalculateWork() {

        Date startDate;
        Date endDate;
        TimeQuantity expected;

        TimeZone timeZone;
        TestWorkingTimeCalendarProvider provider;
        WorkingTimeCalendarDefinition calendarDef;
        ScheduleEntryAssignment assignment;

        timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        provider = new TestWorkingTimeCalendarProvider();
        // Default calendar
        calendarDef = WorkingTimeCalendarDefinitionTest.makeCalendarDefForNonWorkingDays(new int[] {Calendar.SATURDAY, Calendar.SUNDAY});

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Assigned: 100%
        // Expected: 8 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        assignment = makeAssignment(100, timeZone, calendarDef, provider);
        assignment.calculateWork(provider, startDate, endDate);
        assertEquals(expected, assignment.getWork());

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Assigned: 50%
        // Expected: 4 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        assignment = makeAssignment(50, timeZone, calendarDef, provider);
        assignment.calculateWork(provider, startDate, endDate);
        assertEquals(expected, assignment.getWork());

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Assigned: 10%
        // Expected: 0.8 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(new BigDecimal("0.8"), TimeQuantityUnit.HOUR);
        assignment = makeAssignment(10, timeZone, calendarDef, provider);
        assignment.calculateWork(provider, startDate, endDate);
        assertEquals(expected, assignment.getWork());

        // Start: Monday April 5th 2004 @ 8:00 AM
        // End: Monday April 5th 2004 @ 5:00 PM
        // Assigned: 200%
        // Expected: 16 hours
        startDate = makeDateTime("4/5/04 8:00 AM");
        endDate = makeDateTime("4/5/04 5:00 PM");
        expected = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        assignment = makeAssignment(200, timeZone, calendarDef, provider);
        assignment.calculateWork(provider, startDate, endDate);
        assertEquals(expected, assignment.getWork());

    }


    /**
     * Tests {@link ScheduleEntryAssignment#calculatePercentage(net.project.calendar.workingtime.IWorkingTimeCalendarProvider, java.util.Date, java.util.Date)}.
     */
    public void testCalculatePercentage() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate;
        Date endDate;
        ScheduleEntryAssignment assignment;
        TestWorkingTimeCalendarProvider provider;

        provider = new TestWorkingTimeCalendarProvider();

        assignment = makeAssignment(100, TimeZone.getTimeZone("America/Los_Angeles"), WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition(), provider);

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Work: 8 hours
        // Expected: 1.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assignment.calculatePercentage(provider, startDate, endDate);
        assertEquals(new BigDecimal("1.00"), assignment.getPercentAssignedDecimal());

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Work: 1 hour
        // Expected: 0.13
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assignment.setWork(new TimeQuantity(1, TimeQuantityUnit.HOUR));
        assignment.calculatePercentage(provider, startDate, endDate);
        assertEquals(new BigDecimal("0.13"), assignment.getPercentAssignedDecimal());

        // Start Date: Monday June 2nd @ 8:00 AM
        // End Date: Monday June 2nd @ 5:00 PM
        // Work: 40 hours
        // Expected: 5.00
        cal.set(2003, Calendar.JUNE, 2, 8, 0);
        startDate = cal.getTime();
        cal.set(2003, Calendar.JUNE, 2, 17, 0);
        endDate = cal.getTime();
        assignment.setWork(new TimeQuantity(40, TimeQuantityUnit.HOUR));
        assignment.calculatePercentage(provider, startDate, endDate);
        assertEquals(new BigDecimal("5.00"), assignment.getPercentAssignedDecimal());

    }

    public void testGetWorkRemaining() {
        TestWorkingTimeCalendarProvider provider;
        provider = new TestWorkingTimeCalendarProvider();
        ScheduleEntryAssignment assignment = makeAssignment(100, TimeZone.getTimeZone("America/Los_Angeles"), WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition(), provider);

        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assignment.setWorkComplete(new TimeQuantity(4, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), assignment.getWorkRemaining());

        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assignment.setWorkComplete(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assignment.getWorkRemaining());

        assignment.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));
        assignment.setWorkComplete(new TimeQuantity(10, TimeQuantityUnit.HOUR));
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), assignment.getWorkRemaining());
    }

    //
    // Helper Methods
    //

    private static Date makeDateTime(String dateString) {
        return WorkingTimeCalendarDefinitionTest.makeDateTime(dateString);
    }

    /**
     * Makes an assignment with the specified working time calendar definition.
     * <p>
     * Updates the specified provider to contain the calendar for the assignee.
     * </p>
     * @param percentage the percentage assigned
     * @param timeZone the time zone for the assignee
     * @param calendarDef the working time calendar definition to be provided for
     * the assignee
     * @param provider the provider to update such that the calendar definition will
     * be returned for the assignee
     * @return the assignment
     */
    private static ScheduleEntryAssignment makeAssignment(int percentage, TimeZone timeZone,
            WorkingTimeCalendarDefinition calendarDef, TestWorkingTimeCalendarProvider provider) {
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPersonID("1");
        assignment.setPercentAssigned(percentage);
        assignment.setTimeZone(timeZone);
        provider.addResourceCalendarDefintion("1", calendarDef);
        return assignment;
    }
}
