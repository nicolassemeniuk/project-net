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
--%><%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.report.IReport,
            net.project.xml.XMLFormatter,
            net.project.security.SessionManager,
            net.project.util.HttpUtils,
            net.project.base.property.PropertyProvider,
            net.project.report.ReportType"
%><jsp:useBean id="report" type="net.project.report.IReport" scope="session" /><%
    String reportParameters = HttpUtils.getRedirectParameterString(request);
    ReportType reportType = ReportType.getForID(request.getParameter("reportType"));
    XMLFormatter xmlFormatter = new XMLFormatter();
    xmlFormatter.setXML(report.getXML());
    xmlFormatter.setStylesheet(reportType.getXSLPath());
    
    response.setHeader("Content-disposition", "inline; filename=" + report.getReportName()+".xls");
    //Prepare the response object to return data
    response.setContentType("application/vnd.ms-excel");
    out.println(xmlFormatter.getPresentation());
%>