package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasModuleDAO;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.service.IPnSpaceHasModuleService;

public class PnSpaceHasModuleServiceImpl implements IPnSpaceHasModuleService {

	private IPnSpaceHasModuleDAO pnSpaceHasModuleDAO;

	public void setPnSpaceHasModuleDAO(IPnSpaceHasModuleDAO pnSpaceHasModuleDAO) {
		this.pnSpaceHasModuleDAO = pnSpaceHasModuleDAO;
	}

	public PnSpaceHasModule getSpaceHasModule(PnSpaceHasModulePK pnSpaceHasModulePK) {
		return pnSpaceHasModuleDAO.findByPimaryKey(pnSpaceHasModulePK);
	}

	public PnSpaceHasModulePK saveSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
		return pnSpaceHasModuleDAO.create(pnSpaceHasModule);
	}

	public void deleteSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
		pnSpaceHasModuleDAO.delete(pnSpaceHasModule);
	}

	public void updateSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
		pnSpaceHasModuleDAO.update(pnSpaceHasModule);
	}

}
