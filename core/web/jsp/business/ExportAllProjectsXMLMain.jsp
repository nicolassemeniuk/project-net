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
    info="Application Space Business Portfolio"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,net.project.space.SpaceFactory,
            net.project.base.property.PropertyProvider,
            net.project.security.User,net.project.security.Action,
            net.project.security.SessionManager,net.project.space.Space,
            net.project.business.BusinessSpace"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />

<html>
<head>
<title>MS Project export Utility</title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="view" module="<%=Module.BUSINESS_SPACE%>" />
<%
	boolean isBusinessExportXMLEnabled = false ;
	boolean isOwnedProjects = false;
		
	if (request.getParameter("selected") != null && !request.getParameter("selected").trim().equals("")) {
		Space space = SpaceFactory.constructSpaceFromID(request.getParameter("selected"));
		space.load();
		isBusinessExportXMLEnabled = space.isUserSpaceAdministrator(user) ;
		pageContext.setAttribute("space_to_disable" , space , pageContext.SESSION_SCOPE);
		
		final Integer numProjects = new Integer(((BusinessSpace)space).getNumProjects()); 
		isOwnedProjects = numProjects != null && numProjects > 0;
	}	
	
%>

<template:getSpaceCSS space="business"/>

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
	if (<%=isOwnedProjects%>) {
		var theLocation=JSPRootURL+"/servlet/AllMSPExportXml?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&selected=" + self.document.forms[0].selected.value;
    	self.location = theLocation;		
	}
	else {
		var errorMessage = '<%=PropertyProvider.get("prm.business.export.ms.project.no.owned.projects.message")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
	}
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_portfolio&section=delete";
	openwin_help(helplocation);
}


</script>
</head>
 
<body class="main" onLoad="setup();" style="background: none;">
<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.business" showAll="false">
</tb:toolbar>
<br>
<div class="pageTitle">Export Files</div>
<form method="post" action="ExportAllProjectsXMLMainProcessing.jsp">
<%
	if (isBusinessExportXMLEnabled) {
%>	
    <input type="hidden" name="space" value='<%= request.getParameter("selected") %>' >
<%
	}
%>	
	<input type="hidden" name="module">
	<input type="hidden" name="action">
	<input type="hidden" name="selected" value='<%= request.getParameter("selected") %>'/>

	<br />
<table width="97%" cellspacing="0" cellpadding="0" vspace="0"  border="0">
	<tr>
		<td colspan="8"> 
    		<table border=0 cellpadding=0 cellspacing=0 width="97%"><tr>
				<td valign="top" align="left" width="100%">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr class="channelHeader">
						<td class="channelHeader" width="1%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
						<td nowrap class="channelHeader">Note: </td> 
						<td align="right" class="channelHeader">&nbsp;</td>
						<td align="right" class="channelHeader" width="5%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
					</tr>
					<tr valign="top">
						<td class="channelContent">&nbsp;</td>
						<td colspan="4" class="channelContent"></td>
					</tr>
					</table>
				</td>
			</tr></table>
		</td>
	</tr>
    <tr>
		<td width="1%"></td>
		<td class="tableContent"><label>With this option you will export all the MS Project files from all Project's and Sub Project's of the selected Business.</label></td>
	</td>
</tr>
<tr>
  	<td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="finish"/>
		</tb:band>
	</tb:toolbar>

  </form>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>