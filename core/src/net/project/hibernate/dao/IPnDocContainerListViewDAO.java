/**
 * 
 */
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnDocContainerListView;

public interface IPnDocContainerListViewDAO extends IDAO<PnDocContainerListView, Integer>{

	/**
	 * Get all containers and documents
	 * @param spaceID
	 * @return List of PnDocContainerListView instances
	 */
	public List<PnDocContainerListView> getAllContainersDocument(String spaceID);
}
