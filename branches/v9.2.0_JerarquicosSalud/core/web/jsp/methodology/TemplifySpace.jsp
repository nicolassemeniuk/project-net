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
    import="net.project.base.property.PropertyProvider,
			net.project.security.Action,
			net.project.base.Module,
		    net.project.security.SessionManager,
            net.project.space.SpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domainList" class="net.project.methodology.DomainListBean" scope="page"/>
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" /> 

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String refLink = null;	
	refLink = request.getParameter("refLink");

    // Fix for eliminating Schedule checkbox when creating template from Business Space
    // Fix for eliminating Process checkbox when creating templatate from Business Space
    boolean isIncludeSchedule = true;
    boolean isIncludeProcess = true;
    String currentSpaceType;
    if ((currentSpaceType = request.getParameter("currentSpaceType")) != null) {
        if (SpaceTypes.BUSINESS.equals(SpaceTypes.getForID(currentSpaceType))) {
            isIncludeSchedule = false;
            isIncludeProcess = false;
        }
    }

    methodologySpace.clear();
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<template:getSpaceCSS />


<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
    
 function setup() {
    theForm = self.document.forms["templifySpace"];
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_templify";
	openwin_help(helplocation);
}

function cancel() {
<%	if (refLink != null && refLink.length() > 0) { %>
	self.document.location = JSPRootURL + "<%=refLink%>";
<%	} else { %>
	self.document.location = JSPRootURL + "/methodology/Actions.jsp?module=<%=Module.METHODOLOGY_SPACE%>";
<%	} %>
}

function reset() { theForm.reset(); }

 function processForm(myForm)
 {
 	theAction('submit');
 	if(!checkTextbox(myForm.name,'<%=PropertyProvider.get("prm.template.templifyspace.namerequired.message")%>')) return false;
 	if(!checkTextbox(myForm.useScenario,'<%=PropertyProvider.get("prm.template.templifyspace.usescenariorequired.message")%>')) return false; 
	return true;
 }

function submitIndustry() 
{
   theAction("submitIndustry");
   theForm.submit();
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


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.template.name">
	<tb:setAttribute name="leftTitle">
		<display:get name="prm.template.create.title" />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="TemplifySpaceProcessing.jsp" name="templifySpace">
    <input type="hidden" name="module" value="<%=Module.METHODOLOGY_SPACE%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="refLink" value="<%=refLink%>" />	
  
<table border="0" align="left" width="97%" cellpadding="0" cellspacing="0">
  <tr align="left" class="channelHeader"> 
    <td width=1% align="left"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    <td nowrap colspan="2" class="channelHeader" align="left">&nbsp;<display:get name="prm.template.create.section.information" /></td>
    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
  </tr>
   <tr>
	   	<td width="10%">&nbsp;</td>
	   	<td colspan="2" align="left">
	   	    <errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
            <%-- Provide a div for server round-trip error messaging --%>
            <div id="errorLocationID" class="errorMessage"></div>
	   	</td>
	   </tr>
  <tr align="left"> 
	<td nowrap>&nbsp;</td>
	
	<%if(user.getCurrentSpace().getType().equals(SpaceTypes.BUSINESS_SPACE)){ %>
	    <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.businessname" />:</td>
	 <%} else { %>
	    <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.projectname" />:</td>
	 <%} %>   
    <td nowrap class="tableContent"><%=user.getCurrentSpace().getName()%></td>
	<td nowrap>&nbsp;</td>
  </tr>
  <tr align="left"> 
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldRequired"><display:get name="prm.template.create.templatename" />:&nbsp;</td>
    <td nowrap> 
      <input name="name" size="45" maxlength="80" VALUE="<c:out value="${getName}"/>">
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
  <tr align="left"> 
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.description" />:&nbsp;</td>
    <td nowrap> 
      <input type="text" name="description" maxlength="80" size="45" VALUE="<c:out value="${getDescription}"/>">
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
  <tr align="left"> 
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldRequired"><display:get name="prm.template.create.owner" />:&nbsp;</td>
    <td nowrap> 
      <select name="parentSpaceID" onchange="checkWorkspaceSelection(this.value);">
        <option value="<%=user.getID()%>"><display:get name="prm.template.create.personalspace.label"/></option>
        <%=domainList.getAvailableBusinessOptionList( user.getID(), methodologySpace.getParentSpaceID() ) %>
      </select>
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
<%-- Global visibility for templates means everyone in the system can see the template. --%>
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
    <td nowrap colspan="4">&nbsp;</td>
  </tr>
  <tr align="left">
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldRequired" valign="top"><display:get name="prm.template.create.usescenario" />:</td>
    <td nowrap>
      <textarea name="useScenario" cols="60" rows="7"><c:out value="${getUseScenario}"/></textarea>
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
</table>

<br clear="all" />
<br />
<%-- NOT SUPPORTED CURRENTLY
<table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
  <tr align="left" class="channelHeader">
    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    <td nowrap colspan="2" class="channelHeader" align="left">Other Information</td>
    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
  </tr>
  <tr align="left">
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldRequired">Industry:</td>
    <td nowrap>
      <select name="industryID" onChange="submitIndustry();">
        <option>None</option>
        <%=domainList.getIndustryOptionList(methodologySpace.getIndustryID())%>
      </select>
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
  <%

      if (methodologySpace.getIndustryID() != null && !methodologySpace.getIndustryID().equals("")) {
%>
  <tr align="left">
	<td nowrap>&nbsp;</td>
    <td nowrap class="fieldNonRequired">Category:</td>
    <td>
      <select name="categoryID">
        <%=domainList.getCategoryOptionList (methodologySpace.getIndustryID())%>
      </select>
    </td>
	<td nowrap>&nbsp;</td>
  </tr>
  <%
	  }
 %>
--%>

<table border="0" align="left" width="97%" cellpadding="0" cellspacing="0">
    <tr align="left" class="channelHeader">
      <td width=1% align="left"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
      <td nowrap colspan="2" class="channelHeader" align="left">&nbsp;<display:get name="prm.template.create.section.selectmodules" /></td>
      <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
    </tr>
    <tr align="left">
	<td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.security" />:</td>
      <td nowrap>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.SECURITY%>">
      </td>
	<td nowrap>&nbsp;</td>
    </tr>
    <tr align="left"> 
	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.document" />:</td>
      <td nowrap>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.DOCUMENT%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left"> 
  	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.form" />:</td>
      <td>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.FORM%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
<%	if (isIncludeProcess) { %>
    <tr align="left"> 
  	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.process" />:</td>
      <td>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.PROCESS%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
<%	} %>
    <tr align="left"> 
  	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.workflow" />:</td>
      <td>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.WORKFLOW%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
  	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.discussion" />:</td>
      <td>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.DISCUSSION%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
    <%  if (isIncludeSchedule) { %>
    <tr align="left">
  	  <td nowrap>&nbsp;</td>
      <td nowrap class="fieldNonRequired"><display:get name="prm.template.create.select.schedule" />:</td>
      <td>
        <input type="checkbox" name="selectedModule" value="<%=net.project.base.Module.SCHEDULE%>">
      </td>
  	  <td nowrap>&nbsp;</td>
    </tr>
    <%  } %>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
