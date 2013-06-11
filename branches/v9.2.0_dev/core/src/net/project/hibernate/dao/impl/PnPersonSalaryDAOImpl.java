package net.project.hibernate.dao.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnPersonSalaryDAOImpl extends AbstractHibernateAnnotatedDAO<PnPersonSalary, PnPersonSalaryPK> implements IPnPersonSalaryDAO {

	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnPersonSalaryDAOImpl() {
		super(PnPersonSalary.class);
	}
	
	@Override
	public PnPersonSalary getPersonSalaryByPersonId(Integer personID) {
		
		PnPersonSalary pnPersonSalary = null;	
		
		
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Restrictions.eq("personId", personID));
			//criteria.setProjection(Projections.max("startDate"));
			List<PnPersonSalary> salaries = criteria.list();
			if(salaries.size()>0)
				pnPersonSalary = salaries.get(salaries.size()-1);
			session.close();			
			
			if(pnPersonSalary != null){
				return pnPersonSalary;				
			} else {
				//Return an empty person salary.
				PnPersonSalaryPK pk = new PnPersonSalaryPK(0);
				return new PnPersonSalary(pk, personID, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Float("0.0"), "A");
			}
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		
//		try {
//			
//			SessionFactory factory = getHibernateTemplate().getSessionFactory();
//			Session session = factory.openSession();
//
//			pnPersonSalary = (PnPersonSalary) session.get(PnPersonSalary.class, pk);
//			session.close();
//			if(pnPersonSalary!=null){
//				return pnPersonSalary;
//			} else {				
//				return new PnPersonSalary(pk, new Float(0.00));
//			}
//			
//		} catch (Exception e) {
//			log.error("Error occurred while getting a person salary by person id " + e.getMessage());
//		}
		return pnPersonSalary;
		
	}

	@Override
	public PnPersonSalary getPersonSalaryById(Integer personSalaryID) {
		PnPersonSalary pnPersonSalary = null;			
		
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnMaterial.class);
			criteria.add(Restrictions.eq("comp_id.personSalaryId", personSalaryID));
			criteria.setProjection(Projections.max("startDate"));
			pnPersonSalary = (PnPersonSalary) criteria.uniqueResult();
			session.close();			
			
			if(pnPersonSalary != null){
				return pnPersonSalary;				
			} else {
				//Return an empty person salary.
				PnPersonSalaryPK pk = new PnPersonSalaryPK(personSalaryID);
				return new PnPersonSalary(pk, 0, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Float("0.0"), "A");
			}
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		
		return pnPersonSalary;
	}

}
