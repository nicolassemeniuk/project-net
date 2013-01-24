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
/**
 *
 */
package net.project.view.pages.resource.management;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnResourceList;
import net.project.hibernate.model.PnResourceListHasPersons;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnResourceListHasPersonsService;
import net.project.hibernate.service.IPnResourceListService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * @author
 */
public class ViewDetails_V8_5 {

	private static Logger log;
	
	private DateFormat dateFormat;
	
	@ApplicationState
	private String jSPRootURL;

	
	@Persist
	private String pageTitle;

	@Persist
	private String resourceListLabel;

	@Persist
	private String fromTasksAssignedFromTextLabel;

	@Persist
	private String toTextLabel;

	@Persist
	private String viewButtonLabel;

	@Persist
	private String manageListSelectResourcesButtonCaption;
	
	@Persist
	private String gridTitle;

	@Persist
	private String taskAssignmentColumnLabel;

	@Persist
	private String workspaceColumnLabel;

	@Persist
	private String plannedStartColumnLabel;

	@Persist
	private String plannedFinishColumnLabel;

	@Persist
	private String actualStartColumnLabel;

	@Persist
	private String actualFinishColumnLabel;

	@Persist
	private String totalWorkColumnLabel;

	@Persist
	private String workCompleteColumnLabel;

	@Persist
	private String workRemainingColumnLabel;

	@Persist
	private String workPercentCompleteColumnLabel;

	@Persist
	private String gridData;

	@Persist
	private String message;
	
	@Persist
	private String calendarDateFormat;

	@Persist
	private String resourceListSelectionReqMsg;

	@Persist
	private String startDateSelectionReqMsg;

	@Persist
	private String endDateSelectionReqMsg;

	@Persist
	private PnResourceList resourceList;

	@Inject
    private PropertyAccess access;

    private GenericSelectModel<PnResourceList> pnResourceListBeans;

	@Persist
	private String startDate = null;

	@Persist
	private String endDate = null;

	private String dateCriteria = "";

	@InjectPage
	private ViewDetails_V8_5 view;

	@InjectPage
	private SelectResource_V8_5 selectResource;

	private PnResourceList firstPnResourceList;

	private List<PnResourceList> dev;
	private IPnResourceListService pnResourceListService;

	@Component
	private Form displayForm;

	@Component
	private Form viewForm;

	@Inject
	private ComponentResources componentResources;

	/**
	 * default constructor
	 */
	public ViewDetails_V8_5() {
		try {
			log = Logger.getLogger(ViewDetails_V8_5.class);
			pageTitle = PropertyProvider.get("prm.resource.viewdetails.pagetitle");
			resourceListLabel = PropertyProvider.get("prm.resource.viewdetails.resourcelist.label");
			fromTasksAssignedFromTextLabel = PropertyProvider
					.get("prm.resource.viewdetails.fromtasksassignedfrom.label");
			toTextLabel = PropertyProvider.get("prm.resource.viewdetails.to.label");
			viewButtonLabel = PropertyProvider.get("prm.resource.viewdetails.view.button.label");
			manageListSelectResourcesButtonCaption = PropertyProvider.get("prm.resource.viewdetails.managelistselectresources.button.label");
			gridTitle = PropertyProvider.get("prm.resource.viewdetails.gridtitle");
			taskAssignmentColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.taskassignment.label");
			workspaceColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.workspace.label");
			plannedStartColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.plannedstart.label");
			plannedFinishColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.plannedfinish.label");
			actualStartColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.actualstart.label");
			actualFinishColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.actualfinish.label");
			totalWorkColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.totalwork.label");
			workCompleteColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.workcomplete.label");
			workRemainingColumnLabel = PropertyProvider.get("prm.resource.viewdetails.column.workremaining.label");
			workPercentCompleteColumnLabel = PropertyProvider
					.get("prm.resource.viewdetails.column.workpercentcomplete.label");

			/** Validation messages retrieving from database */
			resourceListSelectionReqMsg = PropertyProvider.get("prm.resource.viewdetails.resourcelistselectionrequired.message");
			startDateSelectionReqMsg = PropertyProvider.get("prm.resource.viewdetails.startdateselectionrequired.message");
			endDateSelectionReqMsg = PropertyProvider.get("prm.resource.viewdetails.enddateselectionrequired.message");

			dateFormat = SessionManager.getUser().getDateFormatter();
			calendarDateFormat =  dateFormat.getDateFormatExample().toLowerCase();
			
			// sometimes the user in session in null!, need to check the impact
			// of pnet security model with tapestry sessions?
			//SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			//Date startDate = df.parse(getStartDate());
			//Date endDate = df.parse(getEndDate());
			getAssignmentDetails(null, null, null); // need to decide the default date range to filter assignment view
		} catch (Exception ex) {
			log.error("Error occured while getting ViewDetails page property values.");
			ex.printStackTrace();
		}
		firstPnResourceList = new PnResourceList(0,"Select");
		pnResourceListService = ServiceFactory.getInstance().getPnResourceListService();
		dev = new ArrayList<PnResourceList>();
		setValues();
	}

