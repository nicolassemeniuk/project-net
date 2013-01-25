package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasMaterialDAO;
import net.project.hibernate.model.PnSpaceHasMaterial;
import net.project.hibernate.model.PnSpaceHasMaterialPK;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnSpaceHasMaterialDAOImpl extends AbstractHibernateAnnotatedDAO<PnSpaceHasMaterial, PnSpaceHasMaterialPK> implements IPnSpaceHasMaterialDAO {

	private static Logger log = Logger.getLogger(PnSpaceHasMaterialDAOImpl.class);

	public PnSpaceHasMaterialDAOImpl() {
		super(PnSpaceHasMaterial.class);
	}

	@Override
	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId, Integer materialId) {
		PnSpaceHasMaterialPK pk = new PnSpaceHasMaterialPK(spaceId, materialId);
		PnSpaceHasMaterial pnSpaceHasMaterial = null;
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			pnSpaceHasMaterial=(PnSpaceHasMaterial) session.get(PnSpaceHasMaterial.class, pk);
			session.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return pnSpaceHasMaterial;
	}

	@Override
	public boolean spaceHasMaterial(Integer spaceId, Integer materialId) {
		PnSpaceHasMaterialPK pk = new PnSpaceHasMaterialPK(spaceId, materialId);
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			PnSpaceHasMaterial pnSpaceHasMaterial = (PnSpaceHasMaterial) session.get(PnSpaceHasMaterial.class, pk);
			session.close();
			// If it doesn't exist will return null
			if (pnSpaceHasMaterial.equals(null))
				return false;
			else
				return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getMaterialsFromSpace(Integer spaceId) {
		List<Integer> idList = new ArrayList<Integer>();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			idList = session.createCriteria(PnSpaceHasMaterial.class).add(Restrictions.eq("comp_id.spaceId", spaceId))
					.setProjection(Property.forName("comp_id.materialId")).list();
			session.close();			
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return idList;
	}

}
