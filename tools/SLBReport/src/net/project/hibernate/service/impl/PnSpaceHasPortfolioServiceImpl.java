package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnSpaceHasPortfolioDAO;
import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;
import net.project.hibernate.service.IPnSpaceHasPortfolioService;

public class PnSpaceHasPortfolioServiceImpl implements IPnSpaceHasPortfolioService {
	
	
	private IPnSpaceHasPortfolioDAO pnSpaceHasPortfolioDAO;	

	public void setPnSpaceHasPortfolioDAO(IPnSpaceHasPortfolioDAO pnSpaceHasPortfolioDAO) {
		this.pnSpaceHasPortfolioDAO = pnSpaceHasPortfolioDAO;
	}

	public PnSpaceHasPortfolio getSpaceHasPortfolio(PnSpaceHasPortfolioPK pnSpaceHasPortfolioId) {
		return pnSpaceHasPortfolioDAO.findByPimaryKey(pnSpaceHasPortfolioId);
	}

	public PnSpaceHasPortfolioPK saveSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio) {
		return pnSpaceHasPortfolioDAO.create(pnSpaceHasPortfolio);
	}

	public void deleteSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio) {
		pnSpaceHasPortfolioDAO.delete(pnSpaceHasPortfolio);

	}

	public void updateSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio) {
		pnSpaceHasPortfolioDAO.update(pnSpaceHasPortfolio);
	}

}
