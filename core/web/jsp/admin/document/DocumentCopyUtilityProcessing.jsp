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
|   $RCSfile$
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document Copy Utility Processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
		    net.project.security.User,
		    net.project.security.SessionManager,
		    net.project.base.property.PropertyProvider,
            net.project.security.AuthorizationFailedException"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
    if (!user.isApplicationAdministrator()) { 
        throw new AuthorizationFailedException(PropertyProvider.get("prm.project.document.failed.security.violation.label"));
    }
%> 
 
<%
     String fromContainerID = request.getParameter("fromContainerID");
     String toContainerID = request.getParameter("toContainerID");

     DocumentSpace docSpace = DocumentManager.getDocSpaceForContainerObject (fromContainerID);

     docSpace.copyContents (fromContainerID, toContainerID, user);

     response.sendRedirect ("DocumentCopyUtility.jsp");
%>
    
