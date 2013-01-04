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
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnAssignmentWork;

public interface IPnAssignmentWorkService {
	
	/**
	 * @param assignmentWorkId for AssignmentWork we need to select from database
	 * @return PnAssignmentWork bean
	 */
	public PnAssignmentWork getAssignmentWork(Integer assignmentWorkId);
	
	/**
	 * Saves new AssignmentWork
	 * @param pnAssignmentWork object we want to save
	 * @return primary key for saved AssignmentWork
	 */
	public Integer saveAssignmentWork(PnAssignmentWork pnAssignmentWork);
	
	/**
	 * Deletes AssignmentWork from database
	 * @param pnAssignmentWork object we want to delete
	 */
	public void deleteAssignmentWork(PnAssignmentWork pnAssignmentWork);
	
	/**
	 * Updates AssignmentWork
	 * @param pnAssignmentWork object we want to update
	 */
	public void updateAssignmentWork(PnAssignmentWork pnAssignmentWork);
	
	/**
	 * @param personIds for persons to whom work capture we want to select
	 * @param startDate date that select the amount of work done from this date
	 * @param endDate date that selects the amount of work done until this date
	 * @param spaceId that select the amount of work done by space id
	 * @return List PnAssignmentWork bean 
	 */
	public List<PnAssignmentWork> getTotalWorkByDate(Integer[] personIds, Date startDate, Date endDate, Integer spaceId);
	
	public PnAssignmentWork getWorkDetailsById(Integer assignmentWorkId);
}
