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
|   $Revision: 20534 $
|   $Date: 2010-03-05 11:09:08 -0300 (vie, 05 mar 2010) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page info="Gathers, formats and then dispalys all the subscriptions this user has ."
    contentType="text/html; charset=UTF-8"
	language="java"
	errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.notification.Subscription"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="subscriptionBean" class="net.project.notification.SubscriptionBean" scope="session"/> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>  
<%
	String subscriptionID = request.getParameter("subscriptionID");
	String spaceID = request.getParameter("spaceID");
	boolean isPermitted = Subscription.isSubscriptionCreator(subscriptionID , user.getID());
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;

    function setup() {
		theForm = self.document.forms[0];
		isLoaded = true;
	}

    function back() {
		theAction("back");
		theForm.submit();
	}

    function remove() {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.project.setup.notificationremovalwarning.message" />', function(btn) { 
				if(btn == 'yes'){ 
					theAction ("remove");
					theForm.submit();
				}else{
				 	return false;
				}
		});
	}

    function modify() {
	   theAction ("modify");
	   theForm.submit();
    }
   
	function help() {
       var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=notification_subscription&section=properties";
       openwin_help(helplocation);
    }


</script>

<template:import type="javascript" src="/src/checkRadio.js" />
<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.personal.setup.notifications.link">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.notification.viewsubscription.channel.details.title")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp"%>'
						  queryString='<%= "spaceID="+spaceID%>'
						  level = "1"
			/> 
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	<%if(isPermitted){%>
		<tb:button type="remove" label='<%=PropertyProvider.get("notification.viewsubscription.remove.link")%>' function="javascript:remove();"/>
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.notification.viewsubscription.modify.tooltip")%>'/>
	<%}%>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="ViewSubscriptionPropertiesProcessing.jsp" method="POST"> 
<input type="hidden" name="theAction">
<input type="hidden" name="subscriptionID" value='<%=subscriptionID%>' >
<input type="hidden" name="spaceID" value='<%=spaceID%>' >
<table width="95%" border="0" cellspacing="0" cellpadding="0">
	
    <tr>
        <td nowrap class="editNotifyHeader" colspan="4"><%=PropertyProvider.get("prm.notification.viewsubscription.channel.details.title")%></td>
    </tr>
<%
		subscriptionBean.clear();
		subscriptionBean.setUser(user);
	  	subscriptionBean.setID(subscriptionID);	
%>
		
		<tr><td width="2%"></td>
		<td colspan="3">
		   <jsp:setProperty name="subscriptionBean" property="stylesheet" value="/notification/xsl/view-subscription.xsl" /> 
           <jsp:getProperty name="subscriptionBean" property="subscriptionPresentation" /> 
        </td></tr>                 
	    <tr>
	        <td colspan="4" class="notify-edit-bottom" height="40" align="center"></td>
	    </tr>		  
</table>


</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>  

                           