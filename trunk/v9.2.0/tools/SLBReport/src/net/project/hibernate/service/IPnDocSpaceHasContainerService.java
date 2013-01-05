package net.project.hibernate.service;

import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;

public interface IPnDocSpaceHasContainerService {
	

	/**
	 * @param pnDocSpaceHasContainerId 
	 * @return PnDocSpaceHasContainer bean
	 */
	public PnDocSpaceHasContainer getDocSpaceHasContainer(PnDocSpaceHasContainerPK pnDocSpaceHasContainerId);
	
	/**
	 * Saves new PnDocSpaceHasContainer
	 * @param PnDocSpaceHasContainer object we want to save
	 * @return primary key for saved PnDocSpaceHasContainer bean
	 */
	public PnDocSpaceHasContainerPK saveDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);
	
	/**
	 * Deletes PnDocSpaceHasContainer from database
	 * @param PnDocSpaceHasContainer object we want to delete
	 */
	public void deleteDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);
	
	/**
	 * Updates PnDocSpaceHasContainer
	 * @param PnDocSpaceHasContainer object we want to update
	 */
	public void updateDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);


}
