package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasGroupDAO;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;

public class PnSpaceHasGroupDAOImpl extends AbstractHibernateDAO<PnSpaceHasGroup, PnSpaceHasGroupPK> implements IPnSpaceHasGroupDAO {

	public PnSpaceHasGroupDAOImpl() {
		super(PnSpaceHasGroup.class);
	}

}
