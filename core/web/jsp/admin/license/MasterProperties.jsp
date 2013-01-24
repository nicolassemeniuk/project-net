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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Displays master properties
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Master properties"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%
    net.project.license.system.MasterProperties masterProperties = net.project.license.system.MasterProperties.getInstance();
    pageContext.setAttribute("masterProperties", masterProperties, PageContext.PAGE_SCOPE);
%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<template:getDoctype />
<template:insert>
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 

<%-- Additional HEAD stuff --%>
<template:put name="head">
    <template:import type="javascript" src="/src/checkComponentForms.js" />
<template:getSpaceCSS />
<template:getSpaceJS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}
    
function validateForm() {
    return true;
}

function submit() {
    if(validateForm()) {
        theAction("submit");
	    theForm.submit();
    }
}
    
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicensingTasks.jsp?&module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>';
}

function reset() { self.document.location = JSPRootURL + "/admin/license/MasterProperties.jsp?module=<%=Module.APPLICATION_SPACE%>"; }

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=master_properties");
}

function updateProperties() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/MasterPropertiesUpdater.jsp?&module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>';
}

</script>

</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>		
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display="Master Properties"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/MasterProperties.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.MODIFY%>">
    
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;Master Properties</th>
		<th class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="modify" label="Update Properties" function="javascript:updateProperties();" />
				</tb:band>
			</tb:toolbar>
		</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2">
            <pnet-xml:transform name="masterProperties" stylesheet="/admin/license/xsl/master-properties.xsl" />
        </td>
		<td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />