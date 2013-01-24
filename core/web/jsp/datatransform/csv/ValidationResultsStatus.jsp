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
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Validation Results Status" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*"
%>
<%
	String processingURL = SessionManager.getJSPRootURL() + "/datatransform/csv/ImportResults.jsp";
	String processingParams = "";
%>

<%-- Display Busy page and forward to import page --%>
<%
String titleVal = PropertyProvider.get("prm.form.csvimport.validation.status.channel.wait.title");
String messageVal = PropertyProvider.get("prm.form.csvimport.validation.status.importing.message");
%>
<jsp:forward page="/base/Busy.jsp">
	<jsp:param name="processingURL" value="<%=processingURL%>" />
	<jsp:param name="processingParams" value="<%=java.net.URLEncoder.encode(processingParams, SessionManager.getCharacterEncoding())%>" />
	<jsp:param name="title" value="<%=titleVal%>" />
	<jsp:param name="message" value="<%=messageVal%>" />
</jsp:forward>
