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
    info="MemberAdd1Processing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.SpaceInvitationManager, 
            net.project.base.Module,
            net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
            net.project.security.Action" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 


<%
    String theAction = request.getParameter("theAction");
    String nextPage = null;
    String directoryPage = pageContextManager.getProperty ("directory.url.complete");
	boolean isError = false;
	String errorMessage = null;

    if (theAction != null && (theAction.equals("finish") || theAction.equals("another"))) {
%>
        <jsp:setProperty name="spaceInvitationWizard" property="inviteeResponsibilities" />
        <jsp:setProperty name="spaceInvitationWizard" property="inviteeMessage" />
        <jsp:setProperty name="spaceInvitationWizard" property="autoAcceptInvite" />
        <jsp:setProperty name="spaceInvitationWizard" property="assignedRoles" />
<%
        // Process the invitation
	    try {
            String sendInvitations = request.getParameter("sendNotifications");
            spaceInvitationWizard.setSendNotifications(sendInvitations != null && sendInvitations.equals("true"));

            session.removeValue ("erroMsg");
			spaceInvitationWizard.commit();

        } catch (net.project.resource.SpaceInvitationException e) {
            // A bad problem occurred inviting
            // Invitation of some users may have been achieved
            errorMessage = PropertyProvider.get("prm.directory.invite.memberinformation.invitationserror.message");
            isError = true;

        } catch (net.project.notification.NotificationException e) {
            // Successfully invited most, but problem sending Notification
			
			String[] errMsg = { e.toString()}; 
			errorMessage = PropertyProvider.get("prm.directory.invite.memberinformation.emailsendingerror.message",errMsg);
            isError = true;
        }

        if (isError) {
            // Go to the error page
            response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberSendMailErrors.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&errorMessage=" + java.net.URLEncoder.encode(errorMessage));

        } else {
            if (theAction.equals("finish")) {
                // Done; refresh directory page
                response.sendRedirect(directoryPage);
            } else {
                // Add another; go to first page
                response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberAdd1.jsp?reload=main&module=" + Module.DIRECTORY + "&action=" + Action.CREATE); 
            }
        }

	} else if (theAction.equals("back")) {
		// Back button clicked
	    response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberWizardController.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&direction=back");

    } else if (theAction != null && theAction.equals("removeInvitee")) {
		
        String returnPage = "/roster/MemberAdd2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE;
		String inviteeEmail = request.getParameter("inviteeID");
		spaceInvitationWizard.removeInvitee(inviteeEmail);
		response.sendRedirect (SessionManager.getJSPRootURL() + returnPage);
        //pageContext.forward("/roster/MemberAddInviteeRemove.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&returnPage=" + returnPage);

    }
%>
