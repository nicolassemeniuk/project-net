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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.PnAssignmentServiceImpl;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.Version;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * @author
 */
public class ViewDetails {

	private static Logger log;

	private SimpleDateFormat simpleDateFormat;

	private DateFormat userDateFormat;

	@ApplicationState
	private String jSPRootURL;

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
	private String searchMessage;

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
	private String extAlertTitle;

	@Persist
	private String serverRequestFailedMessage;

	@Persist
	private String fromDateInvalidMessage;

	@Persist
	private String toDateInvalidMessage;

	@Persist
	private String dateFieldBlankMessage;

	@Persist
	private String InvalidDateRangeMessage;

	@Persist
	private String allOption;

	@Persist
	private String noBusinessFoundOption;

	@Persist
	private String noAssignmentMessage;

	@Persist
	private String gridTotalRowLabel;

	@Persist
	private String goOneMonthBackwardImageTooltip;

	@Persist
	private String goThreeMonthsBackwardImageTooltip;

	@Persist
	private String goOneMonthForwardImageTooltip;

	@Persist
	private String goThreeMonthsForwardImageTooltip;

	@Persist
	private BusinessSpace resourceList;

	@Inject
	private PropertyAccess access;

	private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	@Persist
	private String fromDate;

	@Persist
	private String toDate;

	private String dateCriteria = "";

	@InjectPage
	private ViewDetails view;

	private BusinessSpaceFinder bFinder;

	private List<BusinessSpace> businessesList;

	private BusinessSpace firstBusinessSpace;

	private List<PnPerson> defaultPersons;
	
	@Persist
	private String versionNumber;
	
	@Persist
	private String inputDateFieldProperMessage;
    
	private WorkingTimeCalendarDefinition  workingTimeCalendarDefinition;
    
    private Map<Integer, IWorkingTimeCalendarProvider> workingTimeCalendarProviderByProjectID = new HashMap <Integer, IWorkingTimeCalendarProvider> ();

    //  Method to initialize page token
    private void initialzePageToken() {
		try {
			viewButtonLabel = PropertyProvider.get("prm.resource.viewdetails.view.button.label");
			manageListSelectResourcesButtonCaption = PropertyProvider
					.get("prm.resource.viewdetails.managelistselectresources.button.label");
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

			// Validation messages retrieving from database
			resourceListSelectionReqMsg = PropertyProvider
					.get("prm.resource.viewdetails.resourcelistselectionrequired.message");
			startDateSelectionReqMsg = PropertyProvider
					.get("prm.resource.viewdetails.startdateselectionrequired.message");
			endDateSelectionReqMsg = PropertyProvider.get("prm.resource.viewdetails.enddateselectionrequired.message");
			searchMessage = PropertyProvider.get("prm.resource.viewdetails.assignmentsearch.message");
			extAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			serverRequestFailedMessage = PropertyProvider.get("prm.resource.viewdetails.serverrequestfailed.message");
			fromDateInvalidMessage = PropertyProvider
					.get("prm.resource.viewdetails.dateenteredforfromdateisinvalid.message");
			toDateInvalidMessage = PropertyProvider
					.get("prm.resource.viewdetails.dateenteredfortodateisinvalid.message");
			dateFieldBlankMessage = PropertyProvider
					.get("prm.resource.viewdetails.fromandtodateshouldnotbeblank.message");
			InvalidDateRangeMessage = PropertyProvider
					.get("prm.resource.viewdetails.fromdatecannotbeaftertodate.message");
			allOption = PropertyProvider.get("prm.resource.viewdetails.businessdropdownlist.alloptionvalue");
			noBusinessFoundOption = PropertyProvider
					.get("prm.resource.viewdetails.businessdropdownlist.nobusinessfound.message");
			noAssignmentMessage = PropertyProvider.get("prm.resource.viewdetails.noassignmentsintimeperiod.message");
			gridTotalRowLabel = PropertyProvider.get("prm.resource.global.gridtotalrow.label");
			goThreeMonthsBackwardImageTooltip = PropertyProvider
					.get("prm.resource.viewdetails.gothreemonthsbackwardimage.tooltip");
			goOneMonthBackwardImageTooltip = PropertyProvider
					.get("prm.resource.viewdetails.goonemonthbackwardimage.tooltip");
			goOneMonthForwardImageTooltip = PropertyProvider
					.get("prm.resource.viewdetails.goonemonthforwardimage.tooltip");
			goThreeMonthsForwardImageTooltip = PropertyProvider
					.get("prm.resource.viewdetails.gothreemonthsforwardimage.tooltip");
			inputDateFieldProperMessage = PropertyProvider
					.get("prm.resource.viewdetails.inputdatefieldinproperformat.message");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
        } catch (Exception ex) {
            log.error("Error occured while getting View Details page property values.");
        }
    }
  
