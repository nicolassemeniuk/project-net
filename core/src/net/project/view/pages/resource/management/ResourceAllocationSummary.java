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
import net.project.hibernate.service.IPnPersonAllocationService;
import net.project.hibernate.service.IUtilService;
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

public class ResourceAllocationSummary {

	private static Logger log;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	@ApplicationState
	private String jSPRootURL;
	
	@Persist
	private String resourceColumnLabel;

	@Persist
	private String projectColumnLabel;

	@Persist
	private String numberOfProjectColumnLabel;

	@Persist
	private String gridTitle;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String extalertTitle;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String allOption;

	@Persist
	private String noReservationDataFoundMessage;

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
	private Integer monthIndex;

	@Persist
	private String year;

	private String allocationSummaryData;

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
			
			resourceColumnLabel = PropertyProvider.get("prm.resource.resourceallocationsummary.column.resource.label");
			projectColumnLabel = PropertyProvider.get("prm.resource.resourceallocationsummary.column.project.label");
			numberOfProjectColumnLabel = PropertyProvider
					.get("prm.resource.resourceallocationsummary.column.numberofprojects.label");
			gridTitle = PropertyProvider.get("prm.resource.resourceallocationsummary.gridtitle");
			viewButtonCaption = PropertyProvider.get("prm.resource.resourceallocationsummary.view.button.caption");
			extalertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			serverRequestFailedMessage = PropertyProvider
					.get("prm.resource.resourceallocationsummary.serverrequestfailed.message");
			allOption = PropertyProvider
					.get("prm.resource.resourceallocationsummary.businessdropdownlist.alloptionvalue");
			noReservationDataFoundMessage = PropertyProvider
					.get("prm.resource.resourceallocationsummary.noonehasanyreservationinthisperiod.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.resourceallocationsummary.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.resourceallocationsummary.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.resourceallocationsummary.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.resourceallocationsummary.gothreemonthsforwardimage.tooltip");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
            preReleaseEnabled = PropertyProvider.getBoolean("prm.resource.prereleasefunctionality.label.isenabled");
            
		} catch (Exception ex) {
			log.error("Error occured while getting ResourceAllocationSummary page property values.");
		}
    }
    public ResourceAllocationSummary() {
        log = Logger.getLogger(ResourceAllocationSummary.class);
		monthList = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		Calendar cal = Calendar.getInstance();
		yearList = new ArrayList<String>();
		for (int year = 1995; year <= cal.get(Calendar.YEAR) + 10 ; year++) {
			yearList.add(Integer.toString(year));
		}
		
		businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();
	}

    void onActivate() {
        initialzePageToken();
		
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName("All");

		DateFormat df = new DateFormat(SessionManager.getUser());
		if (getMonth() == null || getYear() == null) {
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
			businessesList.addAll(bFinder.findByUser(net.project.security.SessionManager.getUser(), "A"));
		} catch (PersistenceException e) {
			log.error("Error occured while generating list values for Businesses model");
		}
		if (businessesList.size() == 1) {
			businessesList.clear();
			firstBusinessSpace.setID("0");
			firstBusinessSpace.setName("No Businesses Found");
			businessesList.add(firstBusinessSpace);
		}
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(businessesList, BusinessSpace.class, "name", "id",
				access);

		if (getBusiness() != null && !getBusiness().getID().equals("0")) {
			getAllocationSummaryDetails(Integer.parseInt(getBusiness().getID()), getMonth(), getYear());
		} else {
			getAllocationSummaryDetails(null, getMonth(), getYear());
		}
	}

	public void getAllocationSummaryDetails(Integer businessId, String month, String year) {
		String startDateString = "01/" + (monthList.indexOf(month) + 1) + "/" + year;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
		monthIndex = monthList.indexOf(month);

		Date startDate = null;
		Calendar cal = Calendar.getInstance();
		Date endDate = null;
		//date parameter for default users
		Date dateFrom = null;
		Date dateTo = null;

		try {
			startDate = dateFormat.parse(startDateString);
			cal.setTime(startDate);
			cal.add(Calendar.MONTH, 8);
			endDate = cal.getTime();
			dateFrom = userDateFormat.parseDateString(startDateString, "dd/MM/yyyy");
			dateTo = userDateFormat.parseDateString(userDateFormat.formatDate(endDate, "dd/MM/yyyy"), "dd/MM/yyyy"); 
		} catch (Exception e) {
			log.error("Invalid date Exception in view by resource " + e.getMessage());
		}

		List<ReportUserProjects> projectList;
		List<ReportMonth> monthList;

		IPnAssignmentService pnAssignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		List<ReportUser> defaultUsers = pnAssignmentService.getAssignedProjectsByBusiness(SessionManager.getUser()
				.getID(), businessId, dateFrom, dateTo);

		IPnPersonAllocationService allocationService = ServiceFactory.getInstance().getPnPersonAllocationService();
		List<ReportUser> allocationsList = allocationService.getResourceAllocationSumary(Integer
				.parseInt(SessionManager.getUser().getID()), businessId, startDate, endDate);
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		allocationSummaryData = "";
		allocationSummaryData = "[  ";
		if (allocationsList != null) {
			for (ReportUser reportUser : allocationsList) {
				projectList = reportUser.getProjektList();
				for (ReportUserProjects reportProject : projectList) {

					if (defaultUsers != null && defaultUsers.size() > 0) {
						ReportUser defaultUser = new ReportUser(reportUser.getUserId(), reportUser.getFirstName(),
								reportUser.getLastName());
						ReportUserProjects defaultProject = new ReportUserProjects(reportProject.getProjectId(),
								reportProject.getProjectName());
						List<ReportUserProjects> defaultProjects = new ArrayList<ReportUserProjects>();
						defaultProjects.add(defaultProject);
						defaultUser.setProjektList(defaultProjects);
						int foundIndex = defaultUsers.indexOf(defaultUser);
						if (foundIndex > -1) {
							defaultUsers.remove(foundIndex);
						}
					}

					allocationSummaryData += " [ " + reportUser.getUserId() + ", ";
					allocationSummaryData += " '" + reportUser.getFirstName().replaceAll("'", "&acute;") + " "
							+ reportUser.getLastName().replaceAll("'", "&acute;") + "', ";
					allocationSummaryData += " '" + reportProject.getProjectName().replaceAll("'", "&acute;") + "', ";
					allocationSummaryData += " ' ' "; // To count Number of Projects
					monthList = reportProject.getMonthList();
					IUtilService utilService = ServiceFactory.getInstance().getUtilService();
					Calendar calSatrtDate = Calendar.getInstance();
					Calendar calEndDate = Calendar.getInstance();
					calSatrtDate.setTime(startDate);
					for (ReportMonth reportMonth : monthList) {
						calEndDate = (Calendar) calSatrtDate.clone();
						calEndDate.set(Calendar.DAY_OF_MONTH, calEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
						if (reportMonth.getAllocation() != null) {
							allocationSummaryData += " , "
									+ numberFormat.formatPercent((reportMonth.getAllocation().floatValue() * 100.0)
											/ (utilService.getNumberOfWorkingDays(calSatrtDate.getTime(), calEndDate
													.getTime()) * 8.0), "###.##");
						} else {
							allocationSummaryData += " , " + reportMonth.getAllocation();
						}
						calSatrtDate.add(Calendar.MONTH, 1);
					}
					allocationSummaryData += " ], ";
				}
			}
		}

		if (defaultUsers != null && defaultUsers.size() > 0) {
			for (ReportUser defaultUser : defaultUsers) {
				for (ReportUserProjects defaultProject : defaultUser.getProjektList()) {
					allocationSummaryData += " [ " + defaultUser.getUserId() + ",  '" + defaultUser.getFirstName().replaceAll("'", "&acute;")
							+ " " + defaultUser.getLastName().replaceAll("'", "&acute;") + "',  '" + defaultProject.getProjectName().replaceAll("'", "&acute;")
							+ "',  ''  , '' , '' , '' , '' , '' , '' , '' , '' ], ";
				}
			}
		}
		allocationSummaryData = allocationSummaryData.substring(0, allocationSummaryData.length() - 2);

		allocationSummaryData += " ]";
		if (allocationSummaryData.equals("[ ]")) {
			message = noReservationDataFoundMessage;
		} else {
			message = null;
		}
	}
	/**
	 * Method used to fetch data from database by criteria to show in a grid
	 * 
	 * @param param this contains "bussinessid & startMonth & startYear" string
	 * @return Grid data array manipulated String
	 */
	public TextStreamResponse onActivate(String param) {
		String[] params = param.split("&");
		if (params.length > 1) {
			setMonth(params[1]);
			setYear(params[2]);
			if (!params[0].equals("NotSelected") && StringUtils.isNotEmpty(params[0])) {
				setBusiness(new BusinessSpace(params[0]));
				getAllocationSummaryDetails(Integer.parseInt(params[0]), params[1], params[2]);
			} else {
				setBusiness(new BusinessSpace("0"));
				getAllocationSummaryDetails(null, params[1], params[2]);
			}
		}
		return new TextStreamResponse("text/plain", allocationSummaryData);
	}

	/**
	 * Method for getting dropdown list values for Businessspace from database
	 * 
	 * @return GenericSelectModel<BusinessSpace>
	 */
	public GenericSelectModel<BusinessSpace> getBusinessesModel() {
		return businessSpaceBeans;
	}

	public List getMonthsModel() {
		return monthList;
	}

	public List getYearsModel() {
		return yearList;
	}

	@CleanupRender
	void CleanValues() {
		message = null;
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

	public String getNumberOfProjectColumnLabel() {
		return numberOfProjectColumnLabel;
	}

	public String getProjectColumnLabel() {
		return projectColumnLabel;
	}

	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	public String getViewButtonCaption() {
		return viewButtonCaption;
	}

	/**
	 * @return the allocationSummaryData
	 */
	public String getAllocationSummaryData() {
		return allocationSummaryData;
	}

	/**
	 * @param allocationSummaryData
	 *            the allocationSummaryData to set
	 */
	public void setAllocationSummaryData(String allocationSummaryData) {
		this.allocationSummaryData = allocationSummaryData;
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
	 * @return the allOption
	 */
	public String getAllOption() {
		return allOption;
	}

	/**
	 * @return the extalertTitle
	 */
	public String getExtalertTitle() {
		return extalertTitle;
	}

	/**
	 * @return the serverRequestFailedMessage
	 */
	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
	}

	/**
	 * @return the noReservationDataFoundMessage
	 */
	public String getNoReservationDataFoundMessage() {
		return noReservationDataFoundMessage;
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
