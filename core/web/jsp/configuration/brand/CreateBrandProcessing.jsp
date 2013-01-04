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
    info="Process Brand Create"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.brand.*,
	net.project.security.SessionManager"
 %>

<jsp:useBean id="brand" class="net.project.brand.Brand" /> 

<%-- Get the fileObject and save it to the storage directory --%>


<jsp:setProperty name="brand" property="*" />

<%
    String[] languages = request.getParameterValues("supportedLanguages");

	for (int i = 0; i < languages.length; i++)
	    brand.addLanguage (languages[i]);

	 brand.store();
	 response.sendRedirect (SessionManager.getJSPRootURL() + "/configuration/brand/BrandList.jsp");
%>


