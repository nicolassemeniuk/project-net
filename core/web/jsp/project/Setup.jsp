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

<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />

<%@page import="net.project.space.SpaceType"%>
<%@page import="net.project.space.SpaceTypes"%><html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Name"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.security.User,
			net.project.security.Action,
			net.project.base.Module,
			net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
			net.project.project.ProjectSpaceBean"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.methodology.DomainListBean" scope="page"/>
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />
<%
	// This page's URL for backward navigation
	// Tim -- Instituting new standard: refLink is name of parameter to avoid spelling issues
	// Path now includes "/" to start to make more consistent with other paths.
	// Use refLink for direct HREFs.  Use refLinkEncoded for passing refLink as a parameter
	String refLink = "/project/Setup.jsp?module=" + Module.PROJECT_SPACE;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
	
	String[] props = { user.getDisplayName() } ;
	
	// added for new Save as Template dialog
	// Fix for eliminating Schedule checkbox when creating template from Business Space
    // Fix for eliminating Process checkbox when creating templatate from Business Space
    boolean isIncludeSchedule = true;
    boolean isIncludeProcess = true;
    String currentSpaceType = SpaceTypes.PROJECT_SPACE;
    if (SpaceTypes.BUSINESS.equals(SpaceTypes.getForID(currentSpaceType))) {
            isIncludeSchedule = false;
            isIncludeProcess = false;
    }

%>
	<title><display:get name="prm.global.application.title" /></title>
	<%-- Import CSS --%>
	<template:getSpaceCSS />

	<%-- Import Javascript --%>
	<template:import type="javascript" src="/src/standard_prototypes.js" />
	<%-- Validation JS Libraries --%>
	<template:import type="javascript" src="/src/checkComponentForms.js" />
	<template:import type="javascript" src="/src/errorHandler.js" />
	

	<script language="javascript">
		var theForm;
		var isLoaded = false;
		var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	
		function cancel() { self.document.location = JSPRootURL + "/project/Dashboard?module=<%=Module.PROJECT_SPACE%>"; }
		function reset() { self.document.location = JSPRootURL + "/project/Setup.jsp?module=<%=Module.PROJECT_SPACE%>"; }
		function help() {
			var helplocation=JSPRootURL+"/help/Help.jsp?page=project_setup";
			openwin_help(helplocation);
		}
		function ismaxlength(e) {
			var obj = e.target;
			var mlength=obj.getAttribute("maxlength") ? parseInt(obj.getAttribute("maxlength")) : "";
			if (obj.getAttribute("maxlength") && obj.value.length>mlength) {
				obj.value=obj.value.substring(0,mlength);
			}
		}
	</script>
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/methodology/saveAsTemplate.css" />
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/extjs/projectTheme.css" />
<link type="text/css" rel="stylesheet" href="<%=SessionManager.getJSPRootURL()%>/styles/pnetDialogWindow.css" />

</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
  --  Toolbar & History                                                                                   
  ------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.setup">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module displayToken="prm.project.nav.setup"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/project/Setup.jsp"%>'
						  queryString='<%="module=" + Module.PROJECT_SPACE%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------
  --  Page Content                                                                                        
  ------------------------------------------------------------------------------------------------------%>

<div class="projects-setup-header"><%= PropertyProvider.get("prm.project.setup.channel.personalsetup.title")%></div>

<display:if name="prm.global.notification.globalsubscription.isenabled" >
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()+"/notification/ManageSubscriptions.jsp?spaceID="+user.getCurrentSpace().getID() %>"><%= PropertyProvider.get("prm.project.setup.notifications.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.managepersonalnotifications.label")%></div> 
</display:if>

<div class="projects-setup-header"><%= PropertyProvider.get("prm.project.setup.channel.projectadministratorsetting.title")%></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/project/PropertiesEdit.jsp?module=<%=Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&referer=project/Setup.jsp?module=<%=Module.PROJECT_SPACE%>"><%= PropertyProvider.get("prm.project.setup.projectinformation.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.projectedit.label")%></div> 


<%-- Administrator Section --%>

<%
	// bfd-3259 resolved by vmalykhin
	
	String projectLogoID = projectSpace.getProjectLogoID();
	if ((projectLogoID == null) || (projectLogoID.length() == 0)) {
		request.getSession().setAttribute("noLogo", "true");
	}
%>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/project/DirectorySetup.jsp?module=<%=Module.DIRECTORY%>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.roles.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.directoryedit.label")%></div> 

