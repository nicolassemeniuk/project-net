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
    import="net.project.form.report.formitemtimeseries.SQLStatementType,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<html>
<head>
<title><display:get name="prm.form.report.formitemtimeseries.parameters.pagetitle.name"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
</script>

</head>

<body>

<span class="tableHeader">Count Type</span><br>
<input type="radio" name="seriesType" value="<%=SQLStatementType.DAILY_TIME_SERIES.getID()%>" checked><%=SQLStatementType.DAILY_TIME_SERIES.toString()%><br>
<input type="radio" name="seriesType" value="<%=SQLStatementType.WEEKLY_TIME_SERIES.getID()%>"><%=SQLStatementType.WEEKLY_TIME_SERIES.toString()%><br>
<input type="radio" name="seriesType" value="<%=SQLStatementType.MONTHLY_TIME_SERIES.getID()%>"><%=SQLStatementType.MONTHLY_TIME_SERIES.toString()%><br>
<br>
<%--
We used to be able to choose between chart types, but this was lost in the move
to JFreeChart.  It is probably possible to do, but not possible in the time
available.

<span class="tableHeader">Graph Type</span><br>
<input type="radio" name="renderedChartType" value="<%=RenderedChartType.STACKED_BAR_CHART.getID()%>" checked><%=RenderedChartType.STACKED_BAR_CHART.toString()%><br>
<input type="radio" name="renderedChartType" value="<%=RenderedChartType.LINE_GRAPH.getID()%>"><%=RenderedChartType.LINE_GRAPH.toString()%><br>
--%>
<template:getSpaceJS />
</body>
</html>
