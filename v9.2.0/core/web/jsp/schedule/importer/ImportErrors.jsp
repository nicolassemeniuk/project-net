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
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider"
%> 
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="scheduleImporter" type="net.project.schedule.importer.XMLImporter" scope="session"/>
<jsp:useBean id="errors" type="net.project.util.ErrorReporter" scope="session"/>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}
function finish() {
    self.location = '<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>';
}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display=""
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>
<div id='content'>

<table border="0" width="100%" cellspacing="0" cellpadding="0">

<tr class="channelHeader">
    <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;XML Document imported with following errors</td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td><pnet-xml:transform scope="request" stylesheet="/base/xsl/error-report.xsl" content="<%=errors.getXMLBody()%>" /></td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="finish" />
	</tb:band>
</tb:toolbar>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
