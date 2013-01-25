package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnMaterialTypeDAO;
import net.project.hibernate.model.PnMaterialType;
import net.project.material.PnMaterialTypeList;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnMaterialTypeDAOImpl extends AbstractHibernateAnnotatedDAO<PnMaterialType, Integer> implements IPnMaterialTypeDAO {

	public PnMaterialTypeDAOImpl() {
		super(PnMaterialType.class);
	}

	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	@Override
	public PnMaterialType getMaterialTypeById(Integer materialTypeId) {
		PnMaterialType pnMaterialType = null;
		try {
			String sql = " select new PnMaterialType(m.materialTypeId, m.materialTypeName) from PnMaterialType m where m.materialTypeId = :materialTypeId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("materialTypeId", materialTypeId);
			pnMaterialType = (PnMaterialType) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while getting material Type by id " + e.getMessage());
		}
		return pnMaterialType;
	}

	@Override
	public PnMaterialTypeList getMaterialTypes() {
		PnMaterialTypeList result = new PnMaterialTypeList();
		try {
			// HibernateTemplate.initialize(PnMaterial pnMaterial);
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			result = new PnMaterialTypeList(session.createQuery("FROM PnMaterialType").list());
			session.close();
			// String sql =
			// "select distinct new PnMaterial(m.materialId, m.materialName, m.materialDescription, m.materialCost) from PnMaterial m, PnMaterialType mt where "
			// +
			// "m.materialTypeId = mt.materialTypeId";
			// Query query =
			// getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			// result = new PnMaterialList(query.list());
		} catch (Exception e) {
			log.error("Error occurred while getting the list of material types " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
}
