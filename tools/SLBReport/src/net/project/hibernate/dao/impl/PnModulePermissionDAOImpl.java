package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnModulePermissionDAO;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;

public class PnModulePermissionDAOImpl extends AbstractHibernateDAO<PnModulePermission, PnModulePermissionPK> implements IPnModulePermissionDAO {

	public PnModulePermissionDAOImpl() {
		super(PnModulePermission.class);
	}

}
