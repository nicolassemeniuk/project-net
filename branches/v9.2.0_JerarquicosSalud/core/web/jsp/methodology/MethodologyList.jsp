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
|   $Revision: 20874 $
|       $Date: 2010-05-25 05:38:16 -0300 (mar, 25 may 2010) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Methodology List"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.Action,
			net.project.base.Module,
            net.project.base.property.PropertyProvider,
		    net.project.methodology.MethodologySpaceBean,
		    net.project.security.SessionManager,
		    net.project.methodology.MethodologyPortfolioBean,
            net.project.space.SpaceTypes" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />
<jsp:useBean id="methodologyPortfolio" class="net.project.methodology.MethodologyPortfolioBean"  />
<jsp:useBean id="domainList" class="net.project.methodology.DomainListBean" scope="page"/>

<%
	// Implicit security provided.  We are displaying items for the current
	// authenticated user.
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS/>
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/utils.js"/>
<template:import type="javascript" src="/src/checkRadio.js"/>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<%-- reset the properties of the methodologySpace --%>
<% methodologySpace.clear(); %>
<%
	methodologyPortfolio.clear();
	methodologyPortfolio.setParentSpaceID (user.getCurrentSpace().getID());
	methodologyPortfolio.load();
%>

<%-- Scriptlet added for Save As Template Dialog --%>
<%
	// This page's URL for backward navigation
	// Tim -- Instituting new standard: refLink is name of parameter to avoid spelling issues
	// Path now includes "/" to start to make more consistent with other paths.
	// Use refLink for direct HREFs.  Use refLinkEncoded for passing refLink as a parameter
	String refLink = "/business/Setup.jsp?module=" + Module.BUSINESS_SPACE;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
	
	// added for new Save as Template dialog
	// Fix for eliminating Schedule checkbox when creating template from Business Space
    // Fix for eliminating Process checkbox when creating templatate from Business Space
    boolean isIncludeSchedule = true;
    boolean isIncludeProcess = true;
    String currentSpaceType = SpaceTypes.BUSINESS_SPACE;
    if (SpaceTypes.BUSINESS.equals(SpaceTypes.getForID(currentSpaceType))) {
            isIncludeSchedule = false;
            isIncludeProcess = false;
    }

%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_portfolio";
	openwin_help(helplocation);
}

function reset() { 
	self.document.location = JSPRootURL + "/methodology/MethodologyList.jsp"; 
}
function create() {
//	self.document.location = JSPRootURL + "/methodology/MethodologyCreateWizard1.jsp?mode=clear&module=<%=Module.METHODOLOGY_SPACE%>&action=<%=Action.CREATE%>";
}
function modify() {
	//First, check to be sure there is a methodology to select.
    if (theForm.selected == null)
        return;

    if (verifySelection(theForm, 'one')){
		//self.document.location = JSPRootURL + "/methodology/Main.htm?id="+getValueRadio(theForm.selected);
		self.document.location = JSPRootURL + "/methodology/PropertiesEdit.htm?id="+getValueRadio(theForm.selected);
    }
}
function remove() {
    //First, check to be sure there is a methodology to select.
    if (theForm.selected == null)
        return;

    //Now make sure the user knows what they are doing.
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
     	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.template.portfolio.remove.confirm")%>', function(btn) { 
				if(btn == 'yes'){ 
					self.document.location = JSPRootURL + '/methodology/RemoveMethodology.jsp?module=<%=Module.METHODOLOGY_SPACE%>&action=<%=Action.DELETE%>&objectID='+getValueRadio(theForm.selected);
				}else{
				 	return false;
				}
		});
	}
}

