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

import net.project.hibernate.model.PnAssignmentWork;

public interface IScheduleService {
    
    public Integer storePlan(String name, String description, Date startDate, Date endDate, Integer autocalculateTasksEndpoint, Integer defaultCalendarId, String timezoneId, Integer modifiedBy, Integer spaceId, Integer defaultTaskCalcTypeId, Date earliestStartDate, Date earliestFinishDate, Date latestStartDate, Date latestFinishDate, String startConstraintType, Date startConstraintDate, Integer planId);
    
    public Integer storePlanVersion(Integer planId);
    
    public Integer storeAssignmentWork(PnAssignmentWork assignmentWork);

}
