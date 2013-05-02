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

<%@ page contentType="text/html; charset=UTF-8" info="Material Detail Page" language="java" errorPage="/errors.jsp"
    import="net.project.security.*,
            	net.project.material.MaterialBean,
            	net.project.project.DomainListBean,
            	net.project.base.property.PropertyProvider,
            	net.project.hibernate.service.ServiceFactory,
				net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="materialBean" class="net.project.material.MaterialBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />

<security:verifyAccess action="view" module="<%=net.project.base.Module.MATERIAL%>" objectID="<%=materialBean.getMaterialId()%>" />

<%
	// Initialize Bean
	String id = request.getParameter("id");
	if(id != null)
	{
		materialBean.clear();
		materialBean.setMaterialId(id);
		materialBean.load();
	}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
	var currentSpaceTypeForBlog = 'project';
	var currentSpaceIdForBlog = '<%=SessionManager.getUser().getID()%>';
	
	function setup() {
		theForm = self.document.forms[0];
		isLoaded = true;
	}

	function cancel() {
		self.document.location = JSPRootURL + "/material/Main.jsp?module=<%=net.project.base.Module.MATERIAL%>";
	}
</script>
</head>

<body class="main" bgcolor="#FFFFFF" onLoad="setup();" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />

	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.material.main.title">
		<tb:setAttribute name="leftTitle">
			<history:history />
		</tb:setAttribute>
		<tb:band name="standard">
		</tb:band>
	</tb:toolbar>

	<div id='content'>
		<form name="viewMaterial" method="POST" action="#">
			<table border=0 cellpadding=0 cellspacing=0 width="600">
				<tr>
					<td>
						<div align="center">
							<table border="0" align="left" cellpadding="0" cellspacing="0"
								width="100%">
								<tr align="left" class="channelHeader">
									<td width=1%><img
										src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif"
										width=8 height=15 alt="" border=0></td>
									<td nowrap colspan="4" class="channelHeader">
									<display:get name="prm.material.viewmaterial.channel.view.title" /></td>
									<td width=1% align=right><img
										src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif"
										width=8 height=15 alt="" border=0></td>
								</tr>

								<%-- Material Name --%>
								<tr align="left">
									<td>&nbsp;</td>
									<td nowrap class="fieldRequired">
									<display:get name="prm.material.viewmaterial.materialname.label" />:&nbsp;</td>
									<td nowrap class="tableContent" colspan="2">
									<c:out value="${materialBean.name}" /></td>
									<td nowrap class="tableContent" colspan="2">&nbsp;</td>
								</tr>

								<%-- Material Type --%>
								<tr align="left">
									<td>&nbsp;</td>
									<td nowrap class="fieldRequired">
									<display:get name="prm.material.viewmaterial.materialtype.label" />:&nbsp;</td>
									<td nowrap class="tableContent" colspan="2">
									<c:out value="${materialBean.materialTypeName}" /></td>
									<td nowrap class="tableContent" colspan="2">&nbsp;</td>
								</tr>

								<%-- Material Cost --%>
								<tr align="left" class="addSpacingBottom">
									<td>&nbsp;</td>
									<td nowrap class="fieldNonRequired">
									<display:get name="prm.material.viewmaterial.materialcost.label" />:&nbsp;</td>
									<td nowrap class="tableContent" colspan="2">
									<c:out value="${materialBean.cost}">0.0</c:out></td>
									<td nowrap class="tableContent" colspan="2">&nbsp;</td>
								</tr>

								<%-- Material Consumable --%>
								<tr align="left" class="addSpacingBottom">
									<td>&nbsp;</td>
									<td nowrap class="fieldNonRequired">
									<display:get name="prm.material.viewmaterial.materialconsumable.label" />:&nbsp;</td>
									<td nowrap class="tableContent" colspan="2">
									<input type="checkbox" name="consumable" value='<c:out value="${materialBean.consumable}"/>'
										<%=materialBean.getChecked()%> disabled></td>
									<td nowrap class="tableContent" colspan="2">&nbsp;</td>
								</tr>

								<%-- Material Description --%>
								<tr align="left">
									<td>&nbsp;</td>
									<td nowrap colspan="5" class="fieldNonRequired">
									<display:get name="prm.material.viewmaterial.materialdescription.label" />:&nbsp;<br>
									<c:out value="${materialBean.description}"></c:out></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
			<tb:toolbar style="action" showLabels="true" bottomFixed="true">
				<tb:band name="action">
					<tb:button type="cancel" />
				</tb:band>
			</tb:toolbar>
		</form>
	</div>

	<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>