package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnPersonSalary;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class PnPersonSalaryDAOImpl extends AbstractHibernateAnnotatedDAO<PnPersonSalary, Integer> implements IPnPersonSalaryDAO {

	private static Logger log = Logger.getLogger(PnMaterialDAOImpl.class);

	public PnPersonSalaryDAOImpl() {
		super(PnPersonSalary.class);
	}
	
	@Override
	public PnPersonSalary getPersonSalary(Integer personID) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			PnPersonSalary pnPersonSalary = (PnPersonSalary) session.get(PnPersonSalary.class, personID);
			session.close();
			if(pnPersonSalary!=null){
				return pnPersonSalary;
			} else {
				return new PnPersonSalary(personID, new Float(0.00));
			}
			
		} catch (Exception e) {
			log.error("Error occurred while getting a person salary by person id " + e.getMessage());
		}
		return null;
	}

}
