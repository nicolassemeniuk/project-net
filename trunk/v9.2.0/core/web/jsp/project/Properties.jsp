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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 20624 $
|       $Date: 2010-03-29 04:31:13 -0300 (lun, 29 mar 2010) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Project Properties"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
            net.project.business.BusinessSpaceBean,
            net.project.base.property.PropertyProvider,
            net.project.security.*,
            net.project.document.*,
            net.project.util.NumberFormat,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
    String id = request.getParameter("id");
    String selected = request.getParameter("selected");

%>
<security:verifyAccess action="view"
                       module="<%=net.project.base.Module.PROJECT_SPACE%>"
                       objectID="<%=id%>" />
<%
    if ( selected != null && !selected.trim().equals("")) {
        id = selected ;
    }
    if ( id != null && !id.trim().equals("")) {
        projectSpace.setID(id);
    } else {
        projectSpace.setID( user.getCurrentSpace().getID() );
    }
    projectSpace.load();
    projectSpace.setUser (user);

    docManager.setUser(user);
    String baseUrl = SessionManager.getJSPRootURL();

    String ownerBusiness = null;
    String templateApplied = null;
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var t_standard;
    var theForm;
    var page = false;
    var isLoaded = false;

function setup() {
    page=true;
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
    this.focus();
}

function cancel() { self.document.location = "<%= SessionManager.getJSPRootURL() %>/project/Dashboard?module=<%= net.project.base.Module.PROJECT_SPACE %>"; }
function reset() { self.document.location = "<%= SessionManager.getJSPRootURL() %>/project/Properties.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>"; }
function modify() { self.document.location = "<%= SessionManager.getJSPRootURL() %>/project/PropertiesEdit.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>"
                     + "&action=<%=net.project.security.Action.MODIFY%>&referer=project/Properties.jsp"; }
function configuration() { self.document.location = "<%= SessionManager.getJSPRootURL() %>/project/PropertiesInformation.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>"; }
function general() { self.document.location = "<%= SessionManager.getJSPRootURL() %>/project/Properties.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>"; }

