package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnModulePermissionDAO;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;
import net.project.hibernate.service.IPnModulePermissionService;

public class PnModulePermissionServiceImpl implements IPnModulePermissionService {
	
	private IPnModulePermissionDAO pnModulePermissionDAO;	

	public void setPnModulePermissionDAO(IPnModulePermissionDAO pnModulePermissionDAO) {
		this.pnModulePermissionDAO = pnModulePermissionDAO;
	}

	public PnModulePermission getModulePermission(PnModulePermissionPK pnModulePermissionId) {
		return pnModulePermissionDAO.findByPimaryKey(pnModulePermissionId);
	}

	public PnModulePermissionPK saveModulePermission(PnModulePermission pnModulePermission) {
		return pnModulePermissionDAO.create(pnModulePermission);
	}

	public void deleteModulePermission(PnModulePermission pnModulePermission) {
		pnModulePermissionDAO.delete(pnModulePermission);
	}

	public void updateModulePermission(PnModulePermission pnModulePermission) {
		pnModulePermissionDAO.update(pnModulePermission);
	}

}
