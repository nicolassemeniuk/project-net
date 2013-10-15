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
    info="Project Properties"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.ProjectSpaceBean,
            net.project.security.*,
            net.project.document.DocumentManagerBean,
            net.project.base.property.PropertyProvider,
            net.project.project.ProjectVisibility,
            net.project.project.PercentCalculationMethod,
            net.project.util.NumberFormat,
            net.project.util.Conversion,
            net.project.space.Space,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />

<%--
Modify permission for objects of type "project" is based on modify permission
of the project space, since an object of type "project" IS a project space.
Specifying an object id here will cause security to FAIL due to fact that
object type "project" is not securable.
--%>
<%
	Space currentSpace = SessionManager.getUser().getCurrentSpace();
	int currentModule;
    if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
        currentModule = Module.PERSONAL_SPACE;
    } else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
        currentModule = Module.BUSINESS_SPACE;
    } else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
        currentModule = Module.PROJECT_SPACE;
    } else {
    	currentModule = Module.PERSONAL_SPACE;
    }
    securityProvider.setCheckedModuleID(currentModule);
%>
<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.PROJECT_SPACE%>" />
<%
    // Save referer or get from session if null
    String referer = request.getParameter("referer");
    if (referer != null && !referer.equals("")) {
        session.setAttribute("referer", request.getParameter("referer"));
    } else {
        referer = (String) session.getAttribute("referer");
    }

    if (errorReporter.errorsFound() && projectSpace.getID().equals(user.getCurrentSpace().getID())) {

        // Do not reload the project space
        // We are displaying the errors and the project space being editied
        // matches the current space (this prevents someone potentially editing
        // a space that they are not in
        // Do nothing

    } else {
        // Load the project space for editing
        projectSpace.setID ( user.getCurrentSpace().getID() );
        projectSpace.load();
        projectSpace.setUser (user);
    }

    docManager.setUser (user);
    String baseUrl = SessionManager.getJSPRootURL();
%>

<%
	// bfd-3259 resolved by vmalykhin
	
	String noLogoAttribute = (String) request.getSession().getAttribute("noLogo");
	boolean noLogo = false;
	if ((noLogoAttribute != null) && (noLogoAttribute.equals("true"))) {
		noLogo = true;
	}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkRadio.js" />

<!-- bfd-3259 resolved by vmalykhin -->
<!-- Ajax DWR includes -->
<script type='text/javascript'
    src='../dwr/engine.js'></script>
<script type='text/javascript'
    src='../dwr/interface/LogoRemover.js'></script>
<script type='text/javascript'
    src='../dwr/util.js'></script>

<script language="javascript">
var t_standard;
var theForm;
var page = false;
var isLoaded = false;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	selectRadio(theForm.visibilityID, '<%=projectSpace.getVisibility().getID()%>');
	isLoaded = true;
	var calculationMethod = document.getElementById("calculationMethod");
	if(calculationMethod.value == "<%=PercentCalculationMethod.SCHEDULE.getID()%>"){
		calculationMethod.value = '<%=PercentCalculationMethod.MANUAL.getID()%>';
		selectRadio(theForm.percentCalculationMethod, '<%=PercentCalculationMethod.SCHEDULE.getID()%>');
		changeProgressMethod('<%=PercentCalculationMethod.SCHEDULE.getID()%>');
	}else {
		calculationMethod.value = '<%=PercentCalculationMethod.SCHEDULE.getID()%>';
		selectRadio(theForm.percentCalculationMethod, '<%=PercentCalculationMethod.MANUAL.getID()%>');
		changeProgressMethod('<%=PercentCalculationMethod.MANUAL.getID()%>');
	}
	
	var costCalculationMethod = document.getElementById("costCalculationMethod");
	if(costCalculationMethod.value == "manual"){
		selectRadio(theForm.MetaCostCalculationMethod, 'manual');
		enableCostTextFields(true);
	}else{
		selectRadio(theForm.MetaCostCalculationMethod, 'automatic');
	}
	
	this.focus();
}

function changeProgressMethod (value) {

    if (value == "manual") {
        setProgressRequiredFields(true);
    } else {
        setProgressRequiredFields(false);
    }
}

function setProgressRequiredFields (required) {

    var cssClass = (required) ? 'fieldRequired' : 'fieldNonRequired';
    var isFieldDisabled = !required;
<%-- bfd 3273: Overall Completion option box behaves incorrectly when select on and off --%>
       // self.document.getElementById("project.manual.percentComplete").className = cssClass;
        self.document.getElementById("percentComplete").disabled = isFieldDisabled;
}

