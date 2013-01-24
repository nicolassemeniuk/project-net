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
|   $Revision: 20776 $
|       $Date: 2010-04-30 09:24:27 -0300 (vie, 30 abr 2010) $
|
|   Adding a meeting link requires loading the link/addLinkForm jsp
|   in the current frame.  Since this page is designed to appear in
|   a window and is not includable, this frameset acts as a parent
|   which controls the toolbar and provides a means for returning
|   to the MeetingManager.
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Add Meeting Link" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
    net.project.security.SecurityProvider,
    net.project.security.SessionManager,
    net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.DISCUSSION%>" /> 

<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();

String view = request.getParameter("view");
String context = request.getParameter("context");

HttpSession mySess=request.getSession() ;	
mySess.setAttribute("refererLink",SessionManager.getJSPRootURL()+"/discussion/PostView.jsp?module="+module+"&action="+net.project.security.Action.VIEW+"&id="+id);

String url = SessionManager.getJSPRootURL()+"/link/LinkManager.jsp?module="+module+"&action="+action+"&id="+id+"&action="+action+"&view="+view+"&objectId="+request.getParameter("objectId");
response.sendRedirect(url);

%>
