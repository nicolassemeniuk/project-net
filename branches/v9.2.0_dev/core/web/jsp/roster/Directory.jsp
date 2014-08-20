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
    info="Directory" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
		    net.project.security.SessionManager,
		    net.project.security.Action,
		    net.project.space.Space,
			net.project.base.property.PropertyProvider,
		    net.project.resource.RosterBean,
		    net.project.base.Module,
			net.project.resource.SpaceInvitationManager,
            net.project.calendar.PnCalendar,
            java.util.Date,
            net.project.base.ObjectType,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<%
	String id = request.getParameter("id");
	session.setAttribute("setDefaultsForMemberInvitation", "Yes");
	session.setAttribute("setDefaultsForRemoveMember", "Yes");
%>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
	SpaceInvitationManager memberWizard = (SpaceInvitationManager) session.getValue ("spaceInvitationWizard");
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<style type="text/css">
div.spacer-for-toolbox{height: 0px;}
hide-achor-from-tab{text-decoration: none;}
</style>
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/skypeCheck.js" />

<%	
// Don't refresh roster is we are returning search results.
String mode = request.getParameter("mode");
if ( (mode == null) || ((mode != null) && !mode.equals("search")) ) {
	roster.setSpace(user.getCurrentSpace());
	roster.load();
    // also, every time we come to this page (when not returning search results), reload the space's instance of the roster also.
	// It might be appropriate to simply make the directory module act on the space's roster instance, but at this time the impact
    // of such a move has not been analyzed.  (PCD: 6/15/2002)
	user.getCurrentSpace().getRoster().reload();
}
%>

<%
    PnCalendar calendar = new PnCalendar();
    long startOfMonth = calendar.startOfMonth(new Date()).getTime();
    
	if(StringUtils.isEmpty(id)){
		id = user.getCurrentSpace().getID();
	}
%>
<script language="javascript"> 	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  </script>
<template:import type="javascript" src="/src/balloon/showBalloon.js" />
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;

	// Balloon Variable Declarations
	var balloon;
	// Initialize balloon Object
	function initializeBalloonObj(){
		balloon = new Balloon();
		balloon.images = null;
		balloon.balloonImage  = JSPRootURL+'/images/balloons/balloon.jpg';
		balloon.ieImage = JSPRootURL+'/images/balloons/balloon_ie.jpg';
		balloon.upLeftStem    = JSPRootURL+'/images/balloons/up_left.jpg';
		balloon.downLeftStem  = JSPRootURL+'/images/balloons/down_left.jpg';
		balloon.upRightStem   = JSPRootURL+'/images/balloons/up_right.jpg';
		balloon.downRightStem = JSPRootURL+'/images/balloons/down_right.jpg';
		balloon.closeButton   = JSPRootURL+'/images/project/dashboard_close.gif';
		balloon.allowAJAX  = true;
	}
	
	// Method to display balloon pop up for last blog entry
	function showBalloon(event, webLogEntryId, personId){
		balloon.showTooltip(event, 'url:'+JSPRootURL+'/blog/entry/SHOW_LAST_BLOG_ENTRY?module=150&weblogEntryId='+webLogEntryId+'&personId='+personId, 1);
	}
	
function setup() {
	page=true;

	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
	if(document.getElementById("lastBlogItColumn") != null){
		getLastBlogIts(<%=user.getCurrentSpace().getID()%>);
	}
	//initializeBalloonObj();
}

function checkInvited() {
   for (var x=0;x<theForm.selected.length;x++) {
      if ((theForm.selected[x].checked) && (theForm.selected[x].value.indexOf('i_',0) == 0)) {
	     var errorMessage = '<%=PropertyProvider.get("prm.directory.directory.businessmember.message")%>';
		 extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
         return false;
      }
   }
   return true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory&section=people";
	openwin_help(helplocation);
}


function cancel() { self.document.location = "<%=pageContextManager.getProperty("space.main.url")%>"; }
function reset() { self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>"; }

function bulkInvitation(){
	self.document.location = JSPRootURL +"/directory/BulkInvitation?module=<%=Module.DIRECTORY%>&action=<%=Action.CREATE%>";
}
function create() {
   self.document.location = JSPRootURL +"/directory/InviteMember?module=<%=Module.DIRECTORY%>&action=<%=Action.CREATE%>";
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.directory.directory.userremovalwarning.message" />', function(btn) { 
			if(btn == 'yes') { 
				theAction("remove");
				theForm.action.value = '<%=Action.DELETE%>';
				theForm.submit();
			} else {
				 return false;
			}
		});
	}
}

function properties() {
   if ((verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) && (checkInvited())) 
	{	
		theAction("properties");
		theForm.submit();
	}
}

function modify() {
   if ((verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) && (checkInvited())) 
	{	
		theAction("modify");
		theForm.action.value = '<%=Action.MODIFY%>';
		theForm.submit();
	}
}

function updateList(selectbox){
   var newloc;
   newloc = "Directory.jsp?user_status=" + selectbox[selectbox.selectedIndex].value;
   <% if (id != ""){ %>
   newloc += "&id=" + '<%= id %>';
   <% } %>
   newloc += "&module=" + '<%= net.project.base.Module.DIRECTORY %>';
   newloc += "&action=" + '<%= Action.VIEW %>';
   document.location = newloc;
}

function search(key) {
	theForm.key.value = key;
	theForm.action.value = '<%=Action.VIEW%>';
	searchButton();
}

function searchButton() {
	theAction("search");
	theForm.action.value = '<%=Action.VIEW%>';
	theForm.submit();
}

