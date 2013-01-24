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
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * @author
 */
public class ViewSummary {

	private static Logger log;

	@ApplicationState
	private String jSPRootURL;
		
	@Persist
	private String selectResourcesButtonCaption;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String resourceColumnLabel;

	@Persist
	private String projectNameColumnLabel;

	@Persist
	private String totalAssignedColumnLabel;

	@Persist
	private String totalAvailableColumnLabel;

	@Persist
	private String resourceListSelectionReqMsg;

	@Persist
	private String extAlertTitle;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String summaryGridTitle;

	@Persist
	private String message;

	@Persist
	private String searchMessage;

	@Persist
	private String allOption;

	@Persist
	private String noBusinessFoundOption;

	@Persist
	private String goOneMonthBackwardImageTooltip;

	@Persist
	private String goThreeMonthsBackwardImageTooltip;

	@Persist
	private String goOneMonthForwardImageTooltip;

	@Persist
	private String goThreeMonthsForwardImageTooltip;

	@Persist
	private BusinessSpace businessList;

	@Persist
	private String month;

	@Persist
	private String year;

	@Persist
	private String summaryGridData;

	@Persist
	private int monthIndex;

	@Inject
	private PropertyAccess access;

	private List<String> months;

	private List<String> years;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	private BusinessSpaceFinder bFinder;

	private List<BusinessSpace> businessesList;

	private BusinessSpace firstBusinessSpace;

	private String workingHoursInMonth;

	@InjectPage
	private ResourceAssignmentDetail resourceAssignmentDetail;
	
	@Persist
	private String gridTotalRowLabel;
	
	@Persist
	private String versionNumber;

     //  Method to initialize page token
    private void initialzePageToken() {
		try {
			selectResourcesButtonCaption = PropertyProvider
					.get("prm.resource.viewsummary.selectresources.button.caption");
			viewButtonCaption = PropertyProvider.get("prm.resource.viewsummary.view.button.caption");
			resourceColumnLabel = PropertyProvider.get("prm.resource.viewsummary.column.resource.label");
			projectNameColumnLabel = PropertyProvider.get("prm.resource.viewsummary.column.projectname.label");
			totalAssignedColumnLabel = PropertyProvider.get("prm.resource.viewsummary.column.totalassigned.label");
			totalAvailableColumnLabel = PropertyProvider.get("prm.resource.viewsummary.column.totalavailable.label");

			summaryGridTitle = PropertyProvider.get("prm.resource.viewsummary.summaryGrid.title");
			searchMessage = PropertyProvider.get("prm.resource.viewsummary.projectssearch.message");
			extAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			serverRequestFailedMessage = PropertyProvider.get("prm.resource.viewsummary.serverrequestfailed.message");
			allOption = PropertyProvider.get("prm.resource.viewsummary.businessdropdownlist.alloptionvalue");
			noBusinessFoundOption = PropertyProvider
					.get("prm.resource.viewsummary.businessdropdownlist.nobusinessfound.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.viewsummary.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.viewsummary.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.viewsummary.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.viewsummary.gothreemonthsforwardimage.tooltip");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
			log = Logger.getLogger(ViewSummary.class);
			months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
			Calendar cal = Calendar.getInstance();
			years = new ArrayList<String>();
			for (int year = 1995; year <= cal.get(Calendar.YEAR) + 10; year++) {
				years.add(Integer.toString(year));
			}
		} catch (Exception ex) {
			log.error("Error occured while getting ViewSummary page property values." + ex.getMessage());
		}
    }
    
