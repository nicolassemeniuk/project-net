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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document Main Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.User"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docProvider" class="net.project.document.DocumentProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%docProvider.loadDocRepositoryCollection();
 %>



<html>
<head>
<title><display:get name="prm.document.documentprovider.title" /></title>

</head>


<body>
<PRE>
	<%= docProvider.getPresentation()
	 %>
 
 </PRE>
</body>
</html>

