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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Sets filter criteria to filter on person
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="My Envelope List" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.workflow.*, net.project.security.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />

<%
	envelopeManagerBean.setUser(user);
	envelopeManagerBean.setSpace(user.getCurrentSpace());
	envelopeManagerBean.clearFilter();
	envelopeManagerBean.setPersonFilter(user);
	envelopeManagerBean.setStylesheet("/workflow/envelope/include/xsl/my_envelope_list.xsl");
%>
<jsp:include page="EnvelopeList.jsp" flush="true" />
 