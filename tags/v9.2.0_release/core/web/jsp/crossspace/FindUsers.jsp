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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="java.util.List,
            java.util.ArrayList,
            net.project.resource.PersonSearch,
            java.util.Collections,
            net.project.util.Validator,
            java.util.Iterator,
            net.project.security.SessionManager,
            net.project.base.Module,
            java.util.Collection,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<%
    Collection usersToList = Collections.EMPTY_LIST;

    String keyword = request.getParameter("keyword");
    if (!Validator.isBlankOrNull(keyword)) {
        PersonSearch ps = new PersonSearch();
        usersToList = ps.keywordSearch(keyword);
    }
%>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
    theForm.keyword.focus();
    theForm.keyword.select();
}

function add() {
    if (theForm.selected) {
        if (theForm.selected.value) {
            var id = theForm.selected.value;
            var display = document.getElementById(id+"name").innerHTML;
            window.opener.addSharingUser(id, display);
        } else {
            for (var i = 0; i < theForm.selected.length; i++) {
                if (theForm.selected[i].checked == true) {
                    var id = theForm.selected[i].value;
                    var display = document.getElementById(id+"name").innerHTML;
                    window.opener.addSharingUser(id, display);
                }
            }
        }
    }
}

function finish() {
    add();
    window.close();
}
</script>

</head>

<body onLoad="setup();">

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/crossspace/FindUsers.jsp">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">

<span class="tableContent"><display:get name="prm.crossspace.findusers.instructions"/><br></span>
<input type="textbox" name="keyword"<%=(!Validator.isBlankOrNull(keyword) ? " value='"+keyword+"'" : "")%>>
<input type="submit" value='<%=PropertyProvider.get("prm.crossspace.findusers.searchbutton.label")%>'><br>
<br>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
    <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td nowrap="true" class="channelHeader" colspan="2"><display:get name="prm.crossspace.findusers.searchmatches.label"/></td>
    <td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><display:get name="prm.crossspace.findusers.columns.add"/></td>
    <td class="tableHeader"><display:get name="prm.crossspace.findusers.columns.user"/></td>
    <td></td>
</tr>
<tr>
    <td></td>
    <td colspan="2" class="headerSep">
    <td></td>
</tr>

<%
    for (Iterator it = usersToList.iterator(); it.hasNext();) {
        PersonSearch.SharingPerson person = (PersonSearch.SharingPerson) it.next();
        String display = PropertyProvider.get("prm.crossspace.findusers.user.displayformat",
            person.getDisplayName(), person.getEmail());
%>
<tr>
    <td></td>
    <td><input type="checkbox" name="selected" value="<%=person.getPersonID()%>" id="person<%=person.getPersonID()%>"></td>
    <td class="tableContent"><label id="<%=person.getPersonID()%>name" for="person<%=person.getPersonID()%>"><%=display%></label></td>
    <td></td>
</tr>
<tr>
    <td></td>
    <td class="rowSep" colspan="2"></td>
    <td></td>
</tr>
<%
    }
%>
</table>



<tb:toolbar style="action" showLabels="true" width="97%">
    <tb:band name="action">
            <tb:button type="add"/>
            <tb:button type="finish"/>
    </tb:band>
</tb:toolbar>

</form>

<template:getSpaceJS />
</body>
</html>
