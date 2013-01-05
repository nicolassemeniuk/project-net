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
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.document.DocumentManagerBean;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.PnPersonServiceImpl;
import net.project.project.LogoUpload;
import net.project.project.LogoUploadCommand;
import net.project.project.ProjectSpaceBean;
import net.project.resource.Person;
import net.project.resource.RosterBean;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.GroupCollection;
import net.project.util.FileUtils;

public class PersonalImageUpload extends SimpleFormController {

	protected void verifySecurity(int module, int action) {
		AccessVerifier.verifyAccess(module, action);
	}

	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException bindException) throws Exception {

		// verifySecurity(Module.DIRECTORY, Action.MODIFY);
		Map model = new HashMap();
		try {
			LogoUploadCommand luCommand = (LogoUploadCommand) command;

			String imageId = uploadLogoImage(luCommand.getFile(), request.getSession());

			model.put("module", String.valueOf(Module.DIRECTORY));
			model.put("action", String.valueOf(Action.MODIFY));
			String id = request.getParameter("memberid");
			String referrer = request.getParameter("referrer");

			request.setAttribute("selected", id);
			request.setAttribute("id", id == null ? request.getParameter("id") : id);
			request.setAttribute("referrer", referrer);

			User user = (User) request.getSession().getAttribute("user");

			RosterBean roster = new RosterBean();
			roster.setSpace(user.getCurrentSpace());
			roster.load();
			Person rosterPerson = roster.getPerson(user.getID());			
			request.setAttribute("rosterPerson", rosterPerson);
			GroupCollection memberOfRoles = new GroupCollection();
			memberOfRoles.setSpace(user.getCurrentSpace());
			memberOfRoles.loadAllIncludeIndirect(rosterPerson.getID());
			memberOfRoles.updateWithOwningSpace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/tiles_logo_upload", model);

	}

	protected String uploadLogoImage(MultipartFile file, HttpSession session) throws PnetException {
		String imageId = null;
		try {
			DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
			User user = (User) session.getAttribute("user");
			docManager.setUser(user);
			String tempFilePath = FileUtils.commitUploadedFileToFileSystem(file);
			imageId = docManager.addFileToSpace(file.getSize(), file.getOriginalFilename(), tempFilePath, file.getContentType(), user.getCurrentSpace().getID());
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			PnPerson person = personService.getPerson(Integer.valueOf(user.getID()));
			person.setImageId(Integer.valueOf(imageId));
			personService.updatePerson(person);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageId;
	}

}
