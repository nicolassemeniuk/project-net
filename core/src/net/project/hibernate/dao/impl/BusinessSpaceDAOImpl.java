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

import java.util.List;

import net.project.hibernate.dao.IBusinessSpaceDAO;
import net.project.hibernate.model.PnBusinessSpaceView;
import net.project.security.User;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BusinessSpaceDAOImpl extends AbstractHibernateAnnotatedDAO<PnBusinessSpaceView, Integer> implements IBusinessSpaceDAO {

	private static Logger log = Logger.getLogger(BusinessSpaceDAOImpl.class);
	
	public BusinessSpaceDAOImpl() {
		super(PnBusinessSpaceView.class);
	}
	

	public List<PnBusinessSpaceView> findByUser(User user, String recordStatus) {
		List<PnBusinessSpaceView> results = null;		
		if (user == null || recordStatus == null) {
			throw new NullPointerException("User and record status are required");
		}
		try {
			String sql = " select distinct new PnBusinessSpaceView(b.businessSpaceId, b.businessId, b.spaceType, b.completePortfolioId, " +
					" b.recordStatus, b.isMaster, b.businessCategoryId, b.brandId, b.billingAccountId, b.addressId, " +
					" b.businessName, b.businessDesc, b.businessType, b.logoImageId, b.isLocal, b.remoteHostId, " +
					" b.remoteBusinessId, b.numProjects, b.numPeople, b.includesEveryone) " +
					" from " +
					" PnBusinessSpaceView b, " +
					" PnSpaceHasPerson shp " +
					" where " +
					" b.recordStatus = :recordStatus and " +
					" shp.comp_id.spaceId = b.businessSpaceId and " +
					" (shp.comp_id.personId = :personId or b.includesEveryone = 1) and " +
					" shp.recordStatus = :recordStatus2 " +
					" order by b.businessName asc ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			results = query.setString("recordStatus", recordStatus).setString("recordStatus2", recordStatus).setInteger("personId", Integer.valueOf(user.getID())).list();			
	        
		} catch (Exception e) {
			log.error("Error occurred while getting business by user in BusinessSpaceDAOImpl: " + e.getMessage());
		}
		return results;
	}

}
