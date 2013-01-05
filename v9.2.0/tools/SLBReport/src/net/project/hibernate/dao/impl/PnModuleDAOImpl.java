package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PnModuleDAOImpl extends AbstractHibernateDAO<PnModule, BigDecimal> implements IPnModuleDAO {

	@SuppressWarnings("unchecked")
	public List<PnModule> getModuleDefaultPermissions(final BigDecimal spaceId) {
		List<PnModule> pnModuleList = new ArrayList();
		try {
			Iterator pnModuleIterator = (Iterator) getHibernateTemplate().execute(new HibernateCallback() {
				public Iterator doInHibernate(Session session) throws HibernateException, SQLException {
					String sql = " select m.moduleId, m.defaultPermissionActions " +
								"from PnModule m, PnSpaceHasModule shm where "
							+ " shm.comp_id.spaceId = :spaceId and m.moduleId = shm.comp_id.moduleId and shm.isActive = 1";
					Iterator it = null;
					try {
						Query query = session.createQuery(sql);
						query.setBigDecimal("spaceId", spaceId);
						it = query.list().iterator();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return it;
				}

			});
			while (pnModuleIterator.hasNext()) {
				Object[] obj = (Object[]) pnModuleIterator.next();
				pnModuleList.add(new PnModule((BigDecimal) obj[0], ((Long) obj[1]).longValue()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnModuleList;
	}

	public PnModuleDAOImpl() {
		super(PnModule.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnModule> getModuleIds() {
		List<PnModule> pnModuleList = new ArrayList();
		try {
			Iterator pnModuleIterator = getHibernateTemplate().find(" select moduleId from PnModule ").iterator();
			while (pnModuleIterator.hasNext()) {
				Object obj = pnModuleIterator.next();
				pnModuleList.add(new PnModule((BigDecimal) obj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnModuleList;
	}

}
