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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
       info="Business Register Processing" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.User, 
               net.project.space.PersonalSpaceBean,  
               net.project.business.BusinessAssignmentManager"
%>

<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessassignmentmanager" class="net.project.business.BusinessAssignmentManager" scope="session" />

<%
// no need for a security check since we are using an existing bean and the user can only
// affect his own assignments.  There is no way for a user to get into anothers personal space.

String user_response = request.getParameter("theAction");
// Store there response and forward

boolean isExternal = request.getParameter("external") != null && request.getParameter("external").equals("1") ? true : false ; 
if (businessassignmentmanager.hasInvitationActedUpon() && isExternal){
%>
<jsp:forward page="/base/InvitationActionStatus.jsp" />
<%	
}
businessassignmentmanager.storeInvitationResponse(user_response);
pageContext.forward("/personal/Main.jsp");
%>

