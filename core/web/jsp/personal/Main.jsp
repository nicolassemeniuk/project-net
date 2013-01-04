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
       import="net.project.base.property.PropertyProvider,
               net.project.channel.ChannelManager, 
               net.project.channel.Channel, 
               net.project.document.DocumentManagerBean,
               net.project.security.User, 
               net.project.security.SessionManager, 
               net.project.space.PersonalSpaceBean,
               net.project.util.NumberFormat,
               net.project.gui.toolbar.Button,
               net.project.gui.toolbar.ButtonType,
               net.project.base.Module,
               net.project.security.Action,
               net.project.channel.State,
		        net.project.space.SpaceURLFactory"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />

<%-- History setup, but not displayed on this page --%>
<history:history displayHere="false">
	<history:business show="false" />
	<history:project display="<%=personalSpace.getName()%>"
					 jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Main.jsp?page=" + SessionManager.getJSPRootURL() + "/assignments/My?module=" + Module.PERSONAL_SPACE %>' />
</history:history>

<%
   user.setCurrentSpace(personalSpace);
   docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/personal/Main.jsp");

	String forwardTo = request.getParameter("page");
	if (forwardTo != null) {
		response.sendRedirect(java.net.URLDecoder.decode(SpaceURLFactory.constructForwardFromSpaceURL(forwardTo), SessionManager.getCharacterEncoding()));
		return;
	}

   int channelLocationIndex = 0;

%>
<template:getDoctype />
<html>
<head>
<meta http-equiv="expires" content="0">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="pragma" content="no-cache">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<script language="javascript" type="text/javascript">
if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) == -1 ) {
	document.write("<style type=\"text/css\"> div#content { overflow: visible; width: 80%; } </style>");
}

	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   load_header();
   theForm = self.document.forms[0];
   isLoaded = true;
   applyColorToEvenRows();
}

function modifyCurrentAssignments() {
    var objectIdsStr = "";
    for(var i=0;i<document.forms.length;i++) {
        if("currentAssignmentsForm" == document.forms[i].name) {
            var currentAssignmentsForm = document.forms[i];
            for(var j=0;j<currentAssignmentsForm.elements.length;j++){
                if("objectID" == currentAssignmentsForm.elements[j].name) {
                    objectIdsStr += "&objectID="+currentAssignmentsForm.elements[j].value;
                }
            }
            break;
        }
    }
    self.document.location = JSPRootURL + "/servlet/AssignmentController/CurrentAssignments/Update?module=<%=Module.RESOURCE%>&action=<%=Action.MODIFY%>"+objectIdsStr;
}

function reset() { self.document.location = JSPRootURL + "/personal/Main.jsp"; }
function search() { self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=Module.PERSONAL_SPACE%>&action=<%=Action.VIEW%>"; }
function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_main";
	openwin_help(helplocation);
}

function refresh(parameters) {
    self.document.location = JSPRootURL + "/personal/Main.jsp?" + parameters;
}

function popupHelp(page) {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}
</script>

</head>
<body onLoad="setup();" class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-------------------------------------------------------------------------------
  --  Toolbar
  -----------------------------------------------------------------------------%>

<tb:toolbar style="tool" showAll="true" groupTitle="prm.personal.nav.dashboard">
	<tb:band name="standard">
	<tb:button type="personalize_page" />
	</tb:band>
</tb:toolbar>


