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
|   $Revision: 20770 $
|       $Date: 2010-04-29 09:23:10 -0300 (jue, 29 abr 2010) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile License"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.security.AuthorizationFailedException,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="licenseProfile" class="net.project.license.LicenseProfile" scope="page" />
<jsp:useBean id="licenseHistory" class="net.project.license.LicenseHistory" scope="page" />

<%  // No security validation necessary since a user can only access their own Profile
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.PERSONAL_SPACE);
%>

<%
	registration.setID(user.getID());
	registration.setEmail(user.getEmail());
	registration.load();

    // Set and load profile and history
    licenseProfile.setPerson(registration);
    licenseHistory.setPerson(registration);

    String buttonLabel = PropertyProvider.get("prm.personal.profile.license.switch.button.label");
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var updatedProfile = false;

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= jspRootURL %>';  

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function cancel()    { 
<%--Avinash: bfd 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
	var referer = "<%= (String)session.getAttribute("referer")== null ?  SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+ net.project.base.Module.PERSONAL_SPACE  : SessionManager.getJSPRootURL() + "/" + (String)session.getAttribute("referer")+ "?module=" + net.project.base.Module.PROJECT_SPACE %>";
	self.document.location = referer ; 
}
function submit()    { applyChanges(); }

function validateForm() {
    if (!checkRadio(theForm.selectionTypeID,'<%=PropertyProvider.get("prm.personal.profile.license.selctoption.message")%>')) return false;
    return true;
}

function applyChanges() {
    if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == ""))
        document.registration.nextPage.value = '<%="/personal/ProfileLicense.jsp" %>';

    if (updatedProfile) {
       Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.personal.profile.license.update.message")%>', function(btn) { 
			if(btn == 'yes') { 
				if (validateForm(document.registration))
					document.registration.submit();
			} else {
				 document.location = document.location;
			}
		});
   }else{
      document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
   }
}

function setUpdated(updated){
    updatedProfile = updated;
}

function help()
{
    var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=license";
    openwin_help(helplocation);
}

function tabClick(nextPageVal){
   document.registration.nextPage.value = nextPageVal;
   applyChanges();
}

function assignLicense() {
    theAction('assignLicense');
    theForm.submit();
}
<%-- bfd 3238: JS Error occured When Refreshing the License Page of setup Personal Profile --%>
function reset() {
theForm.reset();
setup();
}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history" showSpaceDetails="false">
    <tb:setAttribute name="leftTitle">
        <history:history>
        <history:page level="1" display='<%=PropertyProvider.get("prm.personal.profile.module.history")%>' 
                  jspPage='<%=jspRootURL + "/personal/ProfileName.jsp"%>'
                    queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
        <history:page displayToken="prm.personal.profile.license.tab" />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />

<%-- Tab Bar --%>
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.name.tab")%>' href="javascript:tabClick('ProfileName.jsp');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.address.tab")%>' href="javascript:tabClick('ProfileAddress.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.login.tab")%>' href="javascript:tabClick('ProfileLoginController.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.license.tab")%>' href="javascript:tabClick('ProfileLicense.jsp');" selected="true"/>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.domain.tab")%>' href="javascript:tabClick('ProfileDomainMigration.jsp');" />
</tab:tabStrip>

<form name="registration" action='<%=SessionManager.getJSPRootURL()+"/personal/ProfileLicenseProcessing.jsp"%>' method="post">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="action" value="<%="" + Action.MODIFY%>">
    <input type="hidden" name="nextPage" value="">
    <input type="hidden" name="theAction" value="">

<div align="center">

<table width="600" cellpadding=0 cellspacing=0 border=0>
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.personal.profile.license.channel.info.title")%></th>
		<th class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="create" label="<%=buttonLabel%>" function="javascript:assignLicense();" />
				</tb:band>
			</tb:toolbar>
		</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
    <tr><td colspan="4">&nbsp;</td></tr>
<%  if (!licenseProfile.hasLicense()) { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
          <%=PropertyProvider.get("prm.personal.profile.license.nocurrentlicense.message")%>
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } else { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="licenseProfile" stylesheet="/personal/xsl/license-properties.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } %>

    <tr align="left"><td colspan="4">&nbsp;</td></tr>
    
    <%-- License History --%>
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader" colspan="2">&nbsp;<%=PropertyProvider.get("prm.personal.profile.license.channel.history.title")%></th>
        <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
<%  if (!licenseHistory.hasHistory()) { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <%=PropertyProvider.get("prm.personal.profile.license.noprevious.title")%>
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } else { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="licenseHistory" stylesheet="/personal/xsl/license-history.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } %>
    </table>
</div>
    
<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
