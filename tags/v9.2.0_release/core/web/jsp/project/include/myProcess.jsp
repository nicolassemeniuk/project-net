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
    info="include page for a list of phases" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.process.ProcessBean,
            net.project.security.SecurityProvider,
			net.project.security.SessionManager,
            net.project.xml.XMLFormatter"
%>
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
String m_phases_xml = m_process.getPhaseList().getXML();
XMLFormatter m_formatter = new XMLFormatter();
m_formatter.setStylesheet("/project/include/myProcess.xsl");
String m_output = m_formatter.getPresentation(m_phases_xml);
out.print(m_output);
%>
