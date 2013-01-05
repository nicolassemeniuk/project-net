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
    info="InviteUser1Processing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.security.SessionManager,
			net.project.resource.InvitationException,
            net.project.security.Action" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="invitationManager" type="net.project.resource.LicenseInvitationManager" scope="session" /> 
<jsp:useBean id="license" type="net.project.license.License" scope="session" /> 

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" />


<%
    String theAction = request.getParameter("theAction");
	
	invitationManager.setLicense(license);
	
	if (theAction != null && theAction.equals("cancel")) {
		session.removeValue("invitationManager");
		session.removeValue ("errorMsg");
		session.removeValue ("errorMsgDirectory");
		
		response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW );
    } else if (theAction != null && theAction.equals("search")) {
        // Search link
        pageContext.forward("/admin/license/MemberAddDirectorySearchController.jsp");

    } else if (theAction != null && theAction.equals("add")) {
        // Next or Add button
%>
        <jsp:useBean id="lastInvitee" class="net.project.resource.Invitee" scope="session" />
        <jsp:setProperty name="lastInvitee" property="firstName" param="inviteeFirstName" />
        <jsp:setProperty name="lastInvitee" property="lastName" param="inviteeLastName" />
        <jsp:setProperty name="lastInvitee" property="email" param="inviteeEmail" />
<%

        if (lastInvitee.getEmail() == null || lastInvitee.getEmail().trim().length() == 0) {
            // We need an email address
            String errorMessage = PropertyProvider.get("prm.license.inviteusuer1.error.emailrequired.message");
    		session.putValue("errorMsg", errorMessage);
			response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);

        } else {
            // We have an email address
            net.project.resource.LicenseInviteeValidator validator = new net.project.resource.LicenseInviteeValidator(license, lastInvitee);

			// Check to make sure the entered email address is valid
			if (!validator.isValidEmail()) {
			    // Invalid email address
                // Go back to previous page with error message
				String errorMessage = PropertyProvider.get("prm.license.inviteusuer1.error.validemail.message");
				session.putValue("errorMsg", errorMessage);
				response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);

		    } else if (validator.isAlreadyInvited()) {
                // User already invited
                // Go back to previous page with error message
				String errorMessage = PropertyProvider.get("prm.license.inviteusuer1.error.alreadyinvited.message", new Object[] {lastInvitee.getFirstName(), validator.getInviteeStatus()});
/*DEL			String errorMessage = lastInvitee.getFirstName() + " is already invited to this project.<br> Their current status is: " + validator.getInviteeStatus();*/
				session.putValue("errorMsg", errorMessage);
				response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);

		    } else {
                // All successful

                // Add the validated invitee to the license invitation manager
				try {
                	invitationManager.addMember(lastInvitee);
				} catch (InvitationException ie) {
					String errorMessage = PropertyProvider.get("prm.license.inviteusuer1.error.maxusage.message");
					session.putValue("errorMsg", errorMessage);
				}		
    			// Go back to add another
    			response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&theAction=add");
    		}
		}
    
    } else if (theAction != null && theAction.equals("next")) {
        // Go to next page
    	response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/InviteUser2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);
       // Not sure if we wantthis in license
    } else if (theAction != null && theAction.equals("removeInvitee")) {
		String returnPage = "/admin/license/InviteUser1.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&theAction=add";
		String inviteeEmail = request.getParameter("inviteeID");
		invitationManager.removeInvitee(inviteeEmail);
		response.sendRedirect (SessionManager.getJSPRootURL() + returnPage);
		
    } else {
        throw new net.project.base.PnetException("Missing or unhandled action '" + theAction + "' in InviteUser1Processing.jsp");
	}
%>
