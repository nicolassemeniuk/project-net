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

<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="New Project Wizard -- page 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
    net.project.security.*,
    net.project.security.User,net.project.space.SpaceURLFactory,
    net.project.space.Space"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectWizard" class="net.project.project.ProjectWizard" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	String mySpace=user.getCurrentSpace().getType();
	int module = -1;
	if (mySpace.equals(Space.PERSONAL_SPACE)) module = net.project.base.Module.PERSONAL_SPACE;
	if (mySpace.equals(Space.BUSINESS_SPACE)) module = net.project.base.Module.BUSINESS_SPACE;
	if (mySpace.equals(Space.PROJECT_SPACE)) module = net.project.base.Module.PROJECT_SPACE;
	String verifyAction = null;
	int action = securityProvider.getCheckedActionID();
	if (action == net.project.security.Action.VIEW) verifyAction="view";
	if (action == net.project.security.Action.CREATE) verifyAction="create";
%>
<security:verifyAccess action="<%=verifyAction%>"
					   module="<%=module%>" /> 

<%
	String parentSpaceID = request.getParameter("parent");
	String refLink = (String) pageContext.getAttribute("pnet_refLink" ,  pageContext.SESSION_SCOPE);
%>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
    var currentSpaceTypeForBlog = 'person';
    var currentSpaceIdForBlog = '<%= SessionManager.getUser().getID() %>';
    var formSubmitted = false;
    
function setup() {
 
	theForm = self.document.forms[0];
    	isLoaded = true;
}

function submit() {
	if (!formSubmitted) {
		formSubmitted = true;
	    theAction("submit");
		theForm.submit();
	}
}
function help() 
   {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=project_portfolio&section=create";
	openwin_help(helplocation);
   }

 
