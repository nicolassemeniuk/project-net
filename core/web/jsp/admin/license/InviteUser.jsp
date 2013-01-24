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
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Associate Users To Lcense"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User,
	    	net.project.resource.PersonListBean,
            net.project.xml.XMLFormatter"
  %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<% String id = request.getParameter("id"); %>

<%-- Unlike most of the pages in the application space, this page is not checking
     that you have access to the application space.  Instead it is just checking
     to see that you have access to the application module.  We do this because
     this page is shared between the application space and the personal space. --%>
<security:verifyAccess action="view"
                       module="<%=net.project.base.Module.APPLICATION_SPACE%>" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/document_prototypes.js" />
<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<%-- Import JavaScript --%>
<template:getSpaceJS space="personal" />

<script language="JavaScript">
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var Module = '<%= Module.APPLICATION_SPACE %>';
	var theForm;
	
	if (document.layers)
	document.captureEvents(Event.KEYPRESS);	
	window.onkeypress = keyhandler;


function setup() {
    load_menu();
    load_header();
    isLoaded = true;
	theForm = self.document.forms["mainForm"];
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_license&section=invite_user";
    openwin_help(helplocation);
}

function associate() {
    
    if (theForm.elements["email"].value != '') {
		theForm.submit();
	} else {
		var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.emailnotpresent.error.message")%>';
		extAlert(errorTitle, errorMessage, Ext.MessageBox.ERROR);
	} 	 
}

function reset() {
    self.document.location = JSPRootURL + '/admin/license/InviteUser.jsp?module=' + Module + '&action=<%=Action.VIEW%>';
}

function cancel() {
	self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp?licenseKey="%>' + '<%=request.getParameter("licenseKey")%>' + '<%="&module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>';

}

function showDetail(userID) {
    self.document.location = JSPRootURL + '/admin/license/UserView.jsp?module=' + Module + '&action=<%=Action.VIEW%>&userID=' + userID;
}

function keyhandler(e) {
	var event = e ? e : window.event;
   		if (event.keyCode == 13){
			search(theForm.key.value);
			event.keyCode=0;
			return false;
		}
}

</script>

</head>
<% // Redirect to UserList.jsp if it is an ApplicationAdministrator
	if (user.isApplicationAdministrator()) {
%>
	<jsp:forward page="/admin/license/UserList.jsp">
		<jsp:param name="theAction" value="associateUser"/>
	</jsp:forward>	
<%
	} 
%>
<body class="main" id="bodyWithFixedAreasSupport" onload=setup(); leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />

<%-- Create the toolbar --%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
			<history:business display="<%= personalSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Main.jsp"%>'
                              queryString='<%="module=" + Module.PERSONAL_SPACE%>' />
            <history:module display="Licensing"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseListView.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
            <history:page display="Invite User To License"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/InviteUser.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="<session:getJSPRootURL />/admin/license/InviteUserProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.PERSONAL_SPACE%>" />
<input type="hidden" name="action" value='<%=Action.MODIFY%>' />

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr><td>&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
    	<td class="warnText">
			<%= (String)session.getAttribute("notifyUserMessage") %>
		</td>
		<%
			 session.removeAttribute("notifyUserMessage"); 
		%>
		<tr><td>&nbsp;</td></tr>
  	<tr>
    	<td>

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td colspan="6">
	<channel:channel name='<%="ApplicationSpaceMain_" + personalSpace.getName()%>' customizable="false">
    <channel:insert name='<%="InviteUserToLicense_" + personalSpace.getName()%>'
                    title='Invite Users To Project.net' minimizable="false" closeable="false">                
	</channel:insert>						
	</channel:channel>
    </td></tr>
        <tr>
             <%-- <td class="channelContent">&nbsp;</td> --%>
              <td colspan="6" align="center" >
							  
			    <%=PropertyProvider.get("prm.project.admin.license.enter.email.address.label") %> &nbsp; 
			  	<input type="text" name="email" value="" size="50"/> &nbsp;
				<a href="javascript:associate()"><%=PropertyProvider.get("prm.project.admin.license.send.notification.label")%> <img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-newpost_off.gif" alt="Send Notification" border=0 align="absmiddle"> </a>
			  </td>
			  
         </tr>
      </table>
    </td>
  </tr>
</table>
      
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="personal"/>
</body>
</html>
