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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.TextStreamResponse;

public class ViewVsAssignments {

	private static Logger log;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private String resourceColumnLabel;

	@Persist
	private String projetColumnLabel;

	@Persist
	private String projectIdColumnLabel;

	@Persist
	private String extAlertTitle;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String gridTitle;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String allOption;
	
	@Persist
	private String dataNotFoundMessage;

	@Persist
	private String goOneMonthBackwardImageTooltip;
	
	@Persist
	private String goThreeMonthsBackwardImageTooltip;
	
	@Persist
	private String goOneMonthForwardImageTooltip;
	
	@Persist
	private String goThreeMonthsForwardImageTooltip;

	@Persist
	private BusinessSpace business;

	@Inject
	private PropertyAccess access;

	private BusinessSpaceFinder bFinder;

	private List<BusinessSpace> businessesList;

	private BusinessSpace firstBusinessSpace;

	private List<String> monthList;

	private List<String> yearList;

	@Persist
	private String month;

	@Persist
	private String year;

	@Persist
	private Integer monthIndex;

	@Persist
	private String assignmentVsAllocationData;

	@Persist
	private String message;
	
	@Persist
	private String gridTotalRowLabel;
	
	@Persist
	private String versionNumber;
	
	@Persist
	private boolean preReleaseEnabled;

	//  Method to initialize page token
    private void initialzePageToken() {
		try {
			log = Logger.getLogger(ViewVsAssignments.class);
			
			resourceColumnLabel = PropertyProvider
					.get("prm.resource.allocationvsassignment.column.resource.label");
			projetColumnLabel = PropertyProvider
					.get("prm.resource.allocationvsassignment.column.project.label");
			projectIdColumnLabel = PropertyProvider
					.get("prm.resource.allocationvsassignment.column.projectid.label");
			gridTitle = PropertyProvider
					.get("prm.resource.allocationvsassignment.gridtitle");
			viewButtonCaption = PropertyProvider
					.get("prm.resource.allocationvsassignment.view.button.caption");
			extAlertTitle = PropertyProvider
					.get("prm.resource.global.exterroralert.title");
			serverRequestFailedMessage = PropertyProvider
					.get("prm.resource.allocationvsassignment.serverrequestfailed.message");
			allOption = PropertyProvider
					.get("prm.resource.allocationvsassignment.businessdropdownlist.alloptionvalue");
			dataNotFoundMessage = PropertyProvider
					.get("prm.resource.allocationvsassignment.datanotfoundtoviewinthisperiod.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocationvsassignment.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocationvsassignment.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocationvsassignment.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocationvsassignment.gothreemonthsforwardimage.tooltip");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
            preReleaseEnabled = PropertyProvider.getBoolean("prm.resource.prereleasefunctionality.label.isenabled");
            monthList = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    		Calendar cal = Calendar.getInstance();
    		yearList = new ArrayList<String>();
    		for (int year = 1995; year <= cal.get(Calendar.YEAR) + 10 ; year++) {
    			yearList.add(Integer.toString(year));
    		}
    		businessesList = new ArrayList<BusinessSpace>();
    		bFinder = new BusinessSpaceFinder();
		} catch (Exception ex) {
			log
					.error("Error occured while getting AllocationVsAssignments page property values.");
		}
    }
    
	void onActivate() {
        initialzePageToken();
        
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName(allOption);
		
		DateFormat df = new DateFormat(SessionManager.getUser());
		if(getMonth()==null || getYear()==null){
			month = df.formatDate(new Date(), "MMM yyyy").split(" ")[0];
			year = df.formatDate(new Date(), "MMM yyyy").split(" ")[1];
		}
		// Getting database values for business list
		if (businessesList != null) {
			businessesList.clear();
		}
		businessesList.add(firstBusinessSpace);
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			businessesList.addAll(bFinder.findByUser(
					net.project.security.SessionManager.getUser(), "A"));
		} catch (PersistenceException e) {
			log
					.error("Error occured while generating list values for Businesses model");
		}
		if (businessesList.size() == 1) {
			businessesList.clear();
			firstBusinessSpace.setID("0");
			firstBusinessSpace.setName("No Businesses Found");
			businessesList.add(firstBusinessSpace);
		}		
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(
				businessesList, BusinessSpace.class, "name", "id", access);
		
