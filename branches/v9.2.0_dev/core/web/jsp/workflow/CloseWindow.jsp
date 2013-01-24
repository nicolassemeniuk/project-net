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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| html Page which closes the window it is loaded in
| Assuming it executes quickly enough it should not be seen
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Close Window" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.security.User,
			net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<html>
<head>
<title><%=PropertyProvider.get("prm.workflow.closewindowpage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<script language="javascript1.2">
var theAction = '<%=request.getParameter("theAction")%>';

function finish () {

	var wOpener = self.opener;
	
	// If this was a submit or wizard finish, then we must refresh the window who opened us
    if (theAction == 'submit') {
		if (wOpener) wOpener.reset();
	
	} else if (theAction =='finish') {
		if (wOpener) wOpener.reset();

	}

	// I'm outta here
	self.close();
}
</script>
</head>
<body class="main" onLoad="javascript:finish();">
<div align="center" class="fieldContent">
	<%=PropertyProvider.get("prm.workflow.closewindow.closing.message")%>
	</div>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
