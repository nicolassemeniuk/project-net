package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonProfileDAO;
import net.project.hibernate.model.PnPersonProfile;
import net.project.hibernate.service.IPnPersonProfileService;

public class PnPersonProfileServiceImpl implements IPnPersonProfileService {
	
	private IPnPersonProfileDAO pnPersonProfileDAO; 

	public void setPnPersonProfileDAO(IPnPersonProfileDAO pnPersonProfileDAO) {
		this.pnPersonProfileDAO = pnPersonProfileDAO;
	}

	public PnPersonProfile getPersonProfile(BigDecimal personProfileId) {
		return pnPersonProfileDAO.findByPimaryKey(personProfileId);
	}

	public BigDecimal savePersonProfile(PnPersonProfile pnPersonProfile) {
		return pnPersonProfileDAO.create(pnPersonProfile);
	}

	public void deletePersonProfile(PnPersonProfile pnPersonProfile) {
		pnPersonProfileDAO.delete(pnPersonProfile);
	}

	public void updatePersonProfile(PnPersonProfile pnPersonProfile) {
		pnPersonProfileDAO.update(pnPersonProfile);
	}

}
