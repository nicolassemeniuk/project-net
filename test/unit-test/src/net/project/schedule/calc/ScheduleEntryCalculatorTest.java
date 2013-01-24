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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionTest;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

/**
 * Tests {@link ScheduleEntryCalculator}.
 * <p>
 * <b>Note:</b> This doesn't test the actual calculations, but tests that
 * the appropriate properties of the ScheduleEntry are updated.
 * </p>
 * 
 * @author Tim Morrow
 */
public class ScheduleEntryCalculatorTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ScheduleEntryCalculatorTest(String s) {
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
        TestSuite suite = new TestSuite(ScheduleEntryCalculatorTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link ScheduleEntryCalculator#durationChanged(net.project.util.TimeQuantity)}.
     */
    public void testDurationChanged() throws PersistenceException {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity newDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        BigDecimal expectedAssignmentPercentage1;
        BigDecimal expectedAssignmentPercentage2;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;

        // Increase duration to 2 days

        // Fixed Unit, Effort Driven
        // Work increases to 16 hours
        // Assignment 1: 12 hours @ 75%
        // Assignment 2: 4 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        testDurationChangedFixedUnitDuration(helper, provider);

        // Fixed Unit, Non-effort Driven
        // Same As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        testDurationChangedFixedUnitDuration(helper, provider);

        // Fixed Duration, Effort Driven
        // Same As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        testDurationChangedFixedUnitDuration(helper, provider);

        // Fixed Duration, Non-effort Driven
        // Same As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        testDurationChangedFixedUnitDuration(helper, provider);

        // Fixed Work
        // Work remains at 8 hours
        // Assignment 1: 6 hours @ 37.5%
        // Assignment 2: 2 hours @ 12.5%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);

        newDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.375");
        expectedAssignmentPercentage2 = new BigDecimal("0.125");
        new ScheduleEntryCalculator(scheduleEntry, provider).durationChanged(newDuration);
        assertEquals(expectedWork, scheduleEntry.getWorkTQ());
        assertEquals(newDuration, scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

    }

    /**
     * Tests {@link ScheduleEntryCalculator#durationChanged(net.project.util.TimeQuantity)}
     * for Fixed Unit and Fixed Duration (both Effort Driven and Non Effort Driven) since
     * they all result in the same values.
     * @param helper the helper providing the schedule entry and assignments
     * @param provider the working time calendar provider
     */
    private void testDurationChangedFixedUnitDuration(Helper helper,
            TestWorkingTimeCalendarProvider provider) throws PersistenceException{

        TimeQuantity newDuration;
        TimeQuantity expectedWork;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        BigDecimal expectedAssignmentPercentage1;
        BigDecimal expectedAssignmentPercentage2;

        newDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).durationChanged(newDuration);
        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(newDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});
    }

    /**
     * Tests {@link ScheduleEntryCalculator#workChanged(net.project.util.TimeQuantity)}.
     */
    public void testWorkChanged() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity newWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        BigDecimal expectedAssignmentPercentage1;
        BigDecimal expectedAssignmentPercentage2;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;

        // Change work to 16 hours
        newWork = new TimeQuantity(16, TimeQuantityUnit.HOUR);

        // Fixed Unit, Effort Driven
        // Duration increases to 2 days
        // Assignment 1: 12 hours @ 75%
        // Assignment 2: 4 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).workChanged(newWork);

