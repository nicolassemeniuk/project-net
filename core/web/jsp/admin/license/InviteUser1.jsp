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
    info="InviteUser1" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.resource.Invitee,
		    net.project.security.Action, 
		    net.project.security.SessionManager, 
		    net.project.base.Module,
			net.project.admin.ApplicationSpace" 
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<jsp:useBean id="lastInvitee" class="net.project.resource.Invitee" scope="session" />

<% 	
	net.project.resource.LicenseInvitationManager  invitationManager = (net.project.resource.LicenseInvitationManager)session.getAttribute("invitationManager");
	if (invitationManager == null) {
		invitationManager = new net.project.resource.LicenseInvitationManager();
		session.setAttribute("invitationManager", invitationManager);
	}
%>

<% // Redirect to UserList.jsp if it is an ApplicationAdministrator
	if (user.isApplicationAdministrator()) {
		//request.remove("module");
		//request.remove("action");
		response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/UserList.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW);
		return;
	} 
%>

<security:verifyAccess module="<%=net.project.base.Module.DIRECTORY%>" action="create"/> 

<%
    String theAction = request.getParameter("theAction");

	// if we just entered the wizard, clear old values.
	if (theAction != null &&
        (theAction.equals("create") || theAction.equals(String.valueOf(Action.CREATE))) ) {

		invitationManager.clear();
        lastInvitee = new Invitee();
        session.setAttribute("lastInvitee", lastInvitee);
		session.removeValue ("errorMsg");

    } else if (theAction != null && theAction.equals("add")) {
        // Adding another person to the current wizard
        // Clear out the lastInvitee information
        lastInvitee = new Invitee();
        session.setAttribute("lastInvitee", lastInvitee);

    }

	invitationManager.setSpace(user.getCurrentSpace());
	invitationManager.setUser(user);
%>

<html>
<head>
<META http-equiv="expires" content="0">

<title><%=PropertyProvider.get("prm.license.inviteuser1page.title")%></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/util.js" />

<script language="javascript">
    var theForm;

function setup() {
	theForm = self.document.forms["memberadd"];
    focusFirstField(theForm);
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function checkNew(){
   if (!checkTextbox(document.memberadd.inviteeFirstName,'<%=PropertyProvider.get("prm.license.inviteuser1.firstnamerequired.message")%>')) return false;
   if (!checkTextbox(document.memberadd.inviteeLastName,'<%=PropertyProvider.get("prm.license.inviteuser1.lastnamerequired.message")%>')) return false;
   if (!checkEmail(document.memberadd.inviteeEmail,'<%=PropertyProvider.get("prm.license.inviteuser1.validemail.message")%>')) return false;
   return true;
}

function wizCancel() {
   theAction("cancel");
   theForm.submit();
}

function wizNext() {
   theAction("next");
   theForm.submit();
}

function wizSearch() {
    theAction("search");
    theForm.submit();
}

function add() {
   theAction("add");
   if (checkNew()){
      theForm.submit();
   }
}

function removeInvitee(id) {
    theForm.inviteeID.value = id;
    theAction("removeInvitee");
    theForm.submit();
}

</script>
</head>

<body class="main" onLoad="setup();">

<br>

<form name="memberadd" action="<%=SessionManager.getJSPRootURL()%>/admin/license/InviteUser1Processing.jsp" method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
    <input type="hidden" name="inviteeID" value="">

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td  nowrap  class="channelHeader" colspan="3" width="98%"><nobr><%=PropertyProvider.get("prm.license.inviteuser1.channel.inviteusers.title")%></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>

<tr>
    <td>&nbsp;</td>

<%	if (invitationManager.hasDirectories()) { %>

    <%-- First Column: Directory Search Box --%>
    <td valign="top" width="50%" height="100%">
        <table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#FFCC33">
            <tr>
                <td valign="top" height="100%">
		<jsp:include page="/admin/license/include/DirectorySearch.jsp" flush="true" />
				</td>
            </tr>
        </table>
    </td>

    <%-- Spacer Column --%>
    <td>&nbsp;</td>
    
    <td valign="top" width="50%" height="100%">
<%  } else { %>
    <td colspan="3" valign="top" height="100%">
<%  } %>
  
        <table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#FFCC33">
            <tr>
                <td valign="top" height="100%">
                    <jsp:include page="/admin/license/include/ManualInvite.jsp" flush="true" />
                </td>
            </tr>
        </table>
    </td>
    <td>&nbsp;</td>
</tr>

<tr><td colspan="5">&nbsp;</td></tr>

<%-- Toolbar in middle of page to ensure that even with a long
     invitee list, toolbar buttons are available without scrolling
  --%>
<tr>
    <td>&nbsp;</td>
    <td colspan="3">
	<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="cancel" function="javascript:wizCancel();" />
			<tb:button type="next" function="javascript:wizNext();" />
		</tb:band>
	</tb:toolbar>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>

<%-- Current Invitee List --%>
<tr>
    <td colspan="5">
		<jsp:include page="/admin/license/include/CurrentInvitees.jsp" flush="true" />
    </td>
</tr>
</table>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
<%session.removeValue("errorMsg");%>

