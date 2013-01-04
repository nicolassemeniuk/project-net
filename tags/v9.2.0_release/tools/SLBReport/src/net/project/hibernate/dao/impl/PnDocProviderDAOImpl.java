package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDocProvider;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnDocProviderDAOImpl extends AbstractHibernateDAO<PnDocProvider, BigDecimal> implements IPnDocProviderDAO {

	public PnDocProviderDAOImpl() {
		super(PnDocProvider.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnDocProvider> getDocProviderIds() {
		List<PnDocProvider> pnDocProviderList = new ArrayList();
		Iterator pnDocProviderIterator = null;
		try {
			pnDocProviderIterator = (Iterator) getHibernateTemplate().execute(new HibernateCallback() {
				public Iterator doInHibernate(Session session) throws HibernateException {
					Query query = null;
					Iterator it = null;
					try {
						String sql = " SELECT docProviderId FROM PnDocProvider WHERE isDefault = 1 ";
						query = session.createQuery(sql);
						it = query.list().iterator();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return it;
				}
			});
			while (pnDocProviderIterator.hasNext()) {
				PnDocProvider pnDocProvider = new PnDocProvider((BigDecimal) pnDocProviderIterator.next());
				pnDocProviderList.add(pnDocProvider);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnDocProviderList;
	}

}