<!-- Save As Template -->
<div class="setup-item"><div>
<a id="saveAsTemplateLink" href="<%=SessionManager.getJSPRootURL()%>/methodology/TemplifySpace.jsp?module=<%=Module.METHODOLOGY_SPACE%>&action=<%=Action.CREATE%>&refLink=<%=refLinkEncoded%>&currentSpaceType=<%=SpaceTypes.PROJECT_SPACE%>"><%= PropertyProvider.get("prm.project.setup.saveastemplates.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.templatescreatefromproject.label")%></div> 


<% if (PropertyProvider.getBoolean("prm.project.workflow.isenabled")) { %>
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/workflow/WorkflowDesigner.jsp?module=<%=Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.workflows.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.workflowsedit.label")%></div> 


<% } %>

<% if (PropertyProvider.getBoolean("prm.project.form.isenabled")) { %>
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/form/designer/Main.jsp?module=<%=Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.manageforms.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.channel.formsedit.label")%></div> 
<% } %>

<display:if name="@prm.crossspace.isenabled">
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/servlet/CrossSpaceController/ShowShares?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&referer=project/Setup.jsp"><%=PropertyProvider.get("prm.project.setup.shares.link")%></a></div>
<%=PropertyProvider.get("prm.project.setup.channel.shares.label")%></div> 
</display:if>

<% if (PropertyProvider.getBoolean("prm.project.trashcan.isenabled")) { %>
<!-- sjmittal: ViewDeletedDocuments.jsp to be chnaged to Main.jsp in future once other objects then documents are displayed in the trashcan -->
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/trashcan/ViewDeletedDocuments.jsp?module=<%=Module.TRASHCAN%>&action=<%=net.project.security.Action.LIST_DELETED%>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.trashcan.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.trashcan.view.label")%></div> 
<% } %>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/security/SecurityModuleMain.jsp?module=<%=Module.SECURITY%>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.security.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.channel.managesecurity.label")%></div> 

<%----------- only show this if the flag is set (temp and for debug only -------------%>
<% if (net.project.base.property.PropertyProvider.getBoolean ("prm.project.setup.administration.notification.isenabled")) {%>
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/notification/Main.jsp"><%= PropertyProvider.get("prm.project.setup.notifications.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.manageprojectnotifications.label")%></div> 
<% } %>

<%-- User's Global Settings --%>

<div class="projects-setup-header"><%= PropertyProvider.get("prm.project.setup.channel.personalglobalsetup.title")%></div>

<%--Avinash: bfd 3214 and 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
<%session.removeAttribute("referer"); %>
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/personal/ProfileName.jsp?module=<%= Module.PERSONAL_SPACE %>&referer=project/Setup.jsp"><%= PropertyProvider.get("prm.project.setup.personalprofile.link")%></a></div>
<%= PropertyProvider.get("prm.project.setup.profileedit.label")%></div> 

<%@ include file="/help/include_outside/footer.jsp" %>


</div>

<!-- Save as Template form -->
<div id="saveAsTemplateCnt" style="display:none;">
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
						  <!--option value="<%=user.getID()%>"><display:get name="prm.template.create.personalspace.label"/></option-->
						  <%=domainList.getAvailableBusinessOptionList( user.getID(), methodologySpace.getParentSpaceID() ) %>
						</select>
					</span>
				</p>
				<%-- Global visibility for templates means everyone in the system can see the template. --%>
				<% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
					<p id="globalCheckbox">
						<span class="left non-required"><display:get name="prm.methodology.visibleto.label" />:</span>
						<span class="right"><input type="checkbox" id="isGlobal" name="isGlobal"> <span><display:get name="prm.methodology.allusers.label" /></span></span>
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
<script type="text/javascript">
	var nameRequiredMsg = '<%=PropertyProvider.get("prm.template.templifyspace.namerequired.message")%>';
	var includedModulesRequiredMsg = '<%=PropertyProvider.get("prm.template.templifyspace.includedmodulesrequired.message")%>';
	var userId = '<%=user.getID()%>';
		
	Ext.onReady(function() {
		Ext.get('saveAsTemplateLink').on('click', openPopupDelegate);
		
		// hide Visible To field if Personal Space is selected on page load
		handleOwningBusinessSelection(userId);

		Ext.get('useScenario').on('keyup', ismaxlength);	// limit size of use scenario filed
	});	
</script>
</html>
