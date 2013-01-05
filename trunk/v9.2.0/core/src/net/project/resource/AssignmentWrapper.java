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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.project.security.SessionManager;

/**
 * To render headers on personal assignments page.
 *
 */
public class AssignmentWrapper extends Assignment {

	private String dueDate;

	private String workRemaining;

	private String assigneeName;

	private String myWork;

	private String myWorkComplete;

	private String startDate;

	private String actualStartDate;

	private String baseObjectType;

	private String myPercentComplete;

	private String workSpace;
	
	private String currentType;
	
	private String currentSequencedData;
	
	private boolean expanded;
	
	private boolean visible;
	
	private BigDecimal workInHours;
	
	private BigDecimal workCompleteInHours;
	
	private BigDecimal workRemainingInHours;
	
	private BigDecimal workPercentComplete;
	
	private Date assignmentstartDate;
	
	private Date assignmentDueDate;
	
	private Date assignmentActualStartDate;
	
	public AssignmentWrapper() {
		super();
	}
	
	/**
	 * @return the css name for assignment depending on it's type
	 */
	public String getAssignmentTypeCss() {
		if(this.getBaseObjectType().equalsIgnoreCase("business") || this.getBaseObjectType().equals("non_business")){
			return "business";
		}else if(this.getBaseObjectType().equalsIgnoreCase("project")){
			return "project";
		}
		return "";
	}
	
	/**
	 * To get image depending on object type
	 * @return
	 */
	public String getImageForAssignment(){
		String jspRootUrl = SessionManager.getJSPRootURL();	
			if(this.getObjectType().equalsIgnoreCase("task")){
				return "<img src='"+jspRootUrl+"/src/extjs/resources/images/default/tree/t.gif'/>&nbsp;";
			} else if(this.getObjectType().equalsIgnoreCase("meeting")){
				return "<img src='"+jspRootUrl+"/src/extjs/resources/images/default/tree/m.gif'/>&nbsp;";		
			} else if(this.getBaseObjectType().equalsIgnoreCase("form_data")){
				return "<img src='"+jspRootUrl+"/src/extjs/resources/images/default/tree/f.gif'/>&nbsp;";
			}
			return null;
	}
	
	/**
	 * @return the actualStartDate
	 */
	public String getActualStartDate() {
		return actualStartDate;
	}

	/**
	 * @param actualStartDate the actualStartDate to set
	 */
	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	/**
	 * @return the assigneeName
	 */
	public String getAssigneeName() {
		return assigneeName;
	}

	/**
	 * @param assigneeName the assigneeName to set
	 */
	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	/**
	 * @return the baseObjectType
	 */
	public String getBaseObjectType() {
		return baseObjectType;
	}

	/**
	 * @param baseObjectType the baseObjectType to set
	 */
	public void setBaseObjectType(String baseObjectType) {
		this.baseObjectType = baseObjectType;
	}

	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the myPercentComplete
	 */
	public String getMyPercentComplete() {
		return myPercentComplete; 
	}

	/**
	 * @param myPercentComplete the myPercentComplete to set
	 */
	public void setMyPercentComplete(String myPercentComplete) {
		this.myPercentComplete = myPercentComplete;
	}

	/**
	 * @return the myWork
	 */
	public String getMyWork() {
		return myWork;
	}

	/**
	 * @param myWork the myWork to set
	 */
	public void setMyWork(String myWork) {
		this.myWork = myWork;
	}

	/**
	 * @return the myWorkComplete
	 */
	public String getMyWorkComplete() {
		return myWorkComplete ;
	}

	/**
	 * @param myWorkComplete the myWorkComplete to set
	 */
	public void setMyWorkComplete(String myWorkComplete) {
		this.myWorkComplete = myWorkComplete;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the workRemaining
	 */
	public String getWorkRemaining() {
		return workRemaining;
	}

	/**
	 * @param workRemaining the workRemaining to set
	 */
	public void setWorkRemaining(String workRemaining) {
		this.workRemaining = workRemaining;
	}

	/* (non-Javadoc)
	 * @see net.project.resource.Assignment#populateAssignment(java.sql.ResultSet)
	 */
	@Override
	protected void populateAssignment(ResultSet result) throws SQLException {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the workSpace
	 */
	public String getWorkSpace() {
		return workSpace;
	}

	/**
	 * @param workSpace the workSpace to set
	 */
	public void setWorkSpace(String workSpace) {
		this.workSpace = workSpace;
	}
	
	/**
	 * @return the currentSequencedData
	 */
	public String getCurrentSequencedData() {
		return currentSequencedData;
	}

	/**
	 * @param currentSequencedData the currentSequencedData to set
	 */
	public void setCurrentSequencedData(String currentSequencedData) {
		this.currentSequencedData = currentSequencedData;
	}

	/**
	 * @return the currentType
	 */
	public String getCurrentType() {
		return currentType;
	}

	/**
	 * @param currentType the currentType to set
	 */
	public void setCurrentType(String currentType) {
		this.currentType = currentType;
	}
	
	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}
   
	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public String getAssignmentVisibilityCSS(){
		if(this.visible){
    		return "visible";
    	}else{
    		return "hidden";
    	}
	}
	
	/**
	 * @return the workCompleteInHours
	 */
	public BigDecimal getWorkCompleteInHours() {
		return workCompleteInHours;
	}

	/**
	 * @param workCompleteInHours the workCompleteInHours to set
	 */
	public void setWorkCompleteInHours(BigDecimal workCompleteInHours) {
		this.workCompleteInHours = workCompleteInHours;
	}

	/**
	 * @return the workInHours
	 */
	public BigDecimal getWorkInHours() {
		return workInHours;
	}

	/**
	 * @param workInHours the workInHours to set
	 */
	public void setWorkInHours(BigDecimal workInHours) {
		this.workInHours = workInHours;
	}
 
	/**
	 * @return the workRemainingInHours
	 */
	public BigDecimal getWorkRemainingInHours() {
		return workRemainingInHours;
	}

	/**
	 * @param workRemainingInHours the workRemainingInHours to set
	 */
	public void setWorkRemainingInHours(BigDecimal workRemainingInHours) {
		this.workRemainingInHours = workRemainingInHours;
	}

	/**
	 * @return the workPercentComplete
	 */
	public BigDecimal getWorkPercentComplete() {
		return workPercentComplete;
	}

	/**
	 * @param workPercentComplete the workPercentComplete to set
	 */
	public void setWorkPercentComplete(BigDecimal workPercentComplete) {
		this.workPercentComplete = workPercentComplete;
	}
	
	/**
	 * @return the assignmentDueDate
	 */
	public Date getAssignmentDueDate() {
		return assignmentDueDate;
	}

	/**
	 * @param assignmentDueDate the assignmentDueDate to set
	 */
	public void setAssignmentDueDate(Date assignmentDueDate) {
		this.assignmentDueDate = assignmentDueDate;
	}

	/**
	 * @return the assignmentstartDate
	 */
	public Date getAssignmentstartDate() {
		return assignmentstartDate;
	}

	/**
	 * @param assignmentstartDate the assignmentstartDate to set
	 */
	public void setAssignmentstartDate(Date assignmentstartDate) {
		this.assignmentstartDate = assignmentstartDate;
	}

	/**
	 * @return the assignmentActualStartDate
	 */
	public Date getAssignmentActualStartDate() {
		return assignmentActualStartDate;
	}

	/**
	 * @param assignmentActualStartDate the assignmentActualStartDate to set
	 */
	public void setAssignmentActualStartDate(Date assignmentActualStartDate) {
		this.assignmentActualStartDate = assignmentActualStartDate;
	}
}
