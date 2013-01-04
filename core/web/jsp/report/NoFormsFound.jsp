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
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.report.ReportType,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<%
    ReportType reportType = ReportType.getForID(request.getParameter("reportType"));
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}

function reset() {
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.reports"> 
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.form.report.common.noformsfound.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/report/NoFormsFound.jsp" %>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>
<table border="0" width="97%" cellspacing="0" cellpadding="0">
<tr class="actionBar">
	<td width="1%" class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;</td>
	<td width="1%" align=right class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
</tr>
<tr>
    <td></td>
    <td class="errorMessage"><%=PropertyProvider.get("prm.form.report.common.noformsfound.title", reportType.getName())%></td>
    <td></td>
</tr>
<tr>
    <td></td>
    <td class="tableContent"><%=PropertyProvider.get("prm.form.report.common.noformsfound.message")%></td>
    <td></td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
    	<tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
