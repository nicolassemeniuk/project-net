package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;
import net.project.hibernate.service.IPnObjectPermissionService;

public class PnObjectPermissionServiceImpl implements IPnObjectPermissionService {

	private IPnObjectPermissionDAO pnObjectPermissionDAO;

	public void setPnObjectPermissionDAO(IPnObjectPermissionDAO pnObjectPermissionDAO) {
		this.pnObjectPermissionDAO = pnObjectPermissionDAO;
	}

	public PnObjectPermission getObjectPermission(PnObjectPermissionPK pnObjectPermissionId) {
		return pnObjectPermissionDAO.findByPimaryKey(pnObjectPermissionId);
	}

	public PnObjectPermissionPK saveObjectPermission(PnObjectPermission pnObjectPermission) {
		return pnObjectPermissionDAO.create(pnObjectPermission);
	}

	public void deleteObjectPermission(PnObjectPermission pnObjectPermission) {
		pnObjectPermissionDAO.delete(pnObjectPermission);
	}

	public void updateObjectPermission(PnObjectPermission pnObjectPermission) {
		pnObjectPermissionDAO.update(pnObjectPermission);
	}

}
