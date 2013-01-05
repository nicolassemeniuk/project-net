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
package net.project.view.pages.assignment;

import java.util.Date;
import java.util.List;

import net.project.base.Module;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.schedule.TaskType;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.view.pages.blog.ViewBlog;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;

public class AssignmentPage {

	private static Logger log;

	@Persist
	private String jspRootURL;

	@Persist
	private Integer spaceId;

	@Persist
	private Integer userId;

	@Persist
	private Integer moduleId;

	@Persist
	private String milestoneText;

	@Persist
	private String spaceType;

	@Persist
	private String blogTabTitle;

	@Persist
	private String wikiTabTitle;

	@Persist
	private String linksTabTitle;

	private List<PnWeblogEntry> blogEntries;

	public AssignmentPage() {
		try {
			log = Logger.getLogger(ViewBlog.class);
			jspRootURL = SessionManager.getJSPRootURL();
			milestoneText = TaskType.MILESTONE.getName();
			blogTabTitle = "Blog";
			wikiTabTitle = "Wiki";
			linksTabTitle = "Links";
			ServiceFactory factory = ServiceFactory.getInstance();
			IBlogProvider blogProvider = factory.getBlogProvider();
			blogEntries = blogProvider.getWeblogEntriesFromProjectBlogByPerson(
					new Integer(net.project.security.SessionManager.getUser()
							.getID()), null, new Date(), new Date(),
					WeblogConstants.STATUS_PUBLISHED, 0, 0);
		} catch (Exception e) {
			log.error("Error occured while getting property tokens : "
					+ e.getMessage());
		}
	}

	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
	}

	public List<PnWeblogEntry> getBlogEntries() {
		return blogEntries;
	}

	public void setBlogEntries(List<PnWeblogEntry> blogEntries) {
		this.blogEntries = blogEntries;
	}

	public String getBlogTabTitle() {
		return blogTabTitle;
	}

	public void setBlogTabTitle(String blogTabTitle) {
		this.blogTabTitle = blogTabTitle;
	}

	public String getWikiTabTitle() {
		return wikiTabTitle;
	}

	public void setWikiTabTitle(String wikiTabTitle) {
		this.wikiTabTitle = wikiTabTitle;
	}

	public String getLinksTabTitle() {
		return linksTabTitle;
	}

	public void setLinksTabTitle(String linksTabTitle) {
		this.linksTabTitle = linksTabTitle;
	}

	public String getMilestoneText() {
		return milestoneText;
	}

	public void setMilestoneText(String milestoneText) {
		this.milestoneText = milestoneText;
	}

	Object onActivate(Object[] params) {
		if (params != null) {
			setUserId(Integer.parseInt(SessionManager.getUser().getID()));
			setSpaceId(Integer.parseInt(SessionManager.getUser()
					.getCurrentSpace().getID()));
			setSpaceType(SessionManager.getUser().getCurrentSpace()
					.getSpaceType().getName().toLowerCase());
			setSpaceType(getSpaceType().equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE
					: getSpaceType());
			setModuleId(Module.getModuleForSpaceType(getSpaceType()));
		}
		return null;
	}

	public String getJspRootURL() {
		return jspRootURL;
	}

	public void setJspRootURL(String jspRootURL) {
		this.jspRootURL = jspRootURL;
	}

	public Integer getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Integer spaceId) {
		this.spaceId = spaceId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}

}
