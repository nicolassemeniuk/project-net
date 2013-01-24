package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnDirectoryDAO;
import net.project.hibernate.model.PnDirectory;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnDirectoryDAOImpl extends AbstractHibernateDAO<PnDirectory, BigDecimal> implements IPnDirectoryDAO {

	public PnDirectoryDAOImpl() {
		super(PnDirectory.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnDirectory> getDefaultDirectory() {
		List<PnDirectory> defaultDirectories = new ArrayList();
		try {
			Iterator directoryIterator = (Iterator)getHibernateTemplate().execute(
					new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					
					Query query = null;
					Iterator it = null;
					try {
						query = session.createQuery(" SELECT directoryId FROM PnDirectory WHERE isDefault = 1 ");
						it = query.list().iterator();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return it;
				}
			});			
			while (directoryIterator.hasNext()) {
				Object obj = directoryIterator.next();
				PnDirectory pnDirectory = new PnDirectory((BigDecimal)obj , 1);
				defaultDirectories.add(pnDirectory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultDirectories;
	}

}
