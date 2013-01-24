package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasDocSpaceDAO;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;

public class PnSpaceHasDocSpaceDAOImpl extends AbstractHibernateDAO<PnSpaceHasDocSpace, PnSpaceHasDocSpacePK> implements IPnSpaceHasDocSpaceDAO {

	public PnSpaceHasDocSpaceDAOImpl() {
		super(PnSpaceHasDocSpace.class);
	}

}
