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
    info="Business Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, 
				net.project.base.Module,
				net.project.base.property.PropertyProvider,
				net.project.business.BusinessSpaceBean, 
				net.project.business.BusinessSpace,
				net.project.financial.FinancialSpaceBean,
				net.project.space.SpaceManager,
				net.project.document.DocumentManagerBean, 
				net.project.channel.Channel,
				net.project.channel.ChannelManager,
		        net.project.space.Space,
		        net.project.base.PnetException,
		        net.project.space.SpaceURLFactory" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
	//Business dashboard page must be requested from server everytime, 
	//otherwise business space will not be set.
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader ("Expires", -1);
	response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="financialSpace" class="net.project.financial.FinancialSpaceBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%-- History setup, but not displayed on this page --%>
<history:history displayHere="false">
	<history:business display="<%=businessSpace.getName()%>"
					 jspPage='<%=SessionManager.getJSPRootURL() + "/business/Main.jsp?module=" + Module.BUSINESS_SPACE %>' />
</history:history>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%

String base = SessionManager.getJSPRootURL();

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

<%
/******************************************************************
 *  BEGIN: Custom Security Check
 *****************************************************************/
String id = request.getParameter("id");
if (id != null && businessSpace.getID() == null) 
{
    // Security Check: Is user allowed access to requested space?
    Space testSpace = new BusinessSpaceBean();
    testSpace.setID(id);
    Space oldSpace = securityProvider.getSpace();
    securityProvider.setSpace(testSpace);
	boolean allowed = false;

	try{
		allowed = securityProvider.isActionAllowed(null, Integer.toString(Module.BUSINESS_SPACE), Action.VIEW);
	}catch(Exception ex){
		throw new AuthorizationFailedException(PropertyProvider.get("prm.business.main.authorizationfailed.message"), testSpace);
	}

    if (allowed) {
        // Passed Security Check
		businessSpace.clear();
        businessSpace.setID(id);
        businessSpace.load();
        
// 		//Get financial related space
// 		if(businessSpace.getRelatedSpaceID()!=null){
// 	 		financialSpace = new FinancialSpaceBean();
// 	 		financialSpace.setID(businessSpace.getRelatedSpaceID());
// 	 		financialSpace.load();
// 		}
    }
    else 
	{
        // Failed Security Check - reset old space, throw exception indicating
        // the space for which access was denied
        securityProvider.setSpace(oldSpace);
        throw new AuthorizationFailedException(PropertyProvider.get("prm.business.main.authorizationfailed.message"), testSpace);
    }
} 
else 
{
    if (businessSpace.getID() != null)
        businessSpace.load();
    
// 		//Get financial related space
// 		if(businessSpace.getRelatedSpaceID()!=null){
// 	 		financialSpace = new FinancialSpaceBean();
// 	 		financialSpace.setID(businessSpace.getRelatedSpaceID());
// 	 		financialSpace.load();
// 		}
    
    
    else
        throw new PnetException("No business space specified");
}

/******************************************************************
 *  END: Custom Security Check
 *****************************************************************/
 
	// Set user's current space to this Business
    user.setCurrentSpace(businessSpace);
    
    // Load the roster (directory) for this Business Space.
	user.getCurrentSpace().getRoster().reload();
	
    docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/business/Main.jsp");

    //Sometimes a user will be directed here simply to switch space so that they can view a document or task.
    //If that was the case, they have switched space now, redirect them to the proper place
    if (request.getParameter("page") != null) {
        response.sendRedirect(SpaceURLFactory.constructForwardFromSpaceURL((String)request.getParameter("page")));
    }
%>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
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

function cancel() { self.document.location = JSPRootURL + "/personal/Main.jsp"; }
function search() { self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }
function modify() { self.document.location=JSPRootURL + "/business/ModifyBusiness.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&action=<%=net.project.security.Action.MODIFY%>"; }
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=business_summary";
	openwin_help(helplocation);
}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id="left-navbar">

	<div id='leftheading-business'><%= PropertyProvider.get("prm.global.tool.dashboard.name")%></div>
	<div style='clear: both'></div>
	<div class="project-title"><c:out value="${businessSpace.name}" /></div>
	<div style='clear: both'></div>
	
	<%-- Display project logo, if it exists --%>
	
	<% if (businessSpace.getLogoID() != null) { %>
		<div class="business-logo">
			<IMG SRC="<%=SessionManager.getJSPRootURL()%>/servlet/photo?id=<%=businessSpace.getID()%>&size=medium&logoType=blogo&module=<%=net.project.base.Module.DIRECTORY%>" width="110px">&nbsp;
		</div>
		<div style='clear: both'></div>
	<%} %>

