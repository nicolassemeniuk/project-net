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
|   $Revision: 18441 $
|       $Date: 2008-11-30 13:43:50 -0200 (dom, 30 nov 2008) $
|     $Author: vivana $
|--------------------------------------------------------------------%>
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
			net.project.base.ObjectFactory"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
                                                                 
<jsp:useBean id="notificationObjectTypeCollection" class="net.project.notification.NotificationObjectTypeCollection" scope="request"/> 
<jsp:useBean id="domainListBean" class="net.project.notification.DomainListBean" /> 
<jsp:useBean id="subscription" class="net.project.notification.SubscriptionBean" scope="session"/> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
 
<%--  rig the javascript which will react appropriately to the userz actions, including sending the Form--%> 
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<%
	if(request.getParameter("referer")!= null && request.getParameter("referer").equals("project/Setup.jsp")){
		subscription.clear();
	}
	
	subscription.setUser(user);
    subscription.setCreatedByID(user.getID());
    subscription.setSubscriptionSchedule( new SubscriptionSchedule());
    
    String spaceID = request.getParameter("spaceID");
%>
<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel() { self.close(); }

function next() {
	if (!checkDropdown(theForm.objectType,'<%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.message")%>')) {
	} else {
		if( !(theForm.notificationTypeID == undefined )){	
			if(!checkDropdown_NoSelect(theForm.notificationTypeID,'<%=PropertyProvider.get("prm.notification.createtypesubscription1.selecteventtype.message")%>')) {
			} else {
				theAction("next");
       			theForm.submit();
			}
		} else {
			var errorMessage = '<%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}
	}	
}
function reset() {
	theForm.reset();
}

function loadEventTypes() {
	if (!checkDropdown(theForm.objectType,'<%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.message")%>')) return false;
    theAction("loadEventTypes");
    theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=notification_subscription&section=create_type_subscription";
	openwin_help(helplocation);
}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.notifications.link">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.project.setup.notifications.link")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp"%>'
						  queryString='<%= "spaceID="+spaceID%>'
			/> 
			<history:page display='<%= PropertyProvider.get("prm.notification.createtypesubscription1.module.history") %>' />
		</history:history>		
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="subscriptionWizard1" method="post" action="<%=SessionManager.getJSPRootURL()%>/notification/CreateTypeSubscription1Processing.jsp">
    <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
  <p>
    <input type="hidden" name="theAction">
	<input type="hidden" name="module" value='<%=request.getParameter("module")%>'>
	<input type="hidden" name="referrer" value="<%=SessionManager.getJSPRootURL()%>/notification/CreateTypeSubscription1.jsp">
	<%-- this is probably gone now --%> </p>
  <table width="97%" cellspacing="0" cellpadding="0" vspace="0"  border="0">
  <tr>
<td colspan="8">
<channel:channel name='<%="CreateTypeSubscriptions1" %>' customizable="false">
    <channel:insert name="projectPortfolio" title='<%=PropertyProvider.get("prm.notification.createtypesubscription1.channel.create.title")%>' row="0" column="0"
                    width="100%" minimizable="false" closeable="false">	
    </channel:insert>
</channel:channel>	
</tr>
  <tr>
    <tr>
	 <td width="1%"></td>
      <td class="fieldRequired"><%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.label")%></td>
      <td align="left">
<%
		notificationObjectTypeCollection.loadSubscribable();
%>	  
	  
        <select name="objectType" onchange="loadEventTypes()">
		<option value=""><%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.option")%></option>
          <%=notificationObjectTypeCollection.getOptionList(subscription.getTargetObjectType())%> 
		</select> 
      </td>
    </tr>
	 <tr>
  	<td>&nbsp;</td></tr>
    <tr>
	<% if (subscription.getTargetObjectType() != null) { %>
	 <td width="1%"></td>
	  <td class="fieldRequired"><%=PropertyProvider.get("prm.notification.createtypesubscription1.selectevent.label")%></td>
      <td align="left">
        <select name="notificationTypeID" size="5" multiple>
          <%-- start with a GUID from the folder selected , then derive its object type so we can access the object type specific notifications object list --%> 
		  <%=domainListBean.getNotificationTypesOptionsList(subscription.getTargetObjectType(), null)%> 
        </select>
      </td>
    </tr>
	 <tr>
  	<td>&nbsp;</td></tr>
  	<% } %>
	<tr>
	<td width="1%"></td>
      <td ></td>
      <td></td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
		<tb:band name="action">
			<tb:button type="next" />
		</tb:band>
	</tb:toolbar>

  </form>
<%--
<%=toolbar.drawActionbar()%>
--%>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
