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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Team Member Details" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.RosterBean, 
    		net.project.resource.Person,
    		net.project.security.User, 
    		net.project.security.Action, 
    		net.project.security.SecurityProvider, 
    		net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
    		net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="rosterPerson" class="net.project.resource.Person" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<template:getDoctype />
<html>
<head>

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
roster.setSpace(user.getCurrentSpace());
roster.load();
    
if (request.getParameter("memberid") != null)
    rosterPerson = roster.getPerson(request.getParameter("memberid"));
	
	String[] props = {	rosterPerson.getDisplayName() };
 
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	page = true;
	theForm = self.document.forms[0];
	isLoaded = true;
	this.focus();
}

function cancel() { self.document.location = JSPRootURL + "/business/Directory.jsp?module=<%=Module.DIRECTORY%>"; }
function reset() { self.document.location = JSPRootURL + "/business/MemberView.jsp?module=<%=Module.DIRECTORY%>&memberid="+<%= rosterPerson.getID() %>; }
function modify() { self.document.location = JSPRootURL + "/business/MemberEdit.jsp?module=<%=Module.DIRECTORY%>&action=<%=Action.MODIFY%>&memberid="+<%= rosterPerson.getID() %>; }

function remove() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.business.memberview.personremove.message" />', function(btn) { 
		if(btn == 'yes'){ 
			theAction("remove");
			theForm.submit();
		}else{
		 	return false;
		}
	});
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_project&section=view";
	openwin_help(helplocation);
}


</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.space.business">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%= PropertyProvider.get("prm.business.memberview.page.history",props) %>'
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" labelToken="prm.business.memberview.modify.button.tooltip" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="DirectoryProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
    <input type="hidden" name="action" value="<%=Action.DELETE%>">
    <input type="hidden" name="selected" value="<jsp:getProperty name="rosterPerson" property="ID" />">

<table width="100%" border="0" cellspacing="0" cellpadding="0" >
  <tr class="channelHeader">
  	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td colspan="4" nowrap align="left" class="channelHeader">&nbsp;</td>
 	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>

</tr>
<tr>
	<th nowrap align="left" width="18%" colspan="2" class="tableHeader"><display:get name="prm.business.memberview.name.label" /></th>
	<td colspan="4" nowrap align="left" class="tableContent"><jsp:getProperty name="rosterPerson" property="fullName" /></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberview.title.label" /></th>
	<td colspan="4" align="left" class="tableContent"><jsp:getProperty name="rosterPerson" property="title" /></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberview.responsibilities.label" /></th>
	<td colspan="4" align="left" class="tableContent"><jsp:getProperty name="rosterPerson" property="responsibilities" /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberview.officephone.label" /></th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="officePhone" /></td>
		<th width="13%" nowrap align="left" class="tableHeader"><display:get name="prm.business.memberview.email.label" /></th>

  <td width="33%" align="left" class="tableContent" colspan="2">
		<a href="mailto:<jsp:getProperty name="rosterPerson" property="email" />" >
		<jsp:getProperty name="rosterPerson" property="email" /></a>
	  </td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberview.fax.label" /></th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="faxPhone" /></td>
		<th width="13%" nowrap align="left" class="tableHeader"><display:get name="prm.business.memberview.mobilephone.label" /></th>
	<td width="33%" align="left" class="tableContent" colspan="2"><jsp:getProperty name="rosterPerson" property="mobilePhone" /></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberview.pagerphone.label" /></th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="pagerPhone" /></td>
		<th nowrap align="left" width="18%" class="tableHeader"><display:get name="prm.business.memberview.pageremail.label" /></th>
		<td width="33%" align="left" class="tableContent" colspan="2">
			<a href="mailto:<jsp:getProperty name="rosterPerson" property="pagerEmail" />" >
			<jsp:getProperty name="rosterPerson" property="pagerEmail" /></a>
		</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action" />
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