function enableCostTextFields(val){
	document.EDITPROJ.MetaMaterialsActualCostToDate_value.disabled=!val;
	document.EDITPROJ.MetaResourcesActualCostToDate_value.disabled=!val;
	document.EDITPROJ.MetaMaterialsCurrentEstimatedTotalCost_value.disabled=!val;
	document.EDITPROJ.MetaResourcesCurrentEstimatedTotalCost_value.disabled=!val;	
	document.EDITPROJ.estimatedROI_value.disabled=!val;	
}

function cancel() {
	if (<%=noLogo%>) {
		// Ajax DWR call
		LogoRemover.removeLogo();
	}	
	
	self.document.location = JSPRootURL + '/<%=referer%>' + <%=referer.indexOf("?") >= 0 ? "'&'" : "'?'"%> + 'module=<%=net.project.base.Module.PROJECT_SPACE%>';
}

function submit () {
	if(validate())
	{
		theAction("submit");
		theForm.action.value= "<%= net.project.security.Action.MODIFY %>"
		theForm.submit();
	}
}

function validate() {

	if(!checkTextbox(theForm.name,'<display:get name="prm.project.propertiesedit.projectnamerequired.message" />'))return false;
	if(!checkDropdown_NoSelect(theForm.defaultCurrencyCode,'<display:get name="prm.project.projectcreate.step2.defaultcurrencycoderequired.message" />')) return false;
	if(!checkDropdown_NoSelect(theForm.statusID, '<display:get name="prm.project.propertiesedit.statusrequired.message" />'))return false;
	if(!checkDropdown_NoSelect(theForm.colorCodeID, '<display:get name="prm.project.propertiesedit.colorcoderequired.message" />'))return false;

    if (getValueRadio(theForm.percentCalculationMethod) == '<%=PercentCalculationMethod.MANUAL.getID()%>') {

        if(!checkTextbox(theForm.percentComplete,'<display:get name="prm.project.propertiesedit.percentcompleterequired.message" />'))
            return false;
 	    if(isNaN(theForm.percentComplete.value))
 		    return errorHandler(theForm.percentComplete,'<display:get name="prm.project.propertiesedit.percentcompleteshouldbenumber.message" />');
 	    else if(theForm.percentComplete.value<0 || theForm.percentComplete.value>100)
 		    return errorHandler(theForm.percentComplete,'<display:get name="prm.project.propertiesedit.percentcompletevalidation.message" />');
    }

	if(theForm.parentProjectID.value==<%=projectSpace.getID()%>) {
	return errorHandler(theForm.parentProjectID,'<display:get name="prm.project.propertiesedit.projectownervalidation.message" />');
}
if (!checkMaxLength(theForm.currentStatusDescription,4000,'<display:get name="prm.project.propertiesedit.currentstatusdescriptionlength.message" />')) return false;

<%-- bfd-3267 	Error page when char. are entered in Budgeted Total Cost text box in Project Information page. --%>
if(!isNumeric(theForm.budgetedTotalCost_value.value))
    return errorHandler(theForm.budgetedTotalCost_value,'<display:get name="prm.project.propertiesedit.budgetedTotalCostshouldbenumber.message" />');
if(!isNumeric(theForm.MetaActualDiscretionalCost_value.value))
    return errorHandler(theForm.MetaActualDiscretionalCost_value,'<display:get name="prm.project.propertiesedit.actualdiscretionalcostshouldbenumber.message" />');    
if(!isNumeric(theForm.MetaCurrentDiscretionalCost_value.value))
    return errorHandler(theForm.MetaCurrentDiscretionalCost_value,'<display:get name="prm.project.propertiesedit.currentdiscretionalcostshouldbenumber.message" />');    
if(!isNumeric(theForm.MetaMaterialsCurrentEstimatedTotalCost_value.value))
    return errorHandler(theForm.MetaMaterialsCurrentEstimatedTotalCost_value,'<display:get name="prm.project.propertiesedit.materialscurrentestimatedtotalcostshouldbenumber.message" />');
if(!isNumeric(theForm.MetaResourcesCurrentEstimatedTotalCost_value.value))
    return errorHandler(theForm.MetaResourcesCurrentEstimatedTotalCost_value,'<display:get name="prm.project.propertiesedit.resourcescurrentestimatedtotalcostshouldbenumber.message" />');
if(!isNumeric(theForm.MetaMaterialsActualCostToDate_value.value))
    return errorHandler(theForm.MetaMaterialsActualCostToDate_value,'<display:get name="prm.project.propertiesedit.materialsactualcosttodateshouldbenumber.message" />');
if(!isNumeric(theForm.MetaResourcesActualCostToDate_value.value))
    return errorHandler(theForm.MetaResourcesActualCostToDate_value,'<display:get name="prm.project.propertiesedit.resourcesactualcosttodateshouldbenumber.message" />');    
if(!isNumericWithNegative(theForm.estimatedROI_value.value))
    return errorHandler(theForm.estimatedROI_value,'<display:get name="prm.project.propertiesedit.estimatedROIshouldbenumber.message" />');
if((theForm.estimatedROI_value.value>9999 || theForm.estimatedROI_value.value<-9999))
	return errorHandler(theForm.estimatedROI_value,'<display:get name="prm.project.propertiesedit.estimatedROIshouldbebetweennumbers.message" />')
return true;
}

