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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Popup Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*, net.project.security.*,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%
    // Size the number of columns equal to that of the calling textarea
    // With a minimum of 80
    int cols = 80;
    String colsValue = request.getParameter("cols");
    if (!Validator.isBlankOrNull(colsValue)) {
        cols = Integer.valueOf(colsValue).intValue();
        cols = Math.max(cols, 80);
    }
%>
<META http-equiv="expires" content="0">
<html>
<head>

<title><%=request.getParameter("label")%></title>

<template:import type="css" src="/styles/pnet.css"/>
<template:getSpaceCSS />

<script language="javascript">
var fieldRef = 1;
var popupForm;
var isLoaded = false;

function copyFieldData() {
	popupForm = self.document.forms[0];
	popupForm.popupField.value = fieldRef.value;
	isLoaded = true;
}

function popupCancel() {
	window.open("", "form_popup").close();
}

function popupApply() {
	fieldRef.value = popupForm.popupField.value;
	window.open("", "form_popup").close();
}

function setup() {
	if (parent.opener.fieldCopyFailed == true) {
		parent.opener.setFieldRef();
		copyFieldData();
	}
}
</script>

</head>

<body bgcolor="#FFFFFF" onLoad="setup()">
<form name="popupForm" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="75%">
    <tr>
      <td><span class=smallHeaderGrey style="padding-left:30px"><%=request.getParameter("label")%></span></td>
     </tr>
    <tr align="left"> 
      <td colspan="2" style="font-family : 'monospace'; font-size : 10pt;padding-left:30px" > 
        <textarea name="popupField" cols="<%=cols%>" rows="30" wrap="virtual"></textarea>
      </td>
    </tr>
  </table>
<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" function="javascript:popupApply();" />
		<tb:button type="cancel" function="javascript:popupCancel();" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
