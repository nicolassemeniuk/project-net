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

<%@ page contentType="text/html; charset=UTF-8"
	info="New Subscription Wizard -- Step 2" language="java"
	errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.notification.*,
            net.project.security.SessionManager,
            net.project.security.group.GroupProvider,
            net.project.database.DatabaseUtils,
            net.project.security.SecurityProvider,
            net.project.document.DocumentManagerBean,
            net.project.base.Module,
            net.project.hibernate.service.ServiceFactory,
            net.project.util.StringUtils,
            net.project.space.Space,
			net.project.space.SpaceFactory,
			net.project.space.ISpaceTypes"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="domainListBean"
	class="net.project.notification.DomainListBean" scope="session" />
<jsp:useBean id="subscription"
	class="net.project.notification.SubscriptionBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider"
	class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="groupProvider"
	class="net.project.security.group.GroupProvider" scope="page" />
<jsp:useBean id="notificationObjectTypeCollection" class="net.project.notification.NotificationObjectTypeCollection" scope="request"/> 

<%

	DocumentManagerBean docManager = SessionManager.getDocumentManager();
	String id = request.getParameter("subscriptionID");
	String spaceID = request.getParameter("spaceID");
	String targetObjectID = request.getParameter("targetObjectID");
	String objectType = request.getParameter("objectType");
	String isCreateType = request.getParameter("isCreateType");
	String module = request.getParameter("module");
	String moduleType = request.getParameter("moduleType");
	String checkPopup = request.getParameter("popup");
	String projectID = request.getParameter("projectID");
    String itemName = StringUtils.EMPTY;
    String imageUrl = StringUtils.EMPTY;
	String spaceParticipantGroupCheckBoxList =  StringUtils.EMPTY ;
	String teamMemberId =  StringUtils.EMPTY;
	String tdWidth = null;
	int action = securityProvider.getCheckedActionID();
	boolean isUserSpaceAdministrator = securityProvider.isUserSpaceAdministrator();    

	if(StringUtils.isNotEmpty(projectID)){
		securityProvider.setSpace(SpaceFactory.constructSpaceFromID(projectID));
		isUserSpaceAdministrator = securityProvider.isUserSpaceAdministrator();		
	} 
	
	if(isUserSpaceAdministrator){
		spaceParticipantGroupCheckBoxList = subscription.getSpaceParticipantGroupCheckBoxList(user, spaceID);    	
	}else {
		if(StringUtils.isNotEmpty(projectID)){
			teamMemberId = groupProvider.getPrincipalGroup(projectID, user.getID()).getID();
		} else {
			teamMemberId = groupProvider.getPrincipalGroup(user.getCurrentSpace().getID(), user.getID()).getID();
		}
	}   
	subscription.clear();
	
	session.setAttribute("currentVersionID", request.getParameter("versionID"));
    request.setAttribute("module", request.getParameter("module"));
    session.setAttribute("docManager", docManager);
	

	if (StringUtils.isNotEmpty(targetObjectID)) {
        subscription.setTargetObjectID(targetObjectID); 
   		itemName = ServiceFactory.getInstance().getPnObjectNameService().getNameFofObject(Integer.valueOf(targetObjectID));
   		if(PropertyProvider.isToken(itemName)){
   			itemName = PropertyProvider.get(itemName);
   		}
		imageUrl = subscription.getObjectIconUrl(Integer.valueOf(targetObjectID));	
	} else {
	    targetObjectID = subscription.getTargetObjectID();		
	}

    if (StringUtils.isNotEmpty(objectType)) {
    	subscription.setTargetObjectType(objectType);
    }    
	
    if( StringUtils.isNotEmpty(module)){
    	if(Module.DIRECTORY == Integer.valueOf(module)){
    		imageUrl = SessionManager.getJSPRootURL() + "/images/notify/directory_icon.png";
    	}
	}

    objectType = StringUtils.isNotEmpty(objectType) ? objectType : DatabaseUtils.getTypeForObjectID (targetObjectID); // put this in the call to domainListBean below
    subscription.setUser(user);
    subscription.setCreatedByID(user.getID());
    subscription.setSubscriptionSchedule( new SubscriptionSchedule());
    tdWidth = StringUtils.isEmpty(checkPopup) ? "35%" : "18%";    	
%>

