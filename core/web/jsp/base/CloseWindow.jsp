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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| html Page which closes the window it is loaded in
| Assuming it executes quickly enough it should not be seen
| Usage:  From a processing page:
|
|    pageContext.forward("/base/CloseWindow.jsp");
|
|    pageContext.forward("/base/CloseWindow.jsp?openerCallback=reset()");
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Close Window" 
    language="java" 
    errorPage="/errors.jsp"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
	// Get any callback function to execute
	String openerCallback = request.getParameter("openerCallback");
%>
	

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title>Closing Window...</title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript1.2">
	var wOpener = self.opener;
	
function finish () {

    <%  if (openerCallback != null && openerCallback.length() > 0) { %>
	    eval('wOpener.<%=openerCallback%>');
    <% } %>
    
	// I'm outta here
	self.close();
}
</script>
</head>
<body class="main" onLoad="finish();">
<div align="center" class="fieldContent">
	<%=PropertyProvider.get("prm.project.base.closing.message") %><br />
	<%=PropertyProvider.get("prm.project.base.message") %>
	</div>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
