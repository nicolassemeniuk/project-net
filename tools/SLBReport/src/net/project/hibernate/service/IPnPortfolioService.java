package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnPortfolio;

public interface IPnPortfolioService {
	
	/**
	 * @param personId for person we need to select from database
	 * @return PnPerson bean
	 */
	public PnPortfolio getPortfolio(BigDecimal PortfolioId);
	
	/**
	 * Saves new Portfolio
	 * @param pnPortfolio object we want to save
	 * @return primary key for saved Portfolio
	 */
	public BigDecimal savePortfolio(PnPortfolio pnPortfolio);
	
	/**
	 * Deletes Portfolio from database
	 * @param pnPortfolio object we want to delete
	 */
	public void deletePortfolio(PnPortfolio pnPortfolio);
	
	/**
	 * Updates Portfolio
	 * @param pnPortfolio object we want to update
	 */
	public void updatePortfolio(PnPortfolio pnPortfolio);

}
