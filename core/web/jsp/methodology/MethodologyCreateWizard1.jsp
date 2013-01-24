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
    info="Create Methodology Wizard -- Page 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.methodology.MethodologySpaceBean,
			net.project.security.User,
			net.project.security.Action,
			net.project.base.Module,
            net.project.base.property.PropertyProvider,
			net.project.space.Space,
			net.project.project.DomainListBean,
			net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page"/>
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" /> 

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String mode = request.getParameter("mode");
	if (mode != null && mode.equals("clear")) {
		methodologySpace.clear();
		// Default the parent space to current space if it is a Business space
		if (user.getCurrentSpace().isTypeOf(Space.BUSINESS_SPACE)) {
			methodologySpace.setParentSpaceID(user.getCurrentSpace().getID());
		}
	}

	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink,"UTF-8") : "");
	
	//Avinash:-----check for null refLink ------------------------------------
		if (refLink == null)
			refLink = "";
	//Avinash:----------------------------------------------------------------
%>	
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:getSpaceCSS/>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
    
 function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
    
    checkWorkspaceSelection(theForm.parentSpaceID.value);
}

function submit() { 
    theAction("submit");
    if(processForm(theForm))
    	theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_portfolio&section=create";
	openwin_help(helplocation);
}

function cancel() { 
	self.document.location = JSPRootURL + "<%=(refLink != null && refLink.length() > 0 ? refLink : "/methodology/MethodologyList.jsp?module=" + Module.METHODOLOGY_SPACE)%>";
}

function reset() { theForm.reset(); }

 function processForm(myForm)
 {
 	theAction('submit');
 	if(!checkTextbox(myForm.name,"<%=PropertyProvider.get("prm.template.create.name.required.message")%>")) return false;
 	if(!checkTextbox(myForm.useScenario,"<%=PropertyProvider.get("prm.template.create.usescenario.required.message")%>")) return false;
	return true;
 }

// To hide/unhide global checkbox if personal workspace is selected
function checkWorkspaceSelection(selectedValue){
	var userId = '<%=user.getID()%>';
	if(selectedValue == userId ){
		theForm.isGlobal.checked = false;
		document.getElementById('globalCheckbox').style.display = 'none';
	} else {
		document.getElementById('globalCheckbox').style.display = '';
	}
}

</script>
</head>


<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.template.name" showAll="true" leftTitle='<%=PropertyProvider.get("prm.template.create.title")%>' rightTitle='<%=PropertyProvider.get("prm.template.create.step1")%>'>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MethodologyCreateWizard1Processing.jsp" name="create_methodology">
    <input type="hidden" name="module" value="<%=Module.METHODOLOGY_SPACE%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="refLink" value="<%=refLink%>" />	
  <table border="0" align="left" width="97%" cellpadding="0" cellspacing="0">
    <tr align="left" class="channelHeader"> 
      <td width=1% align="left"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
      <td nowrap colspan="3" class="channelHeader" align="left">&nbsp;<display:get name="prm.template.create.section.information" />&nbsp;&nbsp; 
        <display:get name="prm.global.display.requiredfield" /></td>
      <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td nowrap class="fieldRequired"><display:get name="prm.template.create.templatename" />:&nbsp;</td>
      <td nowrap colspan="2"> 
        <input name="name" size="45" maxlength="80" VALUE="<c:out value="${methodologySpace.name}"/>">
      </td>
	  <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.description" />:&nbsp;</td>
      <td nowrap colspan="2"> 
        <input type="text" name="description" maxlength="1000" size="45" VALUE="<c:out value="${methodologySpace.description}"/>">
      </td>
	  <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td nowrap class="fieldRequired"><display:get name="prm.template.create.owner" />:&nbsp;</td>
      <td colspan="2"> 
        <select name="parentSpaceID" onchange="checkWorkspaceSelection(this.value);">
          <option value="<%=user.getID()%>"><%=PropertyProvider.get("prm.template.createwizard.owner.option.personal.name")%></option>
          <%=domainList.getAvailableBusinessOptionList( user.getID(), methodologySpace.getParentSpaceID(), null ) %> 
        </select>
      </td>
	  <td>&nbsp;</td>
    </tr>
    <% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
    <tr align="left" id="globalCheckbox">
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldNonRequired"><display:get name="prm.methodology.global.label" />&nbsp;</td>
    <td nowrap>
      <input type="checkbox" name="isGlobal">
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
   <% } %>
    <tr align="left"> 
      <td nowrap colspan="5">&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td nowrap class="fieldRequired" valign="top" colspan="3"><display:get name="prm.template.create.usescenario" />:</td>
	  <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td>&nbsp;</td>
      <td nowrap colspan="3"> 
        <textarea name="useScenario" cols="60" rows="7"><c:out value="${methodologySpace.useScenario}"/></textarea>
      </td>
	  <td>&nbsp;</td>
    </tr>
  </table>
  
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="finish" function="javascript:submit();" />
	</tb:band>
</tb:toolbar>
  
</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
