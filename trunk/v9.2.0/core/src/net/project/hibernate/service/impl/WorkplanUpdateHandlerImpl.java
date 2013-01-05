/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.Iterator;
import java.util.List;

import net.project.hibernate.service.IWorkplanUpdateHandler;
import net.project.resource.Assignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleAction;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="workplanUpdateHandler")
public class WorkplanUpdateHandlerImpl implements IWorkplanUpdateHandler {

	
	private static Logger log = Logger.getLogger(WorkplanUpdateHandlerImpl.class);
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkplanUpdateService#updateTaskValues()
	 */
	public String updateTaskValues(Schedule schedule, Schedule clonedSchedule ) {
		List<ScheduleEntry> modifiedEnties = ScheduleAction.getGetModifiedEntries(schedule, clonedSchedule);
		try {
			for (ScheduleEntry me : modifiedEnties){
				ScheduleEntry entry = clonedSchedule.getEntry(me.getID()); 
				entry.store(false, clonedSchedule);
				//store assignments separately
				for (Iterator it = entry.getAssignments().iterator(); it.hasNext();) {
                    Assignment assignment = (Assignment)it.next();
                    assignment.store();
                }
			}
			new TaskEndpointCalculation().recalculateTaskTimes(clonedSchedule, true);
		} catch (Exception e) {
			log.error("error occured while storing clonedSchedule " + e.getMessage());
			return e.getMessage();
		}
		//Task updated Success fully.
		return StringUtils.EMPTY;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkplanUpdateService#revert(net.project.schedule.Schedule)
	 */
	public void revert(Schedule schedule) {
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkplanUpdateService#changeName(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public void changeName(String taskId, String newTaskName, Schedule clonedSchedule) {
		ScheduleEntry scheduleEntry = clonedSchedule.getEntry(taskId);
		scheduleEntry.setName(newTaskName);
	}
	
	public void changePriority(String taskId, int newPriority, Schedule clonedSchedule) {
		ScheduleEntry scheduleEntry = clonedSchedule.getEntry(taskId);
		scheduleEntry.setPriority(newPriority);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWorkplanUpdateHandler#recalculate(net.project.schedule.Schedule)
	 */
	public void recalculate(Schedule clonedSchedule) {
		try {
			 clonedSchedule.recalculateTaskTimes();
		} catch (Exception e) {
			log.error("Error Occured While recalculating all the task details" + e.getMessage());
		}
	}
}
