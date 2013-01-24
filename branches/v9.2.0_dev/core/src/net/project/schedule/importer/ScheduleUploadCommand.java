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
package net.project.schedule.importer;

import org.springframework.web.multipart.MultipartFile;

/**
 * Command classes (in MVC and specifically Spring parlance) refer to the
 * classes which hold the data that appear on the form before it is submitted.
 * 
 * @author Matthew Flower
 * @since December 21, 2005
 */
public class ScheduleUploadCommand {
	private MultipartFile scheduleFile = null;

	public MultipartFile getScheduleFile() {
		return scheduleFile;
	}

	public void setScheduleFile(MultipartFile scheduleFile) {
		this.scheduleFile = scheduleFile;
	}
}
