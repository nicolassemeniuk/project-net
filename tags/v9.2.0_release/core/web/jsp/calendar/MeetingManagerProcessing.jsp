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
|   $Revision: 20669 $
|       $Date: 2010-04-07 10:02:30 -0300 (mié, 07 abr 2010) $
|
|   Meeting manager processing page
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Meeting Manager Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
            net.project.calendar.MeetingBean,
            net.project.security.SecurityProvider,
            net.project.base.Module,
            net.project.security.Action,
            java.util.Hashtable,
            net.project.security.SessionManager,
            net.project.security.ServletSecurityProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
    // set the noForward attribute so included processing jsp's do not forward
    request.setAttribute("noForward", new Boolean(true));

	String theAction = request.getParameter("theAction");
	if (theAction.equals("submit")) {
        %>
        <security:verifyAccess action="modify" module="<%=Module.CALENDAR%>" objectID="<%=meeting.getID()%>"/>
        <%
        String[] includes = request.getParameterValues("IncludeJSP");
        for (int i=0;i<includes.length;i++) {
            String include = includes[i];
            %>
            <jsp:include page="<%= include %>" flush="true"/>
            <%
        }
        request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
        ServletSecurityProvider.setAndCheckValues(request);
        %>
        <jsp:include page="MeetingManager.jsp" flush="true"/>
        <%

        // send out notification to new attendees on submission of meeting modify.    
        meeting.notifyNewAttendees();
        meeting.clearNewAttendeesList();

    } else if (theAction != null && theAction.equals("remove")) {
        %>
        <security:verifyAccess action="<%=Action.DELETE_STRING%>" module="<%=Module.CALENDAR%>" objectID="<%=meeting.getID()%>"/>
        <%
        meeting.remove();

        Hashtable nav = (Hashtable)request.getSession().getAttribute("PageNavigator");
        if (nav == null) {
            nav = new java.util.Hashtable();
            request.getSession().setAttribute("PageNavigator", nav);
        }

        String myReturnTo = (String)nav.get("MeetingManager_returnto");
        if (myReturnTo == null){
        	String id = SessionManager.getUser().getCurrentSpace().getID();
            myReturnTo = SessionManager.getJSPRootURL() + "/project/Dashboard?module="+Module.PROJECT_SPACE+"&id="+id;
        }

        response.sendRedirect(myReturnTo);
    }
%>

