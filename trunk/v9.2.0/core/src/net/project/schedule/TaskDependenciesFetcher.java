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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.persistence.PersistenceException;

/**
 * This class helps to fetch some dependent lists from a schedule around a task.
 * <p>
 * 
 * @author Michael Baranov
 * @author Sachin Mittal
 * 
 */
public class TaskDependenciesFetcher {

    private Map tasks;
    
    public TaskDependenciesFetcher(Map tasks) {
        this.tasks = tasks;
        //Make sure that summary tasks have the appropriate list of children.
        associateChildrenToSummaryTasks();
    }
    
    /**
     * Find the successors of a given schedule entry.
     * <p>
     * Successors include the actual successors of the schedule entry or,
     * if the schedule entry has no successors, the successors of the schedule entry's
     * first parent that has successors.
     * </p>
     * <p>
     * If any of the successors are summary tasks, this method will replace them with the "children
     * without predecessors" because we handle the summary tasks themselves
     * separately.
     * </p>
     * @param se a <code>ScheduleEntry</code> for which we are going to find
     * successors.
     * @return a <code>List</code> of TaskDependency objects which are the
     * successors to this ScheduleEntry.
     * @throws PersistenceException if there is a problem communicating with the
     * database.
     */
    public List getSuccessors(ScheduleEntry se) throws PersistenceException {
        List successors = new LinkedList();

        successors.addAll(se.getSuccessors().getInternalList());

        findParentTaskSuccessors(se, successors);

        //If the successor list has any summary tasks, replace them with the
        //children of that summary task.
        resolveSummaryTaskSuccessors(successors, se);

        return successors;
    }

    /**
     * Find the successors of a task's parent.
     *
     * @param se a <code>ScheduleEntry</code> whose parent's successors we are
     * going to locate.
     * @param successors a <code>List</code> which has already been created to
     * which we are going to add the additional successors that we find.
     * @throws PersistenceException if there is an error loading schedule
     * entries from the database.  (They should have already been loaded by now,
     * so this shouldn't really happen.)
     */
    private void findParentTaskSuccessors(ScheduleEntry se, List successors) throws PersistenceException {

        //In this case we are looking for tasks that are children of a summary task,
        //but that do not have any successors themselves.  These tasks represent
        //the "boundaries" of a group of summary tasks
        while (se.hasParent() && se.getSuccessors().isEmpty()) {
            ScheduleEntry parentTask = (ScheduleEntry)tasks.get(se.getParentTaskID());
            successors.addAll(parentTask.getSuccessors().getInternalList());
            se = parentTask;
        }
    }

    /**
     * Change any summary tasks in this successor list to a list of the children
     * of the summary task.
     *
     * @param successors a <code>Collection</code> of <code>TaskDependency</code>
     * objects through which we are going to search for summary tasks.  If we
     * find them we will append their children to this list and remove the
     * summary task.
     * @param se a <code>ScheduleEntry</code> which is the predecessor for all
     * these successors.
     *
     * @throws PersistenceException never.
     */
    private void resolveSummaryTaskSuccessors(List successors, ScheduleEntry se) throws PersistenceException {
        //If there aren't any children, we don't need to do any more processing.
        //This check is how we leave our recursive method, so be careful if you
        //are thinking about removing it.
        if (successors.isEmpty()) {
            return;
        }

        List childrenOfSummaryTasks = new LinkedList();

        for (Iterator it = successors.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry scheduleEntry = (ScheduleEntry)tasks.get(td.getTaskID());

            if (scheduleEntry instanceof SummaryTask) {
                it.remove();
                Collection children = ((SummaryTask)scheduleEntry).getChildrenWithoutPredecessors();

                for (Iterator it2 = children.iterator(); it2.hasNext();) {
                    ScheduleEntry child = (ScheduleEntry)it2.next();
                    TaskDependency td2 = new TaskDependency(child, se, td.getDependencyType(), td.getLag());
                    childrenOfSummaryTasks.add(td2);
                }
            }
        }

        resolveSummaryTaskSuccessors(childrenOfSummaryTasks, se);
        successors.addAll(childrenOfSummaryTasks);
    }

    /**
     * Get any predecessors for this schedule entry, with a few special twists.
     * If any of the predecessors are summary tasks, replace the summary tasks
     * with the children of the summary task.
     *
     * @param se a <code>ScheduleEntry</code> for which we are going to find
     * predecessors.
     * @return a <code>List</code> containing one or more <code>TaskDependency</code>
     * objects which are the predecessors of and/or parent task of the schedule
     * entry passed into this method.
     * @throws PersistenceException never.  Predecessors should have been
     * preloaded, and if not they would have been lazy loaded by now.
     */
    public List getPredecessors(ScheduleEntry se) throws PersistenceException {
        List predecessors = new ArrayList();

        predecessors.addAll(se.getPredecessors().getInternalList());

        findParentTaskPredecessors(se, predecessors);

        //If the predecessor list has any summary tasks, replace them with the
        //children of that summary task.
        resolveSummaryTaskPredecessors(predecessors, se);

        return predecessors;
    }

