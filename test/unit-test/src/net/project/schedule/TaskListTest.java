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
package net.project.schedule;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for the TaskList class.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class TaskListTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public TaskListTest(String s) {
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
     *         tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TaskListTest.class);
        return suite;
    }

    public void testSetHierarchyLevel1() {
        TaskList taskList = new TaskList();

        ScheduleEntry entry = new Task();
        entry.setID("1");
        entry.setParentTaskID(null);
        taskList.add(entry);

        entry = new Task();
        entry.setID("2");
        entry.setParentTaskID(null);
        taskList.add(entry);

        taskList.calculateHierarchyLevel();

        assertEquals("1", taskList.get("1").getHierarchyLevel());
        assertEquals("1", taskList.get("2").getHierarchyLevel());
    }

    public void testSetHierarchyLevel2() {
        TaskList taskList = new TaskList();

        ScheduleEntry entry = new Task();
        entry.setID("1");
        entry.setParentTaskID(null);
        taskList.add(entry);

        entry = new Task();
        entry.setID("11");
        entry.setParentTaskID("1");
        taskList.add(entry);

        entry = new Task();
        entry.setID("111");
        entry.setParentTaskID("11");
        taskList.add(entry);

        entry = new Task();
        entry.setID("2");
        entry.setParentTaskID(null);
        taskList.add(entry);

        entry = new Task();
        entry.setID("22");
        entry.setParentTaskID("2");
        taskList.add(entry);

        entry = new Task();
        entry.setID("222");
        entry.setParentTaskID("22");
        taskList.add(entry);

        entry = new Task();
        entry.setID("2222");
        entry.setParentTaskID("222");
        taskList.add(entry);

        entry = new Task();
        entry.setID("333");
        entry.setParentTaskID("33");
        taskList.add(entry);

        taskList.calculateHierarchyLevel();

        assertEquals("1", taskList.get("1").getHierarchyLevel());
        assertEquals("2", taskList.get("11").getHierarchyLevel());
        assertEquals("3", taskList.get("111").getHierarchyLevel());
        assertEquals("1", taskList.get("2").getHierarchyLevel());
        assertEquals("2", taskList.get("22").getHierarchyLevel());
        assertEquals("3", taskList.get("222").getHierarchyLevel());
        assertEquals("4", taskList.get("2222").getHierarchyLevel());
        assertEquals("1", taskList.get("333").getHierarchyLevel());
    }

}
