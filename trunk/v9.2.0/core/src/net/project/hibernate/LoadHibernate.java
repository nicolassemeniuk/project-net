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

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.project.MetaData;

import org.apache.log4j.Logger;

public class LoadHibernate {

	private Logger log = Logger.getLogger(LoadHibernate.class);

	public LoadHibernate() {
	}

	public void load() {
		try {
			// executes simple query durring server startup and Hibernate
			// mapping files are loaded
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			PnPerson person = personService.getPerson(1);
			MetaData.initializeMetaData();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

}
