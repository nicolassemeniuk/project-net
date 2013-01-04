package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.IPnObjectService;

public class PnObjectServiceImpl implements IPnObjectService {
	
	
	/**
	 * PnObject database access object
	 */
	private IPnObjectDAO pnObjectDAO;

	/**
	 * Sets the PnObject data access object
	 * @param pnObjectDao
	 */
	public void setPnObjectDAO(IPnObjectDAO pnObjectDAO) {
		this.pnObjectDAO = pnObjectDAO;
	}

	/**
	 *  get PnObject bean by primary key
	 */
	public PnObject getObject(BigDecimal primaryKey) {
		return pnObjectDAO.findByPimaryKey(primaryKey);
	}

	/**
	 *  create new object
	 */
	public BigDecimal saveObject(PnObject pnObject) {
		return pnObjectDAO.create(pnObject);
	}



}
