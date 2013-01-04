package net.project.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.resource.PersonProperty;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.util.DateUtils;
import net.project.util.JSONUtils;
import net.project.util.StringUtils;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 */
public class ScheduleAction {

	private static Logger log = Logger.getLogger(ScheduleAction.class);
	
	/**
	 * @param action
	 * @param ids
	 * @param parentTaskId
	 * @param schedule
	 * @return
	 */
	public static String performAction(String jsonParameterString, Schedule schedule, Schedule cloneSchedule, HttpServletRequest request) {
		ParameterParser parameter = new ParameterParser(jsonParameterString);
		
		//We dont need to check security access for performing these actions.
		if(parameter.action("reload")){
			return StringUtils.EMPTY;
		}else if(parameter.action("applyFilter")){
			applyFilter(parameter, request, schedule);
			return null;
		}  else if(parameter.action("sortChanged")){
			schedule.getTaskList().setColumnIndex(parameter.getInt("columnIndex"));
			schedule.getTaskList().sort();
			setCssAfterSort(schedule);
			return StringUtils.EMPTY;
		}
		
		//First check the secrity access and then perform action.
		ScheduleEntry entry = schedule.getEntry(parameter.get("taskId"));
		
		try {
			if (entry != null || parameter.action("saveSchedule")) {
				validateSecurity(Module.SCHEDULE, Action.MODIFY, entry != null ? entry.getID() : null, request);
			} else {
				validateSecurity(Module.SCHEDULE, Action.CREATE, null, request);
			}
        } catch (AuthorizationFailedException e) {
            return e.getLocalizedMessage();
        } catch (PnetException e) {
            return e.getLocalizedMessage();
        }
		
		if (parameter.action("taskUp")) {
			return ServiceFactory.getInstance().getTaskMoveHandler().taskUp(parameter.get("taskId"), schedule);
		} else if (parameter.action("taskDown")) {
			return ServiceFactory.getInstance().getTaskMoveHandler().taskDown(parameter.get("taskId"), schedule);
		} else if (parameter.action("indent")) {
			return ServiceFactory.getInstance().getWorkplanTaskIndentionHandler().IndentTask(parameter.get("taskId"), parameter.get("parentTask"),schedule);
		} else if (parameter.action("unindent")) {
			return ServiceFactory.getInstance().getWorkplanTaskIndentionHandler().unIndentTask(parameter.get("taskId"), schedule);
		} else if (parameter.action("linktask")) {
			return ServiceFactory.getInstance().getTaskLinkUnlinkHandler().linkTasks(parameter.get("taskId"), schedule);
		} else if (parameter.action("unlinktask")) {
			return ServiceFactory.getInstance().getTaskLinkUnlinkHandler().unlinkTasks(parameter.get("taskId"), schedule);
		} else if (parameter.action("setPhase")) {
			return ServiceFactory.getInstance().getAssignPhaseHandler().assignPhase(parameter.get("taskId"), parameter.get("phaseId"));
		} else if (parameter.action("recalculate")) {
			ServiceFactory.getInstance().getWorkplanUpdateHandler().recalculate(schedule);
		} else if (parameter.action("taskNameChanged")) {
			ServiceFactory.getInstance().getWorkplanUpdateHandler().changeName(parameter.get("taskId"), parameter.get("taskName"), cloneSchedule);
		} else if (parameter.action("workChanged")) {
			return ServiceFactory.getInstance().getWorkplanWorkChangeHandler().workChanged(parameter.get("taskId"), parameter.get("newWork"), parameter.get("workUnit"), cloneSchedule);
		}else if (parameter.action("workCompleteChanged")) {
			return ServiceFactory.getInstance().getWorkplanWorkChangeHandler().WorkCompleteChanged(parameter.get("taskId"), parameter.get("newWorkComplete"), parameter.get("workCompleteUnit"), cloneSchedule);
		}else if (parameter.action("durationChanged")) {
			return ServiceFactory.getInstance().getWorkplanWorkChangeHandler().durationChanged(parameter.get("taskId"), parameter.get("newDuration"), parameter.get("durationUnit"), cloneSchedule);
		} else if (parameter.action("workCompletePercentChanged")) {
			return ServiceFactory.getInstance().getWorkplanWorkPercentChangeHandler().workPercentChanged(parameter.get("taskId"), null,parameter.get("newPercentAmount"), cloneSchedule);
		} else if (parameter.action("startDateChanged")) {
			return ServiceFactory.getInstance().getWorkplanDateChangeHandler().changeStartDate(parameter.get("taskId"), parameter.get("newDateToChange"), cloneSchedule, parameter.getBoolean("makeWorkingDay"),request);
		} else if (parameter.action("endDateChanged")) {
			return ServiceFactory.getInstance().getWorkplanDateChangeHandler().changeEndDate(parameter.get("taskId"), parameter.get("newDateToChange"), cloneSchedule, parameter.getBoolean("makeWorkingDay"),request);
		} else if (parameter.action("saveSchedule")) {
			return ServiceFactory.getInstance().getWorkplanUpdateHandler().updateTaskValues(schedule, cloneSchedule);
		} else if (parameter.action("phaseChanged")) {
			ServiceFactory.getInstance().getAssignPhaseHandler().assignPhase(parameter.get("taskId"), parameter.get("newValue"));
		} else if (parameter.action("priorityChanged")) {
			ServiceFactory.getInstance().getWorkplanUpdateHandler().changePriority(parameter.get("taskId"), parameter.getInt("newValue"), cloneSchedule);
		} else if (parameter.action("quickAdd")){
			try {
				ServiceFactory.getInstance().getAddTaskService().quickAdd(parameter.get("taskName"),parameter.get("taskDesc"),parameter.get("work"),parameter.get("workMeasure"),parameter.get("startDateString"),	parameter.get("finishDateString"), parameter.get("selected").split(","),schedule);
			} catch (IllegalStateException pnetEx) {
				Logger.getLogger(ScheduleAction.class).error("error occurred while quick add task"+pnetEx.getMessage());
				return pnetEx.getMessage();
			} catch (Exception pnetEx) {
				Logger.getLogger(ScheduleAction.class).error("error occurred while quick add task"+pnetEx.getMessage());
				return pnetEx.getMessage();
			}
			//New task Sccessfully added. 
			return StringUtils.EMPTY;
		}
		
		return StringUtils.EMPTY;
	}
	
