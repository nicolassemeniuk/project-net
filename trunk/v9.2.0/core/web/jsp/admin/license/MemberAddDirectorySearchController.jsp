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
    info="MemberAddDirectorySearchController.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SecurityProvider,
			net.project.security.Action,
            net.project.security.SessionManager,
			net.project.base.Module,
			net.project.resource.InvitationException,
            net.project.base.directory.search.IDirectoryContext,
            net.project.base.directory.search.ISearchableDirectory,
            net.project.base.directory.search.ISearchResults,
            net.project.base.directory.search.SearchControls,
            net.project.base.directory.search.SearchFilter" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="invitationManager" type="net.project.resource.LicenseInvitationManager" scope="session" /> 
<jsp:useBean id="license" type="net.project.license.License" scope="session" /> 


<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 

<%
    String theAction = request.getParameter("theAction");

    // Do stuff for capturing search results
    // search - new search
    // addSelections - add selected users
    // nextPage / previousPage - next results
    // next - next part of wizard

    if (theAction != null && theAction.equals("search")) {
        // User wants to search directory
		System.out.println("MemberAddDirectorySearchontroller.jsp : directoryID :" + request.getParameter("directoryID"));
%>
        <jsp:setProperty name="invitationManager" property="searchName" />
        <jsp:setProperty name="invitationManager" property="directoryID" />
<%
        // Grab the selected directory for searching (based on directoryID)
        ISearchableDirectory searchDirectory = invitationManager.getSearchableDirectory();
        IDirectoryContext directoryContext = searchDirectory.getDirectoryContext();

        // Construct search filter
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.add("name", invitationManager.getSearchName());

        // Build search control
        SearchControls searchControls = new SearchControls();

        // Perform search
        ISearchResults searchResults = directoryContext.search(searchFilter, searchControls);

        // Add search results to session
        pageContext.removeAttribute("searchResults");
        pageContext.setAttribute("searchResults", searchResults, PageContext.SESSION_SCOPE);

        // Now forward to display page
        pageContext.forward("/admin/license/MemberAddDirectorySearchResults.jsp");

    } else if (theAction != null && (theAction.equals("next") || theAction.equals("add"))) {
%>
        <jsp:useBean id="searchResults" type="net.project.base.directory.search.ISearchResults" scope="session" />
<%
        // Grab the checked resultIDs
        String[] resultIDs = request.getParameterValues("resultID");
        if (resultIDs == null && invitationManager.getInviteeList().isEmpty()) {
            // Problem.  Client side validation failed to require user to
            // select any checkboxes
            String errorMessage = PropertyProvider.get("prm.license.inviteuser.search.error.selectperson.message");
            request.setAttribute("errorMsgDirectory", errorMessage);
		    pageContext.forward("/admin/license/MemberAddDirectorySearchResults.jsp");

        } else {
            // Either we got resultIDs or we already have at least one invitee
            boolean isAllValid = false;
            
            if (resultIDs != null) {
                // Grab invitees constructed from selected search result IDs
                java.util.Collection newInvitees = net.project.resource.Invitee.makeInvitees(searchResults.getForIDs(resultIDs));
                // Validate
                net.project.resource.LicenseInviteeValidator validator = new net.project.resource.LicenseInviteeValidator(license, newInvitees);
                request.setAttribute("validator", validator);

                // Check to make sure the entered email address is valid
	    		if (!validator.isValidEmail()) {
		    	    // Invalid email address for one or more invitees
                    // Go back to previous page with error message
				    String errorMessage = PropertyProvider.get("prm.license.inviteuser.search.error.emailinvalid.message");
				    request.setAttribute("errorMsgDirectory", errorMessage);
				    pageContext.forward("/admin/license/MemberAddDirectorySearchResults.jsp");

    		    } else if (validator.isAlreadyInvited()) {
                    // One or more Users already invited
                    // Go back to previous page with error message
    				String errorMessage = PropertyProvider.get("prm.license.inviteuser.search.error.alreadyinvited.message");
    				request.setAttribute("errorMsgDirectory", errorMessage);
    			    pageContext.forward("/admin/license/MemberAddDirectorySearchResults.jsp");

                } else {
                    // All successful
                    // Now add all invitees to the wizard
					try {
                    	invitationManager.addAllMembers(newInvitees);
					} catch (InvitationException ie) {
						String errorMessage = PropertyProvider.get("prm.license.inviteuser.search.error.maxusage.message");
						session.putValue("errorMsgDirectory", errorMessage);
						pageContext.forward("/admin/license/MemberAddDirectorySearchResults.jsp");
					}		
                    isAllValid = true;
                }

		    } else {
                // Its ok to not have any results if we already have one invitee
                isAllValid = true;
            }
            

            if (isAllValid) {
                if (theAction.equals("next")) {
        			// Next page
                    response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/InviteUser2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);
        
                } else {
                    // Go back to add more
        			response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/license/MemberAddDirectorySearchResults.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);
                }
            }

        }

    } else if (theAction != null && theAction.equals("removeInvitee")) {
        String returnPage = java.net.URLEncoder.encode("/admin/license/MemberAddDirectorySearchResults.jsp");
        pageContext.forward("/admin/license/MemberAddInviteeRemove.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&returnPage=" + returnPage);

    } else {
        throw new net.project.base.PnetException("Missing or invalid action '" + theAction + "' in MemberAddDirectorySearchController.jsp");
    }
%>
