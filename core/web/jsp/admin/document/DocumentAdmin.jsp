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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 19224 $
|       $Date: 2009-05-14 16:39:59 -0300 (jue, 14 may 2009) $
|     $Author: vmalykhin $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            java.util.Iterator,
            net.project.admin.DocumentRepositoryRoot,
            net.project.util.Conversion,
            net.project.base.property.PropertyProvider,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="documentRepositoryList" type="java.util.List" scope="request" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
var newRootID = 0;

function create() {
    newRootID++;

    var tbody = document.getElementById("pathTableBody");
    var tr = document.createElement("tr");
    tr.id = "new_row_"+newRootID;

    var radioTD = document.createElement("td");
    if (navigator.appVersion.toLowerCase().indexOf("msie") > 0) {
        radioTD.innerHTML = '<input type="hidden" name="docRootID" value="new_' +
            newRootID+'"><input type="radio" name="selected" value="new_'+
            newRootID+'">';
    } else {
        var hiddenDocRootID = document.createElement("input");
        hiddenDocRootID.type = "hidden";
        hiddenDocRootID.name = "docRootID";
        hiddenDocRootID.value = "new_"+newRootID;
        radioTD.appendChild(hiddenDocRootID);

        var radioInput = document.createElement("input");
        radioInput.type = "radio";
        radioInput.name = "docRootID";
        radioInput.value = "new_"+newRootID;
        radioTD.appendChild(radioInput);
    }
    tr.appendChild(radioTD);

    var pathTD = document.createElement("td");
    var pathTB = document.createElement("input");
    pathTB.type = "text";
    pathTB.name = "docPath_new_"+newRootID;
    pathTB.size = 60;
    pathTD.appendChild(pathTB);
    tr.appendChild(pathTD);

    var pathExistTD = document.createElement("td");
    pathExistTD.className = "tableContent";
    pathExistTD.appendChild(document.createTextNode("?"));
    tr.appendChild(pathExistTD);

    var pathWriteableTD = document.createElement("td");
    pathWriteableTD.className = "tableContent";
    pathWriteableTD.appendChild(document.createTextNode("?"));
    tr.appendChild(pathWriteableTD);

    var activeTD = document.createElement("td");
    var activeCB = document.createElement("input");
    activeCB.type = "checkbox";
    activeCB.name = "isActive_new_"+newRootID;
    activeCB.value = "true";
    activeCB.defaultChecked = true;
    activeTD.appendChild(activeCB);
    tr.appendChild(activeTD);

    tbody.appendChild(tr);

    //Now create the row separator
    rowSepTR = document.createElement("tr");
    rowSepTR.id = "new_sep_"+newRootID;

    rowSepTD = document.createElement("td");
    rowSepTD.className = "rowSep";
    rowSepTD.colSpan = "5";
    rowSepTR.appendChild(rowSepTD);

    tbody.appendChild(rowSepTR);
}

function remove() {
    var theForm = self.document.forms[0];

    if (verifySelection(theForm, 'single', '', '', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        var idval = theForm.selected.value;
        if (idval.indexOf("new_") == 0) {
           //Strip off the id we are deleting
           var idonly = idval.substring(4);

           //This is a new row, delete it.
           var tbody = document.getElementById("pathTableBody");

           tbody.removeChild(document.getElementById("new_row_"+idonly));
           tbody.removeChild(document.getElementById("new_sep_"+idonly));
        } else {
           var isActiveCB = document.getElementByName("isActive_"+theForm.selected.value);
           isActiveCB.value = false;
           theForm.submit();
       }
    }
}

function update() {
    var theForm = document.forms[0];
    theForm.theAction.value = "/DocumentRootUpdate";
    theForm.submit();
}

function cancel() {
    self.location = "<%=SessionManager.getJSPRootURL()%>/admin/Main.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>";
}
</script>

</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle"  groupTitle="prm.application.nav.docvault">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display=""
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
        <tb:button type="create"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/AdminController">
<input type="hidden" name="theAction" value="/DocumentRootUpdate">

<table border="0" cellpadding="0" cellspacing="0" width="97%">
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td nowrap="true" class="channelHeader" colspan="4">Document Repository Roots</td>
    <td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr>
    <td></td>
    <td><%
    //Avinash : Show Path Error if any
 if(request.getAttribute("message")!=null){String message = (String)request.getAttribute("message");%>
 		<span class="errorMessage"><display:get name="<%= message %>" /></span><%}%>
        <table border="0">
            <tbody id="pathTableBody">
            <tr>
                <td width="1%">&nbsp</td>
                <td class="tableHeader"><%=PropertyProvider.get("prm.project.document.path.to.repository.label") %></td>
                <td class="tableHeader"><%=PropertyProvider.get("prm.project.document.path.exists.label") %></td>
                <td class="tableHeader"><%=PropertyProvider.get("prm.project.document.path.writeable.label") %></td>
                <td class="tableHeader"><%=PropertyProvider.get("prm.project.document.is.active.label") %></td>
            </tr>
            <tr>
                <td class="headerSep" colspan="5"></td>
            </tr>
            <%
                for (Iterator it = documentRepositoryList.iterator(); it.hasNext();) {
                    DocumentRepositoryRoot docRoot = (DocumentRepositoryRoot)it.next();
            %>
            <tr>
                <td>
                    <input type="hidden" name="docRootID" value="<%=docRoot.getID()%>">
                    <input type="radio" name="selected" value="<%=docRoot.getID()%>">
                </td>
                <td><input type="text" name="docPath_<%=docRoot.getID()%>" value="<%=docRoot.getPath()%>" size="60"></td>
                <td class="tableContent"><%=Conversion.booleanToString(docRoot.pathExists())%></td>
                <td class="tableContent"><%=Conversion.booleanToString(docRoot.pathWriteable())%></td>
                <td><input type="checkbox" name="isActive_<%=docRoot.getID()%>" value="true"<%=docRoot.isActive() ? " checked" : ""%>></td>
            </tr>
            <tr>
                <td class="rowSep" colspan="5"></td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </td>
    <td></td>
</tr>
</table>
</form>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="update"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
