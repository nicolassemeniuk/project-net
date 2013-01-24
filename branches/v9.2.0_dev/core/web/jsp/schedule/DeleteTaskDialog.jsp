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
    info="Dialog box to delete tasks"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.schedule.calc.TaskCalculationType"
%>
<%@ page import="net.project.resource.AssignmentRoster"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="net.project.base.property.PropertyProvider"%>
<%@ page import="net.project.util.NumberFormat"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<jsp:useBean id="assignmentRoster" class="net.project.resource.AssignmentRoster" scope="request" />
<jsp:useBean id="idList" class="java.lang.String" scope="request"/>
<jsp:useBean id="isSummary" class="java.lang.String" scope="request"/>
<jsp:useBean id="deleteTask" class="java.lang.String" scope="request"/>

<html>
<head>
<title><display:get name="prm.schedule.deletetaskdialog.title"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
<%
    NumberFormat nf = NumberFormat.getInstance();
%>
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    window.close();
}

function submit() {
    document.body.style.cursor = "wait";
    theForm.deleteTask.value = getSelectedValue(window.parent.document.forms[0].deleteTask);
    if (parseInt(theForm.deleteTask.value) == 2) {
    	window.parent.document.forms[0].deleteTask[0].checked=true;
    	cancel();
    } else {
    	theForm.submit();
    }
}

</script>

</head>

<body onLoad="setup();">

<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>

<form action='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/MainProcessing/DeleteTask"%>' method="post">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="idList" value="<%=idList%>">
    <input type="hidden" name="isSummary" value="<%=isSummary%>">

  <tr><td>&nbsp;</td></tr>
  <tr>
    <td>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td align="left" colspan="4" class="channelHeader" nowrap><%=PropertyProvider.get("prm.schedule.deletetaskdialog.title")%></td>
            <td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
        </tr>
        </table>
    </td>
  </tr>
    
<table border="0" width="100%">
<%
   if (isSummary.equals("yes")) {
%>
  <tr>
    <td class="tableContent"><display:get name="prm.schedule.deletetaskdialog.deletetasksummarytaskdialog"/></td>
  </tr>
  <tr>
    <td class="tableContent">
        <label><input type="radio" id="deleteTaskYes" name="deleteTask" value="0" checked><display:get name="prm.schedule.deletetaskdialog.deletetasksummarychilddialog"/></label>
    </td>
  </tr>
  <tr>
    <td class="tableContent">
        <label><input type="radio" name="deleteTask" value="1"><display:get name="prm.schedule.deletetaskdialog.deletetasksummarydialog"/></label>
    </td>
  </tr>
<%
} else {
%>  
  <tr>
    <td class="tableContent">
        <label><display:get name="prm.schedule.deletetaskdialog.deletetasksdialog"/></label>
    </td>
  </tr>
  <tr>
    <td class="tableContent">
        <label><input type="radio" id="deleteTaskYes" name="deleteTask" value="0" checked><display:get name="prm.schedule.deletetaskdialog.deletetaskdialog"/></label>
        <label><input type="radio" name="deleteTask" value="2"><display:get name="prm.schedule.deletetaskdialog.deletetaskdialog.no"/></label>
    </td>
  </tr>
<% 
}
%>
</table>

<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
                <tb:button type="submit" id="submitButton"/>
				<tb:button type="cancel" id="cancelButton"/>
			</tb:band>
</tb:toolbar>

</form>

<template:getSpaceJS />
</body>
</html>
