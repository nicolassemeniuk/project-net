/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;

/**
 * @author Avibha
 *
 */
public interface IDurationChangeHandler {
	
	/**
	 * @deprecated use IWorkChangeHandler.durationChanged() instead 
	 */
	public String durationChanged(String taskId,ScheduleEntry scheduleEntry, String durationAmount, String unitSring, Schedule clonedSchedule);

}
