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
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.Version;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * @author
 */
public class AssignmentSummary {

	private static Logger log;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	private GenericSelectModel<PnProjectSpace> pnProjectSpaceBeans;

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private String gridTitle;

	@Persist
	private String orLabel;

	@Persist
	private String portfolioLabel;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String showOtherProjectAssignmentLabel;

	@Persist
	private String resourceIdColumnLabel;

	@Persist
	private String resourceColumnLabel;

	@Persist
	private String projectIdColumnLabel;

	@Persist
	private String projectNameColumnLabel;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String extAlertTitle;

	@Persist
	private String projectNotExistToViewMessage;

	@Persist
	private String allOption;

	@Persist
	private String noBusinessFoundOption;

	@Persist
	private String noProjectsFoundOption;
	
	@Persist
	private String noDataFoundMessage;
	
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

	@Persist
	private PnProjectSpace pnProject;

	@Persist
	private PnPortfolio pnPortfolio;

	@Persist
	private String message;

	@Inject
	private PropertyAccess access;

	private BusinessSpaceFinder bFinder;

	private IPnProjectSpaceService pnProjectSpaceService;

	private List<BusinessSpace> businessesList;

	private List<PnProjectSpace> projectsList;

	private List<String> monthList;

	private List<String> yearList;

	@Persist
	private String month;

	@Persist
	private String year;

	@Persist
	private String summaryGridData;

	@Persist
	private String showOtherProjectAssignment;

	@Persist
	private Integer monthIndex;

	private BusinessSpace firstBusinessSpace;

	private PnProjectSpace firstPnProjectSpace;
	
	@Persist
	private String workingHoursInMonth;
	
	@Persist 
	private String gridTotalRowLabel;
	
	@Persist
	private String versionNumber;
	
     //Method to initialize page token
    private void initialzePageToken() {
		try {
			orLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.or.label");
			portfolioLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.portfolio.label");
			showOtherProjectAssignmentLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.showotherprojectassignment.label");
			viewButtonCaption = PropertyProvider
					.get("prm.resource.assignmentsummary.view.button.caption");
			gridTitle = PropertyProvider
					.get("prm.resource.assignmentsummary.gridtitle");
			serverRequestFailedMessage = PropertyProvider
					.get("prm.resource.assignmentsummary.serverrequestfailed.message");
			resourceIdColumnLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.column.resourceid.label");
			resourceColumnLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.column.resource.label");
			projectIdColumnLabel = PropertyProvider
					.get("prm.resource.allocationvsassignment.column.projectid.label");
			projectNameColumnLabel = PropertyProvider
					.get("prm.resource.assignmentsummary.column.projectname.label");
			extAlertTitle = PropertyProvider
					.get("prm.resource.global.exterroralert.title");
			projectNotExistToViewMessage = PropertyProvider
					.get("prm.resource.assignmentsummary.projectnotexistinbusinesstoviewsummary.message");
			allOption = PropertyProvider
					.get("prm.resource.assignmentsummary.businessdropdownlist.alloptionvalue");
			noBusinessFoundOption = PropertyProvider
					.get("prm.resource.assignmentsummary.businessdropdownlist.nobusinessfound.message");
			noProjectsFoundOption = PropertyProvider
					.get("prm.resource.assignmentsummary.projectdropdownlist.noprojectsfound.message");
			noDataFoundMessage = PropertyProvider
					.get("prm.resource.assignmentsummary.datanotfoundtoviewinthisperiod.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.assignmentsummary.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.assignmentsummary.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.assignmentsummary.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.assignmentsummary.gothreemonthsforwardimage.tooltip");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		} catch (Exception ex) {
			log
					.error("Error occured while getting ViewDetails page property values.");
		}
    }
    
    public AssignmentSummary() {
        log = Logger.getLogger(AssignmentSummary.class);
		businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();

		pnProjectSpaceService = ServiceFactory.getInstance()
				.getPnProjectSpaceService();
		
		firstPnProjectSpace = new PnProjectSpace();
		projectsList = new ArrayList<PnProjectSpace>();

		monthList = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		
		Calendar cal = Calendar.getInstance();
		yearList = new ArrayList<String>();
		for (int year = 1995; year <= cal.get(Calendar.YEAR) + 10 ; year++) {
			yearList.add(Integer.toString(year));
		}
	}

