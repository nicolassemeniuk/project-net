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
		info="Document Main Page"
		language="java"
		errorPage="/errors.jsp"
		import="net.project.base.property.PropertyProvider,
				net.project.document.DocumentManagerBean,
				net.project.security.SessionManager,
				net.project.space.SpaceURLFactory,
				javax.servlet.jsp.PageContext"
		%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session"/>
<jsp:useBean id="container" class="net.project.document.ContainerBean" scope="request"/>
<jsp:useBean id="path" class="net.project.document.PathBean"/>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess
		module="<%=net.project.base.Module.DOCUMENT%>"
		action="view"
		/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
	String id = securityProvider.getCheckedObjectID();

	String spaceID = DocumentManagerBean.getSpaceIDForContainerObject(id);
	if (!spaceID.equals(user.getCurrentSpace().getID())) {
		String redirect = SpaceURLFactory.constructSpaceURLForMainPage(spaceID);
		if (redirect.indexOf('?') != -1) {
			redirect += "&";
		} else {
			redirect += "?";
		}
		redirect += "page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString());
		response.sendRedirect(redirect);
		return;
	}


	docManager = new DocumentManagerBean();
	pageContext.setAttribute("docManager", docManager, PageContext.SESSION_SCOPE);

	StringBuffer topContainerURL = new StringBuffer();
	topContainerURL.append("/document/Main.jsp?").append(request.getQueryString());
	if (!topContainerURL.toString().endsWith("?")) {
		topContainerURL.append("&");
	}
	topContainerURL.append("historyType=").append("unspecified");
	docManager.getNavigator().put("TopContainer", topContainerURL.toString());
	docManager.setCancelPage((String) docManager.getNavigator().get("TopContainerReturnTo"));
	docManager.unSetListDeleted();
	session.setAttribute("refererLink", SessionManager.getJSPRootURL() + "/document/Main.jsp?module=" + net.project.base.Module.DOCUMENT);

	docManager.setSpace(user.getCurrentSpace());
	docManager.setUser(user);

	container.setID(docManager.getCurrentContainerID());
	container.setUser(user);
	container.unSetListDeleted();

	container.setSortBy(docManager.getContainerSortBy());
	container.setSortOrder(docManager.getContainerSortOrder());
	container.load();

	if (!securityProvider.isActionAllowed(container.getID(), Integer.toString(docManager.getModuleFromContainerID(container.getID())), net.project.security.Action.VIEW))
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.main.authorizationfailed.message"));

	docManager.setCurrentContainer(container);

	// set the path object of the container
	path.setRootContainerID(docManager.getRootContainerID());
	path.setObject(container);
	path.load();

	response.sendRedirect(SessionManager.getJSPRootURL() + "/document/PropertyFrameset.jsp?" + request.getQueryString());
%>