<div class="project-description">
		<c:out value="${businessSpace.description}" />
</div>	
	<%-------------------------------------------------------------------------------------------------------------
			    -- Display parent business link if this is a subbusiness                                
	---------------------------------------------------------------------------------------------------------%>

<%
		net.project.space.Space parentBusiness = net.project.space.SpaceManager.getSuperBusiness(businessSpace);
		if (parentBusiness != null) {
			businessSpace.setParentSpaceID(parentBusiness.getID());
		}
		if (businessSpace.isSubbusiness())
		{
			if ((parentBusiness != null) && (parentBusiness.getID() != null))
			{
				parentBusiness.load();
				if (parentBusiness.getRecordStatus() != null && parentBusiness.getRecordStatus().equals("A")) {
		%>
		<div class="project-description">
			<display:get name="prm.business.main.subbusinessof.label"/>&nbsp;
			<a href="<%=SessionManager.getJSPRootURL()%>/business/Main.jsp?id=<%=parentBusiness.getID()%>&module=<%=Module.BUSINESS_SPACE%>"><%=parentBusiness.getName()%></a>
		</div>
		<%
				}
			}
		}
%>

	<tb:toolbar style="tooltitle" showAll="true">
		<tb:band name="standard">
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.business.main.modify.link")%>' />
			<tb:button type="personalize_page" />
		</tb:band>
		
		<tb:band name="action" groupHeading='<%= PropertyProvider.get("prm.global.goto.heading")%>'>
			<display:if name="@prm.business.subbusiness.isenabled">
				<tb:button type="custom" label='<%= PropertyProvider.get("@prm.business.nav.subbusiness")%>' function='<%=base + "/business/Main.jsp?id="+ businessSpace.getID() + "&page="+ base +"/business/subbusiness/Main.jsp?module="+Module.BUSINESS_SPACE%>'/>
			</display:if>
			<display:if name="@prm.business.relatedBusinesses.isenabled">
				<tb:button type="custom" label='<%= PropertyProvider.get("@prm.business.nav.relatedBusinesses")%>' function='<%=base + "/business/relatedBusinesses/Main.jsp?module="+Module.BUSINESS_SPACE+"&relationship=1"%>'/>
			</display:if>		
			<display:if name="@prm.business.reports.isenabled">
				<tb:button type="custom" label='<%= PropertyProvider.get("@prm.business.nav.reports")%>' function='<%=base + "/report/Main.jsp?module="+Module.REPORT%>'/>
			</display:if>		
			<display:if name="@prm.business.news.isenabled">
				<tb:button type="custom" label='<%= PropertyProvider.get("@prm.business.nav.news")%>' function='<%=base + "/news/Main.jsp?module="+Module.NEWS%>'/>
			</display:if>	
		</tb:band>
	</tb:toolbar>
</div>
<form method="post" action="MainProcessing.jsp">
		<div id="content" style="padding-left: 10px;">
			<table width="100%"> <tr> <td  valign="top" width="60%">
					<!--<input type="hidden" name="theAction">-->
						<%
							// Initialize the channel manager
							int numChannels = 5;
							ChannelManager manager = new ChannelManager(pageContext);
							manager.setCustomizable(false);
							ChannelManager personalizer = new ChannelManager(pageContext);
							request.setAttribute("id", "");
							request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
							request.setAttribute("module", Integer.toString(net.project.base.Module.BUSINESS_SPACE));
							net.project.security.ServletSecurityProvider.setAndCheckValues(request);
						%>
					
					<%-- My Projects Channel --%>
					<display:if name="prm.business.main.channel.projects.isenabled">
						<%
							request.setAttribute("id", "");
							request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
							request.setAttribute("module", Integer.toString(net.project.base.Module.BUSINESS_SPACE));
							Channel myProjects = new Channel("BusinessSpace_projects_" + businessSpace.getID());
							myProjects.setTitle(PropertyProvider.get("prm.business.main.channel.projects.title"));
							myProjects.setWidth("100%");
							myProjects.setMinimizable(true);
							myProjects.setCloseable(true);
							myProjects.setInclude("/business/include/projectPortfolio.jsp");
							manager.addChannel(myProjects, 2, 0);
							personalizer.addChannel(myProjects, 2, 0);
						%>
					</display:if>
					
					<display:if name="prm.business.main.channel.documentsmodified.isenabled">
						<%
							Channel myDocsMod = new Channel("BusinessSpace_DocsModified_" + businessSpace.getID());
							myDocsMod.setTitle(PropertyProvider.get("prm.business.main.channel.documentsmodified.title"));
							myDocsMod.setWidth("100%");
							myDocsMod.setMinimizable(true);
							myDocsMod.setCloseable(true);
							myDocsMod.setInclude("/project/include/myDocumentsModified.jsp");
							manager.addChannel(myDocsMod, 3, 0);
							personalizer.addChannel(myDocsMod, 2, 0);
						%>
					</display:if>
					
					<%-- Documents Forms Channel --%>
					<display:if name="prm.business.main.channel.formsmodified.isenabled">
						<%
							Channel modifiedForms = new Channel("BusinessSpace_ModifiedForms");
							modifiedForms.setTitle(PropertyProvider.get("prm.global.main.channel.formsmodified.title.value"));
							modifiedForms.setWidth("100%");
							modifiedForms.setMinimizable(true);
							modifiedForms.setCloseable(true);
							modifiedForms.setInclude("/business/include/myForms.jsp");
							manager.addChannel(modifiedForms, 5, 0);
							personalizer.addChannel(modifiedForms, 2, 0);
						%>
					</display:if>
					
					<%
						manager.display();
						manager = new ChannelManager(pageContext);
						manager.setCustomizable(false);
					%>
		</td>
		<td width="1%"></td> <!-- Space in between two columns -->
		<td valign="top" width="35%"> <!-- Start of right Column -->
					<%-- News Channel --%>
					<display:if name="prm.business.main.channel.news.isenabled">
						<%
							Channel news = new Channel("BusinessSpace_News_" + businessSpace.getID());
							news.setTitle(PropertyProvider.get("prm.business.main.channel.news.title"));
							news.setWidth("100%");
							news.setMinimizable(true);
							news.setCloseable(true);
							news.setInclude("/news/include/NewsChannel.jsp");
							news.addAttribute("filter_viewRange", "" + net.project.news.NewsManager.FILTER_PAST_TWO_WEEKS);
							manager.addChannel(news, 0, 0);
							personalizer.addChannel(news, 2, 0);
						%>
					</display:if>
					
					<%--  Team Members Channel --%>
					<display:if name="prm.business.main.channel.teammembers.isenabled">
						<%
							Channel myTeamMembers = new Channel("BusinessSpace_TeamMembers_" + businessSpace.getID());
							myTeamMembers.setTitle(PropertyProvider.get("prm.business.main.channel.teammembers.title"));
							myTeamMembers.setWidth("100%");
							myTeamMembers.setMinimizable(true);
							myTeamMembers.setCloseable(true);
							myTeamMembers.setInclude("/business/include/myBusinessMembersOnline.jsp");
							manager.addChannel(myTeamMembers, 1, 0);
							personalizer.addChannel(myTeamMembers, 2, 0);
						%>
					</display:if>
					
					<%-- Weekly Meetings Channel --%>
					<display:if name="prm.business.main.channel.meetings.isenabled">
						<%
							Channel myMeetings = new Channel("BusinessSpace_Meetings_" + businessSpace.getID());
							myMeetings.setTitle(PropertyProvider.get("prm.business.main.channel.meetings.title"));
							myMeetings.setWidth("100%");
							myMeetings.setMinimizable(true);
							myMeetings.setCloseable(true);
							myMeetings.setInclude("/project/include/myMeetings.jsp");
							manager.addChannel(myMeetings, 4, 0);
							personalizer.addChannel(myMeetings, 2, 0);
						%>
					</display:if>
					
					<%
						manager.display();
					%>
					<%=personalizer.getPersonalizeLink()%>
			</td>
		</tr>
		</table>
	</div>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

