package net.project.hibernate.service;

import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;

public interface IPnDocProviderHasDocSpaceService {
	
	/**
	 * @param pnDocProviderHasDocSpaceId 
	 * @return PnDocProviderHasDocSpace bean
	 */
	public PnDocProviderHasDocSpace getDocProviderHasDocSpace(PnDocProviderHasDocSpacePK pnDocProviderHasDocSpaceId);
	
	/**
	 * Saves new PnDocProviderHasDocSpace
	 * @param PnDocProviderHasDocSpace object we want to save
	 * @return primary key for saved space and person
	 */
	public PnDocProviderHasDocSpacePK saveDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace);
	
	/**
	 * Deletes PnDocProviderHasDocSpace from database
	 * @param PnDocProviderHasDocSpace object we want to delete
	 */
	public void deleteDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace);
	
	/**
	 * Updates PnDocProviderHasDocSpace
	 * @param PnDocProviderHasDocSpace object we want to update
	 */
	public void updateDocProviderHasDocSpace(PnDocProviderHasDocSpace pnDocProviderHasDocSpace);

}
