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
    info="Process Document Modify"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.Bookmark,
	    	net.project.security.Action,
	    	net.project.security.SessionManager,
	    	net.project.base.Module"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<%
	// security check done in docmanager
	Bookmark bookmarkBean = (Bookmark) docManager.getCurrentObject();
	bookmarkBean.setUser ( docManager.getUser() );
    pageContext.setAttribute("bookmark", bookmarkBean, PageContext.PAGE_SCOPE);

%>
<jsp:useBean id="bookmark" type="net.project.document.Bookmark" scope="page" />

<%-- Get the form fields --%>
	<jsp:setProperty name="bookmark" property="*" />

<%-- Update the new bookmark --%>

<%
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		bookmark.setDescription("");
	}
	if ((request.getParameter("notes") == null) || (request.getParameter("notes").equals(""))) {
		bookmark.setNotes("");
	}
	
	bookmark.setURL(request.getParameter("url"));

	// commit the changes to the system, in the current container
    docManager.modifyObject (bookmark); 

	// return the user to the Main Container List

   String forwardingPage =(String) docManager.getNavigator().get("TopContainer");

   request.setAttribute ("module", Integer.toString(Module.DOCUMENT));
   request.setAttribute ("action", Integer.toString(Action.VIEW));
   request.setAttribute ("id", "");

   if ((forwardingPage != null) && ( !forwardingPage.equals(""))){
	    if(forwardingPage.contains("/documents/Details")) {
	    	response.sendRedirect(SessionManager.getJSPRootURL() +  forwardingPage);
	    } else { 
	        response.sendRedirect (forwardingPage);
	    }
   }else {
		pageContext.forward (SessionManager.getJSPRootURL() + "/document/Main.jsp");
   }
%>


