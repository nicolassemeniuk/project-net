package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;

public class PnPersonServiceImpl implements IPnPersonService {

	/**
	 * PnPerson data access object
	 */
	private IPnPersonDAO pnPersonDAO;

	/**
	 * sets PnPerson data acces object
	 * @param pnPersonDAO
	 */
	public void setPnPersonDAO(IPnPersonDAO pnPersonDAO) {
		this.pnPersonDAO = pnPersonDAO;
	}

	/**
	 * gets PnPerson object for given id
	 */
	public PnPerson getPerson(BigDecimal personId) {
		return pnPersonDAO.findByPimaryKey(personId);
	}

	/**
	 * saves pnPerson object
	 */
	public BigDecimal savePerson(PnPerson pnPerson) {
		return pnPersonDAO.create(pnPerson);
	}

	/**
	 * deletes pnPerson object
	 */
	public void deletePerson(PnPerson pnPerson) {
		pnPersonDAO.delete(pnPerson);
	}

	/**
	 * updates pnPerson
	 */	
	public void updatePerson(PnPerson pnPerson) {
		pnPersonDAO.update(pnPerson);
	}

}
