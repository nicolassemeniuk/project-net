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
import java.util.List;

import net.project.hibernate.dao.IPnObjectLinkDAO;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.model.PnObjectLinkPK;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnObjectLinkDAOImpl extends AbstractHibernateAnnotatedDAO<PnObjectLink, PnObjectLinkPK> implements IPnObjectLinkDAO {
	
	public PnObjectLinkDAOImpl() {
		super(PnObjectLink.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnObjectLink> getObjectsById(Integer parentId, Integer contextId){
		List<PnObjectLink> result = new ArrayList<PnObjectLink>();
		try {
			String query = " select new PnObjectLink(p.comp_id.fromObjectId, p.comp_id.toObjectId, p.comp_id.context) from PnObjectLink p where p.comp_id.fromObjectId = :parentId and p.comp_id.context = :contextId ";
			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(query);
			qry.setInteger("parentId", parentId);
			qry.setInteger("contextId", contextId);
			result = qry.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
    @SuppressWarnings("unchecked")
    public List<PnObjectLink> getObjectLinksByParent(Integer fromObjectId){
    	List<PnObjectLink> result = null;
    	try {
    		String str = " select new PnObjectLink(p.comp_id.fromObjectId, p.comp_id.toObjectId, p.comp_id.context) from PnObjectLink p where p.comp_id.fromObjectId=:fromObjectId ";
    		result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(str).setInteger("fromObjectId", fromObjectId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
    
    public List<PnObjectLink> getObjectLinksByChild(Integer toObjectId){
    	List<PnObjectLink> result = null;
    	try {
    		String str = " select new PnObjectLink(p.comp_id.fromObjectId, p.comp_id.toObjectId, p.comp_id.context) from PnObjectLink p where p.comp_id.toObjectId=:toObjectId ";
    		result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(str).setInteger("toObjectId", toObjectId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
}
