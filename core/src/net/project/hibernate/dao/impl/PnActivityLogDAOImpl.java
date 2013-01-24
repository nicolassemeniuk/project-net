package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.ObjectType;
import net.project.hibernate.dao.IPnActivityLogDAO;
import net.project.hibernate.model.PnActivityLog;
import net.project.util.DateUtils;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnActivityLogDAOImpl extends AbstractHibernateAnnotatedDAO<PnActivityLog, Integer> implements IPnActivityLogDAO {
	
	private static Logger log = Logger.getLogger(PnProjectSpaceDAOImpl.class);
	
	/**
	 */
	public PnActivityLogDAOImpl() {
		super(PnActivityLogDAOImpl.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnActivityLogDAO#getActivityLogBySpaceIdAndDate(java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	public List<PnActivityLog> getActivityLogBySpaceIdAndDate(Integer spaceId, Date startDate, Date endDate, String criteria, Integer personId, Integer offSet, Integer range, Integer currentUserId) {
		List<PnActivityLog> activityLog = new ArrayList<PnActivityLog>();
		String sql = null;
		try {
			sql = " SELECT al, pon.name, pp.displayName " +
					" FROM PnActivityLog al, PnObjectName pon, PnPerson pp ";
			
			if(StringUtils.isNotEmpty(criteria) && criteria.indexOf("alm") > 0){
				sql+=", PnActivityLogMarked alm ";
			}

			sql += " WHERE al.spaceId = :spaceId AND al.targetObjectId = pon.objectId AND al.activityBy = pp.personId "; 
			
			if(startDate != null){
				sql += " AND al.activityOnDate < " + DateUtils.toDBDateTime(startDate) +" ";
			}
			
			if(StringUtils.isNotEmpty(criteria)){
				sql += criteria;
			}
			if(StringUtils.isNotEmpty(criteria) && criteria.indexOf("alm") > 0 && currentUserId != null){
				sql+="AND alm.comp_id.personId = :currentUserId";
			}
			if(personId != null){
				sql += " AND al.activityBy = :personId ";
			}
			
			sql += " ORDER BY al.activityLogId DESC ";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			
			/*if(startDate != null){
				query.setTimestamp("startDate", startDate);
				//query.setTimestamp("endDate", endDate);
			}*/
			
			if(StringUtils.isNotEmpty(criteria) && criteria.indexOf("alm") > 0 && currentUserId != null){
				query.setInteger("currentUserId", currentUserId);
			}
			
			if(personId != null){
				query.setInteger("personId", personId);
			}
			
			if(offSet != null ){
				query.setFirstResult(offSet);
			}
			
			if(range != null ){
				query.setMaxResults(range);
			}	
			
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnActivityLog pnActivityLog = (PnActivityLog) row[0];
				pnActivityLog.setObjectName((String) row[1]);
				pnActivityLog.setPersonName((String) row[2]);
				activityLog.add(pnActivityLog);
			}	
		} catch (Exception e) {
			log.error("Error occurred while getting activity log: " + e.getMessage());
		}
		return activityLog;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnActivityLogDAO#getActivityIdsOfBlogHavingComment(java.util.List)
	 */
	public String getActivityIdsOfBlogHavingComment(List activityIds) {
		String Ids = "";
		try {
			String sql = "SELECT DISTINCT al.activityLogId FROM PnActivityLog al, PnActivityLog pal";
			sql += " WHERE pal.targetObjectType = :blogComment ";
			sql += " AND pal.parentObjectId = al.targetObjectId";
			sql += " AND pal.parentObjectId IN ( :activityIds ) ";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setString("blogComment", ObjectType.BLOG_COMMENT);
			query.setParameterList("activityIds", activityIds);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Ids += results.next() + ",";
			}
		} catch (Exception e) {
			Logger.getLogger(PnActivityLogMarkedDAOImpl.class).error(
					"Error occurred while getting activity ids of blogs having comments" + e);
		}
		return Ids;
	}
}
