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
	info="Personal Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User, 
			net.project.security.Action,
			net.project.security.SessionManager,
			net.project.base.Module,
            net.project.security.group.GroupCollection,
			net.project.resource.SpaceInvitationManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />  
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<jsp:useBean id="groupList" class="net.project.security.group.GroupCollection" scope="page" />
<jsp:useBean id="inheritedGroups" class="net.project.security.group.GroupCollection" scope="page" />


<security:verifyAccess action="view"
					   module="<%=Module.DIRECTORY%>" /> 

<%
	SpaceInvitationManager memberWizard = (SpaceInvitationManager) session.getValue ("spaceInvitationWizard");
  	String refLink = "/security/group/GroupListView.jsp?module=" + Module.DIRECTORY;
%>

<%
// Load all the owned groups and non-owned groups
try {
	groupList.setSpace(user.getCurrentSpace());
    groupList.loadOwned();

    inheritedGroups.setSpace(user.getCurrentSpace());
    inheritedGroups.loadNonOwned();
    inheritedGroups.updateWithOwningSpace();
    
} catch(net.project.persistence.PersistenceException pe) {
	org.apache.log4j.Logger.getLogger(this.getClass()).error("Error loading group list " + pe);

}

%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<style type="text/css">div.spacer-for-toolbox{height: 0px;}</style>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
    theForm = self.document.forms["main"];
}

function cancel() { 
   self.document.location = '<%=pageContextManager.getProperty("directory.url")%>'; 
}

function reset() { self.document.location = JSPRootURL + "/security/group/GroupListView.jsp?module=<%=Module.DIRECTORY%>"; }

function security() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
	{    
		if (!security)
			var security = openwin_security("security");
    
		if (security) 
	    {
              theAction("security");
              theForm.target = "security";
              theForm.action.value= "<%= net.project.security.Action.MODIFY_PERMISSIONS %>";
              theForm.id.value = getSelection(theForm);
              theForm.submit();
              security.focus();
		}
	}
}

function create() {
    var newgroup = openwin_wizard("newgroup", "<%=SessionManager.getJSPRootURL() + "/security/group/GroupProcessing.jsp?theAction=newgroup&module="+Module.DIRECTORY+"&action="+Action.VIEW+"&reflink="+refLink%>");
    newgroup.focus();
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get("prm.directory.grouplistview.remove.message")%>',function(btn){
			if( btn == 'yes'){
				theAction("remove");
				theForm.target = "_self";
				theForm.submit();
			} else {
				return false;
			}
		});
}

function modify() {

    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
      {
      theForm.elements["id"].value = getSelection(theForm);
	  theAction("modify");
	  theForm.target = "_self";
	  theForm.submit();
      }
}

   function help()
   {
   	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory&section=group";
   	openwin_help(helplocation);
   }
   
function sendMail(id) {
    theAction("sendMail");
    theForm.elements["id"].value = id;
	theForm.target = "_self";
	theForm.submit();
}
</script>

</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();" style="overflow-x:hidden;">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<tb:toolbar style="tooltitle" showAll="true" subTitle="<%=SessionManager.getUser().getCurrentSpace().getName() %>" groupTitle="prm.directory.directory.tab.roles.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.directory.grouplistview.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/security/group/GroupListView.jsp"%>'
			     		  queryString='<%="module="+Module.DIRECTORY%>' />
			</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.directory.grouplistview.create.tooltip")%>' />
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.directory.grouplistview.modify.tooltip")%>' />
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.directory.grouplistview.remove.tooltip")%>' />
	</tb:band>
</tb:toolbar>
<div id='content' style="padding-right:10px;padding-top:20px;">
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr>
<td id="leftPane" valign="top">
  <table border="0" cellspacing="0" cellpadding="0">
	      <tr>
	        <td width="130" class="right_tab"><a href="<%=SessionManager.getJSPRootURL() + "/roster/Directory.jsp?module=" + Module.DIRECTORY%>" style="text-decoration: none;">
	        	<span class="disable_tab"><%=PropertyProvider.get("prm.directory.directory.tab.participants.title")%></span></a></td>
	       <td>&nbsp;</td>
	       <display:if name="prm.directory.directory.tab.assignments.isenabled">
	       <td width="130" class="right_tab"><a href="<%=SessionManager.getJSPRootURL() + "/resource/Assignments.jsp?module=" + Module.PROJECT_SPACE%>" style="text-decoration: none;">
	       		<span class="disable_tab"><%=PropertyProvider.get("prm.directory.directory.tab.assignments.title")%></span></a></td>
	       <td>&nbsp;</td>
	       </display:if>
			<td width="130" class="left_tab"><a href="<%=SessionManager.getJSPRootURL()+ "/security/group/GroupListView.jsp?module=" + Module.DIRECTORY%>" style="text-decoration: none;">
				<span class="active_tab"><%=PropertyProvider.get("prm.directory.directory.tab.roles.title")%></span></a></td>
	      </tr>
   </table>
</td>
</tr>
</table>
<div class="block-content UMTableBorder">
<form name="main" method="post" action="<%=SessionManager.getJSPRootURL() + "/security/group/GroupProcessing.jsp"%>">
	<input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
    <input type="hidden" name="action" value="<%=Action.VIEW%>">
    <input type="hidden" name="id" value="">
    <input type="hidden" name="refLink" value="<%=refLink%>" />

<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
<%-- Roles owned by this space --%>
<tr>
</tr>
<tr class="channelHeader">
	<td class="channelHeader" width="100%" colspan="2" style="height:20;padding-left: 7px;"><%=PropertyProvider.get("prm.directory.directory.tab.roles.title")%></td>
</tr>
<tr>
    <td colspan="4">
        <pnet-xml:transform name="groupList" scope="page" stylesheet="/security/group/xsl/group-list.xsl">
            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
        </pnet-xml:transform>
    </td>
</tr>

<%-- Roles inherited by this space --%>
<tr>
	<td colspan="4">&nbsp;</td>
</tr>
<tr class="channelHeader">
	<td class="channelHeader"  width="100%" colspan="2" style="height:20;padding-left: 7px;"><%=PropertyProvider.get("prm.directory.grouplistview.channel.inherited.title")%></td>
</tr>
<tr>
    <td colspan="4">
        <pnet-xml:transform name="inheritedGroups" scope="page" stylesheet="/security/group/xsl/inherited-group-list.xsl">
            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
        </pnet-xml:transform>
    </td>
</tr>
</table>
</form>
</div>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
