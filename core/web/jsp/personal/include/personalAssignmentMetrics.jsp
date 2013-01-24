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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="personalAssignmentMetrics" type="net.project.personal.metric.PersonalAssignmentMetricsGroup" scope="request" />

<form name="personalAssignmentMetricsForm">
<pnet-xml:transform stylesheet="/personal/include/xsl/personal-assignment-metrics.xsl" xml="<%=personalAssignmentMetrics.getXML()%>" />

<%--
<pre>
<%=personalAssignmentMetrics.getXML()%>
</pre>
--%>
</form>
