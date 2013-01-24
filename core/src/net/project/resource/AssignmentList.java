/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.resource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.Node;
import net.project.util.TimeQuantity;

import org.apache.commons.collections.ListUtils;

/**
 * A collection of assignments.  This list is significant because it has the
 * ability to determine if it has changed and to save only the assignments that
 * have changed.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class AssignmentList implements Serializable {
    /**
     * Collection of assignment objects.
     */
    private List assignments = new ArrayList();
    /**
     * The state of the AssignmentList object when it was last loaded from the
     * database.
     */
    private AssignmentList lastSavedState;
    
    /** The column index of assignments. */
    private int columnIndex;
   
    /** The order of sorting. */
    private boolean orderDescending;
    
    private List<Node> assignmentList = new ArrayList<Node>();

    /**
     * Get a list of the assignments current stored in this assignment list as
     * a <code>List</code> object.
     * @return
     */
    public List getAssignments() {
        return assignments;
    }

    /**
     * Add an assignment to this list of Assignments.
     *
     * @param assignment a <code>Assignment</code> object that is to be added
     * to this list.
     */
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void addAllAssignments(Collection assignmentCollection) {
        assignments.addAll(assignmentCollection);
    }

    /**
     * Remove any assignments from this object.
     */
    public void clear() {
        assignments.clear();
    }

    /**
     * Remove an individual assignment from this object.
     *
     * @param assignment
     */
    public void remove(Assignment assignment) {
        assignments.remove(assignment);
    }

    /**
     * Return an iterator to all of the assignments in this list.
     *
     * @return a <code>Iterator</code> to all assignments in this list.
     */
    public Iterator iterator() {
        return assignments.iterator();
    }

    /**
     * Get a list of the assignments that have been modified or deleted since
     * the last saved state.
     *
     * @return a <code>List</code> of Assignment objects that have been
     * modified or deleted since the last saved state.
     */
    List getStaleEntries() {
        return ListUtils.subtract(lastSavedState.assignments, assignments);
    }

    /**
     * Recalculate the work on each of the assignments in the assignment list.
     * This is useful if we are going to do something like re-store all of the
     * assignments.
     *
     * @param work a <code>TimeQuantity</code> which represents the amount of
     * work required to complete the entire ScheduleEntry.
     */
    public void recalculateAssignmentWork(TimeQuantity work) {
        BigDecimal totalPercentAllocation = getTotalPercentage();

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();
            assignment.setWork(AssignmentUtils.calculateAllocatedWork(work, assignment.getPercentAssigned(), totalPercentAllocation));
        }
    }

    /**
     * Get a map which maps person id to which the assignment is assigned to the
     * assignment.
     *
     * @return a <code>Map</code> of person_id's to assignments.
     */
    public Map getAssignmentMap() {
        HashMap map = new HashMap();

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            Assignment assignment = (Assignment)it.next();
            map.put(assignment.getPersonID(), assignment);
        }

        return map;
    }

    /**
     * This method will store any assignment that has changed.
     * <p/>
     * It will delete assignments that have been removed and insert / update
     * other assignments.
     * @throws PersistenceException if there is a problem storing
     */
    public void store(DBBean db) throws SQLException, PersistenceException {
        //Only store if something has been modified
        if (!isModified()) {
            return;
        }

        //Delete any assignments from the database that have been removed here
        AssignmentManager.deleteAssignments(db, getStaleEntries());

        //Now iterate through the assignments that are here and store
        for (Iterator it = assignments.iterator(); it.hasNext();) {
            Assignment assignment = (Assignment)it.next();
            assignment.store(db);
        }
    }

    /**
     * This method will store any assignment that has changed.
     * <p/>
     * It will delete assignments that have been removed and insert / update
     * other assignments.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {
    }

    /**
     * This method indicates that the current state that the object is in is the
     * state that is currently saved in the database for this object.  The
     * object should only be saved if it changes from this state.
     */
    public void setLastSavedState() {
        lastSavedState = (AssignmentList)this.clone();
    }

    /**
     * Indicates if the AssignmentList has been modified since the last time the
     * {@link #setLastSavedState} method was called.
     *
     * @return a <code>boolean</code> indicating if this object has changed.
     */
    public boolean isModified() {
        return (lastSavedState != null && !this.equals(lastSavedState));
    }

    public Object clone() {
        AssignmentList clone = new AssignmentList();

        try {
            for (Iterator it = assignments.iterator(); it.hasNext();) {
                Assignment assignment = (Assignment)it.next();
                clone.addAssignment((Assignment)assignment.clone());
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("AssignmentList is only designed to be " +
                "used with Assignments that implement the Cloneable interfacce.");
        }

        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentList)) return false;

        final AssignmentList assignmentList = (AssignmentList)o;

        if (!assignments.equals(assignmentList.assignments)) return false;

        return true;
    }

    public int hashCode() {
        return assignments.hashCode();
    }

    /**
     * Get the sum of assignment.getPercentAssignedDecimal() for all assignments in this
     * list.
     *
     * @return sum of percentage assigned for all assignments, where <code>1.00</code> = 100%
     */
    public BigDecimal getTotalPercentage() {
        BigDecimal totalPercentageDecimal = new BigDecimal("0.00");

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            Assignment assignment = (Assignment)it.next();
            totalPercentageDecimal = totalPercentageDecimal.add(assignment.getPercentAssignedDecimal());
        }

        return totalPercentageDecimal;
    }

    /**
     * Get the number of assignments stored in this list.
     *
     * @return a <code>int</code> containing the number of assignments in this
     * list.
     */
    public int size() {
        return assignments.size();
    }

    /**
     * Determines if the assignment list is empty.
     */
    public boolean isEmpty() {
        return assignments.size() == 0;
    }

