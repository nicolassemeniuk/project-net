package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasCalendarDAO;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;

public class PnSpaceHasCalendarDAOImpl extends AbstractHibernateDAO<PnSpaceHasCalendar, PnSpaceHasCalendarPK> implements IPnSpaceHasCalendarDAO {

	public PnSpaceHasCalendarDAOImpl() {
		super(PnSpaceHasCalendar.class);
	}

}
