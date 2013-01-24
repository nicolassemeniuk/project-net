/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnSpaceViewContext;

/**
 * Interface for accessing PnSpaceViewContext database service
 *  
 * @author Ritesh S
 *
 */
public interface IPnSpaceViewContextService {

	/**
	 * Persist the viewId of a view and spaceId which personId of person with whome 
	 * this view is made shared. 
	 * if the viewId and spaceId already exist then it will be updated
	 * @param viewId
	 * @param spaceId
	 */
	public void save(Integer spaceId, Integer viewId);	
	
	/**
	 * Get shared views by spaceId which dosen't contains views created by current user
	 * @param pesronId
	 * @return
	 */
    public List<PnSpaceViewContext> getSharedViewByPerson(Integer spaceId);
	
}
