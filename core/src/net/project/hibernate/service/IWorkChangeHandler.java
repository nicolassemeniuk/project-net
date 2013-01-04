/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;


public interface IWorkChangeHandler {
	
	/**
	 * Handle work change.
	 * @param taskId
	 * @param workAmount
	 * @param workUnit
	 * @param schedule
	 * @return errorMessage
	 */
	public String workChanged(String taskId, String workAmount, String workUnit, Schedule schedule);
	
	/**
	 * Handle work complete change.
	 * @param taskId
	 * @param workCompleteAmount
	 * @param unitSring
	 * @param schedule
	 * @return errorMessage.
	 */
	public String WorkCompleteChanged(String taskId, String workCompleteAmount, String unitSring, Schedule schedule);
	
	/**
	 * Handle changed duration change
	 * @param taskId
	 * @param scheduleEntry
	 * @param durationAmount
	 * @param unitSring
	 * @param clonedSchedule
	 * @return String
	 */
	public String durationChanged(String taskId, String durationAmount, String unitSring, Schedule clonedSchedule);

}
