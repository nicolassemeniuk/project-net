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
|   $Revision: 20564 $
|       $Date: 2010-03-12 12:50:10 -0300 (vie, 12 mar 2010) $
|     $Author: uroslates $
|
|   Entry point for methodology space
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Methodology Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*,
    net.project.document.DocumentManagerBean,
    net.project.util.JSPUtils,
    net.project.base.property.PropertyProvider,
    net.project.security.SessionManager,
    net.project.space.SpaceTypes" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />

<template:getDoctype />
<html>
	<head>
		<META http-equiv="expires" content="0">
		<title><display:get name="prm.global.application.title" /></title>
		
		<%-- Import CSS --%>
		<template:getSpaceCSS />
		<style type="text/css">
			.business-setup-header, .projects-setup-header { float: none; }
			.required { font-weight: bold; color: #000000; }
			.non-required { color: #AAAAAA; }
			#templateDetails p { margin: 15px;}
			
			div.left { width: 165px; float: left; }
			div.right { width: 300px; display: inline; }
			div.left span { margin-top: 50px; display: block; }
			div.right span { margin-left: 5px; }
			#useScenario {  }
		</style>
		
		<c:set var="m" value="${model}"/>
		<%
			 String name = "", id = "";
			if(request.getAttribute("model") != null){
				name = (String)((java.util.Map)request.getAttribute("model")).get("methodologySpaceName");
				id = (String)((java.util.Map)request.getAttribute("model")).get("methodologySpaceId");
			}
		%>
		<script language="javascript">
		    var theForm;
		    var isLoaded = false;
		    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
		
			function setup() {
			   load_menu('<c:out value="${m.userCurrentSpaceId}"/>');
			   load_header();
			   theForm = self.document.forms[0];
			   isLoaded = true;
			}
			
			function help(){
				var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_summary";
				openwin_help(helplocation);
			}
			
			function cancel() { self.document.location = JSPRootURL + "/methodology/MethodologyList.htm"; }
			
			function reset() { self.document.location = JSPRootURL + "/methodology/Main.htm"; }
			
			//function modify() { self.document.location = JSPRootURL + "/methodology/PropertiesEdit.htm"; }
			function modify() { self.document.location = JSPRootURL + "/methodology/PropertiesEdit.htm?id="+<%=id%>; }
			
		</script>
	</head>
	
	<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
		<template:getSpaceMainMenu />
		<template:getSpaceNavBar />
		
		<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.template.name">
			<tb:setAttribute name="leftTitle">
				<history:history>	 
					<history:page display='<%=name%>'
									 jspPage='<%=SessionManager.getJSPRootURL() + "/methodology/Main.htm"%>'
									 queryString='<%="module=" + net.project.base.Module.METHODOLOGY_SPACE + "&id="+ id%>' />			 
				</history:history>
			</tb:setAttribute>
			<tb:band name="standard">
				<tb:button type="modify" label='<%= PropertyProvider.get("prm.template.main.edit.button.tooltip")%>' />
			</tb:band>
		</tb:toolbar>
	
		<div id='content'>
	 
			<form method="post" action="<%=SessionManager.getJSPRootURL()%>/methodology/MainProcessing.htm">
				<input type="hidden" name="theAction">
			</form>
	
			<div id="templateDetails">
				<%if(user.getCurrentSpace().getType().equals(SpaceTypes.BUSINESS_SPACE)){ %>
					<div class="business-setup-header"> <display:get name="prm.template.main.channel.properties.title" /></div>
				<%} else {%>
					<div class="projects-setup-header"> <display:get name="prm.template.main.channel.properties.title" /></div>
				<%}%>
				<p>
					<div class="left non-required"><display:get name="prm.template.create.templatename" />:</div> 
					<div class="right"> <div id="name"><c:out value="${m.methodologySpaceName}"/></div> </div>
			    </p> 
				<p>
					<div class="left non-required"><display:get name="prm.template.templifyspace.templatetype" />:</div> 
					<div class="right"> 
						<div id="templatetype" >
							<c:choose>
								<c:when test="${empty m.methodologyBasedOnSpaceType}">&nbsp;</c:when>
								<c:otherwise><c:out value="${m.methodologyBasedOnSpaceType}"/></c:otherwise>
							</c:choose>
						</div> 
					</div>
				</p>
				<p>
					<div class="left non-required"><display:get name="prm.template.main.owner" />:</div> 
					<div class="right"> <div id="templateOwner" ><c:out value="${m.methodologySpaceParentSpaceName}"/></div> </div>
			    </p>
				<p id="globalCheckbox">
					<div class="left non-required"><display:get name="prm.methodology.visibleto.label" />:</div>
					<div class="right"><input type="checkbox" name="isGlobal" id="isGlobal" <c:if test="${m.methodologySpaceIsGlobal}">checked="checked"</c:if> DISABLED/><span><display:get name="prm.methodology.allusers.label" /></span></div>
				</p>
				<p>
					<div class="left non-required"><display:get name="prm.template.create.description" />:</div> 
					<div class="right"> 
						<div id="description" >
							<c:choose>
								<c:when test="${empty m.methodologySpaceDescription}">&nbsp;</c:when>
								<c:otherwise><c:out value="${m.methodologySpaceDescription}"/></c:otherwise>
							</c:choose>
						</div> 
					</div>
			    </p>
				<p>
					<div class="left non-required"><display:get name="prm.template.create.usescenario" />:</div>
				    <div class="right"> 
				    	<div id="useScenario">
							<c:choose>
								<c:when test="${empty m.methodologySpaceUseScenario}">&nbsp;</c:when>
								<c:otherwise><c:out value="${m.methodologySpaceUseScenario}"/></c:otherwise>
							</c:choose>
				    	</div> 
				    </div>
				</p>
				<p id="modulesIncluded">
					<div class="left non-required"><span><display:get name="prm.template.create.includedmodules" />:<span></div>
					<div class="right">
				
						<ul>
						<c:forEach items="${cbModules}" var="sm">
							<li>
								<input type="checkbox" name="selectedModules" DISABLED value='<c:out value="${sm.module.moduleId}" />' <c:if test="${sm.selected}">checked="checked"</c:if> />
						        <span><c:out value="${sm.displayName}" /></span>
						    </li>
						</c:forEach>
						</ul>
				 
					</div>
				</p>				
			</div>	<!-- #templateDetails -->
	
		</div>
		
		<%@ include file="/help/include_outside/footer.jsp" %>
		
		<template:getSpaceJS />
	</body>
</html>
