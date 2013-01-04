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
|   $Revision: 19730 $
|       $Date: 2009-08-12 11:09:22 -0300 (mié, 12 ago 2009) $
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Channel page used to insert a report list"
    language="java"
    errorPage="/errors.jsp"
    import="java.util.Iterator,
            net.project.base.Module,
            net.project.report.ReportType,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%-- Create a list of reports --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
    <td class="tableHeader"><%=PropertyProvider.get("prm.report.reportlist.reportname.name")%></td>
    <td></td>
    <td class="tableHeader"><%=PropertyProvider.get("prm.report.reportlist.reportdescription.name")%></td>
</tr>
<tr>
    <td class="tableLine" colspan="3"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
</tr>
<%
    Iterator reportIterator = ReportType.getReportTypes(user.getCurrentSpace().getType()).iterator();
    while (reportIterator.hasNext()) {
        ReportType rt = (ReportType)reportIterator.next();
%>
<tr>
    <td class="tableContent" width="25%"><a href="<%=SessionManager.getJSPRootURL()+rt.getParameterPageURL()%>"><%=rt.getName()%></a></td>
    <td class="tableContent" width="2"></td>
    <td class="tableContent" width="73%"><%=rt.getDescription()%></td>
</tr>
<tr>
    <td class="tableLine" colspan="3"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" height="1" width="1" border="0"/></td>
</tr>
<%        
    }
%>
</table>
