package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnCalendarDAO;
import net.project.hibernate.model.PnCalendar;

public class PnCalendarDAOImpl extends AbstractHibernateDAO<PnCalendar, BigDecimal> implements IPnCalendarDAO {

	public PnCalendarDAOImpl() {
		super(PnCalendar.class);
	}

}
