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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.project.code.ImprovementCode;
import net.project.project.NoSuchPropertyException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectStatus;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.ProjectNode;
import net.project.util.ProjectNodeFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class will create tree structured Node list for the Project Portfolio
 * 
 * @author Ritesh S
 * 
 */
public class ProjectStoreDataFactory {

	private static Logger log = Logger.getLogger(ProjectStoreDataFactory.class);

	private DateFormat currentuserDateFormat = DateFormat.getInstance();
	
	private String jspRootURL = SessionManager.getJSPRootURL(); 

	private User user = SessionManager.getUser();

	/**
	 * Method to create tree structured project node list
	 * 
	 * @param projects
	 *            total projects loaded from the database
	 * @return List of tree structure project node list
	 */
	public List<ProjectNode> getProjectGridDataString(Collection<ProjectSpace> projectSpace, boolean isDeafultTreeView) {
		
		StringBuilder jsonData = new StringBuilder();

		final ProjectNodeFactory projectNodeFactory = new ProjectNodeFactory();
		List<ProjectNode> nodeToReturn = new ArrayList<ProjectNode>();
		if (CollectionUtils.isNotEmpty(projectSpace)) {
			ProjectNode parentNode = null;

			for (ProjectSpace project : projectSpace) {
				parentNode = getNewNode(projectNodeFactory, project);
			}
			// create tree structure if default tree view by adding a child node in to parent
			if(isDeafultTreeView){
				List<ProjectNode> checkParent = projectNodeFactory.getProjectNodes();
				for (ProjectNode childNode : checkParent) {
					ProjectNode addedNode = null;

					if (childNode.getMap().get("parentProjectId") != null)
						addedNode = getNodeForId(checkParent, Integer.parseInt(childNode.getMap().get("parentProjectId")
								.toString()));
					if (addedNode != null) {
						addedNode.add(childNode);
					}
				}
			}
		}
		
		// Create json data string
		List<ProjectNode> nodes = projectNodeFactory.getProjectNodes();
		if (CollectionUtils.isNotEmpty(nodes)) {
			int order = 1;
			for (ProjectNode node : nodes) {
				/*
				 * Check whether this node has a parent. In such a case it will be added to the json represention by its
				 * parent.
				 */
				if (null == MapUtils.getString(node.getMap(), "_parent")) {
					node.order(order);
					order = node.getRight() + 1;
					jsonData.append(node.toJSON());
					jsonData.append(',');
				}
			}

			jsonData.deleteCharAt(jsonData.length() - 1);

			for (ProjectNode node : nodes) {
				if (null == MapUtils.getString(node.getMap(), "_parent")) {
					nodeToReturn.add(node);
					for (ProjectNode node1 : node.getNodes()) {
						nodeToReturn.add(node1);
						nodeToReturn.addAll(getChildNodes(node1));
					}
				}
			}
		}
		return nodeToReturn;
	}

