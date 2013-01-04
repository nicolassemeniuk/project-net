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

import net.project.hibernate.model.PnObject;

/**
 * To render headers on project portfolio page
 * 
 * @author Ritesh S
 * 
 */
public class ProjectWrapper {

	private Integer projectId;
	private String projectDesc;
	private String projectName;
	private String percentComplete;
	private String startDate;
	private String endDate;
	private String dateModified;
	private String overAllImprovementCodeUrl;
	private String financialStatusCodeUrl;
	private String scheduleStatusCodeUrl;
	private String resourceStatusCodeUrl;
	private PnObject pnObject;
	private String parentProjectId;
	private String parentSpaceType;
	private String projectStatus;
	private String overAllColorCode;
	private String financialStatusColorCode;
	private String scheduleStatusColorCode;
	private String resourceStatusColorCode;
	private String recordStatus;
	private String parentBusinessName;
	private String subProjectOf;
	private String sponsor;
	private String projectManager;
	private String programManager;
	private String initiative;
	private String functionalArea;
	private String priorityCode;
	private String riskRatingCode;
	private String projectCharter;
	private String currentStatusDescription;
	private String statusCode;
	private String defaultCurrencyCode;
	private String typeOfExpense;
	private String budgetedTotalCostMoneyValue;
	private String budgetedTotalCostMoneyCurrency;
	private String currentEstimatedTotalCostMoneyValue;
	private String currentEstimatedTotalCostMoneyCurrency;
	private String actualCostToDateMoneyValue;
	private String actualCostToDateMoneyCurrency;
	private String estimatedROIMoneyValue;
	private String costCenter;
	private boolean expanded;
	private String percentCompleteValue;
	private String templateApplied;
	private String projectNameTooltip;

	
	// the projectId explicitly entered by user while creating a project.
	private String externalProjectId; 

	
	/**
	 * @return the pnObject
	 */
	public PnObject getPnObject() {
		return pnObject;
	}

	/**
	 * @param pnObject
	 *            the pnObject to set
	 */
	public void setPnObject(PnObject pnObject) {
		this.pnObject = pnObject;
	}

	/**
	 * @return the projectId
	 */
	public Integer getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the projectDesc
	 */
	public String getProjectDesc() {
		return projectDesc;
	}

	/**
	 * @param projectDesc
	 *            the projectDesc to set
	 */
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the percentComplete
	 */
	public String getPercentComplete() {
		return percentComplete;
	}

	/**
	 * @param percentComplete
	 *            the percentComplete to set
	 */
	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the dateModified
	 */
	public String getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified
	 *            the dateModified to set
	 */
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	/**
	 * @return the recordStatus
	 */
	public String getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus
	 *            the recordStatus to set
	 */
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * @return the parentProjectId
	 */
	public String getParentProjectId() {
		return parentProjectId;
	}

	/**
	 * @param parentProjectId
	 *            the parentProjectId to set
	 */
	public void setParentProjectId(String parentProjectId) {
		this.parentProjectId = parentProjectId;
	}

	/**
	 * @return the parentSpaceType
	 */
	public String getParentSpaceType() {
		return parentSpaceType;
	}

