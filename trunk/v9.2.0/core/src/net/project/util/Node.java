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
package net.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.project.resource.AssignmentWrapper;
import net.project.security.SessionManager;

import org.json.JSONObject;

public class Node {
    private Integer id = 1;
    private Integer level;
    private Integer left;
    private Integer right;
    private final Map map;
    private String title;
    private final List<Node> nodes;
    private Node parent;
    private AssignmentWrapper assignment;
    private LinkedList<AssignmentWrapper> sequensedAssignment = new LinkedList<AssignmentWrapper>();

	public Node getParent() {
        return this.parent;
    }

    public void setParent(final Node parent) {
        this.parent = parent;
    }

    public Node() {
        super();
        this.nodes = new ArrayList<Node>();
        this.map = new HashMap<String, Object>();
        assignment = new AssignmentWrapper();
        this.map.put("_id", this.id);
    }

    public Node(final List keys) {
        super();
        this.nodes = new ArrayList<Node>();
        this.map = new HashMap<String, Object>();
        assignment = new AssignmentWrapper();
        for (Object key : keys) {
            this.map.put(key.toString(), null);
        }
        this.map.put("_id", this.id);
    }

    public boolean isLeaf() {
        return this.nodes.size() == 0 ? true : false;
    }

    public boolean add(final Node newNode) {
        boolean result = this.nodes.add(newNode);
        newNode.setParent(this);
        return result;
    }

    public ListIterator<Node> listIterator() {
        return this.nodes.listIterator();
    }

    public int size() {
        return this.nodes.size();
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public Map getMap() {
        return this.map;
    }

    public int deep() {
        if (this.parent == null) {
            return 1;
        } else
            return 1 + (this.parent.deep());
    }
    
    /**
     * To get nodes hierarchy level
     * @return
     */
    public int getHierarchyLevel(){
    	return this.deep();
    }

    public int order(int counter) {
        this.left = counter;
        if (this.isLeaf()) {
            counter += 1;
            this.right = counter;
            return counter;
        }

        for (Node sub : this.nodes) {
            counter = sub.order(++counter);
        }

        this.right = counter + 1;

        return counter + 1;

    }

    public String toJSON() {
        int nodesSize = this.nodes.size();
        
        this.map.put("_id", this.id);
        this.map.put("_parent", this.parent == null ? null : this.parent.id);
        this.map.put("_level", this.deep());
        this.map.put("_is_leaf", nodesSize > 0 ? false : true);
        this.map.put("title", this.title);
        this.map.put("_rgt", this.right);
        this.map.put("_lft", this.left);

        if (nodesSize > 0) {
            String title = (String) this.map.get("title");
            this.map.put("title", title + " (" + nodesSize + ")");
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(new JSONObject(this.map).toString());

        for (Node node : this.nodes) {
            sb.append(",");
            sb.append(node.toJSON());
        }

        return sb.toString();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLeft() {
        return this.left;
    }

    public Integer setLeft(Integer left) {
        this.left = left;
        return this.left;
    }

    public Integer getRight() {
        return this.right;
    }

    public Integer setRight(Integer right) {
        this.right = right;
        return this.right;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object set(Object key, Object value) {
        return this.map.put(key, value);
    }
    
    public Object get(Object key) {
        return this.map.get(key);
    }

	/**
	 * @return the assignment
	 */
	public AssignmentWrapper getAssignment() {
		return assignment;
	}

	/**
	 * @param assignment the assignment to set
	 */
	public void setAssignment(AssignmentWrapper assignment) {
		this.assignment = assignment;
	}
	
	/**
	 * To get parent assignment id
	 * @return
	 */
	public String getParentAssignmentId(){
		return this.parent == null ? "" : this.parent.getAssignment().getObjectID();
	}
	  
	/**
	 * To get parents base object type
	 * @return
	 */
	public String getParentBaseObjectType(){
		return this.parent == null ? "" : this.parent.getAssignment().getBaseObjectType();
	}
	
	/**
	 * To get elbow lines depending on level of node
	 * @return
	 */
	public String getElbowLineHTML() {
		if (this.deep() > 1) {
			return "<img src=\""+SessionManager.getJSPRootURL() +"/s.gif\" height=\"1\" width=\"" + ((this.deep() - 1) * 20) + "\" />";
		} else {
			return "";
		}
	}
	
	/**
	 * To check last node
	 * @return
	 */
	public boolean getNextNode(){
		if(this.parent != null){
			List<Node> nodes = this.parent.getNodes();
			Node lastNode = nodes.get(nodes.size()-1);
			if(lastNode.getId().equals(this.getId())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return the sequensedAssignment
	 */
	public LinkedList<AssignmentWrapper> getSequensedAssignment() {
		return sequensedAssignment;
	}

	/**
	 * @param sequensedAssignment the sequensedAssignment to set
	 */
	public void setSequensedAssignment(LinkedList<AssignmentWrapper> sequensedAssignment) {
		this.sequensedAssignment = sequensedAssignment;
	}
}
