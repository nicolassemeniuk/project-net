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
    info="Business Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, 
				net.project.base.Module,
				net.project.base.property.PropertyProvider,
				net.project.business.BusinessSpaceBean, 
				net.project.business.BusinessSpace,
				net.project.space.SpaceManager,
				net.project.document.DocumentManagerBean, 
				net.project.channel.Channel,
				net.project.channel.ChannelManager,
		        net.project.space.Space,
		        net.project.base.PnetException" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%-- Import CSS --%>
<template:getSpaceCSS />


</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

	<iframe frameborder="0" width="100%" height="100%" scrolling="auto" src="<%=SessionManager.getJSPRootURL() + "/schedule/gantt/Gantt.jsp?module=" + Module.SCHEDULE%>">
	</iframe>

</div>

<%@include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
