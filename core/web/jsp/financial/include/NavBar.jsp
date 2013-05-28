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
|   $Revision: 20052 $
|       $Date: 2009-10-02 13:37:27 -0300 (vie, 02 oct 2009) $
|     $Author: ritesh $
|  
| Includable left side navbar
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/javascript; charset=UTF-8"
    info="Financial Space Navigation Bar"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.ObjectType,
            net.project.base.property.PropertyProvider,
            net.project.configuration.ConfigurationPortfolio,
           	net.project.security.Action,net.project.security.User,
            net.project.security.SessionManager,
			net.project.search.SearchManager,
            net.project.search.IObjectSearch,
			net.project.space.Space,
			net.project.space.PersonalSpace,
            net.project.license.system.LicenseProperties,
            net.project.license.system.MasterProperties,
			net.project.gui.navbar.FeaturedFormItems,
			java.util.Date,
			net.project.calendar.PnCalendar"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="configurationPortfolio" class="net.project.configuration.ConfigurationPortfolio" scope="page" />
<%
	String base = SessionManager.getJSPRootURL();
    //Determine whether or not the configuration space will be displayed.
    //(The user has to have at least one configuration)
    configurationPortfolio.setID(user.getMembershipPortfolioID());
    String displayConfigurationSpace = String.valueOf(configurationPortfolio.hasConfiguration());

	FeaturedFormItems forms = new FeaturedFormItems(user.getCurrentSpace().getID(), base+"/form/FormQuickAccess.jsp?module="+Module.FORM);
	String formsMenuString = forms.getNavBarHTML();
    //We decided whether or not to load the side bar menu for one situation in the personal
    //space.  If someone has gone to another space and hit the back button, the items in the
    //left nav menu would point to the wrong place.  In that situation, we will not load the
    //left nav bar.  It can be reloaded whenever we enter the personal space normally.
    boolean bLoadMenu = true; //(user.getCurrentSpace().getType().equals(Space.PERSONAL_SPACE));
    String loadMenu = new Boolean(bLoadMenu).toString();
       
    if(bLoadMenu){
%>

function writeSpaceNavBarMenu() {
	var menuString = "";
	menuString += "<table class='menu-subfinancial'> <tr valign='top' height='19'> <td> <div> ";
	menuString += "<ul class='dropdown'> ";

<display:if name="@prm.financial.dashboard.isenabled">
	menuString += "<li>";
	menuString += "	<span><display:get name='@prm.global.tool.dashboard.name' href='<%=base + "/financial/Dashboard?id=" + user.getCurrentSpace().getID() %>'/></span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.financial.document.isenabled">
	menuString += "<li>";
	menuString += "	<span>";
	menuString += "		<a href='<%=base+"/document/Main.jsp?module="+Module.DOCUMENT%>' onmouseover=\"mopen('mdocument')\" onmouseout='mclosetime()'><propertyProvider:propertyProvider value="@prm.financial.nav.document" /></a>";
	menuString += "	</span>";
	menuString += "</li>";
</display:if>

<display:if name="@prm.financial.setup.isenabled">
	menuString += " <li>";
	menuString += " 	<span>";
	menuString += " 	<display:get name='@prm.financial.nav.setup' href='<%=base + "/financial/Main.jsp?id="+"&page="+base+"/financial/Setup.jsp?module="+Module.FINANCIAL_SPACE%>'/>";
	menuString += " 	</span>";
	menuString += " </li>";
</display:if>

	menuString += "</ul>";
	menuString += "</div>  </td></tr> </table>";
<% } %>


	var spaceNavBarMenuDiv = document.createElement('div');
	spaceNavBarMenuDiv.id = 'subfinancial';
	document.getElementsByTagName('body')[0].appendChild(spaceNavBarMenuDiv);		

	var navBar = document.getElementById('subfinancial');
	navBar.innerHTML = menuString ;
}
