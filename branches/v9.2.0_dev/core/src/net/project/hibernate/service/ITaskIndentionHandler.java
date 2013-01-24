/**
 * 
 */
package net.project.hibernate.service;

import net.project.schedule.Schedule;

/**
 *
 */
public interface ITaskIndentionHandler {

	public String unIndentTask(String taskId , Schedule clonedSchedule);
	
	public String IndentTask(String taskId, String entryAbove, Schedule clonedSchedule);
}
