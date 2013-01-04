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
    import="net.project.util.NumberFormat,
            java.util.Iterator,
            net.project.resource.AssignmentRoster,
            net.project.resource.ScheduleEntryAssignment,
            net.project.resource.AssignmentStatus,
            net.project.util.Validator,
            net.project.security.SessionManager,
            net.project.gui.html.HTMLOptionList,
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="assignmentRoster" class="net.project.resource.AssignmentRoster" scope="request" />
<!--<jsp:useBean id="idList" class="java.lang.String" scope="request"/>-->

<%
    NumberFormat nf = NumberFormat.getInstance();
%>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />

<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
    <%-- For fixed Duration Effort driven tasks, the percentage value will be ignored when
         replacing assignments, or adding new assignments since it is automatically calculated --%>
    window.parent.setFixedDurationEffortDrivenSelected(<%=request.getAttribute("isFixedDurationEffortDrivenSelected")%>);
}

function submitForm() {
    var integerErrMsg = '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>';
    var percentTextbox;

    if (verifySelectionForField(theForm.resource, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        if (theForm.resource.checked) {
            // A single checkbox
            percentTextbox = eval("theForm.percent_"+theForm.resource.value);
            if (!verifyNonBlankField(percentTextbox.value)) percentTextbox.value = "<%=nf.formatNumber(100)%>";
        } else {
            for(var i=0; i< theForm.resource.length; i++) {
                if (theForm.resource[i].checked) {
                    percentTextbox = eval("theForm.percent_"+theForm.resource[i].value);
                    if (!verifyNonBlankField(percentTextbox.value)) percentTextbox.value = "<%=nf.formatNumber(100)%>";
                }
            }
        }
        // Get the replace value from the parent; previously the parent was passing it down to us
        // but for some reason that didn't work in IE
        theForm.replaceExisting.value = getSelectedValue(window.parent.document.forms[0].replaceExisting);
        theForm.submit();
    }

}

function showResourceAllocation(personID) {
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID;

    openwin_large('resource_allocation', url);
}

</script>

</head>

<body onLoad="setup();">

<form action='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/TaskList/AssignResourcesDialogProcessing"%>' method="post">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="replaceExisting" value="0">
    <input type="hidden" name="idList" value="<%=request.getParameter("idList")%>">

<table width="100%" border="0">
<tr class="tableHeader">
    <td></td>
    <td><display:get name="prm.schedule.taskview.resources.assign.person.column"/></td>
    <td><display:get name="prm.schedule.taskview.resources.assign.assigned.column"/></td>
    <td align="center"><display:get name="prm.schedule.taskview.resources.assign.workingcalendar.column"/></td>
</tr>
<%
    for (Iterator it = assignmentRoster.iterator(); it.hasNext();) {
        AssignmentRoster.Person person = (AssignmentRoster.Person)it.next();
%>
<tr class="tableContent">
    <td align="center"><input name="resource" id="<%=person.getID()%>" value="<%=person.getID()%>" type="checkbox"></td>
    <td><label for="<%=person.getID()%>"><%=person.getDisplayName()%></label></td>
    <td><input name="percent_<%=person.getID()%>" type="text" size="4" maxlength="4"></td>
    <td align="center"><a href='javascript:showResourceAllocation(<%=person.getID()%>)'><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0"></a></td>
</tr>
<% } %>
</table>
</form>
<template:getSpaceJS />
</body>
</html>
