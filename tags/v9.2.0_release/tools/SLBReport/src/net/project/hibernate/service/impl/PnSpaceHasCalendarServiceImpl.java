package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasCalendarDAO;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;
import net.project.hibernate.service.IPnSpaceHasCalendarService;

public class PnSpaceHasCalendarServiceImpl implements IPnSpaceHasCalendarService {

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
