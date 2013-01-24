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
    import="net.project.security.SecurityProvider,
			net.project.security.Action,
            net.project.security.SessionManager,
			net.project.base.Module,
			net.project.base.property.PropertyProvider,
            net.project.base.directory.search.IDirectoryContext,
            net.project.base.directory.search.ISearchableDirectory,
            net.project.base.directory.search.ISearchResults,
            net.project.base.directory.search.SearchControls,
            net.project.base.directory.search.SearchFilter" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" /> 


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
%>
        <jsp:setProperty name="spaceInvitationWizard" property="searchName" />
        <jsp:setProperty name="spaceInvitationWizard" property="directoryID" />
<%
        // Grab the selected directory for searching (based on directoryID)
        ISearchableDirectory searchDirectory = spaceInvitationWizard.getSearchableDirectory();
        IDirectoryContext directoryContext = searchDirectory.getDirectoryContext();

        // Construct search filter
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.add("name", spaceInvitationWizard.getSearchName());

        // Build search control
        SearchControls searchControls = new SearchControls();

        // Perform search
        ISearchResults searchResults = directoryContext.search(searchFilter, searchControls);

        // Add search results to session
        pageContext.removeAttribute("searchResults");
        pageContext.setAttribute("searchResults", searchResults, PageContext.SESSION_SCOPE);

        // Now forward to display page, ensuring page is reset to 0
        pageContext.forward("/roster/MemberAddDirectorySearchResults.jsp?page_start=0");

    } else if (theAction != null && (theAction.equals("next") || theAction.equals("add"))) {
%>
        <jsp:useBean id="searchResults" type="net.project.base.directory.search.ISearchResults" scope="session" />
<%
        // Grab the checked resultIDs
        String[] resultIDs = request.getParameterValues("resultID");
        if (resultIDs == null && spaceInvitationWizard.getInviteeList().isEmpty()) {
            // Problem.  Client side validation failed to require user to
            // select any checkboxes
			
            String errorMessage = PropertyProvider.get("prm.directory.memberadddirectorysearchresults.personnotselected.message");
            request.setAttribute("errorMsg", errorMessage);
		    pageContext.forward("/roster/MemberAddDirectorySearchResults.jsp");

        } else {
            // Either we got resultIDs or we already have at least one invitee
            boolean isAllValid = false;
            
            if (resultIDs != null) {
                // Grab invitees constructed from selected search result IDs
                java.util.Collection newInvitees = net.project.resource.Invitee.makeInvitees(searchResults.getForIDs(resultIDs));
                // Validate
                net.project.resource.SpaceInviteeValidator validator = new net.project.resource.SpaceInviteeValidator(user.getCurrentSpace(), newInvitees);
                request.setAttribute("validator", validator);

                // Check to make sure the entered email address is valid
	    		if (!validator.isValidEmail()) {
		    	    // Invalid email address for one or more invitees
                    // Go back to previous page with error message
					
				    String errorMessage = PropertyProvider.get("prm.directory.memberadddirectorysearchresults.invalidemail.message");
				    request.setAttribute("errorMsg", errorMessage);
				    pageContext.forward("/roster/MemberAddDirectorySearchResults.jsp");

    		    } else if (validator.isAlreadyInvited()) {
                    // One or more Users already invited
                    // Go back to previous page with error message
					
    				String errorMessage = PropertyProvider.get("prm.directory.memberadddirectorysearchresults.alreadyinvited.message");
    				request.setAttribute("errorMsg", errorMessage);
    			    pageContext.forward("/roster/MemberAddDirectorySearchResults.jsp");

                } else {
                    // All successful
                    // Now add all invitees to the wizard
                    spaceInvitationWizard.addAllMembers(newInvitees);
                    isAllValid = true;
                }

		    } else {
                // Its ok to not have any results if we already have one invitee
                isAllValid = true;
            }
            

            if (isAllValid) {
                if (theAction.equals("next")) {
        			// Next page
                    response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberAdd2.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE);
        
                } else {
                    // Go back to add more
                    String currentPageStart = (String) request.getParameter("page_start");
        			response.sendRedirect(SessionManager.getJSPRootURL() + "/roster/MemberAddDirectorySearchResults.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&page_start=" + currentPageStart);
                }
            }

        }

    } else if (theAction != null && theAction.equals("removeInvitee")) {
        String returnPage = "/roster/MemberAddDirectorySearchResults.jsp?module="+Module.DIRECTORY+"&action=" + Action.CREATE;
		String inviteeEmail = request.getParameter("inviteeID");
		spaceInvitationWizard.removeInvitee(inviteeEmail);
		response.sendRedirect (SessionManager.getJSPRootURL() + returnPage);
		
        //pageContext.forward("/roster/MemberAddInviteeRemove.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE + "&returnPage=" + returnPage);

    } else {
        throw new net.project.base.PnetException("Missing or invalid action '" + theAction + "' in MemberAddDirectorySearchController.jsp");
    }
%>
