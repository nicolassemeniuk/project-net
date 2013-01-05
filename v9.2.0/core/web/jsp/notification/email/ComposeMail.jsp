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
|   $Revision: 19892 $
|       $Date: 2009-08-31 08:45:51 -0300 (lun, 31 ago 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Compose Email" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<jsp:useBean id="recipients" class="net.project.notification.email.RecipientProvider" scope="session" />


<!--Avinash bhamare:--- storing the previous module which was before this page--->
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<!--------------------------------------------------------------------->
<%
//---Avinash bhamare:----- storing the module action which was before this page------------
	session.setAttribute("prevModule",new Integer(securityProvider.getCheckedModuleID()));
	request.setAttribute("module",""+Module.PERSONAL_SPACE);
	net.project.security.ServletSecurityProvider.setAndCheckValues(request);
//-----------------------------------------------------------------------------------------
%>

<security:verifyAccess action="view"
					   module="<%=Module.PERSONAL_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
	var theForm;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var isLoaded = false;
	
function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   isLoaded = true;
   theForm = self.document.forms["main"];
   focus(theForm, "subject");
}

function cancel() { 
   self.document.location = JSPRootURL + "<%=refLink%>"; 
}

function reset() { 
    theForm.reset();
}

// Sending e-mail 
function submitEmail() {
	if(validate()){
	    theAction("send");   
	    theForm.submit();
    }
}

// Validate the fields of email
function validate(){
	if(theForm.subject.value.trim() == '' && theForm.body.value.trim() == ''){
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.directory.grouplistview.subjectmessageblank.errormessage")%>' , Ext.MessageBox.ERROR);
		return false;
	} else if(theForm.subject.value.trim() == ''){
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.directory.grouplistview.blanksubject.errormessage")%>' , Ext.MessageBox.ERROR);
		return false;
	} else if(theForm.body.value.trim() == ''){
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.directory.grouplistview.blankmessage.errormessage")%>' , Ext.MessageBox.ERROR);
		return false;
	}
	return true;
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=notification&section=compose_email";
    openwin_help(helplocation);
}

</script>

</head>

<body class="main"  onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.notifications.link">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL() + "/notification/email/SendMailProcessing.jsp"%>" >
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%= Module.PERSONAL_SPACE %>">
	<input type="hidden" name="refLink" value="<%=refLink%>" />
    
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
	<tr align="right">
		<th valign="top" align="left" style="font-size:15;border-bottom:1px dotted #80c080; color:#1C68CB; font-weight:bold" colspan="2" ><%=PropertyProvider.get("prm.directory.grouplistview.sendmail.pagetitle")%></th>
		<td align=right></td>
	</tr>
	<tr>
	<td style="padding-left:10px;" width="100%">
	<table cellspacing="3">
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>	
	    <td>&nbsp;</td>
	    <th nowrap align="left" valign="top" class="tableHeader"><%=PropertyProvider.get("prm.directory.grouplistview.sendmail.to.label")%></th>
	     <td>&nbsp;</td>
	    <td class="tableContent">
            <select name="selectedRecipients" multiple size="<%=(recipients.availableCount() < 10 ? recipients.availableCount() : 10)%>">
                <jsp:getProperty name="recipients" property="selectionOptionPresentation" />
            </select>
	    </td>
	    <td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<th nowrap align="left" class="tableHeader"><%=PropertyProvider.get("prm.directory.grouplistview.sendmail.subject.label")%></th>
		 <td>&nbsp;</td>
		<td class="tableContent">
		<input type="text" name="subject" size="60" maxlength="4000" value="">
    	</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
	   <td>&nbsp;</td>
	   <th nowrap align="left" class="tableHeader"></th>
        <td>&nbsp;</td>
       <td class="tableContent" colspan="2">
            <textarea name="body" cols="80" rows="17"></textarea>
    	</td>
		<td>&nbsp;</td>
	</tr>
	</table>
	</td>
	</tr>
	<tr class="memberEditBottom" height="30">
		<td colspan="4" align="center">
		<input type="button" value="<%=PropertyProvider.get("prm.directory.grouplistview.sendmail.button.send.label")%>" onclick="submitEmail();"></input></td>
	</tr>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