function configuration() {
	theAction("configuration");
	theForm.submit();
}
function general() {
	theAction("general");
	theForm.submit();
}
function reset() {
	theForm.reset();
	<%-- bfd 3237: Error Page Displayed When Project Information Page is submitted in Setup  --%>
	setup();
}
function help() {
	var helplocation="<%=baseUrl%>/help/Help.jsp?page=project_info&section=general";
	openwin_help(helplocation);
}

function removeLogo() {
	<%-- // bfd-3259 resolved by vmalykhin --%>
	if (!isNaN('<%=projectSpace.getProjectLogoID()%>')) {

		Ext.MessageBox.confirm('Confirm', '<display:get name="prm.project.propertiesedit.removelogo.message" />', 
		  		  function(btn){
		  		  	if(btn == 'yes') { 
						// Ajax DWR call
						LogoRemover.removeLogo();
						document.getElementById("projectLogo").src = '';
						document.getElementById("projectLogo").width = '0';
						document.getElementById("projectLogo").height = '0';
						theAction("submit");
						theForm.submit();
		  		  		
		  		  	} else { 
		  		  		return false; 
		  		  	}
		  	 });
	} else {
		extAlert(errorTitle, '<display:get name="prm.project.modify.logoempty.error.message"/>' , Ext.MessageBox.ERROR);
	}
}

function addLogo() {
	/*var logoWin = openwin_small("logo_win");
	logoWin.document.location = "AddProjectLogo.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>" +
	"&action=<%=net.project.security.Action.MODIFY%>";*/
	openwin_small("logo_win","AddProjectLogo.jsp?module=<%= net.project.base.Module.PROJECT_SPACE %>" +
	"&action=<%=net.project.security.Action.MODIFY%>");
}

function isNumericWithNegative(val) {
	var roi = '';
	var reg = '0123456789.,';
	var ret = true;
	if (val.charAt(0) == '-'){
		roi = val.substring(1,val.length);
	} else {
		roi = val;
	}
	for(i=0; i<roi.length; i++){
		if(reg.indexOf(roi.charAt(i)) == -1 ){
			ret = false;
			break;	
		}
	}
	return ret;
}

