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
    info="New Project Wizard -- page 0"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
    		net.project.security.*,
			net.project.space.SpaceURLFactory,
    		net.project.security.User,
			net.project.base.property.PropertyProvider,
			net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />     

<%
	String mySpace=user.getCurrentSpace().getType();
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
	String refLink = (String) pageContext.getAttribute("pnet_refLink" ,  pageContext.SESSION_SCOPE);
	String parentSpaceID = request.getParameter("parent");
%>					   
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="project"/>

<%-- Import Javascript --%>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
    isLoaded = true;
}
function submit() {
    theAction("submit");
    if(processForm(theForm))
    	theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=project_portfolio&section=create";
	openwin_help(helplocation);
}

 function processForm(myForm)
 {

<display:if name="@prm.project.create.authcode.isenabled">

	if(!verifyNonBlankField(myForm.serial.value)) {
	 	var errorMessage = '<display:get name="prm.project.projectcreate.step1.serialnumberrequired.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		return false;
	}

</display:if>	
	return true;
	
 }

 function checkSubscriber(){
    if(document.CREATEPROJ.selectsubscriber[1].checked){
    	var errorMessage = '<display:get name="prm.project.projectcreate.step1.evaluation.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
       	document.CREATEPROJ.serial.value="<%=Crypto.generateVerificationCode(6) %>-<%=user.getID()%>";
    }else{
       document.CREATEPROJ.serial.value="";
    }
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
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label">
    <tb:setAttribute name="leftTitle">
        <display:get name="prm.project.create.wizard.title" />
    </tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" name="CREATEPROJ" action="NewProjectWizard0Processing.jsp" onSubmit="return processForm(this);" >
    <input TYPE=hidden name="module" value="<%= module %>">
    <input TYPE=hidden name="action" value="<%= action %>">
    <input type=hidden name="parent" value='<%= parentSpaceID %>' >
	<input type="hidden" name="theAction">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="600">
    <tr align="left" class="channelHeader">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
        <td colspan="4" class="channelHeader" align=center nowrap>&nbsp;<display:get name="prm.global.display.requiredfield" /></td>
        <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>           
    </tr>
    <tr align="left">
        <td colspan="6">&nbsp;</td>  
    </tr>
          <tr>
          <td colspan="6">
          <textarea cols=65 rows=10 WRAP="Virtual" readonly><display:get name="prm.global.legal.termsofuse.projectcreate" /></textarea>
          </td>
    </tr>
    </tr>
          <tr>
          <td colspan="6">&nbsp;</td>
          </tr>    
<display:if name="@prm.project.create.authcode.isenabled">
    <tr>
    <td colspan="2" class="tableContent">
    <display:get name="prm.project.create.authcode.authorizedusing" />:
    </td>
    <td colspan="4" class="tableContent">
    <input type="radio" name="selectsubscriber" value="yes" id="yes" onclick="checkSubscriber()" checked><label for ="yes"><display:get name="prm.project.create.authcode.type.pnet" /></label>    </td>
    </tr>
    <tr>      <td colspan="2" class="tableContent">&nbsp;</td>
              <td colspan="4" class="tableContent">
              <input type="radio" name="selectsubscriber" value="no" onclick="checkSubscriber()" ID="no"><label for="no"><display:get name="prm.project.create.authcode.type.evaluation" /></label>
              </td>
</tr>             

          <tr>
          <td colspan="6">&nbsp;</td>
          </tr>    
    <tr> 		       
          <td colspan="6" class="tableContent"><display:get name="prm.project.create.authcode.enter" />
    </td>
    </tr>
<% if (session.getValue("errorMsg")!=null) { %>
              <tr>
          <td colspan="6"><font COLOR="#ff0000"><%= session.getValue("errorMsg") %></font></td>
          </tr>    
<%
session.removeValue("errorMsg"); 	 
} 
%>
    <tr align="left"> 
        <td colspan="6"> 

<input name=serial size=45 maxlength=80 value=>

        </td>    
    </tr>
    <tr>
    <td colspan="6">&nbsp;</td>
    </tr>
    <tr>
    <td colspan="6" class="tableContent">
    <display:get name="prm.project.create.authcode.info1" />
    </td>
    </tr>
</display:if>
</table>

<br clear="all">
	
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" label='<%= PropertyProvider.get("prm.project.projectcreate.step1.submit.button.label")%>' />
		
		<tb:button type="cancel" label='<%= PropertyProvider.get("prm.project.projectcreate.step1.cancel.button.label")%>' />
	</tb:band>
</tb:toolbar>
	
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
