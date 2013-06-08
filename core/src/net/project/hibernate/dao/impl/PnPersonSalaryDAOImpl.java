package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
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
	public PnPersonSalary getPersonSalary(Integer personID) {
		PnPersonSalaryPK pk = new PnPersonSalaryPK(personID);
		PnPersonSalary pnPersonSalary = null;		
		
		try {
			
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			pnPersonSalary = (PnPersonSalary) session.get(PnPersonSalary.class, pk);
			session.close();
			if(pnPersonSalary!=null){
				return pnPersonSalary;
			} else {				
				return new PnPersonSalary(pk, new Float(0.00));
			}
			
		} catch (Exception e) {
			log.error("Error occurred while getting a person salary by person id " + e.getMessage());
		}
		return pnPersonSalary;
		
	}

}
