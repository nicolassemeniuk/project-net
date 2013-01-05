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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.hibernate.model.PnPersonAllocationPK;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.hibernate.service.IPnPersonAllocationService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.Version;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author
 */
public class AllocateByProject extends BasePage{

	private static Logger log;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	private GenericSelectModel<PnProjectSpace> pnProjectSpaceBeans;

	private GenericSelectModel<PnPerson> personSpaceBean;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String resourceColumnLabel;

	@Persist
	private String untilEndOfPorjectColumnLabel;

	@Persist
	private String gridTitle;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String clickToAddResourceTooltip;

	@Persist
	private String saveAllocationsTooltip;

	@Persist
	private String untilEndOfProjectColumnTooltip;

	@Persist
	private String selectResourceToAddMessage;

	@Persist
	private String clickToEditTooltip;

	@Persist
	private String saveAllocationsTooltipTitle;

	@Persist
	private String addResourceTooltipTitle;

	@Persist
	private String allOption;

	@Persist
	private String noBusinessFoundOption;

	@Persist
	private String selectResourceOption;

	@Persist
	private String noResourcesFoundOption;

	@Persist
	private String noProjectsFoundOption;

	@Persist
	private String confirmIgnoreChangesTitle;

	@Persist
	private String gridTotalRowLabel;

	@Persist
	private String allocationNotMoreThan100PercentMessage;

	@Persist
	private String allocationZeroPercentMessage;

	@Persist
	private String addButtonCaption;

	@Persist
	private String submitButtonCaption;

	@Persist
	private String extAlertTitle;

	@Persist
	private String noRecordToSaveMessage;

	@Persist
	private String projectNotExistToAllocateMessage;

	@Persist
	private String ignoreChangesMessage;

	@Persist
	private String noResourcesToViewAllocationMessage;

	@Persist
	private String resourceAlreadyAddedMessage;

	@Persist
	private String reservationNotFoundMessage;

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
	private PnProjectSpace project;

	@Persist
	private PnPerson person;

	@Persist
	private String month;

	@Persist
	private String year;

	@Persist
	private int monthIndex;

	@InjectPage
	private AllocateByProject allocateByProject;

	@Inject
	private PropertyAccess access;

	private BusinessSpaceFinder bFinder;

	private IPnProjectSpaceService pnProjectSpaceService;

	private IPnPersonService pnPersonService;

	private List<BusinessSpace> businessesList;

	private List<PnProjectSpace> projectsList;

	private List<PnPerson> pnPersonsList;

	private List<String> monthList;

	private List<String> yearList;

	private BusinessSpace firstBusinessSpace;

	private PnPerson firstpersonSpace;

	private PnProjectSpace firstPnProjectSpace;

	@Persist
	private String projectAllocationData;

	@Persist
	private String comboListRecord;

	@Persist
	private String message;

	@ApplicationState
	private String jSPRootURL;
	
	@Persist
	private String versionNumber;
	
	@Persist
	private boolean preReleaseEnabled;

