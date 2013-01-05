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
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.base.Module,net.project.security.Action,
            net.project.security.SessionManager,net.project.resource.PersonListBean,
            net.project.security.domain.UserDomain"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	 domain.clear();
	 	
	 domain.setID (request.getParameter ("selected"));
	 domain.load();
	 
	 //userList.clear();
	 
	 userList.setDisplayMode("initial");
	  
	String refLink, refLinkEncoded,refLinkDecoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
	refLinkDecoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLDecoder.decode(refLink) : "");
	 
%>

<template:getDoctype />
<html>
<head>
	<title><display:get name="prm.global.application.title" /></title>

	<%-- Import CSS --%> 
	<template:getSpaceCSS space="application" />
	<%-- Import Javascript --%>
	<template:getSpaceJS space="application" />

<script language="javascript">
	var theForm;
	var errorMsg;
	var isLoaded = false;
	var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

	function setup() {

		theForm = self.document.forms[0];
		isLoaded = true;

	} // end setup()

    function reset() {
        self.document.location = JSPRootURL + '/admin/domain/DomainProperties.jsp?module=<%=Module.APPLICATION_SPACE%>&selected=<%=domain.getID()%>';
    }

	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=domain_properties";
		openwin_help(helplocation);
	}
	
	function modify()	{
		theAction("modify");
		theForm.action.value='<%=Action.MODIFY%>';
		theForm.submit();
	}
	
	function tabClick(nextPage) {
        self.document.location = JSPRootURL + nextPage +'?module=<%=Module.APPLICATION_SPACE%>&selected=<%=domain.getID()%>';            
    }
	
	</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="application"/>
<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.domainmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display="Domain Properties"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/DomainProperties.jsp"%>'
                          queryString='module=<%=Module.APPLICATION_SPACE%>&selected=<%=domain.getID()%>' />
		</history:history>
    </tb:setAttribute>
    <tb:band name="standard">
		 <tb:button type="modify" />
    </tb:band>
</tb:toolbar>

<br>

<div id='content'>

<tab:tabStrip>
    <tab:tab label="Domain Properties" href="javascript:tabClick('/admin/domain/DomainProperties.jsp');" selected="true" />
	<tab:tab label="Manage Users" href="javascript:tabClick('/admin/domain/DomainUserList.jsp');" />
</tab:tabStrip>

<form action="<%=SessionManager.getJSPRootURL()%>/admin/domain/DomainPropertiesProcessing.jsp" method="post">

	<input type="hidden" name="module"  value='<%= net.project.base.Module.APPLICATION_SPACE%>' >
	<input type="hidden" name="theAction">
	<input type="hidden" name="action" value='<%=Action.MODIFY%>'>
	<input type="hidden" name="domainID" value='<%=request.getParameter ("selected") %>' >
	<input type="hidden" name="refLink" value='<%= refLinkEncoded %>' > 
	
	<table border="0" cellpadding="0" cellspacing="0"  width="97%">
	<tbody>
   	<tr>
		<td>&nbsp;</td>
		<td colspan="2">

			<pnet-xml:transform name="domain" scope="session" stylesheet="/admin/domain/xsl/domain-properties.xsl" />

		</td>
	</tr>

	</tbody>
	</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>


</body>
</html>
