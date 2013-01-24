/**
 * 
 */
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

import net.project.events.ApplicationEvent;
import net.project.hibernate.model.PnActivityLog;

/**
 *
 */
public interface IPnActivityLogService {
	
	public PnActivityLog get(java.lang.Integer key);

	public PnActivityLog load(java.lang.Integer key);

	public java.util.List<PnActivityLog> findAll ();

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnActivityLog a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(PnActivityLog pnActivityLog);

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnActivityLog a transient instance containing updated state
	 */
	public void update(PnActivityLog pnActivityLog);

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnActivityLog the instance to be removed
	 */
	public void delete(PnActivityLog pnActivityLog);
	
	public java.lang.Integer saveApplicationEvent(ApplicationEvent applicationEvent);
	
	/**
	 * Retrieve the activity log by space id, date and criteria
	 * @param spaceId space identifier
	 * @param startDate start date for activity log
	 * @param endDate end date for activity log
	 * @param criteria for activity type
	 * @param personId person identifier
	 * @param offSet
	 * @param range
	 * @param currentUserId
	 * @return List of PnActivityLog instances
	 */
	public List<PnActivityLog> getActivityLogBySpaceIdAndDate(Integer spaceId, Date startDate, Date enddate, String criteria, Integer personId, Integer offSet, Integer range, Integer currentUserId);
	
	/**
	 * Get activity ids of blogs having comments
	 * @param List activityIds
	 * @return String of activity ids
	 */
	public String getActivityIdsOfBlogHavingComment(List activityIds);

}
