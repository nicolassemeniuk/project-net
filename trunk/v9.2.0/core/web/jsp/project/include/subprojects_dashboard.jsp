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
|   $Revision: 17051 $
|       $Date: 2008-03-13 07:47:52 +0100 (jue, 13 mar 2008) $
|     $Author: anarancio $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="include page for a list of subprojects"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.PortfolioManager,
            net.project.portfolio.ProjectPortfolio,
            net.project.project.ProjectSpaceBean,
            net.project.security.User,
            net.project.space.SpaceList,
            net.project.space.SpaceManager,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>

<%@ include file="/base/taglibInclude.jsp"%>

<%@page import="net.project.base.Module"%>
<jsp:useBean id="projectPortfolio" class="net.project.portfolio.ProjectPortfolio" scope="page"/>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%-- Load the information for the ProjectPortfolio --%>
<%
    SpaceList spaceList = SpaceManager.getSubprojects(projectSpace);
    projectPortfolio = (ProjectPortfolio)PortfolioManager.makePortfolioFromSpaceList(spaceList);
    projectPortfolio.setUser(user);
    projectPortfolio.load();

    pageContext.removeAttribute("projectPortfolio");
    pageContext.setAttribute("projectPortfolio", projectPortfolio, PageContext.PAGE_SCOPE);
    
    if(!projectPortfolio.isEmpty()) {
%>

<pnet-xml:transform name="projectPortfolio" scope="page" stylesheet="/project/subproject/xsl/subproject-dashboard.xsl">
    <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
</pnet-xml:transform>
<%
    } else {
%>
	<a href="<%=SessionManager.getJSPRootURL() + "/project/subproject/Main.jsp?module="+Module.PROJECT_SPACE%>"><%=PropertyProvider.get("prm.project.subproject.create.link")%></a>
<%
    }
%>
