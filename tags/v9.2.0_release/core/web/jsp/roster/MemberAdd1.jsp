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
    info="Business Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.Invitee,
		    net.project.security.Action, 
		    net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
		    net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" /> 
<jsp:useBean id="lastInvitee" class="net.project.resource.Invitee" scope="session" />

<%
	String id = request.getParameter("id");
%>

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
    String theAction = request.getParameter("theAction");

	// if we just entered the wizard, clear old values.
	if (theAction != null &&
        (theAction.equals("create") || theAction.equals(String.valueOf(Action.CREATE))) ) {

		spaceInvitationWizard.clear();
        lastInvitee = new Invitee();
        session.setAttribute("lastInvitee", lastInvitee);
		session.removeValue ("erroMsg");

    } else if (theAction != null && theAction.equals("add")) {
        // Adding another person to the current wizard
        // Clear out the lastInvitee information
        lastInvitee = new Invitee();
        session.setAttribute("lastInvitee", lastInvitee);

    }

	spaceInvitationWizard.setSpace(user.getCurrentSpace());
	spaceInvitationWizard.setUser(user);
	// Set invitation URls for each kind of inviation (accept required / auto-accept)
	spaceInvitationWizard.setInvitationAcceptRequiredURL(pageContextManager.getProperty("space.invite.acceptrequired.url"));
	spaceInvitationWizard.setInvitationAutoAcceptURL(pageContextManager.getProperty("space.invite.autoaccept.url"));
%>

<html>
<head>
<META http-equiv="expires" content="0">

<title><display:get name="prm.directory.invite.memberaddition.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var inviteeCount = <%=spaceInvitationWizard.getInviteeList().size()%>;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
	theForm = self.document.forms["memberadd"];
    focusFirstField(theForm);
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function checkNew(){
   if (!checkTextbox(document.memberadd.inviteeFirstName,'<display:get name="prm.directory.invite.memberaddition.firstnamevalidation.message" />')) return false;
   if (!checkTextbox(document.memberadd.inviteeLastName,'<display:get name="prm.directory.invite.memberaddition.lastnamevalidation.message" />')) return false;
   if (!checkEmail(document.memberadd.inviteeEmail,'<display:get name="prm.directory.invite.memberaddition.emailaddressvalidation.message" />')) return false;
   return true;
}

function checkOneInvitee() {
    if (inviteeCount < 1) {
    	var errorMessage = '<display:get name="prm.directory.invite.memberaddition.emptyinviteelist.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        return false;
    }
    return true;
}

function wizCancel() {
    self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>";
}

function wizNext() {
   if (checkOneInvitee()) {
       theAction("next");
       theForm.submit();
   }
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

function help() {
	var helplocation = JSPRootURL + "/help/Help.jsp?page=directory&section=memberadd1";
    openwin_help(helplocation);
}

function removeInvitee(id) {
    theForm.inviteeID.value = id;
    theAction("removeInvitee");
    theForm.submit();
}

/* bfd: 2892 */
function onFormSubmit(){
	if( "" == theForm.theAction.value) {
		return false;
	}
	return true;
}
function reset() {
    theForm.reset();
}
</script>
</head>

<body class="main"  id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.directory.directory.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken="@prm.directory.memberadd1.page.history"
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='scrollwidecontent'>

<form name="memberadd" action="<%=SessionManager.getJSPRootURL()%>/roster/MemberAdd1Processing.jsp" onsubmit="javascript:return onFormSubmit();" method="post">
	<input type="hidden" name="theAction" value="">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
    <input type="hidden" name="inviteeID" value="">

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td  nowrap  class="channelHeader" colspan="3" width="98%"><nobr><display:get name="prm.directory.invite.memberaddition.channel.inviteparticipant.title" /> <display:get name="prm.global.display.requiredfield" /><DISPLAY PROPERTY="request:method"/></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
<tr>
    <td>&nbsp;</td>

<%	if (spaceInvitationWizard.hasDirectories()) { %>

    <%-- First Column: Directory Search Box --%>
    <td valign="top" width="50%" height="100%">
        <table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#000000">
            <tr>
                <td valign="top" height="100%">
    <jsp:include page="/roster/include/DirectorySearch.jsp" flush="true" />
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
  
        <table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#000000">
            <tr>
                <td valign="top" height="100%">
                    <jsp:include page="/roster/include/ManualInvite.jsp" flush="true" />
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
    <td colspan="5">
	<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="cancel" function="javascript:wizCancel();" />
			<tb:button type="next" function="javascript:wizNext();" />
		</tb:band>
	</tb:toolbar>
    </td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>

<%-- Current Invitee List --%>
<tr>
    <td colspan="5">
        <jsp:include page="/roster/include/CurrentInvitees.jsp" flush="true" />
    </td>
</tr>
</table>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%session.removeValue("errorMsg");%>

