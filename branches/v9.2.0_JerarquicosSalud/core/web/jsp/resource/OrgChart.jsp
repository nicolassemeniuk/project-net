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
|   $Revision: 18405 $
|       $Date: 2008-11-23 19:38:47 -0200 (dom, 23 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Budget Documents" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.document.DocumentManagerBean,
            net.project.document.ContainerBean,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.SecurityProvider,
            net.project.base.Module,
			net.project.resource.SpaceInvitationManager,
            net.project.document.ContainerType"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="container" class="net.project.document.ContainerBean" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 

<%
	SpaceInvitationManager memberWizard = (SpaceInvitationManager) session.getValue ("spaceInvitationWizard");
%>

<%

        docManager.getNavigator().put("TopContainer",request.getRequestURI() + "?" + request.getQueryString()); 
        docManager.setCancelPage((String)docManager.getNavigator().get("TopContainerReturnTo"));

        docManager.setSpace ( user.getCurrentSpace() );
        docManager.getDefaultDocumentSpace();
        docManager.setUser (user);

		container.setID( docManager.getSystemContainerIDForSpace(docManager.getSpace().getID(), ContainerType.ORGANIZATION_CHART_CONTAINER));
        container.setUser(user);

        container.setSortBy( docManager.getContainerSortBy() );
        container.setSortOrder ( docManager.getContainerSortOrder() );
        container.load();

        // We are visiting a system folder; ensures root folder reset when
        // user visits workspace's doc vault
        docManager.setVisitSystemContainer(true);

%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<%-- THIS IS THE DOCUMENT WHICH CONTAINS THE SCRIPT FOR THIS PAGE --%>
<template:import type="javascript" src="/src/document/project-planning-actions.js" />
<script language="javascript">
    var theForm;
    var errorMsg;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {

	  load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
        isLoaded = true;

    } // end setup()
    
    function security() 
    {
       if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
       {    
            

        if (!security)
            var security = openwin_security("security");
    
        if (security) {
            theAction("security");
            theForm.target = "security";
            theForm.submit();
            security.focus();
        }

        }
    }

   function help()
   {
   	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_project&section=org_chart";
   	openwin_help(helplocation);
   }


    </script>
    
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.directory.directory.tab.organisationalcharts.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.project.directory.orgchart.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/resource/OrgChart.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.DIRECTORY%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="document">
		<tb:button type="check_out" />
		<tb:button type="check_in" />
		<tb:button type="view" />
		<tb:button type="undo_check_out" />
	</tb:band>
	<tb:band name="standard">
		<tb:button type="modify" />
		<tb:button type="create" label='<%=PropertyProvider.get("prm.project.directory.orgchart.import.tooltip")%>' />
		<tb:button type="remove" />
		<tb:button type="properties" />
		<tb:button type="security" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="container" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker"> 

	<input type="hidden" name="theAction">
    <input type="hidden" name="action">
    <input type="hidden" name="module" value="<%=net.project.base.Module.DIRECTORY%>">    
	<input type="hidden" name="containerID" value="<%= container.getID() %>" >

<table border="0" width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td colspan="3">
	<tab:tabStrip>
    	<tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.participants.title")%>' href='<%=SessionManager.getJSPRootURL() + "/roster/Directory.jsp?module=" + Module.DIRECTORY%>' display="<%=Boolean.valueOf(memberWizard.isParticipantsSupported())%>" />
    	<tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.organisationalcharts.title")%>' selected="true" href='<%=SessionManager.getJSPRootURL() + "/resource/OrgChart.jsp?module=" + Module.DIRECTORY%>' display="<%=Boolean.valueOf(memberWizard.isOrgChartSupported())%>" />
    	<tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.assignments.title")%>' href='<%=SessionManager.getJSPRootURL() + "/resource/Assignments.jsp?module=" + Module.PROJECT_SPACE%>' display="<%=Boolean.valueOf(memberWizard.isAssignmentsSupported())%>" />
    	<tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.roles.title")%>' href='<%=SessionManager.getJSPRootURL()+ "/security/group/GroupListView.jsp?module=" + Module.DIRECTORY%>' display="<%=Boolean.valueOf(memberWizard.isRolesSupported())%>" />
	</tab:tabStrip>
	</td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
    <tr>
 	  	<td nowrap  colspan="3" class="pageTitle"><%=PropertyProvider.get("prm.project.directory.orgchart.pagetitle", new Object[] {user.getCurrentSpace().getName()})%></td>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
    <tr> 
	    <td colspan="3"> 
        <table border="0" width="100%" cellpadding="0" cellspacing="0">

          <%-- Apply stylesheet to format document list--%> 
          <jsp:setProperty name="container" property="stylesheet" value="/document/xsl/container-contents.xsl" /> 
          <jsp:getProperty name="container" property="presentation" /> 

        </table>
        </td>
	</tr>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
