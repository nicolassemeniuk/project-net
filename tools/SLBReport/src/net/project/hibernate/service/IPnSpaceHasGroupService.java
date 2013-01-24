package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;

public interface IPnSpaceHasGroupService {	

	/**
	 * @param pnGroupHasPersonId 
	 * @return PnGroupHasPerson bean
	 */
	public PnSpaceHasGroup getSpaceHasGroup(PnSpaceHasGroupPK pnSpaceHasGroupId);
	
	/**
	 * Saves new PnSpaceHasGroup
	 * @param PnSpaceHasGroup object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasGroupPK saveSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);
	
	/**
	 * Deletes PnSpaceHasGroup from database
	 * @param PnSpaceHasGroup object we want to delete
	 */
	public void deleteSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);
	
	/**
	 * Updates PnSpaceHasGroup
	 * @param PnSpaceHasGroup object we want to update
	 */
	public void updateSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);


}
