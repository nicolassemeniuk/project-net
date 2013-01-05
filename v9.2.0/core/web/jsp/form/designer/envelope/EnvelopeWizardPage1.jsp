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
|   $Revision: 17297 $
|       $Date: 2008-04-24 21:40:00 +0530 (Thu, 24 Apr 2008) $
|     $Author: tincholiguori $
|
| EnvelopeWizardPage1
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Create Envelope Wizard -- Page 1" 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.security.*,
			net.project.workflow.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	Envelope envelopeBean = envelopeManagerBean.getCurrentEnvelope();
    pageContext.setAttribute("envelope", envelopeBean, PageContext.PAGE_SCOPE);
    domainList.setSpace(user.getCurrentSpace());
%>
<jsp:useBean id="envelope" type="net.project.workflow.Envelope" scope="page" />
<html>
<head>
<title><%=PropertyProvider.get("prm.workflow.envelope.wizardpage1page.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<template:import type="javascript" src="/src/standard_prototypes.js" />
<script language="javascript">
	//updated
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

    theForm = self.document.forms[0];
	isLoaded = true;
	selectByValue(theForm.workflowID, '<jsp:getProperty name="envelope" property="workflowID" />');
}

function cancel() {
	theAction("cancel");
	theForm.submit();
}

function finish() {
	theAction("finish");
	theForm.submit();
}

function back() {
	theAction("back");
	theForm.submit();
}

function next() {
	theAction("next");
	theForm.submit();
}
/*
  Select a specific entry in a dropdown (select list)
  theSelect - select object
  theValue - value of the option to select
  e.g.
  	selectByValue(theForm.ownerID, '100');
  of course, the value is most likey going to come from a bean property
 */
function selectByValue(theSelect, theValue) {
	if (theSelect) {
		for (i = 0; i < theSelect.options.length; i++) {
			if (theSelect.options[i].value == theValue) {
				theSelect.options[i].selected = true;
			}
		}
	}
}
</script>
</head>
<body class="main" onLoad="setup();">
<form method="post" action="<session:getJSPRootURL />/form/designer/envelope/EnvelopeWizardPage1Processing.jsp">
<input type="hidden" name="theAction" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <th class="tableHeader" align="left" width="10%">&nbsp;</th>
    <th class="tableHeader" align="left">&nbsp;</th>
    <th class="tableHeader" align="left">&nbsp;</th>
    <th class="tableHeader" align="left" width="10%">&nbsp;</th>
  </tr>	
  <tr>
    <td>&nbsp;</td>
    <td colspan="2">
    <%-- Page Header --%>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage1.pagetitle")%></td>
      </tr>
    </table>
	</td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="2" class="tableHeader" align="left">
	<%=PropertyProvider.get("prm.workflow.envelope.wizardpage1.instruction")%>
	</td>  
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="2">
	<%-- Envelope Properties form --%>
    <table border="0" width="100%" cellspacing="0" cellpadding="2">
      <tr><td colspan="3">&nbsp;</td></tr>
      <tr> 
        <td  align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage1.workflow.label")%></td>
        <td  align="left" nowrap> 
	        <select name="workflowID">
			    <option value=""></option>
				<jsp:setProperty name="envelopeManagerBean" property="stylesheet" value="/form/designer/envelope/xsl/workflow_list_option.xsl" />
				<jsp:getProperty name="envelopeManagerBean" property="compatibleWorkflowListPresentation" />
	        </select>
        </td>
        <td width="25%"></td>
      </tr>
	  <%=envelope.getErrorsRow("workflowID")%>
      <tr> 
        <td  align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage1.title.label")%></td>
        <td  align="left" nowrap> 
          <input type="text" name="name" maxlength="80" size="30" value='<c:out value="${envelope.name}"/>' />
        </td>
        <td width="25%"></td>
      </tr>
	  <%=envelope.getErrorsRow("name")%>
      <tr> 
        <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage1.description.label")%><br>
          <textarea name="description" cols="60" rows="3"><c:out value="${envelope.description}" /></textarea>
        </td>
        <td width="25%"></td>
      </tr>
	  <%=envelope.getErrorsRow("description")%>
      <tr> 
        <td nowrap colspan="3">&nbsp;</td>
      </tr>
    </table>
    </td>
    <td>&nbsp;</td>
  </tr>
</table>

<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
				<tb:button type="cancel" />
				<tb:button type="next" />
			</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
