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

<% String id = request.getParameter("id"); %>
<security:verifyAccess action="view"
                       module="<%=net.project.base.Module.APPLICATION_SPACE%>" />
		       

<%-- SET THE CURRENT SEARCH MODE --%>
<%
      if (request.getParameter("displayMode") != null) {
	  userList.setDisplayMode (request.getParameter("displayMode"));
      }
//Avinash: set blank if it is null
	else{
		userList.setDisplayMode ("");
	}
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
    load_menu();
    load_header();
    isLoaded = true;
	theForm = self.document.forms["mainForm"];
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_license&section=userlist";
    openwin_help(helplocation);
}

function search(filter) {
    if (!filter) {
   	var message = '<%=PropertyProvider.get("prm.project.admin.license.question.js")%>';
   	
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', message, function(btn) { 
			if(btn == 'yes'){ 
				theForm.searchFilter.value = filter;
	    		theForm.action.value=<%=Action.MODIFY%>;
	    		theForm.theAction.value="search";

	    		theForm.submit();
			}else{
			 	return false;
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

function remove() {
    
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		str=getSelection(theForm).substring(0,1);
		if(theForm.userStatus.value=="D") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.disable.disabled.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.disable.unregistered.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="remove";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
	}	 
}

function associate() {
    
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		str=getSelection(theForm).substring(0,1);
			theForm.theAction.value="associate";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
				
	}	 
}

function assignResponsibleUser() {
    
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		str=getSelection(theForm).substring(0,1);
			theForm.theAction.value="assignResponsibility";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
				
	}	 
}

function changeUserStatus(personID, userStatus) {

	openwin_small("status_win",statusWin.document.location =  JSPRootURL + '/admin/UserStatus.jsp?personID=' + personID + 
	'&currentStatus=' + userStatus);
}


function modify() { 
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {	
		if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.modify.unregistered.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else if(theForm.userStatus.value=="R") {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.cannot.disable.unregistered.user.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}	
		else {
			theForm.theAction.value="modify";
			theForm.action.value = "<%=Action.MODIFY%>";
			theForm.submit();
		}		
}	 
	
}

function reset() {
    self.document.location = JSPRootURL + '/admin/license/UserList.jsp?module=' + Module + '&action=<%=Action.VIEW%>';
}

function cancel() {
	self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp?"%>' +  '<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>';

}

function showDetail(userID) {
    self.document.location = JSPRootURL + '/admin/license/UserView.jsp?module=' + Module + '&action=<%=Action.VIEW%>&userID=' + userID;
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

</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onload=setup(); leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />

<%-- Create the toolbar --%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
			<history:business display="<%= applicationSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
            <history:module display="Licensing"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
            <history:page display="Associate User With License"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/UserList.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="modify" />
        <tb:button type="remove" label="Disable User Account"/>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="<session:getJSPRootURL />/admin/license/UserListProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>" />
<input type="hidden" name="action" value='<%=Action.MODIFY%>' />
<input type="hidden" name="userStatus" />
<input type="hidden" name="searchFilter" value="<%=userList.getFilter()%>"/>
<input type="hidden" name="sortOrder" value="<%=userList.getSortOrder()%>"/>
<input type="hidden" name="mode" />
<input type="hidden" name="licenseKey" value="<%= request.getParameter("licenseKey")%>" />

<table border="0" cellspacing="0" cellpadding="0" width="100%">

	<tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" align="left" ><%=PropertyProvider.get("prm.project.admin.license.search.users.label")%> </td>
		<td class="channelHeader" align="right" nowrap>
		&nbsp;				
		<a class="channelNoUnderline" href="javascript:cancel();">    
			Cancel &nbsp;<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-cancel_off.gif" width="15" height="15" alt="Cancel" border="0" align="absmiddle"/>
		</a>
		</td>
		<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
</table>	

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr><td>&nbsp;</td></tr>
		<tr>
		<td>&nbsp;&nbsp;<span class=tableContent><search:letter /></span>
		&nbsp;&nbsp;
		</td>
		<td class="tableHeader">&nbsp;&nbsp;<b><%=PropertyProvider.get("prm.project.admin.domain.keyword.label")%></b>
			<input type="text" onkeyPress="keyhandler(event)" name="key" size="15" maxlength="40" value="<%=userList.getFilter()%>">
			<a href="javascript:search(self.document.forms['mainForm'].key.value);"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"></a>
			&nbsp;&nbsp;
		</td>
		<tr>
		<td colspan=2 class="tableContent"><i>
			<%=PropertyProvider.get("prm.project.admin.license.clicking.letter.will.search.message")%></i>
		</td>
		</tr>
<tr><td>&nbsp;</td></tr>
<tr>
		<td rowspan="2" class="tableHeader">&nbsp;&nbsp;User Status: </br>
		<select name="userStatusFilter" multiple height="4">
		   <%=net.project.resource.ProfileCodes.getUserStatusOptionList(userList.getUserStatusFilter())%>
		</select>
		</td>
		<td rowspan="2" class="tableHeader">&nbsp;&nbsp;Domain(s): </br>
		<select name="userDomainFilter" multiple height="4">
		<%
	net.project.security.domain.DomainOptionList optionList = new net.project.security.domain.DomainOptionList();
	       		optionList.loadAll();
                 %>
		   <%=optionList.getDomainOptionList (userList.getUserDomainFilter())%>
		</select>
		</td>
</tr>	
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
    	<td class="warnText">
    	<!-- Avinash: checking for null values -->
			<%= (String)session.getAttribute("addUserMessage") != null ? (String)session.getAttribute("addUserMessage") : "" %>
			<%= (String)session.getAttribute("assignResponsibleUserMessage") != null ? (String)session.getAttribute("assignResponsibleUserMessage") : "" %>
		</td>
		<%
			 session.removeAttribute("addUserMessage"); 
			 session.removeAttribute("assignResponsibleUserMessage"); 
		%>
		<tr><td>&nbsp;</td></tr>
  	<tr>
    	<td>
<%
    // load and display the userList if the display mode is set to "search"

     if ("search".equals(userList.getDisplayMode())) {
	 
         // see if a filter has been passed in the request
	 String searchFilter = request.getParameter("searchFilter");
	 if (searchFilter != null && !searchFilter.equals("")) {
	     userList.setFilter (searchFilter);
         }
      
         // then load the filtered list
	 userList.loadFiltered();	
     }

     else if ("initial".equals(userList.getDisplayMode())) {
         // clear the userList and all current filters
	 userList.clear();
     }
%>

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td colspan="4">
	<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">
    <channel:insert name='<%="AssociateUserToLicense_" + applicationSpace.getName()%>'
                    title='<%= "Search Result (number of matches) : " + userList.size()%>' minimizable="false" closeable="false">                    
	<channel:button style="channel" type="create" label='Associate User With License' href="javascript:associate();"/>	
	<channel:button style="channel" type="modify" label='Assign Responsibility' href="javascript:assignResponsibleUser();"/>
	</channel:insert>						
	</channel:channel>
    </td></tr>
        <tr class="channelHeader">
             <%-- <td class="channelContent">&nbsp;</td> --%>
              <td colspan="4" align="center">

			 <jsp:setProperty name="userList" property="stylesheet" value="/admin/license/xsl/user-list.xsl" /> 
			 <jsp:getProperty name="userList" property="presentation" />               
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


<template:getSpaceNavBar space="application"/>
</body>
</html>
