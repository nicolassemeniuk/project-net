package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnDocSpace;

public interface IPnDocSpaceService {
	
	/**
	 * @param docSpaceId for DocSpace we need to select from database
	 * @return PnDocSpace bean
	 */
	public PnDocSpace getDocSpace(BigDecimal docSpaceId);
	
	/**
	 * Saves new DocSpace
	 * @param pnDocSpace object we want to save
	 * @return primary key for saved DocSpace
	 */
	public BigDecimal saveDocSpace(PnDocSpace pnDocSpace);
	
	/**
	 * Deletes DocSpace from database
	 * @param pnDocSpace object we want to delete
	 */
	public void deleteDocSpace(PnDocSpace pnDocSpace);
	
	/**
	 * Updates DocSpace
	 * @param pnDocSpace object we want to update
	 */
	public void updateDocSpace(PnDocSpace pnDocSpace);

}
