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
    info="Server Status"
    language="java" 
	contentType="text/xml; charset=UTF-8"
%>
<jsp:useBean id="support" class="net.project.support.Support" />

<%=net.project.persistence.IXMLPersistence.XML_VERSION%>

<%
	// Need to encapsulate in try/catch since errors pages are html only
	try {
    	support.setRequest(request);
%>
        <jsp:getProperty name="support" property="XMLBody" />

<%  } catch (Exception e) { %>

<Exception>
    <Message><%=net.project.xml.XMLUtils.escape("Exception: " + e.getMessage())%></Message>
    <StackTrace>
		<%=net.project.xml.XMLUtils.escape(getStackTrace(e))%>
    </StackTrace>
</Exception>

<%	} %>

<%!
	String getStackTrace(Throwable t) {
		java.io.StringWriter writer = new java.io.StringWriter();
		t.printStackTrace(new java.io.PrintWriter(writer));
		return writer.toString();
	}
%>
