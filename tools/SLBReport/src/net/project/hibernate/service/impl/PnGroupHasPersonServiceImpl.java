package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnGroupHasPersonDAO;
import net.project.hibernate.model.PnGroupHasPerson;
import net.project.hibernate.model.PnGroupHasPersonPK;
import net.project.hibernate.service.IPnGroupHasPersonService;

public class PnGroupHasPersonServiceImpl implements IPnGroupHasPersonService {
	
	private IPnGroupHasPersonDAO pnGroupHasPersonDAO;
	

	public void setPnGroupHasPersonDAO(IPnGroupHasPersonDAO pnGroupHasPersonDAO) {
		this.pnGroupHasPersonDAO = pnGroupHasPersonDAO;
	}

	public PnGroupHasPerson getGroupHasPerson(PnGroupHasPersonPK pnGroupHasPersonId) {
		return pnGroupHasPersonDAO.findByPimaryKey(pnGroupHasPersonId);
	}

	public PnGroupHasPersonPK saveGroupHasPerson(PnGroupHasPerson pnGroupHasPerson) {
		return pnGroupHasPersonDAO.create(pnGroupHasPerson);
	}

	public void deleteGroupHasPerson(PnGroupHasPerson pnGroupHasPerson) {
		pnGroupHasPersonDAO.delete(pnGroupHasPerson);
	}

	public void updateGroupHasPerson(PnGroupHasPerson pnGroupHasPerson) {
		pnGroupHasPersonDAO.update(pnGroupHasPerson);
	}

}
