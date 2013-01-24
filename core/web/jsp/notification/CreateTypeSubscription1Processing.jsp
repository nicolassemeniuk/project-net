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
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
|
|--------------------------------------------------------------------%>
<%-- 
    STATE: the user has chosen a notificaiton type to create and processing has been passed to  this page.
    KNOWN-user, objectID, subscription object is being created. and notification  type has been selected
--%>
<%--
   GOAL: to enter the notificationType into the being-init'ed Subscription and then pass control to the display-generating page
 --%>
<%@ page
    contentType="text/html; charset=UTF-8"
		info="New Subscription Wizard -- Step 1 Processing"
     	language="java"
     	errorPage="/errors.jsp"
     	import="net.project.security.User, 
     		 	net.project.notification.*,
			 	net.project.security.SessionManager"
%>
<jsp:useBean id="subscription" class="net.project.notification.SubscriptionBean" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>  
<%--  get the users response to ascertain what type of subscription they may want to generate --%> 
<%         
    String objectType = request.getParameter("objectType");
	String forwardPage = null;

	subscription.clearNotificationTypes();

	subscription.setIsTypeSubscription();
	subscription.setUser(user);
	subscription.setCreatedByID(user.getID());
	subscription.setSubscriptionSchedule( new SubscriptionSchedule());
	subscription.setSpaceID(user.getCurrentSpace().getID());
	subscription.setTargetObjectType(objectType);

    if (request.getParameter("theAction").equals("loadEventTypes")) {
		forwardPage = "/notification/CreateTypeSubscription1.jsp?module="+request.getParameter("module")+"&action="+net.project.security.Action.MODIFY;
	} else {
		String[] notifyTypes = request.getParameterValues ("notificationTypeID");
		if (notifyTypes.length > 0)
			for (int i = 0; i < notifyTypes.length; i++) {
				subscription.addNotificationType( notifyTypes[i] );
			}
			
			forwardPage = "/notification/CreateSubscription2.jsp?popup=0";
	}

    pageContext.forward( forwardPage );

%>



                
