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
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.gui.html.HTMLOptionList,
            net.project.gui.html.HTMLBackgroundColor,
            net.project.resource.PersonalPropertyMap,
            net.project.util.Validator,
            net.project.base.property.PropertyProvider,
            java.net.URLEncoder,
            net.project.resource.PersonPropertyGlobalScope"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" type="net.project.security.User" scope="session" />
<%
    // Load the person properties from global property scope (irrespective of space)
    PersonalPropertyMap properties = new PersonalPropertyMap(new PersonPropertyGlobalScope(user), "prm.schedule.tasklist");
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:import type="css" src="/styles/colors.css" />
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var imageDialog;
var theForm;
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function setup() {
    theForm = self.document.forms[0];
}

function helpMe() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=schedule_decoration";
 	openwin_help(helplocation);
}

function reset() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>';
}

function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW%>';
}

function submit() {
    theForm.submit();
}

function selectImage(id) {
    imageDialog = openwin_dialog('select_image', '<%=SessionManager.getJSPRootURL()+"/schedule/properties/DecoratorIcons.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY+"&imageID="%>'+id);
}

function imageSelected(imageID, imageURL) {
    var imageToChange = document.getElementById(imageID+"Img");
    var formItem = eval("theForm." + imageID);

    if (imageURL != '<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif') {
        formItem.value = imageURL;
    } else {
        formItem.value = "";
    }
    imageToChange.src = imageURL;

    valueChanged(imageID.substring(0, imageID.length-5));

    imageDialog.close();
}

function valueChanged(propertyName) {
    //Find the corresponding checkbox, select box, and hidden image tag
    var selectBox = eval("theForm." + propertyName + "Color");
    var checkBox = eval("theForm." + propertyName);
    var imageHidden = eval("theForm." + propertyName +"Image");

    checkBox.checked = !(selectBox.value == "<%=HTMLBackgroundColor.NO_COLOR.getID()%>") ||
        !(imageHidden.value == "");
}

</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken='prm.schedule.tasklistdecorating.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp"%>'
					queryString='<%="module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>
<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' selected="true"/>
    <display:if name="@prm.crossspace.isenabled">
        <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>

<form action="<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/ScheduleProperties/TaskListDecoratingProcessing"%>" method="post">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">

<%
    String isDateConstrainedImage = "";
    if (!Validator.isBlankOrNull(properties.getProperty("isDateConstrainedImage"))) {
        isDateConstrainedImage = properties.getProperty("isDateConstrainedImage");
    } else if (!properties.propertyExists("isDateConstrainedImage")) {
        isDateConstrainedImage = SessionManager.getJSPRootURL()+"/images/schedule/constraint.gif";
    }

    String hasAssignmentImage = "";
    if (!Validator.isBlankOrNull(properties.getProperty("hasAssignmentImage"))) {
        hasAssignmentImage = properties.getProperty("hasAssignmentImage");
    } else if (!properties.propertyExists("hasAssignmentImage")) {
        hasAssignmentImage = SessionManager.getJSPRootURL()+"/images/group_person_small.gif";
    }
    
    String hasMaterialAssignmentImage = "";
    if (!Validator.isBlankOrNull(properties.getProperty("hasMaterialAssignmentImage"))) {
    	hasMaterialAssignmentImage = properties.getProperty("hasMaterialAssignmentImage");
    } else if (!properties.propertyExists("hasMaterialAssignmentImage")) {
    	hasMaterialAssignmentImage = SessionManager.getJSPRootURL()+"/images/screwdriver.png";
    }    

    String taskDependenciesExistImage = "";
    if (!Validator.isBlankOrNull(properties.getProperty("taskDependenciesExistImage"))) {
        taskDependenciesExistImage = properties.getProperty("taskDependenciesExistImage");
    } else if (!properties.propertyExists("taskDependenciesExistImage")) {
        taskDependenciesExistImage = SessionManager.getJSPRootURL()+"/images/schedule/dependency.gif";
    }

    String isExternalTaskImage = "";
    if (!Validator.isBlankOrNull(properties.getProperty("isExternalTaskImage"))) {
        isExternalTaskImage = properties.getProperty("isExternalTaskImage");
    } else if (!properties.propertyExists("isExternalTaskImage")) {
        isExternalTaskImage = SessionManager.getJSPRootURL()+"/images/schedule/externalTask.gif";
    }
%>


