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

<%@ page contentType="text/html; charset=UTF-8" info="Material List" language="java" errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider,
            net.project.util.NumberFormat,
            net.project.gui.toolbar.Button,
			net.project.gui.toolbar.ButtonType,
            net.project.base.Module,
            net.project.material.MaterialBeanList,
			net.project.hibernate.service.ServiceFactory"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="materialBeanList" class="net.project.material.MaterialBeanList" scope="page" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<% 
		materialBeanList.setSpaceID(user.getCurrentSpace().getID());
		materialBeanList.load();
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS/>

<%-- Import Javascript --%>
<template:getSpaceJS/>

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

	function create (){
		var theLocation = JSPRootURL + "/material/CreateMaterial.jsp?module=<%=Module.MATERIAL%>"+"&action=<%=Action.CREATE%>";
		self.document.location = theLocation;
	}

	function modify(){
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){ 
			var theLocation = JSPRootURL+"/material/ModifyMaterial.jsp?id=" + getSelectedValueLocal() + "&module=<%=Module.MATERIAL%>&action=<%=Action.MODIFY%>";
			self.document.location=theLocation;
		}
	}

	function remove(){
		if(verifySelection(theForm, 'multiple', '<%=PropertyProvider
						.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
			var redirect_url = JSPRootURL + "/material/MaterialDelete.jsp?selected="+ getSelection(theForm) + "&module=<%=Module.MATERIAL%>"+"&action=<%=Action.DELETE%>";
			console.log(redirect_url);		
			var link_win = openwin_linker(redirect_url);
			link_win.focus();
		}	
	}

	function getSelectedValueLocal() {
		var field = theForm.elements["selected"];
		if(!field) {
			field = document.getElementById('channelIFrame').contentWindow.document.getElementById('iFrameForm').elements['selected'];
			var idval = field.value;
			for (var i = 0; i < field.length; i++) {
				if (field[i].checked == true) {
					idval = field[i].value;
					break;
				}
			}
	  			return idval;
		} else {
			return getSelection(theForm);
		}
	}

	function showResourceAllocation(materialID, startDate) {
		var url = '<%=SessionManager.getJSPRootURL()+"/material/MaterialResourceAllocations.jsp?module=260&materialID="%>'+
		    materialID + '&startDate=' + startDate;

		openwin_large('material_resource_allocation', url);
	}

	function reset() { 
		self.document.location = JSPRootURL + "/material/MaterialPortfolio.jsp?module=<%=Module.MATERIAL%>&portfolio=true"; 
	}

	function search() { 
		self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=Module.MATERIAL%>&action=<%=Action.VIEW%>";
	}

	function help() {
			var helplocation = JSPRootURL + "/help/Help.jsp?page=material_main";
			openwin_help(helplocation);
	}

	function refresh(parameters) {
			var refreshLocation = JSPRootURL + "/material/Main.jsp?Module=<%=Module.MATERIAL%>"	+ parameters;
			self.document.location = refreshLocation;
	}

	function popupHelp(page) {
			var helplocation = JSPRootURL + "/help/Help.jsp?page=" + page;
			openwin_help(helplocation);
	}
</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />

	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.material.main.title">
		<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.material.main.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/material/Main.jsp?module=" + Module.MATERIAL + "&action=1"%>' />
		</history:history>
		</tb:setAttribute>
		<tb:band name="standard">
			<tb:button type="create" label='<%= PropertyProvider.get("prm.material.main.create.button.tooltip")%>' />
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.material.main.modify.button.tooltip")%>' />
			<tb:button type="remove" label='<%= PropertyProvider.get("prm.material.main.remove.button.tooltip")%>' />
		</tb:band>
	</tb:toolbar>

	<div id='content'>
		<form name="materialList" action="#" method="POST">
			<input type="hidden" name="theAction">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr align=left class="channelHeader">
					<td width="1%">
						<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0>
					</td>
					<td class="channelHeader">
						<display:get name="prm.material.main.channel.title" />
					</td>
					<td width="1%" align=right>
						<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td class="tableContent">
						<pnet-xml:transform scope="session" stylesheet="/material/xsl/materials-list.xsl" content="<%=materialBeanList.getXML()%>" />
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</form>
	</div>
	<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>