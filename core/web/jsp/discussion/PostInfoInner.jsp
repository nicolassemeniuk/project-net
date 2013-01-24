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
|   $Revision: 15404 $
|       $Date: 2006-08-28 17:50:09 +0300 (??, 28 ??? 2006) $
|     $Author: deepak $
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
			net.project.security.SessionManager,
			net.project.util.JSPUtils" 
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

<script language="javascript">
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

	function postAddLink() {
		parent.location = JSPRootURL + "/discussion/PostAddLink.jsp?module=20&action=2&id=<%=post.getID()%>&context=10&view=2&objectId=<%=request.getParameter("objectId")%>";	
	}
	
	function backModifyLinks() {
		parent.document.getElementById('infoContainer').style.visibility = "hidden";
		parent.document.getElementById('toolbar').style.display = "block";	
		parent.document.getElementById('infoContainer').height = "0px";
	}
</script>

</head>

<%
	final HttpSession httpSession = request.getSession();
	final String fromUrl = request.getParameter("from");
	if (fromUrl != null && !fromUrl.equals("")) {
		httpSession.setAttribute("from", fromUrl);		
	}
%>

<body class="main" topmargin="0">

<%------------------------------------------------------------------------
  -- Action Bar                                                           
  ----------------------------------------------------------------------%>
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="actionBar">
		<td width="1%" class="actionBar">
			<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0">
		</td>
		<td class="actionBar" align="left">
			<%=PropertyProvider.get("prm.discussion.postinfo.channel.properties.title")%>
		</td>
		<td class="actionBar" align="right">
			&nbsp;
			<nobr>
				&nbsp;&nbsp;&nbsp;
				<a href="javascript:backModifyLinks();" class="channelNoUnderline">
					Back&nbsp;
					<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-back_off.gif" width="27" height="27" alt="Back" title="Back" border="0" align="absmiddle"/>
				</a>
			</nobr>
		</td>
		<td width="1%" align="right" class="actionBar">
			<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0">
		</td>
	</tr>
</table>

<%------------------------------------------------------------------------
  -- Post Properties                                                      
  ----------------------------------------------------------------------%>
<table border="0" width="96%" cellpadding="1" cellspacing="1" vspace="1" hspace="1">
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
<table border="0" width="96%" cellpadding="0" cellspacing="0">
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
	<td align="left" valign="top" class="tableContent"><%=reader.getName()%></td>
	<td  nowrap valign="top" colspan="2" class="tableContent"><%=reader.getDate()%>&nbsp;&nbsp;&nbsp;&nbsp;</td>
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
				<span class="channelHeader">
				  <nobr>
				  	&nbsp;
				  	<A href="javascript:postAddLink();" class="channelNoUnderline">
				  	  <%=PropertyProvider.get("prm.discussion.postview.channel.links.editlink")%>
				  	  <IMG alt='<%=PropertyProvider.get("prm.discussion.postview.channel.links.editlink")%>' title='<%=PropertyProvider.get("prm.discussion.postview.channel.links.editlink")%>' width="20" height="15" border="0" 
				  	  hspace="0" vspace="0" align="top" 
				  	  src="<%= SessionManager.getJSPRootURL() %>/images/<%=JSPUtils.getModifiedCurrentSpaceType(user.getCurrentSpace())%>/channelbar-edit.gif" name="imgmodify">
				  	</A>
				  	&nbsp;
				  </nobr>
				</span>
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

</body>
</html>
