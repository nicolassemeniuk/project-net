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
|   $Revision: 19925 $
|       $Date: 2009-09-08 12:56:09 -0300 (mar, 08 sep 2009) $
|     $Author: nilesh $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            java.util.Collections,
            java.util.Collection,
            net.project.util.Validator,
            net.project.space.SpaceSearch,
            net.project.base.property.PropertyProvider,
            java.util.Iterator"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<%
    Collection spacesToList = Collections.EMPTY_LIST;

    String keyword = request.getParameter("keyword");
    if (!Validator.isBlankOrNull(keyword)) {
        SpaceSearch search = new SpaceSearch();
        spacesToList = search.keywordSearch(keyword);
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
            var display;
            if(document.all) {
            	// This is for IE browsers
            	display = document.getElementById(id+"name").innerText;
            } else{
            	// This is for Firefox browsers
            	display = document.getElementById(id+"name").textContent;
            }
            window.opener.addSharingSpace(id, display);
        } else {
            for (var i = 0; i < theForm.selected.length; i++) {
                if (theForm.selected[i].checked == true) {
                    var id = theForm.selected[i].value;
                    var display;
		            if(document.all) {
		            	// This is for IE browsers
		            	display = document.getElementById(id+"name").innerText;
		            } else{
		            	// This is for Firefox browsers
		            	display = document.getElementById(id+"name").textContent;
		            }
                    window.opener.addSharingSpace(id, display);
                }
            }
        }
    }
}

function finish() {
    add();
    window.opener.document.getElementById('errorsDiv').style.display = "none";
    window.close();
}
</script>

</head>

<body onLoad="setup();">

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/crossspace/FindSpaces.jsp">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">

<span class="tableContent"><display:get name="prm.crossshare.finsspace.nameofspace.label" /></span><br>
<input type="text" name="keyword"<%=(!Validator.isBlankOrNull(keyword) ? "value='"+keyword+"'" : "")%>>
<input type="submit" value="Search"><br>
<br>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
    <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td nowrap="true" class="channelHeader" colspan="2"><display:get name="prm.crossshare.finsspace.searchmatches.label" /></td>
    <td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><display:get name="prm.crossshare.finsspace.add.label" /></td>
    <td class="tableHeader"><display:get name="prm.crossshare.finsspace.spacename.label" /></td>
    <td></td>
</tr>
<tr>
    <td></td>
    <td colspan="2" class="headerSep">
    <td></td>
</tr>
<%
    for (Iterator it = spacesToList.iterator(); it.hasNext();) {
        SpaceSearch.SpaceMatch spaceMatch = (SpaceSearch.SpaceMatch) it.next();
        String name = spaceMatch.getName();
        if(name.startsWith(PropertyProvider.TOKEN_PREFIX)) {
        	name = PropertyProvider.get(name);
        }
%>
<tr>
    <td></td>
    <td class="tableContent"><input type="checkbox" name="selected" id="<%=spaceMatch.getID()%>" value="<%=spaceMatch.getID()%>"></td>
    <td class="tableContent"><label id="<%=spaceMatch.getID()%>name" for="<%=spaceMatch.getID()%>"><%=net.project.util.HTMLUtils.escape(name)%></label></td>
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