function isNumeric(val){
	var reg = '0123456789.,';
	var ret = true;
	for(i=0; i<val.length; i++){
		if(reg.indexOf(val.charAt(i)) == -1 ){
			ret = false;
			break;	
		}
	}
	return ret;
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
--  Toolbar & History
------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label" showSpaceDetails="false">
    <tb:setAttribute name="leftTitle">
        <history:history>
        <%if(request.getParameter("referer")!= null && request.getParameter("referer").equalsIgnoreCase("project/Setup.jsp?module=150")){ %>
            <history:module display='<%= PropertyProvider.get("prm.project.properties.page.history")%>'
                            jspPage='<%=SessionManager.getJSPRootURL() + "/project/Setup.jsp"%>'
                            queryString='<%="module=" + net.project.base.Module.PROJECT_SPACE%>' />
             <history:page display="Edit Project"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/project/PropertiesEdit.jsp"%>'
                          queryString='<%="module=" + net.project.base.Module.PROJECT_SPACE + "&action=" + net.project.security.Action.MODIFY%>' />               
        <%} else { %>
            <history:module display="Edit Project"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/project/PropertiesEdit.jsp"%>'
                          queryString='<%="module=" + net.project.base.Module.PROJECT_SPACE + "&action=" + net.project.security.Action.MODIFY%>' />
        <%} %>
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------
--  Configure beans
----------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%
    // get Parent Business
    net.project.space.Space parentSpace = net.project.space.SpaceManager.getSuperProject(projectSpace);
    if (parentSpace != null) {
        projectSpace.setParentProjectID(parentSpace.getID());
    }
%>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/project/ProjectEditProcessing.jsp" name="EDITPROJ" >
<input type="hidden" name="module" value="<%= net.project.base.Module.PROJECT_SPACE %>">
<input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
<input type="hidden" name="referer" value="<%=referer%>">
<input type="hidden" name="theAction">
<input type="hidden" id="calculationMethod" value="<%=projectSpace.getPercentCalculationMethod().getID()%>"/>
<input type="hidden" id="costCalculationMethod" value="<%=projectSpace.getMetaData().getProperty("CostCalculationMethod")%>"/>

<table border="0"  align="left" width="97%" cellpadding="0" cellspacing="0">

<tr><td colspan="4">&nbsp;</td></tr>

<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.channel.description.title" /></nobr></td>
<td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<%-- Spit out any error message generated during submission of the form --%>
<%if (errorReporter.errorsFound()) {%>
<tr align="left" >
<td>&nbsp;</td>
<td class="warnText" colspan="2">
        <pnet-xml:transform stylesheet="/base/xsl/error-report.xsl" xml="<%=errorReporter.getXML()%>" />
        </td>
        <td>&nbsp;</td>
</tr>
    <%}%>
<%-- Support Code --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.supportcode.label" /></td>
<td nowrap class="tableContent"><%= projectSpace.getID() %></td>
<td>&nbsp;</td>
</tr>
<%-- Project Name --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldRequired"><display:get name="prm.project.propertiesedit.projectname.label" /></td>
<td nowrap class="tableContent">
<input type="text" name="name" size="40" maxlength="80" value='<c:out value="${projectSpace.name}" />'>
</td>
<td>&nbsp;</td>
</tr>

 <%-- Project Description --%>
 <tr align="left" >
 <td>&nbsp;</td>
 <td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.description.label" /></td>
 <td nowrap class="tableContent">
 <input name="description" size="40" maxlength="1000" value="<%=net.project.util.HTMLUtils.escape(projectSpace.getDescription()) %>">
 </td>
 <td>&nbsp;</td>
 </tr>

<%-- Meta: Project ID --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.projectid.label" /></td>
<td nowrap class="tableContent">
<input name="MetaExternalProjectID" size="40" maxlength="25" value="<%= projectSpace.getMetaData().getProperty("ExternalProjectID")== null ? "" : projectSpace.getMetaData().getProperty("ExternalProjectID")%>">
</td>
<td>&nbsp;</td>
</tr>

 <%-- Business Owner --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.businessowner.label" /></td>
<td nowrap class="tableContent">
<input type="hidden" name="previousParentSpaceID" value="<%= projectSpace.getParentBusinessID() != null ? projectSpace.getParentBusinessID() : "" %>">
<input type="hidden" name="previousParentProjectSpaceID" value="<%= projectSpace.getParentProjectID() != null ? projectSpace.getParentProjectID() : "" %>">
<select name="parentBusinessID" size="1" class="leftMargincls">
<option value="">None</option>
            <%= domainList.getAvailableBusinessOptionList( projectSpace.getUser().getID(), projectSpace.getParentBusinessID(), null ) %>
            </select>
            </td>
            <td> &nbsp;</td>
            </tr>

<%-- Subproject --%>
<% if (PropertyProvider.getBoolean("prm.project.subproject.isenabled")) { %>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.create.wizard.subprojectof" />:</td>
<td nowrap class="tableContent">
<select name="parentProjectID" class="leftMargincls">
<option value="">None</option>
        <%= domainList.getParentProjectOptionList(user, projectSpace.getParentProjectID(), projectSpace.getID(), projectSpace.getParentBusinessID()) %>
        </select>
        </td>
        <td>&nbsp;</td>
</tr>
    <% } else { %>
    <input type="hidden" name="parentProjectID" value="">
    <% } %>

<%-- Project Sponsor --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.sponsor.label" /></td>
<td class="tableContent">
<input type="text" name="sponsor" size="40" maxlength="1000" value="<c:out value="${projectSpace.sponsor}"/>">
</td>
<td>&nbsp;</td>
</tr>
<%-- Meta: Project Manager --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.projectmanager.label" /></td>
<td nowrap class="tableContent">
<input name="MetaProjectManager" size="40" maxlength="80" value="<%= projectSpace.getMetaData().getProperty("ProjectManager")== null ? "" : projectSpace.getMetaData().getProperty("ProjectManager") %>">
</td>
<td>&nbsp;</td>
</tr>

<%-- Meta: Program Manager --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.programmanager.label" /></td>
<td nowrap class="tableContent">
<input name="MetaProgramManager" size="40" maxlength="80" value="<%= projectSpace.getMetaData().getProperty("ProgramManager")== null ? "" : projectSpace.getMetaData().getProperty("ProgramManager") %>">
</td>
<td>&nbsp;</td>
</tr>

<%-- Meta: Initiative --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.initiative.label" /></td>
<td nowrap class="tableContent">
<input name="MetaInitiative" size="40" maxlength="80" value="<%= projectSpace.getMetaData().getProperty("Initiative") == null ? "" : projectSpace.getMetaData().getProperty("Initiative") %>">
</td>
<td>&nbsp;</td>
</tr>

<%-- Meta: Functional Area --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.functionalarea.label" /></td>
<td nowrap class="tableContent">
	<select name="MetaFunctionalArea" class="leftMargincls">
		<%= domainList.getValuesOptionListForProperty( new Integer(6), projectSpace.getMetaData().getProperty("FunctionalArea")) %>
	</select>
</td>
<td>&nbsp;</td>
</tr>
<%-- Priority --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.priority.label" /></td>
<td class="tableContent">
<select name="priorityCodeID" class="leftMargincls">
<option value=""></option>
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.project.PriorityCode.getAllPriorityCodes(), projectSpace.getPriorityCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<%-- Risk Rating --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.risk.label" /></td>
<td class="tableContent">
<select name="riskRatingCodeID" class="leftMargincls">
<option value=""></option>
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.project.RiskCode.getAllRiskCodes(), projectSpace.getRiskRatingCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<%-- Meta: Project Charter --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.projectcharter.label" /></td>
<td nowrap class="tableContent">
<input name="MetaProjectCharter" size="40" maxlength="80" value="<%= projectSpace.getMetaData().getProperty("ProjectCharter") == null ? "" : projectSpace.getMetaData().getProperty("ProjectCharter")  %>">
</td>
<td>&nbsp;</td>
</tr>

<%-- Logo --%>
<tr><td colspan="4">&nbsp;</td></tr>

<tr align="left" >
<td> &nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.projectlogo.label" /></td>
<td nowrap class="tableContent">
        <% //bfd 2945
        if (projectSpace.getProjectLogoID() != null) {
        %>
        	<a href="javascript:addLogo()"><IMG id="projectLogo" name="projectLogo" SRC="<%=SessionManager.getJSPRootURL()%>/servlet/photo?id=<%=projectSpace.getID()%>&size=medium&logoType=plogo&module=<%=net.project.base.Module.DOCUMENT%>" width="40px" border="0"></a>
        <%} else { //put dummy image placeholder
        %>    	
        	<IMG id="projectLogo" name="projectLogo" width="1" height="1"/>
        <%} %>
        &nbsp;&nbsp;<b><a href="javascript:addLogo()"><display:get name="prm.project.propertiesedit.changelogo.link" /></a></b>&nbsp;&nbsp;<b><a href="javascript:removeLogo()"><display:get name="prm.project.propertiesedit.removelogo.link" /></a></b><br> <span class="fieldNonRequired"><display:get name="prm.project.propertiesedit.uploadlogo.instruction" /></span><td>
        <td>&nbsp;</td>
</tr>



<%------------------------------------------------------------------------------
Project Completion Section
------------------------------------------------------------------------------%>
<tr><td colspan="4">&nbsp;</td></tr>

<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.completion.section" /></nobr></td>
<td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<tr><td colspan="4">&nbsp;</td></tr>

<tr align="left">
<td>&nbsp;</td>
<td valign="top" class="fieldRequired"><display:get name="prm.project.propertiesedit.completion.select" /></td>
<td class="tableContent">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td class="tableContent" width="1%">
<input type="radio" name="percentCalculationMethod" value="<%=PercentCalculationMethod.SCHEDULE.getID()%>" onChange="changeProgressMethod(this.value);">&nbsp;
</td>
<td class="tableContent" valign="top">
 <span id="project.schedule.percentComplete"><display:get name="prm.project.propertiesedit.completion.schedule.label"/> </span>
</td>
</tr>
<tr>
<td class="tableContent">&nbsp;</td>
<td class="tableContent">
 <display:get name="prm.project.propertiesedit.completion.schedule.description"/>
</td>
</tr>

<tr><td colspan="4">&nbsp;</td></tr>

<tr>
<td class="tableContent" width="1%">
<input type="radio" name="percentCalculationMethod" value="<%=PercentCalculationMethod.MANUAL.getID()%>" onChange="changeProgressMethod(this.value);">&nbsp;
</td>
<td class="tableContent" valign="top"><span id="project.manual.percentComplete"><display:get name="prm.project.propertiesedit.completion.label" /></span>
<%
    NumberFormat formatter = NumberFormat.getInstance();
    String formattedPercentComplete = formatter.formatNumber(Double.parseDouble(projectSpace.getPercentComplete()), 0, 2);
%>

<input type="text" id="percentComplete" name="percentComplete" size="5" maxlength="5" value="<%=formattedPercentComplete%>" %>
</td>
</tr>
<tr>
<td class="tableContent"></td>
<td class="tableContent">
<display:get name="prm.project.propertiesedit.completion.description"/>
</td>
</tr>
</table>
</td>
<td>&nbsp;</td>
</tr>

<%-- Project Visibility --%>

<tr><td colspan="4">&nbsp;</td></tr>

<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.create.wizard.visibility.section" /></nobr></td>
<td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<tr><td colspan="4">&nbsp;</td></tr>

<tr align="left">
<td>&nbsp;</td>
<td valign="top" class="fieldRequired"><display:get name="prm.project.propertiesedit.visibility.label" /></td>
<td class="tableContent">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td class="tableContent" width="1%">
<input type="radio" name="visibilityID" checked="checked" value="<%=ProjectVisibility.PROJECT_PARTICIPANTS.getID()%>"  >&nbsp;
</td>
<td class="tableContent" valign="top">
                    <%=ProjectVisibility.PROJECT_PARTICIPANTS.getName()%>
                    </td>
                    </tr>
                    <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                    <display:get name="prm.project.propertiesedit.visibility.projectparticipants.description" />
                    </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                    <td class="tableContent" width="1%">
                    <input type="radio" name="visibilityID" value="<%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getID()%>">&nbsp;
                    </td>
                    <td class="tableContent" valign="top">
                    <%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getName()%> <br>
                    </td>
                    </tr>
                    <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                    <display:get name="prm.project.create.wizard.visibility.owningbusinessparticipants.description" />
                    </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
                    <tr>
                    <td class="tableContent" width="1%">
                    <input type="radio" name="visibilityID" value="<%=ProjectVisibility.GLOBAL.getID()%>">&nbsp;
                    </td>
                    <td class="tableContent" valign="top">
                    <%=ProjectVisibility.GLOBAL.getName()%>
                    </td>
                    </tr>
                    
                    <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                    <display:get name="prm.project.propertiesedit.visibility.global.description" />
                    </td>
                    </tr>
                    <% } %>
                    </table>
                    </td>
                    <td>&nbsp;</td>
