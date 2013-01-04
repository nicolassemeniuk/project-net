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

<%----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Menu" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.admin.ApplicationSpace,
			net.project.xml.XMLFormatter,
			net.project.configuration.ConfigurationPortfolio" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
<%
	boolean doReload = true;
	ConfigurationPortfolio portfolio = applicationSpace.getDefaultConfigurationPortfolio(doReload);
	XMLFormatter xmlFormatter = new XMLFormatter();
%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr>
	<td>
		<%-- Apply stylesheet to format Configuration portfolio --%>
		<%xmlFormatter.setStylesheet("/admin/include/xsl/configuration-menu.xsl");%>
		<%=xmlFormatter.getPresentation(portfolio.getXML())%>
    </td>
  </tr>
</table>