<input type="hidden" name="lateTasksImage"<%=(!Validator.isBlankOrNull(properties.getProperty("lateTasksImage")) ? " value=\""+properties.getProperty("lateTasksImage")+"\"" : "")%>>
<!--<input type="hidden" name="overallocatedResourcesImage"<%=(!Validator.isBlankOrNull(properties.getProperty("overallocatedResourcesImage")) ? " value=\""+properties.getProperty("overallocatedResourcesImage")+"\"" : "")%>>-->
<input type="hidden" name="completedTasksImage"<%=(!Validator.isBlankOrNull(properties.getProperty("completedTasksImage")) ? " value=\""+properties.getProperty("completedTasksImage")+"\"" : "")%>>
<input type="hidden" name="tasksComingDueImage"<%=(!Validator.isBlankOrNull(properties.getProperty("tasksComingDueImage")) ? " value=\""+properties.getProperty("tasksComingDueImage")+"\"" : "")%>>
<input type="hidden" name="unassignedTasksImage"<%=(!Validator.isBlankOrNull(properties.getProperty("unassignedTasksImage")) ? " value=\""+properties.getProperty("unassignedTasksImage")+"\"" : "")%>>

<input type="hidden" name="taskDependenciesExistImage"<%=(!Validator.isBlankOrNull(taskDependenciesExistImage) ? " value=\""+taskDependenciesExistImage+"\"" : "")%>>
<input type="hidden" name="isDateConstrainedImage"<%=(!Validator.isBlankOrNull(isDateConstrainedImage) ? " value=\""+isDateConstrainedImage+"\"" : "")%>>
<input type="hidden" name="isCriticalPathImage"<%=(!Validator.isBlankOrNull(properties.getProperty("isCriticalPathImage")) ? " value=\""+properties.getProperty("isCriticalPathImage")+"\"" : "")%>>
<input type="hidden" name="hasAssignmentImage"<%=(!Validator.isBlankOrNull(hasAssignmentImage) ? " value=\""+hasAssignmentImage+"\"" : "")%>>
<input type="hidden" name="hasMaterialAssignmentImage"<%=(!Validator.isBlankOrNull(hasMaterialAssignmentImage) ? " value=\""+hasMaterialAssignmentImage+"\"" : "")%>>
<input type="hidden" name="afterDeadlineImage"<%=(!Validator.isBlankOrNull(properties.getProperty("afterDeadlineImage")) ? " value=\""+properties.getProperty("afterDeadlineImage")+"\"" : "")%>>
<input type="hidden" name="isExternalTaskImage"<%=(!Validator.isBlankOrNull(isExternalTaskImage) ? " value=\""+isExternalTaskImage+"\"" : "")%>>


<table width="97%" border="0">
<tr class="tableHeader">
    <td align="center"><display:get name="prm.schedule.tasklistdecorating.columnheader.enabled"/></td>
    <td><display:get name="prm.schedule.tasklistdecorating.columnheader.status"/></td>
    <td><display:get name="prm.schedule.tasklistdecorating.columnheader.image"/></td>
    <td><display:get name="prm.schedule.tasklistdecorating.columnheader.backgroundcolor"/></td>
</tr>
<tr><td class="headerSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="lateTasks" value="true"<%=(properties.propertyExists("lateTasksColor") || properties.propertyExists("lateTasksImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.latetasks.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('lateTasksImage')">&nbsp;<img id="lateTasksImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("lateTasksImage")) ? properties.getProperty("lateTasksImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="lateTasksColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("lateTasksColor")))%>
        </select>
    </td>
</tr>
<!--
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="overallocatedResources" value="true"<%=(properties.propertyExists("overallocatedResourcesColor") || properties.propertyExists("overallocatedResourcesImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.overallocatedresources.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="Select Image" onClick="selectImage('overallocatedResourcesImage')">&nbsp;<img id="overallocatedResourcesImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("overallocatedResourcesImage")) ? properties.getProperty("overallocatedResourcesImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="overallocatedResourcesColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("overallocatedResourcesColor")))%>
        </select>
    </td>
</tr>
-->
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="completedTasks" value="true"<%=(properties.propertyExists("completedTasksColor") || properties.propertyExists("completedTasksImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.completedtasks.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('completedTasksImage')">&nbsp;<img id="completedTasksImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("completedTasksImage")) ? properties.getProperty("completedTasksImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="completedTasksColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("completedTasksColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="tasksComingDue" value="true"<%=(properties.propertyExists("tasksComingDueColor") || properties.propertyExists("tasksComingDueImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.taskscomingdue.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('tasksComingDueImage')">&nbsp;<img id="tasksComingDueImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("tasksComingDueImage")) ? properties.getProperty("tasksComingDueImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="tasksComingDueColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("tasksComingDueColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="unassignedTasks" value="true"<%=(properties.propertyExists("unassignedTasksColor") || properties.propertyExists("unassignedTasksImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.unassignedtasks.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('unassignedTasksImage')">&nbsp;<img id="unassignedTasksImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("unassignedTasksImage")) ? properties.getProperty("unassignedTasksImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="unassignedTasksColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("unassignedTasksColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="taskDependenciesExist" value="true"<%=(properties.propertyExists("taskDependenciesExistColor") || properties.propertyExists("taskDependenciesExistImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.taskdependencies.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('taskDependenciesExistImage')">&nbsp;<img id="taskDependenciesExistImageImg" src="<%=(!Validator.isBlankOrNull(taskDependenciesExistImage) ? taskDependenciesExistImage : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="taskDependenciesExistColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("taskDependenciesExistColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="isDateConstrained" value="true"<%=(properties.propertyExists("isDateConstrainedColor") || properties.propertyExists("isDateConstrainedImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.dateconstrained.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('isDateConstrainedImage')">&nbsp;<img id="isDateConstrainedImageImg" src="<%=(Validator.isBlankOrNull(isDateConstrainedImage) ? SessionManager.getJSPRootURL()+"/images/spacers/trans.gif" : isDateConstrainedImage)%>" border="0">
    </td>
    <td>
        <select name="isDateConstrainedColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("isDateConstrainedColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="isCriticalPath" value="true"<%=(properties.propertyExists("isCriticalPathColor") || properties.propertyExists("isCriticalPathImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.criticalpath.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('isCriticalPathImage')">&nbsp;<img id="isCriticalPathImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("isCriticalPathImage")) ? properties.getProperty("isCriticalPathImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="isCriticalPathColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("isCriticalPathColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="hasAssignment" value="true"<%=(properties.propertyExists("hasAssignmentColor") || properties.propertyExists("hasAssignmentImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.hasAssigment.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('hasAssignmentImage')">&nbsp;<img id="hasAssignmentImageImg" src="<%=(!Validator.isBlankOrNull(hasAssignmentImage) ? hasAssignmentImage : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="hasAssignmentColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("hasAssignmentColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="hasMaterialAssignment" value="true"<%=(properties.propertyExists("hasMaterialAssignmentColor") || properties.propertyExists("hasMaterialAssignmentImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.hasMaterialAssigment.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('hasMaterialAssignmentImage')">&nbsp;<img id="hasMaterialAssignmentImageImg" src="<%=(!Validator.isBlankOrNull(hasMaterialAssignmentImage) ? hasMaterialAssignmentImage : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="hasMaterialAssignmentColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("hasMaterialAssignmentColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="afterDeadline" value="true"<%=(properties.propertyExists("afterDeadlineColor") || properties.propertyExists("afterDeadlineImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.pastdeadline.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('afterDeadlineImage')">&nbsp;<img id="afterDeadlineImageImg" src="<%=(!Validator.isBlankOrNull(properties.getProperty("afterDeadlineImage")) ? properties.getProperty("afterDeadlineImage") : SessionManager.getJSPRootURL()+"/images/spacers/trans.gif")%>" border="0">
    </td>
    <td>
        <select name="afterDeadlineColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("afterDeadlineColor")))%>
        </select>
    </td>
</tr>
<tr><td class="rowSep" colspan="4"></td></tr>
<tr>
    <td align="center"><input type="checkbox" name="isExternalTask" value="true"<%=(properties.propertyExists("isExternalTaskColor") || properties.propertyExists("isExternalTaskImage") ? " checked" : "")%>></td>
    <td class="tableContent"><display:get name="prm.schedule.tasklistdecorating.isexternaltask.label"/></td>
    <td nowrap valign="middle">
        <input type="button" value="<%=PropertyProvider.get("prm.schedule.tasklistdecorating.selectimage.button")%>" onClick="selectImage('isExternalTaskImage')">&nbsp;<img id="isExternalTaskImageImg" src="<%=(!Validator.isBlankOrNull(isExternalTaskImage) ? isExternalTaskImage : properties.getProperty("isExternalTaskImage"))%>" border="0">
    </td>
    <td>
        <select name="isExternalTaskColor" onChange="valueChanged(this.name.substring(0, this.name.length-5));">
            <%=HTMLOptionList.makeHtmlOptionList(HTMLBackgroundColor.getAllColors(), HTMLBackgroundColor.getByClass(properties.getProperty("afterDeadlineColor")))%>
        </select>
    </td>
</tr>
</table>
</form>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit"/>
        <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
