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
package net.project.schedule.report.resourceallocation;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

public class WorkCalculatorTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public WorkCalculatorTest(String s) {
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
        TestSuite suite = new TestSuite(WorkCalculatorTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    public void testGetHoursWorkedBetweenDates() {
        TimeQuantity tq;
        WorkingTimeCalendarDefinition calendarDef;
        TestWorkingTimeCalendarProvider calendarProvider;

        String resourceID = "1";

        // Standard working time calendar
        calendarDef = WorkingTimeCalendarDefinitionTest.
            makeCalendarDefForNonWorkingDays(new int[] {Calendar.SUNDAY, Calendar.SATURDAY});

        // A calendar provider with a single calendar for resource "1"
        calendarProvider = new TestWorkingTimeCalendarProvider();
        calendarProvider.addResourceCalendarDefintion(resourceID, calendarDef);

        final WorkCalculator workCalculator = new WorkCalculator(calendarProvider);

        //Tests that occur on one day
        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            100);
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 9:00 AM"),
            100);
        assertEquals(new TimeQuantity(1, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 9:00 AM"),
            50);
        assertEquals(new TimeQuantity(0.5, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 9:00 AM"),
            0);
        assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 5:00 PM"),
            100);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 6:00 PM"),
            100);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        //Tests that span two sequential days
        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/7/03 8:00 AM"),
            100);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/7/03 9:00 AM"),
            100);
        assertEquals(new TimeQuantity(9, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/7/03 9:00 AM"),
            50);
        assertEquals(new TimeQuantity(4.5, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/7/03 5:00 PM"),
            75);
        assertEquals(new TimeQuantity(12, TimeQuantityUnit.HOUR), tq);

        //Tests that span three sequential days (this is handled differently than 2)
        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/8/03 5:00 PM"),
            25);
        assertEquals(new TimeQuantity(6, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/6/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/8/03 12:00 PM"),
            60);
        assertEquals(new TimeQuantity(12, TimeQuantityUnit.HOUR), tq);

        //Tests that span the weekend
        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/10/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/13/03 8:00 AM"),
            100);
        assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), tq);

        tq = workCalculator.getHoursWorkedBetweenDates(resourceID,
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/10/03 8:00 AM"),
            WorkingTimeCalendarDefinitionTest.makeDateTime("1/13/03 5:00 PM"),
            25);
        assertEquals(new TimeQuantity(4, TimeQuantityUnit.HOUR), tq);
    }

}