	//To apply row css after sorting
	public static void setCssAfterSort(Schedule cloneSchedule){
		List<ScheduleEntry> taskList = cloneSchedule.getTaskList().getList();
		Integer iterator = 0;
		for(ScheduleEntry entry : taskList){
			iterator++;
			entry.setEvenEntryCss( iterator %2 == 0 ?  "dashboard-even" : "" ) ;
		}
	}
	
	//To check for edit action.
	public static boolean isWorkplanEditAction(String jsonParameterString){
		return new ParameterParser(jsonParameterString).isEditAction();
	}
	
	//To check for sort action.
	public static boolean isWorkplanSortChangeAction(String jsonParameterString){
		return new ParameterParser(jsonParameterString).action("sortChanged");
	}
	
	//Handle filter request.
	public static void applyFilter(ParameterParser parameter, HttpServletRequest request, Schedule schedule) {
		WorkplanFilterManager filterManager = new WorkplanFilterManager();
		filterManager.setShowAllTasks(parameter.getBoolean("showLateTasks"));
		filterManager.setShowComingDue(parameter.getBoolean("showComingDue"));
		filterManager.setShowAssignedToUser(parameter.getBoolean("showAssignedToUser"));
		filterManager.setShowOnCriticalPath(parameter.getBoolean("showOnCriticalPath"));
		filterManager.setShowUnassigned(parameter.getBoolean("showUnassigned"));
		filterManager.setShowShouldHaveStarted(parameter.getBoolean("showShouldHaveStarted"));
		filterManager.setShowStartedAfterPlannedStart(parameter.getBoolean("showStartedAfterPlannedStart"));
		filterManager.setStartDateFilterStart(parameter.get("startDateFilterStart"));
		filterManager.setStartDateFilterEnd(parameter.get("startDateFilterEnd"));
		filterManager.setEndDateFilterStart(parameter.get("endDateFilterStart"));
		filterManager.setEndDateFilterEnd(parameter.get("endDateFilterEnd"));
		filterManager.setTaskName(parameter.get("taskName"));
		filterManager.setTaskNameComparator(parameter.get("taskNameComparator"));
		filterManager.setWorkPercentComplete(parameter.get("workPercentComplete"));
		filterManager.setWorkPercentCompleteComparator(parameter.get("workPercentCompleteComparator"));
		filterManager.setTaskType(parameter.get("type"));
		filterManager.setAssignedUser(parameter.get("assignedUser"));
		filterManager.setSelectedPhaseId(parameter.get("selectedPhaseID"));
		filterManager.applyFilter(request, schedule);
	}
	