	/**
	 * @param parentSpaceType
	 *            the parentSpaceType to set
	 */
	public void setParentSpaceType(String parentSpaceType) {
		this.parentSpaceType = parentSpaceType;
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

	/**
	 * @return the projectStatus
	 */
	public String getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus the projectStatus to set
	 */
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	/**
	 * @return the financialStatusCodeUrl
	 */
	public String getFinancialStatusCodeUrl() {
		return financialStatusCodeUrl;
	}

	/**
	 * @param financialStatusCodeUrl the financialStatusCodeUrl to set
	 */
	public void setFinancialStatusCodeUrl(String financialStatusCodeUrl) {
		this.financialStatusCodeUrl = financialStatusCodeUrl;
	}

	/**
	 * @return the overAllImprovementCodeUrl
	 */
	public String getOverAllImprovementCodeUrl() {
		return overAllImprovementCodeUrl;
	}

	/**
	 * @param overAllImprovementCodeUrl the overAllImprovementCodeUrl to set
	 */
	public void setOverAllImprovementCodeUrl(String overAllImprovementCodeUrl) {
		this.overAllImprovementCodeUrl = overAllImprovementCodeUrl;
	}

	/**
	 * @return the resourceStatusCodeUrl
	 */
	public String getResourceStatusCodeUrl() {
		return resourceStatusCodeUrl;
	}

	/**
	 * @param resourceStatusCodeUrl the resourceStatusCodeUrl to set
	 */
	public void setResourceStatusCodeUrl(String resourceStatusCodeUrl) {
		this.resourceStatusCodeUrl = resourceStatusCodeUrl;
	}

	/**
	 * @return the scheduleStatusCodeUrl
	 */
	public String getScheduleStatusCodeUrl() {
		return scheduleStatusCodeUrl;
	}

	/**
	 * @param scheduleStatusCodeUrl the scheduleStatusCodeUrl to set
	 */
	public void setScheduleStatusCodeUrl(String scheduleStatusCodeUrl) {
		this.scheduleStatusCodeUrl = scheduleStatusCodeUrl;
	}

	/**
	 * @return the financialStatusColorCode
	 */
	public String getFinancialStatusColorCode() {
		return financialStatusColorCode;
	}

	/**
	 * @param financialStatusColorCode the financialStatusColorCode to set
	 */
	public void setFinancialStatusColorCode(String financialStatusColorCode) {
		this.financialStatusColorCode = financialStatusColorCode;
	}

	/**
	 * @return the overAllColorCode
	 */
	public String getOverAllColorCode() {
		return overAllColorCode;
	}

	/**
	 * @param overAllColorCode the overAllColorCode to set
	 */
	public void setOverAllColorCode(String overAllColorCode) {
		this.overAllColorCode = overAllColorCode;
	}

	/**
	 * @return the resourceStatusColorCode
	 */
	public String getResourceStatusColorCode() {
		return resourceStatusColorCode;
	}

	/**
	 * @param resourceStatusColorCode the resourceStatusColorCode to set
	 */
	public void setResourceStatusColorCode(String resourceStatusColorCode) {
		this.resourceStatusColorCode = resourceStatusColorCode;
	}

	/**
	 * @return the scheduleStatusColorCode
	 */
	public String getScheduleStatusColorCode() {
		return scheduleStatusColorCode;
	}

	/**
	 * @param scheduleStatusColorCode the scheduleStatusColorCode to set
	 */
	public void setScheduleStatusColorCode(String scheduleStatusColorCode) {
		this.scheduleStatusColorCode = scheduleStatusColorCode;
	}

	/**
	 * @return the actualCostToDateMoneyCurrency
	 */
	public String getActualCostToDateMoneyCurrency() {
		return actualCostToDateMoneyCurrency;
	}

	/**
	 * @param actualCostToDateMoneyCurrency the actualCostToDateMoneyCurrency to set
	 */
	public void setActualCostToDateMoneyCurrency(String actualCostToDateMoneyCurrency) {
		this.actualCostToDateMoneyCurrency = actualCostToDateMoneyCurrency;
	}

	/**
	 * @return the actualCostToDateMoneyValue
	 */
	public String getActualCostToDateMoneyValue() {
		return actualCostToDateMoneyValue;
	}

	/**
	 * @param actualCostToDateMoneyValue the actualCostToDateMoneyValue to set
	 */
	public void setActualCostToDateMoneyValue(String actualCostToDateMoneyValue) {
		this.actualCostToDateMoneyValue = actualCostToDateMoneyValue;
	}

	/**
	 * @return the budgetedTotalCostMoneyCurrency
	 */
	public String getBudgetedTotalCostMoneyCurrency() {
		return budgetedTotalCostMoneyCurrency;
	}

	/**
	 * @param budgetedTotalCostMoneyCurrency the budgetedTotalCostMoneyCurrency to set
	 */
	public void setBudgetedTotalCostMoneyCurrency(String budgetedTotalCostMoneyCurrency) {
		this.budgetedTotalCostMoneyCurrency = budgetedTotalCostMoneyCurrency;
	}

	/**
	 * @return the budgetedTotalCostMoneyValue
	 */
	public String getBudgetedTotalCostMoneyValue() {
		return budgetedTotalCostMoneyValue;
	}

	/**
	 * @param budgetedTotalCostMoneyValue the budgetedTotalCostMoneyValue to set
	 */
	public void setBudgetedTotalCostMoneyValue(String budgetedTotalCostMoneyValue) {
		this.budgetedTotalCostMoneyValue = budgetedTotalCostMoneyValue;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the currentEstimatedTotalCostMoneyCurrency
	 */
	public String getCurrentEstimatedTotalCostMoneyCurrency() {
		return currentEstimatedTotalCostMoneyCurrency;
	}

	/**
	 * @param currentEstimatedTotalCostMoneyCurrency the currentEstimatedTotalCostMoneyCurrency to set
	 */
	public void setCurrentEstimatedTotalCostMoneyCurrency(String currentEstimatedTotalCostMoneyCurrency) {
		this.currentEstimatedTotalCostMoneyCurrency = currentEstimatedTotalCostMoneyCurrency;
	}

	/**
	 * @return the currentEstimatedTotalCostMoneyValue
	 */
	public String getCurrentEstimatedTotalCostMoneyValue() {
		return currentEstimatedTotalCostMoneyValue;
	}

	/**
	 * @param currentEstimatedTotalCostMoneyValue the currentEstimatedTotalCostMoneyValue to set
	 */
	public void setCurrentEstimatedTotalCostMoneyValue(String currentEstimatedTotalCostMoneyValue) {
		this.currentEstimatedTotalCostMoneyValue = currentEstimatedTotalCostMoneyValue;
	}

	/**
	 * @return the currentStatusDescription
	 */
	public String getCurrentStatusDescription() {
		return currentStatusDescription;
	}

	/**
	 * @param currentStatusDescription the currentStatusDescription to set
	 */
	public void setCurrentStatusDescription(String currentStatusDescription) {
		this.currentStatusDescription = currentStatusDescription;
	}

	/**
	 * @return the defaultCurrencyCode
	 */
	public String getDefaultCurrencyCode() {
		return defaultCurrencyCode;
	}

	/**
	 * @param defaultCurrencyCode the defaultCurrencyCode to set
	 */
	public void setDefaultCurrencyCode(String defaultCurrencyCode) {
		this.defaultCurrencyCode = defaultCurrencyCode;
	}

	/**
	 * @return the estimatedROIMoneyValue
	 */
	public String getEstimatedROIMoneyValue() {
		return estimatedROIMoneyValue;
	}

	/**
	 * @param estimatedROIMoneyValue the estimatedROIMoneyValue to set
	 */
	public void setEstimatedROIMoneyValue(String estimatedROIMoneyValue) {
		this.estimatedROIMoneyValue = estimatedROIMoneyValue;
	}

	/**
	 * @return the functionalArea
	 */
	public String getFunctionalArea() {
		return functionalArea;
	}

	/**
	 * @param functionalArea the functionalArea to set
	 */
	public void setFunctionalArea(String functionalArea) {
		this.functionalArea = functionalArea;
	}

	/**
	 * @return the initiative
	 */
	public String getInitiative() {
		return initiative;
	}

	/**
	 * @param initiative the initiative to set
	 */
	public void setInitiative(String initiative) {
		this.initiative = initiative;
	}

	/**
	 * @return the parentBusinessName
	 */
	public String getParentBusinessName() {
		return parentBusinessName;
	}

	/**
	 * @param parentBusinessName the parentBusinessName to set
	 */
	public void setParentBusinessName(String parentBusinessName) {
		this.parentBusinessName = parentBusinessName;
	}

	/**
	 * @return the priorityCode
	 */
	public String getPriorityCode() {
		return priorityCode;
	}

	/**
	 * @param priorityCode the priorityCode to set
	 */
	public void setPriorityCode(String priorityCode) {
		this.priorityCode = priorityCode;
	}

	/**
	 * @return the programManager
	 */
	public String getProgramManager() {
		return programManager;
	}

	/**
	 * @param programManager the programManager to set
	 */
	public void setProgramManager(String programManager) {
		this.programManager = programManager;
	}

	/**
	 * @return the projectCharter
	 */
	public String getProjectCharter() {
		return projectCharter;
	}

	/**
	 * @param projectCharter the projectCharter to set
	 */
	public void setProjectCharter(String projectCharter) {
		this.projectCharter = projectCharter;
	}

	/**
	 * @return the projectManager
	 */
	public String getProjectManager() {
		return projectManager;
	}

	/**
	 * @param projectManager the projectManager to set
	 */
	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * @return the riskRatingCode
	 */
	public String getRiskRatingCode() {
		return riskRatingCode;
	}

	/**
	 * @param riskRatingCode the riskRatingCode to set
	 */
	public void setRiskRatingCode(String riskRatingCode) {
		this.riskRatingCode = riskRatingCode;
	}

	/**
	 * @return the sponsor
	 */
	public String getSponsor() {
		return sponsor;
	}

	/**
	 * @param sponsor the sponsor to set
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the subProjectOf
	 */
	public String getSubProjectOf() {
		return subProjectOf;
	}

	/**
	 * @param subProjectOf the subProjectOf to set
	 */
	public void setSubProjectOf(String subProjectOf) {
		this.subProjectOf = subProjectOf;
	}

	/**
	 * @return the typeOfExpense
	 */
	public String getTypeOfExpense() {
		return typeOfExpense;
	}

	/**
	 * @param typeOfExpense the typeOfExpense to set
	 */
	public void setTypeOfExpense(String typeOfExpense) {
		this.typeOfExpense = typeOfExpense;
	}

	/**
	 * @return the percentCompleteValue
	 */
	public String getPercentCompleteValue() {
		return percentCompleteValue;
	}

	/**
	 * @param percentCompleteValue the percentCompleteValue to set
	 */
	public void setPercentCompleteValue(String percentCompleteValue) {
		this.percentCompleteValue = percentCompleteValue;
	}

	/**
	 * @return the templateApplied
	 */
	public String getTemplateApplied() {
		return templateApplied;
	}

	/**
	 * @param templateApplied the templateApplied to set
	 */
	public void setTemplateApplied(String templateApplied) {
		this.templateApplied = templateApplied;
	}

	/**
	 * @return the externalProjectId
	 */
	public String getExternalProjectId() {
		return externalProjectId;
	}

	/**
	 * @param externalProjectId the externalProjectId to set
	 */
	public void setExternalProjectId(String externalProjectId) {
		this.externalProjectId = externalProjectId;
	}

	/**
	 * @return the projectNameTooltip
	 */
	public String getProjectNameTooltip() {
		return projectNameTooltip;
	}

	/**
	 * @param projectNameTooltip the projectNameTooltip to set
	 */
	public void setProjectNameTooltip(String projectNameTooltip) {
		this.projectNameTooltip = projectNameTooltip;
	}
}
