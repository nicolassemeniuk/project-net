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
    info="Initiate Domain Migration -- Step 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.SessionManager,
			net.project.security.domain.DomainMigrationStatus,
			net.project.base.property.PropertyProvider,
			net.project.security.domain.UserDomain,
			net.project.resource.PersonListBean"
%>                                                                        
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />
<jsp:useBean id="userDomainMigrationManager" class="net.project.security.domain.UserDomainMigrationManager" scope="session"/>

<html>
<head>
<title><%=PropertyProvider.get("prm.application.domainmigration.initialinstructionspage.title") %></title>
<%

	String wizardMode = request.getParameter("WizardMode");	
	boolean isPopUp = false ;
	
	if(wizardMode != null)
		pageContext.setAttribute("WizardMode",wizardMode , pageContext.SESSION_SCOPE);
	
	if(request.getParameter("selected") != null) {
		
		userDomainMigrationManager.setTargetDomainID(request.getParameter("selected"));
		userDomainMigrationManager.setUser(user);
		userDomainMigrationManager.setSourceDomainID(UserDomain.getInstance(user).getID());
	}
	
	Object str[] = { userDomainMigrationManager.getSourceDomain().getName() , userDomainMigrationManager.getTargetDomain().getName() } ;
%>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
	<% 
		if(wizardMode != null && wizardMode.equals("PopUp")) {
			isPopUp = true;
	%>
		self.close();	
	<%
		 } else {
	%>
		var m_url = JSPRootURL + "/NavigationFrameset.jsp";
		self.document.location = m_url;
		
	<%
		}
	%>	
}

function next() {
<% 
	if(!isPopUp) {
%>
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
<%
	}
%>		
	theAction("next");
	theForm.submit();
<% 
	if(!isPopUp) {
%>
	}
<%
	}
%>			
}

function help(){
	var helplocation=JSPRootURL+"/help/Help.jsp?page=domainmigration_initiate&section=page1";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();">
<form action="<%=SessionManager.getJSPRootURL()%>/domain/UserDomainMigration1Processing.jsp" method="post">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%= net.project.base.Module.PROJECT_SPACE %>" > 
<% 
	if(isPopUp) {
%>
<input type="hidden" name="selected" value="<%= DomainMigrationStatus.NEVER_STARTED%>">
<%
	}
%>

<div align="center">
  	
<table width="80%" border="0" cellspacing="0" cellpadding="0">
	<tr><td colspan="4">&nbsp;</td></tr>

	<tr class="actionBar">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
		<td colspan="2" valign="middle" class="actionBar"><%=PropertyProvider.get("prm.personal.domainmigration.initialinstructionspage.channel.title") %></td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>

	<tr><td colspan="4">&nbsp;</td></tr>
	
<%  if(!isPopUp ) { %>
    	<tr>
    		<td>&nbsp;</td>
    		<td colspan="2" class="instructions"><%=PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.initial.instructions", str) %></td>
    		<td>&nbsp;</td>
    	</tr>

    	<tr><td colspan="4">&nbsp;</td></tr>

    <%  if( userDomainMigrationManager.getMigrationMessage() != null ) { %>
        	<tr class="tableLine">
        		<td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="1" border="0"></td>
	        </tr>
        	<tr>
                <td>&nbsp;</td>
        		<td colspan="2" class="tableHeader"><%=PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.applicationadministratorsmessage.label")%></td>
                <td>&nbsp;</td>
	        </tr>
	        <tr>
                <td>&nbsp;</td>
		        <td colspan="2" class="tableContent">
                    <output:text><%= userDomainMigrationManager.getMigrationMessage() %></output:text>
                </td>
                <td>&nbsp;</td>
	        </tr>
	        <tr class="tableLine">
        		<td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="1" border="0"></td>
        	</tr>
        	<tr><td colspan="4">&nbsp;</td></tr>

    <%  } %>
    	<tr>
            <td>&nbsp;</td>
            <td colspan="2" class="tableContent"><input type="Radio" name="selected" value="<%= DomainMigrationStatus.NEVER_STARTED%>">&nbsp;&nbsp;<%= PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.continueoption.text") %></input> </td>
            <td>&nbsp;</td>
    	</tr>
    	<tr>
            <td>&nbsp;</td>
            <td colspan="2" class="tableContent"><input type="Radio" name="selected" value="<%= DomainMigrationStatus.SKIP_FOR_NOW%>">&nbsp;&nbsp;<%= PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.skipoption.text") %></input> </td>
            <td>&nbsp;</td>
    	</tr>
    	<tr>
            <td>&nbsp;</td>
            <td colspan="2" class="tableContent"><input type="Radio" name="selected" value="<%= DomainMigrationStatus.REMIND_LATER%>">&nbsp;&nbsp;<%= PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.remindmeoption.text") %></input> </td>
            <td>&nbsp;</td>
    	</tr>
    	<tr>
            <td>&nbsp;</td>
            <td colspan="2" class="tableContent"><input type="Radio" name="selected" value="<%= DomainMigrationStatus.USER_CANCELLED%>">&nbsp;&nbsp;<%= PropertyProvider.get("prm.personal.domainmigration.administratorinstructed.wizard1.canceloption.text") %></input> </td>
            <td>&nbsp;</td>
    	</tr>

<%  } else { %>
        <tr>
		    <td>&nbsp;</td>
		    <td colspan="2" class="instructions"><%=PropertyProvider.get("prm.personal.domainmigration.userselfadministered.initial.instructions", str) %></td>
            <td>&nbsp;</td>
	    </tr>
<%  } %>
</table>

<tb:toolbar style="action" showLabels="true">
            <tb:band name="action">
                <tb:button type="next" />
		        <tb:button type="cancel" show='<%= ""+isPopUp %>' />
            </tb:band>
</tb:toolbar>

</div>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
<template:getSpaceJS />
</html>