    /**
     * Find the predecessors of a task's parent.
     *
     * @param se a <code>ScheduleEntry</code> whose parent's predecessors we are
     * going to locate.
     * @param predecessors a <code>List</code> which has already been created to
     * which we are going to add the additional predecessors that we find.
     * @throws PersistenceException if there is an error loading schedule
     * entries from the database.  (They should have already been loaded by now,
     * so this shouldn't really happen.)
     */
    private void findParentTaskPredecessors(ScheduleEntry se, List predecessors) throws PersistenceException {
        //In this case we are looking for tasks that are children of a summary task,
        //but that do not have any predecessors themselves.  These tasks represent
        //the "boundaries" of a group of summary tasks
        while (se.hasParent() && se.getPredecessors().isEmpty()) {
            ScheduleEntry parentTask = (ScheduleEntry)tasks.get(se.getParentTaskID());
            predecessors.addAll(parentTask.getPredecessors().getInternalList());
            se = parentTask;
        }

    }

    /**
     * Change any summary tasks in this predecessor list to a list of the children
     * of the summary task.
     *
     * @param predecessors a <code>Collection</code> of <code>TaskDependency</code>
     * objects through which we are going to search for summary tasks.  If we
     * find them we will append their children to this list and remove the
     * summary task.
     * @param se a <code>ScheduleEntry</code> which is the successor for all
     * these predecessors.
     *
     * @throws PersistenceException never.
     */
    private void resolveSummaryTaskPredecessors(List predecessors, ScheduleEntry se) throws PersistenceException {
        List childrenOfSummaryTasks = new LinkedList();

        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry scheduleEntry = (ScheduleEntry)tasks.get(td.getDependencyID());

            if (scheduleEntry instanceof SummaryTask) {
                it.remove();
                Collection children = ((SummaryTask)scheduleEntry).getChildrenWithoutSuccessors();

                for (Iterator it2 = children.iterator(); it2.hasNext();) {
                    ScheduleEntry predecessor = (ScheduleEntry)it2.next();
                        TaskDependency td2 = new TaskDependency();
                        td2.setDependencyID(predecessor.getID());
                        td2.setTaskID(se.getID());
                        childrenOfSummaryTasks.add(td2);
                }
            }
        }

        //Make sure that none of the children of the summary tasks we found are
        //themselves summary tasks.  If so, find the appropriate children for
        //those summary tasks.
        if (!childrenOfSummaryTasks.isEmpty()) {
            resolveSummaryTaskPredecessors(childrenOfSummaryTasks, se);
        }

        predecessors.addAll(childrenOfSummaryTasks);
    }
    
    /**
     * Traverse the task list beforehand and ensure that all children are in
     * their parent's list of children.  We do this because the system doesn't
     * generally preload all of the children because it isn't needed.  There
     * isn't really a more performant way to do this while constructing the
     * task list in the first place, so it finds its way here.
     */
    private void associateChildrenToSummaryTasks() {
        for (Iterator it = tasks.values().iterator(); it.hasNext();) {
            ScheduleEntry se = (ScheduleEntry)it.next();

            if (se instanceof SummaryTask) {
                if (((SummaryTask)se).getChildrenNoLazyLoad() == null) {
                    ((SummaryTask)se).setChildren(new LinkedHashSet());
                }
            }

            if (se.hasParent()) {
                SummaryTask parentTask = (SummaryTask)tasks.get(se.getParentTaskID());
                if (parentTask.getChildrenNoLazyLoad() == null) {
                    parentTask.setChildren(new LinkedHashSet());
                }

                parentTask.getChildrenNoLazyLoad().add(se);
            }
        }
    }

	/**
	 * Searches an returns all TaskDependency objects for a given SummaryTask to
	 * its direct and indirect subtasks.
	 * 
	 * @param entry
	 * @return a List of problematic TaskDependency objects
	 * @throws PersistenceException
	 */
	public List getDependenciesToChildren(SummaryTask entry) throws PersistenceException {

		List dependenciesToChildren = new LinkedList();

		Set children = new HashSet();
		enumChildren(entry, children);

		Iterator it = entry.getPredecessors().getInternalList().iterator();

		while (it.hasNext()) {
			TaskDependency dependency = (TaskDependency) it.next();
			String dependencyID = dependency.getDependencyID();
			if (children.contains(dependencyID))
				dependenciesToChildren.add(dependency);
		}

		return dependenciesToChildren;

	}

	/**
	 * Recursive method to gather all children's IDs.
	 * 
	 * @param entry
	 * @param allChildren
	 * @throws PersistenceException
	 */
	private void enumChildren(SummaryTask entry, Set allChildren) throws PersistenceException {
		Set children = entry.getChildren();
		Iterator it = children.iterator();

		while (it.hasNext()) {
			ScheduleEntry element = (ScheduleEntry) it.next();
			allChildren.add(element.getID());

			if (element instanceof SummaryTask) {
				SummaryTask summary = (SummaryTask) element;
				enumChildren(summary, allChildren);
			}
		}

	}

}
