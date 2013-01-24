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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.report.ReportType,
            net.project.report.IReport,
            java.util.Map,
            java.util.Iterator,
            net.project.space.ISpaceTypes,
            net.project.report.ReportScope"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%
    //Save a report of the proper type into the session scope
    ReportType reportType = ReportType.getForID(request.getParameter("reportType"));
    IReport report = reportType.getInstance(user.getCurrentSpace().getType().equals(ISpaceTypes.ENTERPRISE_SPACE) ? ReportScope.GLOBAL : ReportScope.SPACE);
    pageContext.removeAttribute("report", PageContext.SESSION_SCOPE);
    pageContext.setAttribute("report", report, PageContext.SESSION_SCOPE);

    String processParameters = request.getParameter("processAdditionalParameters");
    if ((processParameters != null) && (processParameters.equals("true"))) {
        report.populateParameters(request);
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

function reset() {
    theForm.reset();
}
function validateSortingOption(){
	//The desired element name will not get by fields name because of it integer name value. so generating array or sorting element list.
	var allSelectTagElement = document.getElementsByTagName("select");
	var allInputTagElement = document.getElementsByTagName("input");
	var sortingSelectList = new Array();
	var sortingRadioList = new Array();
	//first go through sorting dorpdown(select) element.
	for(var i = 0; i < allSelectTagElement.length; i++){
		if ( allSelectTagElement[i].name == '10' || allSelectTagElement[i].name == '20' || allSelectTagElement[i].name == '30'){
			sortingSelectList.push(allSelectTagElement[i].value);
		}
	}
	//and then go through sorting redio element.
	for(var i = 0 ; i < allInputTagElement.length; i++){
		if ((allInputTagElement[i].name == '10order' || allInputTagElement[i].name == '20order' || allInputTagElement[i].name == '30order' )
			&& allInputTagElement[i].type == 'radio' && allInputTagElement[i].checked ){
			sortingRadioList.push(allInputTagElement[i].value);
		}
	}
	//if any option is slected to create conflict soting option show alert.
	if((sortingSelectList[0] == sortingSelectList[1] && sortingRadioList[0] != sortingRadioList[1])
		|| (sortingSelectList[1] == sortingSelectList[2] && sortingRadioList[1] != sortingRadioList[2])
		|| (sortingSelectList[0] == sortingSelectList[2] && sortingRadioList[0] != sortingRadioList[2])){
		   extAlert(errorTitle,'<%=PropertyProvider.get("prm.report.channel.sorting.errormessage")%>', Ext.MessageBox.ERROR);
		   return false;
	}
	return true;
}

function submit() {
	if(validateSortingOption()){
		theForm.submit();
	}
}

function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL()%>/report/Main.jsp?module=<%=Module.REPORT%>';
}

function setup() {
    theForm = self.document.forms[0];
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=report_parameters";
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
			<history:page display='<%=PropertyProvider.get("prm.report.parameterjsp.history.message", report.getReportName())%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/report/parameterPages/parameters.jsp?module="+Module.REPORT%>'
					queryString='<%="module="+Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<form name="reportParameters" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ReportingServlet">
<input type="hidden" name="module" value="<%=Module.REPORT%>"/>
<input type="hidden" name="objectID" value="<%=user.getCurrentSpace().getID()%>">
<input type="hidden" name="reportType" value="<%=reportType.getID()%>">
<input type="hidden" name="reportScope" value="<%=report.getScope().getID()%>">
<%
    Map parameters = report.getPrepopulatedParameters();
    for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
        String keyName = (String)it.next();
        String keyValue = (String)parameters.get(keyName);
%>
<input type="hidden" name="<%=keyName%>" value="<%=keyValue%>">
<%  } %>

<channel:channel name="parameters" customizable="false">
<% if (reportType.getCustomParameterChannelURL() != null) { %>
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.parameters.name")%>' minimizable="false" width="100%"
        closeable="false" row="1" column="1"
        include='<%=reportType.getCustomParameterChannelURL()+"?module=310"%>'/>
<% } %>
<% if (report.getFilterList().size() > 0) { %>
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.filter.name")%>' minimizable="false" width="100%"
        closeable="false" row="2" column="1"
        include="/report/filter.jsp?module=310"/>
<% } %>
<% if (report.getGroupingList().size() > 0) { %>
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.grouping.name")%>' minimizable="false" width="100%"
        closeable="false" row="3" column="1"
        include="/report/group.jsp?module=310"/>
<% } %>
<% if (report.getSorterList().size() > 0) { %>
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.sorting.name")%>' minimizable="false" width="100%"
        closeable="false" row="4" column="1"
        include="/report/sort.jsp?module=310"/>
<% } %>
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.outputtype.name")%>' minimizable="false" width="25%"
        closeable="false" row="5" column="1"
        include="/report/OutputType.jsp?module=310"/>
    <channel:insert name="ReportParameters" title='<%=PropertyProvider.get("prm.report.channel.showreportparameters.name")%>' minimizable="false" width="75%"
        closeable="false" row="5" column="2"
        include="/report/ShowReportParameters.jsp?module=310"/>
<% if (!reportType.getID().equals("nur")) { %>        
    <channel:insert name="Main" title='<%=PropertyProvider.get("prm.report.channel.assignmenttype.name")%>' minimizable="false" width="100%"
        closeable="false" row="6" column="1"
        include="/report/assignmentType.jsp?module=310"/>        
<% } %>        
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit"/>
		<tb:button type="cancel"/>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>


