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
package net.project.view.pages.project;

import java.io.Serializable;

import net.project.hibernate.model.PnTask;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;

public class PnTaskWrapper implements Serializable {

	private ScheduleEntry scheduleEntry;

	public PnTaskWrapper(ScheduleEntry scheduleEntry) {
		this.scheduleEntry = scheduleEntry;
	}

	public String getMilestoneUrl() {
		return "/servlet/ScheduleController/TaskView?module=60&action=1&id="
				+ scheduleEntry.getID();
	}

	public String getName() {
		return scheduleEntry.getName();
	}

	public String getDate() {
		String taskDate = "";
		if (scheduleEntry.getEndTime() != null) {
			taskDate = SessionManager.getUser().getDateFormatter().formatDateMedium(
					scheduleEntry.getEndTime());
		}
		return taskDate;
	}

	public String getPercentComplete() {
		return Double.toString(scheduleEntry.getPercentComplete()) != null ? Double
				.toString(scheduleEntry.getPercentComplete()).toString()
				: "0";
	}

	public ScheduleEntry getScheduleEntry() {
		return scheduleEntry;
	}

	public void setScheduleEntry(ScheduleEntry scheduleEntry) {
		this.scheduleEntry = scheduleEntry;
	}

}
