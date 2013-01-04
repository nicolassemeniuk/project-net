package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.service.IPnDocContainerService;

public class PnDocContainerServiceImpl implements IPnDocContainerService {

	private IPnDocContainerDAO pnDocContainerDAO;

	public void setPnDocContainerDAO(IPnDocContainerDAO pnDocContainerDAO) {
		this.pnDocContainerDAO = pnDocContainerDAO;
	}

	public PnDocContainer getDocContainer(BigDecimal docContainerId) {
		return pnDocContainerDAO.findByPimaryKey(docContainerId);
	}

	public BigDecimal saveDocContainer(PnDocContainer pnDocContainer) {
		return pnDocContainerDAO.create(pnDocContainer);
	}

	public void deleteDocContainer(PnDocContainer pnDocContainer) {
		pnDocContainerDAO.delete(pnDocContainer);
	}

	public void updateDocContainer(PnDocContainer pnDocContainer) {
		pnDocContainerDAO.update(pnDocContainer);
	}

}
