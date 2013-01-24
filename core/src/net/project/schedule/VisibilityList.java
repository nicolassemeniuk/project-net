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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import net.project.channel.ScopeType;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;

/**
 * This object constructs and manages a
 */
public class VisibilityList {
    private Map visibilityMap = new HashMap();
    private Map childrenVisible = new HashMap();

    private void populateVisibilityMap(String id, boolean visible, PersonProperty props) {
        visibilityMap.put(id, Boolean.valueOf(visible));
        childrenVisible.put(id, Boolean.valueOf(getExpandedProp(props, id)));
    }

    private boolean getExpandedProp(PersonProperty props, String id) {
        String[] expandedProps = props.get("prm.schedule.main", "node"+id+"expanded", true);
        String expandedProp = (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: "true");
        return (expandedProp != null && expandedProp.equals("true"));
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

    public void construct(TaskList list) {
        //List of tasks that we have already visited and know their visibility.
        HashSet mark = new HashSet();
        Map taskMap = list.getMap();

        //Get all the visibility properties from the database beforehand so
        //we don't have to fetch each one.
        PersonProperty props = new PersonProperty();
        props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        props.prefetchForContextPrefix("prm.schedule.main");

        //Iterate through the tasks and assign them visibility.
        for (Iterator it = list.iterator(); it.hasNext();) {
            ScheduleEntry se = (ScheduleEntry) it.next();

            if (mark.contains(se.getID())) {
                continue;
            } else {
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
                    if (visible) {
                        visible = childrenAreVisible(props, currentID);
                    }
                }
            }
        }
    }

    public boolean isVisible(String id) {
        return ((Boolean)visibilityMap.get(id)).booleanValue();
    }

    public boolean isChildrenVisible(String id) {
        Boolean bool = (Boolean)childrenVisible.get(id);
        if (bool == null) {
            return true;
        } else {
            return bool.booleanValue();
        }
    }
}
