package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnDocContainer;

public interface IPnDocContainerService {

	/**
	 * @param docContainerId for DocContainer we need to select from database
	 * @return PnDocContainer bean
	 */
	public PnDocContainer getDocContainer(BigDecimal docContainerId);
	
	/**
	 * Saves new DocContainer
	 * @param pnDocContainer object we want to save
	 * @return primary key for saved DocContainer
	 */
	public BigDecimal saveDocContainer(PnDocContainer pnDocContainer);
	
	/**
	 * Deletes DocContainer from database
	 * @param pnDocContainer object we want to delete
	 */
	public void deleteDocContainer(PnDocContainer pnDocContainer);
	
	/**
	 * Updates DocContainer
	 * @param pnDocContainer object we want to update
	 */
	public void updateDocContainer(PnDocContainer pnDocContainer);
	
}
