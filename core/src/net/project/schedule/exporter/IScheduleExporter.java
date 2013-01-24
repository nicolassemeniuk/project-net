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
package net.project.schedule.exporter;

import java.io.ByteArrayOutputStream;

import net.project.schedule.Schedule;
import net.project.soa.schedule.Project;
import net.project.soa.schedule.Project.Tasks;

public interface IScheduleExporter {
    /**
     * Set the schedule that the schedule entries will be imported into.
     *
     * @param schedule the <code>Schedule</code> that the schedule entries will
     * be imported into.
     */
    public void setSchedule(Schedule schedule);
    
    public ByteArrayOutputStream getXml() throws Exception;
    
    public String getProjectName();
    
    public Project getProjectData();
    
    /**
     * Method that retrieves the CSV format of all the Tasks on a determinate project
     * @return Tasks
     * @throws Exception
     */
    public Tasks getCsv() throws Exception;
}
