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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.configuration.ConfigurationSpace,
			net.project.brand.Brand"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="configurationSpace" class="net.project.configuration.ConfigurationSpace" scope="session" />

<jsp:useBean id="xmlFormatter" class="net.project.xml.XMLFormatter" scope="page" />

<%
	String mode = request.getParameter("mode");
	boolean isCreate = false;
	String title = null;
	String queryString = null;
%>

<%
	if (mode != null && mode.equals("create")) { 
		isCreate = true;
		title = "Create Configuration";
		
		configurationSpace = new ConfigurationSpace();
		pageContext.setAttribute("configurationSpace",configurationSpace, pageContext.SESSION_SCOPE);
		
		queryString = "module=" + Module.CONFIGURATION_SPACE + "&action=" + Action.CREATE;
%>
		<security:verifyAccess action="create"
							   module="<%=Module.CONFIGURATION_SPACE%>" /> 
<%
	} else { 
		isCreate = false;
		title = "Edit Configuration";
		queryString = "module=" + Module.CONFIGURATION_SPACE + "&action=" + Action.MODIFY;
%>
		<security:verifyAccess action="modify"
							   module="<%=Module.CONFIGURATION_SPACE%>" /> 

<%
	   	//
		// Check that the user has space admin access.  Throws a security exception
		// if they do not.
		//
		configurationSpace.securityCheckSpaceAdministrator(user, "No permission to edit configuration");

	} 
%>	

<%
	if (isCreate) {
		configurationSpace.clear();
		// Configuration space will belong to default portfolio for application space
		configurationSpace.addPortfolioMemberID(applicationSpace.getDefaultPortfolioID());
	}
	Brand brand = configurationSpace.getBrand();
%>

<template:getDoctype />

<%@page import="org.hibernate.criterion.PropertyProjection"%>
<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_configuration_edit";
	openwin_help(helplocation);
}

function reset() { 
	theForm.reset();
}

function cancel() {

	<% if (configurationSpace.getID() != null && !configurationSpace.getID().trim().equals("") ) { %>
		self.document.location = JSPRootURL + "<%="/configuration/Main.jsp?module=" + Module.CONFIGURATION_SPACE + "&id=" + configurationSpace.getID()%>";
	<% } else { %>
		self.document.location = JSPRootURL + "<%="/admin/ConfigurationMenu.jsp?module=" + Module.APPLICATION_SPACE%>";	
	<% } %>
}

function submit () {
	theAction("submit");
	if(validateForm(theForm))
	    theForm.submit();
}


function validateForm(frm) {

	if (!checkTextbox(theForm.name,"Configuration Name is a required field")) return false;
    if (!checkMaxLength(theForm.description, 1000, "Description must be 1000 characters or less")) return false;
	if (!checkTextbox(theForm.abbreviation,"Abbreviation is a required field")) return false;
    if (!checkTextbox(theForm.supportedHostnames,"Supported Hostmames is a required field")) return false;
    
	
    return true;
}


</script>

</head>

<body  onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.configuration">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="<%=title%>" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/configuration/ConfigurationEdit.jsp"%>'
						  queryString='<%=queryString%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="<session:getJSPRootURL />/configuration/ConfigurationEditProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="mode" value="<%=mode%>" />
	<input type="hidden" name="module" value="<%=Module.CONFIGURATION_SPACE%>" />
	<input type="hidden" name="action" value='<%=(isCreate ? "" + Action.CREATE : "" + Action.MODIFY)%>' />

<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=title%></td>
        <td class="pageTitle" align="right"><%= net.project.util.HTMLUtils.escape(configurationSpace.getName()) %></td>
    </tr>
	<tr><td colspan="2">&nbsp;</td></tr>
</table>

<%-- Configuration Edit Form --%> 
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr> 
      <td align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.name.label") %></td>
      <td align="left" nowrap> 
        <input type="text" name="name" size="40" maxlength="80" value='<%= net.project.util.HTMLUtils.escape(configurationSpace.getName()) %>'>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td align="left" nowrap valign="top" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.description.label")%></td>
      <td align="left" nowrap> 
        <textarea name="description" cols="60" rows="3"><%= net.project.util.HTMLUtils.escape(configurationSpace.getDescription()) %></textarea>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.abbreviaton.label") %></td>
      <td> 
        <input type="text" name="abbreviation" size="40" maxlength="80" value='<%= net.project.util.HTMLUtils.escape(brand.getAbbreviation())%>' />
      </td>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td align="left" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.default.language.label")%></td>
      <td> 
        <select name="defaultLanguage">
			<%=net.project.resource.ProfileCodes.getLanguageOptionList(brand.getDefaultLanguage())%>
         </select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td align="left" nowrap class="fieldNonRequired" valign="top"><%=PropertyProvider.get("prm.project.configuration.supported.language.label")%></td>
      <td> 
		<jsp:setProperty name="xmlFormatter" property="stylesheet" value="/configuration/xsl/supported-languages-select.xsl" />
		<%=xmlFormatter.getPresentation(brand.getSupportedLanguagesXML())%>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.supported.hostnames.label")%></td>
      <td align="left" nowrap> 
        <input type="text" name="supportedHostnames" size="40" maxlength="250" value='<%=brand.getSupportedHostnamesCSV()%>' />
      </td>
      <td>&nbsp;</td>
    </tr>
</table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>