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
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
       info="My Task Assignments" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.User, 
               net.project.security.SessionManager,
               net.project.resource.Assignment,
               net.project.resource.AssignmentManagerBean,
               net.project.resource.AssignmentType,
               net.project.space.Space,
               net.project.resource.AssignmentStatus,
               net.project.util.DateRange,
               java.util.Date,
               java.util.Calendar,
               net.project.base.property.PropertyProvider,
               net.project.resource.AssignmentFinder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="assignments" class="net.project.resource.AssignmentManagerBean" scope="page" />

<%
/* Note:
   No security validation is necessary on this page because it works off the authenticated users id
*/

    // The number of days ahead to look for tasks coming due
    int daysPrior = PropertyProvider.getInt("prm.schedule.filters.taskscomingdue.numberofdaysprior.value");
    int daysAhead = PropertyProvider.getInt("prm.schedule.filters.taskscomingdue.numberofdays.value");

    assignments.setPersonID(user.getID());
    // Set the statuses to those that imply a person is involved with an
    // assignment
    assignments.setStatusFilter(AssignmentStatus.getPersonalAssignmentStatuses());
    // Load only task type assignments
	assignments.setAssignmentTypeFilter(AssignmentType.TASK);
	assignments.setOrderBy(AssignmentFinder.END_DATE_COLUMN);
	assignments.setOrderDescending(false);
    // Limit range to 2 months ago and some days ahead (defined by property)
    assignments.setAssignmentDateRange(DateRange.makeDateRange(daysPrior, Calendar.DAY_OF_MONTH, daysAhead, Calendar.DAY_OF_MONTH));
//    assignments.setLimitTaskAssignmentsToIncompleteTasks(true);
    assignments.loadAssignments();
%>

<%-- Apply stylesheet to format myProjects portfolio rows --%>
<pnet-xml:transform name="assignments" stylesheet="/schedule/xsl/task-assignments.xsl">
 	<pnet-xml:property name="refLink" value="refLink=/personal/Main.jsp" />
</pnet-xml:transform>


