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
|   $Author: avinash $
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="List all Users"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User,
			net.project.base.property.PropertyProvider,
			net.project.security.domain.UserDomain,
	    	net.project.resource.PersonListBean,
            net.project.xml.XMLFormatter"
  %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 


<% String id = request.getParameter("id"); %>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>		       

<%-- SET THE CURRENT SEARCH MODE --%>
<%
      if (request.getParameter("displayMode") != null) {
	  		userList.setDisplayMode (request.getParameter("displayMode"));
      } else if(userList.getDisplayMode() == null) {
	  		userList.setDisplayMode("initial");
	  }
	  
	 String str[] = request.getParameterValues("selected");
	 userList.setUserDomainFilter(str);
	 
	 String orgLink = "/admin/domain/DomainUserList.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&selected="+request.getParameter("selected");
	 session.setAttribute("orgLink",orgLink);
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import JavaScript --%>
<template:getSpaceJS space="application" />

<script language="JavaScript">
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var Module = '<%= Module.APPLICATION_SPACE %>';
	var theForm;
	
	if (document.layers)
	document.captureEvents(Event.KEYPRESS);	
	window.onkeypress = keyhandler;


function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    load_header();
    isLoaded = true;
	theForm = self.document.forms["mainForm"];
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_domain_userlist";
    openwin_help(helplocation);
}

function modify()	{
    self.document.location = JSPRootURL + '/admin/domain/DomainEdit.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>&domainID=<%=domain.getID()%>';
}

function migrate() {
	 var m_url = (JSPRootURL + "/admin/domain/DomainMigrationInstructions.jsp?selected="+ '<%= request.getParameter("selected") %>' + "&action=2&module=240");
	 var myWindow = openwin_large("myWindow" , m_url);
	 myWindow.focus();
} 
function search(filter) {

    if (!filter) {
	    Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.project.admin.license.question.js" />', function(btn) { 
			if(btn == 'yes'){ 
			    theForm.searchFilter.value = filter;
			    theForm.action.value=<%=Action.MODIFY%>;
			    theForm.theAction.value="search";
			    theForm.submit();
			    return true;
			}
		});
    } else {
	theForm.searchFilter.value = filter;
	theForm.action.value=<%=Action.MODIFY%>;
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

function removeUser() {
    
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		str=getSelection(theForm).substring(0,1);
		if(theForm.userStatus.value=="D") {
			extAlert('Error Message', "Cannot disable already Disabled User ", Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			extAlert('Error Message', "Cannot disable Unregistered User", Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="remove";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
	}	 
}

function changeUserStatus(personID, userStatus) {

    /*var statusWin = openwin_small("status_win");  
    statusWin.document.location =  JSPRootURL + "/admin/UserStatus.jsp?personID=" + personID + 
	"&currentStatus=" + userStatus+"&module=" + Module + "&action=<%=Action.MODIFY%>"; */
	openwin_small("status_win",JSPRootURL + "/admin/UserStatus.jsp?personID=" + personID + 
				"&currentStatus=" + userStatus+"&module=" + Module + "&action=<%=Action.MODIFY%>"); 
}


function modifyUser() { 
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {	
		if(theForm.userStatus.value=="R") {
			extAlert('Error Message', "Cannot modify Unregistered User", Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			extAlert('Error Message', "Cannot disable Unregistered User", Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="modify";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
}	 
	
}

function reset() {
    self.document.location = JSPRootURL + '<%=orgLink %>';
}

function showDetail(userID) {
    self.document.location = JSPRootURL + '/admin/UserView.jsp?module=' + Module + '&action=<%=Action.VIEW%>&userID=' + userID;
}

function resend(){
	 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	 	if(theForm.userStatus.value=="A") {
		 	extAlert('Error Message', "Cannot send verification code to an Active User again", Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
		 	extAlert('Error Message', "Cannot send verification code to an Unregistered User", Ext.MessageBox.ERROR);
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

function keyhandler(e) {
	var event = e ? e : window.event;
   		if (event.keyCode == 13){
			search(theForm.key.value);
			event.keyCode=0;
			return false;
		}
}

function tabClick(nextPage) {
	self.document.location = JSPRootURL + nextPage +'?module=<%=Module.APPLICATION_SPACE%>&selected=<%=domain.getID()%>';            
}

</script>

</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="application"/>
<%-- Create the toolbar --%>
<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.domainmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
         	<history:page display="Manage Users"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/DomainUserList.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE+"&selected="+request.getParameter("selected")%>' />			
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="modify" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip>
    <tab:tab label="Domain Properties" href="javascript:tabClick('/admin/domain/DomainProperties.jsp');"/>
	<tab:tab label="Manage Users" href="javascript:tabClick('/admin/domain/DomainUserList.jsp');" selected="true"/>
</tab:tabStrip>
<br>

<%
	if(PropertyProvider.getBoolean("prm.global.domainmigration.isenabled") && Integer.parseInt(domain.getUserCount()) > 0 ) {
%>				
	<a href="javascript:migrate()" >Initiate Domain Migration for All Users</a>
<%
	}
%>	

<form name="mainForm" method="post" action="<session:getJSPRootURL />/admin/domain/DomainUserListProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>" />
<input type="hidden" name="action" value='<%=Action.MODIFY%>' />
<input type="hidden" name="userStatus" />
<input type="hidden" name="searchFilter" value="<%=userList.getFilter()%>"/>
<input type="hidden" name="sortOrder" value="<%=userList.getSortOrder()%>"/>
<input type="hidden" name="mode" />

<br>

<channel:channel name='<%="DomainUserList_" + applicationSpace.getName()%>' customizable="false">
    <channel:insert name='<%="DomainUserList_" + applicationSpace.getName()%>'
                    title="User List" minimizable="false" closeable="false"
                    include="include/DomainUserList.jsp">
	    <channel:button style="channel" type="modify" href="javascript:modifyUser();"/>					
	    <channel:button style="channel" type="remove" label="Disable User Account" href="javascript:removeUser();"/>
	    <channel:button style="channel" type="properties" label='Resend Verification Code' href="javascript:resend();"/>					
	</channel:insert>						
</channel:channel>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>


</body>
</html>