	@SetupRender
	void setValues(){
		// generating list of PnResourceList objects		
		if(dev != null){
			dev.clear();
		}
		dev.add(firstPnResourceList);
		dev.addAll(pnResourceListService.getResourceList());
		pnResourceListBeans = new GenericSelectModel<PnResourceList>(dev,PnResourceList.class,"name","id",access);
	}

	public GenericSelectModel<PnResourceList> getResourceListModel() {
		return pnResourceListBeans;
	}

	/**
	 * @param whereClause
	 */
	public void getAssignmentDetails(Integer[] personsIds, Date startDate, Date endDate) {
		List<PnAssignment> assignmentDetails = new ArrayList<PnAssignment>();
		PnAssignment assignmentDetailsData;

		IPnAssignmentService pnAssignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		assignmentDetails = pnAssignmentService.getAssigmentsList(personsIds, startDate, endDate);
		//SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		gridData = "";
		gridData = "[ ";
		if (assignmentDetails.size() > 0) {
			for (int i = 0; i < assignmentDetails.size(); i++) {
				assignmentDetailsData = assignmentDetails.get(i);
				gridData += " [ " + assignmentDetailsData.getPnTask().getTaskId() + ", " 
						+ "'" + assignmentDetailsData.getPnTask().getTaskName() + "', " 
						+ "'" + assignmentDetailsData.getPnProjectSpace().getProjectName() + "', " 
						+ "'" + assignmentDetailsData.getPnPerson().getDisplayName() + "', " 
						+ "'" + dateFormat.formatDate(assignmentDetailsData.getPnTask().getDateStart()) + "', " 
						+ "'" + dateFormat.formatDate(assignmentDetailsData.getPnTask().getDateFinish()) + "', ";

				if (assignmentDetailsData.getActualStart() != null)
					gridData += "'" + dateFormat.formatDate(assignmentDetailsData.getActualStart()) + "', ";
				else
					gridData += ", ";

				if (assignmentDetailsData.getActualFinish() != null)
					gridData += "'" + dateFormat.formatDate(assignmentDetailsData.getActualFinish()) + "', ";
				else
					gridData += ", ";

				gridData += "" + assignmentDetailsData.getWork() + ", ";

				if (assignmentDetailsData.getWorkComplete() != null)
					gridData += assignmentDetailsData.getWorkComplete() + ", ";
				else
					gridData += " 0, ";
				
				gridData += "0,'" + assignmentDetailsData.getPnTask().getWorkPercentComplete() + "'" + " ],";
				setMessage("");
			}
		} else {
			setMessage(PropertyProvider.get("prm.resource.viewdetails.assignmentsearch.message"));
		}
		gridData = gridData.substring(0, gridData.length() - 1) + " ]";
	}

