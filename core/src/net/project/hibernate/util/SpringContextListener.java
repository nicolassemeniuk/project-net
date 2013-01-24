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
package net.project.hibernate.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.ServiceFactoryImpl;

import org.apache.log4j.Logger;


/**
 * Servlet implementation class for Servlet: SpringContextListener
 * 
 */
public class SpringContextListener implements ServletContextListener {

	private Logger log = Logger.getLogger(SpringContextListener.class);

	public SpringContextListener() {
		super();
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization started ");
			}
			//ServiceFactory.init(ServiceFactoryImpl.class);
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization finished ");
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization failed " + e.getMessage());
			}
		}

	}

}