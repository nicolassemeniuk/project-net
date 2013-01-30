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
|   $Revision: 19936 $
|       $Date: 2009-09-09 14:09:00 -0300 (mié, 09 sep 2009) $
|     $Author: dpatil $
|
| Includable left side navbar
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/javascript; charset=UTF-8"
    info="Project Navigation" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.project.*, 
            net.project.space.Space, 
            net.project.security.*, 
            net.project.base.*,
            net.project.search.SearchManager,
            net.project.search.IObjectSearch,
            net.project.base.property.PropertyProvider,
			net.project.gui.navbar.FeaturedFormItems,
			net.project.util.StringUtils,
			net.project.wiki.WikiURLManager" 
%>
<%
response.setHeader("Cache-Control","no-cache"); 	//HTTP 1.1
response.setHeader("Pragma","no-cache"); 			//HTTP 1.0
response.setDateHeader ("Expires", -1);				//IE5 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    // Figure out whether to load the menu
    // We load the menu if the user is not looking at a portfolio
    // and there is a current project space
    boolean bLoadMenu;
    String loadMenu = null;
    String currentID = null;
	String base = SessionManager.getJSPRootURL();
	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormQuickAccess.jsp?module="+Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
    boolean isPortfolio = (request.getParameter("portfolio") != null && request.getParameter("portfolio").equals("true"));
    if (isPortfolio) {
        bLoadMenu = false;
    } else {
        // We are loading while viewing a space
        // Only display if there is a current space and it is a project space
        currentID = user.getCurrentSpace().getID();
        bLoadMenu= (currentID != null) && ((user.getCurrentSpace().getType().equals(Space.PROJECT_SPACE)) || 
                (StringUtils.isNotEmpty(request.getParameter("s")) && request.getParameter("s").contains(Space.PROJECT_SPACE)));
    }
    loadMenu = new Boolean(bLoadMenu).toString();
%>
function writeSpaceNavBarMenu() {
	var menuString = "";
	<%if(bLoadMenu) {%>
	menuString += "<table class='menu-subprojects'> <tr valign='top' height='19'> <td> <div> ";
	menuString += "<ul class='dropdown'>";

<display:if name="@prm.project.dashboard.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base + "/project/Dashboard?module="+Module.PROJECT_SPACE+"&id="+user.getCurrentSpace().getID()%>' onmouseover=\"mopen('mdashboard')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.global.tool.dashboard.name" /></a>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.activity.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.global.nav.activity.title' href='<%=base+"/activity/view/?module="+Module.PROJECT_SPACE+"&id="+user.getCurrentSpace().getID()%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.blog.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.global.nav.blog.title' href='<%=base+"/blog/view/"+user.getCurrentSpace().getID()+ "/"+user.getID()+"/"+Space.PROJECT_SPACE+ "/"+Module.PROJECT_SPACE+"?module="+Module.PROJECT_SPACE%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.wiki.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.wiki' href='<%=base+"/wiki/"+WikiURLManager.getRootWikiPageNameForSpace()%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>					   

<display:if name="@prm.project.contact.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='#' onmouseover=\"mopen('mdirectorycontact')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.project.nav.directory" /></a>";
	menuString += "	</span>";
	menuString += "		<br/>";
	menuString += "		<div id='mdirectorycontact' onmouseover=\"mcancelclosetime()\" onmouseout='mclosetime()'>";
	menuString += "		<display:get name='@prm.project.nav.team' href='<%=base + "/project/DirectorySetup.jsp?module="+Module.DIRECTORY%>'/>";
	menuString += "				<display:get name='@prm.project.nav.contact' href='<%=base + "/contact/Main.jsp?module="+Module.FORM%>'/>";
	menuString += "		</div>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.directory.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.directory' href='<%=base+"/project/DirectorySetup.jsp?module="+Module.DIRECTORY+"&id="+user.getCurrentSpace().getID()%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.material.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.material' href='<%=base+"/material/Main.jsp?module="+Module.MATERIAL%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.document.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/document/Main.jsp?module="+Module.DOCUMENT%>' onmouseover=\"mopen('mdocument')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.project.nav.document" /></a>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>	

<display:if name="@prm.project.discussion.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.discussion' href='<%=base+ "/discussion/Main.jsp?module="+Module.DISCUSSION%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.form.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/form/Main.jsp?module="+Module.FORM%>' onmouseover=\"mopen('mform')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.project.nav.form" /></a>";
	menuString += "	</span>";
	<%if(!"".equals(formsMenuString)) {%>
	menuString += "		<br/>";
	menuString += "		<div id='mform' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "					<%= forms.getNavBarHTML() %>";
	menuString += "		</div>";
	<%}%>
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.process.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.process' href='<%=base+ "/process/Main.jsp?module="+Module.PROCESS%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.scheduling.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.calendar' href='<%=base+ "/calendar/Main.jsp?module="+Module.CALENDAR%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.scheduling.isenabled">
	menuString += "<li>";
    menuString += " <span>";
    menuString += "  <a href='<%=base+"/workplan/TaskView?id="+user.getCurrentSpace().getID()%>' onmouseover='mopen(\"msc\")' onmouseout='mclosetime()'><propertyProvider:propertyProvider value='@prm.project.nav.schedule' /></a>";
    menuString += " </span>";
	menuString += "</li>";
</display:if>
	
<display:if name="@prm.project.workflow.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.workflow' href='<%=base+ "/workflow/Main.jsp?module="+Module.WORKFLOW%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.project.setup.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "	<display:get name='@prm.project.nav.setup' href='<%=base+ "/project/Setup.jsp?module="+Module.PROJECT_SPACE%>'/>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

	menuString += "</ul>";
	menuString += "</div> </td> </tr> </table> ";
<% } %>
	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subprojects';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subprojects');
	navBar.innerHTML = menuString ;
}
