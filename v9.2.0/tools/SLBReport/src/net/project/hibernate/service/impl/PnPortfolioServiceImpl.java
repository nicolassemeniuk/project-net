package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.service.IPnPortfolioService;

public class PnPortfolioServiceImpl implements IPnPortfolioService {
	
	/**
	 * PnPortfolio data access object
	 */
	private IPnPortfolioDAO pnPortfolioDAO;

	/**
	 * sets PnPortfolio data acces object
	 * @param pnPortfolioDAO
	 */
	public void setPnPortfolioDAO(IPnPortfolioDAO pnPortfolioDAO) {
		this.pnPortfolioDAO = pnPortfolioDAO;
	}

	/**
	 * gets PnPortfolio object for given id
	 */
	public PnPortfolio getPortfolio(BigDecimal PortfolioId) {
		return pnPortfolioDAO.findByPimaryKey(PortfolioId);
	}

	/**
	 * saves pnPortfolio object
	 */
	public BigDecimal savePortfolio(PnPortfolio pnPortfolio) {
		return pnPortfolioDAO.create(pnPortfolio);
	}

	/**
	 * deletes pnPortfolio object
	 */
	public void deletePortfolio(PnPortfolio pnPortfolio) {
		pnPortfolioDAO.delete(pnPortfolio);
	}

	/**
	 * updates pnPortfolio
	 */	
	public void updatePortfolio(PnPortfolio pnPortfolio) {
		pnPortfolioDAO.update(pnPortfolio);
	}


}
