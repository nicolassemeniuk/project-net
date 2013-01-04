<%--
 * Copyright 2000-2009 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Master Properties preview"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
			net.project.license.system.MasterPropertiesNotFoundException,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="masterPropertiesUpdater" class="net.project.license.system.MasterPropertiesUpdater" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<template:getDoctype />
<template:insert>
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" />

<%-- Additional HEAD stuff --%>
<template:put name="head">
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

function finish() {
    theAction("finish");
    theForm.submit();
}
    
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp?&module=" + Module.APPLICATION_SPACE%>';
}

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=properties_preview");
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
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/MasterPropertiesPreview.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/license/MasterPropertiesPreviewProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.MODIFY%>">
    
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("project.admin.license.update.master.properties.label")%></th>
		<th class="channelHeader" align="right">&nbsp;</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<% 
	try {
	 	if (masterPropertiesUpdater.isInconsistentProductInstallationID()) { 
%>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="fieldWithError">
            <%=PropertyProvider.get("prm.project.admin.license.warining.new.product.message")%>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<%  
		}
	} catch (MasterPropertiesNotFoundException mpnfe) {
		if (masterPropertiesUpdater.checkSystemHasExistingLicenses()) {
%>
		<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="fieldWithError">
            <%=PropertyProvider.get("prm.project.admin.license.warining.new.product.message2")%>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<%		}
	} 
%>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2" class="tableContent">
            <pnet-xml:transform name="masterPropertiesUpdater" scope="session" stylesheet="/admin/license/xsl/master-properties.xsl" />
        </td>
		<td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="finish" />
	</tb:band>
</tb:toolbar>

</form>
</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />