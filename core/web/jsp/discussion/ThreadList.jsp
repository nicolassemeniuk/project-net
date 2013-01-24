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
    info="Discussion - Displays the list of threads within a group" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
            net.project.discussion.DiscussionManager, 
            net.project.discussion.PostBean,
            net.project.document.IContainerObject,
            net.project.security.SessionManager,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.space.Space,
            net.project.util.JSPUtils,
            net.project.base.Module,
            net.project.security.Action,
            net.project.util.Validator,
            net.project.link.LinkManagerBean,
            net.project.discussion.Post,
            net.project.project.ProjectSpaceBean,
            org.apache.log4j.Logger"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="post" class="net.project.discussion.PostBean" scope="session" />
<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	String id = request.getParameter("id");
    //id = (id == null ? (String)request.getAttribute("id") : id);
	int module = securityProvider.getCheckedModuleID(); 
%>
<% if (!Validator.isBlankOrNull(id)) { %>
<security:verifyAccess action="view"
					   module="<%=Module.DISCUSSION%>"
					   objectID="<%=discussion.getID()%>"/>
<% } else { %>
<security:verifyAccess action="view"
					   module="<%=Module.DISCUSSION%>"/>
<% } %>

<%
boolean bFinished = false;
String mode = null;
if ((mode = request.getParameter("view")) != null) {
    String lastReadPost = discussion.getCurrentPostID();
    discussion.setViewString(mode);
    if ((lastReadPost == null) || (lastReadPost.length() == 0)) {
        post.setReference(discussion.getFirstPost());
    } else {
        post.setReference(discussion.getPost(lastReadPost));
    }
    bFinished = true;
}
if ((mode = request.getParameter("sort")) != null) {
    String lastReadPost = discussion.getCurrentPostID();
    discussion.setSort(mode);
    if ((lastReadPost == null) || (lastReadPost.length() == 0)) {
        post.setReference(discussion.getFirstPost());
    } else {
        post.setReference(discussion.getPost(lastReadPost));
    }
    bFinished = true;
}

if (!bFinished) {
    if ((mode = request.getParameter("iconId")) != null) {
        discussion.handleThreadClick(mode);
        post.setReference(discussion.getPost(mode));
    } else if ((mode = request.getParameter("postid")) != null) {
        if (mode.equals("next")) {
            post.setReference(discussion.getNextPost());
        } else if (mode.equals("prev")) {
            post.setReference(discussion.getPreviousPost());
        } else {
//            System.out.println("Setting post reference to: "+mode);
            post.setReference(discussion.getPost(mode));
        }
    } else {
        post.setReference(discussion.getFirstPost());
    }
}
String docModule = request.getParameter("docModule");
Boolean showHistory = (Boolean)request.getSession().getValue("Discussion_ShowHistory");
%>

<template:getDoctype />
<html>
<head>
<meta http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<style type="text/css">
	.highlighted {
		background: #ffffcc;
	}
	.hover {
		background: #ffffcc;
		cursor: pointer;
	}
</style>


<template:import type="javascript" src="/src/extjs/adapter/jquery/jquery.js"/>
<script language="javascript">
window.history.forward(-1);
var theForm;
var isLoaded = false;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
var selectedItem;	/* used in Non-threaded view */
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function changeView(viewMode){
     this.location = "ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<%= discussion.getID() %>&view=" + viewMode + "&docModule=<%=docModule%>&postid="+selectedItem;
}

function hasSelection() {
     if (theForm.selectedID) {
         return true;
     } else {
         return verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>');
     }
}

function getSelectedID() {
    if (theForm.selectedID) {
        return theForm.selectedID.value;
    } else {
        return getSelection(theForm);
    }
}

function selectPost(id) {
    this.location = "<%=SessionManager.getJSPRootURL()%>/discussion/ThreadList.jsp?"+
        "module=<%=Module.DISCUSSION%>&action=<%=Action.VIEW%>&id=<%=discussion.getID()%>"+
        "&postid="+id+"&docModule=<%=docModule%>";
}

