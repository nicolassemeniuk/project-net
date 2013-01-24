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

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Manage Subscriptions" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.notification.*,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.base.property.*,
			net.project.space.SpaceTypes"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="subscriptionManagerBean" class="net.project.notification.SubscriptionManagerBean" scope="request"/> 
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
     subscriptionManagerBean.setUser(user);
	 boolean enableCreateButton = PropertyProvider.getBoolean("prm.global.notification.globalsubscription.isenabled") ;

	String spaceID=request.getParameter("spaceID");

	if(user.getCurrentSpace().getSpaceType().equals(SpaceTypes.PERSONAL)){
		enableCreateButton = false;
		spaceID="";
	}
	
	if(spaceID == null){
		spaceID = "";
	}	
	
%>


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:getSpaceCSS />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />


<script language="javascript">
		var theForm;
		var isLoaded = false;
		var editPermissioMessage = '<%=PropertyProvider.get("prm.notification.managesubscriptions.editnotification")%>';
		var deletePermissioMessage = '<%=PropertyProvider.get("prm.notification.managesubscriptions.deletenotification")%>';
    function setup() {
       theForm = self.document.manageSubscriptions;
       isLoaded = true;
	}
	
    function create() {
	   theAction ("create");
	   theForm.submit();
    }

    function remove() {
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		<%-- bfd 3236: User is not getting any warning message when deleting the notification subscriptions. --%>
		<%-- Only creator of notificaion can remove notification --%>
			if(isPermitted()){
				Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.project.setup.notificationremovalwarning.message" />', function(btn) { 
					if(btn == 'yes'){ 
				   		theAction ("remove");
				   		theForm.submit();
					}else{
					 	return false;
					}
				});
			} else {
				extAlert(errorTitle, deletePermissioMessage , Ext.MessageBox.ERROR);
			}
		}
	}

    function modify() {
    	<%-- Only creator of notificaion can modify notification --%>
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			if(isPermitted()){
				theAction ("modify");
				theForm.submit();
			} else {
				extAlert(errorTitle, editPermissioMessage , Ext.MessageBox.ERROR);
			}
		}				
	}
    
    function properties() {
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	   		theAction ("properties");
	   		theForm.submit();
		}	
    }

	function reset() {
	   theForm.reset();
    }

	function help() {
       var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=notification&section=manage_subscriptions";
       openwin_help(helplocation);
    }

	// function to check userId and subscription creator's id equals or not
	function isPermitted() {
		var createdById = document.getElementById("createdById"+getSelection(theForm)).value;
		var userId = '<%=user.getID()%>'; 
		return (userId == createdById); 
	}
</script>

</head>

<%---------------------------------------------------------------------------------- 
    SET the stylesheet into the managerbean and then retrieve all the 
	subscriptions from the database and format them using that style sheet 
	and the XMLFormatter from Bluestone.
-----------------------------------------------------------------------------------%>  


<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Draw Toolbar
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" groupTitle="prm.personal.setup.notifications.link">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.notification.managesubscriptions.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp?spaceID="+spaceID%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.notification.managesubscriptions.edit.tooltip")%>'/>
		<%if(enableCreateButton){%>
		<tb:button type="create" label='<%=PropertyProvider.get("prm.notification.managesubscriptions.create.tooltip")%>'/>
		<%}%>
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.notification.managesubscriptions.delete.tooltip")%>'/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="manageSubscriptions" action="ManageSubscriptionProcessing.jsp" method="POST">
<input type="hidden" name="module" value='<%=request.getParameter("module")%>'>
<input type="hidden" name="action" value='<%=net.project.security.Action.MODIFY%>'>
<input type="hidden" referer="project/Setup.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="spaceID" value='<%=spaceID%>'>
<br clear="all" />

	 <table border="0" cellspacing="0" cellpadding="0">
			<tr class="channelHeader">
	    	    <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		        <td nowrap class="channelHeader" align="left" width="85%"><%=PropertyProvider.get("prm.notification.managesubscriptions.channel.manage.title")%></td>
				<td nowrap class="channelHeader">&nbsp;</td>
		       	<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	        </tr>
	<tr class="channelHeader">
		        <td class="channelContent">&nbsp;</td>
	<td colspan="2" align="center">

        <jsp:setProperty name="subscriptionManagerBean" property="stylesheet" value="/notification/xsl/subscriptions-collection.xsl" /> 
<%
		out.println(subscriptionManagerBean.getSubscriptionsPresentation(spaceID));
%>
		

	 </td> 
     </tr>
  </table> 

</form>

    <%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>                           