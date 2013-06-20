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
    info="Business Modify Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
            	net.project.business.BusinessSpaceBean,
            	net.project.financial.FinancialSpaceBean,
            	net.project.security.SessionManager,
            	net.project.security.SecurityProvider,
            	net.project.resource.ProfileCodes,
				net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="financialSpace" class="net.project.financial.FinancialSpaceBean" scope="session" />
<jsp:useBean id="profileCodes" class="net.project.resource.ProfileCodes" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" />

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.BUSINESS_SPACE%>"
					   objectID="" /> 

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkUrl.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = "<%= SessionManager.getJSPRootURL() %>";

function setup() {
        load_menu('<%=user.getCurrentSpace().getID()%>');
        theForm = self.document.mainForm;
        isLoaded = true;
}

function cancel() {
   var theLocation = JSPRootURL + "/business/Main.jsp";
   self.document.location = theLocation;
}

function reset() {
	theForm.reset();
}
 
function security() {
        var m_url = JSPRootURL + "/process/secureCheck.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.MODIFY_PERMISSIONS%>&id=<%=businessSpace.getID()%>";
        var link_win = openwin_security("security");
        link_win.document.location = m_url;
        link_win.focus();
}

function submit () {
	if (validate(document.mainForm)) {
	        theAction("submit");
	        theForm.submit();
	}
}
 
function addLogo() {
  // var logoWin = openwin_small("logo_win");  
  // logoWin.document.location = "AddLogo.jsp?module=<%= net.project.base.Module.BUSINESS_SPACE %>" +
  //                            "&action=<%=net.project.security.Action.MODIFY%>";
  // Above code commented because of JS error
   var url = "AddLogo.jsp?module=<%= net.project.base.Module.BUSINESS_SPACE %>" +
                              "&action=<%=net.project.security.Action.MODIFY%>";
   var logoWin = openwin_small("logo_win", url);
}
function removeLogo() {
	if(!isNaN('<%= businessSpace.getLogoID()%>')){
	    theAction("removeLogo");
	    theForm.submit();
    }else{
    	extAlert(errorTitle, '<display:get name="prm.business.modify.logoempty.error.message"/>' , Ext.MessageBox.ERROR);
    }	
}

function isValidDigit(num) {
	if((num != null) && (num != "")) {
		var digits = "0123456789-. ";
		for(var i=0; i < num.length; i++) {
			if(digits.indexOf(num.charAt(i)) == -1) {
				return false;
			}
		}
	}
	return true;
}

