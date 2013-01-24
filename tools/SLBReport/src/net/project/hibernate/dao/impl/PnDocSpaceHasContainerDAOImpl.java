package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnDocSpaceHasContainerDAO;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;

public class PnDocSpaceHasContainerDAOImpl extends AbstractHibernateDAO<PnDocSpaceHasContainer, PnDocSpaceHasContainerPK> implements IPnDocSpaceHasContainerDAO {

	public PnDocSpaceHasContainerDAOImpl() {
		super(PnDocSpaceHasContainer.class);
	}

}
