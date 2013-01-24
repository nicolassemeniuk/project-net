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
    info="Calendar Main Page Processing. Omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.calendar.CalendarBean,
            net.project.calendar.Meeting,
            net.project.calendar.CalendarEvent,
            net.project.security.User,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.PnObject,
            net.project.base.ObjectType,
            net.project.schedule.Task,
            net.project.schedule.IScheduleEntry,
            net.project.schedule.ScheduleEntryFactory,
            net.project.schedule.ScheduleEntry,
            java.net.URLEncoder,
            net.project.security.AuthorizationFailedException,
            net.project.security.Action,
			net.project.security.ServletSecurityProvider"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<%
    // validate the security check
    int module = securityProvider.getCheckedModuleID();
    String id = securityProvider.getCheckedObjectID();
    int action = securityProvider.getCheckedActionID();

    //if ((id.length() == 0) || (module != net.project.base.Module.CALENDAR))
     //   throw new AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));

    String theAction = request.getParameter("theAction");
    String mySpace=user.getCurrentSpace().getType();

    if(theAction.equals("security")) {
        // we don't need to validate the id because we will use the one that passed the security
        if (action != net.project.security.Action.MODIFY_PERMISSIONS)
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));

        if (mySpace != null && !mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {
            session.setAttribute("objectID", request.getParameter("id"));
            request.setAttribute("action", Integer.toString(net.project.security.Action.MODIFY_PERMISSIONS));
            request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
            pageContext.forward ("/security/SecurityMain.jsp");
        }
    } else if (theAction.equals("remove")) {
        // we don't need to validate the id because we will use the one that passed the security
        if (action != net.project.security.Action.DELETE)
            throw new AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));

        PnObject obj = new PnObject();
        obj.setID(id);
        obj.load();

        if ((obj.getType().equals(ObjectType.MEETING)) || (obj.getType().equals(ObjectType.EVENT))) {
            calendar.removeEntry(id);
        } else if (obj.getType().equals(ObjectType.TASK)) {
            ScheduleEntry tempTask = new Task();
            tempTask.setID(id);
            tempTask.remove();
        }

        // add attributes for Main.jsp security check
        request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
        request.setAttribute("id", "");
        request.setAttribute("module", Integer.toString(net.project.base.Module.CALENDAR));
        ServletSecurityProvider.setAndCheckValues(request);
        pageContext.forward("/calendar/Main.jsp");
    } else if (theAction.equals("modify")) {
        if (action != net.project.security.Action.MODIFY)
            throw new AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));

        PnObject obj = new PnObject();
        obj.setID(id);
        obj.load();
        if (obj.getType().equals(ObjectType.MEETING)) {
            // if the user is in his personal space we must change to the project before
            // editing the meeting
            if (mySpace == null || mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {
                Meeting meeting = new Meeting();
                meeting.reset();
                meeting.setID(id);
                String project_id = meeting.getSpaceID();
                String redirect = SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + project_id
                    + "&page=" + URLEncoder.encode(SessionManager.getJSPRootURL() +
                    "/calendar/MeetingEdit.jsp?module=" + module + "&action=" +
                    action + "&id=" + id, SessionManager.getCharacterEncoding());
                response.sendRedirect(redirect);
                return;
            }
            pageContext.forward("/calendar/MeetingEdit.jsp");
        } else if (obj.getType().equals(ObjectType.EVENT)) {
            // if the user is in his personal space we must change to the project before
            // editing the event if necessary
            if (mySpace == null || mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {
                CalendarEvent event = new CalendarEvent();
                event.reset();
                event.setID(id);
                String space_id = event.getSpaceID();
                if (!space_id.equals(user.getCurrentSpace().getID())) {
                    String redirect = SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + space_id
                        + "&page=" + URLEncoder.encode(SessionManager.getJSPRootURL() +
                        "/calendar/EventEdit.jsp?module=" + module + "&action=" + action +
                        "&id=" + id, SessionManager.getCharacterEncoding());
                    response.sendRedirect(redirect);
                    return;
                }
            }
            pageContext.forward("/calendar/EventEdit.jsp");
        } else if (obj.getType().equals(ObjectType.TASK)) {
            // if the user is in his personal space we must change to the project before
            // editing the task
            if (mySpace == null || mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {
                IScheduleEntry scheduleEntry = ScheduleEntryFactory.loadForID(id);
                String space_id = scheduleEntry.getSpaceID();
                String redirect = SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + space_id
                    + "&page=" + URLEncoder.encode(SessionManager.getJSPRootURL() +
                    "/servlet/ScheduleController/TaskEdit?module=" + Module.SCHEDULE + "&action=" +
                    Action.VIEW + "&id=" + id, SessionManager.getCharacterEncoding());
                response.sendRedirect(redirect);

                return;
            }

            request.setAttribute("module", Integer.toString(net.project.base.Module.SCHEDULE));
            // Tasks performs own security check; pass view permission
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            pageContext.forward("/servlet/ScheduleController/TaskEdit");
        }
    }
%>