	/**
	 * @param previousSchedule
	 * @param currentSchedule
	 */
	public static List<ScheduleEntry> getGetModifiedEntries(Schedule previousSchedule, Schedule currentSchedule){
		List<ScheduleEntry> scheduleEntries = new ArrayList<ScheduleEntry>();
		try {
			new TaskEndpointCalculation().recalculateTaskTimesNoLoad(currentSchedule);
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while recalculating schedule"+pnetEx.getMessage());
		}
		 
		List<ScheduleEntry> modifiedList = currentSchedule.getTaskList().getList();
		List<ScheduleEntry> originalList = previousSchedule.getTaskList().getList();
		for(int iterator = 0; iterator < originalList.size(); iterator++){
			if(!originalList.get(iterator).equals((modifiedList.get(iterator)))) {
				scheduleEntries.add((ScheduleEntry)modifiedList.get(iterator));
			}
		}
		
		return scheduleEntries;
	}

	/**
	 * @param modifiedEntries
	 * @param scheduleStatus
	 */
	public static String getModifiedEntriesJsonData(List<ScheduleEntry> modifiedEntries, Schedule modifiedSchedule, Schedule orignalSchedule, String message) {
		JSONArray jSONArray = new JSONArray();
		JSONObject obj = null;
		try {
			for (ScheduleEntry entry : modifiedEntries) {
				obj = new JSONObject();
				obj.put("id", entry.getID());
				obj.put("taskName", entry.getNameMaxLength40().replaceAll("'", "`"));
				obj.put("taskWork", entry.getWorkTQ().toShortString(0, 2));
				obj.put("taskDuration", entry.getDurationFormatted());
				obj.put("taskStartDate", entry.getStartTimeStringFormatted());
				obj.put("hiddenStartDate", entry.getStartTimeString());
				obj.put("taskEndDate", entry.getEndTimeStringFormatted());
				obj.put("hiddenEndDate", entry.getEndTimeString());
				obj.put("taskWorkComplete", entry.getWorkCompleteTQ().toShortString(0, 2));
				obj.put("taskWorkPercentComplete", entry.getWorkPercentComplete());
				obj.put("editedCellCss", getCellCSS(entry.getSequenceNumber()));
				jSONArray.put(obj);
			}
			obj = new JSONObject();
			obj.put("startDate", modifiedSchedule.getScheduleStartDateString());
			obj.put("endDate", modifiedSchedule.getScheduleEndDateString());
			obj.put("taskCount", modifiedSchedule.getSchedueEntrieCountString());
			obj.put("work", modifiedSchedule.getSchedueWorkSummaryString());
			if(!modifiedSchedule.getScheduleStartDate().equals(orignalSchedule.getScheduleStartDate())
				||!modifiedSchedule.getScheduleEndDate().equals(orignalSchedule.getScheduleEndDate())){
				obj.put("scheduleDateChanged", true);
				obj.put("changeType", (!modifiedSchedule.getScheduleStartDate().equals(orignalSchedule.getScheduleStartDate()) ? "start" : "end"));
				obj.put("date", (!modifiedSchedule.getScheduleStartDate().equals(orignalSchedule.getScheduleStartDate()) ? modifiedSchedule.getScheduleStartDateString() : modifiedSchedule.getScheduleEndDateString()));
			}else{
				obj.put("scheduleDateChanged", false);
			}
			if(message.startsWith("{")){
				obj.put("message", "");
				obj.put("isNonworkingDay", true);
				ParameterParser parameter = new ParameterParser(message);
				obj.put("dateType", parameter.get("dateType"));
				obj.put("editedDate", parameter.get("editedDate"));
				obj.put("currentDate", parameter.get("currentDate"));
				obj.put("nextDate", parameter.get("nextDate"));
				obj.put("name", parameter.get("name"));
			} else {
				obj.put("message", message);
				obj.put("isNonworkingDay", false);
			}
			jSONArray.put(obj);
			return JSONUtils.jsonArrayToString(jSONArray, new StringBuffer());
		} catch (JSONException pnetEx) {
			log.error("Error occured while recalculating schedule" + pnetEx.getMessage());
		}
		return null;
	}
	
