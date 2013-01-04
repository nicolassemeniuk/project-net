<%--
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
--%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="ActivateEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.form.*,
			java.util.*,
			net.project.util.StringUtils" 
%>

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />

<%
	// remove old error message.
	session.removeValue("formError");
	
	// Toolbar: Submit button
	if (request.getParameter("theAction").equals("submit"))
	{
		if (request.getParameter("formStatus").equals("in_use"))
		{
			if ((formDesigner.getLists() == null) || (formDesigner.getLists().size() < 1))
			{
				session.putValue("formError", PropertyProvider.get("prm.form.designer.activateedit.mustdefinelist.message"));
				pageContext.forward("ActivateEdit.jsp");
			} else {
				List<FormField> fields = formDesigner.getFields();
				List<FormList> lists = formDesigner.getLists();
				boolean desingFieldExist = false;
				String listName = "";
				int index = 0;
				
				// For validating user defined fields.
				boolean isValidFieldSelected = false;
				for(FormList list : lists){
					Iterator fieldIterator = list.getFields().iterator();
					FormField field = null;
				    while (fieldIterator.hasNext()) {
				     field = (FormField) fieldIterator.next();
				     if(field.getElementLabel() != null){
				    	  isValidFieldSelected = true;
				    	  break;
				      } else {
				    	  isValidFieldSelected = false;
				      }
				    }
				    if(!isValidFieldSelected){
				    	listName += list.getName();
				    	if(index < lists.size() - 1){
				    		listName +=  ", ";
				    	}
				    }
				    index++;
				}
				if(!isValidFieldSelected){
					formDesigner.setStatus(Form.PENDING);
					session.putValue("formError", PropertyProvider.get("prm.form.designer.activateedit.listmusthaveuserdefinefields.message", listName));
					pageContext.forward("ActivateEdit.jsp");		
				} else{
					formDesigner.setStatus(Form.ACTIVE);
				}
			}
		}
		else if (request.getParameter("formStatus").equals("read_only"))
			formDesigner.setStatus(Form.READ_ONLY);
		else if (request.getParameter("formStatus").equals("pending"))
			formDesigner.setStatus(Form.PENDING);
	}
	
    //If the user is activating the form, reload the left menu just in case
    //they changed the "show in tools menu" option
    session.putValue("reloadMenu", "true");
    
	pageContext.forward("Main.jsp?module="+ net.project.base.Module.FORM  +"&action=" + net.project.security.Action.MODIFY);
%>