    //Method to initialize page token
	private void initialzePageToken() {
		try {
			viewButtonCaption = PropertyProvider.get("prm.resource.allocatebyproject.view.button.caption");
			resourceColumnLabel = PropertyProvider.get("prm.resource.allocatebyproject.column.resource.label");
			untilEndOfPorjectColumnLabel = PropertyProvider
					.get("prm.resource.allocatebyproject.column.untilendofproject.label");
			gridTitle = PropertyProvider.get("prm.resource.allocatebyproject.gridtitle");
			serverRequestFailedMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.serverrequestfailed.message");
			saveAllocationsTooltipTitle = PropertyProvider
					.get("prm.resource.allocatebyproject.saveallocations.tooltip.title");
			saveAllocationsTooltip = PropertyProvider.get("prm.resource.allocatebyproject.saveallocations.tooltip");
			untilEndOfProjectColumnTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.untilendofproject.column.tooltip");
			selectResourceToAddMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.selectresourcetoadd.message");
			clickToEditTooltip = PropertyProvider.get("prm.resource.allocatebyproject.clicktoedit.tooltip");
			addResourceTooltipTitle = PropertyProvider.get("prm.resource.allocatebyproject.addresource.tooltip.title");
			clickToAddResourceTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.clicktoaddresource.tooltip");
			allOption = PropertyProvider.get("prm.resource.allocatebyproject.businessdropdownlist.alloptionvalue");
			noBusinessFoundOption = PropertyProvider
					.get("prm.resource.allocatebyproject.businessdropdownlist.nobusinessfound.message");
			selectResourceOption = PropertyProvider
					.get("prm.resource.allocatebyproject.resourcedropdownlist.selectresourceoptionvalue");
			noResourcesFoundOption = PropertyProvider
					.get("prm.resource.allocatebyproject.resourcedropdownlist.noresourcesfound.message");
			noProjectsFoundOption = PropertyProvider
					.get("prm.resource.allocatebyproject.projectdropdownlist.noprojectsfound.message");
			confirmIgnoreChangesTitle = PropertyProvider
					.get("prm.resource.allocatebyproject.confirmignorechanges.title");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			allocationNotMoreThan100PercentMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.allocationmorethanhundredpercent.message");
			allocationZeroPercentMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.allocationzeropercent.message");
			addButtonCaption = PropertyProvider.get("prm.resource.allocatebyproject.grid.addbutton.caption");
			submitButtonCaption = PropertyProvider.get("prm.resource.allocatebyproject.grid.submitbutton.caption");
			extAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			noRecordToSaveMessage = PropertyProvider.get("prm.resource.allocatebyproject.grid.norecordtosave.message");
			ignoreChangesMessage = PropertyProvider.get("prm.resource.allocatebyproject.ignorethechanges.message");
			noResourcesToViewAllocationMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.businessdonthaveresourcestoviewallocation.message");
			projectNotExistToAllocateMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.grid.projectnotexisttoallocate.message");
			resourceAlreadyAddedMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.resourcealreadyadded.message");
			reservationNotFoundMessage = PropertyProvider
					.get("prm.resource.allocatebyproject.resourcereservationsnotfoundinthisperiod.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyproject.gothreemonthsforwardimage.tooltip");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
            preReleaseEnabled = PropertyProvider.getBoolean("prm.resource.prereleasefunctionality.label.isenabled");
		} catch (Exception ex) {
			log.error("Error occured while getting AllocateByProject page property values.");
		}
    }
    
    public AllocateByProject() {
        log = Logger.getLogger(AllocateByProject.class);
		businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();

		pnPersonService = ServiceFactory.getInstance().getPnPersonService();
		pnPersonsList = new ArrayList<PnPerson>();

		pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		projectsList = new ArrayList<PnProjectSpace>();

		monthList = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

		Calendar cal = Calendar.getInstance();
		yearList = new ArrayList<String>();
		for (int year = 1995; year <= cal.get(Calendar.YEAR) + 10; year++) {
			yearList.add(Integer.toString(year));
		}
	}

	void onActivate() {
        initialzePageToken();
		// initializing date
		DateFormat userDateFormat = new DateFormat(SessionManager.getUser());
		if (getMonth() == null || getYear() == null) {
			month = userDateFormat.formatDate(new Date(), "MMM yyyy").split(" ")[0];
			year = userDateFormat.formatDate(new Date(), "MMM yyyy").split(" ")[1];
		}

		// Getting database values for business list
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName(allOption);

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
			firstBusinessSpace.setName(noBusinessFoundOption);
			businessesList.add(firstBusinessSpace);
		}
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(businessesList, BusinessSpace.class, "name", "id",
				access);

		// Getting database value for Project list
		if (projectsList != null) {
			projectsList.clear();
		}
		if (pnPersonsList != null) {
			pnPersonsList.clear();
		}
		firstPnProjectSpace = new PnProjectSpace();
		firstpersonSpace = new PnPerson();
		firstpersonSpace.setPersonId(0);
		firstpersonSpace.setDisplayName(selectResourceOption);
		pnPersonsList.add(firstpersonSpace);

		if (getBusiness() != null && !getBusiness().getID().equals("0")) {
			projectsList.addAll(pnProjectSpaceService.getProjectsByBusinessId(Integer.parseInt(getBusiness().getID())));
			pnPersonsList.addAll(pnPersonService.getPersonsByBusinessId(Integer.parseInt(getBusiness().getID())));
		} else {
			projectsList.addAll(pnProjectSpaceService.getProjectsByUserId(Integer
					.parseInt(net.project.security.SessionManager.getUser().getID())));

			List<Integer> businessIds = new ArrayList<Integer>();
			for (BusinessSpace business : businessesList) {
				businessIds.add(Integer.parseInt(business.getID()));
			}
			pnPersonsList.addAll(pnPersonService.getUniqueMembersOfBusinessCollection(businessIds));
		}
		if (pnPersonsList.size() == 1) {
			pnPersonsList.clear();
			firstpersonSpace.setPersonId(0);
			firstpersonSpace.setDisplayName(noResourcesFoundOption);
			pnPersonsList.add(firstpersonSpace);
		} else {
			for(PnPerson person : pnPersonsList) {
				person.setDisplayName(HTMLUtils.escape(person.getDisplayName()).replaceAll("'", "&acute;"));
			}
		}
		if (projectsList.size() == 0) {
			firstPnProjectSpace.setProjectId(0);
			firstPnProjectSpace.setProjectName(noProjectsFoundOption);
			projectsList.add(firstPnProjectSpace);
		}

