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
package net.project.hibernate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnSpaceHasCalendarDAO;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;
import net.project.hibernate.service.IPnSpaceHasCalendarService;

@Service(value="pnSpaceHasCalendarService")
public class PnSpaceHasCalendarServiceImpl implements IPnSpaceHasCalendarService {

	@Autowired
	private IPnSpaceHasCalendarDAO pnSpaceHasCalendarDAO;

	public void setPnSpaceHasCalendarDAO(IPnSpaceHasCalendarDAO pnSpaceHasCalendarDAO) {
		this.pnSpaceHasCalendarDAO = pnSpaceHasCalendarDAO;
	}

	public void deleteSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar) {
		pnSpaceHasCalendarDAO.delete(pnSpaceHasCalendar);
	}

	public PnSpaceHasCalendar getSpaceHasCalendar(PnSpaceHasCalendarPK pnSpaceHasCalendarId) {
		return pnSpaceHasCalendarDAO.findByPimaryKey(pnSpaceHasCalendarId);
	}

	public PnSpaceHasCalendarPK saveSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar) {
		return pnSpaceHasCalendarDAO.create(pnSpaceHasCalendar);
	}

	public void updateSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar) {
		pnSpaceHasCalendarDAO.update(pnSpaceHasCalendar);
	}

}
