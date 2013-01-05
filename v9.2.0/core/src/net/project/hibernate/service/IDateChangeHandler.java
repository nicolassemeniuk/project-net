/**
 * 
 */
package net.project.hibernate.service;

import javax.servlet.http.HttpServletRequest;

import net.project.schedule.Schedule;

/**
 * @author Avibha
 *
 */
public interface IDateChangeHandler {

	public String changeStartDate(String taskId, String newDateToChange, Schedule clonedSchedule, boolean makeWoringDay, HttpServletRequest request);
	
	public String changeEndDate(String taskId, String newDateToChange, Schedule clonedSchedule, boolean makeWoringDay, HttpServletRequest request);
	
}