function help() {
    var helplocation="<%=baseUrl%>/help/Help.jsp?page=project_info&section=general";
    openwin_help(helplocation);
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label">
    <tb:setAttribute name="leftTitle">
        <history:history>
        <% if(StringUtils.isNotEmpty(request.getParameter("selected"))){%>
            <history:module display='<%= projectSpace.getName() %>' 
                            jspPage='<%=SessionManager.getJSPRootURL() + "/project/Dashboard"%>'
                            queryString='<%="id="+ projectSpace.getID()%>'/>
            <history:page display='<%= PropertyProvider.get("prm.project.properties.module.history")%>'
                          jspPage='<%=baseUrl + "/project/Properties.jsp"%>'
                          queryString='<%="module="+net.project.base.Module.PROJECT_SPACE%>' />
          <%} else {%>
          	<history:module display='<%= PropertyProvider.get("prm.project.properties.module.history")%>'
          			jspPage='<%=baseUrl + "/project/Properties.jsp"%>'
                    queryString='<%="module="+net.project.base.Module.PROJECT_SPACE%>' />  
          <% }%>                
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/project/ProjectEditProcessing.jsp" >
    <input type="hidden" name="module" value="<%= net.project.base.Module.PROJECT_SPACE %>">
    <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
    <input type="hidden" name="theAction">

<table border="0" width="100%" cellpadding="0" cellspacing="0">

<%------------------------------------------------------------------------------
  General
------------------------------------------------------------------------------%>
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.properties.channel.description.title" /></nobr></td>
    <td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<%-- Support Code --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.supportcode.label" /></td>
    <td class="tableContent">
        <%= projectSpace.getID() %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Project Name --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.projectname.label" /></td>
    <td class="tableContent">
         <c:out value="${projectSpace.name}"/>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Brief Description --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.description.label" /></td>
    <td class="tableContent">
		<output:text><%= projectSpace.getDescription() == null ? "" : projectSpace.getDescription() %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Project ID --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.projectid.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("ExternalProjectID") == null ? "" : projectSpace.getMetaData().getProperty("ExternalProjectID") %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Business Space --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.businessspace.label" /></td>
    <td class="tableContent">
        <%
        if ((projectSpace.getParentBusinessID() != null) && (!projectSpace.getParentBusinessID().equals(""))) {
            businessSpace.setID(projectSpace.getParentBusinessID());
            businessSpace.load();
            ownerBusiness = businessSpace.getName();
        } else {
            ownerBusiness = "None";
        }
        %>
        <%= ownerBusiness %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Template Used --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.templateapplied.label" /></td>
    <td class="tableContent">
        <%
        if ((projectSpace.getTemplateApplied() != null) && (!projectSpace.getTemplateApplied().equals(""))) {
            templateApplied = projectSpace.getTemplateApplied();
        } else {
        	templateApplied = "None";
        }
        %>
        <%= templateApplied %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Project Logo --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.projectlogo.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getProjectLogoID() != null) { %>
           <IMG SRC="<%=SessionManager.getJSPRootURL()%>/servlet/ViewDocument?id=<%=projectSpace.getProjectLogoID()%>&module=<%=net.project.base.Module.DOCUMENT%>" width=40 height=40 border=0>
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Project Visibility --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.visibility.label" /></td>
    <td class="tableContent"><%=projectSpace.getVisibility().getName()%></td>
    <td>&nbsp;</td>
</tr>
<%-- Project Sponsor --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.sponsor.label" /></td>
    <td class="tableContent">
        <c:out value="${projectSpace.sponsor}" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Project Manager --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.projectmanager.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("ProjectManager") == null ? "" : projectSpace.getMetaData().getProperty("ProjectManager")  %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Program Manager --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.programmanager.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("ProgramManager") == null ? "" : projectSpace.getMetaData().getProperty("ProgramManager") %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Initiative --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.initiative.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("Initiative") == null ? "" : projectSpace.getMetaData().getProperty("Initiative")  %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Functional Area --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.functionalarea.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("FunctionalArea") %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Priority --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.priority.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getPriorityCode() != null) { %>
            <%=projectSpace.getPriorityCode().getName()%>
        <% } else { %>
            &nbsp;
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Risk Rating --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.risk.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getRiskRatingCode() != null) { %>
            <%=projectSpace.getRiskRatingCode().getName()%>
        <% } else { %>
            &nbsp;
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Project Charter --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.projectcharter.label" /></td>
    <td class="tableContent">
        <output:text><%= projectSpace.getMetaData().getProperty("ProjectCharter") == null ? "" : projectSpace.getMetaData().getProperty("ProjectCharter") %></output:text>
    </td>
    <td>&nbsp;</td>
</tr>

<tr align="left"><td colspan="4" class="tableHeader">&nbsp;</td></tr>

<%------------------------------------------------------------------------------
  Project Status
------------------------------------------------------------------------------%>
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.properties.channel.status.title" /></nobr></td>
    <td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<%-- Start Date --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.startdate.label" /></td>
    <td class="tableContent">
        <jsp:getProperty name="projectSpace" property="startDateString" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- End Date --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.enddate.label" /></td>
    <td class="tableContent">
        <jsp:getProperty name="projectSpace" property="endDateString" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Overall Completion --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.overallcompletion.label" /></td>
    <td class="tableContent">
<%
    	NumberFormat formatter = NumberFormat.getInstance();
	    String formattedPercentComplete = formatter.formatNumber(Double.parseDouble(projectSpace.getPercentComplete()), 0, 0);
%>
		<%=formattedPercentComplete%>%
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Overall Status --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.status.label" /></td>
    <td class="tableContent">
        <%= projectSpace.getStatus() %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Overall Improvement --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.statusdisplay.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getImprovementCode() != null) { %>
            <%=projectSpace.getImprovementCode().getHtmlImagePresentation(projectSpace.getColorCode())%>
        <% } else { %>
            &nbsp;
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Financial Status --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.financialstatus.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getFinancialStatusImprovementCode() != null &&
                projectSpace.getFinancialStatusColorCode() != null) { %>
               <%=projectSpace.getFinancialStatusImprovementCode().getHtmlImagePresentation(projectSpace.getFinancialStatusColorCode())%></td>
        <% } else { %>
            &nbsp;
        <% } %>
    <td>&nbsp;</td>
