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
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="java.util.Date,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.material.MaterialAssignmentListBean,
            net.project.util.DateRange,
            java.util.Date,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="materialAssignmentList" class="net.project.material.MaterialAssignmentListBean" scope="page" />
<jsp:useBean id="endMonth" class="java.util.Date" scope="request"/>
<jsp:useBean id="startMonth" class="java.util.Date" scope="request"/>

<security:verifyAccess action="view" module="<%=Module.MATERIAL%>"/>

<%    
	materialAssignmentList.load(request.getParameter("materialID"), startMonth, endMonth);
%>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
</script>

</head>
<body>
<pnet-xml:transform name="materialAssignmentList" scope="page" stylesheet="/material/xsl/material-allocation-list.xsl">
</pnet-xml:transform>
<template:getSpaceJS />
</body>
</html>
