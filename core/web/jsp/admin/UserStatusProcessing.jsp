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
    info="User List Processing.  Emits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.AuthorizationFailedException,
            net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.security.SessionManager,
	    net.project.base.DefaultDirectory"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
     DefaultDirectory.changePersonStatus ( request.getParameter("personID"), request.getParameter("userStatus") );
	 
	 String orgLink = (String) session.getAttribute("orgLink");
%>

<html>
<head>
<title>Close Window</title>
<script language="javascript">

	var targetWindow;

	function setup() {
	  
		JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

	    if (self.opener.name != null && self.opener.name == "main")
		targetWindow = self.opener;
	    else
		targetWindow = parent.opener;
		
		<% 
			if(orgLink != null && !orgLink.trim().equals("")) {
		%>
			theLocation = JSPRootURL + '<%= orgLink %>'
		<%
			 } else {
		%>	
        	theLocation = JSPRootURL + '/admin/UserList.jsp?module=240';
		<%
			}
		%>


		 targetWindow.location = theLocation;
		targetWindow.focus();
	    parent.close();
	}
</script>
</head>
<body onLoad="setup()">


</body>
</html>


