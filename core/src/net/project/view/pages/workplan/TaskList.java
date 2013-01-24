/**
 * 
 */
package net.project.view.pages.workplan;

import java.util.List;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleColumn;
import net.project.schedule.ScheduleDecorator;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleRow;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/**
 *
 */
public class TaskList extends BasePage{
	
	@Property
	private ScheduleRow row;
	
	@Property
	private ScheduleColumn col;
	
	@Property
	private ScheduleEntry scheduleEntry;
	
	@Persist
	private Schedule schedule;
	
	@Persist
	private String errorMessage;
	
	@Property
	private ScheduleDecorator scheduleDecorator = new ScheduleDecorator();
	
	@Persist
	private String personalSettingsString;
	
	@SetupRender
	void initializeSchedule() {
		if (this.schedule != null)
			schedule.getTaskList().constructVisibility();
	}
	
	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@CleanupRender
	void clearValue(){
		this.errorMessage = "";
	}

	/**
	 * @param personalSettingsString the personalSettingsString to set
	 */
	public void setPersonalSettingsString(String personalSettingsString) {
		this.personalSettingsString = personalSettingsString;
	}

	/**
	 * @return the personalSettingsString
	 */
	public String getPersonalSettingsString() {
		return personalSettingsString;
	}

}
