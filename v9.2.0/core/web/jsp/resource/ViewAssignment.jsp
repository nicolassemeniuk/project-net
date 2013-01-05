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
            net.project.base.Module,
            net.project.resource.*,
            net.project.xml.XMLFormatter,
            net.project.security.Action,
            java.net.URLEncoder,
            net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" type="net.project.security.User" scope="session" />
<jsp:useBean id="assignment" type="net.project.resource.Assignment" scope="request" />
<jsp:useBean id="propertyURL" class="java.lang.String" scope="request" />
<jsp:useBean id="assigmentWorkLog" type="net.project.persistence.IXMLPersistence" scope="request" />
<jsp:useBean id="returnTo" type="java.lang.String" scope="request" />

<html>
<head>
<title><display:get name="prm.resource.viewassignment.pagetitle"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function cancel() {
    self.document.location = JSPRootURL + '<%=returnTo%>';
}

function captureWork() {
<%
    int captureWorkModule = Module.SCHEDULE;
    int returnToViewModule = Module.PROJECT_SPACE;
    int action = Action.VIEW;
    if (user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE)) {
        captureWorkModule = Module.RESOURCE;
        returnToViewModule = Module.PERSONAL_SPACE;
        action = Action.VIEW;
    }
%>
    var url = JSPRootURL + "/servlet/AssignmentController/CurrentAssignments"+
        "/Update?module=<%=captureWorkModule%>&action=<%=action%>";
    url += '&objectID=<jsp:getProperty name="assignment" property="objectID" />';
    url += '&returnTo=<%=URLEncoder.encode("/servlet/AssignmentController/View?&module=" + returnToViewModule + "&action=" + Action.VIEW + "&objectID=" + assignment.getObjectID() + "&personID=" + assignment.getPersonID(), "UTF-8")%>';
    url += '&returnTo2=<%=URLEncoder.encode(returnTo, "UTF-8")%>';
    self.document.location = url;
}

function reset() {
	var theLocation = JSPRootURL +'<%="/servlet/AssignmentController/View?&module="+ Module.PROJECT_SPACE +"&objectID="+ assignment.getObjectID() +"&personID="+ assignment.getPersonID()%>';
	self.location = theLocation;
}
</script>

</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.personal.nav.assignment">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="@prm.resource.viewassignment.pagetitle"
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentFinder/View" %>'
					queryString='<%="module="+Module.RESOURCE+"&id="+request.getParameter("objectID")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
        <%-- 
        It will now only be possible to capture work using blog it.
        if (assignment.getAssignmentType().canCaptureWork() && !assignment.getStatus().equals(AssignmentStatus.REJECTED)) { %>
		<tb:button type="capture_work"/>
        <% } --%>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<channel:channel customizable="false" name="viewassignment">
    <channel:insert closeable="false" width="100%" name="viewassignment_channel" minimizable="false" title="<%=assignment.getObjectName()%>" row="0">
        <channel:button type="properties" labelToken="prm.resource.viewassignment.showobject.label" href="<%=propertyURL%>"/>
        <pnet-xml:transform stylesheet="/resource/xsl/view-assignment.xsl" xml="<%=assignment.getXML()%>" />
    </channel:insert>
    <channel:insert closeable="false" width="100%" name="viewassignmentwork_channel" minimizable="false" titleToken="prm.resource.viewassignment.worklog.title" row="1">
        <pnet-xml:transform stylesheet="/resource/xsl/PersonalAssignmentWorkLog.xsl" xml="<%=assigmentWorkLog.getXML()%>" />
    </channel:insert>
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
    	<!--tb:button type="cancel"/-->
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
