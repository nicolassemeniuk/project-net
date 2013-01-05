/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnChargeCodeDAO;
import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnSpaceHasSpace;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ritesh S
 *
 */
@Transactional 
@Repository 
public class PnChargeCodeDAOImpl extends AbstractHibernateAnnotatedDAO<PnChargeCode, Integer> implements IPnChargeCodeDAO{

	private static Logger log = Logger.getLogger(PnBusinessHasViewDAOImpl.class);

	public PnChargeCodeDAOImpl() {
		super(PnChargeCodeDAOImpl.class);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnChargeCodeDAO#getChargeCodeByBusinessId(java.lang.Integer)
	 */
	public List<PnChargeCode> getChargeCodeByBusinessId(Integer businessId) {
		List<PnChargeCode> result = null;
		try {
			String sql = " FROM PnChargeCode cc WHERE cc.businessId = :businessId"
					   + " and cc.recordStatus = :recordStatus";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setString("recordStatus", "A");
			
			result = query.list();
			
		} catch (Exception e) {
			log.error("Error occured while getting charge code "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnChargeCodeDAO#getRootBusinessChargeCodeBySubBusinessId(java.lang.Integer)
	 */
	public List<PnChargeCode> getRootBusinessChargeCodeBySubBusinessId(Integer businessId){
		List<PnChargeCode> result = null;
		Query query;
		try {
			List<PnSpaceHasSpace> list;
			String sql = " FROM PnSpaceHasSpace shs WHERE shs.comp_id.childSpaceId = :childSpaceId"
					   + " and shs.parentSpaceType = :parentSpaceType and shs.childSpaceType = :childSpaceType";

			do {
				query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
				query.setInteger("childSpaceId", businessId);
				query.setString("parentSpaceType", "business");
				query.setString("childSpaceType", "business");
				list = query.list();
				if(!list.isEmpty())
					businessId = list.get(0).getComp_id().getParentSpaceId();
			} while(!list.isEmpty());
			
			sql = " FROM PnChargeCode cc WHERE cc.businessId = :rootBusinessId"
				+ " and cc.recordStatus = :recordStatus";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("rootBusinessId", businessId);
			query.setString("recordStatus", "A");
			result = query.list();
			
		} catch (Exception e) {
			log.error("Error occured while getting charge code "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnChargeCodeDAO#getChargeCodeByProjectId(java.lang.Integer)
	 */
	public List<PnChargeCode> getChargeCodeByProjectId(Integer projectId) {
		List<PnChargeCode> result = null;
		Query query;
		try {
			List<PnSpaceHasSpace> list;
			Integer rootBusinessId = null;
			Integer rootProjectId = projectId;
			String sql = " FROM PnSpaceHasSpace shs WHERE shs.comp_id.childSpaceId = :childSpaceId"
					   + " and shs.parentSpaceType = :parentSpaceType and shs.childSpaceType = :childSpaceType";

			do {
				query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
				query.setInteger("childSpaceId", rootProjectId);
				query.setString("parentSpaceType", "project");
				query.setString("childSpaceType", "project");
				list = query.list();
				if(!list.isEmpty())
					rootProjectId = list.get(0).getComp_id().getParentSpaceId();
			} while(!list.isEmpty());
			

			sql = " FROM PnSpaceHasSpace shs WHERE shs.comp_id.childSpaceId = :childSpaceId"
				+ " and shs.parentSpaceType = :parentSpaceType and shs.childSpaceType = :childSpaceType";

				query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
				query.setInteger("childSpaceId", rootProjectId);
				query.setString("parentSpaceType", "business");
				query.setString("childSpaceType", "project");
				list = query.list();
				if(!list.isEmpty())
					rootBusinessId = list.get(0).getComp_id().getParentSpaceId();


			if(rootBusinessId != null){
				sql = " FROM PnSpaceHasSpace shs WHERE shs.comp_id.childSpaceId = :childSpaceId"
					+ " and shs.parentSpaceType = :parentSpaceType and shs.childSpaceType = :childSpaceType";

				do {
					query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
					query.setInteger("childSpaceId", rootBusinessId);
					query.setString("parentSpaceType", "business");
					query.setString("childSpaceType", "business");
					list = query.list();
					if(!list.isEmpty())
						rootBusinessId = list.get(0).getComp_id().getParentSpaceId();
				} while(!list.isEmpty());
					

				sql = " FROM PnChargeCode cc WHERE cc.businessId = :rootBusinessId"
					+ " and cc.recordStatus = :recordStatus";
				query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
				query.setInteger("rootBusinessId", rootBusinessId);
				query.setString("recordStatus", "A");
				result = query.list();				
			}	
			
		} catch (Exception e) {
			log.error("Error occured while getting charge code "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnChargeCodeDAO#getChargeCodeAppliedOnPersonInSpace(java.lang.Integer, java.lang.Integer)
	 */
	public PnChargeCode getChargeCodeAppliedOnPersonInSpace(Integer personId, Integer spaceId){
		PnChargeCode result = null;
		String sql = " select cc.codeId, cc.codeName"
				   + " from PnChargeCode cc, PnObjectHasChargeCode ohcc"
			       + " where ohcc.comp_id.spaceId = :spaceId"
			       + " and ohcc.comp_id.objectId = :objectId"
		           + " and cc.codeId = ohcc.chargeCodeId";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			query.setInteger("objectId", personId);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				result = new PnChargeCode((Integer) row[0] ,(String) row[1]);
			}
			
		} catch (Exception e) {
			log.error("Error occurred while getting charge code by person and space id : " + e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnChargeCodeDAO#getChargeCodeApliedOnTask(java.lang.Integer, java.lang.Integer)
	 */
	public PnChargeCode getChargeCodeApliedOnTask(Integer taskId, Integer spaceId) {
		PnChargeCode result = null;
		String sql = " select cc.codeId, cc.codeName"
				   + " from PnChargeCode cc, PnObjectHasChargeCode ohcc"
			       + " where ohcc.comp_id.spaceId = :spaceId"
			       + " and ohcc.comp_id.objectId = :objectId"
		           + " and cc.codeId = ohcc.chargeCodeId";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			query.setInteger("objectId", taskId);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				result = new PnChargeCode((Integer) row[0] ,(String) row[1]);
			}
			
		} catch (Exception e) {
			log.error("Error occurred while getting charge code by task and space id  : " + e.getMessage());
		}
		return result;
	}


}
