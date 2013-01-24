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

import java.util.LinkedHashSet;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.persistence.PersistenceException;
import net.project.util.CollectionUtils;
import net.project.util.VisitException;

/**
 * This class is designed to test the {@link net.project.schedule.CyclicDependencyDetector}
 * class to make sure that it works property.
 *
 * @author Matthew Flower
 * @since 7.4
 */
public class CyclicDependencyDetectorTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public CyclicDependencyDetectorTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     *
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(java.lang.String[] args) {
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
        TestSuite suite = new TestSuite(CyclicDependencyDetectorTest.class);
        return suite;
    }

    /**
     * Simpliest possible test to determine if the CyclicDependencyDetector can
     * detect cycles.
     */
    public void testHasCycle1() {
        try {
            // 2 -> 1 -> 2
            // Test if 2 has cycle
            ScheduleEntry[] tasks = new ScheduleEntry[2];
            for (int i = 0; i < tasks.length; i++) {
                tasks[i] = new Task();
                tasks[i].setID(String.valueOf(i + 1));
            }

            //Set up task2
            TaskDependency dependency1 = new TaskDependency();
            dependency1.setTaskID("2");
            dependency1.setDependencyID("1");
            PredecessorList tdl = new PredecessorList();
            tdl.add(dependency1);
            tdl.setLoaded(true);
            tasks[1].setPredecessors(tdl);

            //Set up task1
            TaskDependency dependency2 = new TaskDependency();
            dependency2.setTaskID("1");
            dependency2.setDependencyID("2");
            PredecessorList tdl2 = new PredecessorList();
            tdl2.add(dependency2);
            tdl2.setLoaded(true);
            tasks[0].setPredecessors(tdl2);

            //Put the tasks into the cache
            CyclicDependencyDetector crd = new CyclicDependencyDetector();
            for (int i = 0; i < tasks.length; i++) {
                crd.cachedTasks.put(tasks[i].getID(), tasks[i]);
            }

            //Run the tests
            assertEquals("Dependencies had undetected cycle.", true, crd.hasCycle(tasks[1]));
            assertEquals("Dependencies had undetected cycle.", true, crd.hasCycle(tasks[0]));
        } catch (PersistenceException pe) {
            fail("Failed testHasCycle() because of a PersistenceException.");
        } catch (VisitException ve) {
            fail("Failed testHasCycle() because of a VisitException.");
        }
    }


    public void testHasCycle2() {
        try {
            // 2 -> 1
            // Test if 2 has cycle

            //Set up tasks
            ScheduleEntry[] tasks = new ScheduleEntry[2];
            for (int i = 0; i < tasks.length; i++) {
                tasks[i] = new Task();
                tasks[i].setID(String.valueOf(i + 1));
            }

            //Set up task 1
            PredecessorList tdl = new PredecessorList();
            TaskDependency dependency1 = new TaskDependency();
            dependency1.setTaskID("2");
            dependency1.setDependencyID("1");
            tdl.add(dependency1);
            tdl.setLoaded(true);
            tasks[1].setPredecessors(tdl);

            //Set up task 2
            PredecessorList tdl2 = new PredecessorList();
            tdl2.setLoaded(true);
            tasks[0].setPredecessors(tdl2);

            //Add the tasks to the test
            CyclicDependencyDetector crd = new CyclicDependencyDetector();
            for (int i = 0; i < tasks.length; i++) {
                crd.cachedTasks.put(tasks[i].getID(), tasks[i]);
            }

            //Run the tests
            assertEquals("Cycle detected, but none exists.", false, crd.hasCycle(tasks[1]));
            assertEquals("Cycle detected, but none exists.", false, crd.hasCycle(tasks[0]));
        } catch (PersistenceException pe) {
            fail("Failed testHasCycle() because of a PersistenceException.");
        } catch (VisitException ve) {
            fail("Failed testHasCycle() because of a VisitException.");
        }
    }


    public void testCycleThroughSummaryTask() {
        PredecessorList EMPTY_PRED_LIST = new PredecessorList();
        EMPTY_PRED_LIST.setLoaded(true);

        //Set this up
        // [+] Summary Task 1
        //     [+] Summary Task 2
        //         Task 1
        //
        // Set up a dependency between task1 and summary task 1.  This is a cycle
        // and should not be allowed
        SummaryTask summaryTask1 = new SummaryTask();
        summaryTask1.setID("st1");
        summaryTask1.setPredecessors(EMPTY_PRED_LIST);

        SummaryTask summaryTask2 = new SummaryTask();
        summaryTask2.setID("st2");
        summaryTask2.setPredecessors(EMPTY_PRED_LIST);

        ScheduleEntry task1 = new Task();
        task1.setID("t1");

        //Set up parent-child relationships
        summaryTask2.setParentTaskID("st1");
        task1.setParentTaskID("st2");
        summaryTask1.setChildren(CollectionUtils.createSet(summaryTask2));
        summaryTask2.setChildren(CollectionUtils.createSet(task1));

        //Set up a predecessor relationship between task 1 and summary task 1
        PredecessorList predList = new PredecessorList();
        TaskDependency td1 = new TaskDependency();
        td1.setTaskID("t1");
        td1.setDependencyID("st1");
        predList.add(td1);
        task1.setPredecessors(predList);

        CyclicDependencyDetector crd = new CyclicDependencyDetector();
        crd.cachedTasks.put("st1", summaryTask1);
        crd.cachedTasks.put("st2", summaryTask2);
        crd.cachedTasks.put("t1", task1);

        try {
            assertEquals(true, crd.hasCycle(task1));
        } catch (PersistenceException e) {
            e.printStackTrace();
            fail("CyclicDependencyDetectorTest.testCycleThroughSummaryTask threw an unexpected PersistenceException");
        } catch (VisitException e) {
            e.printStackTrace();
            fail("CyclicDependencyDetectorTest.testCycleThroughSummaryTask threw an unexpected VisitException");
        }
    }

    /**
     * This unit test is very similar to {@link #testCycleThroughSummaryTask()}
     * except that we reverse the direction of the dependency.
     */
    public void testCycleThroughSummaryTasks2() {
        PredecessorList EMPTY_PRED_LIST = new PredecessorList();
        EMPTY_PRED_LIST.setLoaded(true);

        //Set this up
        // [+] Summary Task 1
        //     [+] Summary Task 2
        //         Task 1
        //
        // Make task1 be a predecessor of Summary Task 2.  This is a cycle
        // and should not be allowed
        SummaryTask summaryTask1 = new SummaryTask();
        summaryTask1.setID("st1");
        summaryTask1.setPredecessors(EMPTY_PRED_LIST);

        SummaryTask summaryTask2 = new SummaryTask();
        summaryTask2.setID("st2");

        ScheduleEntry task1 = new Task();
        task1.setID("t1");
        task1.setPredecessors(EMPTY_PRED_LIST);

        //Set up parent-child relationships
        summaryTask2.setParentTaskID("st1");
        task1.setParentTaskID("st2");
        summaryTask1.setChildren(CollectionUtils.createSet(summaryTask2));
        summaryTask2.setChildren(CollectionUtils.createSet(task1));

        //Set up a predecessor relationship between task 1 and summary task 2
        PredecessorList predList = new PredecessorList();
        TaskDependency td1 = new TaskDependency();
        td1.setTaskID("t1");
        td1.setDependencyID("st2");
        predList.add(td1);
        summaryTask2.setPredecessors(predList);

        CyclicDependencyDetector crd = new CyclicDependencyDetector();
        crd.cachedTasks.put("st1", summaryTask1);
        crd.cachedTasks.put("st2", summaryTask2);
        crd.cachedTasks.put("t1", task1);

        try {
            assertEquals(true, crd.hasCycle(summaryTask2));
        } catch (PersistenceException e) {
            e.printStackTrace();
            fail("CyclicDependencyDetectorTest.testCycleThroughSummaryTask threw an unexpected PersistenceException");
        } catch (VisitException e) {
            e.printStackTrace();
            fail("CyclicDependencyDetectorTest.testCycleThroughSummaryTask threw an unexpected VisitException");
        }
    }


    public void testGetDependencyCycles() {
        try {
            // 2 -> 1 -> 2
            // Test if 2 has cycle
            ScheduleEntry[] tasks = new ScheduleEntry[2];
            for (int i = 0; i < tasks.length; i++) {
                tasks[i] = new Task();
                tasks[i].setID(String.valueOf(i + 1));
            }

            //Set up task2
            TaskDependency dependency1 = new TaskDependency();
            dependency1.setTaskID("2");
            dependency1.setDependencyID("1");
            PredecessorList tdl = new PredecessorList();
            tdl.add(dependency1);
            tdl.setLoaded(true);
            tasks[1].setPredecessors(tdl);

            //Set up task1
            TaskDependency dependency2 = new TaskDependency();
            dependency2.setTaskID("1");
            dependency2.setDependencyID("2");
            PredecessorList tdl2 = new PredecessorList();
            tdl2.add(dependency2);

            //Set up another dependency
            TaskDependency dependency3 = new TaskDependency();
            dependency3.setTaskID("1");
            dependency3.setDependencyID("2");
            tdl2.add(dependency3);
            tdl2.setLoaded(true);

            tasks[0].setPredecessors(tdl2);

            //Put the tasks into the cache
            CyclicDependencyDetector crd = new CyclicDependencyDetector();
            for (int i = 0; i < tasks.length; i++) {
                crd.cachedTasks.put(tasks[i].getID(), tasks[i]);
            }

            //Run the tests
            List task1Cycles = crd.getCyclicDependencies(tasks[0]);
            List task2Cycles = crd.getCyclicDependencies(tasks[1]);

            //Task 1 should have two cyclic dependencies, both pointing at task 2
            assertEquals("Task1 should have two cycles, both pointing at task2.",
                2, task1Cycles.size());
            assertEquals("Task2 should have one cycle pointing at task1.", 1, task2Cycles.size());

        } catch (PersistenceException pe) {
            fail("Failed testHasCycle() because of a PersistenceException.");
        } catch (VisitException ve) {
            fail("Failed testHasCycle() because of a VisitException.");
        }
    }

    public void testDependencyParentCycle1() {
        try {
            ScheduleEntry[] tasks = new ScheduleEntry[2];
            tasks[0] = new SummaryTask();
            tasks[0].setID("0");
            tasks[1] = new Task();
            tasks[1].setID("1");

            //Make task 1 a parent of task 2
            tasks[1].setParentTaskID("1");
            LinkedHashSet children = new LinkedHashSet();
            children.add(tasks[0]);
            ((SummaryTask)tasks[0]).setChildren(children);

            //Set up task2
            TaskDependency dependency1 = new TaskDependency();
            dependency1.setTaskID("2");
            dependency1.setDependencyID("1");
            PredecessorList tdl = new PredecessorList();
            tdl.add(dependency1);
            tdl.setLoaded(true);
            tasks[1].setPredecessors(tdl);

            //Set up task 1
            PredecessorList pdl0 = new PredecessorList();
            pdl0.setLoaded(true);
            tasks[0].setPredecessors(pdl0);

            //Put the tasks into the cache
            CyclicDependencyDetector crd = new CyclicDependencyDetector();
            for (int i = 0; i < tasks.length; i++) {
                crd.cachedTasks.put(tasks[i].getID(), tasks[i]);
            }

            //Run the tests
            List task1Cycles = crd.getCyclicDependencies(tasks[0]);
            List task2Cycles = crd.getCyclicDependencies(tasks[1]);

            //Task 1 should have two cyclic dependencies, both pointing at task 2
            assertEquals(1, task1Cycles.size());
            assertEquals(1, task2Cycles.size());

        } catch (PersistenceException pe) {
            fail("Failed testHasCycle() because of a PersistenceException.");
        } catch (VisitException ve) {
            fail("Failed testHasCycle() because of a VisitException.");
        }
    }

}
