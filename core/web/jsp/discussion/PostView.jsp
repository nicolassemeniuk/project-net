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
|   This page displays a post.
|
|   Beans:
|   discussion    The active discussion group should already be loaded in the session
|   post	      The post to display, the reference will be set on this bean
|                 to point to a post obtained from the discussion group.
|
|   Parameters:
|   id            not defined - view the first post in the discussion group
|                 "next" - view the next post in the discussion group
|                 "prev" - view the previous post in the discussion group
|                 id number - view the post matching the specified id number
|                             if it is a member of the discussion group
|   
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Displays the contents of a post" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.link.LinkManagerBean,
			net.project.discussion.PostBean,
			net.project.discussion.Post,
			net.project.security.User,
			net.project.security.SecurityProvider,
			net.project.space.Space,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="post" class="net.project.discussion.PostBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
// Make sure a security check has been passed to view the discussion group
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();
if (id.length() == 0)
    {
    // The user is attempting to view an empty post because the discussion group
    // does not contain any posts.  Validate module, action and the current post bean has
    // an id of -1 (empty post)
    if ((module != net.project.base.Module.DISCUSSION) 
        || (action != net.project.security.Action.VIEW)
        || (!post.getID().equals("-1"))) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    }
else
    {
    if ((module != net.project.base.Module.DISCUSSION) 
        || (action != net.project.security.Action.VIEW)
        || (!id.equals(post.getID()))) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    }

display_linkMgr.setView(display_linkMgr.VIEW_ALL);
display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
display_linkMgr.setRootObject(post.getPost());
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<%--
String mySpace=null;
mySpace=user.getCurrentSpace().getType();
String spaceName=null;
if(mySpace.equals(Space.PROJECT_SPACE))
	spaceName="project";
else if(mySpace.equals(Space.BUSINESS_SPACE))
	spaceName="business";
else
    spaceName="personal";
%>
<link href="/styles/<%=spaceName%>.css" rel="stylesheet" rev="stylesheet" type="text/css">
--%>

<script language="javascript">
this.focus();
var new_window = null;

function LaunchPopupWindow()
   {
   var theLocation="PostNew.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.CREATE %>&id=<jsp:getProperty name="discussion" property="id" />&reply=<jsp:getProperty name="post" property="id" />";  
   my_new_window = window.open(theLocation, "new_window", "height=400,width=500,resizable");
   my_new_window.focus();
   }
   
 function reply()
 { LaunchPopupWindow();}
 
 function postinfo()
 {document.location="PostInfo.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="post" property="id"/>";}
 
 function previouspost()
 {parent.upper_frame.location="ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="discussion" property="id" />&postid=prev";}
 
 function nextpost()
 {parent.upper_frame.location="ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="discussion" property="id" />&postid=next";}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" topmargin="0" leftmargin="0" rightmargin="0">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Top Action Bar                                                       
  ----------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
<%	if ((post.getID() != null) && (!post.getID().equals("-1"))) { %>
		<tb:button type="reply" />
		<tb:button type="previous_post" />
		<tb:button type="next_post" />
		<tb:button type="info" />
<%	} %>
	</tb:band>
</tb:toolbar>

<div id='content'>

<%------------------------------------------------------------------------
  -- Post Message                                                         
  ----------------------------------------------------------------------%>
<%-- bfd 3233 :Modify Link throws exception when there is no posted messages in document  --%>
<%if(!"-1".equals(post.getID())){ %>
<table border="0" cellspacing="2" cellpadding="0" width="100%" >
  <tr>
    <th nowrap width="74" align="left" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postnew.from.label")%></th>
    <td width="546" align="left" class="tableContent"><jsp:getProperty name="post" property="fullname" /></td>
  </tr>
  <tr>
    <th nowrap width="74" align="left" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postnew.subject.label")%></th>
    <td width="546" align="left" class="tableContent"><jsp:getProperty name="post" property="postDate" /></td>
  </tr>
  <tr>
    <th nowrap width="74" align="left" class="tableHeader"><%=PropertyProvider.get("prm.discussion.postnew.message.label")%></th>
    <td width="546" align="left" class="tableContent"><jsp:getProperty name="post" property="subject" /></td>
  </tr>
<%--
  <tr>
    <th nowrap width="74" align="left" class="tableHeader">Links:</th>
    <td width="546" align="left" class="tableContent"><%=display_linkMgr.getLinksCount()%></td>
  </tr>
--%>
  <tr class="tableLine">
	<td  colspan="2" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
  </tr>
  <tr>
    <td width="546" align="left" colspan="2">
		<PRE class="tableContentFontOnly"><jsp:getProperty name="post" property="postBody" />&nbsp;</PRE>
	</td>
  </tr>
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
        <%if(!"-1".equals(post.getID())){ %>
			<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
			<td nowrap class="channelHeader" width="85%"><%=PropertyProvider.get("prm.discussion.postview.channel.links.title")%></td>
			<td class="channelHeader" align=right>
				<tb:toolbar style="channel" showLabels="true">
					<tb:band name="channel">
						<tb:button type="modify" function='<%=SessionManager.getJSPRootURL()+"/discussion/PostAddLink.jsp?module="+net.project.base.Module.DISCUSSION+"&action="+net.project.security.Action.MODIFY+"&id="+post.getID()+"&context="+net.project.link.ILinkableObject.GENERAL+"&view="+display_linkMgr.VIEW_ALL%>' />
					</tb:band>
				</tb:toolbar>
			</td>
			<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
		<%}%>		
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
<%}else
		out.println(""+PropertyProvider.get("prm.discussion.postview.nopost.message"));
%>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body></html>
