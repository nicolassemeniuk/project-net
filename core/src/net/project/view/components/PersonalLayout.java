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

import net.project.base.Module;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * 
 */
public class PersonalLayout {

	private static Logger log = Logger.getLogger(PersonalLayout.class);
	
	@Property
	private String jSPRootURL;
	
	@Property 
	private String versionNumber; 

	@Property
	private String spaceType;
	
	@Inject
	private Request request;
	
	@Property
	private String module;
	
	@Property
	private String userId;
	
	@Property
	private String currentSpaceId;
	
	public PersonalLayout() {
	}
	
	@SetupRender
	void setValues() {
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		module = request.getParameter("module");
		userId = SessionManager.getUser().getID();
		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID(); 
		//spaceType = SessionManager.getUser().getCurrentSpace().getType();
		if (module != null && !module.equalsIgnoreCase("null")){
			if(module.equals(""+Module.PERSONAL_SPACE)) {
				spaceType = Space.PERSONAL_SPACE;
			} else if(module.equals(""+Module.PROJECT_SPACE)) {
				spaceType = Space.PROJECT_SPACE;
			}
		}
	}

}
