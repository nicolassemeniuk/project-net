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
    info="Task History View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.security.SessionManager,
			net.project.base.Module,
			net.project.resource.AssignmentManager,
            java.net.URLEncoder,
            net.project.security.Action,
            net.project.xml.XMLFormatter,
            net.project.gui.history.History"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<html><head>
<link rel="stylesheet" rev="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/global.css">
<link rel="stylesheet" rev="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/fonts.css">
<link rel="stylesheet" rev="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/colors.css">
<link rel="stylesheet" rev="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/schedule.css">
<link rel="stylesheet" rev="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/project.css">
</head>
<body>
<% 
	History history = (History) pageContext.getAttribute("historyTagHistoryObject", PageContext.SESSION_SCOPE);
	history.setTarget("_top");
	//history.
%>
<%=history.getPresentation()%>
</body>
</html>