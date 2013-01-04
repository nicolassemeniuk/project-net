package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnDocProviderHasDocSpaceDAO;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;

public class PnDocProviderHasDocSpaceDAOImpl extends AbstractHibernateDAO<PnDocProviderHasDocSpace, PnDocProviderHasDocSpacePK> implements IPnDocProviderHasDocSpaceDAO {

	public PnDocProviderHasDocSpaceDAOImpl() {
		super(PnDocProviderHasDocSpace.class);
	}

}