//    public Date findEarliestWorkLogTime() {
//        Date earliestTime = null;
//
//        for (Iterator it = assignments.iterator(); it.hasNext();) {
//            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
//            Date earliestWorkingTime = assignment.getDeltaWorkLog().getEarliestWorkingTime();
//
//            if (earliestTime == null || earliestWorkingTime.before(earliestTime)) {
//                earliestTime = earliestWorkingTime;
//            }
//        }
//
//        return earliestTime;
//    }

    /**
     * Returns the assignment based for the specified resource person ID.
     * @param resourceID the person ID of the resource for which to get the assignment
     * @return the assignment
     * @throws NullPointerException if the specified resourceID is null
     * @throws IllegalArgumentException if this list contains no assignment for that resourceID
     */
    public Assignment getForResourceID(String resourceID) {
        if (resourceID == null) {
            throw new NullPointerException("resourceID is required");
        }

        Assignment assignment = lookupAssignment(resourceID);
        if (assignment == null) {
            throw new IllegalArgumentException("No assignment found for resourceID " + resourceID);
        }

        return assignment;
    }

    private Assignment lookupAssignment(String resourceID) {
        Assignment foundAssignment = null;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext() && (foundAssignment == null); ) {
            Assignment nextAssignment = (Assignment) iterator.next();
            if (nextAssignment.getPersonID() != null && nextAssignment.getPersonID().equals(resourceID)) {
                foundAssignment = nextAssignment;
            }
        }
        return foundAssignment;
    }

    /**
     * Replaces the assignment of a particular user with the provided assignment.
     *
     * @param assignment a <code>ScheduleEntryAssignment</code> which will replace
     * an already existing ScheduleEntryAssignment for that user.
     */
    public void replaceAssignment(ScheduleEntryAssignment assignment) {
        Assignment foundAssignment = lookupAssignment(assignment.getPersonID());
        assignments.remove(foundAssignment);
        assignments.add(assignment);
    }

    /**
     * Indicates whether this assignment list contains the specified assignment,
     * based on matching resource person ID.
     * @param assignment the assignment to look for by resource
     * @return true if this assignment list contains an assignment for the same resource;
     * false otherwise
     * @throws NullPointerException if the assignment is null
     */
    public boolean containsForResource(ScheduleEntryAssignment assignment) {

        if (assignment == null) {
            throw new NullPointerException("assignment is required");
        }

        Assignment foundAssignment = lookupAssignment(assignment.getPersonID());
        return (foundAssignment != null);
    }

    /**
     * Removes the assignment with matching resource person ID.
     * <p>
     * The assignment list is modified.  A single assignment is removed.
     * </p>
     * @param assignment the assignment to remove
     * @throws NullPointerException if the specified assignment is null
     * @throws IllegalArgumentException if no assignment can be found with the specified
     * assignment's resource person id
     */
    public void removeAssignment(ScheduleEntryAssignment assignment) {

        if (assignment == null) {
            throw new NullPointerException("assignment is required");
        }

        boolean isContained = false;

        for (Iterator iterator = assignments.iterator(); iterator.hasNext() && !isContained;) {
            Assignment nextAssignment = (Assignment) iterator.next();
                if (nextAssignment.getPersonID() != null && nextAssignment.getPersonID().equals(assignment.getPersonID())) {
                    isContained = true;
                    iterator.remove();
                }
        }

        if (!isContained) {
            // We didn't find the assignment
            throw new IllegalArgumentException("Specified assignment with resouce person ID " + assignment.getPersonID() + " cannot be removed as it was not found in schedule entry");
        }

    }
    
    /**
     * to sort assignment list
     * sorting order will change alternately.
     */
    public List<Node> sort(List<Node> assignmentList){
		this.assignmentList = assignmentList;
 		orderDescending = !orderDescending;
		List sortedTaskList = new LinkedList();
		Collections.sort(this.assignmentList, new CustomComparator());
		for (Iterator it = this.assignmentList.iterator(); it.hasNext();) {
			Node nextEntry = (Node) it.next();
			if(nextEntry.getParent() == null){
				sortedTaskList.add(nextEntry);
				sortedTaskList.addAll(getChildren(nextEntry));
			}
		} 
		this.assignmentList.clear();
		this.assignmentList.addAll(sortedTaskList);  
    	return this.assignmentList;
    }
    
    /**
	 * To rearrange the child nodes.
	 * @param node
	 * @return
	 */
    private List getChildren(Node assignment ){
		ArrayList<Node> childrenEntrys = new ArrayList<Node>();
		for (Iterator it = this.assignmentList.iterator(); it.hasNext();) {
			Node nextEntry = (Node) it.next();
            if(nextEntry.getParent() != null && nextEntry.getParent().getId().equals(assignment.getId()) ){
            	childrenEntrys.add(nextEntry);
            	childrenEntrys.addAll(getChildren(nextEntry));
            }
		}
		return childrenEntrys;
	}
	
    /* Custom Comparator to sort assignments list */
	class CustomComparator implements Comparator{
		public int compare(Object entry1, Object entry2){
			Node assignment1;
			Node assignment2;
			if(orderDescending){
				assignment1 = (Node) entry2;
				assignment2 = (Node) entry1;
			} else {
				assignment1 = (Node) entry1;
				assignment2 = (Node) entry2; 
			}
			int pos = 0;    
			if (columnIndex == AssignmentColumn.assignmentName.getDefaultSequence()) {
				pos = assignment1.getAssignment().getObjectName().toUpperCase().compareTo(assignment2.getAssignment().getObjectName().toUpperCase());
			} else if (columnIndex == AssignmentColumn.assignmentType.getDefaultSequence()) {
				pos = assignment1.getAssignment().getObjectType().toUpperCase().compareTo(assignment2.getAssignment().getObjectType().toUpperCase());
			} else if (columnIndex == AssignmentColumn.assignmentWorkSpace.getDefaultSequence()) {
				pos = assignment1.getAssignment().getWorkSpace().toUpperCase().compareTo(assignment2.getAssignment().getWorkSpace().toUpperCase());
			} else if (columnIndex == AssignmentColumn.assignmentStartDate.getDefaultSequence()) {
				if(assignment1.getAssignment().getAssignmentstartDate() == null || assignment2.getAssignment().getAssignmentstartDate()== null) return 0;
				pos = assignment1.getAssignment().getAssignmentstartDate().compareTo(assignment2.getAssignment().getAssignmentstartDate());
			} else if (columnIndex == AssignmentColumn.assignmentDueDate.getDefaultSequence()) {
				if(assignment1.getAssignment().getAssignmentDueDate() == null || assignment2.getAssignment().getAssignmentDueDate()== null) return 0;
				pos = assignment1.getAssignment().getAssignmentDueDate().compareTo(assignment2.getAssignment().getAssignmentDueDate());
			} else if (columnIndex == AssignmentColumn.assignmentActualStart.getDefaultSequence()) {
				if(assignment1.getAssignment().getAssignmentActualStartDate() == null || assignment2.getAssignment().getAssignmentActualStartDate()== null) return 0;
				pos = assignment1.getAssignment().getAssignmentActualStartDate().compareTo(assignment2.getAssignment().getAssignmentActualStartDate());
			} else if (columnIndex == AssignmentColumn.assignmentPercentComplete.getDefaultSequence()) {
				if(assignment1.getAssignment().getWorkPercentComplete() == null || assignment2.getAssignment().getWorkPercentComplete()== null) return 0;
				pos = assignment1.getAssignment().getWorkPercentComplete().compareTo(assignment2.getAssignment().getWorkPercentComplete());
			} else if (columnIndex == AssignmentColumn.assignmentWork.getDefaultSequence()) {
				if(assignment1.getAssignment().getWorkInHours() == null || assignment2.getAssignment().getWorkInHours()== null) return 0;  
				pos = assignment1.getAssignment().getWorkInHours().compareTo(assignment2.getAssignment().getWorkInHours());
			} else if (columnIndex == AssignmentColumn.assignmentWorkComplete.getDefaultSequence()) {
				if(assignment1.getAssignment().getWorkCompleteInHours() == null || assignment2.getAssignment().getWorkCompleteInHours()== null) return 0;
				pos = assignment1.getAssignment().getWorkCompleteInHours().compareTo(assignment2.getAssignment().getWorkCompleteInHours());
			} else if (columnIndex == AssignmentColumn.assignmentWorkRemaining.getDefaultSequence()) {
				if(assignment1.getAssignment().getWorkRemainingInHours() == null || assignment2.getAssignment().getWorkRemainingInHours()== null) return 0;
				pos = assignment1.getAssignment().getWorkRemainingInHours().compareTo(assignment2.getAssignment().getWorkRemainingInHours());
			} else if (columnIndex == AssignmentColumn.assignmentAssigneeName.getDefaultSequence()) {
				if(assignment1.getAssignment().getAssigneeName() == null || assignment2.getAssignment().getAssigneeName()== null) return 0;
				pos = assignment1.getAssignment().getAssigneeName().compareTo(assignment2.getAssignment().getAssigneeName());
			} else if (columnIndex == AssignmentColumn.assignmentAssignorName.getDefaultSequence()) {
				if(assignment1.getAssignment().getAssignorName() == null || assignment2.getAssignment().getAssignorName()== null) return 0;
				pos = assignment1.getAssignment().getAssignorName().compareTo(assignment2.getAssignment().getAssignorName());
			}
			return pos; 
		}  
	}

	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * @return the orderDescending
	 */
	public boolean isOrderDescending() {
		return orderDescending;
	}

	/**
	 * @param orderDescending the orderDescending to set
	 */
	public void setOrderDescending(boolean orderDescending) {
		this.orderDescending = orderDescending;
	}

}
