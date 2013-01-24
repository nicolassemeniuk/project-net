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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Security Default Object Permissions Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.SecurityManager,
            net.project.security.DefaultObjectPermission,
            net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="defaultObejct" class="net.project.security.DefaultObjectPermission" scope="session" />
<jsp:useBean id="securityManagerPN" class="net.project.security.SecurityManager" scope="session" />

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="modify_permissions"
					   module="<%=net.project.base.Module.SECURITY%>"
					   objectID="<%=id%>" /> 
<%

String mySpace=user.getCurrentSpace().getType();
String spaceName=null;
if(mySpace.equals(Space.PROJECT_SPACE))
	spaceName="project";
else if(mySpace.equals(Space.BUSINESS_SPACE))
	spaceName="business";
else
    spaceName = "personal";

String refLink = (String) pageContext.getAttribute("pnet_reflink",pageContext.SESSION_SCOPE);

if (mySpace.equals(Space.PERSONAL_SPACE)) {
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.security.validationfailed.message"));
}
%>

<%	
	if(request.getMethod().equals("POST"))
	{	
		// SUBMIT
		if (request.getParameter("theAction").equals("submit"))
		{		
			securityManagerPN.setSelectedID(request.getParameter("DisplayListID"));
			securityManagerPN.getDefaultApplyHtmlPost(request);
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            net.project.security.ServletSecurityProvider.setAndCheckValues(request);
			// Goback to the Security Console
			pageContext.forward("/security/SecurityDefaultMain.jsp");
		}
		// APPLY
		else if (request.getParameter("theAction").equals("apply"))
		{
			securityManagerPN.getDefaultApplyHtmlPost(request);
			securityManagerPN.clearModulePermission();
						
			if (refLink != null && !refLink.trim().equals(""))
				response.sendRedirect(SessionManager.getJSPRootURL() + "/" + refLink +"?module=" + net.project.base.Module.SECURITY);
			else
				response.sendRedirect(SessionManager.getJSPRootURL() +"/"+spaceName+"/Main.jsp");
		}
		// CLOSE
		else if (request.getParameter("theAction").equals("close"))
		{
			securityManagerPN.clearModulePermission();
			
			if (refLink != null && !refLink.trim().equals(""))
				response.sendRedirect(SessionManager.getJSPRootURL() + "/" + refLink +"?module=" + net.project.base.Module.SECURITY);
			else
				response.sendRedirect(SessionManager.getJSPRootURL() +"/"+spaceName+"/Main.jsp");


		}
	}
%>
