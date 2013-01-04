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
package net.project.form.soa;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.form.FormLoadSaveHelper;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class FormExportClient implements Controller {

	private IFormExportService formExportClient;

	public void setFormExportClient(IFormExportService formExportClient) {
		this.formExportClient = formExportClient;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String formId = request.getParameter("formId");
		
		response.setContentType("application/octet-stream");
		// response.setContentType("application/xml");
		response.setHeader("Content-Disposition", "attachment; filename= \"form-"+formId+".xml\"");
		response.setCharacterEncoding("UTF-8");

		DataHandler handler = FormLoadSaveHelper.exportToDataHandler(Integer.valueOf(formId));
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			InputStream is = handler.getInputStream();
			FileCopyUtils.copy(is, out);
			is.close();
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// return new ModelAndView("/tile_view_form_definition", model);
		return null;
	}

}
