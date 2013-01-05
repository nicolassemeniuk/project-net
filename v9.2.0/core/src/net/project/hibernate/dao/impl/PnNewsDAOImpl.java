/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnNewsDAO;
import net.project.hibernate.model.PnNews;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 
 *
 */
@Transactional 
@Repository 
public class PnNewsDAOImpl extends AbstractHibernateAnnotatedDAO<PnNews, BigDecimal> implements IPnNewsDAO {
	
	private static Logger log = Logger.getLogger(PnNewsDAOImpl.class);
	
    public PnNewsDAOImpl() {
        super(PnNewsDAOImpl.class);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnNewsDAO#getNewsWithRecordStatus(java.math.BigDecimal)
     */
    public PnNews getNewsWithRecordStatus(BigDecimal newsId) {
		PnNews news = null;

		String sql = " SELECT new PnNews(n.newsId, n.topic, n.recordStatus) "
				+ " FROM PnNews n where n.newsId = :newsId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setBigDecimal("newsId", newsId);
			news = (PnNews) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while getting news details by news id : " + e.getMessage());
		}
		return news;
	}
}
