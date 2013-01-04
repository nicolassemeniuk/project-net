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
|   $Revision: 20379 $
|   $Date: 2010-02-05 11:15:52 -0300 (vie, 05 feb 2010) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%-- 
    STATE: the user has filled in all the forms necessary for a subscription to be registered
    KNOWN- all the fields in the Subscription object. The Subscription object is ready to be passed into the database.
--%>
<%--
   GOAL: to enter the Subscription object into the database
 --%> 
<%@ page
    contentType="text/html; charset=UTF-8"
info="New Subscription Wizard -- Step 1 Processing"
     language="java"
     errorPage="/errors.jsp"
     import="net.project.security.User, 
             net.project.notification.*,
			 java.util.ArrayList,
			 net.project.util.ParseString,
	  		 net.project.security.SessionManager,
	  		 org.json.JSONObject,
	  		 net.project.util.StringUtils,
	  		 net.project.persistence.PersistenceException"
%>
<jsp:useBean id="subscription" class="net.project.notification.SubscriptionBean" scope="session"/>  
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<%
		String action = request.getParameter("theAction");
		String[] notifyTypes = null;
		ArrayList addressList = null;
		String[] teamMembers = null;
		String name = null;
		String description = null;
		String customMessage = null;
		String deliveryInterval = null;
		String status = null;
		String objectType = null;
		String spaceID = request.getParameter("spaceID");
		subscription.setSpaceID(StringUtils.isEmpty(spaceID) || spaceID.equals("null") ? user.getCurrentSpace().getID() : spaceID);
		subscription.setIsTypeSubscription(subscription.getTargetObjectID() == null);

	    if(action.equals("cancel")){
	    	response.sendRedirect(SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp?spaceID=" + user.getCurrentSpace().getID());
	    }

		
		if(action.equals("finish")){
		notifyTypes = request.getParameterValues("notificationType");
		if (notifyTypes.length > 0){
			for (int index = 0; index < notifyTypes.length; index++){
				subscription.addNotificationType(notifyTypes[index]);
			}
		}		
		addressList = ParseString.makeArrayListFromCommaDelimitedString(request.getParameter("otherEmail"));
		if (addressList != null) {
			for (int index=0; index < addressList.size(); index++){
				subscription.addExternalSubscriber((String)addressList.get(index), request.getParameter("deliveryTypeID"));
			}
		}
	
		teamMembers = request.getParameterValues("teamMembers");
		if (teamMembers != null && teamMembers.length > 0){
			for (int index = 0; index < teamMembers.length; index++){
				subscription.addSubscriber ( teamMembers[index],  request.getParameter("deliveryTypeID"));
			}
		}
	
		name = request.getParameter("name");
		description = request.getParameter("description");
		customMessage = request.getParameter("customMessage");                
		deliveryInterval = request.getParameter("deliveryInterval"); 
		objectType = request.getParameter("objectType"); 
		status = request.getParameter("status"); 
		
		subscription.setTargetObjectType(objectType);

	}

	if(action.equals("popup")){
		JSONObject jSONObject = new JSONObject(request.getParameter("newNotifyJsonObject"));
		notifyTypes = jSONObject.getString("notificationType").split(",");

	if (notifyTypes.length > 0){
		for (int index = 0; index < notifyTypes.length; index++){
			subscription.addNotificationType(notifyTypes[index]);
		}
	}		
	
	addressList = ParseString.makeArrayListFromCommaDelimitedString(jSONObject.getString("otherEmail"));
	
	if (addressList != null) {
		for (int index=0; index < addressList.size(); index++){
			subscription.addExternalSubscriber((String)addressList.get(index), jSONObject.getString("deliveryTypeID"));
		}
	}

	teamMembers = jSONObject.getString("teamMembers").split(",");
	if (teamMembers != null && teamMembers.length > 0){
		for (int index = 0; index < teamMembers.length; index++){
			subscription.addSubscriber ( teamMembers[index],  jSONObject.getString("deliveryTypeID"));
		}
	}
	
	name = jSONObject.getString("name");	
    description = jSONObject.getString("description");
    customMessage = jSONObject.getString("customMessage");                
    deliveryInterval = jSONObject.getString("deliveryInterval");
    status = jSONObject.getString("status"); 
	}

	subscription.setName(name);
	subscription.setDescription(description);
	subscription.setCustomMessage(customMessage);                
	subscription.setDeliveryIntervalID(deliveryInterval); 
	subscription.setStatus(Boolean.valueOf(status).booleanValue()); 

	if(action.equals("finish") || (action.equals("popup"))){
		try{
		 	subscription.store();
	    } catch(PersistenceException pnetEx) {
	    	System.out.println("Error occurred while storing subscription :"+ pnetEx.getMessage());	
	    }
	}
	
	subscription.clear();
    
    if(action.equals("finish")){
    	response.sendRedirect(SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp?spaceID=" + user.getCurrentSpace().getID());
    }
  
%>