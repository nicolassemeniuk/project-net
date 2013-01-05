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

import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;

public interface IPnSpaceHasCalendarService {
	
	/**
	 * @param pnGroupHasPersonId 
	 * @return PnGroupHasPerson bean
	 */
	public PnSpaceHasCalendar getSpaceHasCalendar(PnSpaceHasCalendarPK pnSpaceHasCalendarId);
	
	/**
	 * Saves new PnSpaceHasCalendar
	 * @param PnSpaceHasCalendar object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasCalendarPK saveSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);
	
	/**
	 * Deletes PnSpaceHasCalendar from database
	 * @param PnSpaceHasCalendar object we want to delete
	 */
	public void deleteSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);
	
	/**
	 * Updates PnSpaceHasCalendar
	 * @param PnSpaceHasCalendar object we want to update
	 */
	public void updateSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);


}
