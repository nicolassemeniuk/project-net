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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Configuration Properties
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Space Properties" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.xml.XMLFormatter,
			net.project.security.User,
            net.project.security.SecurityProvider,
			net.project.security.Action,
			net.project.space.Space,
			net.project.base.Module,
			net.project.configuration.ConfigurationSpace,
			net.project.brand.Brand,
			net.project.base.property.PropertyManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%@page import="net.project.base.property.PropertyProvider"%><jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="configurationSpace" class="net.project.configuration.ConfigurationSpace" scope="session" />

<%--
	Note on Security:
	Ability to modify in-session configuration should be granted by the INCLUDING
	page.  A configuration is a space, not a real object, so does not have
	ordinary object permissions.  Hence, we cannot test for object permissions
	here.
--%>

<%
	// XML Formatter used for miscellaneous presentation
	XMLFormatter xmlFormatter = new XMLFormatter();

	// Now get the brand for this configuration space
	Brand brandBean = configurationSpace.getBrand();
	pageContext.setAttribute("brand", brandBean, PageContext.SESSION_SCOPE);
%>
<jsp:useBean id="brand" type="net.project.brand.Brand" scope="session" />

<table border="0"  align="left" width="100%" cellpadding="0" cellspacing="2">
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.conf.name.label") %></td>
		<td nowrap  class="tableContent"><jsp:getProperty name="configurationSpace"  property="name" /></td>
	</tr>
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.description.label") %>
		</td>
		<td nowrap  class="tableContent"><%= net.project.util.HTMLUtils.escape(configurationSpace.getDescription()) %></td>
	</tr>
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.abbreviaton.label")%></td>
		<td nowrap  class="tableContent"><jsp:getProperty name="brand"  property="abbreviation" /></td>
	</tr>
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.default.language.label")%></td>
		<td nowrap  class="tableContent"><jsp:getProperty name="brand"  property="defaultLanguageName" /></td>
	</tr>
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.supported.language.label")%></td>
		<td nowrap  class="tableContent">
			<%xmlFormatter.setStylesheet("/configuration/brand/xsl/supported-languages-view.xsl");%>
			<%=xmlFormatter.getPresentation(brand.getSupportedLanguagesXML())%>
		</td>
	</tr>
	<tr align="left" > 
		<td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.supported.hostnames.label")%></td>
		<td nowrap  class="tableContent"><jsp:getProperty name="brand" property="supportedHostnamesCSV" /></td>
	</tr>
</table>
