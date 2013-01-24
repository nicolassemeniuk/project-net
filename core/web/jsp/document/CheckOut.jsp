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
    info="Check Out Document"
    language="java"
    errorPage="DocumentErrorHandler.jsp"
    import="net.project.document.*,
    		net.project.security.User,
			net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
            net.project.xml.XMLFormatter,
            net.project.util.Validator,
            net.project.xml.XMLUtils,
            net.project.base.Module,
            net.project.util.HTMLUtils,
            java.util.Date"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
    int action = net.project.security.Action.MODIFY;
    int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
    String id = docManager.getCurrentObjectID();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:verifyAccess module="<%=module%>" action="modify" objectID = "<%=id%>" />

<%------------------------------------------------------------------------
  -- Verify Document Checkout
  ----------------------------------------------------------------------%>
<%
    DocumentControlManager dcm = new DocumentControlManager();
    Document currentObject = (Document) docManager.getCurrentObject();

    dcm.setUser (docManager.getUser());
    dcm.verifyCheckOut (currentObject);
%>


<html>
<head>
<title><display:get name="prm.document.checkout.title" /></title>

<template:getSpaceCSS />

<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/client_state.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/extjs/adapter/ext/ext-base.js" />
<template:import type="javascript" src="/src/extjs/ext-all.js" />
<template:import type="javascript" src="/src/ext-components.js" />
<script language="javascript">

function theAction (myAction) {
    document.forms[0].theAction.value = myAction;
}

function validate() {
    if (!checkMaxLength(document.forms[0].notes,500,'<display:get name="prm.project.process.noteslength.message" />')) return false;

    return true;
}

function mySubmit (action) {
    if (validate()) {
        theAction("check_out");
        self.document.forms[0].submit();
    }
}

</script>
</head>

<body class="main">

<form name="CheckOut" action="<%=SessionManager.getJSPRootURL()%>/document/CheckOutProcessing.jsp" method="post" >
<input type="hidden" name="module" value="<%=Module.DOCUMENT %>"/>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
<th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.checkout.channel.checkout.title" /></th>
<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<table border="0" cellspacing="4" align="center" width="100">
<tr>
  <td class="warnText" colspan="2">
    <%
        if (errorReporter.errorsFound()) {
            XMLFormatter formatter = new XMLFormatter();
            formatter.setXML(errorReporter.getXML());
            formatter.setStylesheet("/base/xsl/error-report.xsl");
            out.println(formatter.getPresentation());
            //Clear out the error reporter now that we are done with it
            errorReporter.clear();
        }
        String todayString = user.getDateFormatter().formatDate(new Date(), user.getDateFormat());
    %>
  </td>
</tr>
<tr>
  <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkout.documentname.label" /></td>
  <td> <%= HTMLUtils.escape(currentObject.getName()) %> </td>
</tr>
<tr>
  <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.checkout.returndate.label" /></td>
  <td><input type="text" name="ckoReturn" size="12" value="<%= todayString %>"><util:insertCalendarPopup fieldName="ckoReturn"/></td>
</tr>
<tr>
  <td nowrap align="left" valign="top" class="fieldNonRequired"><display:get name="prm.document.checkout.comments.label" /></td>
  <td>
  <textarea rows="3" name="notes" cols="35"><%
if (!Validator.isBlankOrNull(request.getParameter("notes"))) {
  out.println(request.getParameter("notes"));
} %></textarea></td>
</tr>
<tr>
  <td nowrap colspan="2" align="left">&nbsp;</td>
</tr>

</table>

<tb:toolbar style="action" showLabels="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" label='<%= PropertyProvider.get("prm.document.checkout.submit.button.label") %>' function="javascript:mySubmit();" />
        <tb:button type="cancel" function="javascript:parent.opener.focus();self.close();" />
    </tb:band>
</tb:toolbar>


<div align="left">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="id" value="<%=id%>">
</div>

</form>

<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
