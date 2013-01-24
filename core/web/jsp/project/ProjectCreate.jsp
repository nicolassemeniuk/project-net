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
    info="Project Create Start. Omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.space.Space,
			net.project.space.SpaceURLFactory,
    		net.project.security.User,
			net.project.security.SessionManager,
			net.project.space.SpaceFactory,
			net.project.space.ISpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />     
<jsp:useBean id="projectWizard" class="net.project.project.ProjectWizard" scope="session" />

<%
    // Security check, depends on which space is current
	String mySpace = user.getCurrentSpace().getType();
	int module = -1;
	if (mySpace.equals(Space.PERSONAL_SPACE)) module = net.project.base.Module.PERSONAL_SPACE;
	if (mySpace.equals(Space.BUSINESS_SPACE)) module = net.project.base.Module.BUSINESS_SPACE;
	if (mySpace.equals(Space.PROJECT_SPACE)) module = net.project.base.Module.PROJECT_SPACE;
	String verifyAction = null;
	int action = securityProvider.getCheckedActionID();
	if (action == net.project.security.Action.VIEW) verifyAction="view";
	if (action == net.project.security.Action.CREATE) verifyAction="create";
%>
<security:verifyAccess action="<%=verifyAction%>"
					   module="<%=module%>" /> 

<%
    // Setup project wizard bean
	projectWizard.clear();
	projectWizard.setUser(user);
    	
	String parentSpaceID = request.getParameter("parent");
	String parentBusinessID = request.getParameter("business");
	
	// Assign the default link
	//String refLink = SessionManager.getJSPRootURL()+"/portfolio/PersonalPortfolio.jsp?module="+net.project.base.Module.PERSONAL_SPACE+"&portfolio=true";
	String refLink = SessionManager.getJSPRootURL()+"/portfolio/Project?module="+net.project.base.Module.PERSONAL_SPACE+"&portfolio=true";
	
	Space parentSpace = null ;
	
	if( parentSpaceID != null && !parentSpaceID.trim().equals("")) {
		refLink = SpaceURLFactory.constructSpaceURLForMainPage(parentSpaceID) ;
		parentSpace = SpaceFactory.constructSpaceFromID(parentSpaceID);
	
       if (parentSpace.getSpaceType().getID().equals(ISpaceTypes.BUSINESS_SPACE)) {
			refLink =  SessionManager.getJSPRootURL()+"/portfolio/BusinessPortfolio.jsp?module="+net.project.base.Module.BUSINESS_SPACE;
            projectWizard.setParentBusinessID(parentSpace.getID());
    	} else if (parentSpace.getSpaceType().getID().equals(ISpaceTypes.PROJECT_SPACE)) {
			refLink = SessionManager.getJSPRootURL()+"/project/subproject/Main.jsp?module="+net.project.base.Module.PROJECT_SPACE;
    		projectWizard.setParentProjectID(parentSpace.getID());
        }	
	}
	
	Space parentBusinessSpace = null;
	if(parentBusinessID != null && !"null".equals(parentBusinessID) && !parentBusinessID.trim().equals("")){
		parentBusinessSpace = SpaceFactory.constructSpaceFromID(parentBusinessID);
		if(parentBusinessSpace.isTypeOf(ISpaceTypes.BUSINESS_SPACE)){
			projectWizard.setParentBusinessID(parentBusinessSpace.getID());
		}
	}

	pageContext.setAttribute("pnet_refLink", refLink , pageContext.SESSION_SCOPE);
	
%>

<%
    // Skip past terms of use page if not enabled
    if (!projectWizard.isTermsOfUseEnabled()) {
        pageContext.forward("/project/NewProjectWizard0Processing.jsp");
    } else {
        pageContext.forward("/project/NewProjectWizard0.jsp");
    }
%>