</tr>

<tr><td colspan="4">&nbsp;</td></tr>

<%------------------------------------------------------------------------------
Project Status Section
------------------------------------------------------------------------------%>
<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.channel.status.title" /></nobr></td>
<td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<%-- Start Date --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.startdate.label" /></td>
<td nowrap class="tableContent">
<input type="text" name="startDateString" size="12" maxlength="12" value="<jsp:getProperty name="projectSpace" property="startDateString" />">
        <util:insertCalendarPopup fieldName="startDateString"/>
        </td>
        <td>&nbsp;</td>
</tr>

<%-- End Date --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.enddate.label" /></td>
<td nowrap class="tableContent">
<input type="text" name="endDateString" size="12"  maxlength="12" value="<jsp:getProperty name="projectSpace" property="endDateString" />">
        <util:insertCalendarPopup fieldName="endDateString"/>
        </td>
        <td>&nbsp;</td>
</tr>
<%-- Overall Status --%>
<tr align="left" >
<td> &nbsp;</td>
<td class="fieldRequired"><display:get name="prm.project.propertiesedit.status.label" /></td>
<td nowrap class="tableContent">
<select name="statusID" size="1" class="leftMargincls">
            <%= domainList.getStatusOptionList( projectSpace.getStatusID() ) %>
            </select>
            </td>
            <td> &nbsp;</td>
