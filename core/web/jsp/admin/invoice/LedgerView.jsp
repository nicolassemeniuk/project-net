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
| Invoicing Main
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Ledger View"
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
<jsp:useBean id="ledgerEntryCollection" class="net.project.billing.ledger.LedgerEntryCollection" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Additional HEAD stuff --%>
<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />

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
   
function checkFilters(sfOption) {
	theForm.elements['theAction'].value = 'submit';
	if(sfOption == 'entry'){
    	if (theForm.elements['status'].value == 'all' && theForm.elements['category'].value == 'all' && theForm.elements['group'].value == 'all') {
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.ledgerview.loadall.confirm")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.submit();
				}else{
				 	theForm.elements['status'].focus();
				}
			 });
		} else {
			theForm.elements['searchOption'].value = 'ledgerFilter';
			theForm.submit();
		}
	} else {
		if (theForm.elements['payInfo'].value == '' && theForm.elements['folName'].value == '' && theForm.elements['partNumber'].value == '') {
			var errorMessage = "<%=PropertyProvider.get("prm.global.javascript.admin.invoice.search.error.message")%>";
			extAlert('Error Message', errorMessage, Ext.MessageBox.ERROR);
			theForm.elements['payInfo'].focus();
		} else {
			theForm.elements['searchOption'].value = 'userFilter';
			theForm.submit();
		}
	}
    
}

function sort(sortField) {
	theForm.elements['sortField'].value = sortField;
	theForm.elements['theAction'].value = 'sort';
	theForm.submit();

}
    
function cancel() {
    self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/Main.jsp?module=" + Module.APPLICATION_SPACE%>'; 
}

function reset() { self.document.location = JSPRootURL + "/admin/invoice/LedgerView.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>"; }

function help() {
	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=billing_ledgerview");
}

function tabClick(nextPage) {
	nextPage = JSPRootURL + nextPage + '?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>';
    self.document.location =  nextPage ;
}

</script>
</head>
<%-- End of HEAD --%>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-- Begin Content --%>		

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.billingmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Billing"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<history:page display="View Ledger"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE %>' />				
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />
<%-- Tab Bar --%>
<tab:tabStrip>
    <tab:tab label="View Ledger" href="javascript:tabClick('/admin/invoice/LedgerView.jsp');" selected="true"/>
	<tab:tab label="Invoicing Tasks" href="javascript:tabClick('/admin/invoice/InvoicingTasks.jsp');"/>
</tab:tabStrip>


<%  
	boolean displayLedger = false;
	String displayAttribute = (String)session.getAttribute("displayLedger");
	String status = (String)session.getAttribute("status");
	String category = (String)session.getAttribute("category");
	String group = (String)session.getAttribute("group");
	
	if(displayAttribute != null && (displayAttribute).equals("true")) {
		displayLedger = true;
	}
	
	String payInfo = (String)session.getAttribute("payInfo");
	String folName = (String)session.getAttribute("folName");
	String partNumber = (String)session.getAttribute("partNumber");
