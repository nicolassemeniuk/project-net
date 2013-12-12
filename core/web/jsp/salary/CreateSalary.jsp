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

<%@page import="net.project.hibernate.model.PnPersonSalary"%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Create Salary"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
    		net.project.space.SpaceFactory,
            net.project.base.property.PropertyProvider,
            net.project.security.User,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.space.Space,
			net.project.resource.PersonSalaryBean,            
            net.project.util.DateFormat,
            java.util.Date,
            net.project.gui.calendar.CalendarPopup,
			net.project.hibernate.service.ServiceFactory,
			net.project.hibernate.model.PnPersonSalary,
			net.project.util.DateUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="ownerUser" class="net.project.security.User" scope="session" />
<jsp:useBean id="personSalaryBean" class="net.project.resource.PersonSalaryBean" scope="session" />

<security:verifyAccess action="create" module="<%= Module.SALARY %>" />

<%
	// The new start date cannot be previous to the last start date plus one day
	PnPersonSalary lastSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getLastPersonSalaryByPersonId(Integer.valueOf(ownerUser.getID()));
	String minStartDate = ownerUser.getDateFormatter().formatDate(DateUtils.addDay(lastSalary.getStartDate(), 1));
%>

<template:getDoctype />
<html>
<head>
<title><%=PropertyProvider.get("prm.personal.salarycreatepage.title")%></title>

<template:getSpaceCSS/>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var userDateFormatString = '<%= ownerUser.getDateFormatter().getDateFormatExample() %>';    
    
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
	
	function submit()
	{
		if(validate())
		{
			theForm.module.value = '<%= net.project.base.Module.SALARY %>';
			theForm.action.value = '<%= Action.CREATE %>';
			theForm.submit();
		}
	}
	
	function validate()
	{
		if(!checkTextbox(theForm.startDate,'<display:get name="prm.personal.salary.create.startdaterequired.message" />'))
			return false;

		var dateFormat = new DateFormat(userDateFormatString);
		if(!dateFormat.checkValidDate(theForm.startDate))
		{
			errorHandler(theForm.startDate, '<display:get name="prm.personal.salary.create.startdateincorrectformat.message" />');
			return false;
		}
		
		if(isdateStartBeforeEnd(theForm.minStartDate.value, theForm.startDate.value))
		{
			errorHandler(theForm.startDate, '<display:get name="prm.personal.salary.create.startdateincorrectrange.message" />');
			return false;
		}		

		if(!checkTextbox(theForm.costByHour,'<display:get name="prm.personal.salary.create.salaryamountrequired.message" />'))
			return false;		
		
		if(!checkIsPositiveNumber(theForm.costByHour,'<display:get name="prm.personal.salary.create.salaryamountincorrect.message" />', false))
			return false;
				
		return true;
	}
	
	function help()
	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_salary&section=create";
		openwin_help(helplocation);
	}
</script>
</head>

<body class="main" onLoad="setup();">
	<div class="wizardTitle">
		<p class="pageTitle"><%= PropertyProvider.get("prm.personal.salary.create.pagetitle") %></p>
	</div>
	<form method="post" action="CreateSalaryProcessing.jsp">
		<input type="hidden" name="module">
		<input type="hidden" name="action">
		<input type="hidden" name="minStartDate" value="<%= minStartDate %>">		
		<table class="compactTable">
			<tr class="channelHeader">
				<td class="channelHeader" width="1%">
					<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif">
				</td>
				<td class="channelHeader"><%= PropertyProvider.get("prm.personal.salary.create.channel.costbyhour.title") %></td>
				<td class="channelHeader" width="1%">
					<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif">
				</td>
			</tr>
		</table>

		<p class="instructions wizardTitle"><%= PropertyProvider.get("prm.personal.salary.create.channel.costbyhour.warning") %></p>

		<label for="startDate" class="labelSearchField"><%= PropertyProvider.get("prm.personal.salary.create.channel.costbyhour.dateFrom.label") %></label>
		<input type="text" name="startDate" id="startDate" value="<%= ownerUser.getDateFormatter().formatDate(new Date())%>" size="08" maxlength="08" class="inputSearchField">
		<%= CalendarPopup.getCalendarPopupHTML("startDate", null) %>
		
		<label for="endDate" class="labelSearchField"><%= PropertyProvider.get("prm.personal.salary.create.channel.costbyhour.dateTo.label") %></label>
		<input type="text" name="endDate" id="endDate" value="" size="08" maxlength="08" class="inputSearchField" disabled="disabled">
		<%= CalendarPopup.getCalendarPopupHTML("endDate", null) %>		
	
		<label for="costByHour" class="labelSearchField"><%= PropertyProvider.get("prm.personal.salary.create.channel.costbyhour.costbyhour.label") %></label>
		<input type="text" name="costByHour" id="costByHour" value="" size="08" maxlength="08" class="inputSearchField">
	
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
