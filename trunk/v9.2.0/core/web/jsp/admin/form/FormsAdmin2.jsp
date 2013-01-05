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
           	net.project.security.User,
           	net.project.base.property.PropertyProvider,
			net.project.space.SpaceList,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.xml.XMLFormatter,
            net.project.util.JSPUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceList" class="net.project.space.SpaceList" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import Javascript --%>

<template:getSpaceJS space="application" />
<security:verifyAccess action="modify" module="<%=Module.APPLICATION_SPACE%>" />
<%
    ///-----Validate security----------------------------------------
    

    //Get the current module from the command-line, if it is available, otherwise
    //default to the APPLICATION_SPACE
    String currentModule = (request.getAttribute("module") == null ? 
                           String.valueOf(Module.APPLICATION_SPACE) : 
                           (String)request.getAttribute("module"));
    String mode = request.getParameter ("mode");
	String type = request.getParameter("type");
	 
	if ( mode != null && mode.equals("search")) {
	
		spaceList.loadFiltered(request.getParameter("filter"));
		
	} else if(type != null) {
	
		if(type.equals("All")) {
			spaceList.loadAllActive();
		} else {
			spaceList.loadFilteredType(type);
		}	
	} else {
	
		type = "project";
		spaceList.loadFilteredType(type);
	}	  	
			
%>

<script language="javascript">
    var theForm;
    var errorMsg;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var Module = '<%= Module.APPLICATION_SPACE %>';

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function back() {
    var theLocation = JSPRootURL + '/admin/form/FormsAdmin1.jsp?'+'<%="module="+Module.APPLICATION_SPACE+"&action="+Action.MODIFY%>';
    self.document.location = theLocation;
}


function search(filter) {
	var str=theForm.type.value;
    self.document.location = JSPRootURL + '/admin/form/FormsAdmin2.jsp?module=' + Module + 
    '&action=<%=Action.MODIFY%>&filter=' + filter + '&mode=search'+'&type='+str+'&FormID='+<%=request.getParameter("FormID")%>;
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_form&section=step2";
    openwin_help(helplocation);
}

function copy() {
	if (verifySelection(theForm,'multiple')) {
    theForm.submit();
	}
}
	
function finish() {
	if (verifySelection(theForm,'multiple')) {
    theForm.submit();
	}	
}

function change() {
	 theForm = self.document.forms[0];
	var str=theForm.type.value;
	self.document.location = JSPRootURL + '/admin/form/'+'<%="FormsAdmin2.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&status=Active" %>'+'&type='+str+'&FormID='+<%= request.getParameter("FormID")%>;
}
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="application"/>
<tb:toolbar style="tooltitle" showAll="true" leftTitle="Forms Administration" rightTitle="Step 2  " groupTitle="prm.application.nav.formadministrator" >
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Forms Administration"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/form/FormsAdmin2.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE+"&action="+Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="FormsAdmin2Processing.jsp" method="post">
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tr>
<td colspan="8">
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td width=1%>&nbsp;</td>
	<td>
	<table>
	<td class="tableHeader" nowrap>&nbsp;&nbsp;Search: 
		<input type="text" name="key" size="15" maxlength="40">
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
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr><td colspan="6">
	</td></tr>		

	<tr><td>&nbsp;</td></tr>
	<tr>
		<table width="90%" border="0" cellspacing="0" cellpadding="0">
			<tr class="channelHeader">
				<td class="channelHeader" width="1%"><img  src=<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
				<td nowrap class="channelHeader">Select atleast one of the following to copy to</td>
				<td align="right" class="channelHeader"><span class="channelHeader"><nobr>&nbsp;<A href="javascript:copy();" class="channelNoUnderline">Copy<IMG alt="Copy" width="20" height="15" border="0" hspace="0" vspace="0" align="top" src="<%=SessionManager.getJSPRootURL()%>/images/<%=JSPUtils.getModifiedCurrentSpaceType(user.getCurrentSpace())%>/channelbar-edit.gif" name="imgmodify"></A>&nbsp;</nobr></span>&nbsp;</td>
				<td align="right" class="channelHeader" width="5%"><img  src=<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
			</tr>
	<tr valign="top">
		<td class="channelContent">&nbsp;</td>
		<td colspan="4" class="channelContent"></td>
	</tr>
</table>
</tr></table>
<input type="hidden" name="FormID" value='<%=request.getParameter("FormID")%>' >
<input type="hidden" name="module" value='<%=Module.APPLICATION_SPACE%>' >
<input type="hidden" name="action" value='<%=Action.MODIFY%>' >
    <%
        XMLFormatter xml = new XMLFormatter();
        xml.setStylesheet("/admin/form/xsl/SpaceCollection.xsl");
        out.println(xml.getPresentation(spaceList.getXMLProperties()));
    %>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
			<tb:band name="action">
				<tb:button type="back" />
			<tb:button type="finish"/>
		</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
