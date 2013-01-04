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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Application Space Business Portfolio"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.BusinessPortfolioBean,
            net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.User,net.project.business.BusinessSpace,
            net.project.security.SessionManager,
            net.project.space.Space,net.project.security.*,net.project.business.BusinessDeleteWizard,
            net.project.space.PersonalSpaceBean, net.project.search.SearchManager,
            net.project.document.DocumentManagerBean"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="businessPortfolio" class="net.project.portfolio.BusinessPortfolioBean" scope="request" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="businessDeleteWizard" class="net.project.business.BusinessDeleteWizard" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<html>
<head>
<%
	String spaceType = request.getParameter ("spaceType");
	String status = request.getParameter ("status");
	String mode = request.getParameter("mode") == null ? "initial" : request.getParameter("mode"); 
	String filter = request.getParameter("filter") == null ? "" : request.getParameter("filter");
	
	if(status == null) {
		status = "Active";
	}
	
	if(spaceType == null || spaceType.equals("Business")){
		pageContext.forward("/admin/portfolio/BusinessPortfolio.jsp?module=" + net.project.base.Module.APPLICATION_SPACE+"&action=" + net.project.security.Action.MODIFY + "&status="+status +"&filter="+filter+"&mode="+mode);
	} else {
		pageContext.forward("/admin/portfolio/ProjectPortfolio.jsp?module=" + net.project.base.Module.APPLICATION_SPACE+"&action=" + net.project.security.Action.MODIFY + "&status="+status +"&filter="+filter+"&mode="+mode);
	}

%>

