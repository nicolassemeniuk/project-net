/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnBusinessHasView;

/**
 * Interface for accessing PnBusinessHasView database service
 *  
 * @author Ritesh S
 *
 */
public interface IPnBusinessHasViewService {

	/**
	 * Persist the viewId of a view and businessId of business with whome 
	 * this view is made shared. 
	 * if the viewId and spaceId already exist then it will be updated
	 * @param viewId
	 * @param spaceId
	 */
	public void save(Integer businessId, Integer viewId);	
	
	/**
	 * Get shared views by businessId
	 * @param businessId
	 * @return
	 */
    public List<PnBusinessHasView> getSharedViewByBusiness(Integer businessId);
    
    /**
     * Get business by shared view Id.
     * @param viewId
     * @return
     */
    public List<PnBusinessHasView> getBusinessByView(Integer viewId);
    
    /**
     * To remove view from business
     * @param businessId
     * @param viewId
     */
    public void delete(Integer businessId, Integer viewId);
    
}
