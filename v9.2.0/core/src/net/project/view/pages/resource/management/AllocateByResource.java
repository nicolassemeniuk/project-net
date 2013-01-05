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
import net.project.hibernate.model.resource_reports.ReportUserProjects;
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
public class AllocateByResource extends BasePage{

	private static Logger log;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	private GenericSelectModel<PnPerson> personSpaceBean;

	private GenericSelectModel<PnProjectSpace> pnProjectSpaceBeans;

	@Persist
	private String projectColumnLabel;

	@Persist
	private String untilEndOfProjectColumnLabel;

	@Persist
	private String viewButtonCaption;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String gridTitle;

	@Persist
	private String saveAllocationsTooltipTitle;

	@Persist
	private String saveAllocationsTooltip;

	@Persist
	private String untilEndOfProjectColumnTooltip;

	@Persist
	private String selectProjectToAddMessage;

	@Persist
	private String clickToEditTooltip;

	@Persist
	private String addProjectTooltipTitle;

	@Persist
	private String clickToAddProjectTooltip;

	@Persist
	private String allocationNotMoreThan100percentMessage;

	@Persist
	private String allocationZeroPercentMessage;

	@Persist
	private String projectAlreadyAddedMessage;

	@Persist
	private String addButtonCaption;

	@Persist
	private String submitButtonCaption;

	@Persist
	private String extAlertTitle;

	@Persist
	private String noRecordToSaveMessage;

	@Persist
	private String resourceNotExistToAllocateMessage;

	@Persist
	private String ignoreChangesMessage;

	@Persist
	private String noResourcesToViewAllocationMessage;

	@Persist
	private String allOption;

	@Persist
	private String noBusinessFoundOption;

	@Persist
	private String selectProjectOption;

	@Persist
	private String noResourcesFoundOption;

	@Persist
	private String noProjectsFoundOption;

	@Persist
	private String confirmIgnoreChangesTitle;

	@Persist
	private String gridTotalRowLabel;

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
	private PnPerson person;

	@Persist
	private PnProjectSpace project;

	@Inject
	private PropertyAccess access;

	@Persist
	private String month;

	@Persist
	private String year;

	@Persist
	private int monthIndex;

	@Persist
	private String comboListRecord;

	@InjectPage
	private AllocateByResource allocateByResource;

	private BusinessSpaceFinder bFinder;

	private IPnPersonService pnPersonService;

	private IPnProjectSpaceService pnProjectSpaceService;

	private List<BusinessSpace> businessesList;

	private List<Integer> businessIds;

	private List<PnPerson> pnPersonsList;

	private List<PnProjectSpace> projectsList;

	private List<String> monthList;

	private List<String> yearList;

	private BusinessSpace firstBusinessSpace;

	private PnPerson firstpersonSpace;

	private PnProjectSpace firstPnProjectSpace;

	@Persist
	private String resourceAllocationData;

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
			