function createNewSpace(spaceType) {
	// validate users selection first
    if (verifySelection(theForm, 'one')){
    	// redirect user to appropariate create space page (with template id parameter)
    	if(spaceType == '<%= SpaceTypes.BUSINESS_SPACE %>'){
    		self.document.location = JSPRootURL + '/business/CreateBusiness1.jsp?module=170&portfolio=true&methodologyID='+ getValueRadio(theForm.selected);
    	}else if(spaceType == '<%= SpaceTypes.PROJECT_SPACE %>'){
    		self.document.location = JSPRootURL + '/project/ProjectCreate.jsp?module=170&portfolio=true&methodologyID=' + getValueRadio(theForm.selected);
    	}
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

<!-- New Manage Templates Page Styles -->
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/methodology/saveAsTemplate.css" />
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/methodology/methodologyList.css" />
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/extjs/businessTheme.css" />
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/pnetDialogWindow.css" />
<style type="text/css">
	#pnetWinSubHeaderCnt { background: #d7fec4; }
</style>

</head>

<body class="main" onLoad="setup()" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Draw Toolbar
  -----------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" leftTitle='<%=PropertyProvider.get("prm.template.portfolio.title")%>' groupTitle="prm.global.tool.template.name" >
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.template.main.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/methodology/MethodologyList.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.METHODOLOGY_SPACE+"&action="+net.project.security.Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<!-- Note: create must be first item - for Save as Template dialog to work 
				TODO: modify tb:button to accept ID of class attributes to fix this issue -->
		<tb:button type="create" label='<%= PropertyProvider.get("prm.template.main.create.button.tooltip") %>' />
		<tb:button type="modify" label='<%= PropertyProvider.get("prm.template.main.edit.button.tooltip")%>' />
		<tb:button type="remove" label='<%= PropertyProvider.get("prm.template.main.delete.button.tooltip") %>' />
		
		<tb:button type="expand_all" function="javascript:toggleAllItemsDetailsContentDisplay('expand');" />
		<tb:button type="collapse_all" function="javascript:toggleAllItemsDetailsContentDisplay('collapse');" />
		<!-- TODO: Remove system properties: prm.business.setup.managetemplatespnet.expandall, prm.business.setup.managetemplatespnet.collapseall -->
	</tb:band>
	
	<tb:band name="action" groupHeading='<%= PropertyProvider.get("prm.template.main.managetemplates.heading")%>'>
		<tb:button type="custom" label='<%= PropertyProvider.get("prm.template.main.newbusiness.button.tooltip")%>' function='javascript:createNewSpace(\'business\');' imageEnabled='<%=SessionManager.getJSPRootURL() + "/images/icons/toolbar-gen-create_on.gif"%>' imageOver='<%=SessionManager.getJSPRootURL() + "/images/icons/toolbar-rollover-create.gif"%>' />
		<tb:button type="custom" label='<%= PropertyProvider.get("prm.template.main.newproject.button.tooltip")%>' function='javascript:createNewSpace(\'project\');' imageEnabled='<%=SessionManager.getJSPRootURL() + "/images/icons/toolbar-gen-create_on.gif"%>' imageOver='<%=SessionManager.getJSPRootURL() + "/images/icons/toolbar-rollover-create.gif"%>' />
	</tb:band>
</tb:toolbar>

<div id='content'>
	<%-- New Templates Listing UI - By Spaces --%>
	<h2><span class="headTitle"><display:get name="prm.template.portfolio.channel.available.title" /></span></h2>
	<span id="loadIndicator"><img src="<%=SessionManager.getJSPRootURL()%>/images/default/tree/loading.gif" /><strong><display:get name="prm.global.loading.message" /></strong></span>
	
	<form id="templateListForm">
		<div id="templatesListCnt" style="display:none;"></div>
	</form>

	<%@ include file="/help/include_outside/footer.jsp" %>
</div>	<!-- #content -->

<!--  -->
	<!-- Save as Template form -->
	<div id="saveAsTemplateCnt"  style="display:none">
		<div id="saveAsTemplContentWrapper">
	
			<div id="templateDetails">
				<form method="post" action="<%=SessionManager.getJSPRootURL()%>/methodology/TemplifySpaceProcessing.jsp" id="saveAsTemplateForm" name="templifySpace">
				    <input type="hidden" name="module" value="<%=Module.METHODOLOGY_SPACE%>" id="module" />
					<input type="hidden" name="action" value="<%=Action.CREATE%>" id="action" />
					<input type="hidden" name="theAction" id="theAction" />
					<input type="hidden" name="refLink" value="<%=refLink%>" id="refLink" />	
	
					<%-- Provide a elements for server round-trip error messaging --%>
					<p id="errorsCnt" style="display:none;"></p>
					<p>
						<span class="left required"><display:get name="prm.template.create.templatename" />:</span> 
						<span class="right"><input id="name" name="name" size="45" maxlength="80" VALUE="<c:out value="${getName}"/>"></span>
				    </p> 
					<p>
					    <span class="left required"><display:get name="prm.template.property.owner.business" />:</span> 
						<span class="right">
							<select id="parentSpaceID" name="parentSpaceID" onchange="checkWorkspaceSelection(this.value);">
							  <!-- option value="<%=user.getID()%>"><display:get name="prm.template.create.personalspace.label"/></option -->
							  <%=domainList.getAvailableBusinessOptionList( user.getID(), user.getCurrentSpace().getID() ) %>
							</select>
						</span>
					</p>
					<%-- Global visibility for templates means everyone in the system can see the template. --%>
					<% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
						<p id="globalCheckbox">
							<span class="left non-required"><display:get name="prm.methodology.visibleto.label" />:</span>
							<span class="right"><input type="checkbox" id="isGlobal" name="isGlobal"> <span><display:get name="prm.methodology.allusers.label" /></span> </span>
					    </p>
					<% } %>
					<p>
						<span class="left non-required"><display:get name="prm.template.create.description" />:</span> 
						<span class="right"><input type="text" id="description" name="description" maxlength="80" size="45" VALUE="<c:out value="${getDescription}"/>"></span>
				    </p> 
					<p>
						<span class="left non-required"><display:get name="prm.template.create.usescenario" />:</span>
					    <span class="right"><textarea id="useScenario" name="useScenario" cols="60" rows="7" maxlength="500"><c:out value="${getUseScenario}"/></textarea></span>
					</p>
					<p id="modulesIncluded">
						<div class="left required"><display:get name="prm.template.create.includedmodules" />:</div>
						<div class="right">
							<ul>
							    <li> 
							    	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.DISCUSSION%>"/>
							    	<span><display:get name="prm.template.create.select.discussion" /></span>
							    </li>
						        <li>
						        	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.DOCUMENT%>"/>
						        	<span><display:get name="prm.template.create.select.document" /></span>
							    </li>
							    <li>
							    	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.FORM%>"/>
							    	<span><display:get name="prm.template.create.select.form" /></span>	
							    </li>
								<%	if (isIncludeProcess) { %>
								    <li>
								    	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.PROCESS%>"/>
								    	<span><display:get name="prm.template.create.select.process" /></span>
								    </li>
						       	<%	} %>
								<li>
							        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.SECURITY%>" /> 
							        <span><display:get name="prm.template.create.select.security" /></span>
							    </li>
							    <li>
							    	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.WORKFLOW%>"/>
							    	<span><display:get name="prm.template.create.select.workflow" /></span>
							    </li>
							    <%  if (isIncludeSchedule) { %>
								    <li> 
								    	<input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.SCHEDULE%>"/>
								    	<span><display:get name="prm.template.create.select.schedule" /></span>
								    </li>
								<%  } %>
							</ul>
						</div>
					</p>
				</form>
			</div>	<!-- #templateDetails -->
			
		</div> <!-- #saveAsTemplContentWrapper -->
			
	</div> <!-- #saveAsTemplateCnt -->
<!--  -->

<template:getSpaceJS />
</body>

<script type="text/javascript">
	// Save As Template labels
	var saveAsTemplateWindowTitleValue = '<%=PropertyProvider.get("prm.methodology.dialog.saveastemplate.label")%>';
	var footerSubmitLabelValue = '<%=PropertyProvider.get("prm.methodology.dialog.submit.button")%>';
	var footerCancelLabelValue = '<%=PropertyProvider.get("prm.methodology.dialog.cancel.button")%>';
	var templateCreatedDialogTitleValue = '<%=PropertyProvider.get("prm.methodology.dialog.templatecreated.message")%>';
	var templateSavingErrorsHeadingLabelValue = '<%=PropertyProvider.get("prm.methodology.dialog.templatesavingerrors.message")%>'; 
	var ajaxRequestFailureHeadingLabelValue = '<%=PropertyProvider.get("prm.methodology.dialog.ajaxrequesterror.heading.message")%>';
	var ajaxRequestFailureMessageValue = '<%=PropertyProvider.get("prm.methodology.dialog.ajaxrequesterror.message")%>';
	var subHeaderLabelValue = '<font><%=PropertyProvider.get("prm.template.create.project")%>:</font>&nbsp;<%=org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(user.getCurrentSpace().getName())%>';
</script>
<template:import type="javascript" src="/src/methodology/PnetDialogWindow.js" />
<template:import type="javascript" src="/src/methodology/saveAsTemplate.js" />
<template:import type="javascript" src="/src/methodology/methodologyList.js" />
<script type="text/javascript">
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';
	var dataStructure = <%=methodologyPortfolio.getJSONBody()%>;

	var nameRequiredMsg = '<%=PropertyProvider.get("prm.template.templifyspace.namerequired.message")%>';
	var includedModulesRequiredMsg = '<%=PropertyProvider.get("prm.template.templifyspace.includedmodulesrequired.message")%>';
	
	var userId = '<%=user.getID()%>';
	var win;
		
	Ext.onReady(function() {
		tmplItem.compile().overwrite('templatesListCnt', dataStructure);
		Ext.get('templatesListCnt').slideIn('tr', { duration: 1, useDisplay: true })
			.highlight("FFFFE7", { attr: 'background-color', duration: 2 });
		
		// detach previousely attached handlers and reatach new ones to all elements
		Ext.select('.cntTrigger').removeAllListeners().addListener('click', toggleItemContentDisplay);

		// hide loading indicator
		Ext.get('loadIndicator').removeClass('hidden').addClass('hidden');

		// 'Create New' toolbox item click handler - TODO add this link an ID
		//Ext.get('toolbox-item').first().first().on('click', function(e) {
		Ext.get(Ext.get('toolbox-item').select('span').elements[0]).on('click', function(e) {
			openPopupDelegate(e, function(){
				// callback function triggered once form is submited
				location.reload(true);
			});
		});

		Ext.get('useScenario').on('keyup', ismaxlength);	// limit size of use scenario filed
	});
</script>

</html>
