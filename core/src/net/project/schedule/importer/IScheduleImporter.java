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

import java.sql.SQLException;
import java.util.Collection;

import net.project.schedule.Schedule;

/**
 * Interface which describes a class that can import schedule tasks into the
 * database.
 * 
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface IScheduleImporter {
	/**
	 * Gets the name of the file we are importing.
	 * 
	 * @return a <code>String</code> value containing the absolute filename of
	 *         the file we are importing.
	 */
	public String getFileName();

	/**
	 * Set the name of file that we are importing.
	 * 
	 * @param fileName
	 *            a <code>String</code> value containing the name of the file
	 *            we are importing.
	 */
	public void setFileName(String fileName);

	/**
	 * For file types that contain more than one project, such as MS Project
	 * MPD_SUPPORTED files, this returns the list of all available project
	 * names. For most types, this would return only a single name.
	 * 
	 * @return a <code>String</code> containing HTML to render the options of
	 *         an html option list.
	 * @throws SQLException
	 *             if there is an error loading these options.
	 * @throws ImportException
	 *             if unhandled values are encountered in the loaded options
	 */
	public String getProjectNameHTMLOptionList() throws SQLException, ImportException;

	/**
	 * Get a collection of the resources that present in the schedule.
	 * 
	 * @return a <code>Collection</code> of resource objects.
	 * @throws SQLException
	 *             if an error occurs loading this list.
	 */
	public Collection getResources() throws SQLException;

	/**
	 * Set the schedule that the schedule entries will be imported into.
	 * 
	 * @param schedule
	 *            the <code>Schedule</code> that the schedule entries will be
	 *            imported into.
	 */
	public void setSchedule(Schedule schedule);

	public void init() throws ImportException;
	
	public void loadResources();

}
