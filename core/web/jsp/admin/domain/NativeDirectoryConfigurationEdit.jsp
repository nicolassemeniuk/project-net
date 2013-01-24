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
|   $Revision: 20908 $
|   $Date: 2010-06-03 08:32:21 -0300 (jue, 03 jun 2010) $
|   $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Native Authenticator Configuration" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
        	net.project.security.SessionManager,
            net.project.security.Action,
            net.project.security.domain.UserDomain,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    String pageTitle = "Edit Native Authentication Provider Configuration";
    String domainID = domain.getID();
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title> 

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:getSpaceCSS />
     

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">

       var theForm;
       var isLoaded = false;
       var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["main"];
        isLoaded = true;
    }

    function cancel() {
	    self.document.location = JSPRootURL + '/admin/domain/Main.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>&domainID=<%=domainID%>';
    }

	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_native_edit";
		openwin_help(helplocation);
	}
    
    function validate() {
        //Check to make sure that required fields are present
        //if(!checkTextbox(theForm.name, "Please enter a domain name")) return false;
        return true;    
    }

    function tabClick(nextPage) {
        self.document.location = JSPRootURL + nextPage + '?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>&domainID=<%=domainID%>';
    }
    
</script>

</head>

<body  class="main" onload="setup()" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.domainmanager">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=pageTitle%>"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/AuthenticatorConfigurationEdit.jsp"%>'
                          queryString='module=<%=net.project.base.Module.APPLICATION_SPACE%>&domainID=<%=domainID%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip>
    <tab:tab label='<%=PropertyProvider.get("prm.domains.tab.editdomain.label") %>' href="javascript:tabClick('/admin/domain/DomainEdit.jsp');" />
    <tab:tab label='<%=PropertyProvider.get("prm.domains.tab.directoryproviderconfiguration.label")%>' href="javascript:tabClick('/admin/domain/AuthenticatorConfigurationEdit.jsp');" selected="true" />
</tab:tabStrip>

<table width="100%" border="0">
    <tr class="tableContent">
        <td class="tableContent">
            No additional configuration is required for native directory provider.
        </td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="cancel" label="Close" />
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

