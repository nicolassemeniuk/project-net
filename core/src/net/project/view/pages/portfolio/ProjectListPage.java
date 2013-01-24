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
package net.project.view.pages.portfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.project.base.finder.FinderSorter;
import net.project.base.property.PropertyProvider;
import net.project.portfolio.view.MetaColumn;
import net.project.portfolio.view.MetaColumnList;
import net.project.project.ProjectPortfolioRow;
import net.project.resource.ProjectWrapper;
import net.project.util.ProjectNode;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

/**
 * To render project list on ajax request.
 *
 * @author Ritesh S
 */
public class ProjectListPage extends BasePage{
	
	// Categories of project portfolio columns 
	private final String[] FINANCIALCOLUMNCATEGORY = {"DefaultCurrencyCode","TypeOfExpense",
			"BudgetedTotalCost/Money/Value","BudgetedTotalCost/Money/Currency",
			"CurrentEstimatedTotalCost/Money/Value","CurrentEstimatedTotalCost/Money/Currency",
			"ActualCostToDate/Money/Value","ActualCostToDate/Money/Currency","EstimatedROI/Money/Value","CostCenter"};
	private final String[] STATUSCOLUMNCATEGORY = {"StartDate","EndDate","status_code","OverallStatus",
			"FinancialStatus","ScheduleStatus","ResourceStatus", "CurrentStatusDescription"};

	private final String OVERALL_STATUS_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.overallstatus.label");
	private final String FINANCIAl_STATUS_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.financialstatus.label");
	private final String SCHEDULE_STATUS_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.schedulestatus.label");
	private final String RESOURCE_STATUS_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.resourcestatus.label");
	private final String WORKCOMPLETE_COLUMN =  PropertyProvider.get("prm.project.propertiesedit.completion.label");
	private final String PROJECT_NAME_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.projectname.lable");
	private final String END_DATE_COLUMN =  PropertyProvider.get("prm.project.portfolio.column.finishdate.lable");
	private final String CURRENT_STATUS_DESCRIPTION_COLUMN = PropertyProvider.get("prm.project.propertiesedit.currentstatusdescription.label");
	private final String EXTERNAL_PROJECT_ID_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.projectid");
	private final String SPONSOR_COLUMN = PropertyProvider.get("prm.project.properties.sponsor.label");
	private final String PROJECT_MANAGER_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.projectmanager");
	private final String PROGRAM_MANAGER_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.programmanager");
	private final String INITIATIVE_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.initiative");
	private final String FUNCTIONALAREA_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.functionalarea");
	private final String PRIORITY_CODE_COLUMN = PropertyProvider.get("prm.project.properties.priority.label");
	private final String RISKRATING_CODE_COLUMN = PropertyProvider.get("prm.project.properties.risk.label");
	private final String PROJECT_CHARTER_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.projectcharter");
	private final String TYPE_OF_EXPENSE_COLUMN = PropertyProvider.get("prm.project.create.wizard.meta.typeofexpense");
	private final String BUDGETEDTOTALCOST_MONEY_VALUE_COLUMN = PropertyProvider.get("prm.project.properties.budgetedtotalcost.label");
	private final String BUDGETEDTOTALCOST_MONEY_CURRENCY_COLUMN = PropertyProvider.get("prm.project.portfolio.finder.column.budgetedtotalcostcurrency");
	private final String CURRENTESTIMATEDTOTALCOST_MONEY_VALUE_COLUMN = PropertyProvider.get("prm.project.properties.currentestimatedtotalcost.label");
	private final String CURRENTESTIMATEDTOTALCOST_MONEY_CURRENCY_COLUMN = PropertyProvider.get("prm.project.properties.currentestimatedtotalcost.currency.label");
	private final String ACTUALCOSTTODATE_MONEY_VALUE_COLUMN = PropertyProvider.get("prm.project.properties.actualcosttodate.label");
	private final String ACTUALCOSTTODATE_MONEY_CURRENCY_COLUMN = PropertyProvider.get("prm.project.properties.actualcosttodate.currency.label");
	private final String ESTIMATEDROI_MONEY_VALUE_COLUMN = PropertyProvider.get("prm.project.properties.estimatedroi.label");
	private final String COST_CENTER_COLUMN = PropertyProvider.get("prm.project.properties.costcenter.label");