%>
<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerViewProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.VIEW%>">
	<input type="hidden" name="searchOption" value="" >
	<input type="hidden" name="sortField" value="" >
	
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
		<td class="fieldRequired">Ledger Display Filters</td>
		<td class="fieldRequired">&nbsp;&nbsp;</td>
		<td class="fieldRequired">Ledger Entry Search</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
	<%-- BEGIN license attribute based search parameters --%>
	<td align="left">
	<table width="50%" cellpadding="0" cellspacing="0" border="0">
		<tr colspan="3">
			<td class="fieldNonRequired" align="right" nowrap>Entry Status :&nbsp;</td>
			<td align="left">
				<select name="status">
				<% if(status == null || status.equals("all")) {%>
					<option value="all" selected>All</option>
				<% } else {%>	
					<option value="all">All</option>
				<% } %>
				<% if(status != null && status.equals("invoiced")) {%>
					<option value="invoiced" selected>Invoiced</option>
				<% } else {%>
					<option value="invoiced">Invoiced</option>
				<% } %>	
				<% if(status != null && status.equals("notinvoiced")) {%>
					<option value="notinvoiced" selected>Not Invoiced</option>
				<% } else {%>
					<option value="notinvoiced">Not Invoiced</option>
				<% } %>
				</select>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td class="fieldNonRequired"  align="right" nowrap>Entry Category :&nbsp;</td>
			<td align="left">
				<select name="category">
				<% if(category != null && category.equals("all")) {%>
					<option value="all" selected>All</option>
				<% } else {%>	
					<option value="all">All</option>
				<% } %>
				<% if(category != null && category.equals("License_Usage_Type_A")) {%>	
					<option value="License_Usage_Type_A" selected>License_Usage_Type_A</option>
				<% } else {%>	
					<option value="License_Usage_Type_A">License_Usage_Type_A</option>
				<% } %>
				<% if(category != null && category.equals("License_Maintenance_Type_B")) {%>	
					<option value="License_Maintenance_Type_B" selected>License_Maintenance_Type_B</option>
				<% } else {%>	
					<option value="License_Maintenance_Type_B">License_Maintenance_Type_B</option>
				<% } %>
				</select>
			</td>
				
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right" >Entry Group :&nbsp; </td>
			<td  align="left">
			<select name="group">
				<% if(group != null && group.equals("all")) {%>
					<option value="all" selected>All</option>
				<% } else {%>	
					<option value="all">All</option>
				<% } %>
				<% if(group != null && group.equals("Trial")) {%>	
					<option value="Trial" selected>Trial</option>
				<% } else {%>	
					<option value="Trial">Trial</option>
				<% } %>
				<% if(group != null && group.equals("Charge_Code")) {%>	
					<option value="Charge_Code" selected>Charge_Code</option>
				<% } else {%>	
					<option value="Charge_Code">Charge_Code</option>
				<% } %>
				<% if(group != null && group.equals("Credit_Card")) {%>	
					<option value="Credit_Card" selected>Credit_Card</option>
				<% } else {%>	
					<option value="Credit_Card">Credit_Card</option>
				<% } %>
				</select>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td  align="right">
				<a href="javascript:checkFilters('entry')">Filter	<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"> </a>
			</td>
		</tr>
	</table>
	</td>
	<%-- END ledger attribute based search parameters --%>
	<td>&nbsp;</td> 
	<%-- BEGIN search parameters --%>
	<td colspan="6">
	<table width="50%" cellpadding="0" cellspacing="0" border="0">
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right"  >Pay Info :&nbsp; </td>
			<td  align="left">
			<% if(payInfo != null) {%>
				<input type="text" name="payInfo" value='<%= payInfo%>'>
			<% } else {%>
				<input type="text" name="payInfo" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right" nowrap>First/Last Name :&nbsp; </td>
			<td  align="left">
			<% if(folName != null) {%>
				<input type="text" name="folName" value='<%= folName%>'>
			<% } else {%>
				<input type="text" name="folName" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right" nowrap>License Key :&nbsp; </td>
			<td  align="left">
			<% if(partNumber != null) {%>
				<input type="text" name="partNumber" value='<%= partNumber%>'>
			<% } else {%>
				<input type="text" name="partNumber" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td  align="right">
				<a href="javascript:checkFilters('user')">Search <img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"> </a>
			</td>
		</tr>
	</table>	
	</td>
	</tr>
	
	<tr><td colspan="6">&nbsp;</td></tr>
	
</table>

	<% if(displayLedger) { %>
		<channel:channel name='<%="ApplicationSpaceInvoicing_" + applicationSpace.getName()%>' customizable="false">
    		<channel:insert name='<%="LedgerDisplay_" + applicationSpace.getName()%>'
                    title="Ledger Information" minimizable="false" closeable="false"
					include="/admin/invoice/include/LedgerView.jsp" >
				<channel:button style="action" type="cancel"  href="javascript:cancel();"/>	
			</channel:insert>						
		</channel:channel>
		
	<% } %>
</form>
<br>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

