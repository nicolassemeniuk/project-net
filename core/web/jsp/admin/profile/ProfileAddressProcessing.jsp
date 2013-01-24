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
    info="Registration Page 2 Processing - this page does not emit any output."
    language="java"
	errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.admin.RegistrationBean, 
            net.project.admin.RegistrationException, 
            net.project.security.User"
%>

<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%!
	String errorMessage = "";
	boolean isInfoValid = true;
%>

<%-- Process the Form submit --%>
<jsp:setProperty name="registration" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("ecom_ShipTo_Postal_Street_Line2") == null) || (request.getParameter("ecom_ShipTo_Postal_Street_Line2").equals(""))) {
		registration.setEcom_ShipTo_Postal_Street_Line2("");
	}
	if ((request.getParameter("faxPhone") == null) || (request.getParameter("faxPhone").equals(""))) {
		registration.setFaxPhone("");
	}
	if ((request.getParameter("mobilePhone") == null) || (request.getParameter("mobilePhone").equals(""))) {
		registration.setMobilePhone("");
	}
	if ((request.getParameter("pagerPhone") == null) || (request.getParameter("pagerPhone").equals(""))) {
		registration.setPagerPhone("");
	}
	if ((request.getParameter("pagerEmail") == null) || (request.getParameter("pagerEmail").equals(""))) {
		registration.setPagerEmail("");
	}
%>

<%
// clear old error messages.
session.removeValue("errorMsg");
isInfoValid = true;

// Toolbar: Apply
if (request.getParameter("theAction").equals("apply"))
{
	// update the address
    try{
		registration.updateInvitedUser();
	}catch (RegistrationException e){
        session.putValue("errorMsg",e.getMessage());
	}
    
    // Reload the current user
    user.load();

    // goto requested page (tab)
	pageContext.forward(request.getParameter("nextPage"));
}
else
{
	pageContext.forward("ProfileAddress.jsp");
}
%>