	/**
     * Method for initializing values on every render phase of page
     */
	void onActivate() {
		initialzePageToken();
		
		log = Logger.getLogger(ViewDetails.class);
	       
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        userDateFormat = SessionManager.getUser().getDateFormatter();
        calendarDateFormat = "m/d/yyyy";
        
		message = null;
		businessesList = new ArrayList<BusinessSpace>();
		bFinder = new BusinessSpaceFinder();

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

		if (getFromDate() == null || getToDate() == null) {
			Calendar cal = Calendar.getInstance();
			setFromDate((cal.get(Calendar.MONTH) + 1) + "/01/" + cal.get(Calendar.YEAR));
			setToDate((cal.get(Calendar.MONTH) + 1) + "/" + cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "/"
					+ cal.get(Calendar.YEAR));
		}
		getGridDataByCriteria();
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
	 * Getting assignment details from database
	 * 
	 * @param personsIds
	 *            array of person ids
	 * @param dateFrom
	 * @param dateTo
	 */
	public void getAssignmentDetails(Integer[] personsIds, Date startDate, Date endDate) {

		List<PnAssignment> assignmentDetails = new ArrayList<PnAssignment>();
		IPnAssignmentService pnAssignmentService = ServiceFactory.getInstance().getPnAssignmentService();

		if (personsIds == null) {
			List<PnPerson> pseronsInUserBusinessCollection = new ArrayList<PnPerson>();
			IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
			List<Integer> businessIds = new ArrayList<Integer>();
			for (BusinessSpace business : businessesList) {
				businessIds.add(Integer.parseInt(business.getID()));
			}
			pseronsInUserBusinessCollection = pnPersonService.getUniqueMembersOfBusinessCollection(businessIds);
			defaultPersons = pseronsInUserBusinessCollection;
			if (pseronsInUserBusinessCollection.size() > 0) {
				personsIds = new Integer[pseronsInUserBusinessCollection.size()];
				for (int personIndex = 0; personIndex < pseronsInUserBusinessCollection.size(); personIndex++) {
					PnPerson person = pseronsInUserBusinessCollection.get(personIndex);
					personsIds[personIndex] = person.getPersonId();
				}
			} else {
				personsIds = new Integer[1];
				personsIds[0] = 0;
			}
		}
		// creating data parameter to fetch data
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, -1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, 2);

		assignmentDetails = pnAssignmentService.getAssigmentsList(personsIds, startDate, cal.getTime());

 
		//cacuating actual start date
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		

		gridData = "";
		gridData = "[ ";
		int personId = 0;
		float totals[] = { 0f, 0f, 0f };
		// To calculate distributed working hours only.
		Date assignFrom = null;
		Date assignTo = null;
		
		NumberFormat numberFormat = NumberFormat.getInstance();

		if (CollectionUtils.isNotEmpty(assignmentDetails)) {
			for (PnAssignment assignmentDetailsData : assignmentDetails) {

				IUtilService utilService = ServiceFactory.getInstance().getUtilService();
				try {
					assignFrom = simpleDateFormat.parse(userDateFormat.formatDate(assignmentDetailsData.getStartDate(),
							"MM/dd/yyyy"));
					assignTo = simpleDateFormat.parse(userDateFormat.formatDate(assignmentDetailsData.getEndDate(),
							"MM/dd/yyyy"));
				} catch (Exception e) {
					log.error("Error occured while parsing dates " + e.getMessage());
				}
				if (!(assignTo.before(startDate) || assignFrom.after(endDate))) {//if in selected date criteria
					if (personId != assignmentDetailsData.getPnPerson().getPersonId()) {
						gridData += " [,'"
						        + assignmentDetailsData.getPnPerson().getFirstName().replaceAll("'", "&acute;") + " "	
                                + assignmentDetailsData.getPnPerson().getLastName().replaceAll("'", "&acute;") 
								+ "','Resource Name'," + +assignmentDetailsData.getPnPerson().getPersonId()
								+ ",,,,,,,,],";
						personId = assignmentDetailsData.getPnPerson().getPersonId();
						if (defaultPersons != null && defaultPersons.size() > 0) {
							PnPerson foundPerson = new PnPerson();
							foundPerson.setPersonId(assignmentDetailsData.getPnPerson().getPersonId());
							int foundindex = defaultPersons.indexOf(foundPerson);
							if (foundindex > -1) {
								defaultPersons.remove(foundindex);
							}
						}
					}
					gridData += " [ " + assignmentDetailsData.getPnTask().getTaskId() + ", " + "'"
							+ assignmentDetailsData.getPnTask().getTaskName().replaceAll("'", "&acute;") + "', " + "'"
							+ assignmentDetailsData.getPnProjectSpace().getProjectName().replaceAll("'", "&acute;")
							+ "', " + "'" + assignmentDetailsData.getPnPerson().getPersonId() + "', " + "'"
							+ userDateFormat.formatDate(assignmentDetailsData.getStartDate(), "MM/dd/yyyy") + "', "
							+ "'" + userDateFormat.formatDate(assignmentDetailsData.getEndDate(), "MM/dd/yyyy") + "', ";

					if (assignmentDetailsData.getPnTask().getActualStart() != null) {
						gridData += "'"
								+ userDateFormat.formatDate(assignmentDetailsData.getPnTask().getActualStart(),
										"MM/dd/yyyy") + "', ";
					} else {
						gridData += ", ";
					}
					if (assignmentDetailsData.getPnTask().getActualFinish() != null) {
						gridData += "'"
								+ userDateFormat.formatDate(assignmentDetailsData.getPnTask().getActualFinish(),
										"MM/dd/yyyy") + "', ";
					} else {
						gridData += ", ";
					}

					cal.setTime(startDate);
					cal.add(Calendar.DATE, -1);// before one day of start date
					float notCalulatedWork = 0;
                    
					//initialize working time calendar for poroject and resource.
                    initializeWorkingTimeCalender(assignmentDetailsData.getPnProjectSpace().getProjectId(), assignmentDetailsData.getPnPerson().getPersonId());

					if (assignFrom.before(cal.getTime()) || assignFrom.equals(cal.getTime())) {
						notCalulatedWork = (float) (utilService.getWorkingHours(assignFrom, cal.getTime(), this.workingTimeCalendarDefinition)
                                        * assignmentDetailsData.getPercentAllocated().doubleValue() / 100.0);
					}

					float workignHours = 0;
					if ((startDate.after(assignFrom) || startDate.equals(assignFrom))
							&& (endDate.before(assignTo) || endDate.equals(assignTo))) {
                        workignHours = utilService.getWorkingHours(startDate, endDate, this.workingTimeCalendarDefinition);
					}
					if (startDate.before(assignFrom) && (endDate.before(assignTo) || endDate.equals(assignTo))) {
                        workignHours = utilService.getWorkingHours(assignFrom, endDate, this.workingTimeCalendarDefinition);
					}
					if ((startDate.after(assignFrom) || startDate.equals(assignFrom)) && endDate.after(assignTo)) {
                        workignHours = utilService.getWorkingHours(startDate, assignTo, this.workingTimeCalendarDefinition);
					}
					if (startDate.before(assignFrom) && endDate.after(assignTo)) {
                        workignHours = utilService.getWorkingHours(assignFrom, assignTo, this.workingTimeCalendarDefinition);
					}
					float monthWork = (float) (workignHours
							* assignmentDetailsData.getPercentAllocated().doubleValue() / 100.0);

					if (assignmentDetailsData.getWork() != null) {
						if (assignmentDetailsData.getWork().floatValue() < notCalulatedWork + monthWork) {
							if (notCalulatedWork > assignmentDetailsData.getWork().floatValue()) {
								notCalulatedWork = assignmentDetailsData.getWork().floatValue();
							}
							monthWork = assignmentDetailsData.getWork().floatValue() - notCalulatedWork;
						}
						gridData += numberFormat.formatNumber(monthWork, 0, 2) + ", ";
						totals[0] += monthWork;
					} else {
						monthWork = 0;
						gridData += "0, ";
					}

					float workComplete = 0f;
					if (assignmentDetailsData.getPnTask().getWorkPercentComplete() != null) {
						workComplete = (float) ((monthWork * assignmentDetailsData.getPnTask().getWorkPercentComplete().doubleValue()) / 100.0);
					}

					gridData += numberFormat.formatNumber(workComplete, 0, 2) + ", ";
					totals[1] += workComplete;

					gridData += numberFormat.formatNumber((monthWork - workComplete), 0, 2) + ",";
					totals[2] += monthWork - workComplete;

					gridData += "'" + assignmentDetailsData.getPnTask().getWorkPercentComplete() + "'" + " ],";
				}
			}
		}
		if (defaultPersons != null && defaultPersons.size() > 0) {
			for (PnPerson defaultPerson : defaultPersons) {
				gridData += " [,'" + defaultPerson.getLastName().replaceAll("'", "&acute;") + " "
						+ defaultPerson.getFirstName().replaceAll("'", "&acute;") + "','Resource Name',"
						+ +defaultPerson.getPersonId() + ",,,,,,,,],";
				gridData += "[0,'" + noAssignmentMessage + "',,,,,,,,,,],";
			}
		}
		if (gridData.length() > 2) {
			gridData += "[,'" + gridTotalRowLabel + "',," + personId + ",,,,," + totals[0] + "," + totals[1] + ","
					+ totals[2] + ",] ";
		}
		gridData += "]";
		if (gridData.equals("[ ]")) {
			setMessage(searchMessage);
		}
	}

