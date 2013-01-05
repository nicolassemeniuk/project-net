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
|   $Revision: 19849 $
|       $Date: 2009-08-25 07:05:28 -0300 (mar, 25 ago 2009) $
|     $Author: dpatil $
|
| Entry point for application space
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Application Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
            net.project.security.SecurityProvider,
            net.project.security.Action,
            net.project.space.Space,
            net.project.base.Module,
            net.project.admin.ApplicationSpace,
			net.project.base.property.PropertyProvider,
            net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="dbVersion" scope="page" class="net.project.util.DBVersion"/>
<jsp:useBean id="versionCheck" scope="application" class="net.project.versioncheck.VersionCheck"/>

<%
    dbVersion.load();

String id;

if (request.getParameter("id") != null) {
  id = request.getParameter("id");
} else {
  id = ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID;
}


// This page will be accessed via external notifications.  We need to make sure that
// it is sent they are sent through the navigation frameset
if (request.getParameter("external") != null && request.getParameter("inframe") == null) {
    session.putValue("requestedPage", request.getRequestURI() + 
			"?id="+ id + 
			"&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
}
%>

<%
    // If we aren't in the application space, switch to the application space
    if ((user.getCurrentSpace().getID() != null) && (user.getCurrentSpace().getID() != ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID)) {
		Space currentSpace = securityProvider.getSpace();
		Space newSpace = new ApplicationSpace();

		newSpace.setID(id);
        securityProvider.setSpace(newSpace);
		
        // Security Check: Is user allowed access to requested space?
        if (securityProvider.isActionAllowed(null, Integer.toString(Module.APPLICATION_SPACE), Action.VIEW)) {
            // Passed Security Check
			applicationSpace.setID(id);
    	    applicationSpace.load();
			
		} else {
            // Failed Security Check.  Return to previous space.
            securityProvider.setSpace(currentSpace);
            throw new net.project.security.AuthorizationFailedException("Failed security verification", newSpace);
        }
    }

	// Set user's current space to application space
    user.setCurrentSpace(applicationSpace);

  
	// version check related part
    String newVersion = null;
	// version check is performed only once
	//if (request.getSession().getAttribute("newVersion") == null ){
		if (PropertyProvider.getBoolean("prm.versioncheck.isenabled")){
			if(versionCheck.isCheckAgain()){
				newVersion = net.project.hibernate.service.ServiceFactory.getInstance().getVersionCheckService().checkVersion(user, request.getRemoteHost());
				versionCheck.setLastTimeCheck(System.currentTimeMillis());
				//request.getSession().setAttribute("newVersion", newVersion);
				versionCheck.setNewVersionAvailable(true);
				versionCheck.setNewVersion(newVersion);
			}
		}
	//}else{
	//	newVersion = (String)request.getSession().getAttribute("newVersion");
	//}
%>
<template:getDoctype />

<%@page import="java.util.Date"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   load_header();
   theForm = self.document.forms["main"];
   isLoaded = true;
}

function help() {
	var helplocation = JSPRootURL + "/help/Help.jsp?page=admin_main";
    openwin_help(helplocation);
}

function reset() { self.document.location = JSPRootURL + "/admin/Main.jsp?module=<%=Module.APPLICATION_SPACE%>"; }

</script>
</head>

<body onLoad="setup();" id="bodyWithFixedAreasSupport" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.nav.applicationspace">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:project display="<%=applicationSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>
	<%
		if (PropertyProvider.getBoolean("prm.versioncheck.isenabled")){
			
			if(dbVersion.getAppVersion().equals(versionCheck.getNewVersion())){
				out.write(PropertyProvider.get("prm.versioncheck.enabled.message"));
			}else{
				out.write(PropertyProvider.get("prm.versioncheck.message"));
			}
		}else{
			out.write(PropertyProvider.get("prm.versioncheck.disabled.message"));
		}
	%>
<form method="post" name="main" action="<%=SessionManager.getJSPRootURL()%>/admin/MainProcessing.jsp">
	<input type="hidden" name="theAction">
<%
	  if (PropertyProvider.getBoolean("prm.application.main.metrics.isenabled")) {
%>
<channel:channel name='prm.application.main' customizable="true">
    <channel:insert name='prm.application.main.metrics' title='<%=PropertyProvider.get("prm.application.main.metrics")%>' row="0" column="0"
                    width="100%" minimizable="true" closeable="true" include="/admin/include/metrics.jsp?module=240">
    </channel:insert>
</channel:channel>	
<%
      }
%>
	<center>
		<%=PropertyProvider.get("prm.versioncheck.privacy.policy")%>
	</center>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
