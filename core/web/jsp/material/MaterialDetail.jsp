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

<%@ page contentType="text/html; charset=UTF-8" info="Material Modify Page" language="java" errorPage="/errors.jsp"
    import="net.project.security.*,
    			net.project.base.Module,
            	net.project.material.MaterialBean,
            	net.project.project.DomainListBean,
            	net.project.base.property.PropertyProvider,
            	net.project.hibernate.service.ServiceFactory"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="materialBean" class="net.project.material.MaterialBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />
<%
String id = request.getParameter("id");
if (id != null){
	materialBean.clear();
	materialBean.setMaterialId(id);
	materialBean.load();
}

%>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.MATERIAL%>"
					   objectID="<%=materialBean.getMaterialId()%>" /> 
					   
<template:getDoctype />

<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkUrl.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

	function setup() {
		theForm = self.document.mainForm;
		isLoaded = true;
	}

	function cancel() {
		var theLocation = JSPRootURL + "/material/Main.jsp";
		self.document.location = theLocation;
	}

	function reset() {
		theForm.reset();
	}

	function submit() {
		if (validate(document.mainForm)) {
			theAction("submit");
			theForm.submit();
		}
	}

	function isValidDigit(num) {
		if ((num != null) && (num != "")) {
			var digits = "0123456789-. ";
			for ( var i = 0; i < num.length; i++) {
				if (digits.indexOf(num.charAt(i)) == -1) {
					return false;
				}
			}
		}
		return true;
	}

	function validate(frm) {
		if (!checkTextbox(frm.name,	'<display:get name="prm.material.viewmaterial.namerequired.message"/>'))
			return false;
		if (!checkMaxLength(frm.description,200,'<display:get name="prm.material.viewmaterial.descriptionlength.message" />'))
			return false;
		return true;
	}
</script>

</head>

<body class="main" bgcolor="#FFFFFF" onLoad="setup();" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />

	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.space.material">
		<tb:setAttribute name="leftTitle">
			<history:history>
<%-- 			            <history:module display='<%=materialSpace.getName()%>' --%>
<%--                             jspPage='<%=SessionManager.getJSPRootURL() + "/material/MaterialPortfolio.jsp"%>' --%>
<%--                             queryString='<%="module=" + Module.MATERIAL_SPACE%> + "&portfolio=true" ' /> --%>
			
			</history:history>
		</tb:setAttribute>
		<tb:band name="standard">
			<!--tb:button type="security" /-->
		</tb:band>
	</tb:toolbar>

	<div id='content'>

		<form name="mainForm" method="POST" action="ModifyMaterialProcessing.jsp">
			<input type="hidden" name="theAction"> 
			<input type="hidden" name="action" value="<%=Action.MODIFY%>" /> 
			<input type="hidden" name="module" value="<%=Module.MATERIAL%>" />
			<input type="hidden" name="materialId" value="<%=materialBean.getMaterialId()%>" />
			

								<table border=0 cellpadding=0 cellspacing=0 width="600">
									<tr>
										<td>
											<div align="center">
												<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
													<tr align="left" class="channelHeader">
														<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
														<td nowrap colspan="4" class="channelHeader"><display:get name="prm.material.modifymaterial.channel.modify.title" /></td>
														<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
													</tr>

													<tr align="left">
														<td>&nbsp;</td>
														<td nowrap class="fieldRequired"><display:get name="prm.material.viewmaterial.materialname.label" />:&nbsp;</td>
														<td nowrap class="tableContent" colspan="2">															
															<c:out value="${materialBean.name}" />
														</td>
														<td nowrap class="tableContent" colspan="2">&nbsp;</td>
													</tr>
													
													<tr align="left">
														<td>&nbsp;</td>
														<td nowrap class="fieldRequired"><display:get name="prm.material.viewmaterial.materialtype.label" />:&nbsp;</td>
														<td nowrap class="tableContent" colspan="2">															
																<c:out value="${materialBean.materialTypeName}" />
															</td>
														<td nowrap class="tableContent" colspan="2">&nbsp;</td>
													</tr>
													
													<tr align="left" class="addSpacingBottom">
														<td>&nbsp;</td>
														<td nowrap class="fieldNonRequired"><display:get name="prm.material.viewmaterial.materialcost.label" />:&nbsp;</td>
														<td nowrap class="tableContent" colspan="2">
															<c:out value="${materialBean.cost}">0.0</c:out>
														</td>
														<td nowrap class="tableContent" colspan="2">&nbsp;</td>
													</tr>
													
													<%-- Material Consumable --%>
													<tr align="left" class="addSpacingBottom">
														<td>&nbsp;</td>
														<td nowrap class="fieldNonRequired"><display:get name="prm.material.viewmaterial.materialconsumable.label" />:&nbsp;</td>
														<td nowrap class="tableContent" colspan="2">					
														<input type="checkbox" name="consumable" value='<c:out value="${materialBean.consumable}"/>'  <%=materialBean.getChecked()%> disabled></td>
														<td nowrap class="tableContent" colspan="2">&nbsp;</td>
													</tr>

													
													<tr align="left">
														<td>&nbsp;</td>
														<td nowrap colspan="5" class="fieldNonRequired"><display:get name="prm.material.viewmaterial.materialdescription.label" />:&nbsp;<br> 
																	<c:out value="${materialBean.description}"></c:out>
														</td>
													</tr>

													<tr class="tableContent">
														<td nowrap colspan="6" class="tableContent">&nbsp;</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
								</table>



								<tb:toolbar style="action" showLabels="true" bottomFixed="true">
								</tb:toolbar>
		</form>
	</div>

	<%@ include file="/help/include_outside/footer.jsp"%>

	<template:getSpaceJS />
</body>
</html>