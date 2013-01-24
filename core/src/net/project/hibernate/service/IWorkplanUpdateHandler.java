/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;

public interface IWorkplanUpdateHandler {
	
	public String updateTaskValues(Schedule schedule, Schedule clonedSchedule );
	
	public void revert(Schedule schedule);
	
	public void changeName(String taskId, String newTaskName,Schedule clonedSchedule);
	
	public void changePriority(String taskId, int newPriority, Schedule clonedSchedule);
	
	public void recalculate(Schedule clonedSchedule);

}
