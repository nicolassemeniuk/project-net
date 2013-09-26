package net.project.hibernate.dao.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;
import net.project.resource.PnPersonSalaryList;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public class PnPersonSalaryDAOImpl extends AbstractHibernateAnnotatedDAO<PnPersonSalary, PnPersonSalaryPK> implements IPnPersonSalaryDAO {

	private static Logger log = Logger.getLogger(PnPersonSalaryDAOImpl.class);

	public PnPersonSalaryDAOImpl() {
		super(PnPersonSalary.class);
	}
	
	@Override
	public PnPersonSalary getPersonSalaryById(Integer personSalaryId) {
		PnPersonSalaryPK pk = new PnPersonSalaryPK(personSalaryId);
		PnPersonSalary pnPersonSalary = new PnPersonSalary();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			pnPersonSalary=(PnPersonSalary) session.get(PnPersonSalary.class, pk);
			session.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return pnPersonSalary;
	}

	@Override
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personID) {
		PnPersonSalary pnPersonSalary = null;

		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			DetachedCriteria maxDateQuery = DetachedCriteria.forClass(PnPersonSalary.class);
			maxDateQuery.add(Restrictions.eq("personId", personID));
			maxDateQuery.add(Restrictions.le("startDate", new Date(System.currentTimeMillis())));
			maxDateQuery.setProjection(Projections.max("startDate"));

			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Subqueries.propertyEq("startDate", maxDateQuery));
			criteria.add(Restrictions.eq("personId", personID));
			criteria.add(Restrictions.eq("recordStatus", "A"));

			pnPersonSalary = (PnPersonSalary) criteria.uniqueResult();
			session.close();
			// This should be erased, all persons should have a salary
			if (pnPersonSalary == null) {
				PnPersonSalaryPK pk = new PnPersonSalaryPK(0);
				return pnPersonSalary = new PnPersonSalary(pk, personID, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Float(
						"0.0"), "A");
			}
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return pnPersonSalary;

	}
	
	@Override
	public PnPersonSalary getLastPersonSalaryByPersonId(Integer personID) {
		PnPersonSalary pnPersonSalary = null;

		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			DetachedCriteria maxDateQuery = DetachedCriteria.forClass(PnPersonSalary.class);
			maxDateQuery.add(Restrictions.eq("personId", personID));
			maxDateQuery.setProjection(Projections.max("startDate"));

			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Subqueries.propertyEq("startDate", maxDateQuery));
			criteria.add(Restrictions.eq("personId", personID));
			criteria.add(Restrictions.eq("recordStatus", "A"));

			pnPersonSalary = (PnPersonSalary) criteria.uniqueResult();
			session.close();
			// This should be erased, all persons should have a salary
			if (pnPersonSalary == null) {
				PnPersonSalaryPK pk = new PnPersonSalaryPK(0);
				return pnPersonSalary = new PnPersonSalary(pk, personID, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Float(
						"0.0"), "A");
			}
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return pnPersonSalary;
	}

	@Override
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date) {
		PnPersonSalary pnPersonSalary = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date fromDate = calendar.getTime();


		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			DetachedCriteria maxDateQuery = DetachedCriteria.forClass(PnPersonSalary.class);
			maxDateQuery.add(Restrictions.eq("personId", personId));
			maxDateQuery.add(Restrictions.or(Restrictions.and(Restrictions.le("startDate", fromDate),Restrictions.isNull("endDate")),Restrictions.and(Restrictions.le("startDate", fromDate),Restrictions.ge("endDate", fromDate))));
			maxDateQuery.setProjection(Projections.property("startDate"));

			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Subqueries.propertyEq("startDate", maxDateQuery));
			criteria.add(Restrictions.eq("personId", personId));

			pnPersonSalary = (PnPersonSalary) criteria.uniqueResult();
			session.close();
			// This should be erased, all persons should have a salary
			if (pnPersonSalary == null) {
				PnPersonSalaryPK pk = new PnPersonSalaryPK(0);
				return pnPersonSalary = new PnPersonSalary(pk, personId, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Float(
						"0.0"), "A");
			}

		} catch (HibernateException e) {
			log.error(e.getMessage());
		}

		return pnPersonSalary;

	}

	@SuppressWarnings("unchecked")
	@Override
	public PnPersonSalaryList getPersonSalaries(Integer personId) {
		PnPersonSalaryList result = new PnPersonSalaryList();
		
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Restrictions.eq("personId", personId));
			criteria.add(Restrictions.eq("recordStatus", "A"));
			criteria.addOrder(Order.asc("startDate"));
			result = new PnPersonSalaryList(criteria.list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of salaries " + e.getMessage());
		}
		
		return result;
	}

	@Override
	public PnPersonSalaryList getPersonSalaries(Integer personId, Date startDate, Date endDate) {
		PnPersonSalaryList result = new PnPersonSalaryList();
		
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnPersonSalary.class);
			criteria.add(Restrictions.eq("personId", personId));
			criteria.add(Restrictions.eq("recordStatus", "A"));
			if(startDate!=null)
				criteria.add(Restrictions.ge("startDate", startDate));
			if(endDate!=null)
				criteria.add(Restrictions.le("endDate", endDate));
			criteria.addOrder(Order.asc("startDate"));
			result = new PnPersonSalaryList(criteria.list());
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of salaries " + e.getMessage());
		}
		
		return result;
	}





}
