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

<%@ page contentType="text/html; charset=UTF-8" info="New Material Wizard" language="java" errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
            net.project.material.MaterialBean,
            net.project.methodology.MethodologyProvider,
            net.project.project.DomainListBean,
            net.project.security.*, 
            net.project.space.Space,
            net.project.space.SpaceRelationship,
            net.project.util.JSPUtils,
            net.project.hibernate.service.ServiceFactory"%>

<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="materialBean" class="net.project.material.MaterialBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" />


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:getSpaceJS space="project" />
<script language="javascript">

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
	var currentSpaceTypeForBlog = 'project';
    var currentSpaceIdForBlog = '<%=SessionManager.getUser().getID()%>';
    
	function setup() {
		load_menu('<%=user.getCurrentSpace().getID()%>');
	
		theForm = self.document.forms[0];
		isLoaded = true;
		if(theForm.parentSpaceID.value != ''){
			theForm.parentSpaceID.disabled = true;
		}
	}

	function cancel() {
		// TODO Aqui en un futuro se podria retornar al material padre
		self.document.location = JSPRootURL + "/material/Main.jsp?module=<%=net.project.base.Module.MATERIAL%>";
	}

	function submit() {
		if (validate()) {
			theAction("submit");
			theForm.submit();
		}
	}

	function reset() {
		theForm.reset();
	}

	function help() {
		var helplocation = JSPRootURL + "/help/Help.jsp?page=material_create";
		openwin_help(helplocation);
	}

	function validate() {
		if (!checkTextbox(theForm.name,'<display:get name="prm.material.create.wizard.step1.materialnamerequired.message" />')) {
			return false;
		}
		if (!checkMaxLength(theForm.description,200,'<display:get name="prm.material.create.wizard.step1.materialdescriptionlength.message"/>')){
			return false;
		}
		return true;
	}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />

	<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.material" showAll="true"
		leftTitle='<%=PropertyProvider.get("prm.material.create.wizard.step1.lefttitle.label")%>'
		rightTitle='<%=PropertyProvider.get("prm.material.create.wizard.step1.righttitle.label")%>' space="material">
		<tb:band name="standard">
		</tb:band>
	</tb:toolbar>

	<div id='content'>

	<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------
	--  Configure beans                                                                                                                                          
	----------------------------------------------------------------------------------------------------------------------------------------------------------------%>
		<%
			materialBean.clear();
		%>

		<form method="post" action="CreateMaterialProcessing.jsp" name="createMaterial">
			<input type="hidden" name="theAction"> <input type="hidden" name="module" value="<%=securityProvider.getCheckedModuleID()%>">
			<input type="hidden" name="spaceID" value='<%=user.getCurrentSpace().getID()%>' />
			<table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
				<tr align="left" class="channelHeader">
					<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
					<td nowrap colspan="4" class="channelHeader"><display:get name="prm.material.create.wizard.step1.channel.generalinformation.title" /></td>
					<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
				</tr>

				<tr align="left" class="tableContent">
					<td nowrap colspan="6" class="tableContent">&nbsp;</td>
				</tr>

				<%-- Material Name --%>
				<tr align="left" class="addSpacingBottom">
					<td>&nbsp;</td>
					<td nowrap class="fieldRequired" width="20%"><display:get name="prm.material.create.wizard.step1.name.label" />:&nbsp;</td>
					<td nowrap class="tableContent" colspan="2"><input type="text" name="name" size="40" maxlength="80" value='<c:out value="${materialBean.name}"/>'>
					<td nowrap class="tableContent" colspan="2">&nbsp;</td>
				</tr>

				<%-- Material Type --%>
				<tr align="left" class="addSpacingBottom">
					<td>&nbsp;</td>
					<td nowrap class="fieldRequired"><display:get name="prm.material.create.wizard.step1.type.label" />:&nbsp;</td>
					<td class="tableContent" colspan="2"><select name="materialTypeId">
						<%=domainList.getMaterialTypeListForMaterialCreation(ServiceFactory.getInstance().getPnMaterialTypeService().getMaterialTypes())%>
					</select></td>
					<td nowrap class="tableContent" colspan="2">&nbsp;</td>
				</tr>

				<%-- Material Cost --%>
				<tr align="left" class="addSpacingBottom">
					<td>&nbsp;</td>
					<td nowrap class="fieldNonRequired"><display:get name="prm.material.create.wizard.step1.cost.label" />:&nbsp;</td>
					<td nowrap class="tableContent" colspan="2"><input type="number" name="cost" size="40" maxlength="80"
						value='<c:out value="${materialBean.cost}">0.0</c:out>'></td>
					<td nowrap class="tableContent" colspan="2">&nbsp;</td>
				</tr>
				
				<%-- Material Consumable --%>
				<tr align="left" class="addSpacingBottom">
					<td>&nbsp;</td>
					<td nowrap class="fieldNonRequired"><display:get name="prm.material.create.wizard.step1.consumable.label" />:&nbsp;</td>
					<td nowrap class="tableContent" colspan="2">					
					<input type="checkbox" name="consumable"></td>
					<td nowrap class="tableContent" colspan="2">&nbsp;</td>
				</tr>

				<%-- Material Description --%>
				<tr align="left" class="tableContent">
					<td nowrap colspan="6" class="tableContent">&nbsp;</td>
				</tr>
				<tr align="left">
					<td>&nbsp;</td>
					<td nowrap colspan="5" class="fieldNonRequired"><display:get name="prm.material.create.wizard.step1.description.label" />:&nbsp;<br> 
						<textarea name="description" cols="50" rows="3" wrap="virtual">
								<c:out value="${materialBean.description}"></c:out>
						</textarea>
					</td>
				</tr>
				

				


				<tr align="left" class="tableContent">
					<td nowrap colspan="6" class="tableContent">&nbsp;</td>
				</tr>

			</table>

			<br clear="all">

	<%-----------------------------------------------------------------------------------------------------------
    -- Action Bar                                                                                         
	--------------------------------------------------------------------------------------------------------%>
			<tb:toolbar style="action" showLabels="true" bottomFixed="true">
				<tb:band name="action">
					<tb:button type="cancel" />
					<tb:button type="finish" function="javascript:submit();" />
				</tb:band>
			</tb:toolbar>

		</form>

		<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>
