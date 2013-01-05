package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnCalendarDAO;
import net.project.hibernate.model.PnCalendar;
import net.project.hibernate.service.IPnCalendarService;

public class PnCalendarServiceImpl implements IPnCalendarService {

	private IPnCalendarDAO pnCalendarDAO;

	public void setPnCalendarDAO(IPnCalendarDAO pnCalendarDAO) {
		this.pnCalendarDAO = pnCalendarDAO;
	}

	public PnCalendar getCalendar(BigDecimal calendarId) {
		return pnCalendarDAO.findByPimaryKey(calendarId);
	}

	public BigDecimal saveCalendar(PnCalendar pnCalendar) {
		return pnCalendarDAO.create(pnCalendar);
	}

	public void deleteCalendar(PnCalendar pnCalendar) {
		pnCalendarDAO.delete(pnCalendar);
	}

	public void updateCalendar(PnCalendar pnCalendar) {
		pnCalendarDAO.update(pnCalendar);
	}

}
