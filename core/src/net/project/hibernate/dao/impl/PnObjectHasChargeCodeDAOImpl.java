/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnObjectHasChargeCodeDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnBusinessHasViewPK;
import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCodePK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnObjectHasChargeCode database object implementation
 * 
 * @author Ritesh S
 *
 */
@Transactional 
@Repository 
public class PnObjectHasChargeCodeDAOImpl extends AbstractHibernateAnnotatedDAO<PnObjectHasChargeCode, PnObjectHasChargeCodePK> implements IPnObjectHasChargeCodeDAO{

	private static Logger log = Logger.getLogger(PnObjectHasChargeCodeDAOImpl.class);
	
	public PnObjectHasChargeCodeDAOImpl() {
        super(PnObjectHasChargeCode.class);
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnObjectHasChargeCodeDAO#getChargeCodeAssignedPersonFromParentBusiness(java.lang.Integer)
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentBusiness(Integer spaceId, String childSpaceType) {
		List<PnObjectHasChargeCode> result = new ArrayList<PnObjectHasChargeCode>();

		String sql = " select ohcc.comp_id.objectId , ohcc.comp_id.spaceId, cc.codeId , cc.codeName"
				   + " from PnObjectHasChargeCode ohcc, PnObject obj, PnSpaceHasSpace shs, PnChargeCode cc"
				   + " where ohcc.comp_id.spaceId = shs.comp_id.parentSpaceId"
				   + " and ohcc.comp_id.objectId = obj.objectId"
				   + " and cc.codeId = ohcc.chargeCodeId"
				   + " and obj.pnObjectType.objectType = :objectType"
				   + " and shs.comp_id.childSpaceId = :chileSpaceId"
				   + " and shs.childSpaceType = :childSpaceType";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("chileSpaceId", spaceId);
			query.setString("objectType", "person");
			query.setString("childSpaceType", childSpaceType);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnObjectHasChargeCodePK chargeCodePK = new PnObjectHasChargeCodePK((Integer) row[0],(Integer) row[1]);
				PnChargeCode chargeCode = new PnChargeCode((Integer) row[2], (String) row[3]); 
				PnObjectHasChargeCode objectHasChargeCode = new PnObjectHasChargeCode(chargeCodePK, chargeCode);
				result.add(objectHasChargeCode);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting charge code assigned person from parent busines : " + e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnObjectHasChargeCodeDAO#getChargeCodeAssignedPersonFromParentProject(java.lang.Integer)
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentProject(Integer spaceId) {
		List<PnObjectHasChargeCode> result = new ArrayList<PnObjectHasChargeCode>();

		String sql = " select ohcc.comp_id.objectId , ohcc.comp_id.spaceId, cc.codeId , cc.codeName"
				   + " from PnObjectHasChargeCode ohcc, PnObject obj, PnSpaceHasSpace shs, PnChargeCode cc"
				   + " where ohcc.comp_id.spaceId = shs.comp_id.parentSpaceId"
				   + " and ohcc.comp_id.objectId = obj.objectId"
				   + " and cc.codeId = ohcc.chargeCodeId"
				   + " and obj.pnObjectType.objectType = :objectType"
				   + " and shs.comp_id.childSpaceId = :chileSpaceId"
				   + " and shs.childSpaceType = :childSpaceType";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("chileSpaceId", spaceId);
			query.setString("objectType", "person");
			query.setString("childSpaceType", "project");
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnObjectHasChargeCodePK chargeCodePK = new PnObjectHasChargeCodePK((Integer) row[0],(Integer) row[1]);
				PnChargeCode chargeCode = new PnChargeCode((Integer) row[2], (String) row[3]); 
				PnObjectHasChargeCode objectHasChargeCode = new PnObjectHasChargeCode(chargeCodePK, chargeCode);
				result.add(objectHasChargeCode);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting charge code assigned person from parent project : " + e.getMessage());
		}
		return result;
	}

}
