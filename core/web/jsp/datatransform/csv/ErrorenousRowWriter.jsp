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
    info="Errorenous Row Writer" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.datatransform.csv.CSVErrorFileExport,
			net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="columnMaps" class="net.project.datatransform.csv.map.ColumnMaps" scope="session" />
<jsp:useBean id="erWriter" class="net.project.datatransform.csv.ErroneousCSVRowsWriter" scope="page" />

<%
	erWriter.write(csv);
	CSVErrorFileExport csvErrorFileExport = new CSVErrorFileExport(csv);
	session.setAttribute("ErrorFile",csvErrorFileExport);
	response.sendRedirect(SessionManager.getJSPRootURL()+"/servlet/Download?downloadableObjectAttributeName=ErrorFile");
%>	
