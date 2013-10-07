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
    info="Provides a selection list of icons"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<html>
<head>
<title><display:get name="prm.schedule.tasklistdecorating.pleaseselectanicon.title"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW%>';
}

function imageSelected(id) {
    var image = document.getElementById(id);
    var imageSRC = image.src;

    if (imageSRC.indexOf('<%=SessionManager.getSiteURL()%>') == 0) {
        imageSRC = imageSRC.substring(<%=SessionManager.getSiteURL().length()%>);
    }


    window.opener.imageSelected('<%=request.getParameter("imageID")%>', imageSRC);
}
</script>

</head>

<body>

<div class="tableContent"><display:get name="prm.schedule.tasklistdecorating.pleaseselectanicon.title"/></div>
<center>
<table>
    <tr>
        <td><a href="javascript:imageSelected(0);"><img id="0" src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" border="1" height="19" width="18"></a></td>
        <td><a href="javascript:imageSelected(1);"><img id="1" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(2);"><img id="2" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/after_deadline.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(3);"><img id="3" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/critical_path.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(4);"><img id="4" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/dependency.gif" border="0"></a></td>
    </tr>
    <tr>
        <td><a href="javascript:imageSelected(5);"><img id="5" src="<%=SessionManager.getJSPRootURL()%>/images/flag_checkered_small.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(6);"><img id="6" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_green_down.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(7);"><img id="7" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_green_up.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(8);"><img id="8" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_red_down.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(9);"><img id="9" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_red_up.gif" border="0"></a></td>
    </tr>
    <tr>
        <td><a href="javascript:imageSelected(10);"><img id="10" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_white_down.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(11);"><img id="11" src="<%=SessionManager.getJSPRootURL()%>/images/arrow_white_up.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(12);"><img id="12" src="<%=SessionManager.getJSPRootURL()%>/images/group_person_small.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(13);"><img id="13" src="<%=SessionManager.getJSPRootURL()%>/images/blk_check.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(14);"><img id="14" src="<%=SessionManager.getJSPRootURL()%>/images/check_green.gif" border="0"></a></td>
    </tr>
    <tr>
        <td><a href="javascript:imageSelected(15);"><img id="15" src="<%=SessionManager.getJSPRootURL()%>/images/check_red.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(16);"><img id="16" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/externalTask.gif" border="0"></a></td>
        <td><a href="javascript:imageSelected(17);"><img id="17" src="<%=SessionManager.getJSPRootURL()%>/images/screwdriver.png" border="0"></a></td>
        <td><a href="javascript:imageSelected(18);"><img id="18" src="<%=SessionManager.getJSPRootURL()%>/images/financial.png" border="0"></a></td>        
    </tr>
</table>
</center>

<template:getSpaceJS />
</body>
</html>
