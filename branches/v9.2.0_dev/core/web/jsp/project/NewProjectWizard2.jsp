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
    info="New Project Wizard -- page 2"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
    			net.project.security.*,
    			net.project.security.User,
				net.project.space.Space,
				net.project.space.SpaceFactory,
				net.project.space.ISpaceTypes,
				net.project.space.SpaceURLFactory,
    			net.project.methodology.MethodologyProvider,
				net.project.base.property.PropertyProvider,
	            net.project.code.ColorCode,
	            net.project.util.Currency,
	            net.project.base.money.Money,
    	        net.project.code.ImprovementCode"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectWizard" class="net.project.project.ProjectWizard" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />

<%
	String mySpace = user.getCurrentSpace().getType();
	int module = -1;
	if (mySpace.equals(Space.PERSONAL_SPACE)) module = net.project.base.Module.PERSONAL_SPACE;
	if (mySpace.equals(Space.BUSINESS_SPACE)) module = net.project.base.Module.BUSINESS_SPACE;
	if (mySpace.equals(Space.PROJECT_SPACE)) module = net.project.base.Module.PROJECT_SPACE;
	String verifyAction = null;
	int action = securityProvider.getCheckedActionID();
	if (action == net.project.security.Action.VIEW) verifyAction="view";
	if (action == net.project.security.Action.CREATE) verifyAction="create";
%>
<security:verifyAccess action="<%=verifyAction%>"
					   module="<%=module%>" />

<%
	//if (request.getParameter("theAction") != null && !request.getParameter("theAction").equals("back"))
    	//projectWizard.reset();

    projectWizard.setUser(user);

    // Find out whether to display back button or not
    String displayBackButton = (projectWizard.isTermsOfUseEnabled() ? "true" : "false");
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="project"/>

<%
	String refLink = (String) pageContext.getAttribute("pnet_refLink" ,  pageContext.SESSION_SCOPE);
	String methodologyID = request.getParameter("methodologyID") != null ? request.getParameter("methodologyID") : "";
%>


<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/checkRadio.js" />

<script language="javascript">

	javascript:window.history.forward(-1);
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var currentSpaceTypeForBlog = 'person';
    var currentSpaceIdForBlog = '<%= SessionManager.getUser().getID() %>';
    
 function setup() {
    theForm = self.document.forms[0];
    selectRadio(theForm.visibilityID, '<%=projectWizard.getVisibility() != null ? projectWizard.getVisibility().getID() : PropertyProvider.get("prm.project.visibility.defaultvalue")%>');
<%--    selectRadio(theForm.percentCalculationMethod, '<%=projectWizard.getPercentCalculationMethod() != null ? projectWizard.getPercentCalculationMethod().getID() : PropertyProvider.get("prm.project.percentcalculationmethod.defaultvalue")%>'); --%>
    isLoaded = true;
    if(theForm.parentProjectID.value != ''){
		theForm.parentProjectID.disabled = true;
	}
	preselectTemplate('<%= methodologyID %>');
}

function submit() {
    theAction("submit");
    if(processForm(theForm))
    	theForm.submit();
}

function back(){
    self.document.location = JSPRootURL + "/project/NewProjectWizard0.jsp?module=<%= module %>";
}

function help(){
	var helplocation=JSPRootURL+"/help/Help.jsp?page=project_portfolio&section=create";
	openwin_help(helplocation);
}

function enableTextField(val){
	document.CREATEPROJ.percentComplete.disabled=val;
}

function enableCostTextFields(val){
	document.CREATEPROJ.MetaMaterialsActualCostToDate_value.disabled=!val;
	document.CREATEPROJ.MetaResourcesActualCostToDate_value.disabled=!val;
	document.CREATEPROJ.MetaMaterialsCurrentEstimatedTotalCost_value.disabled=!val;
	document.CREATEPROJ.MetaResourcesCurrentEstimatedTotalCost_value.disabled=!val;	
	document.CREATEPROJ.estimatedROI_value.disabled=!val;
}

