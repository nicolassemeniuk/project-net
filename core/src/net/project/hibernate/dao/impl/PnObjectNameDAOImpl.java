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

import net.project.hibernate.dao.IPnObjectNameDAO;
import net.project.hibernate.model.PnObjectName;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository 
public class PnObjectNameDAOImpl extends AbstractHibernateAnnotatedDAO<PnObjectName, Integer> implements IPnObjectNameDAO {
	
	private Logger log = Logger.getLogger(PnObjectDAOImpl.class);

	public PnObjectNameDAOImpl() {
		super(PnObjectName.class);
	}	

    public String getNameFofObject(Integer objectId) {
    	String objectName = null;
    	
		try {
	    	StringBuffer sql = new StringBuffer(" select o.name from PnObjectName o ");
	    	sql.append(" where o.objectId = :objectId ");
	    	
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("objectId", objectId);
			objectName = (String) query.uniqueResult();			
		} catch (Exception e) {
			log.error("Error occured while getting PnObjectName property name by object ID: " + e.getMessage());
			e.printStackTrace();
		}
		return objectName;
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnObjectNameDAO#getObjectNameBySubscriptionId(java.lang.Integer)
	 */
	public String getObjectNameBySubscriptionId(Integer subscriptionId) {
		String objectName = null;
		
		try {
			StringBuffer sql = new StringBuffer(" select o.name from PnObjectName o, PnObjectHasSubscription ohs");
			sql.append(" where o.objectId = ohs.comp_id.objectId ");
			sql.append(" and ohs.comp_id.subscriptionId = :subscriptionId ");
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("subscriptionId", subscriptionId);
			objectName = (String) query.uniqueResult();			
		} catch (Exception e) {
			log.error("Error occured while getting PnObjectName property name by subscription id: " + e.getMessage());
		}
		return objectName;
	}

}