	/**
	 * Method for getting grid data by criteria
	 * 
	 * @return String of grid data array
	 */
	public String getGridDataByCriteria() {
		List<PnPerson> personsInBusiness = new ArrayList<PnPerson>();
		PnPerson person;
		Integer[] personsIds = null;
		try {
			Date startDate = null;
			Date endDate = null;
			if (getFromDate() != null && getToDate() != null) {
				try {
					startDate = simpleDateFormat.parse(getFromDate());
					endDate = simpleDateFormat.parse(getToDate());
				} catch (Exception e) {
					log.error("Error occured while parsing date strings : " + e.getMessage());
				}
			}
			if (getResourceList() != null && !getResourceList().getID().equals("0")) {
				IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
				personsInBusiness = pnPersonService.getPersonsByBusinessId(Integer.parseInt(getResourceList().getID()));
				defaultPersons = personsInBusiness;
				personsIds = new Integer[personsInBusiness.size()];
				if (personsInBusiness.size() > 0) {
					for (int index = 0; index < personsInBusiness.size(); index++) {
						person = personsInBusiness.get(index);
						personsIds[index] = (person.getPersonId());
					}
				}
				getAssignmentDetails(personsIds, startDate, endDate);
			} else {
				getAssignmentDetails(null, startDate, endDate);
			}
		} catch (Exception e) {
			log.error("Error occured while getting grid data by criteria : " + e.getMessage());
		}
		return gridData;
	}

