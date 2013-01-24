/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnBusinessHasViewDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnBusinessHasViewPK;
import net.project.hibernate.model.PnView;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnBusinessHasView database object implementation
 * 
 * @author Ritesh S
 */
@Transactional 
@Repository 
public class PnBusinessHasViewDAOImpl  extends AbstractHibernateAnnotatedDAO<PnBusinessHasView, PnBusinessHasViewPK> implements IPnBusinessHasViewDAO{

	private static Logger log = Logger.getLogger(PnBusinessHasViewDAOImpl.class);

	/**
	 * 
	 */
	public PnBusinessHasViewDAOImpl() {
		super(PnBusinessHasView.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnBusinessHasView> getSharedViewByBusiness(Integer businessId) {
		List<PnBusinessHasView> list = new ArrayList<PnBusinessHasView>();

		String sql = " select distinct v.viewId, v.name, p.displayName, v.modifiedDatetime, v.createdById"
				+ " from PnView v, PnBusinessHasView bhv, PnPerson p"
				+ " where bhv.comp_id.businessId = :businessId"
				+ " and v.viewId = bhv.comp_id.viewId"
				+ " and v.createdById = p.personId"
				+ " and v.recordStatus = :recordStatus";

		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setString("recordStatus", "A");
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnView view = new PnView((Integer) row[0],(String) row[1], (String) row[2], (Date) row[3], (Integer) row[4]);
				PnBusinessHasView businessHasView = new PnBusinessHasView(view);
				list.add(businessHasView);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting shared views by business id : " + e.getMessage());
		}
		return list;
	}

	public List<PnBusinessHasView> getBusinessByView(Integer viewId) {
		List<PnBusinessHasView> list = new ArrayList<PnBusinessHasView>();

		String sql = " select b.businessId, b.businessName"
			+ " from PnBusiness b, PnBusinessHasView bhv"
			+ " where bhv.comp_id.businessId = b.businessId"
			+ " and bhv.comp_id.viewId = :viewId"
			+ " and b.recordStatus = :recordStatus";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("viewId", viewId);
			query.setString("recordStatus", "A");
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnBusiness business = new PnBusiness((Integer) row[0],(String) row[1]);
				PnBusinessHasViewPK businessHasViewPK = new PnBusinessHasViewPK((Integer) row[0],viewId);
				PnBusinessHasView businessHasView = new PnBusinessHasView(businessHasViewPK, business);
				list.add(businessHasView);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting business by  views d : " + e.getMessage());
		}
		return list;
	}

}
