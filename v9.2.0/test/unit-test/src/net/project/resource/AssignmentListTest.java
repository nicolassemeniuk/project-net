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

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AssignmentListTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public AssignmentListTest(String s) {
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
        TestSuite suite = new TestSuite(AssignmentListTest.class);
        return suite;
    }

    public void testIsModifiedWithClear() {
        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setPersonID("1");
        sea1.setObjectID("2");
        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setPersonID("3");
        sea2.setObjectID("4");

        AssignmentList list = new AssignmentList();
        list.addAssignment(sea1);
        list.addAssignment(sea2);
        list.setLastSavedState();

        //Run the test
        list.clear();
        assertTrue(list.isModified());
    }

    public void testIsModifiedWithAddNew() {
        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setPersonID("1");
        sea1.setObjectID("2");
        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setPersonID("3");
        sea2.setObjectID("4");

        AssignmentList list = new AssignmentList();
        list.addAssignment(sea1);
        list.addAssignment(sea2);
        list.setLastSavedState();

        //Run the test
        ScheduleEntryAssignment sea3 = new ScheduleEntryAssignment();
        sea3.setPersonID("3");
        sea3.setObjectID("4");
        list.addAssignment(sea3);

        assertTrue(list.isModified());
    }

    public void testIsModifiedWithAddRemove() {
        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setPersonID("1");
        sea1.setObjectID("2");
        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setPersonID("3");
        sea2.setObjectID("4");

        AssignmentList list = new AssignmentList();
        list.addAssignment(sea1);
        list.addAssignment(sea2);
        list.setLastSavedState();

        //Run the test
        list.remove(sea2);

        assertTrue(list.isModified());
    }

    public void testGetStaleEntries1() {
        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setPersonID("1");
        sea1.setObjectID("2");
        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setPersonID("3");
        sea2.setObjectID("4");

        AssignmentList list = new AssignmentList();
        list.addAssignment(sea1);
        list.addAssignment(sea2);
        list.setLastSavedState();

        //Run the test
        list.remove(sea2);

        List staleEntries = list.getStaleEntries();
        assertEquals(1, staleEntries.size());

        ScheduleEntryAssignment firstEntry = (ScheduleEntryAssignment)staleEntries.iterator().next();
        assertEquals(firstEntry.getObjectID(), sea2.getObjectID());
    }

    public void testGetStaleEntries2() {
        ScheduleEntryAssignment sea1 = new ScheduleEntryAssignment();
        sea1.setPersonID("1");
        sea1.setObjectID("2");
        ScheduleEntryAssignment sea2 = new ScheduleEntryAssignment();
        sea2.setPersonID("3");
        sea2.setObjectID("4");

        AssignmentList list = new AssignmentList();
        list.addAssignment(sea1);
        list.addAssignment(sea2);
        list.setLastSavedState();

        //Run the test
        sea2.setPersonID("5");

        List staleEntries = list.getStaleEntries();
        assertEquals(1, staleEntries.size());

        ScheduleEntryAssignment firstEntry = (ScheduleEntryAssignment)staleEntries.iterator().next();
        assertEquals(firstEntry.getObjectID(), sea2.getObjectID());
    }

    public void testGetForResourceID() {

        AssignmentList assignmentList;
        ScheduleEntryAssignment assignment1;

        // Null resourceID
        try {
            new AssignmentList().getForResourceID(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Missing resource ID on empty list
        try {
            new AssignmentList().getForResourceID("1");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Missing resource ID on populated list
        assignmentList = new AssignmentList();
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignmentList.addAssignment(assignment1);
        try {
            assignmentList.getForResourceID("2");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Matching resource ID
        assignmentList = new AssignmentList();
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignmentList.addAssignment(assignment1);
        assertEquals("1", assignmentList.getForResourceID("1").getPersonID());

    }

    public void testContainsForResource() {

        AssignmentList assignmentList;
        ScheduleEntryAssignment assignment1;
        ScheduleEntryAssignment assignment2;

        // Null asssignment
        try {
            new AssignmentList().containsForResource(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Assignment without person id
        assertFalse(new AssignmentList().containsForResource(new ScheduleEntryAssignment()));

        // Empty AssignmentList
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assertFalse(new AssignmentList().containsForResource(assignment1));

        // List with assignment1 without person id, even when matching the same assignment object
        assignment1 = new ScheduleEntryAssignment();
        assignmentList = new AssignmentList();
        assignmentList.addAssignment(assignment1);
        assertFalse(assignmentList.containsForResource(assignment1));

        // List unmatching assignment
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignment2 = new ScheduleEntryAssignment();
        assignment2.setPersonID("2");
        assignmentList = new AssignmentList();
        assignmentList.addAssignment(assignment1);
        assertFalse(assignmentList.containsForResource(assignment2));

        // List with matching assignment
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignment2 = new ScheduleEntryAssignment();
        assignment2.setPersonID("1");
        assignmentList = new AssignmentList();
        assignmentList.addAssignment(assignment1);
        assertTrue(assignmentList.containsForResource(assignment2));

    }

    public void testRemoveAssignment() {

        AssignmentList assignmentList;
        ScheduleEntryAssignment assignment1;
        ScheduleEntryAssignment assignment2;

        // Null asssignment
        try {
            new AssignmentList().containsForResource(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Assignment not present
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignment2 = new ScheduleEntryAssignment();
        assignment2.setPersonID("2");
        assignmentList = new AssignmentList();
        assignmentList.addAssignment(assignment1);
        try {
            assignmentList.removeAssignment(assignment2);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // List with matching assignment
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignment2 = new ScheduleEntryAssignment();
        assignment2.setPersonID("1");
        assignmentList = new AssignmentList();
        assignmentList.addAssignment(assignment1);
        assignmentList.removeAssignment(assignment2);
        assertEquals(0, assignmentList.size());

        // List with 2 assignments, one matching
        assignmentList = new AssignmentList();
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("1");
        assignmentList.addAssignment(assignment1);
        assignment1 = new ScheduleEntryAssignment();
        assignment1.setPersonID("2");
        assignmentList.addAssignment(assignment1);
        assignment2 = new ScheduleEntryAssignment();
        assignment2.setPersonID("2");
        assignmentList.removeAssignment(assignment2);

        assertEquals(1, assignmentList.size());

        // Check no remaining assignments are the removed assignment
        for (Iterator iterator = assignmentList.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            assertFalse(nextAssignment.getPersonID().equals("2"));
        }


    }

}
