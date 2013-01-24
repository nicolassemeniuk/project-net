/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;


public interface IWorkPercentChangeHandler {
	
	public String workPercentChanged(String taskId, ScheduleEntry scheduleEntry, String newPercentAmount, Schedule clonedSchedule);

}
