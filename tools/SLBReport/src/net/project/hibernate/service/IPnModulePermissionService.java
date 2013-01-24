package net.project.hibernate.service;

import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;

public interface IPnModulePermissionService {
	

	/**
	 * @param pnModulePermissionId 
	 * @return PnModulePermission bean
	 */
	public PnModulePermission getModulePermission(PnModulePermissionPK pnModulePermissionId);
	
	/**
	 * Saves new PnModulePermission
	 * @param PnModulePermission object we want to save
	 * @return primary key for saved PnModulePermission bean
	 */
	public PnModulePermissionPK saveModulePermission(PnModulePermission pnModulePermission);
	
	/**
	 * Deletes PnModulePermission from database
	 * @param PnModulePermission object we want to delete
	 */
	public void deleteModulePermission(PnModulePermission pnModulePermission);
	
	/**
	 * Updates PnModulePermission
	 * @param PnModulePermission object we want to update
	 */
	public void updateModulePermission(PnModulePermission pnModulePermission);


}
