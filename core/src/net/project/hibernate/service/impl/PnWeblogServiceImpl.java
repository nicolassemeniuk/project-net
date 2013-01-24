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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.ObjectType;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.dao.IPnWeblogDAO;
import net.project.hibernate.dao.impl.PnWeblogDAOImpl;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectSpaceService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnWeblogService;
import net.project.project.ProjectSpaceBean;
import net.project.security.User;
import net.project.space.Space;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 
 *
 */
@Service(value="pnWeblogService")
public class PnWeblogServiceImpl implements IPnWeblogService {
	
	private static Logger log = Logger.getLogger(PnWeblogDAOImpl.class);

	/**
	 * PnWeblog data access object
	 */
	@Autowired
	private IPnWeblogDAO pnWeblogDAO;
	
	@Autowired
	private IPnObjectService pnObjectService;
	
	@Autowired
	private IPnObjectSpaceService pnObjectSpaceService;
	
	@Autowired
	private IPnObjectNameService pnObjectNameService;

	/**
	 * @param pnWeblogDAO the pnWeblogDAO to set
	 */
	public void setPnWeblogDAO(IPnWeblogDAO pnWeblogDAO) {
		this.pnWeblogDAO = pnWeblogDAO;
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
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#save(net.project.hibernate.model.PnWeblog)
	 */
	public Integer save(PnWeblog pnWeblog) {
		// create object for weblog
		PnObject weblogObject = new PnObject(ObjectType.BLOG, new Integer(1), new Date(System.currentTimeMillis()), "A");
		
		// save weblog object in database
		Integer weblogObjectId = pnObjectService.saveObject(weblogObject);
		pnWeblog.setWeblogId(weblogObjectId);
		
		// create object od pn_object_space for weblog
		PnObjectSpacePK pnObjectSpacePK = new PnObjectSpacePK(weblogObjectId, pnWeblog.getSpaceId());
		PnObjectSpace pnObjectSpace = new PnObjectSpace(pnObjectSpacePK);
		
		// save weblog object id and space id in pn_object_space table
		pnObjectSpaceService.save(pnObjectSpace);
		
		//save weblog id and blog name in pn_object_name table
		pnObjectNameService.save(new PnObjectName(pnWeblog.getWeblogId(), pnWeblog.getName()));
		
		// save weblog object in pn_weblog table
		return pnWeblogDAO.create(pnWeblog);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#update(net.project.hibernate.model.PnWeblog)
	 */
	public void update(PnWeblog pnWeblog) {
		pnWeblogDAO.update(pnWeblog);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#findAll()
	 */
	public List<PnWeblog> findAll() {
		return pnWeblogDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#get(java.lang.Integer)
	 */
	public PnWeblog get(Integer key) {
		return pnWeblogDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#delete(net.project.hibernate.model.PnWeblog)
	 */
	public void delete(PnWeblog pnWeblog) {
		pnWeblogDAO.delete(pnWeblog);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#getByUserId(java.lang.Integer)
	 */
	public PnWeblog getByUserId(Integer userId) {		
		return pnWeblogDAO.getByUserId(userId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#getByUserAndSpaceId(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblog getByUserAndSpaceId(Integer userId, Integer spaceId) {
		return pnWeblogDAO.getByUserAndSpaceId(userId, spaceId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#getBySpaceId(java.lang.Integer)
	 */
	public PnWeblog getBySpaceId(Integer spaceId) {
		return pnWeblogDAO.getBySpaceId(spaceId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#getBySpaceId(java.lang.Integer, boolean)
	 */
	public PnWeblog getBySpaceId(Integer spaceId, boolean initializePersonObject) {
		return pnWeblogDAO.getBySpaceId(spaceId, initializePersonObject);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogService#createBlog(net.project.security.User, java.lang.String, java.lang.Integer, net.project.project.ProjectSpaceBean)
	 */
	@Autowired
	private IPnPersonService personService;
	
	public void setPersonService(IPnPersonService personService) {
		this.personService = personService;
	}

	public PnWeblog createBlog(User user, String spaceType, Integer spaceId, ProjectSpaceBean project) {
		// create person object from user details which is used
		// while saving blog as well as displaying person details on page		
		PnPerson person = personService.getPersonById(Integer.parseInt(user.getID()));

		PnWeblog pnWeblog = new PnWeblog();
		try {
			// setting blog name and description as per space type
			if (spaceType != null) {
				if (spaceType.equals(Space.PERSONAL_SPACE)) {
					pnWeblog.setName(user.getDisplayName());
					pnWeblog.setDescription(user.getDisplayName() + "'s Personal Blog");
				} else if (spaceType.equals(Space.PROJECT_SPACE)) {
					if (project == null)  {
						project = new ProjectSpaceBean();
						project.setID(spaceId.toString());
						project.load();
					}
					pnWeblog.setName(project.getName());
					if (project.getDescription() != null) {
						if(project.getDescription().length() < 1000){
							pnWeblog.setDescription(project.getDescription());
						} else {
							pnWeblog.setDescription(project.getDescription().substring(0, 1000));
						}
					} else {
						pnWeblog.setDescription(project.getName() + " Project Blog");
					}
				}
			}
			pnWeblog.setEmailAddress(user.getEmail());
			pnWeblog.setCreatedDate(Calendar.getInstance().getTime());
			pnWeblog.setPnPerson(person);
			pnWeblog.setSpaceId(spaceId);
			pnWeblog.setIsActive(WeblogConstants.BLOG_ACTIVE);
			pnWeblog.setIsEnabled(WeblogConstants.BLOG_ENABLED);
			pnWeblog.setDefaultAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
			pnWeblog.setDefaultCommentDays(WeblogConstants.DEFAULT_COMMENT_DAYS);
			pnWeblog.setAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
			pnWeblog.setEmailComments(WeblogConstants.DONT_ALLOW_EMAIL_COMMENTS);
			pnWeblog.setLocale(user.getLocaleCode());
			pnWeblog.setTimezone(user.getTimeZoneCode());
			Integer weblogId = save(pnWeblog);
			if(weblogId != null) {
				pnWeblog.setWeblogId(weblogId);
				return pnWeblog;
			}
		} catch (Exception pnetEx) {
			log.error("Error occurred while creating weblog "+pnetEx.getMessage());
		}
		return null;
	}

	public PnWeblog getPnWeblogById(int weblogId) {		
		return pnWeblogDAO.getPnWeblogById(weblogId);
	}
	
}
