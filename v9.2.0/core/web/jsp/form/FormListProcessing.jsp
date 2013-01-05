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
    info="FormListProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*, 
			net.project.security.*,
            net.project.util.Validator,
            net.project.security.SessionManager,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />

<%
	// Load lists if for some reason form has not lists loaded
	// This is often caused by using the <Back button to return from viewing
	// an instance of this form
	if (!form.hasLists()) {
		form.loadLists(false);
	}

    String displayListID = request.getParameter("displayListID");
    if (Validator.isBlankOrNull(displayListID)) {
        if (form.getDisplayListId() == null) {
            displayListID = form.getDisplayListId();
        } else {
            displayListID = form.getDefaultList().getID();
        }
    }%>

<jsp:setProperty name="form" property="displayListID" param="displayListID" />
<%
    if (request.getParameter("theAction").equals("submit"))	{
		// User set current list as his default list.
		// Checkbox checked and selected list == current list being displayed.
		//if ((request.getParameter("default") != null) && 
		//	request.getParameter("default").equals("Y") && 
		//	request.getParameter("displayedList").equals(request.getParameter("displayListID"))) 
		//{
		//	form.setDefaultListID(request.getParameter("displayListID"));
		//}

		form.setDisplayListID(displayListID);
		//if (request.getParameter("referer") != null)
		//	pageContext.forward(request.getParameter("referer") + "&load=false");
		//else
		pageContext.forward("FormList.jsp?load=false");
	} else if (request.getParameter("theAction").equals("sort")) {
		FormList list = form.getDisplayList();
		list.clearSort();
		list.setSortField(request.getParameter("sortBy"), 0);
		form.setDisplayListID(displayListID);
		pageContext.forward("FormList.jsp?load=false");

	} else if (request.getParameter("theAction").equals("modify")) {
		// Edit form data, indicating that we are to use the form object
		// in session.  This preserves any list selections etc.
		pageContext.forward("FormEdit.jsp?useSessionForm=true");
	} else if (request.getParameter("theAction").equals("remove")) 	{
		// Load the data for the specified id, loading from current form context
        form.loadData(request.getParameter("id"));
        // Get that data and remove it
        form.getData().remove();

        response.sendRedirect(SessionManager.getJSPRootURL() + "/form/FormList.jsp?module=" + Module.FORM);		
	} else if (request.getParameter("theAction").equals("security")) {
		// Check security now
%>
		<security:verifyAccess objectID='<%=request.getParameter("id")%>'
							   action="modify_permissions"
					   		   module="<%=net.project.base.Module.FORM%>"
		/> 
<%
		// Security JSP requires these attributes		
		request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
		request.setAttribute("action", Integer.toString(net.project.security.Action.MODIFY_PERMISSIONS));
		// Security JSP requires this session value
		session.putValue("objectID", request.getParameter("id"));
		
		ServletSecurityProvider.setAndCheckValues(request);
		// Security JSP requires this request parameter
		pageContext.forward("/security/SecurityMain.jsp?objectType=form_data");
	} else if ((request.getParameter("theAction") != null) && request.getParameter("theAction").equals("workflow"))
	{
		pageContext.forward("/workflow/envelope/EnvelopeWizardStart.jsp?id=" + request.getParameter("selected"));
	}
%>
