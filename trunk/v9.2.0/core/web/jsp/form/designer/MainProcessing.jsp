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
    info="MainProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, net.project.form.*,
            net.project.base.property.PropertyProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />


<%
	// Toolbar: Remove
	if (request.getParameter("theAction").equals("remove"))
	{
		formDesigner.setID(request.getParameter("selected"));
		formDesigner.remove();
		formDesigner.setRecordStatus("D");
		
		//---Avibha:-----setting the previous action ------------
		request.setAttribute("action",""+Action.MODIFY);
		ServletSecurityProvider.setAndCheckValues(request);
		//-------------------------------------------------------
	
		pageContext.forward("Main.jsp?id=&action=" + net.project.security.Action.MODIFY);
	}
	
	// Toolbar: Create
	else if (request.getParameter("theAction").equals("create"))
	{
		formDesigner.clear();
		formDesigner.setID(null);
		formFieldDesigner.clear();
		formListDesigner.clear();
		
		formDesigner.setSpace(user.getCurrentSpace());
		formDesigner.setUser(user);
		
		// only "forms" supported now.  Checklists, etc. coming soon.
		//formDesigner.setClassTypeID(Form.FORM);
		formDesigner.setClassTypeID("100");
		
		// new forms have "Pending" status until applied. 
		formDesigner.setRecordStatus("P");
		formDesigner.setShared(false);
		
		pageContext.forward("DefinitionEdit.jsp");
	}
	
	//Toolbar: Modify
	else if (request.getParameter("theAction").equals("modify"))
	{
		if(formDesigner != null && formDesigner.getRecordStatus().equalsIgnoreCase("D")){
			// Exception thrown to prevent deleted form from editing.
			throw new RuntimeSecurityException(PropertyProvider.get("prm.form.deletedfrom.edit.error.message"));
		} else {
		   boolean owner = request.getParameter("owner") != null ? request.getParameter("owner").equals("true") : false; 
		   if (owner){
				pageContext.forward("DefinitionEdit.jsp");
		   }else{
	   		    pageContext.forward("SharedFormActivationEdit.jsp");
		   }
		}
	} else if (request.getParameter("theAction").equals("security")) {
		// Check security now
%>
		<security:verifyAccess objectID='<%=request.getParameter("selected")%>'
							   action="modify_permissions"
					   		   module="<%=net.project.base.Module.FORM%>"
		/> 
<%
		// Security JSP requires these attributes		
		request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
		request.setAttribute("action", Integer.toString(net.project.security.Action.MODIFY_PERMISSIONS));
		// Security JSP requires this session value
		session.putValue("objectID", request.getParameter("selected"));
		// Security JSP requires this request parameter
		
//---Avibha:-----setting the previous action ------------
		ServletSecurityProvider.setAndCheckValues(request);
//-------------------------------------------------------

		pageContext.forward("/security/SecurityMain.jsp?objectType=form");
	}
	
	// Toolbar: Copy
	else if (request.getParameter("theAction").equals("copy"))
	{
		pageContext.forward("FormCopyProcessing.jsp?id=" + request.getParameter("selected")+"&ownerId="+request.getParameter("selectedOwnerId"));
	}
%>
