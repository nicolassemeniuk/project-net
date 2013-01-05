package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnObjectType;

public interface IPnObjectTypeService {
	
	/**
	 * @param pnObjectTypeId 
	 * @return PnObjectType bean
	 */
	public PnObjectType getObjectType(String pnObjectTypeId);
	
	/**
	 * Saves new PnObjectType
	 * @param PnObjectType object we want to save
	 * @return primary key for saved PnObjectType bean
	 */
	public String saveObjectType(PnObjectType pnObjectType);
	
	/**
	 * Deletes PnObjectType from database
	 * @param PnObjectType object we want to delete
	 */
	public void deleteObjectType(PnObjectType pnObjectType);
	
	/**
	 * Updates PnObjectType
	 * @param PnObjectType object we want to update
	 */
	public void updateObjectType(PnObjectType pnObjectType);
	
	/**
	 * selects all objectType and defaultPermissionActions
	 * @param PnObjectType object we want to update
	 */
	public List<PnObjectType> findObjectTypes();	


}
