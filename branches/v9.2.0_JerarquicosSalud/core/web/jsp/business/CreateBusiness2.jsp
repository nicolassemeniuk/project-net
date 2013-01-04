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
|   $Revision: 20062 $
|       $Date: 2009-10-06 05:04:10 -0300 (mar, 06 oct 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="New Business Wizard -- page 2"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.business.BusinessCreateWizard,
    net.project.security.User,
    net.project.base.property.PropertyProvider,
    net.project.security.SessionManager,
    net.project.resource.ProfileCodes,
    net.project.methodology.MethodologyProvider,
    net.project.security.SecurityProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="businessWizard" class="net.project.business.BusinessCreateWizard" scope="session" />
<jsp:useBean id="profileCodes" class="net.project.resource.ProfileCodes" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" /> 
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkUrl.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:getSpaceJS space="business" />
<script language="javascript">

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	var currentSpaceTypeForBlog = 'person';
    var currentSpaceIdForBlog = '<%= SessionManager.getUser().getID() %>';
    
	window.history.forward(-1);
	
	function setup() {
	   load_menu('<%=user.getCurrentSpace().getID()%>');
	   theForm = self.document.forms[0];
	   isLoaded = true;
	}

	function cancel() {
		if('<%= request.getParameter("parent")%>'=='null'){
			self.document.location = JSPRootURL + "/business/BusinessPortfolio.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";
		}else{
			self.document.location = JSPRootURL + "/business/subbusiness/Main.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";
		}
	}

	function submit() {
		if(validate()){
			theAction("submit");
			theForm.submit();
		}
	}

	function isValidDigit(num) {
		if((num != null) && (num != "")) {
			var digits = "0123456789- ";
			for(var i=0; i < num.length; i++) {
				if(digits.indexOf(num.charAt(i)) == -1) {
					return false;
				}
			}
		}
		return true;
	}

	function validate() {
		if(theForm.postalCode.value != null && theForm.postalCode.value != "" && !checkPostalCode(theForm.postalCode, "<display:get name='prm.business.create.wizard.step2.invalidpostalcode.message'/>")) {
			return false;
		}		
		if(theForm.phone.value != null && theForm.phone.value != "" && !checkPhoneNo(theForm.phone, "<display:get name='prm.business.create.wizard.step2.invalidphoneno.message'/>")){
			return false;
		}		
		if(theForm.fax.value != null && theForm.fax.value != "" && !checkPhoneNo(theForm.fax, "<display:get name='prm.business.create.wizard.step2.invalidfaxno.message'/>")){
			return false;
		}		
		if(!isURL(theForm.website.value)) {
			var errorMessage = "<display:get name='prm.business.create.wizard.step2.websiteurl.message'/>";
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
			return false;
		}
		return true;
	}

	function reset() {
		theForm.reset();
	}
	
	function help() {
		var helplocation=JSPRootURL+"/help/Help.jsp?page=business_create";
		openwin_help(helplocation);
	}
   
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.business" showAll="true" leftTitleToken="@prm.business.create.wizard.step2.lefttitle.label" rightTitleToken="@prm.business.create.wizard.step2.righttitle.label" space="business">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="CreateBusiness2Processing.jsp" >
	<input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=securityProvider.getCheckedModuleID()%>">
	<input type="hidden" name="parent" value="<%=request.getParameter("parent")%>">
<table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
          <tr align="left" class="channelHeader">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
 		<td nowrap colspan="4" class="channelHeader">&nbsp;<display:get name="prm.business.create.wizard.step2.channel.address.title" /></td>
 		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>

		
          </tr>
          <tr > 
          <td>&nbsp;</td>
            <td nowrap class="fieldRequired"><display:get name="prm.business.create.wizard.businessname.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"><%=net.project.util.HTMLUtils.escape(businessWizard.getName())%>&nbsp;</td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.address1.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="address1" size="40" maxlength="80" value='<c:out value="${businessWizard.address1}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.address2.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="address2" size="40" maxlength="80" value='<c:out value="${businessWizard.address2}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td >&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.address3.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="address3" size="40" maxlength="80" value='<c:out value="${businessWizard.address3}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom">
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.city.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="city" size="25" maxlength="80" value='<c:out value="${businessWizard.city}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom">
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.state.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
		<select name="provinceCode">
             <%= profileCodes.getStateCodeOptionList( businessWizard.getProvinceCode() ) %>
       </select>
              <!--input type="text" name="state" size="10" maxlength="20"-->
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.postalcode.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="postalCode" size="15" maxlength="20" value='<c:out value="${businessWizard.postalCode}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="tableContent,addSpacingBottom">
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.country.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
		<select name="countryCode">
             <%= profileCodes.getCountryCodeOptionList( businessWizard.getCountryCode() ) %>
		</select>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="tableContent"> 
            <td nowrap colspan="6" class="tableContent">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.businessphone.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="phone" size="20" maxlength="20" value='<c:out value="${businessWizard.phone}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom">
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.fax.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="fax" size="20" maxlength="20" value='<c:out value="${businessWizard.fax}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="addSpacingBottom"> 
          <td>&nbsp;</td>
            <td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.step2.website.label" /></td>
            <td nowrap class="tableContent">&nbsp;</td>
            <td nowrap class="tableContent"> 
              <input type="text" name="website" size="40" maxlength="160" value='<c:out value="${businessWizard.website}" />'>
            </td>
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="tableContent"> 
            <td nowrap colspan="6" class="tableContent">&nbsp;</td>
          </tr>
</table>

<br clear="all">

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" function="javascript:history.back();" />
		<tb:button type="finish" function="javascript:submit();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>


</body>
</html>
