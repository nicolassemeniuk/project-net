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

<%----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------%>
<%@ page
    contentType="text/javascript; charset=UTF-8"
    info="Configuration Space Navigation Bar"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.base.property.PropertyProvider,
			net.project.gui.navbar.FeaturedFormItems"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

	var space = "configuration";   

<%
    // Get the current space id; some menu items need it
    String currentID=user.getCurrentSpace().getID();
	String base = SessionManager.getJSPRootURL();
	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormList.jsp?module="+Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
%>

function writeSpaceNavBarMenu() {
	var menuString = "";
	menuString += "<table class='menu-subadmin' width='500'> <tr valign='top' height='19'> <td width='500' nowrap='nowrap'> <div> ";
	menuString += "<ul class='dropdown'>";

	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tool.dashboard.name' href='<%=base + "/configuration/Main.jsp?module="+Module.CONFIGURATION_SPACE %>'/></span>";
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tool.directory.name' href='<%=base + "/configuration/DirectorySetup.jsp?module="+Module.DIRECTORY %>'/></span>";
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.personal.nav.configurations' href='<%=base + "/portfolio/ConfigurationPortfolio.jsp?module=" + Module.CONFIGURATION_SPACE %>'/></span>";
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/form/Main.jsp?module="+Module.FORM%>' onmouseover=\"mopen('mform')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.application.nav.form" /></a>";
	menuString += "	</span>";
	<%if(!"".equals(formsMenuString)) {%>
	menuString += "		<br/>";
	menuString += "		<div id='mform' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "				<%= forms.getNavBarHTML() %>";
	menuString += "		</div>";
	<%}%>
	menuString += "</li>";

	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tokens.title' href='<%=base + "/configuration/brand/BrandTokenEdit.jsp?module="+Module.CONFIGURATION_SPACE+"&action="+Action.MODIFY+"&suppressTokens=true"%>'/></span>";
	menuString += "</li>";
	menuString += "</ul>";
	menuString += "</div> </td> <td>&nbsp;</td></tr> </table> ";

	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subcondiguration';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subcondiguration');
	navBar.innerHTML = menuString ;
}
