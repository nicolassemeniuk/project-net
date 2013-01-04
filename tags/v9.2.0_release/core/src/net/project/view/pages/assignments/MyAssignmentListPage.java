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
package net.project.view.pages.assignments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.project.channel.ScopeType;
import net.project.resource.AssignmentColumn;
import net.project.resource.AssignmentWrapper;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;
import net.project.util.Node;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

/**
 * To render assignments list on ajax request.
 *
 */
public class MyAssignmentListPage extends BasePage {

	//	Used to iterate node list over tml.
	@Property
	@Persist
	private Node node;

	@Persist
	private List<Node> assignmentList;

	@Persist
	private AssignmentColumn assignmentColumn;

	@Persist
	private String windowWidth;
	
	@Persist 
	private String assignmentsCountString;
	
	@Property
	@Persist
	private AssignmentWrapper assignment;
	
	@Persist
	private boolean rearrangeSequence;
	
	private Map visibilityMap = new HashMap();
	

	/**
	 * @param assignmentColumn the assignmentColumn to set
	 */
	public void setAssignmentColumn(AssignmentColumn assignmentColumn) {
		this.assignmentColumn = assignmentColumn;
		PersonProperty property = PersonProperty.getFromSession(getHttpServletRequest().getSession());
		property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
		property.prefetchForContextPrefix("net.project.column.MyAssignments");
		assignmentColumn.constructColumnSettings(property);
		if(isRearrangeSequence()){
			assignmentList = arrangeAssignmentSequence(assignmentList);
			setRearrangeSequence(false);
		}
		
	}

	/**
	 * @return the assignmentList
	 */
	public List<Node> getAssignmentList() {
		return assignmentList;
	}

	/**
	 * @param assignmentList the assignmentList to set
	 */
	public void setAssignmentList(List<Node> assignmentList) {
		this.assignmentList = assignmentList;
	}

	/**
	 * To get first assignment Id
	 * @return
	 */
	public String getFirstAssignmentId(){
		
		 if (assignmentList.size() > 0) {
			 Node firstNode = (Node)assignmentList.get(0);
	         return firstNode.getAssignment().getObjectID()+"_"+firstNode.getId();
	     } else {
	         return "";
	     }
	}
	
	/**
	 * To rearrange assignment data
	 * @param assignmentList
	 * @return
	 */
	public List<Node> arrangeAssignmentSequence(List<Node> assignmentList){
		for(Node node : assignmentList){
			node.setSequensedAssignment(new LinkedList<AssignmentWrapper>());
			for(AssignmentColumn assignmentColumn : getAssignmentColumn().getAssignmentColumnList()){
				if(assignmentColumn.getVisible() && !assignmentColumn.getId().equalsIgnoreCase("objectName")){
					AssignmentWrapper assignment = new AssignmentWrapper();
					if(assignmentColumn.getId().equalsIgnoreCase("type")){
						assignment.setCurrentSequencedData(node.getAssignment().getObjectType());
					}else if(assignmentColumn.getId().equalsIgnoreCase("workSpace")){
						assignment.setCurrentSequencedData(node.getAssignment().getWorkSpace());
					}else if(assignmentColumn.getId().equalsIgnoreCase("startDate")){
						assignment.setCurrentSequencedData(node.getAssignment().getStartDate());
					}else if(assignmentColumn.getId().equalsIgnoreCase("dueDate")){
						assignment.setCurrentSequencedData(node.getAssignment().getDueDate());
					}else if(assignmentColumn.getId().equalsIgnoreCase("actualStartDate")){
						assignment.setCurrentSequencedData(node.getAssignment().getActualStartDate());
					}else if(assignmentColumn.getId().equalsIgnoreCase("myPercentComplete")){
						assignment.setCurrentSequencedData(node.getAssignment().getMyPercentComplete());
					}else if(assignmentColumn.getId().equalsIgnoreCase("myWork")){
						assignment.setCurrentSequencedData(node.getAssignment().getMyWork());
					}else if(assignmentColumn.getId().equalsIgnoreCase("myWorkComplete")){
						assignment.setCurrentSequencedData(node.getAssignment().getMyWorkComplete());
					}else if(assignmentColumn.getId().equalsIgnoreCase("assigneeName")){
						assignment.setCurrentSequencedData(node.getAssignment().getAssigneeName());
					}else if(assignmentColumn.getId().equalsIgnoreCase("assignorName")){
						assignment.setCurrentSequencedData(node.getAssignment().getAssignorName());
					}else if(assignmentColumn.getId().equalsIgnoreCase("workRemaining")){
						assignment.setCurrentSequencedData(node.getAssignment().getWorkRemaining());
					}
					assignment.setCurrentType(assignmentColumn.getId());
					node.getSequensedAssignment().add(assignment);
				}
			}
		}
		constructVisibility(assignmentList);
		return assignmentList;
	}
	
