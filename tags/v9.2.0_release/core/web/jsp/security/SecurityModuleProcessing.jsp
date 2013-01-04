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
|   $Revision: 19840 $
|       $Date: 2009-08-24 11:24:12 -0300 (lun, 24 ago 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Save Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User, 
            net.project.security.SessionManager,
            net.project.security.SecurityManager,
            net.project.security.SecurityProvider,
            net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityManagerModule" class="net.project.security.SecurityManager" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	String id = request.getParameter("id");
	String userAction = (request.getParameter("theAction").equals("submit")) ? "view" : "modify_permissions";
%>
<security:verifyAccess action="<%=userAction%>"
					   module="<%=net.project.base.Module.SECURITY%>"
					   objectID="<%=id%>" /> 
<%
String mySpace=user.getCurrentSpace().getType();
String spaceName=null;
if(mySpace.equals(Space.PROJECT_SPACE))
	spaceName="project";
else if(mySpace.equals(Space.BUSINESS_SPACE))
	spaceName="business";
else if(mySpace.equals(Space.METHODOLOGY_SPACE))
	spaceName="methodology";
else
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.security.validationfailed.message"));
%>
<%
	String refLink = (String) pageContext.getAttribute("pnet_reflink",pageContext.SESSION_SCOPE);
	
	if(request.getMethod().equals("POST"))
	{	
		if (request.getParameter("theAction").equals("submit"))
		{		
			securityManagerModule.setSelectedID(request.getParameter("DisplayListID"));
			securityManagerModule.getModuleApplyHtmlPost(request);
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            net.project.security.ServletSecurityProvider.setAndCheckValues(request);
%>
			<%-- Goback to the Security Console --%>
			<jsp:forward page="../security/SecurityModuleMain.jsp" />
<%
		}
		else if (request.getParameter("theAction").equals("apply"))
		{
			securityManagerModule.getModuleApplyHtmlPost(request);
			securityManagerModule.clearModulePermission();
			securityProvider.clearCache();
			
			if (refLink != null && !refLink.trim().equals(""))
				response.sendRedirect(SessionManager.getJSPRootURL() + "/" + refLink +"?module=" + net.project.base.Module.SECURITY);
			else
				response.sendRedirect(SessionManager.getJSPRootURL() +"/"+spaceName+"/Main.jsp");
		}
		else if (request.getParameter("theAction").equals("close"))
		{
			securityManagerModule.clearModulePermission();
			
			if (refLink != null && !refLink.trim().equals(""))
				response.sendRedirect(SessionManager.getJSPRootURL() + "/" + refLink +"?module=" + net.project.base.Module.SECURITY);
			else
				response.sendRedirect(SessionManager.getJSPRootURL() +"/"+spaceName+"/Main.jsp");
		}
	}
%>

