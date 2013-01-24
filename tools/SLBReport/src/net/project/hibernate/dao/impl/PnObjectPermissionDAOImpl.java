package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;

public class PnObjectPermissionDAOImpl extends AbstractHibernateDAO<PnObjectPermission, PnObjectPermissionPK> implements IPnObjectPermissionDAO {

	public PnObjectPermissionDAOImpl() {
		super(PnObjectPermission.class);
	}

}
