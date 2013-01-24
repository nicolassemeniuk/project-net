<%--
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form List" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.Date,
			java.text.DateFormat" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formList" class="net.project.form.FormList" scope="page" />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%
	// Set the context for the FormList.
	formList = form.getDisplayList();
	formList.setCurrentSpace(user.getCurrentSpace());
	formList.loadData();
	String spaceName = user.getCurrentSpace().getName();
	String when = user.getDateFormatter().formatDate(new Date(), DateFormat.FULL);
%>
<template:getSpaceCSS />
<style> 

.tableContentHighlight {
	background-color : #FFFFFF;
}

a {
	color : #000000;
	text-decoration: none;
	cursor: default;
}
.tableLine {
	padding-top: 5px;
	padding-bottom: 5px;
	background-color : #FFFFFF;
	border-bottom:1px solid #CCD8FE;
}
</style>
<script language="javascript">
function submit(btn) {
	// dont print button on pritout !!
	btn.style.visibility = 'hidden';
    window.print();	
	self.close();
}
function modify(id) {}
function sort(sortBy) {}
</script>
</head>
<body > 
<%--------------------------------------------------------------------------------------------
  -- Page Content                                                                             
  ------------------------------------------------------------------------------------------%>
<input type='button' id="btn" value='<display:get name="prm.form.designer.main.print.label" />' onclick="submit(this);">
<br/>

<table border="0" width="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="3">
			 <table border="0" cellpadding="1" cellspacing="1"  width="100%">
			  <tr>
				<td width="5%">&nbsp;</td>
				<td >
				<span class="tableHeader"><display:get name="prm.project.main.project.label" />:</span> <span class="tableContentFontOnly"><%= spaceName %></span></td>
				<td align="right">&nbsp;<span class="tableContentFontOnly"><%=when%></span></td>
			  </tr>
			  <tr>
				<td width="5%">&nbsp;</td>
				<td width="100%" colspan="2"><span class="tableHeader"><display:get name="prm.global.search.formdata.results.channel.title" />:</span> <span class="tableContentFontOnly">[<jsp:getProperty name="form" property="abbreviation" />] <jsp:getProperty name="form" property="name" /></span></td>
			  </tr>
			  <tr>
				<td width="5%">&nbsp;</td>
				<td width="100%" colspan="2"><span class="tableHeader"><display:get name="prm.form.list.listview.label" /></span> <span class="tableContentFontOnly"><%= formList.getName() %></span></td>
			  </tr>
			</table>
		</td>
	</tr>

	<tr>
		<td ><img height="1" width="1" border="0" alt="" src="/pnet/images/spacers/trans.gif"/></td>
		<td class="tableLine" ><img height="1" width="1" border="0" alt="" src="/pnet/images/spacers/trans.gif"/></td>
		<td ><img height="1" width="1" border="0" alt="" src="/pnet/images/spacers/trans.gif"/></td>
	</tr>
	<tr>
		<td >&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<% 
				// Display the formlist.
				formList.setIncludeHtmlSelect(false);
				formList.writeHtml(new java.io.PrintWriter(out));
			%>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
</body>
</html>
