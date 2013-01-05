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
    info="ListEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, net.project.form.*, net.project.util.Conversion, net.project.space.PersonalSpaceBean,
            net.project.util.Validator"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>

<%
	// Toolbar: Submit button
	if (request.getParameter("theAction").equals("submit"))
	{
	
		// If a team member is defining this list
		if ((request.getParameter("role") != null) && request.getParameter("role").equals("user"))
		{
			formListDesigner.setOwnerSpace(personalSpace);
			formListDesigner.setIsAdmin(false);
			boolean isShared = Conversion.toBoolean(request.getParameter("isShared"));
			formListDesigner.setIsShared(isShared);
		}
		else
		{
			// The Space Administrator is defining this list
			formListDesigner.setOwnerSpace(user.getCurrentSpace());
			formListDesigner.setIsAdmin(true);
			formListDesigner.setIsShared(true);
		}

        formListDesigner.verifyHttpPost(request, errorReporter);

        if (!errorReporter.errorsFound()) {
            formListDesigner.processHttpPost(request);

            if ( request.getParameter("sortFieldID0") != null && !request.getParameter("sortFieldID0").trim().equals("")){
                boolean isAscending = request.getParameter("sortAscending0").equals("1") ? true :false ;
                formListDesigner.setSortField(request.getParameter("sortFieldID0"),0,  isAscending);
            }
            if ( request.getParameter("sortFieldID1") != null && !request.getParameter("sortFieldID1").trim().equals("")){
                boolean isAscending = request.getParameter("sortAscending1").equals("1") ? true :false ;
                formListDesigner.setSortField(request.getParameter("sortFieldID1"),1,  isAscending);
            }
            if ( request.getParameter("sortFieldID2") != null && !request.getParameter("sortFieldID2").trim().equals("")){
                boolean isAscending = request.getParameter("sortAscending2").equals("1") ? true :false ;
                formListDesigner.setSortField(request.getParameter("sortFieldID2"),2,  isAscending);
            }

            formListDesigner.store();
            formListDesigner.addToSpace(formDesigner.getSpace());

            // reload the form, so we will see the new list.
            formDesigner.setSpace(user.getCurrentSpace());
            formDesigner.load();
            pageContext.forward("ListsManager.jsp");
        } else {
            if (!Validator.isBlankOrNull(formListDesigner.getID())) {
                request.setAttribute("listID", formListDesigner.getID());
            }
            
            errorReporter.populateFromRequest(request);
            pageContext.forward("ListEdit.jsp");
        }
	}
%>