	/**
	 * Method to create Project node for the current project details
	 */
	private ProjectNode getNewNode(ProjectNodeFactory projectNodeFactory, ProjectSpace projectSpace) {

		ProjectNode projectNode = projectNodeFactory.nextProjectNode();

		projectNode.set("projectId", projectSpace.getID());
		projectNode.getProject().setProjectId(Integer.parseInt(projectSpace.getID()));

		if (StringUtils.isEmpty(projectSpace.getParentProjectID())) {
			projectNode.set("parentProjectId", "");
			projectNode.getProject().setParentProjectId(String.valueOf(""));
		} else {
			projectNode.set("parentProjectId", projectSpace.getParentProjectID());
			projectNode.getProject().setParentProjectId(String.valueOf(projectSpace.getParentProjectID()));
		}
		projectNode.set("parentProjectId", projectSpace.getParentProjectID());
		projectNode.getProject().setParentProjectId(String.valueOf(projectSpace.getParentProjectID()));

		projectNode.set("parentSpaceType", SpaceTypes.getForID(projectSpace.getParentSpaceID()));
		projectNode.getProject().setParentSpaceType(projectSpace.getParentSpaceID());

		projectNode.set("projectName", HTMLUtils.escape(projectSpace.getName()).replaceAll("'", "&acute;"));
		projectNode.getProject().setProjectName(HTMLUtils.escape(projectSpace.getName()).replaceAll("'", "&acute;"));
		projectNode.getProject().setProjectNameTooltip(projectSpace.getName());
		
		if (projectSpace.getStartDate() == null) {
			projectNode.getProject().setStartDate("");
		} else {
			projectNode.getProject().setStartDate(currentuserDateFormat.formatDateMedium(projectSpace.getStartDate()));
		}

		if (projectSpace.getEndDate() == null) {
			projectNode.getProject().setEndDate("");
		} else {
			projectNode.getProject().setEndDate(currentuserDateFormat.formatDateMedium(projectSpace.getEndDate()));
		}

		if(StringUtils.isEmpty(projectSpace.getPercentComplete())) {
			projectNode.getProject().setPercentComplete("0.0");
		} else {
			String percentCompleteValue = NumberFormat.getInstance().formatPercent(Double.parseDouble(projectSpace.getPercentComplete())/100, 2);
			String progressBarHtmlString =
					"	<div class=\"progress-bar-container\">"
					+	"	<div class=\"progress-line\" style=\"width:"+projectSpace.getPercentComplete()+"%;\">"+percentCompleteValue+"</div>"
					+	"</div>";	
			projectNode.getProject().setPercentComplete(progressBarHtmlString);
			projectNode.getProject().setPercentCompleteValue(percentCompleteValue);
		}
		
		if(StringUtils.isEmpty(projectSpace.getCurrentStatusDescription())){
			projectNode.getProject().setCurrentStatusDescription("");
		} else {
			projectNode.getProject().setCurrentStatusDescription(projectSpace.getCurrentStatusDescription());
		}

		projectNode.getProject().setProjectStatus(ProjectStatus.findByID(projectSpace.getStatusID()).getName() );
		
		if(projectSpace.getColorCode() == null){
			projectNode.getProject().setOverAllColorCode("");
			projectNode.getProject().setOverAllImprovementCodeUrl("");
		} else {
			if(projectSpace.getImprovementCode() == null){
				projectNode.getProject().setOverAllImprovementCodeUrl(ImprovementCode.NO_CHANGE.getImageURL(projectSpace.getColorCode()));
			} else {
				projectNode.getProject().setOverAllImprovementCodeUrl(projectSpace.getImprovementCode().getImageURL(projectSpace.getColorCode()));
			}
			projectNode.getProject().setOverAllColorCode(projectSpace.getColorCode().getID());
		}
		
		if(projectSpace.getFinancialStatusColorCode() == null){
			projectNode.getProject().setFinancialStatusColorCode("");
			projectNode.getProject().setFinancialStatusCodeUrl("");
		} else {
			if(projectSpace.getImprovementCode() == null){
				projectNode.getProject().setFinancialStatusCodeUrl(ImprovementCode.NO_CHANGE.getImageURL(projectSpace.getFinancialStatusColorCode()));
			} else {
				projectNode.getProject().setFinancialStatusCodeUrl(projectSpace.getFinancialStatusImprovementCode().getImageURL(projectSpace.getFinancialStatusColorCode()));
			}
			projectNode.getProject().setFinancialStatusColorCode((projectSpace.getFinancialStatusColorCode().getID()));
		}
		
		if(projectSpace.getScheduleStatusColorCode() == null){
			projectNode.getProject().setScheduleStatusColorCode("");
			projectNode.getProject().setScheduleStatusCodeUrl("");
		} else {
			if(projectSpace.getImprovementCode() == null){
				projectNode.getProject().setScheduleStatusCodeUrl(ImprovementCode.NO_CHANGE.getImageURL(projectSpace.getScheduleStatusColorCode()));
			} else {
				projectNode.getProject().setScheduleStatusCodeUrl(projectSpace.getScheduleStatusImprovementCode().getImageURL(projectSpace.getScheduleStatusColorCode()));
			}
			projectNode.getProject().setScheduleStatusColorCode(projectSpace.getScheduleStatusColorCode().getID());
		}

		if(projectSpace.getResourceStatusColorCode() == null){
			projectNode.getProject().setResourceStatusColorCode("");
			projectNode.getProject().setResourceStatusCodeUrl("");
		} else {
			if(projectSpace.getImprovementCode() == null){
				projectNode.getProject().setResourceStatusCodeUrl(ImprovementCode.NO_CHANGE.getImageURL(projectSpace.getResourceStatusColorCode()));
			} else {
				projectNode.getProject().setResourceStatusCodeUrl(projectSpace.getResourceStatusImprovementCode().getImageURL(projectSpace.getResourceStatusColorCode()));
			}
			projectNode.getProject().setResourceStatusColorCode(projectSpace.getResourceStatusColorCode().getID());
		}
		
		if(StringUtils.isEmpty(projectSpace.getDescription())){
			projectNode.getProject().setProjectDesc("");
		} else {
			projectNode.getProject().setProjectDesc(projectSpace.getDescription());
		}

		if(StringUtils.isEmpty(projectSpace.getParentBusinessName())){
			projectNode.getProject().setParentBusinessName("");
		} else {
			projectNode.getProject().setParentBusinessName(projectSpace.getParentBusinessName());
		}
		
		if(StringUtils.isEmpty(projectSpace.getSuperProjectName())){
			projectNode.getProject().setSubProjectOf("");
		} else {
			projectNode.getProject().setSubProjectOf(projectSpace.getSuperProjectName());
		}
		
		if(StringUtils.isEmpty(projectSpace.getSponsor())){
			projectNode.getProject().setSponsor("");
		} else {
			projectNode.getProject().setSponsor(projectSpace.getSponsor());
		}
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("ProjectManager"))){
				projectNode.getProject().setProjectManager("");
			} else {
				projectNode.getProject().setProjectManager(projectSpace.getMetaData().getProperty("ProjectManager"));
			}
		}
		catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}		
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("ProgramManager"))){
				projectNode.getProject().setProgramManager("");
			} else {
				projectNode.getProject().setProgramManager(projectSpace.getMetaData().getProperty("ProgramManager"));
			}
		} catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("Initiative"))){
				projectNode.getProject().setInitiative("");
			} else {
				projectNode.getProject().setInitiative(projectSpace.getMetaData().getProperty("Initiative"));
			}
		} catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("FunctionalArea"))){
				projectNode.getProject().setFunctionalArea("");
			} else {
				projectNode.getProject().setFunctionalArea(projectSpace.getMetaData().getProperty("FunctionalArea"));
			}
		} catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("ProjectCharter"))){
				projectNode.getProject().setProjectCharter("");
			} else {
				projectNode.getProject().setProjectCharter(projectSpace.getMetaData().getProperty("ProjectCharter"));
			}
		} catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}
		
		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("TypeOfExpense"))){
				projectNode.getProject().setTypeOfExpense("");
			} else {
				projectNode.getProject().setTypeOfExpense(projectSpace.getMetaData().getProperty("TypeOfExpense"));
			}
		} catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}
		
		if(projectSpace.getPriorityCode() == null){
			projectNode.getProject().setPriorityCode("");
		} else {
			projectNode.getProject().setPriorityCode(projectSpace.getPriorityCode().getName());
		}

		if(projectSpace.getRiskRatingCode() == null){
			projectNode.getProject().setRiskRatingCode("");
		} else {
			projectNode.getProject().setRiskRatingCode(projectSpace.getRiskRatingCode().getName());
		}
		
		if(projectSpace.getDefaultCurrencyCode()  == null){
			projectNode.getProject().setDefaultCurrencyCode("");
		} else {
			projectNode.getProject().setDefaultCurrencyCode(projectSpace.getDefaultCurrencyCode());
		}

		if(projectSpace.getBudgetedTotalCost() == null){
			projectNode.getProject().setBudgetedTotalCostMoneyValue("");
		} else {
			projectNode.getProject().setBudgetedTotalCostMoneyValue(projectSpace.getBudgetedTotalCost().format(user));
		}

		if(projectSpace.getBudgetedTotalCost() == null){
			projectNode.getProject().setBudgetedTotalCostMoneyCurrency("");
		} else {
			projectNode.getProject().setBudgetedTotalCostMoneyValue(projectSpace.getBudgetedTotalCost().format(user));
		}

		if(projectSpace.getCurrentEstimatedTotalCost() == null){
			projectNode.getProject().setCurrentEstimatedTotalCostMoneyValue("");
		} else {
			projectNode.getProject().setCurrentEstimatedTotalCostMoneyValue(projectSpace.getCurrentEstimatedTotalCost().format(user));
		}

		if(projectSpace.getCurrentEstimatedTotalCost() == null){
			projectNode.getProject().setCurrentEstimatedTotalCostMoneyCurrency("");
		} else {
			projectNode.getProject().setCurrentEstimatedTotalCostMoneyCurrency(projectSpace.getCurrentEstimatedTotalCost().format(user));
		}
		
		if(projectSpace.getActualCostToDate() == null){
			projectNode.getProject().setActualCostToDateMoneyValue("");
		} else {
			projectNode.getProject().setActualCostToDateMoneyValue(projectSpace.getActualCostToDate().format(user));
		}
		
		if(projectSpace.getActualCostToDate() == null){
			projectNode.getProject().setActualCostToDateMoneyCurrency("");
		} else {
			projectNode.getProject().setActualCostToDateMoneyCurrency(projectSpace.getActualCostToDate().format(user));
		}

		if(projectSpace.getEstimatedROI() == null){
			projectNode.getProject().setEstimatedROIMoneyValue("");
		} else {
			projectNode.getProject().setEstimatedROIMoneyValue(projectSpace.getEstimatedROI().format(user));
		}
		
		if(StringUtils.isEmpty(projectSpace.getCostCenter())){
			projectNode.getProject().setCostCenter("");
		} else {
			projectNode.getProject().setCostCenter(projectSpace.getCostCenter());
		}
		
		if(StringUtils.isEmpty(projectSpace.getTemplateApplied())){
			projectNode.getProject().setTemplateApplied("");
		} else {
			projectNode.getProject().setTemplateApplied(projectSpace.getTemplateApplied());
		}

		try {
			if(StringUtils.isEmpty(projectSpace.getMetaData().getProperty("ExternalProjectID"))){
				projectNode.getProject().setExternalProjectId("");
			} else {
				projectNode.getProject().setExternalProjectId(projectSpace.getMetaData().getProperty("ExternalProjectID"));
			}
		}
		catch (NoSuchPropertyException pnetEx) {
			Logger.getLogger(ProjectStoreDataFactory.class).error("Exception in Project Store Data Factory Class.." + pnetEx.getMessage());
		}		

		return projectNode;
	}

	/**
	 * To rearrange the child nodes.
	 * 
	 * @param node
	 * @return
	 */
	private List<ProjectNode> getChildNodes(ProjectNode node) {
		List<ProjectNode> nodeList = new ArrayList<ProjectNode>();
		for (ProjectNode node1 : node.getNodes()) {
			nodeList.add(node1);
			nodeList.addAll(getChildNodes(node1));
		}
		return nodeList;
	}

	/**
	 * Method to find target parent node
	 * 
	 * @param nodes
	 *            Source Nodes that could be parent
	 * @param id
	 *            Parent Projet Id
	 * @return Target parent node
	 */
	private ProjectNode getNodeForId(List<ProjectNode> nodes, Integer id) {
		for (ProjectNode targetParent : nodes) {
			if (id == Integer.parseInt(targetParent.getMap().get("projectId").toString())) {
				return targetParent;
			}
		}
		return null;
	}
}
