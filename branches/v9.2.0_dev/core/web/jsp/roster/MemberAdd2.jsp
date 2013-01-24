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
    info="MemberAdd2.jsp" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.security.Action,
			net.project.base.property.PropertyProvider,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" />


<html>
<head>

<title><display:get name="prm.directory.invite.memberinformation.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<script language="javascript">
    var theForm;
    var inviteeCount = <%=spaceInvitationWizard.getInviteeList().size()%>;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	window.history.forward(-1);
	
function setup(){
	theForm = self.document.forms["memberadd"];
	focusFirstField(theForm);
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function checkOneInvitee() {
    if (inviteeCount < 1) {
    	var errorMessage = '<display:get name="prm.directory.invite.memberinformation.emptyinviteelist.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        return false;
    }
    return true;
}

function wizCancel() {
    self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>";
}

function wizBack() {
	theAction("back"); 
	theForm.submit();
}

function wizAddAnother() {
    if (checkOneInvitee()) {
    	theAction("another");
    	theForm.submit();
    }    
}

function wizFinish() {
    if (checkOneInvitee()) {
    	if(validateData()){
    		theAction("finish");
    		theForm.submit();
    	}
    }
}
function validateData(){
	return checkMaxLength(theForm.inviteeMessage, 250, '<display:get name="prm.directory.invite.memberinformation.messagefield.invalid.message"/>');
}

function removeInvitee(id) {
    theForm.inviteeID.value = id;
    theAction("removeInvitee");
    theForm.submit();
}

<%--
User is changing assigned roles
If they cannot, clear all roles, otherwise let them proceed
--%>
function changeAssignedRoles() {
	var assignedRolesElement = theForm.elements["assignedRoles"];

	if (!assignedRolesElement) return;

	if (typeof(canSelectRoles) != 'undefined' &&  !canSelectRoles) {
		assignedRolesElement.selectedIndex = -1;
		var errorMessage = '<display:get name="prm.directory.invite.memberinformation.roleaddition.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
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

function help() {
	var helplocation = JSPRootURL + "/help/Help.jsp?page=directory&section=memberadd2";
    openwin_help(helplocation);
}

//bfd-4201 fix: Uros
function reset() {
	theForm.reset();
}

</script>
</head>
<body class="main" onLoad="setup();"   id="bodyWithFixedAreasSupport"  >
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.directory.directory.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken="@prm.directory.memberadd2.page.history"
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="memberadd" action="<%=SessionManager.getJSPRootURL()%>/roster/MemberAdd2Processing.jsp" method="post">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
<input type="hidden" name="action" value="<%=Action.CREATE%>">
<input type="hidden" name="inviteeID" value="">

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td  nowrap  class="channelHeader" colspan="2" width="98%"><nobr><display:get name="prm.directory.invite.memberinformation.channel.inviteparticipants.title" /></nobr></td>
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

  <tr align="left"> 
  	<td nowrap>&nbsp;</td> 
    <td nowrap class="fieldNonRequired"><display:get name="prm.directory.invite.memberinformation.responsibilities.label" /></td>
    <td nowrap class="tableContent"> 
      <input type="text" name="inviteeResponsibilities"  size="30" value="<%= net.project.util.HTMLUtils.escape(spaceInvitationWizard.getInviteeResponsibilities())%>" maxlength="250">
    </td>
  	<td nowrap>&nbsp;</td> 
  </tr>
  <tr align="left"> 
  	<td nowrap>&nbsp;</td> 
     <td nowrap class="fieldNonRequired" valign="top"><display:get name="prm.directory.invite.memberinformation.message.label" /></td>
     <td nowrap class="tableContent">
          <textarea name="inviteeMessage" cols="35" rows="3" wrap="virtual"><%= net.project.util.HTMLUtils.escape(spaceInvitationWizard.getInviteeMessage())%></textarea>
     </td>
  	<td nowrap>&nbsp;</td>
  </tr>

  <tr align="left">
  	<td>&nbsp;</td>
    <td class="fieldNonRequired" valign="top"><display:get name="prm.directory.invite.memberinformation.notification.label"/></td>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  </tr>

  <tr align="left">
  	<td>&nbsp;</td>
    <td class="tableContent" colspan="2" style="padding-left: 10px">
        <input name="sendNotifications" type="checkbox" value="true"<%=(PropertyProvider.getBoolean("prm.directory.invite.memberinformation.notification.onbydefault.flag") ? " checked" : "")%>>&nbsp;
        <display:get name="prm.directory.invite.memberinformation.notification.message"/></td>
  	<td>&nbsp;</td>
  </tr>

  <tr align="left"> 
   	 <td>&nbsp;</td>
	 <td colspan="2" class="tableContent" style="padding-left: 10px; padding-top: 4px">
	 </td>
   	 <td>&nbsp;</td>
  </tr>
<%-- show the add-user-to-role box if the user is a space administorator --%>
<%	if (user.isSpaceAdministrator()) { %>
  <tr align="left">
   	 <td>&nbsp;</td>
     <td colspan="2" class="fieldNonRequired">
        <display:get name="prm.directory.invite.memberinformation.defaultrole.label" />
     </td>
   	 <td>&nbsp;</td>
  </tr>
  <tr align="left">
   	 <td>&nbsp;</td>
     <td colspan="2" style="padding-left: 10px">
        <select name="assignedRoles" multiple height="5" onchange="changeAssignedRoles()">
            <%=spaceInvitationWizard.getRoleOptionList ( spaceInvitationWizard.getAssignedRoles() )%>
        </select>
     </td>
   	 <td>&nbsp;</td>
  </tr>
<%	} %>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr>
    <td colspan="4" align="center">
	<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="cancel" function="javascript:wizCancel();" />
			<tb:button type="back" function="javascript:wizBack();" />
			<tb:button type="finish" label='<%= PropertyProvider.get("prm.directory.invite.memberinformation.finish.button.label") %>' function="javascript:wizFinish();" />
<%--			<tb:button type="add" label="Invite More" function="javascript:wizAddAnother();" />--%>
		</tb:band>
	</tb:toolbar>
    </td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
    
    <tr>
        <td colspan="4">
            <jsp:include page="/roster/include/CurrentInvitees.jsp" flush="true" />
        </td>
    </tr>
  
</table>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
