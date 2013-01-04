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
|   include page for displaying links  
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="include page for displaying children of a parent object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.link.LinkManagerBean,
            net.project.xml.XMLFormatter,
            net.project.base.ObjectFactory,
            net.project.link.ILinkableObject,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
if (!Validator.isBlankOrNull(id)) {
    ILinkableObject m_parent_obj = ObjectFactory.makeLinkableObject(module, id);
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.ObjectType.getModuleFromObject(m_parent_obj)%>" /> 
<%
    XMLFormatter m_formatter = new XMLFormatter();
    m_formatter.setStylesheet("/link/xsl/displayLinks.xsl");
    final String linksXML = display_linkMgr.getLinksXML();
    String linksOutput = m_formatter.getPresentation(linksXML);
    out.println(linksOutput);
}
%>
