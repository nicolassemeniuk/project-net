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
    info="Salary Directory" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
		    net.project.security.SessionManager,
		    net.project.security.Action,
		    net.project.space.Space,
			net.project.base.property.PropertyProvider,
		    net.project.resource.RosterBean,
		    net.project.portfolio.ProjectPortfolio,
		    net.project.base.Module,
            java.util.Date,
            net.project.base.ObjectType,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean"/>
<%
	String id = request.getParameter("id");
%>

<security:verifyAccess action="view" module="<%=net.project.base.Module.SALARY%>" objectID="<%=id%>" /> 

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/skypeCheck.js" />

<%
	// Don't refresh roster is we are returning search results.
	String mode = request.getParameter("mode");
	if ((mode == null) || ((mode != null) && !mode.equals("search")) )
	{
		request.setAttribute("searchKey", "");
		
		Space financialSpace = user.getCurrentSpace();
		roster.setSpace(user.getCurrentSpace());
		
		//Obtain the portfolio of projects
		businessSpace.setID(financialSpace.getRelatedSpaceID());
		businessSpace.load();
		ProjectPortfolio projectPortfolio = new ProjectPortfolio();
		projectPortfolio.clear();
		projectPortfolio.setID(businessSpace.getProjectPortfolioID("owner"));
		projectPortfolio.setProjectMembersOnly(true);
		projectPortfolio.load();
			
		roster.loadForSpaces(projectPortfolio);
	    // also, every time we come to this page (when not returning search results), reload the space's instance of the roster also.
		// It might be appropriate to simply make the directory module act on the space's roster instance, but at this time the impact
	    // of such a move has not been analyzed.  (PCD: 6/15/2002)
		user.getCurrentSpace().getRoster().reload();
	}
%>

<script language="javascript">
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var theForm;
	var page = false;
	var isLoaded = false;
	
	function setup()
	{
		page = true;
		load_menu('<%=user.getCurrentSpace().getID()%>');
		theForm = self.document.forms[0];
		isLoaded = true;
	}
	
	function help()
	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=salary&section=participants";
		openwin_help(helplocation);
	}
	
	function create()
	{
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
		{		
			var theLocation = JSPRootURL + "/salary/CreateSalary.jsp?module=<%=Module.SALARY%>" + "&action=<%=Action.CREATE%>" + "&user=" + getSelection(theForm);
        	var link_win = openwin_linker(theLocation);
        	link_win.focus();
		}
	}

	function modify()
	{
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
		{
			var theLocation = JSPRootURL+"/salary/ModifySalary.jsp?user=" + getSelection(theForm) + "&module=<%=Module.SALARY%>&action=<%=Action.MODIFY%>";
	        var link_win = openwin_linker(theLocation);
	        link_win.focus();			
		}
	}	
	
	function search(key)
	{
		theForm.key.value = key;
		theForm.action.value = '<%=Action.VIEW%>';
		searchButton();
	}
	
	function searchButton()
	{
		theAction("search");
		theForm.action.value = '<%=Action.VIEW%>';
		theForm.submit();
	}
</script>
</head>
<body class="main" id='bodyWithFixedAreasSupport' style="overflow-x:hidden" onLoad="setup();">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />
		<tb:toolbar style="tooltitle" showAll="true" subTitle="<%=SessionManager.getUser().getCurrentSpace().getName() %>" groupTitle='<%=PropertyProvider.get("prm.financial.salary.title")%>'>
			<tb:band name="standard">
				<tb:button type="create" label='<%= PropertyProvider.get("prm.financial.salary.create.button.tooltip")%>' />
				<tb:button type="modify" label='<%= PropertyProvider.get("prm.financial.salary.modify.button.tooltip")%>' />				
			</tb:band>
	</tb:toolbar>
	<div id='content' style="padding-right:10px;padding-top:20px;">
	
		<tab:tabStrip tabPresentation="true">
			<tab:tab label='<%=PropertyProvider.get("prm.financial.salary.tab.salary.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/SalaryDirectory.jsp?module=" + Module.SALARY%>' selected="true" />
		</tab:tabStrip>
		
		<div class="UMTableBorder marginLeftFix">
		<form method="post" action="<%=SessionManager.getJSPRootURL()%>/salary/SalaryDirectoryProcessing.jsp">
			<input type="hidden" name="theAction">
			<input type="hidden" name="module" value="<%=Module.SALARY%>">
		    <input type="hidden" name="action" value="<%=Action.VIEW%>">
			<label for="searchField" class="labelSearchField"><%=PropertyProvider.get("prm.financial.salary.roster.search.label")%></label>
			<input type="text" name="key" id="searchField" value="<%=request.getAttribute("searchKey")%>" size="40" maxlength="40" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">
			<div class="channelHeader channelHeaderTabSet">
				<p><%=PropertyProvider.get("prm.financial.salary.tab.participants.title")%></p>
			</div>
			<div>	    
	        	<pnet-xml:transform name="roster" scope="session" stylesheet="/salary/xsl/salaryDirectory.xsl">
		            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" /> 
			    </pnet-xml:transform> 
			</div>
		</form>
		</div>
	</div>
	
	<%@ include file="/help/include_outside/footer.jsp" %>
	
	<template:getSpaceJS />
	<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>
