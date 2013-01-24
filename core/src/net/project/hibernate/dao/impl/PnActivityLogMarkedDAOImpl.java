/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnActivityLogMarkedDAO;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;
import net.project.util.DateUtils;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnActivityLogMarkedDAOImpl extends AbstractHibernateAnnotatedDAO<PnActivityLogMarked, PnActivityLogMarkedPK> implements IPnActivityLogMarkedDAO {

	public PnActivityLogMarkedDAOImpl(){
		super(PnActivityLogMarked.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnActivityLogMarkedDAO#deleteByActivityIds(java.util.List)
	 */
	public void deleteByActivityIds(List activityIds) {
		try {
			String sql = "DELETE FROM PnActivityLogMarked alm "
					+ " WHERE alm.comp_id.activityLogId IN ( :activityIds )";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setParameterList("activityIds", activityIds);
			query.executeUpdate();
		} catch (Exception e) {
			Logger.getLogger(PnActivityLogMarkedDAOImpl.class).error(
					"Error occurred while deleting marked activities" + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnActivityLogMarkedDAO#getMarkedByPersonId(java.lang.Integer, java.util.Date, java.util.Date, java.util.List)
	 */
	public String getMarkedByPersonId(Integer personId, Date startDate, Date endDate, List activityIdsPerPage) {
		String activityIds = "";
		try {
			String sql = "SELECT DISTINCT alm.comp_id.activityLogId FROM PnActivityLog al, PnActivityLogMarked alm ";
			sql += " WHERE alm.comp_id.personId = :personId ";
			sql += " AND alm.comp_id.activityLogId IN ( :activityIds ) ";
			if(startDate != null){
				sql += " AND al.activityOnDate < " + DateUtils.toDBDateTime(startDate) +" ";
			}
			sql += " ORDER BY alm.comp_id.activityLogId DESC";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("personId", personId);
			query.setParameterList("activityIds", activityIdsPerPage);
			
			/*if(startDate != null){
				query.setString("startDate", DateUtils.toDBDateTime(startDate));
			//	query.setTimestamp("endDate", endDate);
			}*/
			
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				activityIds += results.next() + ",";
			}
		} catch (Exception e) {
			Logger.getLogger(PnActivityLogMarkedDAOImpl.class).error(
					"Error occurred while getting marked activity ids" + e);
		}
		return activityIds;
	}
}