	/**
	 * Method to get resource list values from database
	 * when a list saved or deleted
	 */
	public Object onSubmitFromDisplayForm(){
		selectResource.setResourceListObject(null);
		selectResource.setPersonIdsInList(null);
		return view;
	}
	
	/**
	 * Method for action called on view button
	 *
	 * @return Object of class to forward
	 */
	public Object onSubmitFromViewForm() {		
		List<PnResourceListHasPersons> pnResourceListHasPersonsList = new ArrayList<PnResourceListHasPersons>();
		PnResourceListHasPersons pnResourceListHasPersons;
		Integer[] personsIds;
		try {
			Date startDate = null;
			Date endDate = null;
			if (getStartDate() != null || !getStartDate().equals("")) {
				startDate = dateFormat.parseDateString(getStartDate());
				endDate = dateFormat.parseDateString(getEndDate());
			}
			if (getResourceList() != null){
				IPnResourceListHasPersonsService pnResourceListHasPersonsService = ServiceFactory.getInstance().getPnResourceListHasPersonsService();
				pnResourceListHasPersonsList = pnResourceListHasPersonsService.getResourcesByListId(getResourceList().getId());
				personsIds = new Integer[pnResourceListHasPersonsList.size()];
				if(pnResourceListHasPersonsList.size() > 0)	{
					for(int index = 0; index < pnResourceListHasPersonsList.size(); index++) {
						pnResourceListHasPersons = pnResourceListHasPersonsList.get(index);
						personsIds[index] = (pnResourceListHasPersons.getComp_id().getPersonId()) ;
					}
				}
				getAssignmentDetails(personsIds, startDate, endDate);
				view.setResourceList(getResourceList());
			} else {
				getAssignmentDetails(null, startDate, endDate);
			}
			view.setGridData(gridData);			
			if (gridData.equalsIgnoreCase("[  ]")){
				view.setMessage(PropertyProvider.get("prm.resource.viewdetails.assignmentsearch.message"));
			}
		} catch (Exception e) {
			log.error("Error occured on performing view action ..");
			e.printStackTrace();
		}
		return view;
	}

	/**
	 * @return Returns the endDate.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            The endDate to set.
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Returns the startDate.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            The startDate to set.
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the gridData.
	 */
	public String getGridData() {
		return gridData;
	}

	/**
	 * @param gridData
	 *            The gridData to set.
	 */
	public void setGridData(String gridData) {
		this.gridData = gridData;
	}

	/**
	 * @return Returns the actualFinishColumnLabel.
	 */
	public String getActualFinishColumnLabel() {
		return actualFinishColumnLabel;
	}

	/**
	 * @param actualFinishColumnLabel
	 *            The actualFinishColumnLabel to set.
	 */
	public void setActualFinishColumnLabel(String actualFinishColumnLabel) {
		this.actualFinishColumnLabel = actualFinishColumnLabel;
	}

	/**
	 * @return Returns the actualStartColumnLabel.
	 */
	public String getActualStartColumnLabel() {
		return actualStartColumnLabel;
	}

	/**
	 * @param actualStartColumnLabel
	 *            The actualStartColumnLabel to set.
	 */
	public void setActualStartColumnLabel(String actualStartColumnLabel) {
		this.actualStartColumnLabel = actualStartColumnLabel;
	}

	/**
	 * @return Returns the fromTasksAssignedFromTextLabel.
	 */
	public String getFromTasksAssignedFromTextLabel() {
		return fromTasksAssignedFromTextLabel;
	}

	/**
	 * @param fromTasksAssignedFromTextLabel
	 *            The fromTasksAssignedFromTextLabel to set.
	 */
	public void setFromTasksAssignedFromTextLabel(String fromTasksAssignedFromTextLabel) {
		this.fromTasksAssignedFromTextLabel = fromTasksAssignedFromTextLabel;
	}

