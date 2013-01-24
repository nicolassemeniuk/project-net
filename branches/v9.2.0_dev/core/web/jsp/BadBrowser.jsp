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
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Bad Browser... sit."
    language="java"
    errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider"
 %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%!
	String userAgent;
%>


<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />
<title>Bad Browser</title>
</head>
<body class="main">	

<div align="center">

<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98%>Browser Not Supported</td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">

<h2><%=PropertyProvider.get("prm.project.main.browser.not.supported.label") %></h2>

<p>
<%=PropertyProvider.get("prm.project.main.browser.only.supported.label") %>
<ul>
	<li><%=PropertyProvider.get("prm.project.main.netscape.navigator.6.or.higher.label") %>  &nbsp; <a href="http://home.netscape.com/browsers/index.html">www.netscape.com</a></li>
	<li><%=PropertyProvider.get("prm.project.main.expl.navigator.5.5.or.higher.label") %> &nbsp; <a href="http://www.microsoft.com/windows/ie/default.htm">www.microsoft.com</a></li>
</ul>
<a href="help/HelpDesk.jsp?page=browser_requirements"><%=PropertyProvider.get("prm.project.main.read.browser.requirements.label") %></a> <%=PropertyProvider.get("prm.project.main.read.browser.requirements.label.second.part") %>
</p>
<br />
<p>
<%=PropertyProvider.get("prm.project.main.current.browser.information.label") %>
<ul>
	<li><%=PropertyProvider.get("prm.project.main.agent.label") %><script>document.write(navigator.userAgent);</script></li>
	<li><%=PropertyProvider.get("prm.project.main.browser.name.label") %><script>document.write(navigator.appName);</script></li> 
	<li><%=PropertyProvider.get("prm.project.main.app.version.label") %><script>document.write(navigator.appVersion);</script></li>
</ul>

		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink"><%=PropertyProvider.get("prm.project.main.return.label") %></a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>

</div>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>

