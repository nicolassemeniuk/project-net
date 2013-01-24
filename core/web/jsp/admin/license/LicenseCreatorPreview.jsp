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
|  Previews license before storing
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Preview"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="externalLicenseCreator" class="net.project.license.create.ExternalLicenseCreator" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
                       
<template:getDoctype />
<template:insert> 
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 
<template:getSpaceCSS />
<%-- Additional HEAD stuff --%>
<template:put name="head">
<template:getSpaceJS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}

function finish() {
    theAction("finish");
    theForm.submit();
}
    
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp?&module=" + Module.APPLICATION_SPACE%>';
}

function reset() { self.document.location = JSPRootURL + "/admin/license/LicenseCreatorPreview.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>"; }

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=external_create_preview");
}

</script>

</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>		
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page level="2" display="Preview"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseCreatorPreview.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseCreatorPreviewProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.MODIFY%>">
    
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.project.admin.license.create.preview.label") %></th>
		<th class="channelHeader" align="right">&nbsp;</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr> 
	<tr><td colspan="4">&nbsp;</td></tr>
<%  if (externalLicenseCreator.isInconsistentNodeID()) { %>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="fieldWithError">
            <%=PropertyProvider.get("prm.project.admin.license.error.license.message")%>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <%=PropertyProvider.get("prm.project.admin.license.node.id.label")%> <code><%=externalLicenseCreator.getLicenseNodeIDDisplayString()%></code><br>
            <%=PropertyProvider.get("prm.project.admin.license.current.system.label")%> <code><%=externalLicenseCreator.getSystemNodeIDDisplayString()%></code>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<%  } %>
    <tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <%=PropertyProvider.get("prm.project.admin.license.license.not.being.installed.label")%>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="externalLicenseCreator" scope="session" stylesheet="/admin/license/xsl/license-view.xsl" />
        </td>
		<td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="finish" show='<%="" + !externalLicenseCreator.isInconsistentNodeID()%>' />
	</tb:band>
</tb:toolbar>

</form>

</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />