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
    info=""
    contentType="text/html; charset=UTF-8"
    language="java"
    errorPage="/errors.jsp"
    import="java.util.List,
            java.util.Iterator,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.crossspace.ExportFinder,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="objectsToShare" type="java.util.List" scope="request" />
<jsp:useBean id="filter" type="net.project.taglibs.input.InputTagFilter" scope="request" />
<jsp:useBean id="multiClassExportedObject" type="net.project.crossspace.mvc.handler.MultiClassExportedObject" scope="request"/>
<jsp:useBean id="isContainer" type="java.lang.Boolean" scope="request"/>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- The needed scripts was already imported in the parent JSP--%>
<script language="javascript">
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
function toggleShareBoxEnabled() {
    var enabled = (getSelectedValue(document.forms[0].permissionType) == 1);
    var shareTable = document.getElementById("shareTable");
    shareTable.disabled = !enabled;
    shareTable.setAttribute("disabled", !enabled);

    var childInputs = shareTable.getElementsByTagName("input");
    for (var i = 0; i < childInputs.length; i++) {
        childInputs[i].disabled = !enabled;
        childInputs[i].readonly = !enabled;
    }
}

function findSharingSpaces() {
    var enabled = (getSelectedValue(document.forms[0].permissionType) == 1);
    if(enabled)
        openwin_dialog('addspace', JSPRootURL+'/crossspace/FindSpaces.jsp?module=<%=Module.SCHEDULE%>', 300, 400, 1);
}

function findSharingUsers() {
    openwin_dialog('adduser', JSPRootURL+'/crossspace/FindUsers.jsp?module=<%=Module.SCHEDULE%>', 300, 400, 1);
}

var spacesAdded = 0;

function addSharingSpace(spaceid, spacename) {
    var spaceTable = document.getElementById("spaceTable");
	spacesAdded = spacesAdded + 1;
    //If the "No spaces.." message is there, remove it
    var nospacesrow = document.getElementById("nospaces");
    if (nospacesrow)
        spaceTable.removeChild(nospacesrow);

    //Check to make sure this space hasn't already been added
    if (document.getElementById("space"+spaceid))
        return;

    var spaceRow = document.createElement("tr");
    spaceRow.id = "space"+spaceid;

    var nameTD = document.createElement("td");
    nameTD.className = "tableContent";
    nameTD.appendChild(document.createTextNode(spacename));
    spaceRow.appendChild(nameTD);

    var actionTD = document.createElement("td");
    var aObj = document.createElement("a");
    aObj.href="javascript:removeSpace('"+spaceid+"')";
    var removeImg = document.createElement("img");
    removeImg.src = JSPRootURL +"/images/icons/toolbar-gen-remove_on.gif";
    removeImg.border = "0";
    aObj.appendChild(removeImg);
    actionTD.appendChild(aObj);

    //Add an hidden form input to let the processing page know to about this space
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "shareSpaceID";
    hiddenInput.value = spaceid;
    actionTD.appendChild(hiddenInput);
    spaceRow.appendChild(actionTD);
    spaceTable.appendChild(spaceRow);
}
function addSharingUser(userid, display) {
    var userTable = document.getElementById("userTable");

    //If the "No users have been added" message is there, remove it.
    var nousersrow = document.getElementById("nousers");
    if (nousersrow)
        userTable.removeChild(nousersrow);

    //If this user has been added, don't add them again, bail out
    if (document.getElementById("user"+userid))
        return;

    var userRow = document.createElement("tr");
    userRow.id = "user"+userid;

    var displayTD = document.createElement("td");
    displayTD.className = "tableContent";
    displayTD.appendChild(document.createTextNode(display));
    userRow.appendChild(displayTD);

    var actionTD = document.createElement("td");
    var aObj = document.createElement("a");
    aObj.href="javascript:removeUser('"+userid+"')";
    var removeImg = document.createElement("img");
    removeImg.src = JSPRootURL +"/images/icons/toolbar-gen-remove_on.gif";
    removeImg.border = "0";
    aObj.appendChild(removeImg);
    actionTD.appendChild(aObj);

    //Add an hidden form input to let the processing page know to about this user
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "shareUserID";
    hiddenInput.value = userid;
    actionTD.appendChild(hiddenInput);
    userRow.appendChild(actionTD);
    userTable.appendChild(userRow);
}
function removeSpace(spaceID) {
	spacesAdded = spacesAdded - 1;
    document.getElementById("spaceTable").removeChild(document.getElementById("space"+spaceID));
}
function removeUser(userID) {
    document.getElementById("userTable").removeChild(document.getElementById("user"+userID));
}