	/**
	 * Method written which will be called on ajax request
	 * 
	 * @param param
	 *            parameter string
	 * @return TextStreamResponse here grid data string text
	 */
	public TextStreamResponse onActivate(String param) {
		onActivate();
		if (businessesList != null) {
			businessesList.clear();
		}
		try {
			// Getting database values for business list
			businessesList.addAll(bFinder.findByUser(net.project.security.SessionManager.getUser(), "A"));
		} catch (PersistenceException e) {
			log.error("Error occured while generating list values for Businesses model");
		}
		String[] params = null;
		if (param != null)
			params = param.split("&");
		List businessSpace = new ArrayList();
		if (params != null) {
			setFromDate(params[0].replace("-", "/"));
			setToDate(params[1].replace("-", "/"));
			Date date = null;
			try {
				date = simpleDateFormat.parse(getFromDate());
			} catch (Exception e1) {
				log.error("Error occured while parsing dates : " + e1.getMessage());
				return new TextStreamResponse("text/plain", "Invalid1");
			}
			if (date == null || getFromDate().split("/").length < 3) {
				return new TextStreamResponse("text/plain", "Invalid1");
			} else {
				if (getFromDate().split("/")[2].length() < 4)
					return new TextStreamResponse("text/plain", "Invalid1");
			}
			date = null;
			try {
				date = simpleDateFormat.parse(getToDate());
			} catch (Exception e2) {
				log.error("Error occured while parsing dates : " + e2.getMessage());
				return new TextStreamResponse("text/plain", "Invalid2");
			}
			if (date == null || getToDate().split("/").length < 3) {
				return new TextStreamResponse("text/plain", "Invalid2");
			} else {
				if (getToDate().split("/")[2].length() < 4)
					return new TextStreamResponse("text/plain", "Invalid2");
			}
			if (params.length > 2 && !params[2].equals("NotSelected")) {
				try {
					businessSpace = bFinder.findByID(params[2]);
					if (businessSpace != null) {
						setResourceList((BusinessSpace) businessSpace.get(0));
					}
				} catch (PersistenceException pe) {
					log.error("Error occured while finding business by id : " + pe.getMessage());
				}
			} else {
				setResourceList(new BusinessSpace("0"));
			}
		}
		gridData = getGridDataByCriteria();
		return new TextStreamResponse("text/plain", gridData);
	}
    
