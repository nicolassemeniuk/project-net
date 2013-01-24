package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnDefaultObjectPermissionDAOImpl extends AbstractHibernateDAO<PnDefaultObjectPermission, PnDefaultObjectPermissionPK> implements IPnDefaultObjectPermissionDAO {


	public PnDefaultObjectPermissionDAOImpl() {
		super(PnDefaultObjectPermission.class);
	}

	public Iterator getObjectPermisions(final BigDecimal spaceId, final String objectType) {
		Iterator it = null;
		try {			
			it = (Iterator)getHibernateTemplate().execute(new HibernateCallback() {
				public Iterator doInHibernate(Session session) throws HibernateException {
					Query query = null;
					Iterator it = null;
					try {
						String sql = " select dop.comp_id.groupId, dop.actions " +
				         " from PnDefaultObjectPermission dop, PnGroup g " +
				         " where dop.comp_id.spaceId = :spaceId and " +
				         " dop.comp_id.objectType = :objectType and " +
				         " g.groupId = dop.comp_id.groupId and " +
				         " g.isPrincipal <> 1";
						query = session.createQuery(sql);
						query.setBigDecimal("spaceId", spaceId);
						query.setString("objectType", objectType);
						it = query.list().iterator();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return it;
				}
			});			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return it;
	}
}
