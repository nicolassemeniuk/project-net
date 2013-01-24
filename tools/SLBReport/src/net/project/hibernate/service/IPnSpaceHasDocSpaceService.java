package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;

public interface IPnSpaceHasDocSpaceService {
	
	
	/**
	 * @param pnSpaceHasDocSpaceId 
	 * @return PnSpaceHasDocSpace bean
	 */
	public PnSpaceHasDocSpace getSpaceHasDocSpace(PnSpaceHasDocSpacePK pnSpaceHasDocSpaceId);
	
	/**
	 * Saves new PnSpaceHasDocSpace
	 * @param PnSpaceHasDocSpace object we want to save
	 * @return primary key for saved space and person
	 */
	public PnSpaceHasDocSpacePK saveSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace);
	
	/**
	 * Deletes PnSpaceHasDocSpace from database
	 * @param PnSpaceHasDocSpace object we want to delete
	 */
	public void deleteSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace);
	
	/**
	 * Updates PnSpaceHasDocSpace
	 * @param PnSpaceHasDocSpace object we want to update
	 */
	public void updateSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace);

}
