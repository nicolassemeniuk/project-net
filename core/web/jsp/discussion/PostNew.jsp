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
|   $Revision: 19700 $
|       $Date: 2009-08-11 11:11:07 -0300 (mar, 11 ago 2009) $
|     $Author: dpatil $
|
|   This page is used to create a new post.
|
|   Beans:
|   user        the current user (should already be in the session)
|   discussion  the current discussion group (should already be in the session)
|   
|   Parameters:
|   reply       the id number of the post that this new post is a reply to
|               if not defined, the new post will represent a base post in
|               the discussion group
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Create a new post" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.security.User,
			net.project.security.SecurityProvider,
			net.project.space.Space,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DISCUSSION%>"
					   objectID="<%=discussion.getID()%>" /> 
<%
// if this new post is a reply auto-populate the subject field
String replySubject = "";
String docModule = request.getParameter("docModule");
String reply = request.getParameter("reply");
if ((reply != null) && (reply.length() > 0))
    {
    replySubject = discussion.getPost(discussion.getCurrentPostID()).getSubject();
    if (!replySubject.toUpperCase().startsWith("RE:"))
        replySubject = PropertyProvider.get("prm.discussion.postnew.subject.text") + replySubject;
    }
%>

<html>
<head>
<meta http-equiv="expires" content="0"> 
<title><%=PropertyProvider.get("prm.discussion.postnewpage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/trim.js" />

<script language="javascript">
function validate()
   {
	   var theForm = document.forms[0];
	   if(!checkTextbox(theForm.subject, '<%=PropertyProvider.get("prm.discussion.postnew.subjectrequired.message")%>')) return false;
	   if(theForm.postbody.value.trim() == ''){
	   	 extAlert('Error', '<%=PropertyProvider.get("prm.discussion.postnew.messagerequired.message")%>', Ext.MessageBox.ERROR);
	   	 return false;
	   }
	  return true;
   }
   
 function submit()
 {
 	if(validate())
 		document.NEWPOST.submit();
 }

 function cancel()
 {
     window.close();
 }
</script>
</head>
<body class="main">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr class="channelHeader">
	<td width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td colspan="2" class="channelHeader"><%=PropertyProvider.get("prm.discussion.postnew.channel.newpost.title")%></td>
	<td align=right width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<form name="NEWPOST" action="PostNewProcessing.jsp" method="POST" onsubmit="return validate();">
<input type="hidden" name="module" value="<%= net.project.base.Module.DISCUSSION %>">
<input type=hidden name="action" value="<%= net.project.security.Action.CREATE %>">
<input type=hidden name="id" value="<jsp:getProperty name="discussion" property="id" />">
<input type=hidden name="urgency" value="100">
<input type="hidden" name="docModule" value="<%=docModule%>">

<%
if ((reply != null) && (reply.length() > 0))
    {
%>
<input type=hidden name="reply" value="<%= reply %>">
<%
    }
%>
  <table width="100%" border="0" cellspacing="2" cellpadding="2">
    <tr>
      <th width="12%" nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.discussion.postnew.from.label")%></th>
      <td width="88%" align="left" class="tableContent">
      <jsp:getProperty name="user" property="fullName" />
      </td>
    </tr>
    <tr>
      <th width="12%" nowrap align="left"  class="fieldRequired"><%=PropertyProvider.get("prm.discussion.postnew.subject.label")%></th>
    <td width="88%" align="left" class="tableContent">
        <input type="text" name="subject" value="<%= replySubject %>" size="35" maxlength="80">
    </td>
    </tr>

<%-- Removed Urgency from the interface.  Roger Bly 9/20/00
    <tr>
      <th width="12%" nowrap align="left"  class="fieldRequired">Urgency:
	</th>
      <td width="88%" align="left" class="tableContent">
	    <select name="urgency">

net.project.code.TableCodeDomain domain = new net.project.code.TableCodeDomain();
domain.setTableName("pn_post");
domain.setColumnName("urgency_id");

domain.load();

<%= domain.getOptionList() %>
		</select>
        
    </td>
    </tr>
--%>

    <tr>
      <td width="100%" nowrap align="left" valign="top" colspan="2"  class="fieldRequired"><b><%=PropertyProvider.get("prm.discussion.postnew.message.label")%></b>
	 </tr>
	 <tr>
	 	<td width="100%" nowrap align="left" valign="top" colspan="2">
        	<textarea cols=50 rows=12 id="postbody" name="postbody" wrap="VIRTUAL" class="monospaced"></textarea>
    	</td>
    </tr>

  </table> 
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
