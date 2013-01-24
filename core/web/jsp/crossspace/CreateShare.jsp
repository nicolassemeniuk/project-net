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
    info=""
    contentType="text/html; charset=UTF-8"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>
<jsp:useBean id="sharedCount" type="java.lang.String" scope="request"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
function cancel() {
self.document.location = '<%=SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW%>';
}
function submit() {
    var currentSharedCount = parseInt("<%=sharedCount%>");
    var noPermissions = (getSelectedValue(document.forms[0].permissionType) == "0");
    var isOKToSubmit = true;

    if (currentSharedCount > 0 && noPermissions) {
    	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.crossspace.createshare.removepermissions.warning", sharedCount)%>', function(btn) {			
			isOKToSubmit = (btn == 'yes');
		});
    }

	if (isOKToSubmit) {
		if(validate() == ''){
        	document.forms[0].submit();
        }else{
        	extAlert('Error', validate(), Ext.MessageBox.ERROR);
        }
    }

    //return isOKToSubmit;	 
}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="all.global.toolbar.standard.share">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display=""
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>
<br>
<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Sharing.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' selected="true"/>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>
<br>

<div id="errorsDiv">
	<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
</div>

<%-- Provide a div for server round-trip error messaging --%>
<div id="errorLocationID" class="errorMessage"></div>
<jsp:include page="include/CreateShareChannels.jsp"/>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="submit"/>
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
