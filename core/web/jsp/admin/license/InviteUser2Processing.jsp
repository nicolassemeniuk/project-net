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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="InviteUser2Processing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.resource.SpaceInvitationManager, 
            net.project.base.Module,
            net.project.security.SessionManager,
            net.project.security.Action" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="invitationManager" type="net.project.resource.LicenseInvitationManager" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<jsp:useBean id="license" class="net.project.license.License" scope="session" />

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
        <jsp:setProperty name="invitationManager" property="inviteeMessage" />

<%
        // Process the invitation
	    try {
            session.removeValue ("errorMsg");
			session.removeValue ("errorMsgDirectory");
			
			invitationManager.commit();

        } catch (net.project.notification.NotificationException e) {
            // Successfully invited most, but problem sending Notification
            errorMessage = PropertyProvider.get("prm.license.inviteuser2.error.emailnotsent.message");
            isError = true;
	 
        }
	
        if (isError) {
            // Go to the error page
            response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberSendMailErrors.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&errorMessage=" + java.net.URLEncoder.encode(errorMessage));

        } else {
            if (theAction.equals("finish")) {
                // Done; refresh directory page
               // response.sendRedirect(directoryPage);
			   session.removeValue("invitationManager");
			   
			   response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.VIEW);
			   
            } else {
                // Add another; go to first page
                response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/InviteUser1.jsp?reload=main&module=" + Module.DIRECTORY + "&action=" + Action.CREATE); 
            }
        }

	} else if (theAction.equals("back")) {
		// Back button clicked
	    response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/InviteUser2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);

    } else if (theAction != null && theAction.equals("removeInvitee")) {
        String returnPage = java.net.URLEncoder.encode("/roster/MemberAdd2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);
        pageContext.forward("/roster/MemberAddInviteeRemove.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&returnPage=" + returnPage);

    }
%>
