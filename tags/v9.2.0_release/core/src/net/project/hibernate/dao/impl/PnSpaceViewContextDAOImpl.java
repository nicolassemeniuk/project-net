/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnSpaceViewContextDAO;
import net.project.hibernate.model.PnSpaceViewContext;
import net.project.hibernate.model.PnSpaceViewContextPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnSpaceViewContext database object implementation
 * 
 * @author Ritesh S
 *
 */
@Transactional 
@Repository 
public class PnSpaceViewContextDAOImpl extends AbstractHibernateAnnotatedDAO<PnSpaceViewContext, PnSpaceViewContextPK> implements IPnSpaceViewContextDAO{

	private static Logger log = Logger.getLogger(PnSpaceViewContextDAOImpl.class);

	public PnSpaceViewContextDAOImpl() {
		super(PnSpaceViewContext.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnSpaceViewContexDAO#getSharedViewByPerson(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<PnSpaceViewContext> getSharedViewByPerson(Integer spaceId) {
	List<PnSpaceViewContext> result = new ArrayList<PnSpaceViewContext>();

		String sql = " select distinct new PnSpaceViewContext(svc.comp_id,v)"
				+ " from PnView v, PnSpaceViewContext svc"
				+ " where svc.comp_id.spaceId = :spaceId"
				+ " and v.viewId = svc.comp_id.viewId"
				+ " and v.createdById <> svc.comp_id.spaceId";
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			result = query.list();
		} catch (Exception e) {
			log.error("Error occurred while getting shared views by person id : " + e.getMessage());
		}
		return result;
	}

}
