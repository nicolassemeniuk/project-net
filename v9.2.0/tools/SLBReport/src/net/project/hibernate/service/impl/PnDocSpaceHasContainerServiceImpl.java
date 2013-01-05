package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnDocSpaceHasContainerDAO;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.service.IPnDocSpaceHasContainerService;

public class PnDocSpaceHasContainerServiceImpl implements IPnDocSpaceHasContainerService {

	private IPnDocSpaceHasContainerDAO pnDocSpaceHasContainerDAO;

	public void setPnDocSpaceHasContainerDAO(IPnDocSpaceHasContainerDAO pnDocSpaceHasContainerDAO) {
		this.pnDocSpaceHasContainerDAO = pnDocSpaceHasContainerDAO;
	}

	public PnDocSpaceHasContainer getDocSpaceHasContainer(PnDocSpaceHasContainerPK pnDocSpaceHasContainerId) {
		return pnDocSpaceHasContainerDAO.findByPimaryKey(pnDocSpaceHasContainerId);
	}

	public PnDocSpaceHasContainerPK saveDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
		return pnDocSpaceHasContainerDAO.create(pnDocSpaceHasContainer);
	}

	public void deleteDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
		pnDocSpaceHasContainerDAO.delete(pnDocSpaceHasContainer);
	}

	public void updateDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
		pnDocSpaceHasContainerDAO.update(pnDocSpaceHasContainer);
	}

}
