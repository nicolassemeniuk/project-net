/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
/**
 * 
 */
package net.project.hibernate.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.security.User;

/**
 *
 */
/**
 *
 */
public interface IAddTaskService {
	 
	/**
	 * @param taskName
	 * @param taskDescription
	 * @param work
	 * @param workUnits
	 * @param startTimeString
	 * @param endTimeString
	 * @param selected
	 * @param schedule
	 * @param nodeExpanded
	 * @return newTaskId
	 * @throws Exception
	 */
	public String quickAdd(String taskName, String taskDescription, String work, String workUnits,
			String startTimeString, String endTimeString, String[] selected, Schedule schedule)
			throws Exception;

	/**
	 * @param taskName
	 * @param taskDescription
	 * @param work
	 * @param workUnits
	 * @param startTimeString
	 * @param endTimeString
	 * @param selectedTaskId(task to create under)
	 * @param schedule
	 * @param nodeExpanded
	 * @return newTaskId
	 * @throws Exception
	 */
	public String quickAddASubTaskUnderSelectedTask(String taskName, String taskDescription, String work, String workUnits,
			String startTimeString, String endTimeString, String selectedTaskId, Schedule schedule, String nodeExpanded)
			throws Exception;
	
	
	
	/**
	 * @param schedule
	 * @param scheduleEntry
	 * @param work
	 * @param workUnit
	 * @param startTimeString
	 * @param endTimeString
	 * @param calculateBy
	 * @param user
	 * @return ScheduleEntry
	 * @throws ParseException
	 */
	public ScheduleEntry reCalculateScheduleEntry(Schedule schedule,ScheduleEntry scheduleEntry, String work, String workUnit, Date date, 
			String calculateBy, User user) throws ParseException;
	
	/**
	 * @param schedule
	 * @param createAfterID
	 * @return ScheduleEntry
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	public ScheduleEntry getNewScheduleEntry(Schedule schedule, String createAfterID) throws PersistenceException, SQLException;
	
	/**
	 * @param newTaskId
	 * @param selectedTaskId
	 * @param schedule
	 * @throws PersistenceException
	 */
	public void indentTaskUnderSelectedTask(String newTaskId, String selectedTaskId, Schedule schedule) throws PersistenceException;

}