<%
 if ( refLink != null && !refLink.trim().equals("")){
%>
	function cancel() { self.document.location = '<%= refLink %>'; }
<%
   } else {
%>
function cancel() { self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?module=<%= module %>&portfolio=true"; }
<%
   }
%>

function reset() { self.document.CREATEPROJ.reset(); }

function methodologyBrowser() {

   var browser = openwin_wizard("methodology_browser");

   browser.document.location=JSPRootURL + "/methodology/MethodologyBrowser.jsp";

}

function processForm(myForm) {

 	theAction('submit');
 	if(!checkTextbox(myForm.name,'<display:get name="prm.project.projectcreate.step2.projectnamerequired.message" />')) return false;
 	if(!checkDropdown(myForm.defaultCurrencyCode,'<display:get name="prm.project.projectcreate.step2.defaultcurrencycoderequired.message" />')) return false;
 	if(!checkDropdown_NoSelect(myForm.statusID,'<display:get name="prm.project.projectcreate.step2.statusrequired.message" />')) return false;

    if (getValueRadio(myForm.percentCalculationMethod) == '<%=PercentCalculationMethod.MANUAL.getID()%>') {
        if(!checkTextbox(myForm.percentComplete,'<display:get name="prm.project.projectcreate.step2.completepercentrequired.message" />')) return false;
 	        if(isNaN(theForm.percentComplete.value))
 		        return errorHandler(theForm.percentComplete,'<display:get name="prm.project.projectcreate.step2.completepercentshouldbenumber.message" />');
 	    else if(theForm.percentComplete.value<0 || theForm.percentComplete.value>100)
 		    return errorHandler(theForm.percentComplete,'<display:get name="prm.project.projectcreate.step2.completepercentvalidation.message" />');
    }

	return true;

}
function preselectTemplate(methodologyId) {
	if(methodologyId != '') {
		Ext.get('methodologyID').dom.value = methodologyId;		// preselect template value
	}
}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />

<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------
	--  Toolbar
	----------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label" space="project" showSpaceDetails="false">
    <tb:setAttribute name="leftTitle">
        <display:get name="prm.project.create.wizard.title" />
    </tb:setAttribute>
    <tb:setAttribute name="rightTitle">
        <display:get name="prm.project.create.wizard.title.step2" />
    </tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="NewProjectWizard2Processing.jsp" name="CREATEPROJ" onSubmit="return processForm(this);">
    <input type="hidden" name="module" value="<%= module %>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="parent" value='<%=request.getParameter("parentSpaceID") %>' >

	<table border="0" align="left" width="700" cellpadding="0" cellspacing="0">
          <tr align="left" class="channelHeader">
            <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td nowrap colspan="3" class="channelHeader" align="left">&nbsp;<display:get name="prm.project.create.wizard.description.heading" />&nbsp;&nbsp;<display:get name="prm.global.display.requiredfield" /></td>
            <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
          </tr>
<%-- Print out any errors --%>
		  <tr align="left" valign="top">
            <td>&nbsp;</td>
            <td colspan="3" class="warnText">
                <pnet-xml:transform stylesheet="/base/xsl/error-report.xsl" xml="<%=errorReporter.getXML()%>" />
            </td>
          	<td>&nbsp;</td>
          </tr>
<%-- Project Name --%>
          <tr align="left" class="addSpacingBottom">
            <td nowrap colspan="2" class="fieldRequired"><display:get name="prm.project.create.wizard.name" />:&nbsp;</td>
            <td nowrap colspan="3">
              <input name="name" size="45" maxlength="80" value="<c:out value="${projectWizard.name}"/>">
            </td>
          </tr>

<%-- Project Description --%>
          <tr align="left" class="addSpacingBottom">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.description" />:&nbsp;</td>
            <td nowrap colspan="3">
              <input type="text" name="description" value="<c:out value="${projectWizard.description}"/>" maxlength="1000" size="45">
            </td>
          </tr>

<%-- Meta: Project ID --%>
		  <tr align="left" class="addSpacingBottom">
			  <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.projectid" />:&nbsp;</td>
			  <td nowrap colspan="3">
				  <input name="MetaExternalProjectID" size="45" maxlength="80" value="">
			  </td>
		  </tr>

<%-- Business Owner --%>
          <tr align="left" class="addSpacingBottom">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.businessowner" />:&nbsp;</td>
            <td colspan="3">
              <select name="parentBusinessID">
                <option value=""><display:get name="prm.project.create.wizard.businessowner.none" /></option>
                <%= domainList.getAvailableBusinessOptionList( user.getID(), projectWizard.getParentBusinessID(), null ) %>
              </select>
            </td>
          </tr>

<%-- Subproject --%>
<% if (PropertyProvider.getBoolean("prm.project.subproject.isenabled")) { %>
          <tr align="left" class="addSpacingBottom">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.subprojectof" />:&nbsp;</td>
            <td nowrap colspan="3">
              <select name="parentProjectID">
                <option value=""><%=PropertyProvider.get("prm.projects.space.create.subproject.option.none.value")%></option> <!-- None -->
                <%= domainList.getSubProjectOptionList(user, projectWizard.getParentProjectID()) %>
              </select>
            </td>
          </tr>
<% } else { %>
			<input type="hidden" name="parentProjectID" value="">
<% } %>

<%-- Methodology --%>
         <tr align="left" class="addSpacingBottom">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.applymethodology" />:&nbsp;</td>
            <td colspan="3">
              <select name="methodologyID" id="methodologyID">
                <option SELECTED value=""><display:get name="prm.project.create.wizard.methodology.none" /></option>
                <%= methodologyProvider.getMethodologyOptionListForUser(user) %>
              </select><%-- <a href="javascript:methodologyBrowser()">Methodology Browser</a>--%>
           </td>
        </tr>

<%-- Meta: Project Manager --%>
			<tr align="left" class="addSpacingBottom">
				<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.projectmanager" />:&nbsp;</td>
				<td nowrap colspan="3">
					<input name="MetaProjectManager" size="45" maxlength="80" value="">
				</td>
			</tr>

<%-- Meta: Program Manager --%>
			<tr align="left" class="addSpacingBottom">
				<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.programmanager" />:&nbsp;</td>
				<td nowrap colspan="3">
					<input name="MetaProgramManager" size="45" maxlength="80" value="">
				</td>
			</tr>

<%-- Meta: Initiative --%>
			<tr align="left" class="addSpacingBottom">
				<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.initiative" />:&nbsp;</td>
				<td nowrap colspan="3">
					<input name="MetaInitiative" size="45" maxlength="80" value="">
				</td>
			</tr>

<%-- Meta: Functional Area --%>
			<tr align="left" class="addSpacingBottom">
				<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.functionalarea" />:&nbsp;</td>
				<td nowrap colspan="3">
					<select name="MetaFunctionalArea">
						<%= domainList.getValuesOptionListForProperty( new Integer(6), null) %>
					</select>
				</td>
			</tr>

<%-- Meta: Project Charter --%>
			<tr align="left" class="addSpacingBottom">
				<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.projectcharter" />:&nbsp;</td>
				<td nowrap colspan="3">
					<input name="MetaProjectCharter" size="45" maxlength="80" value="">
				</td>
			</tr>

	<%------------------------------------------------------------------------------
    Project Completion Section
    ------------------------------------------------------------------------------%>
    <tr>
    	<td colspan="4">&nbsp;</td>
    </tr>

    <tr class="channelHeader">
    	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    	<td nowrap class="channelHeader" colspan="2"><nobr><display:get name="prm.project.propertiesedit.completion.section" /></nobr></td>
    	<td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
    </tr>

    <tr><td colspan="4">&nbsp;</td></tr>

    <tr align="left">
	    <td>&nbsp;</td>
	    
	    <td valign="top" class="fieldRequired">
	    	<display:get name="@prm.project.propertiesedit.completion.select" />
	    </td>
	    
	    <td class="tableContent">
		    <table width="100%" border="0" cellpadding="0" cellspacing="0">
		    
		    <tr>
			    <td class="tableContent" width="1%">
			    	<input type="radio" name="percentCalculationMethod" checked onclick="enableTextField(true)" value="<%=PercentCalculationMethod.SCHEDULE.getID()%>">&nbsp;
			    </td>
			    <td class="tableContent">
			    <display:get name="prm.project.propertiesedit.completion.schedule.label"/>
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
		    <input type="radio" name="percentCalculationMethod" onclick="enableTextField(false)" value="<%=PercentCalculationMethod.MANUAL.getID()%>">&nbsp;
		    </td>
		    <td class="tableContent"><display:get name="prm.project.propertiesedit.completion.label" />
		    <input type="text" name="percentComplete" size="5" maxlength="3" disabled="true" value="<jsp:getProperty name="projectWizard" property="percentComplete" />"> %
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
            <td colspan="2" valign="top" class="fieldRequired"><display:get name="prm.project.create.wizard.visibility.label" />&nbsp;</td>
            <td colspan="3">
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="tableContent" width="1%">
                        <input type="radio" name="visibilityID" value="<%=ProjectVisibility.PROJECT_PARTICIPANTS.getID()%>">&nbsp;
                    </td>
                    <td class="tableContent">
                        <%=ProjectVisibility.PROJECT_PARTICIPANTS.getName()%>
                    </td>
                </tr>
                <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                        <display:get name="prm.project.create.wizard.visibility.projectparticipants.description" />
                    </td>
                </tr>                
                <tr>
                    <td class="tableContent" width="1%">
                        <input type="radio" name="visibilityID" value="<%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getID()%>">&nbsp;
                    </td>
                    <td class="tableContent">
                        <%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getName()%> <br>
                    </td>
                </tr>                
                <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                        <display:get name="prm.project.create.wizard.visibility.owningbusinessparticipants.description" />
                    </td>
                </tr>
                <% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
                <tr>
                    <td class="tableContent" width="1%">
                        <input type="radio" name="visibilityID" value="<%=ProjectVisibility.GLOBAL.getID()%>">&nbsp;
                    </td>
                    <td class="tableContent">
                        <%=ProjectVisibility.GLOBAL.getName()%>
                    </td>
                </tr>                
                <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent">
                        <display:get name="prm.project.create.wizard.visibility.global.description" />
                    </td>
                </tr>
                <% } %>
                </table>
            </td>
          </tr>

          <tr align="left">
            <td nowrap colspan="5">&nbsp;</td>
          </tr>



			<%----------------------------------------- 
						Project Status 
			------------------------------------------%>

		<tr align="left" class="channelHeader">
			<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
			<td nowrap colspan="3" class="channelHeader"><nobr><display:get name="prm.project.create.wizard.projectstatus.heading" /></nobr></td>
			<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
		</tr>
		
			<tr><td colspan="4">&nbsp;</td></tr>
		
		<tr align="left">
            <td nowrap class="fieldRequired" colspan="2"><display:get name="prm.project.create.wizard.status" />:&nbsp;</td>
            <td nowrap colspan="3">
              <select name="statusID" size="1">
                <%= domainList.getStatusOptionList( projectWizard.getStatusID() ) %>
              </select>
            </td>
		</tr>

