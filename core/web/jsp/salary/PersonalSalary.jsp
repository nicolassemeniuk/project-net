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
			net.project.hibernate.service.ServiceFactory"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personSalaryList" class="net.project.resource.PersonSalaryList" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />

<%-- Import Javascript --%>
<template:getSpaceJS space="personal" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
	
	function setup() {
		theForm = self.document.forms[0];
		isLoaded = true;
	}

	function create(){
		/*
		var theLocation = JSPRootURL + "/salary/CreateSalary.jsp?module=<%=Module.SALARY%>"+"&action=<%=Action.CREATE%>";
		self.document.location = theLocation;
		*/
	}

	function modify(){
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
		{ 
			var theLocation = JSPRootURL+"/salary/ModifySalary.jsp?id=" + getSelection(theForm) + "&module=<%=Module.SALARY%>&action=<%=Action.MODIFY%>";
	        var link_win = openwin_linker(theLocation);
	        link_win.focus();			
		}
	}

	function search() { 
		self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=Module.SALARY%>&action=<%=Action.VIEW%>";
	}

	function help() {
			var helplocation = JSPRootURL + "/help/Help.jsp?page=personal_salary";
			openwin_help(helplocation);
	}
</script>
</head>

<% 	
// Don't refresh the Person Salary List is we are returning search results.
String mode = request.getParameter("mode");
if ( (mode == null) || ((mode != null) && !mode.equals("search")) ) {
	session.setAttribute("searchKey", "");	
	personSalaryList.setUser(user);
	personSalaryList.load();
}

%>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />
	<tb:toolbar style="tooltitle" showAll="true" groupTitle="Salary">
		<tb:setAttribute name="leftTitle">
			<history:history />
		</tb:setAttribute>
		<tb:band name="standard">
			<tb:button type="create" label='<%= PropertyProvider.get("prm.personal.salary.create.button.tooltip")%>' />
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.personal.salary.modify.button.tooltip")%>' />
		</tb:band>
	</tb:toolbar>
	
<div id='content' style="padding-top:20px;width:40%">

	<tab:tabStrip tabPresentation="true">
		<tab:tab label='<%=PropertyProvider.get("prm.personal.salary.tab.salaryhistory.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/PersonalSalary.jsp?module=" + Module.SALARY%>' selected="true" />
	</tab:tabStrip>
	
	<div class="UMTableBorder">
	<form method="post" action="<%=SessionManager.getJSPRootURL()%>/salary/PersonalSalaryProcessing.jsp">
		<input type="hidden" name="theAction">
		<input type="hidden" name="module" value="<%=Module.SALARY%>">
	    <input type="hidden" name="action" value="<%=Action.VIEW%>">
		<label for="searchFieldFrom" class="labelSearchField"><%=PropertyProvider.get("prm.personal.salary.roster.searchFrom.label")%></label>
		<input type="text" name="searchFieldFrom" id="searchFieldFrom" value="<%=session.getAttribute("searchKeyFrom")%>" size="10" maxlength="10" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">
		<label for="searchFieldTo" class="labelSearchField"><%=PropertyProvider.get("prm.personal.salary.roster.searchTo.label")%></label>
		<input type="text" name="searchFieldTo" id="searchFieldTo" value="<%=session.getAttribute("searchKeyTo")%>" size="10" maxlength="10" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">		
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