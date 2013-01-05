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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional 
@Repository 
public class PnWeblogCommentDAOImpl extends AbstractHibernateAnnotatedDAO<PnWeblogComment, Integer> implements net.project.hibernate.dao.IPnWeblogCommentDAO {
	
	private static Logger log = Logger.getLogger(PnWeblogCommentDAOImpl.class);

	public PnWeblogCommentDAOImpl() {
		super(PnWeblogComment.class);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogCommentDAO#getWeblogCommentsForWeblogEntry(java.lang.Integer, java.lang.String)
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId, String status) {
		Set<PnWeblogComment> comments = new TreeSet<PnWeblogComment>(Collections.reverseOrder());
		String sql = " select wc.pnWeblogEntry.pnPerson.personId, wc from PnWeblogComment wc where wc.pnWeblogEntry.weblogEntryId = :weblogEntryId ";
			if(status != null){
				sql += " and wc.status = :status ";
			}
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("weblogEntryId", weblogEntryId);
			if(status != null){
				query.setString("status", status);
			}
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnWeblogComment pnWeblogComment = ((PnWeblogComment) row[1]);
				pnWeblogComment.setEntryByPersonId((Integer) row[0]);
				comments.add(pnWeblogComment);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting weblog comments by weblog entry " + e.getMessage());
		}
		return comments;
	}
	
	public PnWeblogComment getWeblogCommentByCommentId(Integer commentId){
		
			PnWeblogComment pnWeblogComment = new PnWeblogComment();
			PnPerson pnPerson = new PnPerson();
			String sql = "  select p1.displayName as CommentedBy, c.content, c.pnWeblogEntry.weblogEntryId, p2.displayName as EntryAuthor "
						+"	from PnWeblogComment c, PnWeblogEntry w, PnPerson p1, PnPerson p2 "
						+"	where w.weblogEntryId = c.pnWeblogEntry.weblogEntryId and p1.personId = c.personId " 
						+"	and p2.personId = w.pnPerson.personId and c.commentId = :commentId";
			try {
				Query query = getHibernateTemplate().getSessionFactory()
						.getCurrentSession().createQuery(sql);
				query.setString("commentId", commentId.toString());
				Iterator result = query.list().iterator();
				if (result.hasNext()) {
					Object[] row = (Object[]) result.next();
					pnWeblogComment.setName((String) row[0]);
					pnWeblogComment.setContent((String) row[1]);
					pnPerson.setDisplayName((String) row[3]);
					PnWeblogEntry entry = new PnWeblogEntry();
					entry.setWeblogEntryId((Integer) row[2]);
					entry.setPnPerson(pnPerson);
					pnWeblogComment.setPnWeblogEntry(entry);
				}
			} catch (Exception e) {
				log.error("Error occured while getting weblog entries : "+e.getMessage());
			}		
			return pnWeblogComment;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogCommentDAO#updateCommentStatus(java.lang.Integer, java.lang.String)
	 */
	public Integer updateCommentStatus(Integer commentId, String status) {
		Integer statusChanged = null;
		String sql = " update PnWeblogComment set status = :status  where commentId = :commentId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("commentId", commentId);
			query.setString("status", status);
			statusChanged = query.executeUpdate();
		} catch (Exception e) {
			log.error("Error occurred while updating comment entry " + e.getMessage());
		}
		return statusChanged;
	}
}
