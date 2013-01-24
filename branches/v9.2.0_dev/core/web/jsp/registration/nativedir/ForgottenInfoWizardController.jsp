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
| Entry point for native forgotten info wizard
| Note: this page expects a request scoped UserDomain object
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Native Forgotten Info Wizard Controller"
    language="java" 
    errorPage="/errors.jsp"
%>

<%-- Grab the UserDomain selected by the user --%>
<jsp:useBean id="userDomain" class="net.project.security.domain.UserDomain" scope="request" />

<%
    // Clear out any old loginPasswordHelper
    session.removeAttribute("loginPasswordHelper");
%>

<%-- Re-create the loginpasswordHelper, populating it with the UserDomain --%>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />
<%
    loginPasswordHelper.setDomainID(userDomain.getID());
%>

<jsp:forward page="/registration/nativedir/ForgottenInfoWizard.jsp" />

