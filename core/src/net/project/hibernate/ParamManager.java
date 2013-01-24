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
package net.project.hibernate;

import org.apache.log4j.Logger;

import net.project.security.SessionManager;

public class ParamManager {
	
	private Logger log = Logger.getLogger(ParamManager.class);
	
	/**
	 * needed for dynamic address creation 
	 * address can not be static data since application can be deployed as non-root
	 * or on different port  
	 * 
	 * @return web-services end-point address
	 */
	public String getAddress(){
		//String address = SessionManager.getSiteScheme() + SessionManager.getSiteHost() + SessionManager.getJSPRootURL();
		String address = "http://localhost:8080";
		return address;
	}	
}