// Validate required allowable actions and permissions
function validate(){
	var errorMessage = '';
	if(!document.getElementById('allowableAction_1').checked && !document.getElementById('allowableAction_4').checked){
		errorMessage = '<%=PropertyProvider.get("prm.schedule.share.select.allowableaction.message")%>';
	}
	
	if(document.getElementById('permissionType_1').checked){
		if(!document.getElementById('permissionType_2').checked && !document.getElementById('permissionType_3').checked ){
			if(errorMessage == ''){
				errorMessage = '<%=PropertyProvider.get("prm.schedule.share.select.permission.message")%>';
			}
		}else{
			if(document.getElementById('permissionType_3').checked){
				if(spacesAdded == 0){
					errorMessage = '<%=PropertyProvider.get("prm.crossspace.createshare.nospaces.warning")%>';
				}
			}
		}
	} 
	return errorMessage;
}
</script>

</head>
<body>

<%-- The form declaration used to be located here, but now it has been moved up
a level to allow all the channels in this page have form controls in them. --%>
<table>
<tr>
    <td><input:radio name="permissionType" elementID="permissionType_0" value="0" onClick="toggleShareBoxEnabled();" filter="<%=filter%>"/></td>
    <td class="tableContent">
        <label for="permissionType_0"><display:get name="prm.crossspace.createshare.option.donotshareobject.label"/></label>
    </td>
</tr>
<tr>
    <td><input:radio name="permissionType" elementID="permissionType_1" value="1" onClick="toggleShareBoxEnabled();" filter="<%=filter%>"/></td>
    <td class="tableContent">
        <label for="permissionType_1"><display:get name="prm.crossspace.createshare.option.shareobjects.label"/></label>
    </td>
</tr>
<tr>
    <td></td>
    <td>
        <table style="border: thin solid #CCCCFF" id="shareTable" disabled="true" width="550">
        <% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
        <tr>
            <td><input:checkbox name="permissionType" elementID="permissionType_2" value="2" filter="<%=filter%>"/>
            <td class="tableContent">
                <label for="permissionType_2"><display:get name="prm.crossspace.createshare.option.sharewithall.label"/></label>
            </td>
        </tr>
         <% } %>
        <tr>
            <td><input:checkbox name="permissionType" elementID="permissionType_3" value="3" filter="<%=filter%>"/></td>
            <td class="tableContent">
                <label for="permissionType_3"><display:get name="prm.crossspace.createshare.option.sharewithworkspaces.label"/></label>
            </td>
        </tr>      
        <tr>
            <td></td>
            <td>
                <table border="0" style="margin-left: 15px" width="90%">
                    <tbody id="spaceTable">
                    <tr>
                        <td class="tableHeader"><display:get name="prm.crossspace.createshare.spaces.label"/></td>
                        <td width="1%"><a href="javascript:findSharingSpaces()"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-create_on.gif" border="0"></a></td>
                    </tr>
                    <tr>
                        <td class="headerSep" colspan="2"></td>
                    </tr>
                    <% if (((List)multiClassExportedObject.permittedSpaces.value).isEmpty()) { %>
                    <tr id="nospaces">
                        <td colspan="2" class="tableContent"><display:get name="prm.crossspace.createshare.nospaces.warning"/></td>
                    </tr>
                    <% } else { %>
                    <% for (Iterator it = ((List)multiClassExportedObject.permittedSpaces.value).iterator(); it.hasNext();) {
                        ExportFinder.PermittedObject po = (ExportFinder.PermittedObject) it.next(); %>
                    <tr class="tableContent" id="space<%=po.getObjectID()%>">
                        <td><%=po.getObjectName()%><input type="hidden" name="shareSpaceID" value="<%=po.getObjectID()%>"></td>
                        <td><a href="javascript:removeSpace('<%=po.getObjectID()%>')"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-remove_on.gif" border="0"/></a></td>
                    </tr>
					<script language="javascript">
						spacesAdded = spacesAdded + 1;
					</script>
                    <% } %>
                    <% } %>
                    </tbody>
                </table>
            </td>
        </tr>
        </table>
    </td>
</tr>

<% if (isContainer.booleanValue()) { %>
<tr>
    <td></td>
    <td class="tableContent">
        <input:checkbox name="propagateToChildren" value="true" filter="<%=filter%>"/><display:get name="prm.crossspace.createshare.propagate.message"/>
    </td>
</tr>
<% } %>
</table>
<template:getSpaceJS />
</body>
</html>
<script language="JavaScript">toggleShareBoxEnabled();</script>
