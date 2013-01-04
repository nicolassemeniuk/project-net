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

import net.project.base.mvc.ControllerException;
import net.project.persistence.PersistenceException;
import net.project.resource.RosterBean;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.security.User;
import net.project.space.Space;

/**
 *
 */
public interface IAssignResourceService {
	
	/**
	 * @param taskIdList
	 * @param assignData
	 * @param resourceList
	 * @param schedule
	 * @param isReplaceOrAddNew
	 * @param rosterBean
	 * @param user
	 * @param space
	 * @throws ControllerException
	 * @throws PersistenceException
	 */
	public void assignResourcesToTasks(String taskIdList, String assignData, String resourceList, Schedule schedule, boolean isReplaceOrAddNew, RosterBean rosterBean, User user, Space space)
			throws ControllerException, PersistenceException ;
	
	/**
	 * @param scheduleEntry
	 * @param user
	 * @param roster
	 * @param schedule
	 * @param resourceIDs
	 * @param primaryOwnerID
	 * @throws Exception
	 */
	public void assignResourcesToSingleTask(ScheduleEntry scheduleEntry, User user, RosterBean roster, Schedule schedule, String[] resourceIDs, String[] percentAssignedDataList, String primaryOwnerID, Space space) 
			throws Exception;
}
