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
    info="include page for business space portfolio channel"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
		net.project.portfolio.ProjectPortfolioBean,
		net.project.business.BusinessSpaceBean,
		net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectPortfolio" class="net.project.portfolio.ProjectPortfolioBean" scope="request" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />

<%
	projectPortfolio.clear();
	projectPortfolio.setID(businessSpace.getProjectPortfolioID("owner"));
	projectPortfolio.setProjectMembersOnly(true);
	projectPortfolio.load();
	if(projectPortfolio.size() > 0 ){
%>
<pnet-xml:transform name="projectPortfolio" scope="request" stylesheet="/portfolio/xsl/project-portfolio.xsl" />
<% }else{ %>
<a class="tableContent" style="padding-left:3px;" href="<%=SessionManager.getJSPRootURL() %>/project/ProjectCreate.jsp?module=170&portfolio=true&parent=<%=businessSpace.getID()%>">Create a Project</a> 
<%
}	
%>