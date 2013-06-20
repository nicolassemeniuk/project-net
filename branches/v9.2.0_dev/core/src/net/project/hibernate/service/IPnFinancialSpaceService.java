package net.project.hibernate.service;

import java.util.ArrayList;

import net.project.financial.FinancialCreateWizard;
import net.project.financial.FinancialSpace;
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
	 * Saves a financial space on the database.
	 * @param financialSpace the financial space to save.
	 */
	public void saveFinancialSpace(FinancialSpace financialSpace);

	/**
	 * Obtain a financial space from an id.
	 * @param financialSpaceId the id from the financial space.
	 * @return a financial space.
	 */
	public PnFinancialSpace getFinancialSpace(String financialSpaceId);

	/**
	 * Get a list of financial spaces from an id collection. Returns only Active financial spaces.
	 * @param additionalSpaceIDCollection the collection of id's.
	 * @return a list of financial spaces.
	 */
	public PnFinancialSpaceList getFinancialSpacesByIds(ArrayList<Integer> additionalSpaceIDCollection);
	
	/**
	 * Disables a financial space. This means mark the space record status as "D". And disable all the space to space relationships.
	 * @param financialSpaceId the id of the financial space to disable.
	 */
	public void disableFinancialSpace(String financialSpaceId);

	/**
	 * Activates a financial space. This means mark the space record status as "A". And activate all the space to space relationships.
	 * @param financialSpaceId
	 */
	public void activateFinancialSpace(String financialSpaceId);


}
