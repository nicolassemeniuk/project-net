/**
 * 
 */
package net.project.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.NumberComparator;
import net.project.base.finder.TextComparator;
import net.project.base.property.PropertyProvider;
import net.project.gui.extjs.ExtJSOptionList;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.process.ProcessBean;
import net.project.resource.RosterBean;
import net.project.schedule.mvc.handler.taskedit.Helper;
import net.project.security.SessionManager;

/**
 *
 */
public class WorkplanFilterManager {
	
	private boolean showAllTasks;
	
	private boolean showLateTasks;
	
	private boolean showComingDue;
	
	private boolean showUnassigned;
	
	private boolean showAssignedToUser;
	
	private boolean showOnCriticalPath;
	
	private boolean showShouldHaveStarted;
	
	private boolean showStartedAfterPlannedStart;
	
	private String assignedUser;
	
	private String taskType;
	
	private String taskName;
	
	private String taskNameComparator;
	
	private String workPercentComplete;
	
	private String workPercentCompleteComparator;
	
	private String selectedPhaseId;
	
	private String startDateFilterStart;
	
	private String startDateFilterEnd;
	
	private String endDateFilterStart;
	
	private String endDateFilterEnd;

	public String getAssignedUserModel() {
		RosterBean roster = new RosterBean();
		roster.setSpace(SessionManager.getUser().getCurrentSpace());
		roster.load();
		return roster.getSelectionList(SessionManager.getUser().getID());
	}

	private ProcessBean getProcess() {
		ProcessBean process = new ProcessBean();
		try {
			process.loadProcess(SessionManager.getUser().getCurrentSpace().getID());
		} catch (PersistenceException pnetEx) {
			pnetEx.printStackTrace();
		}
		return process;
	}
	
	public String getPhaseModel() {
		return HTMLOptionList.makeHtmlOptionList(getProcess().getPhaseOptions(true)) ;
	}
	
	public String getExtJSPhaseModel() {
		List options = new ArrayList(getProcess().getPhaseOptions(false));
        options.add(0, new HTMLOption("",  PropertyProvider.get("prm.schedule.taskedit.phase.option.none.name")));//first will be blank option.
		return ExtJSOptionList.makeExtJSOptionList(options) ;
	}
	
	public String getExtJSPriorityModel() {
		return ExtJSOptionList.makeExtJSOptionList(TaskPriority.getAll());
	}

	public String getTaskTypeModel() {
		return Schedule.ALL + "=" + PropertyProvider.get("prm.schedule.main.type.option.all.name") + ","
				+ TaskType.MILESTONE.getID() + "=" + PropertyProvider.get("prm.schedule.main.type.option.milestone.name") + "," 
				+ TaskType.TASK.getID() + "=" + PropertyProvider.get("prm.schedule.main.type.option.task.name");
	}
	
	public String getWorkUnitOptionData(){
		return HTMLOptionList.makeHtmlOptionList(Helper.getWorkUnitOptions());
	}

	public String getTaskNameComparatorModel() {
		return TextComparator.getOptionList(TextComparator.CONTAINS);
	}

	public String getWorkPercentCompleteComparatorModel() {
		return NumberComparator.getOptionList(NumberComparator.DEFAULT);
	}
	
	public void applyFilter(HttpServletRequest request, Schedule schedule) {
		ServiceFactory.getInstance().getWorkplanFilterHandler().applyFilter(request, schedule,  this.showAllTasks, this.showLateTasks, this.showComingDue, this.showUnassigned, this.showAssignedToUser, this.showOnCriticalPath, this.showShouldHaveStarted, this.showStartedAfterPlannedStart, 
				this.assignedUser, this.selectedPhaseId, this.taskName, this.taskNameComparator, this.workPercentComplete, this.workPercentCompleteComparator, this.taskType,
				this.startDateFilterStart, this.startDateFilterEnd, this.endDateFilterStart, this.endDateFilterEnd);
	}
	
	public void clearFilter() {
		this.showAllTasks = true;
		this.showLateTasks = false;
		this.showComingDue = false;
		this.showUnassigned = false;
		this.showAssignedToUser = false;
		this.showOnCriticalPath = false; 
		this.showShouldHaveStarted = false; 
		this.showStartedAfterPlannedStart = false;
		this.selectedPhaseId = "";
		this.taskName = "";
		this.taskNameComparator = "equals";
		this.workPercentComplete = "";
		this.workPercentCompleteComparator = "equals";
		this.taskType = Schedule.ALL;
		this.startDateFilterStart = "";
		this.startDateFilterEnd = "";
		this.endDateFilterStart = "";
		this.endDateFilterEnd = "";
	}

	/**
	 * @return the endDateFilterEnd
	 */
	public String getEndDateFilterEnd() {
		return endDateFilterEnd;
	}

	/**
	 * @param endDateFilterEnd the endDateFilterEnd to set
	 */
	public void setEndDateFilterEnd(String endDateFilterEnd) {
		this.endDateFilterEnd = endDateFilterEnd;
	}

	/**
	 * @return the endDateFilterStart
	 */
	public String getEndDateFilterStart() {
		return endDateFilterStart;
	}

	/**
	 * @param endDateFilterStart the endDateFilterStart to set
	 */
	public void setEndDateFilterStart(String endDateFilterStart) {
		this.endDateFilterStart = endDateFilterStart;
	}

