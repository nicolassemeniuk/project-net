package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnDocProviderHasDocSpaceDAO;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.service.IPnDocProviderHasDocSpaceService;

public class PnDocProviderHasDocSpaceServiceImpl implements IPnDocProviderHasDocSpaceService {

	private IPnDocProviderHasDocSpaceDAO pnDocProviderHasDocSpaceDAO;

	public void setPnDocProviderHasDocSpaceDAO(IPnDocProviderHasDocSpaceDAO pnDocProviderHasDocSpaceDAO) {
		this.pnDocProviderHasDocSpaceDAO = pnDocProviderHasDocSpaceDAO;
	}

	public PnDocProviderHasDocSpace getDocProviderHasDocSpace(PnDocProviderHasDocSpacePK pnDocProviderHasDocSpaceId) {
		return pnDocProviderHasDocSpaceDAO.findByPimaryKey(pnDocProviderHasDocSpaceId);
	}

	public PnDocProviderHasDocSpacePK saveDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace) {
		return pnDocProviderHasDocSpaceDAO.create(pnDocProviderHasDocSpace);
	}

	public void deleteDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace) {
		pnDocProviderHasDocSpaceDAO.delete(pnDocProviderHasDocSpace);
	}

	public void updateDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace) {
		pnDocProviderHasDocSpaceDAO.update(pnDocProviderHasDocSpace);
	}

}
