package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasModuleDAO;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;

public class PnSpaceHasModuleDAOImpl extends AbstractHibernateDAO<PnSpaceHasModule, PnSpaceHasModulePK> implements IPnSpaceHasModuleDAO {

	public PnSpaceHasModuleDAOImpl() {
		super(PnSpaceHasModule.class);
	}

}
