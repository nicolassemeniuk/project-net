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
    info="Channel processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.channel.Channel,
            net.project.channel.State,
            net.project.channel.ChannelCustomizer,
            net.project.resource.IPersonPropertyScope,
            net.project.channel.ScopeType"
%>
<jsp:useBean id="PR_user_properties" class="net.project.resource.PersonProperty" scope="session" />
<%
    String channelName = request.getParameter("name");
    String stateID = request.getParameter("state");

    if (stateID != null) {
        State state = State.forID(stateID);
        IPersonPropertyScope scope = ScopeType.makeScopeFromRequest(request);
        ChannelCustomizer.updateState(channelName, state, PR_user_properties, scope);
    }

    response.sendRedirect(request.getParameter("referer"));
%>
