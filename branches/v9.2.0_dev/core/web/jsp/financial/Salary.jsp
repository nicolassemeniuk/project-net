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
%>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.SALARY%>"
					   objectID="<%=id%>" /> 

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
	if(StringUtils.isEmpty(id)){
		id = user.getCurrentSpace().getID();
	}
%>
<script language="javascript"> 	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  </script>
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;
	
function setup()
{
	page=true;
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=salary&section=participants";
	openwin_help(helplocation);
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

<%
	// close the add team member popup if was posting to this page
    if (request.getMethod().equals("POST") && request.getParameter("theAction").equals("finish"))
        {
%>
	window.open("", "member_wizard").close();
<%
        }
%>

</script>
</head>
<body class="main" id='bodyWithFixedAreasSupport' style="overflow-x:hidden" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	<tb:toolbar style="tooltitle" showAll="true" subTitle="<%=SessionManager.getUser().getCurrentSpace().getName() %>" groupTitle='<%=PropertyProvider.get("prm.financial.salary.title")%>'>
		<tb:band name="standard">
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.financial.salary.modify.button.tooltip") %>' />
		</tb:band>
</tb:toolbar>
<div id='content' style="padding-right:10px;padding-top:20px;">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
	<tr>
	<td id="leftPane" valign="top">
	<table border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td width="130" class="left_tab"><a href="<%=SessionManager.getJSPRootURL() + "/financial/Salary.jsp?module=" + Module.SALARY%>" style="text-decoration: none;">
		        	<span class="active_tab"><%=PropertyProvider.get("prm.financial.salary.tab.salary.title")%></span></a></td>
		        <td>&nbsp;</td>
		      </tr>
	   </table>
	</td>
	</tr>
	</table>
	<div class="block-content UMTableBorder" style="padding-right:2px;overflow-x:auto;">
	<form method="post" action="<%=SessionManager.getJSPRootURL()%>/financial/SalaryProcessing.jsp">
		<input type="hidden" name="theAction">
		<input type="hidden" name="module" value="<%=Module.SALARY%>">
	    <input type="hidden" name="action" value="<%=Action.VIEW%>">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
			<tr>
			<td colspan="2" class="errorMessage">
				<%net.project.persistence.PersistenceException ee = (net.project.persistence.PersistenceException)session.getAttribute("exception");
				if(ee!=null) { 
					out.println(ee.getMessage()+"\n");
					session.removeAttribute("exception");
				}%>
			</td>
			</tr>
			<tr></tr>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<td class="tableHeader" nowrap>&nbsp;&nbsp;<%=PropertyProvider.get("prm.financial.salary.roster.search.label")%> 
					<input type="text" name="key" value='<c:out value="${searchKey}"/>' size="40" maxlength="40" onKeyDown="if(event.keyCode==13) searchButton()">
				</td>
				<% if(Boolean.parseBoolean(PropertyProvider.get("prm.directory.directory.searchmode.isenabled"))){%>
				<td><span class=tableContent><search:letter /></span></td>
				<%}%>
			</tr>	
			<tr><td>&nbsp;</td></tr>
			<tr>
			<tr class="channelHeader">
				<td class="channelHeader" colspan="2" style="height:20;padding-left: 7px;"><%=PropertyProvider.get("prm.financial.salary.tab.participants.title")%></td>
			</tr>
			<tr>
			<%-- Display the people in the roster. --%>
				<td colspan="2">
						<%-- 	Apply stylesheet to format project team roster --%>
					
			        <pnet-xml:transform name="roster" scope="session" stylesheet="/salary/xsl/salary.xsl">
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