		pnProjectSpaceBeans = new GenericSelectModel<PnProjectSpace>(projectsList, PnProjectSpace.class, "projectName",
				"projectId", access);
		personSpaceBean = new GenericSelectModel<PnPerson>(pnPersonsList, PnPerson.class, "displayName", "personId",
				access);
			
		
		if (getProject() == null) {
			setProject(new PnProjectSpace(projectsList.get(0).getProjectId(), projectsList.get(0).getProjectName()));
		}else if(projectsList.indexOf(getProject()) < 0 ){
			setProject(new PnProjectSpace(projectsList.get(0).getProjectId(), projectsList.get(0).getProjectName()));
		}
		getAllocationDetails();
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
	 * Method for getting dropdown list values for PnProjectSpace from database
	 * 
	 * @return GenericSelectModel<PnProjectSpace>
	 */
	public GenericSelectModel<PnPerson> getPersonModel() {
		return personSpaceBean;
	}

	/**
	 * Method for getting allocation data by businessid and projectid
	 * 
	 * @param businessId
	 * @param projectId
	 */
	private void getAllocationDetails() {
		String startDateString = "01/" + (monthList.indexOf(getMonth()) + 1) + "/" + getYear();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
		monthIndex = monthList.indexOf(month);

		Date startDate = null;
		Calendar cal = Calendar.getInstance();
		Date endDate = null;
		//date parameter for default resources
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
			log.error("Invalid date Exception in AllocateByProject " + e.getMessage());
		}
		Integer businessId = null;
		if (getBusiness() != null && getBusiness().getID().equals("0")) {
			businessId = null;
		}else if(getBusiness() != null){
			Integer.parseInt(getBusiness().getID());
		}

		IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
		List<ReportProjectUsers> defaultResources = pnPersonService.getAssignedResourcesByProject(getProject().getProjectId(), dateFrom,
				dateTo);

		IPnPersonAllocationService pnPersonAllocationService = ServiceFactory.getInstance()
				.getPnPersonAllocationService();
		List<ReportProjectUsers> allocationDetails = pnPersonAllocationService.getResourceAllocationEntryByProject(
				businessId, getProject().getProjectId(), startDate, endDate);
		NumberFormat numberFormat = NumberFormat.getInstance();

