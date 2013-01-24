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
    info="Navigation Frameset"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.*,
    net.project.base.*,
    net.project.resource.PersonProperty,
    net.project.resource.PersonPropertyGlobalScope,
    net.project.space.ISpaceTypes"
%>
<%--
<%@ include file="/base/taglibInclude.jsp" %>
--%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	String requestedPage = (String) session.getAttribute("requestedPage");
	if (requestedPage != null) {
		session.removeAttribute("requestedPage");
		response.sendRedirect(requestedPage);
		return;
	}
	PersonProperty property = new PersonProperty();
	property.setScope(new PersonPropertyGlobalScope(user));
	String[] properties = property.get("prm.global.login", "startPage");
	session.setAttribute("prm.global.login", SessionManager.getJSPRootURL() + "/personal/Main.jsp?module=" + Module.PERSONAL_SPACE);
	if (properties.length != 0) {
		if (properties[0].equals(ISpaceTypes.BUSINESS_SPACE)) {
			response.sendRedirect(SessionManager.getJSPRootURL() + "/business/BusinessPortfolio.jsp?module=" + Module.BUSINESS_SPACE + "&portfolio=true");
			return;
		} else if (properties[0].equals(ISpaceTypes.PROJECT_SPACE)) {
			response.sendRedirect(SessionManager.getJSPRootURL() + "/portfolio/Project?module=" + Module.PROJECT_SPACE + "&portfolio=true");
			return;
		} else if (properties[0].equals(ISpaceTypes.PERSONAL_SPACE)) {
			response.sendRedirect(SessionManager.getJSPRootURL() + "/personal/Main.jsp?module=" + Module.PERSONAL_SPACE);
			return;
		} 
	}
	session.setAttribute("prm.global.login", SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+Module.PERSONAL_SPACE+"&page=" + SessionManager.getJSPRootURL() + "/assignments/My?module=" + Module.PERSONAL_SPACE);
	response.sendRedirect(SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+Module.PERSONAL_SPACE+"&page=" + SessionManager.getJSPRootURL() + "/assignments/My?module=" + Module.PERSONAL_SPACE);
%>
<%--
<html>
<head>

--%>
<%-- Import Javascript --%>
<%--
<template:import type="javascript" src="/src/util.js" />
<script language="javascript">
    setVar("JSPRootURL", "<%=SessionManager.getJSPRootURL()%>");
    setVar('reloadHeaderFrameOnSpaceChange', '<%=Conversion.booleanToString(PropertyProvider.getBoolean("prm.global.header.reloadonspacechange"))%>');
    setVar ('headerCurrentSpace', 'personal');
</script>

<title><display:get name="prm.global.application.title" /></title>
</head>

<script language="javascript">
<!--
if (navigator.appName.indexOf("Microsoft") != -1) {
 document.write('<frameset rows="<%=PropertyProvider.get("prm.global.frameset.rows.ie")%>,*" ')
}else{
 document.write('<frameset rows="<%=PropertyProvider.get("prm.global.frameset.rows.netscape")%>,*" ')
}
//-->
</script> border="0" framespacing="0" frameborder="0" name="top">
	<frame src="<%=SessionManager.getJSPRootURL()%>/toolbar/Main.jsp" name="toolbar_main" frameborder="0" scrolling="No" noresize marginwidth="0" marginheight="0" framespacing="0" border="0">
	<frameset cols="<%=PropertyProvider.get("prm.global.frameset.cols")%>,*" border="0" framespacing="0" frameborder="0" name="frameset_bottom">
		<frame src="<%=SessionManager.getJSPRootURL()%>/personal/NavBar.jsp" name="menu" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<% 
	// if the user was directed through the login screen, 
	// go to the page they origonally requested.
    String thePage = (String)session.getAttribute("requestedPage");
	if ((thePage != null) && (!thePage.endsWith("NavigationFrameset.jsp"))){
%>
		<frame src="<%= thePage %>&inframe=1" name="main" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<% 
		session.removeAttribute("requestedPage");
	} else{
		PersonProperty property = new PersonProperty();
		property.setScope(new PersonPropertyGlobalScope(user));
		String[] properties = property.get("prm.global.login", "startPage");
		if(properties.length == 0){
			// The user hasn't settled yet the starting page property
%>
			<frame src="<%=SessionManager.getJSPRootURL()%>/personal/Main.jsp?module=<%=Module.PERSONAL_SPACE%>" name="main" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<%
		} else{
			if(properties[0].equals(ISpaceTypes.BUSINESS_SPACE)){
%>
			<frame src="<%=SessionManager.getJSPRootURL()%>/business/BusinessPortfolio.jsp?module=<%=Module.BUSINESS_SPACE%>" name="main" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<%
			} else{
				if(properties[0].equals(ISpaceTypes.PROJECT_SPACE)){
%>
				<frame src="<%=SessionManager.getJSPRootURL()%>/portfolio/PersonalPortfolio.jsp?module=<%=Module.PROJECT_SPACE%>" name="main" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<%				
				} else{
					// By default, the user is directed to the personal space.
%>
						<frame src="<%=SessionManager.getJSPRootURL()%>/personal/Main.jsp?module=<%=Module.PERSONAL_SPACE%>" name="main" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" noresize border="0">
<%
				}
			}
		}
%>
		
<%
	}
%>
	</frameset>
</frameset>

<SCRIPT LANGUAGE="javascript">
	top.toolbar_main.space = "personal"
</script>

<noframes>
<body bgcolor="#FFFFFF">
<font face="arial,helvetica,sans-serif" size="-1" color="#000000">
<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width="80%">
	<tr>
		<td width="1%"><img src="/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td width="98%" background="/images/error/error-banner-background.gif"><font color="#ffffff" size="+1"><b>Browser Does Not Support Frames</b></font></td>
		<td width="1%"><img src="/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<h3>Your browser must support frames in order to use this application.</h3>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td width="98%" align=right background="/images/error/error-banner-background.gif"><a href="javascript:history.back();" style="text-decoration: none; color: #FFFFFF;"><font color="#ffffff" size="+1"><b>Return</a></b></font></td>
		<td><a href="javascript:history.back();"><img src="/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>
</font>
</body>
</noframes>

</html>
--%>
