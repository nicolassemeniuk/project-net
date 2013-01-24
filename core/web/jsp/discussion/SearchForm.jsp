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
|     $Author: umesha $
|
|   This page implements the search form used for finding posts
|
|   
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Search form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.security.SecurityProvider,
			net.project.security.User,
			net.project.space.Space,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" 
					   objectID="<%=discussion.getID()%>"/> 
<html>
<head>
<title><%=PropertyProvider.get("prm.discussion.searchformpage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
function SubmitFindForm()
   {
   var errorMsg;
   if (document.findPost.name.value== "" &&
       document.findPost.subject.value=="" &&
       document.findPost.body.value=="")
      {
      L_ValidData_Message = '<%=PropertyProvider.get("prm.discussion.searchform.enterinfo.message")%>';
      extAlert(errorTitle, L_ValidData_Message , Ext.MessageBox.ERROR);
      document.findPost.name.focus();
      }
    else
      document.findPost.submit();
   }
</script>

</head>
<body  class="main">
<form name="findPost" action="SearchResults.jsp" onSubmit="SubmitFindForm();" method="POST" TARGET=searchForm_results>
<input type=hidden name="module" value="<%= net.project.base.Module.DISCUSSION %>">
<input type=hidden name="action" value="<%= net.project.security.Action.VIEW %>">
<input type=hidden name="id" value="<jsp:getProperty name="discussion" property="id" />">
<input type=hidden name=mode value=search>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td>&nbsp;</td></tr>

	<tr  class="channelHeader">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    		<td class="channelHeader" align="left" colspan="2"><%=PropertyProvider.get("prm.discussion.searchform.channel.findposts.title")%></td>
    		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>

  	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td width=25% align=right class="tableHeader">
		<nobr><%=PropertyProvider.get("prm.discussion.searchform.from.label")%></nobr>
		</td>

		<td valign=bottom align=left>
		<input TYPE=TEXTBOX name="name" value="" MAXLENGTH=256 size=40>
		</td>
		<td>&nbsp;</td>
	
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td width=25% align=right class="tableHeader">
		<nobr><%=PropertyProvider.get("prm.discussion.searchform.subject.label")%></nobr>
		</td>		
	
		<td valign=bottom align=left>
		<input TYPE=TEXTBOX name="subject" value="" MAXLENGTH=256 size="40">
		</td>
		<td>&nbsp;</td>

	</tr>
	<tr>
		<td>&nbsp;</td>
		<td width=25% align=right class="tableHeader">
		<nobr><display:get name="prm.discussion.searchform.body.label" /></nobr>
		</td>

		<td valign=bottom align=left>
		<input TYPE=TEXTBOX name="body" value="" MAXLENGTH=256 size="40">
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" function="javascript:parent.close();" />
		<tb:button type="search" function="javascript:SubmitFindForm();"/>
	</tb:band>
</tb:toolbar>

</form>

<script language="JavaScript">
   document.findPost.name.focus()
</script>

</body>
</html>
