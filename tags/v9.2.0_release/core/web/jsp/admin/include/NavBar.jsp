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
    contentType="text/javascript; charset=UTF-8"
    info="Application Space Navigation Bar"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.base.property.PropertyProvider,
            net.project.base.compatibility.Compatibility,
			net.project.gui.navbar.FeaturedFormItems"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

	var space = "application";   
	var spaceID = '<%= user.getCurrentSpace().getID() %>' ;

<%
    // Get the current space id; some menu items need it
    String currentID=user.getCurrentSpace().getID();
	String base = SessionManager.getJSPRootURL();
	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormList.jsp?module=" + Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
%>

function writeSpaceNavBarMenu() {
	var menuString = "";
	menuString += "<table class='menu-subadmin'> <tr valign='top' height='19'> <td> <div> ";
	menuString += "<ul class='dropdown'>";

	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tool.dashboard.name' href='<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp?module=" + Module.APPLICATION_SPACE%>'/></span>";
	menuString += "</li>";
                   
	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tool.directory.name' href='<%=SessionManager.getJSPRootURL() + "/admin/DirectorySetup.jsp?module=" + Module.DIRECTORY%>'/></span>";
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/form/Main.jsp?module="+Module.FORM%>' onmouseover=\"mopen('mform')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.personal.nav.form" /></a>";
	menuString += "	</span>";
	<%if(!"".equals(formsMenuString)) {%>
	menuString += "		<br/>";
	menuString += "		<div id='mform' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "				<%= forms.getNavBarHTML() %>";
	menuString += "		</div>";
	<%}%>
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='#' onmouseover=\"mopen('mappmanager')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.application.nav.appmanager" /></a>";
	menuString += "	</span>";
	menuString += "		<br/>";
	menuString += "		<div id='mappmanager' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "				<a href='<%=base + "/admin/UserList.jsp?module=" + Module.APPLICATION_SPACE + "&displayMode=initial"%>' ><propertyProvider:propertyProvider value="@prm.application.nav.userlist" /></a>";

	menuString += "				<a href='<%=base + "/admin/ConfigurationMenu.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.configuration" /></a>";

	menuString += "				<a href='<%=base + "/admin/workspace/WorkSpace.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' ><propertyProvider:propertyProvider value="@prm.application.nav.workspace" /></a>";

	menuString += "				<a href='<%=base + "/admin/license/Main.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' ><propertyProvider:propertyProvider value="@prm.application.nav.licensemanager" /></a>";

	menuString += "				<a href='<%=base + "/admin/invoice/Main.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' ><propertyProvider:propertyProvider value="@prm.application.nav.billingmanager" /></a>";

	menuString += "				<a href='<%=base + "/admin/domain/Main.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.domainmanager" /></a>";

	menuString += "				<a href='<%=base + "/admin/form/FormsAdmin1.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' ><propertyProvider:propertyProvider value="@prm.application.nav.formadministrator" /></a>";

	menuString += "				<a href='<%=base + "/servlet/AdminController/DocumentAdmin?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY%>' ><propertyProvider:propertyProvider value="@prm.application.nav.docvault" /></a>";

	menuString += "				<a href='<%=base + "/admin/status/Main.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.systemstatus" /></a>";


				<display:if name='<%="" + Compatibility.getConfigurationProvider().isModern()%>'>
	menuString += "					<a href='<%=base + "/servlet/AdminController/Settings?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.systemsettings" /></a>";
				</display:if>
	menuString += "		</div>";
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='#' onmouseover=\"mopen('mutilities')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.application.nav.utilities" /></a>";
	menuString += "	</span>";
	menuString += "		<br/>";
	menuString += "		<div id='mutilities' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "				<a href='<%=base + "/admin/utilities/BuildInfo.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.buildinfo" /></a>";

	menuString += "				<a href='<%=base + "/admin/utilities/JWhich.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.classlookup" /></a>";

	menuString += "				<a href='<%=base + "/servlet/HandlerMappingServlet?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.handlermapping" /></a>";
				
				<display:if name="@prm.application.nav.template.isenabled">
	menuString += "					<a href='<%=base + "/admin/utilities/ApplyMethodologyUtility.jsp?module=" + Module.APPLICATION_SPACE%>' ><propertyProvider:propertyProvider value="@prm.application.nav.template" /></a>";
				</display:if>

	menuString += "		</div>";
	menuString += "</li>";
	menuString += "</ul>";
	menuString += "</div> </td> <td>&nbsp;</td></tr> </table> ";
	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subadmin';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subadmin');
	navBar.innerHTML = menuString ;
}
