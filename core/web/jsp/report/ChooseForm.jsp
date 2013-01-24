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
    info="Report Module Main Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.report.ReportType,
            net.project.security.SessionManager,
            net.project.form.FormMenu,
            java.util.Iterator,
            net.project.form.FormMenuEntry,
            java.util.List,
            net.project.form.Form,
            net.project.gui.html.HTMLOptionList,
            java.util.Collections,
            java.util.ArrayList,
            net.project.gui.html.HTMLOption,
            net.project.base.property.PropertyProvider,
            net.project.form.FormException,
            net.project.form.FormField"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<%
    //Get the report type
    ReportType reportType = ReportType.getForID(request.getParameter("reportType"));

    //Load forms for this space
    FormMenu formMenu = new FormMenu();
    formMenu.setSpace(SessionManager.getUser().getCurrentSpace());

    String formID = request.getParameter("formID");
    List formFields = Collections.EMPTY_LIST;

    try {
        formMenu.load();

        if (((formID == null) || (formID.trim().length() == 0))
            && (formMenu.getEntries().size() > 0)) {
            formID = ((FormMenuEntry)formMenu.getEntries().get(0)).getID();
        }

        if ((formID != null) && (formID.trim().length() > 0)) {
            Form form = new Form();
            form.setID(formID);
            form.setSpace(SessionManager.getUser().getCurrentSpace());
            form.loadFields(false);
            formFields = (List)form.getFields().clone();

            //Remove any form fields that cannot be filtered.
            for (Iterator it = formFields.iterator(); it.hasNext();) {
                FormField formField = (FormField) it.next();
                if (!formField.dataColumnExists()) {
                    it.remove();
                }
            }
        } else {
            formFields = new ArrayList();
        }
    } catch (FormException fe) {
        //This exception is thrown if no forms are defined
        response.sendRedirect(SessionManager.getJSPRootURL()+"/report/NoFormsFound.jsp?" +
            "module="+Module.REPORT+"&reportType="+reportType.getID());
    }

%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
var theForm;

function setup() {
	theForm = self.document.forms[0];
}

function formChange() {
    //Change the text of the field id listbox to let the user know we are reloading
    theForm.formFieldID.length = 1;
    theForm.formFieldID.options[0].text = "<%=PropertyProvider.get("prm.form.report.common.chooseformjsp.loading.message")%>";

    //Figure out the correct form id.  This isn't consistent between browsers
    var formID;
    if (theForm.formID.value) {
        formID = theForm.formID.value;
    } else if (theForm.formID.options) {
        formID = theForm.formID.options[theForm.formID.selectedIndex].value;
    }

    //Redirect back to this page, supplying a form id
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/report/ChooseForm.jsp?"+
        "module=<%=Module.REPORT%>&reportType=<%=reportType.getID()%>&formID="+ formID;
}

function submit() {
    theForm.submit();
}

function cancel() {
    history.back();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=report_choose_form";
	openwin_help(helplocation);
}

</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.reports">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="@prm.form.report.common.chooseform.chooseformfield.message"
					jspPage='<%=SessionManager.getJSPRootURL() + "/report/ChooseForm.jsp"%>'
					queryString='<%="module="+net.project.base.Module.REPORT+"&reportType="+reportType.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<form name="selectForm" method="post" action="<%=SessionManager.getJSPRootURL()%>/report/parameters.jsp">
<input type="hidden" name="module" value="<%=net.project.base.Module.REPORT%>">
<input type="hidden" name="reportType" value="<%=reportType.getID()%>">
<input type="hidden" name="processAdditionalParameters" value="true">

<table border="0" width="97%" cellspacing="0" cellpadding="0">
<tr>
	<td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;<%=PropertyProvider.get("prm.form.report.common.chooseform.chooseformfield.message")%></td>
	<td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
</table>


<table>
<tr class="channelContent">
    <td class="tableHeader"><display:get name="prm.form.report.common.chooseform.pleasechooseform.message"/></td>
    <td>
      <select name="formID" onChange="formChange();">
      <%=HTMLOptionList.makeHtmlOptionList(formMenu.getEntries(), formID)%>
      </select>
    </td>
</tr>
<tr class="channelContent">
    <td class="tableHeader"><display:get name="prm.form.report.common.chooseform.pleasechooseformfield.message"/></td>
    <td>
      <select name="formFieldID">
      <%=HTMLOptionList.makeHtmlOptionList(formFields)%>
      </select>
    </td>
</tr>
</table>

</form>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="submit"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
