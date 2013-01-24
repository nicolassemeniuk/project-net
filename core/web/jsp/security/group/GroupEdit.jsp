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
    info="Edit Role Entry" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
            net.project.security.group.Group,
            net.project.security.group.GroupProvider,
			net.project.security.group.GroupTypeID,
            net.project.space.Space,
            net.project.base.Module,
            org.apache.commons.lang.StringUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<%
	// Necessary because Personal Space has all Modules associated with it
	if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
	    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.rolesnotaccessible.message"));
	}
%>

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="view"
					   module="<%=Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
	// Look for an existing group object in session
	Group groupBean = (Group) pageContext.getAttribute("group", PageContext.SESSION_SCOPE);
	if (groupBean != null && groupBean.hasErrors()) {
		// Re-use that group
	} else {
		// Load a group based on the id
		GroupProvider groupProvider = new GroupProvider();
	    groupBean = groupProvider.newGroup(id);
	    groupBean.setSpace(user.getCurrentSpace());
	    groupBean.load();
        
        // Check that the group is owned by the current space context
        // We do not allow the user to edit inherited groups
        if (!groupBean.isOwnedBySpace()) {
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.inherited.accessdenied.message"));
        }

	    pageContext.removeAttribute("group", PageContext.SESSION_SCOPE);
	    pageContext.setAttribute("group", groupBean, PageContext.SESSION_SCOPE);
	}

    // Set up the URL that the roster/PersonalDirectoryView.htm page uses to return to
    // the caller.  This is used by the two include pages here when linking
    // to the person in the group
    pageContextManager.setProperty("directory.url.complete", SessionManager.getJSPRootURL() + "/security/group/GroupEdit.jsp?module=" + Module.DIRECTORY + "&id=" + id);

	// Decide whether to show actionbar submit button
	// Team Member and Space Administrator groups may not be modified;
	// (Principal Groups cannot be displayed at all).
	boolean isModifiable = false;
    boolean canAddMembers = groupBean.canAddMembers();
	boolean showSubmitButton = false;
	if (!groupBean.isSystem()) {
		showSubmitButton = true;
		isModifiable = true;
	}
%>
<jsp:useBean id="group" type="net.project.security.group.Group" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<script language="javascript">
	var theForm;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var isLoaded = false;
	var spaceAdmin = false;
	var spaceAdminRows = false;
	
function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   isLoaded = true;
   theForm = self.document.forms[0];
}

function cancel() { 
   self.document.location = JSPRootURL + "/security/group/GroupListView.jsp?module=<%=Module.DIRECTORY%>"; 
}

function reset() { 
   self.document.location = JSPRootURL + "/security/group/GroupEdit.jsp?module=<%=Module.DIRECTORY%>" + "&id=<%=id%>"; 
}

function submitGroup() {
<%	if (isModifiable) { %>

   if(validateForm(theForm)) {
      theAction("modify");   
      theForm.action.value = "<%= net.project.security.Action.MODIFY%>";
      theForm.submit();
	}
<%	} else { %>
    self.document.location = JSPRootURL + "/security/group/GroupListView.jsp?module=<%=Module.DIRECTORY%>";
<%  } %>
}

function removeMember() {
	//To check all person can not be remvoed in once.
	var totalCheckedLength = 0;
	var numberOfMemberToRemove = 0;
	if(theForm.person_memberSelected){
		var totalCheckedLength = theForm.person_memberSelected.length;
		if(totalCheckedLength){
			for (var index = 0; index < totalCheckedLength; index++){
				if(theForm.person_memberSelected[index].checked) numberOfMemberToRemove++ ;
			}
		}
	}

    if(spaceAdmin) {
		if(spaceAdminRows || totalCheckedLength == numberOfMemberToRemove) {
			if(spaceAdminRows){
				theAction("remove");
				theForm.action.value = "<%= net.project.security.Action.MODIFY%>";
				theForm.submit();
			} else {
				var errorMessage = '<%=PropertyProvider.get("prm.directory.roles.groupedit.spaceadmin.message")%>';
				extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
				return false;
			}
		} else {
				theAction("remove");
				theForm.action.value = "<%= net.project.security.Action.MODIFY%>";
				theForm.submit();
			}
	} else {
			  theAction("remove");
			  theForm.action.value = "<%= net.project.security.Action.MODIFY%>";
			  theForm.submit();
	}
}