<%-- Project Color Code --%>
        <tr align="left">
            <td nowrap class="fieldRequired" colspan="2"><display:get name="prm.project.create.wizard.colorcode" />:&nbsp;</td>
            <td nowrap colspan="3">
                <%=net.project.code.ColorCode.getHtmlRadioSelection("colorCodeID", (projectWizard.getColorCode() != null ? projectWizard.getColorCode() : ColorCode.GREEN))%>
            </td>
        </tr>
<%-- Improvement --%>
        <tr align="left">
            <td class="fieldRequired" colspan="2"><display:get name="prm.project.create.wizard.improvement.label" /></td>
            <td class="tableContent" colspan="3">
                <select name="improvementCodeID">
                    <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(net.project.code.ImprovementCode.getAllImprovementCodes(), (projectWizard.getImprovementCode() != null ? projectWizard.getImprovementCode() : ImprovementCode.NO_CHANGE))%>
                </select>
            </td>
            <td>&nbsp;</td>
        </tr>
        
        <tr><td colspan="4">&nbsp;</td></tr>
        
	<%------------------------------------------------------------------------------
							Financial Status
	------------------------------------------------------------------------------%>
		<tr class="channelHeader">
			<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
			<td class="channelHeader" colspan="2"><nobr><display:get name="prm.project.create.wizard.financialcalculation.heading" /></nobr></td>
			<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
		</tr>
		
		<tr><td>&nbsp;</td></tr>
		
		<%-- Default Currency Code --%>
        <tr align="left" class="addSpacingBottom">
            <td colspan="2" class="fieldRequired" nowrap="nowrap">
            	<display:get name="prm.project.create.wizard.defaultcurrency" />:&nbsp;</td>
            <td colspan="3">
                <select name="defaultCurrencyCode">
                    <option value=""><display:get name="prm.project.create.wizard.defaultcurrency.select.option.empty" /></option>
                    <%=Currency.getHtmlOptionList(projectWizard.getDefaultCurrencyCode())%>
                </select>
            </td>
         </tr>

		 <%-- Meta: Type of Expense --%>
		<tr align="left" class="addSpacingBottom">
			<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.meta.typeofexpense" />:&nbsp;</td>
			<td nowrap colspan="3">
				<select name="MetaTypeOfExpense">
					<%= domainList.getValuesOptionListForProperty( new Integer(7), null) %>
				</select>
			</td>
		</tr>
		
		<%-- Cost Center --%>
		<tr align="left" class="addSpacingBottom">			
			<td colspan="2" class="fieldNonRequired" nowrap="nowrap">
				<display:get name="prm.project.create.wizard.costcenter.label" />:&nbsp;</td>
			</td>
			<td class="tableContent">
				<input type="text" name="costCenter" size="40" maxlength="1000" value="">
			</td>			
		</tr>
		
		<%-- Budgeted Total Cost --%>
		<tr align="left" class="addSpacingBottom">
			<td colspan="2" class="fieldNonRequired" nowrap="nowrap">
				<display:get name="prm.project.create.wizard.budgetedtotalcost.label" />:&nbsp;</td>
			</td>
			<td class="tableContent">
				<input:money name="budgetedTotalCost" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>"/>
			</td>
			<td>&nbsp;</td>
		</tr>
		
		<%-- Actual Discretional Cost --%>
		<tr align="left" class="addSpacingBottom">
			<td colspan="2" class="fieldNonRequired" nowrap="nowrap">
				<display:get name="prm.project.create.wizard.actualdiscretionalcost.label" />:&nbsp;
			</td>						
			<td class="tableContent">
		        <input:money name="MetaDiscretionalActualCostToDate" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" />
	        </td>
		   	<td>&nbsp;</td>
		</tr>
		
		<%-- Current Discretional Cost --%>
		<tr align="left" class="addSpacingBottom">
			<td colspan="2" class="fieldNonRequired" nowrap="nowrap">
				<display:get name="prm.project.create.wizard.currentdiscretionalcost.label" />:&nbsp;
			</td>						
			<td class="tableContent">
		        <input:money name="MetaDiscretionalCurrentEstimatedTotalCost" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" />
	        </td>
		   	<td>&nbsp;</td>
		</tr>					
		
		<tr><td>&nbsp;</td></tr>
		
		<tr align="left">
			<td>&nbsp;</td>
			
			<td valign="top" class="fieldRequired">
				<display:get name="prm.project.create.wizard.costcompletion.select" />
			</td>
			
			<td class="tableContent">
			
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td class="tableContent" width="1%">
							<input type="radio" checked onclick="enableCostTextFields(false)" name="MetaCostCalculationMethod" value="automatic" >&nbsp;
						</td>
						<td class="tableContent" valign="top">
						 	<span id="project.schedule.percentComplete">
						 		<display:get name="prm.project.create.wizard.completion.projectcosts.label"/>
						 	</span>
						</td>
					</tr>
					
					<tr>
						<td class="tableContent">&nbsp;</td>
						<td class="tableContent"><display:get name="prm.project.create.wizard.completion.projectcosts.description"/>
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
								<display:get name="prm.project.create.wizard.costs.label" />
							</span>				
						</td>						
					</tr>	
					
					<tr>
						<td class="tableContent">&nbsp;</td>
						<td class="tableContent"><display:get name="prm.project.create.wizard.costs.description"/>
						</td>
					</tr>
					
					<%-- Materials Current Estimated Total Cost --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.create.wizard.materialscurrentestimatedtotalcost.label" />:&nbsp;
						</td>
						<td class="tableContent">
					        <input:money name="MetaMaterialsCurrentEstimatedTotalCost" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" disabled="true"/>
				        </td>
					   	<td>&nbsp;</td>
					</tr>
					
					<%-- Resources Current Estimated Total Cost --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.create.wizard.resourcescurrentestimatedtotalcost.label" />:&nbsp;
						</td>
						<td class="tableContent">
					        <input:money name="MetaResourcesCurrentEstimatedTotalCost" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" disabled="true"/>
				        </td>
					   	<td>&nbsp;</td>
					</tr>
					
					<%-- Materials Actual Cost To Date --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.create.wizard.materialsactualcosttodate.label" />:&nbsp;
						</td>						
						<td class="tableContent">
						        <input:money name="MetaMaterialsActualCostToDate" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" disabled="true"/>
				        </td>
				        <td>&nbsp;</td>
					</tr>					
					
					<%-- Resources Actual Cost To Date --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.create.wizard.resourcesactualcosttodate.label" />:&nbsp;
						</td>						
						<td class="tableContent">
						        <input:money name="MetaResourcesActualCostToDate" money="<%=new Money()%>" currency="<%=Currency.getSystemDefaultCurrency()%>" disabled="true"/>
				        </td>
				        <td>&nbsp;</td>
					</tr>					
					
					<%-- Estimated ROI --%>
					<tr align="left">
						<td>&nbsp;</td>
						<td class="fieldNonRequired">
							<display:get name="prm.project.create.wizard.estimatedroi.label" />:&nbsp;
						</td>

						<td class="tableContent">
								<input type="text" name="estimatedROI_value" size="20" maxlength="1000" disabled="true"/>%
						</td>
				        <td>&nbsp;</td>
					</tr>
					
						
				</table>
			
			</td>
			<td>&nbsp;</td>
		</tr>
		
		<tr><td>&nbsp;</td></tr>
        
 	<%------------------------------------------------------------------------------
							Other Properties
	------------------------------------------------------------------------------%>       


          <tr align="left" class="channelHeader">
             <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td nowrap colspan="3" class="channelHeader"><display:get name="prm.project.create.wizard.otherproperties.heading" /></td>
          <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
          </tr>
          
          <tr><td>&nbsp;</td></tr>

		<%-- Project Start/End dates --%>
          <tr align="left">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.startdate" />:&nbsp;</td>
            <td nowrap colspan="3">
            <input name="startDateString" size="20" maxlength="40" value='<jsp:getProperty name="projectWizard" property="startDateString" />' />
			<util:insertCalendarPopup fieldName="startDateString"/>
            </td>
          </tr>
          <tr align="left">
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.enddate" />:&nbsp;</td>
            <td nowrap colspan="3">
            <input name="endDateString" size="20" maxlength="40" value='<jsp:getProperty name="projectWizard" property="endDateString" />' />
			<util:insertCalendarPopup fieldName="endDateString"/>
            </td>
          </tr>
          
          <tr><td>&nbsp;</td></tr>
          
</table>

<br clear="all">

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
		<tb:band name="action">
			<tb:button type="cancel" />
			<tb:button type="back" show="<%=displayBackButton%>" />
			<tb:button type="next" function="javascript:submit();" />
		</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
