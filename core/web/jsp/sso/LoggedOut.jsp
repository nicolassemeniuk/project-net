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

<html>
<head>
<%@ include file="/base/taglibInclude.jsp" %>

<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />

</head>
<body class="main">
<form>
    <input type="hidden" name="theAction">
</form>
<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
<h2><display:get name="prm.global.login.sso.LoggedOut" /></h2>
	</td>
		<td>&nbsp;</td>
	</tr>
</table>
</div>
</body>
</html>                                                                                      