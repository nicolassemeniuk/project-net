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
|   $Revision: 20701 $
|   $Date: 2010-04-14 13:13:17 -0300 (mié, 14 abr 2010) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
info="New Subscription Wizard -- Step 1 Processing"
     language="java"
     errorPage="/errors.jsp"
     import="net.project.security.User, 
             net.project.notification.*,java.util.ArrayList, net.project.util.ParseString,
	  		 net.project.security.SessionManager,java.util.Iterator,
	  		 net.project.space.SpaceType,
			 net.project.space.SpaceTypes"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="editSubscription" class="net.project.notification.SubscriptionBean" scope="session"/>
<%
	String JSPRootUrl = SessionManager.getJSPRootURL();

    //editSubscription.clearSubscribers();
    //editSubscription.setTargetObjectID(request.getParameter("spaceID"));

    // add external subscribers
    ArrayList addressList = ParseString.makeArrayListFromCommaDelimitedString (request.getParameter("otherEmail"));

    if (addressList != null) {

        for (int i=0; i < addressList.size(); i++)
            editSubscription.addExternalSubscriber ( (String)addressList.get(i), request.getParameter("deliveryTypeID") );
    }
    
    /*
    SubscriberCollection oldSubscriberCollection=editSubscription.getSubscriberCollection();
    
    SubscriberCollection newSubscriberCollection = new SubscriberCollection();
    newSubscriberCollection.setSubscriptionID (editSubscription.getID());
    newSubscriberCollection.setBatchID(editSubscription.getSubscriberCollection().getBatchID());
    
    editSubscription.setSubscriberCollection(newSubscriberCollection);
    */								

	String[] notifyTypes = request.getParameterValues("notificationType");
    editSubscription.clearNotificationTypes();
	if (notifyTypes!=null && notifyTypes.length >= 0 ){
		for (int index = 0; index < notifyTypes.length; index++){
			editSubscription.addNotificationType(notifyTypes[index]);
		}
	}		

    editSubscription.setDeliveryTypeID(request.getParameter("deliveryTypeID"));
    editSubscription.setName(  request.getParameter("name") );
    editSubscription.setDescription(  request.getParameter("description") );
    editSubscription.setCustomMessage(  request.getParameter("customMessage") );                
    editSubscription.setDeliveryIntervalID(  request.getParameter("deliveryInterval") ); 
	editSubscription.setStatus(Boolean.valueOf(request.getParameter("status")).booleanValue());

	    editSubscription.update();
	    
	    if(SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.PERSONAL)){
		   	response.sendRedirect(JSPRootUrl +"/notification/ManageSubscriptions.jsp?disabled=true");	
	    } else {
		   	response.sendRedirect(JSPRootUrl +"/notification/ManageSubscriptions.jsp?spaceID="+user.getCurrentSpace().getID());	
	    } 
//	    response.sendRedirect("ManageSubscriptions.jsp?disabled=false");
    
    
    if (request.getParameter("theAction").equals("cancel")) {
        pageContext.forward("ClosePopup.jsp");
    }
    
    else if (request.getParameter("theAction").equals("back")) {
        String backPage = request.getParameter("referrer");

        if (backPage != null && !backPage.equals(""))
            pageContext.forward (backPage);
        else
            pageContext.forward("CreateSubscription1.jsp");
    }
    

%>
