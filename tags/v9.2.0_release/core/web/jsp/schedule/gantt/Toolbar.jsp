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
|   $Revision: 18483 $
|       $Date: 2008-12-04 14:10:23 -0200 (jue, 04 dic 2008) $
|     $Author: sjmittal $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.schedule.Schedule,
            net.project.base.property.PropertyProvider,
            net.project.resource.PersonProperty"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<%
    PersonProperty properties = PersonProperty.getFromSession(session);
    String[] showProgressLines = properties.get("prm.schedule.gantt", "showProgressLines");
    boolean bShowProgressLines = (showProgressLines != null && showProgressLines.length > 0 ? new Boolean(showProgressLines[0]).booleanValue() : false);
%>

<html>
<head>
<template:import type="css" src="/styles/gantt.css" />
<template:getSpaceCSS/>

<%-- ExtJS Components--%>
<template:import type="javascript" src="/src/ext-components.js" />

<template:import type="javascript" src="/src/dhtml/xmlrequest.js"/>

<title></title>
<script language="javascript" type="text/javascript">
var currentViewLevel = 1;

function zoomIn() {
    currentViewLevel--;
    parent.ganttFrame.location.replace("<%=SessionManager.getJSPRootURL()+"/schedule/gantt/GanttFrame.jsp?timeScaleID="%>"+(currentViewLevel)+"<%="&module="+Module.SCHEDULE%>");
    if (currentViewLevel == 0) {
        var zoomInButton = document.getElementById("zoomInButton");
        zoomInButton.disabled = true;
        zoomInButton.style.backgroundImage = "url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomIn-Disabled.gif)";
    } else {
        var zoomOutButton = document.getElementById("zoomOutButton");
        zoomOutButton.disabled = false;
        zoomOutButton.style.backgroundImage = "url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomOut.gif)";
    }
}

function zoomOut() {
    currentViewLevel++;
    parent.ganttFrame.location.replace("<%=SessionManager.getJSPRootURL()+"/schedule/gantt/GanttFrame.jsp?timeScaleID="%>"+(currentViewLevel)+"<%="&module="+Module.SCHEDULE%>");

    if (currentViewLevel == 7) {
        var zoomOutButton = document.getElementById("zoomOutButton");
        zoomOutButton.disabled = true;
        zoomOutButton.style.backgroundImage = "url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomOut-Disabled.gif)";
    } else {
        var zoomInButton = document.getElementById("zoomInButton");
        zoomInButton.disabled = false;
        zoomInButton.style.backgroundImage = "url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomIn.gif)";
    }
}

function print() {
	var errorMessage = "Printing not yet implemented";
	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
}

//This function is called by the gantt chart after the zoom is complete.
function zoomComplete(level) {
    currentViewLevel = level;
}

function onProgressLinesChange() {
    var show = (self.document.forms[0].showProgressLines.checked ? "true" : "false");
    var xml = new XMLRemoteRequest();
    var response = xml.getRemoteDocumentString("<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Gantt/StoreSettings?"+
        "module=<%=Module.SCHEDULE%>&propertyName=showProgressLines&propertyValue="+show);

    //Reload the gantt frame
    parent.ganttFrame.location.replace("<%=SessionManager.getJSPRootURL()+"/schedule/gantt/GanttFrame.jsp?timeScaleID="%>"+(currentViewLevel)+"<%="&module="+Module.SCHEDULE%>");
}

</script>
</head>
<body class="toolbar">
<form>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
<td>
<input type="button" class="toolButton" style="background-image: url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomOut.gif)" id="zoomOutButton" onClick="zoomOut()"><input type="button" class="toolButton" style="background-image: url(<%=SessionManager.getJSPRootURL()%>/images/schedule/zoomIn.gif)" id="zoomInButton" onClick="zoomIn()"><!--<input type="button" class="toolButton" style="background-image: url(/images/schedule/print.gif)" onClick="print()">-->
</td>
</tr>
</table>
</form>
</body>
</html>
