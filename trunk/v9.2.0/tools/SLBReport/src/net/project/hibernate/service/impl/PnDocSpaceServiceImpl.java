package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnDocSpaceDAO;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.service.IPnDocSpaceService;

public class PnDocSpaceServiceImpl implements IPnDocSpaceService {

	private IPnDocSpaceDAO pnDocSpaceDAO;

	public void setPnDocSpaceDAO(IPnDocSpaceDAO pnDocSpaceDAO) {
		this.pnDocSpaceDAO = pnDocSpaceDAO;
	}

	public PnDocSpace getDocSpace(BigDecimal docSpaceId) {
		return pnDocSpaceDAO.findByPimaryKey(docSpaceId);
	}

	public BigDecimal saveDocSpace(PnDocSpace pnDocSpace) {
		return pnDocSpaceDAO.create(pnDocSpace);
	}

	public void deleteDocSpace(PnDocSpace pnDocSpace) {
		pnDocSpaceDAO.delete(pnDocSpace);
	}

	public void updateDocSpace(PnDocSpace pnDocSpace) {
		pnDocSpaceDAO.update(pnDocSpace);
	}

}
