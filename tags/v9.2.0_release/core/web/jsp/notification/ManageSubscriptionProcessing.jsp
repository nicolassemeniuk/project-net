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
|   $RCSfile$
|   $Revision: 20192 $
|   $Date: 2009-12-16 15:19:57 -0300 (mié, 16 dic 2009) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Manage Subscriptions" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.notification.*,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager"
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<%--
<security:verifyAccess 
				module="<%=net.project.base.Module.DOCUMENT%>"
				action="view" 
/>
--%>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>
<%
     
     String action = request.getParameter ("theAction");
	 String subscriptionId = request.getParameter("selected");
	 String JSPRootUrl = SessionManager.getJSPRootURL();
	 if(subscriptionId == null || subscriptionId.equals(""))
		 subscriptionId = request.getParameter("subscriptionID");
	 
	 if (action.equals ("remove"))
		 pageContext.forward ("RemoveSubscriptionProcessing.jsp?subscriptionID=" + request.getParameter("selected"));
	else if (action.equals("create"))
		pageContext.forward ("CreateSubscription2.jsp?module=" + Module.getModuleForSpaceType(user.getCurrentSpace().getType()) + "&popup=0&spaceID="+request.getParameter("spaceID") +"&action="+net.project.security.Action.MODIFY );
//		pageContext.forward ("CreateTypeSubscription1.jsp?module=" + Module.getModuleForSpaceType(user.getCurrentSpace().getType()) + "&action="+net.project.security.Action.VIEW + "&referer=project/Setup.jsp&spaceID="+request.getParameter("spaceID"));

	else if (action.equals("reset"))
		pageContext.forward ("ManageSubscriptions.jsp");

	else if (action.equals("properties"))
		pageContext.forward ("ViewSubscriptionProperties.jsp?subscriptionID=" + request.getParameter("selected"));
		
	else if (action.equals("modify")) 	
		pageContext.forward ("EditSubscription.jsp?subscriptionID=" + subscriptionId + "&module=" + Module.getModuleForSpaceType(user.getCurrentSpace().getType()));	
	 else
		 pageContext.forward(JSPRootUrl +"/notification/ManageSubscriptions.jsp?spaceID=" + user.getCurrentSpace().getID());	
//		  pageContext.forward ("ManageSubscriptions.jsp?spaceID="+request.getParameter("spaceID"));

%>



