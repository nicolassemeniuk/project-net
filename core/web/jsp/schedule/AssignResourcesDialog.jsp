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
    info="Dialog box to assign resources"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.schedule.calc.TaskCalculationType"
%>
<%@ page import="net.project.resource.AssignmentRoster"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="net.project.base.property.PropertyProvider"%>
<%@ page import="net.project.util.NumberFormat"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<jsp:useBean id="assignmentRoster" class="net.project.resource.AssignmentRoster" scope="request" />
<jsp:useBean id="idList" class="java.lang.String" scope="request"/>


<%@page import="net.project.space.SpaceTypes"%><html>
<head>
<title><display:get name="prm.schedule.assignresourcesdialog.title"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
<%
    NumberFormat nf = NumberFormat.getInstance();
%>
var theForm;

function setup() {
    theForm = self.document.forms[0];
    <%-- For fixed Duration Effort driven tasks, the percentage value will be ignored when
         replacing assignments, or adding new assignments since it is automatically calculated --%>
    setFixedDurationEffortDrivenSelected(<%=request.getAttribute("isFixedDurationEffortDrivenSelected")%>);

}

function cancel() {
    window.close();
}

function submit() {
    var integerErrMsg = '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>';
    var percentTextbox;

    if (verifySelectionForField(theForm.resource, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        if (theForm.resource.checked) {
            // A single checkbox
            percentTextbox = eval("theForm.percent_"+theForm.resource.value);
            if (!verifyNonBlankField(percentTextbox.value)){
                percentTextbox.value = "<%=nf.formatNumber(100)%>";
            }else{
            	if(percentTextbox.value == 0){
                  		extAlert(errorTitle, '<%=PropertyProvider.get("prm.schedule.assigne.resource.error.message")%>' , Ext.MessageBox.ERROR);
                  		return;
                }
            }
        } else {
            for(var i=0; i< theForm.resource.length; i++) {
                if (theForm.resource[i].checked) {
                    percentTextbox = eval("theForm.percent_"+theForm.resource[i].value);
                    if (!verifyNonBlankField(percentTextbox.value)){
                   			percentTextbox.value = "<%=nf.formatNumber(100)%>";
                     }else{
                     	if(percentTextbox.value == 0){
                     		extAlert(errorTitle, '<%=PropertyProvider.get("prm.schedule.assigne.resource.error.message")%>' , Ext.MessageBox.ERROR);
                     		return;
                     	}
                     }
                }
            }
        }

        document.body.style.cursor = "wait";
        theForm.submit();
    }
}


// Method to submit resource assigned percentage 
// while submitting form by hitting enter key.
function submitForm() {
	var integerErrMsg = '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>';
    var percentTextbox;

    if (verifySelectionForField(theForm.resource, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        if (theForm.resource.checked) {
            // A single checkbox
            percentTextbox = eval("theForm.percent_"+theForm.resource.value);
            if (!verifyNonBlankField(percentTextbox.value)){
                percentTextbox.value = "<%=nf.formatNumber(100)%>";
            }else{
            	if(percentTextbox.value == 0){
               		extAlert(errorTitle, '<%=PropertyProvider.get("prm.schedule.assigne.resource.error.message")%>' , Ext.MessageBox.ERROR);
               		return false;
                } 
            }
        } else {
            for(var index=0; index< theForm.resource.length; index++) {
                if (theForm.resource[index].checked) {
                    percentTextbox = eval("theForm.percent_"+theForm.resource[index].value);
                    if (!verifyNonBlankField(percentTextbox.value)){
                   			percentTextbox.value = "<%=nf.formatNumber(100)%>";
                     }else{
                     	if(percentTextbox.value == 0){
                     		extAlert(errorTitle, '<%=PropertyProvider.get("prm.schedule.assigne.resource.error.message")%>' , Ext.MessageBox.ERROR);
                     		return false;
                     	}
                     }
                }
            }
        }
        document.body.style.cursor = "wait";
        theForm.submit();
    } else {
    	return false;
    }
}

function showResourceAllocation(personID) {
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID;

    openwin_large('resource_allocation', url);
}

<%-- Called from within IFRAME --%>
function setFixedDurationEffortDrivenSelected(isSelected) {
    document.getElementById("fixedDurationErrorID").style.display = (isSelected ? "block" : "none");
}
</script>

</head>

<body onLoad="setup();">
<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>

<form action='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/TaskList/AssignResourcesDialogProcessing"%>' method="post" onsubmit="return submitForm();">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="idList" value="<%=idList%>">
<table border="0" width="100%">
  <tr>
    <td class="tableHeader"><display:get name="prm.schedule.assignresourcesdialog.title"/></td>
  </tr>
  <%-- Error initially hidden, only unhidden if child IFRAME tells us --%>
  <% if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) {%>  
  <tr id="fixedDurationErrorID" class="errorMessage" style="display:none">
    <td>
        <display:get name="prm.schedule.assignresourcesdialog.fixeddurationeffortdrivenselected.message">
            <display:param value="<%=TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN.formatDisplay()%>" />
        </display:get>
    </td>
  </tr>
   <% } %>  
  <% if (SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) {%>
	<tr class="errorMessage">
		<td>
			<display:get name="prm.schedule.taskview.resources.cantassignmentresource.message"/>
		</td>
	</tr>
	<% } %>
<% if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) {%>  	
  <tr>
    <td class="tableContent">
        <label><input type="radio" name="replaceExisting" value="0" checked><display:get name="prm.schedule.assignresourcesdialog.addtoexisting"/></label>
    </td>
  </tr>
  <tr>
    <td class="tableContent">
        <label><input type="radio" name="replaceExisting" value="1"><display:get name="prm.schedule.assignresourcesdialog.replaceexisting"/></label>
    </td>
  </tr>
<% } %>  
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td align="left" colspan="4" class="channelHeader" nowrap><%=PropertyProvider.get("prm.schedule.assignresourcesdialog.resources")%></td>
            <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
        </tr>
        <tr class="tableHeader">
            <td></td>
            <td></td>
            <td><display:get name="prm.schedule.taskview.resources.assign.person.column"/></td>
            <td><display:get name="prm.schedule.taskview.resources.assign.assigned.column"/></td>
            <td align="center"><display:get name="prm.schedule.taskview.resources.assign.workingcalendar.column"/></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td colspan="4" class="headerSep"></td>
            <td></td>
        </tr>
<%
	 if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) { 
            for (Iterator it = assignmentRoster.iterator(); it.hasNext();) {
                AssignmentRoster.Person person = (AssignmentRoster.Person)it.next();
%>
        <tr class="tableContent">
            <td><input type="hidden" name="timezone_<%=person.getID()%>" value="<%=person.getTimeZone()==null?"":person.getTimeZone().getID()%>"/></td>
            <td align="center"><input name="resource" id="<%=person.getID()%>" value="<%=person.getID()%>" type="checkbox"></td>
            <td><label for="<%=person.getID()%>"><%=person.getDisplayName()%></label></td>
            <td><input name="percent_<%=person.getID()%>" type="text" size="4" maxlength="4"></td>
            <td align="center"><a href='javascript:showResourceAllocation(<%=person.getID()%>)'><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0"></a></td>
            <td></td>
        </tr>
        <% 
        } 
	 }
        %>
        </table>
    </td>
  </tr>
</table>

<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
				<% if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) {%>  
                <tb:button type="submit" id="submitButton"/>
				<% }  %>                
				<tb:button type="cancel" id="cancelButton"/>
			</tb:band>
</tb:toolbar>

</form>

<template:getSpaceJS />
</body>
</html>
