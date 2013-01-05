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

<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Display"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
			net.project.admin.ApplicationSpace"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseCollection" class="net.project.license.LicenseCollection" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:verifyAccess module="<%=net.project.base.Module.APPLICATION_SPACE%>" 
                       action="view" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Additional HEAD stuff --%>
<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />

<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<%-- Import CSS --%>
<template:getSpaceCSS/>
    
<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
    var orgPage = '<%= (String)session.getAttribute("orgPage") %>';
	
function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}
   
function checkFilters(sfOption) {
	theForm.elements['theAction'].value = 'submit';
	if(sfOption == 'license'){
    	if (theForm.elements['licenseType'].value == 'all' && theForm.elements['licenseStatus'].value == 'all' && theForm.elements['searchLicenseKey'].value == '') {
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.license.listview.nofilterset.message")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.submit();
				}else{
				 	theForm.elements['licenseType'].focus();
				}
			 });
		} else {
			theForm.elements['searchOption'].value = 'licenseFilter';
			theForm.submit();
		}
	} else {
		if (theForm.elements['userName'].value == '' && theForm.elements['folName'].value == '' && theForm.elements['emailID'].value == '') {
			var errorMessage = '<%=PropertyProvider.get("prm.license.listview.specifycriteria.message")%>';
			extAlert('Error Message', errorMessage , Ext.MessageBox.ERROR);
			theForm.elements['userName'].focus();
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
    //self.document.location = '<%= SessionManager.getJSPRootURL() + "/admin/Main.jsp?&module=" + Module.APPLICATION_SPACE%>'; 
	<% if (user.isApplicationAdministrator()) { %>
		self.document.location = JSPRootURL + orgPage + '<%="?&module=" + Module.APPLICATION_SPACE%>';
	<% } else { %>
		self.document.location = JSPRootURL + orgPage + '<%="?&module=" + Module.PERSONAL_SPACE%>';
	<% } %>
		 	
}

function reset() { self.document.location = JSPRootURL + "/admin/license/LicenseListView.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>"; }

function help() {
	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin&section=license_listview");
}

function tabClick(nextPage) {
	nextPage = JSPRootURL + nextPage + '?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>';
    self.document.location =  nextPage ;
}

</script>
</head>
<%-- End of HEAD --%>

<body class="main" id='bodyWithFixedAreasSupport' onload="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-- Create the toolbar --%>		

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager">
    <tb:setAttribute name="leftTitle">
		<history:history>
			<% if (user.isApplicationAdministrator()) { %>
			<history:project display="<%= applicationSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<% } else { %>				  
			<history:project display="<%= personalSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<% } %>				  
			<history:module display='<%=PropertyProvider.get("prm.license.module.history")%>'
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />				  
            <history:page display='<%=PropertyProvider.get("prm.license.listview.module.history")%>'
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseListView.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' />
        </history:history>
	</tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<% if (user.isApplicationAdministrator()) {%>
<tab:tabStrip width="97%">
    <tab:tab label="Manage Existing Licenses" href="javascript:tabClick('/admin/license/LicenseListView.jsp');" selected="true" />
	<tab:tab label="Licensing Tasks" href="javascript:tabClick('/admin/license/LicensingTasks.jsp');" />
</tab:tabStrip>
<% } else { %>
	<tab:tabStrip width="97%">
    <tab:tab label='<%=PropertyProvider.get("prm.license.listview.manage.tab")%>' href="javascript:tabClick('/admin/license/LicenseListView.jsp');" selected="true" />
	</tab:tabStrip>
<% } %>

<%  // Must remove the licenseKey attribute from session so that we 
	// always refer to the right licenseKey in the license detail view  
	session.removeAttribute("licenseKey");
	boolean displayLicenses = false;
	String userIdentity = (String)session.getAttribute("userIdentity");
	String displayAttribute = (String)session.getAttribute("displayLicenses");
	String licenseType = (String)session.getAttribute("licenseType");
	String licenseStatus = (String)session.getAttribute("licenseStatus");
	String searchLicenseKey = (String)session.getAttribute("searchLicenseKey");
	
	if(displayAttribute != null && (displayAttribute).equals("true")) {
		displayLicenses = true;
	}
	
	String userName = (String)session.getAttribute("userName");
	String folName = (String)session.getAttribute("folName");
	String emailID = (String)session.getAttribute("emailID");
%>
<form name="main" action="<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseListViewProcessing.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.VIEW%>">
	<input type="hidden" name="searchOption" value="" >
	<input type="hidden" name="sortField" value="" >
	
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
		<td class="fieldRequired"><%=PropertyProvider.get("prm.license.listview.displayfilters.label")%></td>
		<td class="fieldRequired">&nbsp;&nbsp;</td>
		<td class="fieldRequired"><%=PropertyProvider.get("prm.license.listview.search.label")%></td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
	<%-- BEGIN license attribute based search parameters --%>
	<td align="left">
	<table width="50%" cellpadding="0" cellspacing="0" border="0">
		<tr colspan="3">
			<td class="fieldNonRequired" align="right" nowrap><%=PropertyProvider.get("prm.license.listview.type.label")%>&nbsp;</td>
			<td align="left">
				<select name="licenseType">
				<% if(licenseType == null || licenseType.equals("all")) {%>
					<option value="all" selected><%=PropertyProvider.get("prm.license.listview.type.option.all.name")%></option>
				<% } else {%>	
					<option value="all"><%=PropertyProvider.get("prm.license.listview.type.option.all.name")%></option>
				<% } %>
				<% if(licenseType != null && licenseType.equals("trial")) {%>
					<option value="trial" selected><%=PropertyProvider.get("prm.license.listview.type.option.trial.name")%></option>
				<% } else {%>
					<option value="trial"><%=PropertyProvider.get("prm.license.listview.type.option.trial.name")%></option>
				<% } %>	
				<% if(licenseType != null && licenseType.equals("nontrial")) {%>	
					<option value="nontrial" selected><%=PropertyProvider.get("prm.license.listview.type.option.nontrial.name")%></option>
				<% } else {%>	
					<option value="nontrial"><%=PropertyProvider.get("prm.license.listview.type.option.nontrial.name")%></option>
				<% } %>
				</select>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td class="fieldNonRequired"  align="right" nowrap><%=PropertyProvider.get("prm.license.listview.status.label")%>&nbsp;</td>
			<td align="left">
				<select name="licenseStatus">
				<% if(licenseStatus == null || licenseStatus.equals("enabled")) {%>	
					<option value="enabled" selected><%=PropertyProvider.get("prm.license.listview.status.option.enabled.name")%></option>
				<% } else {%>	
					<option value="enabled" selecte><%=PropertyProvider.get("prm.license.listview.status.option.enabled.name")%></option>
				<% } %>
				<% if(licenseStatus != null && licenseStatus.equals("all")) {%>
					<option value="all" selected><%=PropertyProvider.get("prm.license.listview.status.option.all.name")%></option>
				<% } else {%>	
					<option value="all"><%=PropertyProvider.get("prm.license.listview.status.option.all.name")%></option>
				<% } %>
				<% if(licenseStatus != null && licenseStatus.equals("disabled")) {%>	
					<option value="disabled" selected><%=PropertyProvider.get("prm.license.listview.status.option.disabled.name")%></option>
				<% } else {%>	
					<option value="disabled"><%=PropertyProvider.get("prm.license.listview.status.option.disabled.name")%></option>
				<% } %>
				<% if(licenseStatus != null && licenseStatus.equals("canceled")) {%>	
					<option value="canceled" selected><%=PropertyProvider.get("prm.license.listview.status.option.cancelled.name")%></option>
				<% } else {%>	
					<option value="canceled"><%=PropertyProvider.get("prm.license.listview.status.option.cancelled.name")%></option>
				<% } %>
				</select>
			</td>
				
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right" ><%=PropertyProvider.get("prm.license.listview.keystartswith.label")%> &nbsp;</td>
			<td  align="left">
			<% if(searchLicenseKey != null) {%>
				<input type="text" name="searchLicenseKey" value='<%= searchLicenseKey%>'>
			<% } else {%>
				<input type="text" name="searchLicenseKey" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td  align="right">
				<a href="javascript:checkFilters('license')"><%=PropertyProvider.get("prm.license.listview.filter.link")%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"> </a>
			</td>
		</tr>
	</table>
	</td>
	<%-- END license attribute based search parameters --%>
	<td>&nbsp;</td> 
	<%-- BEGIN user attribute based search parameters --%>
	<td colspan="6">
	<table width="50%" cellpadding="0" cellspacing="0" border="0">
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right"  ><%=PropertyProvider.get("prm.license.listview.username.label")%>&nbsp; </td>
			<td  align="left">
			<% if(userName != null) {%>
				<input type="text" name="userName" value='<%= userName%>'>
			<% } else {%>
				<input type="text" name="userName" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">
			<td class="fieldNonRequired"  align="right" nowrap><%=PropertyProvider.get("prm.license.listview.firstlastname.label")%>&nbsp; </td>
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
			<td class="fieldNonRequired"  align="right" nowrap><%=PropertyProvider.get("prm.license.listview.emailaddress.label")%>&nbsp; </td>
			<td  align="left">
			<% if(emailID != null) {%>
				<input type="text" name="emailID" value='<%= emailID%>'>
			<% } else {%>
				<input type="text" name="emailID" value="">
			<% } %>	
			</td>
			
		</tr>
		<tr colspan="3">
			<td>&nbsp;</td>
		</tr>
		<tr colspan="3">	
			<td  align="right">
				<a href="javascript:checkFilters('user')"><%=PropertyProvider.get("prm.license.listview.search.link")%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"> </a>
			</td>
		</tr>
	</table>	
	</td>
	</tr>
	
	<tr><td colspan="6">&nbsp;</td></tr>
	
</table>
	<% if(displayLicenses) { %>
        <a href="<%=SessionManager.getJSPRootURL()%>/admin/license/CSVExport.jsp?module=<%=Module.APPLICATION_SPACE%>">
        <img border="0" src="<%=SessionManager.getJSPRootURL()%>/images/appicons/excel.gif">&nbsp;Export</a>
		<channel:channel name='<%="ApplicationSpaceLicensing_" + applicationSpace.getName()%>' customizable="false">
    		<channel:insert name='<%="LicenseDisplay_" + applicationSpace.getName()%>'
                    title='<%=PropertyProvider.get("prm.license.listview.channel.licensesinfo.title")%>' minimizable="false" closeable="false"
					include="/admin/license/include/LicenseListView.jsp" width="97%">
				<channel:button style="action" type="cancel"  href="javascript:cancel();"/>
			</channel:insert>						
		</channel:channel>
		
	<% } %>
</FORM>

<%@ include file="/help/include_outside/footer.jsp" %>	

<template:getSpaceJS />
</body>
</html>
