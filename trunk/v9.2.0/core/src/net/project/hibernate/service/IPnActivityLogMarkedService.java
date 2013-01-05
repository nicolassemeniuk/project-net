/**
 * 
 */
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;

public interface IPnActivityLogMarkedService {
	
	/**
	 * To save marked activities.
	 * @param pnActivityLogMarked
	 * @return object PnActivityLogMarkedPK
	 */
	public PnActivityLogMarkedPK save(PnActivityLogMarked pnActivityLogMarked);
	
	/**
	 * To delete marked activity
	 * @param pnActivityLogMarked
	 */
	public void delete(PnActivityLogMarked pnActivityLogMarked);
	
	/**
	 * To delete marked activities
	 * @param activityIds
	 */
	public void deleteByActivityIds(java.util.List activityIds);
	
	/**
	 * Get marked or unmarked activity ids by personId
	 * @param personId
	 * @param startDate
	 * @param endDate
	 * @param activityIds
 	 * @return String of activityLog Ids
	 */
	public String getMarkedByPersonId(Integer personId, Date startDate, Date endDate, List activityIdsPerPage);
}