	public static String getCellCSS(Integer taskIndex){
		return taskIndex % 2 == 0 ? "eEditedCell" : "oEditedCell";
	}
	
	private static void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException , PnetException {

        //Verify that the user didn't try to sidestep the intended access method
        //AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);

        //We need the assignees for checking security
        AssignmentManager am = new AssignmentManager();
        if (!Validator.isBlankOrNull(objectID)) {
            am.setObjectID(objectID);
            am.loadAssigneesForObject();
        }

        if (!am.isUserInAssignmentList(SessionManager.getUser().getID())) {
            SecurityProvider sp = (SecurityProvider)request.getSession().getAttribute(("securityProvider"));

            /*
             * Check modify permission if user is not assigned task. A null value is
             * sent as the ObjectId because a role could modify a task if such
             * role had granted some modify access at module level. So, the module
             * access overrides the object access.
             * An exception is thrown if they have no permission.
            */            
            sp.securityCheck(null, Integer.toString(Module.SCHEDULE), action);
        }
    }
	
	//To generate flat view data.
	public static String getFlatViewData(Schedule schedule,Integer start ,Integer limit){
		List<ScheduleEntry> taskList = schedule.getTaskList().getList().subList(start, limit);
		String gridData = "[";
		for(int iterator = 0; iterator < taskList.size(); iterator++){
			ScheduleEntry entry = (ScheduleEntry) taskList.get(iterator);
			gridData += "{\"id\":\"" + entry.getID() 
					+ "\",\"sequenceNo\":\"" + entry.getSequenceNumber()
					+ "\",\"taskName\":\"" + entry.getName().replaceAll("\"", "`") 
					+ "\",\"taskWork\":\"" + entry.getWorkTQ().getAmount() 
					+ "\",\"taskDuration\":\"" + entry.getDurationTQ().getAmount()
					+ "\",\"" + "taskStartDate\":\"" + entry.getStartTimeString()
					+ "\",\"taskEndDate\":\"" + entry.getEndTimeString()
					+ "\",\"workComplete\":\"" + entry.getWorkCompleteTQ().getAmount()
					+ "\",\"workPercentComplete\":\"" + entry.getWorkPercentComplete() 
					+ "\",\"actualStartDate\":\"" + entry.getActualStartDateString()
					+ "\",\"actualEndDate\":\"" + entry.getActualEndDateString()
					+ "\",\"hasAssignments\":\"" + getStatusFor(entry, "resource")
					+ "\",\"hasDependencies\":\"" + getStatusFor(entry, "hasDependencies")
					+ "\",\"isDateConstrained\":\"" + getStatusFor(entry, "isDateConstrained") 
					+ "\",\"criticalPath\":\"" + getStatusFor(entry, "criticalPath") 
					+ "\",\"afterDeadline\":\"" + getStatusFor(entry, "afterDeadline") 
					+ "\",\"externalTask\":\"" + getStatusFor(entry, "externalTask") 
					+ "\",\"statusNotifiers\":\"1\"" 
					+ ",\"ETTP\":\"" + XMLUtils.escape(entry.getSharingSpaceName())
					+ "\",\"ADTP\":\"" + PropertyProvider.get("prm.schedule.list.afterdeadline.message", entry.getDeadlineString())
					+ "\",\"DCTP\":\"" + getDateConstrainedToolTip(entry) + "\",\"ATP\":\""
					+ entry.getAssignmentsTooltip() + "\",\"isMilestone\":\"" + getStatusFor(entry, "isMilestone")
					+ "\"}";
			if(iterator < taskList.size()-1){
				gridData += ",";
			}
		}
		return gridData + "]";
	}
	
