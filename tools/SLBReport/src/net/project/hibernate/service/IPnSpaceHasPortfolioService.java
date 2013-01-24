package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;

public interface IPnSpaceHasPortfolioService {
	
	/**
	 * @param pnSpaceHasPortfolioId 
	 * @return PnSpaceHasPortfolio bean
	 */
	public PnSpaceHasPortfolio getSpaceHasPortfolio(PnSpaceHasPortfolioPK pnSpaceHasPortfolioId);
	
	/**
	 * Saves new PnSpaceHasPortfolio
	 * @param PnSpaceHasPortfolio object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasPortfolioPK saveSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);
	
	/**
	 * Deletes PnSpaceHasPortfolio from database
	 * @param PnSpaceHasPortfolio object we want to delete
	 */
	public void deleteSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);
	
	/**
	 * Updates PnSpaceHasPortfolio
	 * @param PnSpaceHasPortfolio object we want to update
	 */
	public void updateSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);

}