			projectColumnLabel = PropertyProvider.get("prm.resource.allocatebyresource.column.project.label");
			untilEndOfProjectColumnLabel = PropertyProvider
					.get("prm.resource.allocatebyresource.column.untilendofproject.label");
			viewButtonCaption = PropertyProvider.get("prm.resource.allocatebyresource.view.button.caption");
			serverRequestFailedMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.serverrequestfailed.message");
			gridTitle = PropertyProvider.get("prm.resource.allocatebyresource.gridtitle");
			saveAllocationsTooltipTitle = PropertyProvider
					.get("prm.resource.allocatebyresource.saveallocations.tooltip.title");
			saveAllocationsTooltip = PropertyProvider.get("prm.resource.allocatebyresource.saveallocations.tooltip");
			untilEndOfProjectColumnTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.untilendofproject.column.tooltip");
			selectProjectToAddMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.selectprojecttoadd.message");
			clickToEditTooltip = PropertyProvider.get("prm.resource.allocatebyresource.clicktoedit.tooltip");
			addProjectTooltipTitle = PropertyProvider.get("prm.resource.allocatebyresource.addproject.tooltip.title");
			clickToAddProjectTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.clicktoaddproject.tooltip");
			allocationNotMoreThan100percentMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.allocationmorethanhundredpercent.message");
			allocationZeroPercentMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.allocationzeropercent.message");
			projectAlreadyAddedMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.projectalreadyadded.message");
			addButtonCaption = PropertyProvider.get("prm.resource.allocatebyresource.grid.addbutton.caption");
			submitButtonCaption = PropertyProvider.get("prm.resource.allocatebyresource.grid.submitbutton.caption");
			extAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			noRecordToSaveMessage = PropertyProvider.get("prm.resource.allocatebyresource.grid.norecordtosave.message");
			resourceNotExistToAllocateMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.grid.resourcenotexisttoallocate.message");
			ignoreChangesMessage = PropertyProvider.get("prm.resource.allocatebyresource.ignorethechanges.message");
			noResourcesToViewAllocationMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.businessdonthaveresourcestoviewallocation.message");
			allOption = PropertyProvider.get("prm.resource.allocatebyresource.businessdropdownlist.alloptionvalue");
			noBusinessFoundOption = PropertyProvider
					.get("prm.resource.allocatebyresource.businessdropdownlist.nobusinessfound.message");
			selectProjectOption = PropertyProvider
					.get("prm.resource.allocatebyresource.projectdropdownlist.selectprojectoptionvalue");
			noResourcesFoundOption = PropertyProvider
					.get("prm.resource.allocatebyresource.resourcedropdownlist.noresourcesfound.message");
			noProjectsFoundOption = PropertyProvider
					.get("prm.resource.allocatebyresource.projectdropdownlist.noprojectsfound.message");
			confirmIgnoreChangesTitle = PropertyProvider
					.get("prm.resource.allocatebyresource.confirmignorechanges.title");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			reservationNotFoundMessage = PropertyProvider
					.get("prm.resource.allocatebyresource.projectreservationsnotfoundinthisperiod.message");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.allocatebyresource.gothreemonthsforwardimage.tooltip");
            preReleaseEnabled = PropertyProvider.getBoolean("prm.resource.prereleasefunctionality.label.isenabled");
			
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		} catch (Exception ex) {
			log.error("Error occured while getting AllocateByResource page property values.");
		}
    }
        
    public AllocateByResource(){
        log = Logger.getLogger(AllocateByResource.class);
        
        businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();
		businessIds = new ArrayList<Integer>();

		pnPersonsList = new ArrayList<PnPerson>();
		pnPersonService = ServiceFactory.getInstance().getPnPersonService();

		projectsList = new ArrayList<PnProjectSpace>();
		pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();

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
		DateFormat dateFormat = new DateFormat(SessionManager.getUser());
		if (getMonth() == null || getYear() == null) {
			month = dateFormat.formatDate(new Date(), "MMM yyyy").split(" ")[0];
			year = dateFormat.formatDate(new Date(), "MMM yyyy").split(" ")[1];
		}

		// Getting database values for business list
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName(allOption);
		if (businessesList != null) {
			businessesList.clear();
			businessIds.clear();
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

		for (BusinessSpace business : businessesList) {
			businessIds.add(Integer.parseInt(business.getID()));
		}

		if (pnPersonsList != null) {
			pnPersonsList.clear();
		}
		if (projectsList != null) {
			projectsList.clear();
		}
		firstpersonSpace = new PnPerson();
		firstPnProjectSpace = new PnProjectSpace();
		firstPnProjectSpace.setProjectId(0);
		firstPnProjectSpace.setProjectName(selectProjectOption);
		projectsList.add(firstPnProjectSpace);

		if (getBusiness() != null && !getBusiness().getID().equals("0")) {
			pnPersonsList.addAll(pnPersonService.getPersonsByBusinessId(Integer.parseInt(getBusiness().getID())));
			projectsList.addAll(pnProjectSpaceService.getProjectsByBusinessId(Integer.parseInt(getBusiness().getID())));
		} else {
			pnPersonsList.addAll(pnPersonService.getUniqueMembersOfBusinessCollection(businessIds));
			projectsList.addAll(pnProjectSpaceService.getProjectsByUserId(Integer
					.parseInt(net.project.security.SessionManager.getUser().getID())));
		}
		if (pnPersonsList.size() == 0) {
			firstpersonSpace.setPersonId(0);
			firstpersonSpace.setDisplayName(noResourcesFoundOption);
			pnPersonsList.add(firstpersonSpace);
		}
		if (projectsList.size() == 1) {
			projectsList.clear();
			firstPnProjectSpace.setProjectId(0);
			firstPnProjectSpace.setProjectName(noProjectsFoundOption);
			projectsList.add(firstPnProjectSpace);
		} else {
			for(PnProjectSpace projectSpace : projectsList) {
				projectSpace.setProjectName(HTMLUtils.escape(projectSpace.getProjectName()).replaceAll("'", "&acute;"));
			}
		}

		personSpaceBean = new GenericSelectModel<PnPerson>(pnPersonsList, PnPerson.class, "displayName", "personId",
				access);
		pnProjectSpaceBeans = new GenericSelectModel<PnProjectSpace>(projectsList, PnProjectSpace.class, "projectName",
				"projectId", access);

		if (getPerson() == null) {
		 	setPerson(new PnPerson(pnPersonsList.get(0).getPersonId()));
		}else if(pnPersonsList.indexOf(getPerson()) < 0 ){
			setPerson(new PnPerson(pnPersonsList.get(0).getPersonId()));
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
	public GenericSelectModel<PnPerson> getPersonModel() {
		return personSpaceBean;
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
	 * Method to get allocation details by businessid and personid
	 * 
	 * @param businessId
	 * @param personId
	 */
	private void getAllocationDetails() {
		String startDateString = "01/" + (monthList.indexOf(getMonth()) + 1) + "/" + getYear();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
		monthIndex = monthList.indexOf(month);

		Date startDate = null;
		Calendar cal = Calendar.getInstance();
		Date endDate = null;
		//date parameter for default projects
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
			log.error("Invalid date Exception in AllocateByResource " + e.getMessage());
		}

		Integer businessId = null;
		if (getBusiness() != null && getBusiness().getID().equals("0")) {
			businessId = null;
		}else if(getBusiness() != null){
			businessId = Integer.parseInt(getBusiness().getID());
		}

		IPnProjectSpaceService pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		List<PnProjectSpace> defaultProjects = pnProjectSpaceService.getAssignedProjectsByResource(businessId,
				getPerson().getPersonId(), dateFrom, dateTo);

		List<ReportMonth> monthList;
		IPnPersonAllocationService pnPersonAllocationService = ServiceFactory.getInstance()
				.getPnPersonAllocationService();
		List<ReportUserProjects> allocationDetails = pnPersonAllocationService.getResourceAllocationEntryByPerson(
				businessId, getPerson().getPersonId(), startDate, endDate);

		NumberFormat numberFormat = NumberFormat.getInstance();
		float[] columnTotal = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };
		int index = -1;
		comboListRecord = "";
		resourceAllocationData = "";
		resourceAllocationData = "[ ";
		if (allocationDetails != null && allocationDetails.size() > 0) {
			for (ReportUserProjects reportProject : allocationDetails) {
				index = -1;
				if (defaultProjects != null && defaultProjects.size() > 0) {
					int foundIndex = defaultProjects.indexOf(new PnProjectSpace(reportProject.getProjectId(),
							reportProject.getProjectName()));

					if (foundIndex > -1) {
						defaultProjects.remove(foundIndex);
					}
				}

				comboListRecord += "$" + reportProject.getProjectId() + "$";
				resourceAllocationData += " [ " + reportProject.getProjectId() + ", ";
				resourceAllocationData += " '" + reportProject.getProjectName().replaceAll("'", "&acute;") + "', ";
				resourceAllocationData += " '0' ";
				monthList = reportProject.getMonthList();
				IUtilService utilService = ServiceFactory.getInstance().getUtilService();
				Calendar calSatrtDate = Calendar.getInstance();
				Calendar calEndDate = Calendar.getInstance();
				calSatrtDate.setTime(startDate);
				for (ReportMonth reportMonth : monthList) {
					calEndDate = (Calendar) calSatrtDate.clone();
					calEndDate.set(Calendar.DAY_OF_MONTH, calEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));
					if (reportMonth.getAllocationId() != null) {
						resourceAllocationData += " , " + reportMonth.getAllocationId();
						resourceAllocationData += " , "
								+ numberFormat.formatPercent((reportMonth.getAllocation().floatValue() * 100.0)
										/ (utilService.getNumberOfWorkingDays(calSatrtDate.getTime(), calEndDate
												.getTime()) * 8.0), "###.##");
						columnTotal[++index] += (reportMonth.getAllocation().floatValue() * 100.0)
								/ (utilService.getNumberOfWorkingDays(calSatrtDate.getTime(), calEndDate.getTime()) * 8.0);
					} else {
						resourceAllocationData += " , 0 ";
						resourceAllocationData += " , " + reportMonth.getAllocation();
						++index;
					}
					calSatrtDate.add(Calendar.MONTH, 1);
				}
				resourceAllocationData += " ], ";
			}
		}
		if (defaultProjects != null && defaultProjects.size() > 0) {
			for (PnProjectSpace projectSpace : defaultProjects) {
				comboListRecord += "$" + projectSpace.getProjectId() + "$";
				resourceAllocationData += "[" + projectSpace.getProjectId() + ",'"
						+ projectSpace.getProjectName().replaceAll("'", "&acute;")
						+ "','','0','','0','','0','','0','','0','','0','','0','','0',''],";
			}
		}
		if ((allocationDetails != null && allocationDetails.size() > 0)
				|| (defaultProjects != null && defaultProjects.size() > 0)) {
			resourceAllocationData += "['0','" + gridTotalRowLabel + "','','',"
					+ numberFormat.formatPercent(columnTotal[0], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[1], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[2], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[3], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[4], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[5], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[6], "###.##") + ",'',"
					+ numberFormat.formatPercent(columnTotal[7], "###.##") + "]";
		}

		resourceAllocationData += "]";

		if (resourceAllocationData.equals("[ ]")) {
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
		if(getPerson() == null){
			setPerson(new PnPerson(pnPersonsList.get(0).getPersonId()));
		}
		getAllocationDetails();
		return allocateByResource;
	}

	@CleanupRender
	void cleanValues() {
		message = null;
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
				setPerson(new PnPerson(Integer.parseInt(params[2])));
				getAllocationDetails();
			}
			return new TextStreamResponse("text/plain", resourceAllocationData + "|&|" + comboListRecord + "|&|" + getPerson().getPersonId());
		} else if (flag.equalsIgnoreCase("ForFetchPersonsByBusiness")) {
			return new TextStreamResponse("text/html", getPersonsByBusiness(param));
		} else  if (flag.equalsIgnoreCase("ForSaveAllocations")) {
			saveAllcationsDetail(getRequest().getParameter("allocationArray"));
			return new TextStreamResponse("text/plain", resourceAllocationData + "|&|" + comboListRecord + "|&|" 
					+ (getBusiness() == null ? "0" : getBusiness().getID())+ "|&|" + getPerson().getPersonId()
					+ "|&|" + getMonth()+ "|&|" + getYear() + "|&|" + getPersonsByBusiness(getBusiness() == null ? "0" : getBusiness().getID()));
		}else{
			return null;
		}
	}

	/**
	 * Method to get persons on selection of business
	 * 
	 * @param businessId
	 * @return
	 */
	private String getPersonsByBusiness(String businessId) {
		String scriptSelect = "<select name='person' id='person'>";
		if (pnPersonsList != null) {
			pnPersonsList.clear();
		}
		if (!businessId.equals("0")) {
			pnPersonsList.addAll(pnPersonService.getPersonsByBusinessId(Integer.parseInt(businessId)));
		} else {
			pnPersonsList.addAll(pnPersonService.getUniqueMembersOfBusinessCollection(businessIds));
		}

		// Generating html for person select tag for selected business
		if (pnPersonsList.size() > 0) {
			for (PnPerson pnPerson : pnPersonsList) {
				scriptSelect += "<option value=\"" + pnPerson.getPersonId() + "\">";
				scriptSelect += pnPerson.getDisplayName();
				scriptSelect += "</option>";
			}
		} else {
			scriptSelect += "<option value='0'>" + noResourcesFoundOption + "</option>";
		}
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
		String startDateString = "1/" + (getMonthIndex() + 1) + "/" + getYear();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		Date saveFrom = null; 
		Date startDate = null;
		Date endDate = null;
		Date allocationDate = null;
		try {
			saveFrom = dateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Invalid date Exception in AllocateByResource " + e.getMessage());
		}
		
		try {
			JSONArray jsArray = new JSONArray(allocatedData);
			for (int arrayIndex = 0; arrayIndex < jsArray.length(); arrayIndex++) {
				cal.setTime(saveFrom);
				JSONObject object = jsArray.getJSONObject(arrayIndex);
				String allocationIdValue = object.getString("allocationIds").toString();
				String monthValue = object.get("monthValues").toString();
				allocationIdValue = allocationIdValue.substring(1, allocationIdValue.length() - 1);
				monthValue = monthValue.substring(1, monthValue.length() - 1);
				String[] allocationIds = allocationIdValue.split(",");
				String[] monthsAllocation = monthValue.split(",");
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
						allocationPK.setSpaceId(Integer.parseInt(object.getString("projectId")));
						allocationPK.setPersonId(getPerson().getPersonId());
						allocationPK.setAllocationId(Integer.parseInt(allocationIds[index]));
						PnPersonAllocation allocation = new PnPersonAllocation();
						allocation.setComp_id(allocationPK);
						allocation.setPnPerson(getPerson());
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
					log.error("Error while saving allocations in AllocateByResource " + e.getMessage());
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			log.error("Error while parsing Jason object  in AllocateByResource " + e.getMessage());
			e.printStackTrace();
		}
		getAllocationDetails();
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

	public BusinessSpace getBusiness() {
		return business;
	}

	public void setBusiness(BusinessSpace business) {
		this.business = business;
	}

	public PnPerson getPerson() {
		return person;
	}

	public void setPerson(PnPerson person) {
		this.person = person;
	}

	public PnProjectSpace getProject() {
		return project;
	}

	public void setProject(PnProjectSpace project) {
		this.project = project;
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

	public String getGridTitle() {
		return gridTitle;
	}

	public String getProjectColumnLabel() {
		return projectColumnLabel;
	}

	public String getUntilEndOfProjectColumnLabel() {
		return untilEndOfProjectColumnLabel;
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

	/**
	 * @return the resourceAllocationData
	 */
	public String getResourceAllocationData() {
		return resourceAllocationData;
	}

	/**
	 * @param resourceAllocationData
	 *            the resourceAllocationData to set
	 */
	public void setResourceAllocationData(String resourceAllocationData) {
		this.resourceAllocationData = resourceAllocationData;
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
	 * @return the addProjectTooltipTitle
	 */
	public String getAddProjectTooltipTitle() {
		return addProjectTooltipTitle;
	}

	/**
	 * @return the clickToAddProjectTooltip
	 */
	public String getClickToAddProjectTooltip() {
		return clickToAddProjectTooltip;
	}

	/**
	 * @return the clickToEditTooltip
	 */
	public String getClickToEditTooltip() {
		return clickToEditTooltip;
	}

	/**
	 * @return the saveAllocationsTooltip
	 */
	public String getSaveAllocationsTooltip() {
		return saveAllocationsTooltip;
	}

	/**
	 * @return the saveAllocationsTooltipTitle
	 */
	public String getSaveAllocationsTooltipTitle() {
		return saveAllocationsTooltipTitle;
	}

	/**
	 * @return the selectProjectToAddMessage
	 */
	public String getSelectProjectToAddMessage() {
		return selectProjectToAddMessage;
	}

	/**
	 * @return the untilEndOfProjectColumnTooltip
	 */
	public String getUntilEndOfProjectColumnTooltip() {
		return untilEndOfProjectColumnTooltip;
	}

	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
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
	 * @return the addButtonCaption
	 */
	public String getAddButtonCaption() {
		return addButtonCaption;
	}

	/**
	 * @return the allocationNotMoreThan100percentMessage
	 */
	public String getAllocationNotMoreThan100percentMessage() {
		return allocationNotMoreThan100percentMessage;
	}

	/**
	 * @return the allocationZeroPercentMessage
	 */
	public String getAllocationZeroPercentMessage() {
		return allocationZeroPercentMessage;
	}

	/**
	 * @return the ignoretheChangesMessage
	 */
	public String getIgnoreChangesMessage() {
		return ignoreChangesMessage;
	}

	/**
	 * @return the noRecordToSaveMessage
	 */
	public String getNoRecordToSaveMessage() {
		return noRecordToSaveMessage;
	}

	/**
	 * @return the noResourcesToViewAllocationMessage
	 */
	public String getNoResourcesToViewAllocationMessage() {
		return noResourcesToViewAllocationMessage;
	}

	/**
	 * @return the projectAlreadyAddedMessage
	 */
	public String getProjectAlreadyAddedMessage() {
		return projectAlreadyAddedMessage;
	}

	/**
	 * @return the resourceNotExistTotAllocateMessage
	 */
	public String getResourceNotExistToAllocateMessage() {
		return resourceNotExistToAllocateMessage;
	}

	/**
	 * @return the submitButtonCaption
	 */
	public String getSubmitButtonCaption() {
		return submitButtonCaption;
	}

	/**
	 * @return the extAlertTitle
	 */
	public String getExtAlertTitle() {
		return extAlertTitle;
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
	 * @return the gridTotalRowLabel
	 */
	public String getGridTotalRowLabel() {
		return gridTotalRowLabel;
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
	 * @return the noResourcesFoundOption
	 */
	public String getNoResourcesFoundOption() {
		return noResourcesFoundOption;
	}

	/**
	 * @return the selectProjectOption
	 */
	public String getSelectProjectOption() {
		return selectProjectOption;
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
