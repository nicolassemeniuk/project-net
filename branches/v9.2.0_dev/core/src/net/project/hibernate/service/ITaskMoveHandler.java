/**
 * 
 */
package net.project.hibernate.service;
import net.project.schedule.Schedule;

/**
 *
 */
public interface ITaskMoveHandler {
	
	/**
	 * @param taskId
	 * @param clonedSchedule
	 * @return
	 */
	public String taskUp(String taskId , Schedule clonedSchedule);
	
	/**
	 * @param taskId
	 * @param clonedSchedule
	 * @return
	 */
	public String taskDown(String taskId , Schedule clonedSchedule);

}
