/**
 * 
 */
package net.project.schedule;

import org.apache.log4j.Logger;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonalPropertyMap;
import net.project.security.SessionManager;

/**
 *
 */
public class ScheduleDecorator {
	
	private PersonalPropertyMap propertyMap;
	
	public ScheduleDecorator() {
		try {
			if (SessionManager.getUser() != null && SessionManager.getUser().getID() != null)
				propertyMap = new PersonalPropertyMap("prm.schedule.tasklist");
        } catch (PersistenceException e) {
        	Logger.getLogger(TaskList.class).error("Error loading the person property.", e);
        }
	}

	public String getExternalTaskImage() {
		if (propertyMap.propertyExists("isExternalTaskImage"))
			return propertyMap.getProperty("isExternalTaskImage");
		else
			return SessionManager.getJSPRootURL() + "/images/schedule/externalTask.gif";

	}
	
	public String getTaskDependenciesExistImage() {
		if (propertyMap.propertyExists("taskDependenciesExistImage"))
			return propertyMap.getProperty("taskDependenciesExistImage");
		else
			return SessionManager.getJSPRootURL() + "/images/schedule/dependency.gif";

	}
	
	public String getIsDateConstrainedImage() {
		if (propertyMap.propertyExists("isDateConstrainedImage"))
			return propertyMap.getProperty("isDateConstrainedImage");
		else
			return SessionManager.getJSPRootURL() + "/images/schedule/constraint.gif";

	}
	public String getHasAssignmentImage() {
		if (propertyMap.propertyExists("hasAssignmentImage"))
			return propertyMap.getProperty("hasAssignmentImage");
		else
			return SessionManager.getJSPRootURL() + "/images/group_person_small.gif";

	}
	public String getIsCriticalPathImage() {
		if (propertyMap.propertyExists("isCriticalPathImage"))
			return propertyMap.getProperty("isCriticalPathImage");
		else
			return SessionManager.getJSPRootURL() + "/images/schedule/critical_path.gif";

	}
	public String getAfterDeadlineImage() {
		if (propertyMap.propertyExists("afterDeadlineImage"))
			return propertyMap.getProperty("afterDeadlineImage");
		else
			return SessionManager.getJSPRootURL() + "/images/schedule/after_deadline.gif";

	}
	
	public String getUnassignedTasksImage() {
		if (propertyMap.propertyExists("unassignedTasksImage"))
			return propertyMap.getProperty("unassignedTasksImage");
		else
			return null;

	}
	
	public String getTasksComingDueImage() {
		if (propertyMap.propertyExists("tasksComingDueImage"))
			return propertyMap.getProperty("tasksComingDueImage");
		else
			return null;

	}
	
	public String getCompletedTasksImage() {
		if (propertyMap.propertyExists("completedTasksImage"))
			return propertyMap.getProperty("completedTasksImage");
		else
			return null;

	}
	
	public String getLateTasksImage() {
		if (propertyMap.propertyExists("lateTasksImage"))
			return propertyMap.getProperty("lateTasksImage");
		else
			return null;
	}
	
	public String getSharingProjectTitle(){
		return PropertyProvider.get("prm.schedule.list.sharingproject.title");
	}
	
	public String getCriticalpathTitle(){
		return PropertyProvider.get("prm.schedule.list.criticalpath.message");
	}
	
	public String getNoresourcesTitle(){
		return PropertyProvider.get("prm.schedule.list.noresources.message");
	}
	
	public String getTaskcomingdueTitle(){
		return PropertyProvider.get("prm.schedule.list.taskcomingdue.message");
	}
	
	public String getCompletetaskTitle(){
		return PropertyProvider.get("prm.schedule.list.completetask.message");
	}
	
	public String getLatetaskTitle(){
		return PropertyProvider.get("prm.schedule.list.latetask.message");
	}
	
	public String getCusomizeColumnTooltip(){
		return PropertyProvider.get("prm.schedule.cusomizecolumntooltip.message");
	}
	
	public String getExpandCollapseTooltip(){
		return PropertyProvider.get("prm.schedule.expandcollapsetooltip.message");
	}
	
	public String getCheckAllTooltip(){
		return PropertyProvider.get("prm.schdule.tasklist.checkuncheckall.title");
	}
	
	public String getSplitterOpenTooltip(){
		return PropertyProvider.get("prm.schdule.splitter.open.title");
	}
	
	public String getSplitterCloseTooltip(){
		return PropertyProvider.get("prm.schdule.splitter.close.title");
	}
	
	public String getScheduleToolBoxHeading(){
		return PropertyProvider.get("prm.schdule.toolbox.heading.title");
	}
	
	/**
	 * Returns clear filters caption
	 */
	public String getClearFiltersLabel(){
		return PropertyProvider.get("prm.schedule.main.clearfilters.message");
	}
	
	/**
	 * Returns apply filters caption
	 */
	public String getApplyFilterLabel(){
		return PropertyProvider.get("prm.schedule.main.applyfilters.message");
	}
	
	/**
	 * Returns expand all title
	 */
	public String getExpandAllTitle(){
		return PropertyProvider.get("prm.schedule.taskview.link.expanddall.label");
	}
	
	/**
	 * Returns collapse all title
	 */
	public String getCollapseAllTitle(){
		return PropertyProvider.get("prm.schedule.taskview.link.collapseall.label");
	}
	
	/**
	 * Returns quick add title
	 */
	public String getQuickAddTitle(){
		return PropertyProvider.get("prm.schedule.taskview.button.quickadd.caption");
	}
	
	/**
	 * Returns column settings title
	 */
	public String getColumnSettingsTitle(){
		return PropertyProvider.get("prm.schedule.taskview.link.columnsettings.label");
	}
}
