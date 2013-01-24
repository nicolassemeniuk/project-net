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
    info="Process Modify Contaiiner"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.*,
    net.project.base.Module"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
	String id = securityProvider.getCheckedObjectID();
    
    // security check done in docmanager
    Container containerBean = new Container();
    pageContext.setAttribute("container", containerBean, PageContext.PAGE_SCOPE);
    String tmp = null;

	containerBean.setID ( docManager.getCurrentObjectID() );
    containerBean.setContainerID ( docManager.getCurrentContainerID() );
    containerBean.setUser ( docManager.getUser() );

    containerBean.loadProperties();

%>
<jsp:useBean id="container" type="net.project.document.Container" scope="page" />

<%-- Get the form fields --%>
	<jsp:setProperty name="container" property="*" />
	
	<%
		// bfd - 2994 issue
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			container.setDescription("");
		}
	%>

<!-- Avinash: -------------------------------------------------->
<%
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			container.setDescription("");
		}
%>
<!-- Avinash: -------------------------------------------------->

<%-- Store to the database --%>
<% 
	try {
		docManager.modifyObject (container); 
	} catch (net.project.base.UniqueNameConstraintException e) {
		request.setAttribute("error.unique", "1");
		pageContext.forward ("/document/ModifyContainer.jsp");
	}
	//Avinash:------------------------------------------------
	  request.setAttribute("action",String.valueOf(Action.VIEW));	
	  ServletSecurityProvider.setAndCheckValues(request);
	  String forwardingPage = (String)docManager.getNavigator().get("TopContainer");
	  if(forwardingPage.contains("/documents/Details")){
		  response.sendRedirect(SessionManager.getJSPRootURL() +  forwardingPage);
	  } else {
		  request.getRequestDispatcher(forwardingPage).forward(request,response);
	  }
	//Avinash:-----------------------------------------------  
      //response.sendRedirect((String)docManager.getNavigator().get("TopContainer"));
    %>




