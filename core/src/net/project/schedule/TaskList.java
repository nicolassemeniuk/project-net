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

 package net.project.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.resource.PersonalPropertyMap;
import net.project.resource.ScheduleEntryAssignment;
import net.project.security.SessionManager;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.commons.collections.MultiHashMap;

/**
 * A collection of tasks.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class TaskList implements Serializable {
    /** Maps task id's to tasks. */
    private final HashMap taskMap;
    /** A linked list of tasks.  This perserves order. */
    private final List taskList = new LinkedList();
    /** The total work done by all tasks in the list. */
    private TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
    /** The total amount of work complete in all tasks in the list. */
    private TimeQuantity totalWorkComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);
    /** The earliest start date of the tasks in the list. */
    private Date earliestStartDate = null;
    /** The latest end date of the tasks in the list. */
    private Date latestEndDate = null;
    /** The maximum depth of the hierarchy of tasks. */
    private int maxHierarchyLevel = 0;
    
    /** The column index of tasks. */
    private int columnIndex;
    /** The order of sorting. */
    private boolean orderDescending;
    
    private Map visibilityMap = new HashMap();
    
    private ScheduleColumn scheduleColumn;
    
	/**
     * Creates a new TaskList object.
     */
    public TaskList() {
        taskMap = new HashMap();
    }

    /**
     * Add a schedule entry to this list.
     *
     * @param entry a <code>ScheduleEntry</code> to be added to the list.
     */
    public void add(ScheduleEntry entry) {
        taskMap.put(entry.getID(), entry);
        taskList.add(entry);

        if (!(entry instanceof SummaryTask)) {
            totalWork = totalWork.add(ScheduleTimeQuantity.convertToHour(entry.getWorkTQ()));
            totalWorkComplete = totalWorkComplete.add(ScheduleTimeQuantity.convertToHour(entry.getWorkCompleteTQ()));
        } else {
            //sjmittal: just add the work of the resource assigned
            for (Iterator it = entry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
                totalWork = totalWork.add(nextAssignment.getWork());
                totalWorkComplete = totalWorkComplete.add(nextAssignment.getWorkComplete());
            }
        }
            

        if (entry.getStartTime() != null && (earliestStartDate == null || entry.getStartTime().before(earliestStartDate))) {
            earliestStartDate = entry.getStartTime();
        }

        if (entry.getEndTime() != null && (latestEndDate == null || entry.getEndTime().after(latestEndDate))) {
            latestEndDate = entry.getEndTime();
        }
    }

    /**
     * Remove a schedule entry with a given id from this list.
     *
     * @param id a <code>String</code> containing a taskID of a schedule entry
     * that should be removed from this list.
     * @return a <code>ScheduleEntry</code> that was removed.
     */
    public ScheduleEntry remove(String id) {
        ScheduleEntry entry = (ScheduleEntry)taskMap.remove(id);

        if (entry != null) {
            taskList.remove(entry);

            if (!(entry instanceof SummaryTask)) {
                totalWork = totalWork.subtract(ScheduleTimeQuantity.convertToHour(entry.getWorkTQ()));
                totalWorkComplete = totalWorkComplete.subtract(entry.getWorkCompleteTQ());
            } else {
                //sjmittal: just remove the work for the resource assigned
                for (Iterator it = entry.getAssignments().iterator(); it.hasNext();) {
                    ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
                    totalWork = totalWork.subtract(nextAssignment.getWork());
                    totalWorkComplete = totalWorkComplete.subtract(nextAssignment.getWorkComplete());
                }
            }

            //Fix the start date and end date, if necessary
            if (earliestStartDate.equals(entry.getStartTime())) {
                earliestStartDate = null;
                for (Iterator it = taskList.iterator(); it.hasNext();) {
                    ScheduleEntry currentEntry = (ScheduleEntry) it.next();
                    if (earliestStartDate == null || currentEntry.getStartTime().before(earliestStartDate)) {
                        earliestStartDate = currentEntry.getStartTime();
                    }
                }
            }
            if (latestEndDate.equals(entry.getEndTime())) {
                latestEndDate = null;
                for (Iterator it = taskList.iterator(); it.hasNext();) {
                    ScheduleEntry currentEntry = (ScheduleEntry) it.next();
                    if (latestEndDate == null || currentEntry.getEndTime().after(latestEndDate)) {
                        latestEndDate = currentEntry.getEndTime();
                    }
                }
            }
        }

        return entry;
    }

    /**
     * Assume that all objects in the passed in Collection are
     * <code>ScheduleEntry</code> objects and add them to this list.
     *
     * @param scheduleEntries a <code>Collection</code> of <code>ScheduleEntry</code>
     */
    void addAll(Collection scheduleEntries) {
        for (Iterator it = scheduleEntries.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
            add(scheduleEntry);
        }
    }

    /**
     * Get a <code>ScheduleEntry</code> with a given ID.
     *
     * @param id a <code>ScheduleEntry</code> with a given id.
     * @return a <code>ScheduleEntry</code> that has the id provided to this
     * method, or null if there is no matching schedule entry.
     */
    ScheduleEntry get(String id) {
        return (ScheduleEntry)taskMap.get(id);
    }

    /**
     * Get a list of schedule entries housed within.
     *
     * @return a <code>Collection</code> of schedule entries in this list.
     */
    public List getList() {
        return Collections.unmodifiableList(taskList);
    }

    /**
     * Get a map of task id to schedule entries.
     *
     * @return a <code>Map</code> of id to schedule entries.
     */
    Map getMap() {
        return Collections.unmodifiableMap(taskMap);
    }

    /**
     * Restore this object to the state it was in when it was created.
     */
    void clear() {
        taskMap.clear();
        taskList.clear();
        earliestStartDate = null;
        latestEndDate = null;
        totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        totalWorkComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        maxHierarchyLevel = 1;
    }

    /**
     * Indicates if this list has no schedule entries in it.
     * @return
     */
    boolean isEmpty() {
        return taskList.size() == 0;
    }

    /**
     * Indicates the size of the list.
     *
     * @return an <code>int</code> telling how many objects are in the list.
     */
    public int size() {
        return taskList.size();
    }

    /**
     * Provides a method to iterate over the schedule entries stored in this
     * object.
     *
     * @return a <code>Iterator</code> to the schedule entries in this object.
     */
    public Iterator iterator() {
        return taskList.iterator();
    }

    public ScheduleEntry first() {
        if (taskList.size() > 0) {
            return (ScheduleEntry)taskList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Used to assign a hierarchy level to each schedule entry in this
     * collection.  This calculation is designed to put task whose parents aren't
     * in the list at level "1".  This is helpful for creating the tree of tasks
     * when some schedule entries are missing because of a filter.
     */
    void calculateHierarchyLevel() {
        MultiHashMap parentMap = new MultiHashMap();

        for (Iterator it = taskList.iterator(); it.hasNext();) {
            ScheduleEntry entry = (ScheduleEntry) it.next();

            //Don't insert null as a key in the multi hash map.
            String parentTaskID = (entry.getParentTaskID() == null ? "-1" : entry.getParentTaskID());

            //If this entry is an orphan, put it at the top level
            if (!taskMap.containsKey(parentTaskID)) {
                parentTaskID = "-1";
                entry.setOrphan(true);
            } else {
                entry.setOrphan(false);
            }
            parentMap.put(parentTaskID, entry);
        }

        processHierarchyList((List)parentMap.get("-1"), parentMap, 1);
    }

    /**
     * Recursive method to compute the hierarchy level of tasks.
     *
     * @param toProcess a <code>List</code> of ScheduleEntry objects at the same
     * hierarchy level.
     * @param parentMap a <code>MultiHashmap</code> mapping the id of a parent
     * task to all of its children.
     * @param level a <code>int</code> indicating the level that all of the
     * current tasks should be assigned.
     */
    private void processHierarchyList(List toProcess, MultiHashMap parentMap, int level) {
        if (toProcess != null && toProcess.size() > 0) {
            if (level > maxHierarchyLevel)
                maxHierarchyLevel = level;

            for (Iterator it = toProcess.iterator(); it.hasNext();) {
                ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
                scheduleEntry.setHierarchyLevel(String.valueOf(level));
                processHierarchyList((List)parentMap.get(scheduleEntry.getID()), parentMap, level+1);
            }
        }
    }

    /**
     * Get a list of all the task ids corresponding to this task and all of its
     * parents.
     *
     * @param taskID a <code>String</code> containing the task id of a task
     * presumably loaded in this list.
     * @return a <code>List</code> of task ids.
     */
    public List getHierarchyTree(String taskID) {
        List hierarchy = new ArrayList();

        ScheduleEntry task = (ScheduleEntry)taskMap.get(taskID);
        while (task != null && !hierarchy.contains(task.getID())) {
            hierarchy.add(taskID);
            task = (ScheduleEntry)taskMap.get(task.getParentTaskID());
        }

        return hierarchy;
    }

    /**
     * Get the earliest start date of tasks in this list.
     *
     * @return a <code>Date</code> indicating the earliest date of tasks in this
     * list.
     */
    Date getEarliestStartDate() {
        return earliestStartDate;
    }

    /**
     * Get the latest date that any schedule entry ends in this list.
     *
     * @return a <code>Date</code> which is the date the latest schedule entry
     * finishes.
     */
    Date getLatestEndDate() {
        return latestEndDate;
    }

    /**
     * Get the total amount of work that this list of task represents.
     *
     * @return a <code>TimeQuantity</code> indicating the total amount of work
     * for tasks in this list.
     */
    TimeQuantity getTotalWork() {
        return totalWork;
    }

    //sjmittal: this method does not makes any sense as it may include the work
    //for summary task derived from the child tasks hence duplicating the work hrs
    //to be added. Commented this as of now
//    /**
//     * Get the total amount of work that this list of task represents by
//     * recomputing it from the tasks.  This is useful during calculations where
//     * the work inside of a task may have changed.
//     *
//     * @return a <code>TimeQuantity</code> indicating the total amount of work
//     * for tasks in this list.
//     */
//    TimeQuantity getTotalWorkLive() {
//        TimeQuantity totalWork = TimeQuantity.O_HOURS;
//        for (Iterator it = taskList.iterator(); it.hasNext();) {
//            ScheduleEntry entry = (ScheduleEntry) it.next();
//            totalWork = totalWork.add(entry.getWorkTQ());
//        }
//
//        return totalWork;
//    }

    /**
     * Get the total amount of work completed in this list of tasks.
     *
     * @return a <code>TimeQuantity</code> representing the total amount of
     * work completed by all tasks in this list.
     */
    TimeQuantity getTotalWorkComplete() {
        return totalWorkComplete;
    }
    
    public void constructVisibility() {
        //List of tasks that we have already visited and know their visibility.
    	HashSet mark = new HashSet();
        Map taskMap = this.getMap();

        //Get all the visibility properties from the database beforehand so
        //we don't have to fetch each one.
        PersonProperty props = new PersonProperty();
        props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        props.prefetchForContextPrefix("prm.schedule.main");
        //Iterate through the tasks and assign them visibility.
        PersonalPropertyMap taskListpropertyMap = null;
        try {
			taskListpropertyMap = new PersonalPropertyMap("prm.schedule.tasklist");
		} catch (PersistenceException pnetEx) {
		}
		
        for (Iterator it = this.iterator(); it.hasNext();) {
            ScheduleEntry se = (ScheduleEntry) it.next();
                boolean visible = true;
                //Build a stack up to the parent for this task.
                Stack hierarchyStack = new Stack();
                String currentID = se.getID();
                do {
                    hierarchyStack.push(currentID);
                    ScheduleEntry currentTask = (ScheduleEntry) taskMap.get(currentID);
                    if (currentTask == null) {
                        //This can happen when filtering -- parent task isn't going
                        //to be in the list.
                        break;
                    }
                    currentID = currentTask.getParentTaskID();
                    if (mark.contains(currentID)) {

                        //We don't need to revisit things that have already been
                        //visited.  We will use their visibility setting though
                        //so it can propagate down correctly.
                        visible = childrenAreVisible(props, currentID);
                        break;
                    }
                 } while (currentID != null);

                //Now we walk to the stack.  If we find a hidden task at any
                //point, anything below that point isn't visible.
                while (!hierarchyStack.empty()) {
                    currentID = (String)hierarchyStack.pop();

                    //Show that we have visited this id.  Note that we save the
                    //visibility before traversing the children of this node.
                    mark.add(currentID);
                    populateVisibilityMap(currentID, visible, props);

                    //Check the map to see if the children of this object are
                    //visible.  If we are invisible already, we don't have to
                    //do a check because invisibility propagates.
                }
                se.setVisible(visible);
                se.setExpanded(getExpandedProp(props, se.getID()));
                if (taskListpropertyMap != null) {
    				applyDecoration(se, taskListpropertyMap);
    			}
            }
    }
    
    private boolean childrenAreVisible(PersonProperty props, String id) {
        boolean childrenAreVisible = isVisible(id);
        //Just because an object is visible, doesn't mean its children are visible.
        //It could be the "collapsed summary task".
        if (childrenAreVisible) {
            childrenAreVisible = getExpandedProp(props, id);
        }
        return childrenAreVisible;
    }
    
    public boolean isVisible(String id) {
        return ((Boolean)visibilityMap.get(id)).booleanValue();
    }
    
    private void populateVisibilityMap(String id, boolean visible, PersonProperty props) {
        visibilityMap.put(id, new Boolean(visible));
    }
    
    private boolean getExpandedProp(PersonProperty props, String id) {
        String[] expandedProps = props.get("prm.schedule.main", "node"+id+"expanded", true);
        String expandedProp = (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: "true");
        return (expandedProp != null && expandedProp.equals("true"));
    }
    
    private void applyDecoration(ScheduleEntry se, PersonalPropertyMap propertyMap){
        if(se.isComplete() && propertyMap.propertyExists("completedTasksColor")) {
            se.setRowClass(propertyMap.getProperty("completedTasksColor"));
        } else if(se.getEndTime() != null && se.getEndTime().before(new Date()) && propertyMap.propertyExists("lateTasksColor")) {
            se.setRowClass(propertyMap.getProperty("lateTasksColor"));
        } else if(se.getEndTime() != null && (new Date().after(DateUtils.addDay(se.getEndTime(), -7))) && (new Date().before(se.getEndTime())) && propertyMap.propertyExists("tasksComingDueColor")) {
            se.setRowClass(propertyMap.getProperty("tasksComingDueColor"));
        } else if(se.isPastDeadline() && propertyMap.propertyExists("afterDeadlineColor")) {
            se.setRowClass(propertyMap.getProperty("afterDeadlineColor"));
        } else if(se.getAssignmentList().size() == 0 || se.getAssignmentList().getTotalPercentage().signum() == 0 && propertyMap.propertyExists("unassignedTasksColor")) {
            se.setRowClass(propertyMap.getProperty("unassignedTasksColor"));
        } else if(se.getAssignmentList().size() > 0 && propertyMap.propertyExists("hasAssignmentColor")){
        	se.setRowClass(propertyMap.getProperty("hasAssignmentColor"));
        } else if(se.isCriticalPath() && propertyMap.propertyExists("isCriticalPathColor")) {
            se.setRowClass(propertyMap.getProperty("isCriticalPathColor"));
        } else if(se.getConstraintType().isDateConstrained() && propertyMap.propertyExists("isDateConstrainedColor")) {
            se.setRowClass(propertyMap.getProperty("isDateConstrainedColor"));
        } else if(se.isFromShare() && propertyMap.propertyExists("isExternalTaskColor")) {
            se.setRowClass(propertyMap.getProperty("isExternalTaskColor"));
        } else {
            se.setRowClass("");
        }
    }
    
    /**
     * to sort task list.
     * sorting order will change alternately.
     */
    public void sort() {
		orderDescending = !orderDescending;
		
		List sortedTaskList = new LinkedList();
		Collections.sort(this.taskList, new CustomComparator());
		
		if (columnIndex == ScheduleColumn.SEQUENCE.getDefaultSequence()) {
			for (Iterator it = this.iterator(); it.hasNext();) {
				ScheduleEntry nextEntry = (ScheduleEntry) it.next();
				if (nextEntry.getParentTaskID() == null) {
					sortedTaskList.add(nextEntry);
					sortedTaskList.addAll(getChildren(nextEntry));
				}
			}

			this.taskList.clear();
			this.taskList.addAll(sortedTaskList);
		}
	}
    
	/**
	 * Gets all children of ScheduleEntry 
	 * @param ScheduleEntry
	 * @return
	 */
	private List getChildren(ScheduleEntry se ){
		ArrayList<ScheduleEntry> childrenEntrys = new ArrayList<ScheduleEntry>();
		for (Iterator it = this.iterator(); it.hasNext();) {
            ScheduleEntry nextEntry = (ScheduleEntry) it.next();
            if(nextEntry.getParentTaskID() != null && nextEntry.getParentTaskID().equals(se.getID()) ){
            	childrenEntrys.add(nextEntry);
            	childrenEntrys.addAll(getChildren(nextEntry));
            }
		}
		return childrenEntrys;
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
	
	 /* Custom Comparator to sort task list */
	class CustomComparator implements Comparator{
		public int compare(Object entry1, Object entry2){
			ScheduleEntry se1;
			ScheduleEntry se2;
			if(orderDescending){
				se1 = (ScheduleEntry) entry2;
				se2 = (ScheduleEntry) entry1;
			} else {
				se1 = (ScheduleEntry) entry1;
				se2 = (ScheduleEntry) entry2;
			}
			int pos = 0;
			if (columnIndex == ScheduleColumn.SEQUENCE.getDefaultSequence()) {
				pos = se1.getSequenceNumber() - se2.getSequenceNumber();
			} else if (columnIndex == ScheduleColumn.NAME.getDefaultSequence()) {
				pos = se1.getName().toUpperCase().compareTo(se2.getName().toUpperCase());
			} else if (columnIndex == ScheduleColumn.WORK.getDefaultSequence()) {
				pos = se1.getWorkTQ().converToHours().compareTo(se2.getWorkTQ().converToHours());
			} else if (columnIndex == ScheduleColumn.DURATION.getDefaultSequence()) {
				pos = se1.getDurationTQ().converToHours().compareTo(se2.getDurationTQ().converToHours());
			} else if (columnIndex == ScheduleColumn.START_DATE.getDefaultSequence()) {
				pos = se1.getStartTime().compareTo(se2.getStartTime());
			} else if (columnIndex == ScheduleColumn.END_DATE.getDefaultSequence()) {
				pos = se1.getEndTime().compareTo(se2.getEndTime());
			} else if (columnIndex == ScheduleColumn.WORK_COMPLETE.getDefaultSequence()) {
				pos = se1.getWorkCompleteTQ().converToHours().compareTo(se2.getWorkCompleteTQ().converToHours());
			} else if (columnIndex == ScheduleColumn.WORK_PERCENT_COMPLETE.getDefaultSequence()) {
				pos = se1.getWorkPercentCompleteDecimal().compareTo(se2.getWorkPercentCompleteDecimal());
			} else if (columnIndex == ScheduleColumn.PRIORITY.getDefaultSequence()) {
				pos = se1.getPriority().compareTo(se2.getPriority());
			} else if (columnIndex == ScheduleColumn.CALCULATION_TYPE.getDefaultSequence()) {
				pos = se1.getTaskCalculationTypeString().compareTo(se2.getTaskCalculationTypeString());
			} else if (columnIndex == ScheduleColumn.ACTUAL_START_DATE.getDefaultSequence()) {
				if(se1.getActualStartTime() == null || se2.getActualStartTime() == null) return 0;
				pos = se1.getActualStartTime().compareTo(se2.getActualStartTime());
			} else if (columnIndex == ScheduleColumn.BASELINE_START_DATE.getDefaultSequence()) {
				if(se1.getBaselineStart() == null || se2.getBaselineStart() == null) return 0;
				pos = se1.getBaselineStart().compareTo(se2.getBaselineStart());
			} else if (columnIndex == ScheduleColumn.START_VARIANCE.getDefaultSequence()) {
				pos = se1.getStartVarianceTQ().converToHours().compareTo(se2.getStartVarianceTQ().converToHours());
			} else if (columnIndex == ScheduleColumn.ACTUAL_ENDDATE.getDefaultSequence()) {
				if(se1.getActualEndTime() == null || se2.getActualEndTime() == null) return 0;
				pos = se1.getActualEndTime().compareTo(se2.getActualEndTime());
			} else if (columnIndex == ScheduleColumn.BASELINE_END_DATE.getDefaultSequence()) {
				if(se1.getBaselineEnd() == null || se2.getBaselineEnd() == null) return 0;
				pos = se1.getBaselineEnd().compareTo(se2.getBaselineEnd());
			} else if (columnIndex == ScheduleColumn.END_VARIANCE.getDefaultSequence()) {
				pos = se1.getEndVarianceTQ().converToHours().compareTo(se2.getEndVarianceTQ().converToHours());
			} else if (columnIndex == ScheduleColumn.BASELINE_WORK.getDefaultSequence()) {
				if(se1.getBaselineWork() == null || se2.getBaselineWork() == null) return 0;
				pos = se1.getBaselineWork().converToHours().compareTo(se2.getBaselineWork().converToHours());
			} else if (columnIndex == ScheduleColumn.WORK_VARIANCE.getDefaultSequence()) {
				pos = se1.getWorkVariance().compareTo(se2.getWorkVariance());
			} else if (columnIndex == ScheduleColumn.BASELINE_DURATION.getDefaultSequence()) {
				if(se1.getBaselineDuration() == null || se2.getBaselineDuration() == null) return 0;
				pos = se1.getBaselineDuration().converToHours().compareTo(se2.getBaselineDuration().converToHours());
			} else if (columnIndex == ScheduleColumn.DURATION_VARIANCE.getDefaultSequence()) {
				pos = se1.getDurationVariance().converToHours().compareTo(se2.getDurationVariance().converToHours());
			} else if (columnIndex == ScheduleColumn.DEPENDENCIES.getDefaultSequence()) {
				pos = se1.getPredecessorsNoLazyLoad().size() - se2.getPredecessorsNoLazyLoad().size();
			} else if (columnIndex == ScheduleColumn.WBS.getDefaultSequence()) {
				if(se1.getWBS() == null || se2.getWBS() == null) return 0;
				pos = se1.getWBS().compareTo(se2.getWBS());
			} else if (columnIndex == ScheduleColumn.PHASE.getDefaultSequence()) {
				pos = se1.getPhaseSequence() - se2.getPhaseSequence();
			} else if (columnIndex == ScheduleColumn.RESOURCES.getDefaultSequence()) {
				if(se1.getAssignments() == null || se2.getAssignments() == null)return 0;
				pos = se1.getAssignments().size() - se2.getAssignments().size();
			}
			return pos; 
		}
	}
	
	void calculateVariance(IWorkingTimeCalendarProvider provider) {
        for (Iterator it = taskList.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
            scheduleEntry.setStartVariance(scheduleEntry.getStartDateVariance(provider)) ;
            scheduleEntry.setEndVariance(scheduleEntry.getEndDateVariance(provider));
        }
    }
	
	/**
	 * @return rows of tasklist with user applied column settings
	 */
	public List getSequencedTaskListRow(){
	    List<ScheduleRow> row = new ArrayList<ScheduleRow>(); 
		for (Iterator it = taskList.iterator(); it.hasNext();) {
			ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
			row.add(new ScheduleRow(scheduleEntry, getScheduleColumn().getSequencedRow(scheduleEntry)));
		}
		//set schedule column null so that in next call, it can return rows with new updated column settings.
		scheduleColumn = null;
		return row;
	}
	
    /**
	 * @return the scheduleColumn
	 */
	public ScheduleColumn getScheduleColumn() {
		if(scheduleColumn == null){
			PersonProperty props = new PersonProperty();
		    props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
		    props.prefetchForContextPrefix("prm.schedule.main");
		    scheduleColumn = new ScheduleColumn();
		    scheduleColumn.constructColumnSettings(props);
		}
		scheduleColumn.setFlatView(columnIndex != ScheduleColumn.SEQUENCE.getDefaultSequence());
		return scheduleColumn;
	}
	
}
