package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasDocSpaceDAO;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.service.IPnSpaceHasDocSpaceService;

public class PnSpaceHasDocSpaceServiceImpl implements IPnSpaceHasDocSpaceService {

	private IPnSpaceHasDocSpaceDAO pnSpaceHasDocSpaceDAO;

	public void setPnSpaceHasDocSpaceDAO(IPnSpaceHasDocSpaceDAO pnSpaceHasDocSpaceDAO) {
		this.pnSpaceHasDocSpaceDAO = pnSpaceHasDocSpaceDAO;
	}

	public PnSpaceHasDocSpace getSpaceHasDocSpace(PnSpaceHasDocSpacePK pnSpaceHasDocSpaceId) {
		return pnSpaceHasDocSpaceDAO.findByPimaryKey(pnSpaceHasDocSpaceId);
	}

	public PnSpaceHasDocSpacePK saveSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
		return pnSpaceHasDocSpaceDAO.create(pnSpaceHasDocSpace);
	}

	public void deleteSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
		pnSpaceHasDocSpaceDAO.delete(pnSpaceHasDocSpace);
	}

	public void updateSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
		pnSpaceHasDocSpaceDAO.update(pnSpaceHasDocSpace);
	}

}
