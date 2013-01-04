/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnDocContainerListView;

public interface IPnDocContainerListViewService {

	/**
	 * Get all containers and documents
	 * @param spaceID
	 * @return List of PnDocContainerListView instances
	 */
	public List<PnDocContainerListView> getAllContainersDocument(String spaceID);
}
