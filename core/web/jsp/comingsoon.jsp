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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
       info="Coming Soon" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.SessionManager,
				net.project.base.property.PropertyProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<script language="javascript" type="text/javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
}
</script>

</head>
<body onLoad="setup();" class="main">
<form>
    <input type="hidden" name="theAction">
</form>
<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><img src="/images/error/comingsoon-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98%><%=PropertyProvider.get("prm.project.main.comming.soon.label") %></td>
		<td width=1%><img src="/images/error/comingsoon-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
<h2><%=PropertyProvider.get("prm.project.main.comming.soon.message") %></h2>
	</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="/images/error/comingsoon-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink"><%=PropertyProvider.get("prm.project.main.return.label") %></a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="/images/error/comingsoon-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>                                                                                      