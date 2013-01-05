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
| Invoice Preview
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Invoice Preview"
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
<jsp:useBean id="invoiceManager" class="net.project.billing.invoice.InvoiceManager"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<%-- <jsp:useBean id="invoice" scope="session" class="net.project.billing.invoice.Invoice"/>  --%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<%
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.APPLICATION_SPACE);
%>
<template:getDoctype />
<template:insert>
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 

<%-- Additional HEAD stuff --%>
<template:put name="head">	

<%----- import Javascript Files -------------------------------------%>

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

function reset() { self.document.location = JSPRootURL + "/admin/invoice/CreateInvoicePreview.jsp?theAction=createinvoice&module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>"; }

function tabClick(nextPage) {
	self.document.location = JSPRootURL + nextPage + '?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>';
}

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=billing_invoice&section=create_preview");
}

function submit() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', 
	         '<%=PropertyProvider.get("prm.project.admin.createinvoice.createnewinvoice.confirm")%>', function(btn) { 
		if(btn == 'yes') {
			self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/invoice/CreateInvoicePreviewProcessing.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY %>';
 		}else {
			return false; 
		}
	});
}

function cancel() {
	self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/invoice/InvoicingTasks.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW %>'; 
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
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/LedgerView.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<history:page display="Create Invoice Preview"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/invoice/Main.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE %>' />				
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">  
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />
<table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
		<td class="fieldRequired">Invoice Preview</td>
		
	</tr>
	<tr> <td>&nbsp;</td></tr>
	
	<tr><td colspan="6">
	<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">
    	<channel:insert name='<%="CreateInvoicePreview_" + applicationSpace.getName()%>'
                    title="Invoice Preview Summary" minimizable="false" closeable="false"
					include="/admin/invoice/include/CreateInvoicePreview.jsp">  
				<channel:button style="action" type="submit" label='Finish' href="javascript:submit();"/>	
				<channel:button style="action" type="cancel" label='Cancel' href="javascript:cancel();"/>
					
		</channel:insert>						
	</channel:channel>
	</td>
	
	</tr>
	</table>

</template:put>
<%-- End Content --%>

</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />

