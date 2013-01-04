package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasGroupDAO;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.service.IPnSpaceHasGroupService;

public class PnSpaceHasGroupServiceImpl implements IPnSpaceHasGroupService {

	public IPnSpaceHasGroupDAO pnSpaceHasGroupDAO;

	public void setPnSpaceHasGroupDAO(IPnSpaceHasGroupDAO pnSpaceHasGroupDAO) {
		this.pnSpaceHasGroupDAO = pnSpaceHasGroupDAO;
	}

	public PnSpaceHasGroup getSpaceHasGroup(PnSpaceHasGroupPK pnSpaceHasGroupId) {
		return pnSpaceHasGroupDAO.findByPimaryKey(pnSpaceHasGroupId);
	}

	public PnSpaceHasGroupPK saveSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
		return pnSpaceHasGroupDAO.create(pnSpaceHasGroup);
	}

	public void deleteSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
		pnSpaceHasGroupDAO.delete(pnSpaceHasGroup);
	}

	public void updateSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
		pnSpaceHasGroupDAO.update(pnSpaceHasGroup);
	}

}
