package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnGroupHasPersonDAO;
import net.project.hibernate.model.PnGroupHasPerson;
import net.project.hibernate.model.PnGroupHasPersonPK;

public class PnGroupHasPersonDAOImpl extends AbstractHibernateDAO<PnGroupHasPerson, PnGroupHasPersonPK> implements IPnGroupHasPersonDAO {

	public PnGroupHasPersonDAOImpl() {
		super(PnGroupHasPerson.class);
	}

}