function remove() {
    var id;
    var hasRepliesField;
    var theLocation;
	if (hasSelection(theForm)) {
	  id = getSelectedID();
	  Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.post.deleteselectedpost.confirm")%>', function(btn) { 
		if(btn == 'yes'){
	       <%-- We use MODIFY permissions for the DISCUSSION module because we aren't
	            actually deleting a DISCUSSION, just a post --%>
		<% if (user.isSpaceAdministrator()) { %>
		<%-- Space Administrators have the ability to delete all replies to a post, if the
		     post has replies. --%>
		       eval("hasRepliesField = theForm.post"+id+"replies;");
		       if (hasRepliesField.value == 1) {
		           theLocation = "<%=SessionManager.getJSPRootURL() +
		               "/discussion/RemovePost.jsp?module=" + Module.DISCUSSION + "&action=" +
		               Action.MODIFY + "&objectID="%>"+id+"&docModule=<%=docModule%>";
		           my_new_window = window.open(theLocation, "new_window", "height=400,width=550,resizable");
		           my_new_window.focus();
		       } else {
		           theLocation = "<%=SessionManager.getJSPRootURL() +
		               "/discussion/RemovePostProcessing.jsp?module=" + Module.DISCUSSION +
		               "&action=" + Action.MODIFY + "&notPopup=true&objectID="%>"+id+"&docModule=<%=docModule%>";
		           this.document.location = theLocation;
		       }
		<% } else { %>
		       theLocation = "<%=SessionManager.getJSPRootURL() +
		           "/discussion/RemovePostProcessing.jsp?module=" + Module.DISCUSSION +
		           "&action=" + Action.MODIFY + "&notPopup=true&objectID="%>"+id+"&docModule=<%=docModule%>";
		       this.document.location = theLocation;
		<% } %>
	   } else {
	   		return false;
	   }
	  });
	}
}

function notify(){
	var targetObjectID =  getSelection(theForm) ? getSelection(theForm) : selectedItem;
 	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')
 			|| verifySelectionNonThreaded()){
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID="+ targetObjectID + "&action=" + '<%=net.project.security.Action.MODIFY%>' + "&module=" + '<%=net.project.base.Module.DISCUSSION%>');
		  openNotifyPopup(targetObjectID,m_url);
	}	
}

function reset() {
    self.location = "<%= SessionManager.getJSPRootURL() %>/discussion/GroupView.jsp?module=<%=Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<%= discussion.getID()%>&docModule=<%=docModule%>";
}

function create() {
    var theLocation="<%= SessionManager.getJSPRootURL() %>/discussion/PostNew.jsp?module=<%=Module.DISCUSSION %>&action=<%= net.project.security.Action.CREATE %>&id=<jsp:getProperty name="discussion" property="id" />&docModule=<%=docModule%>";
    my_new_window = window.open(theLocation, "new_window", "height=400,width=550,resizable");
    my_new_window.focus();
}

function search() {
    my_new_window = window.open("<%= SessionManager.getJSPRootURL() %>/discussion/SearchFrameSet.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%=Action.VIEW %>&id=<jsp:getProperty name="discussion" property="id" />", "search_window", "height=500,width=480,resizable");
    my_new_window.focus();
}

function help() {
    var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=discussion_main&section=post";
    openwin_help(helplocation);
}

var isNav, isIE
isNav = (navigator.appName == "Netscape");
isIE = (navigator.appName.indexOf("Microsoft") != -1)

var new_window = null;

function LaunchPopupWindow() {
   var theLocation="PostNew.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.CREATE %>&id=<jsp:getProperty name="discussion" property="id" />&reply=<jsp:getProperty name="post" property="id" />";
   my_new_window = window.open(theLocation, "new_window", "height=400,width=500,resizable");
   my_new_window.focus();
 }

 function reply() {
	 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')
			 || verifySelectionNonThreaded()){ 
     	LaunchPopupWindow();
	 }
 }

 function previouspost() {
     document.location="ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="discussion" property="id" />&postid=prev"+"&docModule=<%=docModule%>";
 }

 function nextpost() {
     document.location="ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="discussion" property="id" />&postid=next"+"&docModule=<%=docModule%>";
}

 function postinfo() {
	 // TODO: make it Non-threaded view aware
	 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')
			 || verifySelectionNonThreaded()){
	 	 var infoURLString ="PostInfoInner.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<jsp:getProperty name="post" property="id"/>&objectId=<%=request.getParameter("id")%>&from="+document.URL;
	 	 var infoContainerElement = document.getElementById('infoContainer');
	 	 document.getElementById('toolbar').style.display = "none";
	 	 infoContainerElement.style.visibility = "hidden";
	 	 infoContainerElement.src = infoURLString;
	 	 infoContainerElement.style.visibility = "visible";
	 	 document.getElementById('infoContainer').height = "350px";
	 }
}


/** Nonthreaded view related */
$(document).ready(function() {
	setupNonthreatedView();
});

function setupNonthreatedView() {	
	$('tr.selectable')
		.hover(function() {
			$(this).removeClass('hover').addClass('hover');	
		}, function() {
			$(this).removeClass('hover');
		})
		.click(function() {
			// handle UI changes
			highlightSelectedItem(this);

			// preform selectPost function passing in selected element
			selectPost($(this).attr('data'));
 		});

	preselectPost();
}

function highlightSelectedItem(item) {
	$('tr.selectable').removeClass('highlighted');
	$(item).removeClass('highlighted').addClass('highlighted');
}

