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
|       $Date: 2008-11-21 13:47:28 +0100 (Fri, 21 Nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Methodology List"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*,
    		net.project.methodology.*,
            net.project.base.property.PropertyProvider,
		    net.project.space.Space" 
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />
<jsp:useBean id="methodologyPortfolio" class="net.project.methodology.MethodologyPortfolioBean"  />

<%
	// Implicit security provided.  We are displaying items for the current
	// authenticated user.
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- import all of the following css's and javascript files into this page --%>
<template:getSpaceCSS/>

<style type="text/css">
	div#content {
  		/*overflow-y: auto;*/
  		/*height: 100%;*/
	}
</style>

<%
/*
      methodologyPortfolio.clear();

	  methodologyPortfolio.setUser (user);
	  methodologyPortfolio.loadUserPortfolio();
*/
	  methodologyPortfolio.clear();

      methodologyPortfolio.setParentSpaceID (user.getCurrentSpace().getID());
      methodologyPortfolio.load();

%>


<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';
	
function setup() {

	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_browser";
	openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/methodology/MethodologyList.jsp"; }
function reset() { self.document.location = JSPRootURL + "/methodology/MethodologyBrowser.jsp"; }

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.template.name" showAll="true" leftTitle='<%=PropertyProvider.get("prm.template.browser.title")%>'>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='scrollwidecontent'>

<pnet-xml:transform name="methodologyPortfolio" stylesheet="/methodology/xsl/methodology-browser.xsl">
    <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
    <pnet-xml:property value="@prm.template.browser.name" />
    <pnet-xml:property value="@prm.template.browser.owner" />
    <pnet-xml:property value="@prm.template.browser.description" />
    <pnet-xml:property value="@prm.template.browser.usescenario" />
	<pnet-xml:property value="@prm.template.nonefound.message" />
</pnet-xml:transform>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action" >
	</tb:band>
</tb:toolbar>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