	void onActivate() {
        initialzePageToken();
        
		// Getting database values for business list
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName(allOption);
		
		DateFormat df = new DateFormat(SessionManager.getUser());
		if(getMonth()==null || getYear() == null){
			month = df.formatDate(new Date(), "MMM yyyy").split(" ")[0];
			year = df.formatDate(new Date(), "MMM yyyy").split(" ")[1];
		}
		

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
			firstBusinessSpace.setName(noBusinessFoundOption);
			businessesList.add(firstBusinessSpace);
		}
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(
				businessesList, BusinessSpace.class, "name", "id", access);

		/* Getting database value for Project list */
		if (projectsList != null) {
			projectsList.clear();
		}
		if(getBusiness()!=null && getBusiness().getID() != "0"){
			projectsList.addAll(pnProjectSpaceService
					.getProjectsByBusinessId(Integer.parseInt(getBusiness().getID())));
		}else{
		projectsList.addAll(pnProjectSpaceService
				.getProjectsByUserId(Integer
						.parseInt(net.project.security.SessionManager.getUser()
								.getID())));
		}
		if (projectsList != null && projectsList.size() == 0) {
			projectsList.clear();
			firstPnProjectSpace.setProjectId(0);
			firstPnProjectSpace.setProjectName(noProjectsFoundOption);
			projectsList.add(firstPnProjectSpace);
		}
		pnProjectSpaceBeans = new GenericSelectModel<PnProjectSpace>(
				projectsList, PnProjectSpace.class, "projectName", "projectId",
				access);

		Integer businessId = null;
		Integer projectId = 0;

		if (getBusiness() != null && !getBusiness().getID().equals("0")) {
			businessId = Integer.parseInt(business.getID());
		}
		if (getPnProject() != null) {
			projectId = getPnProject().getProjectId();
		} else if (projectsList != null && projectsList.size() > 0) {
			projectId = projectsList.get(0).getProjectId();
		}
		getAssignmentSummaryData(businessId, projectId, getMonth(), getYear());
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
	 * Method for getting dropdown list values for PnProjectSpace from database
	 * 
	 * @return GenericSelectModel<PnProjectSpace>
	 */
	public GenericSelectModel<PnProjectSpace> getProjectsModel() {
		return pnProjectSpaceBeans;
	}

	/**
	 * Getting assignment summary data by businessid, 
	 * projectid and start - end dates  
	 * 
	 * @param businessId
	 * @param projectId
	 * @param startMonth
	 * @param startYear
	 */
	public void getAssignmentSummaryData(Integer businessId, Integer projectId, String startMonth, String startYear) {
		//setting values
		setMonth(startMonth);
		setYear(startYear);
		
		//set the prjectspce which is to display.
		if(CollectionUtils.isNotEmpty(projectsList)){
			for(PnProjectSpace projectSpace : projectsList){
				if(projectSpace.getProjectId().equals(projectId)){
					setPnProject(projectSpace);
					break;
				}
			}
		}
		//If there is no project of user than set projectSapce
		if (getPnProject() == null){
			setPnProject(new PnProjectSpace(0,""));
		}
		
		String startDateString = "01/" + (monthList.indexOf(startMonth) + 1)
				+ "/" + startYear;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Date startDate = null;
		Calendar endDate = Calendar.getInstance();

		monthIndex = monthList.indexOf(getMonth());

		try {
			startDate = dateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Invalid date Exception in Assignment Summary "
					+ e.getMessage());
		}

		endDate.setTime(startDate);
		endDate.add(Calendar.MONTH, 12);
		
