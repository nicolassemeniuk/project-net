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
|  Updates License Master properties from an encrypted string
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Creator Result"
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

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<template:getDoctype />
<template:insert> 
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 

<%-- Additional HEAD stuff --%>
<template:put name="head">
<template:getSpaceCSS />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:getSpaceJS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}
    
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicensingTasks.jsp?&module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>';
}

function reset() { self.document.location = JSPRootURL + "/admin/license/LicenseCreatorResult.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>"; }

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=external_creator_results");
}

</script>

</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>		
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display="Create License"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseCreatorResult.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.project.admin.license.create.license.results.label") %></th>
		<th class="channelHeader" align="right">&nbsp;</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<% if (request.getParameter("licenseCreate").equals("success")) { %>
	<tr align="left">
		<td>&nbsp;</td>
        <td class="tableContent" colspan="2">
		    <%=PropertyProvider.get("prm.project.admin.license.created.succesfulyy.label") %>
		    <%=PropertyProvider.get("prm.project.admin.license.key.label") %>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td class="tableContent" colspan="2">
            <h4><%=externalLicenseCreator.getGeneratedLicenseKeyDisplay()%></h4>
        </td>
		<td>&nbsp;</td>
    </tr>
<% } else { %>

	<tr align="left">
		<td>&nbsp;</td>
        <td class="tableContent" colspan="2">
            <%=PropertyProvider.get("prm.project.admin.block.license.message") %>
			<P>
			<%=PropertyProvider.get("prm.project.admin.contact.project.net.license.message") %>   
        </td>
		<td>&nbsp;</td>
    </tr>
	
<% } %>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" label="Close" />
	</tb:band>
</tb:toolbar>

</form>

</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />