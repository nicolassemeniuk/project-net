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
    info="include page for business space portfolio channel" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.Enumeration,
            net.project.admin.ApplicationSpace,
            net.project.base.Module,java.util.Iterator,
            net.project.business.BusinessDeleteWizard,
            net.project.business.BusinessSpaceBean,
            net.project.portfolio.ProjectPortfolioBean,
            net.project.project.ProjectSpace,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectPortfolio" class="net.project.portfolio.ProjectPortfolioBean" scope="request" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="businessDeleteWizard" class="net.project.business.BusinessDeleteWizard" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
<%
	String project_id="projectID";
	
	businessSpace.clear();
	businessSpace.setID(businessDeleteWizard.getBusinessID());
	businessSpace.load();
	projectPortfolio.clear();
	projectPortfolio.setID(businessSpace.getProjectPortfolioID("owner"));
	projectPortfolio.load();
	businessDeleteWizard.setProjectPortfolio(projectPortfolio);
	
	Enumeration enumeration = request.getParameterNames();
    businessDeleteWizard.invalidate();
        while(enumeration.hasMoreElements()){
            String name=(String) enumeration.nextElement();
            String[] values=request.getParameterValues(name);
            for(int i=0;i<values.length;i++){
                int checkProjectID=name.indexOf(project_id);
                if(checkProjectID >=0){
                    ProjectSpace space = new ProjectSpace();
                    space.setID(values[i]);
                    space.remove();
                    businessDeleteWizard.addProjectForDeletion(space);
                }
            }
        }
	
	
	if(businessDeleteWizard.checkForAllDeleted())
	{
	out.println("<script language=\"javascript\">");
	out.println("opener.location.reload(true);");
	out.println("self.close();");
	out.println("</script>");
	}
	else
	response.sendRedirect("ProjectsMove.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&action="+net.project.security.Action.MODIFY);	
%>
