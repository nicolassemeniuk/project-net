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
|   $Revision: 19188 $
|       $Date: 2009-05-11 16:24:02 -0300 (lun, 11 may 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.security.domain.UserDomainCollection"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domainCollection" class="net.project.security.domain.UserDomainCollection" />


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
     domainCollection.load();
%>


<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<template:getSpaceJS space="application" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var isLoaded = false;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["main"];
        isLoaded = true;
    }

    function modify() {
        if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	    	self.document.location = JSPRootURL + '/admin/domain/DomainEdit.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&domainID=' + getSelectedValue(theForm.selected);
		}
    }

    function create() {
	    self.document.location = JSPRootURL + '/admin/domain/DomainEdit.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>';
    }

    function reset() {
        self.document.location = JSPRootURL + '/admin/domain/Main.jsp?module=<%=Module.APPLICATION_SPACE%>';
    }
    
	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=domain_main";
		openwin_help(helplocation);
	}

    function deleteDomain() {
        if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	    	self.document.location = JSPRootURL + '/admin/domain/DomainEditProcessing.jsp?module=<%=Module.APPLICATION_SPACE%>&theAction=<%=net.project.security.Action.DELETE%>&domainID=' + getSelectedValue(theForm.selected);
		}
    }    
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="application"/>
<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.domainmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Domain Manager"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/Main.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    	<tb:button type="create" />
    	<tb:button type="modify" />
    	<tb:button type="remove" label="Delete" function='javascript:deleteDomain();'/>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL()%>/admin/domain/DomainProcessing.jsp" method="post">
    <input type="hidden" name="module" >
	<input type="hidden" name="theAction">

<%-- Portfolio Table --%>
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader">User Domain List</td>
	<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="2">
   		<%-- Draw the UserDomainCollection --%>
		<pnet-xml:transform name="domainCollection" stylesheet="/admin/domain/xsl/domain-collection.xsl" />
	</td>
</tr>
</tbody>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