<%
 if ( refLink != null && !refLink.trim().equals("")){
%>
	function cancel() { self.document.location =   '<%= refLink %>'; }
<%
   } else {
%>
function cancel() { self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?module=<%= module %>&portfolio=true"; }
<%
   }
%>

function reset() { self.document.CREATEPROJ.reset(); }

function goBack() {self.document.location = JSPRootURL + "/project/NewProjectWizard2.jsp?module=<%= module %>&theAction=back";}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label" space="project" showSpaceDetails="false">
    <tb:setAttribute name="leftTitle">
        <display:get name="prm.project.create.wizard.title" />
    </tb:setAttribute>
    <tb:setAttribute name="rightTitle">
        <display:get name="prm.project.create.wizard.title.step3" />
    </tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="NewProjectWizard3Processing.jsp">
    <input type="hidden" name="module" value="<%=module %>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="parent" value='<%=request.getParameter("parentSpaceID") %>' >
		
    <table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
        <tr align="left" class="channelHeader">
            <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td nowrap colspan="2" class="channelHeader"><display:get name="prm.project.create.wizard.general.heading" /></td>
            <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
        </tr>

        <%-- Project Name --%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.name" /></td>
            <td class="tableContent"><c:out value="${projectWizard.name}"/></td>
            <td>&nbsp;</td>
        </tr>
		  
<%
	if ((projectWizard.getParentBusinessID() != null) && !projectWizard.getParentBusinessID().equals(""))
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.businessowner" /></td>
            <td class="tableContent"><c:out value="${projectWizard.parentBusinessName}"/></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
	else
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.businessowner" /></td>
            <td class="tableContent"><display:get name="prm.project.create.wizard.businessowner.none" /></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
%>

<%
	if ((projectWizard.getParentProjectID() != null) && !projectWizard.getParentProjectID().equals(""))
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.subprojectof" /></td>
            <td class="tableContent"><c:out value="${projectWizard.parentProjectName}"/></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
	else
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.subprojectof" /></td>
            <td class="tableContent"><display:get name="prm.project.create.wizard.subprojectof.none" /></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
%>

		  
<%
	if ((projectWizard.getMethodologyID() != null) && !projectWizard.getMethodologyID().equals(""))
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.methodology" /></td>
            <td class="tableContent"><c:out value="${projectWizard.methodologyName}"/></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
	else
	{
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.methodology" /></td>
            <td class="tableContent"><display:get name="prm.project.create.wizard.methodology.none" /></td>
            <td>&nbsp;</td>
        </tr>
<%
	}
%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.visibility.displaylabel" /></td>
            <td class="tableContent"><%=projectWizard.getVisibility().getName()%></td>
            <td>&nbsp;</td>
        </tr>

        <tr><td class="tableContent" colspan="4">&nbsp;</td></tr>

        <tr align="left" class="channelHeader">
            <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td nowrap colspan="2" class="channelHeader"><strong><display:get name="prm.project.create.wizard.properties.heading" /></strong></td>
            <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
        </tr>

        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.description" /></td>
            <td class="tableContent"><c:out value="${projectWizard.description}"/> </td>
            <td>&nbsp;</td>
        </tr>

<%-- Meta: Project ID --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.projectid" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("ExternalProjectID")) %></td>
			<td>&nbsp;</td>
		</tr>
<%-- Meta: Project Manager --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.projectmanager" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("ProjectManager")) %></td>
			<td>&nbsp;</td>
		</tr>
<%-- Meta: Program Manager --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.programmanager" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("ProgramManager")) %></td>
			<td>&nbsp;</td>
		</tr>
<%-- Meta: Initiative --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.initiative" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("Initiative")) %></td>
			<td>&nbsp;</td>
		</tr>
<%-- Meta: Functional Area --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.functionalarea" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("FunctionalArea")) %></td>
			<td>&nbsp;</td>
		</tr>
<%-- Meta: Project Charter --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.projectcharter" /></td>
			<td class="tableContent"><%= net.project.util.HTMLUtils.escape(projectWizard.getMetaData().getProperty("ProjectCharter")) %></td>
			<td>&nbsp;</td>
		</tr>


		<tr align="left">
            <td>&nbsp;</td>
            <td class="tableContent"><c:out value="${projectWizard.status}"/></td>
       <%-- bfd-3249 : Not displaying Project status on Project Workspace page,which is used to verify the entered data.--%>     
            <td class="tableContent"><%= ProjectStatus.findByID(projectWizard.getStatusID()).getName() %></td>
            <td>&nbsp;</td>
        </tr>

<%-- Overall Improvement --%>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.improvement.label" /></td>
            <td class="tableContent">
                <% if (projectWizard.getImprovementCode() != null) { %>
                    <%=projectWizard.getImprovementCode().getHtmlImagePresentation(projectWizard.getColorCode())%>
                <% } else { %>
                    &nbsp;
                <% } %>
            </td>
            <td>&nbsp;</td>
        </tr>
        <tr align="left">
            <td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.completionpercent" /></td>
			<td class="tableContent"><c:out value="${projectWizard.percentComplete}"/></td>
            <td>&nbsp;</td>
        </tr>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.defaultcurrency" /></td>
            <td class="tableContent"><%=net.project.util.Currency.getFullDisplayName(projectWizard.getDefaultCurrency())%></td>
            <td>&nbsp;</td>
        </tr>
<%-- Meta: Type of Expense --%>
		<tr align="left">
			<td>&nbsp;</td>
			<td class="tableHeader"><display:get name="prm.project.create.wizard.meta.typeofexpense" /></td>
			<td class="tableContent"><%= projectWizard.getMetaData().getProperty("TypeOfExpense") %></td>
			<td>&nbsp;</td>
		</tr>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.startdate" /></td>
            <td class="tableContent"><c:out value="${projectWizard.startDateString}"/></td>
            <td>&nbsp;</td>
        </tr>
        <tr align="left">
            <td>&nbsp;</td>
            <td class="tableHeader"><display:get name="prm.project.create.wizard.enddate" /></td>
            <td class="tableContent"><c:out value="${projectWizard.endDateString}"/></td>
            <td>&nbsp;</td>
        </tr>
    </table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="finish" function="javascript:submit();" />
	</tb:band>
</tb:toolbar>    
    
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
