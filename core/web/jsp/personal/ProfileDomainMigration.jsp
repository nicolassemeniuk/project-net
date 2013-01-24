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
    info="Registration Domain Selection"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.security.domain.UserDomain,
			java.util.Iterator,
        	net.project.base.property.PropertyProvider"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="availableDomains" class="net.project.security.domain.UserDomainCollection" scope="request" />

<%
	availableDomains.loadForConfigurationID(user.getCurrentConfigurationID());
	
    Iterator itr = availableDomains.iterator();
	
	while(itr.hasNext()) {
	
		UserDomain userDomain = (UserDomain) itr.next();
		if(user.getUserDomainID() != null && user.getUserDomainID().equals(userDomain.getID())) {
			itr.remove();
		}	
	}
	
	boolean isEnabled = PropertyProvider.getBoolean("prm.global.domainmigration.isenabled") ;
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<%-- Import Javascript --%>

<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["registration"];
    	isLoaded = true;
    }

    function validateForm(frm) {
        if (!verifySelection(frm)) return false;
        return true;
    }

	function applyChanges() {
   		if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == ""))
      		document.registration.nextPage.value = "ProfileName.jsp";

         //if (validateForm(document.registration)){
         	document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
   		// }
	}

	function tabClick(nextPageVal){
   		document.registration.nextPage.value = nextPageVal;
   		applyChanges();
	}

    function processForm() {
        if (validateForm(theForm)) {
            theForm.submit();
        }
    }
	    
    function cancel() {
    <%--Avinash: bfd 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
	var referer = "<%= (String)session.getAttribute("referer")== null ?  SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+ net.project.base.Module.PERSONAL_SPACE  : SessionManager.getJSPRootURL() + "/" + (String)session.getAttribute("referer")+ "?module=" + net.project.base.Module.PROJECT_SPACE %>";
	self.document.location = referer ; 
}
	
	function submit() {
		migrateDomain();
    }
	
	function help() {
    	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=domain_migration";
    	openwin_help(helplocation);
	}
	
	function migrateDomain() {
		if (validateForm(theForm)) {
	 		var m_url = (JSPRootURL + "/domain/UserDomainMigration1.jsp?selected="+getSelection(theForm) + "&WizardMode=PopUp&module=<%= net.project.base.Module.PERSONAL_SPACE %>");
			var myWindow = openwin_large("myWindow" , m_url);
	 		myWindow.focus();
		}	
	} 

</script>
</head>
    
<%------------------------------------------------------------------------
  -- Start of Form Body
  ----------------------------------------------------------------------%>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history" showSpaceDetails="false">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" display='<%=PropertyProvider.get("prm.personal.profile.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/personal/ProfileDomainMigration.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
			<history:page displayToken="prm.personal.profile.domain.tab" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />

<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.name.tab")%>' href="javascript:tabClick('ProfileName.jsp');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.address.tab")%>' href="javascript:tabClick('ProfileAddress.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.login.tab")%>' href="javascript:tabClick('ProfileLoginController.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.license.tab")%>' href="javascript:tabClick('ProfileLicense.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.domain.tab")%>' href="javascript:tabClick('ProfileDomainMigration.jsp');" selected="true"/>
</tab:tabStrip>
<br />

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/personal/ProfileDomainMigrationController.jsp"%>" method="post">
	<input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="domainSelect">
	<input type="hidden" name="nextPage" VALUE="">

<div align="center">
<table width="600" cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td colspan="4" class="tableHeader">
			<td colspan="4">&nbsp;</td>
	</tr>
	
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.personal.profile.domain.channel.domain.title")%></th>
		<th class="channelHeader" align="right">
<% 
	if(!user.isApplicationAdministrator() && isEnabled ) {
%>	
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="create" label='<%=PropertyProvider.get("prm.personal.profile.domain.migrate.button.label")%>' function="javascript:migrateDomain();" />
				</tb:band>
			</tb:toolbar>
<%
	}
%>				
		</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
<% 
	if(!user.isApplicationAdministrator() && isEnabled ) {
%>
            <%-- Display the selection of domains --%>
            <pnet-xml:transform name="availableDomains" scope="request" stylesheet="/personal/xsl/UserDomainSelect.xsl" />
<%
	} else if (user.isApplicationAdministrator()) {
%>				
		<%=PropertyProvider.get("prm.project.personal.application.admin.label") %>
<%
	} else if (!isEnabled ) {
%>	
		<%=PropertyProvider.get("prm.personal.profile.domain.disabled.message")%>			
<%
	}
%>			
        </td>
        <td>&nbsp;</td>
    </tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" label='<%=PropertyProvider.get("prm.personal.profile.domain.continue.button.label")%>'/>
	</tb:band>
</tb:toolbar>
	
</form>

</div>

<template:getSpaceJS />
</body>
</html>
