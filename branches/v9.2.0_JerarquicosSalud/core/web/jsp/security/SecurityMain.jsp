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
|   $Revision: 20726 $
|       $Date: 2010-04-20 08:50:29 -0300 (mar, 20 abr 2010) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Role Entry" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SecurityManager, 
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityManager" class="net.project.security.SecurityManager" scope="session" />
<jsp:useBean id="pnObject" class="net.project.base.PnObject" scope="session" />
<html>
<head>

<%
	String id = request.getParameter("id");
	String objectID = (String) session.getValue("objectID");
	
	if (id != null && !id.equals("")) {
%>
	<%-- 	
		object based permission is temporary disabled	 --%>
		<security:verifyAccess action="modify_permissions"
							   module="<%=net.project.base.Module.SECURITY%>"
							   objectID="<%=objectID%>" />  
							  
<%	} else { %>
		<security:verifyAccess action="view"
							   module="<%=net.project.base.Module.SECURITY%>" /> 

<%
	}

	String noDestroy = request.getParameter("noDestroy");
	
	pnObject.setID(objectID);
	securityManager.setSpace(user.getCurrentSpace());

	if(noDestroy == null || noDestroy.length() == 0)
	{
		securityManager.clearObjectPermission();
	}
	
	securityManager.setPnObject(pnObject);
	securityManager.makeSecurityConsole();

	// Decide which tab to display as selected (if any)
	String myDisplay = request.getParameter("tab");
	String DisplayName = null;
	String peopleTabSelected = "false";
	String rolesTabSelected = "false";
	if (myDisplay != null) {
	 	if (myDisplay.equalsIgnoreCase("People")) {
			peopleTabSelected = "true";
	 		DisplayName=PropertyProvider.get("prm.security.editrole.people.tab");
	 	} else if (myDisplay.equalsIgnoreCase("Roles"))	{
			rolesTabSelected = "true";
			DisplayName=PropertyProvider.get("prm.security.editrole.roles.tab");
	 	}
	 }
%>

<title><%=PropertyProvider.get("prm.security.editrolepage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />
<script language="javascript">
	var theForm;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	if (!theForm.groupID) {
		document.getElementById("removeInstruction").style.display = 'none';
	}
}

function remove() {

	var rolesAnsNames = theForm.groupID;
	if (rolesAnsNames == null || rolesAnsNames.length == 0) {
		extAlert(errorTitle, '<display:get name="prm.security.editrole.remove.noitems.message" />' , Ext.MessageBox.ERROR);
		return;
	}
	
	var checkedCount = 0;
	for(i = 0; i < rolesAnsNames.length; i++) {
		if (rolesAnsNames[i].checked) {
			checkedCount++;
		}
	}
	if (checkedCount == 0) {
		extAlert(errorTitle, '<display:get name="prm.security.editrole.remove.select.message" />' , Ext.MessageBox.ERROR);
		return;
	}
	
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get("prm.security.editrole.remove.message")%>',function(btn){ 
		if( btn == 'yes' ){
			theAction("remove");
			theForm.submit();
		} else {
			return false;
		}
	});
}

function modify() {
	var rolesAnsNames;
	var tab = '<%= request.getParameter("tab") %>';

	if(theForm.Persons) {
	    rolesAnsNames = new Array(theForm.Persons);
	} else if(theForm.Roles) {
	    rolesAnsNames = new Array(theForm.Roles);
	} else {
		rolesAnsNames = new Array();
	}

	if(tab == 'null') {
		extAlert(errorTitle, '<display:get name="prm.security.editrole.add.selecttab.message" />' , Ext.MessageBox.ERROR);
		return;
	} else if(rolesAnsNames.length <= 0) {
		extAlert(errorTitle, '<display:get name="prm.security.editrole.add.noitems.message" />' , Ext.MessageBox.ERROR);
		return;
	}
	
	var checkedCount = 0;
	for(i = 0; i < rolesAnsNames.length; i++) {
		if (rolesAnsNames[i].checked) {
			checkedCount++;
		}
	}
	if (checkedCount == 0) {		
		if(theForm.Persons) {
			extAlert(errorTitle, '<display:get name="prm.security.editrole.add.people.select.message" />' , Ext.MessageBox.ERROR);
		} else if(theForm.Roles) {
			extAlert(errorTitle, '<display:get name="prm.security.editrole.add.roles.select.message" />' , Ext.MessageBox.ERROR);
		}
		return;
	}
	
	theAction("add");
	theForm.submit();
}

function apply() {
	theAction("apply");
	theForm.submit();
}

function close() {
	theAction("close");
	theForm.submit();
}

</script>
</head>