	/**
	 * @return Returns the pageTitle.
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	/**
	 * @param pageTitle
	 *            The pageTitle to set.
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * @return Returns the plannedFinishColumnLabel.
	 */
	public String getPlannedFinishColumnLabel() {
		return plannedFinishColumnLabel;
	}

	/**
	 * @param plannedFinishColumnLabel
	 *            The plannedFinishColumnLabel to set.
	 */
	public void setPlannedFinishColumnLabel(String plannedFinishColumnLabel) {
		this.plannedFinishColumnLabel = plannedFinishColumnLabel;
	}

	/**
	 * @return Returns the plannedStartColumnLabel.
	 */
	public String getPlannedStartColumnLabel() {
		return plannedStartColumnLabel;
	}

	/**
	 * @param plannedStartColumnLabel
	 *            The plannedStartColumnLabel to set.
	 */
	public void setPlannedStartColumnLabel(String plannedStartColumnLabel) {
		this.plannedStartColumnLabel = plannedStartColumnLabel;
	}

	/**
	 * @return Returns the resourceListLabel.
	 */
	public String getResourceListLabel() {
		return resourceListLabel;
	}

	/**
	 * @param resourceListLabel
	 *            The resourceListLabel to set.
	 */
	public void setResourceListLabel(String resourceListLabel) {
		this.resourceListLabel = resourceListLabel;
	}

	/**
	 * @return Returns the taskAssignmentColumnLabel.
	 */
	public String getTaskAssignmentColumnLabel() {
		return taskAssignmentColumnLabel;
	}

	/**
	 * @param taskAssignmentColumnLabel
	 *            The taskAssignmentColumnLabel to set.
	 */
	public void setTaskAssignmentColumnLabel(String taskAssignmentColumnLabel) {
		this.taskAssignmentColumnLabel = taskAssignmentColumnLabel;
	}

	/**
	 * @return Returns the totalWorkColumnLabel.
	 */
	public String getTotalWorkColumnLabel() {
		return totalWorkColumnLabel;
	}

	/**
	 * @param totalWorkColumnLabel
	 *            The totalWorkColumnLabel to set.
	 */
	public void setTotalWorkColumnLabel(String totalWorkColumnLabel) {
		this.totalWorkColumnLabel = totalWorkColumnLabel;
	}

	/**
	 * @return Returns the toTextLabel.
	 */
	public String getToTextLabel() {
		return toTextLabel;
	}

	/**
	 * @param toTextLabel
	 *            The toTextLabel to set.
	 */
	public void setToTextLabel(String toTextLabel) {
		this.toTextLabel = toTextLabel;
	}

	/**
	 * @return Returns the viewButtonLabel.
	 */
	public String getViewButtonLabel() {
		return viewButtonLabel;
	}

	/**
	 * @param viewButtonLabel
	 *            The viewButtonLabel to set.
	 */
	public void setViewButtonLabel(String viewButtonLabel) {
		this.viewButtonLabel = viewButtonLabel;
	}

	/**
	 * @return Returns the workCompleteColumnLabel.
	 */
	public String getWorkCompleteColumnLabel() {
		return workCompleteColumnLabel;
	}

	/**
	 * @param workCompleteColumnLabel
	 *            The workCompleteColumnLabel to set.
	 */
	public void setWorkCompleteColumnLabel(String workCompleteColumnLabel) {
		this.workCompleteColumnLabel = workCompleteColumnLabel;
	}

	/**
	 * @return Returns the workPercentCompleteColumnLabel.
	 */
	public String getWorkPercentCompleteColumnLabel() {
		return workPercentCompleteColumnLabel;
	}

	/**
	 * @param workPercentCompleteColumnLabel
	 *            The workPercentCompleteColumnLabel to set.
	 */
	public void setWorkPercentCompleteColumnLabel(String workPercentCompleteColumnLabel) {
		this.workPercentCompleteColumnLabel = workPercentCompleteColumnLabel;
	}