</tr>
<%-- Color --%>
<tr align="left" >
<td> &nbsp;</td>
<td class="fieldRequired"><display:get name="prm.project.propertiesedit.colorcode.label" /></td>
<td nowrap class="tableContent">
        <%=net.project.code.ColorCode.getHtmlRadioSelection("colorCodeID", projectSpace.getColorCode())%>
        </td>
        <td> &nbsp;</td>
</tr>
<%-- Improvement --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldRequired"><display:get name="prm.project.propertiesedit.improvement.label" /></td>
<td class="tableContent">
<select name="improvementCodeID" class="leftMargincls">
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.code.ImprovementCode.getAllImprovementCodes(), projectSpace.getImprovementCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<tr><td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/trans.gif" height="5" /></td></tr>
<%-- Financial Status Color --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.financialstatuscolor.label" /></td>
<td class="tableContent">
        <%=net.project.code.ColorCode.getHtmlRadioSelection("financialStatusColorCodeID", projectSpace.getFinancialStatusColorCode(), true)%>
        </td>
        <td>&nbsp;</td>
</tr>
<%-- Financial Status Improvement --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.financialstatusimprovement.label" /></td>
<td class="tableContent">
<select name="financialStatusImprovementCodeID" class="leftMargincls">
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.code.ImprovementCode.getAllImprovementCodes(), projectSpace.getFinancialStatusImprovementCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<tr><td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/trans.gif" height="5" /></td></tr>
<%-- Schedule Status Color --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.schedulestatuscolor.label" /></td>
<td class="tableContent">
        <%=net.project.code.ColorCode.getHtmlRadioSelection("scheduleStatusColorCodeID", projectSpace.getScheduleStatusColorCode(), true)%>
        </td>
        <td>&nbsp;</td>
