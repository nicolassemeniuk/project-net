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
|   $RCSfile$
|   $Revision: 19764 $
|   $Date: 2009-08-17 05:46:51 -0300 (lun, 17 ago 2009) $
|   $Author: puno $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Build Info" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.util.DBVersion,
            net.project.security.SessionManager,
            net.project.brand.Brand,
            net.project.configuration.ConfigurationSpace"
%>
<jsp:useBean id="dbVersion" scope="page" class="net.project.util.DBVersion"/>
<jsp:useBean id="versionCheck" scope="application" class="net.project.versioncheck.VersionCheck"/>
<jsp:useBean id="brandManager" scope="application" class="net.project.brand.BrandManager"/>


<%@ include file="/base/taglibInclude.jsp" %>

<%
    dbVersion.load();
    brandManager.setAbbreviation("pnet");
    brandManager.setID("2000");
    
    brandManager.load();

	String brandID = brandManager.getID();
	String language = brandManager.getDefaultLanguage();
%>
<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%>
<%@page import="net.project.configuration.ConfigurationSpace"%>
<%@page import="java.util.Enumeration"%><html>
<head>
 <meta http-equiv="Pragma" content="no-cache">
 <meta http-equiv="Cache-Control" content="no-cache">
<title><display:get name="prm.global.application.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />


<script language="javascript">
        var theForm;
        var isLoaded = false;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
		theForm = self.document.forms[0];
        isLoaded = true;
    }
    
    function cancel() {
    	self.document.location = JSPRootURL + '/admin/Main.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>';
    }

</script>	
</head>


<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.buildinfo">
	<tb:setAttribute name="leftTitle" >
		<history:history>
            <history:module display="Build Info"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/utilities/BuildInfo.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br />

<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
    <tr class="channelHeader">
    	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    	<td valign="middle" align="left" class="channelHeader" colspan="2"><%=PropertyProvider.get("prm.project.admin.utilities.build.information.label") %></td>
    	<td>&nbsp;</td>
    	<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>

	<tr>
        <td>&nbsp;</td>
        <td class="tableHeader"><%=PropertyProvider.get("prm.project.admin.utilities.new.application.version.label") %>&nbsp;</td>
        <% if (versionCheck.isVersionCheckEnabled()){ 
        	if ("null".equals(versionCheck.getNewVersion()) || "".equals(versionCheck.getNewVersion()) || versionCheck.getNewVersion() == null){%>
        	<td class="tableContent"><%=PropertyProvider.get("prm.versioncheck.notavailable.message")%></td>
        	<% } else{ %>
        	<td class="tableContent">Project.net <%=versionCheck.getNewVersion()%> is available.</td>
        	<% }%>
        <% } else{ %>
        	<td class="tableContent"><%=PropertyProvider.get("prm.versioncheck.disabled.label")%></td>
        <% } %>
        <td>
        <% if (versionCheck.isVersionCheckEnabled()) {%>
        	<a class="historyText" href="<%=SessionManager.getJSPRootURL()%>/configuration/brand/BrandTokenEditProcessing.jsp?token::prm.versioncheck.isenabled=0&theAction=editToken&versionCheck=true&brandID=<%=brandID%>&currentLanguage=<%=language%>&prm.versioncheck.isenabled::systemProperty=0&prm.versioncheck.isenabled::translatableProperty=1&prm.versioncheck.isenabled::type=boolean"><%=PropertyProvider.get("prm.versioncheck.disable.label") %></a>
        <% } else{  %>
        	<a class="historyText" href="<%=SessionManager.getJSPRootURL()%>/configuration/brand/BrandTokenEditProcessing.jsp?token::prm.versioncheck.isenabled=1&theAction=editToken&versionCheck=true&brandID=<%=brandID%>&currentLanguage=<%=language%>&prm.versioncheck.isenabled::systemProperty=0&prm.versioncheck.isenabled::translatableProperty=1&prm.versioncheck.isenabled::type=boolean"><%=PropertyProvider.get("prm.versioncheck.enable.label") %></a>
        <% } %>
        </td>
        <td>&nbsp;</td>
    </tr>
	
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader"><%=PropertyProvider.get("prm.project.admin.utilities.application.version.label")%>&nbsp;</td>
        <td class="tableContent"><c:out value="${dbVersion.appVersion}"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
<%  if (dbVersion.isClientAppBuild()) { %>
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader"><c:out value="${dbVersion.clientName}"/> <%=PropertyProvider.get("prm.project.admin.utilities.application.version.label")%>&nbsp;</td>
        <td class="tableContent"><c:out value="${dbVersion.clientAppVersion}"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
<%  } %>    
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader">
        	<%=PropertyProvider.get("prm.project.admin.utilities.database.version.label")%>
        	&nbsp;</td>
        <td class="tableContent"><c:out value="${dbVersion.DBVersion}"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
<%  if (dbVersion.isBuildDatePresent()) { %>
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader">
        	<%=PropertyProvider.get("prm.project.admin.utilities.build.date.label")%>
        &nbsp;</td>
        <td class="tableContent"><c:out value="${dbVersion.appBuildDate}"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
<%  } %>    
<%  if (dbVersion.isClientDBBuild()) { %>
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader"><c:out value="${dbVersion.clientName}"/> 
        	<%=PropertyProvider.get("prm.project.admin.utilities.database.version.label")%>
        	&nbsp;</td>
        <td class="tableContent"><c:out value="${dbVersion.clientDBVersion}"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
<%  } %>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