function preselectPost() {
	var postId = '<%=request.getParameter("postid")%>';
	if( postId && (postId !== 'null') ) {
		selectedItem = postId;
		highlightSelectedItem( $('#ROW_'+selectedItem) );
	}
}

function verifySelectionNonThreaded() {
    return $('.highlighted').length > 0 ? true : false;
}

</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin="0"  onLoad="setup();" topmargin="0">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	<div id="left-navbar">
		<div style="padding: 0px;">
			<div id="discussion_channel">
				<div style="padding: 1px; width: 100%;">
					<div style="padding: 1px; width: 100%;">
						<div id="leftheading-<%=user.getCurrentSpace().getType() %>" style="left: 0px;"> <%=PropertyProvider.get("prm.global.tool.discussion.name")%> </div>
						<% if(user.getCurrentSpace().isTypeOf(Space.PROJECT_SPACE)){ %>
							<%= ((ProjectSpaceBean)user.getCurrentSpace()).getProjectSpaceDetails()%>
						<% } %>						
						<br/>
						<div class='toolbox-heading' style="margin-top: 15px;">Toolbox</div>
						<div id="toolbox-item" class="toolbox-item">
							<%if(PropertyProvider.getBoolean("prm.global.actions.icon.isenabled")){ %>
							   <% if (PropertyProvider.getBoolean("prm.blog.isenabled")) { %>
							    <span id="blog-ItEnabled">
                                 <a href="javascript:blogit();" onmouseover=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_over.gif'"
				     				onmouseout=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_on.gif'">
				     				<img hspace="0" border="0" name="imgblogit" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_on.gif"
				 					title="Blog-it" alt="Blog-it" /><%= PropertyProvider.get("all.global.toolbar.standard.blogit") %></a>
                                </span>
                              <br/>	
                              <%} %>
                              <span>
                                <a href="javascript:create();"  onmouseover=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-create.gif'"
				     				onmouseout=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif'"><img hspace="0" border="0" name="createimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif"
				 					title="Create" alt="Create" /><%= PropertyProvider.get("prm.discussion.threadlist.create.tooltip") %></a>
                              </span>
                              <br/>
                               <span>
                                <a href="javascript:reply();" onmouseover=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-create.gif'"
				     				onmouseout=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif'"><img hspace="0" border="0" name="createimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif"
				 					title="Reply" alt="Reply" /><%= PropertyProvider.get("prm.discussion.threadlist.reply.link") %></a>
                              </span>
                              <br/>	
                              <span>
                                <a href="javascript:remove();" onmouseover=" document.removeimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-remove.gif'"
				     				onmouseout=" document.removeimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-remove_on.gif'"><img hspace="0" border="0" name="removeimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-remove_on.gif"
				 					title="Remove" alt="Remove" /><%= PropertyProvider.get("prm.discussion.threadlist.delete.link") %></a>
                              </span>
                              <br/>	 
                              <span>
                                <a href="javascript:postinfo();" onmouseover="document.propertiesimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-properties.gif'"
				     				onmouseout=" document.propertiesimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-properties_on.gif'"><img hspace="0" border="0" name="propertiesimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-properties_on.gif"
				 					title="Properties" alt="Properties" /><%= PropertyProvider.get("prm.discussion.threadlist.info.link") %></a>
                              </span>                             
                              <br/>	 
                              <span>
                                <a href="javascript:notify();" onmouseover="document.notifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-notify.gif'"
				     				onmouseout=" document.notifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-notify_on.gif'"><img hspace="0" border="0" name="notifyimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-notify_on.gif"
				 					title="Notify" alt="Notify" /><%= PropertyProvider.get("all.global.toolbar.standard.notify") %></a>
                              </span>                              
                              <br/>  
						 	<%} else { %>
						 		<% if (PropertyProvider.getBoolean("prm.blog.isenabled")) { %>
							  <span id="blog-ItEnabled">
                                 <a href="javascript:blogit();"><%= PropertyProvider.get("all.global.toolbar.standard.blogit") %></a>
                              </span>
                              <br/>
                              <%} %>
                              <span>
                                <a href="javascript:create();"><%= PropertyProvider.get("prm.discussion.threadlist.create.tooltip") %></a>
                              </span>
                              <br/>
                              <span>
                                <a href="javascript:reply();"><%= PropertyProvider.get("prm.discussion.threadlist.reply.link") %></a>
                              </span>
                              <br/>							
                              <span>
                                <a href="javascript:remove();"><%= PropertyProvider.get("prm.discussion.threadlist.delete.link") %></a>
                              </span>
                              <br/>
                              <span>
                                <a href="javascript:postinfo();"><%= PropertyProvider.get("prm.discussion.threadlist.info.link") %></a>
                              </span>
                              <br/>                                                            
                              <span>
                                <a href="javascript:notify();"><%= PropertyProvider.get("all.global.toolbar.standard.notify") %></a>
                              </span>                             
                              <br/>  
                           <%} %>                                                                                        
						</div>
					</div>
				</div>
			</div>
		</div>		
	</div>


