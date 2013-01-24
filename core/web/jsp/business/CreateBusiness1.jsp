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
    info="New Business Wizard -- page 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.business.BusinessCreateWizard,
            net.project.methodology.MethodologyProvider,
            net.project.project.DomainListBean,
            net.project.security.User, 
            net.project.security.SessionManager,
            net.project.security.SecurityProvider,
            net.project.space.Space,
            net.project.space.SpaceRelationship,
            net.project.util.JSPUtils"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="businessWizard" class="net.project.business.BusinessCreateWizard" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page"/>
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" /> 
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<%
	// No security validation necessary since business can be created by any user
	String methodologyID = request.getParameter("methodologyID") != null ? request.getParameter("methodologyID") : "";
%>

<%-- Import Javascript --%>

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:getSpaceJS space="business" />
<script language="javascript">

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	var currentSpaceTypeForBlog = 'person';
    var currentSpaceIdForBlog = '<%= SessionManager.getUser().getID() %>';
    
	function setup() {
		load_menu('<%=user.getCurrentSpace().getID()%>');
	
		theForm = self.document.forms[0];
		isLoaded = true;
		if(theForm.parentSpaceID.value != ''){
			theForm.parentSpaceID.disabled = true;
		}
		preselectTemplate('<%= methodologyID %>');
	}

	function cancel() {
		if('<%= request.getParameter("parent")%>'=='null'){
			self.document.location = JSPRootURL + "/business/BusinessPortfolio.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";
		}else{
			self.document.location = JSPRootURL + "/business/subbusiness/Main.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";
		}
	}

	function submit() {
		if(validate()) {
			theAction("submit");
			theForm.submit();
		}
	}

	function reset() {
		theForm.reset();
	}

	function help() {
		var helplocation=JSPRootURL+"/help/Help.jsp?page=business_create";
		openwin_help(helplocation);
	}
	
	function validate() {
		if(!checkTextbox(theForm.name,'<display:get name="prm.business.create.wizard.step1.businessnamerequired.message" />')) {
			return false;
		}
		if(!checkMaxLength(theForm.description, 1000, '<display:get name="prm.business.create.wizard.step1.businessdescriptionlength.message" />')) {
			return false;
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

<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.business" showAll="true" leftTitle='<%= PropertyProvider.get("prm.business.create.wizard.step1.lefttitle.label") %>' rightTitle='<%= PropertyProvider.get("prm.business.create.wizard.step1.righttitle.label") %>' space="business">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------
	--  Configure beans                                                                                                                                          
	----------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%
    businessWizard.clear();
	if (request.getParameter("parent") != null)
		businessWizard.setParentSpaceID( request.getParameter("parent"));

    if (JSPUtils.isEqual(request.getParameter("createRelationship"), "1")) {
        businessWizard.setRelationship((SpaceRelationship)request.getSession().getValue("relatedBusinessRelationship"));
        businessWizard.setRelatedSpace((Space)request.getSession().getValue("relatedBusinessParent"));
    } else {
        businessWizard.setRelationship(null);
        businessWizard.setRelatedSpace(null);
    }
%>

<form method="post" action="CreateBusiness1Processing.jsp" name="createBusiness1">
	<input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=securityProvider.getCheckedModuleID()%>">
	<input type="hidden" name="parent" value="<%=request.getParameter("parent")%>">
<table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
<tr align="left" class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td nowrap colspan="4" class="channelHeader"><display:get name="prm.business.create.wizard.step1.channel.generalinformation.title" /></td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>

<tr align="left" class="tableContent"> 
	<td nowrap colspan="6" class="tableContent">&nbsp;</td>
</tr>

<%-- Business Name --%>
<tr align="left" class="addSpacingBottom">
	<td>&nbsp;</td>  
	<td nowrap class="fieldRequired" width="20%"><display:get name="prm.business.create.wizard.step1.name.label" /></td>
	<td nowrap class="tableContent"  colspan="2">
		<input type="text" name="name" size="40" maxlength="80" value='<c:out value="${businessWizard.name}"/>'>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>
  
<%-- Business Type --%>
<tr align="left" class="addSpacingBottom">
	<td>&nbsp;</td> 
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step1.type.label" /></td>
	<td nowrap class="tableContent" colspan="2">
		<input type="text" name="flavor" size="40" maxlength="80" value='<c:out value="${businessWizard.flavor}"/>'>
	</td>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>
  
 <%-- Sub Business --%>
 <% if (PropertyProvider.getBoolean("prm.business.subbusiness.isenabled")) { %>
<tr align="left" class="addSpacingBottom"> 
	<td>&nbsp;</td> 
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.parentbusiness" />:&nbsp;</td>
	<td class="tableContent" colspan="2"> 
		<select name="parentSpaceID">
			<option value=""><display:get name="prm.business.create.wizard.parentbusiness.none" /></option>
			<%= domainList.getAvailableBusinessOptionList( user.getID(), businessWizard.getParentSpaceID(), null) %>
		</select>
	</td>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>
<% } else { %>
			<input type="hidden" name="parentSpaceID" value="">
<% } %>

		 
<%-- Business Description --%>
<tr align="left" class="tableContent"> 
	<td nowrap colspan="6" class="tableContent">&nbsp;</td>
</tr>
<tr align="left"> 
	<td>&nbsp;</td>
	<td nowrap colspan="5" class="fieldNonRequired">
		<display:get name="prm.business.create.wizard.step1.description.label" /><br>
		<textarea name="description" cols="50" rows="3" wrap="virtual"><c:out value="${businessWizard.description}"/> </textarea>
	</td>
</tr>
<tr align="left" class="tableContent"> 
	<td nowrap colspan="6" class="tableContent">&nbsp;</td>
</tr>

<display:if name="prm.business.methodology.isenabled">
<tr align="left"> 
            <td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.project.create.wizard.applymethodology" />:&nbsp;</td>
            <td colspan="3"> 
              <select name="methodologyID" id="methodologyID">
                <option SELECTED value=""><display:get name="prm.project.create.wizard.methodology.none" /></option>
                <%= methodologyProvider.getMethodologyOptionListForUser(user) %>
              </select><%-- <a href="javascript:methodologyBrowser()">Methodology Browser</a>--%>
           </td>
        </tr>	
          <tr align="left"> 
            <td nowrap colspan="5">&nbsp;</td>
</tr>
</display:if>

</table> 

<br clear="all">

<%-----------------------------------------------------------------------------------------------------------
    -- Action Bar                                                                                         
	--------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="next" function="javascript:submit();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
