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
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile License"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.security.AuthorizationFailedException,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.persistence.PersistenceException,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="licenseProfile" class="net.project.license.LicenseProfile" scope="page" />
<jsp:useBean id="licenseHistory" class="net.project.license.LicenseHistory" scope="page" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.APPLICATION_SPACE);
	String orgLink = (String)session.getAttribute("orgLink");
	if(orgLink == null) {
		orgLink = "/admin/UserList.jsp?module=" + module;
	}
    String orgLinkEncoded = java.net.URLEncoder.encode(orgLink, SessionManager.getCharacterEncoding());
%>

<%-- 
    Security Section
    Verify correct permissions were checked
--%>
<security:verifyAccess module="<%=Integer.valueOf(module).intValue()%>"
                       action="modify" />
<%
    // Currently, only allow actual app admin to make changes
    if (!user.isApplicationAdministrator()) {
        throw new AuthorizationFailedException(PropertyProvider.get("prm.project.document.failed.security.violation.label"));
    }
%>
<%--
    End of Security Section
--%>

<%
    //Grab the userID; this is the user to be edited
    String userID = request.getParameter("userID");
    if (userID == null || userID.length() == 0) {
        throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.profile.missing.parameter.label"));
    }

    // Clear out registration bean to ensure we're editing the correct user
    registration.clear();
    //Load pre-existing information for this user
    registration.setID(userID);
    try {
        registration.load();
    } catch (PersistenceException pe) {
        //Perhaps it was only the directory store that couldn't be contacted, try
        //to load only the local information and see if that works.
        registration.loadLocalInformation();
        response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/profile/" +
            "ProfileLoginController.jsp?module="+module+"&userID="+userID);
    }

    // Set and load profile and history
    licenseProfile.setPerson(registration);
    licenseHistory.setCurrentLicenseExcluded(true);
    licenseHistory.setPerson(registration);

    String buttonLabel = null;
    if (!licenseProfile.hasLicense()) {
        buttonLabel = "Assign a new license";
    } else {
        buttonLabel = "Assign a different license";
    }
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

function cancel()    { self.document.location = JSPRootURL + "<%=orgLink%>"; }
function reset() {
    self.document.location = '<%=jspRootURL + "/admin/profile/ProfileLicense.jsp?userID=" + userID + "&module=" + module + "&action=" + Action.MODIFY + "&orgLink=" + orgLinkEncoded%>';
	
}
function submit()    { applyChanges(); }

function validateForm() {
	var message = '<%=PropertyProvider.get("prm.project.admin.profile.select.option.label")%>';
    if (!checkRadio(theForm.selectionTypeID,"prm.project.admin.profile.select.option.label")) return false;
    return true;
}

function applyChanges() {
    if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == "")){
        document.registration.nextPage.value = '<%="/admin/profile/ProfileLicense.jsp?userID=" + userID + "&module=" + module%>&action=<%=Action.MODIFY%>';
	 }
    if (updatedProfile) {
    		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.profile.update.profile.label")%>', function(btn) { 
				if( btn == 'yes' ){
					if (validateForm(document.registration)){
			            document.registration.submit();
			         } 
		         }  
			});
    } else {
        document.location = JSPRootURL + document.registration.nextPage.value;
    }
}

function setUpdated(updated){
    updatedProfile = updated;
}

function help()
{
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_profile&section=license";
    openwin_help(helplocation);
}

function tabClick(nextPage) {
    document.registration.nextPage.value = nextPage + '?module=<%=module%>&userID=<%=userID%>&action=<%=Action.MODIFY%>';
    applyChanges();
}

function assignLicense() {
    theAction('assignLicense');
    theForm.submit();
}

</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history">
    <tb:setAttribute name="leftTitle">
        <history:history>
        <history:page display="Profile" 
                  jspPage='<%=jspRootURL + "/admin/profile/ProfileName.jsp"%>'
                    queryString='<%="userID="+userID+"&module="+module+"&action=" + net.project.security.Action.MODIFY%>' />
            <history:page level="1" display="Name" />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />

<%-- Tab Bar --%>
<tab:tabStrip>
    <tab:tab label="Name" href="javascript:tabClick('/admin/profile/ProfileName.jsp');" />
    <tab:tab label="Address" href="javascript:tabClick('/admin/profile/ProfileAddress.jsp');" />
    <tab:tab label="Login" href="javascript:tabClick('/admin/profile/ProfileLoginController.jsp');" />
    <tab:tab label="License" href="javascript:tabClick('/admin/profile/ProfileLicense.jsp');" selected="true" />
</tab:tabStrip>
<%-- bfd-3283: Space need to be given between the toolbars --%>
<br />
<form name="registration" action='<%=SessionManager.getJSPRootURL()+"/admin/profile/ProfileLicenseProcessing.jsp"%>' method="post">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="action" value="<%="" + Action.MODIFY%>">
    <input type="hidden" name="nextPage" value="">
    <input type="hidden" name="theAction" value="">
    <input type="hidden" name="userID" value="<%=userID%>">

<div align="center">

<table width="600" cellpadding=0 cellspacing=0 border=0>
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.project.admin.profile.license.information.label") %></th>
		<th class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="create" label="<%=buttonLabel%>" function="javascript:assignLicense();" />
				</tb:band>
			</tb:toolbar>
		</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
    <tr><td colspan="4">&nbsp;</td></tr>
<%  if (!licenseProfile.hasLicense()) { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent"> 
          	<%=PropertyProvider.get("prm.project.admin.profile.person.currenlty.license.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } else { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="licenseProfile" stylesheet="/admin/profile/xsl/license-properties.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } %>

    <tr align="left"><td colspan="4">&nbsp;</td></tr>
    
    <%-- License History --%>
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader" colspan="2">&nbsp;
			<%=PropertyProvider.get("prm.project.admin.profile.license.history.label") %>
			</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
<%  if (!licenseHistory.hasHistory()) { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <%=PropertyProvider.get("prm.project.admin.profile.no.previuos.licenses.found.label") %>
        </td>
        <td>&nbsp;</td>
    </tr>
<%  } else { %>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="licenseHistory" stylesheet="/admin/profile/xsl/license-history.xsl" />
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
