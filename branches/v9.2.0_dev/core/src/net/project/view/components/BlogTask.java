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
package net.project.view.components;

import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IPnTaskService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.view.pages.blog.ViewBlog;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * @author
 */
public class BlogTask {

	private static Logger log;

	private boolean isMore;

	private String linkHrefValue;

	private String linkDisplayName;

	@Persist
	private String importantSymbolTooltip;

	private String moreLinkHrefValue;

	private List<BlogTask> taskLink;

	private List<PnTask> taskList;
	
	private PnTask pnTask;

	private List<PnWeblogEntry> userWeblogEntries;

	private PnWeblogEntry pnWeblogEntry;

	private PnWeblogComment pnWeblogComment;

	@Persist
	private String entryPostedByLabel;

	@Persist
	private boolean linkToPersonSpace;

	private Integer taskId;

	@Persist
	private String jspRootURL;

	@InjectPage
	private ViewBlog viewBlog;
	
	@Persist
	private Integer moduleId;
	
	@Inject
	private PropertyAccess access;
	
	private GenericSelectModel<PnTask> taskBeans;

	/**
	 * Setting up values before page render
	 */
	@SetupRender
	void setValues() {
		log = Logger.getLogger(BlogTeam.class);
		taskLink = new ArrayList<BlogTask>();
		taskList = new ArrayList<PnTask>();

		try {
			importantSymbolTooltip = PropertyProvider.get("prm.blog.viewblog.importantsymbol.tooltip");
			jspRootURL = SessionManager.getJSPRootURL();
			moduleId = viewBlog.getModuleId();
			IPnTaskService pnTaskService = ServiceFactory.getInstance().getPnTaskService();
			List<PnTask> tasks = pnTaskService.getTasksByProjectId(viewBlog.getSpaceId());
			
			PnTask firstOption = new PnTask();
			firstOption.setTaskId(0);			
			
			if(tasks != null && tasks.size() > 0){
				firstOption.setTaskName("All tasks");
				taskList.add(firstOption);
				taskList.addAll(tasks); 	
			} else {
				firstOption.setTaskName("No Tasks Found");
				taskList.add(firstOption);
			}
		} catch (Exception e) {
			log.error("Error occured while getting team members : " + e.getMessage());
		}

		setMoreLinkHrefValue(SessionManager.getJSPRootURL() + "/blog/view/teamMore?module=" + viewBlog.getModuleId());
		taskBeans = new GenericSelectModel<PnTask>(taskList, PnTask.class, "taskName", "taskId", access);
	}

	/**
	 * Method for getting dropdown list values for Tasks from database
	 * 
	 * @return GenericSelectModel<PnTask>
	 */
	public GenericSelectModel<PnTask> getTaskModel() {
		return taskBeans;
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
	public List<PnTask> getPersonList() {
		return taskList;
	}

	/**
	 * @param personList
	 *            the personList to set
	 */
	public void setPersonList(List<PnTask> personList) {
		this.taskList = personList;
	}

	/**
	 * @return the teamLink
	 */
	public List<BlogTask> getTeamLink() {
		return taskLink;
	}

	/**
	 * @param teamLink
	 *            the teamLink to set
	 */
	public void setTeamLink(List<BlogTask> teamLink) {
		this.taskLink = teamLink;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public List<PnWeblogEntry> getUserWeblogEntries() {
		return userWeblogEntries;
	}

	public PnWeblogEntry getPnWeblogEntry() {
		return pnWeblogEntry;
	}

	public String getImportantSymbolTooltip() {
		return importantSymbolTooltip;
	}

	public String getJspRootURL() {
		return jspRootURL;
	}

	public void setJspRootURL(String jspRootURL) {
		this.jspRootURL = jspRootURL;
	}

	public PnWeblogComment getPnWeblogComment() {
		return pnWeblogComment;
	}

	public void setPnWeblogComment(PnWeblogComment pnWeblogComment) {
		this.pnWeblogComment = pnWeblogComment;
	}

	public String getEntryPostedByLabel() {
		return entryPostedByLabel;
	}

	public boolean isLinkToPersonSpace() {
		return linkToPersonSpace;
	}

	public void setLinkToPersonSpace(boolean linkToPersonSpace) {
		this.linkToPersonSpace = linkToPersonSpace;
	}

	public void setPnWeblogEntry(PnWeblogEntry pnWeblogEntry) {
		this.pnWeblogEntry = pnWeblogEntry;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the pnTask
	 */
	public PnTask getPnTask() {
		return pnTask;
	}

	/**
	 * @param pnTask the pnTask to set
	 */
	public void setPnTask(PnTask pnTask) {
		this.pnTask = pnTask;
	}

}
