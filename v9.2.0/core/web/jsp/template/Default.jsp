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
| Default layout template
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Default Layout Template"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager, 
			net.project.security.User,
			net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%-- 
	Insert default content into template 
    This content is used if not specified by the JSP page that uses this
	template
--%>
<template:putDefault name="header" content="/toolbar/include/Main.jsp" /> 
<template:putDefault name="navBar" content="/toolbar/include/NavBar.jsp" />
<template:putDefault name="footer" content="/help/include_outside/footer.jsp" /> 
<%-- End of default stuff --%>



<%------------------------------------------------------------------------------
	<html> Section
------------------------------------------------------------------------------%>
<html>

<%------------------------------------------------------------------------------
	<head> Section
------------------------------------------------------------------------------%>
<head>

<%-- Default Stylesheet stuff --%>
<template:getSpaceCSS />

<%-- Default Script stuff --%>
<script language="javascript">
    setVar("JSPRootURL", "<%=SessionManager.getJSPRootURL()%>");

// Execute the setup() function if it is defined in the html page	
function trySetup() {
	if (window.setup) {
		window.setup();
	} else {
		// Not defined, do nothing
	}
}
</script>
<%-- End of Default Script stuff --%>

<title><template:get name="title" /></title>
<template:get name="head" />
</head>

<%------------------------------------------------------------------------------
	<body> Section
------------------------------------------------------------------------------%>
<body class="main" id='bodyWithFixedAreasSupport' onload="trySetup();">
<% if (true) { // change condition to true to simply display the content - this allows the template to be used within the standard frames layout.%>
	<template:get name="content" />
    <template:get name="footer" />
<% } else { %>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<!-- Header Bar -->
		<td colspan="4" align="left" valign="top" height="62">
			<template:get name="header" />
		</td>
		<!-- End of Header Bar -->
	</tr>
	<tr>
		<!-- Navigation Bar -->
		<td width="135" align="left" valign="top">
			<template:get name="navBar" />
		</td>
		<!-- End of Navigation Bar -->
		<td>&nbsp;</td>
		<!-- Content -->
		<td align="left" valign="top">
			<template:get name="content" />
		</td>
		<!-- End of Content -->
		<td>&nbsp;</td>
	</tr>
	<tr>
		<!-- Footer -->
		<td colspan="4" align="left" valign="bottom">
			<template:get name="footer" />
		</td>
		<!-- End of Footer -->
	</tr>
</table>
<% } %>
<template:getSpaceJS />
</body>

</html>