<body class="main"  onLoad="setup();">
<%--
temporary disabled because object level security is disabled --%>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/security/SecurityProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="action" value="<%=Action.VIEW%>">
	<input type="hidden" name="module" value="<%=Module.SECURITY%>">
	<input type="hidden" name="noDestroy" value="1">
<%
String objectType = request.getParameter("objectType");
if (objectType != null &&  objectType.length() > 0) 
    {
%>
	<input type="hidden" name="objectType" value="<%=objectType%>">
<%
    }
%>
<table border="0" width="97%" cellpadding="0" cellspacing="0">
	<tr align="right" colspan="2">
		<td valign="top" align="left" class="pageTitle"><%=PropertyProvider.get("prm.security.editrole.pagetitle")%></td>
	</tr>
	<%-- %>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr align="justify" colspan="2">
		<td class="tableContent"><p>Item-level access permissions are no longer supported. You can still use Setup -> Security -> Module Permissions to set module-level access permissions for a workspace.</p></td>
	</tr>

temporary disabled because object level security is disabled --%>
	<tr><td class="tableContent"><br><%=PropertyProvider.get("prm.security.editrole.applychanges.1.text")%><b><a href="#1" class="channelNoUnderline"><%=PropertyProvider.get("prm.security.editrole.applychanges.2.link")%></a></b><%=PropertyProvider.get("prm.security.editrole.applychanges.3.text")%></td></tr>
</table>

	<!-- Transform Security Settings -->
    <pnet-xml:transform name="securityManager" scope="session" stylesheet="/security/security.xsl" >
    	<pnet-xml:property name="JSPRootURL" value="<%= SessionManager.getJSPRootURL()%>" /> 
    </pnet-xml:transform>	

<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="97%">
	<tr>
		<td colspan="6" class="tableContent">
			<div id="removeInstruction">
				<%=PropertyProvider.get("prm.security.editrole.remove.instruction")%>
			</div>
		</td>
	</tr>
	<tr><td colspan="5">	  
	<tb:toolbar style="action" showLabels="true" align="center">
		<tb:band name="action">
			<tb:button type="up" label='<%=PropertyProvider.get("prm.security.editrole.add.button.label")%>' function="javascript:modify();" />
			<tb:button type="down" label='<%=PropertyProvider.get("prm.security.editrole.remove.button.label")%>' labelPos="right" function="javascript:remove();" />
		</tb:band>
	</tb:toolbar>
	</td></tr>
	<tr><td colspan="5">&nbsp;</td></tr>
	</table>	
	<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="97%">
	<tr>
	<td colspan="6">
	<tab:tabStrip>
		<tab:tab label='<%=PropertyProvider.get("prm.security.editrole.people.tab")%>' href='<%=SessionManager.getJSPRootURL() +"/security/SecurityMain.jsp?noDestroy=1&tab=People&module="+Module.SECURITY%>' selected="<%=peopleTabSelected%>" />
		<tab:tab label='<%=PropertyProvider.get("prm.security.editrole.roles.tab")%>' href='<%=SessionManager.getJSPRootURL() +"/security/SecurityMain.jsp?noDestroy=1&tab=Roles&module="+Module.SECURITY%>' selected="<%=rolesTabSelected%>" />
	</tab:tabStrip>
	</td>
	</tr>
	<%
		if (request.getParameter("tab") == null)
		{
	%>
		<tr><td colspan="6" class="tableContent">
		<ul>
		<li><%=PropertyProvider.get("prm.security.editrole.selectpeople.instruction")%><br>
		<li><%=PropertyProvider.get("prm.security.editrole.selectroles.instruction")%>
		</ul>
		</td></tr>
	<%
		}
	%>
	</table>
	<%
	if(request.getParameter("tab") != null)
	{
	   if( request.getParameter("tab").equals("Roles"))
	   {
	%>
		<!-- My Persons -->
		<input type="hidden" name="tab" value="Roles">
		<jsp:include page="include/securityGroup.jsp" flush="true" />
	<%
	   }
	   else 
	   {
	%>
		<!-- My Persons -->
		<input type="hidden" name="tab" value="People">
		<jsp:include page="include/securityPerson.jsp" flush="true" />
	<%
	   }
	   
	  
	%>
	
		<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="97%">
		<tr><td colspan="6" class="tableContent"><%=PropertyProvider.get("prm.security.editrole.add.instruction", DisplayName)%>
		</td></tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		</table>
	<%
	}
	%>

<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="cancel" function="javascript:close();" />
			<tb:button type="submit" function="javascript:apply();" label='<%=PropertyProvider.get("prm.security.editrole.apply.button.label")%>' />
		</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
<!-- <%=securityManager.getXML()%> -->
