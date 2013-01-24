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
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            java.util.Iterator,
            net.project.util.HTMLUtils,
            net.project.resource.mvc.handler.LogEntry,
            net.project.space.SpaceType,
            net.project.space.ISpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="dateDisplay" class="java.lang.String" scope="request"/>
<jsp:useBean id="assignmentName" class="java.lang.String" scope="request"/>
<jsp:useBean id="objectID" class="java.lang.String" scope="request" />
<jsp:useBean id="previousLogEntries" type="java.util.List" scope="request" />
<jsp:useBean id="currentLogEntries" type="java.util.List" scope="request" />
<jsp:useBean id="exampleTime" type="java.lang.String" scope="request"/>

<html>
<head>
<title><%=PropertyProvider.get("prm.resource.assignments.specifyhours.title", dateDisplay)%></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
    function submit() {
        self.document.forms[0].submit();
    }

    function cancel() {
        window.close();
    }
</script>

</head>

<body>


<form action="<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/SpecifyHoursProcessing" method="post">
<input type="hidden" name="module" value="<%=(user.getCurrentSpace().getSpaceType().getID().equals(ISpaceTypes.PROJECT_SPACE)) ? Module.SCHEDULE : Module.RESOURCE%>">
<input type="hidden" name="objectID" value="<%=objectID%>">
<input type="hidden" name="dateID" value="<%=request.getAttribute("dateID")%>">
<input type="hidden" name="showFilterPane" value="<%=request.getAttribute("showFilterPane")%>">

<errors:show clearAfterDisplay="true" scope="request"/>
<channel:channel name="timesWorkedOn" customizable="false">
    <channel:insert name="times" closeable="false" minimizable="false" title='<%=PropertyProvider.get("prm.resource.assignments.specifyhours.title", dateDisplay)%>' >
        <table border="0" width="100%">
        <tr>
            <td colspan="3" class="instructions">
                <display:get name="prm.resource.assignments.specifyhours.exampleformat.instructions">
                    <display:param value="<%=exampleTime%>" />
                </display:get>
            </td>
        </tr>
        <tr><td colspan="3">&nbsp;</td></tr>
        <tr>
            <td class="tableHeader"><display:get name="prm.resource.assignments.specifyhours.column.from"/></td>
            <td class="tableHeader"><display:get name="prm.resource.assignments.specifyhours.column.to"/></td>
            <td class="tableHeader"><display:get name="prm.resource.assignments.specifyhours.column.comments"/></td>
        </tr>
        <%
        for (Iterator it = previousLogEntries.iterator(); it.hasNext();) {
            LogEntry entry = (LogEntry) it.next();
        %>
        <tr>
            <td class="tableContent"><%=entry.getStartTime()%><input type="hidden" name="previousTimeSpanStart" value="<%=entry.getStartTime()%>"/></td>
            <td class="tableContent"><%=entry.getEndTime()%><input type="hidden" name="previousTimeSpanEnd" value="<%=entry.getEndTime()%>"/></td>
            <td class="tableContent"><%=entry.getComments() == null ? "" : HTMLUtils.escape(entry.getComments())%></td>
        </tr>
        <%
        }
        %>
        <tr>
            <td><input type="text" name="timeSpan1Start" size="5"<%=currentLogEntries.size() > 0 ? " value=\"" + ((LogEntry)currentLogEntries.get(0)).getStartTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan1End" size="5"<%=currentLogEntries.size() > 0 ? " value=\"" + ((LogEntry)currentLogEntries.get(0)).getEndTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan1Comment" size="25"<%=currentLogEntries.size() > 0 ? " value=\"" + HTMLUtils.escape(((LogEntry)currentLogEntries.get(0)).getComments()) + "\"" : ""%>></td>
        </tr>
        <tr>
            <td><input type="text" name="timeSpan2Start" size="5"<%=currentLogEntries.size() > 1 ? " value=\"" + ((LogEntry)currentLogEntries.get(1)).getStartTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan2End" size="5"<%=currentLogEntries.size() > 1 ? " value=\"" + ((LogEntry)currentLogEntries.get(1)).getEndTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan2Comment" size="25"<%=currentLogEntries.size() > 1 ? " value=\"" + HTMLUtils.escape(((LogEntry)currentLogEntries.get(1)).getComments()) + "\"" : ""%>></td>
        </tr>
        <tr>
            <td><input type="text" name="timeSpan3Start" size="5"<%=currentLogEntries.size() > 2 ? " value=\"" + ((LogEntry)currentLogEntries.get(2)).getStartTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan3End" size="5"<%=currentLogEntries.size() > 2 ? " value=\"" + ((LogEntry)currentLogEntries.get(2)).getEndTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan3Comment" size="25"<%=currentLogEntries.size() > 2 ? " value=\"" + HTMLUtils.escape(((LogEntry)currentLogEntries.get(2)).getComments()) + "\"" : ""%>></td>
        </tr>
        <tr>
            <td><input type="text" name="timeSpan4Start" size="5"<%=currentLogEntries.size() > 3 ? " value=\"" + ((LogEntry)currentLogEntries.get(3)).getStartTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan4End" size="5"<%=currentLogEntries.size() > 3 ? " value=\"" + ((LogEntry)currentLogEntries.get(3)).getEndTime() + "\"" : ""%>></td>
            <td><input type="text" name="timeSpan4Comment" size="25"<%=currentLogEntries.size() > 3 ? " value=\"" + HTMLUtils.escape(((LogEntry)currentLogEntries.get(3)).getComments()) + "\"" : ""%>></td>
        </tr>
        </table>
    </channel:insert>
</channel:channel>
</form>

<tb:toolbar style="action" showLabels="true" width="97%">
    <tb:band name="action">
        <tb:button type="submit"/>
        <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>
</center>
<template:getSpaceJS />
</body>
</html>
