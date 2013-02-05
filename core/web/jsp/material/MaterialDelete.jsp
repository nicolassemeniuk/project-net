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
|    $Revision: 13532 $
|    $Date: 2004-10-22 23:37:37 +0530 (Fri, 22 Oct 2004) $
|
|--------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=UTF-8" info="Material Delete" language="java" errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
            net.project.security.*,
            net.project.base.Module,
            net.project.material.Material,
            net.project.hibernate.service.ServiceFactory"%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<template:getDoctype />

<html>
<head>

<title><%=PropertyProvider.get("prm.material.delete.wizard.step1.title")%></title>

<security:verifyAccess action="delete" module="<%=Module.MATERIAL%>" />
<%
		
	if (request.getParameter("selected") != null && !request.getParameter("selected").trim().equals("")) {
		Material material = new Material();
		material.setMaterialId(request.getParameter("selected"));
		material.load();
		pageContext.setAttribute("material_to_disable" , material , pageContext.SESSION_SCOPE);
	}	
	
%>
<template:getSpaceCSS space="project" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    

function setup(){
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel(){ 
	self.close(); 
}

function finish() {
	if(verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
		theForm.module.value='<%=net.project.base.Module.MATERIAL%>';
		theForm.action.value ='<%=Action.DELETE%>';
		theForm.submit();
	}
}

function help() {
	var helplocation = JSPRootURL + "/help/Help.jsp?page=materials&section=delete";
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" onLoad="setup();" style="background: none;">
	<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.project" showAll="false">
	</tb:toolbar>

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="2" align="right" valign="bottom">&nbsp;</td>
		</tr>
		<tr>
			<td align="left" class="pageTitle">'<%=PropertyProvider.get("prm.project.businessportfoliodelete.pagetitle")%>'
			</td>
			<td align="right" class="pageTitle">'<%=PropertyProvider.get("prm.material.delete.wizard.step1.rightpagetitle")%>'
			</td>
		</tr>
	</table>

	<form method="post" action="MaterialDeleteProcessing.jsp">
		<input type="hidden" name="module"> <input type="hidden" name="action"> <br />
		<table width="97%" cellspacing="0" cellpadding="0" vspace="0" border="0">
			<tr>
				<td colspan="8">
					<table border=0 cellpadding=0 cellspacing=0 width="97%">
						<tr>
							<td valign="top" align="left" width="100%">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr class="channelHeader">
										<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15
											alt="" border=0 hspace=0 vspace=0></td>
										<td nowrap class="channelHeader"><%=PropertyProvider.get("prm.material.delete.wizard.step1.channel.selectoption")%></td>
										<td align="right" class="channelHeader">&nbsp;</td>
										<td align="right" class="channelHeader" width="5%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8
											height=15 alt="" border=0 hspace=0 vspace=0></td>
									</tr>
									<tr valign="top">
										<td class="channelContent">&nbsp;</td>
										<td colspan="4" class="channelContent"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td width="1%"><input type="radio" name="selected" value="disable" id="disablematerial"></td>
				<td class="tableContent"><label for="disablematerial"><%=PropertyProvider.get("prm.material.delete.wizard.step1.option.disablematerial.label")%></label></td>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>

		<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
				<tb:button type="finish" label='<%=PropertyProvider.get("prm.material.delete.wizard.step1.button.finish.label")%>' />
			</tb:band>
		</tb:toolbar>

	</form>
	<br clear=all>
	<%@ include file="/help/include_outside/footer.jsp"%>

	<template:getSpaceJS />
</body>
</html>
