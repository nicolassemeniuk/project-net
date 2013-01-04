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
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnMethodologyModulesDAO;
import net.project.hibernate.model.PnMethodologyModules;
import net.project.hibernate.model.PnMethodologyModulesPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnMethodologyModulesDAOImpl extends AbstractHibernateAnnotatedDAO<PnMethodologyModules, PnMethodologyModulesPK> implements IPnMethodologyModulesDAO {
    
	private static Logger log = Logger.getLogger(PnMethodologyModulesDAOImpl.class);

	public PnMethodologyModulesDAOImpl() {
        super(PnMethodologyModules.class);
    }

    @SuppressWarnings("unchecked")
	public List<PnMethodologyModules> getByMethodology(Integer methodologyId) {
    	List<PnMethodologyModules> methodologyModules = new ArrayList<PnMethodologyModules>();
    	
    	String hql = " select mm.comp_id.methodologySpaceId, mm.comp_id.moduleId " +
    				 "	from PnMethodologyModules mm " +
    				 "	where mm.comp_id.methodologySpaceId = :methodologySpaceId ";
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			query.setInteger("methodologySpaceId", methodologyId);
			
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnMethodologyModules methMod = new PnMethodologyModules();
				methMod.setComp_id(new PnMethodologyModulesPK((Integer) row[0], (Integer) row[1]));
				
				methodologyModules.add(methMod);
			}
		} catch (Exception e) {
			log.error("Error occured while getting PnMethodologyModules records by methodologyId: \n" + e.getMessage());
		}
		
		return methodologyModules;
    }
    
    /* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnMethodologyModulesDAO#saveMethodology(net.project.hibernate.model.PnMethodologyModules)
	 */
	public void saveMethodology(PnMethodologyModulesPK methodologyModule) {
    	StringBuffer hql = new StringBuffer();
    	hql.append(" insert into PN_METHODOLOGY_MODULES(METHODOLOGY_ID, MODULE_ID) values (:methodologyId, :moduleId) ");

		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql.toString());
			query.setInteger("methodologyId", methodologyModule.getMethodologySpaceId());
			query.setInteger("moduleId", methodologyModule.getModuleId());
			query.executeUpdate();
			
		} catch (Exception e) {
			log.error("Error occured while saving PnMethodologyModules record: \n" + e.getMessage());
		}		
		
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnMethodologyModulesDAO#delete(java.lang.Integer)
	 */
	public void delete(Integer methodologyId) {
		StringBuffer hql = new StringBuffer();
    	hql.append(" delete from PnMethodologyModules mm where mm.comp_id.methodologySpaceId = :methodologyId ");

		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql.toString());
			query.setInteger("methodologyId", methodologyId);
			query.executeUpdate();
			
		} catch (Exception e) {
			log.error("Error occured while deleting PnMethodologyModules records with methodologyId: " + methodologyId + " \n" + e.getMessage());
			e.printStackTrace();
		}		
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IDAO#createOrUpdate(java.lang.Object)
	 */
	public void createOrUpdate(PnMethodologyModules object) {
		// TODO Auto-generated method stub
	}

}
