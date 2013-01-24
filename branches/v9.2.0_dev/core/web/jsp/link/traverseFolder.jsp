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
|     $Author: umesha $  
|
|   processing page for documentBrowse.jsp
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Traverse Container"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.document.DocumentManagerBean,
            net.project.security.SecurityProvider,
            net.project.link.ILinkableObject,
            net.project.base.ObjectFactory"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="m_docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
ILinkableObject m_parent_obj = ObjectFactory.makeLinkableObject(id);
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.ObjectType.getModuleFromType(m_parent_obj.getType())%>" /> 

<%
    String containerID = request.getParameter("cid");
    String returnPage = request.getParameter("Page");
    String typePick = request.getParameter("type_pick");
    String searchLevel = request.getParameter("search_level");
    
    if(! securityProvider.isActionAllowed(containerID,String.valueOf(net.project.base.Module.DOCUMENT),net.project.security.Action.VIEW))
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.links.security.validationfailed.message"));
%>

<%
	if (!containerID.equals("") && containerID != null)
		m_docManager.setCurrentContainer ( containerID );


%>

<%-- Goback to the documnet list --%>

<%

   response.sendRedirect("addLinkForm.jsp?module=" + module + "&action=" + action + "&id=" + id + "&TYPE_PICK_DD=" + typePick + "&SEARCH_LEVEL=" + searchLevel);

%>
