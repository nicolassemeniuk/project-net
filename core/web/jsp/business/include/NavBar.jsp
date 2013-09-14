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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19825 $
|       $Date: 2009-08-22 09:19:43 -0300 (sáb, 22 ago 2009) $
|     $Author: umesha $
|
| Includable left side navbar
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/javascript; charset=UTF-8"
    info="Business Navigation" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.ObjectType,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.business.BusinessSpace, 
            net.project.space.Space,
			net.project.security.Action,
            net.project.security.SessionManager,
			net.project.search.SearchManager,
            net.project.search.IObjectSearch,
            net.project.security.User,
			net.project.gui.navbar.FeaturedFormItems,
			net.project.util.StringUtils,
			net.project.wiki.WikiURLManager" 
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    // Figure out whether to load the menu
    // We load the menu if the user is not looking at a portfolio
    // and there is a current project space
    boolean bLoadMenu;
    String loadMenu = null;
    String currentID = null;
    String currentSpaceType = null;

	String base = SessionManager.getJSPRootURL();
	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormList.jsp?module=" + Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
    boolean isPortfolio = (request.getParameter("portfolio") != null && request.getParameter("portfolio").equals("true"));
    if (isPortfolio) {
        bLoadMenu = false;

    } else {
        // We are loading while viewing a space
        // Only display if there is a current space and it is a business space
        currentID = user.getCurrentSpace().getID();
        currentSpaceType = user.getCurrentSpace().getType();
        bLoadMenu= (currentID != null) && (currentSpaceType.equals(Space.BUSINESS_SPACE) ||
                (StringUtils.isNotEmpty(request.getParameter("s")) && request.getParameter("s").contains(Space.BUSINESS_SPACE)));
    }
    loadMenu = new Boolean(bLoadMenu).toString();
%>

function writeSpaceNavBarMenu() {
	var menuString = "";
	<%if(bLoadMenu) {%>
	menuString += "<table class='menu-subbusiness'> <tr valign='top' height='19'> <td> <div> ";
	menuString += " <ul class='dropdown'>";

<display:if name="@prm.business.dashboard.isenabled">
	menuString += " <li>";
	menuString += " 	<span><display:get name='@prm.business.nav.dashboard' href='<%=base + "/business/Main.jsp" %>'/></span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.wiki.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.business.nav.wiki' href='<%=base+"/wiki/"+WikiURLManager.getRootWikiPageNameForSpace()%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.business.directory.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.directory' href='<%=base + "/business/DirectorySetup.jsp?module="+Module.DIRECTORY+"&id="+user.getCurrentSpace().getID()%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.material.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.business.nav.material' href='<%=base+"/material/MaterialDirectory.jsp?module="+Module.MATERIAL%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>
	
<display:if name="@prm.business.document.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/document/Main.jsp?module="+Module.DOCUMENT%>' onmouseover=\"mopen('mdocument')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.business.nav.document" /></a>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>
	
<display:if name="@prm.business.discussion.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.discussion' href='<%=base + "/discussion/Main.jsp?module="+Module.DISCUSSION%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.form.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 		<a href='<%=base+"/form/Main.jsp?module="+Module.FORM%>' onmouseover=\"mopen('mform')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.business.nav.form" /></a>";
	menuString += " 	</span>";
	<%if(!"".equals(formsMenuString)) {%>
	menuString += " 		<br/>";
	menuString += " 		<div id='mform' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += " 				<%= forms.getNavBarHTML() %>";
	menuString += " 		</div>";
	<%}%>
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.project.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.project' href='<%=base + "/business/Main.jsp?id="+currentID+"&page="+base+"/portfolio/BusinessPortfolio.jsp?module="+Module.BUSINESS_SPACE%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.calendar.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.calendar' href='<%=base + "/calendar/Main.jsp?module="+Module.CALENDAR%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.workflow.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.workflow' href='<%=base + "/workflow/Main.jsp?module="+Module.WORKFLOW%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

<display:if name="@prm.business.setup.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.business.nav.setup' href='<%=base + "/business/Main.jsp?id="+currentID+"&page="+base+"/business/Setup.jsp?module="+Module.BUSINESS_SPACE%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>
	menuString += " </ul>";
	
	menuString += "</div> </td> <td>&nbsp;</td></tr> </table> ";
<% } %>
	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subbusiness';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subbusiness');
	navBar.innerHTML = menuString ;
}
