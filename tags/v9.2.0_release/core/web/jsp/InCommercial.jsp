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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 10717 $
|       $Date: 2003-03-07 19:44:11 +0200 (??, 07 ??? 2003) $
|
|--------------------------------------------------------------------%>
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Bad Browser... sit."
    language="java"
    errorPage="/errors.jsp"
	import="net.project.security.*,net.project.base.property.PropertyProvider"
 %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%!
	String userAgent;
%> 

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />


<title>Option is not available</title>
</head>
<body class="main"  id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	
<div id='content'>

<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98%>option IS NOT AVAILABLE</td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
			<h3><%=PropertyProvider.get("prm.project.main.available.commercial.release")%></h3>
			<br>
			See <a href="http://www.project.net">http://www.project.net</a>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink">Return</a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
