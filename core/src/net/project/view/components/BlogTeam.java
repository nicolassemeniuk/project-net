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
package net.project.view.components;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.view.pages.blog.ViewBlog;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * @author
 */
public class BlogTeam {
	
	private static Logger log;

	private boolean isMore;

	private String linkHrefValue;

	private String linkDisplayName;

	private String moreLinkHrefValue;

	private List<BlogTeam> teamLink;

	private List<PnPerson> personList;
	
	private PnPerson person;

	@Inject
	private PropertyAccess access;
	
	private GenericSelectModel<PnPerson> teamBeans;

	@InjectPage
	private ViewBlog viewBlog;

	/**
	 * Setting up values before page render
	 */
	@SetupRender
	void setValues() {
		log = Logger.getLogger(BlogTeam.class);
		personList = new  ArrayList<PnPerson>();
		teamLink = new ArrayList<BlogTeam>();
		try {
			IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
			List<PnPerson> persons = pnPersonService.getPersonsByProjectId(viewBlog.getSpaceId());
						
			PnPerson firstOption = new PnPerson();
			firstOption.setPersonId(0);
			firstOption.setDisplayName("All team members");
			personList.add(firstOption);
			
			if(persons != null && persons.size() > 0){			
				personList.addAll(persons);
			}
		} catch (Exception e) {
			log.error("Error occured while getting team members : "+e.getMessage());
		}
		
		setMoreLinkHrefValue(SessionManager.getJSPRootURL() + "/blog/view/teamMore?module=" + viewBlog.getModuleId());
		teamBeans = new GenericSelectModel<PnPerson>(personList, PnPerson.class, "displayName", "personId", access);
	}

	/**
	 * Method for getting dropdown list values for Team members from database
	 * 
	 * @return GenericSelectModel<BlogTeam>
	 */
	public GenericSelectModel<PnPerson> getTeamModel() {
		return teamBeans;
	}

	/**
	 * @return the isMore
	 */
	public boolean getIsMore() {
		return isMore;
	}

	/**
	 * @param isMore
	 *            the isMore to set
	 */
	public void setIsMore(boolean isMore) {
		this.isMore = isMore;
	}

	/**
	 * @return the linkDisplayName
	 */
	public String getLinkDisplayName() {
		return linkDisplayName;
	}

	/**
	 * @param linkDisplayName
	 *            the linkDisplayName to set
	 */
	public void setLinkDisplayName(String linkDisplayName) {
		this.linkDisplayName = linkDisplayName;
	}

	/**
	 * @return the linkHrefValue
	 */
	public String getLinkHrefValue() {
		return linkHrefValue;
	}

	/**
	 * @param linkHrefValue
	 *            the linkHrefValue to set
	 */
	public void setLinkHrefValue(String linkHrefValue) {
		this.linkHrefValue = linkHrefValue;
	}

	/**
	 * @return the moreLinkHrefValue
	 */
	public String getMoreLinkHrefValue() {
		return moreLinkHrefValue;
	}

	/**
	 * @param moreLinkHrefValue
	 *            the moreLinkHrefValue to set
	 */
	public void setMoreLinkHrefValue(String moreLinkHrefValue) {
		this.moreLinkHrefValue = moreLinkHrefValue;
	}

	/**
	 * @return the personList
	 */
	public List<PnPerson> getPersonList() {
		return personList;
	}

	/**
	 * @param personList
	 *            the personList to set
	 */
	public void setPersonList(List<PnPerson> personList) {
		this.personList = personList;
	}

	/**
	 * @return the teamLink
	 */
	public List<BlogTeam> getTeamLink() {
		return teamLink;
	}

	/**
	 * @param teamLink
	 *            the teamLink to set
	 */
	public void setTeamLink(List<BlogTeam> teamLink) {
		this.teamLink = teamLink;
	}

	/**
	 * @return the person
	 */
	public PnPerson getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(PnPerson person) {
		this.person = person;
	}

}