	/**
	 * To construct visibility of assignments nodes
	 * @param assignments
	 */
	public void constructVisibility(List<Node> assignments) {
        //List of tasks that we have already visited and know their visibility.
    	HashSet mark = new HashSet();
        //Map<Node> taskMap = this.assignmentList;

        //Get all the visibility properties from the database beforehand so
        //we don't have to fetch each one.
        PersonProperty props = new PersonProperty();
        props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        props.prefetchForContextPrefix("net.project.column.MyAssignments");
        //Iterate through the tasks and assign them visibility.
        for (Iterator it = assignments.iterator(); it.hasNext();) {
        		Node as = (Node) it.next();
                boolean visible = true;
                //Build a stack up to the parent for this task.
                Stack hierarchyStack = new Stack();
                String currentID = as.getAssignment().getObjectID();
                do {
                    hierarchyStack.push(currentID);
                    Node currentTask = (Node) getCurrentAssignment(currentID, assignments);
                    if (currentTask == null) {
                        //This can happen when filtering -- parent task isn't going
                        //to be in the list.
                        break;
                    }
                    currentID = currentTask.getParentAssignmentId();
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
                as.getAssignment().setVisible(visible);
                as.getAssignment().setExpanded(getExpandedProp(props, as.getAssignment().getObjectID()));
             }
    }
	
	 /**
	 * To check children are visible or not
	 * @param props
	 * @param id
	 * @return
	 */
	private boolean childrenAreVisible(PersonProperty props, String id) {
	        boolean childrenAreVisible = isVisible(id);
	        //Just because an object is visible, doesn't mean its children are visible.
	        //It could be the "collapsed summary task".
	        if (childrenAreVisible) {
	            childrenAreVisible = getExpandedProp(props, id);
	        }
	        return childrenAreVisible;
	    }
	 
	 /**
	 * To get assignment expand/collapse status 
	 * @param props
	 * @param id
	 * @return
	 */
	private boolean getExpandedProp(PersonProperty props, String id) {
	        String[] expandedProps = props.get("net.project.column.MyAssignments", "node"+id+"expanded", true);
	        String expandedProp = (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: "true");
	        return (expandedProp != null && expandedProp.equals("true"));
	    }
	
	 public boolean isVisible(String id) {
	        return ((Boolean)visibilityMap.get(id)).booleanValue();
	 }
	/**
	 * To get node from assignment list
	 * @param id
	 * @param assignments
	 * @return
	 */
	public Node getCurrentAssignment(String id, List<Node> assignments){
		for(Node node : assignments){
			if(node.getAssignment().getObjectID().equalsIgnoreCase(id)){
				return node;
			}
		}
		return null;      
	}
	
	/**
	 * To populate visilibility map
	 * @param id
	 * @param visible
	 * @param props
	 */
	private void populateVisibilityMap(String id, boolean visible, PersonProperty props) {
	        visibilityMap.put(id, new Boolean(visible));
	}
	  
	/**
	 * @return the assignmentsCountString
	 */
	public String getAssignmentsCountString() {
		return assignmentsCountString;
	}

	/**
	 * @param assignmentsCountString the assignmentsCountString to set
	 */
	public void setAssignmentsCountString(String assignmentsCountString) {
		this.assignmentsCountString = assignmentsCountString;
	}

	/**
	 * @return the rearrangeSequence
	 */
	public boolean isRearrangeSequence() {
		return rearrangeSequence;
	}

	/**
	 * @param rearrangeSequence the rearrangeSequence to set
	 */
	public void setRearrangeSequence(boolean rearrangeSequence) {
		this.rearrangeSequence = rearrangeSequence;
	}
	/**
	 * @return the assignmentColumn
	 */
	public AssignmentColumn getAssignmentColumn() {
		return assignmentColumn;
	}

}