		if(getBusiness()!=null && !getBusiness().getID().equals("0")){
			getAssignmentVsAllocationGridData(Integer.parseInt(getBusiness().getID()), getMonth(), getYear());
		} else {
			getAssignmentVsAllocationGridData(null, getMonth(), getYear());
		}
	}

	/**
	 * Method for getting dropdown list values for Businessspace from database
	 * 
	 * @return GenericSelectModel<BusinessSpace>
	 */
	public GenericSelectModel<BusinessSpace> getBusinessesModel() {
		return businessSpaceBeans;
	}

	/**
	 * @return month dropdown list
	 */
	public List getMonthsModel() {
		return monthList;
	}

	/**
	 * @return year dropdown list
	 */
	public List getYearsModel() {
		return yearList;
	}

	/**
	 * Getting assignments vs allocation data by businessid and start - end dates
	 * 
	 * @param businessId
	 * @param startMonth
	 * @param startYear
	 */
	void getAssignmentVsAllocationGridData(Integer businessId, String startMonth, String startYear) {
		String startDateString = "01/" + (monthList.indexOf(startMonth) + 1) + "/" + startYear;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		monthIndex = monthList.indexOf(getMonth());
		
		Date startDate = null;
		Calendar endDate = Calendar.getInstance();

		try {
			startDate = dateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Invalid date exception in Reserved vs Assigned page " + e.getMessage());
		}

		endDate.setTime(startDate);
		endDate.add(Calendar.MONTH, 12);

		List<ReportUser> assignmentVsAllocationDetails;
		List<ReportUserProjects> projectList;
		List<ReportMonth> monthList;

		IPnAssignmentService assignmentService = ServiceFactory.getInstance()
				.getPnAssignmentService();
		assignmentVsAllocationDetails = assignmentService.getAssignmentVsAllocation(Integer.parseInt(SessionManager
				.getUser().getID()), businessId, startDate, endDate.getTime(), SessionManager.getUser()
				.getDateFormatter());
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		assignmentVsAllocationData = "";
		assignmentVsAllocationData = "[  ";
		if (assignmentVsAllocationDetails.size() > 0) {
			for (ReportUser reportUser : assignmentVsAllocationDetails) {
				projectList = reportUser.getProjektList();
				for (ReportUserProjects reportProject : projectList) {
					assignmentVsAllocationData += " [" + reportUser.getUserId() + ", ";
					assignmentVsAllocationData += " '" + reportUser.getFirstName().replaceAll("'", "&acute;") + " "
							+ reportUser.getLastName().replaceAll("'", "&acute;") + "', ";
					assignmentVsAllocationData += reportProject.getProjectId() + ", ";
					assignmentVsAllocationData += " '" + reportProject.getProjectName().replaceAll("'", "&acute;")
							+ "'";
					monthList = reportProject.getMonthList();
					for (ReportMonth reportMonth : monthList) {
						assignmentVsAllocationData += " , ";
						try {
							if (reportMonth.getAllocation() != null && reportMonth.getAllocation().floatValue() > 0) {
								assignmentVsAllocationData += "" + ((reportMonth.getTotalWork() / reportMonth.getAllocation().floatValue()) * 100.0);
							} else {
								assignmentVsAllocationData += "0";
							}
						} catch (Exception e) {
							log
									.error("Error occured calculating utilized allocation "
											+ e.getMessage());
						}
					}
					assignmentVsAllocationData += " ], ";
				}
			}
			assignmentVsAllocationData = assignmentVsAllocationData.substring(0,
					assignmentVsAllocationData.length() - 2);
		}
		assignmentVsAllocationData += " ]";
		if (assignmentVsAllocationData.equals("[   ]")) {
			setMessage(dataNotFoundMessage);
		} else {
			setMessage(null);
		}
	}

	/**
	 * Method used to fetch data from database by criteria to show in a grid
	 * 
	 * @param param this contains "bussinessid & startMonth & startYear" string
	 * @return Grid data array manipulated String
	 */
	public TextStreamResponse onActivate(String param) {
		initialzePageToken();
		String[] params = param.split("&");		
		if (params.length > 1) {
			setMonth(params[1]);
			setYear(params[2]);
			if (!(params[0].equals("0")) && StringUtils.isNotEmpty(params[0])) {
				setBusiness(new BusinessSpace(params[0]));
				getAssignmentVsAllocationGridData(Integer.parseInt(params[0]), params[1], params[2]);
			} else {
				setBusiness(new BusinessSpace("0"));
				getAssignmentVsAllocationGridData(null, params[1], params[2]);
			}
			return new TextStreamResponse("text/plain", assignmentVsAllocationData);
		}
		return null;
	}

	@CleanupRender
	void cleanValues() {
		setMessage(null);
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public BusinessSpace getBusiness() {
		return business;
	}

	public void setBusiness(BusinessSpace business) {
		this.business = business;
	}

	public String getGridTitle() {
		return gridTitle;
	}
	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	/**
	 * @return the projectIdColumnLabel
	 */
	public String getProjectIdColumnLabel() {
		return projectIdColumnLabel;
	}

	public String getViewButtonCaption() {
		return viewButtonCaption;
	}

	public String getProjetColumnLabel() {
		return projetColumnLabel;
	}

	/**
	 * @return the monthIndex
	 */
	public Integer getMonthIndex() {
		return monthIndex;
	}

	/**
	 * @param monthIndex
	 *            the monthIndex to set
	 */
	public void setMonthIndex(Integer monthIndex) {
		this.monthIndex = monthIndex;
	}

	/**
	 * @return the assignmentVsAllocationData
	 */
	public String getAssignmentVsAllocationData() {
		return assignmentVsAllocationData;
	}

	/**
	 * @param assignmentVsAllocationData
	 *            the assignmentVsAllocationData to set
	 */
	public void setAssignmentVsAllocationData(String assignmentVsAllocationData) {
		this.assignmentVsAllocationData = assignmentVsAllocationData;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the extAlertTitle
	 */
	public String getExtAlertTitle() {
		return extAlertTitle;
	}

	/**
	 * @return the serverRequestFailedMessage
	 */
	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
	}

	/**
	 * @return the dataNotFoundMessage
	 */
	public String getDataNotFoundMessage() {
		return dataNotFoundMessage;
	}

	/**
	 * @return the goOneMonthBackwardImageTooltip
	 */
	public String getGoOneMonthBackwardImageTooltip() {
		return goOneMonthBackwardImageTooltip;
	}

	/**
	 * @return the goOneMonthForwardImageTooltip
	 */
	public String getGoOneMonthForwardImageTooltip() {
		return goOneMonthForwardImageTooltip;
	}

	/**
	 * @return the goThreeMonthsBackwardImageTooltip
	 */
	public String getGoThreeMonthsBackwardImageTooltip() {
		return goThreeMonthsBackwardImageTooltip;
	}

	/**
	 * @return the goThreeMonthsForwardImageTooltip
	 */
	public String getGoThreeMonthsForwardImageTooltip() {
		return goThreeMonthsForwardImageTooltip;
	}

	/**
	 * @return the gridTotalRowLabel
	 */
	public String getGridTotalRowLabel() {
		return gridTotalRowLabel;
	}
	
	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the preReleaseEnabled
	 */
	public boolean getPreReleaseEnabled() {
		return preReleaseEnabled;
	}
}
