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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.persistence.PersistenceException;
import net.project.util.VisitException;

/**
 * This class is designed to detect when a Task is dependent upon itself.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CyclicDependencyDetector {
    /**
     * This is the list of preloaded tasks that we created prior to run the
     * CyclicDependencyDetector.  We use this list so we don't have to load tasks
     * individually, which would be very slow.
     */
    protected Map cachedTasks = new HashMap();

    /**
     * This class is used to visit a task and all of its dependent tasks.  This
     * class has been made as an inner class so we don't have to expose the
     * visit method as a public part of the API.
     *
     * @author Matthew Flower
     * @since Version 7.4
     */
    private class TaskDependencyVisitor extends EmptyScheduleVisitor {
        Collection marked = new ArrayList();
        ScheduleEntry lookingFor;
        Collection cyclic = new ArrayList();
        boolean foundCycle = false;

        public void visitTask(ScheduleEntry t) throws VisitException {
            //Check to see if this task has already been visited.  If so, return
            if ((foundCycle) || (marked.contains(t))) {
                return;
            }

            if (t.getID().equals(lookingFor.getID())) {
                //The current task we are visiting is the same as the original task.
                //We have found a cyclic dependency.
                foundCycle = true;
            } else {
                try {
                    //Visit all of the dependencies of this task.
                    PredecessorList tdl = t.getPredecessors();
                    for (Iterator it = tdl.iterator(); it.hasNext();) {
                        ScheduleEntry dependentTask = findTask(((TaskDependency)it.next()).getDependencyID());
                        dependentTask.accept(this);
                    }

                    //Visit the children of this task
                    if (t instanceof SummaryTask) {
                        Set children = ((SummaryTask)t).getChildren();
                        for (Iterator it = children.iterator(); it.hasNext();) {
                            ScheduleEntry child = (ScheduleEntry)it.next();
                            child.accept(this);
                        }
                    }
                } catch (PersistenceException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Load task from the local task cache.
     *
     * @param id a <code>String</code> containing the task id of the schedule
     * entry we are trying to find.
     * @return a <code>ScheduleEntry</code> which corresponds to the task id
     * passed to this method.
     */
    public ScheduleEntry findTask(String id) {
        return (ScheduleEntry)cachedTasks.get(id);
    }

    /**
     * Determine if a new dependency added to this task causes the task to
     * cycle.
     *
     * @param t a <code>Task</code> object for which we are going to look for a
     * cycle.
     * @return a <code>boolean</code> value indicating whether a cycle has been
     * found.
     */
    public boolean hasCycle(ScheduleEntry t) throws PersistenceException, VisitException {
        TaskDependencyVisitor cycleDetector = new TaskDependencyVisitor();
        cycleDetector.lookingFor = t;

        //We can't accept the "looking for" task because we'd automatically detect
        //a cycle.
        PredecessorList list = t.getPredecessors();
        for (Iterator it = list.iterator(); it.hasNext();) {
            ScheduleEntry dependentTask = findTask(((TaskDependency)it.next()).getDependencyID());
            dependentTask.accept(cycleDetector);
        }

        //Visit the children of this task
        if (t instanceof SummaryTask) {
            Set children = ((SummaryTask)t).getChildren();
            for (Iterator it = children.iterator(); it.hasNext();) {
                ScheduleEntry child = (ScheduleEntry)it.next();
                child.accept(cycleDetector);
            }
        }

//        //Visit the parents to see if there is a cycle too
//        if (t.hasParent()) {
//            ScheduleEntry parentTask = findTask(t.getParentTaskID());
//            parentTask.accept(cycleDetector);
//        }

        return cycleDetector.foundCycle;
    }

    public List getCyclicDependencies(ScheduleEntry t) throws PersistenceException, VisitException {
        TaskDependencyVisitor cycleDetector = new TaskDependencyVisitor();
        cycleDetector.lookingFor = t;
        List cyclicDependencies = new ArrayList();

        //We can't accept the "looking for" task because we'd automatically detect
        //a cycle.
        PredecessorList list = t.getPredecessors();
        for (Iterator it = list.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry dependentTask = findTask(td.getDependencyID());
            dependentTask.accept(cycleDetector);
            if (cycleDetector.foundCycle) {
                cycleDetector.foundCycle = false;
                cyclicDependencies.add(td);
            }
        }

        //Visit the children of this task
        if (t instanceof SummaryTask) {
            Set children = ((SummaryTask)t).getChildren();
            for (Iterator it = children.iterator(); it.hasNext();) {
                ScheduleEntry child = (ScheduleEntry)it.next();
                child.accept(cycleDetector);

                if (cycleDetector.foundCycle) {
                    cycleDetector.foundCycle = false;
                    cyclicDependencies.add(child);
                }
            }
        }

        //Visit the parents to see if there is a cycle too
//        if (t.hasParent()) {
//            ScheduleEntry parentTask = findTask(t.getParentTaskID());
//            parentTask.accept(cycleDetector);
//
//            if (cycleDetector.foundCycle) {
//                cycleDetector.foundCycle = false;
//                cyclicDependencies.add(parentTask);
//            }
//        }

        return cyclicDependencies;
    }

    /**
     * Create a cache of tasks so we can just request a task by id later without
     * having to load it from the database.  (Round trips are murder on
     * performance.)
     *
     * @param spaceID a <code>String</code> containing the space id from which
     * we are going to load tasks.
     * @param lookingFor a <code>ScheduleEntry</code> which represents the
     * TaskEntry we are going to be looking for once we start doing the test for
     * cycles.
     * @throws PersistenceException if there is an error loading the tasks from
     * the database.
     */
    void buildTaskCache(String spaceID, ScheduleEntry lookingFor) throws PersistenceException {
        TaskFinder finder = new TaskFinder();
        List tasks = finder.findBySpaceID(spaceID);

        for (Iterator it = tasks.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();
            cachedTasks.put(task.getID(), task);
        }

        //Overwrite the task we're looking for (which was loaded and might be in
        //the cache) with the version passed into this class.  This is important
        //so for the times that it is tested before it is saved to the database.
        if (lookingFor != null) {
            cachedTasks.put(lookingFor.getID(), lookingFor);
        }

        //Make sure that if we have any summary tasks that their children lists
        //are populated
        populateParents(lookingFor);

    }

    /**
     * Create a cache of tasks so we can just request a task by id later without
     * having to load it from the database.  (Round trips are murder on
     * performance.)
     *
     * This method has a side effect of populating all of the "children" lists
     * inside of any summary tasks.
     *
     * @param schedule a <code>Schedule</code> object.  We will use this
     * schedule's internal task list to construct our map of tasks.
     * @param lookingFor a <code>ScheduleEntry</code> which represents the
     * TaskEntry we are going to be looking for once we start doing the test for
     * cycles.
     * @throws PersistenceException if there is an error loading the tasks from
     * the database.
     */
    void buildTaskCache(Schedule schedule, ScheduleEntry lookingFor) throws PersistenceException {
        cachedTasks.putAll(schedule.getEntryMap());

        //Overwrite the task we're looking for (which was loaded and might be in
        //the cache) with the version passed into this class.  This is important
        //so for the times that it is tested before it is saved to the database.
        cachedTasks.put(lookingFor.getID(), lookingFor);

        //Make sure that if we have any summary tasks that their children lists
        //are populated
        populateParents(lookingFor);
    }

    /**
     * This method ensures that all of the "Summary Tasks" in the cached task
     * list have their children tables populated without having to lazy load.
     *
     * @param lookingFor a <code>ScheduleEntry</code> that we are going to looking
     * at to determine if there is a cyclic dependency.
     */
    private void populateParents(ScheduleEntry lookingFor) {
        if (lookingFor != null && lookingFor.hasParent()) {
            ScheduleEntry parent = (ScheduleEntry)cachedTasks.get(lookingFor.getParentTaskID());

            if (!(parent instanceof SummaryTask)) {
                //The cache hasn't been updated yet, this is probably a newly
                //associated parent task.  Convert the task to a summary task for
                //the purpose of cyclic detection.
                SummaryTask parentSummaryTask = (SummaryTask)parent.as(TaskType.SUMMARY);
                cachedTasks.put(parentSummaryTask.getID(), parentSummaryTask);
            }
        }

        TaskFinder.populateParents(cachedTasks);
    }
}
