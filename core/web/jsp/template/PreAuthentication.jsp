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
| Layout template used pre-authentication.
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Pre-authentication Layout Template"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager, 
			net.project.security.User,
			net.project.space.Space,
			net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%-- 
	Insert default content into template 
    This content is used if not specified by the JSP page that uses this
	template
--%>
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
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.login")%>' />

<title><template:get name="title" /></title>
<template:get name="head" />

<script language="javascript">
// Execute the setup() function if it is defined in the html page	
function trySetup() {
	if (window.setup) {
		return window.setup();
	} else {
		// Not defined, do nothing
        return true;
	}
}

function bodyOnLoad(functionsToExecute) {
    setVar("JSPRootURL", "<%=SessionManager.getJSPRootURL()%>");
    setVar("_sid", "", 1, "<%=SessionManager.getJSPRootURL()%>/");
    setVar("_styp", "", 1, "<%=SessionManager.getJSPRootURL()%>/");
    if (functionsToExecute) {
        return eval(functionsToExecute);
    } else {
        return trySetup();
    }
}
</script>
<%-- End of Default Script stuff --%>

</head>

<%------------------------------------------------------------------------------
	<body> Section
------------------------------------------------------------------------------%>
<body class="main" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 onload='bodyOnLoad("<template:get name='bodyOnLoad' />")'>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
        <td align="left" valign="top">
			<template:get name="content" />
		</td>
		<!-- End of Content -->
	</tr>
	<tr>
		<!-- Footer -->
		<td class="footer" align="left" valign="bottom">
			<template:get name="footer" />
		</td>
		<!-- End of Footer -->
	</tr>
</table>
</body>

</html>
