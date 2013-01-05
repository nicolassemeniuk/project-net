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
    info="include page for detailed view of a gate" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.process.PhaseBean,
            net.project.security.SecurityProvider,
            net.project.xml.XMLFormatter" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.PROCESS%>" /> 
<%
	if(m_phase.getGate() != null){
		String gateXML = m_phase.getGate().getXML();
		XMLFormatter m_formatter = new XMLFormatter();
		m_formatter.setStylesheet("/process/include/gateDetail.xsl");
		String m_output = m_formatter.getPresentation(gateXML);
		out.print(m_output);
	}
%>