	/**
	 * @return the showAllTasks
	 */
	public boolean isShowAllTasks() {
		return showAllTasks;
	}

	/**
	 * @param showAllTasks the showAllTasks to set
	 */
	public void setShowAllTasks(boolean showAllTasks) {
		this.showAllTasks = showAllTasks;
	}

	/**
	 * @return the showAssignedToUser
	 */
	public boolean isShowAssignedToUser() {
		return showAssignedToUser;
	}

	/**
	 * @param showAssignedToUser the showAssignedToUser to set
	 */
	public void setShowAssignedToUser(boolean showAssignedToUser) {
		this.showAssignedToUser = showAssignedToUser;
	}

	/**
	 * @return the assignedUser
	 */
	public String getAssignedUser() {
		return assignedUser;
	}

	/**
	 * @param assignedUser the assignedUser to set
	 */
	public void setAssignedUser(String assignedUser) {
		this.assignedUser = assignedUser;
	}

	/**
	 * @return the selectedPhaseId
	 */
	public String getSelectedPhaseId() {
		return selectedPhaseId;
	}

	/**
	 * @param selectedPhaseId the selectedPhaseId to set
	 */
	public void setSelectedPhaseId(String selectedPhaseId) {
		this.selectedPhaseId = selectedPhaseId;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * @return the showComingDue
	 */
	public boolean isShowComingDue() {
		return showComingDue;
	}

	/**
	 * @param showComingDue the showComingDue to set
	 */
	public void setShowComingDue(boolean showComingDue) {
		this.showComingDue = showComingDue;
	}

	/**
	 * @return the showLateTasks
	 */
	public boolean isShowLateTasks() {
		return showLateTasks;
	}

	/**
	 * @param showLateTasks the showLateTasks to set
	 */
	public void setShowLateTasks(boolean showLateTasks) {
		this.showLateTasks = showLateTasks;
	}

	/**
	 * @return the showOnCriticalPath
	 */
	public boolean isShowOnCriticalPath() {
		return showOnCriticalPath;
	}

	/**
	 * @param showOnCriticalPath the showOnCriticalPath to set
	 */
	public void setShowOnCriticalPath(boolean showOnCriticalPath) {
		this.showOnCriticalPath = showOnCriticalPath;
	}

	/**
	 * @return the showShouldHaveStarted
	 */
	public boolean isShowShouldHaveStarted() {
		return showShouldHaveStarted;
	}

	/**
	 * @param showShouldHaveStarted the showShouldHaveStarted to set
	 */
	public void setShowShouldHaveStarted(boolean showShouldHaveStarted) {
		this.showShouldHaveStarted = showShouldHaveStarted;
	}

	/**
	 * @return the showStartedAfterPlannedStart
	 */
	public boolean isShowStartedAfterPlannedStart() {
		return showStartedAfterPlannedStart;
	}

	/**
	 * @param showStartedAfterPlannedStart the showStartedAfterPlannedStart to set
	 */
	public void setShowStartedAfterPlannedStart(boolean showStartedAfterPlannedStart) {
		this.showStartedAfterPlannedStart = showStartedAfterPlannedStart;
	}

	/**
	 * @return the showUnassigned
	 */
	public boolean isShowUnassigned() {
		return showUnassigned;
	}

	/**
	 * @param showUnassigned the showUnassigned to set
	 */
	public void setShowUnassigned(boolean showUnassigned) {
		this.showUnassigned = showUnassigned;
	}

	/**
	 * @return the startDateFilterEnd
	 */
	public String getStartDateFilterEnd() {
		return startDateFilterEnd;
	}

	/**
	 * @param startDateFilterEnd the startDateFilterEnd to set
	 */
	public void setStartDateFilterEnd(String startDateFilterEnd) {
		this.startDateFilterEnd = startDateFilterEnd;
	}

	/**
	 * @return the startDateFilterStart
	 */
	public String getStartDateFilterStart() {
		return startDateFilterStart;
	}

	/**
	 * @param startDateFilterStart the startDateFilterStart to set
	 */
	public void setStartDateFilterStart(String startDateFilterStart) {
		this.startDateFilterStart = startDateFilterStart;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the taskNameComparator
	 */
	public String getTaskNameComparator() {
		return taskNameComparator;
	}

	/**
	 * @param taskNameComparator the taskNameComparator to set
	 */
	public void setTaskNameComparator(String taskNameComparator) {
		this.taskNameComparator = taskNameComparator;
	}

	/**
	 * @return the workPercentComplete
	 */
	public String getWorkPercentComplete() {
		return workPercentComplete;
	}

	/**
	 * @param workPercentComplete the workPercentComplete to set
	 */
	public void setWorkPercentComplete(String workPercentComplete) {
		this.workPercentComplete = workPercentComplete;
	}

	/**
	 * @return the workPercentCompleteComparator
	 */
	public String getWorkPercentCompleteComparator() {
		return workPercentCompleteComparator;
	}

	/**
	 * @param workPercentCompleteComparator the workPercentCompleteComparator to set
	 */
	public void setWorkPercentCompleteComparator(String workPercentCompleteComparator) {
		this.workPercentCompleteComparator = workPercentCompleteComparator;
	}

}
