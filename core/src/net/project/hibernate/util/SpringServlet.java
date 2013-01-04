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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import net.project.events.async.ActivitySubscriber;
import net.project.hibernate.LoadHibernate;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.ServiceFactoryImpl;
import net.project.security.SecurityInstances;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class SpringServlet extends HttpServlet {

	private Logger log = Logger.getLogger(SpringServlet.class);

	@Override
	public void init() throws ServletException {
		try {
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization started ");
			}
			ServiceFactory.init(ServiceFactoryImpl.class, getServletContext());
			if (log.isDebugEnabled()) {
				log.debug(" Spring bussines context initialization finished ");
			}
			// initialize Hibernate mappings
			LoadHibernate loadHibernate = new LoadHibernate();
			loadHibernate.load();
			// initialize security instances
			//Class.forName("net.project.security.SecurityInstances");
			SecurityInstances.testPolicy();			
			
			// Start subscriber after spring intitialization as one time activity
			ActivitySubscriber subscriber = ServiceFactory.getInstance().getActivitySubscriber();
			subscriber.start();
			
			 // Wait a little to drain any left over messages.
	        Thread.sleep(1000);
	        subscriber.flushMessages();
			
		} catch (Exception e) {
			log.error(" Spring bussines context initialization failed " + e.getMessage());
		}
	}

}
