package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnActivityLog;

public interface IPnActivityLogDAO extends IDAO<PnActivityLog, Integer>{
	
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
	public List<PnActivityLog> getActivityLogBySpaceIdAndDate(Integer spaceId, Date startDate,Date endDate, String criteria, Integer personId, Integer offSet, Integer range, Integer currentUserId);
	
	/**
	 * Get activity ids of blogs having comments
	 * @param List activityIds
	 * @return String of activity ids
	 */
	public String getActivityIdsOfBlogHavingComment(List activityIds);
}