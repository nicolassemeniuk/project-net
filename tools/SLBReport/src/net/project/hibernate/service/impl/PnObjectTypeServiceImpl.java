package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnObjectTypeDAO;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.service.IPnObjectTypeService;

public class PnObjectTypeServiceImpl implements IPnObjectTypeService {

	private IPnObjectTypeDAO pnObjectTypeDAO;

	public void setPnObjectTypeDAO(IPnObjectTypeDAO pnObjectTypeDAO) {
		this.pnObjectTypeDAO = pnObjectTypeDAO;
	}

	public PnObjectType getObjectType(String pnObjectTypeId) {
		return pnObjectTypeDAO.findByPimaryKey(pnObjectTypeId);
	}

	public String saveObjectType(PnObjectType pnObjectType) {
		return pnObjectTypeDAO.create(pnObjectType);
	}

	public void deleteObjectType(PnObjectType pnObjectType) {
		pnObjectTypeDAO.delete(pnObjectType);
	}

	public void updateObjectType(PnObjectType pnObjectType) {
		pnObjectTypeDAO.update(pnObjectType);
	}

	public List<PnObjectType> findObjectTypes() {
		return pnObjectTypeDAO.findObjectTypes();
	}

}
