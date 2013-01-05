package net.project.hibernate.service;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnModule;

public interface IPnModuleService {
	
	/**
	 * @param moduleId for Module we need to select from database
	 * @return PnModule bean
	 */
	public PnModule getModule(BigDecimal moduleId);
	
	/**
	 * Saves new PnModule
	 * @param pnModule object we want to save
	 * @return primary key for saved Module
	 */
	public BigDecimal saveModule(PnModule pnModule);
	
	/**
	 * Deletes Module from database
	 * @param pnModule object we want to delete
	 */
	public void deleteModule(PnModule pnModule);
	
	/**
	 * Updates Module
	 * @param pnModule object we want to update
	 */
	public void updateModule(PnModule pnModule);
	
	/**
	 * select all moduleIds from PnModule
	 * @return List of module IDs
	 */
	public List<PnModule> getModuleIds();
	
	/**
	 * select moduleId and defaultPermissionActions from PnModule
	 * @param spaceId 
	 * @return List of PnModule beans
	 */
	public List<PnModule> getModuleDefaultPermissions(BigDecimal spaceId);	

}
