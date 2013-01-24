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
|   $Revision: 15815 $
|       $Date: 2007-04-10 10:04:20 +0530 (Tue, 10 Apr 2007) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            java.util.Iterator,
			net.project.soa.schedule.Project.Resources.Resource,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="scheduleImporter" type="net.project.schedule.importer.XMLImporter" scope="session"/>
<jsp:useBean id="personOptionList" type="java.lang.String" scope="request"/>
<jsp:useBean id="resources" type="java.util.Collection" scope="request"/>
<jsp:useBean id="roster" type="net.project.resource.Roster" scope="session"/>
<jsp:useBean id="user" type="net.project.security.User" scope="session"/>
<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">

    var theForm;

    function setup() {
        theForm = self.document.forms["main"];
    }

    function cancel() {
    	theLocation="<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%= net.project.base.Module.SCHEDULE %>";
    	self.location = theLocation;
    }

    function next() {
        theAction("next");
        theForm.submit();
    }
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken='prm.schedule.import.xml.mapresources.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/importer/MapResources.jsp" %>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE+"&action="+Action.MODIFY%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL()%>/servlet/ImportController/MapResourcesProcessing" method="post">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="theAction">

<table border="0" width="100%" cellspacing="0" cellpadding="0">

<tr class="channelHeader">
    <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;<display:get name="prm.schedule.import.xml.mapresources.pagetitle" /></td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="instructions"><display:get name="prm.schedule.import.xml.mapresourceinstruction.message" /></td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="3">&nbsp;</td></tr>

<tr>
    <td>&nbsp;</td>
    <td>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">

    <%-- Column Headings --%>
    <thead>
    <tr class="tableHeader">
        <td><display:get name="prm.schedule.import.xml.mapresource.mspresourcename.label" /></td>
        <td><display:get name="prm.schedule.import.xml.mapresource.workspaceresource.label" /></td>
        <td><display:get name="prm.resources.assignor.label" /></td>
    </tr>
    <tr class="tableLine">
        <td colspan="3"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" height="2" width="1" border="0"></td>
    </tr>
    </thead>

    <tbody>
    <%-- Mapping rows --%>
<%
    for (Iterator it = resources.iterator(); it.hasNext();) {
        Resource resource = (Resource) it.next();
%>
    <tr class="tableContent">
        <td><%= resource.getName() %></td>
        <td> 
            <select name="resource<%=resource.getUID()%>">
                <option value="ignore"><display:get name="prm.schedule.import.xml.mapresource.ignoreusersassignment.label" /></option>
                <%=roster.getSelectionListByEmail(resource.getEmailAddress())%>
            </select>
        </td>
        <td> 
            <select id="assignor<%=resource.getUID()%>" name="assignor<%=resource.getUID()%>"><%=roster.getSelectionList(user.getID())%></select>
        </td>
    </tr>
    <tr><td colspan="2" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" height="1" width="1" border="0"></td></tr>
<%
    }
%>
    </tbody>
    </table>
    </td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
