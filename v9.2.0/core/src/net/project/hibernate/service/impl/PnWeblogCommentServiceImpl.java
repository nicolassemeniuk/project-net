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
/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.project.base.ObjectType;
import net.project.hibernate.dao.IPnWeblogCommentDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectSpaceService;
import net.project.hibernate.service.IPnWeblogCommentService;

/**
 * @author
 *
 */
@Service(value="pnWeblogCommentService")
public class PnWeblogCommentServiceImpl implements IPnWeblogCommentService {

	/**
	 * PnWeblogComment data access object
	 */
	@Autowired
	private IPnWeblogCommentDAO pnWeblogCommentDAO;
	
	@Autowired
	private IPnObjectService pnObjectService;
	
	@Autowired
	private IPnObjectSpaceService pnObjectSpaceService;
	
	@Autowired
	private IPnObjectNameService pnObjectNameService;

	/**
	 * @param pnWeblogCommentDAO the pnWeblogCommentDAO to set
	 */
	public void setPnWeblogCommentDAO(IPnWeblogCommentDAO pnWeblogCommentDAO) {
		this.pnWeblogCommentDAO = pnWeblogCommentDAO;
	}
	
	/**
	 * @param pnObjectService the pnObjectService to set
	 */
	public void setPnObjectService(IPnObjectService pnObjectService) {
		this.pnObjectService = pnObjectService;
	}

	/**
	 * @param pnObjectSpaceService the pnObjectSpaceService to set
	 */
	public void setPnObjectSpaceService(IPnObjectSpaceService pnObjectSpaceService) {
		this.pnObjectSpaceService = pnObjectSpaceService;
	}

	/**
	 * @param pnObjectNameService the pnObjectNameService to set
	 */
	public void setPnObjectNameService(IPnObjectNameService pnObjectNameService) {
		this.pnObjectNameService = pnObjectNameService;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#save(net.project.hibernate.model.PnWeblogComment)
	 */
	public Integer save(PnWeblogComment pnWeblogComment) {
		// create object for weblog comment
		PnObject commentObject = new PnObject(ObjectType.BLOG_COMMENT, new Integer(1), new Date(System.currentTimeMillis()), "A");
		
		// save weblog comment object in database
		Integer commentObjectId = pnObjectService.saveObject(commentObject);
		pnWeblogComment.setCommentId(commentObjectId);
		
		// create object od pn_object_space for weblog entry comment
		PnObjectSpacePK pnObjectSpacePK = new PnObjectSpacePK(commentObjectId, pnWeblogComment.getPnWeblogEntry().getPnWeblog().getSpaceId());
		PnObjectSpace pnObjectSpace = new PnObjectSpace(pnObjectSpacePK);
		
		// save weblog entry comment object id and space id in pn_object_space table
		pnObjectSpaceService.save(pnObjectSpace);
		
		PnObjectName pnObjectName = new PnObjectName(commentObjectId,"prm.activity.comment");
		pnObjectNameService.save(pnObjectName);
		// save weblog entry comment in pn_weblog_comment table
		return pnWeblogCommentDAO.create(pnWeblogComment);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#update(net.project.hibernate.model.PnWeblogComment)
	 */
	public void update(PnWeblogComment pnWeblogComment) {
		pnWeblogCommentDAO.update(pnWeblogComment);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#findAll()
	 */
	public List<PnWeblogComment> findAll() {
		return pnWeblogCommentDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#get(java.lang.Integer)
	 */
	public PnWeblogComment get(Integer key) {
		return pnWeblogCommentDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#delete(net.project.hibernate.model.PnWeblogComment)
	 */
	public void delete(PnWeblogComment pnWeblogComment) {
		pnWeblogCommentDAO.delete(pnWeblogComment);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#getWeblogCommentsForWeblogEntry(java.lang.Integer)
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId) {
		return pnWeblogCommentDAO.getWeblogCommentsForWeblogEntry(weblogEntryId, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#getWeblogCommentsForWeblogEntry(java.lang.Integer,java.lang.String)
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId, String status) {
		return pnWeblogCommentDAO.getWeblogCommentsForWeblogEntry(weblogEntryId, status);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#getWeblogCommentByCommentId(java.lang.Integer)
	 */
	public PnWeblogComment getWeblogCommentByCommentId(Integer commentId){
		return pnWeblogCommentDAO.getWeblogCommentByCommentId(commentId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogCommentService#updateCommentStatus(java.lang.Integer, java.lang.String)
	 */
	public Integer updateCommentStatus(Integer commentId, String status) {
		return pnWeblogCommentDAO.updateCommentStatus(commentId, status);
	}
}