function showResourceAllocation(personID) {
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID + '&startDate=' + <%=startOfMonth%>;

    openwin_large('resource_allocation', url);
}

<%
	// close the add team member popup if was posting to this page
    if (request.getMethod().equals("POST") && request.getParameter("theAction").equals("finish"))
        {
%>
	window.open("", "member_wizard").close();
<%
        }
%>

// Function to notify on selected events to selected members
function notify(){
	var m_url = JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID=<%=id%>&objectType=" + '<%=ObjectType.PROJECT%>'+"&action=" + '<%=net.project.security.Action.CREATE%>' + "&module=" + '<%=net.project.base.Module.DIRECTORY%>' + "&moduleType=directory";
	openNotifyPopup(<%=id%>, m_url);
}

</script>
</head>
<body class="main" id='bodyWithFixedAreasSupport' style="overflow-x:hidden" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	<tb:toolbar style="tooltitle" showAll="true" subTitle="<%=SessionManager.getUser().getCurrentSpace().getName() %>" groupTitle='<%=PropertyProvider.get("prm.directory.directory.title")%>'>
		<tb:setAttribute name="leftTitle">
			<history:history>
				<history:module display='<%= PropertyProvider.get("prm.directory.directory.module.history") %>'
		 					jspPage='<%=pageContextManager.getProperty ("directory.url")%>'
							queryString='<%=pageContextManager.getProperty ("directory.url.queryString")%>' />
				<history:page display='<%= PropertyProvider.get("prm.directory.directory.page.history") %>' />
			</history:history>
		</tb:setAttribute>
		<tb:band name="standard">
			<tb:button type="create" label='<%= PropertyProvider.get("prm.directory.directory.create.button.tooltip") %>' />
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.directory.directory.modify.button.tooltip") %>' />
			<tb:button type="properties" label='<%= PropertyProvider.get("prm.directory.directory.properties.button.tooltip") %>' />
			<tb:button type="remove" label='<%= PropertyProvider.get("prm.directory.directory.remove.button.tooltip") %>' />
			<%if(SessionManager.getUser().getCurrentSpace().getType().equals(Space.BUSINESS_SPACE)){%>
			<tb:button type="bulkinvitation" label='<%= PropertyProvider.get("prm.directory.directory.managedirectory.button.tooltip") %>' />
			<%}%>
			<tb:button type="notify" />
		</tb:band>
</tb:toolbar>
<div id='content' style="padding-left:10px;padding-right:10px;padding-top:20px;">
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr>
<td id="leftPane" valign="top">
  <table border="0" cellspacing="0" cellpadding="0">
	      <tr>
	        <td width="130" class="left_tab"><a href="<%=SessionManager.getJSPRootURL() + "/roster/Directory.jsp?module=" + Module.DIRECTORY%>" style="text-decoration: none;">
	        	<span class="active_tab"><%=PropertyProvider.get("prm.directory.directory.tab.participants.title")%></span></a></td>
	        <td>&nbsp;</td>
	       <display:if name="prm.directory.directory.tab.assignments.isenabled">
	       <td width="130" class="right_tab"><a href="<%=SessionManager.getJSPRootURL() + "/resource/Assignments.jsp?module=" + Module.PROJECT_SPACE%>" style="text-decoration: none;">
	       		<span class="disable_tab"><%=PropertyProvider.get("prm.directory.directory.tab.assignments.title")%></span></a></td>
       	   <td>&nbsp;</td>
	       </display:if>
			<td width="130" class="right_tab"><a href="<%=SessionManager.getJSPRootURL()+ "/security/group/GroupListView.jsp?module=" + Module.DIRECTORY%>" style="text-decoration: none;">
				<span class="disable_tab"><%=PropertyProvider.get("prm.directory.directory.tab.roles.title")%></span></a></td>
	      </tr>
   </table>
</td>
</tr>
</table>
<div class="block-content UMTableBorder" style="padding-right:2px;overflow-x:auto;">
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/roster/DirectoryProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
    <input type="hidden" name="action" value="<%=Action.VIEW%>">
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<%-- bfd 3232 : Error message should be standardized in directory module.  --%>
<tr><td colspan="2" class="errorMessage">
	<%net.project.persistence.PersistenceException ee = (net.project.persistence.PersistenceException)session.getAttribute("exception");
	if(ee!=null) { 
		out.println(ee.getMessage()+"\n");
		session.removeAttribute("exception");
	}%>
</td></tr>
				<tr></tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td class="tableHeader" nowrap>&nbsp;&nbsp;<%=PropertyProvider.get("prm.directory.roster.search.label")%> 
						<input type="text" name="key" value='<c:out value="${searchKey}"/>' size="40" maxlength="40" onKeyDown="if(event.keyCode==13) searchButton()">
					</td>
					<% if(Boolean.parseBoolean(PropertyProvider.get("prm.directory.directory.searchmode.isenabled"))){%>
					<td><span class=tableContent><search:letter /></span></td>
					<%}%>
				</tr>	
				<tr><td>&nbsp;</td></tr>
				<tr>
				<tr class="channelHeader">
					<td class="channelHeader" colspan="2" style="height:20;padding-left: 7px;"><%=PropertyProvider.get("prm.directory.directory.tab.participants.title")%></td>
				</tr>
				<tr>
				<%-- Display the people in the roster. --%>
					<td colspan="2">
						<%-- Apply stylesheet to format project team roster --%>
				        <pnet-xml:transform name="roster" scope="session" stylesheet="/roster/xsl/roster.xsl">
				            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
				        </pnet-xml:transform>
					</td>
				</tr>
				<tr></tr>
</table>

</form>
</div>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>
