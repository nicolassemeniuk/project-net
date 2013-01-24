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
    info="Process sorting for the container list" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
		    net.project.security.SessionManager"

%>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />

<%

        docManager.setContainerSortBy ( request.getParameter("sort") );

        String forwardingPage = (String)docManager.getNavigator().get("TopContainer");

		if ((forwardingPage != null) && ( !forwardingPage.equals("")))
			response.sendRedirect (SessionManager.getJSPRootURL()+forwardingPage);
		else
			pageContext.forward ("/document/Main.jsp");

%>


