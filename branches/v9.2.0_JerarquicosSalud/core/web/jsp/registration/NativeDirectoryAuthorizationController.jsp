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
|   $Revision: 20348 $
|       $Date: 2010-01-29 11:23:05 -0300 (vie, 29 ene 2010) $
|
| Controller for managing native directory authorization
| during registration
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Registration NativeDirectory; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Redirect to start of process if registration not started
    if (!registration.isStarted()) {
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/Register.jsp");
        return;
    }
	
	request.setCharacterEncoding("UTF-8");
    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("nativeAuthorization")) {
    	fromPage = HTMLUtils.escape(fromPage).replaceAll("'", "&acute;");
        // We were submitted to from the "nativeAuthorization" page
        // We can assume theAction is valid
        
        if (theAction != null && theAction.equals("next")) {
        	theAction = HTMLUtils.escape(theAction).replaceAll("'", "&acute;");
            // User proceeding to next part of registration

%>
            <jsp:useBean id="nativeDirectoryEntry" class="net.project.base.directory.nativedir.NativeRegistrationEditor" scope="request" />
            
            <%
			//Avinash:----------------------------------------------------------------------------            
            nativeDirectoryEntry.setLogin(request.getParameter("login"));
            nativeDirectoryEntry.setClearTextPassword(request.getParameter("password"));
            nativeDirectoryEntry.setEmail(request.getParameter("ecom_ShipTo_Online_Email"));
            nativeDirectoryEntry.setClearTextHintPhrase(request.getParameter("clearTextHintPhrase"));
            nativeDirectoryEntry.setClearTextHintAnswer(request.getParameter("clearTextHintAnswer"));
            nativeDirectoryEntry.setPasswordRetype(request.getParameter("password_2"));
            nativeDirectoryEntry.setEmailRetype(request.getParameter("ecom_ShipTo_Online_Email2"));
            %>
<%--   
		     <jsp:setProperty name="nativeDirectoryEntry" property="login" />
            <jsp:setProperty name="nativeDirectoryEntry" property="clearTextPassword" param="password" />
            <jsp:setProperty name="nativeDirectoryEntry" property="email" param="ecom_ShipTo_Online_Email" />
            <jsp:setProperty name="nativeDirectoryEntry" property="clearTextHintPhrase" />
            <jsp:setProperty name="nativeDirectoryEntry" property="clearTextHintAnswer" />
            <jsp:setProperty name="nativeDirectoryEntry" property="passwordRetype" param="password_2" />
            <jsp:setProperty name="nativeDirectoryEntry" property="emailRetype" param="ecom_ShipTo_Online_Email2" />
   Avinash: --%>        
<%
            nativeDirectoryEntry.validate();
            if (nativeDirectoryEntry.hasErrors()) {
                // There was a problem validating
                pageContext.forward("/registration/NativeDirectoryAuthorization.jsp");

            } else {
                // Everything is OK
                // Proceed to next part of registration
                // This is handled by the main DirectoryAuthorizationController page
                // We redirect to that page in order to reset any request parameters received here
                registration.setDirectoryEntry(nativeDirectoryEntry);
                registration.setEmail(nativeDirectoryEntry.getEmail());
                registration.setLogin(nativeDirectoryEntry.getLogin());
                response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/DirectoryAuthorizationController.jsp?fromPage=authorizationPage&theAction=" + theAction);
            }

        } else {
            // All other actions are handled by main page
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/DirectoryAuthorizationController.jsp?fromPage=authorizationPage&theAction=" + theAction);
        }

    } else {
        // Arriving from main DirectoryAuthorizationController.jsp

        if (registration.getDirectoryEntry() instanceof net.project.base.directory.nativedir.NativeDirectoryEntry) {
            // If we already have a NativeDirectoryEntry (already visited this step)
            // then re-use it
            pageContext.setAttribute("nativeDirectoryEntry", registration.getDirectoryEntry(), PageContext.REQUEST_SCOPE);
        } else {
            // Place new instance in request
            pageContext.setAttribute("nativeDirectoryEntry", new net.project.base.directory.nativedir.NativeRegistrationEditor(), PageContext.REQUEST_SCOPE);
        }

        pageContext.forward("/registration/NativeDirectoryAuthorization.jsp");
    }
%>
