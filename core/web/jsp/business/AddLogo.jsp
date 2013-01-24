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
    info="Add Document Logo"
    language="java" 
    errorPage="DocumentErrorHandler.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.security.SessionManager"
%>
<%@ page import="net.project.base.Module"%>
<%@ page import="net.project.security.Action"%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="container" class="net.project.document.ContainerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess 
	     module="<%=net.project.base.Module.BUSINESS_SPACE%>"
		 action="modify"
/>
                            

<template:getDoctype />

<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript">

function hasFile () {
	var flag = false;

	if (document.forms[0].file.value)
		flag = true;
	else {
		var errorMessage = '<display:get name="prm.business.addlogo.filerequired.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		flag = false;
	}

	return flag;
}

function mySubmit() {
	
	theAction("check_in");

	if (hasFile())
		self.document.forms[0].submit();
}
	
function theAction (myAction) {

	   self.document.forms[0].theAction.value = myAction;
}

</script>

</head>

<body class="main">

<form name="doc_co" onSubmit="return hasFile();" action="<%=SessionManager.getJSPRootURL()%>/business/BusinessLogoUpload.htm?module=<%=Module.BUSINESS_SPACE%>&action=<%=Action.MODIFY%>" method="post" ENCTYPE="MULTIPART/form-DATA">
  <input TYPE=hidden name="module" value="<%= Module.BUSINESS_SPACE %>">
  <input TYPE=hidden name="action" value="<%= Action.MODIFY %>">
  <input type="hidden" name="theAction">
<table border="0" cellspacing="0" cellpadding="0" align="center" width="97%">
  <tr class="channelHeader">
  	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
  	<td align="center" nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.business.addlogo.channel.businesslogo.title" /></nobr></td>
  	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
  </tr>
    <tr>
      <td nowrap align="left" colspan="4">&nbsp;</td>
    </tr>
    <tr> 
    <td nowrap align="left" >&nbsp;</td>
    <td nowrap align="left" class="tableHeader"><display:get name="prm.business.addlogo.businesslogo.title" /></td>
      <td> 
        <input type="file" name="file">
      </td>
      <td nowrap align="left" >&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap align="left" colspan="4">&nbsp; </td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" function="javascript:parent.opener.focus();self.close();" />
		<tb:button type="submit" label='<%= PropertyProvider.get("prm.business.addlogo.submit.button.label") %>' function="javascript:mySubmit();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

