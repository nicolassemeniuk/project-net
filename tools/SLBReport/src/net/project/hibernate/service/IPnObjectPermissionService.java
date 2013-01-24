package net.project.hibernate.service;

import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;

public interface IPnObjectPermissionService {
	
	/**
	 * @param pnObjectPermissionId 
	 * @return PnObjectPermission bean
	 */
	public PnObjectPermission getObjectPermission(PnObjectPermissionPK pnObjectPermissionId);
	
	/**
	 * Saves new PnObjectPermission
	 * @param PnObjectPermission object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnObjectPermissionPK saveObjectPermission(PnObjectPermission pnObjectPermission);
	
	/**
	 * Deletes PnObjectPermission from database
	 * @param PnObjectPermission object we want to delete
	 */
	public void deleteObjectPermission(PnObjectPermission pnObjectPermission);
	
	/**
	 * Updates PnObjectPermission
	 * @param PnObjectPermission object we want to update
	 */
	public void updateObjectPermission(PnObjectPermission pnObjectPermission);


}
