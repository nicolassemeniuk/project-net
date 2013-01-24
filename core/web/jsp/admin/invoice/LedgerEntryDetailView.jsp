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
| Ledger Entry Detail View
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Ledger Detail View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<html>
<head>
<title>Ledger Detail View</title>

<%-- Additional HEAD stuff --%>
<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<%-- Import CSS --%>
<template:getSpaceCSS/>
    
<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
    	
function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}
   
function sort(sortField) {
	theForm.elements['sortField'].value = sortField;
	theForm.elements['theAction'].value = 'sort';
	theForm.submit();

}
    
function cancel() {
    self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp?module=" + Module.APPLICATION_SPACE%>'; 
}

function reset() { self.document.location = JSPRootURL + "/admin/invoice/LedgerEntryDetailView.jsp?ledgerID=<%= request.getParameter("ledgerID")%>&module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>"; }

function help() {
	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=billing_ledgerentrydetailview");
}

</script>
</head>

<body class="main" onload="setup();">

<%-- End of HEAD --%>

<%-- Begin Content --%>		

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.billingmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Billing"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<history:page display="Ledger Entry Detail View"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE %>' />				
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>
<br />

<channel:channel name='<%="ApplicationSpaceInvoicing_" + applicationSpace.getName()%>' customizable="false">
   		<channel:insert name='<%="LedgerEntryDetailView_" + applicationSpace.getName()%>'
                title="Ledger Entry Details" minimizable="false" closeable="false"
				include="/admin/invoice/include/LedgerEntryDetailView.jsp" >
		<channel:button style="action" type="cancel"  href="javascript:cancel();"/>	
		</channel:insert>						
</channel:channel>
<br>
<%@ include file="/help/include_outside/footer.jsp" %>		
</body>
</html>