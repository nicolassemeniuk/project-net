package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.service.IPnDefaultObjectPermissionService;

public class PnDefaultObjectPermissionServiceImpl implements IPnDefaultObjectPermissionService {

	public IPnDefaultObjectPermissionDAO pnDefaultObjectPermissionDAO;

	public void setPnDefaultObjectPermissionDAO(IPnDefaultObjectPermissionDAO pnDefaultObjectPermissionDAO) {
		this.pnDefaultObjectPermissionDAO = pnDefaultObjectPermissionDAO;
	}

	public PnDefaultObjectPermission getDefaultObjectPermission(PnDefaultObjectPermissionPK pnDefaultObjectPermissionId) {
		return pnDefaultObjectPermissionDAO.findByPimaryKey(pnDefaultObjectPermissionId);
	}

	public PnDefaultObjectPermissionPK saveDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
		return pnDefaultObjectPermissionDAO.create(pnDefaultObjectPermission);
	}

	public void deleteDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
		pnDefaultObjectPermissionDAO.delete(pnDefaultObjectPermission);
	}

	public void updateDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
		pnDefaultObjectPermissionDAO.update(pnDefaultObjectPermission);
	}

	public Iterator getObjectPermisions(BigDecimal spaceId, String objectType) {
		return pnDefaultObjectPermissionDAO.getObjectPermisions(spaceId, objectType);
	}

}
