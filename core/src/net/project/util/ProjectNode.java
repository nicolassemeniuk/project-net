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

import net.project.project.ProjectPortfolioRow;
import net.project.resource.ProjectWrapper;
import net.project.security.SessionManager;

import org.json.JSONObject;

/**
 * To generate project nodeList to be shown in ProjectPortfolio in a treeGrid view
 * 
 * @author Ritesh S
 * 
 */
public class ProjectNode {

	private Integer id = 1;

	private Integer level;

	private Integer left;

	private Integer right;

	private final Map map;

	private String title;

	private final List<ProjectNode> nodeList;

	private ProjectNode parent;

	private ProjectWrapper project;
	
    private LinkedList<ProjectPortfolioRow> sequensedProject = new LinkedList<ProjectPortfolioRow>();

	public ProjectNode getParent() {
		return this.parent;
	}

	public void setParent(final ProjectNode parent) {
		this.parent = parent;
	}

	public ProjectNode() {
		super();
		this.nodeList = new ArrayList<ProjectNode>();
		this.map = new HashMap<String, Object>();
		project = new ProjectWrapper();
		this.map.put("_id", this.id);
	}

	public ProjectNode(final List keys) {
		super();
		this.nodeList = new ArrayList<ProjectNode>();
		this.map = new HashMap<String, Object>();
		project = new ProjectWrapper();
		for (Object key : keys) {
			this.map.put(key.toString(), null);
		}
		this.map.put("_id", this.id);
	}

	/**
	 * Check if the node is Leaf node of tree @ return true/false
	 */
	public boolean isLeaf() {
		return this.nodeList.size() == 0 ? true : false;
	}

	/**
	 * To add a node to tree @ return true/false
	 */
	public boolean add(final ProjectNode newNode) {
		boolean result = this.nodeList.add(newNode);
		newNode.setParent(this);
		return result;
	}

	public ListIterator<ProjectNode> listIterator() {
		return this.nodeList.listIterator();
	}

	public int size() {
		return this.nodeList.size();
	}

	public List<ProjectNode> getNodes() {
		return this.nodeList;
	}

	public Map getMap() {
		return this.map;
	}

	/**
	 * To get depth of node in tree hierarchy level
	 * 
	 * @return depth
	 */
	public int deep() {
		if (this.parent == null) {
			return 1;
		} else
			return 1 + (this.parent.deep());
	}

	/**
	 * To get nodeList hierarchy level
	 * 
	 * @return
	 */
	public int getHierarchyLevel() {
		return this.deep();
	}

	public int order(int counter) {
		this.left = counter;
		if (this.isLeaf()) {
			counter += 1;
			this.right = counter;
			return counter;
		}

		for (ProjectNode sub : this.nodeList) {
			counter = sub.order(++counter);
		}

		this.right = counter + 1;

		return counter + 1;

	}

	/**
	 * To get JSON String for corresponding Tree nodes
	 * 
	 * @return
	 */
	public String toJSON() {
		int nodesSize = this.nodeList.size();

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

		for (ProjectNode nodeList : this.nodeList) {
			sb.append(",");
			sb.append(nodeList.toJSON());
		}

		return sb.toString();
	}

	/**
	 * To get node id
	 * 
	 * @return
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * To set node id
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * To set node level in tree hierarchy
	 * 
	 * @return
	 */
	public Integer getLevel() {
		return this.level;
	}

	/**
	 * To get node level in tree hierarchy
	 * 
	 * @param level
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * To get left leaf node from tree hierarchy
	 * 
	 * @return
	 */
	public Integer getLeft() {
		return this.left;
	}

	/**
	 * To set left leaf node in tree hierarchy
	 * 
	 * @param left
	 * @return
	 */
	public Integer setLeft(Integer left) {
		this.left = left;
		return this.left;
	}

	/**
	 * @return
	 */
	public Integer getRight() {
		return this.right;
	}

	/**
	 * @param right
	 * @return
	 */
	public Integer setRight(Integer right) {
		this.right = right;
		return this.right;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public Object set(Object key, Object value) {
		return this.map.put(key, value);
	}

	/**
	 * @param key
	 * @return
	 */
	public Object get(Object key) {
		return this.map.get(key);
	}

	/**
	 * @return the project
	 */
	public ProjectWrapper getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(ProjectWrapper project) {
		this.project = project;
	}

	/**
	 * To get parent project id
	 * 
	 * @return
	 */
	public String getParentProjectId() {
		return this.parent.toString() == null ? "" : String.valueOf(this.parent.getProject().getParentProjectId());
	}

	/**
	 * To get parents base object type
	 * 
	 * @return
	 */
	public String getParentSpaceType() {
		return this.parent.toString() == null ? "" : this.parent.getProject().getParentSpaceType();
	}

	/**
	 * To get elbow lines depending on level of nodeList
	 * 
	 * @return
	 */
	public String getElbowLineHTML() {
		if (this.deep() > 1) {
			return "<img src=\"" + SessionManager.getJSPRootURL() + "/s.gif\" height=\"1\" width=\""
					+ ((this.deep() - 1) * 20) + "\" />";
		} else {
			return "";
		}
	}

	/**
	 * To check last nodeList
	 * 
	 * @return
	 */
	public boolean getNextNode() {
		if (this.parent != null) {
			List<ProjectNode> nodeList = this.parent.getNodes();
			ProjectNode lastNode = nodeList.get(nodeList.size() - 1);
			if (lastNode.getId().equals(this.getId())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the sequensedProject
	 */
	public LinkedList<ProjectPortfolioRow> getSequensedProject() {
		return sequensedProject;
	}

	/**
	 * @param sequensedProject the sequensedProject to set
	 */
	public void setSequensedProject(LinkedList<ProjectPortfolioRow> sequensedProject) {
		this.sequensedProject = sequensedProject;
	}
}