</tr>
<%-- Schedule Status Improvement --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.schedulestatusimprovement.label" /></td>
<td class="tableContent">
<select name="scheduleStatusImprovementCodeID" class="leftMargincls">
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.code.ImprovementCode.getAllImprovementCodes(), projectSpace.getScheduleStatusImprovementCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<tr><td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/trans.gif" height="5" /></td></tr>
<%-- Resource Status Color --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.resourcestatuscolor.label" /></td>
<td class="tableContent">
        <%=net.project.code.ColorCode.getHtmlRadioSelection("resourceStatusColorCodeID", projectSpace.getResourceStatusColorCode(), true)%>
        </td>
        <td>&nbsp;</td>
</tr>
<%-- Resource Status Improvement --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.resourcestatusimprovement.label" /></td>
<td class="tableContent">
<select name="resourceStatusImprovementCodeID" class="leftMargincls">
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.code.ImprovementCode.getAllImprovementCodes(), projectSpace.getResourceStatusImprovementCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<tr><td colspan="4"><img src="<%=SessionManager.getJSPRootURL()%>/images/trans.gif" height="5" /></td></tr>

<%-- Current Status Description --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.currentstatusdescription.label" /></td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr align="left">
<td>&nbsp;</td>
<td class="tableContent" colspan="2">
<textarea name="currentStatusDescription" cols="80" rows="4"><c:out value="${projectSpace.currentStatusDescription}"/></textarea>
</td>
<td>&nbsp;</td>
</tr>

<tr><td colspan="4">&nbsp;</td></tr>

<%------------------------------------------------------------------------------
Financial Status
------------------------------------------------------------------------------%>
<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
<td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.channel.finanacialcompletion.title" /></nobr></td>
<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
<tr><td>&nbsp;</td></tr>

   <%-- Default Currency Code --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldRequired"><display:get name="prm.project.properties.defaultcurrency.label" /></td>
<td class="tableContent">
<select name="defaultCurrencyCode" class="leftMargincls">
            <%=net.project.util.Currency.getHtmlOptionList(projectSpace.getDefaultCurrencyCode())%>
            </select>
            </td>
            <td>&nbsp;</td>
</tr>
<%-- Meta: Type of Expense --%>
<tr align="left" >
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.meta.typeofexpense.label" /></td>
<td nowrap class="tableContent">
	<select name="MetaTypeOfExpense" class="leftMargincls">
		<%= domainList.getValuesOptionListForProperty( new Integer(7), projectSpace.getMetaData().getProperty("TypeOfExpense")) %>
	</select>
</td>
<td>&nbsp;</td>
</tr>
<%-- Cost Center --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.costcenter.label" /></td>
<td class="tableContent">
<input type="text" name="costCenter" size="40" maxlength="1000" value="<c:out value="${projectSpace.costCenter}"/>">
</td>
<td>&nbsp;</td>
<%-- Budgeted Total Cost --%>
<tr align="left">
<td>&nbsp;</td>
<td class="fieldNonRequired"><display:get name="prm.project.propertiesedit.budgetedtotalcost.label" /></td>
<td class="tableContent">
        <input:money name="budgetedTotalCost" money="<%=projectSpace.getBudgetedTotalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>"/>
        </td>
        <td>&nbsp;</td>
</tr>

<%-- Actual Discretional Cost --%>
<tr align="left">
	<td>&nbsp;</td>
	<td class="fieldNonRequired">
		<display:get name="prm.project.propertiesedit.actualdiscretionalcost.label" />:&nbsp;
	</td>						
	<td class="tableContent">
        <input:money name="MetaActualDiscretionalCost" money="<%=projectSpace.getActualDiscretionalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" />
       </td>
   	<td>&nbsp;</td>