	/**
	 * @return Returns the workRemainingColumnLabel.
	 */
	public String getWorkRemainingColumnLabel() {
		return workRemainingColumnLabel;
	}

	/**
	 * @param workRemainingColumnLabel
	 *            The workRemainingColumnLabel to set.
	 */
	public void setWorkRemainingColumnLabel(String workRemainingColumnLabel) {
		this.workRemainingColumnLabel = workRemainingColumnLabel;
	}

	/**
	 * @return Returns the workspaceColumnLabel.
	 */
	public String getWorkspaceColumnLabel() {
		return workspaceColumnLabel;
	}

	/**
	 * @param workspaceColumnLabel
	 *            The workspaceColumnLabel to set.
	 */
	public void setWorkspaceColumnLabel(String workspaceColumnLabel) {
		this.workspaceColumnLabel = workspaceColumnLabel;
	}

	/**
	 * @return Returns the dateCriteria.
	 */
	public String getDateCriteria() {
		return dateCriteria;
	}

	/**
	 * @param dateCriteria
	 *            The dateCriteria to set.
	 */
	public void setDateCriteria(String dateCriteria) {
		this.dateCriteria = dateCriteria;
	}

	/**
	 * @return Returns the resourceList.
	 */
	public PnResourceList getResourceList() {
		return resourceList;
	}

	/**
	 * @param resourceList The resourceList to set.
	 */
	public void setResourceList(PnResourceList resourceList) {
		this.resourceList = resourceList;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return Returns the endDateSelectionReqMsg.
	 */
	public String getEndDateSelectionReqMsg() {
		return endDateSelectionReqMsg;
	}

	/**
	 * @param endDateSelectionReqMsg The endDateSelectionReqMsg to set.
	 */
	public void setEndDateSelectionReqMsg(String endDateSelectionReqMsg) {
		this.endDateSelectionReqMsg = endDateSelectionReqMsg;
	}

	/**
	 * @return Returns the resourceListSelectionReqMsg.
	 */
	public String getResourceListSelectionReqMsg() {
		return resourceListSelectionReqMsg;
	}

	/**
	 * @param resourceListSelectionReqMsg The resourceListSelectionReqMsg to set.
	 */
	public void setResourceListSelectionReqMsg(String resourceListSelectionReqMsg) {
		this.resourceListSelectionReqMsg = resourceListSelectionReqMsg;
	}

	/**
	 * @return Returns the startDateSelectionReqMsg.
	 */
	public String getStartDateSelectionReqMsg() {
		return startDateSelectionReqMsg;
	}

	/**
	 * @param startDateSelectionReqMsg The startDateSelectionReqMsg to set.
	 */
	public void setStartDateSelectionReqMsg(String startDateSelectionReqMsg) {
		this.startDateSelectionReqMsg = startDateSelectionReqMsg;
	}

	/**
	 * @return Returns the manageListSelectResourcesButtonCaption.
	 */
	public String getManageListSelectResourcesButtonCaption() {
		return manageListSelectResourcesButtonCaption;
	}

	/**
	 * @param manageListSelectResourcesButtonCaption The manageListSelectResourcesButtonCaption to set.
	 */
	public void setManageListSelectResourcesButtonCaption(
			String manageListSelectResourcesButtonCaption) {
		this.manageListSelectResourcesButtonCaption = manageListSelectResourcesButtonCaption;
	}

	/**
	 * @return Returns the calendarDateFormat.
	 */
	public String getCalendarDateFormat() {
		return calendarDateFormat;
	}

	/**
	 * @param calendarDateFormat The calendarDateFormat to set.
	 */
	public void setCalendarDateFormat(String calendarDateFormat) {
		this.calendarDateFormat = calendarDateFormat;
	}

	/**
	 * @return the gridTitle
	 */
	public String getGridTitle() {
		return gridTitle;
	}

	/**
	 * @param gridTitle the gridTitle to set
	 */
	public void setGridTitle(String gridTitle) {
		this.gridTitle = gridTitle;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

}