<div id='content'>

<form name="SelectMode">
<input type="hidden" name="docModule" value="<%=docModule%>">
<%if(docModule!= null && !docModule.equals("")) {%>
<%}%>
<br>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<%
    String errorMessage = request.getParameter("errorMessage");
    if (!Validator.isBlankOrNull(errorMessage)) {
%>
    <span class="errorMessage"><%=errorMessage%></span>
<%  } %>

<tr>
 <td colspan="3">
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
       <tr>
			<td >
		<%		if ((showHistory != null) && (showHistory.booleanValue() == true)) { %>
					<history:history>
		                <history:page display='<%=PropertyProvider.get("prm.discussion.threadlist.module.history")%>'
		                              level="2"/>
		            </history:history>
		<%		} else { %>
					&nbsp;
		<%		} %>
			</td>
			<td>
				&nbsp;
			</td>
			<td align="right">
					<%=PropertyProvider.get("prm.discussion.threadlist.view.label")%><select name="view" onChange="changeView(options[selectedIndex].value);">
					<%
					 String[] views = discussion.getViewList();
					 int curView = discussion.getView();
					 String selected = null;
					 for (int i=0;i<views.length;i++)
						{
						if (curView == i)
							selected = "selected";
						else
							selected = "";	
					%>
						<option value="<%= i %>" <%= selected %>><%= views[i] %></option>
					<%
						}
					%>	
			</td>
		  </tr>	
		</table>	
   </td>	
</tr>
<tr><td>&nbsp;</td></tr>

<tr class="actionBar">
	<td width="1%" class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
	<td align="left" class="actionBar" nowrap>&nbsp;<jsp:getProperty name="discussion" property="name" />&nbsp;</td>
	<td width="1%" align=right class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
</tr>
</table>

<jsp:getProperty name="discussion" property="postListAsHTML" />

<br>

<%--
// Make sure a security check has been passed to view the discussion group
String objectId = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();
if (objectId.length() == 0)
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
        || (!objectId.equals(post.getID()))) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    }

display_linkMgr.setView(display_linkMgr.VIEW_ALL);
display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
display_linkMgr.setRootObject(post.getPost());
--%>

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
  <tr class="tableLine">
	<td  colspan="2" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
  </tr>
  <tr>
    <td width="546" align="left" colspan="2">
		<PRE class="tableContentFontOnly"><jsp:getProperty name="post" property="postBody" />&nbsp;</PRE>
	</td>
  </tr>
</table>
<%}else
		out.println(""+PropertyProvider.get("prm.discussion.postview.nopost.message"));
%>

<iframe id='infoContainer' frameborder="0" width="98%" height="0" scrolling="auto">
</iframe>

<script language="javascript">
if (isIE) 
  {
  var theOne = document.all.item("ROW_<%= discussion.getCurrentPostID() %>");
  if (theOne != null)
      this.scroll(0, (theOne.offsetTop + theOne.offsetParent.offsetTop) - (document.body.offsetHeight / 2));
  }
else if (isNav)
  {
  var theOne = null;
  for (i=0;i<document.anchors.length;i++)
     if (document.anchors[i].name == "<%= discussion.getCurrentPostID() %>")
	theOne = document.anchors[i]
  if (theOne != null)
     this.scroll(0, theOne.y - (innerHeight / 2));
  }
</script>

</form>

<div id="toolbar">
	<%if(!"-1".equals(post.getID())){
		display_linkMgr.setView(display_linkMgr.VIEW_ALL);
		display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
		display_linkMgr.setRootObject(post.getPost());	
	%>
	<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
	<%	if ((post.getID() != null) && (!post.getID().equals("-1"))) { %>
			<tb:button type="previous_post" />
			<tb:button type="next_post" />
	<%	} %>
		</tb:band>
	</tb:toolbar>
	<br>
	<%}
	%>
</div>

  <% if (discussion.getDescription() != null && discussion.getDescription().equals("Form Discussion")) {
	  %>
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tbody>
				<tr>
				   <td align="right">
				       <a href=<%= SessionManager.getJSPRootURL() + "/form/FormEdit.jsp?module=" + Module.FORM+ "&action=1&id="+  discussion.getObjectID()  +"&readOnly=false" %>> Return to <%= session.getAttribute("formDataName") %>  </a>&nbsp;&nbsp;&nbsp;
				   </td>
				</tr>
			</tbody>
		</table>
    <% } %>

<%@ include file="/help/include_outside/footer.jsp" %>



<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>

