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
    info="Methodology Properties Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.Action,
			net.project.base.Module,             
            net.project.security.SessionManager,
            net.project.util.Conversion,
            net.project.space.SpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%	
	// added for new Save as Template dialog
	// Fix for eliminating Schedule/Process checkbox when creating template from Business Space
    boolean isIncludeSchedule = true;
    boolean isIncludeProcess = true;
    String currentSpaceType = SpaceTypes.PROJECT_SPACE;
    if (SpaceTypes.BUSINESS.equals(SpaceTypes.getForID(currentSpaceType))) {
            isIncludeSchedule = false;
            isIncludeProcess = false;
    }
%>


<%-- Import CSS --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<c:set var="m" value="${model}"/>
<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	load_menu('<c:out value="${m.userCurrentSpaceId}"/>');
	theForm = self.document.forms["main"];
	isLoaded = true;

	if (<%=PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")%>) {
   		setIsGlobal ('<c:out value="${m.methodologySpaceIsGlobal}"/>');
	}

	Ext.get('mainForm').on('submit', submit);			// register form submit handler
	Ext.get('useScenario').on('keyup', ismaxlength);	// limit size of use scenario filed
}

function help(){
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_propertiesedit";
	openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/methodology/Main.htm"; }

function reset() { theForm.reset(); }

function submit(e) {
	theAction("submit");
	// Fix for bfd-5107
	if(processForm(theForm)) {
		theForm.submit();
		return true;
	}
	e.preventDefault();
}

function processForm(myForm)
 {
 	theAction('submit');
 	
 	if(!checkTextbox(myForm.methodologySpaceName,"<%=PropertyProvider.get("prm.template.create.name.required.message")%>")) return false;
// 	if(!checkCheckBox_NoSelect(myForm.selectedModules, "<%=PropertyProvider.get("prm.template.templifyspace.includedmodulesrequired.message")%>")) return false;
	return true;
 }

// TODO: REMOVE THIS METHOD 
// hide/unhide global checkbox if personal workspace is selected
function checkWorkspaceSelection(selectedValue){
	var userId = '<%=user.getID()%>';
	if(selectedValue == userId ){
		Ext.get('mainForm').dom.isGlobal.checked = false;
		Ext.select('#globalCheckbox').hide(true);
	} else {
		Ext.select('#globalCheckbox').show(true);
	}
}

<%-- Industry not supported yet (Methodlogy writes industry, but does not read it)
function submitIndustry() {
   theAction("submitIndustry");
   theForm.submit();
}
--%>
function setIsGlobal(isGlobal) {
    if (isGlobal == 'true') {
        theForm.isGlobal.checked = true;
    } else {
        theForm.isGlobal.checked = false;
    }
}

function ismaxlength(e) {
	var obj = e.target;
	var mlength=obj.getAttribute("maxlength") ? parseInt(obj.getAttribute("maxlength")) : "";
	if (obj.getAttribute("maxlength") && obj.value.length>mlength) {
		obj.value=obj.value.substring(0,mlength);
	}
}
</script>

<style type="text/css">
	.business-setup-header, .projects-setup-header { float: none; }
	.required { font-weight: bold; color: #000000; }
	.non-required { color: #AAAAAA; }
	form p { margin: 15px;}
	
	div.left { width: 165px; float: left; }
	div.right { width: 300px; display: inline; }
	div.left span { margin-top: 50px; display: block; }
	div.right span { margin-left: 5px; }
	div.formFooter { background: url(/images/table-calendar-bg.png) #F3F3FA bottom repeat-x; width: 93%; padding: 8px; text-align: center; border-top: 2px solid #D5D5D5; border-left: 1px solid #D5D5D5; border-right: 1px solid #D5D5D5; }
	div.formFooter input { width: 100px; }
</style>

</head>
<%
	String id = (String)((java.util.Map)request.getAttribute("model")).get("methodologySpaceId");
%>
<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.template.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.template.modify.title")%>'
							jspPage='<%=SessionManager.getJSPRootURL() + "/methodology/PropertiesEdit.htm"%>'
							queryString='<%="module=" + net.project.base.Module.METHODOLOGY_SPACE + "&action=" + net.project.security.Action.MODIFY + "&id="+ id%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard"></tb:band>
</tb:toolbar>

<div id='content'>

	<form id="mainForm" name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/methodology/PropertiesEditProcessing.htm">
	    <input type="hidden" name="module" value="<%=Module.METHODOLOGY_SPACE%>">
		<input type="hidden" name="action" value="<%=Action.MODIFY%>">
		<input type="hidden" name="theAction" />
		
		<%if(user.getCurrentSpace().getType().equals(SpaceTypes.BUSINESS_SPACE)){ %>
			<div class="business-setup-header"> <display:get name="prm.template.main.channel.edit.title" /> </div>
		<%} else {%>
			<div class="projects-setup-header"> <display:get name="prm.template.main.channel.edit.title" /> </div>
		<%}%>
				
		<p>
			<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
			<%-- Provide a div for server round-trip error messaging --%>
			<div id="errorLocationID" class="errorMessage"></div>
		</p>
		<p>
			<div class="left required"><display:get name="prm.template.modify.name" />:&nbsp;</div> 
			<div class="right"><input name="methodologySpaceName" id="methodologySpaceName" size="45" maxlength="80" VALUE="<c:out value="${m.methodologySpaceName}"/>"></div>
	    </p>
		<p>
		    <div class="left required"><display:get name="prm.template.modify.owner" />:&nbsp;</div> 
			<div class="right">
				<select name="parentSpaceID"  onchange="checkWorkspaceSelection(this.value);">
		          <!-- <option value="<c:out value="${m.userId}"/>"><%=PropertyProvider.get("prm.template.createwizard.owner.option.personal.name")%></option>  -->
		          <c:out value="${m.availableBusinessOptionList}" escapeXml="false"/>          
		        </select>
			</div>
		</p>
		<%-- Global visibility for templates means everyone in the system can see the template. --%>
		<% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
			<p id="globalCheckbox">
				<div class="left non-required"><display:get name="prm.methodology.visibleto.label" />:</div>
				<div class="right"><input type="checkbox" name="isGlobal" id="isGlobal"/> <span><display:get name="prm.methodology.allusers.label" /></span></div>
		    </p>
		<% } %>
		<p>
			<div class="left non-required"><display:get name="prm.template.modify.description" />:&nbsp;</div> 
			<div class="right"><input type="text" name="methodologySpaceDescription" maxlength="1000" size="45" VALUE="<c:out value="${m.methodologySpaceDescription}"/>"></div>
	    </p> 
		<p>
			<div class="left non-required"><span><display:get name="prm.template.modify.usescenario" />:</span></div>
		    <div class="right"><textarea id="useScenario" name="useScenario" cols="60" rows="7" maxlength="500"><c:out value="${m.methodologySpaceUseScenario}"/></textarea></div>
		</p>
		
		<%-- Methodology Modules --%>
<!-- 
		<p id="modulesIncluded">
			<div class="left required"><span>Included Modules:<span></div>
			<div class="right">

				<ul>
				<c:forEach items="${cbModules}" var="sm">
					<li>
				        <input type="checkbox" name="selectedModules" value='<c:out value="${sm.module.moduleId}" />' <c:if test="${sm.selected}">checked="checked"</c:if> /> 
				        <span><c:out value="${sm.displayName}" /></span>
				    </li>
				</c:forEach>
				</ul>
		 
			</div>
		</p>
-->
		<p>
			<div class="formFooter"><input type="submit" value="Submit" ></div>
		</p>
	
	</form>

</div> <!-- #content -->
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
