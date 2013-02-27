package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnMaterialAssignmentPK;
import net.project.material.PnMaterialAssignmentList;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnAssignmentMaterialDAOImpl extends AbstractHibernateAnnotatedDAO<PnMaterialAssignment, PnMaterialAssignmentPK> implements IPnAssignmentMaterialDAO {
	
	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnAssignmentMaterialDAOImpl() {
		super(PnMaterialAssignment.class);
	}

	@Override
	public PnMaterialAssignment getPnAssignmentMaterial(Integer spaceId, Integer materialId, Integer objectId) {
		try {
			PnMaterialAssignmentPK key = new PnMaterialAssignmentPK(spaceId, materialId, objectId);
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			PnMaterialAssignment pnMaterialAssignment = (PnMaterialAssignment) session.get(PnMaterialAssignment.class, key);
			session.close();
			return pnMaterialAssignment;
		} catch (Exception e) {
			log.error("Error occurred while getting assigned material by space, material and id " + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialAssignmentList getAssignments(Integer spaceId, Integer objectId) {
		PnMaterialAssignmentList result = new PnMaterialAssignmentList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnMaterialAssignmentList(session.createCriteria(PnMaterialAssignment.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.objectId", objectId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialAssignmentList getAssignmentsForMaterial(Integer spaceId, Integer materialId) {
		PnMaterialAssignmentList result = new PnMaterialAssignmentList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnMaterialAssignmentList(session.createCriteria(PnMaterialAssignment.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.materialId", materialId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialAssignmentList getAssignmentsForMaterial(Integer spaceId, Integer materialId, Integer objectId) {
		PnMaterialAssignmentList result = new PnMaterialAssignmentList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();			
			result = new PnMaterialAssignmentList(session.createCriteria(PnMaterialAssignment.class).add(Restrictions.eq("comp_id.spaceId", spaceId)).add(Restrictions.eq("comp_id.materialId", materialId)).add(Restrictions.ne("comp_id.objectId", objectId)).list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assigned materials " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

}
