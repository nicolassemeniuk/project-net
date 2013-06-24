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
    info="Modify Salary"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
    		net.project.space.SpaceFactory,
            net.project.base.property.PropertyProvider,
            net.project.security.User,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.space.Space,
            net.project.util.DateFormat,
            java.util.Date,
            net.project.gui.calendar.CalendarPopup"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<template:getDoctype />
<html>
<head>
<title><%=PropertyProvider.get("prm.personal.salarymodifypage.title")%></title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="modify" module="<%=Module.SALARY%>" />

<template:getSpaceCSS space="personal"/>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup()
{
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel()
{
	self.close();
}

/*
function finish()
{
	if(verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		theForm.module.value='<%=net.project.base.Module.SALARY%>';
		theForm.action.value ='<%=Action.MODIFY%>';
		theForm.submit();
	} 
}
*/

function submit()
{
	console.log("submit!!");
}
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_salary&section=modify";
	openwin_help(helplocation);
}
</script>
</head>

<body class="main" onLoad="setup();" style="background: none;">
	<div class="wizardTitle">
		<p class="pageTitle">'<%=PropertyProvider.get("prm.personal.salary.modify.pagetitle")%>'</p>
	</div>
	<form method="post" action="ModifySalaryProcessing.jsp">
		<input type="hidden" name="module">
		<input type="hidden" name="action">
		<table class="compactTable">
			<tr class="channelHeader">
				<td class="channelHeader" width="1%">
					<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif">
				</td>
				<td class="channelHeader"><%=PropertyProvider.get("prm.personal.salary.modify.channel.dateRange")%></td>
				<td class="channelHeader" width="5%">
					<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif">
				</td>
			</tr>
		</table>

		<label for="searchFieldFrom" class="labelSearchField"><%=PropertyProvider.get("prm.personal.salary.roster.searchFrom.label")%></label>
		<input type="text" name="searchFieldFrom" id="searchFieldFrom" value="<%=SessionManager.getUser().getDateFormatter().formatDate(new Date())%>" size="08" maxlength="08" class="inputSearchField">
		<%=CalendarPopup.getCalendarPopupHTML("searchFieldFrom", null)%>		
	
		<!-- bottomFixed="true" -->
		<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
				<tb:button type="next" function="javascript:submit();" />
				<tb:button type="cancel" />		
			</tb:band>
		</tb:toolbar>
	</form>

<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