     /** Initializing WorkingTimeCalendarDefinition as per project and resource.
     * @param spaceID
     * @param resourcePersonID
     */
    private void initializeWorkingTimeCalender(Integer projectId, Integer resourceID){
        //Here the WorkingTimeCalendarDefinition can be initialized multiple time for same project, therefore we are using a map to avoid multiple Initialization.
        //Initialzing ScheduleWorkingTimeCalendarProvider is a very heavy process so if it is already initialized for particular project do not initalize again get it from map. 
        IWorkingTimeCalendarProvider workingTimeCalendarProvider = (IWorkingTimeCalendarProvider)this.workingTimeCalendarProviderByProjectID.get(projectId);
        
        //If ScheduleWorkingTimeCalendarProvider is null, Make it and put it in to the map.   
        if (workingTimeCalendarProvider == null) {
            Schedule schedule = new Schedule();
            schedule.setSpace(new ProjectSpace(projectId.toString()));
            try {
                schedule.loadAll();
                workingTimeCalendarProvider = ScheduleWorkingTimeCalendarProvider.make(schedule);
                this.workingTimeCalendarProviderByProjectID.put(projectId, workingTimeCalendarProvider);
            } catch (Exception pnetEx) {
                Logger.getLogger(PnAssignmentServiceImpl.class).error("Error occured while loading schedule or initializing workingTimeCalendarProvider "+pnetEx.getMessage());
            }
        }
        // get workingTimeCalendarDefinition for resourcePersonID.
        if (workingTimeCalendarProvider != null) {
            this.workingTimeCalendarDefinition = workingTimeCalendarProvider.getForResourceID(resourceID.toString());
        }
    }

