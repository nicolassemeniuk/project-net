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
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
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
<%-- merely extract the parameter and forward it to the next page--%>
<%-- continue to fill in the subscription object--%>

<%
	subscription.clearNotificationTypes();

	String[] notifyTypes = request.getParameterValues ("notificationTypeID");

	subscription.setSpaceID (user.getCurrentSpace().getID());
	subscription.setIsTypeSubscription(subscription.getTargetObjectID() == null);

	if (notifyTypes.length > 0)
		for (int i = 0; i < notifyTypes.length; i++)
			subscription.addNotificationType( notifyTypes[i] );

	//subscription.assignDefaultInformationForNotificationType(); 
	// this has to be here and not before becasue until now we don't know the notification type                

     pageContext.forward("/notification/CreateSubscription2.jsp?theAction=create");
	 
%>

                
