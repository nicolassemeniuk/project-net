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
|   $Revision: 19849 $
|   $Date: 2009-08-25 07:05:28 -0300 (mar, 25 ago 2009) $
|   $Author: dpatil $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Initiate Domain Migration -- Step 11"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
            net.project.admin.ApplicationSpace,
            java.util.Iterator,
            net.project.security.SessionManager,
			net.project.configuration.ConfigurationSpace,
			net.project.base.property.PropertyProvider,
			net.project.security.domain.UserDomain,
			net.project.resource.PersonListBean"
%>                                                                        
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>
<jsp:useBean id="domainMigrationManager" class="net.project.security.domain.DomainMigrationManager" scope="request"/>
<jsp:useBean id="csManager" class="net.project.configuration.ConfigurationSpaceManager" scope="request"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userDomain" class="net.project.security.domain.UserDomain" scope="session"/> 

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<html>
<head>
<title><%=PropertyProvider.get("prm.application.domainmigration.wizardpage4.title") %></title>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

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

function finish(){
	Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.application.domainmigration.wizardpage4.finish.confirm" />', function(btn) { 
		if(btn == 'yes'){ 
	    	theAction("finish");
			theForm.submit();
			return true;
		} 
	});
}
function back() {
	theAction("back");
	theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=initiate_domainmigration&section=page3";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();">

<form action="<%=SessionManager.getJSPRootURL()%>/admin/domain/InitiateMigration3Processing.jsp" method="post">
    <input type="hidden" name="theAction">

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
        <td>&nbsp;</td>
	</tr>	
	
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
        <td nowrap class="channelHeader"><%=PropertyProvider.get("prm.application.domainmigration.wizardpage4.channel.title") %></td>
        <td align="right" class="channelHeader">&nbsp;</td>
        <td align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
    </tr>
	
	<tr>
        <td>&nbsp;</td>
	</tr>	
	
	<tr>			
		<td colspan="4" class="instructions" wrap>
       	<%=PropertyProvider.get("prm.application.domainmigration.wizardpage4.instructions") %>
		</td>		
	</tr>
	
	<tr>
		<td class="tableContent">&nbsp;</td>
	</tr>
	
	<tr><td class="tableContent" colspan="4">
	<ul>
<%
	int size = 0;
	
	size = userDomain.getNewlyAddedConfigurationCollection().size();
	Object[] intAry = { new Integer(size) };
		
	if (size > 0 ) {
%>					
		<li><%=PropertyProvider.get("prm.application.domainmigration.wizardpage4.configurationsadded.message",intAry) %></li>
<%
	}
		
	Object[] usersAry = { new Integer(domainMigration.getSourceDomain().getUserCount()) };	
%>		
		
		<li><%=PropertyProvider.get("prm.application.domainmigration.wizardpage4.totaluseraffected.message",usersAry) %></li>
	</ul>
	</td>
	</tr>
	<tr>
		<td class="tableContent">&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
            <tb:band name="action">
                <tb:button type="finish"/>
				<tb:button type="back"/>
		        <tb:button type="cancel" />
            </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS space="application" />

</body>
</html>