function addMember() {
    theAction("create");
    theForm.action.value = "<%= net.project.security.Action.MODIFY%>";
    theForm.submit();
}

function validateForm(frm)
{
	    if (!checkTextbox(frm.name, '<%=PropertyProvider.get("prm.directory.roles.groupedit.namerequired.message")%>')) return false;
    		return true;
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=security_group&section=edit";
	openwin_help(helplocation);
}

</script>

</head>

<body class="main"  onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="all.global.toolbar.standard.security">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" display='<%=PropertyProvider.get("prm.directory.roles.groupedit.module.history")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content' style='padding-right:10px'>
<div class="block-content borderCls" >
<form method="post" action="GroupEditProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="action">
	<input type="hidden" name="module" value="<%= Module.DIRECTORY %>">
	<input type="hidden" name="id" value="<jsp:getProperty name="group" property="ID" />">

    <%-- The memberType of the selected member --%>
    <input type="hidden" name="memberType" value="" />

<div align="left">
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
	<tr align="right">
	    <td>&nbsp;</td>
		<td valign="top" align="left" class="pageTitle"><%=PropertyProvider.get("prm.directory.roles.groupedit.pagetitle")%></td>
		<td align="left" class="pageTitle"><jsp:getProperty name="group" property="name" /></td>
	    <td>&nbsp;</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    <tr>
	    <td>&nbsp;</td>
        <td colspan="2">
            <%-- Errors pertaining to member add / remove --%>
            <%=group.getErrorMessage("member")%>
        </td>
	    <td>&nbsp;</td>
    </tr>
	<tr>	
	    <td>&nbsp;</td>
	    <th nowrap align="left" class="tableHeader" width="20%"><%=group.getFlagError("name", PropertyProvider.get("prm.directory.roles.groupcreate.name.label"))%></th>
	    <td class="tableContent">

<%	if (isModifiable) { %>
    <input type="text" name="name" size="40" maxlength="80" value="<jsp:getProperty name="group" property="name" />">
	<%=group.getErrorMessage("name")%>
<%	} else { %>
	<jsp:getProperty name="group" property="name" />
<%	} %>

	    </td>
	    <td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<th nowrap align="left" class="tableHeader"><%=group.getFlagError("description", PropertyProvider.get("prm.directory.roles.groupcreate.description.label"))%></th>
		<td class="tableContent">

<%	if (isModifiable) { %>
		<input type="text" name="description" size="40" maxlength="255" value="<%=StringUtils.isNotEmpty(group.getDescription()) ? group.getDescription(): ""%>">
		<%=group.getErrorMessage("description")%>
<%	} else { %>
		<c:out value="${group.description} "/>
<%	} %>        

    	</td>
		<td>&nbsp;</td>
	</tr>
</table>
</div>

<%-- Members of role --%>
<br />
<jsp:include page="include/GroupMembers.jsp" flush="true" />

<%  if(group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) { %>
        <SCRIPT language="javascript">
		    spaceAdmin = true;
		</SCRIPT>
<%      if(group.getMemberCount() <= 1) { %>
            <SCRIPT language="javascript">
			    spaceAdminRows = true;
			</SCRIPT>
<%      }
    }
%>
<p class="memberEditBottom" height="30" align="center" style="padding-top:10px;padding-bottom:3px;">
<% if (canAddMembers) { %>
	<button onclick="addMember();" align="absmiddle"><img src="<%=SessionManager.getJSPRootURL()%>/images/directory/up_arrow.gif"/>&nbsp;&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupeditaddbutton.caption")%></button>
	<button onclick="return removeMember();" align="absmiddle"><img src="<%=SessionManager.getJSPRootURL()%>/images/directory/down_arrow.gif"/>&nbsp;&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupeditremovebutton.caption")%></button>
</p>
<p height="20"></p>
<%-- Available Members - Includes groups and persons in this space --%>
<jsp:include page="include/AvailableMembers.jsp" flush="true" />
<% } %>
<br />
<p class="memberEditBottom" height="30" align="right" style="padding-top:10px">
<%if (showSubmitButton) {%>
	<input type="button" onclick="submitGroup();" value="<%=PropertyProvider.get("prm.directory.roles.groupeditsubmitbutton.caption")%>">
<% } %>
</p>
</form>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%group.clearErrors();%>
