/**
 * 
 */
package net.project.hibernate.service.impl;

import net.project.hibernate.service.ITaskMoveHandler;
import net.project.schedule.Schedule;
import net.project.schedule.TaskListUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service(value="taskMoveHandler")
public class TaskMoveHandlerImpl implements ITaskMoveHandler {
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskMoveHandler#taskUp(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String taskUp(String taskId, Schedule clonedSchedule) {
		String idList[] = { taskId };
		try {
			TaskListUtils.promoteTasks(idList, clonedSchedule.getID());
		} catch (Exception e) {
			Logger.getLogger(TaskMoveHandlerImpl.class).error("Error occured while making task up " + e.getMessage());
			return e.getLocalizedMessage();
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ITaskMoveHandler#taskDown(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String taskDown(String taskId, Schedule clonedSchedule) {
		String idList[] = { taskId };
		try {
			TaskListUtils.demoteTasks(idList, clonedSchedule);
		} catch (Exception e) {
			Logger.getLogger(TaskMoveHandlerImpl.class).error("Error occured while making task up" + e.getMessage());
			return e.getLocalizedMessage();
		}
		return "";
	}





}
