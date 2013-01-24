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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnAssignmentWorkDAO;
import net.project.hibernate.model.PnAssignmentWork;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnAssignmentWorkDAOImpl extends AbstractHibernateAnnotatedDAO<PnAssignmentWork, Integer> implements IPnAssignmentWorkDAO {
	
	private static Logger log = Logger.getLogger(PnAssignmentDAOImpl.class);
	
	public PnAssignmentWorkDAOImpl() {
		super(PnAssignmentWork.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnAssignmentWorkDAO#getTotalWorkByDate(java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<PnAssignmentWork> getTotalWorkByDate(Integer[] personIds, Date startDate, Date endDate, Integer spaceId){
		List<PnAssignmentWork> workCapturedEntries = new ArrayList<PnAssignmentWork>();
		String sql ="select aw.objectId, aw.workStart, aw.workEnd, aw.work, aw.workUnits, aw.personId from " +
			        "PnAssignmentWork aw,PnObject o " ;
			
		if(spaceId != null){
				sql += ",PnObjectSpace os ";
			}
			     
			sql +=  "where aw.workStart <= :endDate " +
			"and aw.workEnd >= :startDate and o.objectId = aw.objectId and o.recordStatus = 'A' ";
			
		    if (personIds != null) {
		    	sql +=  "and aw.personId in (:personIds) ";
		    }
					
			if(spaceId != null){
				sql += " and os.comp_id.objectId = o.objectId " +
						"and os.comp_id.spaceId in (select pshs.comp_id.childSpaceId from PnSpaceHasSpace pshs where comp_id.parentSpaceId = :spaceId)";
			}
			
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if(personIds != null){
				query.setParameterList ("personIds", personIds);
			} 
			query.setDate("endDate", endDate);
			query.setDate("startDate", startDate);
			if(spaceId != null){
				query.setInteger("spaceId", spaceId);
			}
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnAssignmentWork pnAssignmentWork = new PnAssignmentWork();
				Object[] row = (Object[]) result.next();
				pnAssignmentWork.setObjectId((Integer) row[0]);
				pnAssignmentWork.setWorkStart((Timestamp) row[1]);
				pnAssignmentWork.setWorkEnd((Timestamp) row[2]);
				pnAssignmentWork.setWork((BigDecimal) row[3]);
				pnAssignmentWork.setWorkUnits((Integer) row[4]);
				pnAssignmentWork.setPersonId((Integer) row[5]);
				workCapturedEntries.add(pnAssignmentWork);
			}
		} catch (Exception e) {
			log.error("Error Occured while retrieving Work by date: "+e.getMessage());
		}
		return workCapturedEntries;
	}
	
	public PnAssignmentWork getWorkDetailsById(Integer assignmentWorkId){
		PnAssignmentWork pnAssignmentWork = null;
		String sql = "select aw.workStart, aw.workEnd, aw.work, aw.workUnits, aw.comments " +
					 "from PnAssignmentWork aw where aw.assignmentWorkId = :assignmentWorkId";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("assignmentWorkId", assignmentWorkId);
			Iterator result = query.list().iterator();
			if(result.hasNext()){
				pnAssignmentWork = new PnAssignmentWork();
				Object[] row = (Object[]) result.next();
				pnAssignmentWork.setWorkStart((Timestamp) row[0]);
				pnAssignmentWork.setWorkEnd((Timestamp) row[1]);
				pnAssignmentWork.setWork((BigDecimal) row[2]);
				pnAssignmentWork.setWorkUnits((Integer) row[3]);
				pnAssignmentWork.setComments((String)row[4]);
			}
		} catch (Exception e) {
			log.error("Error Occured while retrieving Work Details by Id: "+e.getMessage());
		}
		return pnAssignmentWork;
	}
}
