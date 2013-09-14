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

<%@ page contentType="text/html; charset=UTF-8" info="Personal Salary" language="java" errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider,
            net.project.util.NumberFormat,
            net.project.gui.toolbar.Button,
			net.project.gui.toolbar.ButtonType,
            net.project.base.Module,
            net.project.resource.PersonSalaryList,
			net.project.hibernate.service.ServiceFactory,
			net.project.gui.calendar.CalendarPopup,
			net.project.space.Space"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="ownerUser" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personSalaryList" class="net.project.resource.PersonSalaryList" scope="session" />

<security:verifyAccess action="view" module="<%= Module.SALARY %>" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:getSpaceJS  />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<% 	
	String mode = request.getParameter("mode");
	ownerUser.setID(request.getParameter("user"));
	ownerUser.load();
	
	// Mode "search": Don't refresh the personSalaryList if we are returning search results because the roster was reloaded in the processing	
	if(mode == null || mode.equals("edit"))
	{
		request.setAttribute("searchKeyFrom", "");
		request.setAttribute("searchKeyTo", "");
		
		personSalaryList.clear();		
		personSalaryList.setUser(new User(ownerUser.getID()));
		personSalaryList.load();
	}
%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';
	var userDateFormatString = '<%= ownerUser.getDateFormatter().getDateFormatExample() %>';  	
	
	function setup() {
		theForm = self.document.forms[0];
		isLoaded = true;
	}

	function create(){
		var theLocation = JSPRootURL + "/salary/CreateSalary.jsp?module=<%=Module.SALARY%>" + "&action=<%=Action.CREATE%>" + "&user=<%=ownerUser.getID()%>";
        var link_win = openwin_linker(theLocation);
        link_win.focus();
	}

	function modify(){
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
		{ 
			var theLocation = JSPRootURL+"/salary/ModifySalary.jsp?id=" + getSelection(theForm) + "&module=<%=Module.SALARY%>&action=<%=Action.MODIFY%>" + "&user=<%=ownerUser.getID()%>";
	        var link_win = openwin_linker(theLocation);
	        link_win.focus();			
		}
	}

	function searchButton()
	{
		if(validate())
		{
			theAction("search");			
			theForm.module.value = '<%= net.project.base.Module.SALARY %>';
			theForm.action.value = '<%= Action.VIEW %>';
			theForm.submit();
		}
	}

	function validate()
	{
		var dateFormat = new DateFormat(userDateFormatString);
		if(!dateFormat.checkValidDate(theForm.searchFieldFrom))
		{
			errorHandler(theForm.searchFieldFrom, '<display:get name="prm.personal.salary.roster.searchfieldfromincorrectformat.message" />');
			return false;
		}
		
		if(!dateFormat.checkValidDate(theForm.searchFieldTo))
		{
			errorHandler(theForm.searchFieldTo, '<display:get name="prm.personal.salary.roster.searchfieldtoincorrectformat.message" />');
			return false;
		}
		
		if(!dateFormat.checkValidDate(theForm.searchFieldFrom) &&
		   !dateFormat.checkValidDate(theForm.searchFieldTo) &&
		   !isdateStartBeforeEnd(theForm.searchFieldFrom.value, theForm.searchFieldTo.value))
		{
			errorHandler(theForm.searchFieldFrom, '<display:get name="prm.personal.salary.roster.searchdatesincorrectrange.message" />');
			return false;
		}		
				
		return true;
	}	
	
	function help()
	{
			var helplocation = JSPRootURL + "/help/Help.jsp?page=personal_salary";
			openwin_help(helplocation);
	}
</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />
	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.salary.title" 
				subTitle="<%= ownerUser.getDisplayName() %>" >
		<tb:band name="standard">
			<tb:button type="create" label='<%= PropertyProvider.get("prm.personal.salary.create.button.tooltip")%>' />
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.personal.salary.modify.button.tooltip")%>' />
		</tb:band>
	</tb:toolbar>
	
<div id='content' style="padding-top:20px;width:40%">

	<tab:tabStrip tabPresentation="true">
		<tab:tab label='<%=PropertyProvider.get("prm.personal.salary.tab.salaryhistory.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/PersonalSalary.jsp?module=" + Module.SALARY + "&user=" + ownerUser.getID()%>' selected="true" />
		<%if(SessionManager.getUser().getCurrentSpace().getType().equals(Space.FINANCIAL_SPACE)){%>
			<tab:tab label='<%=PropertyProvider.get("prm.financial.salary.tab.contactinfo.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/ContactInfo.jsp?module=" + Module.SALARY%>' />
		<%}%>
	</tab:tabStrip>
	
	<div class="UMTableBorder marginLeftFix">
	<form method="post" action="<%=SessionManager.getJSPRootURL()%>/salary/PersonalSalaryProcessing.jsp">
		<input type="hidden" name="theAction">
		<input type="hidden" name="module" value="<%=Module.SALARY%>">
	    <input type="hidden" name="action" value="<%=Action.VIEW%>">
	    
		<label for="searchFieldFrom" class="labelSearchField"><%=PropertyProvider.get("prm.personal.salary.roster.searchFrom.label")%></label>
		<input type="text" name="searchFieldFrom" id="searchFieldFrom" value="<%=request.getAttribute("searchKeyFrom")%>" size="08" maxlength="08" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">
		<%=CalendarPopup.getCalendarPopupHTML("searchFieldFrom", null)%>
		
		<label for="searchFieldTo" class="labelSearchField"><%=PropertyProvider.get("prm.personal.salary.roster.searchTo.label")%></label>
		<input type="text" name="searchFieldTo" id="searchFieldTo" value="<%=request.getAttribute("searchKeyTo")%>" size="08" maxlength="08" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">
		<%=CalendarPopup.getCalendarPopupHTML("searchFieldTo", null)%>
		
		<div class="channelHeader channelHeaderTabSet">
			<p><%=PropertyProvider.get("prm.personal.salary.tab.periods.title")%></p>
		</div>
		<div>
			<pnet-xml:transform scope="session" stylesheet="/salary/xsl/personalSalary.xsl" content="<%=personSalaryList.getXML()%>" />
		</div>
	</form>
	</div>
</div>
	<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>