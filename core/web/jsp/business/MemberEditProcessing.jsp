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
    info="MemberEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.Person,
    	net.project.resource.RosterBean,
    	net.project.base.Module, 
    	net.project.security.User,
		net.project.base.property.PropertyProvider,
    	net.project.security.SecurityProvider,
    	net.project.security.Action" 
%>

<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="person" class="net.project.resource.Person" scope="request" />

<%
// Make sure a security check has been passed to view a discussion group
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String memberid = request.getParameter("memberid");
String id = securityProvider.getCheckedObjectID();

if ((id.length() > 0) || 
    (module != net.project.base.Module.DIRECTORY) 
    || (action != net.project.security.Action.MODIFY)
    || (memberid == null)) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.business.memberedit.authorizationfailed.message"));

// We can not use standard security here because person id spans across all spaces.
// Therefore we will only grant edit permission on a member if it is that member or a space
// admin
if ((!user.getID().equals(memberid)) &&
    (!securityProvider.isUserSpaceAdministrator()))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.business.memberedit.authorizationfailed.message"));

person = roster.getPerson(memberid);
%>

<jsp:setProperty name="person" property="responsibilities" param="responsibilities" />
<jsp:setProperty name="person" property="title" param="title" />

<%
	roster.storePersonRosterProperties(person);
	request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
    request.setAttribute("action", Integer.toString(Action.VIEW));
	pageContext.forward("Directory.jsp");
%> 