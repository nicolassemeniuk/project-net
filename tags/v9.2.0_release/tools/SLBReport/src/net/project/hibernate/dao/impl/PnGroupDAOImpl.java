package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnGroupDAOImpl extends AbstractHibernateDAO<PnGroup, BigDecimal> implements IPnGroupDAO {

	public PnGroupDAOImpl() {
		super(PnGroup.class);
	}

	public BigDecimal getGroupId(final BigDecimal spaceId, final BigDecimal personId) {
		BigDecimal groupId = null;
		try {
			Iterator it = (Iterator)getHibernateTemplate().execute(new HibernateCallback() {
				public Iterator doInHibernate(Session session) throws HibernateException {
					Query query = null;
					Iterator it = null;
					try {
						String sql= " select " +
				        "    g.groupId " +
				        " from " +
				        "    PnSpaceHasGroup as shg, PnGroup as g, PnGroupHasPerson as ghp " +
				        " where " +
				        "    shg.comp_id.spaceId = :spaceId and " +
				        "    ghp.comp_id.groupId = shg.comp_id.groupId and " +
				        "    ghp.comp_id.personId = :personId and " +
				        "    g.groupId = shg.comp_id.groupId and " +
				        "    g.isPrincipal = 1 ";						
						query = session.createQuery(sql);
						query.setBigDecimal("spaceId", spaceId).setBigDecimal("personId", personId);
						it = query.list().iterator();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return it;
				}
			});
			while(it.hasNext()){
				groupId = (BigDecimal)it.next();
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupId;
	}
	
	

}
