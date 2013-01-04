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
    info="Forms Administration Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.User,
			net.project.form.FormMenu,
			net.project.security.Action,
            net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<template:getDoctype />
<html>
<head>
<%
    ///-----Validate security----------------------------------------
    

    //Get the current module from the command-line, if it is available, otherwise
    //default to the APPLICATION_SPACE
    String currentModule = (request.getAttribute("module") == null ? 
                           String.valueOf(Module.APPLICATION_SPACE) : 
                           (String)request.getAttribute("module"));
    
	 String mode = request.getParameter ("mode");
	 String type = request.getParameter("type");
	  
	if(mode != null && mode.equals("search")) {
		formMenu.loadFiltered(request.getParameter("filter"));
	} else if( type != null) {
	
		if(type.equals("All")) {
			formMenu.loadAll();
		} else {
			formMenu.loadFilteredType(type);
		}	
		
	} else {
		type = "project";
		formMenu.loadFilteredType(type);
		
	}
	
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import Javascript --%>

<template:getSpaceJS space="application" />
<script language="javascript">
    var theForm;
    var errorMsg;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var Module = '<%= Module.APPLICATION_SPACE %>';
	
	if (document.layers)
	document.captureEvents(Event.KEYPRESS);	
	window.onkeypress = keyhandler;

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function help() {
   var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_form&section=step1";
    openwin_help(helplocation);
}

function copy() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	theForm.theAction.value="active";
	theForm.action.value ='<%=Action.MODIFY%>';
	theForm.FormID.value =getSelection(theForm);
    theForm.submit();
	}
}

function cancel() {
    var theLocation = JSPRootURL + '/admin/Main.jsp?'+'<%="module="+Module.APPLICATION_SPACE+"&id="+net.project.admin.ApplicationSpace.DEFAULT_APPLICATION_SPACE.getID()%>';
    self.document.location = theLocation;
}

function next() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	theForm.theAction.value="active";
	theForm.action.value ='<%=Action.MODIFY%>';
	theForm.FormID.value =getSelection(theForm);
    theForm.submit();
	}
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.form.list.deleteform.message")%>', function(btn) { 
		if(btn == 'yes'){ 
			theForm.theAction.value="remove";
			theForm.action.value ='<%=Action.MODIFY%>';
			theForm.FormID.value =getSelection(theForm);
   			theForm.submit();
		}else{
		 	return false;
			}
		});
	}
}

function reset() {
    self.document.location = JSPRootURL + '/admin/form/FormsAdmin1.jsp?module=' + Module + '&action=<%=Action.MODIFY%>';
}

function search(filter) {
	var str=theForm.type.value;
    self.document.location = JSPRootURL + '/admin/form/FormsAdmin1.jsp?module=' + Module + 
    '&action=<%=Action.MODIFY%>&filter=' + filter + '&mode=search'+'&type='+str;
}

function change() {
	var str=theForm.type.value;
	self.document.location = JSPRootURL + '/admin/form/'+'<%="FormsAdmin1.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&status=Active" %>'+'&type='+str;
}


 function keyhandler(e) {
   		var event = e ? e : window.event;
   		if (event.keyCode == 13){
			search(theForm.key.value);
			event.keyCode=0;
			return false;
		}
	}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="application"/>
<tb:toolbar style="tooltitle" showAll="true" leftTitle="Forms Administration" rightTitle="Step 1"  groupTitle="prm.application.nav.formadministrator">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Forms Administration"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/form/FormsAdmin1.jsp"%>'
                            queryString='<%="module="+currentModule+"&action="+Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
		<tb:button type="copy" />
		<tb:button type="remove" label="Delete Form"/>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form action="FormsAdmin1Processing.jsp" method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%= currentModule %>">
	<input type="hidden" name="action">
	<input type="hidden" name="FormID">
	<input type="hidden" name="status">

<table  border="0" cellpadding="0" cellspacing="0"  width="97%">

<tr>
<td colspan="8">
</tr>
<tr>
	<td width=1%>&nbsp;</td>
	<td>
        <table>
            <td class="tableHeader" nowrap>&nbsp;&nbsp;Search:
                <input type="text"  onkeyPress="keyhandler(event)" name="key" size="15" maxlength="40">
                <a href="javascript:search(self.document.forms[0].key.value);"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"></a>
                &nbsp;&nbsp;
            </td>
            <td colspan="1"><span class=tableContent><search:letter /></span></td>
        </table>
	</td>
	<td class="tableHeader" nowrap>
        &nbsp;&nbsp;<%=PropertyProvider.get("prm.project.admin.form.workspace.type.label")%> &nbsp;&nbsp;
        <select name="type" SIZE="1"  onChange='change();'>
        <option <%=type.equals("All") ? "SELECTED" :"" %> value='All'><%=PropertyProvider.get("prm.project.admin.form.all.label")%>
        <option <%=type.equals("project") ? "SELECTED" :"" %> value='project'><%=PropertyProvider.get("prm.project.admin.project.label")%>
        <option <%=type.equals("business") ? "SELECTED" :"" %> value='business'><%=PropertyProvider.get("prm.project.admin.business.label")%>
        <option <%=type.equals("person") ? "SELECTED" :"" %> value='person'><%=PropertyProvider.get("prm.project.admin.personal.label")%>
        <option  <%=type.equals("methodology") ? "SELECTED" :"" %> value='methodology'><%=PropertyProvider.get("prm.project.admin.methodology.label")%>
        </select>
	</td>
</tr>
<tr><td>&nbsp;</td></tr>
<%
    String showMessage = request.getParameter("showMessage");
    if (showMessage != null && showMessage.equals("true")) { %>
<tr>
    <td class="tableHeader" colspan="6">
        <%=PropertyProvider.get("prm.project.admin.forms.footer.label")%>
    </td>
</tr>
<% } %>
<tr><td colspan="6"></td></tr>
<tr>
    <td>&nbsp;</td>
    <jsp:setProperty name="formMenu" property="stylesheet" value="/admin/form/xsl/FormCollection.xsl" />
    <jsp:getProperty name="formMenu" property="presentation" />
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	    <tb:button type="cancel" />
	    <tb:button type="next" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
