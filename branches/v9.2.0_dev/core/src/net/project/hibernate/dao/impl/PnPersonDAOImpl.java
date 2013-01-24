/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnPersonDAOImpl extends AbstractHibernateAnnotatedDAO<PnPerson, Integer> implements IPnPersonDAO {

	private static Logger log = Logger.getLogger(PnPersonDAOImpl.class);

	public PnPersonDAOImpl() {
		super(PnPerson.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnPerson> getUniqueMembersOfBusinessCollection(List<Integer> businessIds) {

		List<PnPerson> result = new ArrayList<PnPerson>();
		try {
			String sql = "select distinct new PnPerson(p.personId, p.firstName, p.lastName, p.displayName, p.email,"
					+ " p.userStatus, p.membershipPortfolioId, p.recordStatus, p.createdDate)" + " from PnPerson p, PnBusiness b, PnSpaceHasPerson shp"
					+ " where shp.comp_id.spaceId = b.businessId and shp.comp_id.personId = p.personId and" + " p.userStatus != :userStatus and b.businessId in (:businessIds)" 
					+ " order by p.lastName";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setString("userStatus", "Deleted");
			query.setParameterList("businessIds", businessIds.toArray());
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnPersonDAO#getAllPersonsIds(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<PnPerson> getAllPersonsIds(Integer personId) {
		List<PnPerson> result = new ArrayList<PnPerson>();

		try {
			String query = " select new PnPerson(p.personId , p.firstName, p.lastName, p.displayName) from PnPerson p where p.personId <> :personId ";
			result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(query).setInteger("personId", personId).list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<PnPerson> getAllPersonsIds() {
		List<PnPerson> result = null;

		try {
			String query = " select new PnPerson(p.personId , p.firstName, p.lastName, p.displayName) from PnPerson p order by upper(p.displayName)";
			result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(query).list();
		} catch (Exception e) {
			log.error("Error occured while getting all person ids "+e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByProjectId(Integer projectId) {
		List<PnPerson> result = new ArrayList<PnPerson>();
		
		try {
			String sql = "select new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus, "
				+ " pp.membershipPortfolioId,pp.createdDate, pp.recordStatus) "
				+ " FROM PnSpaceHasPerson shp, PnPerson pp"
				+ " WHERE shp.comp_id.spaceId = :projectId and shp.comp_id.personId = pp.personId and " 
				+ " pp.userStatus = :userStatus "
				+ " order by upper(pp.displayName) ";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setString("userStatus", "Active");
			result = query.list();
		} catch (Exception e) {
			log.error("Error occurred while getting person's list by project id " + e.getMessage());
		}
		return result;
	}	
		
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByBusinessId(Integer businessId) {
		List<PnPerson> result = new ArrayList<PnPerson>();
		
		try {
			String sql = "select new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus, "
					+ " pp.membershipPortfolioId,pp.createdDate, pp.recordStatus) "
					+ " FROM PnSpaceHasPerson shp, PnPerson pp"
					+ " WHERE shp.comp_id.spaceId = :businessId and shp.comp_id.personId = pp.personId and " 
					+ " pp.userStatus = :userStatus "
					+ " order by pp.lastName";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setString("userStatus", "Active");
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());			
		}
		return result;
	}
	
	/** Method to get persons by all businesses and all projects */
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByAllBusinessesAndProjects() {
		List<PnPerson> result = new ArrayList<PnPerson>();		
		
		try{
			String sql = "select distinct new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus,"
				+ " pp.membershipPortfolioId, pp.createdDate, pp.recordStatus) "
				+ " FROM PnSpaceHasPerson php, PnPerson pp "
				+ " WHERE php.comp_id.personId = pp.personId "
				+ " and pp.userStatus = :userStatus "
				+ " order by pp.lastName";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);			
			query.setString("userStatus", "Active");
			result = query.list();			
		}catch (Exception e) {
			log.error(e.getMessage());			
		}		    			
		return result;
	}
	
	/*@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByBusinessAndProjectId(Integer businessId, Integer projectId) {
		List<PnPerson> result = new ArrayList<PnPerson>();		
		try{
			String sql = "select new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus, "
				+ " pp.membershipPortfolioId, pp.createdDate, pp.recordStatus) "
				+ " FROM PnSpaceHasPerson php, PnSpaceHasPerson bhp, PnPerson pp "
				+ " WHERE bhp.comp_id.spaceId = :businessId " 
				+ " and bhp.comp_id.personId = pp.personId " 
				+ " and php.comp_id.spaceId = :projectId " 
				+ " and php.comp_id.personId = pp.personId "
				+ " and pp.userStatus = :userStatus "
				+ " order by pp.lastName";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setInteger("projectId", projectId);
			query.setString("userStatus", "Active");
			result = query.list();			
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}		    			
		return result;
	}*/
	
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByBusinessAndProjectId(Integer businessId, Integer projectId) {
		List<PnPerson> result = new ArrayList<PnPerson>();
		
		try{
			String sql = "select new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus, "
				+ " pp.membershipPortfolioId, pp.recordStatus, pp.createdDate) "
				+ " FROM PnSpaceHasPerson php,  PnPerson pp,  PnProjectSpace p, PnPortfolioHasSpace phs  "
				+ " WHERE php.comp_id.spaceId = :businessId and php.comp_id.personId = pp.personId " 
				+ " and pp.userStatus = :userStatus and p.projectId = phs.comp_id.spaceId and " 
				+ " phs.comp_id.portfolioId = pp.membershipPortfolioId and p.projectId = :projectId " 
				+ " order by pp.lastName";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setInteger("projectId", projectId);
			query.setString("userStatus", "Active");
			result = query.list();			
		}catch (Exception e) {
			log.error(e.getMessage());			
		}		    			
		return result;
	}
	
	/** Method to get persons by all businesses */
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByAllBusinesses() {
		List<PnPerson> result = new ArrayList<PnPerson>();		
		
		try{
			String sql = "select distinct new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus,"
				+ " pp.membershipPortfolioId, pp.createdDate, pp.recordStatus) "
				+ " FROM PnSpaceHasPerson php, PnBusiness b, PnPerson pp "
				+ " WHERE b.businessId = php.comp_id.spaceId " 
				+ " and php.comp_id.personId = pp.personId "
				+ " and pp.userStatus = :userStatus "
				+ " order by pp.lastName";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);			
			query.setString("userStatus", "Active");
			result = query.list();			
		}catch (Exception e) {
			log.error(e.getMessage());			
		}		    			
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnPersonDAO#getOnlineMembers(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Teammate> getOnlineMembers(Integer spaceId) {
		List<Teammate> onlineMembers = new ArrayList<Teammate>();	
		String useSpaceId = "";
		if(spaceId != null) {
			useSpaceId = " shp.comp_id.spaceId = :spaceId AND ";
		}
		
		String sql = " SELECT new net.project.hibernate.model.project_space.Teammate(p.personId, p.firstName, p.lastName, p.displayName, MAX(sah.accessDate) AS maxAccessDate) " +
				     " FROM PnSpaceHasPerson shp, PnSpaceAccessHistory sah, PnPerson p, PnUser u " +
				     " WHERE " + useSpaceId + " shp.comp_id.personId = sah.comp_id.personId AND " +
				     " shp.comp_id.personId = u.userId AND u.userId = p.personId AND u.isLogin = 1 " +
				     " GROUP BY p.personId, p.firstName, p.lastName, p.displayName, p.userStatus";
		
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		if(spaceId != null) {
			query.setInteger("spaceId", spaceId);
		}
		onlineMembers = query.list();
		
		return onlineMembers;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnPersonDAO#getAssignedResourcesByProject(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportProjectUsers> getAssignedResourcesByProject(Integer projectId, Date startDate, Date endDate) {
		List<ReportProjectUsers> persons = new ArrayList<ReportProjectUsers>();		
		
		String sql = "SELECT distinct p.personId, p.firstName, p.lastName " +
				" FROM  PnAssignment a, PnPerson p, PnSpaceHasPerson pshp " +
				" WHERE a.comp_id.personId = p.personId AND pshp.comp_id.personId = p.personId " +
				" AND a.comp_id.spaceId = pshp.comp_id.spaceId AND pshp.comp_id.spaceId = :projectId " +
				" AND a.recordStatus = 'A' AND p.userStatus != 'Deleted' " +
				" AND (( a.startDate between :startDate AND :endDate " +
				" OR a.endDate between :startDate AND :endDate ) " +
				" OR (a.startDate <= :startDate AND a.endDate >= :endDate ))"+
				" ORDER BY p.lastName ";
		
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("projectId", projectId);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
				
		Iterator results = query.list().iterator();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			ReportProjectUsers person = new ReportProjectUsers((Integer) row[0],(String) row[1],(String) row[2]);
			persons.add(person);
		}		
		
		return persons;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonByName(String firstName, String lastName, String email) {
		List<PnPerson> result = null;
		try {			
			StringBuffer query = new StringBuffer(" select new PnPerson(p.firstName, p.lastName, p.email, p.personId) from PnPerson p where 1=1 "); 
			if (firstName != null) {
				query.append(" and p.firstName like :firstName ");
			}else if (lastName != null){
				query.append(" and p.lastName like :lastName ");
			}else if (email != null){
				query.append(" and p.email like :email ");
			}
			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(query.toString());
			if (firstName != null) {
				qry.setString("firstName", firstName+"%");
			}else if (lastName != null){
				qry.setString("lastName", lastName+"%");
			}else if (email != null){
				qry.setString("email", email+"%");
			}
			result = qry.list();
		} catch (Exception e) {
			log.error("Error occured while getting all person ids "+e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<PnPerson> getPersonsByBusinessId(Integer businessId, String orderBy) {
		List<PnPerson> result = new ArrayList<PnPerson>();
		
		try {
			String sql = "select new PnPerson(pp.personId, pp.firstName, pp.lastName, pp.displayName, pp.userStatus, "
					+ " pp.membershipPortfolioId,pp.createdDate, pp.recordStatus) "
					+ " FROM PnSpaceHasPerson shp, PnPerson pp"
					+ " WHERE shp.comp_id.spaceId = :businessId and shp.comp_id.personId = pp.personId and " 
					+ " pp.userStatus != :userStatus ";

			if (orderBy != null) {
				if (orderBy.equalsIgnoreCase("firstName")) {
					sql += " order by pp.firstName";
				} else if (orderBy.equalsIgnoreCase("displayName")) {
					sql += " order by pp.displayName";
				} else if (orderBy.equalsIgnoreCase("lastName")) {
					sql += " order by pp.lastName";
				} else if (orderBy.equalsIgnoreCase("personId")) {
					sql += " order by pp.personId";
				}
			}

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setString("userStatus", "Deleted");
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());			
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnPersonDAO#getPersonNameById(java.lang.Integer)
	 */
	public PnPerson getPersonNameById(Integer personId) {
		PnPerson pnPerson = null;
		try {
			String sql = " select new PnPerson(p.personId, p.firstName, p.lastName, p.displayName) from PnPerson p where p.personId = :personId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("personId", personId);
			pnPerson = (PnPerson) query.uniqueResult();
		}catch (Exception e) {
			log.error("Error occurred while getting person's name by id "+e.getMessage());
		}
		return pnPerson;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnPersonDAO#getImageIdByPersonId(java.lang.Integer)
     */
    public Integer getImageIdByPersonId(Integer personId) {
        Integer imageId = null;
        try {
            String sql = " select p.imageId from PnPerson p where p.personId = :personId";
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
            query.setInteger("personId", personId);
            imageId = (Integer)query.uniqueResult();
        }catch (Exception e) {
            log.error("Error occurred while getting person's image id by person id : " + e.getMessage());
        }
        return imageId;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnPersonDAO#getPesronNameAndImageIdByPersonId(java.lang.Integer)
     */
    public PnPerson getPesronNameAndImageIdByPersonId(Integer personId) {
    	PnPerson person = null;
        try {
            String sql = " select new PnPerson(p.personId, p.firstName, p.lastName, p.displayName, p.imageId) from PnPerson p where p.personId = :personId ";
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
            query.setInteger("personId", personId);
            person = (PnPerson) query.uniqueResult();
        }catch (Exception e) {
            log.error("Error occurred while getting person's name and image id by person id : " + e.getMessage());
        }
        return person;
    }
    
    /**
     * 
     * @param personId
     * @return
     */
    public PnPerson getPersonById(Integer personId) {
		PnPerson pnPerson = null;
		try {
			String sql = " from PnPerson p where p.personId = :personId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("personId", personId);
			pnPerson = (PnPerson) query.uniqueResult();
		}catch (Exception e) {
			log.error("Error occurred while getting person's name by id "+e.getMessage());
		}
		return pnPerson;
	}
    
}