        assertEquals(newWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});


        // Fixed Unit, Non-effort Driven
        // As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).workChanged(newWork);

        assertEquals(newWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});


        // Fixed Duration, Effort Driven
        // Assignment percentages change
        // Assignment 1: 12 hours @ 150%
        // Assignment 2: 4 hours @ 50%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.50000");
        expectedAssignmentPercentage2 = new BigDecimal("0.50000");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).workChanged(newWork);

        assertEquals(newWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

        // Fixed Duration, Non-effort Driven
        // As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.50000");
        expectedAssignmentPercentage2 = new BigDecimal("0.50000");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).workChanged(newWork);

        assertEquals(newWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

        // Fixed Work
        // Duration increases to 2 days
        // Same as Fixed Unit
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);
        expectedDuration = new TimeQuantity(2, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).workChanged(newWork);

        assertEquals(newWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

    }

    /**
     * Tests {@link ScheduleEntryCalculator#assignmentPercentageChanged(java.math.BigDecimal, net.project.resource.ScheduleEntryAssignment)}.
     */
    public void testAssignmentPercentageChanged() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        BigDecimal newPercentage;
        BigDecimal expectedAssignmentPercentage2;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;

        // Change Assignment 1 percentage to 50%
        newPercentage = new BigDecimal("0.50");

        // Fixed Unit, Effort Driven
        // Duration changes to 1.5 days
        // Assignment 1: 6 hours @ 50%
        // Assignment 2: 2 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.5, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentPercentageChanged(newPercentage, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{newPercentage, expectedAssignmentPercentage2});


        // Fixed Unit, Non-effort Driven
        // As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.5, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentPercentageChanged(newPercentage, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{newPercentage, expectedAssignmentPercentage2});


        // Fixed Duration, Effort Driven
        // Work changes to 6 hours
        // Assignment 1: 4 hours @ 50%
        // Assignment 2: 2 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentPercentageChanged(newPercentage, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{newPercentage, expectedAssignmentPercentage2});

        // Fixed Duration, Non-effort Driven
        // As above
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentPercentageChanged(newPercentage, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{newPercentage, expectedAssignmentPercentage2});

        // Fixed Work
        // As Fixed Unit
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.5, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentPercentageChanged(newPercentage, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{newPercentage, expectedAssignmentPercentage2});

    }

    public void testAssignmentWorkChanged() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity newWork;
        BigDecimal expectedAssignmentPercentage1;
        BigDecimal expectedAssignmentPercentage2;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;

        // Change Assignment 1 work to 8 hours
        newWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);

        // Fixed Unit, Effort Driven
        // Fixed Unit, Non-Effort Driven
        // Fixed Work
        // All the same results

        // Duration changes to 1.33 days
        // Work changes to 10 hours
        // Assignment 1: 8 hours @ 75%
        // Assignment 2: 2 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(10, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.3333333333, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentWorkChanged(newWork, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration.toString(), helper.scheduleEntry.getDurationTQ().toString());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

        // Fixed Duration, Effort Driven
        // Fixed Duration, Non-Effort Driven
        // Work changes to 10 hours
        // Assignment 1: 8 hours @ 100%
        // Assignment 2: 2 hours @ 25%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(10, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.0000000000");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentWorkChanged(newWork, helper.assignment1);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2});

    }

    /**
     * Tests {@link ScheduleEntryCalculator#assignmentAdded(java.math.BigDecimal, net.project.resource.ScheduleEntryAssignment)}.
     */
    /*public void testAssignmentAdded() throws Exception {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork1;
        TimeQuantity expectedAssignmentWork2;
        TimeQuantity expectedAssignmentWork3;
        BigDecimal expectedAssignmentPercentage1;
        BigDecimal expectedAssignmentPercentage2;
        BigDecimal expectedAssignmentPercentage3;
        BigDecimal newPercentage;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;


        // Add assignment at 50%
        // Fixed Unit, Effort Driven
        // Duration is 0.67 days
        // Assignment 1: 4 hours @ 75%
        // Assignment 2: 1.33 hours @ 25%
        // Assignment 3: 2.67 @ 50%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(0.6666666667, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(4.0000000000, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(1.3333333333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(2.6666666667, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentAdded(newPercentage, helper.assignmentToAdd);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration.toString(), helper.scheduleEntry.getDurationTQ().toString());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2, newPercentage});


        // Add assignment at 50%
        // Fixed Unit, Non-effort Driven
        // Work is 12 hours
        // Assignment 1: 6 hours @ 75%
        // Assignment 2: 2 hours @ 25%
        // Assignment 3: 4 @ 50%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentAdded(newPercentage, helper.assignmentToAdd);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2, newPercentage});


        // Fixed Duration, Effort Driven
        // (Percentage cannot be specified)
        // Assignment 1: 3.43 hours @ 43%
        // Assignment 2: 1.14 hours @ 14% (MS Project says 1.15, but is wrong)
        // Assignment 3: 3.43 @ 43%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.0, TimeQuantityUnit.DAY);//<=> (1.14/.14)/8
        expectedAssignmentWork1 = new TimeQuantity(3.429, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(1.143, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(3.429, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.429");
        expectedAssignmentPercentage2 = new BigDecimal("0.143");
        expectedAssignmentPercentage3 = new BigDecimal("0.429");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentAdded(null, helper.assignmentToAdd);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2, expectedAssignmentPercentage3});

        // Add assignment at 50%
        // Fixed Duration, Non-effort Driven
        // As Fixed Unit, Non-Effort Driven (only because no WT cal involved)
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(12, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(2, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentAdded(newPercentage, helper.assignmentToAdd);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2, newPercentage});

        // Add assignment at 50%
        // Fixed Work
        // As Fixed Unit, Effort Driven
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);
        newPercentage = new BigDecimal("0.50");
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(0.6666666667, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(4, TimeQuantityUnit.HOUR);
        expectedAssignmentWork2 = new TimeQuantity(1.3333333333, TimeQuantityUnit.HOUR);
        expectedAssignmentWork3 = new TimeQuantity(2.6666666667, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        expectedAssignmentPercentage2 = new BigDecimal("0.25");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentAdded(newPercentage, helper.assignmentToAdd);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration.toString(), helper.scheduleEntry.getDurationTQ().toString());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1, expectedAssignmentWork2, expectedAssignmentWork3});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1, expectedAssignmentPercentage2, newPercentage});

        //First assignment added to a scheduleEntry which has greater work than
        //duration.
        //Fixed Unit, Effort-Driven
        helper = new Helper(provider);
        ScheduleEntry entry = new Task();
        entry.setID(WellKnownObjects.TEST_TASK_ID);
        entry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        entry.setWork(new TimeQuantity(16, TimeQuantityUnit.HOUR));
        entry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        entry.setStartTimeD(WorkingTimeCalendarDefinitionTest.makeDateTime("04/05/04 8:00 AM"));
        entry.setEndTimeD(WorkingTimeCalendarDefinitionTest.makeDateTime("04/05/04 5:00 PM"));

        new ScheduleEntryCalculator(entry, provider).assignmentAdded(new BigDecimal("1.00"), helper.assignmentToAdd);

        assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), entry.getWorkTQ());
        assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), entry.getDurationTQ());
        assertEquals(100, helper.assignmentToAdd.getPercentAssignedInt());
    }*/

    /**
     * Tests {@link ScheduleEntryCalculator#assignmentRemoved(net.project.resource.ScheduleEntryAssignment)}.
     */
    public void testAssignmentRemoved() {

        TestWorkingTimeCalendarProvider provider;
        ScheduleEntry scheduleEntry;
        TimeQuantity expectedWork;
        TimeQuantity expectedDuration;
        TimeQuantity expectedAssignmentWork1;
        BigDecimal expectedAssignmentPercentage1;

        provider = new TestWorkingTimeCalendarProvider();
        Helper helper;

        // Remove Assignment 2

        // Fixed Unit, Effort Driven
        // Duration: 1.33 days
        // Work: 8 hours
        // Assignment 1: 8 hours @ 75%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.3333333333, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentRemoved(helper.assignment2);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration.toString(), helper.scheduleEntry.getDurationTQ().toString());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1});

        // Fixed Unit, Non-effort Driven
        // Duration: 1 day
        // Work: 6 hours
        // Assignment 1: 6 hours @ 75%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentRemoved(helper.assignment2);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1});

        // Fixed Duration, Effort Driven
        // Duration: 1 day
        // Work: 8 hours
        // Assignment 1: 8 hours @ 100%
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("1.0000000000");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentRemoved(helper.assignment2);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1});

        // Fixed Duration, Non-effort Driven
        // Same as Fixed Unit, Non-effort Driven
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        expectedWork = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(6, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentRemoved(helper.assignment2);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration, helper.scheduleEntry.getDurationTQ());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1});

        // Fixed Work
        // Same as Fixed Unit, Effort Driven
        helper = new Helper(provider);
        scheduleEntry = helper.scheduleEntry;
        scheduleEntry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);
        expectedWork = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedDuration = new TimeQuantity(1.3333333333, TimeQuantityUnit.DAY);
        expectedAssignmentWork1 = new TimeQuantity(8, TimeQuantityUnit.HOUR);
        expectedAssignmentPercentage1 = new BigDecimal("0.75");
        new ScheduleEntryCalculator(helper.scheduleEntry, provider).assignmentRemoved(helper.assignment2);

        assertEquals(expectedWork, helper.scheduleEntry.getWorkTQ());
        assertEquals(expectedDuration.toString(), helper.scheduleEntry.getDurationTQ().toString());
        helper.assertWork(new TimeQuantity[]{expectedAssignmentWork1});
        helper.assertPercentage(new BigDecimal[]{expectedAssignmentPercentage1});


    }

    //
    // Nested, top-level classes
    //

    /**
     * Creates a ScheduleEntry with test values.
     * <p>
     * Provider is updated with the assignment working time calendar definitions.
     * </p>
     * <p>
     * <ul>
     * <li>Start Date: Monday April 5th @ 8:00 AM
     * <li>End Date: Monday April 5th @ 5:00 PM
     * <li>Duration: 1 day
     * <li>Work: 8 hours
     * <li>Two assignments:
     * <ul>
     * <li>Assignment 1: 6 hours @ 75%, default wt cal
     * <li>Assignment 2: 2 hours @ 25%, default wt cal
     * </ul>
     * </ul>
     * </p>
     */
    private static class Helper {

        final ScheduleEntry scheduleEntry;
        private final ScheduleEntryAssignment assignment1;
        private final ScheduleEntryAssignment assignment2;
        private final ScheduleEntryAssignment assignmentToAdd;

        Helper(TestWorkingTimeCalendarProvider provider) {
            this.scheduleEntry = new Task();
            scheduleEntry.setID(WellKnownObjects.TEST_TASK_ID);
            scheduleEntry.setStartTimeD(WorkingTimeCalendarDefinitionTest.makeDateTime("04/05/04 8:00 AM"));
            scheduleEntry.setEndTimeD(WorkingTimeCalendarDefinitionTest.makeDateTime("04/05/04 5:00 PM"));
            scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
            scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));

            assignment1 = new ScheduleEntryAssignment();
            assignment1.setPersonID("1");
            assignment1.setWork(new TimeQuantity(6, TimeQuantityUnit.HOUR));
            assignment1.setPercentAssigned(75);
            provider.addResourceCalendarDefintion("1", WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

            assignment2 = new ScheduleEntryAssignment();
            assignment2.setPersonID("2");
            assignment2.setWork(new TimeQuantity(2, TimeQuantityUnit.HOUR));
            assignment2.setPercentAssigned(25);
            provider.addResourceCalendarDefintion("2", WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

            // Assignment 3 _not_ added to task; it is used for adding
            assignmentToAdd = new ScheduleEntryAssignment();
            assignmentToAdd.setPersonID("3");
            provider.addResourceCalendarDefintion("3", WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());

            scheduleEntry.addAssignments(Arrays.asList(new ScheduleEntryAssignment[]{assignment1, assignment2}));

            this.scheduleEntry.calculateEndDate(provider);
        }

        /**
         * Sets the task work and updates assignment work.
         * @param work the task work
         */
        void setWork(TimeQuantity work) {
            scheduleEntry.setWork(work);
            assignment1.setWork(work.multiply(assignment1.getPercentAssignedDecimal()));
            assignment2.setWork(work.multiply(assignment2.getPercentAssignedDecimal()));
        }

        void assertWork(TimeQuantity[] workValues) {
            List workList = Arrays.asList(workValues);
            assertEquals("Number of work values must equal the number of assignments", scheduleEntry.getAssignments().size(), workList.size());

            int index = 0;
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext(); index++) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                assertEquals("Assignment " + index + " work incorrect", workList.get(index).toString(), nextAssignment.getWork().toString());
            }
        }

        void assertPercentage(BigDecimal[] percentageValues) {
            List<BigDecimal> percentageList = Arrays.asList(percentageValues);
            assertEquals("Number of percentages values must equal the number of assignments", scheduleEntry.getAssignments().size(), percentageList.size());

            int index = 0;
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext(); index++) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                //sjmittal: keeping a marign of error of .001 other test case may fail in small roundings
                assertEquals("Assignment " + index + " percentage incorrect", percentageList.get(index).doubleValue(), nextAssignment.getPercentAssignedDecimal().doubleValue(), 0.001);
            }
        }

    }

    /**
     * Test that work gets rounded off correctly at all times.
     */
    public void testAssignmentWorkRounding() throws Exception {
        //sjmittal: note* work does not get distributed evenly. I have checked the test cases under
        //AssignmentAdderTest.java to verify this. Hence changing the test data
        
        ScheduleEntryDurationModifierTest.Helper helper = new ScheduleEntryDurationModifierTest.Helper();
        ScheduleEntry entry = helper.makeTaskNoAssignment(new TimeQuantity(1, TimeQuantityUnit.HOUR), new TimeQuantity(32, TimeQuantityUnit.HOUR));
        entry.setTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(entry, helper.provider);


        ScheduleEntryAssignment assn1 = new ScheduleEntryAssignment();
        assn1.setPersonID("1");
        calc.assignmentAdded(new BigDecimal("1.00"), assn1);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ().setScale(3));

        ScheduleEntryAssignment assn2 = new ScheduleEntryAssignment();
        assn2.setPersonID("2");
        calc.assignmentAdded(new BigDecimal("1.00"), assn2);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn2.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn2.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ().setScale(3));

        ScheduleEntryAssignment assn3 = new ScheduleEntryAssignment();
        assn3.setPersonID("3");
        calc.assignmentAdded(new BigDecimal("1.00"), assn3);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("8.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn2.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("8.000", TimeQuantityUnit.HOUR), assn2.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn3.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn3.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ());

        ScheduleEntryAssignment assn4 = new ScheduleEntryAssignment();
        assn4.setPersonID("4");
        calc.assignmentAdded(new BigDecimal("1.00"), assn4);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("4.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn2.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("4.000", TimeQuantityUnit.HOUR), assn2.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn3.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("8.000", TimeQuantityUnit.HOUR), assn3.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn4.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn4.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ());

        calc.assignmentRemoved(assn4);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("8.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn2.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("8.000", TimeQuantityUnit.HOUR), assn2.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn3.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn3.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ());

        calc.assignmentRemoved(assn3);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new BigDecimal("1.00"), assn2.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("16.000", TimeQuantityUnit.HOUR), assn2.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ().setScale(3));

        calc.assignmentRemoved(assn2);
        assertEquals(new BigDecimal("1.00"), assn1.getPercentAssignedDecimal());
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), assn1.getWork().setScale(3));
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ().setScale(3));

        calc.assignmentRemoved(assn1);
        assertEquals(new TimeQuantity("32.000", TimeQuantityUnit.HOUR), entry.getWorkTQ().setScale(3));
    }
}
