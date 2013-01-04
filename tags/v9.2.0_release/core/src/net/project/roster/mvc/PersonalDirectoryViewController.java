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
package net.project.roster.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.resource.Person;
import net.project.resource.RosterBean;
import net.project.security.User;
import net.project.security.group.GroupCollection;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PersonalDirectoryViewController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map model = new HashMap();
		try {
			String id = request.getParameter("memberid");
			String referrer = request.getParameter("referrer");
			// model.put("module", String.valueOf(Module.DIRECTORY));
			// model.put("action", String.valueOf(Action.MODIFY));
			request.setAttribute("memberid", id);
			request.setAttribute("referrer", referrer);
			User user = (User) request.getSession().getAttribute("user");
			RosterBean roster = new RosterBean();
			roster.setSpace(user.getCurrentSpace());
			roster.load();
			Person rosterPerson = roster.getPerson(id);
			request.setAttribute("rosterPerson", rosterPerson);
			GroupCollection memberOfRoles = new GroupCollection();
			memberOfRoles.setSpace(user.getCurrentSpace());
			memberOfRoles.loadAllIncludeIndirect(rosterPerson.getID());
			memberOfRoles.updateWithOwningSpace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/tile_view_personal_profile", model);
	}

}