<div id='content'>
<%	
request.setAttribute("module", Integer.toString(net.project.base.Module.PERSONAL_SPACE));
// Initialize the channel manager
ChannelManager manager = new ChannelManager(pageContext);
manager.setCustomizable(false);
ChannelManager personalizer = new ChannelManager(pageContext);
%>
<table width="100%" border="0">
  <tr>
    <td width="48%" valign="top">

		<display:if name="prm.personal.channels.currentassignments.isenabled">
		<%
		Channel currentAssignments = new Channel("PersonalSpace_currentassignments");
		currentAssignments.setTitle(PropertyProvider.get("prm.personal.main.channel.currentassignments.title"));
		currentAssignments.setWidth("100%");
		currentAssignments.setMinimizable(true);
		currentAssignments.setCloseable(true);
		currentAssignments.setInclude("/personal/include/CurrentAssignments.jsp");
		//currentAssignments.addChannelButton(ButtonType.MODIFY.toString(), PropertyProvider.get("all.global.toolbar.channel.modify"), "javascript:modifyCurrentAssignments();");
		//currentAssignments.addChannelButton(ButtonType.HELP.toString(), PropertyProvider.get("all.global.toolbar.channel.help"), "javascript:popupHelp('current_assignments_channel');");    
		
		manager.addChannel(currentAssignments, channelLocationIndex++, 0);
		personalizer.addChannel(currentAssignments, channelLocationIndex++, 0);
		%>
		</display:if>
		
		<%-- My Projects Channel --%>
		<display:if name="prm.personal.channels.myprojects.isenabled">
		<%
		Channel myProjects = new Channel("PersonalSpace_projects");
		myProjects.setTitle(PropertyProvider.get("prm.personal.main.channel.myprojects.title"));
		myProjects.setWidth("100%");
		myProjects.setMinimizable(true);
		myProjects.setCloseable(true);
		// We have to add in the request parameter here since if an IFRAME is used,
		// The page will be included in a separate request
		myProjects.setInclude("/personal/include/myProjects.jsp?PersonalSpace_projects_viewID=" + request.getParameter("PersonalSpace_projects_viewID")
				+ "&sortcolumn="+ request.getParameter("sortcolumn") 
				+ "&sortorder="+ request.getParameter("sortorder"));
		myProjects.setDefaultState(PropertyProvider.get("prm.personal.main.channel.myprojects.defaultstate"));
		manager.addChannel(myProjects, channelLocationIndex++, 0);
		personalizer.addChannel(myProjects, channelLocationIndex++, 0);
		%>
		</display:if>
		
		<%-- My Businesses Channel --%>
		<display:if name="prm.personal.channels.mybusiness.isenabled">
		<%
		Channel myBusinesses = new Channel("PersonalSpace_MyBusinessList");
		myBusinesses.setTitle(PropertyProvider.get("prm.personal.main.channel.mybusinesses.title"));
		myBusinesses.setWidth("100%");
		myBusinesses.setMinimizable(true);
		myBusinesses.setCloseable(true);
		myBusinesses.setInclude("/personal/include/myBusinesses.jsp");
		myBusinesses.setDefaultState(PropertyProvider.get("prm.personal.main.channel.mybusinesses.defaultstate"));
		manager.addChannel(myBusinesses, channelLocationIndex++,0);
		personalizer.addChannel(myBusinesses, channelLocationIndex++,0);
		%>
		</display:if>
		<%
		manager.display();
		%>
		</td>
		<td width="2%" valign="top"></td>
	    <td width="50%" valign="top">
	    <%		
		// Initialize the channel manager
		manager = new ChannelManager(pageContext);
	    manager.setCustomizable(false);
		%>
		<%-- My Status Messages Channel --%>
		<display:if name="prm.personal.channels.systemstatus.isenabled">
		<%
		Channel myMessages = new Channel("PersonalSpace_systemstatus");
		myMessages.setTitle(PropertyProvider.get("prm.personal.main.channel.sitestatus.title"));
		myMessages.setWidth("100%");
		myMessages.setMinimizable(true);
		myMessages.setCloseable(false);
		myMessages.setInclude("/personal/include/systemStatus.jsp");
		manager.addChannel(myMessages, channelLocationIndex++, 0);
		personalizer.addChannel(myMessages, channelLocationIndex++, 0);
		%>
		</display:if>
		<%-- Assignment Metrics Channel --%>
		<%--display:if name="prm.personal.channels.assignmentMetrics.isenabled"--%>
		<%
		Channel myAssignmentMetrics = new Channel("PersonalSpace_assignmentMetrics");
		myAssignmentMetrics.setTitle(PropertyProvider.get("prm.personal.assignmentmetrics.channelname"));
		myAssignmentMetrics.setWidth("100%");
		myAssignmentMetrics.setMinimizable(true);
		myAssignmentMetrics.setCloseable(true);
		myAssignmentMetrics.setInclude("/servlet/MetricsController/personalAssignmentMetrics");
		myAssignmentMetrics.setDefaultState(PropertyProvider.get("prm.personal.personalassignments.channel.defaultstate"));
		manager.addChannel(myAssignmentMetrics, channelLocationIndex++, 0);
		personalizer.addChannel(myAssignmentMetrics, channelLocationIndex++, 0);
		%>
		<%--/display:if--%>
		
		<%-- Weekly Meetings Channel --%>
		<display:if name="prm.personal.channels.meetings.isenabled">
		<%
		Channel myMeetings = new Channel("PersonalSpace_meetings");
		myMeetings.setTitle(PropertyProvider.get("prm.personal.main.channel.upcomingmeetings.title"));
		myMeetings.setWidth("100%");
		myMeetings.setMinimizable(true);
		myMeetings.setCloseable(true);
		myMeetings.setInclude("/project/include/myMeetings.jsp");
		myMeetings.setDefaultState(PropertyProvider.get("prm.personal.main.channel.upcomingmeetings.defaultstate"));
		manager.addChannel(myMeetings, channelLocationIndex++, 0);
		personalizer.addChannel(myMeetings, channelLocationIndex++, 0);
		%>
		</display:if>
		
		<%-- My Envelopes Channel --%>
		<display:if name="prm.personal.channels.envelopelist.isenabled">
		<%
		Channel myEnvelopes = new Channel("PersonalSpace_MyEnvelopeList");
		myEnvelopes.setTitle(PropertyProvider.get("prm.personal.main.channel.workflowinbox.title"));
		myEnvelopes.setWidth("100%");
		myEnvelopes.setMinimizable(true);
		myEnvelopes.setCloseable(true);
		myEnvelopes.setInclude("/workflow/envelope/include/MyEnvelopeList.jsp");
		myEnvelopes.setDefaultState(PropertyProvider.get("prm.personal.main.channel.workflowinbox.defaultstate"));
		manager.addChannel(myEnvelopes, channelLocationIndex++, 0);
		personalizer.addChannel(myEnvelopes, channelLocationIndex++, 0);
		%>
		</display:if>
		
		<%-- My Documents Checked-out Channel --%>
		<display:if name="prm.personal.channels.documentlist.isenabled">
		<%
		Channel myCkoDocs = new Channel("PersonalSpace_MyCheckedOutDocs");
		myCkoDocs.setTitle(PropertyProvider.get("prm.personal.main.channel.documentscheckedoutbyme.title"));
		myCkoDocs.setWidth("100%");
		myCkoDocs.setMinimizable(true);
		myCkoDocs.setCloseable(true);
		myCkoDocs.setInclude("/document/include/documentList.jsp");
		myCkoDocs.setDefaultState(PropertyProvider.get("prm.personal.main.channel.documentscheckedoutbyme.defaultstate"));
		manager.addChannel(myCkoDocs, channelLocationIndex++, 0);
		personalizer.addChannel(myCkoDocs, channelLocationIndex++, 0);
		%>
		</display:if>
		
		<display:if name="prm.personal.main.channel.formsmodified.isenabled">
		<%
		Channel modifiedForms = new Channel("PersonalSpace_ModifiedForms");
		modifiedForms.setTitle(PropertyProvider.get("prm.global.main.channel.formsmodified.title.value"));
		modifiedForms.setWidth("100%");
		modifiedForms.setMinimizable(true);
		modifiedForms.setCloseable(true);
		modifiedForms.setInclude("/personal/include/myForms.jsp");
		modifiedForms.setDefaultState(PropertyProvider.get("prm.global.main.channel.formsmodified.defaultstate"));
		manager.addChannel(modifiedForms, channelLocationIndex++, 0);
		personalizer.addChannel(modifiedForms, channelLocationIndex++, 0);
		%>
		</display:if>
		<%
		manager.display();
		%>
		<%=personalizer.getPersonalizeLink()%>
		</td>
	</tr>
</table>




<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>

