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
<%@ page import="net.project.schedule.MaterialAssignmentHelper"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="net.project.base.property.PropertyProvider"%>
<%@ page import="net.project.util.NumberFormat"%>
<%@ page import="net.project.space.SpaceTypes"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<jsp:useBean id="idList" class="java.lang.String" scope="request"/>
<jsp:useBean id="materialAssignmentsHelper" class="net.project.schedule.MaterialAssignmentsHelper" scope="request" />
<jsp:useBean id="materialBusinessAssignmentsHelper" class="net.project.schedule.MaterialAssignmentsHelper" scope="request" />

<html>
<head>
<title><display:get name="prm.schedule.assignmaterialsdialog.title"/></title>

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
}

function cancel() {
    window.close();
}

function submit() {
    if(verifySelectionForField(theForm.resource, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))
    {
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

function showMaterialAllocation(materialID) {
    var url = '<%=SessionManager.getJSPRootURL()+"/material/MaterialResourceAllocations.jsp?module=260&materialID="%>'+
        materialID;

    openwin_large('material_resource_allocation', url);
}
</script>

</head>

<body onLoad="setup();">
<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>

<form action='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/TaskList/AssignMaterialsDialogProcessing"%>' method="post" onsubmit="return submitForm();">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="idList" value="<%=idList%>">
<table border="0" width="100%">
  <tr>
    <td class="tableHeader"><display:get name="prm.schedule.assignmaterialsdialog.title"/></td>
  </tr>
<% if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) {%>  	
  <tr>
    <td class="tableContent">
        <label><input type="radio" name="replaceExisting" value="0" checked><display:get name="prm.schedule.assignmaterialsdialog.addtoexisting"/></label>
    </td>
  </tr>
  <tr>
    <td class="tableContent">
        <label><input type="radio" name="replaceExisting" value="1"><display:get name="prm.schedule.assignmaterialsdialog.replaceexisting"/></label>
    </td>
  </tr>
<% } %>  
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
            <td align="left" colspan="3" class="channelHeader" nowrap><%=PropertyProvider.get("prm.schedule.assignmaterialsdialog.materials")%></td>
            <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
        </tr>
        <tr class="tableHeader">
            <td></td>        
            <td></td>
            <td><display:get name="prm.schedule.taskview.material.assign.material.column"/></td>
            <td align="center"><display:get name="prm.schedule.taskview.material.assign.workingcalendar.column"/></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td colspan="4" class="headerSep"></td>
            <td></td>
        </tr>
<%
	 if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) { 
		for (MaterialAssignmentHelper assignment : materialAssignmentsHelper.getMaterialsAssigned()) {
			String materialID = assignment.getMaterial().getMaterialId();
%>
        <tr class="tableContent">
            <td></td>        
            <td align="center"><input name="resource" id="<%=materialID%>" value="<%=materialID%>" type="checkbox" <%=assignment.isAssignedMaterialChecked()%> <%=assignment.isAssignedMaterialEnabled()%>  ></td>
            <td><label for="<%=materialID%>"><%=assignment.getDisplayName()%></label></td>
            <td align="center"><a href='javascript:showMaterialAllocation(<%=materialID%>)'><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0"></a></td>
            <td></td>
        </tr>
        <% 
        } 
	 }
        %>

<%
	 if (!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) { 
		for (MaterialAssignmentHelper assignment : materialBusinessAssignmentsHelper.getMaterialsAssigned()) {
			String materialID = assignment.getMaterial().getMaterialId();
%>
        <tr class="tableContent">
            <td></td>        
            <td align="center"><input name="resource" id="<%=materialID%>" value="<%=materialID%>" type="checkbox" <%=assignment.isAssignedMaterialChecked()%> <%=assignment.isAssignedMaterialEnabled()%>  ></td>
            <td><label for="<%=materialID%>"><%=assignment.getDisplayName()%></label></td>
            <td align="center"><a href='javascript:showMaterialAllocation(<%=materialID%>)'><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0"></a></td>
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
