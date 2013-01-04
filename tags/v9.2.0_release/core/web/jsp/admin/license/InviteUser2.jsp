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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="InviteUser2.jsp" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.resource.Invitee,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.security.Action,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="invitationManager" type="net.project.resource.LicenseInvitationManager" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" />


<html>
<head>

<title><%=PropertyProvider.get("prm.license.inviteuser2page.title")%></title>
<%-- Import CSS --%>

<template:getSpaceCSS />

<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />

<script language="javascript">
var theForm;

function setup(){
	theForm = self.document.forms["memberadd"];
	focusFirstField(theForm);
	clickAutoAcceptInvite(true);
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function wizCancel() {
    self.document.location = "<%=SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE%>";
}

function wizBack() {
	theAction("back"); 
	theForm.submit();
}

function wizAddAnother() {
	theAction("another");
	theForm.submit();
}

function wizFinish() {
	theAction("finish");
	theForm.submit();
}

function removeInvitee(id) {
    theForm.inviteeID = id;
    theAction("removeInvitee");
    theForm.submit();
}

<%-- 
Called when user clicked autoAcceptInvite radio
When true, makes roles list non-readonly
When false, clears roles list and makes it read only
--%>
function clickAutoAcceptInvite(isAutoAccept) {
	var autoAcceptElement = theForm.elements["autoAcceptInvite"];
	var assignedRolesElement = theForm.elements["assignedRoles"];
	
	if (!autoAcceptElement || !assignedRolesElement) return;
	
	if (isAutoAccept) {
	    selectRadio(autoAcceptElement, "true");
	    canSelectRoles = true;

	} else {
	    selectRadio(autoAcceptElement, "false");
		assignedRolesElement.selectedIndex = -1;
	    canSelectRoles = false;

	}
    return true;
}

<%--
User is changing assigned roles
If they cannot, clear all roles, otherwise let them proceed
--%>
function changeAssignedRoles() {
	var autoAcceptElement = theForm.elements["autoAcceptInvite"];
	var assignedRolesElement = theForm.elements["assignedRoles"];

	if (!autoAcceptElement || !assignedRolesElement) return;

	if (!canSelectRoles) {
		assignedRolesElement.selectedIndex = -1;
		var errorMessage = '<%=PropertyProvider.get("prm.license.inviteuser2.addifaccept.message")%>';
		extAlert('Error Message', errorMessage, Ext.MessageBox.ERROR);
		return false;
	}
	
	return true;
}

function selectRadio(radioElement, value) {
	if (radioElement) {
		if (radioElement.length == null) {
			radioElement.checked = true;
		}
		for (i = 0; i < radioElement.length; i++) {
			if (radioElement[i].value == value) {
				radioElement[i].checked = true;
				break;
			}
		}
	}
}

</script>
</head>
<body class="main" onLoad="setup();">

<form name="memberadd" action="<%=SessionManager.getJSPRootURL()%>/admin/license/InviteUser2Processing.jsp" method="post">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
<input type="hidden" name="action" value="<%=Action.CREATE%>">
<input type="hidden" name="inviteeID" value="">

<br>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td  nowrap  class="channelHeader" colspan="2" width="98%"><nobr><%=PropertyProvider.get("prm.license.inviteuser2.channel.usersinfo.title")%></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>

<%  if (session.getValue("errorMsg") != null) { %>
	<tr align="left">
		<td colspan="4">
	              <font color="#FF0000"><b><%= session.getValue("errorMsg") %></b></font>
		</td>
	</tr>
    <tr><td colspan="4">&nbsp;</td></tr>
<% } %>
<%--
  <tr align="left"> 
  	<td nowrap>&nbsp;</td> 
    <td nowrap class="fieldNonRequired">Responsibilities:</td>
    <td nowrap class="tableContent"> 
         <!-- Avinash : Empty value -->
      <input type="text" name="inviteeResponsibilities" size="30" value="<c:out value="${invitationManager.inviteeResponsibilities}"/>">
    </td>
  	<td nowrap>&nbsp;</td> 
  </tr>
 --%>
  <tr align="left"> 
  	<td nowrap>&nbsp;</td> 
     <td nowrap class="fieldNonRequired" valign="top"><%=PropertyProvider.get("prm.license.inviteuser2.message.label")%></td>
     <td nowrap class="tableContent">
     <!-- Avinash : Empty value -->
          <textarea name="inviteeMessage" cols="35" rows="3" wrap="virtual"><c:out value="${invitationManager.inviteeMessage}"/></textarea>
     </td>
  	<td nowrap>&nbsp;</td> 
  </tr>
  
<%--
  <tr align="left"> 
  	<td>&nbsp;</td> 
    <td class="fieldNonRequired" valign="top">Invitation</td>
  	<td>&nbsp;</td> 
  	<td>&nbsp;</td> 
  </tr>
  
  <tr align="left"> 
   	 <td>&nbsp;</td>
	 <td colspan="2">
	 	<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="tableContent">
					<input type="radio" name="autoAcceptInvite" value="false" onclick="clickAutoAcceptInvite(false)">&nbsp;
				</td>
				<td class="tableContent" colspan="2">
					Require invitees to accept invitation
				</td>
			</tr>
			<tr>
				<td class="tableContent">
					<input type="radio" name="autoAcceptInvite" value="true" onclick="clickAutoAcceptInvite(true)">&nbsp;
				</td>
				<td class="tableContent" colspan="2">
					Automatically accept invitation
				</td>
            </tr>
--%>
			<%-- show the add-user-to-role box if the user is a space administorator --%>
<%--			<%	if (user.isSpaceAdministrator()) { %>
			<tr>
                <td>&nbsp;</td>
				<td class="tableContent" colspan="2">
					All invitees will automatically be added to the Team Members role;  You may select other roles to which they will also be added:
				</td>
            </tr>
			<tr>
                <td>&nbsp;</td>
                <td width="5%">&nbsp;</td>
			    <td class="fieldNonRequired" colspan="2">
					<select name="assignedRoles" multiple height="5" onchange="changeAssignedRoles()">
		    			<%=invitationManager.getRoleOptionList ( invitationManager.getAssignedRoles() )%>
					</select>
				</td>
			</tr>
			<%	} %>
		</table>
	 </td>
   	 <td>&nbsp;</td> 
  </tr>
 --%>
  <tr><td colspan="4">&nbsp;</td></tr>
  
  <tr> 
    <td colspan="4" align="center">
	<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="cancel" function="javascript:wizCancel();" />
			<tb:button type="back" function="javascript:wizBack();" />
			<tb:button type="finish" label='<%=PropertyProvider.get("prm.license.inviteuser2.invite.buttonl.label")%>' function="javascript:wizFinish();" />
<%--			<tb:button type="add" label="Invite More" function="javascript:wizAddAnother();" />--%>
		</tb:band>
	</tb:toolbar>
    </td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
    
    <tr>
        <td colspan="4">
            <jsp:include page="/admin/license/include/CurrentInvitees.jsp" flush="true" />
        </td>
    </tr>
  
</table>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
