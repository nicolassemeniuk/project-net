package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.service.IPnSpaceHasPersonService;

public class PnSpaceHasPersonServiceImpl implements IPnSpaceHasPersonService {
	
	private IPnSpaceHasPersonDAO pnSpaceHasPersonDAO;

	public void setPnSpaceHasPersonDAO(IPnSpaceHasPersonDAO pnSpaceHasPersonDAO) {
		this.pnSpaceHasPersonDAO = pnSpaceHasPersonDAO;
	}

	public PnSpaceHasPerson getSpaceHasPerson(PnSpaceHasPersonPK pnSpaceHasPersonId) {
		return pnSpaceHasPersonDAO.findByPimaryKey(pnSpaceHasPersonId);
	}

	public PnSpaceHasPersonPK saveSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		return pnSpaceHasPersonDAO.create(pnSpaceHasPerson);
	}

	public void deleteSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		pnSpaceHasPersonDAO.delete(pnSpaceHasPerson);

	}

	public void updateSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		pnSpaceHasPersonDAO.update(pnSpaceHasPerson);

	}

}