		float[] columnTotal = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };
		int index = -1;
		comboListRecord = "";
		projectAllocationData = "";
		projectAllocationData = "[ ";
		if (allocationDetails != null && allocationDetails.size() > 0) {
			for (ReportProjectUsers reportProjectUser : allocationDetails) {
				index = -1;
				if (defaultResources != null && defaultResources.size() > 0) {
					int foundIndex = defaultResources.indexOf(new ReportProjectUsers(reportProjectUser.getUserId(),
							reportProjectUser.getFirstName(), reportProjectUser.getLastName()));
					if (foundIndex > -1) {
						defaultResources.remove(foundIndex);
					}
				}
				comboListRecord += "$" + reportProjectUser.getUserId() + "$";
				projectAllocationData += " [ " + reportProjectUser.getUserId() + ", ";
				projectAllocationData += " '" + reportProjectUser.getFirstName().replaceAll("'", "&acute;") + " "
						+ reportProjectUser.getLastName().replaceAll("'", "&acute;") + "', ";
				projectAllocationData += " '0' ";
				List<ReportMonth> monthList = reportProjectUser.getMonthList();
				IUtilService utilService = ServiceFactory.getInstance().getUtilService();
				Calendar calSatrtDate = Calendar.getInstance();
				Calendar calEndDate = Calendar.getInstance();
				calSatrtDate.setTime(startDate);

				for (ReportMonth reportMonth : monthList) {
					calEndDate = (Calendar) calSatrtDate.clone();
					calEndDate.set(Calendar.DAY_OF_MONTH, calEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
					if (reportMonth.getAllocation() != null) {
						projectAllocationData += " , " + reportMonth.getAllocationId();
						projectAllocationData += " , "
								+ numberFormat.formatPercent((reportMonth.getAllocation().floatValue() * 100.0)
										/ (utilService.getNumberOfWorkingDays(calSatrtDate.getTime(), calEndDate
												.getTime()) * 8.0), "###.##");
						columnTotal[++index] += (reportMonth.getAllocation().floatValue() * 100.0)
								/ (utilService.getNumberOfWorkingDays(calSatrtDate.getTime(), calEndDate.getTime()) * 8.0);
					} else {
						projectAllocationData += " , 0 ";
						projectAllocationData += " , " + reportMonth.getAllocation();
						++index;
					}
					calSatrtDate.add(Calendar.MONTH, 1);
				}
				projectAllocationData += " ], ";
			}

		}
		if (defaultResources != null && defaultResources.size() > 0) {
			for (ReportProjectUsers person : defaultResources) {
				comboListRecord += "$" + person.getUserId() + "$";
				projectAllocationData += "[" + person.getUserId() + ",'"
						+ person.getFirstName().replaceAll("'", "&acute;") + " "
						+ person.getLastName().replaceAll("'", "&acute;")
						+ "','','0','','0','','0','','0','','0','','0','','0','','0',''],";
			}

		}
		if ((allocationDetails != null && allocationDetails.size() > 0)
				|| (defaultResources != null && defaultResources.size() > 0)) {
			projectAllocationData += "['0','" + gridTotalRowLabel + "','','',"
					+ numberFormat.formatPercent(columnTotal[0], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[1], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[2], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[3], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[4], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[5], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[6], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[7], "###.##") + "]";
		}
		projectAllocationData += "]";
		if (projectAllocationData.equals("[ ]")) {
			message = reservationNotFoundMessage;
		} else {
			message = null;
		}
	}

	/**
	 * Method for action from view button
	 * 
	 * @return
	 */
	public Object onAction() {
		if (getMonth() != null) {
			setMonthIndex(monthList.indexOf(getMonth()));
		}
		if(getProject() == null){
			setProject(new PnProjectSpace(projectsList.get(0).getProjectId(), projectsList.get(0).getProjectName()));
		}
		getAllocationDetails();
		return allocateByProject;
	}

	/**
	 * Method called on ajax request
	 * 
	 * @param param
	 * @param flag
	 * @return
	 */
	public TextStreamResponse onActivate(String param, String flag) {
		if (flag.equalsIgnoreCase("ForFetchDataByMonths")) {
			String[] params = param.split("&");
			if (params != null && params.length > 3) {
				setMonth(params[0]);
				setYear(params[1]);
				setBusiness(new BusinessSpace(params[3]));
				setProject(new PnProjectSpace(Integer.parseInt(params[2]),""));
				getAllocationDetails();
			}
			return new TextStreamResponse("text/plain", projectAllocationData + "|&|" + comboListRecord + "|&|" + getProject().getProjectId());
		} else if (flag.equalsIgnoreCase("ForFetchProjectByBusiness")) {
			return new TextStreamResponse("text/html", getProjectByBusiness(param));
		} else if (flag.equalsIgnoreCase("ForSaveAllocations")) {
			saveAllcationsDetail(getRequest().getParameter("allocationArray"));
			return new TextStreamResponse("text/plain", projectAllocationData+ "|&|" + comboListRecord+ "|&|" 
					+ (getBusiness() == null ? "0" : getBusiness().getID())+ "|&|" +getProject().getProjectId() + "|&|" 
					+ getMonth()+ "|&|" +getYear()+ "|&|" + getProjectByBusiness((getBusiness() == null ? "0" : getBusiness().getID())));
		} else {
			return null;
		}
	}

	/**
	 * Method to get projects on selection of business
	 * 
	 * @param businessId
	 * @return
	 */
	private String getProjectByBusiness(String businessId) {
		String scriptSelect = "<select name='project' id='project'>";
		if (projectsList != null)
			projectsList.clear();
		if (!businessId.equals("0")) {
			projectsList.addAll(pnProjectSpaceService.getProjectsByBusinessId(Integer.parseInt(businessId)));
		} else {
			projectsList.addAll(pnProjectSpaceService.getProjectsByUserId(Integer
					.parseInt(net.project.security.SessionManager.getUser().getID())));
		}
		// Generating html for project select tag for selected business
		if (projectsList.size() > 0) {
			for (PnProjectSpace pnProjectspace : projectsList) {
				scriptSelect += "<option value=\"" + pnProjectspace.getProjectId() + "\">";
				scriptSelect += pnProjectspace.getProjectName();
				scriptSelect += "</option>";
			}
		} else
			scriptSelect += "<option value='0'>" + noProjectsFoundOption + "</option>";
		scriptSelect += "</select>";
		return scriptSelect;
	}

	/**
	 * Method to save or update given allocations on submit
	 * 
	 * @param param
	 */
	private void saveAllcationsDetail(String allocatedData) {
		List<PnPersonAllocation> allocations = new ArrayList<PnPersonAllocation>();
		IPnPersonAllocationService allocationService = ServiceFactory.getInstance().getPnPersonAllocationService();
		IUtilService utilService = ServiceFactory.getInstance().getUtilService();

		String startDateString = "01/" + (getMonthIndex() + 1) + "/" + getYear();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		Date saveFrom = null;
		Date startDate = null;
		Date endDate = null;
		Date allocationDate = null;
		try {
			saveFrom = dateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Invalid start date Exception in AllocateByProject " + e.getMessage());
		}
		try {
			JSONArray jsArray = new JSONArray(allocatedData);
			for (int arrayIndex = 0; arrayIndex < jsArray.length(); arrayIndex++) {
				cal.setTime(saveFrom);
				JSONObject object = jsArray.getJSONObject(arrayIndex);
				String monthValue = object.get("monthValues").toString();
				String allocationIdValue = object.getString("allocationIds").toString();
				allocationIdValue = allocationIdValue.substring(1, allocationIdValue.length() - 1);
				monthValue = monthValue.substring(1, monthValue.length() - 1);
				String[] monthsAllocation = monthValue.split(",");
				String[] allocationIds = allocationIdValue.split(",");
				for (int index = 0; index < monthsAllocation.length; index++) {
					if ((monthsAllocation[index] != null && !monthsAllocation[index].equals("null") && !monthsAllocation[index]
							.equals(""))
							|| !allocationIds[index].equals("0")) {
						if (monthsAllocation[index] == null || monthsAllocation[index].equals("null")
								|| monthsAllocation[index].equals("")) {
							monthsAllocation[index] = "0";
						}

						startDate = cal.getTime();
						cal.add(Calendar.DATE, 14);
						allocationDate =  cal.getTime();
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						endDate = cal.getTime();
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

						PnPersonAllocationPK allocationPK = new PnPersonAllocationPK();
						allocationPK.setAllocationId(Integer.parseInt(allocationIds[index]));
						allocationPK.setSpaceId(getProject().getProjectId());
						allocationPK.setPersonId(Integer.parseInt(object.getString("resourceId")));
						PnPersonAllocation allocation = new PnPersonAllocation();
						allocation.setComp_id(allocationPK);
						allocation.setPnPerson(new PnPerson((Integer) object.get("resourceId")));
						allocation.setHoursAllocated(BigDecimal.valueOf((Float.parseFloat(monthsAllocation[index])
								* utilService.getNumberOfWorkingDays(startDate, endDate) * 8.0) / 100));
						allocation.setAllocationDate(allocationDate);
						allocations.add(allocation);
					}
					cal.add(Calendar.MONTH, 1);
				}
				try {
					allocationService.saveResourceAllocations(allocations);
				} catch (Exception e) {
					log.error("Error while saving allocations in AllocateByProject " + e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error("Error while parsing JSON object in AllocateByProject " + e.getMessage());
			e.printStackTrace();
		}
		getAllocationDetails();
	}

	@CleanupRender
	void cleanValues() {
		message = null;
	}

	public List getMonthsModel() {
		return monthList;
	}

	public List getYearsModel() {
		return yearList;
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

	public PnProjectSpace getProject() {
		return project;
	}

	public void setProject(PnProjectSpace project) {
		this.project = project;
	}

	public PnPerson getPerson() {
		return person;
	}

	public void setPerson(PnPerson person) {
		this.person = person;
	}

	public String getGridTitle() {
		return gridTitle;
	}

	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
	}

	public String getUntilEndOfPorjectColumnLabel() {
		return untilEndOfPorjectColumnLabel;
	}

	public String getViewButtonCaption() {
		return viewButtonCaption;
	}

	public int getMonthIndex() {
		return monthIndex;
	}

	public void setMonthIndex(int monthIndex) {
		this.monthIndex = monthIndex;
	}

	public String getAddResourceTooltipTitle() {
		return addResourceTooltipTitle;
	}

	public String getClickToAddResourceTooltip() {
		return clickToAddResourceTooltip;
	}

	public String getClickToEditTooltip() {
		return clickToEditTooltip;
	}

	public String getSaveAllocationsTooltip() {
		return saveAllocationsTooltip;
	}

	public String getSaveAllocationsTooltipTitle() {
		return saveAllocationsTooltipTitle;
	}

	public String getSelectResourceToAddMessage() {
		return selectResourceToAddMessage;
	}

	public String getUntilEndOfProjectColumnTooltip() {
		return untilEndOfProjectColumnTooltip;
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
	 * @return the projectAllocationData
	 */
	public String getProjectAllocationData() {
		return projectAllocationData;
	}

	/**
	 * @param projectAllocationData
	 *            the projectAllocationData to set
	 */
	public void setProjectAllocationData(String projectAllocationData) {
		this.projectAllocationData = projectAllocationData;
	}

	public String getComboListRecord() {
		return comboListRecord;
	}

	public void setComboListRecord(String comboListRecord) {
		this.comboListRecord = comboListRecord;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the noResourcesToViewAllocationMessage
	 */
	public String getNoResourcesToViewAllocationMessage() {
		return noResourcesToViewAllocationMessage;
	}

	/**
	 * @param noResourcesToViewAllocationMessage
	 *            the noResourcesToViewAllocationMessage to set
	 */
	public void setNoResourcesToViewAllocationMessage(String noResourcesToViewAllocationMessage) {
		this.noResourcesToViewAllocationMessage = noResourcesToViewAllocationMessage;
	}

	/**
	 * @return the addButtonCaption
	 */
	public String getAddButtonCaption() {
		return addButtonCaption;
	}

	/**
	 * @return the allOption
	 */
	public String getAllOption() {
		return allOption;
	}

	/**
	 * @return the confirmIgnoreChangesTitle
	 */
	public String getConfirmIgnoreChangesTitle() {
		return confirmIgnoreChangesTitle;
	}

	/**
	 * @return the extAlertTitle
	 */
	public String getExtAlertTitle() {
		return extAlertTitle;
	}

	/**
	 * @return the ignorChangesMessage
	 */
	public String getIgnoreChangesMessage() {
		return ignoreChangesMessage;
	}

	/**
	 * @return the noBusinessFoundOption
	 */
	public String getNoBusinessFoundOption() {
		return noBusinessFoundOption;
	}

	/**
	 * @return the noProjectsFoundOption
	 */
	public String getNoProjectsFoundOption() {
		return noProjectsFoundOption;
	}

	/**
	 * @return the noRecordToSaveMessage
	 */
	public String getNoRecordToSaveMessage() {
		return noRecordToSaveMessage;
	}

	/**
	 * @return the noResourcesFoundOption
	 */
	public String getNoResourcesFoundOption() {
		return noResourcesFoundOption;
	}

	/**
	 * @return the selectResourceOption
	 */
	public String getSelectResourceOption() {
		return selectResourceOption;
	}

	/**
	 * @return the submitButtonCaption
	 */
	public String getSubmitButtonCaption() {
		return submitButtonCaption;
	}

	/**
	 * @return the allocationNotMoreThan100PercentMessage
	 */
	public String getAllocationNotMoreThan100PercentMessage() {
		return allocationNotMoreThan100PercentMessage;
	}

	/**
	 * @return the allocationZeroPercentMessage
	 */
	public String getAllocationZeroPercentMessage() {
		return allocationZeroPercentMessage;
	}

	/**
	 * @return the gridTotalRowLabel
	 */
	public String getGridTotalRowLabel() {
		return gridTotalRowLabel;
	}

	/**
	 * @return the projectNotExistTotAllocateMessage
	 */
	public String getProjectNotExistToAllocateMessage() {
		return projectNotExistToAllocateMessage;
	}

	/**
	 * @return the resourceAlreadyAddedMessage
	 */
	public String getResourceAlreadyAddedMessage() {
		return resourceAlreadyAddedMessage;
	}

	/**
	 * @return the reservationNotFoundMessage
	 */
	public String getReservationNotFoundMessage() {
		return reservationNotFoundMessage;
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
