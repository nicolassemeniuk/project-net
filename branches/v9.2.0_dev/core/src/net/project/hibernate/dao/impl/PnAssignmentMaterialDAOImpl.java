package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.model.PnAssignmentMaterialPK;
import net.project.hibernate.model.PnSpaceHasMaterial;
import net.project.material.PnAssignmentMaterialList;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnAssignmentMaterialDAOImpl extends AbstractHibernateAnnotatedDAO<PnAssignmentMaterial, PnAssignmentMaterialPK> implements IPnAssignmentMaterialDAO {
	
	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnAssignmentMaterialDAOImpl() {
		super(PnAssignmentMaterial.class);
	}

	@Override
	public PnAssignmentMaterial getPnAssignmentMaterial(Integer spaceId, Integer materialId, Integer objectId) {
		try {
			PnAssignmentMaterialPK key = new PnAssignmentMaterialPK(spaceId, materialId, objectId);
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			PnAssignmentMaterial pnAssignmentMaterial = (PnAssignmentMaterial) session.get(PnAssignmentMaterial.class, key);
			session.close();
			return pnAssignmentMaterial;
		} catch (Exception e) {
			log.error("Error occurred while getting assigned material by space, material and id " + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnAssignmentMaterialList getAssignments(Integer spaceId, Integer objectId) {
		PnAssignmentMaterialList result = new PnAssignmentMaterialList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnAssignmentMaterialList(session.createCriteria(PnSpaceHasMaterial.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.objectId", objectId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnAssignmentMaterialList getAssignmentsForMaterial(Integer spaceId, Integer materialId) {
		PnAssignmentMaterialList result = new PnAssignmentMaterialList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnAssignmentMaterialList(session.createCriteria(PnSpaceHasMaterial.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.materialId", materialId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public PnAssignmentMaterialList getAssignmentsForMaterial(Integer spaceId, Integer materialId, Integer objectId) {
		PnAssignmentMaterialList result = new PnAssignmentMaterialList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnAssignmentMaterialList(session.createCriteria(PnSpaceHasMaterial.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.materialId", materialId)).add(Restrictions.ne("comp_id.objectId", objectId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

}
