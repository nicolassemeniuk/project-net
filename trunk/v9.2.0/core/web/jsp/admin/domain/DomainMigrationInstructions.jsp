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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Initiate Domain Migration -- Step 11"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
			net.project.resource.PersonListBean"
%>                                                                        
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />
<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
	
	domainMigration.setSourceDomainID(request.getParameter("selected"));
	
	Object str[] = {domainMigration.getSourceDomain().getName() , new Integer(domainMigration.getSourceDomain().getUserCount())} ;
%> 
<html>
<head>
<title><%=PropertyProvider.get("prm.application.domainmigration.initialinstructionspage.title") %></title>

<template:getSpaceCSS />
<template:import type="javascript" src="/src/window_functions.js" />

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

function submit() {

	var m_url = JSPRootURL + "/admin/domain/InitiateMigration1.jsp?selected="+ '<%= request.getParameter("selected") %>' + "&action=2&module=240";
	self.document.location = m_url;

}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=domainmigration_initiate&section=initial_instructions";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();">
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/admin/domain/InitiateMigration1Processing.jsp">
    	
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
        <td>&nbsp;</td>
	</tr>	
	
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
        <td nowrap class="channelHeader"><%=PropertyProvider.get("prm.application.domainmigration.wizardpage1.channel.title") %></td>
        <td align="right" class="channelHeader">&nbsp;</td>
        <td align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
    </tr>
	<tr>
        <td>&nbsp;</td>
	</tr>
	<tr>	
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="4" class="instructions"><%=PropertyProvider.get("prm.application.domainmigration.wizardpage1.initial.instructions" ,str) %>
		</td>		
	</tr>
	<tr>
        <td>&nbsp;</td>
	</tr>			
</table>

<tb:toolbar style="action" showLabels="true">
            <tb:band name="action">
                <tb:button type="submit"  label="Continue"/>
		        <tb:button type="cancel" />
            </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>