</tr>

<%-- Current Discretional Cost --%>
<tr align="left">
	<td>&nbsp;</td>
	<td class="fieldNonRequired">
		<display:get name="prm.project.propertiesedit.currentdiscretionalcost.label" />:&nbsp;
	</td>						
	<td class="tableContent">
        <input:money name="MetaCurrentDiscretionalCost" money="<%=projectSpace.getCurrentDiscretionalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" />
       </td>
   	<td>&nbsp;</td>
</tr>	

<tr><td colspan="4">&nbsp;</td></tr>

		<tr align="left">
			<td>&nbsp;</td>
			
			<td valign="top" class="fieldRequired">
				<display:get name="prm.project.propertiesedit.costcompletion.select" />
			</td>
			
			<td class="tableContent">
			
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td class="tableContent" width="1%">
							<input type="radio" checked onclick="enableCostTextFields(false)" name="MetaCostCalculationMethod" value="automatic" >&nbsp;
						</td>
						<td class="tableContent" valign="top">
						 	<span id="project.schedule.percentComplete">
						 		<display:get name="prm.project.propertiesedit.completion.projectcosts.label"/>
						 	</span>
						</td>
					</tr>
					
					<tr>
						<td class="tableContent">&nbsp;</td>
						<td class="tableContent"><display:get name="prm.project.propertiesedit.completion.projectcosts.description"/>
						</td>
					</tr>
					
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					
					<tr>
						<td class="tableContent" width="1%">
							<input type="radio" onclick="enableCostTextFields(true)" name="MetaCostCalculationMethod" value="manual">&nbsp;
						</td>						
						<td class="tableContent" valign="top">
							<span id="project.manual.percentComplete">
								<display:get name="prm.project.propertiesedit.costs.label" />
							</span>				
						</td>
						
					</tr>	
					
					<%-- Materials Current Estimated Total Cost --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.propertiesedit.materialscurrentestimatedtotalcost.label" />:&nbsp;
						</td>						
						<td class="tableContent">
					        <input:money name="MetaMaterialsCurrentEstimatedTotalCost" money="<%=projectSpace.getMaterialsCurrentEstimatedTotalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" disabled="true"/>
				        </td>
					   	<td>&nbsp;</td>
					</tr>					
					
					<%-- Resources Current Estimated Total Cost --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.propertiesedit.resourcescurrentestimatedtotalcost.label" />:&nbsp;
						</td>						
						<td class="tableContent">
					        <input:money name="MetaResourcesCurrentEstimatedTotalCost" money="<%=projectSpace.getResourcesCurrentEstimatedTotalCost()%>" currency="<%=projectSpace.getDefaultCurrency()%>" disabled="true"/>
				        </td>
					   	<td>&nbsp;</td>
					</tr>								
					
					<%-- Materials Actual Cost To Date --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.propertiesedit.materialsactualcosttodate.label" />:&nbsp;
						</td>						
						<td class="tableContent">
						        <input:money name="MetaMaterialsActualCostToDate" money="<%=projectSpace.getMaterialsActualCostToDate()%>" currency="<%=projectSpace.getDefaultCurrency()%>" disabled="true"/>
				        </td>
				        <td>&nbsp;</td>
					</tr>					
					
					<%-- Resources Actual Cost To Date --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.propertiesedit.resourcesactualcosttodate.label" />:&nbsp;
						</td>						
						<td class="tableContent">
						        <input:money name="MetaResourcesActualCostToDate" money="<%=projectSpace.getResourcesActualCostToDate()%>" currency="<%=projectSpace.getDefaultCurrency()%>" disabled="true"/>
				        </td>
				        <td>&nbsp;</td>
					</tr>						
					
					<%-- Estimated ROI --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.propertiesedit.estimatedroi.label" />:&nbsp;
						</td>

						<td class="tableContent">
								<input type="text" name="estimatedROI_value" size="40" maxlength="1000" disabled="true" 
									value="<%
												if (projectSpace.getEstimatedROI() != null && projectSpace.getEstimatedROI().getValue() != null)
															out.print(projectSpace.getEstimatedROI().getValue());
											%>"/>%
						</td>
				        <td>&nbsp;</td>
					</tr>
					
						
				</table>
			
			</td>
			<td>&nbsp;</td>
		</tr>









 


</tr>


<%------------------------------------------------------------------------------
Other Properties
------------------------------------------------------------------------------%>
<%--
<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
<td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.channel.other.title" /></nobr></td>
<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
--%>


</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
        <tb:button type="submit" />
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
