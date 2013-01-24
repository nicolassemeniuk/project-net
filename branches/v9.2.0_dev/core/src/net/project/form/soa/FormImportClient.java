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

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.form.FormLoadSaveHelper;
import net.project.security.User;

import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sun.istack.ByteArrayDataSource;

public class FormImportClient extends SimpleFormController{
	
	private IFormImportService formImportClient;
	
	
	public void setFormImportClient(IFormImportService formImportClient) {
		this.formImportClient = formImportClient;
	}


	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException bindException) throws Exception {
		
		FormImportClientCommand fileCommand = (FormImportClientCommand)command;
		MultipartFile uploadedFile = fileCommand.getFile();
		
		HttpSession session =  request.getSession();
		String userId = ((User)session.getAttribute("user")).getID();
		String spaceId = ((User)session.getAttribute("user")).getCurrentSpace().getID();
		
		try{
			DataSource source = new ByteArrayDataSource(uploadedFile.getBytes(), "application/octet-stream");
			FormLoadSaveHelper.loadFromDataHandler(new DataHandler(source), Integer.valueOf(userId), Integer.valueOf(spaceId));
		} catch (Exception e) {

        	final Map<String,String> model = new HashMap<String,String>();
            model.put("error.invalid_file", "invalid_file");

            return new ModelAndView("/tile_view_form_definition_upload", model);			
		}
		Map model = new HashMap();
		return new ModelAndView("/tile_view_form_designer_list", model);		
		
	}
	
}
