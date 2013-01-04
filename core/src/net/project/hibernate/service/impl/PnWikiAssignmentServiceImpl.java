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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnWikiAssignmentDAO;
import net.project.hibernate.model.PnWikiAssignment;
import net.project.hibernate.service.IPnWikiAssignmentService;

/**
 * @author 
 *
 */
@Service(value="pnWikiAssignmentService")
public class PnWikiAssignmentServiceImpl implements IPnWikiAssignmentService {

	/**
	 * PnWikiPage data access object
	 */
	@Autowired
	private IPnWikiAssignmentDAO pnWikiAssignmentDAO;

	/**
	 * @param pnWikiPageDAO the pnWikiPageDAO to set
	 */
	public void setPnWikiAssignmentDAO(IPnWikiAssignmentDAO pnWikiAssignmentDAO) {
		this.pnWikiAssignmentDAO = pnWikiAssignmentDAO;
	}	
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#save(net.project.hibernate.model.PnWikiPage)
	 */
	public Integer save(PnWikiAssignment pnWikiAssignment) {
		return pnWikiAssignmentDAO.create(pnWikiAssignment);
	}	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#update(net.project.hibernate.model.PnWikiPage)
	 */
	public void update(PnWikiAssignment pnWikiAssignment) {
		pnWikiAssignmentDAO.update(pnWikiAssignment);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#findAll()
	 */
	public List<PnWikiAssignment> findAll() {
		return pnWikiAssignmentDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#get(java.lang.Integer)
	 */
	public PnWikiAssignment get(Integer key) {
		return pnWikiAssignmentDAO.findByPimaryKey(key);
	}
	
	/* Altered delete method to change page record_status to D
	 * 
	 * (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#delete(net.project.hibernate.model.PnWikiPage)
	 */
	public void delete(PnWikiAssignment pnWikiAssignment) {
		pnWikiAssignmentDAO.delete(pnWikiAssignment);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAssignmentService#getWikiAssignmentByObjectId(java.lang.Integer)
	 */
	public PnWikiAssignment getWikiAssignmentByObjectId(Integer objectId) {
		return pnWikiAssignmentDAO.getWikiAssignmentByObjectId(objectId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAssignmentService#deleteWikiAssignmentsByPageId(java.lang.Integer)
	 */
	public void deleteWikiAssignmentsByPageId(Integer wikiPageId) {
		pnWikiAssignmentDAO.deleteWikiAssignmentsByPageId(wikiPageId);
	}
}
