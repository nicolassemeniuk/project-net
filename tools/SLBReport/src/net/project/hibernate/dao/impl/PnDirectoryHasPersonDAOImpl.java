package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnDirectoryHasPersonDAO;
import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;

public class PnDirectoryHasPersonDAOImpl extends AbstractHibernateDAO<PnDirectoryHasPerson, PnDirectoryHasPersonPK> implements IPnDirectoryHasPersonDAO {

	public PnDirectoryHasPersonDAOImpl() {
		super(PnDirectoryHasPerson.class);
	}

}
