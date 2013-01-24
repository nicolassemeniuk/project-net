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
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
	info="Initiate Migration Processing -- Step 1 Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,net.project.configuration.ConfigurationSpace,
			net.project.security.domain.DomainMigration,java.util.Iterator,
			net.project.security.SessionManager"
%>

<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>
<jsp:useBean id="domainMigrationManager" class="net.project.security.domain.DomainMigrationManager" scope="request"/>
<jsp:useBean id="csManager" class="net.project.configuration.ConfigurationSpaceManager" scope="request"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userDomain" class="net.project.security.domain.UserDomain" scope="session"/> 
<jsp:setProperty name="domainMigration" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("migrationMessage") == null) || (request.getParameter("migrationMessage").equals(""))) {
		domainMigration.setMigrationMessage("");
	}
%>


<%
	userDomain.setID(domainMigration.getTargetDomainID());

    pageContext.forward("InitiateMigration2.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&action="+net.project.security.Action.MODIFY);
%>
                
