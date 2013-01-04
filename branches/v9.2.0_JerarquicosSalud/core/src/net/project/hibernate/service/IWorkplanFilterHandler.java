/**
 * 
 */
package net.project.hibernate.service;

import javax.servlet.http.HttpServletRequest;

import net.project.schedule.Schedule;


public interface IWorkplanFilterHandler {
	
	public void applyFilter(HttpServletRequest request,
						Schedule clonedSchedule,
						boolean showAllTasks,
						boolean showLateTasks,
						boolean showComingDue,
						boolean showUnassigned,
						boolean showAssignedToUser,
						boolean showOnCriticalPath,
						boolean showShouldHaveStarted,
						boolean showStartedAfterPlannedStart,
						String showByAssignedToUser,
						String phaseID,
						String taskName,
						String taskNameComparator,
						String workPercentComplete,
						String workPercentCompleteComparator,
						String taskType,
						String startDateFilterStart,
						String startDateFilterEnd,
						String endDateFilterStart,
						String endDateFilterEnd);
}
