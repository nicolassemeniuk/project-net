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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Business Team Member" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
    	    net.project.security.SecurityProvider,
    		net.project.security.SessionManager,
    		net.project.resource.Person,
			net.project.base.property.PropertyProvider,
    		net.project.resource.RosterBean,
    		net.project.base.Module" 
%>
<jsp:useBean id="rosterPerson" class="net.project.resource.Person" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	String id = request.getParameter("id");
	String memberid = request.getParameter("memberid");
	String props[] = { rosterPerson.getDisplayName() };
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
// We can not use standard security here because person id spans across all spaces.
// Therefore we will only grant edit permission on a member if it is that member or a space
// admin
if (memberid == null ||
	(!user.getID().equals(memberid) &&
     !securityProvider.isUserSpaceAdministrator()))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.business.memberedit.authorizationfailed.message"));

rosterPerson = roster.getPerson(memberid);
%>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  
function setup() {
	page = true;
	load_menu('<%=user.getCurrentSpace().getID()%>');

	theForm = self.document.forms[0];
	isLoaded = true;
	this.focus();
}

function cancel() { self.document.location = JSPRootURL + "/business/Directory.jsp?module=<%=Module.DIRECTORY%>"; }
function reset() { theForm.reset(); }
function submit() { theForm.submit(); }
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_project&section=modify";
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
			<history:page display='<%=PropertyProvider.get("prm.business.memberedit.page.history",props)%>'
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MemberEditProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>">
	<input type="hidden" name="memberid" value="<jsp:getProperty name="rosterPerson" property="ID" />">
			
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
		<td nowrap colspan="4" class="channelHeader">&nbsp;</td>
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.name.label" /></th>
		<td colspan="4" nowrap align="left" class="tableContent"><jsp:getProperty name="rosterPerson" property="fullName" /></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.title.label" /></th>
		<td colspan="4" align="left" class="tableContent">
			 <input type="text" name="title" value="<jsp:getProperty name="rosterPerson" property="title" />" size="60" maxlength="80">
		</td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.responsibilities.label" /></th>
		<td colspan="4" align="left" class="tableContent">
      <input type="text" name="responsibilities" value="<jsp:getProperty name="rosterPerson" property="responsibilities" />" size="60" maxlength="80">
		</td>
	</tr>
	<tr>
		<td colspan="6" class="tableContent">&nbsp;</td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.officephone.label" />Office Phone:</th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="officePhone" /></td>
		<th width="13%" nowrap align="left" class="tableHeader"><display:get name="prm.business.memberedit.email.label" /></th>

  <td width="33%" align="left" class="tableContent" colspan="2">
		<a href="mailto:<jsp:getProperty name="rosterPerson" property="email" />" >
		<jsp:getProperty name="rosterPerson" property="email" /></a>
	  </td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.fax.label" /></th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="faxPhone" /></td>
		<th width="13%" nowrap align="left" class="tableHeader"><display:get name="prm.business.memberedit.mobilephone.label" /></th>
	<td width="33%" align="left" class="tableContent" colspan="2"><jsp:getProperty name="rosterPerson" property="mobilePhone" /></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="tableHeader" colspan="2"><display:get name="prm.business.memberedit.pagerphone.label" /></th>
		<td width="36%" class="tableContent"><jsp:getProperty name="rosterPerson" property="pagerPhone" /></td>
		<th nowrap align="left" width="18%" class="tableHeader"><display:get name="prm.business.memberedit.pageremail.label" /></th>
		<td width="33%" align="left" class="tableContent" colspan="2">
			<a href="mailto:<jsp:getProperty name="rosterPerson" property="pagerEmail" />" >
			<jsp:getProperty name="rosterPerson" property="pagerEmail" /></a>
		</td>
	</tr>
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
