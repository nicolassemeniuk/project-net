package net.project.hibernate.service;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnDocProvider;

public interface IPnDocProviderService {
	
	/**
	 * @param docProviderId for DocProvider we need to select from database
	 * @return PnDocProvider bean
	 */
	public PnDocProvider getDocProvider(BigDecimal docProviderId);
	
	/**
	 * Saves new DocProvider
	 * @param pnDocProvider object we want to save
	 * @return primary key for saved DocProvider
	 */
	public BigDecimal saveDocProvider(PnDocProvider pnDocProvider);
	
	/**
	 * Deletes DocProvider from database
	 * @param pnDocProvider object we want to delete
	 */
	public void deleteDocProvider(PnDocProvider pnDocProvider);
	
	/**
	 * Updates DocProvider
	 * @param pnDocProvider object we want to update
	 */
	public void updateDocProvider(PnDocProvider pnDocProvider);
	
	/**
	 * 
	 * @return list of default docProviderIds
	 */
	public List<PnDocProvider> getDocProviderIds();


}
