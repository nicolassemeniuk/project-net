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
package net.project.view.pages.project;

import java.io.Serializable;

import net.project.base.Module;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.resource.PersonStatus;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateFormat;

public class TeammateWrapper implements Serializable {

	private Teammate teammate;

	private PnWeblog weblog;
	
	private PnWeblogEntry weblogEntry;
	
	private IUtilService service;
	
	private DateFormat userDateFormatter;
	
	public TeammateWrapper(Teammate teammate) {
		this.teammate = teammate;
	}

	public TeammateWrapper(Teammate teammate, PnWeblog weblog) {
		this.teammate = teammate;
		this.weblog = weblog;
	}

	/** 
	 * Constructor for getting teammate list, Last blog entry date
	 * 
	 * @param teammate
	 * @param weblog
	 * @param weblogEntry
	 */
	public TeammateWrapper(Teammate teammate, PnWeblogEntry weblogEntry){
		this.teammate = teammate;
		this.weblogEntry = weblogEntry;
	}
	
	/** truncate the name of teammate
	 * @return name
	 */
	public String getName() {
		String name = "";
		if (teammate.getFirstName() != null) {
			if (teammate.getFirstName().length() > 15) {
				name = teammate.getFirstName().substring(0, 11).concat("...");
			} else {
				name = teammate.getFirstName();
			}
		}
		
		name += " " + teammate.getLastName().substring(0, 1) + ".";
		
		return name;
	}
	
	public String getTeammateName() {
		return teammate.getFirstName() + " " + teammate.getLastName();
	}
	
	public String getPersonId() {
		return teammate.getPersonId().toString();
	}

	public int getAssignedTasks() {
		return teammate.getAssignments().size();
	}

	public boolean getOnline() {
		return teammate.isOnline();
	}

	public boolean getOverAssigned() {
		return teammate.isOverassigned();
	}

	public String getUrl() {
		return "/roster/PersonalDirectoryView.htm?module=" + Module.DIRECTORY
				+ "&memberid=" + this.teammate.getPersonId();
	}

	public PnWeblog getWeblog() {
		return weblog;
	}

	public void setWeblog(PnWeblog weblog) {
		this.weblog = weblog;
	}
	
	public String getBlogName() {
		return this.weblog.getName();
	}
	
	public Boolean getHasBlog() {
		return this.weblog != null;
	}
	
	public String getBlogUrl() {
		return "/blog/view/"
				+ Integer.parseInt(SessionManager.getUser().getCurrentSpace()
						.getID()) + "/" + teammate.getPersonId() + "/"+Space.PROJECT_SPACE+"/"
				+ Module.PROJECT_SPACE + "?module=" + Module.PROJECT_SPACE
				+ "&teamMemberId=" + teammate.getPersonId();
	}

	public PnWeblogEntry getWeblogEntry() {
		return weblogEntry;
	}

	public void setWeblogEntry(PnWeblogEntry weblogEntry) {
		this.weblogEntry = weblogEntry;
	}
	
	/** 
	 * This getter get the last blog entry date	 * 
	 * @return String
	 */
	public String getLastEntryDate(){
		service = ServiceFactory.getInstance().getUtilService();
		userDateFormatter = SessionManager.getUser().getDateFormatter();
		
		if(this.weblogEntry!=null){
			return DateFormat.getInstance().formatDateMedium(this.weblogEntry.getPubTime());	
		}
		
		return null;	
	}	
	
	public String getSkype(){
		return this.teammate.getSkype();
	}
	
	/**
	 * @return String
	 */
	public String getLastEntryTitle(){
		return null;
	}

	/**
	 * Getting Weblog Entry Id
	 * @return Integer
	 */
	public Integer getWebLogEntryId(){
		if (this.weblogEntry != null) {
			return this.weblogEntry.getWeblogEntryId();
		}
		return null;
	}
	
	/**
	 * If user is unregistered returns true else false
	 * @return boolean
	 */
	public boolean isRegistered() {
		return teammate.getUserStatus().equals(PersonStatus.UNREGISTERED.getName());
	}
}
