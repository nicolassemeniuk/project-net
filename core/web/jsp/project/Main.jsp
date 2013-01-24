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
            	java.net.URLDecoder,
		        net.project.space.SpaceURLFactory"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<template:getDoctype />
<html>
<head>
<meta http-equiv="expires" content="0"> 
<%
String base = SessionManager.getJSPRootURL();

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
        Space testSpace = new ProjectSpaceBean();
        testSpace.setID(id);
        Space oldSpace = securityProvider.getSpace();
        securityProvider.setSpace(testSpace);
		
        if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE),
                                             net.project.security.Action.VIEW)) {
            // Passed Security Check
			projectSpace.setID(id);
    	    projectSpace.load();
			
		} 
		else {
            // Failed Security Check
            securityProvider.setSpace(oldSpace);
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace);
        }
	} 
	// id was null
	else {
		if(user.getCurrentSpace() instanceof ProjectSpaceBean)
    		projectSpace = (ProjectSpaceBean)user.getCurrentSpace();
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
				 jspPage='<%=SessionManager.getJSPRootURL() + "/project/Dashboard"%>'
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
	
    docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/project/Dashboard"); 
	String forwardTo = request.getParameter("page");
	if (forwardTo != null) {
	    response.sendRedirect(URLDecoder.decode(SpaceURLFactory.constructForwardFromSpaceURL(forwardTo), SessionManager.getCharacterEncoding())+"&id="+projectSpace.getID());
	    return;
    }
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
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
function reset() { self.document.location = JSPRootURL + "/project/Main.jsp"; }
function modify() { self.document.location = JSPRootURL + "/project/PropertiesEdit.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&referer=project/Main.jsp"; }
function search() { self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }
function properties() { self.document.location = JSPRootURL + "/project/Properties.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }

function viewPhase(id) {
   self.document.location = JSPRootURL + "/process/ViewPhase.jsp?id=" + id + "&module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>"; 
}
</script>
</head>
<body onLoad="setup();" class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
  -- Toolbar                                                                                         
  ------------------------------------------------------------------------------------------------------%>   
		<tb:toolbar style="tool" showAll="true"  groupTitle="prm.project.main.project.label">
			<tb:band name="standard">
				<tb:button type="blogit" />

				<tb:button type="modify" label='<%= PropertyProvider.get("prm.project.main.modify.button.tooltip")%>' />
				
				<tb:button type="properties" label='<%= PropertyProvider.get("prm.project.main.properties.button.tooltip")%>'/>
				<tb:button type="personalize_page" label='<%= PropertyProvider.get("prm.global.personalizepage.link")%>' />
			</tb:band>

			<tb:band name="action" groupHeading="Go to">
				<display:if name="@prm.project.subproject.isenabled">
					<tb:button type="custom" label='<%= PropertyProvider.get("@prm.project.nav.subproject")%>' function='<%=SessionManager.getJSPRootURL()+ "/project/subproject/Main.jsp?module="+Module.PROJECT_SPACE%>'/>
				</display:if>					
				<display:if name="@prm.project.reports.isenabled">
					<tb:button type="custom" label='<%= PropertyProvider.get("@prm.project.nav.reports")%>' function='<%=SessionManager.getJSPRootURL()+ "/report/Main.jsp?module="+Module.REPORT%>'/>
				</display:if>
				<display:if name="@prm.project.news.isenabled">
					<tb:button type="custom" label='<%= PropertyProvider.get("@prm.project.nav.news")%>' function='<%=SessionManager.getJSPRootURL()+ "/news/Main.jsp?module="+Module.NEWS%>'/>
				</display:if>
			</tb:band>
			
		</tb:toolbar>
		
<div id='content'>

<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr align="right" class="pageTitle">
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/project/MainProcessing.jsp">
<input type="hidden" name="theAction">
	<td align="left">
		<history:history />
	</td>
<%-- Display project logo, if it exists --%>
<% if (projectSpace.getProjectLogoID() != null) { %>
	<td valign="top" rowspan="3">
		<IMG SRC="<%=SessionManager.getJSPRootURL()%>/servlet/ViewDocument?id=<%=projectSpace.getProjectLogoID()%>&module=<%=Module.DOCUMENT%>">		      
		&nbsp;
	</td>
<% } %>
</tr>
<tr align="right">      
	<td valign="top" align="left" class="tableContent">
		<%= net.project.util.HTMLUtils.escape(projectSpace.getDescription()) %>
    </td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>   

<%-------------------------------------------------------------------------------------------------------------
    -- Display parent project link if this is a subproject.                                     
	---------------------------------------------------------------------------------------------------------%>
<%
if (projectSpace.isSubproject()) {

	ProjectSpace parentProject = (ProjectSpace) SpaceManager.getSuperProject(projectSpace);
	if ((parentProject != null) && (parentProject.getID() != null)) {
		parentProject.load();
        if (parentProject.getRecordStatus() != null && parentProject.getRecordStatus().equals("A")) {
%>
<tr>
	<td align="left" class="tableHeader">
		<%= PropertyProvider.get("prm.project.main.subprojectof.label")%>: &nbsp;
		<a href="<%=SessionManager.getJSPRootURL()%>/project/Dashboard?id=<%=parentProject.getID()%>&module=<%=Module.PROJECT_SPACE%>"><%=parentProject.getName()%></a>
	</td>
</tr>   
<tr>
	<td>&nbsp;</td>
</tr>   
<%
        }
	}
}
%>
</table>

<%--------------------------------------------------------------------------------------------------------
  -- Dashboard Channels                                                                                   
  ------------------------------------------------------------------------------------------------------%>
<%
// Initialize the channel manager
int numChannels = 7;
int nextRow = 0;
ChannelManager manager = new ChannelManager(pageContext);
%>

<display:if name="prm.project.main.channel.news.isenabled">
<%-- News Channel --%>
<%
Channel news = new Channel("ProjectSpace_News_" + projectSpace.getName());
news.setTitle(PropertyProvider.get("prm.project.main.channel.projectnews.title"));
news.setWidth("100%");
news.setMinimizable(true);
news.setCloseable(true);
news.setInclude("/news/include/NewsChannel.jsp");
news.addAttribute("filter_viewRange", "" + net.project.news.NewsManager.FILTER_PAST_TWO_WEEKS);
manager.addChannel(news, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.subprojects.isenabled">
<%-- Subprojects channel --%>
<%
Channel subprojects = new Channel("ProjectSpace_subprojects_" + projectSpace.getName());
subprojects.setTitle(PropertyProvider.get("prm.project.main.channel.subprojects.title"));
subprojects.setWidth("100%");
subprojects.setMinimizable(true);
subprojects.setCloseable(true);
subprojects.setInclude("/project/include/subprojects.jsp");
manager.addChannel(subprojects, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.teammembers.isenabled">
<%--  Team Members Channel --%>
<%
Channel myTeamMembers = new Channel("ProjectSpace_TeamMembers_" + projectSpace.getName());
myTeamMembers.setTitle(PropertyProvider.get("prm.project.main.channel.teammatesonline.title"));
myTeamMembers.setWidth("25%");
myTeamMembers.setMinimizable(true);
myTeamMembers.setCloseable(true);
myTeamMembers.setInclude("/project/include/myTeamMembersOnline.jsp");
manager.addChannel(myTeamMembers, nextRow, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.milestones.isenabled">
<%--  Milestone Channel --%>
<%
Channel taskList = new Channel("ProjectSpace_Milestones_" + projectSpace.getName());
taskList.setTitle(PropertyProvider.get("prm.project.main.channel.milestones.title"));
taskList.setWidth("100%");
taskList.setMinimizable(true);
taskList.setCloseable(true);
taskList.setInclude("/schedule/include/milestoneChannel.jsp");
manager.addChannel(taskList, nextRow++, 1);
%>
</display:if>

<display:if name="prm.project.main.channel.process.isenabled">
<%-- Process Channel --%>
<%
Channel process = new Channel("ProjectSpace_Process_" + projectSpace.getName());
process.setTitleToken(PropertyProvider.get("prm.project.main.channel.process.title"));
process.setWidth("100%");
process.setMinimizable(true);
process.setCloseable(true);
process.setInclude("/project/include/myProcess.jsp");
manager.addChannel(process, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.documentsmodified.isenabled">
<%-- Documents Modified Channel --%>
<%
Channel myDocsMod = new Channel("ProjectSpace_DocsModified_" + projectSpace.getName());
myDocsMod.setTitle(PropertyProvider.get("prm.project.main.channel.documentsmodified.title"));
myDocsMod.setWidth("100%");
myDocsMod.setMinimizable(true);
myDocsMod.setCloseable(true);
myDocsMod.setInclude("/project/include/myDocumentsModified.jsp");
manager.addChannel(myDocsMod, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.meetings.isenabled">
<%-- Weekly Meetings Channel --%>
<%
Channel myMeetings = new Channel("ProjectSpace_Meetings_" + projectSpace.getName());
myMeetings.setTitle(PropertyProvider.get("prm.project.main.channel.upcomingmeetings.title"));
myMeetings.setWidth("100%");
myMeetings.setMinimizable(true);
myMeetings.setCloseable(true);
myMeetings.setInclude("/project/include/myMeetings.jsp");
manager.addChannel(myMeetings, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.formsmodified.isenabled">
<%
Channel modifiedForms = new Channel("ProjectSpace_ModifiedForms");
modifiedForms.setTitle(PropertyProvider.get("prm.global.main.channel.formsmodified.title.value"));
modifiedForms.setWidth("100%");
modifiedForms.setMinimizable(true);
modifiedForms.setCloseable(true);
modifiedForms.setInclude("/project/include/myForms.jsp");
manager.addChannel(modifiedForms, nextRow++, 0);
%>
</display:if>

<display:if name="prm.project.main.channel.discussion.isenabled">
<%-- Discussion Groups Channel --%>
<%
Channel discussionChannel = new Channel("ProjectSpace_Discussion_" + projectSpace.getName());
discussionChannel.setTitle(PropertyProvider.get("prm.project.main.channel.discussiongroups.title"));
discussionChannel.setWidth("100%");
discussionChannel.setMinimizable(true);
discussionChannel.setCloseable(true);
discussionChannel.setInclude("/discussion/include/DiscussionChannel.jsp");
discussionChannel.addAttribute("isSelectable", "0");
manager.addChannel(discussionChannel, nextRow++, 0);
%>
</display:if>

<%
manager.display();
%>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