		//	calculating working hours for month
		workingHoursInMonth = "[";
		IUtilService utilService = ServiceFactory.getInstance().getUtilService();
		Calendar firstDate = Calendar.getInstance();
		Calendar lastDate = Calendar.getInstance();
		firstDate.setTime(startDate);
		for (int mIndex = 0; mIndex < 12; mIndex++) {
			if (mIndex > 0) {
				workingHoursInMonth += ",";
			}
			lastDate.setTime(firstDate.getTime());
			lastDate.set(Calendar.DAY_OF_MONTH, lastDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			workingHoursInMonth += (utilService.getNumberOfWorkingDays(
					firstDate.getTime(), lastDate.getTime())) * 8;
			firstDate.add(Calendar.MONTH, 1);
		}
		workingHoursInMonth += "]";

		List<ReportUser> summaryList = new ArrayList<ReportUser>();
		List<ReportUserProjects> projectList = new ArrayList<ReportUserProjects>();
		List<ReportMonth> monthList = new ArrayList<ReportMonth>();

		IPnAssignmentService assignmentService = ServiceFactory.getInstance()
				.getPnAssignmentService();
		summaryList = assignmentService.getResourceAssignmentSummaryByBusiness(
						Integer.parseInt(SessionManager.getUser().getID()), businessId,
						getPnProject().getProjectId(), startDate, endDate.getTime(), SessionManager.getUser().getDateFormatter());

		summaryGridData = "[  ";
		if (summaryList != null) {
			for (ReportUser reportUser : summaryList) {
				projectList = reportUser.getProjektList();
				for (ReportUserProjects reportProject : projectList) {
					summaryGridData += "[ " + reportUser.getUserId() + ", ";
					summaryGridData += " '"
							+ reportUser.getFirstName().replaceAll("'",
									"&acute;")
							+ " "
							+ reportUser.getLastName().replaceAll("'",
									"&acute;") + "', ";
					summaryGridData += reportProject.getProjectId() + ", ";
					summaryGridData += " '"
							+ reportProject.getProjectName().replaceAll("'",
									"&acute;") + "' ";
					monthList = reportProject.getMonthList();
					if (monthList != null) {
						for (ReportMonth reportMonth : monthList) {
							if (reportMonth.getTotalWork() != null) {
								summaryGridData += " , "
										+ reportMonth.getTotalWork();
							} else {
								summaryGridData += " , 0 ";
							}
						}
					}
					summaryGridData += " ], ";
				}
			}
			summaryGridData = summaryGridData.substring(0, summaryGridData
					.length() - 2);
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
		if (summaryGridData.equals("[ ]")) {
			setMessage(noDataFoundMessage);
		} else {
			setMessage(null);
		}
	}

	/**
	 * Method for Getting dropdown list script for PnProjectSpace by Ajax
	 * request.
	 * @param param this contains "businessId & projectId & startMonth & startYear" string
	 * @return
	 */
	public TextStreamResponse onActivate(String param) {
		String[] params = param.split("&");
		if (params.length == 1) {
			PnProjectSpace pnprojectspace;
			String scriptSelect = "<select id=\"project\">";
			if (projectsList != null)
				projectsList.clear();
			if (!params[0].equals("0")) {
				projectsList.addAll(pnProjectSpaceService
						.getProjectsByBusinessId(Integer.parseInt(params[0])));
			} else {
				projectsList.addAll(pnProjectSpaceService
						.getProjectsByUserId(Integer
								.parseInt(net.project.security.SessionManager
										.getUser().getID())));
			}

			// generating html for select tag options on selection of business
			if (projectsList.size() > 0) {
				for (int i = 0; i < projectsList.size(); i++) {
					pnprojectspace = projectsList.get(i);
					scriptSelect += "<option value=\""
							+ pnprojectspace.getProjectId() + "\">";
					scriptSelect += pnprojectspace.getProjectName();
					scriptSelect += "</option>";
				}
			} else {
				firstPnProjectSpace.setProjectId(0);
				firstPnProjectSpace.setProjectName(noProjectsFoundOption);
				projectsList.add(firstPnProjectSpace);
				scriptSelect += "<option value='0'>No projects found </option>";
			}
			scriptSelect += "</select>";
			return new TextStreamResponse("text/html", scriptSelect);
		} else if (params[0].equals("NotSelected") || StringUtils.isEmpty(params[0])) {
			setBusiness(new BusinessSpace("0"));
			getAssignmentSummaryData(null, Integer.parseInt(params[1]),
					params[2], params[3]);
			return new TextStreamResponse("text/plain", summaryGridData + "|&|" + workingHoursInMonth);
		} else {
			setBusiness(new BusinessSpace(params[0]));
			getAssignmentSummaryData(Integer.parseInt(params[0]), Integer
					.parseInt(params[1]), params[2], params[3]);
			return new TextStreamResponse("text/plain", summaryGridData + "|&|" + workingHoursInMonth);
		}
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
	 * @return the business
	 */
	public BusinessSpace getBusiness() {
		return business;
	}

	/**
	 * @param business
	 *            the business to set
	 */
	public void setBusiness(BusinessSpace business) {
		this.business = business;
	}

	/**
	 * @return the pnProject
	 */
	public PnProjectSpace getPnProject() {
		return pnProject;
	}

	/**
	 * @param pnProject
	 *            the pnProject to set
	 */
	public void setPnProject(PnProjectSpace pnProject) {
		this.pnProject = pnProject;
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

	public String getShowOtherProjectAssignment() {
		return showOtherProjectAssignment;
	}

	public void setShowOtherProjectAssignment(String showOtherProjectAssignment) {
		this.showOtherProjectAssignment = showOtherProjectAssignment;
	}

	/**
	 * @return the gridTitle
	 */
	public String getGridTitle() {
		return gridTitle;
	}

	/**
	 * @return the portfolioLabel
	 */
	public String getPortfolioLabel() {
		return portfolioLabel;
	}

	/**
	 * @return the resourceColumnLabel
	 */
	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	/**
	 * @return the resourceIdColumnLabel
	 */
	public String getResourceIdColumnLabel() {
		return resourceIdColumnLabel;
	}

	/**
	 * @return the serverRequestFailedMessage
	 */
	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
	}

	/**
	 * @return the showOtherProjectAssignmentLabel
	 */
	public String getShowOtherProjectAssignmentLabel() {
		return showOtherProjectAssignmentLabel;
	}

	/**
	 * @return the viewButtonCaption
	 */
	public String getViewButtonCaption() {
		return viewButtonCaption;
	}

	/**
	 * @return the orLabel
	 */
	public String getOrLabel() {
		return orLabel;
	}

	/**
	 * @return the pnPortfolio
	 */
	public PnPortfolio getPnPortfolio() {
		return pnPortfolio;
	}

	/**
	 * @param pnPortfolio
	 *            the pnPortfolio to set
	 */
	public void setPnPortfolio(PnPortfolio pnPortfolio) {
		this.pnPortfolio = pnPortfolio;
	}

	/**
	 * @return the summaryGridData
	 */
	public String getSummaryGridData() {
		return summaryGridData;
	}

	/**
	 * @param summaryGridData
	 *            the summaryGridData to set
	 */
	public void setSummaryGridData(String summaryGridData) {
		this.summaryGridData = summaryGridData;
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
	 * @return the projectNameColumnLabel
	 */
	public String getProjectNameColumnLabel() {
		return projectNameColumnLabel;
	}

	/**
	 * @return the projectIdColumnLabel
	 */
	public String getProjectIdColumnLabel() {
		return projectIdColumnLabel;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
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
	 * @return the extAlertTitle
	 */
	public String getExtAlertTitle() {
		return extAlertTitle;
	}

	/**
	 * @return the projectNotExistToViewMessage
	 */
	public String getProjectNotExistToViewMessage() {
		return projectNotExistToViewMessage;
	}

	/**
	 * @return the noDataFoundMessage
	 */
	public String getNoDataFoundMessage() {
		return noDataFoundMessage;
	}

	/**
	 * @return the workingHoursInMonth
	 */
	public String getWorkingHoursInMonth() {
		return workingHoursInMonth;
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
	 * @return the goThreeMonthsforwardImageTooltip
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
