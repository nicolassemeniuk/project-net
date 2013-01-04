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
|     $Author: umesha $
|
|  Processes license list with user selected filters
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License List View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
			net.project.xml.XMLFormatter"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseCollection" class="net.project.license.LicenseCollection" scope="session" />


<security:verifyAccess module="<%=net.project.base.Module.APPLICATION_SPACE%>" 
                       action="view" /> 
   	
	<%  if(request.getParameter("theAction").equals("submit")) {
			String licenseType = request.getParameter("licenseType");
			String licenseStatus = request.getParameter("licenseStatus");
			String searchLicenseKey = request.getParameter("searchLicenseKey");
		
			session.setAttribute("licenseType", licenseType);
			session.setAttribute("licenseStatus", licenseStatus);
			session.setAttribute("searchLicenseKey", searchLicenseKey);
		
			String userName = request.getParameter("userName");
			String folName = request.getParameter("folName");
			String emailID = request.getParameter("emailID");
		
			session.setAttribute("userName", userName);
			session.setAttribute("folName", folName);
			session.setAttribute("emailID", emailID);
		
	        session.setAttribute("displayLicenses", "true" );

            licenseCollection.clearFilters();
			if(request.getParameter("searchOption").equals("licenseFilter")) {
				licenseCollection.setLicenseFilters(licenseType, licenseStatus, searchLicenseKey);
			} else if(request.getParameter("searchOption").equals("userFilter")) {
				licenseCollection.setUserFilters(userName, folName, emailID);
			} else {
				licenseCollection.setLicenseFilters(licenseType, licenseStatus, searchLicenseKey);
				licenseCollection.setUserFilters(userName, folName, emailID);
			}
			
		} else if(request.getParameter("theAction").equals("sort")) {	
			
			licenseCollection.setSortField(request.getParameter("sortField"));
			licenseCollection.setSortOrder(request.getParameter("sortOrder"));
		}
	%>

        <jsp:forward page="/admin/license/LicenseListView.jsp">
			<jsp:param name="module" value='<%=Module.APPLICATION_SPACE%>'/>
			<jsp:param name="action" value='<%=Action.VIEW%>'/>
		</jsp:forward> 




