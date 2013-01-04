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
    info="Process Document Vault Import"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*, 
			net.project.security.SecurityProvider,
			net.project.base.property.PropertyProvider,
			net.project.base.Module"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<% 
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    if ((!id.equals( docManager.getCurrentContainerID())) || (module != docManager.getModuleFromContainerID(id)) || (action != net.project.security.Action.CREATE)) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.importobject.authorizationfailed.message"));
%>

<%------------------------------------------------------------------------
  -- Forward request to approprite processing page
  ----------------------------------------------------------------------%>

<%

	String documentType = request.getParameter ("documentType");

	if ( documentType.equals (ContainerObjectType.DOCUMENT_OBJECT_TYPE) ) {
	    pageContext.forward ("/document/CreateDocumentProcessing.jsp");
	} else if ( documentType.equals (ContainerObjectType.BOOKMARK_OBJECT_TYPE) ) {
	    pageContext.forward ("/document/CreateBookmarkProcessing.jsp");
    }

%>
