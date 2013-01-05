/**
 * 
 */
package net.project.view.pages.workplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderListener;
import net.project.base.property.PropertyProvider;
import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.schedule.Baseline;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleAction;
import net.project.schedule.ScheduleColumn;
import net.project.schedule.ScheduleDecorator;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleRow;
import net.project.schedule.WorkplanFilterManager;
import net.project.schedule.mvc.handler.taskedit.Helper;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 *
 */
@IncludeJavaScriptLibrary("${tapestry.scriptaculous}/prototype.js")
public class TaskView extends BasePage{
	
	@Property
	@Persist
	private Schedule schedule;
	
	@Persist
	private Schedule cloneSchedule;
	
	@Property
	private WorkplanFilterManager filterManager = new WorkplanFilterManager();
	
	@InjectPage
	@Property
	private TaskList taskListPage;
	
	@Property
	private ScheduleEntry scheduleEntry;
	
	@Property
	private ScheduleDecorator scheduleDecorator = new ScheduleDecorator();
	
	@Property
	private String personalSettingsString;
	
	@Persist
	private Schedule calculatedSchedule;
	
	@Property
	@Persist
	private NumberFormat nf;
	
	@Property
	private ScheduleRow row;
	
	@Property
	private ScheduleColumn col;
	
	private PersonProperty property;
	
	@Property
	private String defaultBaseline;
	
	public List getTaskList(){
		return schedule.getTaskList().getList();
	}

	@SetupRender
	void setValues(){
		initializeSchedule();
		initializePersonalSettings();
	}
	
	private void initializeSchedule(){
		schedule = new Schedule();
		schedule.setSpace(getSpace());
	    // Get the hierarchy view and set value in schedule
    	schedule.setHierarchyView(schedule.HIERARCHY_VIEW_EXPANDED);
	    schedule.clearFinderFilterList();
	    schedule.clearFinderListener();
	    
	    String workspaceFilterListSpaceID = (String)getSession().getAttribute("workspaceFilterListSpaceID");

	    if (workspaceFilterListSpaceID != null && workspaceFilterListSpaceID.equals(getUser().getCurrentSpace().getID())) {
	        FinderFilterList list = (FinderFilterList)getSession().getAttribute("workspaceFilterList");
	        if (list != null) {
	        	schedule.setFinderFilterList(list);
	        }
	        FinderListener listener = (FinderListener)getHttpServletRequest().getAttribute("taskFinderListener");
	        if (listener != null) {
	        	schedule.setFinderListener(listener);
	        }
	    }
	    

	    //Load the schedule and the schedule entries
	    try {
	    	schedule.loadAll();
	    	// Find default baseline
			defaultBaseline = Baseline.getDefaultBaselineName(schedule.getID());
		} catch (PersistenceException pnetEx) {
			logger.error("Error while loading schedule:"+pnetEx.getMessage());
		}
		cloneSchedule = (Schedule)this.schedule.clone();
		
		taskListPage.setSchedule(schedule);
		getSession().setAttribute("schedule", schedule);
		nf = NumberFormat.getInstance();
		//To diplay error message if any.
		ErrorReporter errorReporter = (ErrorReporter)getSession().getAttribute("errorReporter");
		String errorMessage = "";
		if (errorReporter != null) {
			if (errorReporter.errorsFound()) {
				Collection errorList = errorReporter.getErrorDescriptions();
				Iterator it = errorList.iterator();
				while (it.hasNext()) {
					ErrorDescription errorDescription = (ErrorDescription) it.next();
					errorMessage += errorDescription.getErrorText();
				}
				errorReporter.clear();
			} else {
				if(schedule.getWarnings() == 1) {
					errorMessage = PropertyProvider.get("prm.schedule.main.dirty.warning.message");
		        }else{
		        	errorMessage = "";
		        }
			}
		}
		taskListPage.setErrorMessage(errorMessage);
	}
	
	/**
	 * Check access permission on page activate.
	 * @return Url of page.
	 */
	Object onActivate(){
    	Object url = checkForUser();
		//check permission of user to access 
    	if(url == null){
    		url = checkAccess(getUser().getCurrentSpace().getID(), net.project.base.Module.SCHEDULE, net.project.security.Action.VIEW);
    	}
    	
		return url;
	}
	
