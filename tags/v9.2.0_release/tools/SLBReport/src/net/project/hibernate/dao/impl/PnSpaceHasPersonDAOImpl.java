package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;

public class PnSpaceHasPersonDAOImpl extends AbstractHibernateDAO<PnSpaceHasPerson, PnSpaceHasPersonPK> implements IPnSpaceHasPersonDAO {
	
	public PnSpaceHasPersonDAOImpl(){
		super(PnSpaceHasPerson.class);
	}


}
