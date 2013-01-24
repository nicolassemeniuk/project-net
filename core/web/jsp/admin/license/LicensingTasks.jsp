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
    info="Tasks Main"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.license.system.MasterProperties,
            net.project.license.system.PropertyName"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
<%
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.APPLICATION_SPACE);
%>
<template:getDoctype />
<template:insert>
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" />

<%----- import Javascript Files -------------------------------------%>

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

function reset() { self.document.location = JSPRootURL + "/admin/license/Main.jsp?module=<%=Module.APPLICATION_SPACE%>"; }

function tabClick(nextPage) {
	self.document.location = JSPRootURL + nextPage + '?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>';
}

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=license_tasks");
}

</script>


</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Licensing"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<%-- Tab Bar --%>
<tab:tabStrip>
    <tab:tab label="Manage Existing Licenses" href="javascript:tabClick('/admin/license/LicenseListView.jsp');" />
	<tab:tab label="Licensing Tasks" href="javascript:tabClick('/admin/license/LicensingTasks.jsp');" selected="true" />
</tab:tabStrip>

<table width="100%" cellpadding="0" cellspacing="0" border="0">

	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2">
        <table>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/admin/license/MasterProperties.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>">
                        <%=PropertyProvider.get("prm.project.admin.license.view.license.properties.label")%>
                    </a>
                </td>
            </tr>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/admin/license/MasterPropertiesUpdater.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                        <%=PropertyProvider.get("prm.project.admin.license.update.license.properties.label")%>
                    </a>
                </td>
            </tr>
            <% //Show the master properties creation page, as long as the page exists
              if ( application.getResource("/pni/admin/license/MasterPropertiesCreator.jsp") != null ) {
            %>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/pni/admin/license/MasterPropertiesCreator.jsp?module="+Module.APPLICATION_SPACE%>">
                        <%=PropertyProvider.get("prm.project.admin.license.create.license.properties.label")%>
                    </a>
                </td>
            </tr>
            <% } %>
			<tr><td colspan="2">&nbsp;</td></tr>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseCreator.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                        <%=PropertyProvider.get("prm.project.admin.license.install.new.label")%>
                    </a>
                </td>
            </tr>
            <% //Show the licensing page, as long as the licensing page exists
               if ( application.getResource("/pni/admin/license/LicenseCreator.jsp") != null ) {
            %>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/pni/admin/license/LicenseCreator.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                        <%=PropertyProvider.get("prm.project.admin.license.create.new.label")%>
                    </a>
                </td>
            </tr>
            <%
            } else 
				//The LicenseCreator page doesn't exist, let's see if the node locked version is present
	            if ( application.getResource("/pni/admin/license/LicenseCreatorNodeLocked.jsp") != null ) {

            %>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/pni/admin/license/LicenseCreatorNodeLocked.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                        <%=PropertyProvider.get("prm.project.admin.license.create.new.label")%>
                    </a>
                </td>
            </tr>
            <% } else 
            		if ( application.getResource("/pni/admin/license/LicenseCreatorNodeLocked.jsp") != null ) {
            %>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/pni/admin/license/LicenseCreatorNodeLocked.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                        <%=PropertyProvider.get("prm.project.admin.license.create.new.label")%>
                    </a>
                </td>
            </tr>
            <% } %>
            <%
                MasterProperties props = null;
                if (MasterProperties.masterPropertiesExist()) {
                    props = MasterProperties.getInstance();
                }

                if (props != null &&
                    props.get(PropertyName.CREDIT_CARD_ENABLED) != null &&
                    props.get(PropertyName.CREDIT_CARD_ENABLED).getValue() != null &&
                    new Boolean(props.get(PropertyName.CREDIT_CARD_ENABLED).getValue()).booleanValue()) {
            %>
			<tr><td colspan="2">&nbsp;</td></tr>
            <tr>
                <td class="fieldContent" colspan="2">
                    <a href="<%=SessionManager.getJSPRootURL() + "/admin/license/PurchaseLicenses.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>">
                         <%=PropertyProvider.get("prm.project.admin.license.purchase.label")%>
                    </a>
                </td>
            </tr>
            <% } %>
		</table>
        </td>
		<td>&nbsp;</td>
    </tr>
</table>
</template:put>
<%-- End Content --%>
</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />