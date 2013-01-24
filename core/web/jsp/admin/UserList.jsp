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
    info="List all Users"
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
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%-- SET THE CURRENT SEARCH MODE --%>
<%
      if (request.getParameter("displayMode") != null) {
	  userList.setDisplayMode (request.getParameter("displayMode"));
      }
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<template:import type="javascript" src="/src/document_prototypes.js" />
<%-- Import JavaScript --%>

<template:getSpaceJS space="application" />

<script language="JavaScript">
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var Module = '<%= Module.APPLICATION_SPACE %>';
	var theForm;
	var theShadowForm;
	var isLoaded;
	
	if (document.layers)
	document.captureEvents(Event.KEYPRESS);	
	window.onkeypress = keyhandler;


function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    load_header();
    isLoaded = true;
	theForm = self.document.forms["mainForm"];
	theShadowForm = self.document.forms["shadowForm"];
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_userlist";
    openwin_help(helplocation);
}

function search(filter) {
    if (!filter) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.userlist.showalluser.confirm")%>', function(btn) { 
			if(btn == 'yes'){ 
				theForm.searchFilter.value = filter;
	    		theForm.action.value='<%=Action.MODIFY%>';
	    		theForm.theAction.value="search";
	    		theForm.submit();
			}else{
			 	return false;
			}
		});
	} else {
		theForm.searchFilter.value = filter;
		theForm.action.value='<%=Action.MODIFY%>';
		theForm.theAction.value="search";
		theForm.submit();
    }
}

function sort (sortOrder) {

    theForm.sortOrder.value = sortOrder;
    theForm.action.value=<%=Action.MODIFY%>;
    theForm.theAction.value="search";

    theForm.submit();
}

function remove() {
    
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {

		if(theForm.userStatus.value=="X") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.user.delete.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else {                    
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get("prm.project.admin.userlist.deleteuser.confirm")%>', function(btn) { 
			  if(btn == 'yes'){ 
				theForm.theAction.value="remove";
				theForm.action.value = "<%=Action.MODIFY%>";
				theForm.submit();
			  }
		});
	  }
  }	 
}

function disable() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		if(theForm.userStatus.value=="X") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.user.disable.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else {                                                          
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.userlist.disableuser.confirm")%>', function(btn) { 
			  if(btn == 'yes'){ 
				theForm.theAction.value="disable";
				theForm.action.value = "<%=Action.MODIFY%>";
				theForm.submit();
			  }else{
			 	return false;
			 }
		  });
		}
	}	 
}

function changeUserStatus(personID, userStatus) {
	//commented because of js error
	/*var statusWin = openwin_small("status_win");  
	statusWin.document.location =  JSPRootURL + '/admin/UserStatus.jsp?personID=' + personID + 
		'&currentStatus=' + userStatus+"&module=" + Module + "&action=<%=Action.MODIFY%>";*/

	openwin_small("status_win", JSPRootURL + '/admin/UserStatus.jsp?personID=' + personID + 
		'&currentStatus=' + userStatus+"&module=" + Module + "&action=<%=Action.MODIFY%>");  
}


function modify() { 
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {	
		if(theForm.userStatus.value=="X") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.user.modify.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else if (theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.user.modify.unregistered.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else {
			theForm.theAction.value="modify";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
	}	 
}

function reset() {
    self.document.location = JSPRootURL + '/admin/UserList.jsp?module=' + Module + '&action=<%=Action.VIEW%>';
}

function showDetail(userID) {
    self.document.location = JSPRootURL + '/admin/UserView.jsp?module=' + Module + '&action=<%=Action.VIEW%>&userID=' + userID;
}

// bfd-2985 by Avinash Bhamare on 7th Feb 2006.
function shadow()
{
	 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		theShadowForm.submit();
	 }
}

function resend(){
	 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	 
	 	if(theForm.userStatus.value=="A") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.send.verification.code.active.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.send.verification.code.unregistered.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="X") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.send.verification.code.deleted.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="register";
			theForm.submit();
		}		
	}		
}

function setUserStatus(str){
	theForm.userStatus.value=str;
}
// bfd-2985 by Avinash Bhamare on 3rd Feb 2006.
function setUserName(str){
	theShadowForm.shadowUser.value=str;
}
// Setting domain id for selected user for shadowing
function setDomainID(str){
	theShadowForm.shadowUserDomain.value=str;
}
function keyhandler(e) {
	var event = e ? e : window.event;
   		if (event.keyCode == 13){
			search(theForm.key.value);
			event.keyCode=0;
			return false;
		} else {
			return true;
		}
}

</script>

</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<%-- Create the toolbar --%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.userlist">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="User list"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/UserList.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="modify" />
        <tb:button type="remove" label="Delete User"/>
        <tb:button type="custom" label="Disable User" imageEnabled='<%= PropertyProvider.get("all.global.toolbar.standard.disable.image.on") %>' imageOver='<%= PropertyProvider.get("all.global.toolbar.standard.disable.image.over") %>'  function="javascript:disable();" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<!-- new shaDow form to avoid collision between action variable and form action property -->
<form name="shadowForm" method="post" action="<session:getJSPRootURL />/shadowLogin.jsp" target="<%=session.getId()%>">
<input type="hidden" name="shadowUser" />
<input type="hidden" name="shadowUserDomain" />
</form>

<form name="mainForm" method="post" action="<session:getJSPRootURL />/admin/UserListProcessing.jsp" target="_self">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>" />
<input type="hidden" name="action" value='<%=Action.MODIFY%>' />
<input type="hidden" name="userStatus" />
<input type="hidden" name="shadowUser" />
<input type="hidden" name="searchFilter" value="<%=userList.getFilter()%>"/>
<input type="hidden" name="sortOrder" value="<%=userList.getSortOrder()%>"/>
<input type="hidden" name="mode" />

<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">
    <channel:insert name='<%="UserList_" + applicationSpace.getName()%>'
                    title="User List" minimizable="false" closeable="false"
                    include="include/UserList.jsp">
	<channel:button style="channel" type="create" label='Shadow selected user' href="javascript:shadow();"/>	
	<channel:button style="channel" type="modify" label='Resend Verification Code' href="javascript:resend();"/>	
	</channel:insert>						
</channel:channel>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceNavBar space="application"/>
</body>
</html>
