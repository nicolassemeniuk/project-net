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
|   This page displays information about a post.
|
|   Beans:
|   discussion    The active discussion group should already be loaded in the session
|   post	  The post to display info about, should already be in the session
|   
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Displays information about a post" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.link.LinkManagerBean,
			net.project.discussion.PostBean,
			net.project.discussion.PostReader,
			net.project.security.SecurityProvider,
			net.project.security.User,
			net.project.space.Space,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="post" class="net.project.discussion.PostBean" scope="session" />
<jsp:useBean id="reader" class="net.project.discussion.PostReader" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" 
					   objectID="<%=post.getID()%>" /> 

<html>
<head>
<title><%=PropertyProvider.get("prm.discussion.postinfopage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
</head>
<body class="main" topmargin="0">

<%------------------------------------------------------------------------
  -- Action Bar                                                           
  ----------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" leftTitle='<%=PropertyProvider.get("prm.discussion.postinfo.channel.properties.title")%>'>
	<tb:band name="action">
		<tb:button type="back" function='<%="PostView.jsp?module=" + net.project.base.Module.DISCUSSION +"&action="+ net.project.security.Action.VIEW + "&id="+discussion.getCurrentPostID()%>' />
	</tb:band>
</tb:toolbar>

<%------------------------------------------------------------------------
  -- Post Properties                                                      
  ----------------------------------------------------------------------%>
<table border="0" width="100%" cellpadding="1" cellspacing="1" vspace="1" hspace="1">
  <tr  align="left" >
   <td width="1%">&nbsp;</td>
    <th nowrap width="19%" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.group.label")%></th>
    <td width="80%" class="tableContent"><jsp:getProperty name="discussion" property="name" /></td>
  </tr>
  <tr  align="left" >
  <td width="1%">&nbsp;</td>
    <th nowrap width="19%" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.subject.label")%></th>
    <td width="80%" class="tableContent"><jsp:getProperty name="post" property="subject" /></td>
  </tr>
  <tr  align="left" >
  <td width="1%">&nbsp;</td>
    <th nowrap width="19%" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.postedyby.label")%></th>
    <td width="80%" class="tableContent"><jsp:getProperty name="post" property="fullname" /></td>
  </tr>
  <tr  align="left" >
  <td width="1%">&nbsp;</td>
    <th nowrap width="19%" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.dateposted.label")%></th>
    <td width="80%" class="tableContent"><jsp:getProperty name="post" property="postDate" /></td>
  </tr>
  <tr  align="left"  >
  <td width="1%">&nbsp;</td>
    <th nowrap width="19%" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.numberofreaders.label")%></th>
    <td width="80%" class="tableContent"><jsp:getProperty name="post" property="numOfReaders" /></td>
  </tr>
  <tr  align="left" >
    <td width="100%" colspan="3">&nbsp;</td>
  </tr>
</table>


<%------------------------------------------------------------------------
  -- Reader History                                                       
  ----------------------------------------------------------------------%>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
  	<td nowrap class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.discussion.postinfo.channel.readerhistory.title")%></nobr></td>
  	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr nowrap>
	<td>&nbsp;</td>
	<th align="left" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.postreader.column")%></th>	
	<th align="left" colspan="2" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postinfo.dateread.column")%></th>
</tr>
<tr class="tableLine">
	<td  colspan="4" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
</tr>

<%
for (int i=0;i<Integer.parseInt(post.getNumOfReaders());i++)
    {
    reader = post.getReader(i);
%> 
<tr> 
  <td>&nbsp;</td>
	<td align="left" valign="top" class="tableContent"><c:out value="${reader.name}" /></td>
	<td  nowrap valign="top" colspan="2" class="tableContent"><c:out value="${reader.date}" />&nbsp;&nbsp;&nbsp;&nbsp;</td>
</tr>
<tr class="tableLine">
	<td  colspan="4" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>

<%
    }
%> 
</table>


<%------------------------------------------------------------------------
  -- Link Channel                                                         
  ----------------------------------------------------------------------%>
<br clear=all>  
<table border=0 cellpadding=0 cellspacing=0 width="97%">
<tr>
	<td valign="top" align="left">
		<table width="400" border="0" cellspacing="0" cellpadding="0">
        <tr class="channelHeader">
			<td class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
			<td nowrap class="channelHeader" width="85%"><%=PropertyProvider.get("prm.discussion.postview.channel.links.title")%></td>
			<td class="channelHeader" align=right>
				<tb:toolbar style="channel" showLabels="true">
					<tb:band name="channel">
						<tb:button type="modify" function='<%=SessionManager.getJSPRootURL()+"/discussion/PostAddLink.jsp?module="+net.project.base.Module.DISCUSSION+"&action="+net.project.security.Action.MODIFY+"&id="+post.getID()+"&context="+net.project.link.ILinkableObject.GENERAL+"&view="+display_linkMgr.VIEW_ALL%>' />
					</tb:band>
				</tb:toolbar>
			</td>
			<td align=right class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
        </tr>
        <tr valign="top">
        	<td class="channelContent">&nbsp;</td>
        	<td colspan=2 class="channelContent">         
        		<%
        		display_linkMgr.setView(display_linkMgr.VIEW_ALL);
        		display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
        		display_linkMgr.setRootObject(post.getPost());
        		%>         
        		<jsp:include page="/link/displayLinks.jsp" flush="true" />
        	</td>
        </tr>
        </table>
	</td>
	<td width=100%>&nbsp;</td>
</tr>
</table>

<%-- Footer --%>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>

