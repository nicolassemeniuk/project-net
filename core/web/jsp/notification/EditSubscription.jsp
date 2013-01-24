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
|   $Revision: 20905 $
|   $Date: 2010-06-03 03:29:00 -0300 (jue, 03 jun 2010) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="New Subscription Wizard -- Step 2"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.database.DatabaseUtils,
            net.project.util.DateFormat,
            net.project.hibernate.service.ServiceFactory,
            net.project.util.StringUtils,
            java.util.Date,
            net.project.util.DateUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="domainListBean" class="net.project.notification.DomainListBean" scope="session" />
<jsp:useBean id="editSubscription" class="net.project.notification.SubscriptionBean" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session"/>

<jsp:useBean id="groupProvider" class="net.project.security.group.GroupProvider" scope="page"/>
<%
	String id = request.getParameter("subscriptionID");
	String spaceID = request.getParameter("spaceID");
	String spaceImageUrl =  StringUtils.EMPTY;
	String spaceParticipantList = StringUtils.EMPTY;
	String itemName = StringUtils.EMPTY;
	Date createdDate = null;
	Date modifiedDate = null;
	boolean status = false;
	DateFormat dateFormat = DateFormat.getInstance();
	if(!((request.getParameter("theAction")!= null) && (request.getParameter("theAction").equals("next")))){
		editSubscription.clear();
	}
			
	editSubscription.setID(id);
	if (id != null) {
		editSubscription.load();
		spaceID = editSubscription.getSpaceID();
		itemName = ServiceFactory.getInstance()
				.getPnObjectNameService()
				.getObjectNameBySubscriptionId(Integer.valueOf(id));
   		if(PropertyProvider.isToken(itemName)){
   			itemName = PropertyProvider.get(itemName);
   		}
	spaceParticipantList = editSubscription.getSpaceParticipantGroups(user,spaceID);
	}
	status = editSubscription.getStatus();
	spaceImageUrl = editSubscription.getSpaceImageUrl();
	createdDate = editSubscription.getCreatedDate();
	modifiedDate = editSubscription.getModifiedDate();
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
	var editsubscriptionSeperateText = '<%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%>';
	var externalEmailAddressErrorMessage = '<%=PropertyProvider.get("prm.notification.externalEmailAddress.error.message")%>';
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    

	function setup() {
    	theForm = self.document.forms["main"];
    	isLoaded = true;
<%-- Avinash: bfd 3140 	External Email Addresses in Create/Modify suscription is not validated --%>    	
    	document.getElementById("txt").innerHTML='<%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%>';
    	focus(theForm, 'name');
	}

	function fieldCheck() {	
		var reg = new RegExp("teamMembers");
	
   		for (var i = 0; i < self.document.forms[0].elements.length; i++) {
    		if ((self.document.forms[0].elements[i].type == 'checkbox') && reg.test(self.document.forms[0].elements[i].name )) {
      			if(self.document.forms[0].elements[i].checked)
					return true;	
        	}
    	}
		
		return false ;
	}

	
	function validateForm(theForm) {
		var notificationType = document.getElementsByName('notificationType');
		if (!checkTextbox(theForm.name,'<%=PropertyProvider.get("prm.notification.editsubscription.namerequired.message")%>')) 
			return false;
		if((notificationType.length == 1) && (!notificationType[0].checked)){
			extAlert('<%=PropertyProvider.get("prm.resource.global.exterroralert.title")%>', '<%=PropertyProvider.get("prm.notification.createsubscription2.actiontonotifiedrequired.message")%>', Ext.MessageBox.ERROR);
			return false;
		} 
		if(notificationType.length > 1 && !checkCheckBox_NoSelect(theForm.notificationType,'<%=PropertyProvider.get("prm.notification.createsubscription2.actiontonotifiedrequired.message")%>')){ 
			return false;
		}
	<% if (securityProvider.isUserSpaceAdministrator()) { %>
		if(!checkExternalEmailList()) return false;
		<%}%>
		return true;
	}

	function processForm() {
		if(validateForm(theForm)) {
<%-- Avinash: bfd 3140 	External Email Addresses in Create/Modify suscription is not validated --%>		
				theForm.submit();
			}else{
			return false;
			}
	}
	
