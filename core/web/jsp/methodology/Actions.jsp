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
    info="Methodology Actions"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
			net.project.security.Action,
			net.project.base.Module,
			net.project.methodology.*,
			net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");

    // Propogate space type (if any) so that TemplifySpace can decide
    // what modules to list
    String currentSpaceType = request.getParameter("currentSpaceType");
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   theForm = self.document.forms["main"];
   isLoaded = true;
}

function help(){
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_actions";
	openwin_help(helplocation);
}

function cancel() { 
	self.document.location = JSPRootURL + "<%=(refLink != null && refLink.length() > 0 ? refLink : "/project/Setup.jsp?module=" + Module.PROJECT_SPACE)%>";
}

function reset() { self.document.location = JSPRootURL + "/methodology/Actions.jsp?module=<%=Module.METHODOLOGY_SPACE%>&refLink=<%=refLinkEncoded%>"; }

</script>
</head>

<body id="bodyWithFixedAreasSupport" onLoad="setup();" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.template.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.global.template.actions.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/methodology/Actions.jsp"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/methodology/ActionsProcessing.jsp">
	<input type="hidden" name="theAction">

<table border="0" align="left" width="97%" cellpadding="0" cellspacing="0">
	<tr class="channelHeader">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" colspan="2" align="left"><nobr><%=PropertyProvider.get("prm.global.template.actions.channel.select.title")%></nobr></td>
		<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td colspan="2" class="tableContent">
		<%=PropertyProvider.get("prm.global.template.actions.createfromspace.1.text")%>
        <a href="<%=SessionManager.getJSPRootURL()%>/methodology/TemplifySpace.jsp?module=<%=Module.METHODOLOGY_SPACE%>&action=<%=Action.CREATE%>&refLink=<%=refLinkEncoded%>&currentSpaceType=<%=currentSpaceType%>">
        <%=PropertyProvider.get("prm.global.template.actions.createfromspace.2.link")%></a>
        <%=PropertyProvider.get("prm.global.template.actions.createfromspace.3.text")%>
	  </td>
	  <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td colspan="2" class="tableContent">
		<%=PropertyProvider.get("prm.global.template.actions.createnew.1.text")%><a href="<%=SessionManager.getJSPRootURL()%>/methodology/MethodologyCreateWizard1.jsp?mode=clear&module=<%=Module.METHODOLOGY_SPACE%>&action=<%=Action.CREATE%>&refLink=<%=refLinkEncoded%>"><%=PropertyProvider.get("prm.global.template.actions.createnew.2.link")%></a><%=PropertyProvider.get("prm.global.template.actions.createnew.3.link")%>
	  </td>
	  <td>&nbsp;</td>
    </tr>
	<tr>
	    <td nowrap colspan="4">&nbsp;</td>
	</tr>
  </table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
