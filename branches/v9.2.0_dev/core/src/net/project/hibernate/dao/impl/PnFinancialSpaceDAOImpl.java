package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnFinancialSpaceDAO;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.model.PnMaterial;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnFinancialSpaceDAOImpl extends AbstractHibernateAnnotatedDAO<PnFinancialSpace, Integer> implements IPnFinancialSpaceDAO {

	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnFinancialSpaceDAOImpl() {
		super(PnFinancialSpace.class);
	}
	
	@Override
	public PnFinancialSpace getFinancialSpaceById(Integer financialSpaceId) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnMaterial.class);
			criteria.add(Restrictions.ne("recordStatus", "D"));
			PnFinancialSpace pnFinancialSpace = (PnFinancialSpace) criteria.uniqueResult();
			//PnFinancialSpace pnFinancialSpace = (PnFinancialSpace) session.get(PnFinancialSpace.class, financialSpaceId);
			session.close();
			return pnFinancialSpace;
		} catch (Exception e) {
			log.error("Error occurred while getting financial space by id " + e.getMessage());
		}
		return null;
	}

}