<template:getDoctype />

<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<% if (StringUtils.isNotEmpty(checkPopup) && checkPopup.equals("0")) { %>
<template:getSpaceCSS/>
<% } %>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript" type="text/javascript">/*Start Script*/
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	editsubscriptionSeperateText = '<%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%>';
	externalEmailAddressErrorMessage = '<%=PropertyProvider.get("prm.notification.externalEmailAddress.error.message")%>';
	loadingImage = '<%=PropertyProvider.get("prm.global.loading.message")%>';
	nameRequiredErrorMessage = '<%=PropertyProvider.get("prm.notification.editsubscription.namerequired.message")%>'; 
	actionToNotifiedrequiredErrorMessage = '<%=PropertyProvider.get("prm.notification.createsubscription2.actiontonotifiedrequired.message")%>'; 
	deliverToRequiredErrorMessage = '<%=PropertyProvider.get("prm.notification.createsubscription2.delivertorequired.message")%>';
	savingMessage = '<%=PropertyProvider.get("prm.global.saving.message")%>';
	isPersonalSpace = '<%=user.getCurrentSpace().getType().equals(ISpaceTypes.PERSONAL_SPACE) %>';
	notificationSavingFailed = '<%=PropertyProvider.get("prm.notification.createsubscription2.notificationsavingfailed.message")%>';
	
	function setup() {
    	theForm = self.document.forms["main"];
    	isLoaded = true;
		
		<% if (isUserSpaceAdministrator) { %>
			document.getElementById("otherEmailResult").innerHTML='<%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%>';
		<%}%>
		focus(theForm, 'name');
	}

	function fieldCheck() {	
		var reg = new RegExp("teamMembers");
   		for (var i = 0; i < theForm.elements.length; i++) {
    		if ((theForm.elements[i].type == 'checkbox') && reg.test(theForm.elements[i].name )) {
      			if(theForm.elements[i].checked)
					return true;	
        	}
    	}
		return false ;
	}

	function validateForm(theForm) {
		if (!checkTextbox(theForm.name, nameRequiredErrorMessage)) return false;
		if (theForm.objectType.selectedIndex == -1 || (theForm.objectType.selectedIndex == 0 && theForm.objectType.options[0].value == '')){
			extAlert('<%=PropertyProvider.get("prm.resource.global.exterroralert.title")%>', '<%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.message")%>', Ext.MessageBox.ERROR);
			return false;
		}
		if (!checkCheckBox_NoSelect(theForm.notificationType,'<%=PropertyProvider.get("prm.notification.createtypesubscription1.selecteventtype.message")%>')) return false;

		<% if (isUserSpaceAdministrator) { %>
		if (!fieldCheck()){
			extAlert('<%=PropertyProvider.get("prm.resource.global.exterroralert.title")%>',  deliverToRequiredErrorMessage, Ext.MessageBox.ERROR);
			return false;
		} 
		if(!checkExternalEmailList()) return false;
		<% } %>		
		return true;
	}

	function processForm() {
		if(validateForm(theForm)) {
				theForm.submit();
			}else{
				<% if (isUserSpaceAdministrator) { %> 
				document.getElementById("otherEmailResult").innerHTML='<%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%>&nbsp;<span class="errorMessage"><%=PropertyProvider.get("prm.notification.externalEmailAddress.error.message")%></span>';
				<% } %>
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
		theAction("finish");
		if(validateForm(theForm)){
			theForm.submit();
		} else {
			return false;
		}
	}

	function reset() {
		theForm.reset();
	}

	function help() {
		var helplocation=JSPRootURL+"/help/Help.jsp?page=notification_subscription&section=create_step1";
		openwin_help(helplocation);
	}

	function loadEventTypes() {
		var selectedObjectType = document.getElementById('objectType').selectedIndex;
		var objectType = document.getElementById('objectType').options[selectedObjectType].value;
		var url = JSPRootURL +'/notification/create/loadEvents?module=<%=Module.PERSONAL_SPACE%>'
		document.getElementById('eventTypes').innerHTML = loadingImage + '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
				
		Ext.Ajax.request({
		    url: url,
		    params: {objectType : objectType },
		    method: 'POST',
		    success: function(result, request){
		    	document.getElementById('eventTypes').innerHTML = '<table border="0" cellpadding="0" cellspacing="0" >' + result.responseText + '</table>';
		    },
		    failure: function(result, response){
		    	extAlert(errorTitle, '<%=PropertyProvider.get("prm.global.requestfailed.error.message")%>' , Ext.MessageBox.ERROR);
		    }
		});
	}
/*End Script*/</script>
</head>

<% if (StringUtils.isNotEmpty(checkPopup) && checkPopup.equals("0")) { %>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true"
	groupTitle="prm.personal.setup.notifications.link">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page
				display='<%=PropertyProvider.get("prm.notification.createsubscription2.channel.create.title")%>'
				jspPage='<%=SessionManager.getJSPRootURL() + "/notification/ManageSubscriptions.jsp"%>'
				queryString='<%="?spaceID="+spaceID%>' 
				level = "1"
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>
<div id='content'>
<form method="post" name="main" action="<%=SessionManager.getJSPRootURL()%>/notification/CreateSubscription2Processing.jsp">
    <input type="hidden" name="theAction">
	<input type="hidden" name="referrer" value="<%=request.getParameter("referrer")%>">
	<input type="hidden" name="spaceID" value="<%=request.getParameter("spaceID")%>">
<table width="100%" cellpadding="0" cellspacing="0">
<% } %>
<% if (StringUtils.isEmpty(checkPopup)) { %>
<body class="main" onLoad="setup();">
<table width="100%" cellpadding="0" cellspacing="0">
<tr><td colspan="3">
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
			<td width="4" class="top_left"></td>
                <td  class="top_m"><%=PropertyProvider.get("prm.notification.createsubscription2.channel.create.title")%></td>
                <td class="top_m" height="15" align="right" title="<%=PropertyProvider.get("all.global.channelbarbutton.title.close")%>"><a href="javascript:hideNotifyPopup();"><div id="hidePopup"></div></a></td>
                <td width="4" class="top_right"></td>
			</tr>
			<%if(StringUtils.isNotEmpty(targetObjectID)){%>
			<tr>
				<td class="m_left" width="4"></td>
                <td class="notification-itemName" colspan="2" >
                	<div>	
                		<%if(StringUtils.isNotEmpty(imageUrl)){%>
                			<img src="<%=imageUrl%>" align="absmiddle"/>
                		<% } %>
                		&nbsp;<%= StringUtils.isNotEmpty(itemName) ? itemName : ""%>
                	</div>
                </td>
                <td class="m_right" width="4"></td>
			</tr>
			<% } %>
		</table>
</td></tr>
<tr><td class="m_left" width="4"></td>
	<td id="notify-tablepad">
			<table width="100%" border="0">
<%} else { %>
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td nowrap class="editNotifyHeader"><%=PropertyProvider.get("prm.notification.createsubscription2.channel.create.title")%></td>
		<td width="3%">&nbsp;</td>
	</tr>        			
<tr><td id="notify-tablepad">
 		<table width="100%" border="0">
<%}%> 
				<tr>
					<td class="notifyFieldRequired statusPadding" width="<%=tdWidth%>"><%=PropertyProvider.get("prm.notification.createsubscription2.status.label")%></td>
					<td class="tableContent statusPadding">
					<input type="radio" id="status" name="status" value="true" checked="checked" ><%=PropertyProvider.get("prm.notification.createsubscription2.statusenable.label")%></input>
					&nbsp;&nbsp;&nbsp;
					<input type="radio" id="status" name="status" value="false"><%=PropertyProvider.get("prm.notification.createsubscription2.statusdisable.label")%></input>
					</td>
				</tr>
				<tr> <td height="20px" colspan="2"></td> </tr>
				<tr>
					<td class="notifyFieldRequired"><%=PropertyProvider.get("prm.notification.createsubscription2.name.label")%></td>
					<td class="fieldRequired"><input type="text" id="name" name="name" class="elementBorder notifyTextbox"
					 value='<c:out value="${subscription.name}" />'size="60" maxlength="60">
					 </td>
				</tr>
		
				<%-- 11/07/2001 - Tim - Why wouldn't this be editable?  Where does it come from; this
		     JSP is for creating a subscription, not editing one?
		    <tr> 
		        <td class="tableHeader">Description:</td>
		        <td class="tableHeader" colspan="3"><%=subscription.getDescription()%></td>
		    </tr>
		--%>
				<tr>
					<td class="editTableContent"  valign="top"><%=PropertyProvider.get("prm.notification.createsubscription2.description.label")%></td>
					<td class="tableContent">
						<textarea cols="62" rows="5" id="description" name="description" class="elementBorder notifyTextArea"></textarea>
					</td>
				</tr>

				<% if (StringUtils.isEmpty(checkPopup)) { %>
				<tr>
					<td class="notifyFieldRequired"><%=PropertyProvider.get("prm.notification.createsubscription2.selectevent.label")%></td>
					<td >
					<%-- Get the notification types for the object type;  calling
		                 this method sets the notification type count  --%>
		           		<div id ="notifyEventsDiv">
			            	<table width="100%"  cellpadding="0" cellspacing="0">
			            	       <%=domainListBean.getNotificationTypesCheckList(objectType, isCreateType, moduleType)%>
							</table>
						</div>
					</td>
				</tr>
				<%} else {%>
				<tr>
					<td class="notifyFieldRequired" width="<%=tdWidth%>"><%=PropertyProvider.get("prm.notification.createsubscription2.selectevent.new.label")%></td>
					<td class="topPadding">
							<table border="0" cellpadding="0" cellspacing="0"> 
							<tr>
								<td>
									<%notificationObjectTypeCollection.loadSubscribable();%>	  
								    <select name="objectType" onchange="loadEventTypes()" id="objectType">
										<option value=""><%=PropertyProvider.get("prm.notification.createtypesubscription1.selectobjecttype.option")%></option>
					          			<%=notificationObjectTypeCollection.getOptionList(subscription.getTargetObjectType())%> 
									</select> 
								</td>
								<td class="tableContent" style="padding-left: 145px;"><div id="eventTypes"></div></td>
							</tr>
							</table>
					</td>
				</tr>
				<%}%>
				<tr>
					<td class="editTableContent"><%=PropertyProvider.get("prm.notification.createsubscription2.deliverymethod.label")%></td>
					<td class="tableContent">
						<select id="deliveryTypeID" class="elementBorder" name="deliveryTypeID">
							<%=domainListBean.getDeliveryTypesOptionList()%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="editTableContent"><%=PropertyProvider.get("prm.notification.createsubscription2.deliveryschedule.label")%></td>
					<td class="tableContent">
						<select id="deliveryInterval" name="deliveryInterval" class="elementBorder"> 
							<%=domainListBean.getDeliveryIntervalOptionList()%>
						</select>
					</td>
				</tr>
	
				<%if (StringUtils.isNotEmpty(checkPopup) && checkPopup.equals("0")){%>			
				<tr> 
			        <td class="notifyFieldRequired" valign="center"><%=PropertyProvider.get("prm.notification.editsubscription.selectaddmembers.label")%></td>
			        <td align="left"> 
				     <div class="notify-edit-expand" id="moreoptions" onClick="javascript: toggleOptions('more')"></div>
					 <div class="notify-edit-collapse" align="center" style="display: none;" id="lessoptions" onClick="javascript: toggleOptions('less')"></div> 
			        </td>
			    </tr>
			    <% } %>
			</table>
	</td>
<% if (StringUtils.isEmpty(checkPopup)) { %>
	<td class="m_right" width="4"></td> 
</tr>
<tr> 
  <td class="m_left" width="4"></td>
<%} else {%> </tr> 
<%}%>  
<% if (StringUtils.isEmpty(checkPopup)) { %>
	<td >
		<div id="moreoptions" style="position: relative; left: 0; top: 0; display: ''">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td height="20" align="center" class="scrollHeader"><%=PropertyProvider.get("prm.notification.createsubscription2.moreoption.label")%></td>
		</tr>
		<tr>
			<td align="center" class="bar"><a href="javascript:toggleOptions('more');">
			<div id="moreButton"></div>
			</a></td>
		</tr>
	</table>
	</div>
	</td>
	<td class="m_right" width="4"></td> 
</tr>
<tr> 
  <td class="m_left" width="4"></td>
<%} else {%> </tr> <tr> 
<%}%>  
  <td id="notify-tablepad">
	<div id="extraItems" style="position: relative; left: 0; top: 0; display: none">
	<table width="100%" cellpadding="0" cellspacing="0" border="0" >
	<tr><td colspan="2">
		<table width="100%" cellpadding="0" cellspacing="0" >
		<% if (isUserSpaceAdministrator) { %>
		<%-- Space Admins can send to any group --%>
		<tr id="delivertoTR">
			<td class="notifyFieldRequired"  valign="middle">
				<% if (StringUtils.isEmpty(checkPopup)) { %>
					<%=PropertyProvider.get("prm.notification.createsubscription2.deliverto.label")%>
				<% }%>
			</td>
			<td>
				<div id="spaceParticipantDiv">
					<table width="100%" cellpadding="0" cellspacing="0"><%=spaceParticipantGroupCheckBoxList%></table>
				</div>
			</td>
		</tr>
		<tr>
			<td class="editTableContent"  width="<%=tdWidth%>"><%=PropertyProvider.get("prm.notification.createsubscription2.externalemail.label")%><br>
			<span id="otherEmailResult"><%=PropertyProvider.get("prm.notification.editsubscription.separate.text")%></span></td>

			<td><%-- Avinash: bfd 3140 	External Email Addresses in Create/Modify suscription is not validated --%>
			<input type="text" id="otherEmail" name="otherEmail" class="elementBorder notifyTextbox" value='<c:out value="${subscription.externalSubscribersInCSV}" />'
				size="60" onblur="return checkExternalEmailList();" /><br />
			</td>
		</tr>
		<tr>
			<td colspan="2"></td>
		</tr>
		<%}else{%>
		<%-- Non-Space Admins can only send to themselves 
         Note: This may throw a GroupException if there is
         no principal group in this space. This only occurs
         for Personal space, and in that case, the user is always
         a Space Admin of their Personal Space, so this code is not executed --%>
		<input type="hidden" name="teamMembers" id="teamMember"
			value="<%=teamMemberId%>">
		<%}%>
		<tr>
			<td class="editTableContent"  valign="top" width="<%=tdWidth%>"><%=PropertyProvider.get("prm.notification.createsubscription2.message.label")%></td>
			<td class="tableContent" >
				<textarea id="customMessage" rows="5" name="customMessage" class="elementBorder notifyTextArea"><c:out value="${subscription.customMessage}"/></textarea>
			</td>
		</tr>
	</table>
	<%-- End Subscription form table --%>
	</td>
	</tr>
</table>
</div>
</td>
<% if (StringUtils.isEmpty(checkPopup)) { %>
	<td class="m_right" width="4"></td> 
</tr>
<tr> 
  <td class="m_left" width="4"></td>
<%} else {%> </tr> <tr>
<%}%>  
<% if (StringUtils.isEmpty(checkPopup)) {%>
	<td>
		<div id="lessoptions" style="position: relative; left: 0; top: 0; display: none">
			<table width="100%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td height="20" align="center" class="scrollHeader"><%=PropertyProvider.get("prm.notification.createsubscription2.lessoption.label")%></td>
				</tr>
				<tr>
					<td align="center" class="bar"><a href="javascript:toggleOptions('less');"><div id="lessButton"></div></a></td>
				</tr>
			</table>
		</div>
	</td>
	<td class="m_right" width="4"></td> 
</tr>
             <tr>
                <td class="bottom_left" width="4"></td>
                <td class="bottom_m">
                <table width="100%">
                  <tr>
                    <td width="70%"></td>
                    <td width="15%"><button id="submit" style="width: 100px;" onclick="javascript:saveNotification();"><%=PropertyProvider.get("prm.notification.createsubscription2.submitbutton.label")%></button></td>
                    <td width="15%"><button id="cancel" style="width: 100px;" onclick="javascript:hideNotifyPopup();"><%=PropertyProvider.get("prm.notification.createsubscription2.cancelbutton.label")%></button></td>
                  </tr>
                </table>
               </td>
                <td class="bottom_right" width="4"></td>
              </tr>
<%} else { %>      
		<tr> <td height="20px" colspan="2"></td> </tr>
	<tr>      
	 <td class="notify-edit-bottom" align="center"><button type="button"  style="width: 100px;" onclick="return finish();"><%=PropertyProvider.get("prm.notification.createsubscription2.submitbutton.label")%></button></td>
	 <td width="3%">&nbsp;</td>
	 </tr>
</table>
</form>
<%}%>
<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>