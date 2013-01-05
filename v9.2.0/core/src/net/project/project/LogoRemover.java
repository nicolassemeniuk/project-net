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
package net.project.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

// bfd-3259 resolved by vmalykhin

public class LogoRemover {

	public void removeLogo() {
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest httpServletRequest = webContext.getHttpServletRequest();
		HttpSession session = httpServletRequest.getSession();
		ProjectSpaceBean projectSpace = (ProjectSpaceBean) session.getAttribute("projectSpace");
		try {
			if (projectSpace.getProjectLogoID() != null) {
				projectSpace.removeLogo();
				projectSpace.setProjectLogoID(null);
				session.setAttribute("projectSpace", projectSpace);
			}
		} catch (PersistenceException e) {
			throw new RuntimeException(e);
		}
	}

	public void removePersonalImage(int personId) {
		try {
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			PnPerson person = personService.getPerson(Integer.valueOf(personId));
			person.setImageId(null);
			personService.updatePerson(person);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
