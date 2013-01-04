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
    import="net.project.resource.AssignmentManager,
            net.project.util.DateRange,
            java.util.GregorianCalendar,
            java.util.Date,
            net.project.calendar.PnCalendar,
            net.project.xml.XMLFormatter,
            net.project.resource.filters.assignments.CurrentAssignmentsFilter"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
    AssignmentManager assignmentManager = new AssignmentManager();
    assignmentManager.setPersonID(user.getID());
    assignmentManager.addFilter(new CurrentAssignmentsFilter("currentAssignments"));
    assignmentManager.loadAssignments();
%>

<script language="javascript" type="text/javascript">
    function changeSelection() {
        if (self.document.currentAssignmentsForm.selected) {
            changeCheckedState(self.document.currentAssignmentsForm.selected, self.document.currentAssignmentsForm.changeCheckedState.checked);
        }
    }
</script>

<form name="currentAssignmentsForm">
<%
    XMLFormatter xmlFormatter = new XMLFormatter();
    xmlFormatter.setStylesheet("/personal/include/xsl/current-assignments.xsl");
    out.println(xmlFormatter.getPresentation(assignmentManager.getXML()));
%>
</form>