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

<%--
| 09/03/2002
| Use of this JSP page has been deprecated.
| Its functionality is replaced by net.project.document.servlet.DocumentUploadServlet
--%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Process Document Create"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.User,
    net.project.security.SecurityProvider,
    net.project.security.Action,
    net.project.base.Module"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 

<%-- Get the fileObject and save it to the storage directory --%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<% 
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    if ((!id.equals( docManager.getCurrentContainerID())) || (module != docManager.getModuleFromContainerID(id)) || (action != net.project.security.Action.CREATE)) 
       throw new net.project.security.AuthorizationFailedException("Failed security validation");
%>


<%------------------------------------------------------------------------
  -- Bookmark instantiation and context setup
  ----------------------------------------------------------------------%>

<%

	Bookmark bookmarkBean = new Bookmark();

        // set user and container context
	bookmarkBean.setUser ( docManager.getUser() );
    bookmarkBean.setContainerID ( docManager.getCurrentContainerID() );
    pageContext.setAttribute("bookmark", bookmarkBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="bookmark" type="net.project.document.Bookmark" scope="page" />

<%-- Get the form fields --%>

	<jsp:setProperty name="bookmark" property="URL" param="url" />
	<jsp:setProperty name="bookmark" property="name" param="docName" />
	<jsp:setProperty name="bookmark" property="ownerID" param="authorID" />
	<jsp:setProperty name="bookmark" property="description" param="description" />
	<jsp:setProperty name="bookmark" property="statusID" param="statusID" />
	<jsp:setProperty name="bookmark" property="notes" param="notes" />

<%------------------------------------------------------------------------
  -- Bookmark instantiation and context setup
  ----------------------------------------------------------------------%>

<%

	bookmarkBean.store();
%>


<%------------------------------------------------------------------------
  -- Now take the user back where they belong
  ----------------------------------------------------------------------%>

<%

	// return the user to the Main Container List
        String forwardingPage = (String)docManager.getNavigator().get("TopContainer");

        if ( (forwardingPage != null) && ( !forwardingPage.equals("") ) )
            response.sendRedirect(forwardingPage);

	else {

	    request.setAttribute ("action", Integer.toString(Action.VIEW));
            request.setAttribute ("id", "");
            pageContext.forward ("/document/Main.jsp");
        }
	
%>


