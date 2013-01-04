package net.project.hibernate.service;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;

public interface IPnDefaultObjectPermissionService {
	

	/**
	 * @param pnDefaultObjectPermissionId 
	 * @return PnDefaultObjectPermission bean
	 */
	public PnDefaultObjectPermission getDefaultObjectPermission(PnDefaultObjectPermissionPK pnDefaultObjectPermissionId);
	
	/**
	 * Saves new PnDefaultObjectPermission
	 * @param PnDefaultObjectPermission object we want to save
	 * @return primary key for saved PnDefaultObjectPermission bean
	 */
	public PnDefaultObjectPermissionPK saveDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);
	
	/**
	 * Deletes PnDefaultObjectPermission from database
	 * @param PnDefaultObjectPermission object we want to delete
	 */
	public void deleteDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);
	
	/**
	 * Updates PnDefaultObjectPermission
	 * @param PnDefaultObjectPermission object we want to update
	 */
	public void updateDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);

	/**
	 * returns sql specific object permissions
	 * @param spaceId
	 * @param objectType
	 * @return Iterator of objects, that can be seen in PnDefaultObjectPermissionDAOImpl
	 */
	public Iterator getObjectPermisions(BigDecimal spaceId, String objectType);
}
