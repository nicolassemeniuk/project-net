package net.project.hibernate.dao.impl;

import java.util.ArrayList;

import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.dao.IPnFinancialSpaceDAO;
import net.project.hibernate.model.PnFinancialSpace;

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

	private static Logger log = Logger.getLogger(PnFinancialSpaceDAOImpl.class);

	public PnFinancialSpaceDAOImpl() {
		super(PnFinancialSpace.class);
	}
	
	@Override
	public PnFinancialSpace getFinancialSpaceById(Integer financialSpaceId) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnFinancialSpace.class);
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.eq("financialSpaceId", financialSpaceId));
			PnFinancialSpace pnFinancialSpace = (PnFinancialSpace) criteria.uniqueResult();
			session.close();
			return pnFinancialSpace;
		} catch (Exception e) {
			log.error("Error occurred while getting financial space by id " + e.getMessage());
		}
		return null;
	}
	
	@Override
	public PnFinancialSpace getFinancialSpaceByIdAnyStatus(Integer financialSpaceId) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnFinancialSpace.class);
			criteria.add(Restrictions.eq("financialSpaceId", financialSpaceId));
			PnFinancialSpace pnFinancialSpace = (PnFinancialSpace) criteria.uniqueResult();
			session.close();
			return pnFinancialSpace;
		} catch (Exception e) {
			log.error("Error occurred while getting financial space by id " + e.getMessage());
		}
		return null;
	}

	@Override
	public PnFinancialSpaceList getFinancialSpacesByIds(ArrayList<Integer> additionalSpaceIDCollection) {
		PnFinancialSpaceList result = new PnFinancialSpaceList();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnFinancialSpace.class);
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.in("financialSpaceId", additionalSpaceIDCollection));
			result = new PnFinancialSpaceList(criteria.list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of financial spaces " + e.getMessage());
		}
		return result;
	}



	
	

}
