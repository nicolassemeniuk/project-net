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
    info="Create Methodology Wizard Processing -- Page 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.methodology.*,
    net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");

%>

<%

      methodologySpace.setUser (user);
      methodologySpace.setName (request.getParameter("name"));
      methodologySpace.setDescription (request.getParameter("description"));
      methodologySpace.setParentSpaceID (request.getParameter("parentSpaceID"));
      methodologySpace.setUseScenario (request.getParameter("useScenario"));
      methodologySpace.setGlobal(request.getParameter("isGlobal"));
      methodologySpace.store();
%>

<%
	request.setAttribute("action", "" + net.project.security.Action.VIEW);

	if (refLink != null && refLink.length() > 0) {
		pageContext.forward(refLink);
	} else {
		pageContext.forward ("MethodologyList.jsp");
	}
%>
