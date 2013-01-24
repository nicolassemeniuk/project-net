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
    info="Process folder traversal"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.base.ObjectType,
            net.project.base.URLFactory,
			net.project.base.property.PropertyProvider,
            net.project.document.DocumentManagerBean,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.space.Space,
            net.project.space.SpaceURLFactory"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    // This page will be accessed via external notifications.  We need to make sure that
    // it is sent they are sent through the navigation frameset
	if (request.getParameter("external") != null && request.getParameter("inframe") == null) {
        session.setAttribute("requestedPage", request.getRequestURI() +
    			"?id="+ request.getParameter("id") +
    			"&module=" + request.getParameter("module"));
        response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
        return;
    }
%>
<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
	String id = securityProvider.getCheckedObjectID();

	// added 06/27/00 for tr 56876 - AZK
	// If the user is in their personal space their is a chance they do not have access
	// to the document module.  This will prevent them from selecting "documents modified in
	// last 7 days" for projects they are a member of.  This check will redirect through the project
	// main page if this situation occurs.

	String spaceID = DocumentManagerBean.getSpaceIDForContainerObject(id);
	if (!spaceID.equals(user.getCurrentSpace().getID())) {
		String redirect = SpaceURLFactory.constructSpaceURLForMainPage(spaceID);
		if (redirect.indexOf('?') != -1) {
			redirect += "&";
		} else {
			redirect += "?";
		}
		redirect += "page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString());;
		response.sendRedirect(redirect);
		return;
	}

	// security checks are now done within docManager
	if (id != null && id.length() != 0) {
		if (!id.equals(docManager.getCurrentObjectID()))
			docManager.setCurrentObjectID(id);
	}

	docManager.setCancelPage((String) docManager.getNavigator().get("TopContainer"));

	// 08/02/2001 - Tim
	// Adding additional security to check that access to the object is
	// permitted based on its ancestor containers
	if (!docManager.isActionAllowedByAncestorContainers(securityProvider, module, action)) {
		System.out.println("INFO [PropertyFrameset] Denied access to " + id + " due to ancestor folder permission.");
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.propertyframeset.authorizationfailed.message"));
	}

	if (request.getAttribute("isDiscussion") != null) {
		response.sendRedirect(SessionManager.getJSPRootURL() + "/servlet/DocumentActionBroker?theAction=discuss&module=" + module);
	} else {
		 request.getRequestDispatcher("/document/Properties.jsp?module=" + module + "&action=" + action + "&id=" + id).forward(request,response);
	}
%>

<%--
<html>
<head>
<title><display:get name="prm.document.propertyframeset.title" /></title>
</head>

<frameset name="property_frameset" rows="120,*" border=0 frameborder="0" framespacing=0> 

<%if (request.getAttribute("isDiscussion") != null) {%>
  <frame src="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?view=discuss&module=<%= module %>&gotoframe=<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?theAction=discuss&module=<%= module %>" border=0 frameborder="0" framespacing=0 scrolling="NO" name="property_tabs">
<% } else {%>
  <frame src="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?module=<%=module%>&action=<%=action%>&gotoframe=<%=SessionManager.getJSPRootURL()%>/document/Properties.jsp?module=<%=module%>&action=<%=action%>&id=<%= id %>" border=0 frameborder="0" framespacing=0 scrolling="NO" name="property_tabs">
<% } %>
  <frame src="/blank.html" border=0 frameborder="0" framespacing=0 scrolling="AUTO" name="property_main">
</frameset>
<noframes><body bgcolor="#FFFFFF">

</body></noframes>
</html>
--%>
