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
    info="Task Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
			net.project.security.SessionManager" 
%>
<%@ page import="net.project.base.Module, net.project.security.Action"%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}


function next () {
    theForm = self.document.forms[0];

	var fileName =theForm.file.value;
	var pos = fileName.lastIndexOf(".");
	var ext = fileName.substr(pos + 1);

	if(ext != "xml" && ext != ''){
		extAlert(errorTitle, "<%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.formatnotsupported.message")%>", Ext.MessageBox.ERROR);
		return;
	}
	
	if(!checkTextbox(theForm.file,'<%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.filerequired.message")%>')){
		theForm.file.focus();
	}else{
		try{
			theForm.submit();
		}catch(err){ extAlert(errorTitle, "<display:get name='prm.form.formdefintionimport.fileupload.invalidfilepath.message'/>", Ext.MessageBox.ERROR); }
	}
}
function reset() {
	theForm.reset();
}


function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=formdefinition_import&section=upload";
	openwin_help(helplocation);
}


function noenter() {
  return !(window.event && window.event.keyCode == 13); 
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">

	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/form/designer/FormDefinitionUpload.jsp"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%= SessionManager.getJSPRootURL() %>/form/designer/formImportClient.htm"  ENCTYPE="MULTIPART/form-DATA">
<%-- Standard form fields --%>
	<input type="hidden" name="theAction">

<%-- Required by most --%>
    <input type="hidden" name="action" value="<%= Action.VIEW %>">
    <input type="hidden" name="module" value="<%= Module.FORM %>">

<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0">
	<tr class="channelHeader">
		<td width="1%"><IMG height=15 alt="" src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 border=0></td>
		<td nowrap class="channelHeader" colspan="2"><%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.channel.selectfile.title")%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.channel.required.title")%></NOBR></td>
		<td align=right width="1%"><IMG height=15 alt="" src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 border=0></td>
	</tr>


    <tr align="left" valign="top" >
        <td>&nbsp;</td>
        <td nowrap class="fieldRequired"><%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.file.label")%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td><input type="file" name="file" size="50" onkeypress="return noenter()" ></td>
        <td>&nbsp;</td>
    </tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
        <tb:band name="action">
            <tb:button type="next" />
        </tb:band>
</tb:toolbar>

</form>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>

<script>
	var erLabel = '<%= request.getAttribute("error.invalid_file") %>';
	if (erLabel == 'invalid_file'){
		extAlert(errorTitle, "<%=PropertyProvider.get("prm.form.formdefintionimport.fileupload.formatnotsupported.message")%>", Ext.MessageBox.ERROR);	
	}

</script>