function validate(frm) {
 	if (!checkTextbox(frm.name, '<display:get name="prm.business.modifybusiness.namerequired.message"/>')) return false;
	if (frm.parentSpaceID.value==<%=businessSpace.getID()%>)
		return errorHandler(frm.parentSpaceID,'<display:get name="prm.business.modifybusiness.businessownervalidation.message"/>');
		
	if(!checkMaxLength(frm.description, 1000, '<display:get name="prm.business.modifybusiness.descriptionlength.message" />'))return false;

	if(!isURL(frm.website.value)) {
		var errorMessage = "<display:get name='prm.business.create.wizard.step2.websiteurl.message'/>";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		return false;
	}
	
	if(frm.postalCode.value != null && frm.postalCode.value != "" && !checkPostalCode(frm.postalCode, "<display:get name='prm.business.create.wizard.step2.invalidpostalcode.message'/>")) {
		return false;
	}	
	if(frm.phone.value != null && frm.phone.value != "" && !checkPhoneNo(frm.phone, "<display:get name='prm.business.create.wizard.step2.invalidphoneno.message'/>")){
		return false;
	}	
	if(frm.fax.value != null && frm.fax.value != "" && !checkPhoneNo(frm.fax, "<display:get name='prm.business.create.wizard.step2.invalidfaxno.message'/>")){
		return false;
	}	
	return true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=business_modify"
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" bgcolor="#FFFFFF" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.space.business">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		<!--tb:button type="security" /-->
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------------------------------------------------------------------
	--  Configure beans                                                                                                                                          
	----------------------------------------------------------------------------------------------------------------------------------------------------------------%>
<%
		// get Parent Business
		net.project.space.Space parentSpace = net.project.space.SpaceManager.getSuperBusiness(businessSpace);
		if (parentSpace != null) {
			businessSpace.setParentSpaceID(parentSpace.getID());
		}
%>


<form name="mainForm" method="POST" action="ModifyBusinessProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="action" VALUE="<%=net.project.security.Action.MODIFY%>">
<input type="hidden" name="module" VALUE="<%=net.project.base.Module.BUSINESS_SPACE%>">
<%if(request.getParameter("referer") != null){ %> 
	<input type="hidden" name="referer" VALUE="<%=request.getParameter("referer")%>">
<%} %>
<table border=0 cellpadding=0 cellspacing=0 width="600">
<tr><td>
<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
<tr align="left" class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td nowrap colspan="4" class="channelHeader"><display:get name="prm.business.modifybusiness.channel.modify.title"/></td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
	
<%-- Business Name --%>	
<tr align="left">
	<td>&nbsp;</td>  
	<td nowrap class="fieldRequired"><display:get name="prm.business.modifybusiness.businessname.label"/></td>
	<td nowrap class="tableContent"  colspan="2">
		<input:text name="name" size="40" maxLength="80" value="<%=businessSpace.getName() %>" />
	</td>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>
  
<%-- Business Type --%>	
<tr align="left" >
	<td>&nbsp;</td> 
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.businesstype.label"/></td>
	<td nowrap class="tableContent" colspan="2">
		<input type="text" name="flavor" size="40" maxlength="80" VALUE='<c:out value="${businessSpace.flavor}" />'>
	</td>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>

<%-- Sub Business --%>
<input type="hidden" name="previousParentSpaceID" value="<%= businessSpace.getParentSpaceID() != null ? businessSpace.getParentSpaceID() : "" %>">
 <% if (PropertyProvider.getBoolean("prm.business.subbusiness.isenabled")) { %>
<tr align="left"> 
	<td>&nbsp;</td> 
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.create.wizard.parentbusiness" />:&nbsp;</td>
	<td class="tableContent" colspan="2"> 
		<select name="parentSpaceID">
			<option value=""><display:get name="prm.business.create.wizard.parentbusiness.none" /></option>
			<%= domainList.getAvailableBusinessOptionList( user.getID(), businessSpace.getParentSpaceID(), businessSpace.getID() ) %>
		</select>
	</td>
	<td nowrap class="tableContent" colspan="2">&nbsp;</td>
</tr>
<% } else { %>
			<input type="hidden" name="parentSpaceID" value="">
<% } %>


<%-- Business Description --%>	
<tr align="left"> 
	<td>&nbsp;</td>
	<td nowrap colspan="5" class="fieldNonRequired">
		<display:get name="prm.business.modifybusiness.description.label"/><br>
		<textarea name="description" cols="80" rows="3" wrap="virtual"><c:out value="${businessSpace.description}" /></textarea>
	</td>
</tr>

<%-- Business Logo --%>	
<tr align="left" >
	<td> &nbsp;</td>
	<td nowrap width="20%" class="fieldNonRequired"><display:get name="prm.business.modifybusiness.businesslogo.label"/></td>
	<td nowrap class="tableContent">
		<%
		if (businessSpace.getLogoID() != null) {
		%>
			<a href="javascript:addLogo()"><IMG id="projectLogo" name="projectLogo" SRC="<%=SessionManager.getJSPRootURL()%>/servlet/photo?id=<%=businessSpace.getID()%>&size=medium&logoType=blogo&module=<%=net.project.base.Module.DIRECTORY%>" width="40px" border=0></a>
		<%
		} else {
		%>
			<a href="javascript:addLogo()"><IMG id="projectLogo" name="projectLogo" SRC="#" width=0 height=0 border=0></a>		
		<%
		} 
		%>		
		&nbsp;&nbsp;<b><a href="javascript:addLogo()"><display:get name="prm.business.modifybusiness.changelogo.link" /></a></b> <b><a href="javascript:removeLogo()"><display:get name="prm.business.modifybusiness.removelogo.link" /></a></b><br> <span class="fieldNonRequired"><display:get name="prm.business.modifybusiness.editlogo.instruction" /></span><td>
	<td>&nbsp;</td>
</tr>

<tr class="tableContent"> 
	<td nowrap colspan="6" class="tableContent">&nbsp;</td>
</tr>

<%-----------------------------------------------------------------------------------------------------------------------------------------------
     --  Address Fields                                                                                                                    
	 -------------------------------------------------------------------------------------------------------------------------------------------%>	
<tr align="left" class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td nowrap colspan="4" class="channelHeader"><display:get name="prm.business.modifybusiness.channel.address.title"/></font></td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>

<tr > 
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.address1.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="address1" size="40" maxlength="80" value='<c:out value="${businessSpace.address1}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr > 
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.address2.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="address2" size="40" maxlength="80" value='<c:out value="${businessSpace.address2}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr > 
	<td >&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.address3.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="address3" size="40" maxlength="80" value='<c:out value="${businessSpace.address3}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr>
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.city.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="city" size="25" maxlength="80" value='<c:out value="${businessSpace.city}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr >
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.state.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<select name="provinceCode">
	<%= profileCodes.getStateCodeOptionList( businessSpace.getProvinceCode() ) %>
	</select>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr> 
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.postalcode.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="postalCode" size="15" maxlength="20" value='<c:out value="${businessSpace.postalCode}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr class="tableContent">
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.country.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<select name="countryCode">
	<%= profileCodes.getCountryCodeOptionList( businessSpace.getCountryCode() ) %>
	</select>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr class="tableContent"> 
	<td nowrap colspan="6" class="tableContent">&nbsp;</td>
</tr>
	
	<tr > 
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.phone.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="phone" size="20" maxlength="20" value='<c:out value="${businessSpace.phone}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr >
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.fax.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="fax" size="20" maxlength="20" value='<c:out value="${businessSpace.fax}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>

<tr> 
	<td>&nbsp;</td>
	<td nowrap class="fieldNonRequired"><display:get name="prm.business.modifybusiness.website.label"/></td>
	<td nowrap class="tableContent" colspan="2"> 
	<input type="text" name="website" size="40" maxlength="160" value='<c:out value="${businessSpace.website}" />'>
	</td>
	<td colspan="2">&nbsp;</td>
</tr>
</table>

</div>
</td>
</tr>
</table>

<%-------------------------------------------------------------------------------------------------------------------------------------
    -- Action Bar                                                                                                                    
---------------------------------------------------------------------------------------------------------------------------------%>
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
