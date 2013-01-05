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
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.xml.XMLFormatter,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="baselineList" class="net.project.schedule.BaselineList" scope="request" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var theForm;
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function setup() {
    theForm = self.document.forms[0];
}

function create() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/Create?module=<%=Module.SCHEDULE%>&action=<%=Action.CREATE%>";
}

function modify() {
    if (verifySelectionForField(theForm.id, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/Modify?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&baselineID="+getSelectedValue(theForm.id);
    }
}

function remove() {
    if (verifySelectionForField(theForm.id, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.global.javascript.confirmdeletion.message")%>', function(btn) { 
			if(btn == 'yes'){ 
				self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/Remove?module=<%=Module.SCHEDULE%>&action=<%=Action.DELETE%>&baselineID="+getSelectedValue(theForm.id);
			}else{
			 	return false;
			}
		});
    } 
}

function properties() {
    if (verifySelectionForField(theForm.id, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/View?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&baselineID="+getSelectedValue(theForm.id);
    }
}

function reset() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/List?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
}

function cancel() {
	self.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE %>";
}

function helpMe() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=schedule_baseline";
 	openwin_help(helplocation);
}

</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="@prm.schedule.properties.baseline"
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/Baseline/List" %>'
					queryString='<%="module="+Module.SCHEDULE+"&action="+Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
        <tb:button type="create"/>
        <tb:button type="modify"/>
        <tb:button type="remove"/>
        <tb:button type="properties"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' selected="true"/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <display:if name="@prm.crossspace.isenabled">
    <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>


<form>
<%
    baselineList.loadBaselinesForObject(schedule.getID());

    XMLFormatter formatter = new XMLFormatter();
    formatter.setStylesheet("/schedule/properties/xsl/schedule-baseline-list.xsl");
    out.println(formatter.getPresentation(baselineList.getXML()));
%>
</form>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
