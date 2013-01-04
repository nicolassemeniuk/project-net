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
    info="Create Domain Processing"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.domain.UserDomain,
            net.project.security.SessionManager"
 %>

<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>


<%-- Set the domain properties from the request object --%>


<%------------------------------------------------------------------------
  -- Store the new domain
  ----------------------------------------------------------------------%>

<%
	String theAction = request.getParameter("theAction");
	
	if(theAction != null && theAction.equals("modify")){
		pageContext.forward("DomainEdit.jsp");
	} else 
%>


<%------------------------------------------------------------------------
  -- Now take the user back where they belong
  ----------------------------------------------------------------------%>

<%
        response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/domain/Main.jsp");
%>


