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
|    $Revision: 20660 $
|    $Date: 2010-04-03 10:22:40 -0300 (sáb, 03 abr 2010) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Application Space Business Portfolio"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
    		net.project.space.SpaceFactory,    		
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><%=PropertyProvider.get("prm.project.portfoliodeletepage.title")%></title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	Space currentSpace = SessionManager.getUser().getCurrentSpace();
	int currentModule;
    if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
        currentModule = Module.PERSONAL_SPACE;
    } else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
        currentModule = Module.BUSINESS_SPACE;
    } else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
        currentModule = Module.PROJECT_SPACE;
    } else {
    	currentModule = Module.PERSONAL_SPACE;
    }
    securityProvider.setCheckedModuleID(currentModule);
%>
<security:verifyAccess action="view" module="<%=currentModule%>" />
<%
	boolean isProjectDeleteEnabled = false ;
		
	if (request.getParameter("selected") != null && !request.getParameter("selected").trim().equals("")) {
		Space space = SpaceFactory.constructSpaceFromID(request.getParameter("selected"));
		space.load();
		isProjectDeleteEnabled = space.isUserSpaceAdministrator(user) ;
		pageContext.setAttribute("space_to_disable" , space , pageContext.SESSION_SCOPE);
	}	
	
%>
<%--  rig the javascript which will react appropriately to the userz actions, including sending the Form--%> 

<template:getSpaceCSS space="project"/>


<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel() { self.close(); }

function finish() {
	if(verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			theForm.module.value='<%=currentModule%>';
			theForm.action.value ='<%=Action.VIEW%>';
			theForm.submit();
	}
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_portfolio&section=delete";
	openwin_help(helplocation);
}


</script>
</head>
 
<body class="main" onLoad="setup();"  >
<div id='leftheading-project'><%= PropertyProvider.get("prm.project.portfoliodeletepage.title")%></div>
<div style='clear: both'></div>
<form method="post" action="PersonalPortfolioDeleteProcessing.jsp">
<%
	if (isProjectDeleteEnabled) {
%>	
    <input type="hidden" name="space" value='<%= request.getParameter("selected") %>' >
<%
	}
%>	
	<input type="hidden" name="module">
	<input type="hidden" name="action">
	<%-- this is probably gone now --%>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
<table width="97%" cellspacing="0" cellpadding="0" vspace="0"  border="0">
	<tr>
		<td colspan="8"> 
    		<table border=0 cellpadding=0 cellspacing=0 width="97%">
	    		<tr>
					<td valign="top" align="left" width="100%">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr class="channelHeader">
								<td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
								<td nowrap class="channelHeader"><%=PropertyProvider.get("prm.project.portfoliodelete.channel.selectoption.title")%></td>
								<td align="right" class="channelHeader">&nbsp;</td>
								<td align="right" class="channelHeader" width="5%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
							</tr>
							<tr valign="top">
								<td class="channelContent">&nbsp;</td>
								<td colspan="4" class="channelContent"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
    <tr>
		<td width="1%"><input type="radio" name="selected" value="self" id="removeyourself"></td>
		<td class="tableContent"><label for="removeyourself"><%=PropertyProvider.get("prm.project.portfoliodelete.option.removeyourself.name")%></label></td>
	</tr>
</tr>
<% 
	if (isProjectDeleteEnabled) {
%>
<tr>
	<td width="1%"><input type="radio" name="selected" value="project" id="disableproject"></td>
	<td class="tableContent"><label for="disableproject"><%=PropertyProvider.get("prm.project.portfoliodelete.option.disableproject.name")%></label></td>	
</tr>
<%
	}
%>
</table>

<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="finish" label='<%=PropertyProvider.get("prm.project.portfoliodelete.button.finish.label")%>' />
		</tb:band>
</tb:toolbar>

  </form>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
