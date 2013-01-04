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
    info="Create Envelope Wizard -- Page 2" 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.*,
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
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

    theForm = self.document.forms[0];
	isLoaded = true;
	selectByValue(theForm.strictnessID, '<jsp:getProperty name="envelope" property="strictnessID" />');
	selectByValue(theForm.statusID, '<jsp:getProperty name="envelope" property="statusID" />');
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
<form method="post" action="<session:getJSPRootURL />/workflow/envelope/EnvelopeWizardPage2Processing.jsp">
<input type="hidden" name="theAction" />
<%-- Outer table, provides left and right padding columns --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <th class="tableHeader" align="left" width="10%">&nbsp;</th>
    <th class="tableHeader" align="left">&nbsp;</th>
    <th class="tableHeader" align="left">&nbsp;</th>
    <th class="tableHeader" align="left" width="10%">&nbsp;</th>
  </tr>	
  <tr>
    <td>&nbsp;</td>
    <td colspan="2" class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.pagetitle")%></td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="2" class="tableHeader" align="left">
	<%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.instruction")%></td>  
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="2">
	<%-- Envelope Properties table --%>
    <table border="0" width="100%" cellspacing="0" cellpadding="2">
      <tr><td colspan="3">&nbsp;</td></tr>
      <tr> 
        <td  align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.rule.label")%></td>
        <td  align="left" nowrap> 
	        <select name="strictnessID">
			    <option value=""></option>
				<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/strictnessIDList.xsl" />
				<jsp:getProperty name="domainList" property="strictnessIDList" />
	        </select>
        </td>
        <td nowrap width="25%"></td>
      </tr>
	  <%=envelope.getErrorsRow("strictnessID")%>
      <tr> 
        <td  align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.priority.label")%></td>
        <td  align="left" nowrap> 
			<select name="priorityID">
			<jsp:getProperty name="domainList" property="priorityListPresentation" />
			</select>
        </td>
        <td nowrap width="25%" /> 
      </tr>
	  <%=envelope.getErrorsRow("priorityID")%>
      <tr> 
        <td  align="left" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.status.label")%></td>
        <td  align="left" nowrap> 
	        <select name="statusID">
			    <option value=""></option>
				<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/status_list_option.xsl" />
				<jsp:getProperty name="domainList" property="statusListPresentation" />
	        </select>
        </td>
        <td width="25%"></td>
      </tr>
	  <tr><td colspan="2" class="fieldContent"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.message")%></td></tr>
	  <%=envelope.getErrorsRow("statusID")%>
      <tr> 
        <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.workflow.envelope.wizardpage2.comments.label")%><br />
          <textarea name="comments" cols="60" rows="3"><c:out value="${envelope.comments}" /></textarea>
        </td>
        <td width="25%"></td>
      </tr>
	  <%=envelope.getErrorsRow("comments")%>
      <tr> 
        <td nowrap colspan="3">&nbsp;</td>
      </tr>
    </table>
    <%-- End of Envelope Properties --%>
    </td>
    <td>&nbsp;</td>
  </tr>
</table>
<%-- End of outer table --%>

<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
					<tb:button type="cancel" />
					<tb:button type="finish" />
				</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
