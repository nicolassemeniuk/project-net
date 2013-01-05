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
    info="Check In Document"
    language="java" 
    errorPage="DocumentErrorHandler.jsp"
    import="net.project.document.*,
    net.project.security.User,
	net.project.base.property.PropertyProvider,
	net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="container" class="net.project.document.ContainerBean" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
	  int action = net.project.security.Action.MODIFY;
	  int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	  String id = docManager.getCurrentObjectID();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="modify"
				objectID = "<%=id%>"
/>


<%------------------------------------------------------------------------
  -- Verify Document Check In
  ----------------------------------------------------------------------%>

<%
    DocumentControlManager dcm = new DocumentControlManager();
    Document currentObject = (Document) docManager.getCurrentObject();

    dcm.setUser (docManager.getUser());

    try {
	    dcm.verifyCheckIn (currentObject);
    } catch (CheckInFailedException cife) {
%>
<script language="javascript">
    window.opener.showError("<%=cife.getDisplayError()%>");
    window.close();
</script>
<%
    }

%>


<html>
<head>
<title><display:get name="prm.document.checkin.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
     
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />

<%-- ExtJS Components--%>
<template:import type="javascript" src="/src/extjs/adapter/ext/ext-base.js" />
<template:import type="javascript" src="/src/extjs/ext-all.js" />
<template:import type="javascript" src="/src/ext-components.js" />

<script language="javascript">
function mySubmit() {
    theAction("check_in");

    if (validate()) {
    	try {
        	self.document.forms[0].submit();
        } catch(e) {extAlert(errorTitle,'<display:get name="prm.document.importobject.invalid.file.path" />', Ext.MessageBox.ERROR);}	
    }
}

function theAction (myAction) {
    self.document.forms[0].theAction.value = myAction;
}

function validate() {
    if(!checkTextbox(document.doc_co.file,'<display:get name="prm.document.checkin.filevalidation.message" />'))return false;
    if(!checkDropdown_NoSelect(document.doc_co.statusID,'<display:get name="prm.document.checkin.statusvalidation.message" />'))return false;
    if(!checkTextbox(document.doc_co.notes,'<display:get name="prm.document.checkin.checkincommentsvalidation.message" />'))return false;
    if(!checkMaxLength(document.doc_co.notes,500,'<display:get name="prm.document.checkin.checkincommentslength.message" />')) return false;

    return true;
}

function printNoFileMessage() {
	var erLabel = '<%= request.getAttribute("error.nofile") %>';
	if (erLabel == 'nofile') document.write('<br/><font color="#FF0000"><display:get name="prm.document.checkin.wrongfilepath.message" /></font>');
}

// to check that selected file is valid
function hasFile () {
	if (document.forms[0].file.value)
		return true;
	else {
		extAlert(errorTitle, '<display:get name="prm.document.checkin.filevalidation.message" />' , Ext.MessageBox.ERROR);
		return false;
	}
} 
    
</script>
</head>


<body class="main">

<form name="doc_co" onSubmit="return hasFile();" action="<%=SessionManager.getJSPRootURL()%>/document/CheckInUpload.htm" method="post" ENCTYPE="MULTIPART/form-DATA">
<table border="0" cellspacing="0" width="100%" cellpadding="0">

	<tr class="channelHeader">
	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	<th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.checkin.channel.checkin.title" /></th>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
</table>
  <table border="0" cellspacing="4" align="center" width="100%">
    <tr> 
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkin.documentname.label" /></td>
	    <td> <%= currentObject.getName() %> </td>
    </tr>
    <tr> 
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkin.file.label" /></td>
      <td> 
        <input type="file" name="file">
        <script language="javascript">printNoFileMessage();</script>
      </td>
    </tr>
    <tr> 
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkin.status.label" /></td>
      <td> 
        <select name="statusID">

	<%= DomainListBean.getDocStatusOptionList ( currentObject.getStatusID() ) %>

        </select>
      </td>
    </tr>
    <tr> 
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkin.comments.label" /><br>
      </td>
      <td>
        <textarea rows="3" name="notes" cols="35"></textarea>
      </td>
    </tr>
    <tr> 
      <td nowrap align="left" colspan="2">&nbsp; </td>
    </tr>
  </table>

  <input type="hidden" name="theAction">
  <input type="hidden" name="module" value="<%=module%>">
  <input type="hidden" name="action" value="<%=action%>">
  <input type="hidden" name="id" value="<%=id%>">

</form>

<tb:toolbar style="action" showLabels="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" label='<%= PropertyProvider.get("prm.document.checkin.submit.button.label") %>' function="javascript:mySubmit();" />
        <tb:button type="cancel" function="javascript:parent.opener.focus();self.close();" />
    </tb:band>
</tb:toolbar>
  
</body>
</html>

