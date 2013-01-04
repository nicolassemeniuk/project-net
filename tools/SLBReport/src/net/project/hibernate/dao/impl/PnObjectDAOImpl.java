package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnObjectDAOImpl extends AbstractHibernateDAO<PnObject, BigDecimal> implements IPnObjectDAO {

	public PnObjectDAOImpl() {
		super(PnObject.class);
	}

	public BigDecimal create(PnObject pnObject) {
		BigDecimal objectId = new BigDecimal(0);
		try {
			objectId = (BigDecimal) getHibernateTemplate().execute(new HibernateCallback() {
				public BigDecimal doInHibernate(Session session) throws HibernateException {
					BigDecimal callbackObjectId = new BigDecimal(0);
					try {
						SQLQuery query = session.createSQLQuery(" SELECT PN_OBJECT_SEQUENCE.NEXTVAL FROM DUAL ");
						List list = query.list();
						callbackObjectId = (BigDecimal) list.get(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return callbackObjectId;
				}
			});
			pnObject.setObjectId(objectId);
			getHibernateTemplate().save(pnObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectId;
	}

}
