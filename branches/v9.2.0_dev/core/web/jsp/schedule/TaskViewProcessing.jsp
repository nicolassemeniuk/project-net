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
    info="Page to save modifications made on the task view page."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.schedule.Task,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="session"/>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />

<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.SCHEDULE%>"
                       objectID="<%=scheduleEntry.getID()%>"/>

<%-- Get the form forms --%>
<jsp:setProperty name="scheduleEntry" property="*"/>

<%
    //sjmittal: this is unnecessary call as the task view page is view only
    //so nothing gets submited
//    scheduleEntry.store(false, schedule);
                       
    //For some strange reason, I read in TaskAdvancedProcessing.jsp that 
    //"Denormalized database attributes" may not be loaded after storing.
    //I'm going to follow their lead - but with curiosity.
    String id = scheduleEntry.getID();
    scheduleEntry.clear();
    scheduleEntry.setID(id);
    
    //Redirect to the correct page
    response.sendRedirect(SessionManager.getJSPRootURL() + request.getParameter("nextPage"));
%>        