// doa  redirect on canceling
	function cancel() { 
		theAction("cancel");
		theForm.submit();
	}

	function back() {
		theAction("back");
		theForm.submit();
	}

	function finish() {
		if(validateForm(theForm)){
			theAction("finish");
			processForm();
		}
		else 
			return false;
	}

	function reset() {
		theForm.reset();
	}

	function help(){
		var helplocation=JSPRootURL+"/help/Help.jsp?page=notification_subscription&section=edit";
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
            <history:page display='<%=PropertyProvider.get("prm.notification.editsubscription.channel.edit.title")%>'
                          jspPage='<%=SessionManager.getJSPRootURL() + "/notification/EditSubscription.jsp"%>'
                          queryString='<%="?subscriptionID="+id%>'
                          level = "1"
             />
        </history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" name="main" action="<%=SessionManager.getJSPRootURL()%>/notification/EditSubscriptionProcessing.jsp">
    <input type="hidden" name="theAction">
	<input type="hidden" name="referrer" value="<%=request.getParameter("referrer")%>">
	<input type="hidden" name="spaceID" value="<%=request.getParameter("spaceID")%>">
	
<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <tr>
        <td nowrap colspan="3" class="editNotifyHeader"><%=PropertyProvider.get("prm.notification.editsubscription.channel.edit.title")%>
        </td>
        <td width="3%">&nbsp;</td>
    </tr>
    <tr valign="top">
        <td colspan="3" class="channelContent">&nbsp;</td>
    </tr>

	
    <tr>
        <td width="2%">&nbsp;</td>
        <td colspan="2">

<%-- Insert table containing editSubscription form --%>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="editTableContent" width="18%"><%=PropertyProvider.get("prm.notification.editsubscription.status.label")%></td>
		<td class="tableContent" ><input type="radio"
			id="status" name="status" value="true" <%= status ? "checked" : "" %>/><%=PropertyProvider.get("prm.notification.editsubscription.statusenable.label")%>
			&nbsp;&nbsp;&nbsp;
			<input type="radio" id="status" name="status" value="false" <%= status ? "" : "checked" %>/><%=PropertyProvider.get("prm.notification.editsubscription.statusdisable.label")%>
		</td>
	</tr>

	<tr>
		<td class="editTableContent" colspan="2"></td>
	</tr>	
	<tr> 
        <td class="editTableContent"><%=PropertyProvider.get("prm.notification.editsubscription.createdate.label")%></td>
        <td class="tableContent"> 
           	        <%=(DateUtils.isCurrentYear(createdDate) ? dateFormat.formatDate(createdDate, "MMM d") : 
							dateFormat.formatDate(createdDate, "MMM d, yyyy"))%>
					<%=PropertyProvider.get("prm.notification.viewsubscription.details.by.label")%>
           	        <%=editSubscription.getSubscriptionCreatedByName()%>
           	        <%if(!modifiedDate.equals(createdDate) ) {%>
 		            	<%=PropertyProvider.get("prm.notification.editsubscription.modified.label")%>
						<%=(DateUtils.isCurrentYear(modifiedDate) ? dateFormat.formatDate(modifiedDate, "MMM d") : 
							dateFormat.formatDate(modifiedDate, "MMM d, yyyy"))%>
           	        <%}%>
        </td>
    </tr>

	<tr> 
        <td class="notifyFieldRequired"><%=PropertyProvider.get("prm.notification.editsubscription.name.label")%></td>
        <td class="tableContent"> 
            <input type="text" name="name" value='<c:out value="${editSubscription.name}"/>' class="elementBorder notifyTextbox">
        </td>
    </tr>

    <tr> 
        <td class="editTableContent"><%=PropertyProvider.get("prm.notification.editsubscription.description.label")%></td>
        <td class="tableContent"><textarea rows="5" name="description" class="elementBorder notifyTextbox"><c:out value="${editSubscription.description}"/></textarea></td>
    </tr>

    <tr> 
        <td class="notifyFieldRequired" valign="top"><%=PropertyProvider.get("prm.notification.editsubscription.itemsnotified.label")%></td>
        <td>&nbsp;</td>
	</tr>

    <tr> 
        <td class="editTableContent" valign="top"><%=PropertyProvider.get("prm.notification.editsubscription.itemlocation.label")%></td>
        <td class="tableContent">
	        <span class="itemLocationLink">
				<%if(StringUtils.isNotEmpty(spaceImageUrl)){%>
					<img src="<%=spaceImageUrl%>"/>
				<%}%>
				<a href="<%=editSubscription.getSpaceUrl()%>"> 
				<%=editSubscription.getItemLocation()%></a>
			</span>
        </td>
	</tr>

    <tr> 
        <td class="editTableContent" valign="top"><%=PropertyProvider.get("prm.notification.editsubscription.itemtype.label")%></td>
        <td class="tableContent"><c:out value="${editSubscription.subscriptionType}"/></td>
	</tr>

    <tr> 
        <td class="editTableContent" valign="top"><%=PropertyProvider.get("prm.notification.editsubscription.itemname.label")%></td>
        <td class="tableContent"><%=StringUtils.isNotEmpty(itemName) ? itemName : editSubscription.getSubscriptionType()%></td>
	</tr>
    <tr> 
        <td class="notifyFieldRequired" valign="top" ><%=PropertyProvider.get("prm.notification.editsubscription.actionnotified.label")%></td>
        <td>&nbsp;</td>
	</tr>
        <%=editSubscription.getEventSubscribeCheckList(id)%>

	<% if (securityProvider.isUserSpaceAdministrator()) { %>
    <%-- Space Admins can send to any group --%>
	<tr> 
        <td class="editTableContent" valign="top"><%=PropertyProvider.get("prm.notification.editsubscription.deliverto.label")%></td>
		<td class="tableContent"> <%=spaceParticipantList%></td> 
	</tr>	
<%}%>
    <tr> 
        <td class="editTableContent"><%=PropertyProvider.get("prm.notification.editsubscription.deliverymethod.label")%></td>
        <td class="tableContent"> 
            <select name="deliveryTypeID" class="elementBorder">
                <%=domainListBean.getDeliveryTypesOptionList()%> 
            </select>
        </td>
	</tr>
	<tr>
        <td class="editTableContent"><%=PropertyProvider.get("prm.notification.editsubscription.deliveryschedule.label")%></td>
        <td class="tableContent"> 
            <select name="deliveryInterval" class="elementBorder">
                <%=domainListBean.getDeliveryIntervalOptionList()%> 
            </select>
        </td>
    </tr>

    <tr> 
        <td class="editTableContent" valign="top">
        	<% if (securityProvider.isUserSpaceAdministrator()) { %>
        		<%=PropertyProvider.get("prm.notification.editsubscription.selectaddmembers.label")%>
        	<% }%>
        </td>
        <td align="left"> 
	     <div class="notify-edit-expand" id="moreoptions" onClick="javascript: toggleOptions('more')"></div>
		 <div class="notify-edit-collapse" align="center" style="display: none;" id="lessoptions" onClick="javascript: toggleOptions('less')"></div> 
        </td>
    </tr>
	<tr id="extraItems" style="display: none;"><td colspan="2" width="100%">
		<table cellspacing="0" cellpadding="0" width="100%">
			<% if (securityProvider.isUserSpaceAdministrator()) { %>
			     <tr> 
			        <td class="editTableContent" width="18%"><%=PropertyProvider.get("prm.notification.editsubscription.externalemail.label")%></td>
			        <td>
			        <%-- Avinash: bfd 3140 	External Email Addresses in Create/Modify suscription is not validated --%>
			            <input type="text" class="elementBorder notifyTextbox" id="otherEmail" name="otherEmail" value='<c:out value="${editSubscription.externalSubscribersInCSV}"/>' onblur="return checkExternalEmailList();" />
			        </td>
			    </tr>
			    <tr> 
			    	<td>&nbsp;</td>
			       <td  class="tableContent" id="txt"><%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%></td>
				</tr>	
			<%}else{%>
			    <%-- Non-Space Admins can only send to themselves 
			         Note: This may throw a GroupException if there is
			         no principal group in this space. This only occurs
			         for Personal space, and in that case, the user is always
			         a Space Admin of their Personal Space, so this code is not executed --%>
			    <input type="hidden" name="teamMembers" class="elementBorder" value="<%=groupProvider.getPrincipalGroup(user.getCurrentSpace().getID(), user.getID()).getID()%>">
			<%}%>
		    <tr> 
			        <td class="editTableContent" valign="middle" width="18%"><%=PropertyProvider.get("prm.notification.editsubscription.message.label")%></td>
			        <td class="tableContent" > 
			            <textarea rows="5" name="customMessage" class="elementBorder" style="width: 260px;"><c:out value="${editSubscription.customMessage}"/></textarea>
			        </td>
		    </tr>
			</table>
	</td></tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	</table>
	</td>
	</tr>
	<%-- End Subscription form table --%>
		<tr>
	        <td align="center" colspan="3" class="notify-edit-bottom" height="40"><button type="button" style="width: 100px;" onClick="return finish();"> <%=PropertyProvider.get("prm.notification.createsubscription2.submitbutton.label")%></button>
	 		</td>
	 		<td width="3%">&nbsp;</td>
	    </tr>
</table>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>