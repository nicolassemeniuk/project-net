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

<html>
<head>
<META http-equiv="expires" content="0">
<title>Business Delete -- Step 1</title>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Application Space Business Portfolio"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.BusinessPortfolioBean,
            net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.User,net.project.business.BusinessSpace,
            net.project.security.SessionManager,net.project.business.BusinessDeleteWizard,
            net.project.space.Space,net.project.security.*,
            net.project.space.PersonalSpaceBean" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="businessPortfolio" class="net.project.portfolio.BusinessPortfolioBean" scope="request" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="businessDeleteWizard" class="net.project.business.BusinessDeleteWizard" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%
businessDeleteWizard.clear();
businessDeleteWizard.setBusinessID(request.getParameter("selected"));
%>
<%--  rig the javascript which will react appropriately to the userz actions, including sending the Form--%> 
<template:getSpaceCSS/>

<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel() { self.close(); }

function next() {
	if(verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		theForm.module.value='<%=net.project.base.Module.APPLICATION_SPACE%>';
		theForm.action.value ='<%=Action.MODIFY%>';
		theForm.submit();
	} 
}

function reset() {
	theForm.reset();
}

function loadEventTypes() {
   theAction("loadEventTypes");
   theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_business_delete";
	openwin_help(helplocation);
}


</script>
</head>
 
<body class="main" onLoad="setup();">
<tb:toolbar style="tooltitle" showAll="false" leftTitle=" Business Delete Wizard" rightTitle="Step 1   " groupTitle="prm.business.businessportfolio.business.label">
</tb:toolbar>
<form method="post" action="BusinessDeleteProcessing.jsp">
    <input type="hidden" name="theAction">
	<input type="hidden" name="module">
	<input type="hidden" name="action">
	<%-- this is probably gone now --%> 
	<%-- this is probably gone now --%> 
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>
<table width="97%" cellspacing="0" cellpadding="0" vspace="0"  border="0">
 <tr>
<td colspan="8"> 
    <table border=0 cellpadding=0 cellspacing=0 width="97%"><tr>
<td valign="top" align="left" width="100%">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
<td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
<td nowrap class="channelHeader">Select one of the following</td>
<td align="right" class="channelHeader">&nbsp;</td>
<td align="right" class="channelHeader" width="5%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
</tr>
<tr valign="top">
<td class="channelContent">&nbsp;</td>
<td colspan="4" class="channelContent">
</td>
</tr>
</table>
</td>
</tr></table>
</td>
</tr>
    <tr>
<td width="1%"><input type="Radio" name="selected" value="move"></td>
<td class="tableContent"><%=PropertyProvider.get("prm.project.admin.workspace.move.project.label") %></td>
</td>
</tr>
<tr>
<td width="1%"><input type="Radio" name="selected" value="delete"></td>
<td class="tableContent"><%=PropertyProvider.get("prm.project.admin.workspace.delete.business.label") %></td>
</td>
</tr> 
<tr>
  	<td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="next" label="Next" />
		</tb:band>
	</tb:toolbar>

  </form>
<%--
<%=toolbar.drawActionbar()%>
--%>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
