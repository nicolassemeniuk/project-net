package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnGroup;

public interface IPnGroupService {
	
	/**
	 * @param groupId for PnGroup we need to select from database
	 * @return PnGroup bean
	 */
	public PnGroup getGroup(BigDecimal groupId);
	
	/**
	 * Saves new Group
	 * @param pnGroup object we want to save
	 * @return primary key for saved Group
	 */
	public BigDecimal saveGroup(PnGroup pnGroup);
	
	/**
	 * Deletes Group from database
	 * @param pnGroup object we want to delete
	 */
	public void deleteGroup(PnGroup pnGroup);
	
	/**
	 * Updates Group
	 * @param pnGroup object we want to update
	 */
	public void updateGroup(PnGroup pnGroup);
	
	/**
	 * returns groupId
	 * @param spaceId
	 * @param personId
	 * @return groupId
	 */
	public BigDecimal getGroupId(BigDecimal spaceId, BigDecimal personId);

}
