package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnDirectoryHasPersonDAO;
import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;
import net.project.hibernate.service.IPnDirectoryHasPersonService;

public class PnDirectoryHasPersonServiceImpl implements IPnDirectoryHasPersonService {
	
	private IPnDirectoryHasPersonDAO pnDirectoryHasPersonDAO;	

	public void setPnDirectoryHasPersonDAO(IPnDirectoryHasPersonDAO pnDirectoryHasPersonDAO) {
		this.pnDirectoryHasPersonDAO = pnDirectoryHasPersonDAO;
	}

	public PnDirectoryHasPerson getDirectoryHasPerson(PnDirectoryHasPersonPK directoryHasPersonPK) {
		return pnDirectoryHasPersonDAO.findByPimaryKey(directoryHasPersonPK);
	}

	public PnDirectoryHasPersonPK saveDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson) {
		return pnDirectoryHasPersonDAO.create(pnDirectoryHasPerson);
	}

	public void deleteDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson) {
		pnDirectoryHasPersonDAO.delete(pnDirectoryHasPerson);
	}

	public void updateDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson) {
		pnDirectoryHasPersonDAO.update(pnDirectoryHasPerson);
	}

}
