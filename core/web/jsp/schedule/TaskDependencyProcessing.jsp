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
    info="Task edit processing.. emits no output."
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.schedule.*, 
			net.project.security.*, 
			net.project.security.SessionManager,
			net.project.resource.AssignmentManager,
            net.project.base.Module,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="session" />
<jsp:useBean id="assignmentManager" class="net.project.resource.AssignmentManager" scope="session" />

<security:verifyAccess action="view"
                       module="<%=Module.SCHEDULE%>"
                       objectID="<%=scheduleEntry.getID()%>" />

<%
	// Check security
	if (!assignmentManager.isUserInAssignmentList(user.getID())) {
		// Check modify permission if user is not assigned scheduleEntry
		// An exception is thrown if they have no permission
		securityProvider.securityCheck(scheduleEntry.getID(), Integer.toString(net.project.base.Module.SCHEDULE), Action.MODIFY);
	}
    String rootURL = SessionManager.getJSPRootURL();
    if (Validator.isBlankOrNull(request.getParameter("nextPage"))) {
        // No referer, go to Main
        request.setAttribute("id", scheduleEntry.getID());
        request.setAttribute("action", String.valueOf(Action.VIEW));
        pageContext.forward("/servlet/ScheduleController/TaskView/Dependencies?module=" + net.project.base.Module.SCHEDULE);
    } else {			
        request.setAttribute("id", scheduleEntry.getID());
        request.setAttribute("action", String.valueOf(Action.VIEW));
        pageContext.forward(request.getParameter("nextPage"));
    }
%>
