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
|   $Revision: 20163 $
|       $Date: 2009-12-01 16:31:31 -0300 (mar, 01 dic 2009) $
|
|--------------------------------------------------------------------%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionManager,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.space.Space,
			net.project.channel.*,
            net.project.space.ISpaceTypes"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/notifyPopup.js" />
<%
String mySpace=null;
mySpace=user.getCurrentSpace().getType();
String spaceName=null;
if(mySpace.equals(Space.PROJECT_SPACE))
	spaceName=ISpaceTypes.PROJECT_SPACE;
else if(mySpace.equals(Space.BUSINESS_SPACE))
	spaceName=ISpaceTypes.BUSINESS_SPACE;
else
    spaceName=ISpaceTypes.PERSONAL_SPACE;
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" /> 
<%
// Configure the discussion manager
discussions.setSpace(user.getCurrentSpace());
discussions.setUser(user);


String refLink,refLinkEncoded=null;
refLinkEncoded = java.net.URLEncoder.encode(SessionManager.getJSPRootURL()+"/discussion/Main.jsp?module="+net.project.base.Module.DISCUSSION);

%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
	theForm = self.document.forms[0];
}

function reset() 
   { 
   self.location = "<%= SessionManager.getJSPRootURL() %>/discussion/Main.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%=net.project.security.Action.VIEW %>"; 
   }

function cancel()
   {
   self.location = "<%= SessionManager.getJSPRootURL() %>/<%= spaceName %>/Main.jsp";
   }

function create() 
   {
   self.location = "<%= SessionManager.getJSPRootURL() %>/discussion/GroupNew.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%=net.project.security.Action.CREATE %>";	
   }

function remove() {
if ((theForm.selected) && (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))) {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.discussion.main.delete.message")%>', function(btn) { 
		if(btn == 'yes'){ 
			var idval = getSelection(theForm);
         	theForm.action.value = "<%= net.project.security.Action.DELETE %>";
         	theForm.id.value = idval;
         	theAction("remove");
         	theForm.submit();
		}else{
		 	return false;
		}
	});
  }	
}

function modify() 
   {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>' ))      
     {
     var idval = getSelection(theForm);
     this.location = "<%= SessionManager.getJSPRootURL() %>/discussion/GroupNew.jsp?"+
                     "module=<%= net.project.base.Module.DISCUSSION %>"+
                     "&action=<%=net.project.security.Action.MODIFY %>&id="+idval;
     }
   }

function properties() 
   {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))      
     {
	     
     var idval = getSelection(theForm);
     this.location = "<%= SessionManager.getJSPRootURL() %>/discussion/GroupProperties.jsp?"+
                     "module=<%= net.project.base.Module.DISCUSSION %>"+
                     "&action=<%=net.project.security.Action.VIEW %>&id="+idval;
     }
   }

function security()
   {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
      {    
      if (!security)
         var security = openwin_security("security");
    
      if (security) 
         {
         var idval = getSelection(theForm);
         theAction("security");
         theForm.target = "security";
         theForm.action.value = "<%= net.project.security.Action.MODIFY_PERMISSIONS %>";
         theForm.id.value = idval;
         theForm.submit();
         }
      }
}

function notify(){
	var targetObjectID =  getSelection(theForm);
 	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')) 
	   {
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID="+ getSelection(theForm)+ "&action=2&module=10");
		  openNotifyPopup(targetObjectID,m_url);

	   } else{
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?objectType=discussion_group&action=2&module=10&isCreateType=1");
		  openNotifyPopup(targetObjectID,m_url);
	   }
 
	
}

function search() {
    self.document.location = JSPRootURL + '/search/SearchController.jsp?module=<%= net.project.base.Module.DISCUSSION+"&refLink="+refLinkEncoded+"&otype="+net.project.base.ObjectType.POST%>';
}

function help() {
		var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=discussion_main";
		openwin_help(helplocation);
	}
</script>
</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%---------------------------------------------------------------------------------------
  -- Draw toolbar                                                                        
  -------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.discussion.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.discussion.main.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/discussion/Main.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.DISCUSSION+"&action="+net.project.security.Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.discussion.main.create.tooltip")%>'/>
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.discussion.main.modify.tooltip")%>'/>
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.discussion.main.delete.tooltip")%>'/>
		<tb:button type="notify" label='<%=PropertyProvider.get("prm.discussion.main.notify.tooltip")%>'/>
		<tb:button type="properties" label='<%=PropertyProvider.get("prm.discussion.main.properties.tooltip")%>'/>
		<tb:button type="search" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MainProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="action">
<input type="hidden" name="module" VALUE="<%= net.project.base.Module.DISCUSSION %>">
<input type="hidden" name="id">
<%
// Initialize the channel manager
int numChannels = 1;
ChannelManager manager = new ChannelManager(pageContext);

Channel discussionList = new Channel("Discussion_List_Groups");
discussionList.setTitle(PropertyProvider.get("prm.discussion.main.channel.grouplist.title"));
discussionList.setWidth("100%");
discussionList.setMinimizable(false);
discussionList.setCloseable(false);
discussionList.setInclude("/discussion/include/discussionList.jsp");
discussionList.setDisplayActionBar(true);
manager.setCustomizable(false);
manager.addChannel(discussionList,0, 0);
manager.display();
%>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>