</tr>
<%-- Schedule Status --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.schedulestatus.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getScheduleStatusImprovementCode() != null &&
                projectSpace.getScheduleStatusColorCode() != null) { %>
            <%=projectSpace.getScheduleStatusImprovementCode().getHtmlImagePresentation(projectSpace.getScheduleStatusColorCode())%>
        <% } else { %>
            &nbsp;
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Resource Status --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.resourcestatus.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getResourceStatusImprovementCode() != null &&
                projectSpace.getResourceStatusColorCode() != null) { %>
            <%=projectSpace.getResourceStatusImprovementCode().getHtmlImagePresentation(projectSpace.getResourceStatusColorCode())%>
        <% } else { %>
            &nbsp;
        <% } %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Current Status Description --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2"><display:get name="prm.project.properties.currentstatusdescription.label" /></td>
    <td>&nbsp;</td>
</tr>
<tr align="left">
    <td>&nbsp;</td>
    <td class="tableContent" colspan="2">
    	<c:out value="${projectSpace.currentStatusDescription}" />
    </td>
    <td>&nbsp;</td>
</tr>

<tr align="left"><td colspan="4" class="tableHeader">&nbsp;</td></tr>

<%------------------------------------------------------------------------------
  Financial
------------------------------------------------------------------------------%>
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
    <td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.properties.channel.financialstatus.title" /></nobr></td>
    <td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
<%-- Default Currency --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.defaultcurrency.label" /></td>
    <td class="tableContent">
        <%=net.project.util.Currency.getFullDisplayName(projectSpace.getDefaultCurrency())%>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Meta: Type of Expense --%>
<tr align="left" >
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.meta.typeofexpense.label" /></td>
    <td class="tableContent">
        <%= projectSpace.getMetaData().getProperty("TypeOfExpense") %>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Budgeted Total Cost --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.budgetedtotalcost.label" /></td>
    <td class="tableContent">
        <output:money money="<%=projectSpace.getBudgetedTotalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Current Estimated Total Cost --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.currentestimatedtotalcost.label" /></td>
    <td class="tableContent">
        <output:money money="<%=projectSpace.getCurrentEstimatedTotalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Actual Cost To Date --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.actualcosttodate.label" /></td>
    <td class="tableContent">
        <output:money money="<%=projectSpace.getActualCostToDate()%>" currency="<%=projectSpace.getDefaultCurrency()%>" />
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Estimated ROI --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.estimatedroi.label" /></td>
    <td class="tableContent">
        <% if (projectSpace.getEstimatedROI() != null && projectSpace.getEstimatedROI().getValue() != null)
			out.print(projectSpace.getEstimatedROI().getValue() + "%");
		%>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- Cost Center --%>
<tr align="left">
    <td>&nbsp;</td>
    <td class="fieldNonRequired"><display:get name="prm.project.properties.costcenter.label" /></td>
    <td class="tableContent">
        <c:out value="${projectSpace.costCenter}" />
    </td>
    <td>&nbsp;</td>
</tr>
<tr align="left"><td colspan="4" class="tableHeader">&nbsp;</td></tr>

<%------------------------------------------------------------------------------
  Other Properties
------------------------------------------------------------------------------%>
<%--
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
    <td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.properties.channel.other.title" /></nobr></td>
    <td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
--%>

</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	<tb:button type="cancel" label='<%= PropertyProvider.get("prm.project.propertiesedit.cancel.button.label")%>' />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
