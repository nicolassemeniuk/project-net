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
|   $Revision: 19619 $
|       $Date: 2009-08-03 14:49:16 -0300 (lun, 03 ago 2009) $
|     $Author: puno $
|
| Includable left side navbar
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/javascript; charset=UTF-8"
    info="Methodology Navigation" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.space.Space,
			net.project.base.Module,
            net.project.base.ObjectType,
			net.project.security.Action,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.gui.navbar.FeaturedFormItems" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%@page import="net.project.base.property.PropertyProvider"%><jsp:useBean id="user" class="net.project.security.User" scope="session" />

	var space = "methodology";   

<%
	// Setup up referring link URL
	String refLink, refLinkEncoded = "";
	
	// check to see if there is a current methodology loaded.
	// If there is a current methodology then load the menu bar
	String currentID = user.getCurrentSpace().getID();
	String base = SessionManager.getJSPRootURL();
	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormList.jsp?module="+Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
	boolean bLoadMenu=false;
	if (currentID != null &&
            user.getCurrentSpace().getType().equals(Space.METHODOLOGY_SPACE)) {
		bLoadMenu=true;
		refLink = "methodology/Main.htm";
		refLinkEncoded = java.net.URLEncoder.encode(refLink);
	}
	String loadMenu = new Boolean(bLoadMenu).toString();
	
	boolean templateUsed = (Boolean)request.getSession().getAttribute("templateUsed");

%>

function writeSpaceNavBarMenu() {
	var menuString = "";
	<%if (bLoadMenu) { %>
	menuString += "<table class='menu-subprojects' width='900'> <tr valign='top' height='19'> <td width='900' nowrap='nowrap'> <div> ";
	menuString += "<ul class='dropdown'>";

<display:if name="@prm.methodology.dashboard.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.dashboard' href='<%=base + "/methodology/Main.htm"%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.dashboard' href='javascript:templateUsed();'/>";
	<%}%>
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.directory.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.directory' href='javascript:directoryIsForbiden();'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.directory' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.document.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.document' href='<%=base+"/document/Main.jsp?module="+Module.DOCUMENT%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.document' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>	

<display:if name="@prm.methodology.discussion.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.discussion' href='<%=base+"/discussion/Main.jsp?module="+Module.DISCUSSION%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.discussion' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>	

<display:if name="@prm.methodology.form.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "<a href='<%=base+"/form/Main.jsp?module="+Module.FORM%>' onmouseover=\"mopen('mform')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.methodology.nav.form" /></a>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.form' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	<%if(!"".equals(formsMenuString)) {%>
	menuString += "		<br/>";
	menuString += "		<div id='mform' onmouseover='mcancelclosetime()' onmouseout='mclosetime()'>";
	menuString += "				<%= forms.getNavBarHTML() %>";
	menuString += "		</div>";
	<%}%>
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.process.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.process' href='<%=base+"/process/Main.jsp?module="+Module.PROCESS%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.process' href='javascript:templateUsed();'/>";
	<%}%>		
	menuString += "	</span>";
	menuString += "</li>";
</display:if>	
	
<display:if name="@prm.methodology.scheduling.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.scheduling' href='<%=base+"/workplan/taskview?module="+Module.SCHEDULE%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.scheduling' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.workflow.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.workflow' href='<%=base+"/workflow/Main.jsp?module="+Module.WORKFLOW%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.workflow' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.security.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.methodology.nav.security' href='<%=base+"/security/SecurityModuleMain.jsp?module=" + Module.SECURITY+"&referer="+refLinkEncoded%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.methodology.nav.security' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.methodology.form.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	<%if (!templateUsed){ %>
		menuString += "	<display:get name='@prm.form.designer.mainpage.title' href='<%=base+"/form/designer/Main.jsp?module=" + Module.FORM+"&action=2"%>'/>";
	<%}else{%>
		menuString += "	<display:get name='@prm.form.designer.mainpage.title' href='javascript:templateUsed();'/>";
	<%}%>	
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

	
	menuString += "</ul>";
	menuString += "</div> </td> <td>&nbsp;</td></tr> </table> ";
<%}%>
	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subprojects';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subprojects');
	navBar.innerHTML = menuString ;
}

function templateUsed() {
	 extAlert('Warning', '<%=PropertyProvider.get("prm.methodology.template.used.message")%>' , Ext.MessageBox.ERROR);
}

function directoryIsForbiden() {
	 extAlert('Warning', '<%=PropertyProvider.get("prm.methodology.template.directory.forbiden.message")%>' , Ext.MessageBox.ERROR);
}
