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
       info="Personal Space" 
       language="java" 
       errorPage="/errors.jsp"
       import="java.util.List,
               java.util.Iterator,
               java.net.URLEncoder,
               net.project.base.property.PropertyProvider,
               net.project.resource.TimesheetFinder,
               net.project.resource.Timesheet,
               net.project.security.User, 
               net.project.security.SessionManager, 
               net.project.gui.toolbar.Button,
               net.project.gui.toolbar.ButtonType,
               net.project.base.Module,
               net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<%
    TimesheetFinder timesheetFinder = new TimesheetFinder();
    List timesheets = timesheetFinder.findByPersonId(user.getID());
%>
<html>
<head>

<title><display:get name="prm.resource.timesheet.list.pagetitle"/></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<SCRIPT language="javascript" type="text/javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
   theForm = self.document.forms[0];
   isLoaded = true;
}

function modifyCurrentAssignments() {
   self.document.location = JSPRootURL + "/servlet/AssignmentController/CurrentAssignments/Update?module=<%=Module.RESOURCE%>&action=<%=Action.MODIFY%>";
}

function reset() { 
    var returnTo = escape("<%=request.getParameter("returnTo")%>");
    var url = JSPRootURL + "/resource/ListTimesheets.jsp?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>";
    url += "&personID=<%=SessionManager.getUser().getID()%>"
    url += (returnTo != "" ? "&returnTo=" + returnTo : "");
    self.document.location = url; 
}

function popupHelp(page) {
	var helplocation=JSPRootURL + "/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}

function help(page) {
    openwin_help(JSPRootURL + "/help/Help.jsp?page=timesheet_list");
}
</script>

</head>
<body onLoad="setup();" class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.resource.timesheet.label">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page displayToken="@prm.resource.timesheet.list.pagetitle"
                    jspPage='<%=SessionManager.getJSPRootURL() + "/resource/ListTimesheets.jsp" %>'
                    queryString='<%="module="+Module.RESOURCE+"&action="+Action.VIEW%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>
<form>
    <input type="hidden" name="theAction">

<channel:channel name="listtimesheet" customizable="false">
    <channel:insert name="newInformation" row="1" column="1" minimizable="false" titleToken="prm.resource.timesheet.list.pagetitle">

        <% if (timesheets.size() == 0) { %>
            <tr>
                <td width="80%" class="tableContent" align="center"><display:get name="prm.resource.timesheet.list.notimesheetmatch"/></td> 
            </tr>
        <% } else { %>

        <table border="0">
            <tr class="tableHeader">
                <td width="10%">&nbsp;</td>
                <td width="20%" align="center"><display:get name="prm.resource.timesheet.create.startdate.label"/></td>
                <td width="20%" align="center"><display:get name="prm.resource.timesheet.create.enddate.label"/></td>
                <td width="20%" align="center"><display:get name="prm.resource.assignments.update.columns.workreported"/></td>
                <td width="20%" align="center"><display:get name="prm.resource.timesheet.create.status.label"/></td>
                <td width="10%">&nbsp;</td>
            </tr>
            <tr><td colspan="6" class="headerSep"></td></tr>
            <%
                for (Iterator it = timesheets.iterator(); it.hasNext();) {
                    Timesheet timesheet = (Timesheet) it.next();
            %>
            <tr class="tableContent">
                <td align="center"></td>
                <td align="center"><a href="<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentController/CurrentAssignments/CreateTimesheet?module=" + Module.RESOURCE + "&action=" + Action.MODIFY + "&timesheetId=" + timesheet.getID() + "&personID=" + SessionManager.getUser().getID() + "&startDate=" + timesheet.getStartDate().getTime() + "&returnTo=" + URLEncoder.encode("/resource/ListTimesheets.jsp?module=" + Module.PERSONAL_SPACE + "&action=" + Action.VIEW, "UTF-8")%>"><%=timesheet.getStartDateString()%></a></td>
                <td align="center"><%=timesheet.getEndDateString()%></td>
                <td align="center"><%=timesheet.getWork().toShortString(0, 2)%></td>
                <td align="center"><%=timesheet.getTimesheetStatus().getDisplayName()%></td>
                <td align="center"></td>
             </tr>
            <% if (it.hasNext()) { %>
            <tr><td colspan="6" class="rowSep"></td></tr>
            <%
                    }
                }
            %>
        </table>
        <% } %>
    </channel:insert>
</channel:channel>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

