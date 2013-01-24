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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|  Displays detail information of a License
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Display"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.admin.ApplicationSpace,
            net.project.base.Module,
			net.project.license.LicenseStatusCode,
            net.project.security.Action,
            net.project.security.SessionManager,
			net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseManager" class="net.project.license.LicenseManager" scope="session" />
<jsp:useBean id="license" class="net.project.license.License" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpace" scope="session" />


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%
	String orgLink = "/admin/license/LicenseDetailView.jsp?module="+net.project.base.Module.APPLICATION_SPACE;
	session.setAttribute("orgLink",orgLink);
%>
    
<%-- Import CSS --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/document_prototypes.js" />
<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<%-- Import Scripts --%>
<template:getSpaceJS space="application" />  

<script language="javascript">
	var theForm = self.document.forms["main"];
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	isLoaded = true;
    theForm = self.document.forms["main"];
}

function showDetail(userID) {
    self.document.location = JSPRootURL + '/admin/license/UserView.jsp?module=' + <%=Module.APPLICATION_SPACE%> + '&action=<%=Action.VIEW%>&userID=' + userID;
}

function changeUserStatus(personID, userStatus) {

    /*var statusWin = openwin_small("status_win");  
    statusWin.document.location =  JSPRootURL + '/admin/UserStatus.jsp?module=' + <%=Module.APPLICATION_SPACE%> + '&action=<%=Action.MODIFY%>&personID=' + personID + '&currentStatus=' + userStatus;
    */
    openwin_small("status_win",JSPRootURL + '/admin/UserStatus.jsp?module=' + <%=Module.APPLICATION_SPACE%> + '&action=<%=Action.MODIFY%>&personID=' + personID + '&currentStatus=' + userStatus);  
}

function modify() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.modify.unregistered.user.error.message")%>';
			extAlert('Error Message', errorMessage , Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.disable.unregistered.user.error.message")%>';
			extAlert('Error Message', errorMessage , Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="modify";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
	}	 
	
}

function dissociate() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.license.detailview.dissociate.message")%>', function(btn) { 
				if(btn == 'yes'){ 
					if(theForm.userStatus.value=="R") {
						var errorMessage = '<%=PropertyProvider.get("prm.license.detailview.unregistereduser.message")%>';
						extAlert('Error Message', errorMessage , Ext.MessageBox.ERROR);
					}else{
						theForm.theAction.value="dissociate";
						theForm.submit();
					}	
				}else{
				 	return false;
				}
			 });
	} 
}

function associate() {
	self.document.location = JSPRootURL + "/admin/license/InviteUser1.jsp?module=<%= Module.DIRECTORY%>&action=<%= Action.CREATE%>&theAction=associateUser";
	
}

function assignResponsibleUser() {
    self.document.location = JSPRootURL + "/admin/license/UserList.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>&theAction=assignResponsibility" ;

}

function setUserStatus(str){
    	theForm.userStatus.value=str;
}
    

function modifyStatus() {
    self.document.location = JSPRootURL + "/admin/license/ModifyStatus.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.DELETE%>";

}

function cancel() {
    <%  String cancelButton = request.getParameter("cancelButton");
        if (cancelButton != null && cancelButton.equals("useBack")) { %>
    history.back();
    <% } else { %>
	self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseListView.jsp?module=" + Module.APPLICATION_SPACE%>';
    <% } %>
}

function reset() { self.document.location = JSPRootURL + "/admin/license/LicenseDetailView.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>"; }

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=detail_view");
}

</script>
</head>

<%-- End of HEAD --%>

<%-- Begin Content --%>		
<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
			<% if (user.isApplicationAdministrator()) { %>
			<history:business display="<%= applicationSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<% } else { %>
			<history:business display="<%= personalSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Main.jsp"%>'
                              queryString='<%="module=" + Module.PERSONAL_SPACE%>' />
			<% } %>
			<history:module display='<%=PropertyProvider.get("prm.license.module.history")%>'
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
            <history:page display='<%=PropertyProvider.get("prm.license.detailview.module.history")%>'
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<% 
	    String licenseKey = null;
		if (request.getParameter("licenseKey") != null) {
			licenseKey = request.getParameter("licenseKey");
			session.setAttribute("licenseKey", licenseKey);
		} else {
			licenseKey = (String)session.getAttribute("licenseKey");
		}
		//System.out.println("LicenseDetailView.jsp : " + licenseKey);
		license.setClear();
		license.load(net.project.license.LicenseKey.createLicenseKey(licenseKey));
%>

<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/license/UserListProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.MODIFY%>">
	<input type="hidden" name="userStatus" />

<table width="100%" cellpadding="0" cellspacing="0" border="0">	    
<tr><td>
<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">

    <channel:insert name='<%="LicenseDetailView_" + applicationSpace.getName()%>'
                      title='<%=PropertyProvider.get("prm.license.detailview.channel.licenseinfo.title")%>'
                      minimizable="false" closeable="false"
                      include="/admin/license/include/LicenseDetail.jsp">
<%
    // The following if condition is to prevent changing status of cancelled licenses.
	//This is in addition to the server side check. 
	if (user.isApplicationAdministrator() && !(license.getStatus().getCode().equals(LicenseStatusCode.CANCELED)) ) { %>
    	<channel:button style="channel" type="modify" label='Modify Status' href="javascript:modifyStatus();"/>
    	<channel:button style="channel" type="create" label='Assign Responsible User' href="javascript:assignResponsibleUser();"/>
<% } else if (user.isApplicationAdministrator()) {%>
    	<channel:button style="channel" type="create" label='Assign Responsible User' href="javascript:assignResponsibleUser();"/>
<% } %>
	</channel:insert>
</channel:channel>
</td></tr>
	
<tr><td>	
<table width="100%" cellpadding="0" cellspacing="0" border="0">	
	<tr><td colspan="4">&nbsp;</td></tr>
<%--  Show the associate user button only if the license is enabled.
      This is in addition to a server side check.
	  Also show modify user button only if user is application administrator.
--%>	
	<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">
        <channel:insert name='<%="CurrentUsers_" + applicationSpace.getName()%>'
                        title='<%=PropertyProvider.get("prm.license.detailview.channel.currentusers.title")%>' minimizable="false" closeable="false"
                        include="/admin/license/include/LicenseUserList.jsp">                    
            <% if (user.isApplicationAdministrator()) { %>
            <channel:button style="channel" type="modify" label='Modify User' href="javascript:modify();"/>					
            <% } %>
            <channel:button style="channel" type="remove" label='<%=PropertyProvider.get("prm.license.detailview.user.dissociate.button.label")%>' href="javascript:dissociate();"/>
            <% if (license.getStatus().getCode().equals(LicenseStatusCode.ENABLED)) { %>
                <% if (user.isApplicationAdministrator()) { %>
            <channel:button style="channel" type="create" label='Associate User' href="javascript:associate();"/>	
                <% } else { %>
            <channel:button style="channel" type="create" label='<%=PropertyProvider.get("prm.license.detailview.user.invite.button.label")%>' href="javascript:associate();"/>		
                <% } %>
            <% } %>
        </channel:insert>
    						
	</channel:channel>
</table>
</td></tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</FORM>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="application"/>
</body>
<%-- End Content --%>
</html>


