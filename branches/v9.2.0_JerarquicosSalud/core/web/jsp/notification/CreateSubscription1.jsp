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
    info="New Subscription Wizard -- Step 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User, 
            net.project.notification.*,
            net.project.security.SessionManager,
            net.project.database.DatabaseUtils,
            net.project.base.ObjectFactory,
            net.project.resource.Roster,
            net.project.security.AuthorizationFailedException"
%>                                                                        
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="domainListBean" class="net.project.notification.DomainListBean" /> 
<jsp:useBean id="subscription" class="net.project.notification.SubscriptionBean" scope="session"/> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%
    Roster roster = new Roster();
    roster.setSpace(user.getCurrentSpace());
    roster.load();

    if (!roster.hasPerson(user)) {
        throw new AuthorizationFailedException ("Only members of this workspace are permitted to create new notification subscriptions.  Please contact the administrator(s) of this workspace to request an invitation to be a participant.");
    } else {
%>

<%
    String objectType = null;
    String userResponse = null;
    String targetObjectID = request.getParameter("selected");
	String isCreateType = request.getParameter("isCreateType");
	objectType = request.getParameter("objectType");

    if (targetObjectID != null && !targetObjectID.equals("") && !targetObjectID.equals("undefined"))
        subscription.setTargetObjectID(targetObjectID); 
    else
	    targetObjectID = subscription.getTargetObjectID();

    if (objectType != null) {
    	subscription.setTargetObjectType(objectType);
    }    
    
    objectType = objectType != null ? objectType : DatabaseUtils.getTypeForObjectID (targetObjectID); // put this in the call to domainListBean below
     
    subscription.setUser(user);
    subscription.setCreatedByID(user.getID());
    subscription.setSubscriptionSchedule( new SubscriptionSchedule());
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/ext-components.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<%-- ExtJs Component --%>
<template:import type="javascript" src="/src/extjs/adapter/ext/ext-base.js" />
<template:import type="javascript" src="/src/extjs/ext-all.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
    focusFirstField(theForm);
}

// do a  redirect on canceling
function cancel() { self.close(); }

function next() {
	if (theForm.notificationTypeID.selectedIndex == -1 ||(theForm.notificationTypeID.selectedIndex == 0 && theForm.notificationTypeID.options[0].value == '')){
		extAlert('<display:get name="prm.resource.global.exterroralert.title"/>', '<display:get name="prm.notification.createsubscription1.selecteventtype.message" />', Ext.MessageBox.ERROR);
	} else {
       theAction("next");
       theForm.submit();
	}
}

function reset() {
	theForm.reset();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=notification_subscription&section=create_step1";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();" >

<form method="post" name="subscriptionWizard1" action="<%=SessionManager.getJSPRootURL()%>/notification/CreateSubscription1Processing.jsp">
    <input type="hidden" name="theAction">

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
        <td>&nbsp;</td>
	</tr>	
	
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
        <td nowrap class="channelHeader"><%=PropertyProvider.get("prm.notification.createsubscription1.channel.create.title")%>&nbsp;<b><%=PropertyProvider.get("prm.notification.createsubscription1.channel.create.fields.required")%></b></td>
        <td align="right" class="channelHeader">&nbsp;</td>
        <td align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
    </tr>
    <tr valign="top">
        <td colspan="4" class="channelContent">&nbsp;</td>
    </tr>
	
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">

<%-- Insert table containing subscription form --%>
<table width="100%" border="0">

    <tr> 
        <td class="fieldRequired"><%=PropertyProvider.get("prm.notification.createsubscription1.selectevent.label")%></td>
    </tr>

    <tr>
        <td>
            <%-- Get the notification types for the object type;  calling
                 this method sets the notification type count --%>
            <%String notificationTypes = domainListBean.getNotificationTypesOptionsList(objectType, isCreateType);%>
            <select name="notificationTypeID" size='<%=domainListBean.getNotificationTypeCount()%>' multiple>                  
                <%=notificationTypes%>
            </select>
        </td>
    </tr>

</table>

        </td>
        <td>&nbsp;</td>
    </tr>
	
	<tr>
        <td>&nbsp;</td>
	</tr>	
</table>

<tb:toolbar style="action" showLabels="true" >
            <tb:band name="action">
                <tb:button type="next" />
		        <tb:button type="cancel" />
		        
            </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>


<%
    }
%>