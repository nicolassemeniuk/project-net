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

<%@page import="net.project.hibernate.model.PnMaterialType"%>
<%@ page contentType="text/html; charset=UTF-8" info="Material List" language="java" errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider,
            net.project.util.NumberFormat,
            net.project.gui.toolbar.Button,
			net.project.gui.toolbar.ButtonType,
            net.project.base.Module,
            net.project.material.MaterialBeanList,
            net.project.project.DomainListBean,            
			net.project.hibernate.service.ServiceFactory,
			net.project.space.Space,
			net.project.project.ProjectSpace,
			net.project.business.BusinessSpace,
			net.project.material.PnMaterialTypeList"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="materialBeanList" class="net.project.material.MaterialBeanList" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />

<%
	String mode = request.getParameter("mode");
	String currentTab = request.getParameter("currentTab");
	
	if(currentTab == null)
		currentTab = "project";
	session.setAttribute("currentTab", currentTab);	
	
	Space currentSpace = user.getCurrentSpace();
	request.setAttribute("currentSpace", currentSpace);

	String parentBusinessID = null;	
	if(currentSpace instanceof ProjectSpace)
	{
		ProjectSpace projectSpace = (ProjectSpace) currentSpace;		
		parentBusinessID = projectSpace.getParentBusinessID();
		request.setAttribute("parentBusinessID", parentBusinessID);			
	}
	
	// Generates the material types list with the "Any" element
	PnMaterialTypeList materialTypes = new PnMaterialTypeList();
	PnMaterialType anyMaterial = new PnMaterialType();
	anyMaterial.setMaterialTypeId(0);
	anyMaterial.setMaterialTypeName("Any");
	materialTypes.add(anyMaterial);
	materialTypes.addAll(ServiceFactory.getInstance().getPnMaterialTypeService().getMaterialTypes());
	
	// Don't refresh the materialBeanList if we are returning search results, it was previously loaded
	if(mode == null)
	{
		request.setAttribute("searchKey", "");
		request.setAttribute("materialTypeId", "0");
		request.setAttribute("consumable", "off");
		request.setAttribute("minCost", "");
		request.setAttribute("maxCost", "");			
		materialBeanList.clear();
		
		// Showing the materials of the business in the business workspace
		if(currentSpace instanceof BusinessSpace)
			materialBeanList.setSpaceID(currentSpace.getID());
		// Showing the materials of the project in the project workspace
		else if(currentSpace instanceof ProjectSpace)
		{
			// Showing the materials of the business in the project workspace
			if(currentTab.equals("business"))
				materialBeanList.setSpaceID(parentBusinessID);						
			// Showing the materials of the project in the project workspace
			else if(currentTab.equals("project"))
				 	materialBeanList.setSpaceID(currentSpace.getID());
		}
		
		materialBeanList.load();
	}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS/>
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

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

	function search(key)
	{
		theForm.key.value = key;
		theForm.action.value = '<%=Action.VIEW%>';
		searchButton();			
	}
	
	function searchButton()
	{
		if(validate())
		{		
			theAction("search");
			theForm.action.value = '<%=Action.VIEW%>';
			theForm.submit();
		}
	}
	
	function validate()
	{
		if(!checkIsPositiveNumber(theForm.minCost,'<display:get name="prm.material.main.material.mincostamountincorrect.message" />', true))
			return false;
		
		if(!checkIsPositiveNumber(theForm.maxCost,'<display:get name="prm.material.main.material.maxcostamountincorrect.message" />', true))
			return false;
		
		if(theForm.minCost.value != "" && theForm.maxCost.value != "" && theForm.minCost.value >= theForm.maxCost.value)
		{
			errorHandler(theForm.minCost, '<display:get name="prm.material.main.material.costincorrectrange.message" />');
			return false;			
		}
	
		return true;
	}	
	
	function help() {
			var helplocation = JSPRootURL + "/help/Help.jsp?page=material_main";
			openwin_help(helplocation);
	}

	function refresh(parameters) {
			var refreshLocation = JSPRootURL + "/material/MaterialDirectory.jsp?Module=<%=Module.MATERIAL%>"	+ parameters;
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
							jspPage='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&action=1"%>' />
		</history:history>
		</tb:setAttribute>
		<tb:band name="standard">
			<c:choose>
				<c:when test="${currentSpace.spaceType.ID eq 'project' && currentTab eq 'project' || currentSpace.spaceType.ID eq 'business'}">
					<tb:button type="create" label='<%= PropertyProvider.get("prm.material.main.create.button.tooltip")%>' />
					<tb:button type="modify" label='<%= PropertyProvider.get("prm.material.main.modify.button.tooltip")%>' />
					<tb:button type="remove" label='<%= PropertyProvider.get("prm.material.main.remove.button.tooltip")%>' />
				</c:when>			
			</c:choose>
		</tb:band>			
	</tb:toolbar>

	<div id='content' style="padding-top:20px;width:75%">
		<tab:tabStrip tabPresentation="true">		
			<c:choose>
				<%--Showing the materials of the business in the business workspace --%> 
				<c:when test="${currentSpace.spaceType.ID eq 'business'}">
					<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.businessmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL%>' selected="true" />
				</c:when>
				<%-- Showing the materials of the project in the project workspace --%>						
				<c:when test="${currentSpace.spaceType.ID eq 'project'}">
					<c:choose>
						<%-- Showing the materials of the project in the project workspace, the project doesn't have an owner --%>
						<c:when test="${empty parentBusinessID}">
							<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.projectmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&currentTab=project" %>' selected="true" />									
						</c:when>
						<c:otherwise>
							<c:choose>
								<%--Showing the materials of the project in the project workspace --%>
								<c:when test="${currentTab eq 'project'}">
									<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.projectmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&currentTab=project" %>' selected="true" />
									<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.businessmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&currentTab=business"%>' />						
								</c:when>
								<%-- Showing the materials of the business in the project workspace --%>
								<c:when test="${currentTab eq 'business'}">
									<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.projectmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&currentTab=project" %>' />
									<tab:tab label='<%=PropertyProvider.get("prm.material.main.material.tab.businessmaterials.title")%>' href='<%=SessionManager.getJSPRootURL() + "/material/MaterialDirectory.jsp?module=" + Module.MATERIAL + "&currentTab=business"%>' selected="true" />						
								</c:when>
							</c:choose>		
						</c:otherwise>
					</c:choose>											
				</c:when>
			</c:choose>		
		</tab:tabStrip>
	
		<div class="UMTableBorder marginLeftFix">
			<form method="post" action="<%=SessionManager.getJSPRootURL()%>/material/MaterialDirectoryProcessing.jsp">
				<input type="hidden" name="theAction">
				<input type="hidden" name="module" value="<%=Module.MATERIAL%>">
		    	<input type="hidden" name="action" value="<%=Action.VIEW%>">
		    	
				<label for="searchField" class="labelSearchField"><%=PropertyProvider.get("prm.material.main.roster.namesearch.label")%></label>
				<input type="text" name="key" id="searchField" value="<%=request.getAttribute("searchKey")%>" size="40" maxlength="40" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchField">
		    	
				<label for="materialTypeId" class="labelSearchField"><%=PropertyProvider.get("prm.material.main.roster.typesearch.label")%></label>		    	
				<select name="materialTypeId" id="materialTypeId" class="inputSearchField">
					<%=domainList.getMaterialTypeListForMaterialModification(materialTypes, (String) request.getAttribute("materialTypeId"))%>
				</select>		    	
		    	
				<label for="consumable" class="labelSearchField"><%=PropertyProvider.get("prm.material.main.roster.consumablesearch.label")%></label>
		    	<select name="consumable" id="consumable" class="inputSearchField">
		    		<option value="" <%=(request.getAttribute("consumable") == null ? "selected" : "")%>></option>
		    		<option value="true" <%=(request.getAttribute("consumable").equals("true") ? "selected" : "")%> ><%=PropertyProvider.get("prm.material.main.roster.consumablesearch.yesoption")%></option>
		    		<option value="false"<%=(request.getAttribute("consumable").equals("false") ? "selected" : "")%> ><%=PropertyProvider.get("prm.material.main.roster.consumablesearch.nooption")%></option>		    		
		    	</select>

				<label for="minCost" class="labelSearchField"><%=PropertyProvider.get("prm.material.main.roster.costrangesearch.label")%></label>			    	
		    	<input type="number" name="minCost" id="minCost" maxlength="14" value="<%=request.getAttribute("minCost")%>" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchFieldCost"/>
		    	<span>-</span>
		    	<input type="number" name="maxCost" id="maxCost" maxlength="14" value="<%=request.getAttribute("maxCost")%>" onKeyDown="if(event.keyCode==13) searchButton()" class="inputSearchFieldCost"/>		    	
		    	
				<div class="channelHeader channelHeaderTabSet">
					<p><%=PropertyProvider.get("prm.material.main.channel.title")%></p>
				</div>
				<div>	    
					<pnet-xml:transform scope="session" stylesheet="/material/xsl/materials-list.xsl" content="<%=materialBeanList.getXML()%>" />
				</div>
			</form>
		</div>
	</div>
	<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>