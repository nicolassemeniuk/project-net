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
    info="Project Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.project.ProjectSpaceBean,
				net.project.project.ProjectSpace,
				net.project.security.User, 
				net.project.security.SecurityProvider,
				net.project.security.SessionManager,
				net.project.base.Module,
				net.project.channel.ChannelManager,
				net.project.channel.Channel,
				net.project.document.DocumentManagerBean, 
				net.project.process.ProcessBean,
				net.project.space.Space,
				net.project.space.SpaceList,
				net.project.space.SpaceManager,
				net.project.base.property.PropertyProvider,
            java.net.URLDecoder"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<template:getDoctype />
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.model.project_space.ProjectSchedule"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="net.project.hibernate.service.IPnPersonService"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="net.project.calendar.PnCalendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<html>
<head>
<META http-equiv="expires" content="0"> 
<%
//bfd 3269 : Gantt view is sets to all projects when select gantt view in a particular project. 
session.removeAttribute("workspaceHierarchyView");
// This page will be accessed via external notifications.  We need to make sure that
// it is sent they are sent through the navigation frameset
if (request.getParameter("external") != null && request.getParameter("inframe") == null) {
    session.putValue("requestedPage", request.getRequestURI() + 
			"?id="+ request.getParameter("id") + 
			"&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
}
%>

<%--------------------------------------------------------------------------------------------------------
  -- Security Check                                                                                 
  ------------------------------------------------------------------------------------------------------%>
<%
    String id = request.getParameter("id");
	if (id != null) {
        // Security Check: Is user allowed access to requested space?
		projectSpace.setID(id);
   	    projectSpace.load();
	} 
	// id was null
	else {
    	if (projectSpace.getID() != null) {
        	projectSpace.load();
		}
    }
	
	request.setAttribute("id", "");
	request.setAttribute("module", Integer.toString(net.project.base.Module.PROJECT_SPACE));
%>

<%--------------------------------------------------------------------------------------------------------
  -- History
  ------------------------------------------------------------------------------------------------------%>
<%
	// Update navigation history
	String BusinessId=projectSpace.getParentBusinessID();
	if (BusinessId!=null) {
%>
		<%-- Set the business display --%>	
		<history:history displayHere="false">
			<history:business display="<%=projectSpace.getParentBusinessName()%>"
							  jspPage='<%=SessionManager.getJSPRootURL() + "/business/Main.jsp"%>'
							  queryString='<%="id="+BusinessId%>' />
		</history:history>
<%
	} else {
%>
		<%-- Turn off business display --%>
		<history:history displayHere="false">
			<history:business show="false" />
		</history:history>
<%
	}
%>
	<%-- Now add the project display --%>
	<history:history displayHere="false">
		<history:page display="Business"
							  jspPage='<%=SessionManager.getJSPRootURL() + "/business/BusinessPortfolio.jsp"%>' />
		<history:project display="<%=projectSpace.getName()%>"
				 jspPage='<%=SessionManager.getJSPRootURL() + "/project/Main.jsp" + "module=?" + net.project.base.Module.PROJECT_SPACE %>'
				 queryString='<%="module=" + net.project.base.Module.PROJECT_SPACE + "&id="+ projectSpace.getID()%>' />
	</history:history>
	

<%--------------------------------------------------------------------------------------------------------
  -- Configure Objects                                                                              
  ------------------------------------------------------------------------------------------------------%>
<%
	// Set user's current space to this Project
    user.setCurrentSpace(projectSpace);
	
	// Load team roster (directory) for this project.
	user.getCurrentSpace().getRoster().reload();
	
	// Load process for channel display
	m_process.clear();
	m_process.loadProcess(user.getCurrentSpace().getID());
	
    docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/project/Main.jsp"); 
	String forwardTo = request.getParameter("page");
	if (forwardTo != null) {
	    response.sendRedirect(URLDecoder.decode(forwardTo, SessionManager.getCharacterEncoding()));
	    return;
    }
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>

<template:import type="css" src="/styles/project_dashboard.css" />
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/menu/JSCookMenu.js" />
<template:import type="css" src="/src/menu/ThemeOffice/theme.css" />
<template:import type="javascript" src="/src/menu/ThemeOffice/theme.js" />

<script language="javascript">

<%

	Date actual = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");
	Calendar c = Calendar.getInstance();
	c.add(Calendar.DAY_OF_MONTH, 7);
	
%>

	var actualDate = "<%= sdf.format(actual) %>";
	var weekLaterDate = "<%= sdf.format(c.getTime()) %>";

	var projectMenu =
	[
	    ['', 'Dashboard', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', '',
	    	['', 'Dashboard (Beta)', '<%=SessionManager.getJSPRootURL() + "/project/Main2.jsp?module="+Module.PROJECT_SPACE%>', '', '']
	    ], _cmSplit
<%
		if(PropertyProvider.get("prm.project.contact.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.directory" />', '<%=SessionManager.getJSPRootURL() + "/project/DirectorySetup.jsp?module="+Module.DIRECTORY%>', '', ''], _cmSplit
<%
		}
	    if(PropertyProvider.get("prm.project.wiki.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.wiki" />', '<%=SessionManager.getJSPRootURL() + "/wiki/Welcome/"+user.getCurrentSpace().getID()+"/"+user.getID()+"?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.document.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.document" />', '<%=SessionManager.getJSPRootURL() + "/document/Main.jsp?module="+Module.DOCUMENT%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.discussion.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.discussion" />', '<%=SessionManager.getJSPRootURL() + "/discussion/Main.jsp?module="+Module.DISCUSSION%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.discussion.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.form" />', '<%=SessionManager.getJSPRootURL() + "/form/Main.jsp?module="+Module.FORM%>', '', '',
	    	['', 'Cool Assignable form', '<%=SessionManager.getJSPRootURL() + "/form/FormQuickAccess.jsp?module="+Module.FORM%>&id=<%= user.getCurrentSpace().getID() %>', '', '']
	    ], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.process.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.process" />', '<%=SessionManager.getJSPRootURL() + "/process/Main.jsp?module="+Module.PROCESS%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.scheduling.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.scheduling" />', '', '', '',
	    	['', '<display:get name="prm.project.nav.calendar" />', '<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp?module="+Module.CALENDAR%>', '', ''],
	    	['', '<display:get name="prm.project.nav.schedule" />', '<%=SessionManager.getJSPRootURL() + "/schedule/Main.jsp?module="+Module.SCHEDULE%>', '', '']
	    ], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.workflow.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.workflow" />', '<%=SessionManager.getJSPRootURL() + "/workflow/Main.jsp?module="+Module.WORKFLOW%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.news.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.news" />', '<%=SessionManager.getJSPRootURL() + "/news/Main.jsp?module="+Module.NEWS%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.subproject.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.subproject" />', '<%=SessionManager.getJSPRootURL() + "/project/subproject/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.reports.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.reports" />', '<%=SessionManager.getJSPRootURL() + "/report/Main.jsp?module="+Module.REPORT%>', '', ''], _cmSplit
<%
	    }
	    if(PropertyProvider.get("prm.project.setup.isenabled").equals("1")) {
%>
	    , ['', '<display:get name="prm.project.nav.setup" />', '<%=SessionManager.getJSPRootURL() + "/project/Setup.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit
<%
	    }
%>
	    , ['', 'Blog', '<%=SessionManager.getJSPRootURL()+"/blog/view/"+user.getCurrentSpace().getID()+"/"+user.getID()+"/project"+"/"+Module.PROJECT_SPACE+"?module="+Module.PROJECT_SPACE%>', '', '']
	];

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
   var spaceID = '<%=user.getCurrentSpace().getID()%>'; 
   load_menu(spaceID);
   load_header();
   theForm = self.document.forms[0];
   isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=project_summary";
	openwin_help(helplocation);
}
function cancel() { self.document.location = JSPRootURL + "/personal/Main.jsp"; }
function reset() { self.document.location = JSPRootURL + "/project/Main2.jsp"; }
function modify() { self.document.location = JSPRootURL + "/project/PropertiesEdit.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&referer=project/Main.jsp"; }
function search() { self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }
function properties() { self.document.location = JSPRootURL + "/project/Properties.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }

function viewPhase(id) {
   self.document.location = JSPRootURL + "/process/ViewPhase.jsp?id=" + id + "&module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>"; 
}

function showReport(type) {
	if(type == "tcdr") {
		document.getElementById("checkbox30").value = "true";
		document.getElementById("filter40Start").value = actualDate;
		document.getElementById("filter40Finish").value = weekLaterDate;
		document.getElementById("reportType").value = type;
	} else if(type == "unassigned") {
		document.getElementById("reportType").value = "str";
		document.getElementById("grouping").value = "20";
	} else if (type == "wcr") {
		document.getElementById("reportType").value = "str";
		document.getElementById("checkbox30").value = "true";
		document.getElementById("filter40comparator").value = "equals";
		document.getElementById("filter40").value = "100";
	} else {
		document.getElementById("reportType").value = type;
		document.getElementById("grouping").value = "10";
	}
	document.getElementById("reportParameters").submit();
}

function showTeammateTasksReport(tammate) {
	document.getElementById("reportType").value = "str";
	document.getElementById("checkbox10").value = "true";
	document.getElementById("filter20").value = tammate;
	document.getElementById("reportParameters").submit();
}

function showResourceAllocation(personID) {
<%
    PnCalendar calendar = new PnCalendar();
    long startOfMonth = calendar.startOfMonth(new Date()).getTime();
%>
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID + '&startDate=' + <%=startOfMonth%>;

    openwin_large('resource_allocation', url);
}
</script>
</head>
<body onLoad="setup();" class="main">
<template:getSpaceMainMenu />

<%--------------------------------------------------------------------------------------------------------
  -- Toolbar                                                                                         
  ------------------------------------------------------------------------------------------------------%>   
<div style="margin-top: 60px;">
	<div id="toolbar_top">
		<div id="toolbar_container">
			<div id="projectMenuId"></div>
		</div>
		<script type="text/javascript"><!--
			cmDraw ('projectMenuId', projectMenu, 'hbr', cmThemeOffice);
		--></script>
	</div>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
	<td colspan="2" align="right">
		<tb:toolbar style="tool" showAll="true">
			<tb:band name="standard">
				<tb:button type="modify" label='<%= PropertyProvider.get("prm.project.main.modify.button.tooltip")%>' />
				<tb:button type="properties" label='<%= PropertyProvider.get("prm.project.main.properties.button.tooltip")%>'/>
			</tb:band>
		</tb:toolbar>
	</td>
</tr>
</table>
</div>

<form name="reportParameters" id="reportParameters" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ShowReportServlet">
	<input type="hidden" name="reportType" id="reportType" />
	<input type="hidden" name="module" id="module" value="<%= Module.REPORT %>" />
	<input type="hidden" name="objectID" id="objectID" value="<%= projectSpace.getID() %>" />
	<input type="hidden" name="reportScope" id="reportScope" value="10" />
	<input type="hidden" name="filter10" id="filter10" value="20" />
	<input type="hidden" name="filter20" id="filter20" />
	<input type="hidden" name="grouping" id="grouping" value="10" />
	<input type="hidden" name="output" id="output" value="html" />
	<input type="hidden" name="checkbox30" id="checkbox30" />
	<input type="hidden" name="checkbox10" id="checkbox10" />
	<input type="hidden" name="filter40" id="filter40" />
	<input type="hidden" name="filter40Start" id="filter40Start" />
	<input type="hidden" name="filter40Finish" id="filter40Finish" />
	<input type="hidden" name="filter40comparator" id="filter40comparator" />
	<input type="hidden" name="showReportParameters" id="showReportParameters" value="false" />
	<input type="hidden" name="assignmentType" id="assignmentType" value="task" />
</form>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/project/MainProcessing.jsp">
<input type="hidden" name="theAction">

<%--------------------------------------------------------------------------------------------------------
  -- Dashboard Channels                                                                                   
  ------------------------------------------------------------------------------------------------------%>
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr valign="top">
			<td width="20%">
				<div id="left_section">
					<div style="padding: 5px;">
						<div id="project_channel">
							<div style="padding: 5px;">
								<div style="font-weight: bold; font-size: medium;">Project: <a href="javascript:modify();"><%= net.project.util.HTMLUtils.escape(projectSpace.getName()) %></a></div>
								<b>Description:</b> <%= net.project.util.HTMLUtils.escape(projectSpace.getDescription()) %><br />
								<b>Manager:</b> <%= projectSpace.getMetaData().getProperty("ProjectManager") %><br />
								<b>Status:</b> <%= net.project.util.HTMLUtils.escape(projectSpace.getStatus()) %>
								<br /><p />
								<%
									
									String percent = projectSpace.getPercentComplete();
									NumberFormat nf = NumberFormat.getNumberInstance();
									nf.setMaximumFractionDigits(2);
									percent = nf.format(Double.parseDouble(percent));
								
								%>
								<b>Completion</b> <%= percent %>%<br />
								<div style="width: 100px;">
									<table border="1" width="100" height="8" cellspacing="0" cellpadding="0">
										<tr>
											<td bgcolor="#FFFFFF" title="<%= percent %>%">
												<img src="<%= SessionManager.getJSPRootURL() %>/images/lgreen.gif" width="<%= percent %>" height="8"/>
											</td>
										</tr>
									</table>
								</div>
								<div id="project_separator"></div>
								<div>
									<table cellspacing="5" cellpadding="0" style="font-size: small;">
										<tr>
											<td><b>O</b></td>
											<td><b>F</b></td>
											<td><b>S</b></td>
											<td><b>R</b></td>
										</tr>
										<tr>
											<td>
											<%
												if(projectSpace.getImprovementCode() != null) {
											%>
												<img src="<%=SessionManager.getJSPRootURL() %>/<%= projectSpace.getImprovementCode().getImageURL(projectSpace.getColorCode()) %>" />
											<%
												}
											%>
											</td>
											<td>
											<%
												if(projectSpace.getFinancialStatusColorCode() != null) {
											%>
												<img src="<%=SessionManager.getJSPRootURL() %>/<%= projectSpace.getImprovementCode().getImageURL(projectSpace.getFinancialStatusColorCode()) %>" />
											<%
												}
											%>
											</td>
											<td>
											<%
												if(projectSpace.getScheduleStatusColorCode() != null) {
											%>
												<img src="<%=SessionManager.getJSPRootURL() %>/<%= projectSpace.getImprovementCode().getImageURL(projectSpace.getScheduleStatusColorCode()) %>" />
											<%
												}
											%>
											</td>
											<td>
											<%
												if(projectSpace.getResourceStatusColorCode() != null) {
											%>
												<img src="<%=SessionManager.getJSPRootURL() %>/<%= projectSpace.getImprovementCode().getImageURL(projectSpace.getResourceStatusColorCode()) %>" />
											<%
												}
											%>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</div>
						<div id="project_separator"></div>
						<div style="width: 100%;">
							<div style="padding: 5px;">
			<%
				// Initialize the channel manager
				ChannelManager manager = new ChannelManager(pageContext);
				manager.setCustomizable(false);
				Channel news = new Channel("ProjectSpace_News_" + projectSpace.getName());
				news.setTitle(PropertyProvider.get("prm.project.main.channel.projectnews.title"));
				news.setWidth("20%");
				news.setMinimizable(false);
				news.setCloseable(false);
				news.setInclude("/news/include/NewsChannelDashboard.jsp");
				news.addAttribute("filter_viewRange", "" + net.project.news.NewsManager.FILTER_PAST_TWO_WEEKS);
				manager.addChannel(news, 0, 0);
				
				Channel myMeetings = new Channel("ProjectSpace_Meetings_" + projectSpace.getName());
				myMeetings.setTitle(PropertyProvider.get("prm.project.main.channel.upcomingmeetings.title"));
				myMeetings.setWidth("100%");
				myMeetings.setMinimizable(false);
				myMeetings.setCloseable(false);
				myMeetings.setInclude("/project/include/upcomingMeetings.jsp");
				manager.addChannel(myMeetings, 1, 0);
				
				news = new Channel("ProjectSpace_Changes_" + projectSpace.getName());
				news.setTitle("Changes Within 7 Days");
				news.setWidth("20%");
				news.setMinimizable(false);
				news.setCloseable(false);
				news.setInclude("/project/include/lastChanges.jsp");
				news.addAttribute("filter_viewRange", "" + net.project.news.NewsManager.FILTER_PAST_TWO_WEEKS);
				manager.addChannel(news, 2, 0);
				
				manager.display();
			%>	
							</div>
						</div>
					</div>
				</div>
			</td>
			<td width="50%">
				<div id="middle_section">
					<div style="padding: 5px;">
						<div style="width: 100%;">
							<jsp:include flush="true" page="/project/include/projectSchedule.jsp"></jsp:include>
							<div id="project_separator"></div>
		<%
			manager = new ChannelManager(pageContext);
			manager.setCustomizable(false);
			
			news = new Channel("ProjectSpace_phases_" + projectSpace.getName());
			news.setTitle("Phases and Milestones");
			news.setWidth("100%");
			news.setMinimizable(false);
			news.setCloseable(false);
			news.setInclude("/process/include/phasesMilestones.jsp");
			manager.addChannel(news, 0, 0);
			
			Channel subprojects = new Channel("ProjectSpace_subprojects_" + projectSpace.getName());
			subprojects.setTitle(PropertyProvider.get("prm.project.main.channel.subprojects.title"));
			subprojects.setWidth("100%");
			subprojects.setMinimizable(false);
			subprojects.setCloseable(false);
			subprojects.setInclude("/project/include/subprojects_dashboard.jsp");
			manager.addChannel(subprojects, 1, 0);
			
			manager.display();
		%>	
						</div>
					</div>
				</div>
			</td>
			<td width="30%">
				<div id="right_section">
					<div id="resource_section">
						<div style="padding: 5px;">
							<div style="font-weight: bold; font-size: medium;">Resources</div>
							<%= ServiceFactory.getInstance().getPnPersonService().getPersonsByProjectId(Integer.parseInt(projectSpace.getID())).size() %> People Assigned
						</div>
					</div>
					<div id="teammate_section">
						<div style="padding: 5px;">
		<%
			manager = new ChannelManager(pageContext);
			manager.setCustomizable(false);
			news = new Channel("ProjectSpace_teammates_" + projectSpace.getName());
			news.setTitle("Teammates");
			news.setWidth("40%");
			news.setMinimizable(false);
			news.setCloseable(false);
			news.setInclude("/teammates/include/TeammatesChannel.jsp");
			news.addAttribute("filter_viewRange", "" + net.project.news.NewsManager.FILTER_PAST_TWO_WEEKS);
			manager.addChannel(news, 0, 0);
			manager.display();
		%>
						</div>
					</div>
				</div>
			</td>
		</tr>
	</table>

</form>
<template:getSpaceJS />
</body>
</html>