	/**Perform action on ajax request.
	 * @param prm
	 * @return       
	 */
	public Object onActivate(String prm){
		if (!prm.equalsIgnoreCase("getFlatViewData") && !prm.equalsIgnoreCase("getPhaseCount")) {
			String result = ScheduleAction.performAction(getParameter("parameterString"), this.schedule, this.cloneSchedule, getHttpServletRequest());
			if(ScheduleAction.isWorkplanEditAction(getParameter("parameterString"))){
				if(ScheduleAction.isWorkplanSortChangeAction(getParameter("parameterString"))){
					taskListPage.setSchedule(this.schedule);
				} else {
					if(calculatedSchedule == null){
						calculatedSchedule = (Schedule)schedule.clone();
					} 
					List<ScheduleEntry> modifiedEntries = null;
					List<ScheduleEntry> modifiedScheduleEntry = new ArrayList<ScheduleEntry>();
					if(ScheduleAction.isRecalculate(getParameter("parameterString"))){
						// Get all the modified schedule entries from schedule by recalculating current schedule
						// with previous schedule
						modifiedEntries = ScheduleAction.getGetModifiedEntries(calculatedSchedule, cloneSchedule); 
					} else {
						// create only single row response
						modifiedScheduleEntry.add(cloneSchedule.getEntry(ScheduleAction.getTaskIDFromJSON(getParameter("parameterString")))); 
					}
					
					if((CollectionUtils.isNotEmpty(modifiedEntries) && modifiedEntries.size() <= 100)){
						String modifiedEntriesJsonString = ScheduleAction.getModifiedEntriesJsonData(modifiedEntries, this.cloneSchedule, this.schedule, result);
						calculatedSchedule = (Schedule)cloneSchedule.clone();
						return new TextStreamResponse("text",modifiedEntriesJsonString);
					} else if(modifiedScheduleEntry.size() == 1){
						String modifiedEntriesJsonString = ScheduleAction.getModifiedEntriesJsonData(modifiedScheduleEntry, this.cloneSchedule, this.schedule, result);
						if(ScheduleAction.getJSONParameter(getParameter("parameterString"),"action").equalsIgnoreCase("phaseChanged")){
							initializeSchedule();
						}
						return new TextStreamResponse("text",modifiedEntriesJsonString);
					} else {
						calculatedSchedule = (Schedule)cloneSchedule.clone();
						taskListPage.setSchedule(calculatedSchedule);
						taskListPage.setErrorMessage(result);
						return taskListPage;
					}
				}
			} else {
				initializeSchedule();
			}
			taskListPage.setErrorMessage(result);
			initializePersonalSettings();
			return taskListPage;
		}else if(prm.equalsIgnoreCase("getPhaseCount")){
			return new TextStreamResponse("text", ""+getPhaseCount());
		}else if(prm.equalsIgnoreCase("getFlatViewData")) {	
			int range = 0;
		    int offset = 0;
	        if(StringUtils.isNotEmpty(getParameter("limit")) && StringUtils.isNotEmpty(getParameter("start"))){
	        	range = Integer.parseInt(getParameter("limit"));
	        	offset = Integer.parseInt(getParameter("start"));
	        }
	        if(offset != 0){
	        	range = range + offset;
	        }	
	        if(range >= schedule.getTaskList().size()){
	        	range = schedule.getTaskList().size();
	        }
	        if(schedule.getTaskList().size() > range){
	        	return new TextStreamResponse("json","{\"response\":{\"value\":{\"items\":"+ScheduleAction.getFlatViewData(schedule,offset,range)+",\"total_count\":\""+schedule.getTaskList().getList().size()+"\",\"version\":1}}}");
	        } else{
	        	return new TextStreamResponse("json","{\"response\":{\"value\":{\"items\":"+ScheduleAction.getFlatViewData(schedule, offset, schedule.getTaskList().getList().size())+",\"total_count\":\""+schedule.getTaskList().getList().size()+"\",\"version\":1}}}");
	        }
		}
		return null;
	}
	
	@CleanupRender
	public void setDefaultValues(){
		calculatedSchedule = null;
	}
	
	public int getScheduleModule(){
		return Module.SCHEDULE;
	}
	
	/**
	 * Get space by parsing request parameter.
	 * if no parameter return user's current space.
	 * @return space.
	 */
	private Space getSpace() {
		String spaceID = getParameter("id");
		if (spaceID != null) {
			try {
				return SpaceFactory.constructSpaceFromID(spaceID);
			} catch (PersistenceException e) {
				logger.error("Error while loading consturcting space:" + e.getMessage());
			}
		}
		return getUser().getCurrentSpace();
	}
	
	/**
	 *Initialzie personal settings of login user. 
	 */
	private void initializePersonalSettings(){
		property = PersonProperty.getFromSession(getSession());
        property.setScope(ScopeType.SPACE.makeScope(getUser()));
        property.prefetchForContextPrefix("prm.schedule.main");
        personalSettingsString = ScheduleAction.getPersonalSettingsJSONString(property);
        taskListPage.setPersonalSettingsString(this.personalSettingsString);
	}
	
	/**
	 * To get current space name
	 * @return String
	 */
	public String getSpaceName() {
		return HTMLUtils.jsEscape(getUser().getCurrentSpace().getName());
	}
	
	/**
	 * @return pnet tab set width.
	 */
	public int getPnetTabSetWidth() {
		return getWindowWidth() - 217;
	}

	/**
	 * @return the sliding panel width.
	 */
	public int getSlidingPanelContentWidth() {
		return getWindowWidth() - 229;
	}
	
	/**
	 * @return the task list header Width
	 */
	public int getTaskListHeaderWidth() {
		return getWindowWidth() - 248;
	}

	/**
	 * first checks for personal property if not then get sum of visible column width.
	 * @return the task list container table width
	 */
	public int getTaskLstTableContainerWidth() {
		String width = ScheduleAction.getSchedulePropertyValue(property, "tasklisttablecontainerwidth");
		return StringUtils.isNumeric(width) ? Integer.valueOf(width) : ScheduleColumn.getVisibleColumnsWidth();
	}

	/**
	 * @return the pnet tab set height
	 */
	public int getPnetTabSetHeight() {
		return ((getWindowHeight() - 125) < 560 ? 560 : (getWindowHeight() - 125));
	}

	/**
	 * @return the tab content tab height
	 */
	public int getTabContentHeight() {
		return ((getWindowHeight() - 205) < 475 ? 475 : (getWindowHeight() - 205));
	}
	
	/**
	 * @return the splitter bar height
	 */
	public int getSplitterBarHeight() {
		return ((getWindowHeight() - 153) < 527 ? 527 : (getWindowHeight() - 153));
	}
	
	/**
	 * To get phase count.
	 * @return
	 */
	public int getPhaseCount(){
		return Helper.getPhaseOptions(getUser().getCurrentSpace()).size();
	}
}
