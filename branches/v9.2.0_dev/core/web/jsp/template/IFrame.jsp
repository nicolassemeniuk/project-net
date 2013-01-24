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
| IFrame layout template - contains no navigation decoration
| Supports sections:
|       title		- title of the page, placed inside html <title></title> tags
|		head		- Placed inside html <head></head> tags
|       content     - Placed inside html <body><template:getSpaceJS />
</body> tags
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="IFrame Layout Template"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager, 
			net.project.security.User,
			net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

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

<script language="javascript">
    setVar("JSPRootURL", "<%=SessionManager.getJSPRootURL()%>");

// Execute the setup() function if it is defined in the html page	
function trySetup() {
	if (window.setup) {
		window.setup();
	} else {
		// Not defined, do nothing
	}
	applyColorToEvenRows();
}
</script>
<%-- End of Default Script stuff --%>

<title><template:get name="title" /></title>
<template:get name="head" />
</head>

<%------------------------------------------------------------------------------
	<body> Section
------------------------------------------------------------------------------%>
<body class="iframeBody" onload="trySetup();">
<%--
	While the content need not be in a table, placing it in a table now will ensure
	that no table-incompatible html tags are used, thus allowing us to
	perform more complex layout later
--%>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<!-- Content -->
		<td align="left" valign="top">
			<template:get name="content" />
		</td>
		<!-- End of Content -->
	</tr>
</table>
<template:getSpaceJS />
</body>

</html>