    public void onActivate() {
		initialzePageToken();
        
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName(allOption);

		DateFormat df = new DateFormat(SessionManager.getUser());
		if (getMonth() == null || getYear() == null) {
			month = df.formatDate(new Date(), "MMM yyyy").split(" ")[0];
			year = df.formatDate(new Date(), "MMM yyyy").split(" ")[1];
		}

		businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();
		if (businessesList != null) {
			businessesList.clear();
		}
		businessesList.add(firstBusinessSpace);
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			// Getting database values for business list
			businessesList.addAll(bFinder.findByUser(net.project.security.SessionManager.getUser(), "A"));
		} catch (PersistenceException e) {
			log.error("Error occured while generating list values for Businesses model");
		}
		if (businessesList.size() == 1) {
			businessesList.clear();
			firstBusinessSpace.setID("0");
			firstBusinessSpace.setName(noBusinessFoundOption);
			businessesList.add(firstBusinessSpace);
		}
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(businessesList, BusinessSpace.class, "name", "id",
				access);

		if (getBusinessList() == null) {
			setBusinessList(firstBusinessSpace);
		}
		getAssignmentSummaryGridData(Integer.parseInt(net.project.security.SessionManager.getUser().getID()), Integer
				.parseInt(getBusinessList().getID()));
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
		return months;
	}

	/**
	 * @return year dropdown list
	 */
	public List getYearsModel() {
		return years;
	}

	/**
	 * Method to get assignement summary by userid and businessid
	 * 
	 * @param userId
	 * @param businessId
	 */
	void getAssignmentSummaryGridData(Integer userId, Integer businessId) {
		List<ReportUser> assignmentSummaryDetails = new ArrayList<ReportUser>();
		List<ReportUserProjects> projectList;
		List<ReportMonth> monthList;

		String startDateString = "01/" + (months.indexOf(getMonth()) + 1) + "/" + getYear();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		monthIndex = months.indexOf(getMonth());

		Date startDate = null;
		Calendar endDate = Calendar.getInstance();

		try {
			startDate = dateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Invalid date Exception in View Summary report" + e.getMessage());
		}

		endDate.setTime(startDate);
		endDate.add(Calendar.MONTH, 6);

		// calculating working hours for month
		workingHoursInMonth = "[";
		IUtilService utilService = ServiceFactory.getInstance().getUtilService();
		Calendar firstDate = Calendar.getInstance();
		Calendar lastDate = Calendar.getInstance();
		firstDate.setTime(startDate);
		for (int mIndex = 0; mIndex < 6; mIndex++) {
			if (mIndex > 0) {
				workingHoursInMonth += ",";
			}
			lastDate.setTime(firstDate.getTime());
			lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			workingHoursInMonth += (utilService.getNumberOfWorkingDays(firstDate.getTime(), lastDate.getTime())) * 8;
			firstDate.add(Calendar.MONTH, 1);
		}
		workingHoursInMonth += "]";

		if (businessId != null && businessId.intValue() == 0) {
			businessId = null;
		}

		IPnAssignmentService assignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		assignmentSummaryDetails = assignmentService.getWorkSumByMonthForUsers(userId, businessId, startDate, endDate
				.getTime(), SessionManager.getUser().getDateFormatter());
		resourceAssignmentDetail.setAssignedProjects(assignmentSummaryDetails);

		summaryGridData = "[ ";
		if (assignmentSummaryDetails.size() > 0) {
			for (ReportUser assignmentSummary : assignmentSummaryDetails) {
				projectList = assignmentSummary.getProjektList();
				for (ReportUserProjects reportProject : projectList) {
					summaryGridData += "[" + assignmentSummary.getUserId() + ", " + "'"
							+ assignmentSummary.getFirstName().replaceAll("'", "&acute;") + " "
							+ assignmentSummary.getLastName().replaceAll("'", "&acute;") + "', "
							+ reportProject.getProjectId() + ", " + "'"
							+ reportProject.getProjectName().replaceAll("'", "&acute;") + "' ";
					monthList = reportProject.getMonthList();
					for (ReportMonth reportMonth : monthList) {
						summaryGridData += ", " + reportMonth.getTotalWork();
					}
					summaryGridData += ", 'Total Available'";
					summaryGridData += "], ";
				}
			}
			summaryGridData = summaryGridData.substring(0, summaryGridData.length() - 2);
		}
		
		// todo: the previous loop block should be fixed.
		// the reason - in certain cases the value of summaryGridData is " ]".
		// this is producing JS error (at least for HtmlUnit and FF browser console)
		// so a temporary hack/fix is:
		if (summaryGridData.equals("")) {
			summaryGridData = "[ ";
		}
		//
		
		summaryGridData += " ]";
		if (summaryGridData.endsWith("[  ]")) {
			message = searchMessage;
		} else {
			message = null;
		}
	}

	/**
	 * Method called on ajax request
	 * 
	 * @param param
	 *            this contains "bussinessid & startMonth & startYear" string
	 * @return
	 */
	public TextStreamResponse onActivate(String param) {
		initialzePageToken();
		String[] params = param.split("&");
		if (params != null && params.length > 2) {
			setMonth(params[1]);
			setYear(params[2]);
			setBusinessList(new BusinessSpace(params[0]));
			getAssignmentSummaryGridData(Integer.parseInt(net.project.security.SessionManager.getUser().getID()),
					Integer.parseInt(params[0]));
		}
		return new TextStreamResponse("text/plain", summaryGridData + "|&|" + workingHoursInMonth);
	}

	@CleanupRender
	void cleanValues() {
		setMessage(null);
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return Returns the selectResourcesButtonCaption.
	 */
	public String getSelectResourcesButtonCaption() {
		return selectResourcesButtonCaption;
	}

	/**
	 * @return Returns the businessList.
	 */
	public BusinessSpace getBusinessList() {
		return businessList;
	}

	/**
	 * @param businessList
	 *            The businessList to set.
	 */
	public void setBusinessList(BusinessSpace businessList) {
		this.businessList = businessList;
	}

	/**
	 * @return Returns the month.
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            The month to set.
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return Returns the year.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year
	 *            The year to set.
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return Returns the summaryGridData.
	 */
	public String getSummaryGridData() {
		return summaryGridData;
	}

	/**
	 * @param summaryGridData
	 *            The summaryGridData to set.
	 */
	public void setSummaryGridData(String summaryGridData) {
		this.summaryGridData = summaryGridData;
	}

	/**
	 * @return Returns the monthIndex.
	 */
	public int getMonthIndex() {
		return monthIndex;
	}

	/**
	 * @param monthIndex
	 *            The monthIndex to set.
	 */
	public void setMonthIndex(int monthIndex) {
		this.monthIndex = monthIndex;
	}

	/**
	 * @return Returns the projectNameColumnLabel.
	 */
	public String getProjectNameColumnLabel() {
		return projectNameColumnLabel;
	}

	/**
	 * @return Returns the resourceColumnLabel.
	 */
	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	/**
	 * @return Returns the resourceListSelectionReqMsg.
	 */
	public String getResourceListSelectionReqMsg() {
		return resourceListSelectionReqMsg;
	}

	/**
	 * @return Returns the summaryGridTitle.
	 */
	public String getSummaryGridTitle() {
		return summaryGridTitle;
	}

	/**
	 * @return Returns the totalAssignedColumnLabel.
	 */
	public String getTotalAssignedColumnLabel() {
		return totalAssignedColumnLabel;
	}

	/**
	 * @return Returns the totalAvailableColumnLabel.
	 */
	public String getTotalAvailableColumnLabel() {
		return totalAvailableColumnLabel;
	}

	/**
	 * @return Returns the viewButtonCaption.
	 */
	public String getViewButtonCaption() {
		return viewButtonCaption;
	}

	/**
	 * @return the searchMessage
	 */
	public String getSearchMessage() {
		return searchMessage;
	}

	/**
	 * @param searchMessage
	 *            the searchMessage to set
	 */
	public void setSearchMessage(String searchMessage) {
		this.searchMessage = searchMessage;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the workingDaysInMonth
	 */
	public String getWorkingHoursInMonth() {
		return workingHoursInMonth;
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

}