	/**
	 * Method for cleaning values
	 */
	@CleanupRender
	void cleanValues() {
		message = null;
		businessesList.clear();
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate
	 *            the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * @param toDate
	 *            the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	 * @return Returns the actualStartColumnLabel.
	 */
	public String getActualStartColumnLabel() {
		return actualStartColumnLabel;
	}

	/**
	 * @return Returns the plannedFinishColumnLabel.
	 */
	public String getPlannedFinishColumnLabel() {
		return plannedFinishColumnLabel;
	}

	/**
	 * @return Returns the plannedStartColumnLabel.
	 */
	public String getPlannedStartColumnLabel() {
		return plannedStartColumnLabel;
	}

	/**
	 * @return Returns the taskAssignmentColumnLabel.
	 */
	public String getTaskAssignmentColumnLabel() {
		return taskAssignmentColumnLabel;
	}

	/**
	 * @return Returns the totalWorkColumnLabel.
	 */
	public String getTotalWorkColumnLabel() {
		return totalWorkColumnLabel;
	}

	/**
	 * @return Returns the viewButtonLabel.
	 */
	public String getViewButtonLabel() {
		return viewButtonLabel;
	}

	/**
	 * @return Returns the workCompleteColumnLabel.
	 */
	public String getWorkCompleteColumnLabel() {
		return workCompleteColumnLabel;
	}

	/**
	 * @return Returns the workPercentCompleteColumnLabel.
	 */
	public String getWorkPercentCompleteColumnLabel() {
		return workPercentCompleteColumnLabel;
	}

	/**
	 * @return Returns the workRemainingColumnLabel.
	 */
	public String getWorkRemainingColumnLabel() {
		return workRemainingColumnLabel;
	}

	/**
	 * @return Returns the workspaceColumnLabel.
	 */
	public String getWorkspaceColumnLabel() {
		return workspaceColumnLabel;
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
	public BusinessSpace getResourceList() {
		return resourceList;
	}

	/**
	 * @param resourceList
	 *            The resourceList to set.
	 */
	public void setResourceList(BusinessSpace resourceList) {
		this.resourceList = resourceList;
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
	 * @return Returns the endDateSelectionReqMsg.
	 */
	public String getEndDateSelectionReqMsg() {
		return endDateSelectionReqMsg;
	}

	/**
	 * @return Returns the resourceListSelectionReqMsg.
	 */
	public String getResourceListSelectionReqMsg() {
		return resourceListSelectionReqMsg;
	}

	/**
	 * @return Returns the startDateSelectionReqMsg.
	 */
	public String getStartDateSelectionReqMsg() {
		return startDateSelectionReqMsg;
	}

	/**
	 * @return Returns the manageListSelectResourcesButtonCaption.
	 */
	public String getManageListSelectResourcesButtonCaption() {
		return manageListSelectResourcesButtonCaption;
	}

	/**
	 * @return Returns the calendarDateFormat.
	 */
	public String getCalendarDateFormat() {
		return calendarDateFormat;
	}

	/**
	 * @param calendarDateFormat
	 *            The calendarDateFormat to set.
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
	 * @param gridTitle
	 *            the gridTitle to set
	 */
	public void setGridTitle(String gridTitle) {
		this.gridTitle = gridTitle;
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
	 * @return the fromDateInvalidMessage
	 */
	public String getFromDateInvalidMessage() {
		return fromDateInvalidMessage;
	}

	/**
	 * @return the invalidDateRangeMessage
	 */
	public String getInvalidDateRangeMessage() {
		return InvalidDateRangeMessage;
	}

	/**
	 * @return the serverRequestFailedMessage
	 */
	public String getServerRequestFailedMessage() {
		return serverRequestFailedMessage;
	}

	/**
	 * @return the toDateInvalidMessage
	 */
	public String getToDateInvalidMessage() {
		return toDateInvalidMessage;
	}

	/**
	 * @return the dateFieldBlankMessage
	 */
	public String getDateFieldBlankMessage() {
		return dateFieldBlankMessage;
	}

	/**
	 * @return the allOption
	 */
	public String getAllOption() {
		return allOption;
	}

	/**
	 * @return the noAssignmentMessage
	 */
	public String getNoAssignmentMessage() {
		return noAssignmentMessage;
	}

	/**
	 * @return the noBusinessFoundOption
	 */
	public String getNoBusinessFoundOption() {
		return noBusinessFoundOption;
	}

	/**
	 * @return the gridTotalRowLabel
	 */
	public String getGridTotalRowLabel() {
		return gridTotalRowLabel;
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
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the inputDateFieldProperMessage
	 */
	public String getInputDateFieldProperMessage() {
		return inputDateFieldProperMessage;
	}
	
}
