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
    info="Workflow" 
	language="java" 
	errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.Space,net.project.security.User,
			net.project.security.SessionManager,net.project.datatransform.csv.CSVWizard,
			net.project.datatransform.*,net.project.datatransform.csv.transformer.*,
			net.project.datatransform.csv.map.*,net.project.datatransform.csv.CSV,
			net.project.datatransform.csv.ColumnMapPageProcessor,net.project.datatransform.csv.DataResolutionPageRenderer,
			net.project.base.attribute.*,
            net.project.base.Module,
            net.project.security.Action"
			
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="columnMaps" class="net.project.datatransform.csv.map.ColumnMaps" scope="session" />
<jsp:useBean id="drPageRenderer" class="net.project.datatransform.csv.DataResolutionPageRenderer" scope="session" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />
<%-- Import Javascript --%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
	var theLocation='<%=SessionManager.getJSPRootURL() +csvWizard.getReferrer()%>';
	self.location = theLocation;
}

function create() { 
	theAction("create");
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.id.value = '';
	theForm.submit();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=csv_import&section=data_resolution";
	openwin_help(helplocation);
}

function validate() {
    return true;
}

function next() {
    if (validate()) {
	    theForm.submit();
    }
}

function back() {
    history.back();
}

function handleDateFormat(field) {
	// this is to check if the user has entered 
	// valid (as per java formatting) but made mistake like entering 'm' instead of 'M' for day,
	// causing potential errornous import of data.
	var dateFormat = field.value;
	if(dateFormat != "") {
		if(dateFormat.indexOf('m') < dateFormat.indexOf('y') || dateFormat.indexOf('m') < dateFormat.indexOf('d')) {
			dateFormat = dateFormat.replace('m', 'M');
			if(dateFormat.indexOf('m') < dateFormat.indexOf('y') || dateFormat.indexOf('m') < dateFormat.indexOf('d')) {
				dateFormat = dateFormat.replace('m', 'M');
			}
		}
	}
	field.value = dateFormat;
}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--</template:put>--%>
<%-- End of head --%>

<%-- Begin Content --%>		
<%--<template:put name="content">--%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
			<history:history>
			<history:page display='<%=PropertyProvider.get("prm.form.csvimport.dataresolution.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/datatransform/csv/DataResolution.jsp"%>' />
		</history:history>
	
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post"action="<session:getJSPRootURL />/datatransform/csv/DataResolutionProcessing.jsp">
<input type="hidden" name="module" value="<%=Module.FORM%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">
<table border="0" align="left" cellpadding="0" cellspacing="0">
<%
    String renderString=drPageRenderer.render(attributes ,csv,columnMaps);
    out.println(renderString);
%>	
<tr><td colspan="4">&nbsp;</td></tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
			<tb:band name="action">
				<tb:button type="cancel" />
				<tb:button type="back" />
				<tb:button type="next" />
			</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
