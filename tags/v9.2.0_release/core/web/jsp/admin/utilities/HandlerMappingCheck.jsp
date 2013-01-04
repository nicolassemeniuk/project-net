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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.xml.XMLFormatter,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="xml" class="java.lang.String" scope="request" />

<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
function cancel() {
    self.location="<%=SessionManager.getJSPRootURL()%>/admin/Main.jsp?module=<%=Module.APPLICATION_SPACE%>";
}

function reset() {
	 self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/HandlerMappingServlet?module=<%=Module.APPLICATION_SPACE%>";
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.handlermapping">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="Handler Mappings"
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/HandlerMappingServlet" %>'
					queryString='<%="module="+Module.APPLICATION_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="refresh"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<span class="tableContent" style="width:97%">
	<%=PropertyProvider.get("prm.project.admin.utilities.message.label") %>
</span>
<br>
<%
    XMLFormatter xmlF = new XMLFormatter();
    xmlF.setStylesheet("/admin/utilities/xsl/handlermapping.xsl");
    out.print(xmlF.getPresentation(xml));
%>

<tb:toolbar style="action" showLabels="true" width="100%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
