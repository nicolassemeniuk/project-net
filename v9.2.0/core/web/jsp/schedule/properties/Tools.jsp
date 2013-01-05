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
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<template:getSpaceJS />


<script language="javascript" type="text/javascript">
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW%>';
}

function recalculate() {
	Ext.Ajax.request({
		url: "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/Recalculate?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>",
		params: { module: <%=Module.SCHEDULE%> },
		method: 'POST',
		success: function(response , request){
			var res = Ext.util.JSON.decode(response.responseText);
			if(res.success){
				self.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>";
			}else if(res.errors) {
					extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
			}
		}
	});
    //self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/Recalculate?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>";
}

function resequence() {
	Ext.Ajax.request({
		url: "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/Resequence?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>",
		params: { module: <%=Module.SCHEDULE%> },
		method: 'POST',
		success: function(response , request){
			var res = Ext.util.JSON.decode(response.responseText);
			if(res.success){
				self.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>";
			}else if(res.errors) {
					extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
			}
		}
	});
    //self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/Resequence?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>";
}

function findInconsistentTasks() {
		Ext.Ajax.request({
		url: "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/FindInconsistentTaskFilters?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>",
		params: { module: <%=Module.SCHEDULE%> },
		method: 'POST',
		success: function(response , request){
			var res = Ext.util.JSON.decode(response.responseText);
			if(res.success){
				self.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>";
			}else if(res.errors) {
					extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
			}
		}
	});
    //self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/FindInconsistentTaskFilters?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
}

function findInconsistentAssignments() {
		Ext.Ajax.request({
		url: "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/FindInconsistentTaskWorkFilters?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>",
		params: { module: <%=Module.SCHEDULE%> },
		method: 'POST',
		success: function(response , request){
			var res = Ext.util.JSON.decode(response.responseText);
			if(res.success){
				self.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>";
			}else if(res.errors) {
					extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
			}
		}
	});
    //self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/MainProcessing/FindInconsistentTaskWorkFilters?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>"
}

function reset() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>';
}

function helpMe() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=schedule_tools";
 	openwin_help(helplocation);
}

</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.schedule.tools.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/properties/Tools.jsp" %>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE+"&action="+Action.MODIFY%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>
<tab:tabStrip>
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <display:if name="@prm.crossspace.isenabled">
        <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' selected="true"/>
</tab:tabStrip>
<br>
<table border="0" width="100%">
<tr class="tableHeader">
    <td width="20%"><display:get name="prm.schedule.tools.columns.tool"/></td>
    <td width="60%"><display:get name="prm.schedule.tools.columns.description"/></td>
    <td><display:get name="prm.schedule.tools.columns.action"/></td>
</tr>
<tr><td colspan="3" class="headerSep"></td></td>
<tr class="tableContent" valign="top">
    <td><display:get name="prm.schedule.tools.resequence.toolname"/></td>
    <td>
    <display:get name="prm.schedule.tools.resequence.description"/>
    </td>
    <td><input type="button" value='<%=PropertyProvider.get("prm.schedule.tools.runtool")%>' onClick="resequence();"></td>
</tr>
<tr><td colspan="3" class="rowSep"></td></td>
<tr class="tableContent" valign="top">
    <td><display:get name="prm.schedule.tools.recalculate.toolname"/></td>
    <td>
    <display:get name="prm.schedule.tools.recalculate.description"/>
    </td>
    <td><input type="button" value='<%=PropertyProvider.get("prm.schedule.tools.runtool")%>' onClick="recalculate();"<%=schedule.isAutocalculateTaskEndpoints() ? "" : " disabled"%>></td>
</tr>
<tr><td colspan="3" class="rowSep"></td></td>
<tr class="tableContent" valign="top">
    <td><display:get name="prm.schedule.tools.inconsistenttask.toolname"/></td>
    <td>
    <display:get name="prm.schedule.tools.inconsistenttask.description"/>
    </td>
    <td><input type="button" value='<%=PropertyProvider.get("prm.schedule.tools.runtool")%>' onClick="findInconsistentTasks()"></td>
</tr>
<tr><td colspan="3" class="rowSep"></td></td>
<tr class="tableContent" valign="top">
    <td><display:get name="prm.schedule.tools.inconsistentassignment.toolname"/></td>
    <td>
    <display:get name="prm.schedule.tools.inconsistentassignment.description"/>
    </td>
    <td><input type="button" value='<%=PropertyProvider.get("prm.schedule.tools.runtool")%>' onClick="findInconsistentAssignments()"></td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" width="100%" bottomFixed="true">
    <tb:band name="action">
    	<tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
