package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.service.IPnModuleService;

public class PnModuleServiceImpl implements IPnModuleService {

	private IPnModuleDAO pnModuleDAO;

	public void setPnModuleDAO(IPnModuleDAO pnModuleDAO) {
		this.pnModuleDAO = pnModuleDAO;
	}

	public PnModule getModule(BigDecimal moduleId) {
		return pnModuleDAO.findByPimaryKey(moduleId);
	}

	public BigDecimal saveModule(PnModule pnModule) {
		return pnModuleDAO.create(pnModule);
	}

	public void deleteModule(PnModule pnModule) {
		pnModuleDAO.delete(pnModule);

	}

	public void updateModule(PnModule pnModule) {
		pnModuleDAO.update(pnModule);

	}

	public List<PnModule> getModuleIds() {
		return pnModuleDAO.getModuleIds();
	}

	public List<PnModule> getModuleDefaultPermissions(BigDecimal spaceId) {
		return pnModuleDAO.getModuleDefaultPermissions(spaceId);
	}

}
