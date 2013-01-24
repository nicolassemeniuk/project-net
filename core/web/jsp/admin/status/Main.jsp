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
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
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
            net.project.security.Action,
            net.project.status.SiteStatusManagerBean"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="siteStatusManager" class="net.project.status.SiteStatusManagerBean" />


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
     siteStatusManager.loadAll();
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
<template:getSpaceJS space="application" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var isLoaded = false;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms[0];
        isLoaded = true;
    }

    function create() {
	    self.document.location = JSPRootURL + '/admin/status/SystemStatusEdit.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>';
    }

	function remove(){                           
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.status.main.removestatus.confirm")%>', function(btn) { 
				if(btn == 'yes'){ 
					theAction("remove");
					theForm.module.value = '<%= Module.APPLICATION_SPACE%>';
					theForm.action.value = '<%= Action.DELETE %>';
        			theForm.submit();
				}else{
			 		return false;
				}
			});
		}
	}
    function reset() {
        self.document.location = JSPRootURL + '/admin/status/Main.jsp?module=<%=Module.APPLICATION_SPACE%>';
    }
    
	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=status_main";
		openwin_help(helplocation);
	}
    
</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.systemstatus">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="System Status"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/status/Main.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    	<tb:button type="create" />
		<tb:button type="remove" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<form action="<%=SessionManager.getJSPRootURL()%>/admin/status/MainProcessing.jsp" method="post">
    <input type="hidden" name="module">
	<input type="hidden" name="theAction">
	<input type="hidden" name="action">
		
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader">Site Status Messages List</td>
	<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="2">
		<pnet-xml:transform name="siteStatusManager" stylesheet="/admin/status/xsl/sitestatus-collection.xsl" />
	</td>
</tr>
</tbody>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="application"/>
</body>
</html>
