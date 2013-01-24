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

import javax.activation.DataHandler;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import net.project.form.FormLoadSaveHelper;


@WebService(endpointInterface = "net.project.form.soa.IFormExportService", serviceName = "FormExportService")
public class FormExportServiceImpl implements IFormExportService {

	@WebResult(name = "FormExport")
	public FormExport getFormData(@WebParam(name = "getForm") GetForm getForm) {
		FormExport formExport = new FormExport();
		
		formExport.setFormId(getForm.getId());
	
		
		DataHandler handler = FormLoadSaveHelper.exportToDataHandler(getForm.getId()); 
		formExport.setFormExport(handler);
						
		return formExport;
	}	
}