    //	Used to iterate node list over tml.
	@Property
	@Persist
	private ProjectNode node;

	@Persist
	private List<ProjectNode> projectList;

	@Persist
	private List<MetaColumn> projectColumnList;
	
	@Persist
	private String windowWidth;
	
	@Persist 
	private String projectsCountString;
	
	@Property
	@Persist
	private ProjectWrapper project;
	
	@Persist
	private boolean rearrangeSequence;
	
	@Property
	private MetaColumn projectColumn;
	
	@Property
	@Persist
	private ProjectPortfolioRow projectRow;

	@Persist
	private MetaColumnList allColumnList;
	
	@Persist
	private List<FinderSorter> sorterList;
	
	/**
	 * @param assignmentColumn the assignmentColumn to set
	 */
	public void setProjectColumnList(MetaColumnList columnList, String viewType) {
		setAllColumnList(columnList);
		if (viewType.equalsIgnoreCase("tree")){
			projectColumnList = getProjectColumnList(columnList);
		} else {
			projectColumnList = columnList.getSortedIncludedColumns();
		}
		updatedtMetaColumn();
	}

	/**
	 * @return the projectList
	 */
	public List<ProjectNode> getProjectList() {
		return projectList;
	}

	/**
	 * @param projectList the projectList to set
	 */
	public void setProjectList(List<ProjectNode> projectList) {
		this.projectList = projectList;
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
	 * @return projectColumn
	 */
	public List<MetaColumn> getProjectColumnList() {
		return projectColumnList;
	}
	
	/**
	 * To get column header css name and width when views are loaded using ajax
	 * this width will be set to the repective column using changeStyle() method of tablekit.js
	 * @return
	 */
	public String getProjectListHeaderCss(){
		String cssString = "[";
		for(MetaColumn column : projectColumnList){
			cssString += "{\"cssClassName\":\".div_thd_"+ column.getPropertyName() +"\",\"columnWidth\":\""+column.getColumnWidth()+"\"},";
		}
		cssString += "]";
		return cssString;
	}
	
	/**
	 * To rearrange project data
	 * @param projecttList
	 * @return List of ProjectNode
	 */
	public List<ProjectNode> arrangeProjectSequence(List<ProjectNode> projectList){
		setProjectList(projectList);
		String JSPRootURL = getJSPRootURL();
		for(ProjectNode node : projectList){
			node.setSequensedProject(new LinkedList<ProjectPortfolioRow>());
			for(MetaColumn column : projectColumnList){
				ProjectPortfolioRow project = new ProjectPortfolioRow();
				String columnName = column.getPropertyName();
				String imgUrl;
				String displayValue = "";
				String togglerImg = "<img src=\"" + JSPRootURL + "/u.gif\" id=\"toggler" + node.getProject().getProjectId() + "_"+ node.getId() + "\" onclick=\"toggleTree('" + node.getProject().getProjectId() + "_"+ node.getId() + "');\" />&nbsp;";
					
				if(columnName.equalsIgnoreCase("Name"))
					displayValue += "<td id=\"t" + node.getProject().getProjectId() + "\" onmouseover=\"t(" + node.getProject().getProjectId() + ");\">"
									+	"<div class=\"div_thd_" + column.getPropertyName() + "\" >";
				else
					displayValue += "<td>"
									+	"<div class=\"div_thd_" + column.getPropertyName() + "\" >";
				if(column.getColumnOrder() == 1)
					displayValue += node.getElbowLineHTML();	

				if(!node.isLeaf() && column.getColumnOrder() == 1)
					displayValue += togglerImg;

				if(column.getColumnOrder() == 1 && columnName.equalsIgnoreCase("Name"))
					displayValue += "<img src=\"" + JSPRootURL + "/images/project-icon.gif\" />&nbsp;<span id=\"s" + node.getProject().getProjectId() + "\">";
				else if(columnName.equalsIgnoreCase("Name"))
					displayValue += "<span id=\"s" + node.getProject().getProjectId() + "\">";
				
				if(columnName.equalsIgnoreCase("Name")){
					project.setDisplayValue(displayValue + node.getProject().getProjectName() + "</span></div></td>");
					column.setColumnName("p.project_name");
				} else if(columnName.equalsIgnoreCase("description")){
					project.setDisplayValue(displayValue + node.getProject().getProjectDesc() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ExternalProjectID")){
					project.setDisplayValue(displayValue + node.getProject().getExternalProjectId() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ParentBusinessName")){
					project.setDisplayValue(displayValue + node.getProject().getParentBusinessName() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("SubProjectOf")){
					project.setDisplayValue(displayValue + node.getProject().getSubProjectOf() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("Sponsor")){
					project.setDisplayValue(displayValue + node.getProject().getSponsor() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ProjectManager")){
					project.setDisplayValue(displayValue + node.getProject().getProjectManager() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ProgramManager")){
					project.setDisplayValue(displayValue + node.getProject().getProgramManager() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("Initiative")){
					project.setDisplayValue(displayValue + node.getProject().getInitiative() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("FunctionalArea")){
					project.setDisplayValue(displayValue + node.getProject().getFunctionalArea() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("PriorityCode")){
					project.setDisplayValue(displayValue + node.getProject().getPriorityCode() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("RiskRatingCode")){
					project.setDisplayValue(displayValue + node.getProject().getRiskRatingCode() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ProjectCharter")){
					project.setDisplayValue(displayValue + node.getProject().getProjectCharter() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("StartDate")){
					project.setDisplayValue(displayValue + node.getProject().getStartDate() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("EndDate")){
					project.setDisplayValue(displayValue + node.getProject().getEndDate() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("percent_complete")){
					project.setDisplayValue(displayValue + node.getProject().getPercentComplete() + "</div></td>");
					project.setActualValue(node.getProject().getPercentCompleteValue());
				} else if(columnName.equalsIgnoreCase("OverallStatus")){
					imgUrl = node.getProject().getOverAllImprovementCodeUrl();
					project.setDisplayValue(displayValue + getImageHtmlString(imgUrl) + "</div></td>");
					project.setImageUrl(imgUrl);
					project.setActualValue(node.getProject().getOverAllColorCode());
					column.setColumnName("p.color_code_id");
				} else if(columnName.equalsIgnoreCase("FinancialStatus")){
					imgUrl = node.getProject().getFinancialStatusCodeUrl();
					project.setImageUrl(imgUrl);
					project.setDisplayValue(displayValue + getImageHtmlString(imgUrl) + "</div></td>");
					project.setActualValue(node.getProject().getFinancialStatusColorCode());
					column.setColumnName("p.financial_status_color_code_id");
				} else if(columnName.equalsIgnoreCase("ScheduleStatus")){
					imgUrl = node.getProject().getScheduleStatusCodeUrl();
					project.setImageUrl(imgUrl);
					project.setDisplayValue(displayValue + getImageHtmlString(imgUrl) + "</div></td>");
					project.setActualValue(node.getProject().getScheduleStatusColorCode());
					column.setColumnName("p.schedule_status_color_code_id");
				} else if(columnName.equalsIgnoreCase("ResourceStatus")){
					imgUrl = node.getProject().getResourceStatusCodeUrl();
					project.setImageUrl(imgUrl);
					project.setDisplayValue(displayValue + getImageHtmlString(imgUrl) + "</div></td>");
					project.setActualValue(node.getProject().getResourceStatusColorCode());
					column.setColumnName("p.resource_status_color_code_id");
				} else if(columnName.equalsIgnoreCase("status_code")){
					project.setDisplayValue(displayValue + node.getProject().getProjectStatus() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("DefaultCurrencyCode")){
					project.setDisplayValue(displayValue + node.getProject().getDefaultCurrencyCode() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("TypeOfExpense")){
					project.setDisplayValue(displayValue + node.getProject().getTypeOfExpense() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("BudgetedTotalCost/Money/Value")){
					project.setDisplayValue(displayValue + node.getProject().getBudgetedTotalCostMoneyValue() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("BudgetedTotalCost/Money/Currency")){
					project.setDisplayValue(displayValue + node.getProject().getBudgetedTotalCostMoneyCurrency() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("CurrentEstimatedTotalCost/Money/Value")){
					project.setDisplayValue(displayValue + node.getProject().getCurrentEstimatedTotalCostMoneyValue() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("CurrentEstimatedTotalCost/Money/Currency")){
					project.setDisplayValue(displayValue + node.getProject().getCurrentEstimatedTotalCostMoneyCurrency() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ActualCostToDate/Money/Value")){
					project.setDisplayValue(displayValue + node.getProject().getActualCostToDateMoneyValue() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("ActualCostToDate/Money/Currency")){
					project.setDisplayValue(displayValue + node.getProject().getActualCostToDateMoneyCurrency() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("EstimatedROI/Money/Value")){
					project.setDisplayValue(displayValue + node.getProject().getEstimatedROIMoneyValue() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("CostCenter")){
					project.setDisplayValue(displayValue + node.getProject().getCostCenter() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("TemplateApplied")){
					project.setDisplayValue(displayValue + node.getProject().getTemplateApplied() + "</div></td>");
				} else if(columnName.equalsIgnoreCase("CurrentStatusDescription")){
					project.setDisplayValue(displayValue + node.getProject().getCurrentStatusDescription() + "</div></td>");
				}
				node.getSequensedProject().add(project);
			}
		}
		updateProjectColumnList();
		setProjectAndColumnListInSession();
		return projectList;
	}

	/*	*//**
	 * To construct visibility of assignments nodes
	 * @param assignments
	 *//*
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
	
	 *//**
	 * To check children are visible or not
	 * @param props
	 * @param id
	 * @return
	 *//*
	private boolean childrenAreVisible(PersonProperty props, String id) {
	        boolean childrenAreVisible = isVisible(id);
	        //Just because an object is visible, doesn't mean its children are visible.
	        //It could be the "collapsed summary task".
	        if (childrenAreVisible) {
	            childrenAreVisible = getExpandedProp(props, id);
	        }
	        return childrenAreVisible;
	    }
*/
	/**
	 * To get list of columns to be displayed in default tree view
	 * @param columnList
	 * @return List of MetaColumn
	 */
	private List<MetaColumn> getProjectColumnList(MetaColumnList columnList){
		List<MetaColumn> projectColumnList = new Vector<MetaColumn>();
		List<MetaColumn> allColumns = columnList.getAllColumns();
		MetaColumnList metaColumnList = new MetaColumnList();
		int columnOrder = 1;
			for(MetaColumn column : allColumns){
				String columnName = column.getPropertyName();
				if(columnName.equalsIgnoreCase("Name")){
					column.setColumnWidth(300);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.project_name");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("ParentBusinessName")){
					column.setColumnWidth(120);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("StartDate")){
					column.setColumnWidth(85);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.start_date");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("EndDate")){
					column.setColumnWidth(85);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.end_date");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("status_code")){
					column.setColumnWidth(80);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.status_code_id");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("OverallStatus")){
					column.setColumnWidth(30);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.color_code_id");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("FinancialStatus")){
					column.setColumnWidth(30);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.financial_status_color_code_id");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("ScheduleStatus")){
					column.setColumnWidth(30);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.schedule_status_color_code_id");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("ResourceStatus")){
					column.setColumnWidth(30);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.resource_status_color_code_id");
					projectColumnList.add(column);
				} else if(columnName.equalsIgnoreCase("percent_complete")){
					column.setColumnWidth(125);
					column.setColumnOrder(columnOrder);
					column.setInclude(true);
					columnOrder++;
					column.setColumnName("p.project_name");
					projectColumnList.add(column);
				}
			}
			
		return projectColumnList;
	}

	/**
	 * @return the allMetaColumnList
	 */
	public MetaColumnList getAllColumnList() {
		return allColumnList;
	}

	/**
	 * @param allMetaColumnList the allMetaColumnList to set
	 */
	public void setAllColumnList(MetaColumnList allMetaColumnList) {
		this.allColumnList = allMetaColumnList;
	}

	/**
	 * @param projectColumnList the projectColumnList to set
	 */
	public void setProjectColumnList(List<MetaColumn> projectColumnList) {
		this.projectColumnList = projectColumnList;
	}

	/**
	 * Handling columns dragging,dropping and rearranging columns sequence
	 * this rearrangement will be saved when we create new or modify existing view.
	 * @param draggedColumnName
	 * @param droppedColumnName
	 * @param draggedColumnOrder
	 * @param droppedColumnOrder
	 */
	public void handleColumnDragAndDrop(String draggedColumnName, String droppedColumnName,int draggedColumnOrder, int droppedColumnOrder) {
		MetaColumn draggedColumn = null;

		for(MetaColumn column : getProjectColumnList()){
			if( column.getPropertyName().equalsIgnoreCase(draggedColumnName)){
				draggedColumn = new MetaColumn(column.getPropertyName(),column.isMetaProperty(),column.getDescription(),
												column.getShortDescription(),column.getCategory(),column.isInclude(),
												column.getColumnWidth());
			}
		}

		//shift columns up from dropzone to dragged column if dargged column sequnce is less than dropped zone sequuence.
		if (draggedColumnOrder < droppedColumnOrder) {
			for(int index = draggedColumnOrder ; index < droppedColumnOrder ; index++) {
				MetaColumn col = this.projectColumnList.get(index);
				col.setColumnOrder(index);
				this.projectColumnList.set(index-1, col);
			}
			draggedColumn.setColumnOrder(droppedColumnOrder);
		} else {//shift columns down from dropzone to dragged column, if dargged column sequnce is more than dropped zone sequuence.
			for(int index = draggedColumnOrder ; index > droppedColumnOrder  ; index--) {
				MetaColumn col = this.projectColumnList.get(index-2);
				col.setColumnOrder(index);
				this.projectColumnList.set(index-1, col);
			}
			draggedColumn.setColumnOrder(droppedColumnOrder);
		}
		
		this.projectColumnList.set(droppedColumnOrder-1, draggedColumn);  // set dragged column to its new position in projectColumnList
		updatedtMetaColumn();
	}
	
	/**
	 * Handling columns resizing
	 * this resizing will be saved when we create new or modify existing view.
	 * @param columnID
	 * @param width
	 */
	public void saveColumnWidth(String columnID, int width){
		MetaColumnList reSequencedColumn = new MetaColumnList();
		for(MetaColumn metaColumn : getAllColumnList().getAllColumns()){
			if(columnID.indexOf(metaColumn.getPropertyName()) != -1){
				metaColumn.setColumnWidth(width);
			}
			reSequencedColumn.addMetaColumn(metaColumn);
		}
		setAllColumnList(reSequencedColumn);
	}
	
	// update metacolumnlist according to loaded view and rearranged column order which is to be saved in view
	public void updatedtMetaColumn(){
		MetaColumnList updatedColumn = new MetaColumnList();

        Set financialColumn = new HashSet(Arrays.asList(FINANCIALCOLUMNCATEGORY));
        Set statusColumn = new HashSet(Arrays.asList(STATUSCOLUMNCATEGORY));
        
		for(MetaColumn metaColumn : getAllColumnList().getAllColumns()){ 
			String properytyName = metaColumn.getPropertyName();
			for(MetaColumn projectColumn : getProjectColumnList()){
				if(properytyName.equalsIgnoreCase(projectColumn.getPropertyName())){
					metaColumn.setColumnOrder(projectColumn.getColumnOrder());
					metaColumn.setInclude(projectColumn.isInclude());
				}
				if(("percent_complete").equals(properytyName)){
					metaColumn.setCategory("completion");
				} else if(financialColumn.contains(properytyName)){
					metaColumn.setCategory("financial");
				} else if(statusColumn.contains(properytyName)){
					metaColumn.setCategory("status");
				} else{
					metaColumn.setCategory("general");
				}
			}
			updatedColumn.addMetaColumn(metaColumn);
		}
		setAllColumnList(updatedColumn);
	}
	
	/**
	 *  Set the list of projects and columns in session
	 */
	private void setProjectAndColumnListInSession(){
		setSessionAttribute("projectList", this.projectList);
		setSessionAttribute("projectColumnList", this.getProjectColumnList());
	}

	/**
     * set image url for OFSR
	 * @param url
	 * @return
	 */
	public String getImageHtmlString(String url){
		return StringUtils.isEmpty(url) ? "" : "<img src=" + getJSPRootURL() + "" + url + " width=\"13\"/>";
	}
	
	/**
	 * To set width to column
	 * and to change the column header name with new corrected name
	 * to the views which are created from old project portfolio->manage view page   
	 */
	public void updateProjectColumnList(){
		List<MetaColumn> updatedProjectColumnList = new ArrayList<MetaColumn>();
		for(MetaColumn column : projectColumnList){
			String columnName = column.getPropertyName();
			int columnWidth = column.getColumnWidth();
			if(columnName.equalsIgnoreCase("Name")){
				columnWidth = columnWidth > 0 ? columnWidth : 300;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(PROJECT_NAME_COLUMN);
			} else if(columnName.equalsIgnoreCase("description")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.project_desc");
			} else if(columnName.equalsIgnoreCase("ExternalProjectID")){
				columnWidth = columnWidth > 0 ? columnWidth : 150;
				column.setColumnName("p.project_name");
				column.setDescription(EXTERNAL_PROJECT_ID_COLUMN);
			} else if(columnName.equalsIgnoreCase("ParentBusinessName")){
				columnWidth = columnWidth > 0 ? columnWidth : 150;
				column.setColumnName("upper(b.business_name)");
			} else if(columnName.equalsIgnoreCase("SubProjectOf")){
				columnWidth = columnWidth > 0 ? columnWidth : 150;
				column.setColumnName("");
			} else if(columnName.equalsIgnoreCase("Sponsor")){
				columnWidth = columnWidth > 0 ? columnWidth : 80;
				column.setColumnName("p.sponsor_desc");
				column.setDescription(SPONSOR_COLUMN);
			} else if(columnName.equalsIgnoreCase("ProjectManager")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(PROJECT_MANAGER_COLUMN);
			} else if(columnName.equalsIgnoreCase("ProgramManager")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(PROGRAM_MANAGER_COLUMN);
			} else if(columnName.equalsIgnoreCase("Initiative")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(INITIATIVE_COLUMN);
			} else if(columnName.equalsIgnoreCase("FunctionalArea")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(FUNCTIONALAREA_COLUMN);
			} else if(columnName.equalsIgnoreCase("PriorityCode")){
				columnWidth = columnWidth > 0 ? columnWidth : 80;
				column.setColumnName("p.priority_code_id");
				column.setDescription(PRIORITY_CODE_COLUMN);
			} else if(columnName.equalsIgnoreCase("RiskRatingCode")){
				columnWidth = columnWidth > 0 ? columnWidth : 80;
				column.setColumnName("p.risk_rating_code_id");
				column.setDescription(RISKRATING_CODE_COLUMN);
			} else if(columnName.equalsIgnoreCase("ProjectCharter")){
				columnWidth = columnWidth > 0 ? columnWidth : 80;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(PROJECT_CHARTER_COLUMN);
			} else if(columnName.equalsIgnoreCase("StartDate")){
				columnWidth = columnWidth > 0 ? columnWidth : 80;
				column.setColumnName("p.start_date");
			} else if(columnName.equalsIgnoreCase("EndDate")){
				columnWidth = columnWidth > 0 ? columnWidth : 120;
				column.setColumnName("p.end_date");
				column.setDescription(END_DATE_COLUMN);
			} else if(columnName.equalsIgnoreCase("percent_complete")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.percent_complete");
				column.setDescription(WORKCOMPLETE_COLUMN);
			} else if(columnName.equalsIgnoreCase("OverallStatus")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.color_code_id");
				column.setDescription(OVERALL_STATUS_COLUMN);
			} else if(columnName.equalsIgnoreCase("FinancialStatus")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.financial_status_color_code_id");
				column.setDescription(FINANCIAl_STATUS_COLUMN);
			} else if(columnName.equalsIgnoreCase("ScheduleStatus")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.schedule_status_color_code_id");
				column.setDescription(SCHEDULE_STATUS_COLUMN);
			} else if(columnName.equalsIgnoreCase("ResourceStatus")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.resource_status_color_code_id");
				column.setDescription(RESOURCE_STATUS_COLUMN);
			} else if(columnName.equalsIgnoreCase("status_code")){
				columnWidth = columnWidth > 0 ? columnWidth : 100;
				column.setColumnName("p.status_code_id");
			} else if(columnName.equalsIgnoreCase("DefaultCurrencyCode")){
				columnWidth = columnWidth > 0 ? columnWidth : 150;
				column.setColumnName("p.default_currency_code");
			} else if(columnName.equalsIgnoreCase("TypeOfExpense")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("upper(p.project_name)");
				column.setDescription(TYPE_OF_EXPENSE_COLUMN);
			} else if(columnName.equalsIgnoreCase("BudgetedTotalCost/Money/Value")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("p.budgeted_total_cost_value");
				column.setDescription(BUDGETEDTOTALCOST_MONEY_VALUE_COLUMN);
			} else if(columnName.equalsIgnoreCase("BudgetedTotalCost/Money/Currency")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("p.budgeted_total_cost_cc");
				column.setDescription(BUDGETEDTOTALCOST_MONEY_CURRENCY_COLUMN);
			} else if(columnName.equalsIgnoreCase("CurrentEstimatedTotalCost/Money/Value")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("p.current_est_total_cost_value");
				column.setDescription(CURRENTESTIMATEDTOTALCOST_MONEY_VALUE_COLUMN);
			} else if(columnName.equalsIgnoreCase("CurrentEstimatedTotalCost/Money/Currency")){
				columnWidth = columnWidth > 0 ? columnWidth : 350;
				column.setColumnName("p.current_est_total_cost_cc");
				column.setDescription(CURRENTESTIMATEDTOTALCOST_MONEY_CURRENCY_COLUMN);
			} else if(columnName.equalsIgnoreCase("ActualCostToDate/Money/Value")){
				columnWidth = columnWidth > 0 ? columnWidth : 350;
				column.setColumnName("p.actual_to_date_cost_value");
				column.setDescription(ACTUALCOSTTODATE_MONEY_VALUE_COLUMN);
			} else if(columnName.equalsIgnoreCase("ActualCostToDate/Money/Currency")){
				columnWidth = columnWidth > 0 ? columnWidth : 350;
				column.setColumnName("p.actual_to_date_cost_cc");
				column.setDescription(ACTUALCOSTTODATE_MONEY_CURRENCY_COLUMN);
			} else if(columnName.equalsIgnoreCase("EstimatedROI/Money/Value")){
				columnWidth = columnWidth > 0 ? columnWidth : 300;
				column.setColumnName("p.estimated_roi_cost_value");
				column.setDescription(ESTIMATEDROI_MONEY_VALUE_COLUMN);
			} else if(columnName.equalsIgnoreCase("CostCenter")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("p.cost_center");
				column.setDescription(COST_CENTER_COLUMN);
			} else if(columnName.equalsIgnoreCase("TemplateApplied")){
				columnWidth = columnWidth > 0 ? columnWidth : 200;
				column.setColumnName("upper(p.project_name)");
			} else if(columnName.equalsIgnoreCase("CurrentStatusDescription")){
				columnWidth = columnWidth > 0 ? columnWidth : 300;
				column.setColumnName("p.current_status_description");
				column.setDescription(CURRENT_STATUS_DESCRIPTION_COLUMN);
			}
			column.setColumnWidth(columnWidth);
			updatedProjectColumnList.add(column);
		}
		projectColumnList.clear();
		projectColumnList.addAll(updatedProjectColumnList);
	}

	/**
	 * @return the sorterList
	 */
	public List<FinderSorter> getSorterList() {
		return sorterList;
	}

	/**
	 * @param sorterList the sorterList to set
	 */
	public void setSorterList(List<FinderSorter> sorterList) {
		this.sorterList = sorterList;
	}
}
