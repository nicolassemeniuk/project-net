/**
 * 
 */
package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;

public interface IPnActivityLogMarkedDAO extends IDAO<PnActivityLogMarked, PnActivityLogMarkedPK>{
	
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
