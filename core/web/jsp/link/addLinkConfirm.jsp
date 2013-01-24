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
|   $Revision: 20776 $
|       $Date: 2010-04-30 09:24:27 -0300 (vie, 30 abr 2010) $
|     $Author: uroslates $  
|
|   The processing page for addLink.jsp
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="page for adding a link to an object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.link.LinkManagerBean,
            net.project.link.ILinkableObject,
            net.project.base.ObjectFactory,
            net.project.security.SecurityProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="m_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
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
m_linkMgr.addLink(id,request.getParameter("selected"));

response.sendRedirect("LinkManager.jsp?module=" + module + "&action=" + action + "&id=" + id + "&linked=1" + "&objectId=" + request.getParameter("objectId"));

%>    
