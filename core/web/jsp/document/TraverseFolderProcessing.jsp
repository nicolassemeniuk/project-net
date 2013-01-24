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
    info="Traverse Container"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.*,
	net.project.base.property.PropertyProvider,
    net.project.base.Module"
%>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
    int module = securityProvider.getCheckedModuleID();
    int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    if ((id.length() == 0) || (module != docManager.getModuleFromContainerID(id)) || (action != net.project.security.Action.VIEW && action != net.project.security.Action.LIST_DELETED))
          throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.traversefolder.authorizationfailed.message"));

    String returnPage = request.getParameter("Page");

%>

<%
	if (!id.equals(""))
		docManager.setCurrentContainer ( id );

%>

<%-- Goback to the documnet list --%>

<%

   if (returnPage == null || ( returnPage.equals("") ))
	if(module == Module.TRASHCAN)
		returnPage = "/trashcan/ViewDeletedDocuments.jsp";
	else
	    returnPage = "/document/Main.jsp?historyType=unspecified&module="+Module.DOCUMENT;

   pageContext.forward ( returnPage );

%>
