package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IReportsDAO;
import net.project.hibernate.model.PnPerson;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ReportsDAOImpl implements IReportsDAO {

	/**
	 * Spring's hibernate template.
	 */
	private HibernateTemplate hibernateTemplate;	
	
	
	
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	
	public List getProjectActivity() {
				
		List projects = new ArrayList();
		try {
			Iterator itr = (Iterator)hibernateTemplate.execute(new HibernateCallback(){
					public Object doInHibernate(Session session) throws HibernateException {
						Query query = null;
						Iterator it = null;
						try {
							String sql2 = " select  new net.project.hibernate.model.reports.PnProjectActivity(" +
							" ps.projectId, " +
							" ps.projectName, " +
							" s.accessDate, " +
							" p.firstName, " +
							" p.lastName, " +
							" a.officePhone, " +
							" p.email)  " +
					     " from    " +
					     		" PnSpaceAccessHistory s, " +
					            " PnProjectSpace ps,  " +
					            " PnPerson p,  " +
					            " PnAddress a, " +
					            " PnPersonProfile pp " + 
					     " where " +  
					           "(s.comp_id.spaceId, s.accessDate) in ( " +      
					           " select sah.comp_id.spaceId, max(sah.accessDate) " + 
					           " from PnSpaceAccessHistory sah, PnObject o " + 
					           " where sah.comp_id.spaceId=o.objectId and " + 
					           " o.pnObjectType.objectType=:project " +  
					           " group by sah.comp_id.spaceId) and " + 
					           " ps.projectId = s.comp_id.spaceId and " + 
					           " p.personId = s.comp_id.personId and " + 
					           " a.addressId = pp.addressId and " + 
					           " pp.personId = p. personId " +
					      " order by " +
					           " s.accessDate desc";
							query = session.createQuery(sql2);
							query.setString("project", "project");
							it = query.list().iterator();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return it;
					}		
			});
			while (itr.hasNext()){
				projects.add(itr.next());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return projects;
	}



	public List getUsersActivity() {

		String sql = "select new net.project.hibernate.model.reports.PnUserActivity(p.firstName, p.lastName, " +
					 "u.lastLogin, p.email, a.officePhone, p.userStatus ) " +
					 "from PnPerson p, PnUser u, PnAddress a, PnPersonProfile pp " +
					 " where u.userId = p.personId and a.addressId = pp.addressId and pp.personId = p.personId and u.lastLogin is not null order by u.lastLogin desc";
		List users = new ArrayList();
		try {
			Iterator itr = hibernateTemplate.find(sql).iterator();
			while (itr.hasNext()){
				users.add(itr.next());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return users;
	}
	
	public List getUsersActivityNulls() {

		String sql = "select new net.project.hibernate.model.reports.PnUserActivity(p.firstName, p.lastName, " +
					 "u.lastLogin, p.email, a.officePhone, p.userStatus ) " +
					 "from PnPerson p, PnUser u, PnAddress a, PnPersonProfile pp " +
					 " where u.userId = p.personId and a.addressId = pp.addressId and pp.personId = p.personId and u.lastLogin is null order by u.lastLogin desc";
		List users = new ArrayList();
		try {
			Iterator itr = hibernateTemplate.find(sql).iterator();
			while (itr.hasNext()){
				users.add(itr.next());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return users;
	}	


	public PnPerson getPerson(BigDecimal personId) {
		PnPerson person = new PnPerson();
		String sql = "from PnPerson where personId = 1";
		try{
			Iterator itr = hibernateTemplate.find(sql).iterator();
			person = (PnPerson) itr.next();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return person;
	}

	
	
}
