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

import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Tests <code>TaskDependencyTest</code>.
 * @author
 * @since
 */
public class TaskDependencyTest extends TestCase {

    public TaskDependencyTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TaskDependencyTest.class);

        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link TaskDependency#clone}.
     */
    public void testClone() {

        ScheduleEntry successor;
        ScheduleEntry predecessor;
        TaskDependency dep;
        TaskDependency expected;

        successor = new Task();
        successor.setID("2");
        predecessor = new Task();
        predecessor.setID("1");

        // Check same values on clone
        dep = new TaskDependency(successor, predecessor, TaskDependencyType.FINISH_TO_START, new TimeQuantity(0, TimeQuantityUnit.DAY));
        expected = new TaskDependency(successor, predecessor, TaskDependencyType.FINISH_TO_START, new TimeQuantity(0, TimeQuantityUnit.DAY));
        assertEquals(expected, dep);
        assertEquals(expected, dep.clone());

        // Issue:
        // Problem occurred when CyclicDependencyDetector object not deep-cloned; setting dependency ID
        // would modify the original object too
        // This unit test would fail
        dep = new TaskDependency(successor, predecessor, TaskDependencyType.FINISH_TO_START, new TimeQuantity(0, TimeQuantityUnit.DAY));
        TaskDependency cloned = (TaskDependency) dep.clone();
        dep.setDependencyID("3");
        // Both should not be equal; only the first should have changed
        assertFalse("Expected cloned TaskDependency to be independent of original object", dep.equals(cloned));

    }

    /**
     * Tests {@link TaskDependency#setLag(Number, String)}.
     */
    public void testSetLagString() throws ParseException {

        TaskDependency td;

        // BFD-2029
        // 0.5555 stored imprecisely, when scaled to 3 resulting in 0.555 instead of 0.556
        // Problem is in TimeQuantity(Number, TimeQuantityUnit) constructor
        td = new TaskDependency();
        td.setLag(new BigDecimal("0.5555"), "8");
        assertEquals("0.5555", td.getLag().getAmount().toString());
        assertEquals("0.556", td.getLag().getAmount().setScale(3, BigDecimal.ROUND_HALF_UP).toString());
    }
}
