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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19103 $
|       $Date: 2009-04-19 06:37:34 -0300 (dom, 19 abr 2009) $
|     $Author: vivana $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form Designer -- Copy Form Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.FormManager, 
		    net.project.form.FormDesigner,
		    net.project.form.Form,
			net.project.security.SessionManager,
			net.project.base.property.PropertyProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess objectID='<%=request.getParameter("id")%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/> 
<%
	//
	// Load the form field for the specified ID
	// This is the form field that we will copy
	//
	
	String id = request.getParameter("id");
	String ownerId = request.getParameter("ownerId");
	if (id == null || id.length() == 0) {
		throw new net.project.base.PnetException(PropertyProvider.get("prm.project.form.designer.missing.parameter.label"));
	}
	String formId;
	if(id.equals(ownerId)){
		formId = new FormManager().copyForm(id, user.getCurrentSpace().getID(), user);
	}else{
		formId = new FormManager().copyForm(id, ownerId, user.getCurrentSpace().getID(), user);
	}
//	FormDesigner formDesigner = new FormDesigner(); 
//	formDesigner.setID(formId);
//	formDesigner.load();
//	formDesigner.setStatus(Form.PENDING);
	// Return to Main
	response.sendRedirect(SessionManager.getJSPRootURL() + "/form/designer/Main.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY);
%>
