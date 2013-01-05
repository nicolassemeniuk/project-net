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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
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
            net.project.security.User"
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
	String base = SessionManager.getJSPRootURL();
    // We are loading while viewing a space
    // Only display if there is a current space and it is a business space
    currentID = user.getCurrentSpace().getID();
    bLoadMenu= (currentID != null) && (user.getCurrentSpace().getType().equals(Space.ENTERPRISE_SPACE));

    loadMenu = new Boolean(bLoadMenu).toString();
if (bLoadMenu) {
%>

<div class="menu-subbusiness">
<ul class="dropdown">
	
<display:if name="@prm.business.dashboard.isenabled">
<li>
	<span>
	<display:get name='@prm.enterprise.nav.dashboard' href='<%=base+"/enterprise/Main.jsp"%>'/>
	</span>
</li>
</display:if>

<display:if name="@prm.enterprise.directory.isenabled">
<li>
	<span>
	<display:get name='@prm.enterprise.nav.directory' href='<%=base+"/enterprise/DirectorySetup.jsp?module="+Module.DIRECTORY%>'/>
	</span>
</li>
</display:if>

<display:if name="@prm.enterprise.reports.isenabled">
<li>
	<span>
	<display:get name='@prm.enterprise.nav.reports' href='<%=base+"/report/Main.jsp?module="+Module.REPORT%>'/>
	</span>
</li>
</display:if>

</ul>
<div class="menu-subbusiness-end">&nbsp;</div>
</div>
<% } %>
