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
| View Invoice List
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="View Invoice List"
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
<jsp:useBean id="invoiceCollection" class="net.project.billing.invoice.InvoiceCollection" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<template:getDoctype />
<template:insert>
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 

<%-- Additional HEAD stuff --%>
<template:put name="head">

<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />

<template:getSpaceJS />
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
	
	if (theForm.elements['invoicePayInfo'].value == '' && theForm.elements['invoiceFolName'].value == '' && theForm.elements['invoicePartNumber'].value == '') {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', 
				'<%=PropertyProvider.get("prm.project.admin.viewinvoicelist.loadallinvoice.confirm")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.elements['searchOption'].value = 'loadAll';
					theForm.submit();
				}else{
				 	theForm.elements['invoicePayInfo'].focus();
				}
			 });
	} else {
		theForm.elements['searchOption'].value = 'userFilter';
		theForm.submit();
	}
   
}

function sort(sortField) {
	theForm.elements['sortField'].value = sortField;
	theForm.elements['theAction'].value = 'sort';
	theForm.submit();

}
    
function cancel() {
    self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/invoice/InvoicingTasks.jsp?module=" + Module.APPLICATION_SPACE%>'; 
}

function reset() { self.document.location = JSPRootURL + "/admin/invoice/ViewInvoiceList.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>"; }

function help() {
	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=billing_invoicelist");
}

function tabClick(nextPage) {
	nextPage = JSPRootURL + nextPage + '?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>';
    self.document.location =  nextPage ;
}

</script>

</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>		
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.billingmanager">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Billing"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/InvoicingTasks.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<history:page display="View Invoice List"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/ViewInvoiceList.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE %>' />				
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>
<br />

<%  
	boolean displayInvoice = false;
	String displayAttribute = (String)session.getAttribute("displayInvoice");
	
	if(displayAttribute != null && (displayAttribute).equals("true")) {
		displayInvoice = true;
	}
	
	String invoicePayInfo = (String)session.getAttribute("invoicePayInfo");
	String invoiceFolName = (String)session.getAttribute("invoiceFolName");
	String invoicePartNumber = (String)session.getAttribute("invoicePartNumber");
%>
<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/invoice/ViewInvoiceListProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.VIEW%>">
	<input type="hidden" name="searchOption" value="" >
	<input type="hidden" name="sortField" value="" >
	
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
		<td align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.admin.invoice.search.label") %></td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
	<%-- BEGIN search parameters --%>
	<tr>
		<td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.admin.invoice.pay.info.label")%>&nbsp; </td>
		<td  align="left">
		<% if(invoicePayInfo != null) {%>
			<input type="text" name="invoicePayInfo" value='<%= invoicePayInfo%>'>
		<% } else {%>
			<input type="text" name="invoicePayInfo" value="">
		<% } %>	
		</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr colspan="3">
		<td align="left" class="fieldNonRequired" nowrap><%=PropertyProvider.get("prm.project.admin.invoice.first.last.name.label")%>&nbsp; </td>
		<td  align="left">
		<% if(invoiceFolName != null) {%>
			<input type="text" name="invoiceFolName" value='<%= invoiceFolName%>'>
		<% } else {%>
			<input type="text" name="invoiceFolName" value="">
		<% } %>	
		</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<td colspan="1" align="left" class="fieldNonRequired" nowrap><%=PropertyProvider.get("prm.project.admin.invoice.license.key.label")%>&nbsp; </td>
		<td colspan="2" align="left">
		<% if(invoicePartNumber != null) {%>
			<input type="text" name="invoicePartNumber" value='<%= invoicePartNumber%>'>
		<% } else {%>
			<input type="text" name="invoicePartNumber" value="">
		<% } %>	
		</td>
		<td colspan="3">&nbsp;</td>	
	</tr>
	<tr><td>&nbsp;</td><tr>
	<tr colspan="3">	
		<td  align="left">
			<a href="javascript:checkFilters('user')"><%=PropertyProvider.get("prm.project.admin.invoice.search.label")%> <img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"> </a>
		</td>
	</tr>
		
	<tr><td colspan="6">&nbsp;</td></tr>
	
</table>

	<channel:channel name='<%="ApplicationSpaceInvoicing_" + applicationSpace.getName()%>' customizable="false">
    	<channel:insert name='<%="ViewInvoiceList_" + applicationSpace.getName()%>'
                   title="Invoice List" minimizable="false" closeable="false"
				include="/admin/invoice/include/ViewInvoiceList.jsp" >
			<channel:button style="action" type="cancel"  href="javascript:cancel();"/>	
		</channel:insert>						
	</channel:channel>
</FORM>

</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />

