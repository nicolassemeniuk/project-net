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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.gui.html.IHTMLOption;

/**
 * Tests {@link TaskCalculationType}.
 * 
 * @author Tim Morrow
 */
public class TaskCalculationTypeTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public TaskCalculationTypeTest(String s) {
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
        TestSuite suite = new TestSuite(TaskCalculationTypeTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link TaskCalculationType#forID(java.lang.String)}.
     */
    public void testForID() {

        assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, TaskCalculationType.forID("10"));
        assertEquals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN, TaskCalculationType.forID("20"));
        assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, TaskCalculationType.forID("30"));
        assertEquals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN, TaskCalculationType.forID("40"));
        assertEquals(TaskCalculationType.FIXED_WORK, TaskCalculationType.forID("50"));

        // Failures
        try {
            TaskCalculationType.forID(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            TaskCalculationType.forID("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            TaskCalculationType.forID("0");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    public void testEquals() {
        assertFalse(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.equals(null));
        assertFalse(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.equals(new Object()));
        assertFalse(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.equals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN));
        assertTrue(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.equals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN));
        assertTrue(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN.equals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN));
        assertTrue(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.equals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN));
        assertTrue(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN.equals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN));
        assertTrue(TaskCalculationType.FIXED_WORK.equals(TaskCalculationType.FIXED_WORK));
    }

    public void testHashCode() {
        // Consistency (two invocations are same value)
        // Equal objects, equal hashcode
        assertTrue(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.hashCode() == TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.hashCode());
    }

    public void testIsFixedDuration() {
        assertFalse(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.isFixedDuration());
        assertFalse(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN.isFixedDuration());
        assertTrue(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.isFixedDuration());
        assertTrue(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN.isFixedDuration());
        assertFalse(TaskCalculationType.FIXED_WORK.isFixedDuration());
    }
    public void testIsFixedUnit() {
        assertTrue(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.isFixedUnit());
        assertTrue(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN.isFixedUnit());
        assertFalse(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.isFixedUnit());
        assertFalse(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN.isFixedUnit());
        assertFalse(TaskCalculationType.FIXED_WORK.isFixedUnit());
    }

    public void testIsFixedWork() {
        assertFalse(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.isFixedWork());
        assertFalse(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN.isFixedWork());
        assertFalse(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.isFixedWork());
        assertFalse(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN.isFixedWork());
        assertTrue(TaskCalculationType.FIXED_WORK.isFixedWork());
    }

    public void testIsEffortDriven() {
        assertTrue(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN.isEffortDriven());
        assertFalse(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN.isEffortDriven());
        assertTrue(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.isEffortDriven());
        assertFalse(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN.isEffortDriven());
        assertTrue(TaskCalculationType.FIXED_WORK.isEffortDriven());
    }

    public void testGetFixedElementHTMLOptions() {

        // There are three types
        assertEquals(3, TaskCalculationType.getFixedElementHTMLOptions().size());

        // Order is Duration, Unit, Work
        // The order doesn't necessarily matter but it is deliberate
        assertEquals(TaskCalculationType.FixedElement.DURATION, TaskCalculationType.getFixedElementHTMLOptions().get(0));
        assertEquals(TaskCalculationType.FixedElement.UNIT, TaskCalculationType.getFixedElementHTMLOptions().get(1));
        assertEquals(TaskCalculationType.FixedElement.WORK, TaskCalculationType.getFixedElementHTMLOptions().get(2));

        // It must be unmodifiable
        try {
            TaskCalculationType.getFixedElementHTMLOptions().remove(0);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }

        // All types must be IHTMLOption; we can tell by converting to an IHTMLOption array
        TaskCalculationType.getFixedElementHTMLOptions().toArray(new IHTMLOption[3]);
    }

    public void testMakeFromComponents() {

        // Null type
        try {
            TaskCalculationType.makeFromComponents(null, true);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected

        }

        assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.DURATION, true));
        assertEquals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.DURATION, false));
        assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.UNIT, true));
        assertEquals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.UNIT, false));
        // Boolean is ignored for Fixed Work
        assertEquals(TaskCalculationType.FIXED_WORK, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.WORK, true));
        assertEquals(TaskCalculationType.FIXED_WORK, TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.WORK, false));

    }
}