	//Generate date constrained tool tip.
	private static String getDateConstrainedToolTip(ScheduleEntry entry) {
		return PropertyProvider.get("prm.schedule.workplan.calendargraphic.taskcontsraint.tooltip") + " "
				+ entry.getConstraintType().getName()
				+ (entry.getConstraintType().isDateConstrained() ? " (" + entry.getConstraintDateString() + ")" : "");
	}
	
	//To get status identifiers.
	private static String getStatusFor(ScheduleEntry entry, String statusIdentifier) {
		if (statusIdentifier.equalsIgnoreCase("resource")) {
			return entry.getAssignmentList().size() > 0 ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("hasDependencies")) {
			return !entry.getPredecessorsNoLazyLoad().isEmpty() ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("isDateConstrained")) {
			return entry.getConstraintType().isDateConstrained() ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("criticalPath")) {
			return entry.isCriticalPath() ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("afterDeadline")) {
			return entry.isPastDeadline() ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("externalTask")) {
			return entry.isFromShare() ? "1" : "0";
		}
		if (statusIdentifier.equalsIgnoreCase("isMilestone")) {
			return entry.isMilestone() ? "1" : "0";
		}
		return null;
	}
	
	public static String getPersonalSettingsJSONString(PersonProperty property){
		org.json.JSONObject jSONObject = new org.json.JSONObject();
		try {
			// Task List Table Container width
			jSONObject.put("taskListTableContainerWidth", getSchedulePropertyValue(property, "tasklisttablecontainerwidth"));
			//is right panel expanded
			jSONObject.put("rightTabsetExpanded", getSchedulePropertyValue(property, "righttabsetexpanded"));
			//left panel width
			jSONObject.put("taskListPanelWidth", getSchedulePropertyValue(property, "tasklistpanelwidth"));
			//right tab activated
			jSONObject.put("rightTabSet", getSchedulePropertyValue(property, "rightTabSet"));
			return JSONUtils.jsonToObjectLibertal(jSONObject, null);
		} catch (JSONException pnetEx) {
			log.error("Error occured while jsonToObjectLibertal : " + pnetEx.getMessage());
		}
		return null;
	}
	
	public static String getSchedulePropertyValue(PersonProperty property, String name){
		String values[] = property.get("prm.schedule.main", name, true);
		return values != null && values.length > 0 ? values[0] : "null"; 
	}
	
	/**
	 * To check whether current action is recalculate 
	 * 
	 * @param jsonParameterString input JSON parameter string to pass
	 * @return true / false depending on the current action
	 */
	public static boolean isRecalculate(String jsonParameterString){
		return new ParameterParser(jsonParameterString).get("action").equals("recalculateChanged");
	}
	
	/**
	 * To get task id from input JSON String
	 * 
	 * @param jsonParameterString input JSON parameter string to pass
	 * @return task id from JSON String
	 */
	public static String getTaskIDFromJSON(String jsonParameterString){
		return new ParameterParser(jsonParameterString).get("taskId");
	}
	
	public static String getJSONParameter(String jsonParameterString, String name){
		return new ParameterParser(jsonParameterString).get(name);
	}
}



class ParameterParser {
	
	private JSONObject jSONObject;
	
	/**
	 * @param jSONString
	 */
	public ParameterParser(String jSONString) {
		this.jSONObject = new JSONObject(jSONString);
	}
	
	String get(String parameter) {
		try {
			return this.jSONObject.getString(parameter);
		} catch (Exception e) {
			return null;
		}
	}
	
	Integer getInt(String parameter){
		return Integer.parseInt(get(parameter));
	}
	
	boolean getBoolean(String parameter){
		return Boolean.valueOf(get(parameter));
	}
	
	boolean action(String action){
		return (StringUtils.isNotEmpty(get("action")) && get("action").equalsIgnoreCase(action));
	}
	
	boolean isEditAction(){
		return (StringUtils.isNotEmpty(get("action")) && get("action").endsWith("Changed"));
	}
}
