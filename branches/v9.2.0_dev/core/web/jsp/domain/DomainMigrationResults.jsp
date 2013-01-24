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
    info="User Details"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.resource.Person,
            net.project.security.User,
            net.project.security.Action,
            net.project.security.SecurityProvider,
			net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="userDomainMigrationManager" class="net.project.security.domain.UserDomainMigrationManager" scope="session"/>
<security:verifyAccess action="view"
                       module="<%=net.project.base.Module.PERSONAL_SPACE%>" />
<%
	String wizardMode = (String)pageContext.getAttribute("WizardMode" , pageContext.SESSION_SCOPE);
	
	Object[] str = { userDomainMigrationManager.getTargetDomain().getName() };
	
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%----- Import CSS --------------------------------------------------%>
<template:getSpaceCSS />
<%----- import Javascript Files -------------------------------------%>

<script language="JavaScript">
    JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    
	function setup() {
		theForm = self.document.forms[0];
    	isLoaded = true;
	}
	
    function back(){
		theAction("back");
		theForm.submit();
	}
	
	function finish() {
	Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.domain.domainmigrationresults.confirmation.message" />', function(btn) { 
			if(btn == 'yes'){ 
				theAction("finish");
				theForm.submit();
			}
		});
	}
	
    function cancel() { 
		
		<% 
			if(wizardMode != null && wizardMode.equals("PopUp")) {
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

    function help() {
		var helplocation = JSPRootURL + "/help/Help.jsp?page=domain_migration_results";
        openwin_help(helplocation);
    }

</script>
</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%----- Set up the toolbar for the page -----------------------------%>

<div id='content'>

<form method="post" action="DomainMigrationResultsController.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.PERSONAL_SPACE%>">
<input type="hidden" name="fromPage" value="profile">


<table width="100%" cellpadding="1" cellspacing="0" border="0">
    <tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
</table>

<br />

<div align="center">
<table width="80%" border="0" cellspacing="0" cellpadding="0">
	<tr class="actionBar">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.domainmigration.migrationresultspage.channel.title") %></td>		
		<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
	<tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
	<tr>
		<td>&nbsp;</td>			
		<td colspan="4" class="instructions"><%=PropertyProvider.get("prm.personal.domainmigration.migrationresultspage.initial.instructions",str) %>
		</td>		
	</tr>
	<tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
	<tr>
		<td colspan="6" nowrap align="left">
<%
       
		if ( user.getEmail() != null && !user.getEmail().equalsIgnoreCase(registration.getEmail())) {
                registration.setAlternateEmail1(user.getEmail());
        }
			
%>
	
	<pnet-xml:transform name="registration" scope="session" stylesheet="/domain/xsl/user-details.xsl">
	</pnet-xml:transform>
		</td>
	</tr>
	<tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
	<tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
</table>
</div>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="finish" />
	</tb:band>
</tb:toolbar>

</div>

<template:getSpaceJS />
</body>
</html>
