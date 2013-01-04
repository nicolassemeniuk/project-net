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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.ServiceFactory;
import net.project.roster.model.MemberModel;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

public class AddMemberWizardController extends AbstractWizardFormController {

	private MemberModel model;

	public AddMemberWizardController() {
		setCommandClass(MemberModel.class);
		setCommandName("command");
	}

	public AddMemberWizardController(MemberModel model) {
		this.model = model;
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object object, BindException bindException) throws Exception {
		return new ModelAndView();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) {
		try {
			if (!isFormSubmission(request)) {
				return new MemberModel();
			} else {
				return super.formBackingObject(request);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void validatePage(Object object, Errors errors, int page) {
		super.validatePage(object, errors, page);
	}

	protected void onBind(HttpServletRequest request, Object command, BindException errors) {
		MemberModel model = (MemberModel) command;
	}

	protected Object getModelObject(HttpServletRequest request) throws Exception {
		// If not in session-form mode or form is shown for the first time,
		// create a new form-backing object.
		if (!isSessionForm() || !isFormSubmission(request)) {
			return formBackingObject(request);
		}
		String formAttrName = getFormSessionAttributeName(request);
		Object sessionFormObject = request.getSession().getAttribute(formAttrName);
		if (sessionFormObject == null) {
			throw new Exception("Form object not found in session (in session-form mode)");
		}

		// Check the command object to make sure its valid
		if (!checkCommand(sessionFormObject)) {
			throw new Exception("Object found in session does not match commandClass");
		}
		return sessionFormObject;

	}

	@Override
	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
//		MemberModel model = (MemberModel) command;
//		Enumeration e = request.getParameterNames();
//		while (e.hasMoreElements()) {
//			Object o = e.nextElement();
//			System.out.println(o + ": " + request.getParameter(o.toString()));
//		}
		if (currentPage == 0 && (request.getParameter("_target1") != null)) {
			return 0;
		} else {
			return currentPage + 1;
		}
		
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, int page) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		MemberModel mm = (MemberModel) getModelObject(request);
//		Enumeration e = request.getParameterNames();
//		while (e.hasMoreElements()) {
//			Object o = e.nextElement();
//			System.out.println(o + ": " + request.getParameter(o.toString()));
//		}
//		System.out.println("\n\n\niz sesije:\n");
//		Enumeration es = request.getSession().getAttributeNames();
//		while (es.hasMoreElements()) {
//			Object o = es.nextElement();
//			System.out.println(o + ": " + request.getSession().getAttribute(o.toString()));
//		}
//		System.out.println("\n\nsesija:" + mm.toString() + "\n\n");
		switch (page) {
		case 0:
			if(request.getParameter("_target1") != null){
				List<PnPerson> list = ServiceFactory.getInstance().getPnPersonService().getPersonByName(mm.getFirstName(), mm.getLastName(), mm.getEmail());
				System.out.println("\n\n\n\n\n\nlist:"+list.size()+"\n\n\n\n\n\n");
				request.setAttribute("model", list);				
			}
			
			break;
		case 1:

			break;
		case 2:

		default:
		}

		return data;
	}

	public void setModel(MemberModel model) {
		this.model = model;
	}

}
