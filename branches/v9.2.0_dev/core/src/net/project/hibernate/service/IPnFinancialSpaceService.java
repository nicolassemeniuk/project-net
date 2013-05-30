package net.project.hibernate.service;

import java.util.Collection;

import net.project.financial.FinancialCreateWizard;
import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.model.PnFinancialSpace;

public interface IPnFinancialSpaceService {
	
	/**
	 * Saves a new financial space.
	 * @param financialSpaceBean the financial space to save.
	 * @return the id from the new financial space.
	 */
	public Integer saveFinancialSpace(FinancialCreateWizard financialSpaceBean);

	/**
	 * Obtain a financial space from an id.
	 * @param financialSpaceId the id from the financial space.
	 * @return a financial space.
	 */
	public PnFinancialSpace getFinancialSpace(Integer financialSpaceId);

	public PnFinancialSpaceList getFinancialSpacesByIds(Collection additionalSpaceIDCollection);
	
	
}
