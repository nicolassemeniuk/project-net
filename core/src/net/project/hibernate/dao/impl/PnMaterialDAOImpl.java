package net.project.hibernate.dao.impl;

import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.dao.IPnMaterialDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnMaterialDAOImpl extends AbstractHibernateAnnotatedDAO<PnMaterial, Integer> implements IPnMaterialDAO {

	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnMaterialDAOImpl() {
		super(PnMaterial.class);
	}

	@Override
	public PnMaterial getMaterialById(Integer materialId) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			PnMaterial pnMaterial = (PnMaterial) session.get(PnMaterial.class, materialId);
			session.close();
			return pnMaterial;
		} catch (Exception e) {
			log.error("Error occurred while getting materials by id " + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialList getMaterials() {
		PnMaterialList result = new PnMaterialList();
		try {
			// HibernateTemplate.initialize(PnMaterial pnMaterial);
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			result = new PnMaterialList(session.createQuery("FROM PnMaterial").list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of materials " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialList getMaterials(List<Integer> materialsId) {
		PnMaterialList result = new PnMaterialList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnMaterial.class);
			criteria.add(Restrictions.in("materialId", materialsId));
			result = new PnMaterialList(criteria.list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of materials " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(Integer spaceID) {
		PnMaterialList materialList = new PnMaterialList();
		Integer percentComplete = PropertyProvider.getInt("prm.global.taskcompletedpercentage");
		
	    try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			String sql = " SELECT DISTINCT m " +
					" FROM PnTask t, PnPlan pp, PnPlanHasTask pht, PnSpaceHasPlan shp, PnProjectSpace ps, PnMaterial m, PnMaterialAssignment ma " +
					" WHERE shp.comp_id.spaceId = ps.projectId AND shp.comp_id.planId = pp.planId " +
					" AND pht.comp_id.taskId = t.taskId AND pp.planId = pht.comp_id.planId " +
					" AND ma.comp_id.objectId = t.taskId AND m.materialId = ma.comp_id.materialId" +
					" AND ps.projectId = :projectId AND t.recordStatus = 'A' " +
					" AND t.percentComplete = :percentComplete ";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", spaceID);
			query.setInteger("percentComplete", percentComplete);
			materialList = new PnMaterialList(query.list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of materials " + e.getMessage());
		}
	    
		
		return materialList;
	}

}
