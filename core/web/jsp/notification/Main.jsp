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
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Name"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
            net.project.security.User"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function cancel() { self.document.location = '<%= net.project.space.SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace().getID()) %>'; }
function reset() { self.document.location = JSPRootURL + "/project/Setup.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>"; }
function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=notification&section=main";
	openwin_help(helplocation);
}
</script>
<%
	boolean isEnabled = false ;
    
    //Get the type of space we are currently in.  We use this in the text below.
    String currentSpaceType;
    if (user.getCurrentSpace() != null)
        currentSpaceType = user.getCurrentSpace().getType();
    else
        currentSpaceType = "space";
%>
</head>
<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
  --  Toolbar & History                                                                                   
  ------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.notifications.link">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.global.notification.main.module.history")%>' 
                          level="1"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/notification/Main.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------
  --  Page Content                                                                                        
  ------------------------------------------------------------------------------------------------------%>
<table border="0" vspace="0" width="100%">
<tr> 
<td colspan=2>&nbsp;</td>
</tr>
<tr>

<td colspan=2>
	<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
	<tr>
	<td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
<td nowrap class="channelHeader" align=left><%=PropertyProvider.get("prm.global.notification.main.channel.manage.title")%></td>
	<td width=1% align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
	<tr>
	</table>
</td>
</tr>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.global.notification.main.managesubscriptions.1.text")%><a href='<%=SessionManager.getJSPRootURL()+"/notification/ManageSubscriptions.jsp?spaceID="+user.getCurrentSpace().getID()+"&module="+request.getParameter("module") %>'><%=PropertyProvider.get("prm.global.notification.main.managesubscriptions.2.link")%></a><%=PropertyProvider.get("prm.global.notification.main.managesubscriptions.3.text")%></td>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.global.notification.main.managesubscriptions.description", new Object[] {user.getCurrentSpace().getType()})%></td><tr>
</tr>
<tr>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.global.notification.main.createsubscriptions.1.text")%><a href="<%=SessionManager.getJSPRootURL()%>/notification/CreateTypeSubscription1.jsp?module=<%=request.getParameter("module") %>"><%=PropertyProvider.get("prm.global.notification.main.createsubscriptions.2.link")%></a><%=PropertyProvider.get("prm.global.notification.main.createsubscriptions.3.text")%></td>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.global.notification.main.createsubscriptions.description", new Object[] {user.getCurrentSpace().getType()})%></td>
</tr>
<%
	if(isEnabled) {
%>
<tr>
<td class="tableContentFontOnly"><a href="<%=SessionManager.getJSPRootURL()%>/notification/NotificationScheduler.jsp"><%=PropertyProvider.get("prm.project.notification.label") %></a></td>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.project.notification.temporary.run.subscription.label") %></td>
</tr>
<tr>
<td class="tableContentFontOnly"><a href="<%=SessionManager.getJSPRootURL()%>/notification/PostmanAgent.jsp"><%=PropertyProvider.get("prm.project.notification.temporary.postman.agent.label") %></a></td>
<td class="tableContentFontOnly"><%=PropertyProvider.get("prm.project.notification.temporary.run.postman.agent.label") %></td>
</tr>
<tr>
<%
}
%>
<td colspan=2>&nbsp;</td>
</tr>
</table>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
