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

<%----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Edit Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.Module,
			net.project.brand.Brand" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="configurationSpace" class="net.project.configuration.ConfigurationSpace" scope="session" />

<%
	String action = request.getParameter("theAction");
	String mode = request.getParameter("mode");

	configurationSpace.setUser(user);
	
	Brand brandBean = configurationSpace.getBrand();
	pageContext.setAttribute("brand", brandBean, PageContext.PAGE_SCOPE);
	
%>
<jsp:useBean id="brand" type="net.project.brand.Brand" scope="page" />

<%	
	if (mode != null && mode.equals("create")) { %>
		<security:verifyAccess action="create" module="<%=Module.CONFIGURATION_SPACE%>" /> 
<%	} else { %>
		<security:verifyAccess action="modify" module="<%=Module.CONFIGURATION_SPACE%>" /> 
<%	} %>	

<%
	if (action != null && action.equals("submit")) {
%>
		<%-- Get the form fields --%>
		<jsp:setProperty name="configurationSpace" property="name" />
		<jsp:setProperty name="configurationSpace" property="description" />

		<jsp:setProperty name="brand" property="abbreviation" />
		<jsp:setProperty name="brand" property="defaultLanguage" />
		<jsp:setProperty name="brand" param="supportedLanguages" property="supportedLanguagesArray" />
		<jsp:setProperty name="brand" property="supportedHostnames" />
<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			configurationSpace.setDescription("");
		}

		if ((request.getParameter("supportedLanguages") == null) || (request.getParameter("supportedLanguages").equals(""))) {
			brandBean.setSupportedLanguagesArray(new String[0]);
		}
		
		configurationSpace.store();
		response.sendRedirect(SessionManager.getJSPRootURL() + "/configuration/Main.jsp?module=" + Module.CONFIGURATION_SPACE + "&id=" + configurationSpace.getID());

	} else {
		throw new net.project.base.PnetException("Unknown action '" + action + "' in ConfigurationEditProcessing.jsp");
	}
%>
